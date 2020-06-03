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
	
	public Tile getTile(int x, int y) {
		return grid.get(new Coordinate(x, y));
	}
	
	public void setTile(Tile tile) {
		grid.replace(new Coordinate(tile.getX(), tile.getY()), tile);
	}
	
	public void addObstacle(int x, int y) {
		System.out.println(grid.get(new Coordinate(x, y)).toString());
		grid.get(new Coordinate(x, y)).setObstacle(true);
	}
	
	public Mesh getMesh() {
		List<Vertex> vertices = new ArrayList<>();
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				vertices.add(new Vertex(new Vector3f(-size/2 + i, 0, -size/2 + j), new Vector3f(0, 1, 0), new Vector2f(i, j)));
			}
		}
		
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				indices.add(i * size + j);
				indices.add((i + 1) * size + j);
				indices.add(i * size + j + 1);
				indices.add((i + 1) * size + j);
				indices.add(i * size + j + 1);
				indices.add((i + 1) * size + j + 1);
			}
		}

		Vertex[] verticesArray = new Vertex[vertices.size()];
		return new Mesh(vertices.toArray(verticesArray),
				indices.stream().mapToInt(i->i).toArray());
	}
	
	public int getSize() {
		return size;
	}
}
