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
	
	public void add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
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
