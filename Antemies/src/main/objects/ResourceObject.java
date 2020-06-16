package main.objects;

import engine.graphics.Mesh;
import engine.maths.Vector3f;
import engine.model_loaders.StaticModelLoader;
import engine.objects.GameObject;

public class ResourceObject extends GameObject {

	public ResourceObject(Vector3f position, Vector3f rotation, Vector3f scalar, Mesh[] meshes) {
		super(position, rotation, scalar, meshes);
	}
	
	public ResourceObject(Vector3f position, Vector3f rotation, Vector3f scalar) throws Exception {
		this(position, rotation, scalar, StaticModelLoader.load("resources/models/watermelonSlice.obj", "/textures/antskin.jpg"));
	}
}
