package ndproofs.swingwindow;

import ndproofs.puzzle.TutorialController;
import ndproofs.swingwindow.LayerLineSet;
import ndproofs.swingwindow.clickmode.FormatClass;
import ndproofs.puzzle.rules.RuleSet;
import ndproofs.puzzle.rules.RuleReader;
import ndproofs.proof.LineMaker;
import ndproofs.logic.Op;
import ndproofs.swingwindow.NDConfig;
import ndproofs.swingwindow.main.NDSwingWindow;
import java.awt.Font;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JEditorPane;
import java.util.LinkedList;


/**
 * In charge of printing functions.
 * @author Oh
 */
public class Printer {
    public JTextArea textDisplay; // Uninitialised
    public JEditorPane hintBox; // Uninitialised
    public JTextField objectiveBox; // Uninitialised
    public JTextField stageBox; // Uninitialised
    public JTextField errorField; // Uninitialised
    public JFrame mainFrame; // Uninitialised
    public DefaultListModel<String> levelListModel; // Uninitialised
    
    public DefaultListModel<String> lineListModel = null;
    public SelectableTextDisplay selectableTextDisplay = null;
    public JTextField errorFieldCM = null;
    
    public LineMaker lineMaker; // Uninitialised
    public RuleSet ruleSet; // Uninitialised
    
    private String defaultHintText = "";
    public TutorialController tutorial;
    
    private boolean helpMode = false;
    private int currentHelpModeType;
    
    public static final int HELPMODE_HELP = 0;
    public static final int HELPMODE_RULES = 1;
    public static final int HELPMODE_ABOUT = 2;
    
    public final LayerLineSet layerLineSet;
    
    private boolean inClickMode = false;
    
    public Printer() {
        layerLineSet = new LayerLineSet();
    }
    
    public void exit() {
        mainFrame.setVisible(false);
        mainFrame.dispose();
    }
    
    public void addDrawerLine(int layer, String justification) {
        layerLineSet.addLine(layer, -1, justification);
    }
    
    public void addDrawerHypo(int layer, int hypoLength, String justification) {
        layerLineSet.addLine(layer, hypoLength, justification);
    }
    
    public void setDrawerSpacing(int labelSpacing) {
        layerLineSet.setLabelSpacing(labelSpacing);
    }
    
    public void println(String input) {
        textDisplay.append(input + "\n");
        lineListModel.addElement(input);
        selectableTextDisplay.ensureIndexIsVisible(lineListModel.size()-1);
    }
    
    /*public void print(String input) {
        textDisplay.append(input);
    }*/
    
    public void clear() {
        textDisplay.setText("");
        lineListModel.clear();
        layerLineSet.clear();
    }
    
    public void error(String message) {
        errorField.setText("  " + message);
        errorFieldCM.setText("  " + message);
    }
    
    public void clearError() {
        errorField.setText("");
        errorFieldCM.setText("");
    }
    
    public void setHintText(String input) {
        hintBox.setText(input);
        hintBox.setCaretPosition(0);
    }
    
    public void setDefaultHintText(String message) {
        defaultHintText = message;
        clearHints();
    }
    
    public void clearHints() {
        if (tutorial.tutorialMode()) {
            if (NDConfig.instructionsOn())
                hintBox.setText(tutorial.getHint() + "<br><br>" + defaultHintText);
            else
                hintBox.setText(tutorial.getHint());
        }
        else {
            if (NDConfig.instructionsOn())
                hintBox.setText(defaultHintText);
            else
                hintBox.setText("");
        }
        hintBox.setCaretPosition(0);
    }
    
    public void resetHintText() {
        defaultHintText = "";
        clearHints();
    }
    
    public void hintScrollToTop() {
        hintBox.setCaretPosition(0);
    }
    
    
    public void setConsoleMode() {
        setDefaultHintText("Console Mode Usage:" + "<br>" + 
                "Type commands into the panel at the bottom left corner.");
        
        inClickMode = false;
    }
    
    public void setClickMode() {
        setDefaultHintText("Click Mode Usage:<br>" +
                            "1) Click on a command<br>" +
                            "2) Click the lines or<br>" +
                            "the \'Logic\' button<br>" +
                            "3) Press OK!");
        
        inClickMode = true;
    }
    
    public void setObjective(String message) {
        objectiveBox.setText(message);
    }
    
    public void clearObjective() {
        objectiveBox.setText("");
    }
    
