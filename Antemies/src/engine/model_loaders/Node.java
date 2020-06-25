package engine.model_loaders;

import org.joml.Matrix4f;
import org.lwjgl.assimp.AIMatrix4x4;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a node as given by the Assimp model loader
 */
public class Node
{
	// all the children of the node
	private List<Node> children;
	// the parent of the node
	private Node parent;
	// the name
	private String name;
	// the transformation matrix of the node, also called
	// the local transform
	private Matrix4f transformation;

	public Node(Node parent, String name, AIMatrix4x4 matrix) {
		this.name = name;
		this.parent = parent;
		this.children = new ArrayList<>();
		this.transformation = AnimModelLoader.AIMatrixToMatrix(matrix);
	}

	public void addChild(Node child) {
		children.add(child);
	}

	public Node findNode(String name) {
		if (this.name.equals(name)) {
			return this;
		} else {
			Node result = null;
			for (Node child : children) {
				result = child.findNode(name);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	public Node getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public Matrix4f getTransformation() {
		return new Matrix4f(transformation);
	}

	public void setTransformation(Matrix4f matrix) {
		transformation = matrix;
	}

}
