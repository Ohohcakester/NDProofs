package ndproofs.puzzle;

import ndproofs.proof.LineMaker;
import ndproofs.logic.LineClass;
import ndproofs.logic.Logic;
import ndproofs.logic.Op;
import java.util.ArrayList;
import ndproofs.swingwindow.Printer;

/**
 * Manages the tutorial mode.
 * 
 * @author Oh
 */
public class TutorialController {
    private final InbuiltPuzzles inbuiltPuzzles;
    private final LineMaker lineMaker;
    private final Printer printer;
    private final PuzzleInterpreter puzzle1;
    private final PuzzleInterpreter puzzle2;
    
    private ArrayList<Logic> logicList;
    
    private int puzzleNo;
    private int sequenceNo;
    
    private String currentHint;
    
    public TutorialController(InbuiltPuzzles inbuiltPuzzles, LineMaker lineMaker, Printer printer) {
        puzzle1 = new PuzzleInterpreter("A, (AvC)>B |- A^B");
        puzzle2 = new PuzzleInterpreter("A, ~A |- B");
        this.inbuiltPuzzles = inbuiltPuzzles;
        this.lineMaker = lineMaker;
        this.printer = printer;
    }
    
    public boolean tutorialMode() {
        return inbuiltPuzzles.tutorialMode();
    }
    
    public String getHint() {
        return currentHint;
    }
    
    public void setupTutorial1() {
        inbuiltPuzzles.saveProgressToMemory();
        inbuiltPuzzles.stageSelect(puzzle1, -2, null, -1);
        puzzleNo = 1;
        sequenceNo = 0;
        logicList = new ArrayList<>();
        
        // 0 = AvC,  1 = B,  2 = A^B
        logicList.add(new Logic(Op.DIS, new Logic("A"), new Logic("C")));
        logicList.add(new Logic("B"));
        logicList.add(new Logic(Op.CON, new Logic("A"), logicList.get(1)));
        
        t1seq0Hint();
        
        /*
        Tutorial 1 Sequence Number Guide:
        A, (AvC)>B |- A^B
        seq 0: Starting     -> tell user to make AvC
        seq 1: AvC          -> E> to make B
        seq 2: B            -> I^ to make A^B
        seq 3: A^B          -> QED
        
        Things that could go wrong & response:
        seq0-3- Made assumption (layer >= 1)     >> recommend undo
        seq3 - made additional lines             >> recommend undo
        
        Undo: check if last sequence number action was undid.
        */
    }
    
    public void setupTutorial2() {
        inbuiltPuzzles.saveProgressToMemory();
        inbuiltPuzzles.stageSelect(puzzle2, -2, null, -1);
        puzzleNo = 2;
        sequenceNo = 0;
        logicList = new ArrayList<>();
        
        // 0 = ~B,   1 = A^~A,   2 = ~B>(A^~A),
        // 3 = ~~B,  4 = B       5 = ~A^A
        {
            Logic B = new Logic("B");
            Logic A = new Logic("A");
            logicList.add(new Logic(Op.NOT, B)); // ~B
            logicList.add(new Logic(Op.CON, A, new Logic(Op.NOT, A))); // A^~A
            logicList.add(new Logic(Op.IMP, logicList.get(0), logicList.get(1)));
            logicList.add(new Logic(Op.NOT, logicList.get(0)));
            logicList.add(B);
            logicList.add(new Logic(Op.CON, new Logic(Op.NOT, A), A));
        }
        
        t2seq0Hint();
        
        /*
        Tutorial 2 Sequence Number Guide:
        A, ~A |- B
        seq 0: Starting             -> tell user to test for contradiction.
        seq 1: Contradiction tested -> assume ~B
        seq 2: Assumed ~B           -> I^ to make A^~A
        seq 3: A^~A                 -> I> to make ~B>(A^~A)
        seq 4: ~B>(A^~A)            -> E> to make ~~B
        seq 5: ~~B                  -> E~ to make B
        seq 6: B                    -> QED
        
        Things that could go wrong & response:
        seq0- assumed ~B.                     >> jump straight to seq 2.
        seq1- assume something other than ~B  >> recommend undo.
        seq2- went to layer 2 and above       >> recommend undo.
            - went back to layer 0            >> recommend undo.
            - made ~A^A                       >> A^~A is required, not ~A^A.
        seq3- went to layer 2 and above       >> recommend undo.
        seq4-5- went to layer 1 and above     >> recommend undo.
        seq6- made additional lines           >> recommend undo.
        
        Undo: if seq!=0 check if last sequence number action was undid.
        */
    }
    
