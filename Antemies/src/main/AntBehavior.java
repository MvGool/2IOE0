package main;

import main.objects.AntObject;
import main.objects.NestObject;
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
    //private ArrayList<Tile> foodSources;
    //private ArrayList<Tile> materialSources;
    private AntObject foragerAnt;
    private NestObject nest;
    private Tile base;
    

    public AntBehavior(Grid2D grid, ArrayList<AntObject> ants, AntObject foragerAnt, Tile base, NestObject nest) {
    	this.grid = grid;
    	this.ants = ants;
    	this.foragerAnt = foragerAnt;
    	this.nest = nest;
    	this.base = nest.getTile();
    }
    
    public enum LeaveRequestState {
        //Idle {
        //},
        followForager {
        },
        goToSource {
        },
        goToBase {
        };
    }

    public void run() {
        /*while (true) {
            for (AntObject ant : ants) {
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
        }*/
    	
        ArrayList<Tile> foundSources = new ArrayList<>();
        while (true) {
        	if (grid.containsResource(foragerAnt.getTile())) {
        		Tile tile = grid.getTile(foragerAnt.getTile().getX(), foragerAnt.getTile().getY());
        		if (!foundSources.contains(tile)) {
        			foundSources.add(tile);
        		}
        	}
        	
            for (AntObject ant : ants){
                if (ant.getState() == LeaveRequestState.followForager && !ant.isMoving()) {
                	if (!foundSources.isEmpty()) {
                		ant.moveTo(grid, foundSources.get(0));
                        ant.setState(LeaveRequestState.goToSource);
                	} else {
                		ant.moveTo(grid, foragerAnt.getTile());
                	}
                } else if (ant.getState() == LeaveRequestState.goToSource && !ant.isMoving()) {
                	if (grid.containsResource(ant.getTile())) {
                		Tile source = grid.getTile(ant.getTile().getX(), ant.getTile().getY());
                		
                		int foodOverload = ant.addFood(grid.getTile(ant.getTile().getX(), ant.getTile().getY()).getFood());
	                    int materialOverload = ant.addMaterial(grid.getTile(ant.getTile().getX(), ant.getTile().getY()).getMaterial());
	                    
	                    if (foodOverload != 0) {
	                    	
	                    } else if (materialOverload != 0) {
	                    	
	                    }
	                    
	                    ant.moveTo(grid, base);
                	}
                	ant.setState(LeaveRequestState.goToBase);
                } else if (ant.getState() == LeaveRequestState.goToBase && !ant.isMoving()) {
                	nest.depositFood(ant.getFood());
                	ant.setFood(0);
                	
                	nest.depositMaterial(ant.getMaterial());
                	ant.setMaterial(0);
                	
                	
                    ant.setState(LeaveRequestState.followForager);
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
