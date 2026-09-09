package restudio.reglass.client.api;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * Stable 1.21.1 rendering API. The renderer intentionally uses GuiGraphics so it
 * remains compatible with NeoForge 21.1 without depending on the newer GUI
 * render-state pipeline introduced in later Minecraft versions.
 */
public final class ReGlassApi {
    private ReGlassApi() {}

    public static WidgetStyle inactiveStyle = new WidgetStyle().tint(0x000000, 0.30f);

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

        private Builder(GuiGraphics graphics) {
            this.graphics = graphics;
        }

        public Builder fromWidget(AbstractWidget widget) {
            return position(widget.getX(), widget.getY())
                    .size(widget.getWidth(), widget.getHeight())
                    .text(widget.getMessage())
                    .hover(widget.isHoveredOrFocused() ? 1f : 0f);
        }

        public Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = Math.max(0, width);
            this.height = Math.max(0, height);
            return this;
        }

        public Builder dimensions(int x, int y, int width, int height) {
            return position(x, y).size(width, height);
        }

        public Builder cornerRadius(float radius) {
            this.cornerRadius = Math.max(0f, radius);
            return this;
        }

        public Builder text(@Nullable Component text) {
            this.text = text;
            return this;
        }

        public Builder style(@Nullable WidgetStyle style) {
            this.style = style == null ? new WidgetStyle() : style;
            return this;
        }

        public Builder hover(float amount) {
            this.hover = clamp01(amount);
            return this;
        }

        public Builder focus(float amount) {
            this.focus = clamp01(amount);
            return this;
        }

        public Builder selected(float amount) {
            return focus(amount);
        }

        public void render() {
            if (width <= 0 || height <= 0) return;

            float radius = cornerRadius < 0 ? Math.min(width, height) * 0.5f : cornerRadius;
            radius = Math.min(radius, Math.min(width, height) * 0.5f);

            float expand = Math.max(0f, style.getShadowExpand());
            int shadowAlpha = Math.round(clamp01(style.getShadowFactor()) * style.getShadowColorAlpha() * 255f);
            int shadow = argb(shadowAlpha, style.getShadowColor());
            int sx = Math.round(style.getShadowOffsetX());
            int sy = Math.round(style.getShadowOffsetY());
            if (shadowAlpha > 0) drawRoundedBox(x + sx, y + sy, width, height, radius, expand, shadow);

            int tintAlpha = Math.round(clamp01(style.getTintAlpha()) * 255f);
            int tint = argb(tintAlpha, style.getTintColor());
            drawRoundedBox(x, y, width, height, radius, 0f, tint);

            // Subtle highlight makes the fallback renderer read as glass even
            // when shader support is unavailable on the 1.21.1 render path.
            float highlight = Math.min(1f, 0.10f + hover * 0.10f + focus * 0.12f);
            int highlightColor = argb(Math.round(highlight * 255f), 0xFFFFFF);
            drawRoundedOutline(x, y, width, height, radius, highlightColor);

            if (focus > 0f) {
                int focusColor = argb(Math.round(clamp01(style.getTintAlpha() + 0.25f) * focus * 255f), 0xFFFFFF);
                drawRoundedOutline(x - 1, y - 1, width + 2, height + 2, radius + 1, focusColor);
            }

            if (text != null && width > 20 && height >= 12) {
                int color = 0xFFFFFFFF;
                int tx = x + 6;
                int ty = y + Math.max(2, (height - 8) / 2);
                graphics.drawString(net.minecraft.client.Minecraft.getInstance().font, text, tx, ty, color, true);
            }
        }

        private void drawRoundedBox(int left, int top, int w, int h, float radius, float expand, int color) {
            int l = Math.round(left - expand);
            int t = Math.round(top - expand);
            int r = Math.round(left + w + expand);
            int b = Math.round(top + h + expand);
            int rr = Math.max(0, Math.round(radius + expand));
            if (rr < 2) {
                graphics.fill(l, t, r, b, color);
                return;
            }
            graphics.fill(l + rr, t, r - rr, b, color);
            graphics.fill(l, t + rr, r, b - rr, color);
            graphics.fill(l + rr / 2, t, r - rr / 2, t + rr, color);
            graphics.fill(l + rr / 2, b - rr, r - rr / 2, b, color);
        }

        private void drawRoundedOutline(int left, int top, int w, int h, float radius, int color) {
            int thickness = 1;
            graphics.fill(left + Math.round(radius), top, left + w - Math.round(radius), top + thickness, color);
            graphics.fill(left + Math.round(radius), top + h - thickness, left + w - Math.round(radius), top + h, color);
            graphics.fill(left, top + Math.round(radius), left + thickness, top + h - Math.round(radius), color);
            graphics.fill(left + w - thickness, top + Math.round(radius), left + w, top + h - Math.round(radius), color);
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
