package ndproofs.puzzle.rules;

import ndproofs.proof.ProofData;
import ndproofs.logic.Logic;
import ndproofs.logic.Op;
import ndproofs.logic.LogicTree;
import java.util.ArrayList;
import java.util.LinkedList;
import ndproofs.puzzle.ArgumentReader;

public class RuleSet {
    public ArrayList<RuleReader> ruleList;
    public ArrayList<String> ruleStringList;
    public ArrayList<String> ruleSymbolList;
    public ArrayList<String> ruleNameList;
    public String lastError;
    public String lastInputString;
    public boolean ruleNameMatch;
    public boolean allRulesLogical;
    
    public RuleSet() {
        // A rule is called by RULESYMBOL <ARGUMENTS>
        ruleList = new ArrayList<>();
        ruleStringList = new ArrayList<>();
        ruleSymbolList = new ArrayList<>();
        ruleNameList = new ArrayList<>();
        
        ruleNameMatch = false;
        allRulesLogical = true;
        lastError = "";
        lastInputString = "";
    }
    
    public int size() {
        return ruleList.size();
    }
    
    public void clearRules() {
        ruleList.clear();
        ruleStringList.clear();
        ruleSymbolList.clear();
        ruleNameList.clear();
        allRulesLogical = true;
    }
    
    public void addRule(String ruleSymbol, String ruleName, RuleReader newRule) {
        ruleList.add(newRule);
        ruleStringList.add(newRule.ruleString());
        ruleSymbolList.add(ruleSymbol);
        ruleNameList.add(ruleName);
    }
    
    public boolean addRule(String ruleString, String ruleSymbol, String ruleName) {
        RuleReader newRule = new RuleReader(ruleString);
        ruleString = newRule.ruleString();
        if (!newRule.valid) {
            lastError = newRule.lastError;
            return false;
        }
        if (!newRule.isLogical) {
            allRulesLogical = false;
        }
        
        if (ruleSymbol.length() <= 1) {
            lastError = "Rule name too short";
            return false;            
        }
            
        ruleList.add(newRule);
        ruleStringList.add(ruleString);
        ruleSymbolList.add(ruleSymbol);
        ruleNameList.add(ruleName);
        return true;
    }
    
    public boolean ruleLogical(int ruleNo) {
        return ruleList.get(ruleNo).isLogical;
    }
    
    /**
     * @param inputString
     * @return 
     * Returns the index of the rule if the string matches the name of one of the rules.<br>
     * Returns -1 if no match is found.
     */
    public int identifyRule(String inputString) {
        
        int spacePosition = inputString.indexOf(" ");
        if (spacePosition == -1)
            return -1;
        
        String inputRule = inputString.substring(0,spacePosition);
        
        int nRules = ruleList.size();
        
        for (int i=0; i<nRules; i++) {
            String ruleName = ruleSymbolList.get(i);
            if (inputRule.equals(ruleName))
                return i;
        }
        
        return -1;
    }
    
    public String getRuleName(int ruleNo) {
        return ruleNameList.get(ruleNo);
    }
    
    public String getRuleString(int ruleNo, boolean formatted, int index, boolean maybeLatex) {
        if (formatted)
            return ruleList.get(ruleNo).ruleString(true, index, maybeLatex);
        else // not formatted
            return ruleStringList.get(ruleNo);
    }
    
    public String getTokenFromIndex(int ruleNo, int index) {
        return ruleList.get(ruleNo).getTokenFromIndex(index);
    }
    
    public Logic useRule(int ruleNo, ArgumentReader reader, boolean testOnly, ProofData proof) {
        
        RuleReader rule = ruleList.get(ruleNo);
        
        boolean wentDownOneLayer = false;
        
        LogicTree[] treeArray = generateTreeArray(rule, reader.lineNoList, proof);
        if (treeArray == null) {
            // Down one layer and try again.
            if (proof._curLayer > 0) { // Not layer 0. Thus can go one level up.
                wentDownOneLayer = proof.downOneLayer(); 
                
                treeArray = generateTreeArray(rule, reader.lineNoList, proof);
                if (treeArray == null) {
                    proof.upOneLayer();
                    return null;
                }
                
                // else it's ok.
                //continue...
            }
            else
                return null;
        }
        
        int nLines = reader.logicList.size();
        Logic[] extraStatements = new Logic[nLines];
        for (int i=0; i<nLines; i++) {
            extraStatements[i] = reader.logicList.get(i);
        }
        
        Logic result = rule.useRule(treeArray, extraStatements, testOnly);
        if (result == null) {
            lastError = rule.lastError;
            if (wentDownOneLayer)
                proof.upOneLayer();
            return null;
        }
        
        if (testOnly && wentDownOneLayer)
            proof.upOneLayer();
        
        return result;
    }
    
