package engine.graphics;

import engine.model_loaders.Bone;
import engine.model_loaders.Node;
import engine.model_loaders.Weight;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class that represents a mesh with its bones
 * Extends Mesh as it has some extra info like a list of bones, map of weights
 * and the root node
 */
public class BoneMesh extends Mesh
{
	// list of all the bones for the mesh
	private List<Bone> boneList;
	// a map thet holds the weights for each vertex
	private Map<Integer, List<Weight>> weightMap;
	// the root node of the mesh
	private Node root;

	public BoneMesh(Vertex[] vertices, int[] indices, List<Bone> boneList, Map<Integer, List<Weight>> weightMap, Node root, Material texture) {
		super(vertices, indices, texture);
		this.boneList = boneList;
		this.weightMap = weightMap;
		this.root = root;
	}

	// for every bone create the transforms to get into bone space
	// we multiply all parent node transformations, the bone offset transformation and the
	// transformation of the root node
	public Matrix4f[] getTransforms() {
		// we create an array to store the transforms for every bone
		Matrix4f[] matrices = new Matrix4f[boneList.size()];

		// for every bone we calculate the global transform and stoe it in the array
		for (int i = 0; i < boneList.size(); i++) {
			matrices[i] = getGlobalTransform(boneList.get(i));
		}
		return matrices;
	}

	public List<Weight> getWeightsForVertex(int id) {
		return weightMap.get(id);
	}

	// return a set of all vertex id in the weightmap
	public Set<Integer> getWeightsKeys() {
		return weightMap.keySet();
	}

	// compute the global transformation matrix of a bone by
	// multiplying its transformation matrix with those of its parents up until the root node.
	public Matrix4f getGlobalTransform(Bone bone) {
		Node boneNode = root.findNode(bone.name());
		Node parent = boneNode.getParent();
		Matrix4f offset = bone.getOffsetMatrix();
		System.out.println(bone.name());

		Matrix4f transform = boneNode.getTransformation();

		while (parent.getParent() != null) {
			Matrix4f matrix = parent.getTransformation();
			transform.mul(matrix);
			parent = parent.getParent();
		}

		transform.mul(offset);
		return root.getTransformation().mul(transform);
	}
}
