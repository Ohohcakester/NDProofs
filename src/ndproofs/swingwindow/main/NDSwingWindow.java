package ndproofs.swingwindow.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import ndproofs.swingwindow.menu.NDMenu;
import ndproofs.swingwindow.levelselect.NDLevelSelect;
import ndproofs.swingwindow.buttons.physical.RulesButton;
import ndproofs.swingwindow.buttons.HotkeyButton;
import ndproofs.swingwindow.buttons.physical.CommandsListButton;
import ndproofs.swingwindow.buttons.TriggerButton;
import ndproofs.swingwindow.buttons.physical.HelpAboutButton;
import ndproofs.swingwindow.buttons.physical.ChangeModeButton;
import ndproofs.logic.CheckAndPrint;
import ndproofs.puzzle.InbuiltPuzzles;
import ndproofs.proof.LineMaker;
import ndproofs.swingwindow.clickmode.NDClickMode;
import ndproofs.puzzle.PuzzlePackReader;
import ndproofs.puzzle.rules.RuleSet;
import ndproofs.puzzle.TutorialController;
import ndproofs.swingwindow.HotkeySetup;
import ndproofs.swingwindow.InputTextBox;
import ndproofs.swingwindow.NDConfig;
import ndproofs.swingwindow.Printer;
import ndproofs.swingwindow.TextDisplayBox;

/**
 * Initialises the main window and functions.
 * @author Oh
 */
public class NDSwingWindow {
    public static final String introName = "Natural Deduction Prover";
    public static final String introCredit = "Made by Oh";
    public static final String introVersion = "v0.9.9b";
    public static final String introString = introName + " - " + introCredit + " - " + introVersion;
    
    public static final Font standardMonospace = new Font( Font.MONOSPACED, Font.PLAIN, 14 );
    public static final Font monospaceSize12 = new Font( Font.MONOSPACED, Font.PLAIN, 12 );
    
    public static JFrame mainFrame = null;
    public static Container defaultContentPane = null;
    public static JPanel displayWindow = null; 
    
    public static JPanel levelSelectPane = null;
    public static InputTextBox inputField = null;
    
    public static JTextField stageBox = null;
    public static JTextField errorField = null;
    public static JEditorPane hintBox = null;
    public static JTextField objectiveBox = null;
    public static JTextArea textDisplay = null;
    public static JTextField packNameField = null;
    
    public static JPanel sidePanel = null;
    public static JPanel consoleModePanel = null;
    
    public static HotkeyButton changeModeButton = null;
    public static HotkeyButton helpButton = null;
    public static HotkeyButton rulesButton = null;
    public static HotkeyButton aboutButton = null;
    
    private static TriggerButton currentStageButton;
    
    public static Printer printer = null;
    
    public static LineMaker lineMaker;
    public static InbuiltPuzzles inbuiltPuzzles;
    public static RuleSet ruleSet;
    public static PuzzlePackReader puzzlePackReader;
    //public static SaveLoadProgress saveLoadProgress;
    public static HotkeySetup hotkeySetup;
    public static CheckAndPrint checkAndPrint;
    
    public static NDMenu ndMenu;
    
    private static int dialogBoxWidth = 56;
    
    public static void main(String[] args) {
        //try {UIManager.setLookAndFeel("com.apple.laf.AquaLookAndFeel");}
        //try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        try {UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");}
        catch (Exception e2) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
            catch (Exception e3) {
                System.out.println("Unable to initialise look and feel!");
            }
        }
        
        mainFrame = new JFrame(introString);
        mainFrame.setSize(640,523); // 23 extra pixels in height for the menu bar.
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setResizable(false);
        NDConfig.initialise();
        initializeMenu();
        initializeGame();
        
        mainFrame.addWindowListener(new MainFrameWindowListener(mainFrame, puzzlePackReader));
        mainFrame.setIconImage( new ImageIcon( (new NDSwingWindow()).getClass().getResource("images/NDProofs.png" ) ).getImage() ); 
        
        defaultContentPane = mainFrame.getContentPane();
        levelSelectPane = new JPanel();
        
        initialiseLeftDisplayWindow();
        initialiseSidePanel();                
            
        defaultContentPane.add(displayWindow, BorderLayout.WEST);
        defaultContentPane.add(sidePanel, BorderLayout.EAST);
        
        
        initializeRuleSet();
        NDLevelSelect.createListModels();
        NDLevelSelect.setupLevelSelect();
        NDClickMode.initialize();
        initializePrinter();
        finalInitialization();
        ndMenu.initializeMenuButtons();
        hotkeySetup.setupGeneralHotkeys();
        
