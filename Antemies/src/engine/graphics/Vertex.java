package engine.graphics;

import engine.maths.Vector2f;
import engine.maths.Vector3f;

public class Vertex {
	private Vector3f position;
	private Vector2f textureCoord;
	
	public Vertex(Vector3f position, Vector2f textureCoord) {
		this.position = position;
		this.textureCoord = textureCoord;
	}

	public Vertex(float x, float y, float z) {
		this.position = new Vector3f(x, y ,z);
	}

	public Vertex(Vector3f position, Vector2f textureCoord) {
		this.position = position;
		this.color = new Vector3f(1, 1, 1);
		this.textureCoord = textureCoord;
	}
	
	public Vector3f getPostion() {
		return position;
	}

	public Vector2f getTextureCoord() {
		return textureCoord;
	}
}
