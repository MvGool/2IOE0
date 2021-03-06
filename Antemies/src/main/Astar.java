package main;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

import engine.objects.*;

public class Astar {
    private static final int moveCost = 10; //move cost for horizontal/vertical movements
    private static final int diagonalCost = 14; //move cost for diagonal movements
    private Grid2D grid; //grid of the map
    private PriorityQueue<Tile> openList;
    private boolean[][] closedList;
    private Tile start; //start node
    private Tile goal; //goal node
    private Tile[] solution;

    public Astar(Grid2D grid, Tile start, Tile goal) {
        closedList = new boolean[grid.getSize()][grid.getSize()];
        openList = new PriorityQueue<Tile>((Tile n1, Tile n2) -> {
            return n1.getFinal() < n2.getFinal() ? -1 : n1.getFinal() > n2.getFinal() ? 1 : 0;
        });

        this.start = start;
        this.goal = goal;
        grid.setTile(start);
        grid.setTile(goal);

        for (int i = 0; i < grid.getSize(); i++) {
            for (int j = 0; j < grid.getSize(); j++) {
                grid.getTile(i-grid.getSize()/2, j-grid.getSize()/2).setHeuristic(Math.abs(i-grid.getSize()/2 - goal.getX()) + Math.abs(j-grid.getSize()/2 - goal.getY()));
            }
        }

        start.setFinal(0);
        this.grid = grid;
    }

    /**
     * Main algorithm
     */
    public Tile[] run() {
        openList.add(start);
        Tile current;

        while (true) {
            current = openList.poll();

            if (current.isObstacle()) {
                break;
            }
            
            closedList[current.getX() + grid.getSize()/2][current.getY() + grid.getSize()/2] = true;

            if (current.equals(goal)) {
                break;
            }

            Tile next;
            for (int[] neighbor : getNeighbors(current)) {
                next = grid.getTile(neighbor[0], neighbor[1]);

                if (neighbor[2] == 0) { //if neighbor is horizontal/vertical
                    updateCost(current, next, current.getFinal() + moveCost);
                } else if (neighbor[2] == 1) { //if neighbor is diagonal
                    updateCost(current, next, current.getFinal() + diagonalCost);
                }
            }
        }
        
        computeSolution();
        
        return solution;
    }

    /**
     * Get neighbors of a node. If it is a horizontal/vertical neighbor add 0 as a 3rd integer
     * and if neighbor is diagonal add 1 as a 3rd integer (used later in calculating scores)
     */
    private List<int[]> getNeighbors(Tile current) {
        int[][] directions = {{1,0}, {0,1}, {-1,0}, {0,-1}};
        int[][] diagonalDirections = {{1,1}, {-1,-1}, {1,-1}, {-1,1}};

        List<int[]> result = new ArrayList<int[]>();
        for (int[] dir : directions) {
            if (current.getX() + dir[0] >= -grid.getSize()/2 && current.getY() + dir[1] >= -grid.getSize()/2
                    && current.getX() + dir[0] < grid.getSize()/2 && current.getY() + dir[1] < grid.getSize()/2) {
                result.add(new int[] {current.getX() + dir[0], current.getY() + dir[1], 0});
            }
        }
        for (int[] dir : diagonalDirections) {
            if (current.getX() + dir[0] >= -grid.getSize()/2 && current.getY() + dir[1] >= -grid.getSize()/2
                    && current.getX() + dir[0] < grid.getSize()/2 && current.getY() + dir[1] < grid.getSize()/2) {
                result.add(new int[] {current.getX() + dir[0], current.getY() + dir[1], 1});
            }
        }
        return result;
    }

    //calculate the final cost
    private void updateCost(Tile current, Tile next, int cost) {
        if (next.isObstacle() || closedList[next.getX() + grid.getSize()/2][next.getY() + grid.getSize()/2]) {
            return;
        }
        int tFinalCost = next.getHeuristic() + cost;
        boolean isOpen = openList.contains(next);
        
        if (!isOpen || tFinalCost < next.getFinal()) {
            next.setFinal(tFinalCost);
            next.setParent(current);
            if (!isOpen) {
                openList.add(next);
            }
        }
    }
    
    private void computeSolution() {
    	ArrayList<Tile> listSolution = new ArrayList<>();
    	
    	if (closedList[goal.getX() + grid.getSize()/2][goal.getY() + grid.getSize()/2]) {
            Tile current = goal;
        	listSolution.add(goal);
            
            while (current.getParent() != null) {
            	listSolution.add(0, current.getParent());
                current = current.getParent();
            }
        } else {
            System.out.println("No possible path");
        }
    	
    	solution = new Tile[listSolution.size()];
    	listSolution.toArray(solution);
    }
    
    //display the grid
    public void display() {
        System.out.println("Grid: ");

        for (int i = 0; i < grid.getSize(); i++) {
            for (int j = 0; j < grid.getSize(); j++) {
            	if (grid.getTile(i - grid.getSize()/2, j - grid.getSize()/2).isObstacle()) {
            		System.out.print("##  ");
            	} else if (grid.getTile(i - grid.getSize()/2, j - grid.getSize()/2).equals(start)) {
                    System.out.print("ST  ");
                } else if (grid.getTile(i - grid.getSize()/2, j - grid.getSize()/2).equals(goal)) {
                    System.out.print("FH  ");
                } else {
                    System.out.printf("%-3d ", 0);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    //display scores for nodes
    public void displayScores() {
        System.out.println("SCORES: ");
        for (int i = 0; i < grid.getSize(); i++) {
            for (int j = 0; j < grid.getSize(); j++) {
                if (grid.getTile(i - grid.getSize()/2, j - grid.getSize()/2) != null) {
                    System.out.printf("%-3d ", grid.getTile(i - grid.getSize()/2, j - grid.getSize()/2).getFinal());
                } else {
                    System.out.print("## ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    //display the shortest path
    public void displaySolution() {
        if (closedList[goal.getX() + grid.getSize()/2][goal.getY() + grid.getSize()/2]) {
            System.out.println("Path: ");
            Tile current = goal;
            System.out.println(current);
            while (current.getParent() != null) {
                System.out.println(" ->" + current.getParent());
                current = current.getParent();
            }

            System.out.println("\n");
            for (int i = 0; i < grid.getSize(); i++) {
                for (int j = 0; j < grid.getSize(); j++) {
                	if (grid.getTile(i - grid.getSize()/2, j - grid.getSize()/2).isObstacle()) {
                		System.out.print("##  ");
                	} else if (grid.getTile(i - grid.getSize()/2, j - grid.getSize()/2).equals(start)) {
                        System.out.print("ST  ");
                    } else if (grid.getTile(i - grid.getSize()/2, j - grid.getSize()/2).equals(goal)) {
                        System.out.print("FH  ");
                    } else if (Arrays.asList(solution).contains(grid.getTile(i - grid.getSize()/2, j - grid.getSize()/2))) {
                    	System.out.print("X   ");
                    } else {
                    	System.out.print("0   ");
                    }
                }
                System.out.println();
            }
            System.out.println();
        } else {
            System.out.println("No possible path");
        }
    }
}
