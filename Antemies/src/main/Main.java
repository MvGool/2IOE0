package main;

import engine.graphics.*;
import engine.io.Input;
import engine.io.Window;
import engine.maths.*;
import engine.objects.Camera;

import static org.lwjgl.glfw.GLFW.*;

import java.io.File;

import javax.sound.sampled.*;

public class Main implements Runnable {
	public UserInterface UI = new UserInterface();
	public String cameraMode = "topdown"; // Options: firstperson, topdown
	public Thread game;
	public Window window;
	public Renderer renderer;
	public World world;
	public boolean holdF1;
	public boolean holdClick;
	public boolean holdPause;
	public final int WIDTH = UI.width, HEIGHT = UI.height;
	
	public static Clip clip;
	public static boolean playSound;
	
	public Camera camera = new Camera(new Vector3f(0, 1, 0), new Vector3f(0, 0, 0));
	
	public void start() {
		game = new Thread(this, "Antemies");
		game.start();
	}
	
	public void init() throws Exception {
		UI.run();
		if (UI.close) {
			return;
		}
		window = new Window(WIDTH, HEIGHT, "Antemies");
		renderer = new Renderer(window);
		world = new World(renderer, camera);
		window.setBackgroundColor(0.56f, 0.92f, 0.75f);
		window.create();
		renderer.init();
		world.load();
		world.create();
		playSound("forest_background");
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
		if (!UI.close) {
			while (!window.shouldClose() && !UI.close) {
				handleInputs();
				update();
				render(); 
			}
		}
		
		cleanup();
	}
	
	private void handleInputs() {
		if (Input.isKeyDown(GLFW_KEY_F11)) window.setFullscreen(!window.isFullscreen());
		if (Input.isKeyDown(GLFW_KEY_F1) && !holdF1) {
			holdF1 = true;
		}
		
		if (!Input.isKeyDown(GLFW_KEY_F1) && holdF1) {
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
		
		if (!Input.isKeyDown(GLFW_KEY_P) && !Input.isKeyDown(GLFW_KEY_ESCAPE)) {
			holdPause = false;
		}
		
		if ((Input.isKeyDown(GLFW_KEY_P) || Input.isKeyDown(GLFW_KEY_ESCAPE)) && !holdPause) {
			window.hide();
			UI.run();
			window.show();
			holdPause = true;
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
		if (clip != null) {
			clip.stop();
			clip.close();
		}
		
		UI.destroy();
		
		if (window != null) {
			window.destroy();
		}
		
		if (world != null) {
			world.destroy();
		}
	}
	
	public Vector3f screenToWorldSpace(double x, double y) {
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
	
	// Plays a .wav audio file
	public void playSound(String audio) {
		try {
		    if (clip != null && clip.isOpen()) {
		    	clip.close();
		    }
		    
		    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("resources/audio/" + audio + ".wav").getAbsoluteFile());
	        clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	        control.setValue(20f * (float) Math.log10(0.5f));
	        
	        if (playSound) {
	        	clip.loop(Clip.LOOP_CONTINUOUSLY);
	        }
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		new Main().start();
	}
}
