package main;

import engine.graphics.Material;
import engine.graphics.Mesh;
import engine.graphics.Renderer;
import engine.graphics.Vertex;
import engine.maths.Vector2f;
import engine.maths.Vector3f;
import engine.model_loaders.StaticModelLoader;
import engine.objects.*;

public class World {
	private Renderer renderer;
	private Camera camera;
	private Grid2D grid = new Grid2D(100);
	
	private Mesh cubeMesh;	
	private GameObject cube;
	private Mesh gridMesh;	
	private GameObject gridObject;
	private AntObject ant;
	
	public World(Renderer renderer, Camera camera) {
		this.renderer = renderer;
		this.camera = camera;
	}
	
	public void load() {
		cubeMesh = null;
		cube = new GameObject(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), cubeMesh);
		ant = new AntObject(new Vector3f(1, 1, 1), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
		ant.moveTo(new Vector3f(1, 1, 1));
		
		gridMesh = grid.getMesh();
		gridMesh.setMaterial(new Material("/textures/forest_ground_1k/forrest_ground_01_diff_1k.jpg"));
		gridObject = new GameObject(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), gridMesh);
//		try {
//			ant = StaticModelLoader.load("resources/models/ant.obj", "/textures/forest_ground_1k/forrest_ground_01_diff_1k.jpg");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		antModel = new GameObject(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), ant);
	}
	
	public void create() {
//		cube.create(true);
		gridObject.create(true);
//		antModel.create(false);
		ant.create(false);
//		for (int i = 0; i < objects.length; i++) {
//			objects[i] = new GameObject(new Vector3f((float) (Math.random() * 50 - 25), (float) (Math.random() * 50 - 25), (float) (Math.random() * 50 - 25)), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), ant);
//		}
	}
	
	public void update(Renderer renderer, Camera camera) {
		this.renderer = renderer;
		this.camera = camera;
		ant.update();
	}
	
	public void render() {
//		for (int i = 0; i < objects.length; i++) {
//			renderer.renderMesh(objects[i], camera);
//		}
//		renderer.renderMesh(cube, camera);
		renderer.renderMesh(ant, camera);
		renderer.renderMesh(gridObject, camera);
//		renderer.renderMesh(antModel, camera);
	}
	
	public void destroy() {
		
	}
}
