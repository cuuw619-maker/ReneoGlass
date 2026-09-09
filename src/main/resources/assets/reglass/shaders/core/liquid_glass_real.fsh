#version 150

uniform sampler2D uSnapshot;
uniform vec4 uRect;
uniform float uRadius;
uniform vec2 uScreen;
uniform float uRefraction;
uniform float uHighlight;
uniform float uTintAlpha;
uniform float uHover;
uniform float uTime;

in vec2 vLocal;
out vec4 fragColor;

float roundedBoxSdf(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + r;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;
}

vec3 sampleLens(vec2 uv, vec2 normal, float edge, float curvature) {
    // Refraction grows toward the curved edge, while remaining neutral in the
    // center of the lens. The source is the real framebuffer snapshot.
    float edgeCurve = smoothstep(0.0, 1.0, curvature);
    vec2 offset = normal * (uRefraction * (2.0 + 10.0 * edgeCurve) * edge) / uScreen;

    float chroma = (0.35 + 0.65 * uHover) * uRefraction * edge * 1.25 / uScreen.x;
    vec2 tangent = vec2(-normal.y, normal.x);

    float r = texture(uSnapshot, uv + offset + tangent * chroma).r;
    float g = texture(uSnapshot, uv + offset).g;
    float b = texture(uSnapshot, uv + offset - tangent * chroma).b;
    return vec3(r, g, b);
}

void main() {
    vec2 pixel = uRect.xy + vLocal * uRect.zw;
    vec2 center = uRect.xy + uRect.zw * 0.5;
    vec2 local = pixel - center;
    vec2 halfSize = uRect.zw * 0.5;

    float d = roundedBoxSdf(local, halfSize, uRadius);
    if (d > 0.0) discard;

    float edge = clamp(-d / max(uRadius, 1.0), 0.0, 1.0);
    float rim = smoothstep(0.0, 0.18, -d);
    float inner = 1.0 - smoothstep(0.15, 0.8, edge);

    vec2 normal = normalize(vec2(
        sign(local.x) * max(abs(local.x) - halfSize.x + uRadius, 0.0),
        sign(local.y) * max(abs(local.y) - halfSize.y + uRadius, 0.0)
    ));
    if (length(normal) < 0.001) normal = normalize(local + vec2(0.001));

    // GUI coordinates have a top-left origin; convert to framebuffer UVs.
    vec2 uv = vec2(pixel.x / uScreen.x, 1.0 - pixel.y / uScreen.y);
    vec3 refracted = sampleLens(uv, normal, edge, 1.0 - inner);

    // Five-tap micro-blur produces the soft optical volume without replacing
    // the underlying image with a flat translucent rectangle.
    vec2 texel = 1.0 / uScreen;
    vec3 soft = refracted * 0.42;
    soft += texture(uSnapshot, uv + vec2(texel.x * 1.4, 0.0)).rgb * 0.14;
    soft += texture(uSnapshot, uv - vec2(texel.x * 1.4, 0.0)).rgb * 0.14;
    soft += texture(uSnapshot, uv + vec2(0.0, texel.y * 1.4)).rgb * 0.14;
    soft += texture(uSnapshot, uv - vec2(0.0, texel.y * 1.4)).rgb * 0.14;

    // Volumetric body: the center stays transparent enough to reveal the
    // framebuffer, while the curved rim becomes denser like a glass lens.
    vec3 body = mix(refracted, soft, 0.42 * inner);
    vec3 glassTint = vec3(0.94, 0.97, 1.0);
    body = mix(body, glassTint, uTintAlpha * (0.20 + 0.18 * inner));

    // Moving reflection band. It is view-dependent and animated rather than a
    // static white border, so the capsule reads as a curved optical surface.
    float sweep = 0.5 + 0.5 * sin((pixel.x + pixel.y) * 0.035 + uTime * 1.7);
    float reflection = pow(max(0.0, sweep), 8.0) * uHighlight;
    reflection *= 0.35 + 0.65 * (0.35 + 0.65 * rim);

    float fresnel = pow(1.0 - max(0.0, dot(normal, vec2(0.0, -1.0))), 3.0);
    vec3 rimLight = vec3(0.90, 0.96, 1.0) * (0.16 * uHighlight + 0.16 * fresnel);
    body += rimLight * rim;
    body += vec3(1.0) * reflection;

    // Hover changes the optical field, not merely the alpha of the widget.
    body += vec3(0.025, 0.035, 0.05) * uHover * inner;

    fragColor = vec4(clamp(body, 0.0, 1.0), 1.0);
}
