package engine.model_loaders;

import engine.graphics.Material;
import engine.graphics.Mesh;
import engine.graphics.Vertex;
import org.lwjgl.PointerBuffer;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.assimp.*;

import static org.lwjgl.assimp.Assimp.*;

public class StaticModelLoader extends ModelLoader {

	public static Mesh[] load(String modelPath, String texturePath) throws Exception {
		AIScene scene = aiImportFile(modelPath, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals);

		// if scene is null then there was an error
		if (scene == null || scene.mRootNode() == null) {
			throw new Exception(aiGetErrorString());
		}

		// loop through all meshes and extract all info to create them with our
		// own mesh class so we can render them
		PointerBuffer aiMeshes = scene.mMeshes();
		Mesh[] meshes = new Mesh[scene.mNumMeshes()];
		for (int i = 0; i < meshes.length; i++) {
			AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
			Mesh mesh = processMesh(aiMesh, texturePath); // Error here
			meshes[i] = mesh;
		}
		return meshes;
	}
}
