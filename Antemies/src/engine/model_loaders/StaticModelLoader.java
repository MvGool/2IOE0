package engine.model_loaders;

import engine.graphics.Mesh;
import engine.graphics.Vertex;
import org.lwjgl.PointerBuffer;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import static org.lwjgl.assimp.Assimp.*;

public class StaticModelLoader
{

	public static Mesh[] load(String src) throws Exception {
		src = System.getProperty("user.dir") + "/Antemies/resources" + src;
		AIScene scene = aiImportFile(src, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals);
		if (scene == null || scene.mRootNode() == null) {
			throw new Exception(aiGetErrorString());
		}

		PointerBuffer aiMeshes = scene.mMeshes();
		Mesh[] meshes = new Mesh[scene.mNumMeshes()];
		for (int i = 0; i < meshes.length; i++) {
			AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
			Mesh mesh = processMesh(aiMesh);
			meshes[i] = mesh;
		}
		return meshes;
	}

	private static Mesh processMesh(AIMesh aiMesh) {

		List<Vertex> vertices = processVertices(aiMesh);
		List<Integer> indices = processIndices(aiMesh);

		Vertex[] verticesArray = new Vertex[vertices.size()];
		return new Mesh(vertices.toArray(verticesArray),
				indices.stream().mapToInt(i->i).toArray()
		);
	}

	private static List<Vertex> processVertices(AIMesh aiMesh) {
		AIVector3D.Buffer aiVertices = aiMesh.mVertices();
		List<Vertex> vertices = new ArrayList<>();
		while (aiVertices.remaining() > 0) {
			AIVector3D aiVertex = aiVertices.get();
			vertices.add(new Vertex(aiVertex.x(), aiVertex.y(), aiVertex.z()));
		}
		return vertices;
	}

	private static List<Integer> processIndices(AIMesh aiMesh) {
		List<Integer> indices = new ArrayList<>();
		int numFaces = aiMesh.mNumFaces();
		AIFace.Buffer aiFaces = aiMesh.mFaces();
		for (int i = 0; i < numFaces; i++) {
			AIFace aiFace = aiFaces.get(i);
			IntBuffer buffer = aiFace.mIndices();
			while (buffer.remaining() > 0) {
				indices.add(buffer.get());
			}
		}
		return indices;
	}
}
