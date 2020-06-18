package main;

import java.util.ArrayList;

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
	private GameObject otherModel;
	private AnimGameObject ericModel;
	
	private float scaleFood = 50, scaleMaterial = 0.02f, scaleRock = 0.005f;
	private ArrayList<Mesh> foodSources = new ArrayList<>();
	private ArrayList<Mesh> materialSources = new ArrayList<>();
	private ArrayList<Mesh> stoneSources = new ArrayList<>();
	private Mesh foodMesh;
	private Mesh materialMesh;
	private Mesh stoneMesh;

	
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
			
			nest = new NestObject(new Vector3f(0, 0, 0), new Vector3f(-90, 0, 0), new Vector3f(.1f, .1f, .1f), 10, 50);
			
			for (Tile tile : grid.getTiles()) {
				if (tile.getFood() > 0) {
					Mesh[] foodMeshes = StaticModelLoader.load("resources/models/watermelonSlice.obj", "/textures/antskin.jpg");
					for (Mesh mesh : foodMeshes) {
						mesh.rotateScale(scaleFood);
						mesh.move(new Vector3f(tile.getX(), 0, tile.getY()));
						foodSources.add(mesh);
					}
				} else if (tile.getMaterial() > 0) {
					Mesh[] materialMeshes = StaticModelLoader.load("resources/models/nest_material.obj", "/models/forrest_ground_03_diff_1k.jpg");
					for (Mesh mesh : materialMeshes) {
						mesh.rotateScale(scaleMaterial);
						mesh.move(new Vector3f(tile.getX(), 0, tile.getY()));
						materialSources.add(mesh);
					}
				} else if (tile.isObstacle()) {
					Mesh[] stoneMeshes = StaticModelLoader.load("resources/models/rock.obj", "/models/rock_01_AO_1k.jpg");
					for (Mesh mesh : stoneMeshes) {
						mesh.rotateScale(scaleRock);
						mesh.move(new Vector3f(tile.getX(), 0, tile.getY()));
						stoneSources.add(mesh);
					}
				}
			}
			foodMesh = Mesh.merge(foodSources);
			foodMesh.setMaterial(new Material( "/textures/antskin.jpg"));
			materialMesh = Mesh.merge(materialSources);
			materialMesh.setMaterial(new Material("/models/forrest_ground_03_diff_1k.jpg"));
			stoneMesh = Mesh.merge(stoneSources);
			stoneMesh.setMaterial(new Material("/models/rock_01_AO_1k.jpg"));

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		userAnt = new AntObject(new Vector3f(0, 0.1f, 0), new Vector3f(0, 0, 0), new Vector3f(.0001f, .0001f, .0001f), antMesh);
		
		ericModel = new AnimGameObject(new Vector3f(200, 0, 0), new Vector3f(90, 0, 0), new Vector3f(.01f, .01f, .01f), eric);
		
		gridMesh = grid.getMesh();
		gridMesh.setMaterial(new Material("/textures/tileTest.jpg")); //"/textures/forest_ground_1k/forrest_ground_01_diff_1k.jpg"));	
		shadowMesh = grid.getShadowMesh();
	}

	public void create() {
		nest.create(false);
		gridMesh.create(true);
		userAnt.create(false);
		shadowMesh.create(false);
		foodMesh.create(false);
		materialMesh.create(false);
		stoneMesh.create(false);
	}
	
	public void update(Renderer renderer, Camera camera) {
		this.renderer = renderer;
		this.camera = camera;
		updateShadow();
		updateObjects();
	}
	
	public void render() {
		renderer.renderTerrain(gridMesh, camera);
		renderer.renderShadow(shadowMesh, camera);
		renderer.renderMesh(nest, camera);
		renderer.renderMesh(userAnt, camera);
		renderer.renderResources(foodMesh, camera);
		renderer.renderResources(materialMesh, camera);
		renderer.renderResources(stoneMesh, camera);
	}
	
	public void destroy() {
		
	}
	
	public void moveUser(Vector3f position) {
		Tile tile = new Tile((int) Math.floor(position.getX()), (int) Math.ceil(position.getZ()));
		
		if (grid.hasTile(tile.getX(), tile.getY()) && !userAnt.getTile().equals(tile)) {
			userAnt.moveTo(grid, tile);
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
