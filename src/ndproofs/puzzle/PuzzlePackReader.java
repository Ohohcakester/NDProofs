package ndproofs.puzzle;

import ndproofs.puzzle.rules.RuleStrings;
import ndproofs.puzzle.rules.RuleSet;
import ndproofs.puzzle.packsavedata.PackSaveData;
import ndproofs.puzzle.packsavedata.StoredProofs;
import ndproofs.proof.ProofTextConverter;
import ndproofs.proof.ProofChecker;
import ndproofs.logic.LogicTree;
import ndproofs.swingwindow.clickmode.ClickModeButtons;
import ndproofs.swingwindow.main.NDSwingWindow;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.HashSet;
import javax.swing.JTextField;


public class PuzzlePackReader {
    public static final int MAX_RULES = 18;
    
    public JTextField packNameField; // Uninitialised
    public String lastError;
    
    public String packName;
    public LinkedList<RuleStrings> ruleStringsList;
    public LinkedList<String> puzzleStrings;
    
    public LinkedList<Integer> proofNoList;
    public LinkedList<ProofChecker> proofList;
    
    public File packFile;
    public PackSaveData packSaveData;
    
    public InbuiltPuzzles inbuiltPuzzles; // Uninitialized
    public RuleSet ruleSet; // Uninitialized
    public ClickModeButtons commandButtonPanel; // Uninitialized
    
    public StringBuilder strBuild = null;
    
    public PuzzlePackReader() {
        packSaveData = new PackSaveData();
    }
    
    public boolean clearTempMemory() {
        packName = null;
        ruleStringsList = null;
        puzzleStrings = null;
        strBuild = null;
        proofNoList = null;
        proofList = null;
        return false;
    }
    
    public void loadFileIntoString(File file, boolean encrypted) throws IOException {
        
        if (!encrypted) {
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            strBuild = new StringBuilder();
            while (appendLine(strBuild, reader));
            reader.close();
        }
        else {
            throw new UnsupportedOperationException("Encryption not supported");
            /*strBuild = new StringBuilder(
                    StringEncoder.decryptWithChecksum(FileIO.readFile(file)));*/
        }
        
        packFile = file;
    }
    
    private boolean appendLine(StringBuilder tempBuilder, BufferedReader reader) throws IOException {
        String newLine = reader.readLine();
        if (newLine == null)
            return false;
        tempBuilder.append(newLine);
        tempBuilder.append('\n');
        return true;
    }
    
    public boolean loadPuzzlePack(File file){
        try {
            loadFileIntoString(file, false);
        }
        catch (IOException ex) {
            lastError = "Unable to open file";
            spawnErrorDialog();
            clearTempMemory();
            return false;
        }
        
        boolean success = deployPuzzlePack();
        clearTempMemory();
        return success;
    }
    
