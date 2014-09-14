
package ndproofs.swingwindow.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import ndproofs.swingwindow.buttons.HotkeyButton;
import ndproofs.swingwindow.menu.NDMenu;

/**
 * Splash screen shown at start.
 * @author Oh
 */
class SplashScreen extends JDialog implements ActionListener {
    private JPanel dialogPanel = null;
    private JButton openButton = null;
    private JButton tutorialButton = null;
    private JButton continueButton = null;

    public SplashScreen(JFrame frame, boolean modal) {
        super(frame, modal);
        setUndecorated(true);
        Font splashFont = new Font("Monospaced", Font.PLAIN, 20);
        dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(370, 270));
        getContentPane().add(dialogPanel);
        dialogPanel.setLayout(new BorderLayout());
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        dialogPanel.setBackground(new Color(190, 199, 206));
        JPanel detailsPanel = new JPanel();
        detailsPanel.setPreferredSize(new Dimension(360, 160));
        JLabel label1 = new JLabel(NDSwingWindow.introName);
        label1.setFont(splashFont);
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        label1.setPreferredSize(new Dimension(360, 30));
        JLabel label2 = new JLabel(NDSwingWindow.introVersion);
        label2.setFont(splashFont);
        label2.setHorizontalAlignment(SwingConstants.CENTER);
        label2.setPreferredSize(new Dimension(360, 30));
        JLabel label3 = new JLabel(NDSwingWindow.introCredit);
        label3.setFont(splashFont);
        label3.setHorizontalAlignment(SwingConstants.CENTER);
        label3.setPreferredSize(new Dimension(360, 30));
        JLabel label4 = new JLabel("Open a Puzzle Pack or Tutorial to begin.");
        label4.setFont(NDSwingWindow.standardMonospace);
        label4.setHorizontalAlignment(SwingConstants.CENTER);
        label4.setPreferredSize(new Dimension(360, 20));
        detailsPanel.add(label1);
        detailsPanel.add(label2);
        detailsPanel.add(label3);
        detailsPanel.add(Box.createRigidArea(new Dimension(360, 20)));
        detailsPanel.add(label4);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(360, 100));
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 30, 10));
        openButton = new JButton("Open Puzzle Pack");
        openButton.setPreferredSize(new Dimension(120, 30));
        openButton.setAlignmentX(CENTER_ALIGNMENT);
        openButton.addActionListener(this);
        tutorialButton = new JButton("Tutorial");
        tutorialButton.setPreferredSize(new Dimension(120, 30));
        tutorialButton.setAlignmentX(CENTER_ALIGNMENT);
        tutorialButton.addActionListener(this);
        continueButton = new JButton("Continue");
        continueButton.setPreferredSize(new Dimension(140, 30));
        continueButton.setAlignmentX(CENTER_ALIGNMENT);
        continueButton.addActionListener(this);
        buttonPanel.add(openButton);
        buttonPanel.add(tutorialButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(200, 5)));
        buttonPanel.add(continueButton);
        dialogPanel.add(detailsPanel, BorderLayout.NORTH);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
        //getRootPane().setDefaultButton(continueButton);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent evt) {
                continueButton.requestFocusInWindow();
                removeWindowListener(this);
            }
        });
        pack();
        setLocationRelativeTo(frame);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (openButton == e.getSource()) {
            setVisible(false);
            this.dispose();
            ((HotkeyButton) NDMenu.loadPackButton).trigger();
        } else if (tutorialButton == e.getSource()) {
            setVisible(false);
            this.dispose();
            ((HotkeyButton) NDMenu.tutorialModeButton).trigger();
        } else if (continueButton == e.getSource()) {
            setVisible(false);
            this.dispose();
            NDSwingWindow.printer.dialogHelp();
        }
    }
    
}
