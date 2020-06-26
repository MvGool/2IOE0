package engine.graphics;

import engine.model_loaders.Bone;
import org.joml.Matrix4f;

import java.util.Map;

/**
 * An animations frame contains a mapping of which transformations to apply to
 * which bone
 * Not finished due to troubles with animation system
 */
public class AnimationFrame
{
	private Map<String, Matrix4f> transforms;

	public AnimationFrame(Map<String, Matrix4f> transforms) {
		this.transforms = transforms;
	}
}
