package engine.maths;

public class CubicPolynomial {
	private Vector3f a;
	private Vector3f b;
	private Vector3f c;
	private Vector3f d;

	public CubicPolynomial(Vector3f a, Vector3f b, Vector3f c, Vector3f d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	public Vector3f position(float t) {
		Vector3f result = Vector3f.add(Vector3f.add(Vector3f.add(Vector3f.multiply(a, t * t * t), Vector3f.multiply(b, t * t)), Vector3f.multiply(c, t)), d);
				
		return result;
	}
	
	public Vector3f getA() {
		return a;
	}

	public void setA(Vector3f a) {
		this.a = a;
	}

	public Vector3f getB() {
		return b;
	}

	public void setB(Vector3f b) {
		this.b = b;
	}

	public Vector3f getC() {
		return c;
	}

	public void setC(Vector3f c) {
		this.c = c;
	}

	public Vector3f getD() {
		return d;
	}

	public void setD(Vector3f d) {
		this.d = d;
	}
}
