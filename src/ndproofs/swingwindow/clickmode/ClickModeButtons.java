package ndproofs.swingwindow.clickmode;

import ndproofs.swingwindow.SelectableTextDisplay;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ndproofs.logic.CheckAndPrint;
import ndproofs.proof.LineMaker;
import ndproofs.logic.Logic;
import ndproofs.logic.LogicAnalyser;
import ndproofs.logic.Op;
import ndproofs.swingwindow.Printer;
import ndproofs.puzzle.rules.RuleReader;
import ndproofs.puzzle.rules.RuleSet;
import ndproofs.logic.LineClass;

public class ClickModeButtons extends JPanel {
    public JEditorPane inputFieldCM = null; // Uninitialized
    public RuleSet ruleSet = null; // Uninitialized
    public LineMaker lineMaker = null; // Uninitialized
    public Printer printer = null; // Uninitialized
    public SelectableTextDisplay selectableTextDisplay = null; // Uninitialized
    public CheckAndPrint checkAndPrint = null; // Uninitialized
    
    public JPanel leftPanel = null;
    public JPanel centerPanel = null;
    public JPanel rightPanel = null;
    
    public CommandButton[] ruleButtons = null;
    public static int MAX_RULES = 18;
    
    public int readMode;
    FormatClass currentFormat = null;
    
    public String headerString = null;
    
    public ClickModeButtons() {
        readMode = CommandButton.ID_CLEAR;
        setPreferredSize(new Dimension(400,95));
        setLayout(new BorderLayout());
        
        leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(70,95));
        leftPanel.setLayout(new GridLayout(3,2,2,2));
        
        centerPanel = new JPanel();
        centerPanel.setPreferredSize(new Dimension(270,95));
        centerPanel.setLayout(new GridLayout(3,5,2,2));
        //centerPanel.setBackground(new Color(204,204,204));
        
        rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(60,95));
        rightPanel.setLayout(new GridLayout(3,1,2,2));
        
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    public void initialize() {
        //AHO button 40x29. 2px spacing.
        // Panel Width 90 approx
        
        //Clear and Logic buttons 60x35.
        
        // LHS Buttons:
        // Premise, Assume, Objective
        // Try, Check
        CommandButton buttonPremise = new CommandButton(CommandButton.ID_PREMISE, this);        
        CommandButton buttonAssume = new CommandButton(CommandButton.ID_ASSUME, this);
        CommandButton buttonDown = new CommandButton(CommandButton.ID_DOWNONELEVEL, this);
        
        buttonAssume.setText("Assume");
        buttonAssume.setTooltip("Assume", "Make an Assumption");
        
        buttonPremise.setText("Premise");
        buttonPremise.setTooltip("Make a Premise", "(Free Mode)");
        
        buttonDown.setText("Down");
        buttonDown.setTooltip("Down one layer", "Move one layer downwards.");
        
        
        leftPanel.add(buttonAssume);
        leftPanel.add(buttonPremise);
        leftPanel.add(buttonDown);

        
        
        // RHS Buttons:
        // Key in Logic
        // Undo
        // Clear
        CommandButton buttonKeyLogic = new CommandButton(CommandButton.ID_KEYLOGIC, this);
        CommandButton buttonUndo = new CommandButton(CommandButton.ID_UNDO, this);
        CommandButton buttonClear = new CommandButton(CommandButton.ID_CLEAR, this);
        
        buttonKeyLogic.setText("Logic");
        buttonKeyLogic.setTooltip("Key in a Logical Statement");
        
        buttonUndo.setText("Undo");
        buttonUndo.setTooltip("Undo last action");
        
        buttonClear.setText("Clear");
        buttonClear.setTooltip("Clear input");
        
        rightPanel.add(buttonKeyLogic);
        rightPanel.add(buttonUndo);
        rightPanel.add(buttonClear);
        
        
        //impIntroButton = new CommandButton(CommandButton.ID_IMPLINTRO, this);
        ruleButtons = new CommandButton[MAX_RULES];
        for (int i=0; i<MAX_RULES; i++) {
            ruleButtons[i] = new CommandButton(i, this);
            ruleButtons[i].setVisible(false);
        }
    }
    
    public void clearRuleButtons() {
        for (int i=0; i<MAX_RULES; i++) {
            centerPanel.remove(ruleButtons[i]);
            ruleButtons[i].setVisible(false);
        }
        
        /*centerPanel.remove(impIntroButton);
        impIntroButton.setVisible(false);*/
    }
    
    
    public void createRuleButtons() {
        // Rule button dimensions: 35 x 29. 2px spacing.
        // Panel dimensions: 234 x 95
        clearRuleButtons();
        
        int nRules = ruleSet.size();
        
        /*impIntroButton.setText(ruleSet.implicationIntroSymbol);
        impIntroButton.setTooltip(ruleSet.implicationIntroName,
                                    ruleSet.implicationIntroString);*/
        
        for (int i=0; i<nRules; i++) {
            if (ruleSet.ruleLogical(i)) {
                ruleButtons[i].setText(Op.maybeLatex(ruleSet.ruleSymbolList.get(i)));
                ruleButtons[i].setTooltip(ruleSet.ruleNameList.get(i),
                                            Op.maybeLatex(ruleSet.ruleStringList.get(i)));
            }
            else {
                ruleButtons[i].setText(Printer.colourRed(Op.maybeLatex(ruleSet.ruleSymbolList.get(i))));
                ruleButtons[i].setTooltipRed(ruleSet.ruleNameList.get(i),
                                            Op.maybeLatex(ruleSet.ruleStringList.get(i)));
            }
            centerPanel.add(ruleButtons[i]);
            ruleButtons[i].setVisible(true);
        }
        
        /*centerPanel.add(impIntroButton);
        if (ruleSet.implicationIntroOn)
            impIntroButton.setVisible(true);*/
        
        for (int i=nRules; i<MAX_RULES;i++)
            centerPanel.add(ruleButtons[i]);
        
    }
    
    public boolean isMode(int id) {
        if (id >= 0) // One of the rules
            return true;
        
        return (//id == CommandButton.ID_IMPLINTRO ||
                id == CommandButton.ID_PREMISE ||
                id == CommandButton.ID_ASSUME ||
                id == CommandButton.ID_OBJECTIVE ||
                id == CommandButton.ID_TRY
                //id == CommandButton.ID_CHECK
                );
    }
    
    public String modeString(int id) {
        // Assume isMode == true.
        if (id >= 0)
            return ruleSet.ruleSymbolList.get(id);
        
        // Not rule.
        switch(id) {
            /*case CommandButton.ID_IMPLINTRO:
                return ruleSet.implicationIntroName;*/
            case CommandButton.ID_PREMISE:
                return "P";
            case CommandButton.ID_ASSUME:
                return "A";
            case CommandButton.ID_OBJECTIVE:
                return "O";
            case CommandButton.ID_TRY:
                return "Try";
            //case CommandButton.ID_CHECK:
            //    return "C";
        }
        
        // Invalid.
        return null;
    }
    
    public void buttonPressed(int id) {
        printer.clearError();
        selectableTextDisplay.clearSelection();
        
        if (id >= 0) {
            readMode = id;
            headerString = ruleSet.ruleSymbolList.get(id);
            
            RuleReader currentRule = ruleSet.ruleList.get(id);
            currentFormat = currentRule.generateFormat();
            //currentFormat = new FormatClass(currentRule.conditions.length,
            //                                currentRule.extraVariables.size());
        }
        
        switch (id) {
            /*case CommandButton.ID_IMPLINTRO:
                readMode = id;
                headerString = ruleSet.implicationIntroSymbol;
                currentFormat = new FormatClass(2,0);
                break;*/
                
            case CommandButton.ID_ASSUME:
                readMode = id;
                headerString = "A";
                currentFormat = new FormatClass(0,1);
                break;
                
            case CommandButton.ID_PREMISE:
                readMode = id;
                headerString = "P";
                currentFormat = new FormatClass(0,1);
                break;
                
            case CommandButton.ID_OBJECTIVE:
                readMode = id;
                headerString = "O";
                currentFormat = new FormatClass(0,1);
                break;
                
            case CommandButton.ID_TRY:
                readMode = id;
                headerString = "Try";
                currentFormat = new LineOnlyFormatClass();
                break;
                
            case CommandButton.ID_UNDO: {
                printer.refreshBefore();
                if (lineMaker.tryUndo(true)) {
                    printer.refreshAfter();
                }
                break;
            }
                
            case CommandButton.ID_DOWNONELEVEL: {
                printer.refreshBefore();
                lineMaker.downOneLayer();
                printer.refreshAfter();
                break;
            }
                
            case CommandButton.ID_QED: {
                printer.refreshBefore();
                if (lineMaker.tryQed(true)) {
                    printer.refreshAfter();
                }
                break;
            }
                
            case CommandButton.ID_CLEAR:
                clearInput();
                break;
                
            case CommandButton.ID_KEYLOGIC:
                if (readMode != CommandButton.ID_CLEAR) {
                    JPanel dialogFrame = new JPanel();
                    
                    // Haven't figure out how to set the font of the Logic yet.
                    /*JTextField dialogText = new JTextField();
                    dialogText.setFont(NDSwingWindow.standardMonospace);
                    
                    JOptionPane.showOptionDialog(
                            dialogFrame, dialogText, "Enter Logical Statement",
                            JOptionPane.PLAIN_MESSAGE,JOptionPane.QUESTION_MESSAGE,null,null,null);
                    
                    String inputString = dialogText.getText();*/
                    
                    String inputString = (String)JOptionPane.showInputDialog(
                            dialogFrame, "Enter Logical Statement",
                            null,JOptionPane.PLAIN_MESSAGE,null,null,null);
                    
                    if (inputString != null && inputString.length() != 0)
                        addLogic(inputString);
                }
                break;
                
            case CommandButton.ID_BACK:
                if (currentFormat == null)
                    break;
                if (!currentFormat.undo())
                    lineMaker.error("Unable to reverse further");
                break;
                
                
            case CommandButton.ID_CHECK_CONTRADICTION:
                readMode = id;
                headerString = "Cont:";
                currentFormat = new FreeFormatClass();
                break;
                
            case CommandButton.ID_CHECK_TAUTOLOGY:
                readMode = id;
                headerString = "Taut:";
                currentFormat = new OneItemFormatClass();
                break;
                
            case CommandButton.ID_CHECK_CONSEQUENCE:
                readMode = id;
                headerString = "CC:";
                currentFormat = new ConseqFormatClass();
                break;
                
            case CommandButton.ID_CHECK_EQUIVALENCE:
                readMode = id;
                headerString = "CE:";
                currentFormat = new TwoItemsFormatClass();
                break;
                
        }
        
        updateInputField();
    }
    
    public void clearInput() {
        readMode = CommandButton.ID_CLEAR;
        currentFormat = null;
        setHintTooltip(null);
    }

    public void lineSelected(int lineNo) {
        if (lineNo == 0)
            return;
        
        if (lineNo > lineMaker._curLine)
            return;
        
        printer.clearError();
        
        if (readMode == CommandButton.ID_CLEAR)
            return;
        
        if (currentFormat == null)
            return;
        
        //currentFormat.addInt(lineNo);
        if (!currentFormat.addInt(lineNo))
            lineMaker.error("Unable to add new line");
        
        updateInputField();
    }
    
    public void addLogic(String logicString) {
        if (!LogicAnalyser.isLogic(logicString)) {
            lineMaker.error("Invalid Logic");
            return;
        }
        
        if (currentFormat == null)
            return;
        
        if (!currentFormat.addLogic(LogicAnalyser.readStatement(logicString)))
            lineMaker.error("Unable to add Logical Statement");
        
        updateInputField();
    }
    
    public void pressOk() {
        printer.refreshBefore();
        selectableTextDisplay.clearSelection();
        if (pressOkAction()) {
            readMode = CommandButton.ID_CLEAR;
            currentFormat = null;
        }
        
        printer.refreshAfter();
        updateInputField();
    }
    
    public boolean pressOkAction() {
        // Returns true if the action succeeds. This will clear the input.
        // Returns false if nothing happens. The input will not be cleared.
        
        if (readMode >= 0) {
            if (!currentFormat.full)
                return lineMaker.error("Incomplete Arguments");
            
            Logic result = ruleSet.readAndApplyRule(inputFieldText(false), lineMaker);
            
            if (result == null)
                return lineMaker.error(ruleSet.lastError);
            
            if (lineMaker.complete)
                return lineMaker.error("Unable to make additional statements at this point");
            
            lineMaker.makeLine(result, ruleSet.lastInputString);
            return true;
        }
        switch(readMode) {

            case CommandButton.ID_CLEAR:{
                return false;}

            case CommandButton.ID_ASSUME:{
                if (!currentFormat.full) 
                    return lineMaker.error("Key in a logical statement to assume");
                
                if (lineMaker.complete)
                    return lineMaker.error("Unable to make additional statements at this point");

                lineMaker.makeHypo(currentFormat.logics[0]);
                return true;}


            case CommandButton.ID_PREMISE: {
                if (!currentFormat.full)
                    return lineMaker.error("Key in a premise");

                if (!lineMaker.premiseEditable)
                    return lineMaker.error("Premise cannot be changed");

                if (lineMaker.complete ||
                    (lineMaker._curLine != 0 &&
                    !lineMaker.lines[lineMaker._curLine].justification.equals(LineClass.premiseP)))
                    return lineMaker.error("Unable to make additional premises at this point");

                lineMaker.makeLine(currentFormat.logics[0], "P");
                return true;}


            case CommandButton.ID_OBJECTIVE: {
                if (lineMaker.complete)
                    return lineMaker.error("Unable to change objective at this point");
                if (!lineMaker.premiseEditable)
                    return lineMaker.error("Unable to change objective");

                if (currentFormat.full)
                    lineMaker.objective = currentFormat.logics[0];
                else
                    lineMaker.objective = null;
                return true; }

            case CommandButton.ID_TRY: {
                if (!currentFormat.full)
                    return false;
                
                LinkedList<String> possibleRuleList = ruleSet.testRules(currentFormat.toString(), lineMaker);
                if (possibleRuleList == null || possibleRuleList.isEmpty()) {
                    printer.setHintText("No possible rules for:<br>" + currentFormat.toString());
                    return true;
                }
                // else
                StringBuilder sb = new StringBuilder();
                String newl = "";
                for (String rule : possibleRuleList) {
                    sb.append(newl);
                    sb.append(Op.maybeLatex(rule));
                    newl = "<br>";
                }
                System.out.println(sb.toString());
                printer.setHintText(sb.toString());
                
                return true;}
            
                
            case CommandButton.ID_CHECK_CONTRADICTION: {
                currentFormat.copyInfoToArrays();
                
                Logic[] premises = lineMaker.convertToLogics(currentFormat.lineNos, currentFormat.logics);
                /*Logic[] premises = new Logic[currentFormat.lineNos.length];
                for (int i=0; i<currentFormat.lineNos.length; i++)
                    premises[i] = linelinesr.line[currentFormat.lineNos[i]].statement;
                */
                checkAndPrint.forContradiction(premises);
                return true;}
                
            
            case CommandButton.ID_CHECK_TAUTOLOGY: {
                if (!currentFormat.full)
                    return lineMaker.error("Enter a Line or Logical Statement to check");
                
                Logic statement;
                
                if (currentFormat.lineNos[0] == -1)
                    statement = currentFormat.logics[0];
                else
                    statement = lineMaker.lines[currentFormat.lineNos[0]].statement;
                
                checkAndPrint.forTautology(statement);
                return true;}
                
            case CommandButton.ID_CHECK_CONSEQUENCE: {
                if (!currentFormat.full)
                    return lineMaker.error("Key in a Logical Statement to check");
                
                currentFormat.copyInfoToArrays();
                Logic[] premises = new Logic[currentFormat.lineNos.length-1];
                int logicIndex = 0;
                for (int i=0; i<currentFormat.lineNos.length-1; i++) {
                    if (currentFormat.lineNos[i] == -1) {
                        premises[i] = currentFormat.logics[logicIndex];
                        logicIndex++;
                    }
                    else
                        premises[i] = lineMaker.lines[currentFormat.lineNos[i]].statement;
                }
                
                Logic result;
                {
                    int finalIndex = currentFormat.lineNos.length-1;
                    if (currentFormat.lineNos[finalIndex] == -1)
                        result = currentFormat.logics[logicIndex];
                    else
                        result = lineMaker.lines[currentFormat.lineNos[finalIndex]].statement;
                }
                
                checkAndPrint.forConsequence(premises, result);
                return true;}
            
            case CommandButton.ID_CHECK_EQUIVALENCE: {
                if (!currentFormat.full)
                    return lineMaker.error("Key in two statements or lines");
                
                int index = 0;
                Logic[] statements = new Logic[2];
                
                for (int i=0; i<2; i++) {
                    if (currentFormat.lineNos[i] != -1) {
                        if (!lineMaker.isActualLine(currentFormat.lineNos[i]))
                            return lineMaker.error("Invalid line referenced");
                        statements[index] = lineMaker.lines[currentFormat.lineNos[i]].statement;
                        index++;
                    }
                    
                    if (currentFormat.logics[i] != null) {
                        statements[index] = currentFormat.logics[i];
                        index++;
                    }
                }
                if (index != 2)
                    System.out.println("SOMETHING WRONG WITH EQUIVALENCE CHECK");
                
                checkAndPrint.forEquivalence(statements[0], statements[1]);
                return true;}
                        
        }
        
        return true;
    }
    
    private boolean isNumber(char c) {
        return (c >= '0' && c <= '9');
    }
    
    private void formatNumbers(StringBuilder sb) {
        int startIndex = -1;
        boolean insideTags = false;
        
        for (int i=0; i<=sb.length(); i++) {
            char c = ' ';
            if (i < sb.length())
                c = sb.charAt(i);
            
            if (c == '<')
                insideTags = true;
            else if (c == '>') {
                insideTags = false;continue;
            }
            if (insideTags) continue;
            
            if (isNumber(c)) {
                startIndex = i;
            }
            else {
                if (startIndex != -1) {
                    String before = "<sub>";
                    String after = "</sub>";
                    sb.insert(i, after);
                    sb.insert(startIndex, before);
                    i += before.length() + after.length();
                    
                    startIndex = -1;
                }
            }
        }
    }
    
    public void updateInputField() {
        inputFieldCM.setText(inputFieldText(true));
        
        /*int length = inputFieldCM.getDocument().getLength();
        inputFieldCM.setCaretPosition(length == 0 ? 0 : length-1);*/
        
        
        if (readMode != CommandButton.ID_CLEAR)
            setHintTooltip(hintboxFieldText());
    }
        
    public String inputFieldText(boolean formatted) {
        if (readMode == CommandButton.ID_CLEAR)
            return "";
        
        StringBuilder sb = new StringBuilder();
        
        if (!formatted) {
            sb.append(headerString);
            sb.append(" ");
            sb.append(currentFormat.toString(false));
            return sb.toString();
        }
        //sb.append("<pre>   </pre><html><font face=\"monospaced\">");
        sb.append("<html>&nbsp;&nbsp;<font face=\"monospaced\">");
        sb.append(Op.maybeLatex(headerString));
        formatNumbers(sb);
        sb.append("&nbsp;<font color=\"#404040\">");
        sb.append(currentFormat.toString(true));
        sb.append("</font>");
        sb.append("</font></html>");
        
        return sb.toString();
    }
    
    public String hintboxFieldText() {
        // Returns the text that should appear in the hint box.
        // readMode represents command ID.
        
        // If it is a rule, readMode >= 0
        if (readMode >= 0) {
            StringBuilder text = new StringBuilder();
            text.append(ruleSet.getRuleName(readMode));
            text.append("<br><br>");
            if (currentFormat.full) {
                text.append(ruleSet.getRuleString(readMode, false, 0, true));
                text.append("<br>");
                text.append("Press OK");
            }
            else {
                int index = currentFormat.getIndex();
                text.append(ruleSet.getRuleString(readMode, true, index, true));
                text.append("<br>");
                text.append(currentFormat.getNextInputType());
                //text.append(": ").append(ruleSet.getTokenFromIndex(readMode, index));
            }
            
            return text.toString();
        }
        
        // If it is something else, readMode < 0
        else {
            StringBuilder text = new StringBuilder();
            
            switch(readMode) {
                case CommandButton.ID_CLEAR:
                    return null;
                    
                case CommandButton.ID_PREMISE: {
                    if (!currentFormat.full)
                        text.append("Create Premise");
                    else {
                        text.append("Create Premise: ").append(currentFormat.logics[0].toString(true));
                        text.append("<br><br>Press OK");
                    }
                    break;
                }
                    
                case CommandButton.ID_ASSUME: {
                    if (!currentFormat.full)
                        text.append("Create Assumption");
                    else {
                        text.append("Create Assumption: ").append(currentFormat.logics[0].toString(true));
                        text.append("<br><br>Press OK");
                    }
                    break;
                }
                
                case CommandButton.ID_OBJECTIVE: {
                    if (!currentFormat.full)
                        text.append("Set Objective to: null");
                    else {
                        text.append("Set Objective to: ").append(currentFormat.logics[0].toString(true));
                        text.append("<br><br>Press OK");
                    }
                    break;
                }
                    
                case CommandButton.ID_TRY: {
                    text.append("Try for possible rules");
                    if (currentFormat.full)
                        text.append("<br><br>Press OK");
                    break;
                }
                    
                case CommandButton.ID_CHECK_CONSEQUENCE:
                    text.append("Check: Consequence");
                    break;
                    
                case CommandButton.ID_CHECK_EQUIVALENCE:
                    text.append("Check: Equivalence");
                    break;
                    
                case CommandButton.ID_CHECK_CONTRADICTION:
                    text.append("Check: Contradiction");
                    break;
                    
                case CommandButton.ID_CHECK_TAUTOLOGY:
                    text.append("Check: Tautology");
                    break;
                    
                default:
                    return null;
            }
            return text.toString();
        }
    }
    
    public void setHintTooltip(String message) {
        printer.clearHints();
        if (message != null)
            printer.setHintText(message);
    }

}


class OkButton extends JButton implements ActionListener {
    
    ClickModeButtons commandButtonPanel;
    
    public OkButton(ClickModeButtons commandButtonPanel) {
        this.commandButtonPanel = commandButtonPanel;
        setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        setMargin(new Insets(0, 0, 0, 0));
        setText("OK");
        addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        commandButtonPanel.pressOk();
    }
    
}