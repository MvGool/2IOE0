package engine.maths;

import java.util.HashMap;

public class Spline {
	private Vector3f[] controlPoints;
	private int n;
	private MatrixXf constraints;
	private VectorXf xValues;
	private VectorXf yValues;
	private VectorXf zValues;
	
	public Spline(Vector3f[] controlPoints) {
		this.controlPoints = controlPoints;
		this.n = controlPoints.length;
		this.constraints = MatrixXf.zero(4 * (n - 1));
		this.xValues = new VectorXf(4 * (n - 1));
		this.yValues = new VectorXf(4 * (n - 1));
		this.zValues = new VectorXf(4 * (n - 1));
	}
	
	public CubicPolynomial[] createSpline() {
		CubicPolynomial[] result = new CubicPolynomial[n - 1];

		int row = 0;
		for (int i = 0; i < n - 1; i++) {
			if (i == 0) {
				setConstraint(row, i, 1, 1);
				setValues(row, new Vector3f(0, 0, 0));
				row++;
			}
			
			setConstraint(row, i, 3, 1);
			setValues(row, controlPoints[i]);
			row++;
			
			setConstraint(row, i, 0, 1);
			setConstraint(row, i, 1, 1);
			setConstraint(row, i, 2, 1);
			setConstraint(row, i, 3, 1);
			setValues(row, controlPoints[i + 1]);
			row++;
			
			if (i == n - 2) {
				setConstraint(row, i, 0, 6);
				setConstraint(row, i, 1, 2);
				setValues(row, new Vector3f(0, 0, 0));
			} else {
				setConstraint(row, i, 0, 3);
				setConstraint(row, i, 1, 2);
				setConstraint(row, i, 2, 1);
				setConstraint(row, i + 1, 2, -1);
				setValues(row, new Vector3f(0, 0, 0));
				row++;
				
				setConstraint(row, i, 0, 6);
				setConstraint(row, i, 1, 2);
				setConstraint(row, i + 1, 1, -2);
				setValues(row, new Vector3f(0, 0, 0));
				row++;
			}
		}
		
		return result;
	}
	
	private void setConstraint(int row, int splineNumber, int unknown, float value) {
		constraints.set(row, splineNumber * 4 + unknown, value);
	}
	
	private void setValues(int row, Vector3f point) {
		xValues.set(row, point.getX());
		yValues.set(row, point.getY());
		zValues.set(row, point.getZ());
	}
}