    public void setHelpMode(boolean mode) {
        if (mode == helpMode)
            return;
        
        if (!helpMode) {
            textDisplay.setFont( new Font( "Monospaced", Font.ITALIC, 14));
            helpMode = true;
        }
        else {
            textDisplay.setFont(NDSwingWindow.standardMonospace);
            helpMode = false;
        }
    }
    
    public void setCurrentPuzzle(int stageNo) {
        if (stageNo == -1)
            stageBox.setText("Free Mode");
        else if (stageNo == -2)
            stageBox.setText("Tutorial");
        else
            stageBox.setText(stageNo+1 + "");
            //stageBox.setText(String.format("Puzzle %d", stageNo));
    }
    
    public void refreshBefore() {
        clearError();
        clearHints();
        setHelpMode(false);
    }
    
    public void refreshAfter() {
        if(lineMaker.objective != null)
            setObjective(lineMaker.objective.toString(true));
        else
            clearObjective();
            
        if (!helpMode)
            lineMaker.printAllLines();
    }
    
    public void scrollToTop() {
        textDisplay.setCaretPosition(0);
    }
    
    public void toggleHelp() {
        dialogHelp();
        /*if (helpMode && (currentHelpModeType == HELPMODE_ABOUT)) {
            refreshBefore();
            refreshAfter();
        }
        else {
            refreshBefore();
            printHelp();
            refreshAfter();
        }*/
    }
    
    public void toggleRules() {
        dialogRules();
        /*
        if (helpMode && (currentHelpModeType == HELPMODE_RULES)) {
            refreshBefore();
            refreshAfter();
        }
        else {
            refreshBefore();
            printRules();
            refreshAfter();
        }*/
    }
    
    public void toggleCommands() {
        dialogCommands();
        /*if (helpMode && (currentHelpModeType == HELPMODE_HELP)) {
            refreshBefore();
            refreshAfter();
        }
        else {
            refreshBefore();
            printCommands();
            refreshAfter();
        }*/
    }
    
    private LinkedList<String> stringQueueHelp() {
        LinkedList<String> queue = new LinkedList<>();
        
        queue.offer(NDSwingWindow.introString);
        queue.offer("");
        queue.offer("Press File > Open Puzzle Pack");
        queue.offer("   to open a puzzle pack.");
        queue.offer("");
        queue.offer("Press Mode > Select Puzzle");
        queue.offer("   to select a puzzle after opening a puzzle pack.");
        queue.offer("");
        queue.offer("Press Mode > Tutorial");
        queue.offer("   to learn how to use this program.");
        queue.offer("");
        queue.offer("    A^B  - Conjunction");
        queue.offer("    AvB  - Disjunction");
        queue.offer("    A>B  - Implication");
        queue.offer("    ~A   - Negation");
        queue.offer("    A|-B - Logical Consequence");
        
        return queue;
    }
    
    public void dialogHelp() {
        StringBuilder sb = new StringBuilder();
        LinkedList<String> queue = stringQueueHelp();
        while (!queue.isEmpty())
            sb.append(queue.poll()).append("\n");
        
        NDSwingWindow.showScrollableDialog(sb.toString(), "Help");
    }
    
    public void printHelp() {
        clear();
        LinkedList<String> queue = stringQueueHelp();
        while (!queue.isEmpty())
            println(queue.poll());
        
        scrollToTop();
        currentHelpModeType = HELPMODE_ABOUT;
        setHelpMode(true);
    }
    
    private LinkedList<String> stringQueueRules() {
        LinkedList<String> queue = new LinkedList<>();
        
        queue.offer("<<RULE LIST>>");
        queue.offer("Format:");
        queue.offer(" #) Rule Name");
        queue.offer("    Command to use Rule");
        queue.offer("    Description of Rule");
        queue.offer("");
        queue.offer("Arguments:");
        queue.offer("  a1,a2,a3 = Line Numbers");
        queue.offer("  L1,L2,L3 = Logical Statements");
        queue.offer("");
        int nRules = ruleSet.ruleList.size();
        
        for (int i=0; i< nRules; i++) {
            queue.offer(String.format("%2d) %s", i+1, ruleSet.ruleNameList.get(i)));
            
            StringBuilder displayString = new StringBuilder("    ");
            
            if (inClickMode)
                displayString.append(Op.maybeLatex(ruleSet.ruleSymbolList.get(i)));
            else
                displayString.append(ruleSet.ruleSymbolList.get(i));
            displayString.append(" ");
            
            RuleReader currentRule = ruleSet.ruleList.get(i);
            FormatClass ruleFormat = currentRule.generateFormat();
            displayString.append(ruleFormat.toString());
            
            queue.offer(displayString.toString());
            queue.offer("    " + Op.maybeLatex(ruleSet.ruleStringList.get(i)));
            queue.offer("");
        }
        
        return queue;
    }
    
