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
	// build the mesh using the loaded vertices and indices
	static Mesh processMesh(AIMesh aiMesh, String texturePath) {

		List<Vertex> vertices = processVertices(aiMesh);
		List<Integer> indices = processIndices(aiMesh);

		Vertex[] verticesArray = new Vertex[vertices.size()];
		// if no texture path specified we don't have to add a material
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

	// load all the vertices from the model fata
	static List<Vertex> processVertices(AIMesh aiMesh) {
		// get buffer for all parts we need for a vertex
		AIVector3D.Buffer aiVertices = aiMesh.mVertices();
		AIVector3D.Buffer aiNormals = aiMesh.mNormals();
		AIVector3D.Buffer aiTextCoords = aiMesh.mTextureCoords(0);

		// loop through all vertices and add them to a list
		List<Vertex> vertices = new ArrayList<>();
		while (aiVertices.remaining() > 0) {
			AIVector3D aiVertex = aiVertices.get();

			Vector3f normalV;
			Vector2f texV;

			// we check if there are normal and/or texture present, if so we load that information
			// if not we give null. If there are errors with models it is likely that they don't have texture coordinates
			if (aiNormals == null) {
				System.out.println("non existing normals");
				normalV = new Vector3f(0, 1, 0);
			} else {
				AIVector3D aiNormal = aiNormals.get();
				normalV = new Vector3f(aiNormal.x(), aiNormal.y(), aiNormal.z());
			}

			// if no texture coords we set them as null
			if (aiTextCoords == null) {
				System.out.println("no texCoords");
				texV = null;
			} else {
				AIVector3D textCoords = aiTextCoords.get();
				texV = new Vector2f(textCoords.x(), 1 - textCoords.y());
			}

			vertices.add(new Vertex(
					new Vector3f(aiVertex.x(), aiVertex.y(), aiVertex.z()),
					normalV,
					texV
				)
			);
		}
		return vertices;
	}

	// get all the indices from the model data for use in the mesh
	static List<Integer> processIndices(AIMesh aiMesh) {
		List<Integer> indices = new ArrayList<>();
		AIFace.Buffer aiFaces = aiMesh.mFaces();
		for (int i = 0; i < aiMesh.mNumFaces(); i++) {
			AIFace aiFace = aiFaces.get(i);
			IntBuffer buffer = aiFace.mIndices();
			while (buffer.remaining() > 0) {
				indices.add(buffer.get());
			}
		}
		return indices;
	}
}
