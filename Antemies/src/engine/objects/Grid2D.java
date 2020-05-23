package engine.objects;

import java.util.ArrayList;
import java.util.List;

import engine.graphics.Mesh;
import engine.graphics.Vertex;
import engine.maths.Vector2f;
import engine.maths.Vector3f;

public class Grid2D {
	private int size;
	private Tile[][] grid = new Tile[size][size];
	
	public Grid2D(int size) {
		this.size = size;
	}
	
	public Tile getTile(int x, int y) {
		return grid[x][y];
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
}
