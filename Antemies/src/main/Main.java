package main;

import engine.graphics.*;
import engine.io.Input;
import engine.io.Window;
import engine.maths.Vector2f;
import engine.maths.Vector3f;
import engine.model_loaders.StaticModelLoader;
import engine.objects.Camera;
import engine.objects.GameObject;
import org.lwjgl.glfw.GLFW;

public class Main implements Runnable {
	public Thread game;
	public Window window;
	public Renderer renderer;
	public Shader shader;
	public final int WIDTH = 1280, HEIGHT = 760;
	
	public Mesh mesh = new Mesh(new Vertex[] {
			new Vertex(new Vector3f(-0.5f,  0.5f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f), new Vector2f(0.0f, 0.0f)),
			new Vertex(new Vector3f(-0.5f, -0.5f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector2f(0.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f, -0.5f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f), new Vector2f(1.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f,  0.5f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector2f(1.0f, 0.0f))
	}, new int[] {
			0, 1, 2,
			0, 3, 2

	}, new Material("/textures/forest_ground_1k/forrest_ground_01_diff_1k.jpg"));
	
	public GameObject object = new GameObject(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), mesh);
	public GameObject monkeyModel;
	Mesh[] monkey;
	
	public Camera camera = new Camera(new Vector3f(0, 0, 1), new Vector3f(0, 0, 0));
	
	public void start() {
		game = new Thread(this, "game");
		game.start();
	}
	
	public void init() {
		monkey = null;
		try {
			monkey = StaticModelLoader.load("resources/models/monkey.obj", "/textures/forest_ground_1k/forrest_ground_01_diff_1k.jpg");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		monkeyModel = new GameObject(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), monkey);

		window = new Window(WIDTH, HEIGHT, "Game");
		shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
		renderer = new Renderer(window, shader);
		window.setBackgroundColor(1.0f, 0.0f, 0.0f);
		window.create();
		object.create(true);
		monkeyModel.create(true);
		shader.create();
	}
	
	public void run() {
		init();
		while (!window.shouldClose() && !Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
			update();
			render();
			if (Input.isKeyDown(GLFW.GLFW_KEY_F11)) window.setFullscreen(!window.isFullscreen());
		}
		close();
	}
	
	private void update() {
		window.update();
		//monkeyModel.update();
		if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) System.out.println("X: " + Input.getMouseX() + ", Y: " + Input.getMouseY());
	}
	
	private void render() {
		//renderer.renderMesh(object, camera);
		renderer.renderMesh(monkeyModel,camera);
		window.swapBuffers();
	}
	
	private void close() {
		window.destroy();
		mesh.destroy();
		shader.destroy();
	}
	
	public static void main(String[] args) {
		new Main().start();
	}
}
