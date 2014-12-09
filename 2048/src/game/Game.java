package game;

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
 * Game Main class that specifies the frame of the GUI
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

		final JButton help = new JButton("Explanation");
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

	/**
	 * Main method run to start and run the game Initializes the GUI elements
	 * specified in Game and runs it.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Game());
	}

	/**
	 * Creates window with information when help button is pressed
	 * @param frame - JFrame which is the game frame. 
	 * @return ActionListener which handles the response when the help button 
	 * is pressed
	 */
	private ActionListener helpButtonListener(final JFrame frame) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = "Gameplay:<br>"
						+ "The goal of 2048 is to use the arrow keys to "
						+ "combine tiles and reach 2048. Tiles slide as far as "
						+ "possible in the chosen direction until they are "
						+ "stopped by either another tile or the edge of the "
						+ "grid. If two tiles of the same number collide while "
						+ "moving, they will merge into a tile with the total "
						+ "value of the two colliding tiles that collided."
						+ "The resulting tile cannot merge with another tile "
						+ "in the same turn."
						+ "In single player mode, a new tile will randomly "
						+ "appear every turn in an empty spot with a value of "
						+ "either 2 or 4. <br>"
						+ "In multiplayer mode, tiles don't show up randomly. "
						+ "Instead, your opponent chooses where tiles "
						+ "show up. When it is your turn, choose a spot "
						+ "for a tile to show up on your opponents board and "
						+ "then use your arrow keys to shift your tiles. "
						+ "<br><br>Layout:<br>"
						+ "The board on the left is your board and the board "
						+ "on the right is your opponent's. The score and "
						+ "current turn is presented below the boards."
						+ "<br><br>Controls:<br>"
						+ "Keyboard controls are used in both single and "
						+ "multiplayer modes. When you select one of the four "
						+ "arrow keys, the tiles will shift in the selected "
						+ "direction. Click control is used in multiplayer "
						+ "mode. Once a turn, select a single tile on your "
						+ "opponent's board that will be filled with a new "
						+ "tile. You can only select spots that are empty."
						+ "<br><br>Cool features:<br><ul>"
						+ "<li>Networking</li>"
						+ "<li>Multiplayer</li>"
						+ "<li>Dynamic color changing</li>"
						+ "<li>Interaction between multiplayer and networking"
						+ "</li></ul>";

				JOptionPane.showMessageDialog(frame, 
						"<html><body><p style='width: 300px;'>"
								+ message+"</body></html>");
			}
		};
	}
	
	/**
	 * Handles incoming network requests for both server and client
	 * @param board - The GameBoard which includes both 2048 grids and handles
	 * the game state
	 * @return Listener which listens for incoming network requests
	 */
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

	/**
	 * Handles action when player wants to join a game
	 * @param frame - The game frame
	 * @param board - The GameBoard which includes both 2048 grids and handles
	 * the game state
	 * @return ActionListener which provides a response when the joinButton
	 * is clicked
	 */
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
						"Host:", "", 
						JOptionPane.QUESTION_MESSAGE, null, null, "localhost");
				final String host = input.trim();

				// Creates a new thread for the client to run on
				new Thread("Connect") {
					public void run () {
						try {
							client.connect(5000, host, Network.port);
						} catch (IOException e) {
							e.printStackTrace();
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

	/**
	 * Handles action when player wants to host a game
	 * @param frame - The game frame
	 * @param board - The GameBoard which includes both 2048 grids and handles
	 * the game state
	 * @return ActionListener which provides a response when the hostButton
	 * is clicked
	 */
	private ActionListener hostButtonListener(final JFrame frame,
			final GameBoard board) {
		return new ActionListener() {
			// Action when player wants to host a game
			@Override
			public void actionPerformed(ActionEvent e) {

				final Server server = new Server();

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
					JOptionPane.showMessageDialog(frame, 
							"You are hosting at: localhost" + address);
				} catch (BindException e1) {
					JOptionPane.showMessageDialog(frame, 
							"You are already hosting at: localhost" + address);
				} 

				catch (IOException e1) {
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
