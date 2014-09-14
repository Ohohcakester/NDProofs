package ndproofs.puzzle;

import java.util.ArrayList;
import ndproofs.logic.LogicAnalyser;


public class ArgumentReaderConseq extends ArgumentReader {

    /**
     * Reads all ints and all Logics simultaneously.<br>
     * must be in the form a1,a2,..am, L1,L2,...Lm
     * 
     * @param inputString 
     */
    public ArgumentReaderConseq(String inputString) {
        
        lineNoList = new ArrayList<>();
        logicList = new ArrayList<>();        
        
        String[] argStrings = inputString.split(",");
        
        int nStrings = argStrings.length;
        
        //Trim all argStrings
        for (int i=0; i<nStrings; i++)
            argStrings[i] = argStrings[i].trim();
                    
        int i = 0;
        
        for (String token : argStrings) {
            if (isInteger(token))
                lineNoList.add(Integer.parseInt(token));
            else {
                if (LogicAnalyser.isLogic(token)) {
                    lineNoList.add(null);
                    logicList.add(LogicAnalyser.readStatement(token));
                }
                else {
                    error = true;
                    return;
                }
            }
        }
        
        error = lineNoList.isEmpty();
    }

    
}