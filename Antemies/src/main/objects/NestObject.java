package main.objects;

import engine.graphics.Mesh;
import engine.maths.Vector3f;
import engine.model_loaders.StaticModelLoader;
import engine.objects.GameObject;

public class NestObject extends GameObject {
	
	private int ants = 10;
	private int food = 50;

	public NestObject(Vector3f position, Vector3f rotation, Vector3f scalar, Mesh[] meshes) {
		super(position, rotation, scalar, meshes);
	}
	
	public NestObject(Vector3f position, Vector3f rotation, Vector3f scalar, int ants, int food) throws Exception {
		this(position, rotation, scalar, StaticModelLoader.load("resources/models/ant_nest.obj", "/models/forrest_ground_03_diff_1k.jpg"));
		this.ants = ants;
		this.food = food;
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
}