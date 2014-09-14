
package ndproofs.swingwindow.buttons.physical;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JList;
import ndproofs.proof.ProofChecker;
import ndproofs.puzzle.InbuiltPuzzles;
import ndproofs.puzzle.PuzzleInterpreter;
import ndproofs.swingwindow.Printer;
import ndproofs.swingwindow.buttons.HotkeyButton;
import ndproofs.swingwindow.main.NDSwingWindow;

/**
 *
 * @author Oh
 */
public class SelectProofButton extends JButton implements ActionListener, HotkeyButton {
    private final JList<String> levelList;
    private final JList<ProofChecker> proofList;
    private final InbuiltPuzzles inbuiltPuzzles;
    private final Printer printer;

    public SelectProofButton(InbuiltPuzzles inbuiltPuzzles, Printer printer, JList<String> levelList, JList<ProofChecker> proofList) {
        this.inbuiltPuzzles = inbuiltPuzzles;
        this.printer = printer;
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
        if (selectedProofIndex == -1) {
            selectedProofIndex = 0;
        }
        if (puzzleNo != -1 && selectedProofIndex != -1) {
            // Disallow setting of invalid proofs. Will do nothing if invalid.
            trySelectProof(puzzleNo, selectedProofIndex - 1);
        }
    }

    private boolean trySelectProof(int stageNo, int proofNo) {
        // Note: Disallows setting of invalid proofs. Will do nothing if invalid.
        // Assume stageNo != -1
        PuzzleInterpreter puzzle = inbuiltPuzzles.getPuzzle(stageNo);
        if (puzzle == null) {
            return error("Invalid puzzle");
        }
        if (!puzzle.isValid()) {
            return error("Invalid puzzle");
        }
        if (proofNo == -1) {
            setupStage(puzzle, stageNo, null, -1); // Note: NEW PROOF => proofNo = -1;
        } else {
            ProofChecker proof = inbuiltPuzzles.getProof(stageNo, proofNo);
            if (proof == null) {
                return error("Invalid Proof");
            }
            if (!proof.valid) {
                return error("Invalid Proof\n" + proof.lastError);
            }
            setupStage(puzzle, stageNo, proof, proofNo);
        }
        return true;
    }

    private void setupStage(PuzzleInterpreter puzzle, int stageNo, ProofChecker proof, int proofNo) {
        // Does nothing if nothing selected.
        NDSwingWindow.mainFrame.setContentPane(NDSwingWindow.defaultContentPane);
        NDSwingWindow.mainFrame.validate();
        NDSwingWindow.setFocusOnConsoleInput();
        printer.refreshBefore();
        inbuiltPuzzles.stageSelect(puzzle, stageNo, proof, proofNo); // Note: NEW PROOF => proofNo = -1;
        printer.refreshAfter();
    }

    private boolean error(String message) {
        NDSwingWindow.showErrorDialog(message, "Error opening proof");
        return false;
    }
    
}
