package main;

import engine.graphics.*;
import engine.io.Input;
import engine.io.Window;
import engine.maths.Vector2f;
import engine.maths.Vector3f;
import engine.model_loaders.AnimModelLoader;
import engine.model_loaders.StaticModelLoader;
import engine.objects.AnimGameObject;
import engine.objects.Camera;
import engine.objects.GameObject;
import org.lwjgl.glfw.GLFW;

public class Main implements Runnable {
	public String CAMERA_MODE = "topdown"; // Options: firstperson, topdown
//	public String CAMERA_MODE = "firstperson"; // Options: firstperson, topdown
	
	public Thread game;
	public Window window;
	public Renderer renderer;
	public Shader shader;
	public World world;
	public final int WIDTH = 1280, HEIGHT = 760;

	public Camera camera = new Camera(new Vector3f(0, 1, 0), new Vector3f(0, 0, 0));
	
	public void start() {
		game = new Thread(this, "game");
		game.start();
	}
	
	public void init() {

		window = new Window(WIDTH, HEIGHT, "Game");
		shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
		renderer = new Renderer(window, shader);
		world = new World(renderer, camera);
		window.setBackgroundColor(0.56f, 0.92f, 0.75f);
		window.create();

		shader.create();
		world.load();
		world.create();
	}
	
	public void run() {
		init();
		while (!window.shouldClose() && !Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
			update();
			render(); 
			if (Input.isKeyDown(GLFW.GLFW_KEY_F11)) window.setFullscreen(!window.isFullscreen());
			if (CAMERA_MODE == "firstperson") {
				window.mouseState(true);
			}
		}
		close();
	}
	
	private void update() {
		window.update();
		world.update(renderer, camera);
		camera.update(CAMERA_MODE);
	}

	
	private void render() {
		world.render();
		window.swapBuffers();
	}
	
	private void close() {
		window.destroy();
		world.destroy();
		shader.destroy();
	}
	
	public static void main(String[] args) {
		new Main().start();
	}
}
