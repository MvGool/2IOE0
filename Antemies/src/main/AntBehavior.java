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
    private AntObject foragerAnt;
    private NestObject nest;
    
    public AntBehavior(Grid2D grid, ArrayList<AntObject> ants, AntObject foragerAnt, NestObject nest) {
    	this.grid = grid;
    	this.ants = ants;
    	this.foragerAnt = foragerAnt;
    	this.nest = nest;
    }
    
    public enum LeaveRequestState {
        followForager {
        },
        goToSource {
        },
        goToBase {
        };
    }

    public void run() {
        ArrayList<Tile> foundSources = new ArrayList<>();
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
            		
            		int foodOverload = ant.addFood(source.getFood());
                    int materialOverload = ant.addMaterial(source.getMaterial());
                    
                    source.setFood(foodOverload);
                    source.setMaterial(materialOverload);
                    
                    ant.moveTo(grid, nest.getTile());
                    ant.setState(LeaveRequestState.goToBase);
            	} else {
            		if (ant.getTile().equals(foundSources.remove(0))) {
            			foundSources.remove(0);
            		}
                    ant.setState(LeaveRequestState.followForager);
            	}
            } else if (ant.getState() == LeaveRequestState.goToBase && !ant.isMoving()) {
            	if (ant.getTile().equals(nest.getTile())) {
                	nest.setFood(nest.getFood() + ant.getFood());
                	ant.setFood(0);
                	
                	nest.setMaterial(nest.getMaterial() + ant.getMaterial());
                	ant.setMaterial(0);
            	}
            	
            	ant.setState(LeaveRequestState.followForager);
            }
        }
    }
}