        currentStageButton.setTrigger(NDMenu.levelSelectTrigger);
        
        mainFrame.setVisible(true);
        displayWindow.setVisible(true);
        
        //default console mode
        //printer.printHelp();
        //setFocusOnConsoleInput();
        
        changeModeButton.trigger();
        
        if (NDConfig.splashScreenOn())
            showIntroSplash();
    }

    private static void initialiseLeftDisplayWindow() {
        // Creating Left Side JPanel
        displayWindow = new JPanel();
        displayWindow.setPreferredSize(new Dimension(420,500));
        
        GridBagConstraints gridCons1 = new GridBagConstraints();
        {
            GridBagLayout gridBag = new GridBagLayout();
            displayWindow.setLayout(gridBag);

            gridCons1.gridwidth = 1;
            gridCons1.fill = GridBagConstraints.BOTH;
            gridCons1.weightx = 1.0;
            gridCons1.weighty = 1.0;
            //gridCons1.ipadx = 30;
            //gridCons1.ipady = 30;
        }
        textDisplay = new TextDisplayBox(printer.layerLineSet);
        textDisplay.setEditable(false);
        textDisplay.setFont(standardMonospace);
        
        JScrollPane scrollPane = new JScrollPane(textDisplay);
        scrollPane.setPreferredSize(new Dimension(420,300));
        displayWindow.add(scrollPane, gridCons1);
        //scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        GridBagConstraints gridCons2 = new GridBagConstraints();
        {
            gridCons2.gridwidth = GridBagConstraints.REMAINDER;
            gridCons2.fill = GridBagConstraints.HORIZONTAL;
            gridCons2.gridy = 1;
        }
            
        errorField = new JTextField();
        errorField.setEditable(false);
        errorField.setBackground(new Color(238,238,238));
        errorField.setForeground(new Color(238,0,0));
                
        displayWindow.add(errorField, gridCons2);

        GridBagConstraints gridCons3 = new GridBagConstraints();
        {
            gridCons3.gridwidth = GridBagConstraints.REMAINDER;
            gridCons3.fill = GridBagConstraints.HORIZONTAL;
            gridCons3.gridy = 2;
            //gridCons3.ipadx = 3;
            //gridCons3.ipady = 3;
        }
                
        inputField = new InputTextBox();
        inputField.setPreferredSize(new Dimension(600,80));
        inputField.addActionListener(inputField);
        inputField.setFont(standardMonospace);
        
        displayWindow.add(inputField, gridCons3);
        
        displayWindow.setBorder(BorderFactory.createEmptyBorder(10, 10, 14, 0));
    }

    private static void initialiseSidePanel() {
        // SIDE PANEL
        sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(190,500));
        sidePanel.setLayout(new BorderLayout());
        sidePanel.setBorder(BorderFactory.createEmptyBorder(15,0,8,10));
        {
            JPanel topRightPanel = new JPanel();
            topRightPanel.setPreferredSize(new Dimension(190,298));
            topRightPanel.setLayout(new BorderLayout());
            //topRightPanel.setBackground(new Color(255,255,0));
            
            JPanel objectivePanel = new JPanel();
            {
                // Objective Panel
                objectivePanel.setPreferredSize(new Dimension(190,45));
                objectivePanel.setLayout(new BorderLayout());
                objectivePanel.setBackground(new Color(224,224,240));
                {
                    JLabel objectiveLabel = new JLabel("OBJECTIVE:", SwingConstants.CENTER);
                    objectiveLabel.setPreferredSize(new Dimension(190,20));

                    objectiveBox = new JTextField();
                    objectiveBox.setHorizontalAlignment(SwingConstants.CENTER);
                    objectiveBox.setFont(standardMonospace);
                    objectiveBox.setEditable(false);
                    objectiveBox.setBackground(new Color(255,255,255));

                    objectivePanel.add(objectiveLabel, BorderLayout.NORTH);
                    objectivePanel.add(objectiveBox, BorderLayout.SOUTH);
                }
            }
            
            JPanel buttonPanel = new JPanel();
            {
                // Button Panel
                buttonPanel.setPreferredSize(new Dimension(190,155));
                buttonPanel.setBorder(BorderFactory.createEmptyBorder(15,0,0,0));
                //buttonPanel.setBackground(new Color(224,224,0));
                
                //buttonPanel.add(Box.createRigidArea(new Dimension(190,10)));
                {
                // Current Stage
                JPanel currentStagePanel = new JPanel();
                currentStagePanel.setLayout(new BorderLayout());
                currentStagePanel.setBackground(new Color(224,224,240));
                currentStagePanel.setPreferredSize(new Dimension(140,45));
                {
                    currentStageButton = new TriggerButton();
                    currentStageButton.setPreferredSize(new Dimension(190,20));
                    currentStageButton.setText("CURRENT PUZZLE");
                    //currentStageButton.setBackground(new Color(224,224,240));
                    //currentStageButton.setContentAreaFilled(false);
                    
                    /*JLabel currentStageLabel = new JLabel("CURRENT PUZZLE:", SwingConstants.CENTER);
                    currentStageLabel.setPreferredSize(new Dimension(190,20));*/
                    
                    stageBox = new JTextField();
                    stageBox.setHorizontalAlignment(SwingConstants.CENTER);
                    stageBox.setFont(standardMonospace);
                    stageBox.setEditable(false);
                    stageBox.setBackground(new Color(255,255,255));
                    
                    currentStagePanel.add(currentStageButton, BorderLayout.NORTH);
                    currentStagePanel.add(Box.createRigidArea(new Dimension(190,10)));
                    currentStagePanel.add(stageBox, BorderLayout.SOUTH);
                }
                buttonPanel.add(currentStagePanel);
            }
                //buttonPanel.add(Box.createRigidArea(new Dimension(190,5)));
                
                // Button: Click Mode
                {
                ChangeModeButton button = new ChangeModeButton(printer);
                button.setPreferredSize(new Dimension(130,40));
                buttonPanel.add(button);
                changeModeButton = button;
            }
            }
            
            JPanel hintPanel = new JPanel();
            {
                // Hint Panel
                hintPanel.setPreferredSize(new Dimension(190,125));
                hintPanel.setLayout(new BorderLayout());
                hintPanel.setBackground(new Color(224,224,240));
                {
                    JLabel hintLabel = new JLabel("INFO", SwingConstants.CENTER);
                    hintLabel.setPreferredSize(new Dimension(190,20));

                    hintBox = new JEditorPane("text/html", "");
                    //hintBox.setBackground(new Color(255,255,255));
                    //hintBox = new JTextArea(5,10);
                    hintBox.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
                    hintBox.setFont(monospaceSize12);
                    //hintBox.setLineWrap(true);
                    hintBox.setEditable(false);
                    //hintBox.setWrapStyleWord(true);
                    JScrollPane hintScrollPane = new JScrollPane(hintBox);
                    hintScrollPane.setPreferredSize(new Dimension(420,105));
                    hintScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

                    hintPanel.add(hintLabel, BorderLayout.NORTH);
                    hintPanel.add(hintScrollPane, BorderLayout.SOUTH);
                }
            }
            topRightPanel.add(objectivePanel, BorderLayout.NORTH);
            topRightPanel.add(buttonPanel, BorderLayout.CENTER);
            topRightPanel.add(hintPanel, BorderLayout.SOUTH);
            
            
            consoleModePanel = new JPanel();
            consoleModePanel.setPreferredSize(new Dimension(190,144));
            consoleModePanel.setBorder(BorderFactory.createEmptyBorder(32, 0, 0, 0));
                    
            {
                CommandsListButton button = new CommandsListButton(printer, lineMaker);
                button.setText("Commands");
                button.setPreferredSize(new Dimension(100,26));
                consoleModePanel.add(button);
                helpButton = button;
            }
            
            {
                RulesButton button = new RulesButton(printer);
                button.setText("Rules");
                button.setPreferredSize(new Dimension(100,26));
                consoleModePanel.add(button);
                rulesButton = button;
            }
            
            {
                HelpAboutButton button = new HelpAboutButton(printer);
                button.setText("Help");
                button.setPreferredSize(new Dimension(100,26));
                consoleModePanel.add(button);
                aboutButton = button;
            }
            
            sidePanel.add(topRightPanel, BorderLayout.NORTH);
            sidePanel.add(consoleModePanel, BorderLayout.SOUTH);
        }
    }
    
    public static void setFocusOnConsoleInput() {
        inputField.requestFocusInWindow();
    }
    
    public static void initializeRuleSet() {
        ruleSet = new RuleSet();
        ruleSet.initializeDefaultRules();
        
        /*
        if (!ruleSet.addRule("AvB, (A|-C, D) |- C", "OH", "Test Rule"))
            System.out.println(ruleSet.lastError);
        
        //ruleSet.addRule("(AvB, A|-C, B|-C) |- C", "OH2", "blah");
        ruleSet.addRule("(A|-(B,~B)) |- C", "OH3", "blah"); // THIS IS BAD!
        ruleSet.addRule("(A|-(B,~B)) |- ~A", "OH4", "blah");*/
        
        //ruleSet.addRule("|-Av~A", "OH", "A or not A must be true");
    }
        
    public static void initializePrinter() {
        printer.errorField = errorField;
        printer.hintBox = hintBox;
        printer.levelListModel = NDLevelSelect.levelListModel;
        printer.mainFrame = mainFrame;
        printer.objectiveBox = objectiveBox;
        printer.stageBox = stageBox;
        printer.textDisplay = textDisplay;
        
        printer.lineMaker = lineMaker;
        printer.ruleSet = ruleSet;
        printer.tutorial = new TutorialController(inbuiltPuzzles, lineMaker, printer);
        
        printer.errorFieldCM = NDClickMode.errorFieldCM;
        printer.selectableTextDisplay = NDClickMode.selectableTextDisplay;
        printer.lineListModel = NDClickMode.lineListModel;
    }
    
    public static void finalInitialization() {
        lineMaker.printer = printer;
        inbuiltPuzzles.levelListModel = NDLevelSelect.levelListModel;
        //inbuiltPuzzles.saveLoadProgress = saveLoadProgress;
        inbuiltPuzzles.puzzlePackReader = puzzlePackReader;
        
        inputField.initialiseVariables(printer, lineMaker);
        
        inbuiltPuzzles.initializeDefaultLevels();
        inbuiltPuzzles.refreshLevelList();
        
        NDClickMode.commandButtonPanel.printer = printer;
        
        //ruleSet.lineMaker = lineMaker;
        lineMaker.ruleSet = ruleSet;
        lineMaker.inbuiltPuzzles = inbuiltPuzzles;
        lineMaker.checkAndPrint = checkAndPrint;
        
        puzzlePackReader.inbuiltPuzzles = inbuiltPuzzles;
        puzzlePackReader.ruleSet = ruleSet;
        puzzlePackReader.commandButtonPanel = NDClickMode.commandButtonPanel;
        //puzzlePackReader.saveLoadProgress = saveLoadProgress;
        puzzlePackReader.packNameField = packNameField;
        
        //saveLoadProgress.inbuiltPuzzles = inbuiltPuzzles;
        //saveLoadProgress.printer = printer;
        //saveLoadProgress.puzzlePackReader = puzzlePackReader;
        
        inbuiltPuzzles.freeMode(true);
        
        hotkeySetup.commandButtonPanel = NDClickMode.commandButtonPanel;
        hotkeySetup.clickModePanel = NDClickMode.clickModePanel;
        hotkeySetup.setupClickMode();
        
        checkAndPrint.printer = printer;
    }
    
    public static void initializeGame() {
        lineMaker = new LineMaker();
        
        printer = new Printer();
        
        inbuiltPuzzles = new InbuiltPuzzles(lineMaker);
        
        puzzlePackReader = new PuzzlePackReader();
        
        //saveLoadProgress = new SaveLoadProgress();
        
        hotkeySetup = new HotkeySetup();
        
        checkAndPrint = new CheckAndPrint();
    }
    
    public static void initializeMenu() {
        ndMenu = new NDMenu(mainFrame);
        ndMenu.initializeMenu();
    }
    
    public static void updateTitle(String packName) {
        mainFrame.setTitle(introString + " - " + packName);
    }
    
    public static boolean showConfirmDialog(String message, String title) {
        // 0 = OK
        // 2 = CANCEL
        // -1 = "x"
        
        JPanel errorPanel = new JPanel();
        int result = JOptionPane.showConfirmDialog(errorPanel, message, title, JOptionPane.OK_CANCEL_OPTION);
        return result == 0;
    }
    
    public static void showDialog(String message, String title) {
        JPanel errorPanel = new JPanel();
        JOptionPane.showMessageDialog(errorPanel, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void showScrollableDialog(String message, String title) {
        JPanel errorPanel = new JPanel();
        
        JTextArea textArea = new JTextArea(18,dialogBoxWidth);
        textArea.setFont(standardMonospace);
        textArea.setText(message);
        textArea.setEditable(false);
        textArea.setCaretPosition(0);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        JOptionPane.showMessageDialog(errorPanel, scrollPane, title, JOptionPane.PLAIN_MESSAGE);
    }
    
    public static void showErrorDialog(String message, String title) {
        JPanel errorPanel = new JPanel();
        JOptionPane.showMessageDialog(errorPanel, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    
    public static void showIntroSplash() {
        SplashScreen splashScreen = new SplashScreen(mainFrame, true);
    }
}