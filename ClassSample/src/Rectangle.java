import java.awt.Color;
import java.awt.Graphics;


public class Rectangle implements Shape {
  private final int x, y, w, h;
  
  public Rectangle(int x, int y, int w, int h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }

  @Override
  public void draw(Graphics gc) {
    gc.setColor(Color.blue);
    gc.drawRect(x, y, w, h);
  }
}
