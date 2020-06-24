package engine.graphics;

import engine.model_loaders.Bone;
import org.joml.Matrix4f;

import java.util.Map;

public class AnimationFrame
{
	private Map<String, Matrix4f> transforms;

	public AnimationFrame(Map<String, Matrix4f> transforms) {
		this.transforms = transforms;
	}
}
