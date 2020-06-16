package engine.model_loaders;

public class Weight
{
	private int boneID;
	private int vertexID;
	private float weight;

	public Weight(int boneID, int vertexID, float weight) {
		this.boneID = boneID;
		this.vertexID = vertexID;
		this.weight = weight;
	}

	public float getWeight() {
		return weight;
	}

	public int getBoneID() {
		return boneID;
	}
}
