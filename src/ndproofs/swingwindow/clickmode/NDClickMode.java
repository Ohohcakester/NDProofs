package ndproofs.swingwindow.clickmode;

import ndproofs.swingwindow.SelectableTextDisplay;
import ndproofs.swingwindow.main.NDSwingWindow;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import static ndproofs.swingwindow.main.NDSwingWindow.standardMonospace;

public class NDClickMode {
    public static JPanel clickModeWindow = null;
    public static JPanel clickModePanel = null;
    
    public static ClickModeButtons commandButtonPanel = null;
    
    public static DefaultListModel<String> lineListModel = null;
    public static SelectableTextDisplay<String> selectableTextDisplay = null;
    public static JTextField errorFieldCM = null;
    public static JEditorPane inputFieldCM = null;
    
    public static void initialize() {
        lineListModel = new DefaultListModel<>();
        
        // Creating Left Side JPanel
        clickModeWindow = new JPanel();
        clickModeWindow.setPreferredSize(new Dimension(420,500));
        
        clickModeWindow.setLayout(new BoxLayout(clickModeWindow, BoxLayout.PAGE_AXIS));

        selectableTextDisplay = new SelectableTextDisplay<>(lineListModel, NDSwingWindow.printer.layerLineSet);
        selectableTextDisplay.setFont(standardMonospace);
        
        JScrollPane scrollPane = new JScrollPane(selectableTextDisplay);
        scrollPane.setPreferredSize(new Dimension(400,290));
        scrollPane.setMinimumSize(new Dimension(400,0));
        scrollPane.setMaximumSize(new Dimension(400,900));
                
        clickModeWindow.add(scrollPane);

        errorFieldCM = new JTextField();
        errorFieldCM.setEditable(false);
        errorFieldCM.setPreferredSize(new Dimension(400,20));
        errorFieldCM.setMaximumSize(errorFieldCM.getPreferredSize());
        errorFieldCM.setMinimumSize(errorFieldCM.getPreferredSize());
        errorFieldCM.setBackground(new Color(238,238,238));
        errorFieldCM.setForeground(new Color(238,0,0));
                
        clickModeWindow.add(errorFieldCM);

        JPanel commandPanel = new JPanel();
        commandPanel.setPreferredSize(new Dimension(400,120));
        commandPanel.setMaximumSize(commandPanel.getPreferredSize());
        commandPanel.setMinimumSize(commandPanel.getPreferredSize());
        commandPanel.setFont(standardMonospace);
        commandPanel.setLayout(new BorderLayout());
        
            
            // Command Button Panel
            commandButtonPanel = new ClickModeButtons();
            
            
            // Input Field Panel
            JPanel inputFieldPanel = new JPanel();
            inputFieldPanel.setPreferredSize(new Dimension(400,25));
            inputFieldPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            inputFieldPanel.setLayout(new BorderLayout());
            
                CommandButton backButton = new CommandButton(CommandButton.ID_BACK, commandButtonPanel);
                backButton.setPreferredSize(new Dimension(60,20));
                backButton.setText("Back");
                
                inputFieldCM = new JEditorPane("text/html", "");
                inputFieldCM.setEditable(false);
                inputFieldCM.setFont(standardMonospace);
                inputFieldCM.setPreferredSize(new Dimension(255,20));
                inputFieldCM.setBackground(new Color(255,255,255));
                
                OkButton okButton = new OkButton(commandButtonPanel);
                okButton.setPreferredSize(new Dimension(75,20));
                
                inputFieldPanel.add(backButton, BorderLayout.WEST);
                inputFieldPanel.add(inputFieldCM, BorderLayout.CENTER);
                inputFieldPanel.add(okButton, BorderLayout.EAST);
                
            
            // Initialization of Command Button Panel
            commandButtonPanel.inputFieldCM = inputFieldCM;
            commandButtonPanel.lineMaker = NDSwingWindow.lineMaker;
            commandButtonPanel.ruleSet = NDSwingWindow.ruleSet;
            commandButtonPanel.selectableTextDisplay = selectableTextDisplay;
            commandButtonPanel.checkAndPrint = NDSwingWindow.checkAndPrint;
            
            commandButtonPanel.initialize();
            commandButtonPanel.createRuleButtons();
            
        commandPanel.add(inputFieldPanel, BorderLayout.NORTH);
        commandPanel.add(commandButtonPanel, BorderLayout.SOUTH);

            
        selectableTextDisplay.commandButtonPanel = commandButtonPanel;
            
        clickModeWindow.add(commandPanel);
        
        clickModeWindow.setBorder(BorderFactory.createEmptyBorder(10, 10, 14, 0));
        
        initializeCornerPanel();
    }     
    
