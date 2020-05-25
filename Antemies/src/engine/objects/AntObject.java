package engine.objects;

import engine.graphics.Mesh;
import engine.graphics.Vertex;
import engine.maths.*;

public class AntObject extends GameObject {
	private static final Mesh ANT_MESH = new Mesh(new Vertex[] {new Vertex(0, 0, 1), new Vertex(1, 0, 1), new Vertex(1, 1, 1),new Vertex(0, 1, 1)}, new int[] {0, 1, 2, 0, 3, 2});
	private boolean move = false;
	private CubicPolynomial[] functions;
	private CubicPolynomial currentFunction;
	private int functionNumber;
	private float t;
	
	public AntObject(Vector3f position, Vector3f rotation, Vector3f scalar) {
		super(position, rotation, scalar, ANT_MESH);
	}
	
	public void moveTo(Vector3f newPosition) {
		// A* Algorithm with input: (this.getPosition, newPosition)
		
		// Choose control points for the spline
		Vector3f[] controlPoints = new Vector3f[] {this.getPosition(), new Vector3f(5, 1, 2), new Vector3f(-2, 1, 8)};
		
		Spline spline = new Spline(controlPoints);
		move = true;
		functions = spline.createSpline();
		currentFunction = functions[0];
		functionNumber = 0;
		t = 0;
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
