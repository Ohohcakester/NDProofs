package ndproofs.proof;

// Proofs by Natural Deduction

import ndproofs.logic.LineClass;
import ndproofs.logic.Logic;
import ndproofs.logic.Op;
import ndproofs.logic.LogicAnalyser;
import ndproofs.logic.LogicTree;
import ndproofs.swingwindow.NDConfig;
import ndproofs.swingwindow.main.NDSwingWindow;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import ndproofs.puzzle.ArgumentReader;
import ndproofs.puzzle.ArgumentReaderConseq;
import ndproofs.logic.CheckAndPrint;
import ndproofs.puzzle.FileIO;
import ndproofs.puzzle.InbuiltPuzzles;
import ndproofs.swingwindow.Printer;

// Made by Oh
public class LineMaker extends ProofData{
    public static final int MAX_LAYER = 100;
    public static final int MAX_LINES = 2000;

    //public InputTextBox textConsole;
    public Printer printer; // Uninitialized
    public InbuiltPuzzles inbuiltPuzzles; // Uninitialized
    
    public boolean premiseEditable = true;
    
    public boolean complete;
    public Logic objective;
    
    public CheckAndPrint checkAndPrint; // Uninitialized
    
    public LineMaker() {
        _curLine = 0;
        _lastHypo = new int[MAX_LAYER]; // Maximum 100 layers.
        _lastHypo[0] = 0;
        
        _curLayer = 0;
        lines = new LineClass[MAX_LINES];
        complete = false;
    }

// ACTIONS
    public void completePuzzle() {
        complete = true;
        if (inbuiltPuzzles.currentStage >= 0) {
            inbuiltPuzzles.completeCurrentStage();
        }
        printer.tutorial.completeTutorial();
    }

    public boolean makeLine(Logic varStatement, String reason) {
        if (_curLine >= MAX_LINES-1) // Don't allow further making of lines.
            return error("Unable to make additional lines");
        
        _curLine++;
        lines[_curLine] = new LineClass(_curLine, varStatement, _lastHypo[_curLayer], _curLayer, reason);
        printer.tutorial.makeLineAction();
        return true;
    }//m.a.d.e.b.y.o.h

    public boolean makeHypo(Logic hypoStatement) {
        
        if (_curLayer >= MAX_LAYER-1) // Don't allow further making of layers.
            return error("Unable to increase layer further");
        if (_curLine >= MAX_LINES-1) // Don't allow further making of lines.
            return error("Unable to make additional lines");
        
        _curLayer++;

        _lastHypo[_curLayer] = _curLine + 1;
        makeLine(hypoStatement, LineClass.hypothesisH);
        return true;
    }
    
    public void clearEverything() {
        objective = null;
        inbuiltPuzzles.freeMode(true);
        while (_curLine > 0)
            undo();
    }
    
    public void clearCurrentProof() {
        printer.refreshBefore();
        
        // Read input
        boolean canUndo = true;
        while (canUndo)
            canUndo = tryUndo(false);
        
        printer.refreshAfter();
    }
    
    
    public boolean undo() {
        // Returns true iff undo did something.
        
        // If in "Complete" status, undo complete.
        if (complete) {
            complete = false;
            return true;
        }

        // If already at starting lines, return.
        if (_curLine == 0) {
            return false;
        }
        
        //If curLayer < layer of last lines, then curLayer++
        //Else delete the last lines
        if (_curLayer != lines[_curLine].layer) {
            _curLayer = lines[_curLine].layer;
            printer.tutorial.downOneLevelAction();
            return true;
        }
        
        //ELSE: Same layer. So undo means delete lines.
        // Removing lines;
        _curLine--;
        if (_curLine != 0) {
            _curLayer = lines[_curLine].layer; // Set layer to layer of last lines
            _lastHypo[_curLayer] = lines[_curLine].hypoLine;
        } else {
            _curLayer = 0; // Set layer to 0 cause no last lines.
        }
        printer.tutorial.undoAction();
        return true;
    }
    
    public boolean tryUndo(boolean print) {
        // Returns true iff something is done.
        if (_curLine == 0)
            return false;
        
        if (!premiseEditable)
            if (lines[_curLine].justification.equals(LineClass.premiseP))
                return print? error("Premise cannot be changed"):false;

        return undo();
    }
    
    public boolean tryQed(boolean print) {
        if (objective == null)
            return print? error("No objective specified"):false;
        
        if (_curLine == 0)
            return print? error("Last line does not match objective"):false;

        if (lines[_curLine].statement.equals(objective)) {
            if (lines[_curLine].layer != 0)
                return print? error("Unable to conclude from an assumption"):false;
            completePuzzle();
            return true;
        }
        return print? error("Last line does not match objective"):false;

    }
    
