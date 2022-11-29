import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import org.w3c.dom.Node;

//Brianna Morreale
//CS4720

public class GameState implements Comparable<GameState>{

	private static int numNodesExplored = 0;
	private static int numNodesCreated = 0;

	public static final char WALL = Screen.S_WALL;
	public static final char GROUND = Screen.S_GROUND;
	public static final char GOLD = Screen.S_GOLD;
	public static final char ELIXER = Screen.S_ELIXER;
	public static final char ENEMY = Screen.S_ENEMY;
	public static final char PLAYER = Screen.S_PLAYER;
	public static final char PLAYER_AND_GOLD = Screen.S_PLAYER_AND_GOLD;
	public static final char PLAYER_AND_MISSING_GOLD = Screen.S_PLAYER_AND_MISSING_GOLD;
	public static final char MISSING_GOLD = Screen.S_MISSING_GOLD;




	//Probably faster to compare characters (==) than strings (.equals())
	protected final char[][] map;

	//x and y are the locations of the character on the map
	protected final int row;
	protected final int column;

	//Distance is the current distance the character traveled to get to this location, not how far remaining
	private final int currentDistance;

	//Parent is the GameState that generated this one
	private GameState parent;

	//Action will ultimately be the action we take - Note: Do not create "new" ones, just use the existing static ones
	protected AgentAction action;

	//For now, we will say that two states are the same if they have the same string representation
	//Currently build like "x y mapCharactersHere"
	private String stringRepresentationOfState; //We may need to do lazy instantiation on this, if it is slow

