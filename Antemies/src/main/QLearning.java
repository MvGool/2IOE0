package main;

import main.Node;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class QLearning {

    private Node[][] grid; //grid of the map

    private int playerX, playerY; //initial position of controlled ant (probably not needed)

    // private int states = grid.length * grid[0].length; //number of states

    private double gamma = 0.4d;

    private String[] actions = {
            "left", "right", "up", "down" //TODO update for diagonal movement as well
    };

    Random ran = new Random();


    public void execute(Node[][] grid) {
        int states = grid.length * grid[0].length;
        final Double[][] Q = new Double[states][actions.length]; //Q-table


        // Initialize Q
        for (int s = 0; s < states; s++) {
            for (int a = 0; a < actions.length; a++) {
                if (a == 0) { // right movement
                    if (!((s%grid.length)+1 >= grid.length)) {
                        if (grid[s / grid.length][(s % grid.length) + 1] == null) {
                            Q[s][a] = null;
                        } else {
                            Q[s][a] = 0d;
                        }
                    } else {
                        Q[s][a] = null;
                    }
                } else if (a == 1) { //left movement
                    if (!((s%grid.length)-1< 0)) {
                        if (grid[s / grid.length][(s % grid.length) - 1] == null) {
                            Q[s][a] = null;
                        } else {
                            Q[s][a] = 0d;
                        }
                    }else {
                        Q[s][a] = null;
                    }
                }  else if (a == 2) { //upper movement
                    if (!((s/grid.length)-1< 0)) {
                        if (grid[(s / grid.length) - 1][s % grid.length] == null) {
                            Q[s][a] = null;
                        } else {
                            Q[s][a] = 0d;
                        }
                    }else {
                        Q[s][a] = null;
                    }
                } else if (a == 3) { //downward movement
                    if (!((s / grid.length) + 1 >= grid.length)) {
                        if (grid[(s / grid.length) + 1][s % grid.length] == null) {
                            Q[s][a] = null;
                        } else {
                            Q[s][a] = 0d;
                        }
                    }else {
                        Q[s][a] = null;
                    }
                }
            }
        }

        for (int k = 0; k<100; k++) { //outer loop
            int startState = ran.nextInt(states);
            int nextState = 0;

            for (int i = 0; i < 7; i++) { // until end state is reached (time limit in our case)
                ArrayList<Integer> randomActions = new ArrayList<Integer>();
                double maxQ = 0d;
                int action = 0;
                for (int j = 0; j < actions.length; j++) { //choose a random action
                    if (Q[startState][j] != null) {
                        randomActions.add(j);

                    }
                }

                int randomer = ran.nextInt(randomActions.size());
                action = randomActions.get(randomer);

                //Determine the next state using the best action
                if (action == 0) {
                    nextState = startState + 1;
                } else if (action == 1) {
                    nextState = startState - 1;
                } else if (action == 2) {
                    nextState = startState - grid.length;
                } else if (action == 3) {
                    nextState = startState + grid.length;
                }

                maxQ = 0d; //reset maxQ in order to use for the formula below

                for (int j = 0; j < actions.length; j++) { //compute max Q using the actions possible in the nextState
                    if (Q[nextState][j] != null && Q[nextState][j] >= maxQ) {
                        maxQ = Q[nextState][j];
                    }
                }

                //compute Q-value for (s,a) where s = startState and a = action by usinng the immediate reward (rewards(nextState)
                // and gamma * Q(s',a') where s' = nextState and a' = best action in nextState
                Q[startState][action] = grid[nextState/grid.length][nextState%grid.length].reward + gamma * maxQ;

                startState = nextState; //set the nextState as startState
            }
        }
        for (int i=0;i<Q.length;i++){
            for (int j=0;j<Q[0].length;j++){
                System.out.printf("%.3f", Q[i][j]);
                System.out.print(" | ");
            }
            System.out.println();
        }
    }
    public static void main(String args[]) throws InterruptedException {

        Node[][] grid = new Node[3][3];
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                grid[i][j] = new Node(i,j);
            }
        }
        grid[1][2].reward = 100;
        QLearning qlearn = new QLearning();
        qlearn.execute(grid);
    }
}



