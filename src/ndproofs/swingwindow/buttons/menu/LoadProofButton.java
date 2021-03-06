
package ndproofs.swingwindow.buttons.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import ndproofs.proof.LineMaker;
import ndproofs.swingwindow.Printer;
import ndproofs.swingwindow.buttons.HotkeyButton;

/**
 *
 * @author Oh
 */
public class LoadProofButton extends JMenuItem implements ActionListener, HotkeyButton {
    private final JFileChooser fileChooser;
    private final LineMaker lineMaker;
    private final Printer printer;

    public LoadProofButton(LineMaker lineMaker, Printer printer) {
        setText("Import Proof");
        fileChooser = new JFileChooser();
        this.lineMaker = lineMaker;
        this.printer = printer;
        try {
            // Set current directory to default
            File f = new File(new File(".").getCanonicalPath());
            fileChooser.setCurrentDirectory(f);
        } catch (IOException ex) {
        }
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        trigger();
    }

    @Override
    public void trigger() {
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            printer.refreshBefore();
            lineMaker.importFromFile(file);
            printer.refreshAfter();
        }
    }
    
}
