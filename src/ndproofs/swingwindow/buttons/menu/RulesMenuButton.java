
package ndproofs.swingwindow.buttons.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import ndproofs.swingwindow.Printer;
import ndproofs.swingwindow.buttons.HotkeyButton;

/**
 *
 * @author Oh
 */
public class RulesMenuButton extends JMenuItem implements ActionListener, HotkeyButton {
    private final Printer printer;

    public RulesMenuButton(Printer printer) {
        this.printer = printer;
        setText("Rules");
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        trigger();
    }

    @Override
    public void trigger() {
        printer.dialogRules();
    }
    
}
