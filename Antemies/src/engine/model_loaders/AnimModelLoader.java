package engine.model_loaders;

import engine.graphics.BoneMesh;
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
	public static BoneMesh[] load(String modelPath) throws Exception {
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
			List<Bone> boneList = new ArrayList<>();
			Map<Integer, List<Weight>> weightMap = new HashMap<>();
			AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
			processBones(aiMesh, boneList, weightMap);
			Node root = processNodes(scene.mRootNode() , null);
			BoneMesh mesh = processMesh(aiMesh, null, boneList, weightMap, root);
			meshes[i] = mesh;
		}
		//System.out.println(meshes.length);
		return meshes;
	}

	// Goes through all bones in the mesh and adds these to the given boneList
	// Also creates a map with vertex ids and their corresponding weight
	private static void processBones(AIMesh mesh, List<Bone> boneList, Map<Integer, List<Weight>> weightMap) {
		PointerBuffer aiBones = mesh.mBones();
		for (int i = 0; i < mesh.mNumBones(); i++) {
			AIBone aiBone = AIBone.create(aiBones.get(i));
			Bone bone = new Bone(boneList.size(), aiBone.mName().dataString(), AIMatrixToMatrix(aiBone.mOffsetMatrix()));

			boneList.add(bone);

			AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
			for (int j = 0; j < aiBone.mNumWeights(); j++) {
				AIVertexWeight aiWeight = aiWeights.get(j);
				//TODO calculate the weights ourselves
				Weight weight = new Weight(bone.getId(), aiWeight.mVertexId(), aiWeight.mWeight());
				if (weightMap.get(aiWeight.mVertexId()) == null) {
					List<Weight> weights = new ArrayList<>();
					weights.add(weight);
					weightMap.put(aiWeight.mVertexId(), weights);
				} else {
					weightMap.get(aiWeight.mVertexId()).add(weight);
				}
			}
		}
		System.out.println("weightmap size: " + weightMap.size());
		System.out.println("Vertices: " + mesh.mNumVertices());
	}

	// Creates all information needed to create a boneMesh
	static BoneMesh processMesh(AIMesh aiMesh, String texturePath, List<Bone> boneList, Map<Integer, List<Weight>> weightMap, Node root) {
		List<Vertex> vertices = processVertices(aiMesh);
		List<Integer> indices = processIndices(aiMesh);

		System.out.println(vertices.size());

		Vertex[] verticesArray = new Vertex[vertices.size()];
			return new BoneMesh(vertices.toArray(verticesArray),
					indices.stream().mapToInt(i->i).toArray(),
					boneList,
					weightMap,
					root
			);
	}

	// build a node hierarchy using the root node
	private static Node processNodes(AINode aiNode, Node parent) {
		Node node = new Node(parent, aiNode.mName().dataString(), aiNode.mTransformation());

		PointerBuffer aiMeshes = aiNode.mChildren();
		for (int i = 0; i < aiNode.mNumChildren(); i++) {
			AINode aiChild = AINode.create(aiMeshes.get(i));
			Node child = processNodes(aiChild, node);
			node.addChild(child);
		}
		return node;
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

		return result.transpose();
	}

	// Transform a joml matrix to our own matrix implementation
	public static engine.maths.Matrix4f jomlMatrixToMatrix(Matrix4f matrix) {
		engine.maths.Matrix4f result = new engine.maths.Matrix4f();
		result.set(0, 0, matrix.m00());
		result.set(0, 1, matrix.m01());
		result.set(0, 2, matrix.m02());
		result.set(0, 3, matrix.m03());
		result.set(1, 0, matrix.m10());
		result.set(1, 1, matrix.m11());
		result.set(1, 2, matrix.m12());
		result.set(1, 3, matrix.m13());
		result.set(2, 0, matrix.m20());
		result.set(2, 1, matrix.m21());
		result.set(2, 2, matrix.m22());
		result.set(2, 3, matrix.m23());
		result.set(3, 0, matrix.m30());
		result.set(3, 1, matrix.m31());
		result.set(3, 2, matrix.m32());
		result.set(3, 3, matrix.m33());

		return result;
	}
}
