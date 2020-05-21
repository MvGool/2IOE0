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
			//Back face
			new Vertex(new Vector3f(-0.5f,  0.5f, -0.5f), new Vector2f(0.0f, 0.0f)),
			new Vertex(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector2f(0.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f, -0.5f, -0.5f), new Vector2f(1.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f,  0.5f, -0.5f), new Vector2f(1.0f, 0.0f)),
			
			//Front face
			new Vertex(new Vector3f(-0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 0.0f)),
			new Vertex(new Vector3f(-0.5f, -0.5f,  0.5f), new Vector2f(0.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f, -0.5f,  0.5f), new Vector2f(1.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f,  0.5f,  0.5f), new Vector2f(1.0f, 0.0f)),
			
			//Right face
			new Vertex(new Vector3f( 0.5f,  0.5f, -0.5f), new Vector2f(0.0f, 0.0f)),
			new Vertex(new Vector3f( 0.5f, -0.5f, -0.5f), new Vector2f(0.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f, -0.5f,  0.5f), new Vector2f(1.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f,  0.5f,  0.5f), new Vector2f(1.0f, 0.0f)),
			
			//Left face
			new Vertex(new Vector3f(-0.5f,  0.5f, -0.5f), new Vector2f(0.0f, 0.0f)),
			new Vertex(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector2f(0.0f, 1.0f)),
			new Vertex(new Vector3f(-0.5f, -0.5f,  0.5f), new Vector2f(1.0f, 1.0f)),
			new Vertex(new Vector3f(-0.5f,  0.5f,  0.5f), new Vector2f(1.0f, 0.0f)),
			
			//Top face
			new Vertex(new Vector3f(-0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 0.0f)),
			new Vertex(new Vector3f(-0.5f,  0.5f, -0.5f), new Vector2f(0.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f,  0.5f, -0.5f), new Vector2f(1.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f,  0.5f,  0.5f), new Vector2f(1.0f, 0.0f)),
			
			//Bottom face
			new Vertex(new Vector3f(-0.5f, -0.5f,  0.5f), new Vector2f(0.0f, 0.0f)),
			new Vertex(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector2f(0.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f, -0.5f, -0.5f), new Vector2f(1.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f, -0.5f,  0.5f), new Vector2f(1.0f, 0.0f)),
	}, new int[] {
			//Back face
			0, 1, 3,	
			3, 1, 2,	
			
			//Front face
			4, 5, 7,
			7, 5, 6,
			
			//Right face
			8, 9, 11,
			11, 9, 10,
			
			//Left face
			12, 13, 15,
			15, 13, 14,
			
			//Top face
			16, 17, 19,
			19, 17, 18,
			
			//Bottom face
			20, 21, 23,
			23, 21, 22
	}, new Material("/textures/forest_ground_1k/forrest_ground_01_diff_1k.jpg"));
	
	public GameObject[] objects = new GameObject[1];
	public GameObject object = new GameObject(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), mesh);
	public GameObject antModel;
	Mesh[] ant;
	
	public Camera camera = new Camera(new Vector3f(0, 0, 1), new Vector3f(0, 0, 0));
	
	public void start() {
		game = new Thread(this, "game");
		game.start();
	}
	
	public void init() {
		ant = null;
		try {
			ant = StaticModelLoader.load("resources/models/ant.obj", "/textures/forest_ground_1k/forrest_ground_01_diff_1k.jpg");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		antModel = new GameObject(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), ant);

		window = new Window(WIDTH, HEIGHT, "Game");
		shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
		renderer = new Renderer(window, shader);
		window.setBackgroundColor(1.0f, 0.0f, 0.0f);
		window.create();
		object.create(true);
		antModel.create(false);
		for (int i = 0; i < objects.length; i++) {
			objects[i] = new GameObject(new Vector3f((float) (Math.random() * 50 - 25), (float) (Math.random() * 50 - 25), (float) (Math.random() * 50 - 25)), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), ant);
		}
		
		shader.create();
	}
	
	public void run() {
		init();
		while (!window.shouldClose() && !Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
			update();
			render();
			if (Input.isKeyDown(GLFW.GLFW_KEY_F11)) window.setFullscreen(!window.isFullscreen());
			if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) window.mouseState(true);
		}
		close();
	}
	
	private void update() {
		window.update();
		camera.update(object);
	}
	
	private void render() {
		for (int i = 0; i < objects.length; i++) {
			renderer.renderMesh(objects[i], camera);
		}
		renderer.renderMesh(object, camera);
//		renderer.renderMesh(antModel, camera);
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
