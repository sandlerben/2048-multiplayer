package game;

// imports necessary libraries for Java swing
import game.ChatServer2.ChatConnection;
import game.Network.Score;
import game.Network.TileRequest;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Game implements Runnable {
	@Override
	public void run() {

		// Top-level frame in which game components live
		final JFrame frame = new JFrame("Multiplayer 2048");

		// Status panel
		final JPanel status_panel = new JPanel();
		frame.add(status_panel, BorderLayout.SOUTH);

		// Main playing area
		final GameBoard board = new GameBoard(status_panel);
		frame.add(board, BorderLayout.CENTER);

		// Control panel
		final JPanel control_panel = new JPanel();
		frame.add(control_panel, BorderLayout.NORTH);

		final JButton start = new JButton("Single Player");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board.setSingleGame(true);
				board.reset();
			}
		});

		final JButton help = new JButton("Help");
		help.addActionListener(helpButtonListener(frame));

		final JButton join = new JButton("Multiplayer (Join)");
		join.addActionListener(joinButtonListener(frame, board));

		final JButton host = new JButton("Multiplayer (Host)");
		host.addActionListener(hostButtonListener(frame, board));

		control_panel.add(start);
		control_panel.add(host);
		control_panel.add(join);
		control_panel.add(help);

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

	private ActionListener helpButtonListener(final JFrame frame) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = "Multiplayer 2048 is simple! Unlike in "
						+ "regular 2048, tiles don't show up randomly. "
						+ "Instead, your opponent chooses where tiles "
						+ "show up. When it is your turn, choose a spot "
						+ "for a tile to show up on your opponents board and "
						+ "then use your arrow keys to shift your tiles.";

				JOptionPane.showMessageDialog(frame, 
						"<html><body><p style='width: 200px;'>"
								+ message+"</body></html>");
			}
		};
	}

	public Listener connectionListener (final GameBoard board) {
		return new Listener() {
			// Receives data from other player
			@Override
			public void received (Connection connection, Object object){
				if (object instanceof TileRequest) {
					TileRequest request = (TileRequest)object;							
					board.addTile(request);
					return;
				}

				else if (object instanceof Score) {
					Score score = (Score)object;
					board.updateOtherScore(score);
					return;
				} 

				else if (object instanceof int[][]) {
					int[][] otherData = (int[][])object;
					board.updateOtherData(otherData);
					return;
				} 

				else if (object instanceof Boolean) {
					Boolean otherTurn = (Boolean)object; 
					board.setMyTurn(!otherTurn.booleanValue());
					return;
				} 

			}
		};
	}

	private ActionListener joinButtonListener(final JFrame frame,
			final GameBoard board) {
		return new ActionListener() {
			// Action when player wants to join a game
			@Override
			public void actionPerformed(ActionEvent e) {

				final Client client = new Client();
				client.start();
				Network.register(client);

				client.addListener(connectionListener(board));

				// Request the host information from the user.
				String input = (String)JOptionPane.showInputDialog(null, 
						"Host:", "Connect to chat server", 
						JOptionPane.QUESTION_MESSAGE, null, null, "localhost");
				final String host = input.trim();

				// Creates a new thread for the client to run on
				new Thread("Connect") {
					public void run () {
						try {
							client.connect(5000, host, Network.port);
						} catch (IOException e) {
							// Handle connection errors
							JOptionPane.showMessageDialog(frame, 
									"Could not connect. Please try again.", 
									"Error", JOptionPane.ERROR_MESSAGE);
							
						}
					}
				}.start();

				frame.addWindowListener(new WindowAdapter() {
					// Close client when window is closed
					@Override
					public void windowClosed (WindowEvent evt) {
						client.stop();
					}
				});

				// Client determines who begins
				final boolean myTurn;
				int r = 1 + (int)(Math.random()*2);
				if(r == 1){
					myTurn = true;
				}
				else{
					myTurn = false;
				}

				try {
					// Must sleep very briefly before sending TCP data
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// Handle connection errors
					JOptionPane.showMessageDialog(frame, 
							"Could not connect. Please try again.", 
							"Error", JOptionPane.ERROR_MESSAGE);
				}

				new Runnable() {
					public void run () {
						client.sendTCP(new Boolean(myTurn));
					}

				}.run();

				// Sets up standard multiplayer game
				board.setSingleGame(false);
				board.reset();
				board.repaint();
				board.setMyTurn(myTurn);

				board.addClient(client);
			}
		};
	}

	private ActionListener hostButtonListener(final JFrame frame,
			final GameBoard board) {
		return new ActionListener() {
			// Action when player wants to host a game
			@Override
			public void actionPerformed(ActionEvent e) {

				final Server server = new Server() {
					@Override
					protected ChatConnection newConnection () {
						return new ChatConnection();
					}
				};

				Network.register(server);

				server.addListener(connectionListener(board));
				String address;
				try {
					address = " and "+InetAddress.getLocalHost().getHostAddress();

				} catch (UnknownHostException e2) {
					address = "";
				}
				try {
					server.bind(Network.port);
					JOptionPane.showMessageDialog(frame, "You are hosting at: localhost" + address);
				} catch (BindException e1) {
					JOptionPane.showMessageDialog(frame, "You are already hosting at: localhost" + address);
				} 

				//TODO bug where the you are hosting dialogue is up and random is never run, deal with that
				catch (IOException e1) {
					// TODO Actually handle this
					e1.printStackTrace();
				}
				server.start();

				frame.addWindowListener(new WindowAdapter() {
					// Close server when window is closed
					@Override
					public void windowClosed (WindowEvent evt) {
						server.stop();
					}
				});

				// Sets up standard multiplayer game
				board.setSingleGame(false);
				board.reset();
				board.repaint();

				board.addServer(server);
			}
		};
	}


}
