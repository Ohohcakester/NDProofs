package ndproofs.logic;

import java.util.LinkedList;
import java.util.Objects;
import ndproofs.swingwindow.Printer;

    /**
     * For more advanced logical structures, so that discharge rules can be used.
     * e.g. (A,B,C |- D)
     * 
     * @author Oh
     */
public class LogicTree {

    public int optr;
    public Logic logic;
    
    // I don't think we'll need multi-layered logic. For now it'll be single layer.
    public LinkedList<Logic> logicList;
    
    public LogicTree(Logic logic) {
        this.logic = logic;
        optr = Op.LOGIC;
    }
    
    /*public LogicTree(LinkedList<Logic> logicTrees) {
        logicList = logicTrees;
        optr = Op.COMMA;
    }*/
    
    public LogicTree(Logic hypothesis, LinkedList<Logic> results) {
        logic = hypothesis;
        logicList = results;
        optr = Op.CONSEQ;
    }
    
    public void addResult(Logic result) {
        logicList.add(result);
    }
    
    public int numStatements() {
        if (optr == Op.LOGIC)
            return 1;
        if (optr == Op.CONSEQ)
            return 1 + logicList.size();
        
        return -1;
    }
    
    public String getTokenFromIndex(int index) {
        if (optr == Op.LOGIC)
            return logic.toString();
        if (optr == Op.CONSEQ) {
            if (index == 0)
                return logic.toString();
            else
                return logicList.get(index-1).toString();
        }
        
        return "Invalid LogicTree";
    }
    
    
    @Override
    public String toString() {
        return toString(false, false);
    }
    
    public String toString(boolean brackets, boolean maybeLatex) {
        if (optr == Op.LOGIC)
            return logic.toString(maybeLatex);
        if (optr == Op.CONSEQ) {
            StringBuilder output = new StringBuilder();
            if (brackets) output.append("(");
           
            output.append(logic.toString(maybeLatex));
            
            output.append(" ");
            output.append(Op.conseqChar(maybeLatex));
            output.append(" ");
            
            String comma = ""; // This method is genius
            for (Logic logic : logicList) {
                output.append(comma).append(logic.toString(maybeLatex));
                comma = ", ";
            }
            
            if (brackets) output.append(")");
            return output.toString();
        }
        
        return null;
    }
    
    public String toStringHighlight(boolean brackets, int index, boolean maybeLatex) {
        if (optr == Op.LOGIC) {
            if (index != 0) throw new IllegalArgumentException("Op.LOGIC but nonzero index.");
            return Printer.boldTags(logic.toString(maybeLatex));
        }
        if (optr == Op.CONSEQ) {
            StringBuilder output = new StringBuilder();
            if (brackets) output.append("(");
            
            if (index == 0)
                output.append(Printer.boldTags(logic.toString(maybeLatex)));
            else
                output.append(logic.toString(maybeLatex));
            index--;
            
            output.append(" ");
            output.append(Op.conseqChar(maybeLatex));
            output.append(" ");
            
            String comma = ""; // This method is genius
            for (Logic logic : logicList) {
                if (index == 0)
                    output.append(comma).append(Printer.boldTags(logic.toString(maybeLatex)));
                else
                    output.append(comma).append(logic.toString(maybeLatex));
                index--;
                
                comma = ", ";
            }
            
            if (brackets) output.append(")");
            
            return output.toString();
        }
        
        return null;
    }
    
