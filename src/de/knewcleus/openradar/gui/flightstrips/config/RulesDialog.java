package de.knewcleus.openradar.gui.flightstrips.config;

// TODO: rule name edit

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import de.knewcleus.openradar.gui.flightstrips.actions.AbstractAction;
import de.knewcleus.openradar.gui.flightstrips.conditions.AbstractOperatorCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.AbstractCondition;
import de.knewcleus.openradar.gui.flightstrips.config.DialogTools.RemoveActionListenerFactory;

public class RulesDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private final RulesManager rulesManager;

	private final JList<Rule> list = new JList<Rule>();

	private final JPanel details = new JPanel(new BorderLayout());
	private final NameEdit ruleName = new NameEdit();
	private final JPanel conditions_panel = new JPanel(new BorderLayout());
	private final JPanel actions_panel = new JPanel(new BorderLayout());
	private final JButton moveUp = new JButton("up");
	private final JButton moveDown = new JButton("down");
	private final JButton newRule = new JButton("+");
	private final JButton deleteRule = new JButton("-");
	
	private boolean editCondition = false;
	private boolean editAction = false;
	
	public RulesDialog(RulesManager rulesManager) {
		this.rulesManager =  rulesManager;
		setTitle("Rules");
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
		fillDetailsPanel();
		details.setVisible(false);
		panel.add(details, gbc);
		gbc.gridx++;
		// do layout and adjust size
		rulesSelectionChanged();
		validate();
		pack();
	}
	
	protected JComponent createRulesList() {
        JPanel panel = new JPanel(new GridBagLayout());
        // constraints
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 2;
		// --- rules list ---
		panel.add(list, gbc);
		list.setToolTipText("<html>select rule to edit rule details</html>");
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setModel(rulesManager);
		list.setCellRenderer(new ListCellRenderer<Rule>() {
			@Override
			public Component getListCellRendererComponent(
					JList<? extends Rule> list, Rule value,
					int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = new JLabel(value.getMenuText());
				label.setFont(list.getFont());
				label.setOpaque(true);
				label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
				label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
				return label;
			}
		});
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				rulesSelectionChanged();
			}
		});
		// --- buttons ---
		gbc.gridy++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.5;
		gbc.weighty = 0;
		gbc.gridwidth = 1;
		// move section up
		moveUp.setToolTipText("<html>move selected section up</html>");
		moveUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Rule selected = list.getSelectedValue();
				if (selected != null)
				{
					rulesManager.moveRule(selected, -1);
					list.setSelectedValue(selected, true);
				}
			}
		});
		panel.add(moveUp, gbc);
		gbc.gridx++;
		// move section down
		moveDown.setToolTipText("<html>move selected section down</html>");
		moveDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Rule selected = list.getSelectedValue();
				if (selected != null)
				{
					rulesManager.moveRule(selected, 1);
					list.setSelectedValue(selected, true);
				}
			}
		});
		panel.add(moveDown, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		// add section
		newRule.setToolTipText("<html>create a new section</html>");
		newRule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Rule rule = new Rule("new rule", null, null);
				rulesManager.add(rule);
				list.setSelectedValue(rule, true);
				pack();
			}
		});
		panel.add(newRule, gbc);
		gbc.gridx++;
		// delete section 
		deleteRule.setToolTipText("<html>delete the selected section</html>");
		deleteRule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!list.isSelectionEmpty()) {
					Rule selected = list.getSelectedValue();
					if (selected != null)
					{
						rulesManager.remove(selected);
						pack();
					}
				}
			}
		});
		panel.add(deleteRule, gbc);
		// outer panel
        JPanel outer_panel = new JPanel(new BorderLayout());
		outer_panel.add(panel, BorderLayout.PAGE_START);
		return outer_panel;
	}
	
	protected void rulesSelectionChanged() {
		editCondition = false;
		editAction = false;
		fillConditionsPanel();
		fillActionsPanel();
		boolean isSelected = !list.isSelectionEmpty();
		if (isSelected) ruleName.init();
		details.setVisible(isSelected);
		moveUp.setEnabled(isSelected && (list.getSelectedIndex() > 0));
		moveDown.setEnabled(isSelected && (list.getSelectedIndex() < list.getModel().getSize() - 1));
		deleteRule.setEnabled(isSelected);
		pack();
	}
	
	protected void fillDetailsPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Rule details"));
		details.add(panel, BorderLayout.PAGE_START);
        // constraints
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		// rule name
		panel.add(ruleName, gbc);
		gbc.gridy++;
		// conditions texts
		conditions_panel.setBorder(BorderFactory.createTitledBorder("Condition(s)"));
		conditions_panel.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2) {
					editCondition = !editCondition;
					fillConditionsPanel();
					pack();
				}
			}
		});
		panel.add(conditions_panel, gbc);
		gbc.gridy++;
		// actions texts
		actions_panel.setBorder(BorderFactory.createTitledBorder("Action(s)"));
		actions_panel.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2) {
					editAction = !editAction;
					fillActionsPanel();
					pack();
				}
			}
		});
		panel.add(actions_panel, gbc);
		gbc.gridy++;
	}

	public void showDialog() {
		//setLocationRelativeTo(null);
		setLocation(50, 50);
		setVisible(true);
	}
	
	// --- conditions ---
	
	protected void fillConditionsPanel() {
		conditions_panel.removeAll();
		if (!list.isSelectionEmpty()) {
			Rule selected = list.getSelectedValue();
			if (selected != null) {
				AbstractCondition condition = selected.getCondition();
				JPanel panel = new JPanel(new GridBagLayout());
				conditions_panel.add(panel, BorderLayout.CENTER);
		        // constraints
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx = 0;
				gbc.gridy = 0;
				gbc.weightx = 1.0;
				gbc.fill = GridBagConstraints.BOTH;
				// conditions panel
				panel.add(createConditionComponent(condition, new BaseNewCondition(selected)), gbc);
				gbc.gridx++;
				if (editCondition) {
					// delete button
					gbc.weightx = 0.0;
					JButton delete = new JButton("X");
					delete.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							list.getSelectedValue().setCondition(null);
							fillConditionsPanel();
							pack();
						}
					});
					panel.add(delete, gbc);
					gbc.gridx++;
				}
			}
		}
	}
	
	protected JComponent createConditionComponent(AbstractCondition condition, ActionListener newConditionActionListener) {
		if (condition instanceof AbstractOperatorCondition) return createAbstractOperatorConditionComponent((AbstractOperatorCondition) condition);
		if (editCondition) {
			return (condition == null) ? createNewConditionComponent(newConditionActionListener) : DialogTools.createPanel(condition);
		}
		return DialogTools.createSimpleTextComponent(condition, "<No condition defined!>");
	}
	
	protected JComponent createNewConditionComponent(ActionListener newConditionActionListener) {
		JComboBox<String> conditions = new JComboBox<String>(RulesManager.getAvailableConditionsNames());
		conditions.addActionListener(newConditionActionListener);
		return conditions;
	}
	
	protected JComponent createAbstractOperatorConditionComponent(AbstractOperatorCondition rule) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(rule.getSimpleText()));
        // constraints
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		// list rules
		for (AbstractCondition r : rule.getConditions()) {
			JPanel row_panel = new JPanel(new GridBagLayout());
			panel.add(row_panel, gbc);
			gbc.gridy++;
	        // constraints
			GridBagConstraints gbc_row = new GridBagConstraints();
			gbc_row.gridx = 0;
			gbc_row.gridy = 0;
			gbc_row.weightx = 1.0;
			gbc_row.fill = GridBagConstraints.BOTH;
			// rule panel
			row_panel.add(createConditionComponent(r, new OperatorNewCondition(rule)), gbc_row);
			gbc_row.gridx++;
			if (editCondition) {
				// delete button
				gbc_row.weightx = 0.0;
				JButton delete = new JButton("X");
				delete.addActionListener(new OperatorRemoveCondition(rule, r));
				row_panel.add(delete, gbc_row);
				gbc_row.gridx++;
			}
		}
		if (editCondition) panel.add(createConditionComponent(null, new OperatorNewCondition(rule)), gbc);
		return panel;
	}
	
	// --- actions ---
	
	protected void fillActionsPanel() {
		ArrayList<AbstractAction> actions = new ArrayList<AbstractAction>(); 
		if (!list.isSelectionEmpty()) {
			Rule selected = list.getSelectedValue();
			if (selected != null) actions = selected.getActions();
		}
		
		ActionListener newActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> actions = (JComboBox<String>)e.getSource();
				String selected = (String) actions.getSelectedItem();
				try {
					list.getSelectedValue().addAction(RulesManager.createActionClassByName(selected, AbstractAction.UseCase.RULE));
					fillActionsPanel();
					pack();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		};
		
		DialogTools.fillActionsPanel(actions_panel, actions, editAction, AbstractAction.UseCase.RULE, newActionListener, new RuleRemoveActionListenerFactory());
	}
	
	// === NameEdit ===
	
	protected class NameEdit extends RegExpEdit {
		
		private static final long serialVersionUID = 1L;

		@Override
		protected String fetchStringValue() {
			return list.getSelectedValue().getMenuText();
		}
		
		@Override
		protected void putStringValue(String value) {
			rulesManager.renameRule(list.getSelectedValue(), value);
		}

		@Override
		protected String fetchRegExp() {
			return "[^\"]*";
		}

		@Override
		protected String fetchToolTipText() { 
			return "Enter a name which helps you (and others) to guess what should happen under which conditions";
		} 

	}
	
	// === ActionListeners ===
	
	protected class BaseNewCondition implements ActionListener {
		
		protected final Rule rule;
		
		public BaseNewCondition(Rule rule) {
			this.rule = rule;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<String> conditions = (JComboBox<String>)e.getSource();
			String selected = (String) conditions.getSelectedItem();
			try {
				rule.setCondition(RulesManager.createConditionClassByName(selected));
				fillConditionsPanel();
				pack();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}
		
	protected class OperatorNewCondition implements ActionListener {
		
		protected final AbstractOperatorCondition condition;
		
		public OperatorNewCondition(AbstractOperatorCondition condition) {
			this.condition = condition;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<String> conditions = (JComboBox<String>)e.getSource();
			String selected = (String) conditions.getSelectedItem();
			try {
				condition.add(RulesManager.createConditionClassByName(selected));
				fillConditionsPanel();
				pack();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}
		
	protected class OperatorRemoveCondition implements ActionListener {
		
		protected final AbstractOperatorCondition operator_condition;
		protected final AbstractCondition condition;
		
		public OperatorRemoveCondition(AbstractOperatorCondition condition, AbstractCondition removecondition) {
			this.operator_condition = condition;
			this.condition = removecondition;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			operator_condition.remove(condition);
			fillConditionsPanel();
			pack();
		}
	}

	protected class RemoveAction implements ActionListener {
		
		protected final AbstractAction action;
		
		public RemoveAction(AbstractAction action) {
			this.action = action;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			list.getSelectedValue().removeAction(action);
			fillActionsPanel();
			pack();
		}
	}

	protected class RuleRemoveActionListenerFactory extends RemoveActionListenerFactory {
		@Override
		public ActionListener createRemoveActionListener(AbstractAction action) {
			return new RemoveAction(action);
		}
	}
	
}
