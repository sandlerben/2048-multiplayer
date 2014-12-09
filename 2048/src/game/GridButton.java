package game;
import javax.swing.JButton;

/**
 * Extends JButton and graphically represents a tile. Adds state about the 
 * tile's position on the 2048 grid so appropriate action is taken when it is 
 * pressed.
 */
@SuppressWarnings("serial")
public class GridButton extends JButton{
	int r;
	int c;
	
	public GridButton (int r, int c) {
		this.r = r;
		this.c = c;
	}
	
	public int getR() {
		return r;
	}
	public int getC() {
		return c;
	}
}
