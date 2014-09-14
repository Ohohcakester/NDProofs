package ndproofs.swingwindow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import ndproofs.swingwindow.clickmode.ClickModeButtons;


public class SelectableTextDisplay<E> extends JList<E> implements MouseListener{
    LayerLineDrawer layerLineDrawer;
    public ClickModeButtons commandButtonPanel; // Uninitialized
    
    public SelectableTextDisplay(ListModel<E> listModel, LayerLineSet layerLineSet) {
        super(listModel);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addMouseListener(this);
        //addListSelectionListener(this);
        layerLineDrawer = new LayerLineDrawerClick(layerLineSet);
    }
    
    /*@Override
    public void valueChanged(ListSelectionEvent e) {
        //if (e.getValueIsAdjusting() == false)
        //    select(getSelectedIndex());
    }*/
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.BLACK);
        layerLineDrawer.drawLines(g);
        /*g.fillRect(20, 7, 2, 22);
        g.fillRect(28, 7, 2, 26);
        g.fillRect(44, 29, 24, 2);
        g.fillRect(44, 41, 24, 2);*/
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int index = locationToIndex(e.getPoint());
        select(index);
    }
    
    private void select(int index) {
        commandButtonPanel.lineSelected(index+1);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    
    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}