    private void t2seq0Hint() {
        setHint("We note that the statements A and "+Op.maybeLatex("~A")+" are contradictory. With a contradiction, we can prove anything." +
                "<br>Let us demonstrate that A and "+Op.maybeLatex("~A")+" form a contradiction." +
                "<br><br>Click the drop-down box below (Click Mode) and select contradiction. (Use the command \"cont:\" for console mode)" +
                "<br>Test the first 2 lines to show that they contradict.");
    }
    
    private void t2seq1Hint() {
        LineClass lastLine = lineMaker.lines[lineMaker._curLine];
        if (lastLine.layer >= 1 && !lastLine.statement.equals(logicList.get(0))) {
            setHint(Op.maybeLatex("You made an unneeded assumption. Press undo and assume ~B instead."));
        }
        else if (lastLine.layer >=2) { // && lastLine.statement.equals(logicList.get(0))
            // Made an assumption of ~B but on the wrong layer.
            setHint("You made an assumption of "+Op.maybeLatex("~B")+" on the wrong layer." +
                    "Press undo until you return to layer 0 and assume "+Op.maybeLatex("~B")+".");
        }
        else
            setHint("Make an assumption of "+Op.maybeLatex("~B")+".<br>We will attempt to prove B this way." +
                    "<br><br>Use the Assume button for Click mode." +
                    "<br>Use the \"A\" command for Console mode.");
    }
    
    private void t2seq2Hint() {
        LineClass lastLine = lineMaker.lines[lineMaker._curLine];
        if (lastLine.layer >= 2)
            youMadeAnUnneededAssumption();
        else if (lineMaker._curLayer == 0)
            setHint("You returned to layer 0. Press undo to return to layer 1.");
        else if (lastLine.statement.equals(logicList.get(5)))
            setHint(Op.maybeLatex("You made ~A^A instead. A^~A is required, not ~A^A."));
        else if (!lineMaker.lines[lastLine.hypoLine].statement.equals(logicList.get(0)))
            setHint("You are on the correct layer, but any further statements you make from here will not be" +
                    Op.maybeLatex(" a consequence of ~B. Recommend pressing undo.") + 
                    "<br><br>Note: In this program, use of the \":\" character instead of a \"|\" beside the statement indicates an assumption.");
        else
            setHint(Op.maybeLatex("Use Conjunction Introduction [I^] on A and ~A to make A^~A"));
    }
    
    private void t2seq3Hint() {
        LineClass lastLine = lineMaker.lines[lineMaker._curLine];
        if (lastLine.layer >= 2)
            youMadeAnUnneededAssumption();
        else
            setHint(Op.maybeLatex("Use Implication Introduction [I>] on ~B and A^~A to make ~B>(A^~A)"));
    }
    
    private void t2seq4Hint() {
        if (!TestAssumptionComplaint())
            setHint(Op.maybeLatex("Use Negation Introduction [I~] on ~B>(A^~A) to make ~~B"));
    }
    
    private void t2seq5Hint() {
        if (!TestAssumptionComplaint())
            setHint(Op.maybeLatex("Use Negation Elimination [E~] on ~~B to make B"));
    }
    
    private void youMadeAnUnneededAssumption() {
        setHint("You made an unneeded assumption. Press undo (or type \"u\") to undo your last move.");}
    private void youHaveAlreadyCompletedThePuzzle() {
        setHint("You have already completed the puzzle, and do not need to make any more lines. Press undo (or type \"u\") to undo the extra line(s) you made.");}
    
