
package ndproofs.proof;

import ndproofs.logic.LineClass;
import ndproofs.logic.Logic;
import ndproofs.logic.Op;
import ndproofs.logic.LogicInterpreter;
import ndproofs.logic.LogicTree;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import ndproofs.puzzle.rules.RuleSet;

public class ProofData {
    
    public int _curLine; // _curLine represents the last line that was written.
    public int[] _lastHypo; // _lastHypo represents the line number of the last hypothesis of each layer.
    public int _curLayer; // _curLayer represents the layer of the last line written, unless upOneLayer/downOneLayer was used.
    
    //NOTE: FIRST LINE IS lines[1]
    public LineClass[] lines;
    
    public RuleSet ruleSet; // Uninitialized

    public ProofData() {}
    
    public ProofData(LineClass[] inputLines, int curLine) {
        int nLines = curLine+1;
        
        lines = new LineClass[nLines];
        
        for (int i=0; i<nLines; i++) {
            lines[i] = inputLines[i] == null ? null : new LineClass(inputLines[i]);
        }
        
    }
    public boolean downOneLayer() {
        if (_curLayer > 0) {
            _curLayer--;
            return true;
        }
        return false;
    }
    
    public boolean upOneLayer() {
        if (_curLayer < lines[_curLine].layer) {
            _curLayer++;
            return true;
        }
        return false;
    }
    
    
    /*-----------------*
     |  Q U E R I E S  |
     *-----------------*/
    
    /**
     * Checks whether an input statement can be deduced from the premises / assumptions.<br>
     * (aka Logical Consequence).
     * 
     * @param queryStatement the statement to check
     * @param includeAssumptions false means premises only
     * @return true iff queryStatement can be deduced from the premises / assumptions.
     */
    public boolean checkDeducible(Logic queryStatement, boolean includeAssumptions) {
        Stack<Logic> establishedStack = new Stack<>();
        for (int lineNo=1; lineNo<=_curLine; lineNo++)
            if (isEstablished(lineNo))
                if(lines[lineNo].justification.equals(LineClass.premiseP) ||
                  (includeAssumptions && lines[lineNo].justification.equals(LineClass.hypothesisH)))
                    establishedStack.add(lines[lineNo].statement);

        Logic[] establishedArray = new Logic[establishedStack.size()];

        {
            int lineNo = 0;
            while (!establishedStack.isEmpty()) {
                    establishedArray[lineNo] = establishedStack.pop();
                    lineNo++;
            }
        }
        
        return LogicInterpreter.isLogicalConsequence(establishedArray, queryStatement);
    }
    
    /**
     * Checks whether line refers to an actual statement
     * 
     * @param lineNum index of the line to check
     * @return 
     */
    public boolean isActualLine(int lineNum) {
        if (lineNum == 0) {
            return false;
        }
        if (_curLine < lineNum) {
            return false;
        }

        return true;
    }

    /**
     * Checks whether <br>
     * 1) It refers to an actual statement <br>
     * 2) Whether that statement is established. (Not a hypothesis) <br>
     * 
     * @param lineNum index of the line to check
     * @return true iff it is established.
     */
    public boolean isEstablished(int lineNum) {
        
        if (!isActualLine(lineNum)) {
            return false;
        }

        // 0 or greater than _curLine means not an actual statement.
        if (lines[lineNum].layer > _curLayer) {
            return false;
        }
        // Error. trying to iterate result from hypothesis.

        if (lines[lineNum].hypoLine != _lastHypo[lines[lineNum].layer]) {
            return false;
        }
        // hypo of the current lines must be lasthypo of the current layer.
        // Error. Same layer but from wrong thread.

        return true; // No issues.
    }
    
    /**
     * Check whether statement is a hypothesis
     * 
     * @param lineNum index of line to check
     * @return true iff statement is a hypothesis
     */
    public boolean isEstablishedHypothesis(int lineNum) {
        if (!isActualLine(lineNum)) {
            return false;
        }
        
        if (!lines[lineNum].justification.equals(LineClass.hypothesisH))
            return false;
        
        return isEstablishedDownOneLayer(lineNum);
    }
    
    /**
     * Checks whether <br>
     * 1) It refers to an actual statement <br>
     * 2) Whether that statement is established. (Not a hypothesis) <br>
     * The current lines and the statement cannot share the same hypothesis. (Unless currentlayer != currentline's layer) 
     * 
     * @param lineNum index of the line to check
     * @return 
     *  true iff it is established.
     */
    public boolean isEstablishedDownOneLayer(int lineNum) {
        
        // Idea: Check whether the layer just below is established.
        
        if (!isActualLine(lineNum)) {
            return false;
        }
        
        if (_lastHypo[_curLayer] == lines[lineNum].hypoLine) // QUESTION. Is this needed?
            return false;
        
        // Doesn't accept established lines. Only stuff that come from assumptions.
        if (isEstablished(lineNum))
            return false;

        // We look for the first lines above it that is from a lower layer.
        int lowerLayerLine = lines[lineNum].hypoLine-1;
        
        while (lowerLayerLine != 0 && lines[lowerLayerLine].layer >= lines[lineNum].layer)
            lowerLayerLine = lines[lowerLayerLine].hypoLine-1;
        
        // Then we check whether this lines is established.
        if (lowerLayerLine == 0)
            return true;
        
        return isEstablished(lowerLayerLine);
    }
    
