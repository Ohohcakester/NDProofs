
package ndproofs.swingwindow.buttons.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import ndproofs.puzzle.PuzzlePackReader;
import ndproofs.swingwindow.buttons.HotkeyButton;
import ndproofs.swingwindow.main.NDSwingWindow;

/**
 *
 * @author Oh
 */
public class SaveButton extends JMenuItem implements ActionListener, HotkeyButton {
    private PuzzlePackReader puzzlePackReader;

    public SaveButton(PuzzlePackReader puzzlePackReader) {
        setText("Save");
        this.puzzlePackReader = puzzlePackReader;
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
        puzzlePackReader.save();
    }
    
}
