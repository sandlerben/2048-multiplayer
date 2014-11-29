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
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 * Grid class which stores the state of a 2048 grid
 */
public class Grid extends JPanel{
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

	private int[][] data;
	private int scoreCount;
	private boolean activated;
	JButton [][] buttons;

	public Grid() {
		data = new int[4][4];
		buttons = new JButton[4][4];
		scoreCount = 0;
		activated = false;
		
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		setLayout(new GridBagLayout());
		
		for (int r = 0; r < buttons.length; r++) {
			for (int c = 0; c < buttons[0].length; c++) {
				final GridButton b = new GridButton(r, c);
				buttons[r][c] = b;
				b.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(activated){
							data[b.getR()][b.getC()] = 2;
						}
					}
				});
				b.setFocusable(false);
				b.setPreferredSize(new Dimension(100, 100));
				b.setBorder(new LineBorder(Color.BLACK));
				b.setForeground(Color.WHITE);
				b.setIcon(new Icon() {				 
				      @Override
				      public void paintIcon(Component c, Graphics g, int x, int y) {
				        try{
				        	Color w = matchColor(Integer.parseInt(b.getText()));
				        	g.setColor(matchColor(Integer.parseInt(b.getText())));
				        	if(w.equals(two) || w.equals(four)) {
				        		b.setForeground(Color.BLACK);
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
				b.setFont(new Font("Arial", Font.PLAIN, 40));
                //b.setOpaque(true);
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
	
	public int getScore() {
		return scoreCount;
	}
	
	public boolean gameOver() {
		for (int r = 0; r < data.length; r++) {
			for (int c = 0; c < data[0].length; c++) {
				if (data[r][c] != 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void wipeGrid () {
		data = new int[4][4];
	}
	
	public void addValue (int value, int r, int c) throws IllegalArgumentException {
		if (data[r][c] != 0) {
			throw new IllegalArgumentException();
		}
		
		data[r][c] = value;
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
	
	public void shift (Shifter s) {
		// Ensures element is not merged twice
		boolean[][] alreadyMerged = new boolean[4][4];
		switch(s) {
		case DOWN:
			for(int r = data.length - 1; r >= 0; r--) {
				for (int c = 0; c<data[0].length; c++) {
					// Pulls down an element above r,c
					pullDown(alreadyMerged, r, c);
				}
			}
			break;
		case UP:
			for(int r = 0; r < data.length; r++) {
				for (int c = 0; c<data[0].length; c++) {
					// Pulls down an element above r,c
					pullUp(alreadyMerged, r, c);
				}
			}
			break;
		case LEFT:
			for(int c = 0; c < data[0].length; c++) {
				for (int r = 0; r<data.length; r++) {
					// Pulls down an element above r,c
					pullLeft(alreadyMerged, r, c);
				}
			}
			break;
		case RIGHT:
			for(int c = data[0].length - 1; c >= 0; c--) {
				for (int r = 0; r<data.length; r++) {
					// Pulls down an element above r,c
					pullRight(alreadyMerged, r, c);
				}
			}
		}
	}

	private void pullDown(boolean[][] alreadyMerged, int r, int c) {
		// Loop to pull elements above r,c down
		for(int r1 = r - 1; r1 >= 0; r1--){
			if(data[r1][c] != 0){
				if(data[r1][c] == data[r][c] && !alreadyMerged[r][c]){
					data[r][c] *= 2;
					scoreCount += data[r][c];
					data[r1][c] = 0;
					alreadyMerged[r][c] = true;
					break; 
				}
				// Else, if its not going to be merged
				else{
					// If r,c is not zero, place one before r,c
					if(data[r][c] != 0){
						int val = data[r1][c];
						data[r1][c] = 0;
						data[r - 1][c] = val;
						break; //necesary ?
					}
					// If r,c is zero, place in r,c
					else {
						data[r][c] = data[r1][c];
						data[r1][c] = 0;
					}
				}
			}
		}
	}
	
	private void pullUp(boolean[][] alreadyMerged, int r, int c) {
		// Loop to pull elements below r,c up
		for(int r1 = r + 1; r1 < data.length; r1++){
			if(data[r1][c] != 0){
				if(data[r1][c] == data[r][c] && !alreadyMerged[r][c]){
					data[r][c] *= 2;
					scoreCount += data[r][c];
					data[r1][c] = 0;
					alreadyMerged[r][c] = true;
					break; 
				}
				// Else, if its not going to be merged
				else{
					// If r,c is not zero, place one before r,c
					if(data[r][c] != 0){
						int val = data[r1][c];
						data[r1][c] = 0;
						data[r + 1][c] = val;
						break; //necesary ?
					}
					// If r,c is zero, place in r,c
					else {
						data[r][c] = data[r1][c];
						data[r1][c] = 0;
					}
				}
			}
		}
	}
	
	private void pullLeft(boolean[][] alreadyMerged, int r, int c) {
		// Loop to pull elements above r,c down
		for(int c1 = c + 1; c1 < data[0].length; c1++){
			if(data[r][c1] != 0){
				if(data[r][c1] == data[r][c] && !alreadyMerged[r][c]){
					data[r][c] *= 2;
					scoreCount += data[r][c];
					data[r][c1] = 0;
					alreadyMerged[r][c] = true;
					break; 
				}
				// Else, if its not going to be merged
				else{
					// If r,c is not zero, place one before r,c
					if(data[r][c] != 0){
						int val = data[r][c1];
						data[r][c1] = 0;
						data[r][c + 1] = val;
						break; //TODO:necesary ?
					}
					// If r,c is zero, place in r,c
					else {
						data[r][c] = data[r][c1];
						data[r][c1] = 0;
					}
				}
			}
		}
	}
	
	private void pullRight(boolean[][] alreadyMerged, int r, int c) {
		// Loop to pull elements above r,c down
		for(int c1 = c - 1; c1 >= 0; c1--){
			if(data[r][c1] != 0){
				if(data[r][c1] == data[r][c] && !alreadyMerged[r][c]){
					data[r][c] *= 2;
					scoreCount += data[r][c];
					data[r][c1] = 0;
					alreadyMerged[r][c] = true;
					break; 
				}
				// Else, if its not going to be merged
				else{
					// If r,c is not zero, place one before r,c
					if(data[r][c] != 0){
						int val = data[r][c1];
						data[r][c1] = 0;
						data[r][c - 1] = val;
						break; //TODO:necesary ?
					}
					// If r,c is zero, place in r,c
					else {
						data[r][c] = data[r][c1];
						data[r][c1] = 0;
					}
				}
			}
		}
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
