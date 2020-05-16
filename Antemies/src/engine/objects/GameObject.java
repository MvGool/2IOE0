package engine.objects;

import engine.graphics.Mesh;
import engine.maths.Vector3f;

public class GameObject {
	private Vector3f position, rotation, scalar;
	private Mesh[] meshes;
	private double temp;
	
	public GameObject(Vector3f position, Vector3f rotation, Vector3f scalar, Mesh[] meshes) {
		this.position = position;
		this.rotation = rotation;
		this.scalar = scalar;
		this.meshes = meshes;
	}

	public GameObject(Vector3f position, Vector3f rotation, Vector3f scalar, Mesh mesh) {
		this(position, rotation, scalar, new Mesh[]{mesh});
	}
	
	public void update() {
		temp += 0.02;
		position.setX((float) Math.sin(temp));
		rotation.set((float) Math.sin(temp) * 360, (float) Math.sin(temp) * 360, (float) Math.sin(temp) * 360);
		scalar.set((float) Math.sin(temp), (float) Math.sin(temp), (float) Math.sin(temp));
	}

	public void create(boolean initTextureBuffer) {
		for (Mesh m : meshes) {
			m.create(initTextureBuffer);
		}
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public Vector3f getScalar() {
		return scalar;
	}

	public Mesh[] getMeshes() {
		return meshes;
	}
}