    public void dialogRules() {
        StringBuilder sb = new StringBuilder();
        LinkedList<String> queue = stringQueueRules();
        while (!queue.isEmpty())
            sb.append(queue.poll()).append("\n");
        
        NDSwingWindow.showScrollableDialog(sb.toString(), "Rules");
    }
    
    public void printRules() {
        clear();
        LinkedList<String> queue = stringQueueRules();
        while (!queue.isEmpty())
            println(queue.poll());
        
        scrollToTop();
        currentHelpModeType = HELPMODE_RULES;
        setHelpMode(true);
    }
    
    private LinkedList<String> stringQueueCommands() {
        LinkedList<String> queue = new LinkedList<>();
        
        queue.offer("----------------------------");
        queue.offer("<< CONSOLE MODE HELP PAGE >>");
        queue.offer("----------------------------");
        queue.offer("NOTE:");
        queue.offer("a1, a2, etc represents line numbers. Key in line numbers as arguments.");
        queue.offer("L1, L2  etc represents logical statements. Key in logical statements as arguments.");
        queue.offer("");
        queue.offer("Executing a Rule (Example):");
        queue.offer("Iv1 13, AvB");
        queue.offer("Refer to rules page for details on rules.");
        queue.offer("");
        queue.offer("Testing a Rule (Example):");
        queue.offer("13, 14");
        queue.offer("- Displays a list of rules that can be executed on lines 13,14.");
        queue.offer("");
        queue.offer("");
        queue.offer("------------------");
        queue.offer("<< COMMAND LIST >>");
        queue.offer("------------------");
        queue.offer("");
        queue.offer("help");
        queue.offer("- Displays the command list.");
        queue.offer("");
        queue.offer("rules");
        queue.offer("- Displays the list of available rules");
        queue.offer("");
        queue.offer("back");
        queue.offer("- Returns to the proof screen.");
        queue.offer("");
        queue.offer("quit");
        queue.offer("- Exits the program");
        queue.offer("");
        queue.offer("A L1");
        queue.offer(" - Make Assumption");
        queue.offer("");
        queue.offer("P L1");
        queue.offer(" - Make Premise (free mode only)");
        queue.offer("");
        queue.offer("D");
        queue.offer(" - Down one Layer");
        queue.offer("");
        queue.offer("U");
        queue.offer(" - Undo");
        queue.offer("");
        queue.offer("O L1");
        queue.offer("- Set Objective to L1 (free mode only)");
        queue.offer("");
        queue.offer("QED");
        queue.offer("- QED (complete puzzle)");
        queue.offer("");
        queue.offer("CC: a1,a2,...,ak,");
        queue.offer("- Check Consequence");
        queue.offer("- Note: a1,a2,...,ak can be replaced with logical statements.");
        queue.offer("");
        queue.offer("CE: a1,a2");
        queue.offer("- Check Equivalence");
        queue.offer("- Note: a1, a2 can be replaced with logical statements as well.");
        queue.offer("");
        queue.offer("Taut: a1");
        queue.offer("- Check for Tautology");
        queue.offer("- Note: a1 can be replaced with a logical statement.");
        queue.offer("");
        queue.offer("Cont: a1,a2,...,ak");
        queue.offer("- Check for Contradiction");
        queue.offer("- Note: a1,a2,...,ak can be replaced with logical statements.");
        
        return queue;
    }
    
    private void dialogCommands() {
        StringBuilder sb = new StringBuilder();
        LinkedList<String> queue = stringQueueCommands();
        while (!queue.isEmpty())
            sb.append(queue.poll()).append("\n");
        
        NDSwingWindow.showScrollableDialog(sb.toString(), "Rules");
    }
    
    public void printCommands() {
        clear();
        
        LinkedList<String> queue = stringQueueCommands();
        while (!queue.isEmpty())
            println(queue.poll());

        scrollToTop();
        
        clearHints();
        setHintText("Press the \'Click Mode\'<br>button above to switch to Click Mode!");
        
        currentHelpModeType = HELPMODE_HELP;
        setHelpMode(true);
    }
    
    
    public static String colourRed(String message) {
        return "<html><font color=\"#800000\">" + message + "</font></html>";
    }
    
    public static String boldTags(String message) {
        return "<font color =\"#E00000\">" + message + "</font>";
    }
}



