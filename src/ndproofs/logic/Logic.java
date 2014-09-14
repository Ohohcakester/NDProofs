package ndproofs.logic;

import java.util.Objects;
import ndproofs.swingwindow.Printer;

public class Logic {
// ¬∧∨⇒
    // operator
    // VAR uses no a or b
    // NOT uses only a
    // IMP uses a => b
    // CON and DIS use a,b

    public int optr;

    public Logic a, b;
    public String varName;

    public Logic(){}

    public Logic(String varName) {

        this.varName = varName;
        //No operator. a variable itself or a truth.
        optr = Op.VAR;
    }

    public Logic(int varOptr, Logic varA) {

        // For negation operator.
        optr = varOptr;
        a = varA;

    }

    public Logic(int varOptr, Logic varA, Logic varB) {

        optr = varOptr;
        a = varA;
        b = varB;

    }
    
    // Copy constructor
    public Logic(Logic copy) {
        this.optr = copy.optr;
        this.a = copy.a == null ? null : new Logic(copy.a);
        this.b = copy.b == null ? null : new Logic(copy.b);
        this.varName = copy.varName;
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof Logic))
            return false;
        
        Logic logic2 = (Logic)that;
                
        // If operators are different, immediately return false.
        if (optr != logic2.optr)
            return false;

        // Operators the same.
        if (optr == Op.VAR) // VAR: Compare variable names
            return varName.equals(logic2.varName); 

        if (optr == Op.NOT) // NOT: Compare statement A with statement A of logic2.
            return a.equals(logic2.a);

        // Operator is IMP
        if (optr == Op.IMP) // IMP: Both must be true.
            return a.equals(logic2.a) && b.equals(logic2.b);

        // Operator is DIS OR CON
        return a.equals(logic2.a) && b.equals(logic2.b);
        
        /*
        if (Op.orderMattersCONDIS) {
            // Order of Conjunction and Disjunction matters. AvB != BvA
            return a.equals(logic2.a) && b.equals(logic2.b);
        }
        else {
            // Order of Conjunction and Disjunction does not matter. AvB == BvA
            return (a.equals(logic2.a) && b.equals(logic2.b)) ||
                (a.equals(logic2.b) && b.equals(logic2.a));
        }*/

    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + this.optr;
        hash = 83 * hash + Objects.hashCode(this.a);
        hash = 83 * hash + Objects.hashCode(this.b);
        hash = 83 * hash + Objects.hashCode(this.varName);
        return hash;
    }
    
    /**
     * Surrounds any variable with the same name as target with <b> tags.
     * Outputs the line as a string. For all later iterations
     * 
     * @param target original string
     * @param maybeLatex true to use to latex characters if latex mode on.
     * @return formatted version of target
     */
    private String toStringBracketsHighlight(String target, boolean maybeLatex) {
        if (optr == Op.VAR)
            return varName.equals(target) ? Printer.boldTags(varName) : varName;
        else if (optr == Op.NOT)
            return Op.opChar(optr, maybeLatex) + a.toStringBracketsHighlight(target, maybeLatex);
        else //optr = v ^ >  one of these
            return "(" + a.toStringBracketsHighlight(target, maybeLatex) +
                    Op.opChar(optr, maybeLatex) + b.toStringBracketsHighlight(target, maybeLatex) + ")";

    }
    
    /**
     * Surrounds any variable with the same name as target with <b> tags.
     * 
     * @param target original string
     * @param maybeLatex true to use to latex characters if latex mode on.
     * @return formatted version of target
     */
    public String toStringHighlight(String target, boolean maybeLatex) {
        
        if (optr == Op.VAR)
            return varName.equals(target) ? Printer.boldTags(varName) : varName;
        else if (optr == Op.NOT)
            return Op.opChar(optr, maybeLatex) + a.toStringBracketsHighlight(target, maybeLatex);
        else //optr = v ^ >  one of these
            return a.toStringBracketsHighlight(target, maybeLatex) +
                    Op.opChar(optr, maybeLatex) + b.toStringBracketsHighlight(target, maybeLatex);
    }
    
    /**
     * Outputs the line as a string. For all later iterations
     * 
     * @param maybeLatex true to use to latex characters if latex mode on.
     * @return formatted toString
     */
    private String toStringBrackets(boolean maybeLatex) {
        if (optr == Op.VAR)
            return varName;
        else if (optr == Op.NOT)
            return Op.opChar(optr, maybeLatex) + a.toStringBrackets(maybeLatex);
        else //optr = v ^ >  one of these
            return "(" + a.toStringBrackets(maybeLatex) +
                    Op.opChar(optr, maybeLatex) + b.toStringBrackets(maybeLatex) + ")";

    }
    
    /**
     * Outputs the line as a string. For the topmost level, where brackets are not needed.
     * 
     * @param maybeLatex true to use to latex characters if latex mode on.
     * @return formatted toString
     */
    public String toString(boolean maybeLatex) {
        if (optr == Op.VAR)
            return varName;
        else if (optr == Op.NOT)
            return Op.opChar(optr, maybeLatex) + a.toStringBrackets(maybeLatex);
        else //optr = v ^ >  one of these
            return a.toStringBrackets(maybeLatex) +
                    Op.opChar(optr, maybeLatex) + b.toStringBrackets(maybeLatex);
    }
    
    @Override
    public String toString() {
        return toString(false);
    }
    
    
    public static Logic makeContradiction() {
        Logic a = new Logic("A");
        return new Logic(Op.CON, a, new Logic(Op.NOT, a));
    }
    
    public static Logic makeTautology() {
        Logic a = new Logic("A");
        return new Logic(Op.DIS, a, new Logic(Op.NOT, a));
    }
}