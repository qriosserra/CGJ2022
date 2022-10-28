#version 120

uniform sampler2D sampler;

uniform float radius;
uniform vec2 center;
uniform vec2 resolution;
uniform vec3 color;
uniform bool gradient;

varying vec2 tex_coords;

void main() {
    gl_FragColor = vec4(color, 0);

    float d = distance(tex_coords.xy * resolution, center * resolution);
    if (d > radius) {
        if (gradient) {
            float m = sqrt(resolution.x * resolution.x + resolution.y * resolution.y) / 2.0F;
            gl_FragColor = vec4(color, (d - radius) / (m / 3.0F));
        } else gl_FragColor = vec4(color, 1);

        if (gl_FragColor.y > 1) gl_FragColor.y = 1;
        if (gl_FragColor.y < 0) gl_FragColor.y = 0;
    }
}