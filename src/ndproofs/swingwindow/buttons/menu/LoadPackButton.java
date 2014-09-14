
package ndproofs.swingwindow.buttons.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import ndproofs.puzzle.PuzzlePackReader;
import ndproofs.swingwindow.buttons.HotkeyButton;
import ndproofs.swingwindow.menu.NDMenu;

/**
 *
 * @author Oh
 */
public class LoadPackButton extends JMenuItem implements ActionListener, HotkeyButton {
    private final PuzzlePackReader puzzlePackReader;
    private final JFileChooser fileChooser;

    public LoadPackButton(PuzzlePackReader puzzlePackReader) {
        setText("Open Puzzle Pack");
        fileChooser = new JFileChooser();
        this.puzzlePackReader = puzzlePackReader;
        try {
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
            if (puzzlePackReader.loadPuzzlePack(file)) {
                NDMenu.levelSelectTrigger.trigger();
            }
        }
    }
    
}
