package engine.graphics;

import engine.model_loaders.AnimModelLoader;
import engine.objects.AnimGameObject;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

import engine.io.Window;
import engine.maths.Matrix4f;
import engine.objects.Camera;
import engine.objects.GameObject;
import engine.utils.FileUtils;

public class Renderer {
	private Window window;
	private ShaderProgram objectShader;
	private ShaderProgram terrainShader;

	public Renderer(Window window) {
		this.window = window;
	}
	
	public void init() throws Exception {
		// Load and bind objectShader
		objectShader = new ShaderProgram();
		objectShader.createVertexShader(FileUtils.loadAsString("/shaders/mainVertex.glsl"));
		objectShader.createFragmentShader(FileUtils.loadAsString("/shaders/mainFragment.glsl"));
		objectShader.link();
		// Load and bind terrainShader
		terrainShader = new ShaderProgram();
		terrainShader.createVertexShader(FileUtils.loadAsString("/shaders/terrain/terrainVertex.vs"));
		terrainShader.createFragmentShader(FileUtils.loadAsString("/shaders/terrain/terrainFragment.fs"));
		terrainShader.link();
		try {
			createUniforms();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void renderMesh(GameObject object, Camera camera) {
		for (Mesh m : object.getMeshes()) {
			glBindVertexArray(m.getVAO());
			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);
			glEnableVertexAttribArray(2);
			glEnableVertexAttribArray(3);
			glEnableVertexAttribArray(4);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m.getIBO());
			if (m.getMaterial() != null) {
				glActiveTexture(GL_TEXTURE0);
				glBindTexture(GL_TEXTURE_2D, m.getMaterial().getTextureID());
			}
			objectShader.bind();
			objectShader.setUniform("model", Matrix4f.transform(object.getPosition(), object.getRotation(), object.getScalar()));
			objectShader.setUniform("view", Matrix4f.view(camera.getPosition(), camera.getRotation()));
			objectShader.setUniform("projection", window.getProjectionMatrix());
			// If it is an AnimGameObject we make the shader use the skeleton
			// for vertex deformation
			if (object instanceof AnimGameObject) {
				objectShader.setUniform("useSkeleton", true);
				org.joml.Matrix4f[] transforms = ((AnimGameObject)object).getMeshes()[0].getTransforms();
				objectShader.setUniform("boneMatrix", transforms);
			} else {
				objectShader.setUniform("useSkeleton", false);
			}
			glDrawElements(GL_TRIANGLES, m.getIndices().length, GL_UNSIGNED_INT, 0);
			objectShader.unbind();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(1);
			glDisableVertexAttribArray(2);
			glDisableVertexAttribArray(3);
			glDisableVertexAttribArray(4);
			glBindVertexArray(0);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
	}
	
	public void renderTerrain(Mesh terrainMesh, Camera camera) {
		glBindVertexArray(terrainMesh.getVAO());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(2);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, terrainMesh.getIBO());
		if (terrainMesh.getMaterial() != null) {
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, terrainMesh.getMaterial().getTextureID());
		}
		terrainShader.bind();
		terrainShader.setUniform("model", Matrix4f.identity());
		terrainShader.setUniform("view", Matrix4f.view(camera.getPosition(), camera.getRotation()));
		terrainShader.setUniform("projection", window.getProjectionMatrix());
		glDrawElements(GL_TRIANGLES, terrainMesh.getIndices().length, GL_UNSIGNED_INT, 0);
		terrainShader.unbind();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	private void createUniforms() throws Exception {
		objectShader.createUniform("model");
		objectShader.createUniform("view");
		objectShader.createUniform("projection");
		
		terrainShader.createUniform("model");
		terrainShader.createUniform("view");
		terrainShader.createUniform("projection");
	}
}
