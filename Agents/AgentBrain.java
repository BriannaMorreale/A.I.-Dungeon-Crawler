import java.util.LinkedList;
import java.util.Queue;

public class AgentBrain {

	private Queue<AgentAction> nextMoves;	
	
	public AgentBrain() {
		nextMoves = new LinkedList<AgentAction>();
	}
	
	public void addNextMove(AgentAction nextMove) {
		this.nextMoves.add(nextMove);
	}

	public void clearAllMoves() {
		nextMoves = new LinkedList<AgentAction>();
	}

	public AgentAction getNextMove() {
		if(nextMoves.isEmpty()) {
			return AgentAction.doNothing;
		}
		return nextMoves.remove();
	}
	
	
	public void search(char [][] map) {
		
		//For the bi-directional search
//		GameStateReversed[] g = GameStateReversed.createInitialGameStates(map);
//		for(int i = 0; i < g.length; i++) {
//			System.out.println(g[i]); //Just in case you want to "see" them
//		}
		
		//TODO: Change this to false
		boolean useOnlyKeyListener = false;
		if(useOnlyKeyListener) {
			//For Key Listener, and empty list
			nextMoves = new LinkedList<AgentAction>();
		} else {
			//For code
			nextMoves = GameState.search(map);
		}
		
		

		//Add up everything except doNothing and victory
		int num = 0;
		AgentAction [] a = nextMoves.toArray(new AgentAction[] {});
		for(int i = 0; i < a.length; i++) {
			if(a[i] != AgentAction.doNothing && a[i] != AgentAction.declareVictory) {
				num++;
			}
		}
		System.out.println("Solution Depth = " + num);

	}
	
	
	
}
