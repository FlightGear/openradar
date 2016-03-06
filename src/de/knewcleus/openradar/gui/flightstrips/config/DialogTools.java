package de.knewcleus.openradar.gui.flightstrips.config;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.knewcleus.openradar.gui.flightstrips.actions.AbstractAction;

public class DialogTools {
	
	// --- actions ---

	static protected void fillActionsPanel(JPanel actions_panel, ArrayList<AbstractAction> actions, boolean editAction,
			AbstractAction.UseCase useCase, ActionListener newActionListener, RemoveActionListenerFactory factory) {
		actions_panel.removeAll();
		JPanel panel = new JPanel(new GridBagLayout());
		actions_panel.add(panel, BorderLayout.CENTER);
        // constraints
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		// actions panel
		for (AbstractAction action : actions) {
			panel.add(createActionComponent(action, editAction), gbc);
			gbc.gridx++;
			if (editAction) {
				// delete button
				gbc.weightx = 0.0;
				JButton delete = new JButton("X");
				try {
					delete.addActionListener(factory.createRemoveActionListener(action));
				} catch (Exception e) {
					e.printStackTrace();
				}
				panel.add(delete, gbc);
				gbc.gridx++;
			}
			gbc.gridx = 0;
			gbc.gridy++;
		}
		if ((actions.size() <= 0) || editAction){
			if (editAction) panel.add(createNewActionComponent(useCase, newActionListener), gbc);
			else panel.add(createActionComponent(null, editAction), gbc);
		}
	}
	
	static protected JComponent createActionComponent(AbstractAction action, boolean editAction) {
		if (editAction && (action != null)) return DialogTools.createPanel(action);
		return DialogTools.createSimpleTextComponent(action, "<No action defined!>");
	}
	
	static protected JComponent createNewActionComponent(AbstractAction.UseCase useCase, ActionListener actionListener) {
		JComboBox<String> actions = new JComboBox<String>(RulesManager.getAvailableActionsNames(useCase));
		actions.addActionListener(actionListener);
		return actions;
	}
	
	// --- IRuleTextProvider ---
	
	static protected JComponent createSimpleTextComponent(IRuleTextProvider ruleTextProvider, String nullText) {
		String s = (ruleTextProvider == null) ? nullText : ruleTextProvider.getSimpleText();
		return new JLabel(s);
	}
	
	// --- IEditProvider ---
	
	static protected JComponent createPanel(IEditProvider provider) {
		DialogTools instance = new DialogTools(); 
		GridBagLayout layout = new GridBagLayout();
		JPanel panel = new JPanel(layout);
        // constraints
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		// components
		JComponent last = null;
		boolean weight = true;
		for (int i = 0; i <= provider.getMaxIndex(); i++) {
			switch(provider.getType(i)) {
			case TEXT:		last = new JLabel(provider.getStringValue(i));
							break;
			case STRING:	if (provider.getMaxLength(i) < 0) {
								weight = false;
								gbc.weightx = 1.0; 
							}
							last = instance.new Edit(provider, i);
							break;
			case NUMBER:	if (provider.getMaxLength(i) < 0) {
								weight = false;
								gbc.weightx = 1.0; 
							}
							last = instance.new Edit(provider, i);
							((Edit)last).setHorizontalAlignment(JTextField.RIGHT);
							break;
			case LIST:		last = instance.new Combobox(provider, i);
							break;
			}
			panel.add(last, gbc);
			gbc.gridx++;
			gbc.weightx = 0.0;
		}
		// if no other component got weightx = 1.0 then the last component gets it
		if (weight) {
			gbc = layout.getConstraints(last);
			gbc.weightx = 1.0;
			layout.setConstraints(last, gbc);
		}
		return panel;
	}
	
	// === Combobox ===
	
	protected class Combobox extends JComboBox<String> {

		private static final long serialVersionUID = 1L;
	
		private final IEditProvider provider;
		private final int index;
		
		public Combobox(IEditProvider provider, int index) {
			super (provider.getStringList(index));
			this.provider = provider;
			this.index = index;
			setSelectedIndex(provider.getIndexedValue(index));
			getModel().addListDataListener(new ListDataListener() {
				@Override
				public void intervalAdded(ListDataEvent e) {}
				@Override
				public void intervalRemoved(ListDataEvent e) {}
				@Override
				public void contentsChanged(ListDataEvent e) {
					Combobox.this.provider.setIndexedValue(getSelectedIndex(), Combobox.this.index);
				}
			});
		}
		
	}
	
	// === Edit ===
	
	protected class Edit extends RegExpEdit {
		
		private static final long serialVersionUID = 1L;

		private final IEditProvider provider;
		private final int index;
		
		public Edit(IEditProvider provider, int index) {
			this.provider = provider;
			this.index = index;
			init();
		}

		@Override
		protected String fetchStringValue() {
			return provider.getStringValue(index);
		}
		
		@Override
		protected void putStringValue(String value) {
			provider.setStringValue(index, value);
		}

		@Override
		protected int fetchMaxLength() {
			return provider.getMaxLength(index);
		}

		@Override
		protected String fetchRegExp() {
			return provider.getRegExp(index);
		}

		@Override
		protected String fetchToolTipText() { 
			return provider.getToolTipText(index);
		} 

	}
	
	static abstract protected class RemoveActionListenerFactory {
		public abstract ActionListener createRemoveActionListener(AbstractAction action);
	}
}
