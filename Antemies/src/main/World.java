package main;

import engine.graphics.BoneMesh;
import engine.graphics.Material;
import engine.graphics.Mesh;
import engine.graphics.Renderer;
import engine.maths.Vector3f;
import engine.model_loaders.AnimModelLoader;
import engine.model_loaders.StaticModelLoader;
import engine.objects.*;

public class World {
	private Renderer renderer;
	private Camera camera;
	private Grid2D grid = new Grid2D(100);
	
	private Mesh[] antMesh;	
	private GameObject cube;
	private Mesh gridMesh;	

	private AntObject antModel;

	private GameObject otherModel;
	private AnimGameObject ericModel;

	
	public World(Renderer renderer, Camera camera) {
		this.renderer = renderer;
		this.camera = camera;
	}
	
	public void load() {
//		cubeMesh = null;
//		cube = new GameObject(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), cubeMesh);

		// TODO: remove other and eric model, is now just for debugging
		Mesh[] other = null;
		BoneMesh[] eric = null;
		
		try {
//			antMesh = StaticModelLoader.load("resources/models/banana.obj", "/textures/antskin.jpg");
//			eric = AnimModelLoader.load("resources/models/eric.fbx");
			//eric = AnimModelLoader.load("resources/models/eric.fbx");
			eric = AnimModelLoader.load("resources/models/testmodels/eric.dae");
			for (Mesh m : eric) {
				m.setMaterial(new Material("/models/grey.jpg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
//		antModel = new AntObject(new Vector3f(0, 1, 0), new Vector3f(0, 0, 0), new Vector3f(.01f, .01f, .01f), antMesh);
		//ericModel = new AnimGameObject(new Vector3f(200, 0, 0), new Vector3f(90, 0, 0), new Vector3f(.01f, .01f, .01f), eric);
		//antModel.moveTo(grid, new Tile(15, 20));


		System.out.println("eric size: " + eric.length);
		//otherModel = new GameObject(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(.001f, .001f, .001f), other);
		ericModel = new AnimGameObject(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(.01f, .01f, .01f), eric);
		
		//ant = new AntObject(new Vector3f(1, 1, 1), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), antMesh);
		//ant.moveTo(grid, new Vector3f(3, 1, 2));
		
		gridMesh = grid.getMesh();
		gridMesh.setMaterial(new Material("/textures/forest_ground_1k/forrest_ground_01_diff_1k.jpg"));

//		antModel = new GameObject(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), ant);
	}
	
	public void create() {
//		cube.create(true);
//		antModel.create(false);
		//ant.create(false);
		gridMesh.create(true);
//		antModel.create(false);
//		antModel.moveTo(grid, new Vector3f(3000, 800, 2000));
		ericModel.create(false);
//		for (int i = 0; i < objects.length; i++) {
//			objects[i] = new GameObject(new Vector3f((float) (Math.random() * 50 - 25), (float) (Math.random() * 50 - 25), (float) (Math.random() * 50 - 25)), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), ant);
//		}
	}
	
	public void update(Renderer renderer, Camera camera) {
		this.renderer = renderer;
		this.camera = camera;
		updateGrid();
		updateObjects();
	}
	
	public void render() {
//		for (int i = 0; i < objects.length; i++) {
//			renderer.renderMesh(objects[i], camera);
//		}
//		renderer.renderMesh(cube, camera);
//		renderer.renderMesh(ant, camera);
		renderer.renderTerrain(gridMesh, camera);
//		renderer.renderMesh(antModel, camera);
		renderer.renderMesh(ericModel, camera);
	}
	
	public void destroy() {
		
	}
	
	private void updateGrid() {
		int x = (int) camera.getPosition().getX() + 1/2;
		int z = (int) camera.getPosition().getZ() + 1/2;
		
		if (!grid.hasTile(x, z)) {
			grid.setTile(new Tile(x, z));
			Mesh newMesh = grid.getMesh();
			gridMesh.reset(newMesh.getVertices(), newMesh.getIndices(), true);
		}
	}

	private void updateObjects() {
//		antModel.update();
	}
}
