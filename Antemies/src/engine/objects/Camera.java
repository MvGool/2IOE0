package engine.objects;

import org.lwjgl.glfw.GLFW;

import engine.io.Input;
import engine.maths.Vector3f;

public class Camera {
	private Vector3f position, rotation;
	private float moveSpeed = 0.05f, mouseSensitivity = 0.15f, distance = 2.0f, angle = 0, horizontalAngle = 0, verticalAngle = 0;
	private double oldMouseX, oldMouseY = 0, newMouseX, newMouseY;

	public Camera(Vector3f position, Vector3f rotation) {
		this.position = position;
		this.rotation = rotation;
	}
	
	public void update() {
		newMouseX = Input.getMouseX();
		newMouseY = Input.getMouseY();
		
		float rotCos = (float) Math.cos(Math.toRadians(rotation.getY())) * moveSpeed;
		float rotSin = (float) Math.sin(Math.toRadians(rotation.getY())) * moveSpeed;
		
		if (Input.isKeyDown(GLFW.GLFW_KEY_A)) position = Vector3f.add(position, new Vector3f(-rotCos, 0, rotSin));
		if (Input.isKeyDown(GLFW.GLFW_KEY_D)) position = Vector3f.add(position, new Vector3f(rotCos, 0, -rotSin));
		if (Input.isKeyDown(GLFW.GLFW_KEY_W)) position = Vector3f.add(position, new Vector3f(-rotSin, 0, -rotCos));
		if (Input.isKeyDown(GLFW.GLFW_KEY_S)) position = Vector3f.add(position, new Vector3f(rotSin, 0, rotCos));
		if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) position = Vector3f.add(position, new Vector3f(0, moveSpeed, 0));
		if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) position = Vector3f.add(position, new Vector3f(0, -moveSpeed, 0));
		
		float dx = (float) (newMouseX - oldMouseX);
		float dy = (float) (newMouseY - oldMouseY);
		
		oldMouseX = newMouseX;
		oldMouseY = newMouseY;
		
		rotation = Vector3f.add(rotation, new Vector3f(-dy * mouseSensitivity, -dx * mouseSensitivity, 0));
	}
	
	public void update(GameObject object) {
		newMouseX = Input.getMouseX();
		newMouseY = Input.getMouseY();
		
		float dx = (float) (newMouseX - oldMouseX);
		float dy = (float) (newMouseY - oldMouseY);
		
		oldMouseX = newMouseX;
		oldMouseY = newMouseY;

		if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
			verticalAngle -= dy * mouseSensitivity;
			horizontalAngle += dx * mouseSensitivity;
		}
		
		if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_MIDDLE)) {
			if (distance > 0) {
				distance += dy * mouseSensitivity;
			} else {
				distance = 0.1f;
			}
		}
		
		float horizontalDist = (float) (distance * Math.cos(Math.toRadians(verticalAngle)));
		float verticalDist = (float) (distance * Math.sin(Math.toRadians(verticalAngle)));
		
		float xOffset = (float) (horizontalDist * Math.sin(Math.toRadians(-horizontalAngle)));
		float zOffset = (float) (horizontalDist * Math.cos(Math.toRadians(-horizontalAngle)));
		
		position.set(object.getPosition().getX() + xOffset, object.getPosition().getY() - verticalDist, object.getPosition().getZ() + zOffset);
		
		rotation.set(verticalAngle, -horizontalAngle, 0);
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}
}
