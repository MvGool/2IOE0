package engine.maths;

/* INDEX: (ROW, COLUMN) */
public class MatrixXf {
	private int size;
	private float[] elements;

	public MatrixXf(int size) {
		this.size = size;
		this.elements =  new float[size * size];
	}

	public static MatrixXf identity(int size) {
		MatrixXf result = new MatrixXf(size);

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (i == j) {
					result.set(i,  j, 1);
				} else {
					result.set(i,  j, 0);
				}
			}
		}

		return result;
	}

	public static MatrixXf zero(int size) {
		MatrixXf result = new MatrixXf(size);

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				result.set(i,  j, 0);
			}
		}

		return result;
	}
	
	public static MatrixXf inverse(MatrixXf matrix) {
		MatrixXf result = GaussJordanElimination.solve(matrix, MatrixXf.identity(matrix.getSize()));
		
		return result;
	}

	public static VectorXf multiply(MatrixXf matrix, VectorXf vector) {
		int size = matrix.getSize();
		VectorXf result = new VectorXf(vector.getSize());

		for (int i = 0; i < size; i++) {
			float value = 0;
			for (int j = 0; j < size; j++) {
				value += matrix.get(i, j) * vector.get(j);
			}
			
			result.set(i, value);
		}

		return result;
	}
	
	public static MatrixXf multiply(MatrixXf matrix1, MatrixXf matrix2) {
		int size = matrix1.getSize();
		MatrixXf result = identity(size);

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				float value = 0;
				for (int k = 0; k < size; k++) {
					value += matrix1.get(i, k) * matrix2.get(k, j);
				}

				result.set(i, j, value);
			}
		}

		return result;
	}

	public float get(int x, int y) {
		return elements[x * size + y];
	}

	public void set(int x, int y, float value) {
		elements[x * size + y] = value;
	}

	public void setRow(int x, VectorXf values) {
		for (int i = 0; i < size; i++) {
			elements[x * size + i] = values.get(i);
		}
	}

	public float[] getMatrix() {
		return elements;
	}

	public int getSize() {
		return size;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof MatrixXf)) {
			return false;
		}
		
		MatrixXf matrix = (MatrixXf) o;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (this.get(i, j) != matrix.get(i, j)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		String matrix = "";
		for (int i = 0; i < size; i++) {
			matrix += "[";
			for (int j = 0; j < size; j++) {
				if (j == size - 1) {
					matrix += this.get(i, j);
				} else {
					matrix += this.get(i, j) + " ";
				}
			}
			matrix += "]\n";
		}

		return matrix;
	}
}
