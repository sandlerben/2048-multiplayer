package game;


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

	private Server server = null;
	private Client client = null;
	
	// Game constants
	public static final int COURT_WIDTH = 800;
	public static final int COURT_HEIGHT = 500;

	public GameBoard (final JLabel scoreLabel) {
		me = new Grid();
		other = new Grid();

		// add both boards to the board by default
		add(me, BorderLayout.CENTER);
		add(other, BorderLayout.CENTER);

		// enable keyboard focus on the game area. 
		setFocusable(true);

		// forwards directions to client Grid
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (playing) {
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
							scoreLabel.setText("Game over: "+me.getScore());
							playing = false;
						}
						else {
							scoreLabel.setText("Your score: "+me.getScore());
						}
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
}
