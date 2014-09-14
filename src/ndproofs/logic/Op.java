package ndproofs.logic;


import ndproofs.swingwindow.NDConfig;

    /**
     * <b>Operators</b><br>
     * VAR - Variable. No operator.<br>
     * CON - Conjunction - ^   <br>
     * DIS - Disjunction - v   <br>
     * IMP - Implication - >   <br>
     * NOT - Negation - ~      <br>
     * 
     * @author Oh
     */
public class Op {

    public static final int VAR = 0;
    public static final int CON = 1;
    public static final int DIS = 2;
    public static final int IMP = 3;
    public static final int NOT = 4;

    public static final int LOGIC = 0;
    public static final int COMMA = 1;
    public static final int CONSEQ = 2;
    
    public static final char[] opChar = {' ', '^', 'v', '>', '~'};
    public static final char[] latexChar = {' ', '∧', '∨', '⇒', '¬'};
    
    private static final String conseqChar = "|-";
    private static final String conseqCharLatex = "⊢";
    //¬∧∨⇒
    
    public static char opChar(int optr, boolean maybeLatex) {
        return maybeLatex ? opChar(optr) : opChar[optr];
    }
    
    private static char opChar(int optr) { // Check for latex mode on
        return NDConfig.latexModeOn() ? latexChar[optr] : opChar[optr];
    }
    
    public static String conseqChar(boolean maybeLatex) {
        return maybeLatex ? conseqChar() : conseqChar;
    }
    
    private static String conseqChar() {
        return NDConfig.latexModeOn() ? conseqCharLatex : conseqChar;
    }

    private static String replaceOperator(String input, int optr) {
        return input.replace(opChar[optr], latexChar[optr]);
    }

    private static String convertToLatex(String input) {
        String conseqString = String.valueOf(conseqCharLatex);
        
        input = replaceOperator(input, Op.CON);
        input = replaceOperator(input, Op.DIS);
        input = replaceOperator(input, Op.IMP);
        input = replaceOperator(input, Op.NOT);
        input = input.replace("|-", conseqString);
        
        return input;
    }
    
    public static String maybeLatex(String input) {
        if (NDConfig.latexModeOn())
            return convertToLatex(input);
        else
            return input;
    }
    
    //public static boolean orderMattersCONDIS = true;
    //public static boolean hideUpperLayers = false;
}