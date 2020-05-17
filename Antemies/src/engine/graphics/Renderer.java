package engine.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import engine.io.Window;
import engine.maths.Matrix4f;
import engine.objects.Camera;
import engine.objects.GameObject;

public class Renderer {
	private Window window;
	private Shader shader;

	public Renderer(Window window, Shader shader) {
		this.window = window;
		this.shader = shader;
	}

	public void renderMesh(GameObject object, Camera camera) {
		for (Mesh m : object.getMeshes()) {
			GL30.glBindVertexArray(m.getVAO());
			GL30.glEnableVertexAttribArray(0);
			GL30.glEnableVertexAttribArray(1);
			GL30.glEnableVertexAttribArray(2);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m.getIBO());
			if (m.getMaterial() != null) {
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL13.glBindTexture(GL11.GL_TEXTURE_2D, m.getMaterial().getTextureID());
			}
			shader.bind();
			shader.setUniform("model", Matrix4f.transform(object.getPosition(), object.getRotation(), object.getScalar()));
			shader.setUniform("view", Matrix4f.view(camera.getPosition(), camera.getRotation()));
			shader.setUniform("projection", window.getProjectionMatrix());
			GL11.glDrawElements(GL11.GL_TRIANGLES, m.getIndices().length, GL11.GL_UNSIGNED_INT, 0);
			shader.unbind();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL30.glDisableVertexAttribArray(0);
			GL30.glDisableVertexAttribArray(1);
			GL30.glDisableVertexAttribArray(2);
			GL30.glBindVertexArray(0);
		}
	}
}