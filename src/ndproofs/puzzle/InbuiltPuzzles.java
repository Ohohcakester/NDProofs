package ndproofs.puzzle;

import ndproofs.puzzle.packsavedata.PackSaveData;
import ndproofs.proof.LineMaker;
import ndproofs.proof.ProofChecker;
import ndproofs.swingwindow.levelselect.NDLevelSelect;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

public class InbuiltPuzzles {
    public LineMaker lineMaker;
    public PuzzlePackReader puzzlePackReader; // Uninitialized
    
    public DefaultListModel<String> levelListModel; // Uninitialised
    public DefaultListModel<String> proofListModel; // Uninitialised
    
    public int currentStage = -1;
    public int currentProof = -1;
    public ArrayList<PuzzleInterpreter> puzzleList;
    public static int LINE_LENGTH = 50;
    
    public InbuiltPuzzles(LineMaker lineMaker) {
        this.lineMaker = lineMaker;
        puzzleList = new ArrayList<>();
    }
    
    public void clearPuzzleList() {
        currentStage = -1;
        puzzleList.clear();
    }
    
    public void refreshLevelList() {
        levelListModel.clear();
        
        int nPuzzles = puzzleList.size();
        for (int i=0; i<nPuzzles; i++) {
            levelListModel.addElement(levelDisplayString(i));
        }
    }
    
    public void updateLevelList() {
        int nPuzzles = puzzleList.size();
        for (int i=0; i<nPuzzles; i++) {
            updateLevelName(i);
        }
    }
    
    public void updateLevelName(int levelNo) {
        levelListModel.set(levelNo, levelDisplayString(levelNo));
    }
    
    public String levelDisplayString(int levelNo) {
        PackSaveData packSaveData = puzzlePackReader.packSaveData;
        String levelString = String.format("%3d) %s", levelNo+1, puzzleList.get(levelNo).toStringColoured());

        if (packSaveData.isComplete(levelNo) == true) {
            while (levelString.length() < LINE_LENGTH)
                levelString = levelString.concat(" ");
            levelString = levelString.concat("  CLEAR");
        }
        
        return levelString;
    }
    
    
    public void completeCurrentStage() {
        updateLevelName(currentStage);
    }
    
    public boolean alreadyFreeMode() {
        return currentStage == -1;
    }
    
    public boolean tutorialMode() {
        return currentStage == -2;
    }
    
    public void freeMode(boolean saveProgressToMemory) {
        // Note: will save first.
        if (saveProgressToMemory)
            saveProgressToMemory();
        
        lineMaker.premiseEditable = true;
        currentStage = -1;
        lineMaker.printer.setCurrentPuzzle(currentStage);
    }
    
    /**
     * Assumption: puzzle != null, and puzzle is valid.<br>
     * If proof == null, means new puzzle. proofNo must be equal to -1.
     * 
     * @param puzzle
     * @param stageNo
     * @param proof
     * @param proofNo 
     */
    public void stageSelect(PuzzleInterpreter puzzle, int stageNo, ProofChecker proof, int proofNo) {
        
        // NEW PROOF => -1;
        //PackSaveData packSaveData = puzzlePackReader.packSaveData;
        lineMaker.setupLevel(puzzle.premises, puzzle.objective);

        if (proof != null)
            lineMaker.loadProofData(proof);
        currentStage = stageNo;
        currentProof = proofNo;
        lineMaker.printer.setCurrentPuzzle(stageNo);
    }
    
    public PuzzleInterpreter getPuzzle(int stageNo) {
        if (stageNo == -1)
            throw new IndexOutOfBoundsException("Does not accept -1");
        
        return puzzleList.get(stageNo);
    }
    
    public ProofChecker getProof(int stageNo, int proofNo) {
        if (proofNo == -1 || stageNo == -1)
            throw new IndexOutOfBoundsException("Does not accept -1");
        
        return puzzlePackReader.packSaveData.getProof(stageNo, proofNo);
    }
    
    public void saveProgressToMemory() {
        // Saves the current proof progress to the program memory.
        // Where is this function called:
        //   1) When switching to free mode (InbuiltPuzzles.freeMode())
        //   2) When opening the level select menu. (LevelSelectButton.actionPerformed())
        //   3) when clicking save. (PuzzlePackReader.writeMemoryToFile())
        // (by right, it should be called after every new line is made)
        // (limiting it to these times should give the same effect)
        
        // NOTE: IF NOTHING HAS BEEN DONE SO FAR, DO NOTHING.
        
        if (currentStage <= -1)
            return;
        
        PackSaveData packSaveData = puzzlePackReader.packSaveData;
        
        if (currentProof == -1) {
            // New Proof. Add the new proof and set the currentProof to the new proof.
            
            // NOTE: WE CHANGE THE PROOF ONLY IF SOMETHING HAS BEEN CHANGED.
            // We check that by checking whether the last line is a premise. (alternatively, if it's complete, save too.)
            if (lineMaker.hasSomethingWorthSaving())
                currentProof = packSaveData.addProof(currentStage, lineMaker.copyOutProofData());
        }
        else {
            // Overwrite old proof.
            packSaveData.autosaveProof(currentStage, currentProof, lineMaker.copyOutProofData());
        }
        updateLevelList();
    }
    
    public boolean deleteProof(int puzzleNo, int proofNo) {
        PackSaveData packSaveData = puzzlePackReader.packSaveData;
        
        // DO A CHECK WHETHER IT IS THE CURRENT PROOF.
        if (currentStage == puzzleNo) {
            if (currentProof == proofNo) // Disallow deleting of current proof.
                return false;
            if (proofNo < currentProof) // Shift proof index down by 1 if deleting a proof before the current index.
                currentProof--;
        }
        
        packSaveData.deleteProof(puzzleNo, proofNo);
        NDLevelSelect.refreshProofList(puzzleNo);
        updateLevelName(puzzleNo);
        
        return true;
    }
    
    public void copyProof(int puzzleNo, int proofNo) {
        PackSaveData packSaveData = puzzlePackReader.packSaveData;
        ProofChecker newProof = new ProofChecker(packSaveData.getProof(puzzleNo, proofNo));
        packSaveData.addProof(puzzleNo, newProof);
        NDLevelSelect.refreshProofList(puzzleNo);
    }
    
    
    public void add(String puzzleString) {
        PuzzleInterpreter puzzle = new PuzzleInterpreter(puzzleString);
        puzzle.checkLogical();
        puzzle.setInvalidIfAssumptionPremise();
        puzzleList.add(puzzle);
    }
    
    public void initializeDefaultLevels() {
        puzzleList.clear();
    }
}