/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

// imports necessary libraries for Java swing
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Game implements Runnable {
	@Override
	public void run() {
		Grid me = new Grid();
		Grid other = new Grid();
		
		// Top-level frame in which game components live
		final JFrame frame = new JFrame("Multiplayer 2048");
		frame.setLocation(0, 0);

		// Status panel
		final JPanel status_panel = new JPanel();
		frame.add(status_panel, BorderLayout.SOUTH);
		final JLabel status = new JLabel();
		status_panel.add(status);

		// Main playing area
		final GameBoard board = new GameBoard(status);
		frame.add(board, BorderLayout.CENTER);

		// Control panel
		final JPanel control_panel = new JPanel();
		frame.add(control_panel, BorderLayout.NORTH);

		final JButton start = new JButton("Single Player");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board.singleGame = true;
				board.reset();
			}
		});

		final JButton join = new JButton("Multiplayer (Join)");
		join.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String server = JOptionPane.showInputDialog(frame,
                        "What is the server?", null);
				board.singleGame = false;
				board.reset();
				board.repaint();
			}
		});
		
		final JButton host = new JButton("Multiplayer (Host)");
		host.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "You are hosting at: ");
				board.singleGame = false;
				board.reset();
				board.repaint();
			}
		});
		
		//status.setText("Your score: "+board.getMyScore()+". Their score: "+board.getOtherScore());
		control_panel.add(start);
		control_panel.add(host);
		control_panel.add(join);

		// Put the frame on the screen
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}

	/*
	 * Main method run to start and run the game Initializes the GUI elements
	 * specified in Game and runs it.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Game());
	}
}
