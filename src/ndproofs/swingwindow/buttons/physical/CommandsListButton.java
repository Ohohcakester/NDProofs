
package ndproofs.swingwindow.buttons.physical;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import ndproofs.proof.LineMaker;
import ndproofs.swingwindow.Printer;
import ndproofs.swingwindow.buttons.HotkeyButton;

/**
 *
 * @author Oh
 */
public class CommandsListButton extends JButton implements ActionListener, HotkeyButton {
    private final Printer printer;
    private final LineMaker lineMaker;

    public CommandsListButton(Printer printer, LineMaker lineMaker) {
        this.printer = printer;
        this.lineMaker = lineMaker;
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        trigger();
    }

    @Override
    public void trigger() {
        printer.toggleCommands();
    }
    
}
