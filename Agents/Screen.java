import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class Screen extends JPanel implements KeyListener{
	private static final long serialVersionUID = 1L;
	public static final int tileSize = 32;

	public static final int SLEEP_TIME = 50;
	
	private BufferedImage wall;
	private BufferedImage ground;

	public static final char S_WALL = 'w';
	public static final char S_GROUND = ' ';
	public static final char S_GOLD = '.';
	public static final char S_ELIXER = 'o';
	public static final char S_ENEMY = 'E';
	public static final char S_PLAYER = 'S';
	public static final char S_PLAYER_AND_GOLD = 'A';
	public static final char S_PLAYER_AND_MISSING_GOLD = 'H';
	public static final char S_MISSING_GOLD = 'G';


	private BufferedImage[][] screen;
	private char [][] map;

	private ArrayList<GameObject> enemys;
	private ArrayList<GameObject> gold;
	private ArrayList<GameObject> elixers;
	private GameObject player = null;

	private int numActions; 
	private boolean playerDeclaresVictory;
	private int numEnemyHits;
	private String mapName;
	private int startingGold;


	public Screen(String theMapName, char [][] theMap) {
		setupInitialVariables(theMapName, theMap);
	}

	public Screen(File filename){

		//Read from the file into a String[][]
		char [][] theMap = null;
		try {
			Scanner in = new Scanner(filename);
			ArrayList<String> contents = new ArrayList<String>();

			while(in.hasNextLine()) {
				contents.add(in.nextLine());
			}

			int rows = contents.size();
			int cols = contents.get(0).length();

			theMap = new char[rows][cols];

			for (int i = 0; i < rows; i++){
				char [] columns = contents.get(i).toCharArray();
				for (int j = 0; j < cols; j++){
					theMap[i][j] = columns[j];
				}
			}
			in.close();

			String mapName = filename.getCanonicalPath();
			mapName = mapName.substring(mapName.lastIndexOf("\\")+1, mapName.lastIndexOf('.'));
			setupInitialVariables(mapName, theMap);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private void setupInitialVariables(String theMapName, char[][] theMap) {

		//Just in case things don't work so well with the map
		this.setSize(10*tileSize,10*tileSize);
		this.setPreferredSize(getSize());
		this.setMinimumSize(getSize());

		this.setFocusable(true);

		System.out.println("Key Listener");
		System.out.println("\tawsd or arrows to move");
		System.out.println("\tspacebar to pickup gold/elixer");
		System.out.println("\tp to place gold - mainly for bi-directional search");
		System.out.println("\tv to declare victory");
		this.addKeyListener(this);

		setDoubleBuffered(true);

		enemys = new ArrayList<GameObject>();
		gold = new ArrayList<GameObject>();
		elixers = new ArrayList<GameObject>();

		numActions = 0;
		playerDeclaresVictory = false;
		numEnemyHits = 0;

		mapName = theMapName;

		int rows = theMap.length;
		int cols = theMap[0].length;
		map = new char[rows][cols];
		screen = new BufferedImage[rows][cols];

		//Select the images to use for this run
		wall = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\dungeon\\wall");
		ground = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\dungeon\\floor"); 


		for (int i = 0; i < rows; i++){
			for (int j = 0; j < cols; j++){
				if(theMap[i][j] == S_WALL) {
					screen[i][j] = wall;
				}
				else if(theMap[i][j] == S_GROUND) {
					screen[i][j] = ground;
				}
				else if(theMap[i][j] == S_PLAYER) {
					screen[i][j] = ground;
					loadPlayerImage(j,i);
				}
				else if(theMap[i][j] == S_PLAYER_AND_GOLD) {
					screen[i][j] = ground;
					BufferedImage coin = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\item\\gold");
					gold.add(new GameObject(j*tileSize,i*tileSize,coin,tileSize));
					loadPlayerImage(j,i);
				}
				else if(theMap[i][j] == S_GOLD) {
					//put the gold on the ground
					BufferedImage coin = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\item\\gold");
					gold.add(new GameObject(j*tileSize,i*tileSize,coin,tileSize));
					screen[i][j] = ground;
				}
				else if(theMap[i][j] == S_ENEMY) {
					BufferedImage enemy = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\monster");
					enemys.add(new GameObject(j*tileSize,i*tileSize,enemy,tileSize));
					screen[i][j] = ground;
				}
				else if(theMap[i][j] == S_ELIXER) {
					BufferedImage elixer = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\item\\potion");
					elixers.add(new GameObject(j*tileSize,i*tileSize,elixer,tileSize));
					screen[i][j] = ground;
				}
				else {
					System.err.println("Unhandled case: " + theMap[i][j]);
					throw new ArithmeticException();
				}
				map[i][j] = theMap[i][j];
			}
		}

		startingGold = gold.size();
		
		player.search(map);

		this.setSize(cols*tileSize,rows*tileSize);
		this.setPreferredSize(getSize());
		this.setMinimumSize(getSize());

	}


	public boolean isValidMove(int newRow, int newCol) {
		//System.out.println("Looking at spot " + newX + " " + newY);
		if( 0 <= newRow && 0 <= newCol && newRow < screen.length && newCol < screen[0].length) {
			//System.out.println("In bounds");
			if(screen[newRow][newCol] == ground) {
				//System.out.println("Found ground in that spot");
				return true;
			}
			else {
				//System.out.println("Didn't find a ground tile");
				if(screen[newRow][newCol] == wall) {
					//System.out.println("Found a wall");
				}
			}
		}
		else {
			System.out.println("Out of bounds");
		}

		return false;
	}

	public void move(GameObject g) {
		AgentAction action = g.getMove();
		if(action == null) {
			return;
		}
		//System.out.println("Screen action: "+action);
		int col = g.getColLocation();
		int row = g.getRowLocation();

		if(action == AgentAction.declareVictory) {
			playerDeclaresVictory = true;
		}
		else if(action == AgentAction.pickupSomething) {
			for (GameObject go: gold) {
				if((int)(go.getColLocationOnGraphics()/tileSize) == col && (int)(go.getRowLocationOnGraphics()/tileSize) == row) {
					gold.remove(go);
					//System.out.println("Pickup gold");
					break; //assume only 1 gold can be picked up at a time
				}
			}
			for (GameObject go: elixers) {
				if((int)(go.getColLocationOnGraphics()/tileSize) == col && (int)(go.getRowLocationOnGraphics()/tileSize) == row) {
					elixers.remove(go);
					break; //assume only 1 elixer can be picked up at a time
				}
			}
		}
		else if(action == AgentAction.moveRight) {
			if(isValidMove(row,col+1)) {
				//System.out.println("Move right");
				g.setColLocation(col+1);
			}
		}
		else if(action == AgentAction.moveLeft) {
			if(isValidMove(row,col-1)) {
				//System.out.println("Move left");
				g.setColLocation(col-1);
			}
		}
		else if(action == AgentAction.moveUp) {
			if(isValidMove(row-1,col)) {
				//System.out.println("Move up");
				g.setRowLocation(row-1);
			}
		}
		else if(action == AgentAction.moveDown) {
			if(isValidMove(row+1,col)) {
				//System.out.println("Move down");
				g.setRowLocation(row+1);
			}
		}
		else if(action == AgentAction.putDownGold) {
			//Check to see if there already is gold in that square, and don't allow it
			for (GameObject go: gold) {
				if((int)(go.getColLocationOnGraphics()/tileSize) == col && (int)(go.getRowLocationOnGraphics()/tileSize) == row) {
					break; //assume only 1 gold can be on a square at a time
				}
			}
			BufferedImage coin = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\item\\gold");
			gold.add(new GameObject(col*tileSize,row*tileSize,coin,tileSize));
			startingGold++;
		}
		else if(action == AgentAction.doNothing) {
				//System.out.println("Doing nothing");
			
		}
		else {
			System.out.println("Unhandled action " + action);
		}

		if(action.isAnAction()) {
			numActions++;
		}
		
		//repaint();
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		if(playerDeclaresVictory) {
			g.clearRect(0, 0, this.getWidth(), this.getHeight());
			g.drawString("Map: " + mapName, 3, 20);
			g.drawString("Gold Remaining " + gold.size() + " of " + startingGold, 3, 40);
			g.drawString("Total Actions: " + numActions, 3, 60);
			g.drawString("Enemy Hits: " + numEnemyHits, 3, 80);
			return;
		}
		else {
			move(player);
		}

		if(screen != null){
			for (int i = 0; i < screen.length; i++){
				for (int j = 0; j < screen[i].length; j++){
					g.drawImage(screen[i][j], j*tileSize, i*tileSize, null); //Drawing is by col, then row
				}
			}
		}

		//draw the gold
		for (GameObject go: gold){
			go.drawTheImage(g);
		}

		//draw the elixers
		for (GameObject go: elixers){
			go.drawTheImage(g);
		}

		//draw the enemies
		for (GameObject go: enemys){
			go.drawTheImage(g);
			if(sameSquare(player,go)){
				numEnemyHits++;
			}
		}

		player.drawTheImage(g);
		
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		repaint();
	}

	private void loadPlayerImage(int col, int row) {
		BufferedImage playerImage = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\player\\base");
		playerImage = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\player\\cloak",playerImage);
		playerImage = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\player\\boots",playerImage);
		playerImage = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\player\\gloves",playerImage);
		playerImage = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\player\\draconic_head",playerImage);
		playerImage = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\player\\draconic_wing",playerImage);
		player = new GameObject(col*tileSize,row*tileSize,playerImage,tileSize);
	}

	private boolean sameSquare(GameObject a, GameObject b) {
		if((int)(a.getColLocationOnGraphics()/tileSize) == (int)(b.getColLocationOnGraphics()/tileSize) && (int)(a.getRowLocationOnGraphics()/tileSize) == (int)(b.getRowLocationOnGraphics()/tileSize)) {
			return true;
		}
		return false;
	}

	private static BufferedImage copyImage(BufferedImage source){
		BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		Graphics g = b.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return b;
	}

	private static BufferedImage findRandomImage(String foldername) {
		BufferedImage image = null;
		try {
			File folder = new File(foldername);
			String [] files = folder.list();
			File f = new File(folder.getCanonicalPath() + "\\" + files[(int)((Math.random()*files.length))]);
			while(f.isDirectory()) { //2 folders in that array
				f = new File(folder.getCanonicalPath()  + "\\" + files[(int)((Math.random()*files.length))]);
			}
			image = ImageIO.read(f);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;
	}
	private static BufferedImage findRandomImage(String foldername, BufferedImage bottomStartingImage) {
		BufferedImage image = copyImage(bottomStartingImage);
		BufferedImage tmp = findRandomImage(foldername);
		image.getGraphics().drawImage(tmp,0,0,null);
		return image;
	}


	@Override
	public void keyPressed(KeyEvent e) {
		//System.out.println("Key pressed");
		player.setNextMove(e,this);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//System.out.println("Key released");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//System.out.println("Key typed");
	}

}
