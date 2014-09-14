package ndproofs.swingwindow;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedList;


public abstract class LayerLineDrawer {
    LayerLineSet layerLineSet;
    Graphics g;
    int xOffset;
    
    abstract void drawHorizontal(LayerLineHorizontal line);
    abstract void drawVertical(LayerLineVertical line);
    abstract void drawJustification(LayerJustification justification);
    
    public final void drawLines(Graphics g) {
        this.g = g;
        this.xOffset = layerLineSet.getLabelSpacing()*8 + 3;
        
        for(LinkedList<LayerLineVertical> verticalList : layerLineSet.verticalList) {
            for (LayerLineVertical line : verticalList)
                drawVertical(line);
        }
        for (LayerLineHorizontal line : layerLineSet.horizontalList)
            drawHorizontal(line);
        
        for (LayerJustification justification : layerLineSet.justificationList)
            drawJustification(justification);
        
        g = null;
    }
}

class LayerLineDrawerConsole extends LayerLineDrawer {

    public LayerLineDrawerConsole(LayerLineSet set) {
        this.layerLineSet = set;
    }
    
    @Override
    void drawHorizontal(LayerLineHorizontal line) {
        // X: Layer 0: 21. Every space +8
        // Y: Line 0: 20. Every new line +20
        g.fillRect(13+xOffset + 24*line.layer,
                -1 + 20*line.lineNo,
                8*line.numChars,
                2);
    }

    @Override
    void drawVertical(LayerLineVertical line) {
        // X: Layer 0: 13+xOffset. Every space +8
        // Y: Line 0: 8+10. Every new line +20
        g.fillRect(13+xOffset + 24*line.layer,
                -17 + 20*line.startLineNo,
                2,
                16 + 20*(line.endLineNo-line.startLineNo));
    }

    @Override
    void drawJustification(LayerJustification just) {
        // Y: Line 0: -3. Every new line +20
        g.drawString(just.justification,
                308,
                -3 + 20*just.line);
    }
}

class LayerLineDrawerClick extends LayerLineDrawer {

    public LayerLineDrawerClick(LayerLineSet set) {
        this.layerLineSet = set;
    }
    
    @Override
    void drawHorizontal(LayerLineHorizontal line) {
        // X: Layer 0: 20. Every space +8
        // Y: Line 0: 19. Every new line +22
        g.fillRect(12+xOffset + 24*line.layer,
                -2 + 22*line.lineNo,
                8*line.numChars,
                2);
    }

    @Override
    void drawVertical(LayerLineVertical line) {
        // X: Layer 0: 20. Every space +8
        // Y: Line 0: 7+10. Every new line +22
        g.fillRect(12+xOffset + 24*line.layer,
                -20 + 22*line.startLineNo,
                2,
                18 + 22*(line.endLineNo-line.startLineNo));
    }

    @Override
    void drawJustification(LayerJustification just) {
        // Y: Line 0: -4. Every new line +22
        g.drawString(just.justification,
                297,
                -6 + 22*just.line);
    }
}


class LayerLineSet {
    public ArrayList<LinkedList<LayerLineVertical>> verticalList;
    public LinkedList<LayerLineHorizontal> horizontalList;
    public LinkedList<LayerJustification> justificationList;
    private int currentLayer;
    private int currentLineNo;
    private int labelSpacing;
    
    public LayerLineSet() {
        clear();
    }
    
    public void clear() {
        currentLayer = 0;
        currentLineNo = 0;
        verticalList = new ArrayList<>();
        horizontalList = new LinkedList<>();
        justificationList = new LinkedList<>();
        
        TryAddLayers(0);
    }
    
    public void setLabelSpacing(int labelSpacing) {
        this.labelSpacing = labelSpacing;
    }
    
    public int getLabelSpacing() {
        return labelSpacing;
    }
    
    private void TryAddLayers(int layer) {
        while (verticalList.size() < layer+1) {
            verticalList.add(new LinkedList<LayerLineVertical>());
        }
    }

    void addLine(int layer, int hypoLength, String justification) {
        currentLineNo++;
        TryAddLayers(layer);
        int firstCancelledLayer = layer+1;
        
        if (justification != null && justification.length() > 0) {
            justificationList.add(new LayerJustification(currentLineNo, justification));
        }
        
        if (hypoLength != -1) {
            horizontalList.add(new LayerLineHorizontal(currentLineNo, layer, hypoLength));
            firstCancelledLayer = layer;
        }
        
        for (int i = firstCancelledLayer; i <= currentLayer; i++) {
            LinkedList<LayerLineVertical> currentList = verticalList.get(i);
            LayerLineVertical layerLine = currentList.getLast();
            layerLine.ended = true;
        }
        
        for (int i = 0; i <= layer; i++) {
            LinkedList<LayerLineVertical> currentList = verticalList.get(i);
            LayerLineVertical layerLine = null;
            if (!currentList.isEmpty())
                layerLine = currentList.getLast();
            
            if (layerLine == null || layerLine.ended) {
                currentList.add(new LayerLineVertical(i, currentLineNo));
            }
            else {
                layerLine.endLineNo = currentLineNo;
            }
        }
        
        currentLayer = layer;
    }
}

class LayerJustification {
    public int line;
    public String justification;
    
    public LayerJustification(int line, String justification) {
        this.line = line;
        this.justification = justification;
    }
    
    @Override
    public String toString() {
        return justification;
    }
}

class LayerLineHorizontal {
    public int lineNo;
    public int layer;
    public int numChars;

    public LayerLineHorizontal(int lineNo, int layer, int hypoLength) {
        this.lineNo = lineNo;
        this.layer = layer;
        if (NDConfig.latexModeOn()) {
            hypoLength = (int)(hypoLength*1.2f);
        }
        this.numChars = hypoLength+3;
    }
}

class LayerLineVertical {
    public int layer;
    public int startLineNo;
    public int endLineNo;
    public boolean ended;

    public LayerLineVertical(int layer, int startLineNo) {
        this.layer = layer;
        this.startLineNo = startLineNo;
        this.endLineNo = startLineNo;
        ended = false;
    }
}