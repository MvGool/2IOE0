#version 330 core

in vec2 passTextureCoord;
in vec3 passPos;
in vec3 passNormal;

out vec4 outColor;

uniform sampler2D tex;
uniform vec3 lightPos;
uniform vec3 viewPos;

void main() {
	vec3 lightColor = vec3(1);
	float ambientStrength = 0.8;
	vec3 ambient = ambientStrength * lightColor;

	vec3 norm = normalize(passNormal);
	vec3 lightDir = normalize(lightPos - passPos);
	float diff = max(dot(norm, lightDir), 0.0);
	vec3 diffuse = diff * lightColor;

	float specularStrength = 0.5;
	vec3 viewDir = normalize(viewPos - passPos);
	vec3 reflectDir = reflect(-lightDir, norm);
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
	vec3 specular = specularStrength * spec * lightColor;

	vec4 textureColor = texture(tex, passTextureCoord);

	outColor = vec4(ambient + diffuse + specular, 1.0) * textureColor;
	//outColor = vec4(passColor, 1.0);
}
