import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class DrawingExample implements Runnable {

    private List<Shape> shapes;
    private DrawingPane view;
  
    public DrawingExample() {
      this.shapes= new ArrayList<Shape>();
      this.view = new DrawingPane(shapes);
    }
    
    public static void main(String[] args) {
        // The proper way to create a top-level window:
        //   - make a class that implements Runnable
        //   - the 'run' method creates the actual window
        //   - SwingUtilities.invokeLater calls the 'run'
        //     method after initializing the program state. 
        SwingUtilities.invokeLater(new DrawingExample());
    }

    private static int randomInt(int bound) {
      return (int)(Math.random() * bound); 
    }
    
    @Override
    public void run() {             
        // create the top-level window
        JFrame frame = new JFrame("Drawing Example");
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        frame.add(p);
        
        // create a button to add a rectangle
        JButton bRect = new JButton("Rect");
        bRect.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            shapes.add(new Rectangle(randomInt(500), randomInt(500), 100, 50));
            view.repaint();
          }
        });
        
        // create a button to add a line
        JButton bLine = new JButton("Line");
        bLine.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            shapes.add(new Line(randomInt(500), randomInt(500), 
                                randomInt(500), randomInt(500)));
            view.repaint();
          }
        });
        
        // create a button to add a circle
        JButton bCircle = new JButton("Circle");
        bCircle.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            shapes.add(new Circle(randomInt(500), randomInt(500), 60));
            view.repaint();
          }
        });
        
        // Position the view and buttons
        p.add(view, BorderLayout.NORTH);
        JPanel q = new JPanel();
        p.add(q, BorderLayout.SOUTH);
        q.add(bRect);
        q.add(bLine);
        q.add(bCircle);
        
        // pack sets the size of the frame automatically
        // based on the sub-components' preferred sizes
        frame.pack();
        frame.setVisible(true);
        
        // make sure to end the program when the window is 
        // closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}