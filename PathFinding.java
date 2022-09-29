package com.example.examplemod;

import java.util.LinkedList;

public class PathFinding {
	
	public static LinkedList<Node> getPath(Node[][] grid, int startX, int startY, int endX, int endY) {
		
		//Start and end nodes are made using passed x and y co-ordinates
		Node startNode = grid[startY][startX];
		Node endNode = grid[endY][endX];
		
		/*
		 * Two linked lists are made: the open list and the closed list
		 * Open List: List of nodes to be considered for the path
		 * Closed List: List of nodes do not need to be considered
		 */
		LinkedList<Node> openList = new LinkedList<Node>();
		openList.add(startNode);
		LinkedList<Node> closedList = new LinkedList<Node>();
		
		boolean pathFound = false;
		Node currentNode;
		
		//Loop runs while there are nodes to be considered
		while(!openList.isEmpty()) {
			
			/*
			 * Current node to be considered is the node with the best F value,
			 * where F = G + H
			 * (G is the cost of moving from the startNode to the currentNode,
			 * H is the heuristic/estimated cost of moving from the currentNode to the endNode)
			 */
			currentNode = bestNode(openList);
			
			//Terminate if the path has reached the endNode
			if(currentNode == endNode) {
				pathFound = true;
				return getPath(endNode);
			}
			
			//Move currentNode to closedList so it will not be considered in future.
			openList.remove(currentNode);
			closedList.add(currentNode);
			
			int tempG = 0;
			//The neighbouring nodes of the current node are all checked.
			for (Node n : getNeighbours(grid, currentNode, false)) {
				
				//Node will not be checked if it is in the closed list or is inaccessible
				if(!closedList.contains(n) && n.isWalkabale()) {
					
					/*
					 * Nodes with a greater G than the currentNode are added to the openList
					 * if they aren't already. Their parent is also set to be the currentNode
					 * (i.e. if the node n is in the final path, the previous node in that path
					 * will be currentNode.
					 * 
					 * At the start of the next loop, the node with the best F is chosen
					 * and the merry dance repeats.
					 */
					tempG = currentNode.getG();
					if (currentNode.getX() == n.getX() || currentNode.getY() == n.getY()) {
						tempG += 10;
					} else {
						tempG += 14;
					}
					
					if (!openList.contains(n) || tempG < n.getG()) {
						
						if (!openList.contains(n)) {
							openList.add(n);
						}
						n.setParent(currentNode);
						n.setG();
						
					}
					
				}
				
			}
			
		}
		return null;
		
	}
	
	public static void printGrid(Node[][] grid, LinkedList<Node> path) {
		
		/*
		 * The Node[][] is converted to a String[][] by stringGrid and then
		 * concatenated and printed to the console.
		 */
		
		String[][] stringGrid = stringGrid(grid, path);
		
		int i = 0;
		int j = 0;
		
		String[] row;
		String line;
		
		for(j = 0; j < stringGrid.length; j++) {

			line = "";
			row = stringGrid[j];
			for(i = 0; i < row.length; i++) {
				line = line.concat(row[i]);
			}
			System.out.println(line);
			
		}
		
	}
	
	public static String[][] stringGrid(Node[][] grid, LinkedList<Node> path) {
		
		/*
		 * A grid of Nodes and the path traversing it is converted to Strings
		 * so they can be concatenated and printed by printGrid.
		 * # = wall
		 *   = space
		 * . = path
		 */
		
		String[][] result = new String[grid.length][grid[0].length];
		
		int i = 0;
		int j = 0;
		
		for(j = 0; j < grid.length; j++) {
			
			for(i = 0; i < grid[j].length; i++) {
				
				if (grid[j][i].isWalkabale()) {
					result[j][i] = " ";
				} else {
					result[j][i] = "#";
				}
				
			}
			
		}
		
		for (Node n : path) {
			result[n.getY()][n.getX()] = ".";
		}
		
		return result;
		
	}
	
