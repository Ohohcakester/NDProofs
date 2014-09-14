package ndproofs.proof;

import ndproofs.logic.LineClass;
import ndproofs.logic.Logic;
import ndproofs.logic.LogicAnalyser;
import ndproofs.swingwindow.main.NDSwingWindow;
import java.io.File;
import ndproofs.puzzle.ArgumentReader;
import ndproofs.puzzle.FileIO;


public class ProofTextConverter {
    // Not thread safe.
    public static String lastError = null;
        
    public static StringBuilder toText(ProofData proof) {
     
        int labelSpacing = ProofData.calculateLabelSpacing(proof._curLine);

        StringBuilder strBuild = new StringBuilder();
        
        for (int i=1; i<proof._curLine+1; i++) {
            strBuild.append(proof.lines[i].toString(labelSpacing, true, false));
            strBuild.append(System.lineSeparator());
        }
        
        return strBuild;
    }
    
    public static ProofChecker toProofChecker(String input) {
        lastError = "";
        
        input = input.replaceAll("\r\n", "\n");
        String[] splitString = input.split("\n");
        String[] lineStrings = new String[splitString.length];
        
        int nLines = 0;
        for (int i=0; i<splitString.length; i++) {
            if (0 != splitString[i].length() && 0 != splitString[i].trim().length()) {
                lineStrings[nLines] = splitString[i];
                nLines++;
            }
        }
        
        if (nLines > LineMaker.MAX_LINES) // Do not accept proofs with too many lines.
            return null;
        
        input = null;
        
        LineClass[] lines = new LineClass[nLines+1];
        for (int i=0; i<nLines; i++) {
            lines[i+1] = readLine(lineStrings[i], i+1);
            if (lines[i+1] == null)
                return null;
        }
        
        setupHypoLines(lines);
        
        return new ProofChecker(lines, nLines, NDSwingWindow.ruleSet);
    }
    
    public static ProofChecker readProofFromFile(File file) {
        return toProofChecker(FileIO.readFile(file));
    }

    /**
     * If string is a valid line, returns the LineClass converted from the string.<br>
     * Note: hypoLine will be initialised to -1 because the string does not give this information.<br>
     * If string is invalid, returns null.<br>
     * 
     * @param string from string
     * @param lineNo Note: lineNo is only input for error message printing.
     * @return LineClass object converted from string.
     */
    public static LineClass readLine(String string, int lineNo) {
        // Note: lineNo is only input for error message printing.
        
        StringBuilder read = new StringBuilder(string);
        string = null;
        
        int lineNum;
        Logic statement;
        int hypoLine; // this is a problem...
        int layer;
        String justification = null;;
        
        int index = read.indexOf("|");
        if (index == -1 || index == 0)
            return error("Missing '|'", lineNo);
        
        { // Analysing line number
            String lineNoString = read.substring(0, index).trim();
            if (!ArgumentReader.isInteger(lineNoString))
                return error("Invalid line number", lineNo);
            lineNum = Integer.parseInt(lineNoString);
        }
        
        layer = 0;
        index += 1;
        while (read.charAt(index) == ' ')
            index++;
            
        while(read.length()>2 && read.substring(index,index+2).equals("| ")) {
            layer++;
            index += 2;
            while (read.charAt(index) == ' ')
                index++;
        }
        if (read.length()>2 && read.substring(index,index+2).equals("|_")) {
            layer++;
            justification = LineClass.hypothesisH;
            index += 2;
        }
        
        if (layer > LineMaker.MAX_LAYER)
            return error("Exceeds Max Layer.", lineNo);
        
        // When reading proof from a file, a '.' will be put just before the justification.
        int justificationIndex = read.indexOf(".", index);
        if (justificationIndex == -1)
            return error("Missing '.' before justification", lineNo);
        
        if (read.length() < justificationIndex+2)
            return error("Missing justification", lineNo);
        
        { // Analysing logic statement
            String logicString = read.substring(index,justificationIndex).trim();
            while (logicString.charAt(0) == '_')
                logicString = logicString.substring(1);
            while (logicString.charAt(logicString.length()-1) == '_')
                logicString = logicString.substring(0, logicString.length()-1);

            if (LogicAnalyser.isLogic(logicString))
                statement = LogicAnalyser.readStatement(logicString);
            else
                return error("Invalid logic", lineNo);
        }
        
        { // Analysing justification
            String justString = read.substring(justificationIndex+1).trim();
            switch (justString) {
                case LineClass.premiseString:
                    // Premise.
                    justification = LineClass.premiseP;
                    break;
                    case LineClass.hypothesisString:
                        // Hypothesis. check whether justification is already LineClass.hypothesisH.
                        // It should be, because the earlier ":" should have already been detected.
                        if (!LineClass.hypothesisH.equals(justification))
                            return error("Assumption mismatch", lineNo);
                        break;
                default:
                    justification = justString;
                    break;
            }
        }
        
        hypoLine = -1;
        // Everything ok.
        return new LineClass(lineNum, statement, hypoLine, layer, justification);
    }
    
    public static boolean setupHypoLines(LineClass[] lines) {
        
        int maxLayer = ProofData.maxLayer(lines);
        
        int[] lastHypo = new int[maxLayer+1];
        // All initialized to 0/
        
        for (int i=1; i<lines.length; i++) {
            LineClass line = lines[i];
            
            if (LineClass.hypothesisH.equals(line.justification))
                lastHypo[line.layer] = line.lineNum;
            line.hypoLine = lastHypo[line.layer];
        }
        
        // Successful
        return true;
    }
    
    private static LineClass error(String message, int lineNo) {
        lastError = message + ": line " + lineNo;
        return null;
    }
}