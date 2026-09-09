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
 * Native Sodium Config API integration. Reese's Sodium Options consumes the
 * same API, so this page works in both Sodium's and Reese's option screens.
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
                .addPage(createRefractionPage(builder))
                .addPage(createGlarePage(builder))
                .addPage(createLightingPage(builder))
                .addPage(createInteractionPage(builder))
                .addPage(createAdvancedPage(builder));
    }

    private OptionPageBuilder createGeneralPage(ConfigBuilder b) {
        return b.createOptionPage().setName(t("page.general"))
                .addOptionGroup(b.createOptionGroup().setName(t("group.features"))
                        .addOption(bool(b, "enable_redesign", () -> CONFIG.features.enableRedesign, v -> CONFIG.features.enableRedesign = v))
                        .addOption(bool(b, "buttons", () -> CONFIG.features.buttons, v -> CONFIG.features.buttons = v))
                        .addOption(bool(b, "sliders", () -> CONFIG.features.sliders, v -> CONFIG.features.sliders = v))
                        .addOption(bool(b, "hotbar", () -> CONFIG.features.hotbar, v -> CONFIG.features.hotbar = v))
                        .addOption(bool(b, "cancel_screen_darkening", () -> CONFIG.features.cancelScreenDarkening, v -> CONFIG.features.cancelScreenDarkening = v))
                        .addOption(bool(b, "pixelated_grid", () -> CONFIG.features.pixelatedGrid, v -> CONFIG.features.pixelatedGrid = v)));
    }

    private OptionPageBuilder createAppearancePage(ConfigBuilder b) {
        return b.createOptionPage().setName(t("page.appearance"))
                .addOptionGroup(b.createOptionGroup().setName(t("group.appearance"))
                        .addOption(intOpt(b, "tint_color", () -> CONFIG.defaultTintColor, v -> CONFIG.defaultTintColor = v, 0, 0xFFFFFF, 1))
                        .addOption(floatOpt(b, "tint_alpha", () -> CONFIG.defaultTintAlpha, v -> CONFIG.defaultTintAlpha = v, 0, 1, 0.01f))
                        .addOption(intOpt(b, "blur_radius", () -> CONFIG.defaultBlurRadius, v -> CONFIG.defaultBlurRadius = v, 0, 64, 1))
                        .addOption(floatOpt(b, "smoothing", () -> CONFIG.defaultSmoothing, v -> CONFIG.defaultSmoothing = v, -0.02f, 0.02f, 0.001f)))
                .addOptionGroup(b.createOptionGroup().setName(t("group.shadow"))
                        .addOption(floatOpt(b, "shadow_expand", () -> CONFIG.defaultShadowExpand, v -> CONFIG.defaultShadowExpand = v, 0, 100, 0.5f))
                        .addOption(floatOpt(b, "shadow_factor", () -> CONFIG.defaultShadowFactor, v -> CONFIG.defaultShadowFactor = v, 0, 1, 0.01f))
                        .addOption(floatOpt(b, "shadow_offset_x", () -> CONFIG.defaultShadowOffsetX, v -> CONFIG.defaultShadowOffsetX = v, -50, 50, 0.1f))
                        .addOption(floatOpt(b, "shadow_offset_y", () -> CONFIG.defaultShadowOffsetY, v -> CONFIG.defaultShadowOffsetY = v, -50, 50, 0.1f))
                        .addOption(intOpt(b, "shadow_color", () -> CONFIG.defaultShadowColor, v -> CONFIG.defaultShadowColor = v, 0, 0xFFFFFF, 1))
                        .addOption(floatOpt(b, "shadow_color_alpha", () -> CONFIG.defaultShadowColorAlpha, v -> CONFIG.defaultShadowColorAlpha = v, 0, 1, 0.01f)));
    }

    private OptionPageBuilder createRefractionPage(ConfigBuilder b) {
        return b.createOptionPage().setName(t("page.refraction"))
                .addOptionGroup(b.createOptionGroup().setName(t("group.refraction"))
                        .addOption(floatOpt(b, "refraction_thickness", () -> CONFIG.defaultRefThickness, v -> CONFIG.defaultRefThickness = v, 0, 100, 0.5f))
                        .addOption(floatOpt(b, "refraction_factor", () -> CONFIG.defaultRefFactor, v -> CONFIG.defaultRefFactor = v, 0.5f, 4, 0.01f))
                        .addOption(floatOpt(b, "dispersion", () -> CONFIG.defaultRefDispersion, v -> CONFIG.defaultRefDispersion = v, 0, 100, 0.1f))
                        .addOption(floatOpt(b, "fresnel_range", () -> CONFIG.defaultRefFresnelRange, v -> CONFIG.defaultRefFresnelRange = v, 0, 100, 0.5f))
                        .addOption(floatOpt(b, "fresnel_hardness", () -> CONFIG.defaultRefFresnelHardness, v -> CONFIG.defaultRefFresnelHardness = v, 0, 100, 0.5f))
                        .addOption(floatOpt(b, "fresnel_factor", () -> CONFIG.defaultRefFresnelFactor, v -> CONFIG.defaultRefFresnelFactor = v, 0, 100, 0.5f)));
    }

    private OptionPageBuilder createGlarePage(ConfigBuilder b) {
        return b.createOptionPage().setName(t("page.glare"))
                .addOptionGroup(b.createOptionGroup().setName(t("group.glare"))
                        .addOption(floatOpt(b, "glare_range", () -> CONFIG.defaultGlareRange, v -> CONFIG.defaultGlareRange = v, 0, 100, 0.5f))
                        .addOption(floatOpt(b, "glare_hardness", () -> CONFIG.defaultGlareHardness, v -> CONFIG.defaultGlareHardness = v, 0, 100, 0.5f))
                        .addOption(floatOpt(b, "glare_convergence", () -> CONFIG.defaultGlareConvergence, v -> CONFIG.defaultGlareConvergence = v, 0, 100, 0.5f))
                        .addOption(floatOpt(b, "glare_opposite_factor", () -> CONFIG.defaultGlareOppositeFactor, v -> CONFIG.defaultGlareOppositeFactor = v, 0, 100, 0.5f))
                        .addOption(floatOpt(b, "glare_factor", () -> CONFIG.defaultGlareFactor, v -> CONFIG.defaultGlareFactor = v, 0, 100, 0.5f))
                        .addOption(intOpt(b, "glare_angle", () -> Math.round(CONFIG.defaultGlareAngleRad * 180f / (float) Math.PI), v -> CONFIG.defaultGlareAngleRad = v * (float) Math.PI / 180f, -180, 180, 1)));
    }

    private OptionPageBuilder createLightingPage(ConfigBuilder b) {
        return b.createOptionPage().setName(t("page.lighting"))
                .addOptionGroup(b.createOptionGroup().setName(t("group.rim_light"))
                        .addOption(floatOpt(b, "rim_light_x", () -> CONFIG.rimLight.direction().x, v -> rebuildRim(v, CONFIG.rimLight.direction().y), -1, 1, 0.01f))
                        .addOption(floatOpt(b, "rim_light_y", () -> CONFIG.rimLight.direction().y, v -> rebuildRim(CONFIG.rimLight.direction().x, v), -1, 1, 0.01f))
                        .addOption(intOpt(b, "rim_light_color", () -> CONFIG.rimLight.color(), v -> rebuildRim(CONFIG.rimLight.direction().x, CONFIG.rimLight.direction().y, v, CONFIG.rimLight.intensity()), 0, 0xFFFFFF, 1))
                        .addOption(floatOpt(b, "rim_light_intensity", () -> CONFIG.rimLight.intensity(), v -> rebuildRim(CONFIG.rimLight.direction().x, CONFIG.rimLight.direction().y, CONFIG.rimLight.color(), v), 0, 2, 0.01f)));
    }

    private OptionPageBuilder createInteractionPage(ConfigBuilder b) {
        return b.createOptionPage().setName(t("page.interaction"))
                .addOptionGroup(b.createOptionGroup().setName(t("group.interaction"))
                        .addOption(floatOpt(b, "hover_scale", () -> CONFIG.hoverScalePx, v -> CONFIG.hoverScalePx = v, 0, 20, 0.1f))
                        .addOption(floatOpt(b, "focus_scale", () -> CONFIG.focusScalePx, v -> CONFIG.focusScalePx = v, 0, 20, 0.1f))
                        .addOption(floatOpt(b, "focus_border_width", () -> CONFIG.focusBorderWidthPx, v -> CONFIG.focusBorderWidthPx = v, 0, 20, 0.1f))
                        .addOption(floatOpt(b, "focus_border_intensity", () -> CONFIG.focusBorderIntensity, v -> CONFIG.focusBorderIntensity = v, 0, 2, 0.01f))
                        .addOption(floatOpt(b, "focus_border_speed", () -> CONFIG.focusBorderSpeed, v -> CONFIG.focusBorderSpeed = v, 0, 20, 0.1f))
                        .addOption(floatOpt(b, "pixelated_grid_size", () -> CONFIG.pixelatedGridSize, v -> CONFIG.pixelatedGridSize = v, 1, 64, 0.5f)));
    }

    private OptionPageBuilder createAdvancedPage(ConfigBuilder b) {
        return b.createOptionPage().setName(t("page.advanced"))
                .addOptionGroup(b.createOptionGroup().setName(t("group.runtime"))
                        .addOption(floatOpt(b, "pixel_epsilon", () -> CONFIG.pixelEpsilon, v -> CONFIG.pixelEpsilon = v, 0, 20, 0.05f))
                        .addOption(floatOpt(b, "debug_step", () -> CONFIG.debugStep, v -> CONFIG.debugStep = v, 0, 20, 0.1f)));
    }

    private void rebuildRim(float x, float y) {
        rebuildRim(x, y, CONFIG.rimLight.color(), CONFIG.rimLight.intensity());
    }

    private void rebuildRim(float x, float y, int color, float intensity) {
        float len = (float) Math.sqrt(x * x + y * y);
        if (len < 0.0001f) { x = -1f; y = 1f; len = (float) Math.sqrt(2); }
        CONFIG.rimLight = new restudio.reglass.client.api.model.RimLight(new org.joml.Vector2f(x / len, y / len), color, intensity);
    }

    private Component t(String key) {
        return Component.translatable("reglass.options." + key);
    }

    private OptionBuilder bool(ConfigBuilder b, String id,
                               java.util.function.Supplier<Boolean> getter,
                               java.util.function.Consumer<Boolean> setter) {
        return b.createBooleanOption(id(id))
                .setName(t(id + ".name"))
                .setTooltip(t(id + ".tooltip"))
                .setDefaultValue(getter.get())
                .setBinding(setter, getter)
                .setStorageHandler(ReGlassSettingsIO::saveFromMemory)
                .setApplyHook(ReGlassSettingsIO::saveFromMemory);
    }

    private OptionBuilder intOpt(ConfigBuilder b, String id,
                                 java.util.function.Supplier<Integer> getter,
                                 java.util.function.Consumer<Integer> setter,
                                 int min, int max, int step) {
        return b.createIntegerOption(id(id))
                .setName(t(id + ".name"))
                .setTooltip(t(id + ".tooltip"))
                .setRange(min, max, step)
                .setDefaultValue(getter.get())
                .setBinding(setter, getter)
                .setStorageHandler(ReGlassSettingsIO::saveFromMemory)
                .setApplyHook(ReGlassSettingsIO::saveFromMemory);
    }

    private OptionBuilder floatOpt(ConfigBuilder b, String id,
                                   java.util.function.Supplier<Float> getter,
                                   java.util.function.Consumer<Float> setter,
                                   float min, float max, float step) {
        int scale = scaleFor(step);
        int scaledMin = Math.round(min * scale);
        int scaledMax = Math.round(max * scale);
        int scaledStep = Math.max(1, Math.round(step * scale));
        return b.createIntegerOption(id(id))
                .setName(t(id + ".name"))
                .setTooltip(t(id + ".tooltip"))
                .setRange(scaledMin, scaledMax, scaledStep)
                .setValueFormatter(v -> Component.literal(format(v / (float) scale)))
                .setDefaultValue(Math.round(getter.get() * scale))
                .setBinding(v -> setter.accept(v / (float) scale), () -> Math.round(getter.get() * scale))
                .setStorageHandler(ReGlassSettingsIO::saveFromMemory)
                .setApplyHook(ReGlassSettingsIO::saveFromMemory);
    }

    private int scaleFor(float step) {
        if (step >= 1f) return 1;
        if (step >= 0.1f) return 10;
        if (step >= 0.01f) return 100;
        return 1000;
    }

    private String format(float value) {
        if (Math.abs(value - Math.round(value)) < 0.0001f) return Integer.toString(Math.round(value));
        return String.format(java.util.Locale.ROOT, "%.3f", value);
    }

    private ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
