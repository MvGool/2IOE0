package engine.graphics;

import engine.maths.Vector2f;
import engine.maths.Vector3f;

public class Vertex {

	private Vector3f position, normal, color = new Vector3f(0, 0, 0);
	private Vector2f textureCoord;
	private float transparency = 1.0f;
	
	public Vertex(Vector3f position, Vector3f normal, Vector2f textureCoord) {
		this.position = position;
		this.normal = normal;
		this.textureCoord = textureCoord;
	}
	
	public Vertex(Vector3f position, Vector3f normal, Vector3f color) {
		this.position = position;
		this.normal = normal;
		this.color = color;
	}
	
	public Vertex(Vector3f position, Vector3f normal, Vector3f color, float transparency) {
		this.position = position;
		this.normal = normal;
		this.color = color;
		this.transparency = transparency;
	}

	public Vertex(float x, float y, float z) {
		this.position = new Vector3f(x, y ,z);
	}
	
	public Vector3f getPostion() {
		return position;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public Vector3f getNormal() {
		return normal;
	}

	public Vector2f getTextureCoord() {
		return textureCoord;
	}
	
	public Vector3f getColor() {
		return color;
	}
	
	public float getTransparency() {
		return transparency;
	}
}
