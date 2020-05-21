package engine.maths;

public class Vector3f {
	private float x, y, z;

	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static Vector3f add(Vector3f vec1, Vector3f vec2) {
		return new Vector3f(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY(), vec1.getZ() + vec2.getZ());
	}

	public static Vector3f subtract(Vector3f vec1, Vector3f vec2) {
		return new Vector3f(vec1.getX() - vec2.getX(), vec1.getY() - vec2.getY(), vec1.getZ() - vec2.getZ());
	}
	
	public static Vector3f multiply(Vector3f vec, float num) {
		return new Vector3f(vec.getX() * num, vec.getY() * num, vec.getZ() * num);
	}

	public static Vector3f divide(Vector3f vec, float num) {
		return new Vector3f(vec.getX() / num, vec.getY() / num, vec.getZ() / num);
	}
	
	public static float length(Vector3f vec) {
		return (float) Math.sqrt(vec.getX() * vec.getX() + vec.getY() * vec.getY() + vec.getZ() * vec.getZ());
	}
	
	public static Vector3f normalize(Vector3f vec) {
		return divide(vec, length(vec));
	}
	
	public static float dotProduct(Vector3f vec1, Vector3f vec2) {
		return vec1.getX() * vec2.getX() + vec1.getY() * vec2.getY() + vec1.getZ() * vec2.getZ();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
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
		Vector3f other = (Vector3f) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
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

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public static Vector3f add(Vector3f vector1, Vector3f vector2) {
		Vector3f result = new Vector3f(vector1.getX() + vector2.getX(),
									   vector1.getY() + vector2.getY(),
									   vector1.getZ() + vector2.getZ());

		return result;
	}

	public static Vector3f multiply(Vector3f vector, float value) {
		Vector3f result = new Vector3f(value * vector.getX(), value * vector.getY(), value * vector.getZ());

		return result;
	}

	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
