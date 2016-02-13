package de.knewcleus.openradar.gui.flightstrips.config;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.flightstrips.rules.RuleAndAction;

public class RulesDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final GuiMasterController master;
	
	private final JList<RuleAndAction> list = new JList<RuleAndAction>();
	private final JList<String> rule = new JList<String>(new DefaultListModel<String>());
	private final JList<String> action = new JList<String>(new DefaultListModel<String>());
	
	public RulesDialog(GuiMasterController master) {
		this.master =  master;
		setTitle("Rules and Actions");
		// --- components ---
        JPanel panel = new JPanel();
        setContentPane(panel);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // constraints
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.PAGE_START;
		// rules list
		panel.add(createRulesList(), gbc);
		gbc.gridx++;
		// details
		gbc.weightx = 1.0;
		panel.add(createDetails(), gbc);
		gbc.gridx++;
		// do layout and adjust size
		validate();
		pack();
	}
	
	protected JComponent createRulesList() {
		list.setToolTipText("<html>select section to edit details</html>");
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setModel(master.getRulesManager());
		list.setCellRenderer(new ListCellRenderer<RuleAndAction>() {
			@Override
			public Component getListCellRendererComponent(
					JList<? extends RuleAndAction> list, RuleAndAction value,
					int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = new JLabel(value.getMenuText());
				label.setOpaque(true);
				label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
				label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
				return label;
			}
		});
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				DefaultListModel<String> rule_model = (DefaultListModel<String>) (rule.getModel());
				rule_model.clear();
				DefaultListModel<String> action_model = (DefaultListModel<String>) (action.getModel());
				action_model.clear();
				RuleAndAction selected = list.getSelectedValue();
				System.out.println((selected == null) ? "<null>" : selected.getClass().getSimpleName());  
				if (selected != null) {
					for (String s : selected.getRule().getText()) System.out.println(s);
					for (String s : selected.getRule().getText()) rule_model.addElement(s);
					for (String s : selected.getAction().getText()) action_model.addElement(s);
				}
//				rule.revalidate();
//				action.revalidate();
				pack();
			}
		});
        JPanel panel = new JPanel(new BorderLayout());
		panel.add(list, BorderLayout.PAGE_START);
		return panel;
	}
	
	protected Component createDetails() {
        JPanel outer_panel = new JPanel(new BorderLayout());
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Details"));
        // constraints
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		// rule text
		JPanel rule_panel = new JPanel(new BorderLayout());
		rule_panel.setBorder(BorderFactory.createTitledBorder("Rule"));
		panel.add(rule_panel, gbc);
		gbc.gridy++;
		rule.setToolTipText("<html>description of the conditions</html>");
		rule.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		rule.setFixedCellHeight(rule.getFontMetrics(rule.getFont()).getHeight());
		rule_panel.add(rule, BorderLayout.CENTER);
		// action text
		JPanel action_panel = new JPanel(new BorderLayout());
		action_panel.setBorder(BorderFactory.createTitledBorder("Action"));
		panel.add(action_panel, gbc);
		gbc.gridy++;
		action.setToolTipText("<html>description of the action(s)</html>");
		action.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		action.setFixedCellHeight(action.getFontMetrics(action.getFont()).getHeight());
		action_panel.add(action, BorderLayout.CENTER);
		outer_panel.add(panel, BorderLayout.PAGE_START);
		return outer_panel;
	}

	public void showDialog() {
		//setLocationRelativeTo(null);
		setLocation(50, 50);
		setVisible(true);
	}
	
}
