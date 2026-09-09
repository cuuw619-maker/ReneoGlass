package restudio.reglass.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import net.neoforged.fml.loading.FMLPaths;
import org.joml.Vector2f;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.client.api.model.RimLight;

public final class ReGlassSettingsIO {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ReGlassSettingsIO() {}

    private static Path configPath() {
        return FMLPaths.CONFIGDIR.get().resolve("reglass.json");
    }

    public static void loadIntoMemory() {
        try {
            Path p = configPath();
            if (!Files.exists(p)) {
                saveFromMemory();
                return;
            }
            try (Reader r = Files.newBufferedReader(p)) {
                Data d = GSON.fromJson(r, Data.class);
                if (d != null) apply(d);
            }
        } catch (Exception ignored) {
        }
    }

    public static void saveFromMemory() {
        try {
            Path p = configPath();
            Files.createDirectories(p.getParent());
            try (Writer w = Files.newBufferedWriter(p)) {
                GSON.toJson(snapshot(), w);
            }
        } catch (Exception ignored) {
        }
    }

    private static Data snapshot() {
        ReGlassConfig c = ReGlassConfig.INSTANCE;
        Data d = new Data();
        d.features_enableRedesign = c.features.enableRedesign;
        d.features_buttons = c.features.buttons;
        d.features_sliders = c.features.sliders;
        d.features_hotbar = c.features.hotbar;
        d.features_cancelScreenDarkening = c.features.cancelScreenDarkening;
        d.features_pixelatedGrid = c.features.pixelatedGrid;

        d.tintColor = c.defaultTintColor;
        d.tintAlpha = c.defaultTintAlpha;
        d.smoothing = c.defaultSmoothing;
        d.blurRadius = c.defaultBlurRadius;
        d.shadowExpand = c.defaultShadowExpand;
        d.shadowFactor = c.defaultShadowFactor;
        d.shadowOffsetX = c.defaultShadowOffsetX;
        d.shadowOffsetY = c.defaultShadowOffsetY;
        d.shadowColor = c.defaultShadowColor;
        d.shadowColorAlpha = c.defaultShadowColorAlpha;
        d.refThickness = c.defaultRefThickness;
        d.refFactor = c.defaultRefFactor;
        d.refDispersion = c.defaultRefDispersion;
        d.refFresnelRange = c.defaultRefFresnelRange;
        d.refFresnelHardness = c.defaultRefFresnelHardness;
        d.refFresnelFactor = c.defaultRefFresnelFactor;
        d.glareRange = c.defaultGlareRange;
        d.glareHardness = c.defaultGlareHardness;
        d.glareConvergence = c.defaultGlareConvergence;
        d.glareOppositeFactor = c.defaultGlareOppositeFactor;
        d.glareFactor = c.defaultGlareFactor;
        d.glareAngleRad = c.defaultGlareAngleRad;
        d.rimLightX = c.rimLight.direction().x;
        d.rimLightY = c.rimLight.direction().y;
        d.rimLightColor = c.rimLight.color();
        d.rimLightIntensity = c.rimLight.intensity();
        d.pixelEpsilon = c.pixelEpsilon;
        d.debugStep = c.debugStep;
        d.pixelatedGridSize = c.pixelatedGridSize;
        d.hoverScalePx = c.hoverScalePx;
        d.focusScalePx = c.focusScalePx;
        d.focusBorderWidthPx = c.focusBorderWidthPx;
        d.focusBorderIntensity = c.focusBorderIntensity;
        d.focusBorderSpeed = c.focusBorderSpeed;
        d.classWhitelist.addAll(c.features.classWhitelist);
        d.classBlacklist.addAll(c.features.classBlacklist);
        return d;
    }

