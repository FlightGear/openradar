package de.knewcleus.openradar.gui.flightstrips;

import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import de.knewcleus.openradar.gui.Palette;

public class ListDialog extends JDialog implements MouseListener {

	private static final long serialVersionUID = 1L;
	
	private final JList<String> list = new JList<String>();
	private final DefaultListModel<String> listModel = new DefaultListModel<String>();
	private ListDialogListener listener;
	
	public ListDialog() {
		// design and layout
        setUndecorated(true);
		setLayout(new GridBagLayout());
		addWindowListener(new DialogCloseListener());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		// gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		// list
		list.setFont(Palette.STRIP_FONT);
		FontMetrics fm = list.getFontMetrics(Palette.STRIP_FONT);
		list.setFixedCellHeight(fm.getHeight());
		list.setModel(listModel);
		list.setVisibleRowCount(-1);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(this);
		add(list, gbc);
	}
	
	public void setListItems(ArrayList<String> items, int selected, ListDialogListener listener) {
		this.listener = listener;
		listModel.clear();
		for (String item : items) listModel.addElement(item);
		list.setSelectedIndex(selected);
		setSize(list.getPreferredSize());
	}
	
    public void closeDialog() {
        setVisible(false);
        this.listener = null;
    }

    // --- MouseListener ---

	@Override
	public void mouseClicked(MouseEvent e) {
		listener.valueSelected(list.getSelectedIndex());
		closeDialog();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

    // === DialogCloseListener ===
    
    private class DialogCloseListener extends WindowAdapter {
        
    	@Override
        public void windowClosed(WindowEvent e) {
            closeDialog();
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            closeDialog();
        }

        @Override
        public void windowLostFocus(WindowEvent e) {
            closeDialog();
        }

    }
    
    // === ListDialogListener ===
    
    public interface ListDialogListener {

    	public abstract void valueSelected(int index);
    	
    }
    
}
