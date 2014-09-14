package ndproofs.swingwindow.levelselect;

import ndproofs.swingwindow.buttons.physical.SelectProofButton;
import ndproofs.swingwindow.buttons.physical.DeleteProofButton;
import ndproofs.swingwindow.buttons.HotkeyButton;
import ndproofs.swingwindow.buttons.physical.CopyProofButton;
import ndproofs.swingwindow.buttons.physical.BackButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import ndproofs.proof.ProofChecker;
import ndproofs.swingwindow.main.NDSwingWindow;

import static ndproofs.swingwindow.main.NDSwingWindow.inbuiltPuzzles;
import static ndproofs.swingwindow.main.NDSwingWindow.levelSelectPane;
import static ndproofs.swingwindow.main.NDSwingWindow.packNameField;
import static ndproofs.swingwindow.main.NDSwingWindow.printer;
import static ndproofs.swingwindow.main.NDSwingWindow.puzzlePackReader;
import static ndproofs.swingwindow.main.NDSwingWindow.standardMonospace;


public class NDLevelSelect {
    
    public static DefaultListModel<String> levelListModel = null;
    static ProofListModel proofListModel = null;
    public static PuzzleTextDisplay<String> levelList;
    
    public static HotkeyButton selectProofButton;
    public static HotkeyButton backButton;
    public static HotkeyButton deleteProofButton;
    public static HotkeyButton copyProofButton;
    
    public static void createListModels() {
        levelListModel = new DefaultListModel<>();
        proofListModel = new ProofListModel(puzzlePackReader);
    }
    
    public static void setupLevelSelect() {
        // Uses levelSelectPane;
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(624,60));
        titlePanel.setLayout(new BorderLayout());
        
            JLabel headerLabel = new JLabel("Select a puzzle from the list:");
            headerLabel.setFont(NDSwingWindow.standardMonospace);
            headerLabel.setPreferredSize(new Dimension(400,15));
            headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
            packNameField = new JTextField();
            packNameField.setText("--no puzzle pack currently open--");
            packNameField.setHorizontalAlignment(SwingConstants.CENTER);
            packNameField.setFont(new Font("Monospaced", Font.PLAIN, 16));
            packNameField.setEditable(false);
            packNameField.setBackground(new Color(238,238,238));
            packNameField.setPreferredSize(new Dimension(400,30));
            packNameField.setBorder(null);
        
            titlePanel.setBorder(BorderFactory.createEmptyBorder(15,40,0,40));
            titlePanel.add(packNameField, BorderLayout.NORTH);
            titlePanel.add(headerLabel, BorderLayout.SOUTH);
        
        
        JPanel selectionPanel = new JPanel();
        selectionPanel.setPreferredSize(new Dimension(624,340));
        //selectionPanel.setBackground(new Color(255,96,255));
                
            JPanel levelListPanel = new JPanel();
            
                levelList = new PuzzleTextDisplay<>(levelListModel);
                levelList.addMouseListener(new DoubleClickSelect());
                levelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                levelList.setLayoutOrientation(JList.VERTICAL);
                levelList.setVisibleRowCount(-1);
                levelList.setFont(standardMonospace);

                JScrollPane listScroller = new JScrollPane(levelList);
                listScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                
            levelListPanel.setLayout(new BorderLayout());
            //levelListPanel.setBackground(Color.red);
            levelListPanel.setPreferredSize(new Dimension(560, 230));
            levelListPanel.add(listScroller);
                
            JPanel proofListPanel = new JPanel();
            
                JList<ProofChecker> proofList = new JList<>(proofListModel);
                proofList.addMouseListener(new DoubleClickSelect());
                proofList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                proofList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
                proofList.setVisibleRowCount(1);
                proofList.setFont(standardMonospace);
                proofList.setCellRenderer(new ProofCellRenderer());
                
                JScrollPane proofScroller = new JScrollPane(proofList);
                proofScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                
            proofListPanel.setLayout(new BorderLayout());
            //proofListPanel.setBackground(Color.blue);
            proofListPanel.setPreferredSize(new Dimension(560, 90));
            proofListPanel.add(proofScroller);
            
        selectionPanel.setLayout(new BorderLayout());
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(10,40,10,40));
        selectionPanel.add(levelListPanel, BorderLayout.NORTH);
        selectionPanel.add(proofListPanel, BorderLayout.SOUTH);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(624,62));
        {       
            SelectProofButton button = new SelectProofButton(inbuiltPuzzles, printer, levelList, proofList);
            button.setText ("Select");
            button.setPreferredSize(new Dimension(90,30));
            buttonPanel.add(button);
            selectProofButton = button;
        }
            buttonPanel.add(Box.createRigidArea(new Dimension(20,30)));
        {
            BackButton button = new BackButton();
            button.setText("Back");
            button.setPreferredSize(new Dimension(90,30));
            buttonPanel.add(button);
            backButton = button;
        }
            buttonPanel.add(Box.createRigidArea(new Dimension(20,30)));
        {
            DeleteProofButton button = new DeleteProofButton(inbuiltPuzzles, levelList, proofList);
            button.setText("Delete");
            button.setPreferredSize(new Dimension(90,30));
            buttonPanel.add(button);
            deleteProofButton = button;
        }
            buttonPanel.add(Box.createRigidArea(new Dimension(20,30)));
        {
            CopyProofButton button = new CopyProofButton(inbuiltPuzzles, levelList, proofList);
            button.setText("Copy");
            button.setPreferredSize(new Dimension(90,30));
            buttonPanel.add(button);
            copyProofButton = button;
        }
            
        levelSelectPane.setLayout(new BorderLayout());
        levelSelectPane.add(titlePanel, BorderLayout.NORTH);
        levelSelectPane.add(selectionPanel, BorderLayout.CENTER);
        levelSelectPane.add(buttonPanel, BorderLayout.SOUTH);
        
    }
    
    public static void refreshProofList(int puzzleNo) {
        proofListModel.refreshProofList(puzzleNo);
    }
}