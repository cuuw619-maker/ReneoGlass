package restudio.reglass.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import restudio.reglass.client.api.ReGlassApi;
import restudio.reglass.client.api.WidgetStyle;

/**
 * Standalone NeoForge-native glass widget.
 *
 * Rendering deliberately goes through GuiGraphics/ReGlassApi only. There is
 * no Fabric renderer, mixin, render-state hook, or uniform manager involved.
 */
public class LiquidGlassWidget extends AbstractWidget {
    private float cornerRadiusPx;
    private boolean moveable;
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;
    public WidgetStyle style = new WidgetStyle();

    public LiquidGlassWidget(int x, int y, int width, int height, WidgetStyle style) {
        super(x, y, width, height, Component.empty());
        this.cornerRadiusPx = 0.5f * Math.min(width, height);
        if (style != null) this.style = style;
    }

    public LiquidGlassWidget setCornerRadiusPx(float radiusPx) {
        this.cornerRadiusPx = Math.max(0f, radiusPx);
        return this;
    }

    public LiquidGlassWidget setMoveable(boolean moveable) {
        this.moveable = moveable;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ReGlassApi.create(graphics)
                .fromWidget(this)
                .cornerRadius(cornerRadiusPx)
                .style(this.style)
                .render();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.moveable) return super.mouseClicked(mouseX, mouseY, button);
        if (button == 0 && isMouseOver(mouseX, mouseY)) {
            this.dragging = true;
            this.dragOffsetX = (int) (mouseX - getX());
            this.dragOffsetY = (int) (mouseY - getY());
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.dragging && button == 0) {
            setX((int) (mouseX - dragOffsetX));
            setY((int) (mouseY - dragOffsetY));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.dragging && button == 0) this.dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
}
