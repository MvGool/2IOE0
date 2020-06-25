package main.objects;

import engine.graphics.Mesh;
import engine.maths.Vector3f;
import engine.model_loaders.StaticModelLoader;
import engine.objects.GameObject;

public class NestObject extends GameObject {
	private int ants;
	private int food;
	private int material;

	public NestObject(Vector3f position, Vector3f rotation, Vector3f scalar, Mesh[] meshes) {
		super(position, rotation, scalar, meshes);
	}
	
	public NestObject(Vector3f position, Vector3f rotation, Vector3f scalar, int ants, int food, int material) throws Exception {
		this(position, rotation, scalar, StaticModelLoader.load("resources/models/ant_nest.obj", "/models/forrest_ground_03_diff_1k.jpg"));
		this.ants = ants;
		this.food = food;
		this.material = material;
	}

	public int getAnts() {
		return ants;
	}

	public void setAnts(int ants) {
		this.ants = ants;
	}

	public int getFood() {
		return food;
	}

	public void setFood(int food) {
		this.food = food;
	}
	
	public int getMaterial() {
		return material;
	}

	public void setMaterial(int material) {
		this.material = material;
	}
}