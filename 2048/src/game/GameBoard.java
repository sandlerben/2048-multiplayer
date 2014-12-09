package game;


import game.Network.Score;
import game.Network.TileRequest;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

/**
 * This class contains the two 2048 boards and state about the games.
 */
@SuppressWarnings("serial")
public class GameBoard extends JPanel {
	private Grid me;
	private Grid other;

	// whether the game is running
	private boolean playing = false; 
	// whether this is singlePlayer game
	private boolean singleGame = true;
	// whether it is currently this Game's turn
	private boolean myTurn = true;

	// Server and Client objects which enable the GameBoard to make network
	// requests
	private Server server = null;
	private Client client = null;

	// Game constants
	public static final int COURT_WIDTH = 800;
	public static final int COURT_HEIGHT = 500;

	private final JLabel myScore = new JLabel();
	private final JLabel otherScore = new JLabel();
	private final JLabel turn = new JLabel();

	private Runnable updateOther;

	public GameBoard (final JPanel status_panel) {
		me = new Grid();
		other = new Grid();

		// add both boards to the board by default
		add(me, BorderLayout.CENTER);
		add(other, BorderLayout.CENTER);

		// enable keyboard focus on the game area. 
		setFocusable(true);

		// Runnable that runs when server or client wants to update the other
		// player with their score
		updateOther = new Runnable() {
			public void run () {
				if(client != null){
					Score score = new Score();
					score.value = me.getScore();
					client.sendTCP(score);
					client.sendTCP(me.data);
				}
				else if (server != null){
					Score score = new Score();
					score.value = me.getScore();
					server.sendToAllTCP(score);
					server.sendToAllTCP(me.data);
				}
			}
		};

		// Adds labels to status_panel 
		status_panel.add(myScore);
		status_panel.add(otherScore);
		status_panel.add(turn);
		myScore.setText("Your score: 0");
		otherScore.setText("");
		turn.setText("");

		// forwards directions to client Grid
		addKeyListener(boardKeyListener());
	}

	/**
	 * Resets the game board(s) to begin a single or multiplayer game
	 */
	public void reset() {
		playing = true;
		me.wipeGrid();
		if(singleGame){
			me.random();
			remove(other);
			myScore.setText("Your score: 0");
			otherScore.setText("");
			turn.setText("");
		}
		else {
			add(other);
			other.wipeGrid();
			other.buttonPressIncomplete();
			myScore.setText("Your score: 0");
			otherScore.setText("Other score: 0");

		}
		requestFocusInWindow();
		repaint();
	}

	/**
	 * Adds a server to the GameBoards state so the GameBoard can make requests
	 * @param server - Server which represents the user's network state when 
	 * they are hosting the match
	 */
	public void addServer(Server server) {
		this.server = server;
		other.server = server;
	}

	/**
	 * Adds a client to the GameBoards state so the GameBoard can make requests
	 * @param client - Client which represents the user's network state when 
	 * they have joined a match
	 */
	public void addClient(Client client) {
		this.client = client;
		other.client = client;
	}

	/**
	 * Handles a TileRequest and places a tile on the board with given location
	 * and value
	 * @param request - TileRequest from other player which contains state about
	 * where opponent placed his or her tile
	 */
	public void addTile(TileRequest request) {
		me.addValue(request.row, request.col, request.value);
	}

	/**
	 * Updates the rightmost Grid after the opponent has shifted his or her
	 * tiles
	 * @param otherData - 2d array representing the state of the opponent's
	 * grid
	 */
	public void updateOtherData(int[][] otherData) {
		other.data = otherData;
		if(other.gameOver()) {
			myScore.setText("You win: "+me.getScore());
			playing = false;
		}
		else {
			otherScore.setText("Other score: "+other.getScore());
		}
		repaint();
		other.repaint();
	}

	/**
	 * Updates the opponents score after they play their turn
	 * @param score - Score object which allows the opponent to update this
	 * client with their score
	 */
	public void updateOtherScore(Score score) {
		other.scoreCount = score.value;
	}

	/**
	 * Performs several functions in order to end this player's turn
	 */
	public void endMyTurn() {
		setMyTurn(false);
		other.buttonPressIncomplete();
		if(client != null){
			new Runnable() {
				public void run () {
					client.sendTCP(new Boolean(myTurn));
				}

			}.run();
		}
		else if (server != null){
			new Runnable() {
				public void run () {
					server.sendToAllTCP(new Boolean(myTurn));
				}

			}.run();
		}
	}

	/**
	 * Sets the turn of the player
	 * @param myTurn - boolean which indicates if it is this player's turn or
	 * the opponents turn
	 */
	public void setMyTurn(boolean myTurn) {
		this.myTurn = myTurn;
		turn.setText("Turn: " + (myTurn ? "Your turn" : "Other turn"));
		if(myTurn){
			other.activate();
			if(me.isEmpty()){
				me.random();
				updateOther.run();
			}
		}
	}

	/**
	 * Changes whether the GameBoard is running a single or multiplayer game
	 * @param singleGame - boolean which is true if this is a singleGame and
	 * false otherwise
	 */
	public void setSingleGame(boolean singleGame) {
		this.singleGame = singleGame;
	}
	
	/**
	 * Listens for the user typing on the keyboard
	 * @return KeyAdapter - Adapter which listens for the user typing on the
	 * keyboard and changes state accordingly
	 */
	private KeyAdapter boardKeyListener() {
		return new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (playing && myTurn && other.buttonPressComplete()) {
					boolean shifted = false;
					if (e.getKeyCode() == KeyEvent.VK_LEFT){
						shifted = me.shift(Shifter.LEFT);
					}
					else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
						shifted = me.shift(Shifter.RIGHT);
					}
					else if (e.getKeyCode() == KeyEvent.VK_DOWN){
						shifted = me.shift(Shifter.DOWN);
					}
					else if (e.getKeyCode() == KeyEvent.VK_UP){
						shifted = me.shift(Shifter.UP);
					}

					if(singleGame && shifted){
						me.random();
						if(me.gameOver()) {
							myScore.setText("Game over: "+me.getScore());
							playing = false;
						}
						else if(me.hasWon()){
							myScore.setText("You won: "+me.getScore());
						}
						else {
							myScore.setText("Your score: "+me.getScore());
						}
					}
					else if(!singleGame && shifted){
						if(me.gameOver() || other.hasWon()) {
							myScore.setText("You lose: "+me.getScore());
							playing = false;
						}
						if(other.gameOver() || me.hasWon()){
							myScore.setText("You won: "+me.getScore());
							playing = false;
						}
						else {
							myScore.setText("Your score: "+me.getScore());
							turn.setText("Turn: " + 
									(myTurn ? "Your turn" : "Other turn"));
									
							// end my turn if shifted and button press complete 
							// also reset button press
							endMyTurn();

						}
						updateOther.run();
					}

					// Repaints board to reflect change
					//repaint();
					me.repaint();
					other.repaint();
					myScore.repaint();
				}
			}
		};
	}
}
