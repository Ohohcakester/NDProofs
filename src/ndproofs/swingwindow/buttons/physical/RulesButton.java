
package ndproofs.swingwindow.buttons.physical;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import ndproofs.swingwindow.Printer;
import ndproofs.swingwindow.buttons.HotkeyButton;

/**
 *
 * @author Oh
 */
public class RulesButton extends JButton implements ActionListener, HotkeyButton {
    private final Printer printer;

    public RulesButton(Printer printer) {
        this.printer = printer;
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        trigger();
    }

    @Override
    public void trigger() {
        printer.toggleRules();
    }
    
}
