
package ndproofs.swingwindow.buttons.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import ndproofs.swingwindow.buttons.HotkeyButton;
import ndproofs.swingwindow.levelselect.PuzzleTextDisplay;
import ndproofs.swingwindow.main.NDSwingWindow;

/**
 *
 * @author Oh
 */
public class LevelSelectButton extends JMenuItem implements ActionListener, HotkeyButton {
    private final PuzzleTextDisplay puzzleTextDisplay;

    public LevelSelectButton(PuzzleTextDisplay puzzleTextDisplay) {
        setText("Select Puzzle");
        this.puzzleTextDisplay = puzzleTextDisplay;
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        trigger();
    }

    @Override
    public void trigger() {
        NDSwingWindow.inbuiltPuzzles.saveProgressToMemory();
        NDSwingWindow.mainFrame.setContentPane(NDSwingWindow.levelSelectPane);
        NDSwingWindow.mainFrame.validate();
        puzzleTextDisplay.refresh();
    }
    
}
