package restudio.reglass.client.api;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * Liquid-glass style renderer for the stable Minecraft 1.21.1 GuiGraphics path.
 * It uses layered translucent geometry, Fresnel-like edge lighting and a moving
 * highlight so the UI remains visually close to Apple's Liquid Glass language
 * without relying on the newer 1.21.8 render-state API.
 */
public final class ReGlassApi {
    private ReGlassApi() {}

    public static WidgetStyle inactiveStyle = new WidgetStyle().tint(0xFFFFFF, 0.12f);

    public static ReGlassConfig getGlobalConfig() {
        return ReGlassConfig.INSTANCE;
    }

    public static Builder create(GuiGraphics graphics) {
        return new Builder(graphics);
    }

    public static final class Builder {
        private final GuiGraphics graphics;
        private int x, y, width, height;
        private float cornerRadius = -1f;
        @Nullable private Component text;
        private WidgetStyle style = new WidgetStyle();
        private float hover;
        private float focus;
        private float refraction = 1f;
        private float highlight = 0.8f;
        private float morphing = 1f;

        private Builder(GuiGraphics graphics) {
            this.graphics = graphics;
        }

        public Builder fromWidget(AbstractWidget widget) {
            return position(widget.getX(), widget.getY())
                    .size(widget.getWidth(), widget.getHeight())
                    .text(widget.getMessage())
                    .hover(widget.isHoveredOrFocused() ? 1f : 0f);
        }

        public Builder position(int x, int y) { this.x = x; this.y = y; return this; }
        public Builder size(int width, int height) {
            this.width = Math.max(0, width);
            this.height = Math.max(0, height);
            return this;
        }
        public Builder dimensions(int x, int y, int width, int height) { return position(x, y).size(width, height); }
        public Builder cornerRadius(float radius) { this.cornerRadius = Math.max(0f, radius); return this; }
        public Builder text(@Nullable Component text) { this.text = text; return this; }
        public Builder style(@Nullable WidgetStyle style) { this.style = style == null ? new WidgetStyle() : style; return this; }
        public Builder hover(float amount) { this.hover = clamp01(amount); return this; }
        public Builder focus(float amount) { this.focus = clamp01(amount); return this; }
        public Builder selected(float amount) { return focus(amount); }
        public Builder refraction(float amount) { this.refraction = Math.max(0f, Math.min(3f, amount)); return this; }
        public Builder highlight(float amount) { this.highlight = clamp01(amount); return this; }
        public Builder morphing(float amount) { this.morphing = Math.max(0f, Math.min(4f, amount)); return this; }

