package ndproofs.swingwindow.clickmode;

import ndproofs.logic.Logic;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class FormatClass {
    private static final String LINE_PREFIX = "a";
    private static final String LOGIC_PREFIX = "L";
    
    private int itrIndex;
    private boolean nextIsInt; // Default: true.
    public int[] lineNos;
    public Logic[] logics;
    public boolean full;
    
    public FormatClass(){}
    
    public FormatClass(int nLines, int nLogics) {
        lineNos = new int[nLines];
        logics = new Logic[nLogics];
       
        if (nLines == 0)
            nextIsInt = false;
        else
            nextIsInt = true;
        
        if (nLines != 0 || nLogics != 0)
            full = false;
        
        itrIndex = 0;
    }
    
    public boolean addInt(int newInt) {
        if (!nextIsInt)
            return false;
        
        // Next is Int
        lineNos[itrIndex] = newInt;
        itrIndex++;
        if (itrIndex >= lineNos.length) {
            nextIsInt = false;
            itrIndex = 0;
            
            if (logics.length == 0)
                full = true;
        }
        
        return true;
    }
    
    public boolean addLogic(Logic newLogic) {
        if (nextIsInt)
            return false;
        
        if (itrIndex >= logics.length)
            return false;
        
        // Next is Logic
        logics[itrIndex] = newLogic;
        itrIndex++;
        if (itrIndex >= logics.length)
            full = true;
        
        return true;
    }
    
    public int getIndex() {
        if (nextIsInt)
            return itrIndex;
        else
            return lineNos.length + itrIndex;
    }
    
    public String getNextInputType() {
        if (nextIsInt)
            return "<font size=4><i>add line</i></font>";
        else
            return "<font size=4><i>add logic</i></font>";
    }
    
    /*public String getArgumentName() {
        if (nextIsInt)
            return LINE_PREFIX + (itrIndex+1);
        else
            return LOGIC_PREFIX + (itrIndex+1);
    }*/
    
    public boolean undo() {
        if (itrIndex == 0) {
            if (nextIsInt)
                return false;
            if (lineNos.length == 0)
                return false;
            
            /*if (logics.length != 0)
                logics[0] = null;*/
            nextIsInt = true;
            itrIndex = lineNos.length-1;
            lineNos[itrIndex] = 0;
            full = false;
            return true;
        }
        
        // itrIndex != 0
        itrIndex--;
        if (nextIsInt)
            lineNos[itrIndex] = 0;
        else
            logics[itrIndex] = null;
        
        full = false;
        return true;
    }
    
    
    protected void appendLinePrefix(StringBuilder sb, int number, boolean formatted) {
        sb.append(LINE_PREFIX);
        if (formatted)
            sb.append("<sub>").append(number).append("</sub>");
        else
            sb.append(number);
    }
    
    protected void appendLogicPrefix(StringBuilder sb, int number, boolean formatted) {
        sb.append(LOGIC_PREFIX);
        if (formatted)
            sb.append("<sub>").append(number).append("</sub>");
        else
            sb.append(number);
    }
    
    @Override
    public final String toString() {
        return toString(false);
    }
    
    public String toString(boolean formatted) {
        if (lineNos.length == 0 && logics.length == 0)
            return "";
        
        StringBuilder sb = new StringBuilder();
        
        boolean first = formatted;
        
        for (int i=0; i<lineNos.length; i++) {
            if (lineNos[i] != 0)
                sb.append(lineNos[i]);
            else {
                if (first) sb.append(textHighlightHead());
                appendLinePrefix(sb, i+1, formatted);
                if (first) {sb.append(textHighlightTail()); first = false;}
            }
            sb.append(",");
        }
        
        for (int i=0; i<logics.length; i++) {
            if (logics[i] != null)
                sb.append(logics[i].toString(formatted));
            else {
                if (first) sb.append(textHighlightHead());
                appendLogicPrefix(sb, i+1, formatted);
                if (first) {sb.append(textHighlightTail()); first = false;}
            }
            sb.append(",");
        }
        
        return sb.substring(0,sb.length()-1);
    }

    public void copyInfoToArrays() {} // template class
    
    
    protected static String textHighlightHead() {
        return "<font color=\"#ff0000\">";
    }
    
    protected static String textHighlightTail() {
        return "</font>";
    }
}


