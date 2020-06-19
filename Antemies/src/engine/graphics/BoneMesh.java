package engine.graphics;

import engine.model_loaders.AnimModelLoader;
import engine.model_loaders.Bone;
import engine.model_loaders.Node;
import engine.model_loaders.Weight;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.Assimp;

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
	private List<Bone> boneList;
	private Map<Integer, List<Weight>> weightMap;
	private Node root;

	public BoneMesh(Vertex[] vertices, int[] indices, List<Bone> boneList, Map<Integer, List<Weight>> weightMap, Node root) {
		super(vertices, indices, null);
		this.boneList = boneList;
		this.weightMap = weightMap;
		this.root = root;
	}

	// for every bone create the transforms to get into bone space
	// we multiply all parent node transformations, the bone offset transformation and the
	// transformation of the root node
	public Matrix4f[] getTransforms() {
		Matrix4f[] matrices = new Matrix4f[boneList.size()];
		for (int i = 0; i < boneList.size(); i++) {
			Node boneNode = root.findNode(boneList.get(i).name());
			Node temp = boneNode.getParent();
			Matrix4f offset = boneList.get(i).getOffsetMatrix();
			//Matrix4f transform = new org.joml.Matrix4f();

			Matrix4f transform = AnimModelLoader.AIMatrixToMatrix(boneNode.getTransformation());

			/*if (boneList.get(i).name().equals("rp_eric_rigged_001_lowerarm_l")) {
				Matrix4f rot = (new Matrix4f()).identity();
				rot = rot.rotateZ(1.5708f);
				transform = transform.mul(rot);
			}*/

			while (temp.getParent() != null) {
				//Matrix4f matrix = AnimModelLoader.AIMatrixToMatrix(temp.getTransformation());
				Matrix4f matrix;

				/*if (temp.getName().equals("rp_eric_rigged_001_lowerarm_l")) {
					Matrix4f rot = (new Matrix4f()).identity();
					rot = rot.rotateZ(1.5708f);
					matrix = AnimModelLoader.AIMatrixToMatrix(temp.getTransformation()).mul(rot);
				}*/ // else {
					matrix = AnimModelLoader.AIMatrixToMatrix(temp.getTransformation());
				//}

				transform = transform.mul(matrix);
				temp = temp.getParent();
			}

			transform = transform.mul(offset);
			transform = AnimModelLoader.AIMatrixToMatrix(root.getTransformation()).mul(transform);

			matrices[i] = transform;
		}
		return matrices;
	}

	public Bone getBoneByName(String name) {
		for (Bone bone : boneList) {
			if (bone.name().equals(name)) {
				return bone;
			}
		}
		return null;
	}

	public List<Weight> getWeightsForVertex(int id) {
		return weightMap.get(id);
	}

	public Set<Integer> getWeightsKeys() {
		return weightMap.keySet();
	}
}
