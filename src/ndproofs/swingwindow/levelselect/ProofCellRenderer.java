
package ndproofs.swingwindow.levelselect;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import ndproofs.proof.ProofChecker;
import ndproofs.swingwindow.main.NDSwingWindow;

/**
 *
 * @author Oh
 */
class ProofCellRenderer extends JLabel implements ListCellRenderer<ProofChecker> {
    private final Border etchedBorder;
    private static final Color selectedColor = new Color(208, 208, 255);
    private static final Color newProofColor = new Color(255, 240, 192);

    public ProofCellRenderer() {
        setOpaque(true);
        etchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ProofChecker> list, ProofChecker proof, int index, boolean isSelected, boolean cellHasFocus) {
        if (proof == null) {
            setText("<html><div align=\"center\">CREATE<br>NEW PROOF</div></html>");
        } else {
            if (proof.valid) {
                setDetails(proof._curLine, proof.complete);
            } else {
                setText("<html><div align=\"center\">INVALID<br>PROOF</div></html>");
            }
        }
        this.setFont(NDSwingWindow.standardMonospace);
        setHorizontalAlignment(SwingConstants.CENTER);
        setPreferredSize(new Dimension(100, 70));
        if (isSelected) {
            setBackground(selectedColor);
            //setForeground(Color.red);
        } else {
            if (proof == null) {
                setBackground(newProofColor);
            } else {
                setBackground(Color.white);
                //setForeground(Color.black);
            }
        }
        setBorder(etchedBorder);
        //setBorder(BorderFactory.createRaisedBevelBorder());
        return this;
    }

    private void setDetails(Integer nLines, boolean complete) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><div align=\"center\">");
        sb.append(complete ? "COMPLETE" : "INCOMPLETE");
        sb.append("<br>PROOF<br>");
        sb.append(nLines);
        sb.append(" LINE");
        if (nLines != 1) {
            sb.append("S");
        }
        sb.append("</div></html>");
        setText(sb.toString());
    }
    
}
