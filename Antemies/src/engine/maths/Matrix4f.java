package engine.maths;

import java.util.Arrays;

public class Matrix4f {
	public static final int SIZE = 4;
	private float[] elements = new float[SIZE * SIZE];
	
	public static Matrix4f identity() {
		Matrix4f result = new Matrix4f();
		
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (i == j) {
					result.set(i,  j, 1);
				} else {
					result.set(i,  j, 0);
				}
			}
		}
		
		return result;
	}
	
	public static Matrix4f translate(Vector3f translation) {
		Matrix4f result = Matrix4f.identity();
		
		result.set(3, 0, translation.getX());
		result.set(3, 1, translation.getY());
		result.set(3, 2, translation.getZ());
		
		return result;
	}
	
	public static Matrix4f rotate(float angle, Vector3f axis) {
		Matrix4f result = Matrix4f.identity();
		
		float cos = (float) Math.cos(Math.toRadians(angle));
		float sin = (float) Math.sin(Math.toRadians(angle));
		float invcos = 1 - cos;
		
		result.set(0, 0, cos + axis.getX() * axis.getX() * invcos);
		result.set(0, 1, axis.getX() * axis.getY() * invcos - axis.getZ() * sin);
		result.set(0, 2, axis.getX() * axis.getZ() * invcos + axis.getY() * sin);
		result.set(1, 0, axis.getY() * axis.getX() * invcos + axis.getZ() * sin);
		result.set(1, 1, cos + axis.getY() * axis.getY() * invcos);
		result.set(1, 2, axis.getY() * axis.getZ() * invcos - axis.getX() * sin);
		result.set(2, 0, axis.getZ() * axis.getX() * invcos - axis.getY() * sin);
		result.set(2, 1, axis.getZ() * axis.getY() * invcos + axis.getX() * sin);
		result.set(2, 2, cos + axis.getZ() * axis.getZ() * invcos);
		
		return result;
	}
	
	public static Matrix4f scale(Vector3f scalar) {
		Matrix4f result = Matrix4f.identity();
		
		result.set(0, 0, scalar.getX());
		result.set(1, 1, scalar.getY());
		result.set(2, 2, scalar.getZ());
		
		return result;
	}
	
	public static Matrix4f transform(Vector3f position, Vector3f rotation, Vector3f scalar) {
		Matrix4f result = Matrix4f.identity();
		
		Matrix4f translationMatrix = Matrix4f.translate(position);
		Matrix4f XRotationMatrix = Matrix4f.rotate(rotation.getX(), new Vector3f(1, 0, 0));
		Matrix4f YRotationMatrix = Matrix4f.rotate(rotation.getY(), new Vector3f(0, 1, 0));
		Matrix4f ZRotationMatrix = Matrix4f.rotate(rotation.getZ(), new Vector3f(0, 0, 1));
		Matrix4f scalarMatrix = Matrix4f.scale(scalar);
		
		Matrix4f rotationMatrix = Matrix4f.multiply(XRotationMatrix, Matrix4f.multiply(YRotationMatrix, ZRotationMatrix));
		
		result = Matrix4f.multiply(translationMatrix, Matrix4f.multiply(rotationMatrix, scalarMatrix));
				
		return result;
	}
	
	public static Matrix4f projection(float fov, float aspectRatio, float near, float far) {
		Matrix4f result = Matrix4f.identity();
		
		float tanFOV = (float) Math.atan(Math.toRadians(fov/2));
		float range = far - near;
		
		result.set(0, 0, 1.0f / (aspectRatio * tanFOV));
		result.set(1, 1, 1.0f / tanFOV);
		result.set(2, 2, -(far + near) / range);
		result.set(2, 3, -1.0f);
		result.set(3, 2, -(2*far*near) / range);
		
		return result;
	}
	
	public static Matrix4f view(Vector3f position, Vector3f rotation) {
		Matrix4f result = Matrix4f.identity();
		
		Vector3f negativePos = new Vector3f(-position.getX(), -position.getY(), -position.getZ());
		
		Matrix4f translationMatrix = Matrix4f.translate(negativePos);
		Matrix4f xRotationMatrix = Matrix4f.rotate(rotation.getX(), new Vector3f(1, 0, 0));
		Matrix4f yRotationMatrix = Matrix4f.rotate(rotation.getY(), new Vector3f(0, 1, 0));
		Matrix4f zRotationMatrix = Matrix4f.rotate(rotation.getZ(), new Vector3f(0, 0, 1));
		
		Matrix4f rotationMatrix = Matrix4f.multiply(zRotationMatrix, Matrix4f.multiply(yRotationMatrix, xRotationMatrix));
		
		result = Matrix4f.multiply(translationMatrix, rotationMatrix);
				
		return result;
	}
	
	public static Matrix4f multiply(Matrix4f matrix1, Matrix4f matrix2) {
		Matrix4f result = Matrix4f.identity();
		
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				result.set(i,  j, matrix1.get(i, 0) * matrix2.get(0, j) +
								  matrix1.get(i, 1) * matrix2.get(1, j)	+
								  matrix1.get(i, 2) * matrix2.get(2, j)	+
								  matrix1.get(i, 3) * matrix2.get(3, j));
			}
		}
		
		return result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(elements);
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
		Matrix4f other = (Matrix4f) obj;
		if (!Arrays.equals(elements, other.elements))
			return false;
		return true;
	}

	public float get(int x, int y) {
		return elements[y * SIZE + x];
	}
	
	public void set(int x, int y, float value) {
		elements[y * SIZE + x] = value;
	}
	
	public float[] getMatrix() {
		return elements;
	}
	
	public String toString() {
		String matrix = "";
		for (int i = 0; i < SIZE; i++) {
			matrix += "[";
			for (int j = 0; j < SIZE; j++) {
				if (j == SIZE - 1) {
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