	public static LinkedList<Node> getPath(Node node) {
		
		/*
		 * Starting with a Node n, the path to n from the startNode is found by
		 * recursively finding each node's parent, forming a chain of nodes: the path. 
		 */
		
		LinkedList<Node> path = new LinkedList<Node>();
		path.add(node);
		
		while(node.getParent() != null) {
			
			path.add(node.getParent());
			node = node.getParent();
			
		}
		
		return path;
		
	}
	
	public static Node bestNode(LinkedList<Node> nodes) {
		
		/*
		 * Each Node in the list is checked and
		 * the node with the greatest F value is returned.
		 */
		
		Node bestNode = null;
		
		for (Node n : nodes) {
			
			if (bestNode == null) {
				bestNode = n;
			} else if (n.getF() < bestNode.getF()) {
				bestNode = n;
			}
			
		}
		
		return bestNode;
		
	}
	
	public static LinkedList<Node> getNeighbours(Node[][] grid, Node n, boolean diagonals) {
		
		/*
		 * Taking a Node[][] and a Node in that grid, a list of all adjacent nodes is returned.
		 * The 'diagonals' flag can be set if diagonal neighbours should also be added.
		 */
		
		LinkedList<Node> neighbourList = new LinkedList<Node>();
		int nX = n.getX();
		int nY = n.getY();
		
		//Left Column
		if (nX > 0) {
			if (nY > 0) {
				if (diagonals) {
					neighbourList.add(grid[nY - 1][nX - 1]);
				}
			}
			neighbourList.add(grid[nY][nX - 1]);
			if (nY < grid.length - 1) {
				if (diagonals) {
					neighbourList.add(grid[nY + 1][nX - 1]);
				}
			}
		}
		//Middle Column
		if (nY > 0) {
			neighbourList.add(grid[nY - 1][nX]);
		}
		
		if (nY < grid.length - 1) {
			neighbourList.add(grid[nY + 1][nX]);
		}
		//Right Column
		if (nX < grid[0].length - 1) {
			if (nY > 0) {
				if (diagonals) {
					neighbourList.add(grid[nY - 1][nX + 1]);
				}
			}
			neighbourList.add(grid[nY][nX + 1]);
			if (nY < grid.length - 1) {
				if (diagonals) {
					neighbourList.add(grid[nY + 1][nX + 1]);
				}
			}
		}
		
		return neighbourList;
	}
	
	public static Node[][] createGrid(int width, int height) {
		
		/*
		 * This method was mainly used for testing, but still works.
		 * Given a width and height, a Node[][] is returned consisting of randomly placed
		 * walls and gaps. Adjust wallProb to obtain the desired wall density.
		 */
		double wallProb = 0.25;
		Node[][] newGrid = new Node[height][width];
		
		int i = 0;
		int j = 0;
		
		for(j = 0; j < height; j++) {
			
			for(i = 0; i < width; i++) {
				
				double rand = Math.random();
				boolean walkable = false;
				if (rand > wallProb) {
					walkable = true;
				} else {
					walkable = false;
				}
				newGrid[j][i] = new Node(i, j, null, walkable);
				
			}
			
		}
		
		return newGrid;
		
	}
	
	public static Node[][] gridFromMaze(int[][] maze) {
		
		/*
		 * For the sake of simplicity, mazes from DFSMaze are int[][] consisting of
		 * 1s and 0s. This method converts them into Node[][]s so they can be traversed.
		 */
		
		Node[][] newGrid = new Node[maze.length][maze[0].length];
		
		int i = 0;
		int j = 0;
		
		for(j = 0; j < maze.length; j++) {
			
			for(i = 0; i < maze[0].length; i++) {
				
				if (maze[j][i] == 0) {
					newGrid[j][i] = new Node(i, j, null, true);
				} else {
					newGrid[j][i] = new Node(i, j, null, false);
				}
				
			}
			
		}
		
		return newGrid;
		
	}

}