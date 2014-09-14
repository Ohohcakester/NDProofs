package ndproofs.logic;

import ndproofs.swingwindow.Printer;

public class CheckAndPrint {
    public Printer printer = null; //Uninitialized
    
    public CheckAndPrint() {
    }
    
    
    public void forContradiction(Logic[] premises) {
        if (LogicInterpreter.isContradiction(premises)) {
            printer.tutorial.madeContradictionAction();
            printer.setHintText("True.<br>Statements form a contradiction.");
        }
        else
            printer.setHintText("False<br>Statements do not form a contradiction.");
    }

    public void forTautology(Logic statement) {
        if (LogicInterpreter.isTautology(statement))
            printer.setHintText("Tautology.<br>Statement is always true.");
        else
            printer.setHintText("Not a Tautology.<br>Statement is not always true.");
    }

    public void forConsequence(Logic[] premises, Logic result) {
        if (LogicInterpreter.isLogicalConsequence(premises, result))
            printer.setHintText("True.<br>Last statement can be deduced from the selected statements.");
        else
            printer.setHintText("False.<br>Last statement cannot be deduced from the selected statements.");
    }

    public void forEquivalence(Logic statement1, Logic statement2) {
        if (LogicInterpreter.isLogicalConsequence(statement1, statement2)) {
            if (LogicInterpreter.isLogicalConsequence(statement2, statement1)) {
                printer.setHintText("True. Statements are logically equivalent.");                    
            }
            else
                printer.setHintText("False. but " + statement2.toString(true) +
                                " is a logical consequence of " +
                                statement1.toString(true) + ".");
        }
        else {
            if (LogicInterpreter.isLogicalConsequence(statement2, statement1)) {
                printer.setHintText("False. but " + statement1.toString(true) +
                                " is a logical consequence of " +
                                statement2.toString(true) + ".");
            }
            else
                printer.setHintText("False. The statements do not imply each other");
        }
    }
    
}