    @Override
    public boolean downOneLayer() {
        boolean temp = super.downOneLayer();
        printer.tutorial.downOneLevelAction();
        return temp;
    }
    
    /**
     * Used to parse the user's input in Console Mode.
     * 
     * possible inputs: <br>
     * a,b,c,L,1,2 are possible parameters.
     * a,b,c represent lines numbers, L represents a logical statement.
     * 1 or 2 in E^ represents taking the left or right Statement respectively.
     * 
     * @param inputString the user's input
     * @return 
     * false to not print all lines after read.<br>
     * true to print all lines after read. Generally when a statement is accepted.
     */
    public boolean read(String inputString) {
        //debugProofRead(); // TEMPORARY: DEBUGGING
        
        if (inputString == null) {
            return false;
        }
        if (inputString.length() == 0) {
            return false;
        }

        if (inputString.equals("help")) {
            printer.printCommands();
            return true;
        }
        if (inputString.equals("rules")) {
            printer.printRules();
            return true;
        }
        if (inputString.equals("back")) {
            //printAllLines();
            return true;
        }
        if (inputString.equals("quit")) {
            printer.exit();
            return false;
        }
        if (inputString.equalsIgnoreCase("d")) {
            downOneLayer();
            return true;
        }

// 0) Complete the problem (QED)
        else if (inputString.equalsIgnoreCase("QED")) {
            return tryQed(true);
        }

// 2) Undo
        // u (include int parameter to run multiple numes)
        if (inputString.equalsIgnoreCase("u")) {
            return tryUndo(true);
        }
        
// 1) Set Objective
        if (inputString.equalsIgnoreCase("O")) {
            if (complete == true)
                return error("Unable to change objective at this point");
            if (!premiseEditable)
                return error("Unable to change objective");

            objective = null;
            return true;
        }
        if (inputString.length()>=2 && inputString.substring(0,2).equalsIgnoreCase("O ")) {                
            if (inputString.length() < 3) {
                return error("Invalid input");
            }
            
            String argString = inputString.substring(2, inputString.length());
            if (LogicAnalyser.isLogic(argString)) {
                if (complete == true)
                    return error("Unable to change objective at this point");
                if (!premiseEditable)
                    return error("Unable to change objective");
               
                objective = LogicAnalyser.readStatement(argString);
                return true;
            }
            return error("Invalid Logic");
        }
       
        
// 2.7.1) Check Logical Consequence
        if (inputString.length()>=4 && inputString.substring(0,4).equalsIgnoreCase("CC: ")) {
            if (inputString.length() < 5)
                return error("Key in lines and statements to compare");
            
            ArgumentReader reader = new ArgumentReaderConseq(inputString.substring(4,inputString.length()));
            if (!reader.isValid(-1, -1))
                return error("Invalid Arguments");
            
            Logic[] statements = new Logic[reader.lineNoList.size()-1];
            Iterator<Logic> logicItr = reader.logicList.iterator();
            for (int i=0; i<statements.length; i++) {
                Integer lineNo = reader.lineNoList.get(i);
                
                if (lineNo == null)
                    statements[i] = logicItr.next();
                else {
                    if (isActualLine(lineNo))
                        statements[i] = lines[lineNo].statement;
                    else
                        return error("Invalid Line Referenced");
                }
            }
            
            Logic result;
            Integer lastLineNo = reader.lineNoList.get(statements.length);
            if (lastLineNo == null)
                result = logicItr.next();
            else {
                if (isActualLine(lastLineNo))
                    result = lines[lastLineNo].statement;
                else
                    return error("Invalid Line Referenced");
            }
            
            checkAndPrint.forConsequence(statements, result);
            return true;
        }
        
// 2.7.2) Check Logical Equivalence
        if (inputString.length()>=4 && inputString.substring(0,4).equalsIgnoreCase("CE: ")) {
            if (inputString.length() < 5)
                return error("Key in two lines or statements to compare");
            
            ArgumentReader reader = new ArgumentReader(inputString.substring(4,inputString.length()));
            if (!reader.isValid(2))
                return error("Invalid arguments");
            
            Logic[] statements = convertToLogics(reader.lineNoList, reader.logicList);
            if (statements == null)
                return error("Invalid line referenced");

            checkAndPrint.forEquivalence(statements[0], statements[1]);
            return true;
        }
       
// 2.7.3) Check Contradiction
        if (inputString.length()>=6 && inputString.substring(0,6).equalsIgnoreCase("Cont: ")) {
            if (inputString.length() < 7)
                return error("Key in lines to check for contradiction.");
            
            ArgumentReader reader = new ArgumentReader(inputString.substring(6,inputString.length()));
            if (!reader.isValid(-1, -1))
                return error("Invalid arguments");
            
            Logic[] statements = convertToLogics(reader.lineNoList, reader.logicList);
            if (statements == null)
                return error("invalid line referenced");
            
            checkAndPrint.forContradiction(statements);
            return true;
        }
        
// 2.7.4) Check for tautology
        if (inputString.length()>=6 && inputString.substring(0,6).equalsIgnoreCase("Taut: ")) {
            if (inputString.length() < 7)
                return error("Key in a line or logic to check.");
            
            ArgumentReader reader = new ArgumentReader(inputString.substring(6,inputString.length()));
            if (!reader.isValid(1))
                return error("Invalid arguments");
            
            Logic[] statements = convertToLogics(reader.lineNoList, reader.logicList);
            if (statements == null)
                return error("invalid line referenced");
            
            checkAndPrint.forTautology(statements[0]);
            return true;
        }
        

// 3) Make a Premise
        // P L
        if (inputString.length()>=2 && inputString.substring(0,2).equalsIgnoreCase("P ")) {
            if (inputString.length() < 3)
                return error("Key in a premise");

            String argString = inputString.substring(2, inputString.length());
            if (LogicAnalyser.isLogic(argString)) {
                if (!premiseEditable)
                    return error("Premise cannot be changed");

                if (complete || (_curLine != 0 && !lines[_curLine].justification.equals(LineClass.premiseP))) {
                    return error("Unable to make additional premises at this point");
                }
                // Last lines is not premise.
                // Generally, you can't make premises after any non-premise statement has been made.

                makeLine(LogicAnalyser.readStatement(argString), LineClass.premiseP);
                return true;
            }
            return error("Invalid logic");
        }

// 4) Make a Hypothesis
        // H L
        if (inputString.length()>=2 && inputString.substring(0,2).equalsIgnoreCase("A ")) {
            if (inputString.length() < 3)
                return error("Key in a logical statement to assume");
            
            String argString = inputString.substring(2, inputString.length());
            if (LogicAnalyser.isLogic(argString)) {
                if (complete)
                    return error("Unable to make additional statements at this point");
                makeHypo(LogicAnalyser.readStatement(argString));
                return true;
            }
            return error("Invalid logic");
        }
        
        

        // 5) Make a Line
        // IT, I^, E^, Iv, Ev, I>, E>, I~, E~
        // IT a
        // I^ a,b or b,a (different result)
        // E^ a,1
        // Iv a,L or L,a (different result)
        // Ev a,b,c (a must be a AvB statement. b and c interchangable)  
        // I> a,b   (a must be a hypothesis)
        // E> a,b   (a must be A, b must be A>B) 
        // I~ a,b   (b is an implication to a contradiction)
        // E~ a
        if (inputString.length() >= 4) {
            if (complete)
                return error("Unable to make additional statements at this point");

            Logic result = ruleSet.readAndApplyRule(inputString, this);
            if (ruleSet.ruleNameMatch) {
                if (result == null) {
                    return error(ruleSet.lastError);
                }
                // If result != null
                makeLine(result, ruleSet.lastInputString);
                return true;
            }
            // else continue
        }
        
        { // Deduction testing;
            LinkedList<String> possibleRuleList = ruleSet.testRules(inputString, this);
            if (possibleRuleList != null) {
                StringBuilder sb = new StringBuilder();
                if(possibleRuleList.isEmpty()) {
                    sb.append("No possible rules for:<br> ").append(inputString);
                }
                while (!possibleRuleList.isEmpty()) {
                    sb.append(possibleRuleList.poll());
                    if (!possibleRuleList.isEmpty())
                        sb.append("<br>");
                }
                
                printer.setHintText(sb.toString());
                return true;
            }
        }

        return error("Invalid Input"); // Doesn't read anything. error
    } // LineReader.read(String inputString) - END

    
    /**
     * premises and objective cannot be null.   <br>
     * for there to be no premises, pass in an empty array. (size 0)    <br>
     * currentStage should be set before this function is called.
     */
    public void setupLevel(LogicTree[] premises, Logic objective) {
        
        if (premises == null || objective == null)
            throw new NullPointerException("Don't put null pointer into lineMaker.setupLevel");
        
        _curLine = 0;
        _curLayer = 0;
        complete = false;
        
        int nPremises = premises.length;
        
        for (int i=0; i<nPremises; i++) {
            if (premises[i].optr == Op.LOGIC)
                makeLine(premises[i].logic, LineClass.premiseP);
            else if (premises[i].optr == Op.CONSEQ) {
                NDSwingWindow.showErrorDialog("Assumptions in Premise not supported", "Puzzle Load Error");
                System.out.println("WIP. inbuiltpuzzles.setuplevel");
            }
            else {
                System.out.println("ERROR: InbuiltPuzzles.setupLevel");
            }
        }
        this.objective = objective;
        premiseEditable = false;
    }
    
    
    
