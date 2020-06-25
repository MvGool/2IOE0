#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 textureCoord;
layout(location = 3) in vec4 boneWeights;
layout(location = 4) in ivec4 boneIndices;

out vec2 passTextureCoord;
out vec3 passPos;
out vec3 passNormal;
out mat4 passModelViewMatrix;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 boneMatrix[200];
uniform bool useSkeleton;

void main() {
	vec4 endPosition = vec4(0, 0, 0, 0);
	if (useSkeleton) {
		mat4 transform;
		for (int i = 0; i < 4; i++) {
			float weight = boneWeights[i];
			if (weight > 0) {
				transform += boneMatrix[boneIndices[i]] * weight;
			}
		}
		endPosition = transform * vec4(position, 1.0);
	} else {
		endPosition = vec4(position, 1.0);
	}
	gl_Position = projection * view * model * endPosition;
	passTextureCoord = textureCoord;
	passPos = vec3(model * endPosition);
	passNormal = normalize(view * model * vec4(normal, 0.0)).xyz;
	passModelViewMatrix = view * model;
}
