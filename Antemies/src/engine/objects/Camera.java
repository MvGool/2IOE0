package engine.objects;

import org.lwjgl.glfw.GLFW;

import engine.io.Input;
import engine.io.Window;
import engine.maths.*;
import main.Main;
import main.World;

public class Camera {
	private Vector3f position, rotation;
	private float moveSpeed = 0.2f, mouseSensitivity = 0.15f, scrollSpeed = 50, distance = 2.0f, viewAngle = 0, 
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
		
		float rotCos = (float) Math.cos(Math.toRadians(-rotation.getY())) * moveSpeed;
		float rotSin = (float) Math.sin(Math.toRadians(-rotation.getY())) * moveSpeed;
		
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
		
		rotation = Vector3f.add(rotation, new Vector3f(dy * mouseSensitivity, dx * mouseSensitivity, 0));
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
		
		Vector3f topLeftCorner = screenToWorldSpace(0, 0, Main.window, this);
		Vector3f bottomRightCorner = screenToWorldSpace(Main.window.getWidth(), Main.window.getHeight(), Main.window, this);
		int gridSize = World.getGridSize();
		int xLowerBound = - gridSize/2;
		int xUpperBound = gridSize/2;
		int zLowerBound = gridSize/2 - 1;
		int zUpperBound = - gridSize/2 - 1;
		
		if (topLeftCorner.getX() < xLowerBound) {
			position.setX(position.getX() + 0.1f);
		} else if (bottomRightCorner.getX() > xUpperBound) {
			position.setX(position.getX() - 0.1f);
		}
		if (topLeftCorner.getZ() < zUpperBound) {
			position.setZ(position.getZ() + 0.1f);
		} else if (bottomRightCorner.getZ() > zLowerBound) {
			position.setZ(position.getZ() - 0.1f);
		}
		
		if (Input.isKeyDown(GLFW.GLFW_KEY_A) && topLeftCorner.getX() >= xLowerBound) {
			position = Vector3f.add(position, new Vector3f(-moveSpeed, 0, 0));
		}
		if (Input.isKeyDown(GLFW.GLFW_KEY_D) && bottomRightCorner.getX() <= xUpperBound) {
			position = Vector3f.add(position, new Vector3f(moveSpeed, 0, 0));
		}
		if (Input.isKeyDown(GLFW.GLFW_KEY_W) && topLeftCorner.getZ() >= zUpperBound) {
			position = Vector3f.add(position, new Vector3f(0, 0, -moveSpeed));
		}
		if (Input.isKeyDown(GLFW.GLFW_KEY_S) && bottomRightCorner.getZ() <= zLowerBound) {
			position = Vector3f.add(position, new Vector3f(0, 0, moveSpeed));
		}
		
		rotation.set(viewAngle + 90, 0, 0);
	}
	
	public static Vector3f screenToWorldSpace(double x, double y, Window window, Camera camera) {
		Matrix4f viewProjection = Matrix4f.multiply(window.getProjectionMatrix(), Matrix4f.view(new Vector3f(0, camera.getPosition().getY(), 0), camera.getRotation()));
	    MatrixXf viewProjectionInverse = MatrixXf.inverse(Matrix4f.toMatrixXf(viewProjection));
	    
	    float newX = (float) (2.0 * x / window.getWidth() - 1);
	    float newZ = (float) (- 2.0 * y / window.getHeight() + 1);
	    Vector3f vec3f = new Vector3f(newX, 1, newZ);
	    VectorXf vec4f = new VectorXf(4);
	    vec4f.set(0, vec3f.getX());
	    vec4f.set(1, vec3f.getY());
	    vec4f.set(2, vec3f.getZ());
	    vec4f.set(3, 1);
	    
	    VectorXf mul = MatrixXf.multiply(viewProjectionInverse, vec4f);
	    Vector3f result;
	    if (viewProjectionInverse.equals(MatrixXf.zero(4))) {
	    	result = null;
	    } else {
	    	result = new Vector3f(camera.getPosition().getX() + camera.getPosition().getY() * mul.get(0), 1, camera.getPosition().getZ() + camera.getPosition().getY() * mul.get(1));
	    }
	    
	    return result;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}
}
