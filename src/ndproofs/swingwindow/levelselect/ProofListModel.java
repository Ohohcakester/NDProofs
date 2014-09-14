
package ndproofs.swingwindow.levelselect;

import java.util.LinkedList;
import javax.swing.DefaultListModel;
import ndproofs.proof.ProofChecker;
import ndproofs.puzzle.PuzzlePackReader;

/**
 *
 * @author Oh
 */
class ProofListModel extends DefaultListModel<ProofChecker> {
    private final PuzzlePackReader puzzlePackReader;
    private LinkedList<ProofChecker> proofList;

    public ProofListModel(PuzzlePackReader puzzlePackReader) {
        this.puzzlePackReader = puzzlePackReader;
    }

    public void refreshProofList(int puzzleNo) {
        clear();
        if (puzzleNo == -1) {
            return;
        }
        proofList = puzzlePackReader.packSaveData.getProofList(puzzleNo);
        addElement(null);
        int nProofs = proofList.size();
        for (ProofChecker proof : proofList) {
            addElement(proof);
        }
    }

    public ProofChecker getProofFromIndex(int selectedIndex) {
        if (selectedIndex == 0) {
            return null;
        } else {
            return proofList.get(selectedIndex - 1);
        }
    }
    
}
