
package ndproofs.swingwindow.main;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import ndproofs.puzzle.PuzzlePackReader;

/**
 *
 * @author Oh
 */
class MainFrameWindowListener implements WindowListener {
    private final JFrame parentFrame;
    private final PuzzlePackReader puzzlePackReader;

    public MainFrameWindowListener(JFrame parentFrame, PuzzlePackReader puzzlePackReader) {
        this.parentFrame = parentFrame;
        this.puzzlePackReader = puzzlePackReader;
    }

    @Override
    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
        if (puzzlePackReader.noPuzzlePackLoaded()) {
            System.exit(0);
            return;
        }
        int result = JOptionPane.showConfirmDialog(parentFrame, "Save progress before exiting?", "Save", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            puzzlePackReader.save();
            System.exit(0);
        } else if (result == JOptionPane.NO_OPTION) {
            System.exit(0);
        } else {
            // Do nothing.
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
    
}
