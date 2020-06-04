package engine.objects;

import engine.graphics.BoneMesh;
import engine.graphics.Mesh;
import engine.maths.Vector3f;

public class GameObject {
	private Vector3f position, rotation, scalar;
	private Tile tile;
	private Mesh[] meshes;
	
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
		position.setZ(position.getZ() - 0.5f);
	}

	public void create(boolean initTextureBuffer) {
		//System.out.println(meshes.length);
		//System.out.println((Mesh)meshes[0]);
		for (Mesh m : meshes) {
			m.create(initTextureBuffer);
		}
		//for (int i = 0; i < meshes.length; i++) {
		//	meshes[i].create(initTextureBuffer);
		//}
	}

	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getRotation() {
		return rotation;
	}
	
	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public Vector3f getScalar() {
		return scalar;
	}
	
	public void setScalar(Vector3f scalar) {
		this.scalar = scalar;
	}
	
	public Tile getTile() {
		return tile;
	}

	public Mesh[] getMeshes() {
		return meshes;
	}
}
