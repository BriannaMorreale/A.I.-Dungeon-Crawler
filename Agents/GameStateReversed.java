import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class GameStateReversed extends GameState {
	

	public static final char WALL = Screen.S_WALL;
	public static final char GROUND = Screen.S_GROUND;
	public static final char GOLD = Screen.S_GOLD;
	public static final char ELIXER = Screen.S_ELIXER;
	public static final char ENEMY = Screen.S_ENEMY;
	public static final char PLAYER = Screen.S_PLAYER;
	public static final char PLAYER_AND_GOLD = Screen.S_PLAYER_AND_GOLD;
	public static final char PLAYER_AND_MISSING_GOLD = Screen.S_PLAYER_AND_MISSING_GOLD;
	public static final char MISSING_GOLD = Screen.S_MISSING_GOLD;



	//This constructor assumes that it is being created as a child from the existing s
	public GameStateReversed(GameStateReversed s, int newX, int newY) {
		super(s,newX, newY);
	}
	
	@Override
	public GameState[] getNextStates() {
			
		GameState[] gameArray = new GameState[4];
			
		if(map[row][column] == MISSING_GOLD) {
			GameStateReversed child = new GameStateReversed(this, row, column);
			child.action = AgentAction.putDownGold;
			child.changeMissingGoldToGold(row, column);
			GameStateReversed[] goldArray = new GameStateReversed[1];				
			goldArray[0] = child;
			return goldArray;
		}
		else {
			if(map[row][column-1] != WALL && map[row][column-1] != ENEMY) {
				GameStateReversed nodeUp = new GameStateReversed(this, row, column-1);
				nodeUp.action = AgentAction.moveLeft;
				gameArray[0] = nodeUp;
				}
			if(map[row][column+1] != WALL && map[row][column+1] != ENEMY) {
				GameStateReversed nodeDown = new GameStateReversed(this, row, column+1);
				nodeDown.action = AgentAction.moveRight;
				gameArray[1] = nodeDown;
				}
			if(map[row+1][column] != WALL && map[row+1][column] != ENEMY) {
				GameStateReversed nodeLeft = new GameStateReversed(this, row+1, column);
				nodeLeft.action = AgentAction.moveDown;
				gameArray[2] = nodeLeft;
				}
			if(map[row-1][column] != WALL && map[row-1][column] != ENEMY) {
				GameStateReversed nodeRight = new GameStateReversed(this, row-1, column);
				nodeRight.action = AgentAction.moveUp;
				gameArray[3] = nodeRight;
				}
				
				return gameArray;
			}
	
	}

	//Probably don't call this one, except from the static createInitialGameStates method
	private GameStateReversed(char[][] map) {
		super(map);
		
	}
	
	public static GameStateReversed[] createInitialGameStates(char [][] map) {
		
		//Figure out how many gold
		char[][] startingMap = new char[map.length][];
		int numInitialStates = 0;
		for(int i = 0; i < map.length; i++) {
			startingMap[i] = new char[map[i].length];
			for(int j = 0; j < map[i].length; j++) {
				startingMap[i][j] = map[i][j];
				if(map[i][j] == GOLD) {
					numInitialStates++;
					startingMap[i][j] = MISSING_GOLD;
				}
				else if(map[i][j] == PLAYER_AND_GOLD) {
					numInitialStates++;
					startingMap[i][j] = MISSING_GOLD; //We will put the player on every gold
				}
				else if(map[i][j] == PLAYER) {
					startingMap[i][j] = GROUND; //We will put the player on every gold, so remove it from where it is
				}
			}
		}
		//create blank states
		GameStateReversed [] gss = new GameStateReversed[numInitialStates];
		int currentNum = 0;
		//Put player on every missing gold, as they all could potentially be the "last" gold we pickup
		for(int i = 0; i < startingMap.length; i++) {
			for(int j = 0; j < startingMap[i].length; j++) {
				if(startingMap[i][j] == MISSING_GOLD) {
					startingMap[i][j] = PLAYER_AND_MISSING_GOLD;
					gss[currentNum++] = new GameStateReversed(startingMap);
					startingMap[i][j] = MISSING_GOLD;
				}
			}
		}
		
		return gss;

	}

}
