
package ndproofs.swingwindow.buttons.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import ndproofs.puzzle.InbuiltPuzzles;
import ndproofs.swingwindow.buttons.HotkeyButton;
import ndproofs.swingwindow.main.NDSwingWindow;

/**
 *
 * @author Oh
 */
public class FreeModeButton extends JMenuItem implements ActionListener, HotkeyButton {
    private final InbuiltPuzzles inbuiltPuzzles;

    public FreeModeButton(InbuiltPuzzles inbuiltPuzzles) {
        setText("Free Mode");
        this.inbuiltPuzzles = inbuiltPuzzles;
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        trigger();
    }

    @Override
    public void trigger() {
        if (inbuiltPuzzles.alreadyFreeMode()) {
            return;
        }
        if (NDSwingWindow.showConfirmDialog("Switch to Free Mode?\n Your progress will no longer be recorded", "Confirmation")) {
            inbuiltPuzzles.freeMode(true);
        }
    }
    
}