    public Logic readAndApplyRule(String inputString, ProofData proof) {
        lastInputString = inputString;
        int ruleNo = identifyRule(inputString);
        
        if (ruleNo == -1) {
            lastError = "Invalid Input";
            ruleNameMatch = false;
            return null;
        }
        ruleNameMatch = true;
        
        String ruleName = ruleSymbolList.get(ruleNo);
        
        if (inputString.length() <= ruleName.length()+1) {
            lastError = "Key in arguments";
            return null;
        }
        if (inputString.charAt(ruleName.length()) != ' ') {
            lastError = "Invalid Input";
            ruleNameMatch = false;
            return null;
        }
            
        // Trim excess characters
        inputString = inputString.substring(ruleName.length()+1, inputString.length());
        ArgumentReader reader = new ArgumentReader(inputString);
        if (reader.hasError()) {
            lastError = "Invalid Arguments";
            return null;
        }
        
        return useRule(ruleNo, reader, false, proof);
    }
    
    
    public LinkedList<String> testRules(String argumentString, ProofData proof) {
        int nRules = ruleList.size();
        ArgumentReader reader = new ArgumentReader(argumentString);
        if (reader.hasError()) {
            return null;
        }
        if (!reader.logicList.isEmpty())
            return null;
            
        
        LinkedList<String> resultList = new LinkedList<>();
        
        for (int i=0; i<nRules; i++) {
            if (useRule(i, reader, true, proof) != null) {
                String returnString = ruleSymbolList.get(i);
                returnString = returnString.concat(" ");
                returnString = returnString.concat(argumentString);
                resultList.add("TRY: " + returnString);
            }
        }
        
        return resultList;
    }

    private LogicTree[] generateTreeArray(RuleReader rule, ArrayList<Integer> lineNoList, ProofData proof) {
        
        int nStatements = rule.countStatements();
        if (lineNoList.size() != nStatements) {
            lastError = "Number of arguments does not match";
            return null;
        }
            
        LogicTree[] treeArray = new LogicTree[rule.conditions.length];
        
        int conditionItr = 0;
        int lineItr = 0;
        
        while(lineItr < nStatements) {
            LogicTree condition = rule.conditions[conditionItr];
            
            if (condition.optr == Op.LOGIC) {
                int curLineNo = lineNoList.get(lineItr);
                
                if (!proof.isEstablished(curLineNo)) {
                    lastError = "Invalid Line Referenced: " + curLineNo;
                    return null;
                }
                treeArray[conditionItr] = new LogicTree(proof.lines[curLineNo].statement);
                
                lineItr++;
            }
            if (condition.optr == Op.CONSEQ) {
                int curHypoNo = lineNoList.get(lineItr);
                
                if (!proof.isEstablishedHypothesis(curHypoNo)) {
                    lastError = "Invalid Assumption: " + curHypoNo;
                    return null;
                }
                
                lineItr++;
                
                LinkedList<Logic> resultList = new LinkedList<>();
                int nResults = condition.logicList.size();
                
                for (int i=0; i<nResults; i++) {
                    int curLineNo = lineNoList.get(lineItr);
                    if (!proof.isEstablishedDownOneLayer(curLineNo)) {
                        lastError = "Line " + curLineNo + " must be a consequence of line " + curHypoNo;
                        return null;
                    }
                    
                    if (proof.lines[curLineNo].hypoLine != curHypoNo) {
                        lastError = "Line " + curLineNo + " must be a consequence of line " + curHypoNo;
                        return null;
                    }
                    
                    resultList.offer(proof.lines[curLineNo].statement);
                    lineItr++;
                }
                
                treeArray[conditionItr] = new LogicTree(proof.lines[curHypoNo].statement,
                                                        resultList);
            }
            
            conditionItr++;
        }
        
        
        return treeArray;
    }
    
    public void initializeDefaultRules() {
        clearRules();
        
        addRule("A |- A", "IT", "Iteration");
        addRule("A,B |- A^B", "I^", "Conjunction Introduction");
        addRule("A^B |- A", "E^1", "Conjunction Elimination (Left)");
        addRule("A^B |- B", "E^2", "Conjunction Elimination (Right)");
        addRule("A |- AvB", "Iv1", "Disjunction Introduction (Left)");
        addRule("B |- AvB", "Iv2", "Disjunction Introduction (Right)");
        addRule("AvB, A>C, B>C |- C", "Ev", "Disjunction Elimination");
        addRule("A>B^~B |- ~A", "I~", "Negation Introduction");
        addRule("~~A |- A", "E~", "Double Negation Elimination");
        addRule("(A |- B) |- A>B", "I>", "Implication Introduction");
        addRule("A, A>B |- B", "E>", "Implication Elimination");
    }
}
