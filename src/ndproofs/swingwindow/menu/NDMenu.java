package ndproofs.swingwindow.menu;

import ndproofs.swingwindow.levelselect.NDLevelSelect;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import ndproofs.swingwindow.buttons.HotkeyButton;
import ndproofs.swingwindow.buttons.menu.ClearAllButton;
import ndproofs.swingwindow.buttons.menu.ExportButton;
import ndproofs.swingwindow.buttons.menu.FreeModeButton;
import ndproofs.swingwindow.buttons.menu.HelpAboutMenuButton;
import ndproofs.swingwindow.buttons.menu.LevelSelectButton;
import ndproofs.swingwindow.buttons.menu.LoadPackButton;
import ndproofs.swingwindow.buttons.menu.LoadProofButton;
import ndproofs.swingwindow.buttons.menu.RulesMenuButton;
import ndproofs.swingwindow.buttons.menu.SaveAsButton;
import ndproofs.swingwindow.buttons.menu.SaveButton;
import ndproofs.swingwindow.buttons.menu.ToggleInstructionsButton;
import ndproofs.swingwindow.buttons.menu.ToggleLatexModeButton;
import ndproofs.swingwindow.buttons.menu.ToggleSplashScreenButton;
import ndproofs.swingwindow.buttons.menu.TutorialModeButton;
import ndproofs.swingwindow.main.NDSwingWindow;

/**
 * For handling the JMenu (File Edit etc..)
 * @author Oh
 */
public class NDMenu {
    private final JFrame mainFrame;
    private JMenuBar menuBar;
    private JMenu menu_file;
    private JMenu menu_edit;
    private JMenu menu_mode;
    private JMenu menu_options;
    private JMenu menu_help;
    
    public static JMenuItem tutorialModeButton;
    public static JMenuItem freeModeButton;
    public static JMenuItem levelSelectButton;
    public static HotkeyButton levelSelectTrigger;
    public static JMenuItem clearAllButton;
    public static JMenuItem loadPackButton;
    public static JMenuItem saveButton;
    public static JMenuItem saveAsButton;
    public static JMenuItem exportButton;
    public static JMenuItem loadProofButton;
    public static JMenuItem helpButton;
    public static JMenuItem rulesButton;
    
    public static HotkeyButton toggleInstructionsButton;
    public static HotkeyButton toggleSplashScreenButton;
    public static HotkeyButton toggleLatexModeButton;
    
    public NDMenu(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
    
    public void initializeMenu() {
        menuBar = new JMenuBar();
        
        menu_file = new JMenu("File");
        menu_edit = new JMenu("Edit");
        menu_mode = new JMenu("Mode");
        menu_options = new JMenu("Options");
        menu_help = new JMenu("Help");
        
        menuBar.add(menu_file);
        menuBar.add(menu_edit);
        menuBar.add(menu_mode);
        menuBar.add(menu_options);
        menuBar.add(menu_help);
        
        mainFrame.setJMenuBar(menuBar);
    }
        
    public void initializeMenuButtons(){
        // One of the last initializations called
        Dimension buttonSize = new Dimension(200,30);
        
        // Buttons: Help

        // Button: Display Help
        {
            HelpAboutMenuButton button = new HelpAboutMenuButton(NDSwingWindow.printer);
            button.setPreferredSize(buttonSize);
            menu_help.add(button);
            helpButton = button;
        }
        
        // Button: Display Rules
        {
            RulesMenuButton button = new RulesMenuButton(NDSwingWindow.printer);
            button.setPreferredSize(buttonSize);
            menu_help.add(button);
            rulesButton = button;
        }
        
        
        
        
        // Buttons: Options

        // Button: Toggle Splash Screen
        {
            ToggleInstructionsButton button = new ToggleInstructionsButton();
            button.setPreferredSize(buttonSize);
            menu_options.add(button);
            toggleInstructionsButton = button;
        }
        
        // Button: Toggle Instructions
        {
            ToggleSplashScreenButton button = new ToggleSplashScreenButton();
            button.setPreferredSize(buttonSize);
            menu_options.add(button);
            toggleSplashScreenButton = button;
        }
        
        // Button: Toggle Latex Mode
        {
            ToggleLatexModeButton button = new ToggleLatexModeButton();
            button.setPreferredSize(buttonSize);
            menu_options.add(button);
            toggleLatexModeButton = button;
        }
        
        
        
        // Buttons: Mode
        
        // Button: Free Mode
        {
            FreeModeButton button = new FreeModeButton(NDSwingWindow.inbuiltPuzzles);
            button.setPreferredSize(buttonSize);
            menu_mode.add(button);
            freeModeButton = button;
        }

        // Button: Level Select
        {
            LevelSelectButton button = new LevelSelectButton(NDLevelSelect.levelList);
            button.setPreferredSize(buttonSize);
            menu_mode.add(button);
            levelSelectButton = button;
            levelSelectTrigger = button;
        }
        
        // Button: Tutorial
        {
            TutorialModeButton button = new TutorialModeButton(NDSwingWindow.printer, NDSwingWindow.puzzlePackReader);
            button.setPreferredSize(buttonSize);
            menu_mode.add(button);
            tutorialModeButton = button;
        }
        
        
        
        // Buttons: Edit

        // Button: Clear All
        {
            ClearAllButton button = new ClearAllButton(NDSwingWindow.lineMaker);
            button.setPreferredSize(buttonSize);
            menu_edit.add(button);
            clearAllButton = button;
        }
        
        
        
        
        // Buttons: File
        
        // Button: Open Puzzle Pack
        {
            LoadPackButton button = new LoadPackButton(NDSwingWindow.puzzlePackReader);
            button.setPreferredSize(buttonSize);
            menu_file.add(button);
            loadPackButton = button;
        }
        
        
        // Button: Save
        {
            SaveButton button = new SaveButton(NDSwingWindow.puzzlePackReader);
            button.setPreferredSize(buttonSize);
            menu_file.add(button);
            saveButton = button;
        }
        
        
        // Button: Save As
        {
            SaveAsButton button = new SaveAsButton(NDSwingWindow.puzzlePackReader);
            button.setPreferredSize(buttonSize);
            menu_file.add(button);
            saveAsButton = button;
        }
        

        // Button: Export
        {
            ExportButton button = new ExportButton(NDSwingWindow.lineMaker);
            button.setPreferredSize(buttonSize);
            menu_file.add(button);
            exportButton = button;
    //button.setHorizontalTextPosition(SwingConstants.RIGHT);
    //button.invalidate();
    //button.validate();
        }
        
        
        // Button: Load Proof
        {
            LoadProofButton button = new LoadProofButton(NDSwingWindow.lineMaker, NDSwingWindow.printer);
            button.setPreferredSize(buttonSize);
            menu_file.add(button);
            loadProofButton = button;
        }
        
        
    }
}