package ndproofs.puzzle;

import ndproofs.logic.Logic;
import ndproofs.logic.LogicAnalyser;
import java.util.ArrayList;

public class ArgumentReader {
    public ArrayList<Integer> lineNoList;
    public ArrayList<Logic> logicList;
    protected boolean error;
    
    public static boolean isInteger(String argString) {
        if (argString.length() == 0) return false;
	if (argString.length() > 8) return false;

        boolean isInteger = true;
        for (int i=0; i<argString.length(); i++) {
            char curChar = argString.charAt(i);
            if (curChar < '0' || curChar > '9')
                isInteger = false;
        }
        return isInteger;
    }
    
    public ArgumentReader(){}
    
    /**
     * Reads all ints, then reads all Logics.<br>
     * must be in the form a1,a2,..am, L1,L2,...Lm
     * 
     * @param inputString 
     */
    public ArgumentReader(String inputString) {
        
        if (inputString.endsWith(",")) {
            //Just to fix a bug where arguments like IT 3,,,,, are accepted.
            error = true;
            return;
        }
        
        lineNoList = new ArrayList<>();
        logicList = new ArrayList<>();        
        
        String[] argStrings = inputString.split(",");
        
        int nStrings = argStrings.length;
        
        //Trim all argStrings
        for (int i=0; i<nStrings; i++)
            argStrings[i] = argStrings[i].trim();
                    
        int i = 0;
        
        while (i < nStrings && isInteger(argStrings[i])) {
            lineNoList.add(Integer.parseInt(argStrings[i]));
            i++;
        }
        while (i < nStrings && LogicAnalyser.isLogic(argStrings[i])) {
            logicList.add(LogicAnalyser.readStatement(argStrings[i]));
            i++;
        }
        
        error = (i < nStrings);
    }
    
    /**
     * Check whether the number of ints and logics match.
     * -1 to ignore amount.
     * 
     * @param nInts
     * @param nLogics
     * @return 
     */
    public boolean isValid(int nInts, int nLogics) {
        
        if (error)
            return false;
        
        if (nInts != -1 && nInts != lineNoList.size())
            return false;
        
        if (nLogics != -1 && nLogics != logicList.size())
            return false;
        
        return true;
    }
    
    
    /**
     * Check whether the total number of ints and logics match nTotal.
     * -1 to ignore amount.
     * 
     * @param nTotal
     * @return 
     */
    public boolean isValid(int nTotal) {
        
        if (error)
            return false;
        
        if (nTotal != -1 && nTotal != lineNoList.size()+logicList.size())
            return false;
        
        return true;
    }
    
    public boolean hasError() {
        return error;
    }
}