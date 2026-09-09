package restudio.reglass.client.screen.config;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Vector2f;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.client.api.model.RimLight;
import restudio.reglass.client.config.ReGlassSettingsIO;
import restudio.reglass.client.ui.MappedSlider;

public final class ReGlassConfigScreen extends Screen {
    private final Screen parent;
    private final List<Placed> settings = new ArrayList<>();
    private double scroll;
    private int contentHeight;

    private record Placed(AbstractWidget widget, int baseY) {}

    public ReGlassConfigScreen(Screen parent) {
        super(Component.literal("ReGlass Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        settings.clear();
        ReGlassConfig c = ReGlassConfig.INSTANCE;
        int x = 12;
        int w = Math.min(360, width / 2 - 20);
        int y = 8;
        int step = 24;

        title("GENERAL", x, y, w); y += step;
        bool("Enable Redesign", c.features.enableRedesign, x, y, w, v -> c.features.enableRedesign = v); y += step;
        bool("Glass Buttons", c.features.buttons, x, y, w, v -> c.features.buttons = v); y += step;
        bool("Glass Sliders", c.features.sliders, x, y, w, v -> c.features.sliders = v); y += step;
        bool("Glass Hotbar", c.features.hotbar, x, y, w, v -> c.features.hotbar = v); y += step;
        bool("Cancel Screen Darkening", c.features.cancelScreenDarkening, x, y, w, v -> c.features.cancelScreenDarkening = v); y += step;
        bool("Pixelated Grid", c.features.pixelatedGrid, x, y, w, v -> c.features.pixelatedGrid = v); y += step + 8;

        title("APPEARANCE", x, y, w); y += step;
        integer("Tint Color (HEX)", 0, 0xFFFFFF, c.defaultTintColor, x, y, w, v -> c.defaultTintColor = v); y += step;
        decimal("Tint Alpha", 0, 1, .01, c.defaultTintAlpha, x, y, w, v -> c.defaultTintAlpha = (float) v); y += step;
        integer("Blur Radius", 0, 64, c.defaultBlurRadius, x, y, w, v -> c.defaultBlurRadius = v); y += step;
        decimal("Smoothing", -.02, .02, .001, c.defaultSmoothing, x, y, w, v -> c.defaultSmoothing = (float) v); y += step + 8;

        title("SHADOW", x, y, w); y += step;
        decimal("Shadow Expand", 0, 100, .5, c.defaultShadowExpand, x, y, w, v -> c.defaultShadowExpand = (float) v); y += step;
        decimal("Shadow Factor", 0, 1, .01, c.defaultShadowFactor, x, y, w, v -> c.defaultShadowFactor = (float) v); y += step;
        decimal("Shadow Offset X", -50, 50, .1, c.defaultShadowOffsetX, x, y, w, v -> c.defaultShadowOffsetX = (float) v); y += step;
        decimal("Shadow Offset Y", -50, 50, .1, c.defaultShadowOffsetY, x, y, w, v -> c.defaultShadowOffsetY = (float) v); y += step;
        integer("Shadow Color (HEX)", 0, 0xFFFFFF, c.defaultShadowColor, x, y, w, v -> c.defaultShadowColor = v); y += step;
        decimal("Shadow Color Alpha", 0, 1, .01, c.defaultShadowColorAlpha, x, y, w, v -> c.defaultShadowColorAlpha = (float) v); y += step + 8;

        title("REFRACTION", x, y, w); y += step;
        decimal("Refraction Thickness", 0, 100, .5, c.defaultRefThickness, x, y, w, v -> c.defaultRefThickness = (float) v); y += step;
        decimal("Refraction Factor", .5, 4, .01, c.defaultRefFactor, x, y, w, v -> c.defaultRefFactor = (float) v); y += step;
        decimal("Dispersion", 0, 100, .1, c.defaultRefDispersion, x, y, w, v -> c.defaultRefDispersion = (float) v); y += step;
        decimal("Fresnel Range", 0, 100, .5, c.defaultRefFresnelRange, x, y, w, v -> c.defaultRefFresnelRange = (float) v); y += step;
        decimal("Fresnel Hardness", 0, 100, .5, c.defaultRefFresnelHardness, x, y, w, v -> c.defaultRefFresnelHardness = (float) v); y += step;
        decimal("Fresnel Factor", 0, 100, .5, c.defaultRefFresnelFactor, x, y, w, v -> c.defaultRefFresnelFactor = (float) v); y += step + 8;

        title("GLARE", x, y, w); y += step;
        decimal("Glare Range", 0, 100, .5, c.defaultGlareRange, x, y, w, v -> c.defaultGlareRange = (float) v); y += step;
        decimal("Glare Hardness", 0, 100, .5, c.defaultGlareHardness, x, y, w, v -> c.defaultGlareHardness = (float) v); y += step;
        decimal("Glare Convergence", 0, 100, .5, c.defaultGlareConvergence, x, y, w, v -> c.defaultGlareConvergence = (float) v); y += step;
        decimal("Glare Opposite Factor", 0, 100, .5, c.defaultGlareOppositeFactor, x, y, w, v -> c.defaultGlareOppositeFactor = (float) v); y += step;
        decimal("Glare Factor", 0, 100, .5, c.defaultGlareFactor, x, y, w, v -> c.defaultGlareFactor = (float) v); y += step;
        integer("Glare Angle (degrees)", -180, 180, Math.round(c.defaultGlareAngleRad * 180f / (float)Math.PI), x, y, w, v -> c.defaultGlareAngleRad = v * (float)Math.PI / 180f); y += step + 8;

        title("RIM LIGHT", x, y, w); y += step;
        decimal("Rim Light X", -1, 1, .01, c.rimLight.direction().x, x, y, w, v -> setRim((float) v, c.rimLight.direction().y, c.rimLight.color(), c.rimLight.intensity())); y += step;
        decimal("Rim Light Y", -1, 1, .01, c.rimLight.direction().y, x, y, w, v -> setRim(c.rimLight.direction().x, (float) v, c.rimLight.color(), c.rimLight.intensity())); y += step;
        integer("Rim Light Color (HEX)", 0, 0xFFFFFF, c.rimLight.color(), x, y, w, v -> setRim(c.rimLight.direction().x, c.rimLight.direction().y, v, c.rimLight.intensity())); y += step;
        decimal("Rim Light Intensity", 0, 2, .01, c.rimLight.intensity(), x, y, w, v -> setRim(c.rimLight.direction().x, c.rimLight.direction().y, c.rimLight.color(), (float) v)); y += step + 8;

        title("INTERACTION", x, y, w); y += step;
        decimal("Hover Scale (px)", 0, 20, .1, c.hoverScalePx, x, y, w, v -> c.hoverScalePx = (float) v); y += step;
        decimal("Focus Scale (px)", 0, 20, .1, c.focusScalePx, x, y, w, v -> c.focusScalePx = (float) v); y += step;
        decimal("Focus Border Width", 0, 20, .1, c.focusBorderWidthPx, x, y, w, v -> c.focusBorderWidthPx = (float) v); y += step;
        decimal("Focus Border Intensity", 0, 2, .01, c.focusBorderIntensity, x, y, w, v -> c.focusBorderIntensity = (float) v); y += step;
        decimal("Focus Border Speed", 0, 20, .1, c.focusBorderSpeed, x, y, w, v -> c.focusBorderSpeed = (float) v); y += step;
        decimal("Pixelated Grid Size", 1, 64, .5, c.pixelatedGridSize, x, y, w, v -> c.pixelatedGridSize = (float) v); y += step + 8;

        title("ADVANCED", x, y, w); y += step;
        decimal("Pixel Epsilon", 0, 20, .05, c.pixelEpsilon, x, y, w, v -> c.pixelEpsilon = (float) v); y += step;
        decimal("Debug Step", 0, 20, .1, c.debugStep, x, y, w, v -> c.debugStep = (float) v); y += step;
        contentHeight = y;

        addRenderableWidget(Button.builder(Component.literal("Reset Defaults"), b -> {
            ReGlassSettingsIO.apply(new ReGlassSettingsIO.Data());
            ReGlassSettingsIO.saveFromMemory();
            rebuildWidgets();
        }).bounds(width / 2 - 105, height - 28, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> close()).bounds(width / 2 + 5, height - 28, 100, 20).build());
    }

    private void rebuildWidgets() { clearWidgets(); init(); }

    private void title(String name, int x, int y, int w) {
        Button b = Button.builder(Component.literal("[ " + name + " ]"), v -> {}).bounds(x, y, w, 20).build();
        b.active = false;
        settings.add(new Placed(b, y));
        addRenderableWidget(b);
    }

    private void bool(String name, boolean value, int x, int y, int w, java.util.function.Consumer<Boolean> setter) {
        Button b = Button.builder(Component.literal(name + ": " + (value ? "ON" : "OFF")), v -> {
            boolean next = !v.getMessage().getString().endsWith("ON");
            setter.accept(next);
            v.setMessage(Component.literal(name + ": " + (next ? "ON" : "OFF")));
        }).bounds(x, y, w, 20).build();
        settings.add(new Placed(b, y));
        addRenderableWidget(b);
    }

    private void integer(String name, int min, int max, int value, int x, int y, int w, java.util.function.IntConsumer setter) {
        MappedSlider s = MappedSlider.intSlider(x, y, w, 20, Component.literal(name), min, max, Mth.clamp(value, min, max), setter::accept);
        settings.add(new Placed(s, y));
        addRenderableWidget(s);
    }

    private void decimal(String name, double min, double max, double step, double value, int x, int y, int w, java.util.function.DoubleConsumer setter) {
        MappedSlider s = MappedSlider.floatSlider(x, y, w, 20, Component.literal(name), min, max, Mth.clamp(value, min, max), setter::accept);
        settings.add(new Placed(s, y));
        addRenderableWidget(s);
    }

    private void setRim(float x, float y, int color, float intensity) {
        Vector2f d = new Vector2f(x, y);
        if (d.lengthSquared() < .000001f) d.set(-1, 1);
        ReGlassConfig.INSTANCE.rimLight = new RimLight(d.normalize(), color, intensity);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        int top = 32;
        int bottom = height - 32;
        int max = Math.max(0, contentHeight - (bottom - top));
        scroll = Mth.clamp(scroll, 0, max);
        for (Placed p : settings) {
            int yy = p.baseY() - (int)scroll + top;
            p.widget().setY(yy);
            p.widget().visible = yy >= top && yy + p.widget().getHeight() <= bottom;
        }
        graphics.drawCenteredString(font, "ReGlass — Advanced Configuration", width / 2, 12, 0xFFFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll -= verticalAmount * 18;
        return true;
    }

    @Override
    public void close() {
        ReGlassSettingsIO.saveFromMemory();
        if (minecraft != null) minecraft.setScreen(parent);
    }
}