    public static void apply(Data d) {
        ReGlassConfig c = ReGlassConfig.INSTANCE;
        c.features.enableRedesign = d.features_enableRedesign;
        c.features.buttons = d.features_buttons;
        c.features.sliders = d.features_sliders;
        c.features.hotbar = d.features_hotbar;
        c.features.cancelScreenDarkening = d.features_cancelScreenDarkening;
        c.features.pixelatedGrid = d.features_pixelatedGrid;
        c.defaultTintColor = d.tintColor;
        c.defaultTintAlpha = d.tintAlpha;
        c.defaultSmoothing = d.smoothing;
        c.defaultBlurRadius = d.blurRadius;
        c.defaultShadowExpand = d.shadowExpand;
        c.defaultShadowFactor = d.shadowFactor;
        c.defaultShadowOffsetX = d.shadowOffsetX;
        c.defaultShadowOffsetY = d.shadowOffsetY;
        c.defaultShadowColor = d.shadowColor;
        c.defaultShadowColorAlpha = d.shadowColorAlpha;
        c.defaultRefThickness = d.refThickness;
        c.defaultRefFactor = d.refFactor;
        c.defaultRefDispersion = d.refDispersion;
        c.defaultRefFresnelRange = d.refFresnelRange;
        c.defaultRefFresnelHardness = d.refFresnelHardness;
        c.defaultRefFresnelFactor = d.refFresnelFactor;
        c.defaultGlareRange = d.glareRange;
        c.defaultGlareHardness = d.glareHardness;
        c.defaultGlareConvergence = d.glareConvergence;
        c.defaultGlareOppositeFactor = d.glareOppositeFactor;
        c.defaultGlareFactor = d.glareFactor;
        c.defaultGlareAngleRad = d.glareAngleRad;
        c.pixelEpsilon = d.pixelEpsilon;
        c.debugStep = d.debugStep;
        c.pixelatedGridSize = d.pixelatedGridSize;
        c.hoverScalePx = d.hoverScalePx;
        c.focusScalePx = d.focusScalePx;
        c.focusBorderWidthPx = d.focusBorderWidthPx;
        c.focusBorderIntensity = d.focusBorderIntensity;
        c.focusBorderSpeed = d.focusBorderSpeed;
        c.features.classWhitelist.clear();
        c.features.classWhitelist.addAll(d.classWhitelist);
        c.features.classBlacklist.clear();
        c.features.classBlacklist.addAll(d.classBlacklist);
        Vector2f direction = new Vector2f(d.rimLightX, d.rimLightY);
        if (direction.lengthSquared() < 0.000001f) direction.set(-1f, 1f);
        c.rimLight = new RimLight(direction.normalize(), d.rimLightColor, d.rimLightIntensity);
    }

    public static class Data {
        public boolean features_enableRedesign = true;
        public boolean features_buttons = true;
        public boolean features_sliders = true;
        public boolean features_hotbar = true;
        public boolean features_cancelScreenDarkening = true;
        public boolean features_pixelatedGrid = false;
        public int tintColor = 0x000000;
        public float tintAlpha = 0f;
        public float smoothing = 0.003f;
        public int blurRadius = 12;
        public float shadowExpand = 30.0f;
        public float shadowFactor = 0.25f;
        public float shadowOffsetX = 0.0f;
        public float shadowOffsetY = 2.0f;
        public int shadowColor = 0x000000;
        public float shadowColorAlpha = 1.0f;
        public float refThickness = 20.0f;
        public float refFactor = 1.4f;
        public float refDispersion = 7.0f;
        public float refFresnelRange = 30.0f;
        public float refFresnelHardness = 20.0f;
        public float refFresnelFactor = 20.0f;
        public float glareRange = 30.0f;
        public float glareHardness = 20.0f;
        public float glareConvergence = 50.0f;
        public float glareOppositeFactor = 80.0f;
        public float glareFactor = 90.0f;
        public float glareAngleRad = (float) (-45.0 * Math.PI / 180.0);
        public float rimLightX = -0.7071f;
        public float rimLightY = 0.7071f;
        public int rimLightColor = 0xFFFFFF;
        public float rimLightIntensity = 0.1f;
        public float pixelEpsilon = 2.0f;
        public float debugStep = 9.0f;
        public float pixelatedGridSize = 8.0f;
        public float hoverScalePx = 1.5f;
        public float focusScalePx = 2.5f;
        public float focusBorderWidthPx = 2.0f;
        public float focusBorderIntensity = 0.75f;
        public float focusBorderSpeed = 1.6f;
        public java.util.Set<String> classWhitelist = new java.util.HashSet<>();
        public java.util.Set<String> classBlacklist = new java.util.HashSet<>();
    }
}
