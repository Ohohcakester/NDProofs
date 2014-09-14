
package ndproofs.swingwindow.buttons.physical;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import ndproofs.swingwindow.Printer;
import ndproofs.swingwindow.buttons.HotkeyButton;
import ndproofs.swingwindow.clickmode.NDClickMode;
import ndproofs.swingwindow.main.NDSwingWindow;

/**
 *
 * @author Oh
 */
public class ChangeModeButton extends JButton implements ActionListener, HotkeyButton {
    private boolean clickMode;
    private final Printer printer;

    public ChangeModeButton(Printer printer) {
        clickMode = false;
        this.printer = printer;
        addActionListener(this);
        setClickModeLabel();
    }

    public void setClickModeLabel() {
        setText("Click Mode");
        setTooltip("[Ctrl+M]", "Switch to Click Mode Input");
    }

    public void setConsoleModeLabel() {
        setText("Console Mode");
        setTooltip("[Ctrl+M]", "Switch to Console Mode Input");
    }

    public void setTooltip(String line1, String line2) {
        setToolTipText("<html><p><font face=\"Monospace\">" + line1 + "<br>" + line2 + "</font></p></html>");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        trigger();
    }

    @Override
    public void trigger() {
        if (clickMode) {
            clickMode = false;
            NDSwingWindow.defaultContentPane.remove(NDClickMode.clickModeWindow);
            NDSwingWindow.defaultContentPane.add(NDSwingWindow.displayWindow, BorderLayout.WEST);
            NDSwingWindow.sidePanel.remove(NDClickMode.clickModePanel);
            NDSwingWindow.sidePanel.add(NDSwingWindow.consoleModePanel, BorderLayout.SOUTH);
            NDSwingWindow.mainFrame.setContentPane(NDSwingWindow.defaultContentPane);
            //NDSwingWindow.defaultContentPane.validate();
            NDSwingWindow.mainFrame.validate();
            NDSwingWindow.setFocusOnConsoleInput();
            setClickModeLabel();
            printer.clearHints();
            printer.setConsoleMode();
        } else {
            clickMode = true;
            NDSwingWindow.defaultContentPane.remove(NDSwingWindow.displayWindow);
            NDSwingWindow.defaultContentPane.add(NDClickMode.clickModeWindow, BorderLayout.WEST);
            NDSwingWindow.sidePanel.remove(NDSwingWindow.consoleModePanel);
            NDSwingWindow.sidePanel.add(NDClickMode.clickModePanel, BorderLayout.SOUTH);
            NDSwingWindow.mainFrame.setContentPane(NDSwingWindow.defaultContentPane);
            //NDSwingWindow.defaultContentPane.validate();
            NDSwingWindow.mainFrame.validate();
            setConsoleModeLabel();
            printer.setHelpMode(false);
            printer.refreshBefore();
            printer.refreshAfter();
            printer.setClickMode();
            printer.hintScrollToTop();
        }
    }
    
}
