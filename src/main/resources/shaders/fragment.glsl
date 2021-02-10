#version 300 es
precision highp float;

uniform vec3 u_ambientColor;
uniform vec3 u_diffuseColor;
uniform vec3 u_specularColor;

uniform float u_ambientIntensity;
uniform float u_shininess;

uniform vec3 u_lightPosition;

// Interpolated world position
in vec3 v_worldPosition;

// Interpolated surface normal
in vec3 v_worldNormal;

out vec4 fragColor;

void main() {
    vec3 N = normalize(v_worldNormal);
    vec3 L = normalize(u_lightPosition - v_worldPosition);

    // Lambert's cosine law
    float lambertian = max(dot(N, L), 0.0);

    // Compute specularity
    float specular = 0.0;
    if (lambertian > 0.0) {
        // Reflected light vector
        vec3 R = reflect(-L, N);

        // Vector to viewer
        vec3 V = normalize(-v_worldPosition);

        // Compute the specular term
        float specAngle = max(dot(R, V), 0.0);
        specular = pow(specAngle, u_shininess);
    }

    vec3 ambient = u_ambientIntensity * u_ambientColor * u_diffuseColor;
    vec3 diffuse = lambertian * u_diffuseColor;
    vec3 specularity = specular * u_specularColor;
    fragColor = vec4(ambient + diffuse + specularity, 1.0);
}

