package restudio.reglass.client.config;

import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPointForge;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import restudio.reglass.client.api.ReGlassConfig;

/**
 * Native Sodium 0.8 configuration integration for NeoForge.
 *
 * Sodium discovers this class through @ConfigEntryPointForge when Sodium is
 * installed. Reese's Sodium Options uses the same Sodium Config API, so the
 * ReGlass page is automatically available in Reese's replacement screen too.
 */
@ConfigEntryPointForge("reglass")
public final class ReGlassSodiumConfig implements ConfigEntryPoint {
    private static final String MOD_ID = "reglass";
    private static final ReGlassConfig CONFIG = ReGlassConfig.INSTANCE;

    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        builder.registerOwnModOptions()
                .setName("ReGlass")
                .setNonTintedIcon(ResourceLocation.fromNamespaceAndPath(MOD_ID, "icon.png"))
                .addPage(createGeneralPage(builder))
                .addPage(createAppearancePage(builder))
                .addPage(createEffectsPage(builder))
                .addPage(createInteractionPage(builder));
    }

    private OptionPageBuilder createGeneralPage(ConfigBuilder builder) {
        return builder.createOptionPage()
                .setName(Component.translatable("reglass.options.page.general"))
                .addOptionGroup(builder.createOptionGroup()
                        .setName(Component.translatable("reglass.options.group.features"))
                        .addOption(booleanOption(builder, "enable_redesign", "features_enableRedesign",
                                () -> CONFIG.features.enableRedesign,
                                value -> CONFIG.features.enableRedesign = value,
                                true))
                        .addOption(booleanOption(builder, "buttons", "features_buttons",
                                () -> CONFIG.features.buttons,
                                value -> CONFIG.features.buttons = value,
                                true))
                        .addOption(booleanOption(builder, "sliders", "features_sliders",
                                () -> CONFIG.features.sliders,
                                value -> CONFIG.features.sliders = value,
                                true))
                        .addOption(booleanOption(builder, "hotbar", "features_hotbar",
                                () -> CONFIG.features.hotbar,
                                value -> CONFIG.features.hotbar = value,
                                true))
                        .addOption(booleanOption(builder, "cancel_screen_darkening", "features_cancelScreenDarkening",
                                () -> CONFIG.features.cancelScreenDarkening,
                                value -> CONFIG.features.cancelScreenDarkening = value,
                                true))
                        .addOption(booleanOption(builder, "pixelated_grid", "features_pixelatedGrid",
                                () -> CONFIG.features.pixelatedGrid,
                                value -> CONFIG.features.pixelatedGrid = value,
                                false)));
    }

    private OptionPageBuilder createAppearancePage(ConfigBuilder builder) {
        return builder.createOptionPage()
                .setName(Component.translatable("reglass.options.page.appearance"))
                .addOptionGroup(builder.createOptionGroup()
                        .setName(Component.translatable("reglass.options.group.appearance"))
                        .addOption(floatOption(builder, "tint_alpha", () -> CONFIG.defaultTintAlpha,
                                value -> CONFIG.defaultTintAlpha = value, 0, 100, 0.01f, 0))
                        .addOption(intOption(builder, "blur_radius", () -> CONFIG.defaultBlurRadius,
                                value -> CONFIG.defaultBlurRadius = value, 0, 32, 1))
                        .addOption(floatOption(builder, "smoothing", () -> CONFIG.defaultSmoothing,
                                value -> CONFIG.defaultSmoothing = value, -20, 20, 0.001f, 3))
                        .addOption(floatOption(builder, "shadow_expand", () -> CONFIG.defaultShadowExpand,
                                value -> CONFIG.defaultShadowExpand = value, 0, 100, 1f, 30))
                        .addOption(floatOption(builder, "shadow_factor", () -> CONFIG.defaultShadowFactor,
                                value -> CONFIG.defaultShadowFactor = value, 0, 100, 0.01f, 25))
                        .addOption(floatOption(builder, "shadow_offset_y", () -> CONFIG.defaultShadowOffsetY,
                                value -> CONFIG.defaultShadowOffsetY = value, -100, 100, 0.1f, 20)));
    }

    private OptionPageBuilder createEffectsPage(ConfigBuilder builder) {
        return builder.createOptionPage()
                .setName(Component.translatable("reglass.options.page.effects"))
                .addOptionGroup(builder.createOptionGroup()
                        .setName(Component.translatable("reglass.options.group.refraction"))
                        .addOption(floatOption(builder, "refraction_thickness", () -> CONFIG.defaultRefThickness,
                                value -> CONFIG.defaultRefThickness = value, 1, 60, 1f, 20))
                        .addOption(floatOption(builder, "refraction_factor", () -> CONFIG.defaultRefFactor,
                                value -> CONFIG.defaultRefFactor = value, 100, 250, 0.01f, 140))
                        .addOption(floatOption(builder, "dispersion", () -> CONFIG.defaultRefDispersion,
                                value -> CONFIG.defaultRefDispersion = value, 0, 100, 0.1f, 70))
                        .addOption(floatOption(builder, "fresnel_range", () -> CONFIG.defaultRefFresnelRange,
                                value -> CONFIG.defaultRefFresnelRange = value, 0, 60, 1f, 30))
                        .addOption(floatOption(builder, "fresnel_hardness", () -> CONFIG.defaultRefFresnelHardness,
                                value -> CONFIG.defaultRefFresnelHardness = value, 0, 100, 1f, 20))
                        .addOption(floatOption(builder, "fresnel_factor", () -> CONFIG.defaultRefFresnelFactor,
                                value -> CONFIG.defaultRefFresnelFactor = value, 0, 100, 1f, 20)))
                .addOptionGroup(builder.createOptionGroup()
                        .setName(Component.translatable("reglass.options.group.glare"))
                        .addOption(floatOption(builder, "glare_range", () -> CONFIG.defaultGlareRange,
                                value -> CONFIG.defaultGlareRange = value, 0, 60, 1f, 30))
                        .addOption(floatOption(builder, "glare_hardness", () -> CONFIG.defaultGlareHardness,
                                value -> CONFIG.defaultGlareHardness = value, 0, 100, 1f, 20))
                        .addOption(floatOption(builder, "glare_convergence", () -> CONFIG.defaultGlareConvergence,
                                value -> CONFIG.defaultGlareConvergence = value, 0, 100, 1f, 50))
                        .addOption(floatOption(builder, "glare_opposite_factor", () -> CONFIG.defaultGlareOppositeFactor,
                                value -> CONFIG.defaultGlareOppositeFactor = value, 0, 100, 1f, 80))
                        .addOption(floatOption(builder, "glare_factor", () -> CONFIG.defaultGlareFactor,
                                value -> CONFIG.defaultGlareFactor = value, 0, 100, 1f, 90)));
    }

    private OptionPageBuilder createInteractionPage(ConfigBuilder builder) {
        return builder.createOptionPage()
                .setName(Component.translatable("reglass.options.page.interaction"))
                .addOptionGroup(builder.createOptionGroup()
                        .setName(Component.translatable("reglass.options.group.interaction"))
                        .addOption(floatOption(builder, "hover_scale", () -> CONFIG.hoverScalePx,
                                value -> CONFIG.hoverScalePx = value, 0, 60, 0.1f, 15))
                        .addOption(floatOption(builder, "focus_scale", () -> CONFIG.focusScalePx,
                                value -> CONFIG.focusScalePx = value, 0, 80, 0.1f, 25))
                        .addOption(floatOption(builder, "focus_border_width", () -> CONFIG.focusBorderWidthPx,
                                value -> CONFIG.focusBorderWidthPx = value, 0, 60, 0.1f, 20))
                        .addOption(floatOption(builder, "focus_border_intensity", () -> CONFIG.focusBorderIntensity,
                                value -> CONFIG.focusBorderIntensity = value, 0, 100, 0.01f, 75))
                        .addOption(floatOption(builder, "focus_border_speed", () -> CONFIG.focusBorderSpeed,
                                value -> CONFIG.focusBorderSpeed = value, 0, 40, 0.1f, 16))
                        .addOption(floatOption(builder, "pixelated_grid_size", () -> CONFIG.pixelatedGridSize,
                                value -> CONFIG.pixelatedGridSize = value, 1, 32, 1f, 8)));
    }

    private OptionBuilder booleanOption(ConfigBuilder builder, String id, String unused,
                                        java.util.function.Supplier<Boolean> getter,
                                        java.util.function.Consumer<Boolean> setter,
                                        boolean rebuild) {
        OptionBuilder option = builder.createBooleanOption(id(id))
                .setName(Component.translatable("reglass.options." + id + ".name"))
                .setTooltip(Component.translatable("reglass.options." + id + ".tooltip"))
                .setDefaultValue(getter.get())
                .setBinding(setter, getter)
                .setStorageHandler(ReGlassSettingsIO::saveFromMemory);
        if (rebuild) {
            option.setApplyHook(ReGlassSettingsIO::saveFromMemory);
        }
        return option;
    }

    private OptionBuilder intOption(ConfigBuilder builder, String id,
                                    java.util.function.Supplier<Integer> getter,
                                    java.util.function.Consumer<Integer> setter,
                                    int min, int max, int step) {
        return builder.createIntegerOption(id(id))
                .setName(Component.translatable("reglass.options." + id + ".name"))
                .setTooltip(Component.translatable("reglass.options." + id + ".tooltip"))
                .setRange(min, max, step)
                .setDefaultValue(getter.get())
                .setBinding(setter, getter)
                .setStorageHandler(ReGlassSettingsIO::saveFromMemory);
    }

    private OptionBuilder floatOption(ConfigBuilder builder, String id,
                                      java.util.function.Supplier<Float> getter,
                                      java.util.function.Consumer<Float> setter,
                                      int min, int max, float step, int defaultScaled) {
        int scale = scaleFor(step);
        int scaledMin = Math.round(min * scale);
        int scaledMax = Math.round(max * scale);
        int scaledStep = Math.max(1, Math.round(step * scale));
        int scaledDefault = Math.round(getter.get() * scale);

        return builder.createIntegerOption(id(id))
                .setName(Component.translatable("reglass.options." + id + ".name"))
                .setTooltip(Component.translatable("reglass.options." + id + ".tooltip"))
                .setRange(scaledMin, scaledMax, scaledStep)
                .setValueFormatter(value -> Component.literal(format(value / (float) scale)))
                .setDefaultValue(scaledDefault == 0 && defaultScaled != 0 ? defaultScaled : scaledDefault)
                .setBinding(value -> setter.accept(value / (float) scale), () -> Math.round(getter.get() * scale))
                .setStorageHandler(ReGlassSettingsIO::saveFromMemory);
    }

    private int scaleFor(float step) {
        if (step >= 1f) return 1;
        if (step >= 0.1f) return 10;
        if (step >= 0.01f) return 100;
        return 1000;
    }

    private String format(float value) {
        if (Math.abs(value - Math.round(value)) < 0.0001f) {
            return Integer.toString(Math.round(value));
        }
        return String.format(java.util.Locale.ROOT, "%.3f", value);
    }

    private ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
