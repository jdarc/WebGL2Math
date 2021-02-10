#version 300 es

uniform mat4 u_modelViewMatrix;
uniform mat4 u_normalMatrix;
uniform mat4 u_projectionMatrix;

in vec3 a_position;
in vec3 a_normal;

out vec3 v_worldPosition;
out vec3 v_worldNormal;

void main() {
    // Transform vertex from model space to view space
    vec4 transformed = u_modelViewMatrix * vec4(a_position, 1.0);

    // Divide by w to take transformed point into world space
    v_worldPosition = vec3(transformed) / transformed.w;

    // Transform normal
    v_worldNormal = vec3(u_normalMatrix * vec4(a_normal, 0.0));

    // Project transformed vertex
    gl_Position = u_projectionMatrix * transformed;
}
