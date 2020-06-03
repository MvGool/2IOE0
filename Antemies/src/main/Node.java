package main;


public class Node {
    public int x,y;
    public int heuristic;
    public int finalCost;
    boolean solution;
    public Node parent;
    public int reward;


    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getHeuristic() {
        return this.heuristic;
    }

    public int getFinal() {
        return this.finalCost;
    }

    public void setHeuristic(int value) {
        this.heuristic = value;
    }

    public void setFinal(int value) {
        this.finalCost = value;
    }
    public void setSolution(boolean sol) {
        this.solution = sol;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

}
