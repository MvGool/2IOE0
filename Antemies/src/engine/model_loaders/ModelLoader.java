package engine.model_loaders;

import engine.graphics.Material;
import engine.graphics.Mesh;
import engine.graphics.Vertex;
import engine.maths.Vector2f;
import engine.maths.Vector3f;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelLoader
{
	static Mesh processMesh(AIMesh aiMesh, String texturePath) {

		List<Vertex> vertices = processVertices(aiMesh);
		List<Integer> indices = processIndices(aiMesh);

		Vertex[] verticesArray = new Vertex[vertices.size()];
		if (texturePath == null) {
			return new Mesh(vertices.toArray(verticesArray),
					indices.stream().mapToInt(i->i).toArray()
			);
		} else {
			return new Mesh(vertices.toArray(verticesArray),
					indices.stream().mapToInt(i->i).toArray(),
					new Material(texturePath)
			);
		}
	}

	static List<Vertex> processVertices(AIMesh aiMesh) {
		AIVector3D.Buffer aiVertices = aiMesh.mVertices();
		AIVector3D.Buffer aiNormals = aiMesh.mNormals();
		AIVector3D.Buffer aiTextCoords = aiMesh.mTextureCoords(0);

		List<Vertex> vertices = new ArrayList<>();
		while (aiVertices.remaining() > 0 && aiTextCoords.remaining() > 0) {
			AIVector3D aiVertex = aiVertices.get();
			AIVector3D aiNormal = aiNormals.get();
			AIVector3D textCoords = aiTextCoords.get();
			vertices.add(new Vertex(
					new Vector3f(aiVertex.x(), aiVertex.y(), aiVertex.z()),
					new Vector3f(aiNormal.x(), aiNormal.y(), aiNormal.z()),
					new Vector2f(textCoords.x(), 1 - textCoords.y())
				)
			);
		}
		return vertices;
	}

	static List<Integer> processIndices(AIMesh aiMesh) {
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
