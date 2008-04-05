package de.knewcleus.radar.ui.plaf.refghmi;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class REFGHMIButtonCellListRenderer extends JButton implements ListCellRenderer {
	private static final long serialVersionUID = -7783333827080913858L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		setText(value.toString());
		getModel().setPressed(isSelected);
		getModel().setArmed(isSelected);
		return this;
	}

}
