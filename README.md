# A.I.-Dungeon-Crawler

The Dungeon Crawler program is for Artifical Intelligence and uses a GUI to showcase different types of searches. When a search is played, a screen will load with a specific maze and a player. The goal is for the player to find and traverse the map to collect all of the gold. Each map varies in size, path, and if there is a solution. Depending on the search, the player will use a varying range of optimal paths to get all of the gold on the screen. At the end, it will display a vitory screen showing how much gold is left and the actions. The searches include: Breadth-First, Depth-First, Iterative Deepening, Bi-Directional, Priority Queue, A*, and Suboptimal A*. 
(The GUI will not operate without the Graphics file, which is too big to upload)

To test different mazes, comment in/out the mazes in main starting at line 154 in the DrBsGui file. 

To see Breadth-First: uncomment line 331 in GameState and comment out lines 333-358. 
To see Depth-First: uncomment lines 334-336, 352-358 and comment out the surrounding lines.
To see Iterative Deepening: uncomment line 339, 352-358 and comment out what is above and below that method.
To see Bi-Directional: uncomment line 341, 352-358 and comment out what is above and below that method.
To see Priority Queue: uncomment line 344, 352-358 and comment out what is above and below that method.
To see A*: uncomment line 347, 352-358 and comment out what is above and below that method.
To see Suboptimal A*: uncomment line 350, 352-358 and comment out what is above and below that method.
