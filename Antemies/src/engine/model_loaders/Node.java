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
		// immediately store the transformation as a joml matrix as these
		// are easier to work with
		this.transformation = AnimModelLoader.AIMatrixToMatrix(matrix);
	}

	public void addChild(Node child) {
		children.add(child);
	}

	// recursively try to find a node by name
	public Node findNode(String name) {
		// if the name is the same we return it immediately
		if (this.name.equals(name)) {
			return this;
		} else {
			Node result = null;
			for (Node child : children) {
				// otherwise we keep looking through all children
				result = child.findNode(name);
				if (result != null) {
					return result;
				}
			}
		}
		// if we haven't returned at this point then we can conclude that there was
		// no node with the given name
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
