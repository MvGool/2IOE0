package engine.graphics;

import engine.model_loaders.AnimModelLoader;
import engine.objects.AnimGameObject;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.ArrayList;

import engine.io.Window;
import engine.maths.Matrix4f;
import engine.maths.Vector3f;
import engine.objects.Camera;
import engine.objects.GameObject;
import engine.utils.FileUtils;
import main.objects.FoodObject;

public class Renderer {
	private Window window;
	private ShaderProgram objectShader;
	private ShaderProgram terrainShader;
	private ShaderProgram shadowShader;
	private ShaderProgram trailShader;
	private Vector3f lightPosition = new Vector3f(1000, -2000, 2000);

	public Renderer(Window window) {
		this.window = window;
	}
	
	public void init() throws Exception {
		// Load and bind objectShader
		objectShader = new ShaderProgram();
		objectShader.createVertexShader(FileUtils.loadAsString("/shaders/mainVertex.vs"));
		objectShader.createFragmentShader(FileUtils.loadAsString("/shaders/mainFragment.fs"));
		objectShader.link();
		// Load and bind terrainShader
		terrainShader = new ShaderProgram();
		terrainShader.createVertexShader(FileUtils.loadAsString("/shaders/terrain/terrainVertex.vs"));
		terrainShader.createFragmentShader(FileUtils.loadAsString("/shaders/terrain/terrainFragment.fs"));
		terrainShader.link();
		// Load and bind shadowShader
		shadowShader = new ShaderProgram();
		shadowShader.createVertexShader(FileUtils.loadAsString("/shaders/terrain/shadowVertex.vs"));
		shadowShader.createFragmentShader(FileUtils.loadAsString("/shaders/terrain/shadowFragment.fs"));
		shadowShader.link();
		// Load and bind trailShader
		trailShader = new ShaderProgram();
		trailShader.createVertexShader(FileUtils.loadAsString("/shaders/terrain/trailVertex.vs"));
		trailShader.createFragmentShader(FileUtils.loadAsString("/shaders/terrain/trailFragment.fs"));
		trailShader.link();
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
			objectShader.setUniform("lightPos", lightPosition);
			objectShader.setUniform("viewPos", camera.getPosition());
			// If it is an AnimGameObject we make the shader use the skeleton
			// for vertex deformation
			if (object instanceof AnimGameObject) {
				objectShader.setUniform("useSkeleton", true);
				org.joml.Matrix4f[] transforms = ((BoneMesh)m).getTransforms();
				objectShader.setUniform("boneMatrix", transforms);
			} else {
				objectShader.setUniform("useSkeleton", false);
			}
			// Check if normalMap should be used
			if (m.getMaterial().hasNormalMap()) {
				objectShader.setUniform("useNormalMap", true);
			} else {
				objectShader.setUniform("useNormalMap", true);
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
	
	public void renderShadow(Mesh shadowMesh, Camera camera) {
		glBindVertexArray(shadowMesh.getVAO());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(2);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, shadowMesh.getIBO());
		if (shadowMesh.getMaterial() != null) {
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, shadowMesh.getMaterial().getTextureID());
		}
		shadowShader.bind();
		shadowShader.setUniform("model", Matrix4f.identity());
		shadowShader.setUniform("view", Matrix4f.view(camera.getPosition(), camera.getRotation()));
		shadowShader.setUniform("projection", window.getProjectionMatrix());
		glDrawElements(GL_TRIANGLES, shadowMesh.getIndices().length, GL_UNSIGNED_INT, 0);
		shadowShader.unbind();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void renderTrail(Mesh trailMesh, Camera camera) {
		glBindVertexArray(trailMesh.getVAO());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(5);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, trailMesh.getIBO());
		if (trailMesh.getMaterial() != null) {
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, trailMesh.getMaterial().getTextureID());
		}
		trailShader.bind();
		trailShader.setUniform("model", Matrix4f.identity());
		trailShader.setUniform("view", Matrix4f.view(camera.getPosition(), camera.getRotation()));
		trailShader.setUniform("projection", window.getProjectionMatrix());
		glDrawElements(GL_TRIANGLES, trailMesh.getIndices().length, GL_UNSIGNED_INT, 0);
		trailShader.unbind();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(5);
		glDisable(GL_BLEND);
		glBindVertexArray(0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void renderResources(Mesh m, Camera camera) {
		glBindVertexArray(m.getVAO());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glEnableVertexAttribArray(4);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m.getIBO());
		if (m.getMaterial() != null) {
			// Bind texture
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, m.getMaterial().getTextureID());
			// Bind normalMap
			if (m.getMaterial().hasNormalMap()) {
				glActiveTexture(GL_TEXTURE1);
				glBindTexture(GL_TEXTURE_2D, m.getMaterial().getNormalMapID());
			}
		}
		objectShader.bind();
		objectShader.setUniform("model", Matrix4f.translate(new Vector3f(0.5f, 0, -0.5f)));
		objectShader.setUniform("view", Matrix4f.view(camera.getPosition(), camera.getRotation()));
		objectShader.setUniform("projection", window.getProjectionMatrix());
		objectShader.setUniform("useSkeleton", false);
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
	
	private void createUniforms() throws Exception {
		objectShader.createUniform("model");
		objectShader.createUniform("view");
		objectShader.createUniform("projection");
		objectShader.createUniform("lightPos");
		objectShader.createUniform("viewPos");
		objectShader.createUniform("useNormalMap");
		
		terrainShader.createUniform("model");
		terrainShader.createUniform("view");
		terrainShader.createUniform("projection");

		shadowShader.createUniform("model");
		shadowShader.createUniform("view");
		shadowShader.createUniform("projection");
		
		trailShader.createUniform("model");
		trailShader.createUniform("view");
		trailShader.createUniform("projection");
	}
}
