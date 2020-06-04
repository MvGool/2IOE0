package main;

import engine.graphics.*;
import engine.io.Input;
import engine.io.Window;
import engine.maths.Vector3f;
import engine.objects.Camera;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements Runnable {
	public String cameraMode = "topdown"; // Options: firstperson, topdown
	public Thread game;
	public Window window;
	public Renderer renderer;
	public World world;
	public boolean hold;
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
		if (Input.isKeyDown(GLFW_KEY_F1) && !hold) {
			if (cameraMode == "topdown") {
				cameraMode = "firstperson";
			} else {
				cameraMode = "topdown";
			}
			hold = true;
		}
		
		if (!Input.isKeyDown(GLFW_KEY_F1)) hold = false;
		
		if (cameraMode == "firstperson") {
			window.mouseState(true);
		} else {
			window.mouseState(false);
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
	
	public static void main(String[] args) {
		new Main().start();
	}
}