    /*-------------------*
     |  P R I N T I N G  |
     *-------------------*/
    
    /**
     * @return 
     * Returns false with an error message.
     */
    public boolean error(String message) {
        printer.error(String.format("Error: %s\n", message));
        return false;
    }
    
    /**
     * @return 
     * Returns false with an error message.
     */
    public boolean errorPopup(String message) {
        NDSwingWindow.showErrorDialog(message, "Error");
        return false;
    }
 

    public void printLine(int lineNum, int labelSpacing) {
        //¬∧∨⇒
        LineClass line = lines[lineNum];
        
        printer.println(line.toString(labelSpacing, false, NDConfig.latexModeOn()));
        if (line.isHypo())
            printer.addDrawerHypo(line.layer, line.statement.toString().length(), line.justificationString(true));
        else
            printer.addDrawerLine(line.layer, line.justificationString(true));
    }

    public void printAllLines() {
        printer.clear();
        
        int n = calculateLabelSpacing(_curLine);
        printer.setDrawerSpacing(n);

        for (int i = 1; i <= _curLine; i++) {
            printLine(i, n);
        }
        
        if (complete) {
            StringBuilder lineBuild = new StringBuilder();
            for (int i=0; i<n; i++)
                lineBuild.append(" ");
            lineBuild.append(">> Q.E.D.");
            
            printer.println(lineBuild.toString());
        }
        else {
            // Print extra lines.
            StringBuilder lineBuild = new StringBuilder();
            for (int i=0; i<n; i++)
                lineBuild.append(" ");
            
            lineBuild.append("  "); // " |"
            for (int i = 0; i < _curLayer; i++) {
                lineBuild.append("   "); // "  |"
            }
            
            lineBuild.append(" ...");
            
            printer.println(lineBuild.toString());
            printer.addDrawerLine(_curLayer, null);
        }
    }
    
    
    
    
    
