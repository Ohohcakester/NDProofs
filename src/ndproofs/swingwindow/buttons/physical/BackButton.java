
package ndproofs.swingwindow.buttons.physical;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import ndproofs.swingwindow.buttons.HotkeyButton;
import ndproofs.swingwindow.main.NDSwingWindow;

/**
 *
 * @author Oh
 */
public class BackButton extends JButton implements ActionListener, HotkeyButton {
    
    public BackButton() {
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        trigger();
    }

    @Override
    public void trigger() {
        NDSwingWindow.mainFrame.setContentPane(NDSwingWindow.defaultContentPane);
        NDSwingWindow.mainFrame.validate();
        NDSwingWindow.setFocusOnConsoleInput();
    }
    
}
