#version 120

attribute vec3 vertices;
attribute vec2 textures;

varying vec2 tex_coords;

uniform mat4 projection;
uniform mat4 tex_modifier;

void main() {
    tex_coords = (tex_modifier * vec4(textures, 0, 1)).xy;
    gl_Position = projection * vec4(vertices, 1);
}