package engine.maths;

public class VectorXf {
	private int size;
	private float[] elements;

	public VectorXf(int size) {
		this.size = size;
		this.elements = new float[size];
	}

	public float get(int x) {
		return elements[x];
	}

	public void set(int x, float value) {
		elements[x] = value;
	}

	public int getSize() {
		return size;
	}

	public String toString() {
		String values = "";
		for (int i = 0; i < size; i++) {
			if (i == size - 1) {
				values += this.get(i);
			} else {
				values += this.get(i) + ", ";
			}
		}

		return "(" + values + ")";
	}
}
