package engine.model_loaders;

import org.joml.Matrix4f;

/**
 * A class that represents a bone in a model\
 * Includes id, name and offsetmatrix
 */
public class Bone
{
	private int id;
	private String name;
	private Matrix4f offsetMatrix;

	public Bone(int id, String name, Matrix4f offsetMatrix) {
		this.id = id;
		this.name = name;
		this.offsetMatrix = offsetMatrix;
	}

	public int getId() {
		return this.id;
	}

	public String name() {
		return this.name;
	}

	public Matrix4f getOffsetMatrix() {
		return this.offsetMatrix;
	}
}
