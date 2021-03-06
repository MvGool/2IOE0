package main.objects;

import java.util.ArrayList;

import engine.graphics.Mesh;
import engine.maths.*;
import engine.objects.GameObject;
import engine.objects.Grid2D;
import engine.objects.Tile;
import main.AntBehavior;
import main.Astar;
import main.AntBehavior.LeaveRequestState;

public class AntObject extends GameObject implements Comparable<AntObject> {
	private float speed = 0.5f; // probably within [0, 2]
	private boolean move = false;
	private Spline spline;
	private CubicPolynomial[] functions;
	private CubicPolynomial currentFunction;
	private int functionNumber;
	private float t;
	private float dt;

	private AntBehavior.LeaveRequestState state = LeaveRequestState.followForager;
  
	private NestObject nest;
	private int health;
	private int food;
	private int material;
	private final int ATTACK_DAMAGE = 20;

	public AntObject(Vector3f position, Vector3f rotation, Vector3f scalar, Mesh[] meshes, NestObject nest, boolean add) {
		super(position, rotation, scalar, meshes);
		this.nest = nest;
		if (add) {
			this.nest.getAnts().add(this);
		}
		health = 100;
		food = 0;
		material = 0;
	}
	
	public AntObject(Vector3f position, Vector3f rotation, Vector3f scalar, Mesh[] meshes) {
		super(position, rotation, scalar, meshes);
		health = 100;
		food = 0;
		material = 0;
	}

	public int getHealth() {
		return health;
	}

	public void updateHealth(int added_value) {
		health += added_value;
		if (health > 100) {
			health = 100;
		}
		if (health <= 0) {
			health = 0;
		}
	}

	public int addFood(int new_value){
		food += new_value;
		if (material + food > 10) {
			int overload = food - 10;
			food = food - overload;
			return overload; //So the source can put take it back
		}
		else {
			return 0;
		}
	}

	public int addMaterial(int new_value){
		material += new_value;
		if (material + food > 10) {
			int overload = material - 10;
			material = material - overload;
			return overload; //So the source can put take it back
		}
		else {
			return 0;
		}
	}

	public void depositResource() {
		nest.depositFood(food);
		nest.depositMaterial(material);
		food = 0;
		material = 0;
	}

	@Override
	public int compareTo(AntObject ant) {
		return health - ant.getHealth();
	}

	public void moveTo(Grid2D grid, Tile goal) {
		if (grid.getTile(goal.getX(), goal.getY()).isObstacle()) {
			System.out.println("New position is an obstacle");
			return;
		}

		Tile[] shortestPath;
		if (goal.equals(this.getTile())) {
			return;
		} else {
			Grid2D astarGrid = new Grid2D(grid);
			Astar astar = new Astar(astarGrid, this.getTile(), goal);
			shortestPath = astar.run();
			if (shortestPath.length == 0) {
				System.out.println("No path exists");
				return;
			}
		}

		Vector3f[] controlPoints = chooseControlPoints(shortestPath, goal);
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

	private Vector3f[] chooseControlPoints(Tile[] shortestPath, Tile goal) {
		ArrayList<Vector3f> controlPointsList = new ArrayList<>();

		if (shortestPath.length == 1) {
			controlPointsList.add(this.getPosition());
			controlPointsList.add(new Vector3f(goal.getX() + 0.5f, 0.1f, goal.getY() - 0.5f));
		} else {
			int[] currentDirection = getDirection(shortestPath[0], shortestPath[1]);

			for (int i = 0; i < shortestPath.length; i++) {
				if (i == 0) {
					controlPointsList.add(this.getPosition());
				} else {
					int[] direction = getDirection(shortestPath[i - 1], shortestPath[i]);

					if (direction[0] != currentDirection[0] || direction[1] != currentDirection[1]) {
						currentDirection = direction;
						controlPointsList.add(new Vector3f(shortestPath[i - 1].getX() + 0.5f, 0.1f, shortestPath[i - 1].getY() - 0.5f));
					}

					if (i == shortestPath.length - 1) {
						controlPointsList.add(new Vector3f(goal.getX() + 0.5f, 0.1f, goal.getY() - 0.5f));
					}
				}
			}
		}

		Vector3f[] controlPoints = new Vector3f[controlPointsList.size()];
		controlPointsList.toArray(controlPoints);

		return controlPoints;
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
		float angle;
		Vector3f normTangent = Vector3f.normalize(currentFunction.computeTangent(t));

		if (normTangent.getZ() < 0) {
			angle = (float) Math.toDegrees(Math.acos(normTangent.getX()));
		} else {
			angle = - (float) Math.toDegrees(Math.acos(normTangent.getX()));
		}

		return 180 + angle;
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

	public boolean isMoving() {
		return move;
	}

	public AntBehavior.LeaveRequestState getState() {
		return state;
	}

	public void setState(AntBehavior.LeaveRequestState state) {
		this.state = state;
	}

	public int getFood() {
		return this.food;
	}

	public int getMaterial() {
		return this.material;
	}

	public void setFood(int food) {
		this.food = food;
	}

	public void setMaterial(int material) {
		this.material = material;
	}
}
