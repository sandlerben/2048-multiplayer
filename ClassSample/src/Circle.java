import java.awt.Color;
import java.awt.Graphics;


public class Circle implements Shape {
  private int x;
  private int y;
  private int radius;
  
  public Circle(int x, int y, int radius) {
    this.x = x;
    this.y = y;
    this.radius = radius;
  }
  
  @Override
  public void draw(Graphics gc) {
    gc.setColor(Color.GREEN);
    gc.fillOval(x, y, radius, radius);
  }

}
