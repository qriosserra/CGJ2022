#version 120

uniform sampler2D sampler;
uniform vec2 center;
uniform float time;
uniform vec3 shockParams;
uniform vec2 resolution;

varying vec2 tex_coords;

void main() {
    float m = sqrt(resolution.x * resolution.x + resolution.y * resolution.y) / 2.0F;
    vec2 coords = tex_coords.xy;
    vec2 uv = coords;
    float d = distance(coords * resolution, center * resolution) / m;
    if ((d <= (time + shockParams.z)) &&  (d >= (time - shockParams.z))) {
        float diff = (d - time);
        float powDiff = 1.0 - pow(abs(diff*shockParams.x), shockParams.y);
        float diffTime = diff  * powDiff;
        vec2 diffUV = normalize(coords - center);
        uv = coords + (diffUV * diffTime);
    }
    gl_FragColor = texture2D(sampler, uv);
}