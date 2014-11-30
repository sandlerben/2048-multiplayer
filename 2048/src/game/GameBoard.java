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
 * GameBoard
 * 
 * This class contains the two 2048 boards and state about the games.
 * 
 */
@SuppressWarnings("serial")
public class GameBoard extends JPanel {
	private Grid me;
	private Grid other;

	public boolean playing = false; // whether the game is running
	public boolean singleGame = false; // whether this is singlePlayer game
	// TODO figure out what state should be public/private
	private boolean myTurn = true;

	private Server server = null;
	private Client client = null;
	
	// Game constants
	public static final int COURT_WIDTH = 800;
	public static final int COURT_HEIGHT = 500;
	
	private final JLabel myScore = new JLabel();
	private final JLabel otherScore = new JLabel();
	private final JLabel turn = new JLabel();

	public GameBoard (final JPanel status_panel) {
		me = new Grid();
		other = new Grid();

		// add both boards to the board by default
		add(me, BorderLayout.CENTER);
		add(other, BorderLayout.CENTER);

		// enable keyboard focus on the game area. 
		setFocusable(true);

		// Adds labels to status_panel
		if(singleGame){
			status_panel.add(myScore);
			myScore.setText("Your score: ");
		}
		else {
			status_panel.add(myScore);
			status_panel.add(otherScore);
			status_panel.add(turn);
			myScore.setText("Your score: ");
			otherScore.setText("Other score: ");
		}

		// forwards directions to client Grid
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (playing && myTurn) {
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
						else {
							myScore.setText("Your score: "+me.getScore());
						}
					}
					else if(!singleGame && shifted){
						if(me.gameOver()) {
							myScore.setText("You lose: "+me.getScore());
							playing = false;
						}
						else {
							myScore.setText("Your score: "+me.getScore());
						}
						
						new Runnable() {
	                        public void run () {
	                        	if(client != null){
									Score score = new Score();
									score.value = me.getScore();
									server.sendToAllTCP(score);
									client.sendTCP(me.data);
								}
								else if (server != null){
									Score score = new Score();
									score.value = me.getScore();
									server.sendToAllTCP(score);
									server.sendToAllTCP(me.data);
								}
	                        }
						}.run();
					}

					// Repaints board to reflect change
					repaint();
				}
			}
		});

	}

	// Resets the game board(s) to begin a single or multiplayer game
	public void reset() {
		playing = true;
		me.activate();
		me.wipeGrid();
		me.touch = false; // Sets whether the tiles should be clickable 
		if(singleGame){
			me.random();
			remove(other);
		}
		else {
			other.touch = true;
			add(other);
			other.activate();
			other.wipeGrid();
		}
		requestFocusInWindow();
		repaint();
	}
	
	public void addServer(Server server) {
		this.server = server;
		other.server = server;
	}
	
	public void addClient(Client client) {
		this.client = client;
		other.client = client;
	}
	
	public void addTile(TileRequest request) {
		me.addValue(request.row, request.col, request.value);
	}
	
	public void updateOtherData(int[][] otherData) {
		other.data = otherData;
		if(other.gameOver()) {
			myScore.setText("You win: "+me.getScore());
			playing = false;
		}
		else {
			otherScore.setText("Other score: "+other.getScore());
		}
	}
	
	public void updateOtherScore(Score score) {
		other.scoreCount = score.value;
	}
}
