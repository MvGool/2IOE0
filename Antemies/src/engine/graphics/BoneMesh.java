package engine.graphics;

import engine.model_loaders.Bone;
import engine.model_loaders.Node;
import engine.model_loaders.Weight;
import org.joml.*;

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

		Matrix4f transform = boneNode.getTransformation();

		// we multiply the local transform with those of all the parents
		// up until the root node
		while (parent.getParent() != null) {
			Matrix4f matrix = parent.getTransformation();
			transform.mul(matrix);
			parent = parent.getParent();
		}

		// after that we multiply by offset matrix of the bone
		transform.mul(offset);
		// lastly we multiply the root transform with the transform and we then have
		// the final transform of the bone
		return root.getTransformation().mul(transform);
	}

	// interpolate between two given matrices using the given interpolation factor
	public static Matrix4f interpolate(Matrix4f m1, Matrix4f m2, float t) {
		// we extract the translation vectors
		Vector3f v1 = new Vector3f(m1.get(3,0), m1.get(3,1), m1.get(3,2));
		Vector3f v2 = new Vector3f(m2.get(3,0), m2.get(3,1), m2.get(3,2));
		// we use linear interpolation on them
		v1 = v1.lerp(v2, t);

		// we extract the rotation quaternions
		Quaternionf q1 = new Quaternionf();
		m1.getNormalizedRotation(q1);
		Quaternionf q2 = new Quaternionf();
		m2.getNormalizedRotation(q2);
		// we use spherical linear interpolation between them.
		q1 = q1.slerp(q2, t);

		// we use the calculated vector and quaternion to construct a new matrix that will be the result.
		Matrix4f result =  new Matrix4f().identity().rotate(q1);
		return result.translate(v1);
	}
}
