package engine.maths;

public class AugmentedMatrix {
	private int rowCount;
	private int columnCount;
	private float[] elements;
	
	public AugmentedMatrix(MatrixXf matrix, VectorXf vector) {
		this.rowCount = matrix.getSize();
		this.columnCount = matrix.getSize() + 1;
		this.elements = new float[rowCount * columnCount];
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				if (j < columnCount - 1) {
					elements[i * columnCount + j] = matrix.get(i, j);
				} else {
					elements[i * columnCount + j] = vector.get(i);
				}
			}
		}
	}
	
	public float get(int x, int y) {
		return elements[x * columnCount + y];
	}
	
	public void set(int x, int y, float value) {
		elements[x * columnCount + y] = value;
	}
	
	public float[] getMatrix() {
		return elements;
	}
	
	public int getRowCount() {
		return rowCount;
	}
	
	public int getColumnCount() {
		return columnCount;
	}
	
	@Override
	public String toString() {
		String matrix = "";
		for (int i = 0; i < rowCount; i++) {
			matrix += "[";
			for (int j = 0; j < columnCount; j++) {
				if (j == columnCount - 1) {
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
