package game;
import game.Network.TileRequest;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

/**
 * Grid class which stores the state of a 2048 grid
 */
public class Grid extends JPanel{
	/* Tile Colors */
	private static final Color two = Color.decode("#eee4da");
	private static final Color four = Color.decode("#ede0c8");
	private static final Color eight = Color.decode("#f2b179");
	private static final Color sixteen = Color.decode("#f59563");
	private static final Color thirtytwo = Color.decode("#f67c5f");
	private static final Color sixtyfour = Color.decode("#f65e3b");
	private static final Color onetwentyeight = Color.decode("#edcf72");
	private static final Color twofiftysix = Color.decode("#edcc61");
	private static final Color fivetwelve = Color.decode("#edc850");
	private static final Color tentwentyfour = Color.decode("#edc53f");
	private static final Color twentyfourtyeight = Color.decode("#edc22e");

	public int[][] data; // stores state of grid
	public int scoreCount;
	private boolean activated;
	JButton [][] buttons;
	Server server;
	Client client;
	
	public boolean touch;

	public Grid() {
		data = new int[4][4];
		buttons = new JButton[4][4];
		scoreCount = 0;
		activated = false;
		
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setBackground(Color.decode("#BBADA0"));
		setLayout(new GridBagLayout());
		
		// Adds 16 buttons to grid
		for (int r = 0; r < buttons.length; r++) {
			for (int c = 0; c < buttons[0].length; c++) {
				final GridButton b = new GridButton(r, c);
				buttons[r][c] = b;
				b.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(activated && touch){ 
							data[b.getR()][b.getC()] = 2;
							
							new Runnable() {
		                        public void run () {
		                        	if(client != null){
		                        		TileRequest r = new TileRequest();
		                        		r.row = b.r;
		                        		r.col = b.c;
		                        		r.value = 2;
										client.sendTCP(r);
									}
									else if (server != null){
										TileRequest r = new TileRequest();
		                        		r.row = b.r;
		                        		r.col = b.c;
		                        		r.value = 2;
										server.sendToAllTCP(r);
									}
		                        }
							}.run();
						}
					}
				});
				b.setFocusable(false);
				b.setPreferredSize(new Dimension(100, 100));
				b.setForeground(Color.WHITE);
				b.setIcon(new Icon() {				 
				      @Override
				      public void paintIcon(Component c, Graphics g, int x, int y) {
				        try{
				        	Color w = matchColor(Integer.parseInt(b.getText()));
				        	g.setColor(matchColor(Integer.parseInt(b.getText())));
				        	if(w.equals(two) || w.equals(four)) {
				        		b.setForeground(Color.decode("#776E65"));
				        	}
				        	else{
				        		b.setForeground(Color.WHITE);
				        	}
				        } catch(NumberFormatException e){
				        	g.setColor(Color.decode("#CCC0B3"));
				        }
				        g.fillRect(0, 0, c.getWidth(), c.getHeight());
				      }
				 
				      @Override
				      public int getIconWidth() {
				        return 0;
				      }
				 
				      @Override
				      public int getIconHeight() {
				        return 0;
				      }
				    });
				b.setFont(new Font("Helvetica Neue", Font.BOLD, 40));
				GridBagConstraints con = new GridBagConstraints();
				con.gridx = c;
				con.gridy = r;
				con.insets = new Insets(10,10,10,10);
				add(b, con);
			}
		}
	}
	
	public static Color matchColor(int n){
		switch(n){
		case 2:
			return two;
		case 4:
			return four;
		case 8:
			return eight;
		case 16:
			return sixteen;
		case 32:
			return thirtytwo;
		case 64:
			return sixtyfour;
		case 128:
			return onetwentyeight;
		case 256:
			return twofiftysix;
		case 512:
			return fivetwelve;
		case 1024:
			return tentwentyfour;
		default:
			return twentyfourtyeight;
		}
			
	}
	
	public void activate() {
		activated = true;
	}
	
	public void deactivate() {
		activated = false;
	}
	
	public void wipeGrid () {
		data = new int[4][4];
		scoreCount = 0;
	}
	
	public int getScore() {
		return scoreCount;
	}
	
	// Checks if the grid is full
	public boolean isFull() {
		for (int r = 0; r < data.length; r++) {
			for (int c = 0; c < data[0].length; c++) {
				if (data[r][c] == 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	// Checks if the game is over. 
	// True if the grid is full and there are no tiles which can
	// be merged.
	public boolean gameOver() {
		for (int r = 0; r < data.length; r++) {
			for (int c = 0; c < data[0].length - 1; c++) {
				if (data[r][c] == 0 || data[r][c+1] == 0) {
					return false;
				}
				if (data[r][c] == data[r][c+1]) {
					return false;
				}
			}
		}
		for (int c = 0; c < data[0].length; c++) {
			for (int r = 0; r < data.length - 1; r++) {
				if (data[r][c] == data[r+1][c]) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void addValue (int r, int c, int value) throws IllegalArgumentException {
		if (data[r][c] != 0) {
			throw new IllegalArgumentException();
		}
		
		data[r][c] = value;
	}
	
	public void random () {
		boolean complete = false;
		if(!isFull()) {
			while(!complete){ 
				int r = (int)(Math.random()*4); 
				int c = (int)(Math.random()*4); 
				if(data[r][c] == 0){
					data[r][c] = 2*(1+(int)(Math.random()*2));
					complete = true;
				}
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int r = 0; r < buttons.length; r++) {
			for (int c = 0; c < buttons[0].length; c++) {
				buttons[r][c].setText(data[r][c] != 0 ? ""+data[r][c] : "");
			}
		}
	}
	
	public boolean shift (Shifter s) {
		// Ensures element is not merged twice
		boolean[][] alreadyMerged = new boolean[4][4];
		boolean shifted = false;
		switch(s) {
		case DOWN:
			for(int r = data.length - 1; r >= 0; r--) {
				for (int c = 0; c<data[0].length; c++) {
					// Pulls down an element above r,c
					if(pullDown(alreadyMerged, r, c)) {
						shifted = true;
					}
				}
			}
			break;
		case UP:
			for(int r = 0; r < data.length; r++) {
				for (int c = 0; c<data[0].length; c++) {
					// Pulls down an element above r,c
					if (pullUp(alreadyMerged, r, c)) {
						shifted = true;
					}
				}
			}
			break;
		case LEFT:
			for(int c = 0; c < data[0].length; c++) {
				for (int r = 0; r<data.length; r++) {
					// Pulls down an element above r,c
					if (pullLeft(alreadyMerged, r, c)) {
						shifted = true;
					}
				}
			}
			break;
		case RIGHT:
			for(int c = data[0].length - 1; c >= 0; c--) {
				for (int r = 0; r<data.length; r++) {
					// Pulls down an element above r,c
					if (pullRight(alreadyMerged, r, c)) {
						shifted = true;
					}
				}
			}
		}
		return shifted;
	}

	private boolean pullDown(boolean[][] alreadyMerged, int r, int c) {
		// Loop to pull elements above r,c down
		boolean shifted = false;
		for(int r1 = r - 1; r1 >= 0; r1--){
			if(data[r1][c] != 0){
				if(data[r1][c] == data[r][c] && !alreadyMerged[r][c]){
					data[r][c] *= 2;
					scoreCount += data[r][c];
					data[r1][c] = 0;
					alreadyMerged[r][c] = true;
					shifted = true;
					break; 
				}
				// Else, if its not going to be merged
				else{
					// If r,c is not zero, place one before r,c
					if(data[r][c] != 0){
						int val = data[r1][c];
						data[r1][c] = 0;
						data[r - 1][c] = val;
						shifted = true;
						break; 
					}
					// If r,c is zero, place in r,c
					else {
						data[r][c] = data[r1][c];
						data[r1][c] = 0;
						shifted = true;
					}
				}
			}
		}
		return shifted;
	}
	
	private boolean pullUp(boolean[][] alreadyMerged, int r, int c) {
		// Loop to pull elements below r,c up
		boolean shifted = false;
		for(int r1 = r + 1; r1 < data.length; r1++){
			if(data[r1][c] != 0){
				if(data[r1][c] == data[r][c] && !alreadyMerged[r][c]){
					data[r][c] *= 2;
					scoreCount += data[r][c];
					data[r1][c] = 0;
					alreadyMerged[r][c] = true;
					shifted = true;
					break; 
				}
				// Else, if its not going to be merged
				else{
					// If r,c is not zero, place one before r,c
					if(data[r][c] != 0){
						int val = data[r1][c];
						data[r1][c] = 0;
						data[r + 1][c] = val;
						shifted = true;
						break; 
					}
					// If r,c is zero, place in r,c
					else {
						data[r][c] = data[r1][c];
						data[r1][c] = 0;
						shifted = true;
					}
				}
			}
		}
		return shifted;
	}
	
	private boolean pullLeft(boolean[][] alreadyMerged, int r, int c) {
		// Loop to pull elements above r,c down
		boolean shifted = false;
		for(int c1 = c + 1; c1 < data[0].length; c1++){
			if(data[r][c1] != 0){
				if(data[r][c1] == data[r][c] && !alreadyMerged[r][c]){
					data[r][c] *= 2;
					scoreCount += data[r][c];
					data[r][c1] = 0;
					alreadyMerged[r][c] = true;
					shifted = true;
					break; 
				}
				// Else, if its not going to be merged
				else{
					// If r,c is not zero, place one before r,c
					if(data[r][c] != 0){
						int val = data[r][c1];
						data[r][c1] = 0;
						data[r][c + 1] = val;
						shifted = true;
						break; 
					}
					// If r,c is zero, place in r,c
					else {
						data[r][c] = data[r][c1];
						data[r][c1] = 0;
						shifted = true;
					}
				}
			}
		}
		return shifted;
	}
	
	private boolean pullRight(boolean[][] alreadyMerged, int r, int c) {
		// Loop to pull elements above r,c down
		boolean shifted = false;
		for(int c1 = c - 1; c1 >= 0; c1--){
			if(data[r][c1] != 0){
				if(data[r][c1] == data[r][c] && !alreadyMerged[r][c]){
					data[r][c] *= 2;
					scoreCount += data[r][c];
					data[r][c1] = 0;
					alreadyMerged[r][c] = true;
					shifted = true;
					break; 
				}
				// Else, if its not going to be merged
				else{
					// If r,c is not zero, place one before r,c
					if(data[r][c] != 0){
						int val = data[r][c1];
						data[r][c1] = 0;
						data[r][c - 1] = val;
						shifted = true;
						break; 
					}
					// If r,c is zero, place in r,c
					else {
						data[r][c] = data[r][c1];
						data[r][c1] = 0;
						shifted = true;
					}
				}
			}
		}
		return shifted;
	}

	@Override
	public String toString() {
		String str = "";
		for (int r = 0; r<data.length; r++) {
			str = str + Arrays.toString(data[r]) + "\n";
		}
		return str;
	}
}
