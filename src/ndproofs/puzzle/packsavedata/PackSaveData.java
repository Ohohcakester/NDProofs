package ndproofs.puzzle.packsavedata;

import ndproofs.proof.ProofChecker;
import java.io.File;
import java.util.LinkedList;
import ndproofs.puzzle.rules.RuleStrings;



/**
 * This data structure stores all the information about a puzzle pack.
 * 
 * @author Oh
 */
public class PackSaveData {
    
    public String packName;
    public LinkedList<RuleStrings> ruleStringsList;
    public LinkedList<String> puzzleStrings;
    
    public String packFile;
    public LinkedList<StoredProofs> storedProofsList;
    
    public PackSaveData() {
        packFile = null;
    }
    
    public void writeData(String packName,
                        LinkedList<RuleStrings> ruleStringsList,
                        LinkedList<String> puzzleStrings,
                        File packFile) {
        
        this.packName = packName;
        this.ruleStringsList = ruleStringsList;
        this.puzzleStrings = puzzleStrings;
        this.packFile = packFile.getPath();
        setFile(packFile);
        
        initialiseProofSaveData();
    }
    
    public final void setFile(File file) {
        this.packFile = file.getPath();
    }
     
    private void initialiseProofSaveData() {
        storedProofsList = new LinkedList<>();
        
        // Initialise StoredProofs for every puzzle.
        for (int i=0; i<puzzleStrings.size(); i++)
            storedProofsList.add(new StoredProofs(i));
    }
    
    /**
     * @param puzzleNo
     * @param proof
     * @return Returns the index of the newly added proof.
     */
    public int addProof(int puzzleNo, ProofChecker proof) {
        StoredProofs storedProofs = storedProofsList.get(puzzleNo);
        return storedProofs.addProof(proof);
    }
    
    public void deleteProof(int puzzleNo, int proofNo) {
        StoredProofs storedProofs = storedProofsList.get(puzzleNo);
        storedProofs.deleteProof(proofNo);
    }
    
    public LinkedList<ProofChecker> getProofList(int puzzleNo) {
        return storedProofsList.get(puzzleNo).getProofList();
    }
    
    /**
     * @param puzzleNo
     * @param proofIndex proofIndex == -1 means save as a new proof.
     * @param proof 
     */
    public void autosaveProof(int puzzleNo, int proofIndex, ProofChecker proof) {
        
        if (proofIndex == -1) {
            // New proof.
            storedProofsList.get(puzzleNo).addProof(proof);
        }
        else {
            storedProofsList.get(puzzleNo).overwriteProof(proofIndex, proof);
        }
    }
    
    public ProofChecker getProof(int puzzleNo, int proofIndex) {
        if (proofIndex == -1 || puzzleNo == -1)
            return null;
        return storedProofsList.get(puzzleNo).getProof(proofIndex);
    }
    
    public boolean isComplete(int puzzleNo) {
        return storedProofsList.get(puzzleNo).hasCompleteProof();
    }
}
