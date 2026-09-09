package restudio.reglass.client;

import net.minecraft.client.gui.GuiGraphics;

/** Compatibility facade for the NeoForge 1.21.1 GuiGraphics renderer. */
public final class LiquidGlassUniforms {
    private static final LiquidGlassUniforms INSTANCE = new LiquidGlassUniforms();
    public static final int MAX_WIDGETS = 64;
    public static final int MAX_BLUR_LEVELS = 5;

    public static LiquidGlassUniforms get() { return INSTANCE; }
    private LiquidGlassUniforms() {}

    public void beginFrame(double dtSeconds) {}
    public void setScreenWantsBlur(boolean wantsBlur) {}
    public void uploadSharedUniforms() {}
    public void uploadWidgetInfo() {}
    public void addWidget(Object element) {}
    public void tryApplyBlur(GuiGraphics graphics) {}
    public int getCount() { return 0; }
}