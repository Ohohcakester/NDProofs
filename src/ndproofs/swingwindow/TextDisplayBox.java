package ndproofs.swingwindow;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JTextArea;

public class TextDisplayBox extends JTextArea{
    LayerLineDrawer layerLineDrawer;
    
    public TextDisplayBox(LayerLineSet layerLineSet) {
        super();
        layerLineDrawer = new LayerLineDrawerConsole(layerLineSet);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.BLACK);
        layerLineDrawer.drawLines(g);
        //g.drawLine(21, 0, 21, 50);
        /*g.fillRect(21, 8, 2, 20);
        g.fillRect(29, 8, 2, 10);
        g.fillRect(45, 30, 24, 2);
        g.fillRect(45, 42, 24, 2);*/
    }
    
    
}
