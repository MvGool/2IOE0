package main;

import main.objects.AntObject;

import engine.graphics.Mesh;
import engine.maths.Vector3f;
import engine.objects.GameObject;
import engine.objects.Grid2D;
import engine.objects.Tile;

import java.lang.reflect.Array;
import java.util.*;

public class AntBehavior implements Runnable {
    private Thread t;

    private Grid2D grid;
    private ArrayList<AntObject> ants;
    private ArrayList<Tile> foodSources;
    private ArrayList<Tile> materialSources;
    private Tile base;

    public AntBehavior(Grid2D grid, ArrayList<AntObject> ants, ArrayList<Tile> foodSources, ArrayList<Tile> materialSources, Tile base) {
    	this.grid = grid;
    	this.ants = ants;
    	this.foodSources = foodSources;
    	this.materialSources = materialSources;
    	this.base = base;
    }
    
    public enum LeaveRequestState {
        Idle {
        },
        goToFoodSource {
        },
        goToBase {
        };
    }

    public void run() {
        while (true) {
            for (AntObject ant : ants){
                if (!foodSources.isEmpty()){
                    if (ant.getState() == LeaveRequestState.Idle && !ant.isMoving()) {
                        ant.moveTo(grid, foodSources.get(0));
                        ant.setState(LeaveRequestState.goToFoodSource);
                    } else if (ant.getState() == LeaveRequestState.goToFoodSource && !ant.isMoving()) {
                        ant.moveTo(grid, base);
                        ant.setState(LeaveRequestState.goToBase);
                    } else if (ant.getState() == LeaveRequestState.goToBase && !ant.isMoving()) {
                        ant.setState(LeaveRequestState.Idle);
                    }
                }
            }
        }
    }
    
    public void start(){
        System.out.println("starting");
        t = new Thread (this);
        if (t == null) {
            t = new Thread (this);
            t.start ();
        }
    }
}

/*class TestThread {

    public static void main(String args[]) throws InterruptedException {
    	new AntBehavior().start();
    }
}*/
