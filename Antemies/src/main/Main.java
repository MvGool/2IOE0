package main;

import engine.graphics.*;
import engine.io.Input;
import engine.io.Window;
import engine.maths.*;
import engine.objects.Camera;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements Runnable {
	public String cameraMode = "topdown"; // Options: firstperson, topdown
	public Thread game;
	public Window window;
	public Renderer renderer;
	public World world;
	public boolean holdF1;
	public boolean holdClick;
	public final int WIDTH = 1280, HEIGHT = 760;

	public Camera camera = new Camera(new Vector3f(0, 1, 0), new Vector3f(0, 0, 0));
	
	public void start() {
		game = new Thread(this, "game");
		game.start();
	}
	
	public void init() throws Exception {
		window = new Window(WIDTH, HEIGHT, "Game");
		renderer = new Renderer(window);
		world = new World(renderer, camera);
		window.setBackgroundColor(0.56f, 0.92f, 0.75f);
		window.create();
		renderer.init();
		world.load();
		world.create();
	}
	
	public void run() {
		try {
			init();
			gameLoop();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cleanup();
		}
	}
	
	private void gameLoop() {
		while (!window.shouldClose() && !Input.isKeyDown(GLFW_KEY_ESCAPE)) {
			handleInputs();
			update();
			render(); 
		}
	}
	
	private void handleInputs() {
		if (Input.isKeyDown(GLFW_KEY_F11)) window.setFullscreen(!window.isFullscreen());
		if (Input.isKeyDown(GLFW_KEY_F1) && !holdF1) {
			holdF1 = true;
		}
		
		if (!Input.isKeyDown(GLFW_KEY_F1) && holdF1 == true) {
			if (cameraMode == "topdown") {
				cameraMode = "firstperson";
			} else {
				cameraMode = "topdown";
			}
			
			holdF1 = false;
		}
		
		if (cameraMode == "firstperson") {
			window.mouseState(true);
		} else {
			window.mouseState(false);
		}
		
		if (!Input.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) holdClick = false;
		
		if (cameraMode == "topdown" && Input.isButtonDown(GLFW_MOUSE_BUTTON_LEFT) && !holdClick) {
			double inputX = Input.getClickX();
			double inputY = Input.getClickY();
			Vector3f position = screenToWorldSpace(inputX, inputY);
			if (position != null) {
				world.moveUser(position);
				holdClick = true;
			}
		}
	}
	
	private void update() {
		window.update();
		world.update(renderer, camera);
		camera.update(cameraMode);
	}
	
	private void render() {
		world.render();
		window.swapBuffers();
	}
	
	private void cleanup() {
		window.destroy();
		world.destroy();
	}
	
	public Vector3f screenToWorldSpace(double x, double y) {		
	    Matrix4f viewProjection = Matrix4f.multiply(window.getProjectionMatrix(), Matrix4f.view(camera.getPosition(), camera.getRotation()));
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
	
	public static void main(String[] args) {
		new Main().start();
	}
}
