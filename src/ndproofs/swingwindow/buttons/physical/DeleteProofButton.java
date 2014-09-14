
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
public class DeleteProofButton extends JButton implements ActionListener, HotkeyButton {
    private final JList<String> levelList;
    private final JList<ProofChecker> proofList;
    private final InbuiltPuzzles inbuiltPuzzles;

    public DeleteProofButton(InbuiltPuzzles inbuiltPuzzles, JList<String> levelList, JList<ProofChecker> proofList) {
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
        // Disallow deleting of NEW PROOF.
        if (puzzleNo != -1 && selectedProofIndex >= 1) {
            promptBeforeDelete(puzzleNo, selectedProofIndex - 1);
        }
    }

    public void promptBeforeDelete(int puzzleNo, int proofNo) {
        // Make a prompt.
        if (NDSwingWindow.showConfirmDialog("Are you sure you want to delete this proof?", "Confirm Deletion")) {
            if (!inbuiltPuzzles.deleteProof(puzzleNo, proofNo)) {
                NDSwingWindow.showDialog("Proof is currently open. Unable to delete proof.", null);
            }
        }
    }
    
}