    public static void initializeCornerPanel() {
        clickModePanel = new JPanel();
        clickModePanel.setPreferredSize(new Dimension(190,144));
        //clickModePanel.setBackground(Color.red);
        clickModePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        //clickModePanel.setLayout(new BoxLayout(clickModePanel, BoxLayout.PAGE_AXIS));
        
        
        CheckComboBox checkComboBox = new CheckComboBox(commandButtonPanel);
        //checkComboBox.addButton(CommandButton.ID_CHECK, "Check");
        checkComboBox.addButton(CommandButton.ID_CHECK_CONSEQUENCE, "Logical Consequence");
        checkComboBox.addButton(CommandButton.ID_CHECK_EQUIVALENCE, "Equivalence");
        checkComboBox.addButton(CommandButton.ID_CHECK_CONTRADICTION, "Contradiciton");
        checkComboBox.addButton(CommandButton.ID_CHECK_TAUTOLOGY, "Tautology");
        checkComboBox.setPreferredSize(new Dimension(160,26));
        
        
        //CommandButton buttonCheck = new CommandButton(CommandButton.ID_CHECK, commandButtonPanel);
        CommandButton buttonTry = new CommandButton(CommandButton.ID_TRY, commandButtonPanel);
        CommandButton buttonQED = new CommandButton(CommandButton.ID_QED, commandButtonPanel);
        CommandButton buttonObjective = new CommandButton(CommandButton.ID_OBJECTIVE, commandButtonPanel);
        
        /*buttonCheck.setText("Check");
        buttonCheck.setTooltip("Check");
        buttonCheck.setPreferredSize(new Dimension(100,26));*/
        
        buttonTry.setText("Try");
        buttonTry.setTooltip("Try", "Tests for possible rules between two lines");
        buttonTry.setPreferredSize(new Dimension(120,26));
        
        buttonObjective.setText("Objective");
        buttonObjective.setTooltip("Set Objective", "(Free Mode)");
        buttonObjective.setPreferredSize(new Dimension(120,26));
        
        buttonQED.setText("QED");
        buttonQED.setTooltip("Completes Puzzle");
        buttonQED.setPreferredSize(new Dimension(120,26));
        
        
        
        clickModePanel.add(checkComboBox);
        //clickModePanel.add(buttonCheck);
        clickModePanel.add(buttonTry);
        clickModePanel.add(buttonObjective);
        clickModePanel.add(buttonQED);
        
    }
}


class CheckComboBox extends JComboBox<String> implements ActionListener {
    private final ClickModeButtons commandButtonPanel;
    private ArrayList<Integer> idList;
    
    public CheckComboBox(ClickModeButtons commandButtonPanel) {
        super();
        this.commandButtonPanel = commandButtonPanel;
        idList = new ArrayList<>();
        addActionListener(this);
    }
    
    public void addButton(int id, String text) {
        addItem(text);
        idList.add(id);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        //System.out.print(getSelectedIndex());
        int index = getSelectedIndex();
        
        if (index >= idList.size() )
            return;
            
        commandButtonPanel.buttonPressed(idList.get(index));
    }


}