        public void render() {
            if (width <= 0 || height <= 0) return;

            float radius = cornerRadius < 0 ? Math.min(width, height) * 0.5f : cornerRadius;
            radius = Math.min(radius, Math.min(width, height) * 0.5f);

            float pulse = (float) ((Math.sin(System.nanoTime() * 0.000000003 * Math.max(0.25f, morphing)) + 1.0) * 0.5);
            float active = Math.max(hover, focus);

            float shadowExpand = Math.max(2f, style.getShadowExpand() * 0.35f);
            int shadowAlpha = Math.round(clamp01(style.getShadowFactor() * 0.75f) * 255f);
            drawRoundedBox(x + Math.round(style.getShadowOffsetX()), y + Math.round(style.getShadowOffsetY()),
                    width, height, radius, shadowExpand, argb(shadowAlpha, 0x000000));

            // Base translucent body. White rather than black keeps the material
            // visually similar to Apple's neutral Liquid Glass surface.
            float bodyAlpha = Math.min(0.72f, Math.max(0.10f, style.getTintAlpha() + 0.10f + active * 0.08f));
            drawRoundedBox(x, y, width, height, radius, 0f, argb(Math.round(bodyAlpha * 255f), 0xFFFFFF));

            // Slight cool inner layer: this reads as depth rather than a flat fill.
            int innerAlpha = Math.round((0.05f + refraction * 0.025f) * 255f);
            drawRoundedBox(x + 1, y + 1, Math.max(0, width - 2), Math.max(0, height - 2),
                    Math.max(0, radius - 1), 0f, argb(innerAlpha, 0xBFD8FF));

            // Fresnel-like edge: stronger near interaction and refraction values.
            float edge = Math.min(1f, (0.18f + refraction * 0.10f + active * 0.18f) * (0.65f + highlight * 0.35f));
            drawRoundedOutline(x, y, width, height, radius, argb(Math.round(edge * 255f), 0xFFFFFF));

            // Moving specular band. This approximates a live lens highlight on the
            // stable 1.21.1 GUI path without claiming to sample the framebuffer.
            int bandWidth = Math.max(2, Math.round(width * 0.16f));
            int bandX = x + Math.round((width + bandWidth) * pulse) - bandWidth;
            int bandAlpha = Math.round((0.04f + highlight * 0.10f + active * 0.06f) * 255f);
            graphics.fill(bandX, y + 1, Math.min(x + width, bandX + bandWidth), y + Math.min(height, 3),
                    argb(bandAlpha, 0xFFFFFF));

            // Selected/hovered state becomes a small inner capsule rather than a
            // hard recolor, giving the navigation/morphing look from Liquid Glass.
            if (active > 0f) {
                int inset = Math.max(2, Math.round(2f + active * 2f));
                int selectedAlpha = Math.round((0.06f + active * 0.10f) * 255f);
                drawRoundedBox(x + inset, y + inset, Math.max(0, width - inset * 2),
                        Math.max(0, height - inset * 2), Math.max(0, radius - inset), 0f,
                        argb(selectedAlpha, 0xFFFFFF));
                drawRoundedOutline(x + 1, y + 1, width - 2, height - 2, Math.max(0, radius - 1),
                        argb(Math.round((0.15f + active * 0.15f) * 255f), 0xFFFFFF));
            }

            if (text != null && width > 20 && height >= 12) {
                int tx = x + Math.max(6, Math.round(width * 0.08f));
                int ty = y + Math.max(2, (height - 8) / 2);
                graphics.drawString(net.minecraft.client.Minecraft.getInstance().font, text, tx, ty, 0xFFFFFFFF, true);
            }
        }

        private void drawRoundedBox(int left, int top, int w, int h, float radius, float expand, int color) {
            if (w <= 0 || h <= 0) return;
            int l = Math.round(left - expand), t = Math.round(top - expand);
            int r = Math.round(left + w + expand), b = Math.round(top + h + expand);
            int rr = Math.max(0, Math.round(radius + expand));
            if (rr < 2) { graphics.fill(l, t, r, b, color); return; }
            graphics.fill(l + rr, t, r - rr, b, color);
            graphics.fill(l, t + rr, r, b - rr, color);
            graphics.fill(l + rr / 2, t, r - rr / 2, t + rr, color);
            graphics.fill(l + rr / 2, b - rr, r - rr / 2, b, color);
        }

        private void drawRoundedOutline(int left, int top, int w, int h, float radius, int color) {
            if (w <= 0 || h <= 0) return;
            int rr = Math.max(1, Math.round(radius));
            int thickness = 1;
            graphics.fill(left + rr, top, left + w - rr, top + thickness, color);
            graphics.fill(left + rr, top + h - thickness, left + w - rr, top + h, color);
            graphics.fill(left, top + rr, left + thickness, top + h - rr, color);
            graphics.fill(left + w - thickness, top + rr, left + w, top + h - rr, color);
        }
    }

    private static float clamp01(float value) {
        if (Float.isNaN(value)) return 0f;
        return Math.max(0f, Math.min(1f, value));
    }

    private static int argb(int alpha, int rgb) {
        return (Math.max(0, Math.min(255, alpha)) << 24) | (rgb & 0xFFFFFF);
    }
}
