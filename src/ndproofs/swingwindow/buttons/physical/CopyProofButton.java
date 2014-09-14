
package ndproofs.swingwindow.buttons.physical;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JList;
import ndproofs.proof.ProofChecker;
import ndproofs.puzzle.InbuiltPuzzles;
import ndproofs.swingwindow.buttons.HotkeyButton;
import ndproofs.swingwindow.main.NDSwingWindow;

/**
 *
 * @author Oh
 */
public class CopyProofButton extends JButton implements ActionListener, HotkeyButton {
    private final JList<String> levelList;
    private final JList<ProofChecker> proofList;
    private final InbuiltPuzzles inbuiltPuzzles;

    public CopyProofButton(InbuiltPuzzles inbuiltPuzzles, JList<String> levelList, JList<ProofChecker> proofList) {
        this.inbuiltPuzzles = inbuiltPuzzles;
        this.levelList = levelList;
        this.proofList = proofList;
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        trigger();
    }

    @Override
    public void trigger() {
        int puzzleNo = levelList.getSelectedIndex();
        int selectedProofIndex = proofList.getSelectedIndex();
        // Note that selectedProofIndex is 1 more than the actual proof number.
        // Because the first index is "NEW PROOF"
        // Disallow copying of NEW PROOF.
        if (puzzleNo != -1 && selectedProofIndex >= 1) {
            inbuiltPuzzles.copyProof(puzzleNo, selectedProofIndex - 1);
            NDSwingWindow.showDialog("Proof Copied", "Copy");
        } else {
            NDSwingWindow.showDialog("No proof selected.", "Copy Error");
        }
    }
    
}
