package engine.objects;

import engine.maths.Vector3f;

public class Tile {
	private int x, y;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tile other = (Tile) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
