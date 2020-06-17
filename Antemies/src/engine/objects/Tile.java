package engine.objects;

import engine.maths.Vector3f;

public class Tile {
	private int x, y;
	private boolean obstacle = false;
	private boolean discovered;
	private int heuristic;
	private int finalCost;
	private int food = 0;
	private int material = 0;
	private Tile parent;
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
		this.discovered = false;
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
	
	public boolean isDiscovered() {
		return discovered;
	}

	public void setDiscovered(boolean discovered) {
		this.discovered = discovered;
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
	
	public int getFood() {
		return food;
	}

	public void setFood(int food) {
		this.food = food;
	}

	public int getMaterial() {
		return material;
	}

	public void setMaterial(int material) {
		this.material = material;
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
