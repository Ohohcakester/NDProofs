
package ndproofs.puzzle.packsavedata;

import java.util.LinkedList;
import ndproofs.proof.ProofChecker;

/**
 * This stores all the proofs for a certain puzzle.
 * @author Oh
 */
public class StoredProofs {

    private final LinkedList<ProofChecker> proofList;
    private final int puzzleIndex;
    private boolean hasCompleteProof;
    
    public StoredProofs(int proofNo) {
        proofList = new LinkedList<>();
        this.puzzleIndex = proofNo;
        hasCompleteProof = false;
    }
    
    /**
     * Note: proof should be checked for completeness before writing in.
     * 
     * @param newProof
     * @return 
     * Returns the index of the newly added proof.
     */
    public int addProof(ProofChecker newProof) {
        proofList.add(newProof);
        updateCompleteStatusAdd(newProof);
        return proofList.size()-1;
    }
    
    public boolean isEmpty() {
        return proofList.isEmpty();
    }
    
    public LinkedList<ProofChecker> getProofList() {
        return proofList;
    }

    public int getIndex() {
        return puzzleIndex;
    }
    
    public ProofChecker getProof(int proofIndex) {
        return proofList.get(proofIndex);
    }

    public void overwriteProof(int proofIndex, ProofChecker proof) {
        proofList.set(proofIndex, proof);
        updateCompleteStatusOverwrite(proof);
    }
    
    public void deleteProof(int proofIndex) {
        proofList.remove(proofIndex);
        updateCompleteStatusDelete();
    }
    
    public boolean hasCompleteProof() {
        return hasCompleteProof;
    }
    
    private void setHasCompleteProof() {
        hasCompleteProof = checkForCompleteProof();
    }
    
    private void updateCompleteStatusDelete() {
        if (hasCompleteProof)
            setHasCompleteProof();
    }
    
    private void updateCompleteStatusAdd(ProofChecker proof) {
        if (!hasCompleteProof)
            if (proof.complete)
                hasCompleteProof = true;
    }
    
    private void updateCompleteStatusOverwrite(ProofChecker proof) {
        if (!hasCompleteProof) {
            if (proof.complete)
                hasCompleteProof = true;
        }
        else {
            if (!proof.complete) {
                setHasCompleteProof();
            }
        }
    }
    
    private boolean checkForCompleteProof() {
        for (ProofChecker proof : proofList) {
            if (proof.complete) {
                return true;
            }
        }
        return false;
    }
}
