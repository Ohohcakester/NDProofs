
package ndproofs.proof;

import ndproofs.puzzle.PuzzleInterpreter;
import ndproofs.puzzle.rules.RuleSet;
import ndproofs.logic.LineClass;
import ndproofs.logic.Logic;

public class ProofChecker extends ProofData {
    
    public Logic objective;
    public boolean valid;
    public boolean complete;
    public String lastError;
    
    public ProofChecker(LineClass[] inputLines, int curLine, RuleSet ruleSet) {
        this.ruleSet = ruleSet;
        
        int nLines = curLine+1;
        
        lines = new LineClass[nLines];
        
        for (int i=0; i<nLines; i++) {
            lines[i] = inputLines[i] == null ? null : new LineClass(inputLines[i]);
        }
        
        this._curLine = curLine;
        if (curLine != 0)
            this._curLayer = lines[curLine].layer;
        else
            this._curLayer = 0;
        valid = false;
        complete = false;
        objective = null;
    }
    
    public ProofChecker(LineClass[] inputLines, int curLine, RuleSet ruleSet, int[] lastHypo) {
        this(inputLines, curLine, ruleSet);
        
        this._lastHypo = new int[lastHypo.length];
        for (int i=0; i<lastHypo.length; i++) {
            this._lastHypo[i] = lastHypo[i];
        }
    }
    
    public ProofChecker(LineClass[] inputLines, int curLine, Logic objective, RuleSet ruleSet) {
        this(inputLines, curLine, ruleSet);
        this.objective = objective;
    }
    
    public ProofChecker(ProofChecker proof) {
        this(proof.lines, proof._curLine, proof.ruleSet, proof._lastHypo);
        complete = proof.complete;
        valid = proof.valid;
        lastError = proof.lastError;
        objective = proof.objective;
        _curLayer = proof._curLayer;
        
        _lastHypo = new int[proof._lastHypo.length];
        for (int i=0; i<_lastHypo.length; i++)
            _lastHypo[i] = proof._lastHypo[i];
    }
    
    /**
     * Checks whether the proof is valid.<br><br><br>
     * 
     * 
     * What to check:                                    <br>
     * Premises                                                <br>
     *     - must be at the start of the proof                  <br>
     *     - must be layer 0.                                       <br>
     *                                                                      <br>
     * Assumptions                                                            <br>
     *     - cannot be layer 0.                                                      <br>
     *     - Cannot be 2 layers above previous lines.                                     <br>
     * Other justification: Must match a rule. must output same logic if rule applied.      <br>
     *                                                                                    <br>
     * LineNum:                                                                          <br>
     * - must be equal to position in array.                                           <br>
     *                                                                                <br>
     * hypoLine:                                                                      <br>
     *     NEW CONDITIONS                                                               <br>
     *     Line must be either equal to hypoline or _lastHypo = hypoLine.                 <br>
     *                                                                                     <br>
     *    1) lines must be a hypothesis.                                                       <br>
     *    2) lines must be of the same layer.                                                     <br>
     *    3) no lines of lower layer in between them. (I think condition 4 implies condition 3)     <br>
     *    4) No hypotheses of same layer in between them.                                          <br>
     * 
     * @return 
     * Sets <b>valid</b> to true if proof is valid.
     * Sets <b>lastError</b> if an error occurred.
     */
    public final boolean checkProof() {
        lastError = "";
        
        for (int i=1; i<lines.length; i++) {
            // First check. Check all lineNums
            if (lines[i].lineNum != i)
                return setInvalid("Line number mismatch");
        }
        
        int maxLayer = ProofData.maxLayer(lines);
        
        _lastHypo = new int[maxLayer+1];
        _lastHypo[0] = 0;
        
        _curLine = 1;
        boolean premiseMode = true;
        
        _curLayer = 0;
        
        while(_curLine < lines.length) {
            
            LineClass line = lines[_curLine];
            
            // Possible repeat statement?
            if (_curLine != line.lineNum)
                return setInvalid(line, "Line number mismatch");
            
            if (line.justification.equals(LineClass.premiseP)) {
                if (!premiseMode)
                    return setInvalid(line, "Invalid premise location");
                if (line.layer != 0)
                    return setInvalid(line, "Premise in invalid layer");
            }
            else {
                if (premiseMode)
                    premiseMode = false;
                
                if (line.justification.equals(LineClass.hypothesisH)) {
                    if (line.layer == 0)
                        return setInvalid(line, "Hypothesis in invalid layer");
                    if (line.layer > _curLayer+1)
                        return setInvalid(line, "Hypothesis in invalid layer");
                    
                    _lastHypo[line.layer] = _curLine;
                }
                else {
                    // Not assumption or premise.
                    // Must be a rule.
                    if (line.hypoLine != _lastHypo[line.layer])
                        return setInvalid(line, "Hypothesis mismatch");
                    
                    // Cannot be one layer up without being a hypo.
                    if (line.layer > _curLayer)
                        return setInvalid(line, "Statement in invalid layer");
                    
                    // NOW IMPLEMENT RULE CHECKING... IS THERE ANYTHING ELSE I MISSED?
                    Logic result = ruleSet.readAndApplyRule(line.justification, this);
                    if (!line.statement.equals(result))
                        return setInvalid(line, "Invalid deduction from rule");
                }
                _curLayer = line.layer;
            }
            _curLine++;
        }
        _curLine--;
        
        tryToCompleteObjective();
        
        return setValid();
    }
    
    private boolean setValid() 
    {
        valid = true;
        return true;
    }
    
    private boolean setInvalid() {
        valid = false;
        return false;
    }
    
    private boolean setInvalid(String error) {
        lastError = error;
        return setInvalid();
    }
    
    private boolean setInvalid(LineClass line, String error) {
        return setInvalid(error + ": line " + line.lineNum);
    }
    
    public void checkPremises(PuzzleInterpreter puzzle) {
        // The proof must be checked first. (valid == true)
        if (valid == false) return;
        
        if (!comparePremises(puzzle.premises)) 
            setInvalid("Premise Mismatch");
    }
    
    private boolean tryToCompleteObjective() {
        if (objective == null)
            return false;
        
        if (_curLine == 0)
            return false;

        if (lines[_curLine].statement.equals(objective)) {
            if (lines[_curLine].layer != 0)
                return false;
            complete = true;
            return true;
        }
        return false;
    }
    
    public void checkWithObjective(Logic objective) {
        this.objective = objective;
        tryToCompleteObjective();
    }
}