package ndproofs.swingwindow;

import ndproofs.proof.LineMaker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;

public class InputTextBox extends JTextField implements ActionListener{
    
    private Printer printer;
    private LineMaker lineMaker;
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String input = getText();
        
        printer.refreshBefore();
        
        // Read input
        lineMaker.read(input);
        
        selectAll();
        
        printer.refreshAfter();
    }
    
    public void initialiseVariables(Printer printer, LineMaker lineMaker) {
        this.printer = printer;
        this.lineMaker = lineMaker;
    }
}

