package ndproofs.swingwindow.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * A general button that is used to trigger another button.
 * @author Oh
 */
public class TriggerButton extends JButton implements ActionListener, HotkeyButton {
    private HotkeyButton button;

    public TriggerButton() {
        addActionListener(this);
        button = null;
    }

    /**
     * Creates a trigger button and binds the button.
     * @param button pressing the TriggerButton will trigger this HotkeyButton.
     */
    public TriggerButton(HotkeyButton button) {
        addActionListener(this);
        this.button = button;
    }

    public void setTrigger(HotkeyButton button) {
        this.button = button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        trigger();
    }

    @Override
    public void trigger() {
        button.trigger();
    }
    
}
