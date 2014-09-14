package ndproofs.puzzle.rules;

import ndproofs.logic.Logic;
import ndproofs.logic.Op;
import ndproofs.logic.LogicInterpreter;
import ndproofs.logic.LogicTree;
import java.util.Iterator;
import java.util.LinkedList;
import ndproofs.puzzle.PuzzleInterpreter;
import ndproofs.swingwindow.clickmode.FormatClass;

class VariableMapping {
    private final LinkedList<String> varNameList;
    private final LinkedList<Logic> varLogicList;
    public int size;
    
    public VariableMapping() {
        varNameList = new LinkedList<>();
        varLogicList = new LinkedList<>();
        size = 0;
    }
    
    public VariableMapping(VariableMapping copy) {
        varNameList = new LinkedList<>(copy.varNameList);
        varLogicList = new LinkedList<>(copy.varLogicList);
        size = copy.size;
    }
    
    public void addMapping(String varName, Logic varLogic) {
        varNameList.add(varName);
        varLogicList.add(varLogic);
        size++;
    }
    
    public boolean contains(String varName) {
        return varNameList.contains(varName);
    }
    
    public boolean contains(Logic varLogic) {
        return varLogicList.contains(varLogic);
    }
    
    public Logic toLogic(String varName) {
        return varLogicList.get(varNameList.indexOf(varName));
    }
    
    public String toName(Logic varLogic) {
        return varNameList.get(varLogicList.indexOf(varLogic));
    }
}


public class RuleReader {
    public LogicTree[] conditions;
    public Logic result;
    public LinkedList<String> extraVariables;
    public boolean valid;
    public boolean isLogical;
    public String lastError;
    
    // May be unused;
    public int nPremiseVars;
    
    public RuleReader(String ruleString) {
        valid = true;
        
        PuzzleInterpreter interpret = new PuzzleInterpreter(ruleString);
        if (!interpret.isValid()) {
            valid = false;
            lastError = "Invalid rule string";
            return;
        }
        
        conditions = interpret.premises;
        result = interpret.objective;
        
        // Set the isLogical variable to false if rule is not logical.
        isLogical = LogicInterpreter.isLogicalConsequence(conditions, result);
        
        extraVariables = new LinkedList<>();
        for (LogicTree condition : conditions) {
            LogicInterpreter.addAllVarsToQueue(condition, extraVariables);
        }
        
        nPremiseVars = extraVariables.size();
        
        LogicInterpreter.addAllVarsToQueue(result, extraVariables);
        
        // Clear non-extra variables.
        for (int i=0; i<nPremiseVars; i++)
            extraVariables.poll();
    }
    
    // checkConsistency - Logics
    /**
     * checkConsistency - Logics<br>
     * <br>
     * Checks whether the input is consistent with the template.<br>
     * If a mapping is yet to be defined, it adds the variable to the mapping.<br>
     * If a mapping is already defined, it checks the variable with the mapping.<br>
     * 
     * @param template
     * @param input
     * @param mapping
     * @return 
     * Returns true if no conflicts are found with the mapping or Logic structure.<br>
     * Returns false if a conflict is found.
     */
    private boolean checkConsistency(Logic template, Logic input, VariableMapping mapping) {
        
        if (template.optr == Op.VAR) {
            if (mapping.contains(template.varName)) {
                return mapping.toLogic(template.varName).equals(input);
            }
            else {
                mapping.addMapping(template.varName, input);
                return true;
            }
        }
        else {
            if (template.optr != input.optr)
                return false;
            
            if (template.optr == Op.NOT)
                return checkConsistency(template.a, input.a, mapping);
            else
                return checkConsistency(template.a, input.a, mapping) &&
                        checkConsistency(template.b, input.b, mapping);
        }
    } // checkConsistency - Logics
    
    
    /**
     * checkConsistency - LogicTree<br>
     * <br>
     * Checks whether the input is consistent with the template.<br>
     * If a mapping is yet to be defined, it adds the variable to the mapping.<br>
     * If a mapping is already defined, it checks the variable with the mapping.<br>
     * 
     * @param template
     * @param input
     * @param mapping
     * @return 
     * Returns true if no conflicts are found with the mapping or Logic structure.<br>
     * Returns false if a conflict is found.
     */
    private boolean checkConsistency(LogicTree template, LogicTree input, VariableMapping mapping) {
        
        if (template.optr != input.optr)
            return false;
        
        if (template.optr == Op.LOGIC) {
            return checkConsistency(template.logic, input.logic, mapping);
        }
        
        if (template.optr == Op.CONSEQ) {
            if (!checkConsistency(template.logic, input.logic, mapping))
                return false;
            
            
            Iterator<Logic> templateItr = template.logicList.listIterator();
            Iterator<Logic> inputItr = input.logicList.listIterator();
            
            while (templateItr.hasNext() && inputItr.hasNext()) {
                if (!checkConsistency(templateItr.next(), inputItr.next(), mapping))
                    return false;
            }
            
            if (templateItr.hasNext() || inputItr.hasNext())
                return false;
            
            return true;
        }
        
        //TEMPORARY
        return false;
        
    }// checkConsistency - LogicTree
    
    
    /**
     * 
     * @param selectedTrees
     * @param extraStatements
     * @param testOnly
     * @return 
     * Returns null if invalid.<br>
     * Returns result of valid.
     */
    public Logic useRule(LogicTree[] selectedTrees, Logic[] extraStatements, boolean testOnly) {
        if (selectedTrees.length != conditions.length) {
            lastError = "Number of arguments do not match";
            return null;
        }
        
        if (!testOnly) {
            if (extraStatements.length != extraVariables.size()) {
                lastError = "Number of statements do not match";
                return null;
            }
        }
        
        VariableMapping mapping = new VariableMapping();

        for (int i=0; i<conditions.length; i++) {
            // check the consistency of each line, and at the same time generate the mapping.
            if (!checkConsistency(conditions[i], selectedTrees[i], mapping)) {
                lastError = "Statements do not match the rule";
                return null;
            }
        }
        
        if (testOnly)
            return new Logic("pass");
        
        // Lines are consistent.
        // First we complete the mapping.
        for (int i=0; i<extraVariables.size(); i++) {
            mapping.addMapping(extraVariables.get(i), extraStatements[i]);
        }
        
        // Finally, time to generate the final statement.
        return generateLogic(result, mapping);
         
    }
    
