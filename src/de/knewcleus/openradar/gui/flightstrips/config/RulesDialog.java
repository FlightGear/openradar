package de.knewcleus.openradar.gui.flightstrips.config;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.flightstrips.rules.RuleAndAction;

public class RulesDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final GuiMasterController master;
	
	private final JScrollPane scrollpane = new JScrollPane();
	private final JList<RuleAndAction> list = new JList<RuleAndAction>();
	
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
			}
		});
		panel.add(list, gbc);
		gbc.gridx++;
		
		validate();
		pack();
	}
	
	public void showDialog() {
		//setLocationRelativeTo(null);
		setLocation(50, 50);
		setVisible(true);
	}
	
}
