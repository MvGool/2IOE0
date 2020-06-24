package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	private static final int GRIDSIZE = 50;
	private Grid2D grid = new Grid2D(GRIDSIZE);

	private Mesh[] antMesh;
	private Mesh gridMesh;
	private Mesh shadowMesh;
	private Mesh trailMesh;
	private Tile previousTile;

	private AntObject userAnt;

	private NestObject nest;
	private AnimGameObject ericModel;

	private float scaleFood = 0.1f, scaleMaterial = 0.08f, scaleRock = 1f;
	private ArrayList<Mesh> foodSources = new ArrayList<>();
	private ArrayList<Mesh> materialSources = new ArrayList<>();
	private ArrayList<Mesh> stoneSources = new ArrayList<>();
	private Mesh foodMesh;
	private Mesh materialMesh;
	private Mesh stoneMesh;
	private Mesh foodObjectMesh;
	private Mesh materialObjectMesh;
	private Mesh stoneObjectMesh;

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

			nest = new NestObject(new Vector3f(0, 0, 0), new Vector3f(-90, 0, 0), new Vector3f(.1f, .1f, .1f), 10, 50);

			Mesh[] foodMeshes = StaticModelLoader.load("resources/models/Apricot_02_hi_poly.obj", "/models/textures/Apricot_02_diffuse.png");
			foodObjectMesh = foodMeshes[0];
			foodObjectMesh.rotateScale(scaleFood, true);
			
			ArrayList<Mesh> materialMeshes = new ArrayList<>();
			materialMeshes.addAll(Arrays.asList(StaticModelLoader.load("resources/models/sticks.obj", "/textures/stick/Bark_Pine_baseColor.jpg")));
			for (Mesh mesh : materialMeshes) {
				mesh.rotateScale(scaleMaterial, false);
			}
			materialObjectMesh = Mesh.merge(materialMeshes); 
			
			ArrayList<Mesh> stoneMeshes = new ArrayList<>();
			stoneMeshes.addAll(Arrays.asList(StaticModelLoader.load("resources/models/rock_large.obj", "/models/rock_large_texture_001.png")));
			for (Mesh mesh : stoneMeshes) {
				mesh.rotateScale(scaleRock, true);
			}
			stoneObjectMesh = Mesh.merge(stoneMeshes); 
			
			for (Tile tile : grid.getTiles()) {
//				if (tile.isDiscovered()) {
					if (tile.getFood() > 0) {
						Mesh newFoodMesh = new Mesh(foodObjectMesh);
						newFoodMesh.move(new Vector3f(tile.getX(), 0, tile.getY()));
						foodSources.add(newFoodMesh);
					} else if (tile.getMaterial() > 0) {
						Mesh newMaterialMesh = materialObjectMesh.copy();
						newMaterialMesh.move(new Vector3f(tile.getX(), 0, tile.getY()));
						materialSources.add(newMaterialMesh);
					} else if (tile.isObstacle()) {
						Mesh newStoneMesh = stoneObjectMesh.copy();
						newStoneMesh.move(new Vector3f(tile.getX(), 0, tile.getY()));
						stoneSources.add(newStoneMesh);
					}
//				}
			}
			
			foodMesh = Mesh.merge(foodSources);
			materialMesh = Mesh.merge(materialSources);
			materialMesh.getMaterial().setNormalMap("/textures/stick/Bark_Pine_normal.jpg");
			stoneMesh = Mesh.merge(stoneSources);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		userAnt = new AntObject(new Vector3f(0.5f, 0.1f, -0.5f), new Vector3f(0, 0, 0), new Vector3f(.0001f, .0001f, .0001f), antMesh);

		ericModel = new AnimGameObject(new Vector3f(200, 0, 0), new Vector3f(90, 0, 0), new Vector3f(.01f, .01f, .01f), eric);

		//ant = new AntObject(new Vector3f(1, 1, 1), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), antMesh);
		//ant.moveTo(grid, new Vector3f(3, 1, 2));

		gridMesh = grid.getMesh();
		gridMesh.setMaterial(new Material("/textures/forest_ground_1k/forrest_ground_01_diff_1k.jpg")); // Test texture: "/textures/tileTest.jpg"
		shadowMesh = grid.getShadowMesh();
		trailMesh = grid.getTrailMesh();
		previousTile = userAnt.getTile();
	}

	public void create() {
		nest.create(true);
		gridMesh.create(true);
		userAnt.create(true);
		shadowMesh.create(false);
		trailMesh.create(false);
		foodObjectMesh.create(true);
		materialMesh.create(true);
		stoneMesh.create(true);
	}

	public void update(Renderer renderer, Camera camera) {
		this.renderer = renderer;
		this.camera = camera;
		updateShadow();
		updateObjects();
		updateTrail();
	}

	public void render() {
		renderer.renderTerrain(gridMesh, camera);
		renderer.renderShadow(shadowMesh, camera);
		renderer.renderTrail(trailMesh, camera);
		renderer.renderMesh(nest, camera);
		renderer.renderMesh(userAnt, camera);
		renderer.renderResources(foodObjectMesh, camera);
		renderer.renderResources(materialMesh, camera);
		renderer.renderResources(stoneMesh, camera);
	}

	public void destroy() {

	}

	public void moveUser(Vector3f position) {
		Tile tile = new Tile((int) Math.floor(position.getX()), (int) Math.ceil(position.getZ()));

		if (grid.hasTile(tile.getX(), tile.getY()) && !userAnt.getTile().equals(tile) && grid.getTile(tile.getX(), tile.getY()).isDiscovered()) {
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
						updateTile(grid.getTile(tile.getX() + i, tile.getY() + j));
					}
				}
			}
		}

		Mesh newMesh = grid.getShadowMesh();
		shadowMesh.reset(newMesh.getVertices(), newMesh.getIndices(), true);
	}
	
	private void createObjectMeshes() throws Exception {
	}
	
	private void updateTile(Tile tile) {
		tile.setDiscovered(true);
//		if (tile.getFood() > 0) {
//			Mesh newFoodMesh = foodMesh.copy();
//			newFoodMesh.move(new Vector3f(tile.getX(), 0, tile.getY()));
//			foodSources.add(newFoodMesh);
//			foodMesh.destroy();
//			foodMesh = Mesh.merge(foodSources);
//			foodMesh.create(true);
//			System.out.println("food added " + foodSources.size());
//		} else if (tile.getMaterial() > 0) {
//			Mesh newMaterialMesh = materialMesh.copy();
//			newMaterialMesh.move(new Vector3f(tile.getX(), 0, tile.getY()));
//			materialSources.add(newMaterialMesh);
//			materialMesh.destroy();
//			materialMesh = Mesh.merge(materialSources);
//			materialMesh.create(true);
//			System.out.println("material added");
//		} else if (tile.isObstacle()) {
//			Mesh newStoneMesh = stoneMesh.copy();
//			newStoneMesh.move(new Vector3f(tile.getX(), 0, tile.getY()));
//			stoneSources.add(newStoneMesh);
//			stoneMesh.destroy();
//			stoneMesh = Mesh.merge(stoneSources);
//			stoneMesh.create(true);
//			System.out.println("stone added");
//		}
	}

	private void updateObjects() {
		userAnt.update();
	}

	private void updateTrail() {
		Tile currentTile = userAnt.getTile();
		if (!currentTile.equals(previousTile) && !grid.getTile(currentTile.getX(), currentTile.getY()).isObstacle()) {
			grid.addToTrail(previousTile);
			previousTile = currentTile;
		}

		ArrayList<Tile> trail = grid.getTrail();
		List<Tile> toRemove = new ArrayList<>();
		for (Tile tile : trail) {
			grid.getTile(tile.getX(), tile.getY()).setTrailValue(grid.getTile(tile.getX(), tile.getY()).getTrailValue() - 0.01f);
			if (grid.getTile(tile.getX(), tile.getY()).getTrailValue() <= 0) {
				toRemove.add(tile);
			}
		}
		grid.removeFromTrail(toRemove);

		Mesh newMesh = grid.getTrailMesh();
		trailMesh.reset(newMesh.getVertices(), newMesh.getIndices(), false);
	}
	
	public static int getGridSize() {
		return GRIDSIZE;
	}
}
