package main;

import engine.io.Window;

public class Main implements Runnable {
	public Thread game;
	public static Window window;
	public static final int WIDTH = 1280, HEIGHT = 760;
	
	public void start() {
		game = new Thread(this, "game");
		game.start();
	}
	
	public static void init() {
		System.out.println("Initializing game!");
		window = new Window(WIDTH, HEIGHT, "Game");
		window.create();
	}
	
	public void run() {
		init();
		while (!window.shouldClose()) {
			update();
			render();
		}
	}
	
	private void update() {
//		System.out.println("Updating game!");
		window.update();
	}
	
	private void render() {
//		System.out.println("Rendering game!");
		window.swapBuffers();
	}
	
	public static void main(String[] args) {
		new Main().start();
	}
}
