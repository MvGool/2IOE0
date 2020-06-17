package main;

import engine.graphics.BoneMesh;
import engine.graphics.Material;
import engine.graphics.Mesh;
import engine.graphics.Renderer;
import engine.maths.Vector3f;
import engine.model_loaders.AnimModelLoader;
import engine.model_loaders.StaticModelLoader;
import engine.objects.*;
import main.objects.AntObject;
import main.objects.NestObject;
import main.objects.ResourceObject;

public class World {
	private Renderer renderer;
	private Camera camera;
	private Grid2D grid = new Grid2D(50);
	
	private Mesh[] antMesh;	
	private GameObject cube;
	private Mesh gridMesh;	
	private Mesh shadowMesh;	

	private AntObject userAnt;

	private NestObject nest;
	private ResourceObject banana;
	private GameObject otherModel;
	private AnimGameObject ericModel;

	
	public World(Renderer renderer, Camera camera) {
		this.renderer = renderer;
		this.camera = camera;
	}
	
	public void load() {
		// TODO: remove other and eric model, is now just for debugging
		Mesh[] other = null;
		BoneMesh[] eric = null;
		
		try {
			antMesh = StaticModelLoader.load("resources/models/testmodels/Ant_fbx.fbx", "/textures/antskin.jpg");
			eric = AnimModelLoader.load("resources/models/testmodels/eric.fbx");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		userAnt = new AntObject(new Vector3f(0, 1, 0), new Vector3f(0, 0, 0), new Vector3f(.001f, .001f, .001f), antMesh);
		
		try {
		nest = new NestObject(new Vector3f(0, 0, 0), new Vector3f(-90, 0, 0), new Vector3f(.1f, .1f, .1f), 10, 50);
		banana = new ResourceObject(new Vector3f(10, 1, 0), new Vector3f(-90, 0, 0), new Vector3f(50f, 50f, 50f));
		} catch (Exception e) {
			e.printStackTrace();
		}
		ericModel = new AnimGameObject(new Vector3f(200, 0, 0), new Vector3f(90, 0, 0), new Vector3f(.01f, .01f, .01f), eric);
		
		gridMesh = grid.getMesh();
		gridMesh.setMaterial(new Material("/textures/forest_ground_1k/forrest_ground_01_diff_1k.jpg"));	
		shadowMesh = grid.getShadowMesh();
	}

	public void create() {
//		cube.create(true);
//		antModel.create(false);
		//ant.create(false);
		nest.create(false);
		banana.create(false);
		gridMesh.create(true);
		userAnt.create(false);
		shadowMesh.create(false);
		userAnt.create(false);
//		antModel.moveTo(grid, new Vector3f(3000, 800, 2000));
//		ericModel.create(false);
//		for (int i = 0; i < objects.length; i++) {
//			objects[i] = new GameObject(new Vector3f((float) (Math.random() * 50 - 25), (float) (Math.random() * 50 - 25), (float) (Math.random() * 50 - 25)), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), ant);
//		}
	}
	
	public void update(Renderer renderer, Camera camera) {
		this.renderer = renderer;
		this.camera = camera;
		updateGrid();
		updateShadow();
		updateObjects();
	}
	
	public void render() {
//		for (int i = 0; i < objects.length; i++) {
//			renderer.renderMesh(objects[i], camera);
//		}
		renderer.renderTerrain(gridMesh, camera);
		renderer.renderShadow(shadowMesh, camera);
		renderer.renderMesh(nest, camera);
		renderer.renderMesh(banana, camera);
		renderer.renderMesh(userAnt, camera);
//		renderer.renderMesh(ericModel, camera);
	}
	
	public void destroy() {
		
	}
	
	public void moveUser(Vector3f position) {
		Tile tile = new Tile(Math.round(position.getX()), Math.round(position.getZ()));
		
		if (grid.hasTile(tile.getX(), tile.getY()) && !userAnt.getTile().equals(tile)) {
			userAnt.moveTo(grid, tile);
		}
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
	
	private void updateShadow() {
		Tile tile = userAnt.getTile();
		int range = 6;
		for (int i = -range; i <= range; i++) {
			for (int j = -range; j <= range; j++) {
				if (grid.hasTile(tile.getX() + i, tile.getY() + j) && !grid.getTile(tile.getX() + i, tile.getY() + j).isDiscovered()) {
					if (Math.abs(i) < range + 2 - Math.abs(j)) {
						grid.getTile(tile.getX() + i, tile.getY() + j).setDiscovered(true);
					}
				}
			}
		}
			
		Mesh newMesh = grid.getShadowMesh();
		shadowMesh.reset(newMesh.getVertices(), newMesh.getIndices(), true);
	}

	private void updateObjects() {
		userAnt.update();
	}
}