    private void t1seq0Hint() {
        if (!TestAssumptionComplaint())
            setHint(Op.maybeLatex("First, we use Disjunction Introduction (left) [Iv1] on line 1 and C to make AvC") +
                    "<br><br>[Click Mode] Click the "+Op.maybeLatex("Iv1")+" button, then on line 1, then press the Logic button and enter \"C\". Then press Ok." +
                    "<br><br>[Console Mode] Type \"Iv1 1,C\"<br><br>");
    }
    private void t1seq1Hint() {
        if (!TestAssumptionComplaint())
            setHint(Op.maybeLatex("Use Implication Elimination [E>] on AvC and (AvC)>B to make B"));
    }
    private void t1seq2Hint() {
        if (!TestAssumptionComplaint())
            setHint(Op.maybeLatex("Use Conjunction Introduction [I^] on A and B to make A^B"));
    }
    private void qedHint() {
        int qedLogicIndex = (puzzleNo == 1) ? 2 : 4;
        LineClass lastLine = lineMaker.lines[lineMaker._curLine];
        if (lastLine.layer != 0 || !lastLine.statement.equals(logicList.get(qedLogicIndex)))
            youHaveAlreadyCompletedThePuzzle();
        else
            setHint("Press the QED button (or type \"qed\" in console mode) to complete the proof!");
    }

