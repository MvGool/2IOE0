package engine.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.graphics.Mesh;
import engine.graphics.Vertex;
import engine.maths.Vector2f;
import engine.maths.Vector3f;
import engine.maths.Coordinate;

public class Grid2D {
	private int size;
	private Map<Coordinate, Tile> grid = new HashMap<>();
	
	public Grid2D(int size) {
		this.size = size;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Coordinate key = new Coordinate(i-size/2, j-size/2);
				if (grid.containsKey(key)) {
					System.out.println(grid.get(key));
				} else {
					grid.put(key, new Tile(i-size/2, j-size/2));
				}
			}
		}
	}
	
	// Creates a copy of otherGrid
	public Grid2D(Grid2D otherGrid) {
		int size = otherGrid.getSize();
		this.size = size;
		for (int i = 0; i < this.size; i++) {
			for (int j = 0; j < this.size; j++) {
				Tile tile = otherGrid.getTile(i-size/2, j-size/2);
				grid.put(new Coordinate(i-size/2, j-size/2), tile);
			}
		}
	}
	
	public boolean hasTile(int x, int y) {
		return grid.containsKey(new Coordinate(x, y));
	}
	
	public Tile getTile(int x, int y) {
		Tile tile = grid.get(new Coordinate(x, y));
		
		if (tile == null) {
			System.err.println("Tile not found");
		}
		
		return tile;
	}
	
	public void setTile(Tile tile) {
		if (grid.replace(new Coordinate(tile.getX(), tile.getY()), tile) == null) {
			grid.put(new Coordinate(tile.getX(), tile.getY()), tile);
		}
	}
	
	public void addObstacle(int x, int y) {
		System.out.println(grid.get(new Coordinate(x, y)).toString());
		grid.get(new Coordinate(x, y)).setObstacle(true);
	}
	
	public Mesh getMesh() {
		// Create list for vertices and indices; 
		List<Vertex> vertices = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		
		// Loop over all tiles adding them to the mesh
		int iter = 0;
		for (Tile tile : grid.values()) {
			int x = tile.getX();
			int y = tile.getY();
			
			// Add 4 corners
			vertices.add(new Vertex(new Vector3f(x, 0, y), new Vector3f(0, 1, 0), new Vector2f(x, y)));
			vertices.add(new Vertex(new Vector3f(x, 0, y + 1), new Vector3f(0, 1, 0), new Vector2f(x, y + 1)));
			vertices.add(new Vertex(new Vector3f(x + 1, 0, y), new Vector3f(0, 1, 0), new Vector2f(x + 1, y)));
			vertices.add(new Vertex(new Vector3f(x + 1, 0, y + 1), new Vector3f(0, 1, 0), new Vector2f(x + 1, y + 1)));
		
			// Draw triangles for that tile
			indices.add(4*iter);
			indices.add(4*iter + 2);
			indices.add(4*iter + 1);
			indices.add(4*iter + 2);
			indices.add(4*iter + 1);
			indices.add(4*iter + 3);
			
			// Increase counter for the indices
			iter++;
		}
		
		Vertex[] verticesArray = new Vertex[vertices.size()];
		return new Mesh(vertices.toArray(verticesArray),
				indices.stream().mapToInt(i->i).toArray());
	}
	
	public int getSize() {
		return size;
	}
}
