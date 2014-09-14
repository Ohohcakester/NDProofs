
package ndproofs.swingwindow.buttons.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import ndproofs.proof.LineMaker;
import ndproofs.swingwindow.buttons.HotkeyButton;
import ndproofs.swingwindow.main.NDSwingWindow;

/**
 *
 * @author Oh
 */
public class ClearAllButton extends JMenuItem implements ActionListener, HotkeyButton {
    private final LineMaker lineMaker;

    public ClearAllButton(LineMaker lineMaker) {
        setText("Clear All");
        this.lineMaker = lineMaker;
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        trigger();
    }

    @Override
    public void trigger() {
        if (NDSwingWindow.showConfirmDialog("Clear current progress?\nThis only clears your current proof.", "Clear All")) {
            lineMaker.clearCurrentProof();
        }
    }
    
}
