package engine.graphics;

import engine.model_loaders.AnimModelLoader;
import engine.objects.AnimGameObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

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
	}

	public void renderMesh(GameObject object, Camera camera) {
		for (Mesh m : object.getMeshes()) {
			GL30.glBindVertexArray(m.getVAO());
			GL30.glEnableVertexAttribArray(0);
			GL30.glEnableVertexAttribArray(1);
			GL30.glEnableVertexAttribArray(2);
			GL30.glEnableVertexAttribArray(3);
			GL30.glEnableVertexAttribArray(4);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m.getIBO());
			if (m.getMaterial() != null) {
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL13.glBindTexture(GL11.GL_TEXTURE_2D, m.getMaterial().getTextureID());
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
			GL11.glDrawElements(GL11.GL_TRIANGLES, m.getIndices().length, GL11.GL_UNSIGNED_INT, 0);
			objectShader.unbind();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL30.glDisableVertexAttribArray(0);
			GL30.glDisableVertexAttribArray(1);
			GL30.glDisableVertexAttribArray(2);
			GL30.glDisableVertexAttribArray(3);
			GL30.glDisableVertexAttribArray(4);
			GL30.glBindVertexArray(0);
		}
	}
}
