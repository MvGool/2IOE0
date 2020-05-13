#version 330 core

in vec3 position;
in vec3 color;

out vec3 passColor;

uniform mat4 model;

void main() {
	gl_Position = vec4(position, 1.0) * model;
	passColor = color;
}