    public Logic[] convertToLogics(List<Integer> lineNoList, List<Logic> logicList) {
     
        int size = lineNoList.size() + logicList.size();
        int index = 0;
        Logic[] statements = new Logic[size];

        {
            ListIterator<Integer> lineItr = lineNoList.listIterator();
            ListIterator<Logic> logicItr = logicList.listIterator();

            while (lineItr.hasNext()) {
                int currentLine = lineItr.next();

                if (!isActualLine(currentLine))
                    return null;

                statements[index] = lines[currentLine].statement;
                index++;
            }

            while (logicItr.hasNext()) {
                statements[index] = logicItr.next();
                index++;
            }
        }
        if (index != size) {
            System.out.println("SOMETHING WRONG WITH EQUIVALENCE CHECK");
            return null;
        }   
        
        return statements;
    }

    
    public Logic[] convertToLogics(int[] lineNos, Logic[] logics) {
     
        int size = lineNos.length + logics.length;
        int index = 0;
        Logic[] statements = new Logic[size];

        {
            for (int lineNo : lineNos) {
                if (!isActualLine(lineNo))
                    return null;

                statements[index] = lines[lineNo].statement;
                index++;
            }

            for (Logic logic : logics) {
                statements[index] = logic;
                index++;
            }
        }
        if (index != size) {
            System.out.println("SOMETHING WRONG WITH EQUIVALENCE CHECK");
            return null;
        }   
        
        return statements;
    }
    
    
    /**
     * @param lastLineNo line number of the last line of the proof.
     * @return 
     * Returns the number of padding required by the line number labels during printing.<br>
     * to be used with line.toString(int labelSpacing);
     */
    public static int calculateLabelSpacing(int lastLineNo) {
        
        int n = 1;
        for (int i = lastLineNo; i > 9; i /= 10) {
            n++;
        }
        
        return n;
    }
    
    
    public static int maxLayer(LineClass[] lines) {
        int maxLayer = 0;
        
        for (int i=1; i<lines.length; i++) {            
            if (lines[i].layer > maxLayer)
                maxLayer = lines[i].layer;
        }
        return maxLayer;
    }
    
    
    public void debug() {
        System.out.println(lines.length);
        for (LineClass line : lines) {
            if (line == null)
                System.out.println("NULL");
            else {
                System.out.print(line.lineNum);
                System.out.print(", ");
                System.out.print(line.layer);
                System.out.print(", ");
                System.out.print(line.statement);
                System.out.print(", ");
                System.out.print(line.justification);
                System.out.print("\n");
            }
        }
    }
    
    /**
     * 
     * @return 
     * Returns the line number of the last premise in the proof.<br>
     * Returns 0 if there are no premises.<br>
     * Returns -1 if there is an error.
     */
    private int lastPremise() {
        
        int last = 0;
        for (int i=1; i<=_curLine; i++) {
            if (LineClass.premiseP.equals(lines[i].justification)) {
                // The previous line must have been a premise too.
                if (last == i-1)
                    last = i;
                else
                    return -1;
            }
        }
        
        return last;
    }
    
    /**
     * @param premises insert premise array for comparison
     * @return 
     * Returns true iff the proof matches the given premises.
     */
    public boolean comparePremises(LogicTree[] premises) {
     
        // Step 1: check the number of premises. They should be the same.
        int thisLastPremise = lastPremise();
        if (thisLastPremise == -1) return false;
        
        if (thisLastPremise != premises.length)
            return false;
        
        
        // Step 2: Compare the statements of each of the premise lines.
        for (int i=0; i<thisLastPremise; i++) {
            if (premises[i].optr != Op.LOGIC)
                return false; // All input premises should be standalone logical statements.
            if (!premises[i].logic.equals(lines[i+1].statement))
                return false; // Check whether the statements are equal.
        }
        
        return true;
    }
    
    /**
     * @param proof insert another proof to compare with
     * @return 
     * Returns true iff both proofs have exactly the same premises.
     */
    public boolean comparePremises(ProofData proof) {
     
        // Step 1: find last premise for each.
        // Compare the number of premises.
        int leftLastPremise = lastPremise();
        if (leftLastPremise == -1) return false;
        
        int rightLastPremise = proof.lastPremise();
        //if (rightLastPremise == -1) return false; //Unnecessary statement.
        
        if (leftLastPremise != rightLastPremise)
            return false;
        
        
        // Step 2: Compare the statements of each of the premise lines.
        for (int i=1; i<=leftLastPremise; i++) {
            if (!proof.lines[i].statement.equals(lines[i].statement))
                return false; // Check whether the statements are equal.
        }
        
        return true;
    }
    
    public LogicTree[] getPremises() {
        int size = 0;
        for (int i=1; i<=_curLine; i++) {
            if (LineClass.premiseP.equals(lines[i].justification))
                size = i;
        }
        LogicTree[] premises = new LogicTree[size];
        for (int i=0; i<size; i++) {
            premises[i] = new LogicTree(lines[i+1].statement);
        }
        return premises;
    }
    
    public boolean contains(Logic logic, int layer) {
        for (int i=_curLine; i>=1; i--) {
            if (lines[i].layer == layer)
                if (lines[i].statement.equals(logic))
                    return true;
        }
        return false;
    }
    
    public boolean containsWithHypo(Logic logic, int layer, Logic hypo) {
        for (int i=_curLine; i>=1; i--) {
            if (lines[i].layer == layer)
                if (lines[i].statement.equals(logic)) {
                    if (lines[lines[i].hypoLine].statement.equals(hypo))
                        return true;
                }
        }
        return false;
    }
}
