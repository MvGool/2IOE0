package engine.maths;

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
}
