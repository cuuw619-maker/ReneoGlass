package restudio.reglass.client.config;

import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPointForge;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import restudio.reglass.client.api.ReGlassConfig;

@ConfigEntryPointForge("reglass")
public final class ReGlassSodiumConfig implements ConfigEntryPoint {
    private static final String MOD_ID = "reglass";
    private static final ReGlassConfig CONFIG = ReGlassConfig.INSTANCE;

    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        builder.registerOwnModOptions()
                .setName("ReGlass")
                .setNonTintedIcon(ResourceLocation.fromNamespaceAndPath(MOD_ID, "icon.png"))
                .addPage(createPage(builder));
    }

    private OptionPageBuilder createPage(ConfigBuilder b) {
        return b.createOptionPage().setName(t("page.liquid_glass"))
                .addOptionGroup(b.createOptionGroup().setName(t("group.liquid_glass"))
                        .addOption(bool(b, "enable", () -> CONFIG.features.enableRedesign,
                                v -> CONFIG.features.enableRedesign = v))
                        .addOption(floatOpt(b, "glass_opacity", () -> CONFIG.defaultTintAlpha,
                                v -> CONFIG.defaultTintAlpha = v, 0, 1, 0.01f))
                        .addOption(floatOpt(b, "refraction", () -> CONFIG.defaultRefFactor,
                                v -> CONFIG.defaultRefFactor = v, 0, 3, 0.01f))
                        .addOption(floatOpt(b, "highlight", () -> CONFIG.defaultGlareFactor / 100f,
                                v -> CONFIG.defaultGlareFactor = v * 100f, 0, 1, 0.01f))
                        .addOption(floatOpt(b, "morphing", () -> CONFIG.focusBorderSpeed,
                                v -> CONFIG.focusBorderSpeed = v, 0, 4, 0.05f)));
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
                .setApplyHook(state -> ReGlassSettingsIO.saveFromMemory());
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
                .setBinding(v -> setter.accept(v / (float) scale),
                        () -> Math.round(getter.get() * scale))
                .setStorageHandler(ReGlassSettingsIO::saveFromMemory)
                .setApplyHook(state -> ReGlassSettingsIO.saveFromMemory());
    }

    private int scaleFor(float step) {
        if (step >= 1f) return 1;
        if (step >= 0.1f) return 10;
        if (step >= 0.01f) return 100;
        return 1000;
    }

    private String format(float value) {
        if (Math.abs(value - Math.round(value)) < 0.0001f) return Integer.toString(Math.round(value));
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }

    private Component t(String key) {
        return Component.translatable("reglass.options." + key);
    }

    private ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
