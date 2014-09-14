
package ndproofs.swingwindow.levelselect;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Oh
 */
public class PuzzleTextDisplay<E> extends JList<E> implements ListSelectionListener {

    public PuzzleTextDisplay(ListModel<E> listModel) {
        super(listModel);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addListSelectionListener(this);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
            select(getSelectedIndex());
        }
    }

    public void refresh() {
        int selectedIndex = getSelectedIndex();
        if (selectedIndex != -1) {
            select(selectedIndex);
        }
    }

    private void select(int index) {
        NDLevelSelect.refreshProofList(index);
    }
    
}
