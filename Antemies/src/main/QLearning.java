package main;

import main.Node;

public class QLearning {

    private Node[][] grid; //grid of the map

    private int playerX, playerY; //initial position of controlled ant (probably not needed)

    private int states = grid.length * grid[0].length; //number of states

    private double gamma = 0.4d;

    private String[] actions = {
            "left", "right", "up", "down" //TODO update for diagonal movement as well
    };


    public void execute() {
        final Double[][] Q = new Double[states][actions.length]; //Q-table
        int d = actions.length;

        // Initialize Q
        for (int s = 0; s < states; s++) {
            for (int a = 0; a < actions.length; a++) {
                if (a == 0) { // right movement
                    if (grid[s /d ][(s % d)+1] == null) {
                        Q[s][a] = null;
                    } else {
                        Q[s][a] = 0d;
                    }
                } else if (a == 1) { //left movement
                    if (grid[s / d][(s % d)-1] == null) {
                        Q[s][a] = null;
                    } else {
                        Q[s][a] = 0d;
                    }
                }  else if (a == 2) { //upper movement
                    if (grid[(s / d)-1][s % d] == null) {
                        Q[s][a] = null;
                    } else {
                        Q[s][a] = 0d;
                    }
                } else if (a == 3) { //downward movement
                    if (grid[(s / d)+1][s % d] == null) {
                        Q[s][a] = null;
                    } else {
                        Q[s][a] = 0d;
                    }
                }
            }
        }

        int startState = 0; //should be random
        int nextState = 0;

        //TODO make another for loop for repeating until Q converges

        for (int i=0; i<50;i++){ // until end state is reached (time limit in our case)
            double maxQ = 0d;
            int action = 0;
            for (int j=0; j<actions.length;j++){ //choose the best action from startState.
                if (Q[startState][j] >= maxQ){
                    maxQ = Q[startState][j];
                    action = j;
                }
            }

            //Determine the next state using the best action
            if (action == 0){
                nextState = startState - 1;
            } else if (action == 1){
                nextState = startState + 1;
            } else if (action == 2){
                nextState = startState + 4;
            } else if (action == 3){
                nextState = startState - 4;
            }

            maxQ = 0d; //reset maxQ in order to use for the formula below

            for (int j=0; j<actions.length;j++){ //compute max Q using the actions possible in the nextState
                if (Q[nextState][j] >= maxQ){
                    maxQ = Q[nextState][j];
                }
            }

            //compute Q-value for (s,a) where s = startState and a = action by usinng the immediate reward (rewards(nextState)
            // and gamma * Q(s',a') where s' = nextState and a' = best action in nextState
            Q[startState][action] = rewards(nextState) + gamma * maxQ;

            startState = nextState; //set the nextState as startState
        }

    }

    public int rewards(int state){
        return grid[state/4][state%4].reward;
    } //get rewards from node
    //TODO update node class to have attribute showing whether its unexplroed/explored/source



}



