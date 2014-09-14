package ndproofs.logic;

public class LineClass {
    public int lineNum;
    public int hypoLine;
    public int layer;
    public Logic statement;
    public String justification; // e.g. "P" for premise, "H" for assumption, if rule used, justification = input string.
    
    public static final String premiseP = "P";
    public static final String hypothesisH = "H";
    
    public static final String premiseString = "Premise";
    public static final String hypothesisString = "Assumption";
    
    public static int LINE_WIDTH = 35;

    public LineClass(int lineNum, Logic varStatement, int hypoLine, int layer, String justification) {

        this.lineNum = lineNum;
        statement = varStatement;
        this.hypoLine = hypoLine;
        this.layer = layer;
        this.justification = justification;
    }
    
    public LineClass(LineClass copy) {
        this.lineNum = copy.lineNum;
        this.hypoLine = copy.hypoLine;
        this.layer = copy.layer;
        this.statement = new Logic(copy.statement);
        this.justification = copy.justification;
    }
    
    public boolean isHypo() {
        return justification.equals(hypothesisH);
    }
    
    public boolean isPremise() {
        return justification.equals(premiseP);
    }
    
    @Override
    public String toString() {
        return toString(1, true, false);
    }
    
    public String toString(int labelSpacing, boolean writingToFile, boolean maybeLatex) {
        //¬∧∨⇒
        String pFormat = " %" + labelSpacing + "d" + (maybeLatex ? " " : "|");
        
        StringBuilder lineString = new StringBuilder(String.format(pFormat, lineNum));

        for (int i = 0; i < layer; i++) {
            lineString.append(maybeLatex ? "   " : "  |");
        }
           
        /*if (justification.equals(hypothesisH)) {
            lineString.deleteCharAt(lineString.length()-1);
            lineString.append(':');
        }*/

        if (isHypo() && !maybeLatex)
            lineString.append("_");
        else
            lineString.append(" ");
        
        lineString.append(statement.toString(maybeLatex));
        
        if (isHypo() && !maybeLatex) {
            lineString.append("_");
        }

        if (writingToFile) {
            while (lineString.length() < LINE_WIDTH) {
                lineString.append(" ");
            }

            if (writingToFile)
                lineString.append(" .");
            else
                lineString.append("  ");

            lineString.append(justificationString(maybeLatex));
        }
        
        return lineString.toString();
    }
    
    public String justificationString(boolean maybeLatex) {
        if (isPremise()) {
            return premiseString;
        } else if (isHypo()) {
            return hypothesisString;
        } else {
            if (maybeLatex)
                return Op.maybeLatex(justification);
            else
                return justification;
        }
    }

}