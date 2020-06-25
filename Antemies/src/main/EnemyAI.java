package main;

import java.util.HashMap;

import engine.maths.Vector3f;
import engine.objects.Grid2D;
import engine.objects.Tile;
import main.objects.AntObject;

public class EnemyAI {
    private Grid2D grid;
    private QLearning qLearn = new QLearning();
    private HashMap<Tile, Double[]> qTable;

    public EnemyAI(Grid2D grid) {
    	this.grid = grid;
    	this.qTable = qLearn.execute(grid);
    }

    public void behave(AntObject ant) {
        Vector3f startState = new Vector3f(ant.getTile().getX(), 1, ant.getTile().getY());
        Vector3f nextState = new Vector3f(ant.getTile().getX(), 1, ant.getTile().getY());
        
        int action = 0;
        double actionValue = 0;
        for (int i = 0; i < qTable.get(ant.getTile()).length; i++) {
            if (qTable.get(ant.getTile())[i] > actionValue) {
                action = i;
                actionValue = qTable.get(ant.getTile())[i];
            }
        }

        if (action == 0) {
            nextState = Vector3f.add(startState, new Vector3f(1, 0, 0));
        } else if (action == 1) {
            nextState = Vector3f.add(startState, new Vector3f(-1, 0, 0));
        } else if (action == 2) {
            nextState = Vector3f.add(startState, new Vector3f(0, 0, -1));
        } else if (action == 3) {
            nextState = Vector3f.add(startState, new Vector3f(0, 0, 1));
        } else if (action == 4) {
            nextState = Vector3f.add(startState, new Vector3f(-1, 0, 1));
        } else if (action == 5){
            nextState = Vector3f.add(startState, new Vector3f(-1, 0, -1));
        } else if (action == 6){
            nextState = Vector3f.add(startState, new Vector3f(1, 0, 1));
        } else if (action == 7){
            nextState = Vector3f.add(startState, new Vector3f(1, 0, -1));
        }
        
        ant.moveTo(grid, new Tile((int) nextState.getX(), (int) nextState.getZ()));
    }
}
