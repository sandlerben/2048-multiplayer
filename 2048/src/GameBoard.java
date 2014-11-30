

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * GameBoard
 * 
 * This class contains the two 2048 boards.
 * 
 */
public class GameBoard extends JPanel {
	private Grid me;
	private Grid other;

	public boolean playing = false; // whether the game is running
	public boolean singleGame = false; // whether this is singlePlayer game

	// Game constants
	public static final int COURT_WIDTH = 800;
	public static final int COURT_HEIGHT = 500;

	public GameBoard () {
		me = new Grid();
		other = new Grid();

		add(me, BorderLayout.CENTER);
		add(other, BorderLayout.CENTER);

		// Enable keyboard focus on the game area. 
		setFocusable(true);

		// Forwards directions to Grid 'me'
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT){
					me.shift(Shifter.LEFT);
					if(singleGame){
						me.random();
					}
				}
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
					me.shift(Shifter.RIGHT);
					if(singleGame){
						me.random();
					}
				}
				else if (e.getKeyCode() == KeyEvent.VK_DOWN){
					me.shift(Shifter.DOWN);
					if(singleGame){
						me.random();
					}
				}
				else if (e.getKeyCode() == KeyEvent.VK_UP){
					me.shift(Shifter.UP);
					if(singleGame){
						me.random();
					}
				}

				// Repaints board to reflect change
				repaint();
			}
		});

	}

	public void reset() {
		playing = true;
		me.activate();
		me.wipeGrid();
		me.touch = false;
		if(!singleGame){
			other.touch = true;
			add(other);
			other.activate();
			other.wipeGrid();
		}
		else {
			remove(other);
		}
		requestFocusInWindow();
		repaint();
	}
	
	public int getMyScore() {
		return me.getScore();
	}
	
	public int getOtherScore() {
		return other.getScore();
	}

	//	@Override
	//	public void paintComponent(Graphics g) {
	//		super.paintComponent(g);
	//		final JPanel me_panel = new JPanel();
	//		final JPanel other_panel = new JPanel();
	//	}
}
