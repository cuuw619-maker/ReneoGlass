#version 150

layout(location = 0) in vec2 Position;
layout(location = 1) in vec2 UV0;

uniform vec4 uRect;
uniform vec2 uScreen;

out vec2 vLocal;

void main() {
    vec2 pixel = uRect.xy + UV0 * uRect.zw;
    vec2 ndc = vec2(pixel.x / uScreen.x * 2.0 - 1.0,
                    1.0 - pixel.y / uScreen.y * 2.0);
    gl_Position = vec4(ndc, 0.0, 1.0);
    vLocal = UV0;
}
