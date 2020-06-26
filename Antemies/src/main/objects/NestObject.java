package main.objects;

import java.util.ArrayList;
import java.util.Collections;

import engine.graphics.Mesh;
import engine.maths.Vector3f;
import engine.model_loaders.StaticModelLoader;
import engine.objects.GameObject;

public class NestObject extends GameObject {
	private final int INITIAL_ANTS = 1; // the number of ants at the beginning of the game
	private Mesh[] antMesh;
	private int food;
	private int material;
	private ArrayList<AntObject> ants;

	public NestObject(Vector3f position, Vector3f rotation, Vector3f scalar, Mesh[] meshes) throws Exception {
		super(position, rotation, scalar, meshes);
		food = 0;
		material = 0;
		ants = new ArrayList<AntObject>();
		antMesh = StaticModelLoader.load("resources/models/testmodels/Ant_fbx.fbx", "/textures/antskin.jpg");
		//generateAnts(INITIAL_ANTS);
	}

	public NestObject(Vector3f position, Vector3f rotation, Vector3f scalar) throws Exception {
		this(position, rotation, scalar, StaticModelLoader.load("resources/models/ant_nest.obj", "/models/forrest_ground_03_diff_1k.jpg"));
		food = 0;
		material = 0;
		ants = new ArrayList<AntObject>();
		antMesh = StaticModelLoader.load("resources/models/testmodels/Ant_fbx.fbx", "/textures/antskin.jpg");
		//generateAnts(INITIAL_ANTS);
	}
  
	public void generateAnts (int numberOfAnts) {
		for (int i = 0; i < numberOfAnts; i++) {
			ants.add(new AntObject(
					getRandomNestPosition(), new Vector3f(0, 0, 0), new Vector3f(.0001f, .0001f, .0001f), antMesh, this, true));
		}
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
	
	public ArrayList<AntObject> getAnts() {
		return ants;
	}
  
	public int getPopulation() {
		return ants.size();
	}

	public void feedAnts() {
		sort();
		feed();
	}

	private void sort() {
		Collections.sort(ants);
	}

	private void feed() {
		int fedAnts = Math.min(food, ants.size());
		for (int i = 0; i < fedAnts; i++) {
			ants.get(i).updateHealth(10);
			food -= 1;
		}
		for (int i = fedAnts; i < ants.size(); i++) {
			ants.get(i).updateHealth(-40);
		}
	}

	public void increasePopulation() {
		int gainedAnts = material / 10;
		material -= 10 * gainedAnts;
		generateAnts(gainedAnts);
	}
	
	public void removeDeadAnts() {
		sort();
		remove();
	}

	private void remove() {
		while (ants.size() > 0 && ants.get(0).getHealth() == 0) {
			ants.remove(0);
		}
	}

	public void depositFood(int food) {
		this.food += food;
	}

	public void depositMaterial(int material) {
		this.material += material;
	}

	public Vector3f getRandomNestPosition() {
		float x = this.getPosition().getX() + (float) Math.random()*4 - 2;
		float z = this.getPosition().getZ() + (float) Math.random()*4 - 2;
		
		return new Vector3f(x, 0, z);
	}
}
