
package ndproofs.swingwindow;

import ndproofs.swingwindow.buttons.HotkeyButton;
import ndproofs.swingwindow.levelselect.NDLevelSelect;
import ndproofs.swingwindow.menu.NDMenu;
import ndproofs.swingwindow.main.NDSwingWindow;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import ndproofs.swingwindow.clickmode.ClickModeButtons;
import ndproofs.swingwindow.clickmode.CommandButton;
import ndproofs.swingwindow.clickmode.NDClickMode;



class ActionOk extends AbstractAction {
    public ClickModeButtons commandButtonPanel;

    public ActionOk(ClickModeButtons commandButtonPanel) {
        this.commandButtonPanel = commandButtonPanel;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        commandButtonPanel.pressOk();
    }
}

class ActionButtonPress extends AbstractAction {
    private final HotkeyButton button;
    
    public ActionButtonPress(HotkeyButton button) {
        this.button = button;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        button.trigger();
    }
    
}

/*class ActionChangeMode extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
        NDSwingWindow.changeModeButton.toggleMode();
    }
}*/

class ActionHotkeyPress extends AbstractAction {
    public ClickModeButtons commandButtonPanel;
    public int id;

    public ActionHotkeyPress(int id, ClickModeButtons commandButtonPanel) {
        this.commandButtonPanel = commandButtonPanel;
        this.id = id;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        commandButtonPanel.buttonPressed(id);
    }
}

/**
 * Sets up hotkeys for the program (especially Click Mode)
 * @author Oh
 */
public class HotkeySetup {
    public ClickModeButtons commandButtonPanel;
    public JPanel clickModeWindow;
    public JPanel clickModePanel;
    
    public void setupGeneralHotkeys() {
        
        NDMenu.saveButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        
        NDMenu.saveAsButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
        
        NDMenu.exportButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        
        NDMenu.loadProofButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
        
        NDMenu.loadPackButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        
        NDMenu.freeModeButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        
        NDMenu.levelSelectButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        
        //NDMenu.clearAllButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
        
        
        setInGameHotkey(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK),
                NDSwingWindow.changeModeButton);
        
        
        setLevelSelectHotkey(KeyStroke.getKeyStroke("ENTER"),
                NDLevelSelect.selectProofButton);
        
        setLevelSelectHotkey(KeyStroke.getKeyStroke("BACK_SPACE"),
                NDLevelSelect.backButton);
        
        setLevelSelectHotkey(KeyStroke.getKeyStroke("ESCAPE"),
                NDLevelSelect.backButton);
        
        setLevelSelectHotkey(KeyStroke.getKeyStroke("DELETE"),
                NDLevelSelect.deleteProofButton);
        
        //setLevelSelectHotkey(KeyStroke.getKeyStroke("DELETE"),
                //NDLevelSelect.copyProofButton);
        
    }
    
    public void setupClickMode() {
        
        clickModeWindow = NDClickMode.clickModeWindow;
        
        setOkHotkey("ENTER");
        
        setHotkey("P", CommandButton.ID_PREMISE);
        setHotkey("A", CommandButton.ID_ASSUME);
        setHotkey("D", CommandButton.ID_DOWNONELEVEL);
        setHotkey("L", CommandButton.ID_KEYLOGIC);
        setHotkey("U", CommandButton.ID_UNDO);
        setHotkey("T", CommandButton.ID_TRY);
        setHotkey("Q", CommandButton.ID_QED);
        setHotkey("O", CommandButton.ID_OBJECTIVE);
        setHotkey("BACK_SPACE", CommandButton.ID_BACK);
        setHotkey("DELETE", CommandButton.ID_CLEAR);
    }
    
    
    /*private void setGeneralHotkey(KeyStroke key, HotkeyButton button) {
        setHotkey(key,button,NDSwingWindow.displayWindow);
        setHotkey(key,button,NDClickMode.clickModeWindow);
        setHotkey(key,button,NDSwingWindow.levelSelectPane);
    }*/
    
    private void setInGameHotkey(KeyStroke key, HotkeyButton button) {
        setHotkey(key,button,NDSwingWindow.displayWindow);
        setHotkey(key,button,NDClickMode.clickModeWindow);
    }
    
    private void setLevelSelectHotkey(KeyStroke key, HotkeyButton button) {
        setHotkey(key,button,NDSwingWindow.levelSelectPane);
    }
    
    private static void setHotkey(KeyStroke key, HotkeyButton button, JComponent component) {
        ActionButtonPress action = new ActionButtonPress(button);
        
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key,
                            "doBT" + key);
        component.getActionMap().put("doBT" + key,
                             action);
    }
    
    private void setOkHotkey(String key) {
        ActionOk actionOk = new ActionOk(commandButtonPanel);
        
        clickModeWindow.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(key),
                            "doOK" + key);
        clickModeWindow.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key),
                            "doOK2" + key);
        clickModeWindow.getActionMap().put("doOK" + key,
                             actionOk);
        clickModeWindow.getActionMap().put("doOK2" + key,
                             actionOk);
        
        clickModePanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(key),
                            "doOK" + key);
        clickModePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key),
                            "doOK2" + key);
        clickModePanel.getActionMap().put("doOK" + key,
                             actionOk);
        clickModePanel.getActionMap().put("doOK2" + key,
                             actionOk);
    }
    
    private void setHotkey(String key, int buttonId) {
        ActionHotkeyPress hotkeyAction = new ActionHotkeyPress(buttonId, commandButtonPanel);
        
        clickModeWindow.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key),
                            "do" + key);
        clickModeWindow.getActionMap().put("do" + key,
                             hotkeyAction);
    }
    
}
