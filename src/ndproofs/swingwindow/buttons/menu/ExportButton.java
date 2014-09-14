
package ndproofs.swingwindow.buttons.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import ndproofs.proof.LineMaker;
import ndproofs.puzzle.FileIO;
import ndproofs.swingwindow.buttons.HotkeyButton;

/**
 *
 * @author Oh
 */
public class ExportButton extends JMenuItem implements ActionListener, HotkeyButton {
    private final LineMaker lineMaker;
    private final JFileChooser fileChooser;

    public ExportButton(LineMaker lineMaker) {
        setText("Export Proof");
        fileChooser = new JFileChooser();
        this.lineMaker = lineMaker;
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
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            file = FileIO.setFileExtension(file, ".txt");
            lineMaker.exportToFile(file);
        }
    }
    
}