    private Logic generateLogic(Logic template, VariableMapping mapping) {
        if (template.optr == Op.VAR) {
            return mapping.toLogic(template.varName);
        }
        else {
            if (template.optr == Op.NOT)
                return new Logic(template.optr, generateLogic(template.a, mapping));
            else
                return new Logic(template.optr,
                                generateLogic(template.a, mapping),
                                generateLogic(template.b, mapping));
        }
    }
    
    
    public FormatClass generateFormat() {
        return new FormatClass(countStatements(), extraVariables.size());
    }
    
    /**
     * counts the number of line arguments used in the rule. 
     * @return 
     */
    public int countStatements() {
        int numStatements = 0;
        for (LogicTree condition : conditions) {
            numStatements += condition.numStatements();
        }
        return numStatements;
        
    }
    
    public String getTokenFromIndex(int index) {
        int i = 0;
        while (i < conditions.length) {
            int nStatements = conditions[i].numStatements();
            if (index < nStatements) {
                //Found the correct condition
                return conditions[i].getTokenFromIndex(index);
            }
            else {
                // Not yet. increment i.
                index -= nStatements;
                i++;
            }
        }
        // None of the conditions.
        if (index >= extraVariables.size()) {
            try{
            throw new IllegalArgumentException("getTokenFromIndex error");
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            return "ERROR";
        }
        
        return extraVariables.get(index);
    }
    
    /**
     * Like a toString() method.
     * @return 
     * returns the rule in string form.
     */
    public String ruleString() {
        return ruleString(false, 0, false);
    }
    
    /**
     * Like a toString() method.
     * 
     * @param formatted Formatted==true means the currently selected rule is highlighted.
     * @param index
     * @param maybeLatex
     * @return 
     * returns the rule in string form.
     */
    public String ruleString(boolean formatted, int index, boolean maybeLatex) {
        StringBuilder sb = new StringBuilder();
        int nStatements = 0;
        if (formatted) {
            nStatements = countStatements();
        }
        
        // PART 1 - PREMISES
        if (formatted && index < nStatements) { // Formatted version
            for (int i=0; i<conditions.length; i++) {
                LogicTree condition = conditions[i];
                int conditionStatements = -10;
                
                if (index != -1)
                    conditionStatements = condition.numStatements();
                
                if (index < conditionStatements) {
                    sb.append(condition.toStringHighlight(true, index, maybeLatex));
                    // Calls a variant of the toString method which highlights the specified index.
                    index = -1;
                }
                else {
                    sb.append(condition.toString(true, maybeLatex));
                    if (index != -1)
                        index -= conditionStatements;
                }

                if (i < conditions.length-1)
                    sb.append(", ");
            }
        }
        else { // Unformatted version
            for (int i=0; i<conditions.length; i++) {
                LogicTree condition = conditions[i];
                sb.append(condition.toString(true, maybeLatex));

                if (i < conditions.length-1)
                    sb.append(", ");
            }  
        }
        
        sb.append(" ");
        sb.append(Op.conseqChar(maybeLatex)); // SEPARATOR
        sb.append(" ");
        
        // PART 2 - RESULT
        if (formatted && index >= nStatements) { // Formatted version
            index -= nStatements;
            
            String highlightedVariable = extraVariables.get(index);
            sb.append(result.toStringHighlight(highlightedVariable, maybeLatex));
            // Calls a variant of the toString method which highlights the specified variable.
        }
        else { // Unformatted version
            sb.append(result.toString(maybeLatex));
        }
        
        return sb.toString();
    }
}
