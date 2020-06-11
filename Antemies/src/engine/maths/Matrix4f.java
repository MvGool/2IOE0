package engine.maths;

import java.nio.FloatBuffer;
import java.util.Arrays;

/* INDEX: (COLUMN, ROW) */
public class Matrix4f {
	public static final int SIZE = 4;
	private float 	m00, m01, m02, m03,
					m10, m11, m12, m13,
					m20, m21, m22, m23,
					m30, m31, m32, m33;
	
	public static Matrix4f identity() {
		Matrix4f result = new Matrix4f();
		
		result.m00 = 1.0f;
		result.m11 = 1.0f;
		result.m22 = 1.0f;
		result.m33 = 1.0f;
		
		return result;
	}
	
	public static Matrix4f translate(Vector3f translation) {
		Matrix4f result = Matrix4f.identity();
		
		result.m30 = translation.getX();
		result.m31 = translation.getY();
		result.m32 = translation.getZ();
		
		return result;
	}
	
	public static Matrix4f rotate(float angle, Vector3f axis) {
		float x = axis.getX();
		float y = axis.getY();
		float z = axis.getZ();
		
		if (y == 0.0f && z == 0.0f && Math.abs(x) == 1)
            return rotationX(x * angle);
        else if (x == 0.0f && z == 0.0f && Math.abs(y) == 1)
            return rotationY(y * angle);
        else if (x == 0.0f && y == 0.0f && Math.abs(z) == 1)
            return rotationZ(z * angle);
        return rotationInternal(angle, x, y, z);
	}
     
	private static Matrix4f rotationInternal(float angle, float x, float y, float z) {
        Matrix4f result = Matrix4f.identity();
		
		float cos = (float) Math.cos(Math.toRadians(angle));
		float sin = (float) Math.sin(Math.toRadians(angle));
		float invcos = 1 - cos;
		
		result.m00 = cos + x * x * invcos;
		result.m10 = x * y * invcos - z * sin;
		result.m20 = x * z * invcos + y * sin;
		result.m01 = x * y * invcos + z * sin;
		result.m11 = cos + y * y * invcos;
		result.m21 = y * z * invcos - x * sin;
		result.m02 = z * x * invcos - y * sin;
		result.m12 = z * y * invcos + x * sin;
		result.m22 = cos + z * z * invcos;
		
		return result;
	}
	
	private static Matrix4f rotationX(float angle) {
		Matrix4f result = Matrix4f.identity();
		
		float cos = (float) Math.cos(Math.toRadians(angle));
		float sin = (float) Math.sin(Math.toRadians(angle));

		result.m11 = cos;
		result.m12 = sin;
		result.m21 = -sin;
		result.m22 = cos;
		
		return result;
	}
	
	private static Matrix4f rotationY(float angle) {
		Matrix4f result = Matrix4f.identity();
		
		float cos = (float) Math.cos(Math.toRadians(angle));
		float sin = (float) Math.sin(Math.toRadians(angle));

		result.m00 = cos;
		result.m02 = -sin;
		result.m20 = sin;
		result.m22 = cos;
		
		return result;
	}
	
	private static Matrix4f rotationZ(float angle) {
		Matrix4f result = Matrix4f.identity();
		
		float cos = (float) Math.cos(Math.toRadians(angle));
		float sin = (float) Math.sin(Math.toRadians(angle));

		result.m00 = cos;
		result.m01 = sin;
		result.m10 = -sin;
		result.m11 = cos;
		
		return result;
	}
	
	public static Matrix4f scale(Vector3f scalar) {
		Matrix4f result = Matrix4f.identity();
		
		result.m00 = scalar.getX();
		result.m11 = scalar.getY();
		result.m22 = scalar.getZ();
		
		return result;
	}
	
	public static Matrix4f transform(Vector3f position, Vector3f rotation, Vector3f scalar) {
		Matrix4f result = Matrix4f.identity();
		
		Matrix4f translationMatrix = Matrix4f.translate(position);
		Matrix4f xRotationMatrix = Matrix4f.rotate(rotation.getX(), new Vector3f(1, 0, 0));
		Matrix4f yRotationMatrix = Matrix4f.rotate(rotation.getY(), new Vector3f(0, 1, 0));
		Matrix4f zRotationMatrix = Matrix4f.rotate(rotation.getZ(), new Vector3f(0, 0, 1));
		Matrix4f scalarMatrix = Matrix4f.scale(scalar);
		
		Matrix4f rotationMatrix = Matrix4f.multiply(zRotationMatrix, Matrix4f.multiply(yRotationMatrix, xRotationMatrix));
		
		result = Matrix4f.multiply(translationMatrix, Matrix4f.multiply(rotationMatrix, scalarMatrix));
				
		return result;
	}
	
	public static Matrix4f projection(float fov, float aspectRatio, float near, float far) {
		Matrix4f result = Matrix4f.identity();
		
		float tanFOV = (float) Math.tan(Math.toRadians(fov/2));
		
		result.m00 = 1.0f / (aspectRatio * tanFOV);
		result.m11 = 1.0f / tanFOV;
		result.m22 = (far + near) / (near - far);
		result.m23 = -1.0f;
		result.m32 = (2*far*near) / (near - far);
		result.m33 = 0.0f;
		
		return result;
	}
	
