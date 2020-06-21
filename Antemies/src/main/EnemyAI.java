package main;

import engine.objects.AntObject;

public class EnemyAI {

    Node[][] grid = new Node[3][3];
    private QLearning qLearn = new QLearning();
    private AntObject enemyAnt = new AntObject();
    private Double[][] Qtable = qLearn.execute(grid);


    public void behave(AntObject ant) {
        int startState = ant.getPosition();
        int nextState = 0;
        int action = 0;

        for (int i=0;i<Qtable[0].length;i++) {
            if (Qtable[position][i] >= action) {
                action = i;
            }
        }

        if (action == 0) {
            nextState = startState + 1;
        } else if (action == 1) {
            nextState = startState - 1;
        } else if (action == 2) {
            nextState = startState - grid.length;
        } else if (action == 3) {
            nextState = startState + grid.length;
        } else if (action == 4) {
            nextState = (startState - grid.length) + 1;
        } else if (action == 5){
            nextState = (startState - grid.length) - 1;
        } else if (action == 6){
            nextState = (startState + grid.length) + 1;
        } else if (action == 7){
            nextState = (startState + grid.length) -1;
        }

        ant.moveTo(grid, goal); // TODO move to nextState

    }


}
