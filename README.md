2048 Multiplayer
================

##About The Game
2048 Multiplayer is just like regular 2048 except instead of tiles showing up randomly, your opponent chooses where tiles appear. The goal of the game is to reach 2048 before your opponent does.

### Technologies Used For This Project 
* [KryoNet](https://github.com/EsotericSoftware/kryonet)
* [Java](https://www.oracle.com/java/index.html)
* [Swing](http://docs.oracle.com/javase/7/docs/api/javax/swing/package-summary.html)

### Cool features:
* Networking
* Multiplayer

### Program Design
* Game.java: Game Main class which specifies the frame and buttons of the GUI. Also initializes the server or client and handles incoming network requests.
* Network.java: Contains network logic common to both the client and server such as registering data with KryoNet. Contains several classes used to transmit data over the network including TileRequest, Score, and MyTurn. Includes network global variables including port.
* GameBoard.java: Contains the two Grids and the state of the game including score and turn. Updates the GUI with the state of the game (such as score, turn, and game overs). Forwards keyboard input to the Grid object. 
* Grid.java: Contains the state of a 2048 grid including where tiles are, tile values, and tile colors. Includes methods to determine if the game is over, to determine if the user has won, and to reset the grid. Also contains functionality to drop random tiles on the board in single player mode. Finally, includes the 2048 game logic such as what happens to tiles when they are shifted in a certain direction and when tiles merge.
* Shifter.java: Contains Shifter enum which represents direction to shift tiles in.
* GridButton.java: Extends JButton and graphically represents a tile. Adds state about the tile's position on the 2048 grid so appropriate action is taken when it is pressed.
