package engine.objects;

import engine.maths.Vector3f;

public class Tile {
	private int x, y;
	private Vector3f position; // No value assigned yet
	private boolean obstacle;
	private int heuristic;
	private int finalCost;
	private Tile parent;
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Vector3f getVectorPosition() {
		return position;
	}
	
	public void setObstacle(boolean bool) {
		obstacle = bool;
	}
	
	public boolean isObstacle() {
		return obstacle;
	}
	
	public int getHeuristic() {
	    return heuristic;
	}
	
	public void setHeuristic(int value) {
	    heuristic = value;
	}
	
	public int getFinal() {
	    return finalCost;
	}
	
	public void setFinal(int value) {
	    finalCost = value;
	}
	
	public Tile getParent() {
		return parent;
	}
	
	public void setParent(Tile par) {
		parent = par;
	}
	
	@Override
	public String toString() {
	    return "[" + x + ", " + y + "]";
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		} else {
	    	Tile tile = (Tile) o;
	    	return this.getX() == tile.getX() && this.getY() == tile.getY();
		}
	}
}