	public static Matrix4f view(Vector3f position, Vector3f rotation) {
		Matrix4f result = Matrix4f.identity();
				
		Matrix4f translationMatrix = Matrix4f.translate(position.invert());
		Matrix4f xRotationMatrix = Matrix4f.rotate(rotation.getX(), new Vector3f(1, 0, 0));
		Matrix4f yRotationMatrix = Matrix4f.rotate(rotation.getY(), new Vector3f(0, 1, 0));
		Matrix4f zRotationMatrix = Matrix4f.rotate(rotation.getZ(), new Vector3f(0, 0, 1));
		
		Matrix4f rotationMatrix = Matrix4f.multiply(zRotationMatrix, Matrix4f.multiply(yRotationMatrix, xRotationMatrix));
		
		result = Matrix4f.multiply(rotationMatrix, translationMatrix);
				
		return result;
	}
	
	public static Matrix4f multiply(Matrix4f matrix1, Matrix4f matrix2) {
		Matrix4f result = Matrix4f.identity();
		
		result.m00 = matrix1.m00*matrix2.m00 + matrix1.m10*matrix2.m01 + matrix1.m20*matrix2.m02 + matrix1.m30 * matrix2.m03;
        result.m01 = matrix1.m01*matrix2.m00 + matrix1.m11*matrix2.m01 + matrix1.m21*matrix2.m02 + matrix1.m31 * matrix2.m03;
        result.m02 = matrix1.m02*matrix2.m00 + matrix1.m12*matrix2.m01 + matrix1.m22*matrix2.m02 + matrix1.m32 * matrix2.m03;
        result.m03 = matrix1.m03*matrix2.m00 + matrix1.m13*matrix2.m01 + matrix1.m23*matrix2.m02 + matrix1.m33 * matrix2.m03;
        result.m10 = matrix1.m00*matrix2.m10 + matrix1.m10*matrix2.m11 + matrix1.m20*matrix2.m12 + matrix1.m30 * matrix2.m13;
        result.m11 = matrix1.m01*matrix2.m10 + matrix1.m11*matrix2.m11 + matrix1.m21*matrix2.m12 + matrix1.m31 * matrix2.m13;
        result.m12 = matrix1.m02*matrix2.m10 + matrix1.m12*matrix2.m11 + matrix1.m22*matrix2.m12 + matrix1.m32 * matrix2.m13;
        result.m13 = matrix1.m03*matrix2.m10 + matrix1.m13*matrix2.m11 + matrix1.m23*matrix2.m12 + matrix1.m33 * matrix2.m13;
        result.m20 = matrix1.m00*matrix2.m20 + matrix1.m10*matrix2.m21 + matrix1.m20*matrix2.m22 + matrix1.m30 * matrix2.m23;
        result.m21 = matrix1.m01*matrix2.m20 + matrix1.m11*matrix2.m21 + matrix1.m21*matrix2.m22 + matrix1.m31 * matrix2.m23;
        result.m22 = matrix1.m02*matrix2.m20 + matrix1.m12*matrix2.m21 + matrix1.m22*matrix2.m22 + matrix1.m32 * matrix2.m23;
        result.m23 = matrix1.m03*matrix2.m20 + matrix1.m13*matrix2.m21 + matrix1.m23*matrix2.m22 + matrix1.m33 * matrix2.m23;
        result.m30 = matrix1.m00*matrix2.m30 + matrix1.m10*matrix2.m31 + matrix1.m20*matrix2.m32 + matrix1.m30 * matrix2.m33;
        result.m31 = matrix1.m01*matrix2.m30 + matrix1.m11*matrix2.m31 + matrix1.m21*matrix2.m32 + matrix1.m31 * matrix2.m33;
        result.m32 = matrix1.m02*matrix2.m30 + matrix1.m12*matrix2.m31 + matrix1.m22*matrix2.m32 + matrix1.m32 * matrix2.m33;
        result.m33 = matrix1.m03*matrix2.m30 + matrix1.m13*matrix2.m31 + matrix1.m23*matrix2.m32 + matrix1.m33 * matrix2.m33;
		
		return result;
	}
	
	public static MatrixXf toMatrixXf(Matrix4f matrix) {
		MatrixXf result = new MatrixXf(SIZE);
		VectorXf row = new VectorXf(SIZE);
		
		row.set(0, matrix.m00);
		row.set(1, matrix.m01);
		row.set(2, matrix.m02);
		row.set(3, matrix.m03);
		result.setRow(0, row);
		
		row.set(0, matrix.m10);
		row.set(1, matrix.m11);
		row.set(2, matrix.m12);
		row.set(3, matrix.m13);
		result.setRow(1, row);
		
		row.set(0, matrix.m20);
		row.set(1, matrix.m21);
		row.set(2, matrix.m22);
		row.set(3, matrix.m23);
		result.setRow(2, row);
		
		row.set(0, matrix.m30);
		row.set(1, matrix.m31);
		row.set(2, matrix.m32);
		row.set(3, matrix.m33);
		result.setRow(3, row);
		
		return result;
	}
	
