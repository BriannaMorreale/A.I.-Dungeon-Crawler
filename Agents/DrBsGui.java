import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.swing.JFrame;

//Brianna Morreale
//CS4720
//9/14/22

public class DrBsGui extends JFrame{
	private static final long serialVersionUID = 1L;

	private Screen screen;

	public DrBsGui() {
		setTitle("Dr Bs Dungeon Crawler");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		File fileWithLayout = findRandomLayout();
		screen = new Screen(fileWithLayout);
		this.add(screen);

		pack();

		setVisible(true);
	}

	public DrBsGui(File fileWithLayout) {
		setTitle("Dr Bs Dungeon Crawler - " + fileWithLayout.getName());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		screen = new Screen(fileWithLayout);
		this.add(screen);
		
		pack();

		setVisible(true);
	}
	
	public DrBsGui(String mapName, String [] theMap) {
		setTitle(mapName);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		char [][] theNewMap = new char[theMap.length][theMap[0].length()];
		for(int i = 0; i < theNewMap.length; i++) {
			char[] characters = theMap[i].toCharArray();
			for(int j = 0; j<theNewMap[i].length; j++) {
				theNewMap[i][j] = characters[j];
			}
		}
		
		screen = new Screen(mapName, theNewMap);
		this.add(screen);
		
		pack();

		setVisible(true);
	}

	public DrBsGui(String mapName, char [][] theMap) {
		setTitle(mapName);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		screen = new Screen(mapName, theMap);
		this.add(screen);
		
		pack();

		setVisible(true);
	}

	@SuppressWarnings("unused")
	private static void loadAllFiles() {
		try {
			File folder = new File("DungeonLayouts");
			String [] files = folder.list();

			for(int i = 0; i < files.length; i++) {
				File f = new File(folder.getCanonicalPath() + "\\" + files[i]);
				if(f.isFile()) {
					new DrBsGui(new File(folder.getCanonicalPath() + "\\" + files[i]));
					//MyGui g = new MyGui(new File(folder.getCanonicalPath() + "\\" + files[i]));
					//g.dispose();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

	private static File findRandomLayout() {
		File f = null;
		try {
			File folder = new File("DungeonLayouts");
			String [] files = folder.list();

			f = new File(folder.getCanonicalPath() + "\\" + files[(int)((Math.random()*files.length))]);
			while(f.isDirectory()) {
				f = new File(folder.getCanonicalPath()  + "\\" + files[(int)((Math.random()*files.length))]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return f;
	}
	
	
	public static void main(String [] args){
		//loadAllFiles(); //You can try this one if you want, but probably do them one at a time.....
	//	new DrBsGui(new File("DungeonLayouts\\testMaze.txt"));

//	new DrBsGui(new File("DungeonLayouts\\testSearch.txt"));
//		new DrBsGui(new File("DungeonLayouts\\trappedClassic.txt"));
//		new DrBsGui(new File("DungeonLayouts\\tinySafeSearch.txt"));
//		new DrBsGui(new File("DungeonLayouts\\tinySearch.txt"));
//		new DrBsGui(new File("DungeonLayouts\\greedySearch.txt"));
//		new DrBsGui(new File("DungeonLayouts\\smallSafeSearch.txt"));
//		new DrBsGui(new File("DungeonLayouts\\smallSearch.txt"));
//		new DrBsGui(new File("DungeonLayouts\\mediumSafeSearch.txt"));
//		new DrBsGui(new File("DungeonLayouts\\trickySearch.txt"));

		//These might be too hard for some of the searches
//		new DrBsGui(new File("DungeonLayouts\\openSearch.txt"));
//		new DrBsGui(new File("DungeonLayouts\\boxSearch.txt"));
//		new DrBsGui(new File("DungeonLayouts\\mediumSearch.txt"));
//		new DrBsGui(new File("DungeonLayouts\\bigSafeSearch.txt"));
//		new DrBsGui(new File("DungeonLayouts\\oddSearch.txt"));
		
		
		
		//Some files for testing
		//	new DrBsGui(Paths.get("DungeonLayouts","testMaze.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","testSearch.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","smallMaze.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","smallSafeSearch.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","smallSearch.txt").toFile());

		//BFS should be able to complete all of these
		// new DrBsGui(Paths.get("DungeonLayouts","bigCorners.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","bigMaze.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","contoursMaze.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","mediumDottedMaze.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","mediumScaryMaze.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","openMaze.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","trappedClassic.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","trickySearch.txt").toFile());


		//Two more for us to try, but probably not BFS
		 new DrBsGui(Paths.get("DungeonLayouts","oddSearch.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","smallClassic.txt").toFile());

		//If you want, these should also be a go for BFS
		// new DrBsGui(Paths.get("DungeonLayouts","capsuleClassic.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","greedySearch.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","mediumCorners.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","mediumMaze.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","mediumSafeSearch.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","minimaxClassic.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","testClassic.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","tinyCorners.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","tinyMaze.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","tinySearch.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","tinySafeSearch.txt").toFile());



		//Maybe - but not BFS, DFS can do all of these, probably not uniform cost
		// new DrBsGui(Paths.get("DungeonLayouts","bigSafeSearch.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","bigSearch.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","boxSearch.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","contestClassic.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","mediumClassic.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","mediumSearch.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","openClassic.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","openSearch.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","originalClassic.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","powerClassic.txt").toFile());
		// new DrBsGui(Paths.get("DungeonLayouts","trickyClassic.txt").toFile());
		
	}


}
