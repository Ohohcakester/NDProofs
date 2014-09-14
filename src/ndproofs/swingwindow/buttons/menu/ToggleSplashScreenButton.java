
package ndproofs.swingwindow.buttons.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import ndproofs.swingwindow.NDConfig;
import ndproofs.swingwindow.buttons.HotkeyButton;

/**
 *
 * @author Oh
 */
public class ToggleSplashScreenButton extends JCheckBoxMenuItem implements ActionListener, HotkeyButton {

    public ToggleSplashScreenButton() {
        setText("Splash Screen");
        setSelected(NDConfig.splashScreenOn());
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        trigger();
    }

    @Override
    public void trigger() {
        NDConfig.setSplashScreenOn(isSelected());
    }
    
}
