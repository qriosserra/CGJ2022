#version 120

uniform sampler2D sampler;

uniform vec4 color;

varying vec2 tex_coords;

void main() {
    gl_FragColor = texture2D(sampler, tex_coords);

    if (gl_FragColor.x == 1.0 && gl_FragColor.y == 1.0 && gl_FragColor.z == 1.0) {
        gl_FragColor = color;
    }
}