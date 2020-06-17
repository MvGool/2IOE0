package main.objects;

import engine.graphics.Mesh;
import engine.maths.Vector3f;
import engine.model_loaders.StaticModelLoader;
import engine.objects.GameObject;
import engine.objects.Grid2D;
import engine.objects.Tile;

public class MaterialObject extends GameObject {

	private Grid2D grid;
	
	public MaterialObject(Vector3f position, Vector3f rotation, Vector3f scalar, Mesh[] meshes, Grid2D grid) {
		super(position, rotation, scalar, meshes);
		this.grid = grid;
	}
	
	public MaterialObject(Vector3f position, Vector3f rotation, Vector3f scalar, Grid2D grid) throws Exception {
		this(position, rotation, scalar, StaticModelLoader.load("resources/models/nest_material.obj", "/models/forrest_ground_03_diff_1k.jpg"), grid);
	}
	
	public Tile getTile() {
		return grid.getTile((int) this.getPosition().getX(), (int) this.getPosition().getZ());
	}
}
