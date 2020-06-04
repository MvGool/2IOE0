package engine.graphics;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import engine.model_loaders.Weight;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Mesh {
	private Vertex[] vertices;
	private int[] indices;
	private Material material;
	private int vao, pbo, ibo, cbo, tbo;

	public Mesh(Vertex[] vertices, int[] indices, Material material) {
		this.vertices = vertices;
		this.indices = indices;
		this.material = material;
	}

	public Mesh(Vertex[] vertices, int[] indices) {
		this(vertices, indices, null);
	}

	public void create(boolean initTextureBuffer) {
		if (material != null) {
			material.create();
		}

		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] positionData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			positionData[i * 3] = vertices[i].getPostion().getX();
			positionData[i * 3 + 1] = vertices[i].getPostion().getY();
			positionData[i * 3 + 2] = vertices[i].getPostion().getZ();
		}
		((Buffer)positionBuffer.put(positionData)).flip();

		pbo = storeData(positionBuffer, 0, 3);

//		FloatBuffer colorBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
//		float[] colorData = new float[vertices.length * 3];
//		for (int i = 0; i < vertices.length; i++) {
//			colorData[i * 3] = vertices[i].getColor().getX();
//			colorData[i * 3 + 1] = vertices[i].getColor().getY();
//			colorData[i * 3 + 2] = vertices[i].getColor().getZ();
//		}
//		colorBuffer.put(colorData).flip();
//
//		cbo = storeData(colorBuffer, 1, 3);

		if (initTextureBuffer) {
			FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
			float[] textureData = new float[vertices.length * 2];
			for (int i = 0; i < vertices.length; i++) {
				textureData[i * 2] = vertices[i].getTextureCoord().getX();
				textureData[i * 2 + 1] = vertices[i].getTextureCoord().getY();
			}
			((Buffer)textureBuffer.put(textureData)).flip();

			tbo = storeData(textureBuffer, 2, 2);
		}

		// If we have a bonemesh we also bind the weights per vertex
		if (this instanceof  BoneMesh) {
			FloatBuffer weightBuffer = MemoryUtil.memAllocFloat(vertices.length * 4);
			float[] weightData = new float[vertices.length * 4];
			BoneMesh m = (BoneMesh)this;
			for (Integer i : m.getWeightsKeys()) {
				List<Weight> weights = m.getWeightsForVertex(i);
				for (int j = 0; j < weights.size() && j < 4; j++) {
					weightData[i * 4 + j] = weights.get(j).getWeight();
				}
				if (weights.size() < 4) {
					for (int j = weights.size(); j < 4; j++) {
						weightData[i * 4 + j] = 0;
					}
				}
			}
			weightBuffer.put(weightData).flip();
			storeData(weightBuffer, 3, 4);
		}

		// if we have a bonemesh we also bind the bones corresponding to the weights
		if (this instanceof BoneMesh) {
			FloatBuffer indexBuffer = MemoryUtil.memAllocFloat(vertices.length * 4);
			float[] indexData = new float[vertices.length * 4];
			BoneMesh m = (BoneMesh)this;
			for (Integer i : m.getWeightsKeys()) {
				List<Weight> weights = m.getWeightsForVertex(i);
				for (int j = 0; j < weights.size() && j < 4; j++) {
					indexData[i * 4 + j] = weights.get(j).getBoneID();
				}
			}
			indexBuffer.put(indexData).flip();
			storeData(indexBuffer, 4, 4);
		}

		IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
		((Buffer)indicesBuffer.put(indices)).flip();

		ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	private int storeData(FloatBuffer buffer, int index, int size) {
		int bufferID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, bufferID);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		return bufferID;
	}
	
	public void reset(Vertex[] vertices, int[] indices, boolean initTextureBuffer) {
		this.vertices = vertices;
		this.indices = indices;
		create(initTextureBuffer);
	}

	public void destroy() {
		glDeleteBuffers(pbo);
		glDeleteBuffers(cbo);
		glDeleteBuffers(ibo);
		glDeleteBuffers(tbo);

		glDeleteVertexArrays(vao);

		if (material != null) {
			material.destroy();
		}
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Vertex[] getVertices() {
		return vertices;
	}

	public int[] getIndices() {
		return indices;
	}

	public int getVAO() {
		return vao;
	}

	public int getPBO() {
		return pbo;
	}

	public int getIBO() {
		return ibo;
	}

	public int getCBO() {
		return cbo;
	}

	public int getTBO() {
		return tbo;
	}
}
