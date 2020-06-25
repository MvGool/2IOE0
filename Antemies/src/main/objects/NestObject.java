package main.objects;

import java.util.ArrayList;
import java.util.Collections;

import engine.graphics.Mesh;
import engine.maths.Vector3f;
import engine.model_loaders.StaticModelLoader;
import engine.objects.GameObject;

public class NestObject extends GameObject {
	private final int INITIAL_ANTS = 10; // the number of ants at the beginning of the game
	private int food;
	private int material;
	private ArrayList<AntObject> ants;

	public NestObject(Vector3f position, Vector3f rotation, Vector3f scalar, Mesh[] meshes) {
		super(position, rotation, scalar, meshes);
		food = 0;
		material = 0;
		ants = new ArrayList<AntObject>();
		generateAnts(INITIAL_ANTS);
	}
	
	public NestObject(Vector3f position, Vector3f rotation, Vector3f scalar) throws Exception {
		this(position, rotation, scalar, StaticModelLoader.load("resources/models/ant_nest.obj", "/models/forrest_ground_03_diff_1k.jpg"));
		food = 0;
		material = 0;
		ants = new ArrayList<AntObject>();
		generateAnts(INITIAL_ANTS);
	}
	
	private void generateAnts (int numberOfAnts) {
		for (int i = 0; i < numberOfAnts; i++) {
			ants.add(new AntObject(
					this.getPosition(), this.getRotation(),
					this.getScalar(), this.getMeshes(), this));
		}
	}
	
	public int getFood() {
		return food;
	}
	
	public int getMaterial() {
		return material;
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
	
	private void removeDeadAnts() {
		sort();
		remove();
	}
	
	private void remove() {
		while (ants.get(0).getHealth() == 0) {
			ants.remove(0);
		}
	}
	
	public void depositFood(int food) {
		this.food += food;
	}
	
	public void depositMaterial(int material) {
		this.material += material;
	}
	
//	public static void main(String[] args) {
//		Vector3f vector = new Vector3f(0, 0, 0);
//		Mesh[] meshes = new Mesh[1];
//		NestObject nest = new NestObject(vector, vector, vector, meshes);
//		for (int i = 0; i < nest.ants.size(); i++) {
//			nest.ants.get(i).updateHealth(-10*i);
//			System.out.println(i + ": " + nest.ants.get(i).getHealth());
//		}
//		nest.food += 5;
//		System.out.println(nest.food);
//		nest.feedAnts();
//		System.out.println(nest.food);
//		for (int i = 0; i < nest.ants.size(); i++) {
//			System.out.println(i + ": " + nest.ants.get(i).getHealth());
//		}
//		nest.feedAnts();
//		System.out.println(nest.food);
//		for (int i = 0; i < nest.ants.size(); i++) {
//			System.out.println(i + ": " + nest.ants.get(i).getHealth());
//		}
//		System.out.println("AAAAAA");
//		nest.removeDeadAnts();
//		for (int i = 0; i < nest.ants.size(); i++) {
//			System.out.println(i + ": " + nest.ants.get(i).getHealth());
//		}
//	}
}