    /**
     * Use only after loadFileIntoString is called. i.e. after strBuilder is initialised.<br>
     * @return true iff puzzle pack is valid.<br>
     * @throws IOException 
     */
    private boolean analyseFile() throws IOException {
        
        packName = null;
        ruleStringsList = new LinkedList<>();
        puzzleStrings = new LinkedList<>();
        proofList = new LinkedList<>();
        proofNoList = new LinkedList<>();
        
        // PART 1 - Title
        // Command used to add line.
        if (strBuild.indexOf("Title:") == -1)
            return error("error code 2:\nTitle not found");
        
        strBuild.delete(0, strBuild.indexOf("Title:")+6);
        
        if (strBuild.indexOf(";") == -1)
            return error("error code 3:\nTitle not found");
        
        packName = strBuild.substring(0, strBuild.indexOf(";")).trim();
        strBuild.delete(0, strBuild.indexOf(";")+1);
        // Part 1 - Title - END
        
        
        // Part 2 - Rules
        if (strBuild.indexOf("Rules:") == -1) {
            ruleStringsList = null; // Set to default rules.
            
            //return error("error code 7:\nRules not found");
        }
        else {
            strBuild.delete(0, strBuild.indexOf("Rules:")+6);

            {
                int bracePosition = strBuild.indexOf("}");
                
                HashSet<String> usedSymbols = new HashSet<>(26);

                while (bracePosition != -1 && bracePosition < strBuild.indexOf(":")) {

                    String readSubstring = strBuild.substring(0, bracePosition+1).trim();
                    RuleStrings ruleStrings = new RuleStrings(readSubstring);
                    ruleStrings.processRule();
                    ruleStrings.checkIfSymbolUsed(usedSymbols);

                    if (!ruleStrings.valid) {
                        if (ruleStrings.lastError == null)
                            return error("Unable to process rule:\n" + readSubstring);
                        else
                            return error("Unable to process rule:\n" + readSubstring +
                                    "\n" + ruleStrings.lastError);
                    }

                    if (ruleStringsList.size() >= MAX_RULES)
                        return error("Exceeded maximum number of rules: " + MAX_RULES);
                    
                    ruleStringsList.offer(ruleStrings);
                    strBuild.delete(0, bracePosition+1);

                    bracePosition = strBuild.indexOf("}");
                }
            }
        }
        // Part 2 - Rules - END
        
        // Part 3 - Puzzles
        if (strBuild.indexOf("Puzzles:") == -1)
            return error("error code 10:\nPuzzles not found");
        
        strBuild.delete(0, strBuild.indexOf("Puzzles:")+8);
        
        int semicolPosition = strBuild.indexOf(";");
        int colPosition = strBuild.indexOf(":");
        while (semicolPosition != -1 && (colPosition == -1 || semicolPosition < colPosition)) {
            
            puzzleStrings.add(strBuild.substring(0, semicolPosition).trim());
            strBuild.delete(0, semicolPosition+1);
            
            semicolPosition = strBuild.indexOf(";");
            colPosition = strBuild.indexOf(":");
        }
        // Part 3 - Puzzles - END
        
        // Part 4 - Saved Proofs
        int proofPosition = strBuild.indexOf("#Q");
        
        while (proofPosition != -1) {
            colPosition = strBuild.indexOf(":");
            
            proofPosition += 2;
            
            String questionString = strBuild.substring(proofPosition, colPosition);
            if (!ArgumentReader.isInteger(questionString))
                return error("error code 11:\nInvalid proof header");
            
            int questionNo = Integer.parseInt(questionString)-1;
            
            strBuild.delete(0, colPosition+1);
            int hashPosition = strBuild.indexOf("#");
            
            String proofString;
            if (hashPosition == -1)
                proofString = strBuild.toString();
            else
                proofString = strBuild.substring(0, hashPosition);
            
            ProofChecker proof = ProofTextConverter.toProofChecker(proofString);
            if (proof == null)
                return error("error code 12:\nUnreadable proof\n" + ProofTextConverter.lastError);
            
            // If everything ok,
            proofNoList.add(questionNo);
            proofList.add(proof);
            
            if (hashPosition == -1)
                strBuild.delete(0,strBuild.length());
            else
                strBuild.delete(0,hashPosition);
            
            proofPosition = strBuild.indexOf("#Q");
        }
        
        // Part 4 - Saved Proofs - END
        
        return true;
    }
    
    private boolean error(String errorString) {
        lastError = errorString;
        return false;
    }
    
    public boolean checkIfAllProofsReferToValidPuzzles() {
        int nPuzzles = puzzleStrings.size();
        for (int proofNo: proofNoList) {
            if (proofNo < 0 || proofNo >= nPuzzles) {
                lastError = "Error with puzzle number in proof:\nPuzzle " + (proofNo+1) + " does not exist.";
                return false;
            }
        }
        return true;
    }
    
    /**
     * @return 
     * Checks the puzzles stored in the list puzzleStrings.<br>
     * Returns true only if all puzzles are well-defined and solvable.<br>
     * Will mess up the puzzle queue if false.
     */
    public boolean arePuzzlesConsistent() {
        
        int nPuzzles = puzzleStrings.size();
        for (int i=0; i<nPuzzles; i++) {
            String currentPuzzle = puzzleStrings.poll();
            
            PuzzleInterpreter puzzle = new PuzzleInterpreter(currentPuzzle);
            if (!puzzle.isValid()) {
                return error("Error with puzzle definition:\n" + currentPuzzle);
            }
            
            puzzleStrings.offer(currentPuzzle);
        }
        return true;
    }
    
    private void spawnErrorDialog() {
        String errorMessage = "Unable to open pack\n";
        if (lastError != null)
            errorMessage = errorMessage + lastError;
        
        NDSwingWindow.showErrorDialog(errorMessage, "Error opening file");
    }
    
    private boolean deployPuzzlePack() {
        boolean error = true;
        lastError = null;
        //String tempSig = null;
        
        try {
            error = !analyseFile();
            //tempSig = signatureGenerator.generateSignature();
        }
        catch (IOException exc) {
            lastError = "IOException";
        }
        
        //if (tempSig == null)
        //    error = true;
        
        if (!error) {
            error = !arePuzzlesConsistent();
        }
        
        if (!error) {
            error = !checkIfAllProofsReferToValidPuzzles();
        }
        
        if (error) {
            spawnErrorDialog();
            return false;
        }
        
        // No errors.
        //saveLoadProgress.packSignature = tempSig;
        
        inbuiltPuzzles.lineMaker.clearEverything();
        commandButtonPanel.clearInput();
        commandButtonPanel.updateInputField();
        commandButtonPanel.printer.refreshBefore();
        commandButtonPanel.printer.refreshAfter();
        
        inbuiltPuzzles.clearPuzzleList();
        for (String puzzleString : puzzleStrings)
            inbuiltPuzzles.add(puzzleString);
        
        /*if (!saveLoadProgress.loadData(saveLoadProgress.packSignature)) {
            System.out.println("No save data found");
            inbuiltPuzzles.initialiseToUnsolved();
        }*/
        
        ruleSet.clearRules();
        
        if (ruleStringsList == null) {
            ruleSet.initializeDefaultRules();
        }
        else {
            for (RuleStrings current : ruleStringsList) {
                ruleSet.addRule(current.ruleSymbol, current.ruleName, current.ruleReader);
            }
        }
        
        commandButtonPanel.createRuleButtons();
        
        packNameField.setText(packName);
        
        writePackDataToMemory();
        clearTempMemory();
        
        inbuiltPuzzles.refreshLevelList();
        NDSwingWindow.mainFrame.setContentPane(NDSwingWindow.defaultContentPane);
        NDSwingWindow.mainFrame.validate();
        
        return true;
    }
    
