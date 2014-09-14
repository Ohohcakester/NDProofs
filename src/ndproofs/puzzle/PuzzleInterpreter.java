package ndproofs.puzzle;

import ndproofs.logic.Logic;
import ndproofs.logic.Op;
import ndproofs.logic.LogicInterpreter;
import ndproofs.logic.LogicAnalyser;
import ndproofs.logic.LogicTree;
import java.util.LinkedList;

public class PuzzleInterpreter {
    public LogicTree[] premises;
    //public boolean[] impliesNext;
    public Logic objective;
    private boolean valid;
    private boolean logical;
    
    /**
     * Converts String to PuzzleInterpreter
     * @param input e.g. P>Q, Q>R |- P>Q^R
     */
    public PuzzleInterpreter(String input) {
        // e.g. P>Q, Q>R |- P>Q^R
        // NOTE: As of now, does not support |- in premises.
        int separator = LogicTree.searchForLogCon(input);
        
        if (separator == -2) { // invalid input
            valid = false;
            return;
        }
        
        if (input.length() <= separator+2 || input.charAt(separator+1) != '-') {
            // Invalid inputs
            valid = false;
            return;
        }
        
        
        //String[] premiseStrings = input.substring(0,separator).split(",");
        String[] premiseStrings;
        {
            String substring = input.substring(0,separator);
            substring = LogicAnalyser.trimBrackets(substring);
            
            int hasLogCon = LogicTree.searchForLogCon(substring);
            if (hasLogCon == -2) {
                valid = false;
                return;
            }
            if (hasLogCon != -1) {
                // There is a logical consequence operator (that isn't within brackets) in there.
                // This means there should be only one premise.
                
                premiseStrings = new String[1];
                premiseStrings[0] = substring;
            }
            else { 
                // There is no logical consequence operator (except within brackets)
                // So split the string by commas.
                
                LinkedList<String> stringList = LogicTree.splitCommas(substring);
                // Copy to array.
                int size = stringList.size();
                premiseStrings = new String[size];
                for (int i=0; i<size; i++) {
                    premiseStrings[i] = stringList.poll();
                }
            }
        }
        String objectiveString = input.substring(separator+2,input.length());
        
        
        if (premiseStrings.length == 1 && premiseStrings[0].trim().equals(""))
            premises = new LogicTree[0];
        else
            premises = new LogicTree[premiseStrings.length];
        
        for (int i=0; i<premises.length; i++) {
            
            premises[i] = LogicTree.readLogicTree(premiseStrings[i]);
            if (premises[i] == null) {
                valid = false; // << ERROR HERE
                return;
            }
        }
        
        if (LogicAnalyser.isLogic(objectiveString)) {
            objective = LogicAnalyser.readStatement(objectiveString);
        }
        else {
            valid = false;
            return;
        }
        
        valid = true;
    }
    
    public void checkLogical() {
        logical = LogicInterpreter.isLogicalConsequence(premises, objective);
    }
    
    public void setInvalidIfAssumptionPremise() {
        for (LogicTree premise : premises) {
            if (premise.optr != Op.LOGIC) {
                valid = false;
            }
        }
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public boolean isLogical() {
        if (!valid)
            return false;
        
        return logical;
    }
    
    public String toStringColoured() {
        if (isLogical())
            return toString();
        else
            return toString() + " [!]";
    }
    
    @Override
    public String toString() {
        /*boolean bracketNeeded = false;
        
        for (LogicTree premise : premises) {
            if (premise.optr != Op.LOGIC) {
                bracketNeeded = true;
                break;
            }
        }*/
        
        StringBuilder sb = new StringBuilder();
        
        if (premises.length != 0) {
            //if (bracketNeeded) sb.append('(');
        
            for (int i=0; i<premises.length-1; i++) {
                sb.append(premises[i].toString(true, true));
                sb.append(", ");
            }
            sb.append(premises[premises.length-1].toString(true, true));
        
            //if (bracketNeeded) sb.append(')');
            
            sb.append(" ");
        }
        
        sb.append(Op.conseqChar(true));
        sb.append(" ");
        sb.append(objective.toString(true));
        
        return sb.toString();
    }
}
