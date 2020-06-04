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
	
	public Vector3f computePosition(float t) {
		Vector3f result = Vector3f.add(Vector3f.add(Vector3f.add(Vector3f.multiply(a, t * t * t), Vector3f.multiply(b, t * t)), Vector3f.multiply(c, t)), d);
				
		return result;
	}
	
	public Vector3f computeTangent(float t) {
		Vector3f result = Vector3f.add(Vector3f.add(Vector3f.multiply(a, 3 * t * t), Vector3f.multiply(b, 2 * t)), c);
				
		return result;
	}
	
	@Override
	public String toString() {
		return a.toString() + "t^3 + " + b.toString() + "t^2 + " + c.toString() + "t + " + d.toString();
	}
}
