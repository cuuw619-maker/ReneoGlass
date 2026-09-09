package restudio.reglass.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
import restudio.reglass.client.api.ReGlassApi;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.client.api.WidgetStyle;
import restudio.reglass.client.config.ReGlassSettingsIO;
import restudio.reglass.client.screen.config.ReGlassConfigScreen;

@EventBusSubscriber(modid = "reglass", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ReGlassClient {
    private static KeyMapping playgroundKey;
    private static KeyMapping configKey;
    public static Minecraft minecraftClient;

    private ReGlassClient() {}

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            minecraftClient = Minecraft.getInstance();
            ReGlassSettingsIO.loadIntoMemory();
        });
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        String category = "key.categories.reglass";
        playgroundKey = new KeyMapping("key.reglass.playground", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, category);
        configKey = new KeyMapping("key.reglass.config", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, category);
        event.register(playgroundKey);
        event.register(configKey);
    }

    @EventBusSubscriber(modid = "reglass", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static final class GameEvents {
        private GameEvents() {}

        @SubscribeEvent
        public static void clientTick(ClientTickEvent.Post event) {
            Minecraft client = Minecraft.getInstance();
            minecraftClient = client;
            if (client.screen == null && configKey != null && configKey.consumeClick()) {
                client.setScreen(new ReGlassConfigScreen(null));
            }
            if (client.screen == null && playgroundKey != null && playgroundKey.consumeClick()) {
                client.setScreen(new PlaygroundScreen());
            }
        }

        @SubscribeEvent
        public static void renderScreen(ScreenEvent.Render.Post event) {
            Screen screen = event.getScreen();
            if (screen instanceof ReGlassConfigScreen || screen instanceof PlaygroundScreen) return;

            ReGlassConfig config = ReGlassConfig.INSTANCE;
            if (!config.features.enableRedesign) return;

            GuiGraphics graphics = event.getGuiGraphics();
            for (var listener : screen.children()) {
                if (!(listener instanceof AbstractWidget widget) || !widget.visible) continue;

                String name = widget.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
                boolean slider = name.contains("slider");
                boolean button = widget instanceof Button || !slider;
                if ((slider && !config.features.sliders) || (button && !config.features.buttons)) continue;

                ReGlassApi.create(graphics)
                        .fromWidget(widget)
                        .text(null)
                        // Full capsule geometry is the core Liquid Glass language.
                        .cornerRadius(widget.getHeight() * 0.5f)
                        .style(new WidgetStyle()
                                .tint(0xFFFFFF, Math.max(0.08f, config.defaultTintAlpha))
                                .shadow(18f, 0.22f, 0f, 2f)
                                .shadowColor(0x000000, 0.9f))
                        .refraction(config.defaultRefFactor)
                        .highlight(config.defaultGlareFactor / 100f)
                        .morphing(config.focusBorderSpeed)
                        .hover(widget.isHoveredOrFocused() ? 1f : 0f)
                        .focus(widget.isFocused() ? 1f : 0f)
                        .render();
            }
        }
    }

    public static class PlaygroundScreen extends Screen {
        private boolean blur;
        private WidgetStyle customStyle;

        public PlaygroundScreen() {
            super(Component.literal("ReGlass Playground"));
        }

        @Override
        protected void init() {
            super.init();
            customStyle = WidgetStyle.create()
                    .tint(0xFFFFFF, 0.22f)
                    .blurRadius(0)
                    .shadow(18f, 0.22f, 0f, 3f)
                    .smoothing(.05f)
                    .shadowColor(0x000000, 0.9f);
            addRenderableWidget(new LiquidGlassWidget(width / 2 - 75, height / 2 - 25, 150, 50, customStyle).setMoveable(true));
            addRenderableWidget(Button.builder(Component.literal("Toggle BG Blur"), b -> blur = !blur)
                    .bounds(10, 10, 120, 20).build());
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            graphics.drawString(font, Component.literal("Liquid Glass Playground"), width / 2 - 55, 10, 0xFFFFFFFF, true);
            super.render(graphics, mouseX, mouseY, delta);
        }

        @Override
        public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            if (blur) super.renderBackground(graphics, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 1) {
                addRenderableWidget(new LiquidGlassWidget((int) mouseX - 50, (int) mouseY - 50, 100, 100,
                        WidgetStyle.create().tint(0xFFFFFF, 0.18f).smoothing(.05f)).setMoveable(true));
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}
