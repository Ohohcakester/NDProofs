package ndproofs.swingwindow.clickmode;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;


public class CommandButton extends JButton implements ActionListener {
    public static final int ID_PREMISE = -11;
    public static final int ID_ASSUME = -12;
    public static final int ID_OBJECTIVE = -13;
    public static final int ID_TRY = -14;
    //public static final int ID_CHECK = -15;
    public static final int ID_QED = -16;
    public static final int ID_BACK = -17;
    public static final int ID_CHECK_CONSEQUENCE = -18;
    public static final int ID_CHECK_EQUIVALENCE = -19;
    public static final int ID_CHECK_CONTRADICTION = -20;
    public static final int ID_CHECK_TAUTOLOGY = -21;
    public static final int ID_DOWNONELEVEL = -22;
    
    public static final int ID_KEYLOGIC = -2;
    public static final int ID_CLEAR = -1;
    public static final int ID_UNDO = -3;
    
    int id;
    ClickModeButtons commandButtonPanel;
    
    public CommandButton(int id, ClickModeButtons commandButtonPanel) {
        this.id = id;
        this.commandButtonPanel = commandButtonPanel;
        //if (id <= ID_PREMISE && id >= ID_CHECK)
        //    setFont(new Font("Monospace", Font.BOLD, 14));
        //else
            setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        setMargin(new Insets(0, 0, 0, 0));
        //setBorder(null);
        addActionListener(this);
    }
    
    public void setTooltip(String line1) {
        setToolTipText("<html><p><font face=\"Monospace\">" + line1 +
                        "</font></p></html>");
    }
    
    public void setTooltip(String line1, String line2) {
        setToolTipText("<html><p><font face=\"Monospace\">" + line1 +
                        "<br>" + line2 + "</font></p></html>");
    }
    
    public void setTooltipRed(String line1, String line2) {
        setToolTipText("<html><p><font face=\"Monospace\" color=\"#800000\">" + line1 +
                        "<br>" + line2 + "</font></p></html>");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        commandButtonPanel.buttonPressed(id);
    }
    
}