    public boolean importFromFile(File file) {
        ProofChecker proof = ProofTextConverter.readProofFromFile(file);
        if (proof == null)
            return error(ProofTextConverter.lastError);
            
        if (objective != null)
            proof.objective = objective;
        
        proof.checkProof();
        
        if (!proof.valid)
            return errorPopup("Unable to load proof:\n" + proof.lastError);
            //return error("Invalid proof");
        
        return loadProofData(proof);
    }
    
    public boolean loadProofData(ProofChecker proof) {
        // If premises not editable, Only allow importing of files with exactly the same premises.
        if (!premiseEditable && !this.comparePremises(proof))
            return error("Unable to edit premises");
        
        // Transfer proof data.    
        this._curLayer = proof._curLayer;
        this._curLine = proof._curLine;
        for (int i=0; i<proof._lastHypo.length; i++)
            this._lastHypo[i] = proof._lastHypo[i];
        for (int i=1; i<proof.lines.length; i++)
            this.lines[i] = new LineClass(proof.lines[i]);
        this.complete = proof.complete;
        
        return true;
    }
    
    
    public ProofChecker copyOutProofData() {
        ProofChecker proof = new ProofChecker(lines, _curLine, ruleSet, _lastHypo);
        proof.complete = this.complete;
        proof.valid = true;
        
        return proof;
    }
    
    
    public void debugProofRead() {
        File file = new File("exportTest.txt");
        exportToFile(file);
        importAndCheck(file);
    }
    
    
    public void importAndCheck(File file) {
        ProofChecker proof = ProofTextConverter.readProofFromFile(file);
        if (proof == null) {
            System.out.println("Error");
            return;
        }
        if (objective != null)
            proof.objective = objective;
        
        proof.checkProof();
        if (!proof.valid)
            System.out.println(proof.lastError);
        else
            System.out.println("Valid");
        //System.out.println(ProofTextConverter.toText(proof));
        //proof.debug();
        
        
    }
    
    public void exportToFile(File file) {
        StringBuilder fileString = ProofTextConverter.toText(this);
        FileIO.saveStringToFile(fileString.toString(), file);
    }

    public boolean hasSomethingWorthSaving() {
        // We check that by checking whether the last line is a premise. (alternatively, if it's complete, save too.)
        if (complete) return true;
        
        if (_curLine == 0) return false;
        if (lines[_curLine].justification.equals(LineClass.premiseP)) return false;
        return true;
    }
    
}