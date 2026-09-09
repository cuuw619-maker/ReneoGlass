package restudio.reglass.client.screen.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import restudio.reglass.client.api.ReGlassApi;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.client.config.ReGlassSettingsIO;
import restudio.reglass.client.ui.MappedSlider;

/** Compact user-facing ReGlass configuration: the renderer owns the complexity. */
public final class ReGlassConfigScreen extends Screen {
    private final Screen parent;
    private MappedSlider opacity;
    private MappedSlider refraction;
    private MappedSlider highlight;
    private MappedSlider morphing;

    public ReGlassConfigScreen(Screen parent) {
        super(Component.literal("Liquid Glass"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        ReGlassConfig c = ReGlassConfig.INSTANCE;
        int w = Math.min(360, width - 40);
        int x = (width - w) / 2;
        int y = Math.max(46, height / 2 - 82);

        Button enable = Button.builder(Component.literal(c.features.enableRedesign ? "Liquid Glass: ON" : "Liquid Glass: OFF"), b -> {
            c.features.enableRedesign = !c.features.enableRedesign;
            b.setMessage(Component.literal(c.features.enableRedesign ? "Liquid Glass: ON" : "Liquid Glass: OFF"));
        }).bounds(x, y, w, 24).build();
        addRenderableWidget(enable);
        y += 34;

        opacity = slider("Glass Opacity", 0, 1, 0.01, c.defaultTintAlpha, x, y, w,
                v -> c.defaultTintAlpha = (float) v);
        y += 30;
        refraction = slider("Refraction", 0, 3, 0.01, c.defaultRefFactor, x, y, w,
                v -> c.defaultRefFactor = (float) v);
        y += 30;
        highlight = slider("Dynamic Highlight", 0, 1, 0.01, c.defaultGlareFactor / 100.0, x, y, w,
                v -> c.defaultGlareFactor = (float) v * 100f);
        y += 30;
        morphing = slider("Morphing", 0, 4, 0.05, c.focusBorderSpeed, x, y, w,
                v -> c.focusBorderSpeed = (float) v);
        y += 38;

        addRenderableWidget(Button.builder(Component.literal("Reset"), b -> {
            ReGlassSettingsIO.apply(new ReGlassSettingsIO.Data());
            ReGlassSettingsIO.saveFromMemory();
            rebuildConfigWidgets();
        }).bounds(x, y, w / 2 - 5, 22).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> close())
                .bounds(x + w / 2 + 5, y, w / 2 - 5, 22).build());
    }

    private MappedSlider slider(String name, double min, double max, double step, double value,
                                int x, int y, int w, java.util.function.DoubleConsumer setter) {
        MappedSlider s = MappedSlider.floatSlider(x, y, w, 22, Component.literal(name), min, max,
                Mth.clamp(value, min, max), setter);
        addRenderableWidget(s);
        return s;
    }

    private void rebuildConfigWidgets() {
        clearWidgets();
        init();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, "Liquid Glass", width / 2, 18, 0xFFFFFFFF);
        graphics.drawCenteredString(font, "Apple-inspired glass surface", width / 2, 31, 0xBFFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTick);

        // Add the same capsule material over the controls so the settings screen
        // demonstrates the effect it configures.
        ReGlassConfig c = ReGlassConfig.INSTANCE;
        for (var child : children()) {
            if (child instanceof net.minecraft.client.gui.components.AbstractWidget w && w.visible) {
                ReGlassApi.create(graphics)
                        .position(w.getX(), w.getY())
                        .size(w.getWidth(), w.getHeight())
                        .cornerRadius(w.getHeight() * 0.5f)
                        .style(new restudio.reglass.client.api.WidgetStyle().tint(0xFFFFFF, 0.10f))
                        .refraction(c.defaultRefFactor)
                        .highlight(c.defaultGlareFactor / 100f)
                        .morphing(c.focusBorderSpeed)
                        .hover(w.isHoveredOrFocused() ? 1f : 0f)
                        .focus(w.isFocused() ? 1f : 0f)
                        .render();
            }
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public void close() {
        ReGlassSettingsIO.saveFromMemory();
        if (minecraft != null) minecraft.setScreen(parent);
    }
}
