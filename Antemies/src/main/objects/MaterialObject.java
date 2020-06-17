package main.objects;

import engine.graphics.Mesh;
import engine.maths.Vector3f;
import engine.model_loaders.StaticModelLoader;
import engine.objects.GameObject;

public class MaterialObject extends GameObject {

	public MaterialObject(Vector3f position, Vector3f rotation, Vector3f scalar, Mesh[] meshes) {
		super(position, rotation, scalar, meshes);
	}
	
	public MaterialObject(Vector3f position, Vector3f rotation, Vector3f scalar) throws Exception {
		this(position, rotation, scalar, StaticModelLoader.load("resources/models/nest_material.obj", "/models/forrest_ground_03_diff_1k.jpg"));
	}
}
