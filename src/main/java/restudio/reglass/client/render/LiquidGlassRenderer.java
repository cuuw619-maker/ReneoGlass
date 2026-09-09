package restudio.reglass.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import restudio.reglass.ReGlass;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Real Liquid Glass renderer for GUI widgets.
 *
 * The renderer snapshots the Minecraft main framebuffer into a separate texture,
 * then renders each glass capsule with a GLSL fragment shader that samples that
 * snapshot and performs UV displacement, chromatic dispersion, edge Fresnel and
 * specular highlighting. This is intentionally separate from the GuiGraphics
 * approximation used by the public API.
 */
public final class LiquidGlassRenderer {
    private static final ResourceLocation VERTEX =
            ResourceLocation.fromNamespaceAndPath(ReGlass.MOD_ID, "shaders/core/liquid_glass_real.vsh");
    private static final ResourceLocation FRAGMENT =
            ResourceLocation.fromNamespaceAndPath(ReGlass.MOD_ID, "shaders/core/liquid_glass_real.fsh");

    private static int program;
    private static int vao;
    private static int vbo;
    private static int snapshotTexture;
    private static int snapshotWidth;
    private static int snapshotHeight;
    private static boolean initialized;
    private static boolean frameReady;

    private static int uRect;
    private static int uRadius;
    private static int uScreen;
    private static int uRefraction;
    private static int uHighlight;
    private static int uTintAlpha;
    private static int uHover;
    private static int uTime;
    private static int uSampler;

    private LiquidGlassRenderer() {}

    public static void beginFrame() {
        if (!RenderSystem.isOnRenderThread()) return;
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();
        if (width <= 0 || height <= 0) return;

        ensureInitialized(width, height);
        if (!initialized) return;

        // The main render target is bound while ScreenEvent.Render.Post fires.
        // Copying into our own texture avoids sampling from and writing to the
        // same image in one pass (undefined feedback on OpenGL).
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, snapshotTexture);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, width, height);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        frameReady = true;
    }

    public static void renderWidget(float x, float y, float width, float height,
                                    float radius, float refraction, float highlight,
                                    float tintAlpha, float hover) {
        if (!frameReady || !initialized || width <= 0 || height <= 0) return;
        if (!RenderSystem.isOnRenderThread()) return;

        Minecraft mc = Minecraft.getInstance();
        float screenW = mc.getWindow().getGuiScaledWidth();
        float screenH = mc.getWindow().getGuiScaledHeight();
        if (screenW <= 0 || screenH <= 0) return;

        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, snapshotTexture);

        GL20.glUseProgram(program);
        GL30.glBindVertexArray(vao);

        GL20.glUniform4f(uRect, x, y, width, height);
        GL20.glUniform1f(uRadius, Math.min(radius, Math.min(width, height) * 0.5f));
        GL20.glUniform2f(uScreen, screenW, screenH);
        GL20.glUniform1f(uRefraction, Math.max(0f, Math.min(1f, refraction)));
        GL20.glUniform1f(uHighlight, Math.max(0f, Math.min(1f, highlight)));
        GL20.glUniform1f(uTintAlpha, Math.max(0f, Math.min(1f, tintAlpha)));
        GL20.glUniform1f(uHover, Math.max(0f, Math.min(1f, hover)));
        GL20.glUniform1f(uTime, (System.nanoTime() / 1_000_000_000.0f));
        GL20.glUniform1i(uSampler, 0);

        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);

        GL30.glBindVertexArray(0);
        GL20.glUseProgram(0);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        frameReady = false;
    }

    private static void ensureInitialized(int width, int height) {
        if (!initialized) {
            try {
                createResources(width, height);
            } catch (Throwable t) {
                initialized = false;
                ReGlass.LOGGER.error("Unable to initialize real Liquid Glass renderer", t);
            }
            return;
        }

        if (width != snapshotWidth || height != snapshotHeight) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, snapshotTexture);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0,
                    GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            snapshotWidth = width;
            snapshotHeight = height;
        }
    }

    private static void createResources(int width, int height) throws IOException {
        String vertexSource = loadResource(VERTEX);
        String fragmentSource = loadResource(FRAGMENT);

        int vertex = compile(GL20.GL_VERTEX_SHADER, vertexSource);
        int fragment = compile(GL20.GL_FRAGMENT_SHADER, fragmentSource);
        program = GL20.glCreateProgram();
        GL20.glAttachShader(program, vertex);
        GL20.glAttachShader(program, fragment);
        GL20.glLinkProgram(program);
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(program);
            throw new IllegalStateException("Liquid Glass shader link failed: " + log);
        }
        GL20.glDeleteShader(vertex);
        GL20.glDeleteShader(fragment);

        uRect = GL20.glGetUniformLocation(program, "uRect");
        uRadius = GL20.glGetUniformLocation(program, "uRadius");
        uScreen = GL20.glGetUniformLocation(program, "uScreen");
        uRefraction = GL20.glGetUniformLocation(program, "uRefraction");
        uHighlight = GL20.glGetUniformLocation(program, "uHighlight");
        uTintAlpha = GL20.glGetUniformLocation(program, "uTintAlpha");
        uHover = GL20.glGetUniformLocation(program, "uHover");
        uTime = GL20.glGetUniformLocation(program, "uTime");
        uSampler = GL20.glGetUniformLocation(program, "uSnapshot");

        float[] vertices = {
                0f, 0f,  0f, 0f,
                1f, 0f,  1f, 0f,
                0f, 1f,  0f, 1f,
                1f, 1f,  1f, 1f
        };
        FloatBuffer buffer = org.lwjgl.BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();

        vao = GL30.glGenVertexArrays();
        vbo = GL15.glGenBuffers();
        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 16, 0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 16, 8);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        snapshotTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, snapshotTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP_TO_EDGE);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        snapshotWidth = width;
        snapshotHeight = height;
        initialized = true;
    }

    private static int compile(int type, String source) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shader);
            GL20.glDeleteShader(shader);
            throw new IllegalStateException("Liquid Glass shader compile failed: " + log);
        }
        return shader;
    }

    private static String loadResource(ResourceLocation location) throws IOException {
        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(location);
        if (resource.isEmpty()) throw new IOException("Missing shader resource: " + location);
        try (InputStream in = resource.get().open()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
