
package ndproofs.swingwindow.buttons.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import ndproofs.puzzle.PuzzlePackReader;
import ndproofs.swingwindow.Printer;
import ndproofs.swingwindow.buttons.HotkeyButton;
import ndproofs.swingwindow.main.NDSwingWindow;
import ndproofs.swingwindow.menu.TutorialSelect;

/**
 *
 * @author Oh
 */
public class TutorialModeButton extends JMenuItem implements ActionListener, HotkeyButton {
    private final Printer printer;
    private final PuzzlePackReader puzzlePackReader;

    public TutorialModeButton(Printer printer, PuzzlePackReader puzzlePackReader) {
        this.printer = printer;
        this.puzzlePackReader = puzzlePackReader;
        setText("Tutorial");
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        trigger();
    }

    @Override
    public void trigger() {
        if (!puzzlePackReader.noPuzzlePackLoaded()) {
            NDSwingWindow.showDialog("The tutorial may not work correctly if the rule set has been changed.", "Warning");
        }
        TutorialSelect tutorialSelect = new TutorialSelect(NDSwingWindow.mainFrame, true, printer);
    }
    
}