class LineOnlyFormatClass extends FormatClass {
    public ArrayList<Integer> lineNoList;
    // full == 1 iff there is at least one line.
    // full == 0 iff there are no inputs.
    
    public LineOnlyFormatClass() {
        lineNoList = new ArrayList<>();
        full = false;
    }
    
    @Override
    public boolean addInt(int newInt) {
        lineNoList.add(newInt);
        full = true;
        return true;
    }
    
    @Override
    public boolean addLogic(Logic newLogic) {
        return false;
    }
    
    @Override
    public boolean undo() {
        if (lineNoList.isEmpty())
            return false;
        
        // Remove last
        lineNoList.remove(lineNoList.size()-1);
        if (lineNoList.isEmpty())
            full = false;
        return true;
    }
    
    @Override
    public String toString(boolean formatted) {
        if (lineNoList.isEmpty())
            return "";
        
        StringBuilder sb = new StringBuilder();
        
        int nLines = lineNoList.size();
        for (int i=0; i<nLines; i++) {
            sb.append(lineNoList.get(i)).append(",");
        }
        
        return sb.substring(0,sb.length()-1);
    }
    
    @Override
    public void copyInfoToArrays() {
        ListIterator<Integer> itr = lineNoList.listIterator();
        lineNos = new int[lineNoList.size()];
        
        int i=0;
        while (itr.hasNext()) {
            lineNos[i] = itr.next();
            i++;
        }
    }
}


class ConseqFormatClass extends FormatClass {
    LinkedList<Integer> lineNoList;
    LinkedList<Logic> logicList;
    
    public ConseqFormatClass() {
        lineNoList = new LinkedList<>(); // Main list
        logicList = new LinkedList<>(); // Secondary list.
        full = false; // full if there is at least one object in the list.
    }
    
    @Override
    public boolean addInt(int newInt) {
        lineNoList.add(newInt);
        full = true;
        return true;
    }
    
    @Override
    public boolean addLogic(Logic newLogic) {
        lineNoList.add(null);
        logicList.add(newLogic);
        full = true;
        return true;
    }
    
    @Override
    public boolean undo() {
        if (lineNoList.isEmpty())
            return false;
        
        if (lineNoList.peekLast() == null)
            logicList.removeLast();
        lineNoList.removeLast();
        
        if (lineNoList.isEmpty())
            full = false;
        return true;
    }
    
    @Override
    public String toString(boolean formatted) {
        StringBuilder strBuild = new StringBuilder();
        
        Iterator<Logic> logicItr = logicList.iterator();
        Iterator<Integer> lineItr = lineNoList.iterator();
        while (lineItr.hasNext()) {
            Integer line = lineItr.next();
            boolean last = !lineItr.hasNext();
            
            if (last) strBuild.append("<font color=\"#0000ff\"><b>");
            
            if (line == null)
                strBuild.append(logicItr.next().toString(formatted));
            else
                strBuild.append(line);
            
            if (last) strBuild.append("</b></font>");
            else strBuild.append(",");
        }
        
        return strBuild.toString();
    }
    
    @Override
    public void copyInfoToArrays() {
        ListIterator<Integer> itrLine = lineNoList.listIterator();
        lineNos = new int[lineNoList.size()];
        ListIterator<Logic> itrLogic = logicList.listIterator();
        logics = new Logic[logicList.size()];
        
        int i=0;
        while (itrLine.hasNext()) {
            Integer next = itrLine.next();
            lineNos[i] = next == null ? -1 : next;
            i++;
        }
        i=0;
        while (itrLogic.hasNext()) {
            logics[i] = itrLogic.next();
            i++;
        }
    }
}


class TwoItemsFormatClass extends FormatClass {
    int numElements;
    
    public TwoItemsFormatClass() {
        numElements = 0;
        logics = new Logic[2];
        lineNos = new int[2];
        full = false;
        
        for (int i=0; i<2; i++) {
            logics[i] = null;
            lineNos[i] = -1;
        }
    }
    
    @Override
    public boolean addInt(int newInt) {
        if (numElements >= 2)
            return false;
        
        if (lineNos[0] == -1) 
            lineNos[0] = newInt;
        else
            lineNos[1] = newInt;
        
        numElements++;
        if (numElements == 2)
            full = true;
        
        return true;
    }
    
