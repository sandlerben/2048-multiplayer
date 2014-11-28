import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Grid class which stores the state of a 2048 grid
 */
public class Grid extends JPanel{
	private int[][] data;
	private int scoreCount;
	JButton [][] buttons;

	public Grid() {
		data = new int[4][4];
		buttons = new JButton[4][4];
		scoreCount = 0;
		
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		setLayout(new GridLayout(4,4));
		
		for (int r = 0; r < buttons.length; r++) {
			for (int c = 0; c < buttons[0].length; c++) {
				final GridButton b = new GridButton(r, c);
				buttons[r][c] = b;
				b.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						data[b.getR()][b.getC()] = 2;
					}
				});
				b.setFocusable(false);
				add(b);
			}
		}
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