    public static LinkedList<String> splitCommas(String input) {
        
        LinkedList<String> stringList = new LinkedList<>();
        
        int nChars = input.length();
        int bracketLevel = 0;
        
        int start = 0;
        int end = 0;
        
        while (end < nChars) {
            if (input.charAt(end) == '(')
                bracketLevel++;
            else if (input.charAt(end) == ')')
                bracketLevel--;
            
            if (bracketLevel == 0) {
                if (input.charAt(end) == ',') {
                    String substring = input.substring(start,end);
                                        
                    stringList.offer(substring);
                    start = end+1;
                }
                
            }
            end++;
        }
        
        stringList.offer(input.substring(start,end));
        
        return stringList;
    }
    
    
    public static LinkedList<Logic> readLogicList(String input) {
        input = LogicAnalyser.trimBrackets(input);
        
        LinkedList<Logic> logicList = new LinkedList<>();
        
        int nChars = input.length();
        int bracketLevel = 0;
        
        int start = 0;
        int end = 0;
        
        while (end < nChars) {
            if (input.charAt(end) == '(')
                bracketLevel++;
            else if (input.charAt(end) == ')')
                bracketLevel--;
            
            if (bracketLevel == 0) {
                if (input.charAt(end) == ',') {
                    String substring = input.substring(start,end);
                    substring = LogicAnalyser.trimBrackets(substring);
                    
                    if (!LogicAnalyser.isLogic(substring))
                        return null;
                    
                    logicList.offer(LogicAnalyser.readStatement(substring));
                    
                    start = end+1;
                }
                
            }
            end++;
        }
        if (bracketLevel != 0) {
            System.out.println("Check readLogicList statement. possible error.");
            return null;
        }
        
        String substring = input.substring(start,end);
        if (!LogicAnalyser.isLogic(substring))
            return null;
        
        logicList.offer(LogicAnalyser.readStatement(substring));
        
        return logicList;
    }
    
    
    public static LogicTree readLogicTree(String input) {
        input = LogicAnalyser.removeWhitespace(input);
        input = LogicAnalyser.trimBrackets(input);
        
        int bracketLevel = 0;
        int nChars = input.length();
        int linePosition = -1;
        
        for (int i=0; i<nChars; i++) {
            if (input.charAt(i) == '(') {
                bracketLevel++;
            }
            else if (input.charAt(i) == ')') {
                bracketLevel--;
            }
            else if (input.charAt(i) == '|') {
                if (bracketLevel == 0) {                
                    // Can only have one |-
                    if (linePosition == -1)
                        linePosition = i;
                    else
                        return null;
                }
            }
            
            if (bracketLevel < 0)
                return null;
        }
        
        if (bracketLevel != 0)
            return null;
        
        
        if (linePosition != -1) {
            // Case 1: |- in string
            
            if (linePosition+2 >= nChars)
                return null;

            if (input.charAt(linePosition+1) != '-')
                return null;

            String leftString = input.substring(0,linePosition);
            String rightString = input.substring(linePosition+2, nChars);
            
            leftString = LogicAnalyser.trimBrackets(leftString);
            rightString = LogicAnalyser.trimBrackets(rightString);

            if (!LogicAnalyser.isLogic(leftString))
                return null;
            
            LinkedList<Logic> logicList = readLogicList(rightString);
            if (logicList == null)
                return null;
            
            return new LogicTree(LogicAnalyser.readStatement(leftString), logicList);
        }
        
        if (!LogicAnalyser.isLogic(input))
            return null;
        
        return new LogicTree(LogicAnalyser.readStatement(input));
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LogicTree other = (LogicTree) obj;
        if (this.optr != other.optr) {
            return false;
        }
        if (!Objects.equals(this.logic, other.logic)) {
            return false;
        }
        if (!Objects.equals(this.logicList, other.logicList)) {
            return false;
        }
        return true;
    }
    
    public static boolean match(LogicTree[] array1, LogicTree[] array2) {
        // Compares two LogicTree arrays to check if they are the same.
        
        if (array1.length != array2.length) return false;
        for (int i=0; i<array1.length; i++) {
            if (!array1[i].equals(array2[i]))
                return false;
        }
        return true;
    }
    
    
    /**
     * @return 
     * Returns index of Logical Consequence operator.<br>
     * Returns -1 if it can't find a separator.<br>
     * Returns -2 if there is an error in the string. (e.g. more than one |- operator)
     */
    public static int searchForLogCon(String input) {
        int separator = -1;
        
        {
            int bracketLayer = 0;
            int nChars = input.length();
            
            for (int i=0; i<nChars; i++) {
                if (input.charAt(i) == '(')
                    bracketLayer++;
                else if (input.charAt(i) == ')')
                    bracketLayer--;
                else if (input.charAt(i) == '|') {
                    if (bracketLayer == 0) {
                        if (separator == -1)
                            separator = i;
                        else // More than one |-
                            return -2;
                    }
                }
                
                if (bracketLayer < 0)
                    return -2;
            }
            
            if (bracketLayer != 0)
                return -2;
        }   
        return separator;
    }
}