package ndproofs.swingwindow.buttons;

/**
 * All buttons inherit this.<br>
 * The trigger() method should be overriden with the function of the button.<br>
 * This is so that the buttons can be triggered from the code as well via calling HotkeyButton.trigger().
 * 
 * @author Oh
 */
public interface HotkeyButton {
    public void trigger();
}