package engine.model_loaders;

import org.joml.Matrix4f;
import org.lwjgl.assimp.AIMatrix4x4;

import java.util.ArrayList;
import java.util.List;

public class Node
{
	private List<Node> children;
	private Node parent;
	private String name;
	private AIMatrix4x4 transformation;

	public Node(Node parent, String name, AIMatrix4x4 matrix) {
		this.name = name;
		this.parent = parent;
		this.children = new ArrayList<>();
		this.transformation = matrix;
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

	public AIMatrix4x4 getTransformation() {
		return transformation;
}
}
