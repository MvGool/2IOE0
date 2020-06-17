package main;

import main.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import engine.objects.Grid2D;
import engine.objects.Tile;

public class QLearning {

    // private Node[][] grid; //grid of the map
    private Grid2D grid; //grid of the map

    private double gamma = 0.4d;

    private String[] actions = {
            "right", "left", "up", "down", "45", "-45", "135", "-135"
    };

    Random ran = new Random();


    public HashMap<Tile, Double[]> execute(Grid2D grid) {
        int states = grid.getSize() * grid.getSize();
        //final Double[][] Q = new Double[states][actions.length]; //Q-table

        HashMap<Tile, Double[]> Q
                = new HashMap<>();


        // Initialize Q
        for (int x = -grid.getSize()/2; x <= grid.getSize()/2; x++) {
            for (int y = -grid.getSize()/2; y <= grid.getSize()/2; y++) {
                Double[] legalMoves = new Double[actions.length];

                for (int a = 0; a < actions.length; a++) {
                    if (a == 0) { // right movement
                        if (grid.hasTile(x, y+1)) {
                            if ( grid.getTile(x, y+1).isObstacle()) {
                                legalMoves[a] = null;
                            } else {
                                legalMoves[a] = 0d;
                            }
                        } else {
                            legalMoves[a] = null;
                        }
                    } else if (a == 1) { //left movement
                        if (grid.hasTile(x, y-1)) {
                            if (grid.getTile(x, y-1).isObstacle()) {
                                legalMoves[a] = null;
                            } else {
                                legalMoves[a] = 0d;
                            }
                        }else {
                            legalMoves[a] = null;
                        }
                    }  else if (a == 2) { //upper movement
                        if (grid.hasTile(x-1, y)) {
                            if (grid.getTile(x-1, y).isObstacle()) {
                                legalMoves[a] = null;
                            } else {
                                legalMoves[a] = 0d;
                            }
                        }else {
                            legalMoves[a] = null;
                        }
                    } else if (a == 3) { //downward movement
                        if (grid.hasTile(x+1, y)) {
                            if (grid.getTile(x+1, y).isObstacle()) {
                                legalMoves[a] = null;
                            } else {
                                legalMoves[a] = 0d;
                            }
                        }else {
                            legalMoves[a] = null;
                        }
                    } else if (a == 4) { //45 degrees diagonal movement
                        if (grid.hasTile(x-1, y+1)) {
                            if (grid.getTile(x-1, y+1).isObstacle()) {
                                legalMoves[a] = null;
                            } else {
                                legalMoves[a] = 0d;
                            }
                        }else {
                            legalMoves[a] = null;
                        }
                    } else if (a == 5) { //-45 degrees diagonal movement
                        if (grid.hasTile(x-1, y-1)) {
                            if (grid.getTile(x-1, y-1).isObstacle()) {
                                legalMoves[a] = null;
                            } else {
                                legalMoves[a] = 0d;
                            }
                        }else {
                            legalMoves[a] = null;
                        }
                    } else if (a == 6) { //135 degrees diagonal movement
                        if (grid.hasTile(x+1, y+1)) {
                            if (grid.getTile(x+1, y+1).isObstacle()) {
                                legalMoves[a] = null;
                            } else {
                                legalMoves[a] = 0d;
                            }
                        }else {
                            legalMoves[a] = null;
                        }
                    } else if (a == 7) { //-135 degrees diagonal movement
                        if (grid.hasTile(x+1, y-1)) {
                            if (grid.getTile(x+1, y-1).isObstacle()) {
                                legalMoves[a] = null;
                            } else {
                                legalMoves[a] = 0d;
                            }
                        }else {
                            legalMoves[a] = null;
                        }
                    }
                }
                Q.put(grid.getTile(x, y), legalMoves);
            }
        }

        for (int k = 0; k<100; k++) { //outer loop
            int startStateX = ran.nextInt(grid.getSize()/2 + grid.getSize()/2) - grid.getSize()/2;
            int startStateY = ran.nextInt(grid.getSize()/2 + grid.getSize()/2) - grid.getSize()/2;
            int nextStateX = 0;
            int nextStateY = 0;

            long t = System.currentTimeMillis();
            long end = t + 500;
            while (System.currentTimeMillis() < end){
                ArrayList<Integer> randomActions = new ArrayList<Integer>();
                double maxQ = 0d;
                int action = 0;
                for (int j = 0; j < actions.length; j++) { //choose a random action
                    if (Q.get(grid.getTile(startStateX, startStateY))[j] != null) {
                        randomActions.add(j);
                    }
                }

                int randomer = ran.nextInt(randomActions.size());
                action = randomActions.get(randomer);

                //Determine the next state using the best action
                if (action == 0) { //right movement
                    nextStateX = startStateX;
                    nextStateY = startStateY + 1;
                } else if (action == 1) { //left movement
                    nextStateX = startStateX;
                    nextStateY = startStateY - 1;
                } else if (action == 2) { //upper movement
                    nextStateX = startStateX - 1;
                    nextStateY = startStateY;
                } else if (action == 3) { //downward movement
                    nextStateX = startStateX + 1;
                    nextStateY = startStateY;
                } else if (action == 4) { //45 degrees diagonal movement
                    nextStateX = startStateX - 1;
                    nextStateY = startStateY + 1;
                } else if (action == 5){ //-45 degrees diagonal movement
                    nextStateX = startStateX - 1;
                    nextStateY = startStateY - 1;
                } else if (action == 6){ //135 degrees diagonal movement
                    nextStateX = startStateX + 1;
                    nextStateY = startStateY + 1;
                } else if (action == 7){ //135 degrees diagonal movement
                    nextStateX = startStateX + 1;
                    nextStateY = startStateY - 1;
                }

                maxQ = 0d; //reset maxQ in order to use for the formula below

                for (int j = 0; j < actions.length; j++) { //compute max Q using the actions possible in the nextState
                    if (Q.get(grid.getTile(nextStateX, nextStateY))[j] != null && Q.get(grid.getTile(nextStateX, nextStateY))[j] >= maxQ) {
                        maxQ = Q.get(grid.getTile(nextStateX, nextStateY))[j];
                    }
                }

                //compute Q-value for (s,a) where s = startState and a = action by usinng the immediate reward of nextState
                // and gamma * Q(s',a') where s' = nextState and a' = best action in nextState
                Q.get(grid.getTile(startStateX, startStateY))[action] = grid.getTile(nextStateX, nextStateY).getReward() + gamma * maxQ;

                //set the nextState as startState
                startStateX = nextStateX;
                startStateY = nextStateY;
            }
        }
        /**
         for (int i=0;i<Q.length;i++){
         for (int j=0;j<Q[0].length;j++){
         System.out.printf("%.3f", Q[i][j]);
         System.out.print(" | ");
         }
         System.out.println();
         }
         */
        return Q;
    }


}



