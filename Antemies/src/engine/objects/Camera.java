package engine.objects;

import org.lwjgl.glfw.GLFW;
import org.lwjglx.input.Mouse;

import engine.io.Input;
import engine.maths.Vector3f;

public class Camera {
	private Vector3f position, rotation;
	private float moveSpeed = 0.05f, mouseSensitivity = 0.15f, scrollSpeed = 50, distance = 2.0f, viewAngle = 0, 
			horizontalAngle = 0, verticalAngle = 0, zoomLowerBound = 1, zoomUpperBound = 20;
	private double oldMouseX, oldMouseY, oldScroll, currentZoom = 10, newMouseX, newMouseY, newScroll, newZoom = 10;

	public Camera(Vector3f position, Vector3f rotation) {
		this.position = position;
		this.rotation = rotation;
	}
	
	public void update(String type) {
		switch(type) {
		case "firstperson": firstPerson();
		break;
		case "topdown": topDown();
		break;
		}
	}
	
	private void firstPerson() {
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
	
	private void topDown() {	
		newScroll = Input.getScrollY();
		
		float dScroll = (float) (newScroll - oldScroll);
		
		oldScroll = newScroll;
				
		newZoom -= dScroll;
		newZoom = Math.max(zoomLowerBound, Math.min(newZoom, zoomUpperBound));

		float dZoom = (float) (newZoom - currentZoom);
		
		currentZoom += dZoom/scrollSpeed;
		
		if (position.getY() + dZoom/scrollSpeed >= zoomLowerBound && position.getY() + dZoom/scrollSpeed <= zoomUpperBound) {
			position = Vector3f.add(position, new Vector3f(0, dZoom/scrollSpeed, 0));		
		}
		
		if (Input.isKeyDown(GLFW.GLFW_KEY_A)) position = Vector3f.add(position, new Vector3f(-moveSpeed, 0, 0));
		if (Input.isKeyDown(GLFW.GLFW_KEY_D)) position = Vector3f.add(position, new Vector3f(moveSpeed, 0, 0));
		if (Input.isKeyDown(GLFW.GLFW_KEY_W)) position = Vector3f.add(position, new Vector3f(0, 0, -moveSpeed));
		if (Input.isKeyDown(GLFW.GLFW_KEY_S)) position = Vector3f.add(position, new Vector3f(0, 0, moveSpeed));
		
		rotation.set(viewAngle-90, 0, 0);
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}
}
