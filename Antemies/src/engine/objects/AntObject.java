package engine.objects;

import java.awt.List;

import engine.graphics.Mesh;
import engine.graphics.Vertex;
import engine.model_loaders.StaticModelLoader;
import engine.maths.*;
import main.Astar;

public class AntObject extends GameObject {
	private static final Mesh[] ANT_MESH = null;
	private boolean move = false;
	private CubicPolynomial[] functions;
	private CubicPolynomial currentFunction;
	private int functionNumber;
	private float t;
	
	public AntObject(Vector3f position, Vector3f rotation, Vector3f scalar, Mesh[] meshes) {
		super(position, rotation, scalar, meshes);
	}
	
	public void moveTo(Grid2D grid, Tile newPosition) {
		if (grid.getTile(newPosition.getX(), newPosition.getY()).isObstacle()) {
			System.out.println("New position is an obstacle");
			return;
		}
		
		Grid2D astarGrid = new Grid2D(grid);
		Astar astar = new Astar(astarGrid, new Tile(2, 3), new Tile(8, 6)); // (this.getTile(), newPosition)
		Tile[] shortestPath = astar.run();
		if (shortestPath.length == 0) {
			System.out.println("No path exists");
			return;
		}
		astar.displaySolution();
		
		// Choose control points for the spline
		Vector3f[] controlPoints = chooseControlPoints(shortestPath);
		
		Spline spline = new Spline(controlPoints);
		move = true;
		functions = spline.createSpline();
		currentFunction = functions[0];
		functionNumber = 0;
		t = 0;
	}
	
	private Vector3f[] chooseControlPoints(Tile[] shortestPath) {
		Vector3f[] controlPoints = new Vector3f[] {this.getPosition(), new Vector3f(5, 1, 2), new Vector3f(-2, 1, 8), new Vector3f(-5, 1, -3)};
		
		return controlPoints;
	}
	
	public void update() {
		if (move && t > 1) {
			if (functionNumber + 1 < functions.length) {
				functionNumber += 1;
				currentFunction = functions[functionNumber];
				t = 0;
				setPosition(currentFunction.computePosition(t));
				increaseT();
			} else {
				move = false;
			}
		} else if (move && t <= 1) {
			setPosition(currentFunction.computePosition(t));
			increaseT();
		}
	}
	
	private void increaseT() {
		t += 0.01;
	}
}
