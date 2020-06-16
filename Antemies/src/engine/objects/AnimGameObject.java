package engine.objects;

import engine.graphics.BoneMesh;
import engine.graphics.Mesh;
import engine.maths.Vector3f;

public class AnimGameObject extends GameObject
{
	public AnimGameObject(Vector3f position, Vector3f rotation, Vector3f scalar, Mesh[] meshes) {
		super(position, rotation, scalar, meshes);
	}

	public BoneMesh[] getMeshes() {
		return (BoneMesh[])super.getMeshes();
	}
}
