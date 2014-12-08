package game;
import javax.swing.JButton;


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
