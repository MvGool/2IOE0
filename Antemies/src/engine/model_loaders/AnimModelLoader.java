package engine.model_loaders;

import engine.graphics.BoneMesh;
import engine.graphics.Material;
import engine.graphics.Vertex;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.*;

/**
 * A model loader that is used to load models with skeletons
 */
public class AnimModelLoader extends ModelLoader
{
	// Loads the model and returns an array of Bonemeshes
	public static BoneMesh[] load(String modelPath, String texturePath) throws Exception {
		// try to load file
		AIScene scene = aiImportFile(modelPath, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals
										| aiProcess_LimitBoneWeights | aiProcessPreset_TargetRealtime_Quality);
		// If scene empty we have an error, so throw exception
		if (scene == null) {
			throw new Exception(aiGetErrorString());
		}
		//List<Bone> boneList = new ArrayList<>();
		//Map<Integer, List<Weight>> weightMap = new HashMap<>();
		PointerBuffer aiMeshes = scene.mMeshes();
		BoneMesh[] meshes = new BoneMesh[scene.mNumMeshes()];
		for (int i = 0; i < meshes.length; i++) {
			// for every mesh we create lists for its info
			List<Bone> boneList = new ArrayList<>();
			Map<Integer, List<Weight>> weightMap = new HashMap<>();
			AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));

			// then we invoke methods to fill these lists and construct the mesh
			processBones(aiMesh, boneList, weightMap);
			Node root = buildNodesHierarchy(scene.mRootNode());
			BoneMesh mesh = processMesh(aiMesh, texturePath, boneList, weightMap, root);
			meshes[i] = mesh;
		}
		return meshes;
	}

	// Goes through all bones in the mesh and adds these to the given boneList
	// Also creates a map with vertex ids and their corresponding weight
	private static void processBones(AIMesh mesh, List<Bone> boneList, Map<Integer, List<Weight>> weightMap) {
		PointerBuffer aiBones = mesh.mBones();
		for (int i = 0; i < mesh.mNumBones(); i++) {
			AIBone aiBone = AIBone.create(aiBones.get(i));
			// create a new bone object and add it to the list
			Bone bone = new Bone(boneList.size(), aiBone.mName().dataString(), AIMatrixToMatrix(aiBone.mOffsetMatrix()));

			boneList.add(bone);

			AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
			for (int j = 0; j < aiBone.mNumWeights(); j++) {
				AIVertexWeight aiWeight = aiWeights.get(j);
				Weight weight = new Weight(bone.getId(), aiWeight.mVertexId(), aiWeight.mWeight());
				// if no weights specified yet for the vertex we create a new list
				// otherwise we just add the weight
				if (weightMap.get(aiWeight.mVertexId()) == null) {
					List<Weight> weights = new ArrayList<>();
					weights.add(weight);
					weightMap.put(aiWeight.mVertexId(), weights);
				} else {
					weightMap.get(aiWeight.mVertexId()).add(weight);
				}
			}
		}
	}

	// Creates all information needed to create a boneMesh
	// AnimModelLoader needs its own processMesh as we need to give a boneList and weightMap
	static BoneMesh processMesh(AIMesh aiMesh, String texturePath, List<Bone> boneList, Map<Integer, List<Weight>> weightMap, Node root) {
		List<Vertex> vertices = processVertices(aiMesh);
		List<Integer> indices = processIndices(aiMesh);

		Vertex[] verticesArray = new Vertex[vertices.size()];
		if (texturePath == null) {
			return new BoneMesh(vertices.toArray(verticesArray),
					indices.stream().mapToInt(i->i).toArray(),
					boneList,
					weightMap,
					root,
					null
			);
		} else {
			return new BoneMesh(vertices.toArray(verticesArray),
					indices.stream().mapToInt(i->i).toArray(),
					boneList,
					weightMap,
					root,
					new Material(texturePath)
			);
		}
	}

	// start building a node hierarchy from the given root node
	private static Node buildNodesHierarchy(AINode root) {
		Node rootNode = new Node(null, root.mName().dataString(), root.mTransformation());

		// we loop through all children of the root and handle their children
		PointerBuffer aiMeshes = root.mChildren();
		for (int i = 0; i < root.mNumChildren(); i++) {
			AINode aiChild = AINode.create(aiMeshes.get(i));
			Node child = new Node(rootNode, aiChild.mName().dataString(), aiChild.mTransformation());
			processNodeChildren(aiChild, child);
			rootNode.addChild(child);
		}

		return rootNode;
	}

	// here we take the given AINode and Node and loop through all children.
	// We add the children as children of the Node object and the children Node objects
	// get the given Node as parent.
	// works recursively to travle through all the node remaining in the tree
	private static void processNodeChildren(AINode aiNode, Node node) {
		PointerBuffer aiMeshes = aiNode.mChildren();
		for (int i = 0; i < aiNode.mNumChildren(); i++) {
			AINode aiChild = AINode.create(aiMeshes.get(i));
			Node child = new Node(node, aiChild.mName().dataString(), aiChild.mTransformation());
			processNodeChildren(aiChild, child);
			node.addChild(child);
		}
	}

	// Transform an AIMatrix to a Joml matrix
	public static Matrix4f AIMatrixToMatrix(AIMatrix4x4 matrix) {
		org.joml.Matrix4f result = new org.joml.Matrix4f();

		result.m00(matrix.a1());
		result.m10(matrix.a2());
		result.m20(matrix.a3());
		result.m30(matrix.a4());
		result.m01(matrix.b1());
		result.m11(matrix.b2());
		result.m21(matrix.b3());
		result.m31(matrix.b4());
		result.m02(matrix.c1());
		result.m12(matrix.c2());
		result.m22(matrix.c3());
		result.m32(matrix.c4());
		result.m03(matrix.d1());
		result.m13(matrix.d2());
		result.m23(matrix.d3());
		result.m33(matrix.d4());

		// we transpose the returned matrix as AIMatrix is row majore
		// while joml matrices are not
		return result.transpose();
	}
}
