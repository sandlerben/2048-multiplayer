import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class DrawingPane extends JComponent {
  private final int width = 500;
  private final int height = 500;
  private Iterable<Shape> model;
  
  public DrawingPane(Iterable<Shape> shapes) {
    this.model = shapes;
  }
  
  
  @Override
  public void paintComponent(Graphics gc) {
      super.paintComponent(gc);
      for (Shape s : model) {
        s.draw(gc);
      }
  }
  
  // get the size of the drawing panel
  @Override
  public Dimension getPreferredSize() {
      return new Dimension(width, height);
  }
  
}