	//This constructor assumes that there is an 'S' somewhere on the map and the game is just starting
	//i.e., no parent node, and no distance
	public GameState(char[][] map) {
		numNodesCreated++; //One more node created

		parent = null;
		currentDistance = 0;

		action = null;

		int newX = -1;
		int newY = -1;

		this.map = new char[map.length][map[0].length];
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[i].length; j++) {
				//Copy things over
				this.map[i][j] = map[i][j];

				//Patch if necessary
				if(this.map[i][j] == MISSING_GOLD) {
					s.append(GROUND); //Keep the missing gold, but put ground in our state
				}
				else if(this.map[i][j] == PLAYER_AND_MISSING_GOLD) {
					newX = i;
					newY = j;
					this.map[i][j] = MISSING_GOLD;
					s.append(GROUND); //Keep the player and missing gold, but put Player in our state
				}
				else if(this.map[i][j] == PLAYER) {
					newX = i;
					newY = j;
					this.map[i][j] = GROUND;
					s.append(this.map[i][j]);
				}
				else if(this.map[i][j] == PLAYER_AND_GOLD) {
					newX = i;
					newY = j;
					this.map[i][j] = GOLD;
					s.append(this.map[i][j]);
				} else {
					s.append(this.map[i][j]);
				}
			}
		}

		row = newX;
		column = newY;
		stringRepresentationOfState = row + " " + column + " " + s;
	}

	public void changeMissingGoldToGold(int row, int col) {
		if(row >= 0 && row <= map.length && col >= 0 && col < map[0].length && map[row][col] == MISSING_GOLD ) {
			map[row][col] = GOLD;
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < map.length; i++) {
				for(int j = 0; j < map[i].length; j++) {
					this.map[i][j] = map[i][j];
					//Patch if necessary
					if(this.map[i][j] == MISSING_GOLD) {
						sb.append(GROUND); //Keep the missing gold, but put ground in our state
					} else {
						sb.append(map[i][j]);
					}
				}
			}
			stringRepresentationOfState = row + " " + col + " " + sb;
		}
	}

	public void changeGoldToGround(int row, int col) {
		if(row >= 0 && row < map.length && col >= 0 && col < map[0].length && map[row][col] == GOLD) {
			map[row][col] = GROUND;
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < map.length; i++) {
				for(int j = 0; j < map[i].length; j++) {
					this.map[i][j] = map[i][j];
					//Patch if necessary
					if(this.map[i][j] == MISSING_GOLD) {
						sb.append(GROUND); //Keep the missing gold, but put ground in our state
					} else {
						sb.append(map[i][j]);
					}
				}
			}
			stringRepresentationOfState = row + " " + col + " " + sb;
		}
	}

	//This constructor assumes that it is being created as a child from the existing s
	public GameState(GameState s, int newX, int newY) {
		numNodesCreated++; //One more node created

		parent = s;
		currentDistance = s.currentDistance+1;

		action = null;

		this.row = newX;
		this.column = newY;
		this.map = new char[s.map.length][s.map[0].length];
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < s.map.length; i++) {
			for(int j = 0; j < s.map[i].length; j++) {
				this.map[i][j] = s.map[i][j];
				//Patch if necessary
				if(this.map[i][j] == MISSING_GOLD) {
					sb.append(GROUND); //Keep the missing gold, but put ground in our state
				} else {
					sb.append(map[i][j]);
				}
			}
		}
		stringRepresentationOfState = row + " " + column + " " + sb;
	}

	@Override
	public String toString() {
		//All one line, so that we can use the string as a "hash" to quickly determine if things are already being searched
		return stringRepresentationOfState;
	}

	//These 2 methods make sure the HashSet works correctly
	@Override
	public boolean equals(final Object o)
	{
		if (o instanceof GameState)
		{
			return stringRepresentationOfState.equals(((GameState)o).stringRepresentationOfState);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return stringRepresentationOfState.hashCode();
	}

	@Override
	public int compareTo(GameState o) {

		int thisGold = 0;
		int oGold = 0;

		for(int i=0; i< map.length; i++) {
			for(int j=0; j < map[i].length; j++) {
				if(o.map[i][j] == GOLD) {
					//oGold += 2;
					oGold += 4;
				}
				if(this.map[i][j] == GOLD) {
					//thisGold += 2;
					thisGold += 4;
				}
			}
		}

		if(map[row][column] == GOLD ) {
			thisGold--;
		}

		if(o.map[o.row][o.column] == GOLD) {
			oGold--;
		}


		if(thisGold + this.currentDistance > oGold + o.currentDistance) {
			return 1;
		}
		else if(thisGold + this.currentDistance < oGold + o.currentDistance) {
			return -1;
		}
		else {
			return 0;
		}
		
		//return stringRepresentationOfState.compareTo(o.stringRepresentationOfState);

	}


	public void printMaze() {
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[i].length; j++) {
				if(i == row && j == column) {
					if(map[i][j] == GOLD) {
						System.out.print(PLAYER_AND_GOLD);
					}
					else {
						System.out.print(PLAYER);
					}
				}
				else {
					System.out.print(map[i][j]);
				}
			}
			System.out.println();
		}
	}

	public Queue<AgentAction> getAllActions(){
		if(parent == null) {
			Queue<AgentAction> moves = new LinkedList<AgentAction>();
			if(action != null) {
				moves.add(action);
			}
			return moves;
		}
		else {
			Queue<AgentAction> moves = parent.getAllActions();
			moves.add(action);
			return moves;
		}
	}

	public void setAction(AgentAction a) {
		action = a;
	}

	public boolean isGoalState() {
		//TODO
		//is goal if all of the gold is collected
		for(int i=0; i< map.length; i++) {
			for(int j=0; j<map[i].length; j++) {
				if(map[i][j] == GOLD) {
					return false;

				}
			}
		}

		return true;
	}

	public GameState[] getNextStates() {

		GameState[] gameArray = new GameState[4];

		if(map[row][column] == GOLD) {
			GameState child = new GameState(this, row, column);
			child.action = AgentAction.pickupSomething;
			child.changeGoldToGround(row, column);
			GameState[] goldArray = new GameState[1];
			goldArray[0] = child;
			return goldArray;
		}
		else {
			if(map[row][column-1] != WALL && map[row][column-1] != ENEMY) {
				GameState nodeUp = new GameState(this, row, column-1);
				nodeUp.action = AgentAction.moveLeft;
				gameArray[0] = nodeUp;
			}
			if(map[row][column+1] != WALL && map[row][column+1] != ENEMY) {
				GameState nodeDown = new GameState(this, row, column+1);
				nodeDown.action = AgentAction.moveRight;
				gameArray[1] = nodeDown;
			}
			if(map[row+1][column] != WALL && map[row+1][column] != ENEMY) {
				GameState nodeLeft = new GameState(this, row+1, column);
				nodeLeft.action = AgentAction.moveDown;
				gameArray[2] = nodeLeft;
			}
			if(map[row-1][column] != WALL && map[row-1][column] != ENEMY) {
				GameState nodeRight = new GameState(this, row-1, column);
				nodeRight.action = AgentAction.moveUp;
				gameArray[3] = nodeRight;
			}

			return gameArray;
		}

	}

	public static Queue<AgentAction> search(char[][] problem){

		numNodesExplored = 0;
		numNodesCreated = 0;
		
		//return breadthFirstSearch(problem);

		//hashset for DFS
		//HashSet<GameState> reached = new HashSet<GameState>();
		//GameState node = new GameState(problem);
		//Queue<AgentAction> solution = depthFirstSearch(node, reached);
		
		
		//Queue<AgentAction> solution = iterativeDeepening(node);

		//Queue<AgentAction> solution = biDirectionalSearch(problem);
		
		
		//Queue<AgentAction> solution = bestCost(problem);
		
		
		//Queue<AgentAction> solution = aStarSearch(problem);
		
		
		Queue<AgentAction> solution = suboptimalAStarSearch(problem);

		if(solution == null) {
			GameState noVictory = new GameState(problem);
			noVictory.setAction(AgentAction.declareVictory);
			System.out.println("The Number of nodes explored = " + numNodesExplored);
			System.out.println("The Number of nodes created = " + numNodesCreated);
			return noVictory.getAllActions();
		}

		return solution;
	}

	private static Queue<AgentAction> breadthFirstSearch(char[][] problem){
		//Some static variables so that we can determine how "hard" problems are

		GameState node = new GameState(problem); //Essentially the second line of the book's BFS


		if(node.isGoalState()) { //Essentially the start of line 3 of the book's BFS
			node.setAction(AgentAction.declareVictory); //We don't have to do anything
			return node.getAllActions(); //Just the single thing, but this is an example for later
		}

		//Create the frontier queue, and reached hash
		Queue<GameState> frontier = new LinkedList<GameState>();
		HashSet<GameState> reached = new HashSet<GameState>();

		//Add the first node to the hash
		frontier.add(node);
		reached.add(node);

		do {
			node = frontier.poll();
			reached.add(node);
			GameState [] children = node.getNextStates();

			for(int i=0; i< children.length; i++) {
				if(children[i] != null) {
					if(reached.contains(children[i]) == false ) {	
						frontier.add(children[i]);
						reached.add(children[i]);
						numNodesExplored++;

						if(children[i].isGoalState()) {

							GameState grandchild = new GameState(children[i], children[i].row, children[i].column);
							grandchild.setAction(AgentAction.declareVictory);
							System.out.println("*Number of nodes explored = " + numNodesExplored);
							System.out.println("Number of nodes created = " + numNodesCreated);

							return grandchild.getAllActions();
						}
					}	
				}
			}

		}while(frontier.isEmpty() == false);

		GameState noVictory = new GameState(problem);
		noVictory.setAction(AgentAction.declareVictory);

		if(frontier.isEmpty() == true) {
			System.out.println("Number of nodes explored = " + numNodesExplored);
			System.out.println("Number of nodes created = " + numNodesCreated);
			return noVictory.getAllActions();
		}



		//Print this at the end, so we know how "hard" the problem was
		System.out.println("*Number of nodes explored = " + numNodesExplored);
		System.out.println("Number of nodes created = " + numNodesCreated);

		return noVictory.getAllActions(); 

	}

	private static Queue<AgentAction> depthFirstSearch (GameState node, HashSet<GameState> reached){//pass node nad hashset


		// Check goal state
		if(node.isGoalState()) {
			GameState grandchild = new GameState(node, node.row, node.column); //fix this area
			grandchild.setAction(AgentAction.declareVictory); 
			return grandchild.getAllActions();
		}


		// Add node
		reached.add(node);
		numNodesExplored++;

		// Get next states
		GameState [] children = node.getNextStates();

		// For each state n, call depth first search recursively (so long as node is not in discovered)
		for(int i =0; i < children.length; i++) {
			if(!reached.contains(children[i]) && children[i] != null) {
				Queue<AgentAction> solution = depthFirstSearch(children[i], reached);

				if(solution != null) {
					return solution;
				}
			}
		}


		return null;

	}

	private static Queue<AgentAction> recursiveDLS (GameState node, HashSet<GameState> reached, int limit){
		//cutoff is null or zero

		// Check goal state
		if(node.isGoalState()) {
			GameState grandchild = new GameState(node, node.row, node.column); //fix this area
			grandchild.setAction(AgentAction.declareVictory); 
			return grandchild.getAllActions();
		}

		if(limit == 0) {
			return null;
		}

		// Add node
		reached.add(node);
		numNodesExplored++;

		// Get next states
		GameState [] children = node.getNextStates();

		// For each state n, call depth first search recursively (so long as node is not in discovered)
		for(int i =0; i < children.length; i++) {
			if(!reached.contains(children[i]) && children[i] != null) {
				Queue<AgentAction> solution = recursiveDLS(children[i], reached, limit-1);

				if(solution != null) {
					return solution;
				}
			}
		}

		return null;
	}

	private static Queue<AgentAction> iterativeDeepening(GameState node){
		for(int depth = 0; depth< Integer.MAX_VALUE; depth++) {
			HashSet<GameState> reached = new HashSet<GameState>();
			Queue<AgentAction> result = recursiveDLS(node, reached, depth);
			if(result != null) {
				return result;
			}
		}

		return null;
	}

	private static HashSet<GameState> reversedDirectionSearch(char[][] problem, int depth){

		GameStateReversed [] nodeArray = GameStateReversed.createInitialGameStates(problem);

		Queue<GameState> frontier = new LinkedList<GameState>();
		HashSet<GameState> reached = new HashSet<GameState>();

		for(int i = 0; i< nodeArray.length; i++) {
			frontier.add(nodeArray[i]);
			reached.add(nodeArray[i]);
		}


		do {
			GameState node = frontier.poll();
			reached.add(node);
			GameState [] children = node.getNextStates();

			for(int i=0; i< children.length; i++) {
				if(children[i] != null) {
					if(reached.contains(children[i]) == false ) {	
						if(children[i].currentDistance >= depth  ) {
							break;
						}
						frontier.add(children[i]);
						reached.add(children[i]);
						numNodesExplored++;

					}
				}	
			}


		}while(frontier.isEmpty() == false);

		return reached; 

	}

	private static Queue<AgentAction> normalDirectionSearch(char[][] problem, HashSet<GameState> reversed){

		GameState node = new GameState(problem); //Essentially the second line of the book's BFS

		//Create the frontier queue, and reached hash
		Queue<GameState> frontier = new LinkedList<GameState>();
		HashSet<GameState> reached = new HashSet<GameState>();

		//Add the first node to the hash
		frontier.add(node);
		reached.add(node);


		do {
			node = frontier.poll();
			reached.add(node);
			GameState [] children = node.getNextStates();

			for(int i=0; i< children.length; i++) {
				if(children[i] != null) {
					if(reached.contains(children[i]) == false ) {	
						frontier.add(children[i]);
						reached.add(children[i]);
						numNodesExplored++;

						if(reversed.contains(children[i])) {

							if(children[i].map[children[i].row][children[i].column] == GROUND) {

								children[i].map[children[i].row][children[i].column] = PLAYER;

							}
							else if(children[i].map[children[i].row][children[i].column] == GOLD) {

								children[i].map[children[i].row][children[i].column] = PLAYER_AND_GOLD;

							}
							else if(children[i].map[children[i].row][children[i].column] == MISSING_GOLD) {

								children[i].map[children[i].row][children[i].column] = PLAYER_AND_MISSING_GOLD;
							}

							Queue<AgentAction> secondHalf = breadthFirstSearch(children[i].map);

							Queue<AgentAction> firstHalf = children[i].getAllActions();

							firstHalf.addAll(secondHalf);

							System.out.println("Number of nodes explored = " + numNodesExplored);
							System.out.println("Number of nodes created = " + numNodesCreated);

							return firstHalf;

						}
					}	
				}
			}

		}while(frontier.isEmpty() == false);

		GameState noVictory = new GameState(problem);
		noVictory.setAction(AgentAction.declareVictory);

		if(frontier.isEmpty() == true) {
			//System.out.println("Number of nodes explored = " + numNodesExplored);
			//System.out.println("Number of nodes created = " + numNodesCreated);
			return noVictory.getAllActions();
		}



		//Print this at the end, so we know how "hard" the problem was
		//System.out.println("Number of nodes explored = " + numNodesExplored);
		//System.out.println("Number of nodes created = " + numNodesCreated);

		return noVictory.getAllActions(); 

	}

	private static Queue<AgentAction> biDirectionalSearch(char[][] problem){
		HashSet<GameState> list = reversedDirectionSearch(problem, 50);
		return normalDirectionSearch(problem, list);
	}

	private static Queue<AgentAction> bestCost(char[][] problem){
		//all the same number, same distance shoulf be stupid
		GameState node = new GameState(problem); 


		if(node.isGoalState()) { 
			node.setAction(AgentAction.declareVictory); 
			return node.getAllActions(); 
		}

		PriorityQueue<GameState> frontier = new PriorityQueue<GameState>();
		HashSet<GameState> reached = new HashSet<GameState>();

		frontier.add(node);
		reached.add(node);

		do {
			node = frontier.poll();
			reached.add(node);
			GameState [] children = node.getNextStates();

			for(int i=0; i< children.length; i++) {
				if(children[i] != null) {
					if(reached.contains(children[i]) == false ) {	
						frontier.add(children[i]);
						reached.add(children[i]);
						numNodesExplored++;

						if(children[i].isGoalState()) {

							GameState grandchild = new GameState(children[i], children[i].row, children[i].column);
							grandchild.setAction(AgentAction.declareVictory);
							System.out.println("Number of nodes explored = " + numNodesExplored);
							System.out.println("Number of nodes created = " + numNodesCreated);

							return grandchild.getAllActions();
						}
					}	
				}
			}

		}while(frontier.isEmpty() == false);

		GameState noVictory = new GameState(problem);
		noVictory.setAction(AgentAction.declareVictory);

		if(frontier.isEmpty() == true) {
			System.out.println("Number of nodes explored = " + numNodesExplored);
			System.out.println("Number of nodes created = " + numNodesCreated);
			return noVictory.getAllActions();
		}



		//Print this at the end, so we know how "hard" the problem was
		System.out.println("Number of nodes explored = " + numNodesExplored);
		System.out.println("Number of nodes created = " + numNodesCreated);

		return noVictory.getAllActions(); 

	}
	
	private static Queue<AgentAction> aStarSearch(char[][] problem){
		
		GameState node = new GameState(problem); 


		if(node.isGoalState()) { 
			node.setAction(AgentAction.declareVictory); 
			return node.getAllActions(); 
		}

		PriorityQueue<GameState> frontier = new PriorityQueue<GameState>();
		HashSet<GameState> reached = new HashSet<GameState>();

		frontier.add(node);
		reached.add(node);

		do {
			node = frontier.poll();
			reached.add(node);
			GameState [] children = node.getNextStates();

			for (int i = 0; i < children.length; i++) {
				if (children[i] != null) {
					if (reached.contains(children[i]) == false && frontier.contains(children[i]) == false) {
						frontier.add(children[i]);
						reached.add(children[i]);
						numNodesExplored++;

						if (children[i].isGoalState()) {

							GameState grandchild = new GameState(children[i], children[i].row, children[i].column);
							grandchild.setAction(AgentAction.declareVictory);
							System.out.println("Number of nodes explored = " + numNodesExplored);
							System.out.println("Number of nodes created = " + numNodesCreated);

							return grandchild.getAllActions();
						}
					}

					else if (frontier.contains(children[i]) ){
						Iterator<GameState> it = frontier.iterator();
						while(it.hasNext()) {
							GameState g = it.next();
							if(g.stringRepresentationOfState.equals(children[i].stringRepresentationOfState)) {
								if(g.currentDistance > children[i].currentDistance) {
									frontier.remove(g);
									frontier.add(children[i]);
									break;

								}
				
							}
						}


					}
				}
			}

		}while(frontier.isEmpty() == false);

		GameState noVictory = new GameState(problem);
		noVictory.setAction(AgentAction.declareVictory);

		if(frontier.isEmpty() == true) {
			System.out.println("The Number of nodes explored = " + numNodesExplored);
			System.out.println("The Number of nodes created = " + numNodesCreated);
			return noVictory.getAllActions();
		}


		System.out.println("The Number of nodes explored = " + numNodesExplored);
		System.out.println("The Number of nodes created = " + numNodesCreated);

		return noVictory.getAllActions(); 

	}
	
	private static Queue<AgentAction> suboptimalAStarSearch(char[][] problem){
		
		GameState node = new GameState(problem); 


		if(node.isGoalState()) { 
			node.setAction(AgentAction.declareVictory); 
			return node.getAllActions(); 
		}

		PriorityQueue<GameState> frontier = new PriorityQueue<GameState>();
		HashSet<GameState> reached = new HashSet<GameState>();

		frontier.add(node);
		reached.add(node);

		do {
			node = frontier.poll();
			reached.add(node);
			GameState [] children = node.getNextStates();

			for (int i = 0; i < children.length; i++) {
				if (children[i] != null) {
					if (reached.contains(children[i]) == false && frontier.contains(children[i]) == false) {
						frontier.add(children[i]);
						reached.add(children[i]);
						numNodesExplored++;

						if (children[i].isGoalState()) {

							GameState grandchild = new GameState(children[i], children[i].row, children[i].column);
							grandchild.setAction(AgentAction.declareVictory);
							System.out.println("Number of nodes explored = " + numNodesExplored);
							System.out.println("Number of nodes created = " + numNodesCreated);

							return grandchild.getAllActions();
						}
					}

					else if (frontier.contains(children[i]) ){
						Iterator<GameState> it = frontier.iterator();
						while(it.hasNext()) {
							GameState g = it.next();
							if(g.stringRepresentationOfState.equals(children[i].stringRepresentationOfState)) {
								if(g.currentDistance > children[i].currentDistance) {
									frontier.remove(g);
									frontier.add(children[i]);
									break;

								}
				
							}
						}


					}
				}
			}

		}while(frontier.isEmpty() == false);

		GameState noVictory = new GameState(problem);
		noVictory.setAction(AgentAction.declareVictory);

		if(frontier.isEmpty() == true) {
			System.out.println("The Number of nodes explored = " + numNodesExplored);
			System.out.println("The Number of nodes created = " + numNodesCreated);
			return noVictory.getAllActions();
		}


		System.out.println("The Number of nodes explored = " + numNodesExplored);
		System.out.println("The Number of nodes created = " + numNodesCreated);

		return noVictory.getAllActions(); 

	}


}

