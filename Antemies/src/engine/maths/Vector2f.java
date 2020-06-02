package engine.maths;

public class Vector2f {
	private float x, y;
	
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public static Vector2f add(Vector2f vec1, Vector2f vec2) {
		return new Vector2f(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY());
	}

	public static Vector2f subtract(Vector2f vec1, Vector2f vec2) {
		return new Vector2f(vec1.getX() - vec2.getX(), vec1.getY() - vec2.getY());
	}
	
	public static Vector2f multiply(Vector2f vec, float num) {
		return new Vector2f(vec.getX() * num, vec.getY() * num);
	}

	public static Vector2f divide(Vector2f vec, float num) {
		return new Vector2f(vec.getX() / num, vec.getY() / num);
	}
	
	public static float length(Vector2f vec) {
		return (float) Math.sqrt(vec.getX() * vec.getX() + vec.getY() * vec.getY());
	}
	
	public static Vector2f normalize(Vector2f vec) {
		return divide(vec, length(vec));
	}
	
	public static float dotProduct(Vector2f vec1, Vector2f vec2) {
		return vec1.getX() * vec2.getX() + vec1.getY() * vec2.getY();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector2f other = (Vector2f) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
