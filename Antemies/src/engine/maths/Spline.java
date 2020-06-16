package engine.maths;

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
		
		if (n == 2) {
			CubicPolynomial function = new CubicPolynomial(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), Vector3f.subtract(controlPoints[1], controlPoints[0]), controlPoints[0]);
			result[0] = function;
			
			return result;
		}

		setConstraints();
		VectorXf xConstants = GaussJordanElimination.solve(constraints, xValues);
		VectorXf yConstants = GaussJordanElimination.solve(constraints, yValues);
		VectorXf zConstants = GaussJordanElimination.solve(constraints, zValues);
		
		Vector3f[] aValues = new Vector3f[n];
		Vector3f[] bValues = new Vector3f[n];
		Vector3f[] cValues = new Vector3f[n];
		Vector3f[] dValues = new Vector3f[n];
		for (int i = 0; i < xConstants.getSize(); i++) {
			Vector3f constant = new Vector3f(xConstants.get(i), yConstants.get(i), zConstants.get(i));
			
			switch(i % 4) {
				case 0:
					aValues[i / 4] = constant;
					break;
				case 1:
					bValues[i / 4] = constant;
					break;
				case 2:
					cValues[i / 4] = constant;
					break;
				case 3:
					dValues[i / 4] = constant;
					break;
			}
		}
		
		for (int i = 0; i < n - 1; i++) {
			CubicPolynomial function = new CubicPolynomial(aValues[i], bValues[i], cValues[i], dValues[i]);
			result[i] = function;
		}
		
		return result;
	}
	
	private void setConstraints() {
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
	}
	
	private void setConstraint(int row, int splineNumber, int unknown, float value) {
		constraints.set(row, splineNumber * 4 + unknown, value);
	}
	
	private void setValues(int row, Vector3f point) {
		xValues.set(row, point.getX());
		yValues.set(row, point.getY());
		zValues.set(row, point.getZ());
	}
	
	public Vector3f[] getControlPoints() {
		return controlPoints;
	}
}