    private void setHint(String hint) {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color=\"#000080\">");
        sb.append(hint);
        sb.append("</font>");
        currentHint = sb.toString();
        printer.clearHints();
    }
    
    public boolean TestAssumptionComplaint() {
        LineClass lastLine = lineMaker.lines[lineMaker._curLine];
        if (lastLine.layer >= 1) {
            youMadeAnUnneededAssumption();
            return true;
        }
        return false;
    }

    public void makeLineAction() {
        // Called from LineMaker.makeLine(Logic varStatement, String reason);
        if (!tutorialMode()) return;
        
        LineClass lastLine = lineMaker.lines[lineMaker._curLine];
        
        if (puzzleNo == 1) { // PUZZLE 1
            // 0 = AvC,  1 = B,  2 = A^B
            if (sequenceNo == 0) {
                if (lastLine.layer == 0 && lastLine.statement.equals(logicList.get(0))) {
                    sequenceNo = 1;
                    t1seq1Hint();
                }
                else
                    t1seq0Hint();
            }
            else if (sequenceNo == 1) {
                if (lastLine.layer == 0 && lastLine.statement.equals(logicList.get(1))) {
                    sequenceNo = 2;
                    t1seq2Hint();
                }
                else
                    t1seq1Hint();
            }
            else if (sequenceNo == 2) {
                if (lastLine.layer == 0 && lastLine.statement.equals(logicList.get(2))) {
                    sequenceNo = 3;
                    qedHint();
                }
                else
                    t1seq2Hint();
            }
            else if (sequenceNo == 3) {
                qedHint();
            }
        } // PUZZLE 1 - END
        
        else { // puzzleNo == 2   // PUZZLE 2
            // 0 = ~B,   1 = A^~A,   2 = ~B>(A^~A),
            // 3 = ~~B,  4 = B
            if (sequenceNo <= 1) {
                if (lastLine.layer == 1 && lastLine.statement.equals(logicList.get(0))) {
                    sequenceNo = 2;
                    t2seq2Hint();
                }
                else {
                    if (sequenceNo == 0)
                        t2seq0Hint();
                    else
                        t2seq1Hint();
                }
            }
            else if (sequenceNo == 2) {
                if (lastLine.layer == 1 && lastLine.statement.equals(logicList.get(1)) &&
                        lineMaker.lines[lastLine.hypoLine].statement.equals(logicList.get(0))) {
                    // Last line has A^~A with its backing assumption as ~B.
                    sequenceNo = 3;
                    t2seq3Hint();
                }
                else
                    t2seq2Hint();
            }
            else if (sequenceNo == 3) {
                if (lastLine.layer == 0 && lastLine.statement.equals(logicList.get(2))) {
                    sequenceNo = 4;
                    t2seq4Hint();
                }
                else
                    t2seq3Hint();
            }
            else if (sequenceNo == 4) {
                if (lastLine.layer == 0 && lastLine.statement.equals(logicList.get(3))) {
                    sequenceNo = 5;
                    t2seq5Hint();
                }
                else
                    t2seq4Hint();
            }
            else if (sequenceNo == 5) {
                if (lastLine.layer == 0 && lastLine.statement.equals(logicList.get(4))) {
                    sequenceNo = 6;
                    qedHint();
                }
                else
                    t2seq5Hint();
            }
            else if (sequenceNo == 6) {
                qedHint();
            }
            
        } // PUZZLE 2 - END
    }
    
    public void undoAction() {
        // Called from LineMaker.undo();
        if (!tutorialMode()) return;
        
        if (puzzleNo == 1) { // PUZZLE 1
            if (sequenceNo == 3) {
                if (lineMaker.contains(logicList.get(2), 0)) {
                    qedHint();
                }
                else {
                    sequenceNo = 2;
                }
            }
            if (sequenceNo == 2) {
                if (lineMaker.contains(logicList.get(1), 0)) {
                    t1seq2Hint();
                }
                else {
                    sequenceNo = 1;
                }
            }
            if (sequenceNo == 1) {
                if (lineMaker.contains(logicList.get(0), 0)) {
                    t1seq1Hint();
                }
                else {
                    sequenceNo = 0;
                }
            }
            if (sequenceNo == 0) {
                t1seq0Hint();
            }
        } // PUZZLE 1 - END
        
        else { // if (puzzleNo == 2) { // PUZZLE 2
            // 0 = ~B,   1 = A^~A,   2 = ~B>(A^~A),
            // 3 = ~~B,  4 = B
            if (sequenceNo == 6) {
                if (lineMaker.contains(logicList.get(4), 0)) {
                    qedHint();
                }
                else {
                    sequenceNo = 5;
                }
            }
            if (sequenceNo == 5) {
                if (lineMaker.contains(logicList.get(3), 0)) {
                    t2seq5Hint();
                }
                else {
                    sequenceNo = 4;
                }
            }
            if (sequenceNo == 4) {
                if (lineMaker.contains(logicList.get(2), 0)) {
                    t2seq4Hint();
                }
                else {
                    sequenceNo = 3;
                }
            }
            if (sequenceNo == 3) {
                if (lineMaker.containsWithHypo(logicList.get(1), 1, logicList.get(0))) {
                    // Have an A^~A with its backing assumption as ~B
                    t2seq3Hint();
                }
                else {
                    sequenceNo = 2;
                }
            }
            if (sequenceNo == 2) {
                if (lineMaker.contains(logicList.get(0), 1)) {
                    t2seq2Hint();
                }
                else {
                    sequenceNo = 1;
                }
            }
            if (sequenceNo == 1) {
                t2seq1Hint();
            }
            else if (sequenceNo == 0) {
                t2seq0Hint();
            }
        } // PUZZLE 2 - END
    }

    public void madeContradictionAction() {
        // Called from CheckAndPrint.forContradiction(Logic[] premises);
        if (!tutorialMode()) return;
        if (puzzleNo != 2 || sequenceNo != 0) return;
        
        // SequenceNo == 0
        sequenceNo = 1;
        t2seq1Hint();
    }
    
    public void downOneLevelAction() {
        // Called from LineMaker.downOneLayer(); and LineMaker.undo();
        if (!tutorialMode()) return;
        if (puzzleNo != 2 || sequenceNo != 2) return;
        t2seq2Hint();
    }
    
    public void completeTutorial() {
        if (!tutorialMode()) return;
        
        printer.setHintText("Congratulations, you have completed the tutorial.");
        logicList = null;
        inbuiltPuzzles.freeMode(false);
    }
}
