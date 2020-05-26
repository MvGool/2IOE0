package main;


import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.List;


public class Astar {
    public static final int moveCost = 10; //move cost for horizontal/vertical movements
    public static final int diagonalCost = 14; //move cost for diagonal movements

    private Node[][] grid; //grid of the map
    private PriorityQueue<Node> openList;
    private boolean[][] closedList;

    private int startX, startY; //start node
    private int goalX, goalY; //goal node

    public Astar(int width, int height, int startX, int startY, int goalX, int goalY, int blocks[][]) {
        grid = new Node[width][height];
        closedList = new boolean[width][height];
        openList = new PriorityQueue<Node>((Node n1, Node n2) -> {
            return n1.getFinal() < n2.getFinal() ? -1 : n1.getFinal() > n2.getFinal() ? 1 : 0;
        });

        this.startX = startX;
        this.startY = startY;
        this.goalX = goalX;
        this.goalY = goalY;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = new Node(i,j);
                grid[i][j].setHeuristic(Math.abs(i-goalX) + Math.abs(j-goalY));
                grid[i][j].setSolution(false);
            }
        }

        grid[startX][startY].setFinal(0);

        for (int i = 0; i < blocks.length; i++) {
            addObstacle(blocks[i][0],blocks[i][1]);
        }
    }

    //calculate the final cost
    public void updateCost(Node current, Node next, int cost) {
        if (next == null || closedList[next.getX()][next.getY()]) {
            return;
        }

        int tFinalCost = next.getHeuristic() + cost;
        boolean isOpen = openList.contains(next);

        if (!isOpen || tFinalCost < next.getFinal()) {
            next.setFinal(tFinalCost);
            next.parent = current;

            if (!isOpen)
                openList.add(next);
        }
    }

    //method to add obstacles on the grid
    public void addObstacle(int x, int y) {
        grid[x][y] = null;
    }

    /**
     * Main algorithm
     */
    public void process() {
        openList.add(grid[startX][startY]);
        Node current;

        while(true) {
            current = openList.poll();

            if (current == null) {
                break;
            }

            closedList[current.getX()][current.getY()] = true;

            if (current.equals(grid[goalX][goalY])) {
                return;
            }

            Node next;


            for(int[] neighbor : getNeighbors(current)) {
                next = grid[neighbor[0]][neighbor[1]];
                if (neighbor[2] == 0) { //if neighbor is horizontal/vertical
                    updateCost(current, next, current.getFinal() + moveCost);
                } else if (neighbor[2] == 1) { //if neighbor is diagonal
                    updateCost(current, next, current.getFinal() + diagonalCost);
                }
            }

        }

    }

    /**
     * Get neighbors of a node. If it is a horizontal/vertical neighbor add 0 as a 3rd integer
     * and if neighbor is diagonal add 1 as a 3rd integer (used later in calculating scores)
     */
    public List<int[]> getNeighbors(Node current) {
        int[][] directions = {{1,0},{0,1},{-1,0},{0,-1}};
        int[][] diagonalDirections = {{1,1},{-1,-1},{1,-1},{-1,1}};

        List<int[]> result = new ArrayList<int[]>();
        for (int[] dir : directions) {
            if (current.getX() + dir[0] >= 0 && current.getY() + dir[1] >= 0
                    && current.getX() + dir[0] < grid.length && current.getY() + dir[1] < grid.length	) {
                result.add(new int[] {current.getX()+dir[0], current.getY() + dir[1],0});
            }
        }
        for (int[] dir : diagonalDirections) {
            if (current.getX() + dir[0] >= 0 && current.getY() + dir[1] >= 0
                    && current.getX() + dir[0] < grid.length && current.getY() + dir[1] < grid.length	) {
                result.add(new int[] {current.getX()+dir[0], current.getY() + dir[1],1});
            }
        }
        return result;

    }

    //display the grid
    public void display() {
        System.out.println("Grid: ");

        for (int i = 0; i< grid.length; i++) {
            for (int j = 0; j< grid[i].length; j++) {
                if (i == startX && j == startY) {
                    System.out.print("ST ");
                } else if (i == goalX && j == goalY) {
                    System.out.print("FH ");
                } else if (grid[i][j]!=null) {
                    System.out.printf("%-3d ", 0);
                } else {
                    System.out.print("## ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    //display scores for nodes
    public void displayScores() {
        System.out.println("SCORES: ");
        for (int i = 0; i< grid.length; i++) {
            for (int j = 0; j< grid[i].length; j++) {
                if (grid[i][j] != null) {
                    System.out.printf("%-3d ", grid[i][j].getFinal());
                } else {
                    System.out.print("## ");
                }

            }
            System.out.println();
        }
        System.out.println();
    }

    //display the shorters path
    public void displaySolution() {
        if (closedList[goalX][goalY]) {
            System.out.println("Path: ");
            Node current = grid[goalX][goalY];
            System.out.println(current);
            grid[current.getX()][current.getY()].setSolution(true);

            while(current.parent != null){
                System.out.println(" ->" + current.parent);
                grid[current.parent.getX()][current.parent.getY()].solution = true;
                current = current.parent;
            }

            System.out.println("\n");

            for (int i = 0; i< grid.length; i++) {
                for (int j = 0; j< grid[i].length; j++) {
                    if (i == startX && j == startY) {
                        System.out.print("ST ");
                    } else if (i == goalX && j == goalY) {
                        System.out.print("FH ");
                    } else if (grid[i][j]!=null) {
                        System.out.printf("%-3s ", grid[i][j].solution ? "X" : "0" );
                    } else {
                        System.out.print("## ");
                    }
                }
                System.out.println();
            }
            System.out.println();
        } else {
            System.out.println("No possible path");
        }
    }

    public static void main(String[] args) {
        Astar aStar = new Astar(5,5,0,0,3,2,new int[][] {
                {0,4},{2,2},{3,1},{3,3},{2,1},{2,3}
        });
        aStar.display();
        aStar.process();
        aStar.displayScores();
        aStar.displaySolution();
    }

}
