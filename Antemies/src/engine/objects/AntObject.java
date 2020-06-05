package engine.objects;

import java.awt.List;
import java.util.ArrayList;

import engine.graphics.Mesh;
import engine.graphics.Vertex;
import engine.model_loaders.StaticModelLoader;
import engine.maths.*;
import main.Astar;

public class AntObject extends GameObject {
	float speed = 10; // probably within [0, 2]
	private boolean move = false;
	private Spline spline;
	private CubicPolynomial[] functions;
	private CubicPolynomial currentFunction;
	private int functionNumber;
	private float t;
	private float dt;
	private Vector3f newPosition;
	
	public AntObject(Vector3f position, Vector3f rotation, Vector3f scalar, Mesh[] meshes) {
		super(position, rotation, scalar, meshes);
	}
	
	public void moveTo(Grid2D grid, Vector3f newPosition) {
		this.newPosition = newPosition;
		Tile newTile = new Tile((int) newPosition.getX(), (int) newPosition.getZ());
		
		if (grid.getTile(newTile.getX(), newTile.getY()).isObstacle()) {
			System.out.println("New position is an obstacle");
			return;
		}
		
		Tile[] shortestPath;
		if (newTile.equals(this.getTile())) {
			shortestPath = new Tile[] {newTile};
		} else {
			Grid2D astarGrid = new Grid2D(grid);
			Astar astar = new Astar(astarGrid, new Tile(2, 3), newTile); // (this.getTile(), newTile)
			shortestPath = astar.run();
			if (shortestPath.length == 0) {
				System.out.println("No path exists");
				return;
			}
		}
		
		Vector3f[] controlPoints = chooseControlPoints(shortestPath);
		spline = new Spline(controlPoints);
		move = true;
		functions = spline.createSpline();
		currentFunction = functions[0];
		functionNumber = 0;
		t = 0;
	}
	
	@Override
	public void update() {
		super.update();
		
		float angle;
		
		if (move && t > 1) {
			setPosition(currentFunction.computePosition(1));
			if (functionNumber + 1 < functions.length) {
				functionNumber += 1;
				currentFunction = functions[functionNumber];
				t = 0;
				setPosition(currentFunction.computePosition(t));
				angle = getAngle();
				setRotation(new Vector3f(this.getRotation().getX(), angle, this.getRotation().getZ()));
				increaseT();
			} else {
				move = false;
			}
		} else if (move && t <= 1) {
			setPosition(currentFunction.computePosition(t));
			angle = getAngle();
			setRotation(new Vector3f(this.getRotation().getX(), angle, this.getRotation().getZ()));
			increaseT();
		}
	}
	
	private Vector3f[] chooseControlPoints(Tile[] shortestPath) {
		ArrayList<Vector3f> controlPointsList = new ArrayList<>();
		
		if (shortestPath.length == 1) {
			controlPointsList.add(this.getPosition());
			controlPointsList.add(newPosition);
		} else {
			int[] currentDirection = getDirection(shortestPath[0], shortestPath[1]);
			
			for (int i = 0; i < shortestPath.length; i++) {
				if (i == 0) {
					controlPointsList.add(this.getPosition());
				} else if (i == shortestPath.length - 1) {
					controlPointsList.add(newPosition);
				} else {
					int[] direction = getDirection(shortestPath[i - 1], shortestPath[i]);
					
					if (direction[0] != currentDirection[0] || direction[1] != currentDirection[1]) {
						currentDirection = direction;
						controlPointsList.add(shortestPath[i - 1].getVectorPosition());
					}
				}
			}
		}
		
		//Vector3f[] test = new Vector3f[] {this.getPosition(), new Vector3f(5, 1, 2), new Vector3f(-2, 1, 8), new Vector3f(-2, 1, 9), new Vector3f(-5, 1, -3)};
		Vector3f[] test = new Vector3f[] {this.getPosition(), new Vector3f(2, 1, 1), new Vector3f(4, 1, 1)};
		return test;
		
		/*Vector3f[] controlPoints = new Vector3f[controlPointsList.size()];
		controlPointsList.toArray(controlPoints);
		
		return controlPoints;*/
	}
	
	private int[] getDirection(Tile previous, Tile current) {
		int[] direction = new int[2];
		
		if (current.getX() > previous.getX()) {
			direction[0] = 1;
		} else if (current.getX() < previous.getX()) {
			direction[0] = -1;
		} else {
			direction[0] = 0;
		}
		
		if (current.getY() > previous.getY()) {
			direction[1] = 1;
		} else if (current.getY() < previous.getY()) {
			direction[1] = -1;
		} else {
			direction[1] = 0;
		}
		
		return direction;
	}
	
	private float getAngle() {
		/*Vector3f tangent = currentFunction.computeTangent(t);
		float dotProduct = Vector3f.dotProduct(new Vector3f(-1, 0, 0), tangent);
		float angle = (float) Math.toDegrees(Math.acos(dotProduct / (Vector3f.length(tangent))));
		if (angle >= 180) {
			angle = -angle;
		}
		System.out.println(angle);
		
		return angle;*/
		
		Vector3f current = currentFunction.computePosition(t);
		Vector3f next = currentFunction.computePosition(t + dt);
		
		float angle;
		float slope = (next.getZ() - current.getZ()) / (next.getX() - current.getX());
		if (slope > 0) {
			angle = (float) Math.toDegrees(Math.atan2(next.getZ() - current.getZ(), next.getX() - current.getX()));
		} else {
			angle = (float) Math.toDegrees(Math.atan2(next.getX() - current.getX(), next.getZ() - current.getZ()));
		}
		/*if (angle < 0) {
	        angle += 360;
	    }*/
		
		return angle;
	}
	
	private void computeInterval() {
		float functionLength = 0;
		
		if (functions.length == 1) {
			Vector3f diff = Vector3f.subtract(spline.getControlPoints()[1], spline.getControlPoints()[0]);
			functionLength = Vector3f.length(diff);
		} else {
			Vector3f previous = currentFunction.computePosition(0);
			
			for (float t = 0.01f; t <= 1; t += 0.001f) {
				Vector3f current = currentFunction.computePosition(t);
				Vector3f diff = Vector3f.subtract(current, previous);
				functionLength += Vector3f.length(diff);
				previous = current;
			}
		}
		
		dt = speed * 0.1f / functionLength;
	}
	
	private void increaseT() {
		if (t == 0) {
			computeInterval();
		}
		
		t += dt;
	}
}
