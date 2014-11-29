

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
				}
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
					me.shift(Shifter.RIGHT);
				}
				else if (e.getKeyCode() == KeyEvent.VK_DOWN){
					me.shift(Shifter.DOWN);
				}
				else if (e.getKeyCode() == KeyEvent.VK_UP){
					me.shift(Shifter.UP);
				}

				// Repaints board to reflect change
				repaint();
			}
		});

	}

	public void reset() {
		playing = true;
		me.activate();
		other.activate();
		me.wipeGrid();
		other.wipeGrid();
		requestFocusInWindow();
		repaint();
	}

	//	@Override
	//	public void paintComponent(Graphics g) {
	//		super.paintComponent(g);
	//		final JPanel me_panel = new JPanel();
	//		final JPanel other_panel = new JPanel();
	//	}
}