    public void writePackDataToMemory() {
        // Note: inbuiltpuzzles must be set up first.
        packSaveData.writeData(packName, ruleStringsList, puzzleStrings, packFile);
        NDSwingWindow.updateTitle(packFile.getName());
        
        // Write all the proofs to memory.
        while (!proofList.isEmpty()) {
            int proofNo = proofNoList.poll();
            ProofChecker proof = proofList.poll();
            
            PuzzleInterpreter puzzle = inbuiltPuzzles.puzzleList.get(proofNo);
            
            LogicTree[] proofPremises = proof.getPremises();
            if (!LogicTree.match(proofPremises, puzzle.premises))
                proof.valid = false;
            
            // Check whether the proof is complete.
            proof.checkProof();
            proof.checkWithObjective(puzzle.objective);
            proof.checkPremises(puzzle);
            
            packSaveData.addProof(proofNo, proof);
        }
    }
    
    
    
        
    public boolean verifyFileFormat(File file) {
        //Note: modifies reader.
        lastError = null;
        
        try {
            loadFileIntoString(file, false);
            
            if (!analyseFile())
                return clearTempMemory();
            
            /*if (signatureGenerator.generateSignature() == null)
                return clearTempMemory();*/
        }
        catch (IOException exc) {
            lastError = "IOException/Unable to open file";
            return clearTempMemory();
        }
        
        if (!arePuzzlesConsistent())
            return clearTempMemory();
        
        //file will be loaded into stringbuilder.
        return true;
    }
    
    
    
    private boolean writeMemoryToFile(File file) {
        inbuiltPuzzles.saveProgressToMemory();
        
        StringBuilder sb = new StringBuilder();
        
        // Part 1 - Title Block
        sb.append("Title:");
        appendln(sb);
        sb.append(packSaveData.packName);
        sb.append(";");
        appendln(sb,3);
        
        // Part 2 - Rules Block
        if (packSaveData.ruleStringsList != null) {
            sb.append("Rules:");
            appendln(sb);
            for (RuleStrings ruleString : packSaveData.ruleStringsList) {
                sb.append("{");
                sb.append(ruleString.ruleName);
                sb.append("; ");
                sb.append(ruleString.ruleSymbol);
                sb.append("; ");
                sb.append(ruleString.ruleString);
                sb.append("}");
                appendln(sb);
            }
            appendln(sb,2);
        }
        
        // Part 3 - Puzzles Block
        sb.append("Puzzles:");
        appendln(sb);
        for (String puzzleString : packSaveData.puzzleStrings) {
            sb.append(puzzleString);
            sb.append(";");
            appendln(sb);
        }
        appendln(sb,2);
        
        // Part 4 - Proofs Block    `
        for (StoredProofs storedProofs : packSaveData.storedProofsList) {
            for (ProofChecker proof : storedProofs.getProofList()) {
                sb.append("#Q");
                sb.append(storedProofs.getIndex()+1);
                sb.append(":");
                appendln(sb);
                sb.append(ProofTextConverter.toText(proof));
                appendln(sb,2);
            }
        }
        
        // Returns true/false depending on whether the file saving worked.
        return FileIO.saveStringToFile(sb.toString(), file);
    }
    
    private static void appendln(StringBuilder sb) {
        sb.append(System.lineSeparator());
    }
    
    private static void appendln(StringBuilder sb, int nLines) {
        for (int i=0; i<nLines; i++)
            appendln(sb);
    }
    
    public boolean noPuzzlePackLoaded() {
        return packSaveData.packFile == null;
    }
    
    public boolean save() {
        if (noPuzzlePackLoaded()) {
            NDSwingWindow.showErrorDialog("No puzzle pack loaded.", "Unable to save file");
            return false;
        }
        
        return writeMemoryToFile(new File(packSaveData.packFile));
    }
    
    public boolean saveAs(File file) {
        if (noPuzzlePackLoaded()) {
            NDSwingWindow.showErrorDialog("No puzzle pack loaded.", "Unable to save file");
            return false;
        }
        
        if (writeMemoryToFile(file)) {
            packSaveData.setFile(file);
            NDSwingWindow.updateTitle(file.getName());
            return true;
        }
        NDSwingWindow.showErrorDialog("The file was not saved.", "Error saving file");
        return false;
    }
    
}