package main;

import java.util.ArrayList;
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
	private AntBehavior userColonyBehavior;
	private ArrayList<AntObject> userColony = new ArrayList<>();
	
	private AntObject enemyAnt;
	private ArrayList<AntObject> enemyColony = new ArrayList<>();
	private AntBehavior enemyColonyBehavior;
	private EnemyAI enemyAI;

	private AntObject follower;
	
	private NestObject userNest;
	private NestObject enemyNest;
	private AnimGameObject ericModel;

	private static float scaleFood = 0.1f, scaleMaterial = 0.08f, scaleRock = 1f;
	private static ArrayList<Mesh> foodSources = new ArrayList<>();
	private static ArrayList<Mesh> materialSources = new ArrayList<>();
	private ArrayList<Mesh> stoneSources = new ArrayList<>();
	private static Mesh foodMesh;
	private static Mesh materialMesh;
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

			userNest = new NestObject(new Vector3f(0, 0, 0), new Vector3f(-90, 0, 0), new Vector3f(.1f, .1f, .1f));
			enemyNest = new NestObject(new Vector3f(-20, 0, 0), new Vector3f(-90, 0, 0), new Vector3f(.1f, .1f, .1f));

			for (Tile tile : grid.getTiles()) {
				if (tile.getFood() > 0) {
					Mesh[] foodMeshes = StaticModelLoader.load("resources/models/Apricot_02_hi_poly.obj", "/models/textures/Apricot_02_diffuse.png");
					for (Mesh mesh : foodMeshes) {
						mesh.rotateScale(scaleFood, true);
						mesh.move(new Vector3f(tile.getX(), 0, tile.getY()));
						foodSources.add(mesh);
					}
				} else if (tile.getMaterial() > 0) {
					Mesh[] materialMeshes = StaticModelLoader.load("resources/models/sticks.obj", "/textures/stick/Bark_Pine_baseColor.jpg");
					for (Mesh mesh : materialMeshes) {
						mesh.rotateScale(scaleMaterial, false);
						mesh.move(new Vector3f(tile.getX(), 0, tile.getY()));
						materialSources.add(mesh);
					}
				} else if (tile.isObstacle()) {
					Mesh[] stoneMeshes = StaticModelLoader.load("resources/models/rock_large.obj", "/models/rock_large_texture_001.png");
					for (Mesh mesh : stoneMeshes) {
						mesh.rotateScale(scaleRock, true);
						mesh.move(new Vector3f(tile.getX(), 0, tile.getY()));
						stoneSources.add(mesh);
					}
				}
			}
			foodMesh = Mesh.merge(foodSources);
			foodMesh.setMaterial(new Material( "/models/textures/Apricot_02_diffuse.png"));
			materialMesh = Mesh.merge(materialSources);
			materialMesh.setMaterial(new Material("/textures/stick/Bark_Pine_baseColor.jpg"));
			materialMesh.getMaterial().setNormalMap("/textures/stick/Bark_Pine_normal.jpg");
			stoneMesh = Mesh.merge(stoneSources);
			stoneMesh.setMaterial(new Material("/models/rock_large_texture_001.png"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		userAnt = new AntObject(new Vector3f(0.5f, 0.1f, -0.5f), new Vector3f(0, 0, 0), new Vector3f(.0001f, .0001f, .0001f), antMesh, userNest);
		enemyAnt = new AntObject(new Vector3f(-5.5f, 0.1f, -0.5f), new Vector3f(0, 0, 0), new Vector3f(.0001f, .0001f, .0001f), antMesh, enemyNest);
		
		userColony.add(new AntObject(new Vector3f(1.5f, 0.1f, -0.5f), new Vector3f(0, 0, 0), new Vector3f(.0001f, .0001f, .0001f), antMesh, userNest));
		enemyColony.add(new AntObject(new Vector3f(-6.5f, 0.1f, -0.5f), new Vector3f(0, 0, 0), new Vector3f(.0001f, .0001f, .0001f), antMesh, enemyNest));
		
		userColonyBehavior = new AntBehavior(grid, userColony, userAnt, userNest);
		enemyColonyBehavior = new AntBehavior(grid, enemyColony, enemyAnt, enemyNest);
		enemyAI = new EnemyAI(grid);
		
		ericModel = new AnimGameObject(new Vector3f(200, 0, 0), new Vector3f(90, 0, 0), new Vector3f(.01f, .01f, .01f), eric);

		gridMesh = grid.getMesh();
		gridMesh.setMaterial(new Material("/textures/forest_ground_1k/forrest_ground_01_diff_1k.jpg")); // Test texture: "/textures/tileTest.jpg"
		shadowMesh = grid.getShadowMesh();
		trailMesh = grid.getTrailMesh();
		previousTile = userAnt.getTile();
	}

	public void create() {
		userNest.create(true);
		enemyNest.create(true);
		gridMesh.create(true);
		userAnt.create(true);
		enemyAnt.create(true);
		for (AntObject ant : userColony) {
			ant.create(true);
		}
		for (AntObject ant : enemyColony) {
			ant.create(true);
		}
		shadowMesh.create(false);
		trailMesh.create(false);
		foodMesh.create(true);
		materialMesh.create(true);
		stoneMesh.create(true);
	}

	public void update(Renderer renderer, Camera camera) {
		this.renderer = renderer;
		this.camera = camera;
		
		updateShadow();
		updateObjects();
		updateTrail();
		
		userColonyBehavior.run();
		
		enemyColonyBehavior.run();
		
		if (!enemyAnt.isMoving()) {
			enemyAI.behave(enemyAnt);
		}
	}

	public void render() {
		renderer.renderTerrain(gridMesh, camera);
		//renderer.renderShadow(shadowMesh, camera);
		renderer.renderTrail(trailMesh, camera);
		renderer.renderMesh(userNest, camera);
		renderer.renderMesh(enemyNest, camera);
		renderer.renderMesh(userAnt, camera);
		renderer.renderMesh(enemyAnt, camera);
		for (AntObject ant : userColony) {
			renderer.renderMesh(ant, camera);
		}
		for (AntObject ant : enemyColony) {
			renderer.renderMesh(ant, camera);
		}
		renderer.renderResources(foodMesh, camera);
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
		enemyAnt.update();
		for (AntObject ant : userColony) {
			ant.update();
		}
		for (AntObject ant : enemyColony) {
			ant.update();
		}
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
	
	public static void removeFoodFrom(Tile tile) {
		try {
			Mesh[] foodMeshes = StaticModelLoader.load("resources/models/Apricot_02_hi_poly.obj", "/models/textures/Apricot_02_diffuse.png");
			for (Mesh mesh : foodMeshes) {
				mesh.rotateScale(1f/scaleFood, true);
				mesh.move(new Vector3f(-tile.getX(), 0, -tile.getY()));
				foodSources.remove(mesh);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		foodMesh = Mesh.merge(foodSources);
	}
	
	public static void removeMaterialFrom(Tile tile) {
		try {
			Mesh[] materialMeshes = StaticModelLoader.load("resources/models/Apricot_02_hi_poly.obj", "/models/textures/Apricot_02_diffuse.png");
			for (Mesh mesh : materialMeshes) {
				mesh.rotateScale(1f/scaleMaterial, true);
				mesh.move(new Vector3f(-tile.getX(), 0, -tile.getY()));
				materialSources.remove(mesh);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		materialMesh = Mesh.merge(materialSources);
	}
	
	public static int getGridSize() {
		return GRIDSIZE;
	}
}
