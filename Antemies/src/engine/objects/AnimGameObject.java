package engine.objects;

import engine.graphics.Animation;
import engine.graphics.BoneMesh;
import engine.graphics.Mesh;
import engine.maths.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class AnimGameObject extends GameObject
{
	private Map<String, Animation> animations = new HashMap<>();

	public AnimGameObject(Vector3f position, Vector3f rotation, Vector3f scalar, Mesh[] meshes) {
		super(position, rotation, scalar, meshes);
	}

	public BoneMesh[] getMeshes() {
		return (BoneMesh[])super.getMeshes();
	}

	public void playAnimation(String name) {

	}

	public void stopAnimation(String name) {

	}

	public void addAnimation(String name, Animation animation) {
		animations.put(name, animation);
	}
}