    @Override
    public boolean addLogic(Logic newLogic) {
        if (numElements >= 2)
            return false;
        
        if (logics[0] == null)
            logics[0] = newLogic;
        else
            logics[1] = newLogic;
        
        numElements++;
        if (numElements == 2)
            full = true;
        
        return true;
    }
    
    @Override
    public boolean undo() {
        if (numElements <= 0)
            return false;
        
        if (logics[1] != null)
            logics[1] = null;
        else if (logics[0] != null)
            logics[0] = null;
        else if (lineNos[1] != -1)
            lineNos[1] = -1;
        else
            lineNos[0] = -1;
        
        if (numElements == 2)
            full = false;
        
        numElements--;
        return true;
    }
    
    @Override
    public String toString(boolean formatted) {
        if (numElements == 0)
            return "";
        
        StringBuilder strBuild = new StringBuilder();
        
        for (int i=0; i<2; i++) {
            if (lineNos[i] != -1) {
                strBuild.append(lineNos[i]);
                strBuild.append(",");
            }
        }
        
        for (int i=0; i<2; i++) {
            if (logics[i] != null) {
                strBuild.append(logics[i]);
                strBuild.append(",");
            }
        }
        
        strBuild.deleteCharAt(strBuild.length()-1);
        
        return strBuild.toString();
    }
}


class OneItemFormatClass extends FormatClass {
    
    public OneItemFormatClass() {
        logics = new Logic[1];
        lineNos = new int[1];
        full = false;
        
        lineNos[0] = -1;
        logics[0] = null;
    }
    
    @Override
    public boolean addInt(int newInt) {
        if (full)
            return false;
        
        lineNos[0] = newInt;
        full = true;
        
        return true;
    }
    
    @Override
    public boolean addLogic(Logic newLogic) {
        if (full)
            return false;
            
        logics[0] = newLogic;
        full = true;
        
        return true;
    }
    
    @Override
    public boolean undo() {
        if (!full)
            return false;
        
        logics[0] = null;
        lineNos[0] = -1;
        full = true;
        
        return true;
    }
    
    @Override
    public String toString(boolean formatted) {
        if (!full)
            return "";
        
        if (lineNos[0] != -1)
            return "" + lineNos[0];
        else
            return logics[0].toString(formatted);
        
    }
}


class FreeFormatClass extends FormatClass {
    LinkedList<Integer> lineNoList;
    LinkedList<Logic> logicList;
    
    public FreeFormatClass() {
        lineNoList = new LinkedList<>();
        logicList = new LinkedList<>();
        // full is not used.
    }
    
    @Override
    public boolean addInt(int newInt) {
        lineNoList.add(newInt);
        return true;
    }
    
    @Override
    public boolean addLogic(Logic newLogic) {
        logicList.add(newLogic);
        return true;
    }
    
    @Override
    public boolean undo() {
        if (!logicList.isEmpty()) {
            logicList.removeLast();
            return true;
        }
        
        // logicList is empty
        
        if (lineNoList.isEmpty()) {
            return false;
        }
        
        lineNoList.removeLast();
        return true;
    }
    
    @Override
    public String toString(boolean formatted) {
        if (lineNoList.isEmpty() && logicList.isEmpty())
            return "";
        
        StringBuilder strBuild = new StringBuilder();
        
        ListIterator<Integer> lineItr = lineNoList.listIterator();
        ListIterator<Logic> logicItr = logicList.listIterator();
        
        while(lineItr.hasNext()) {
            strBuild.append(lineItr.next());
            strBuild.append(",");
        }
        
        while(logicItr.hasNext()) {
            strBuild.append(logicItr.next().toString(formatted));
            strBuild.append(",");
        }
        
        strBuild.deleteCharAt(strBuild.length()-1);
        
        return strBuild.toString();
    }
    
    @Override
    public void copyInfoToArrays() {
        ListIterator<Integer> lineItr = lineNoList.listIterator();
        lineNos = new int[lineNoList.size()];
        
        int i=0;
        while (lineItr.hasNext()) {
            lineNos[i] = lineItr.next();
            i++;
        }
        
        ListIterator<Logic> logicItr = logicList.listIterator();
        logics = new Logic[logicList.size()];
        
        i=0;
        while (logicItr.hasNext()) {
            logics[i] = logicItr.next();
            i++;
        }
    }
}