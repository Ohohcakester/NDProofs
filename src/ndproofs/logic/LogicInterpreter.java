package ndproofs.logic;

import java.util.LinkedList;
import java.util.ListIterator;

    /**
     * For doing logic comparisons for equivalence, consequence, tautology etc
     * @author Oh
     */
public class LogicInterpreter {
    
    public static int pow(int a, int exponent) {
        return exponent==0? 1 : a*pow(a,exponent-1);
    }
    
    public static void nextCombination(boolean[] array, int size) {
        // Increments a boolean array by 1.
        
        for (int i=size-1; i>=0; i--) {
            if (array[i] == false) {
                array[i] = true;
                i = -1;
            }
            else // array[i] == true
                array[i] = false;
        }
    }
    
    /**
     * Note: Logic, varQueue and testArray must be consistent with each other.
     * O(2^n), n = number of variables
     * 
     * @param A
     * @param varQueue contains all the variables used in A.
     * @param testArray must have the same size as varQueue.
     * @return 
     */
    public static boolean interpret(Logic A, LinkedList<String> varQueue, boolean[] testArray) {
        
        if (A.optr == Op.VAR) {
            return testArray[varQueue.indexOf(A.varName)];
        }
        else if (A.optr == Op.NOT) {
            return !interpret(A.a,varQueue,testArray);
        }
        else if (A.optr == Op.CON) {
            return interpret(A.a,varQueue,testArray) && interpret(A.b,varQueue,testArray);
        }
        else if (A.optr == Op.DIS) {
            return interpret(A.a,varQueue,testArray) || interpret(A.b,varQueue,testArray);
        }
        else if (A.optr == Op.IMP) {
            return !interpret(A.a,varQueue,testArray) || interpret(A.b,varQueue,testArray);
        }
        
        return error();
    }
    
    public static boolean arrayInterpretAND(Logic[] logicArray, int start, LinkedList<String> varQueue, boolean[] testArray) {
        if (start == logicArray.length)
            return true;
        return interpret(logicArray[start],varQueue,testArray) && arrayInterpretAND(logicArray,start+1,varQueue,testArray);
    }
    
    public static boolean error() {
        System.out.println("error found");
        return false;
    }
    
    public static void addAllVarsToQueue(Logic A, LinkedList<String> varQueue) {
        if (A.optr == Op.VAR) {
            if (!varQueue.contains(A.varName))
                varQueue.offer(A.varName);
        }
        else {
            if (A.a != null)
                addAllVarsToQueue(A.a, varQueue);
            if (A.b != null)
                addAllVarsToQueue(A.b, varQueue);
        }
    }
    
        
    public static void addAllVarsToQueue(LogicTree logicTree, LinkedList<String> varQueue) {
        if (logicTree.optr == Op.LOGIC) {
            addAllVarsToQueue(logicTree.logic, varQueue);
        }
        else if (logicTree.optr == Op.CONSEQ) {
            addAllVarsToQueue(logicTree.logic, varQueue);
            
            for (Logic logic : logicTree.logicList) {
                addAllVarsToQueue(logic, varQueue);
            }
        }
        
    }
    
    /**
     * Note: inefficient algorithm. Avoid use with logic with more than 10 variables.
     */
    public static boolean isLogicalConsequence(Logic A, Logic B) {
        
        // Step 1: Add all variables to a queue.
        LinkedList<String> varQueue = new LinkedList<String>();
        addAllVarsToQueue(A, varQueue);
        addAllVarsToQueue(B, varQueue);
        
        int nVars = varQueue.size();
        boolean[] testArray = new boolean[nVars];
        for (int i=0; i<nVars; i++)
            testArray[i] = false;
        
        int numCombis = pow(2,nVars);
        for (int i=0; i<numCombis; i++) {
            
            if (interpret(A, varQueue, testArray))
                if (!interpret(B, varQueue, testArray))
                    return false;
            
            nextCombination(testArray, nVars);
        }
        return true;
    }
    
    /**
     * Note: inefficient algorithm. Avoid use with logic with more than 10 variables.
     */
    public static boolean isLogicalConsequence(Logic[] established, Logic result) {
        int nStatements = established.length;
        
        // Step 1: Add all variables to a queue.
        LinkedList<String> varQueue = new LinkedList<String>();
        for (int i=0; i<nStatements; i++)
            addAllVarsToQueue(established[i], varQueue);
        addAllVarsToQueue(result, varQueue);
        
        
        int nVars = varQueue.size();
        boolean[] testArray = new boolean[nVars];
        for (int i=0; i<nVars; i++)
            testArray[i] = false;
        
        int numCombis = pow(2,nVars);
        for (int i=0; i<numCombis; i++) {
            
            if (arrayInterpretAND(established, 0, varQueue, testArray))
                if (!interpret(result, varQueue, testArray))
                    return false;
            
            nextCombination(testArray, nVars);
        }
        return true;
    }
    
    /**
     * Note: inefficient algorithm. Avoid use with logic with more than 10 variables.
     */
    public static boolean isLogicalConsequence(LogicTree[] trees, Logic result) {
        LinkedList<Logic> logicList = new LinkedList<>();
        
        for (LogicTree tree : trees) {
            if (tree.optr == Op.LOGIC) {
                logicList.offer(tree.logic);
            }
            else if (tree.optr == Op.CONSEQ) {
                ListIterator<Logic> itr = tree.logicList.listIterator();
                while (itr.hasNext()) {
                    logicList.offer(new Logic(Op.IMP, tree.logic, itr.next()));
                }
            }
        }

        int size = logicList.size();
        Logic[] established = new Logic[size];
        
        for (int i=0; i<size; i++) {
            established[i] = logicList.poll();
        }
        
        return isLogicalConsequence(established, result);
    }
    
    /**
     * Note: inefficient algorithm. Avoid use with logic with more than 10 variables.
     */
    public static boolean isContradiction(Logic[] statements) {
        return isLogicalConsequence(statements, Logic.makeContradiction());
    }
    
    /**
     * Note: inefficient algorithm. Avoid use with logic with more than 10 variables.
     */
    public static boolean isTautology(Logic statement) {
        return isLogicalConsequence(new Logic[0], statement);
    }
}
