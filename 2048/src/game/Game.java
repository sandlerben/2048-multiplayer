package game;
/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

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
				board.singleGame = true;
				board.reset();
			}
		});

		final JButton join = new JButton("Multiplayer (Join)");
		join.addActionListener(new ActionListener() {
			// Action when player wants to join a game
			@Override
			public void actionPerformed(ActionEvent e) {

				final Client client = new Client();
				client.start();
				Network.register(client);
				
				client.addListener(new Listener() {
					// Receives data from other player
					// TODO probably make this the same for client and server
					@Override
					public void received (Connection connection, Object object) {
						if (object instanceof TileRequest) {
							TileRequest request = (TileRequest)object;
							System.out.println(request.row + ", " + request.col);
							board.addTile(request);
							
							// 0 indicates success
							connection.sendTCP(new Integer(0));
							return;
						}
						else if (object instanceof Integer) {
							System.out.println("Client got a code of " + (Integer)object +" from server");
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
					}
				});
				
				// Request the host information from the user.
				String input = (String)JOptionPane.showInputDialog(null, "Host:", "Connect to chat server", JOptionPane.QUESTION_MESSAGE,
						null, null, "localhost");
				if (input == null || input.trim().length() == 0) System.exit(1); //TODO figure out what this does
				final String host = input.trim();
				
				// Creates a new thread for the client to run on
				new Thread("Connect") {
					public void run () {
						try {
							client.connect(5000, host, Network.port);
						} catch (IOException e) {
							// TODO Make this do something
							e.printStackTrace();
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

				// Sets up standard multiplayer game
				board.singleGame = false;
				board.reset();
				board.repaint();
				
				board.addClient(client);
			}
		});

		final JButton host = new JButton("Multiplayer (Host)");
		host.addActionListener(new ActionListener() {
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

				server.addListener(new Listener() {
					// Receives data from other player
					@Override
					public void received (Connection connection, Object object) {
						if (object instanceof TileRequest) {
							TileRequest request = (TileRequest)object;
							System.out.println(request.row + ", " + request.col);
							board.addTile(request);

							// 0 indicates success
							connection.sendTCP(new Integer(0));
							return;
						}
						
						else if (object instanceof Integer) {
							System.out.println("Server got a code of " + (Integer)object +" from client");
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
					}
				});

				try {
					server.bind(Network.port);
					JOptionPane.showMessageDialog(frame, "You are hosting at: localhost");
				} catch (BindException e1) {
					JOptionPane.showMessageDialog(frame, "You are already hosting at: localhost");
				} 
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
				board.singleGame = false;
				board.reset();
				board.repaint();
				
				board.addServer(server);
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
		
		//TODO Something like this for buttons
//		chatFrame.setSendListener(new Runnable() {
//            public void run () {
//                    ChatMessage chatMessage = new ChatMessage();
//                    chatMessage.text = chatFrame.getSendText();
//                    client.sendTCP(chatMessage);
//            }
//    });
	}
}
