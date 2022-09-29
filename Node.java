package com.example.examplemod;

public class Node {

	private int x;
	private int y;
	private Node parent;
	private boolean walkable;
	private int G;
	private int H;
	
	public Node(int pX, int pY, Node pParent, boolean pWalkable) {
		
		x = pX;
		y = pY;
		parent = pParent;
		walkable = pWalkable;
		
	}
	
	public int getF() {
		
		return G + H;
		
	}
	
	public int getG() {
		
		return G;
		
	}
	
	public void setG() {
		
		if (parent != null) {
			
			int parentG = parent.getG();
			
			if (parent.getX() == x || parent.getY() == y) {
				
				G = parentG + 10;
				
			} else {
				
				G = parentG + 14;
				
			}
			
		}
		
	}
	
	public int getH() {
		
		return H;
		
	}
	
	public void setH(Node endNode) {
		
		H = 10 * (Math.abs(endNode.getX() - x) + Math.abs(endNode.getY() - y));
		
	}
	
	public boolean isWalkabale() {
		
		return walkable;
		
	}
	
	public int getX() {
		
		return x;
		
	}
	
	public int getY() {
		
		return y;
		
	}
	
	public void setParent(Node newParent) {
		
		parent = newParent;
		
	}
	
	public Node getParent() {
		
		return parent;
		
	}
	
	
}