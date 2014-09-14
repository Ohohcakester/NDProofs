
package ndproofs.swingwindow.levelselect;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author Oh
 */
class DoubleClickSelect implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            NDLevelSelect.selectProofButton.trigger();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
}
