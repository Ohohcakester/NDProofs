
package ndproofs.swingwindow.menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import ndproofs.swingwindow.Printer;
import ndproofs.swingwindow.main.NDSwingWindow;

/**
 * Dialog box for choosing a tutorial
 * @author Oh
 */
public class TutorialSelect extends JDialog implements ActionListener {
    private final JPanel dialogPanel;
    private final JButton cancelButton;
    private final JButton selectButton;
    private final JRadioButton tut1Button;
    private final JRadioButton tut2Button;
    private final Printer printer;

    public TutorialSelect(JFrame frame, boolean modal, Printer printer) {
        super(frame, modal);
        setResizable(false);
        setTitle("Select a Tutorial");
        this.printer = printer;
        dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(200, 120));
        getContentPane().add(dialogPanel);
        dialogPanel.setLayout(new BorderLayout());
        //dialogPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        //dialogPanel.setBackground(new Color(190,199,206));
        JPanel selectionPanel = new JPanel();
        selectionPanel.setPreferredSize(new Dimension(200, 70));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        tut1Button = new JRadioButton("Tutorial 1: A, (AvC)>B |- A^B");
        tut1Button.setPreferredSize(new Dimension(180, 30));
        tut2Button = new JRadioButton("Tutorial 2: A, ~A |- B");
        tut2Button.setPreferredSize(new Dimension(180, 30));
        ButtonGroup group = new ButtonGroup();
        group.add(tut1Button);
        group.add(tut2Button);
        selectionPanel.add(tut1Button);
        selectionPanel.add(tut2Button);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(200, 50));
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        selectButton = new JButton("Select");
        selectButton.setPreferredSize(new Dimension(80, 25));
        selectButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(80, 25));
        cancelButton.addActionListener(this);
        buttonPanel.add(selectButton);
        //buttonPanel.add(Box.createRigidArea(new Dimension(200,5)));
        buttonPanel.add(cancelButton);
        dialogPanel.add(selectionPanel, BorderLayout.NORTH);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
        //getRootPane().setDefaultButton(continueButton);
        addHotKey(KeyStroke.getKeyStroke("ENTER"), new SelectAction(), dialogPanel);
        addHotKey(KeyStroke.getKeyStroke("ESCAPE"), new CancelAction(), dialogPanel);
        pack();
        setLocationRelativeTo(frame);
        setVisible(true);
    }

    private void addHotKey(KeyStroke key, AbstractAction action, JComponent component) {
        //ActionButtonPress action = new ActionButtonPress(button);
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, "" + key);
        component.getActionMap().put("" + key, action);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (selectButton == e.getSource()) {
            selectButtonPressed();
        } else if (cancelButton == e.getSource()) {
            disappear();
        }
    }

    private class SelectAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            selectButtonPressed();
        }
    }

    private class CancelAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            disappear();
        }
    }

    private void selectButtonPressed() {
        if (tut1Button.isSelected()) {
            NDSwingWindow.mainFrame.setContentPane(NDSwingWindow.defaultContentPane);
            NDSwingWindow.mainFrame.validate();
            NDSwingWindow.setFocusOnConsoleInput();
            printer.refreshBefore();
            printer.tutorial.setupTutorial1();
            printer.refreshAfter();
            disappear();
        } else if (tut2Button.isSelected()) {
            NDSwingWindow.mainFrame.setContentPane(NDSwingWindow.defaultContentPane);
            NDSwingWindow.mainFrame.validate();
            NDSwingWindow.setFocusOnConsoleInput();
            printer.refreshBefore();
            printer.tutorial.setupTutorial2();
            printer.refreshAfter();
            disappear();
        }
    }

    private void disappear() {
        setVisible(false);
        this.dispose();
    }
    
}
