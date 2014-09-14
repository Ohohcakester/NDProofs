
package ndproofs.puzzle.rules;

import java.util.HashSet;


public class RuleStrings {
    public String ruleString;
    public String ruleSymbol;
    public String ruleName;
    public RuleReader ruleReader;
    
    public String lastError;
    public boolean valid;
    
    public RuleStrings(String inputString) {
        // Should start with a { and end with }
        // In the form: (after removing \n characters)
        // {ruleName;ruleSymbol;ruleString}
        if (inputString == null) {
            valid = false; return;
        }
        
        if (inputString.charAt(0) != '{') {
            valid = false; return;
        }
        if (inputString.charAt(inputString.length()-1) != '}') {
            valid = false; return;
        }
        
        String argString[] = inputString.substring(1,inputString.length()-1).split(";");
        if (argString.length != 3) {
            valid = false; return;
        }
        
        ruleName = argString[0].trim();
        ruleSymbol = argString[1].trim();
        ruleString = argString[2].trim();
        valid = true;        
    }
    
    public void processRule() {
        if (!valid) return;
        
        if (ruleSymbol.length() <= 1) {
            lastError = "Rule name too short";
            valid = false;
            return;            
        }
        
        ruleReader = new RuleReader(ruleString);
        valid = ruleReader.valid;
    }

    public void checkIfSymbolUsed(HashSet<String> usedSymbols) {
        if (usedSymbols.contains(ruleSymbol)) {
            lastError = "Rule command name \"" + ruleSymbol + "\" already used";
            valid = false;
        }
        else
            usedSymbols.add(ruleSymbol);
    }
}
