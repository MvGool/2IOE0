package engine.graphics;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import engine.maths.Vector3f;
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
	private int vao, pbo, ibo, nbo, tbo, transbo;

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

		FloatBuffer normalBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] normalData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			normalData[i * 3] = vertices[i].getNormal().getX();
			normalData[i * 3 + 1] = vertices[i].getNormal().getY();
			normalData[i * 3 + 2] = vertices[i].getNormal().getZ();
		}
		normalBuffer.put(normalData).flip();

		nbo = storeData(normalBuffer, 1, 3);

		if (initTextureBuffer) {
			FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
			float[] textureData = new float[vertices.length * 2];
			for (int i = 0; i < vertices.length; i++) {
				textureData[i * 2] = vertices[i].getTextureCoord().getX();
				textureData[i * 2 + 1] = vertices[i].getTextureCoord().getY();
			}
			((Buffer)textureBuffer.put(textureData)).flip();

			tbo = storeData(textureBuffer, 2, 2);
		} else {
			FloatBuffer colorBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
			float[] colorData = new float[vertices.length * 3];
			for (int i = 0; i < vertices.length; i++) {
				colorData[i * 3] = vertices[i].getColor().getX();
				colorData[i * 3 + 1] = vertices[i].getColor().getY();
				colorData[i * 3 + 2] = vertices[i].getColor().getZ();
			}
			colorBuffer.put(colorData).flip();
			
			cbo = storeData(colorBuffer, 1, 3);
			
			FloatBuffer transparencyBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
			float[] transparencyData = new float[vertices.length * 3];
			for (int i = 0; i < vertices.length; i++) {
				transparencyData[i * 3] = vertices[i].getTransparency();
				transparencyData[i * 3 + 1] = vertices[i].getTransparency();
				transparencyData[i * 3 + 2] = vertices[i].getTransparency();
			}
			transparencyBuffer.put(transparencyData).flip();
			
			transbo = storeData(transparencyBuffer, 5, 3);
		}
			
		// If we have a bonemesh we also bind the weights per vertex
		if (this instanceof  BoneMesh) {
			FloatBuffer weightBuffer = MemoryUtil.memAllocFloat(vertices.length * 4);
			float[] weightData = new float[vertices.length * 4];
			System.out.println(vertices.length);
			BoneMesh m = (BoneMesh)this;
			System.out.println(m.getWeightsKeys().toString());
			for (Integer i : m.getWeightsKeys()) {
				System.out.println(i);
				List<Weight> weights = m.getWeightsForVertex(i);
				for (int j = 0; j < weights.size() && j < 4; j++) {
					weightData[i * 4 + j] = weights.get(j).getWeight();
				}
				/*if (weights.size() < 4) {
					for (int j = weights.size(); j < 4; j++) {
						weightData[i * 4 + j] = 0;
					}
				}*/
			}
			System.out.println("weight list: " + m.getWeightsKeys().size() + " weight data: " + weightData.length);
			weightBuffer.put(weightData).flip();
			storeData(weightBuffer, 3, 4);
		}

		// if we have a bonemesh we also bind the bones corresponding to the weights
		if (this instanceof BoneMesh) {
			IntBuffer indexBuffer = MemoryUtil.memAllocInt(vertices.length * 4);
			int[] indexData = new int[vertices.length * 4];
			BoneMesh m = (BoneMesh)this;
			for (Integer i : m.getWeightsKeys()) {
				List<Weight> weights = m.getWeightsForVertex(i);
				for (int j = 0; j < weights.size() && j < 4; j++) {
					indexData[i * 4 + j] = weights.get(j).getBoneID();
				}
				if (weights.size() < 4) {
					for (int j = weights.size(); j < 4; j++) {
						indexData[i * 4 + j] = 0;
					}
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

	private int storeData(IntBuffer buffer, int index, int size) {
		int bufferID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, bufferID);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		return bufferID;
	}

	public void move(Vector3f position) {
		for (Vertex vertex : vertices) {
			Vector3f cur = vertex.getPostion();
			vertex.setPosition(Vector3f.add(cur, position));
		}
	}
	
	public void rotateScale(float scale) {
		for (Vertex vertex : vertices) {
			Vector3f cur = vertex.getPostion();
			Vector3f out = Vector3f.multiply(new Vector3f(cur.getX(), cur.getZ(), -cur.getY()), scale);
			vertex.setPosition(out);
		}
	}
	
	public static Mesh merge(ArrayList<Mesh> meshes) {
		ArrayList<Vertex> vertices = new ArrayList<>();
		ArrayList<Integer> indices = new ArrayList<>();
		
		for (Mesh mesh : meshes) {
			for (int index : mesh.getIndices()) {
				indices.add(index + vertices.size());
			}
			vertices.addAll(Arrays.asList(mesh.getVertices()));
		}
		
		Vertex[] verticesOut = vertices.toArray(new Vertex[vertices.size()]);
		int[] indicesOut = new int[indices.size()];
		for (int i = 0; i < indices.size(); i++) {
			indicesOut[i] = indices.get(i);
		}
		
		return new Mesh(verticesOut, indicesOut);
	}
	
	public void reset(Vertex[] vertices, int[] indices, boolean initTextureBuffer) {
		this.vertices = vertices;
		this.indices = indices;
		destroy();
		create(initTextureBuffer);
	}

	public void destroy() {
		glDeleteBuffers(pbo);
		glDeleteBuffers(nbo);
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

	public int getNBO() {
		return nbo;
	}

	public int getTBO() {
		return tbo;
	}
	
	public int getTRANSBO() {
		return transbo;
	}
}