	public void set(int column, int row, float value) {
		switch (column) {
        case 0:
            switch (row) {
            case 0:
                m00 = value;
            case 1:
                m01 = value;
            case 2:
                m02 = value;
            case 3:
                m03 = value;
            default:
                break;
            }
            break;
        case 1:
            switch (row) {
            case 0:
                m10 = value;
            case 1:
                m11 = value;
            case 2:
                m12 = value;
            case 3:
                m13 = value;
            default:
                break;
            }
            break;
        case 2:
            switch (row) {
            case 0:
                m20 = value;
            case 1:
                m21 = value;
            case 2:
                m22 = value;
            case 3:
                m23 = value;
            default:
                break;
            }
            break;
        case 3:
            switch (row) {
            case 0:
                m30 = value;
            case 1:
                m31 = value;
            case 2:
                m32 = value;
            case 3:
                m33 = value;
            default:
                break;
            }
            break;
        default:
            break;
        }
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(m00);
		result = prime * result + Float.floatToIntBits(m01);
		result = prime * result + Float.floatToIntBits(m02);
		result = prime * result + Float.floatToIntBits(m03);
		result = prime * result + Float.floatToIntBits(m10);
		result = prime * result + Float.floatToIntBits(m11);
		result = prime * result + Float.floatToIntBits(m12);
		result = prime * result + Float.floatToIntBits(m13);
		result = prime * result + Float.floatToIntBits(m20);
		result = prime * result + Float.floatToIntBits(m21);
		result = prime * result + Float.floatToIntBits(m22);
		result = prime * result + Float.floatToIntBits(m23);
		result = prime * result + Float.floatToIntBits(m30);
		result = prime * result + Float.floatToIntBits(m31);
		result = prime * result + Float.floatToIntBits(m32);
		result = prime * result + Float.floatToIntBits(m33);
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
		if (Float.floatToIntBits(m00) != Float.floatToIntBits(other.m00))
			return false;
		if (Float.floatToIntBits(m01) != Float.floatToIntBits(other.m01))
			return false;
		if (Float.floatToIntBits(m02) != Float.floatToIntBits(other.m02))
			return false;
		if (Float.floatToIntBits(m03) != Float.floatToIntBits(other.m03))
			return false;
		if (Float.floatToIntBits(m10) != Float.floatToIntBits(other.m10))
			return false;
		if (Float.floatToIntBits(m11) != Float.floatToIntBits(other.m11))
			return false;
		if (Float.floatToIntBits(m12) != Float.floatToIntBits(other.m12))
			return false;
		if (Float.floatToIntBits(m13) != Float.floatToIntBits(other.m13))
			return false;
		if (Float.floatToIntBits(m20) != Float.floatToIntBits(other.m20))
			return false;
		if (Float.floatToIntBits(m21) != Float.floatToIntBits(other.m21))
			return false;
		if (Float.floatToIntBits(m22) != Float.floatToIntBits(other.m22))
			return false;
		if (Float.floatToIntBits(m23) != Float.floatToIntBits(other.m23))
			return false;
		if (Float.floatToIntBits(m30) != Float.floatToIntBits(other.m30))
			return false;
		if (Float.floatToIntBits(m31) != Float.floatToIntBits(other.m31))
			return false;
		if (Float.floatToIntBits(m32) != Float.floatToIntBits(other.m32))
			return false;
		if (Float.floatToIntBits(m33) != Float.floatToIntBits(other.m33))
			return false;
		return true;
	}
	
	public FloatBuffer getMatrixFloatBuffer(FloatBuffer buffer) {
		return getMatrixFloatBuffer(0, buffer);
	}
		
	public FloatBuffer getMatrixFloatBuffer(int offset, FloatBuffer buffer) {
		buffer.put(offset,    m00)
        .put(offset+1,  m01)
        .put(offset+2,  m02)
        .put(offset+3,  m03)
        .put(offset+4,  m10)
        .put(offset+5,  m11)
        .put(offset+6,  m12)
        .put(offset+7,  m13)
        .put(offset+8,  m20)
        .put(offset+9,  m21)
        .put(offset+10, m22)
        .put(offset+11, m23)
        .put(offset+12, m30)
        .put(offset+13, m31)
        .put(offset+14, m32)
        .put(offset+15, m33);
		
		return buffer;
	}
		
	@Override
	public String toString() {
		String matrix = "[" + m00 + ", " + m01 + ", " + m02 + ", " + m03 + "] \n" + 
						"[" + m10 + ", " + m11 + ", " + m12 + ", " + m13 + "] \n" + 
						"[" + m20 + ", " + m21 + ", " + m22 + ", " + m23 + "] \n" + 
						"[" + m30 + ", " + m31 + ", " + m32 + ", " + m33 + "] \n";
		return matrix;
	}
}
