
package ndproofs.swingwindow.buttons.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import ndproofs.puzzle.FileIO;
import ndproofs.puzzle.PuzzlePackReader;
import ndproofs.swingwindow.buttons.HotkeyButton;
import ndproofs.swingwindow.main.NDSwingWindow;

/**
 *
 * @author Oh
 */
public class SaveAsButton extends JMenuItem implements ActionListener, HotkeyButton {
    private final JFileChooser fileChooser;
    private final PuzzlePackReader puzzlePackReader;

    public SaveAsButton(PuzzlePackReader puzzlePackReader) {
        setText("Save As...");
        fileChooser = new JFileChooser();
        this.puzzlePackReader = puzzlePackReader;
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
        if (puzzlePackReader.noPuzzlePackLoaded()) {
            NDSwingWindow.showErrorDialog("No puzzle pack loaded.", "Unable to save file");
            return;
        }
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!FileIO.hasFileExtension(file)) {
                file = FileIO.setFileExtension(file, ".ndpack");
            }
            boolean okToSave = true;
            if (file.exists()) {
                if (!NDSwingWindow.showConfirmDialog(file.getName() + " already exists.\nDo you want to replace it?", "Confirm Overwrite")) {
                    okToSave = false;
                }
            }
            if (okToSave) {
                puzzlePackReader.saveAs(file);
            }
        }
    }
    
}
