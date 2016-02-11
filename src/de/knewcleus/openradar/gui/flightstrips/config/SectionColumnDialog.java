package de.knewcleus.openradar.gui.flightstrips.config;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import de.knewcleus.openradar.gui.flightstrips.LogicManager.FilenameId;
import de.knewcleus.openradar.gui.flightstrips.SectionData;
import de.knewcleus.openradar.gui.flightstrips.order.AbstractOrder;
import de.knewcleus.openradar.gui.flightstrips.order.OrderManager;

public class SectionColumnDialog extends JDialog implements WindowFocusListener, TableModel {

	private static final long serialVersionUID = 1L;

	private SectionData section = null;
	
	// layout
	private final JPanel layoutPanel;
	private final ButtonGroup filename = new ButtonGroup();
	private final JRadioButton defaultFilename = new JRadioButton("default");
	private final JRadioButton roleFilename = new JRadioButton("role");
	private final JRadioButton airportFilename = new JRadioButton("airport");
	private final JRadioButton callsignFilename = new JRadioButton("callsign");
	private final JButton loadLayout = new JButton("load");
	private final JButton saveLayout = new JButton("save");
	private final JButton rules = new JButton("rules");
	// sections list
	private final JPanel listPanel = new JPanel();
	private final JList<String> sections = new JList<String>();
	private final JButton moveUp = new JButton("up");
	private final JButton moveDown = new JButton("down");
	boolean sections_enable_event;
	// section details
	private final JCheckBox autoVisible = new JCheckBox("auto visible");
	private final JCheckBox showHeader = new JCheckBox("show header");
	private final JTextField sectionTitle = new JTextField();
	private final JCheckBox showColumnTitles = new JCheckBox("show column titles");
	private final OrderComboBox sortOrder = new OrderComboBox();
	private final JCheckBox ascending = new JCheckBox("ascending");
	private final JTable columns = new JTable();
	private final JButton addColumn = new JButton("+");
	private final JButton delColumn = new JButton("-");
	private final JToggleButton more = new JToggleButton("<<");
	
	public SectionColumnDialog() {
		setUndecorated(true);
		addWindowFocusListener(this);
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
        // left column
		layoutPanel = createLayoutColumn(); 
		panel.add(layoutPanel, gbc);
		gbc.gridx++;
        // right column
		panel.add(createSectionsPanel(), gbc);
		gbc.gridx++;
		// layout and size
        doLayout();
        pack();
	}
	
	protected void adjustSizeAndPosition() {
		int x = getX() + getWidth();
		revalidate();
        pack();
		setLocation(x - getWidth(), getY());
	}
	
	public JPanel createSectionsPanel() {
        JPanel outer_panel = new JPanel();
        outer_panel.setBorder(BorderFactory.createTitledBorder("Sections"));
        outer_panel.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        outer_panel.add(panel, BorderLayout.PAGE_START);
        // constraints
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		// sections list
		panel.add(createSectionsColumn(), gbc);
		gbc.gridx++;
        // section details
		gbc.weightx = 0.0;
		panel.add(createSectionColumn(), gbc);
		gbc.gridx++;
		return outer_panel;
	}

	public JPanel createSectionsColumn() {
        JPanel panel = listPanel;
        panel.setLayout(new GridBagLayout());
        // constraints
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 2;
		// sections list
		sections.setToolTipText("<html>select section to edit details</html>");
		sections.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		sections.setModel(new DefaultListModel<String>());
		sections.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (sections_enable_event) {
					setSection(section.getLogicManager().getSectionByTitle(sections.getSelectedValue()), more.isSelected());
				}
			}
		});
		panel.add(sections, gbc);
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
		    	section.getLogicManager().moveSectionUp(section);
		    	updateSections();
			}
		});
		panel.add(moveUp, gbc);
		gbc.gridx++;
		// move section down
		moveDown.setToolTipText("<html>move selected section down</html>");
		moveDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		    	section.getLogicManager().moveSectionDown(section);
		    	updateSections();
			}
		});
		panel.add(moveDown, gbc);
		gbc.gridx++;
		return panel;
	}
	
	public void updateSections() {
		if (section != null) {
			sections_enable_event = false;
			DefaultListModel<String> model = (DefaultListModel<String>) sections.getModel();
			model.clear();
			for (SectionData s : section.getLogicManager().getSections()) {
				model.addElement(s.getTitle());
			}
			sections.setSelectedValue(section.getTitle(), true);
			sections_enable_event = true;
		}
	}
		
	public JPanel createSectionColumn() {
        JPanel outer_panel = new JPanel();
        outer_panel.setBorder(BorderFactory.createTitledBorder("Section"));
        outer_panel.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        outer_panel.add(panel, BorderLayout.PAGE_START);
        // constraints
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		// autoVisible
		autoVisible.setToolTipText("<html>section is ...<br>unchecked: ...always visible<br>checked: ...visible if not empty</html>");
		autoVisible.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		    	section.setAutoVisible(e.getStateChange() == ItemEvent.SELECTED);
		    }
		});
		autoVisible.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {}
			@Override
			public void focusLost(FocusEvent e) {
		    	section.setAutoVisible(autoVisible.isSelected());
			}
		});
		panel.add(autoVisible, gbc);
		gbc.gridy++;
		// showHeader
		showHeader.setToolTipText("<html>show or hide the header of the section</html>");
		showHeader.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		    	section.setShowHeader(e.getStateChange() == ItemEvent.SELECTED);
		    }
		});
		showHeader.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {}
			@Override
			public void focusLost(FocusEvent e) {
		    	section.setShowHeader(showHeader.isSelected());
			}
		});
		panel.add(showHeader, gbc);
		gbc.gridy++;
		// sectionTitle
		sectionTitle.setToolTipText("<html>enter a section title and hit RETURN</html>");
		sectionTitle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		    	section.setTitle(sectionTitle.getText());
		    	updateSections();
			}
		});
		sectionTitle.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {}
			@Override
			public void focusLost(FocusEvent e) {
		    	section.setTitle(sectionTitle.getText());
			}
		});
		panel.add(sectionTitle, gbc);
		gbc.gridy++;
		// showColumnTitles
		showColumnTitles.setToolTipText("<html>show or hide column titles</html>");
		showColumnTitles.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		    	section.setShowColumnTitles(e.getStateChange() == ItemEvent.SELECTED);
		    }
		});
		showColumnTitles.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {}
			@Override
			public void focusLost(FocusEvent e) {
		    	section.setShowColumnTitles(showColumnTitles.isSelected());
			}
		});
		panel.add(showColumnTitles, gbc);
		gbc.gridy++;
		// sortOrder
		sortOrder.setToolTipText("<html>choose how the flightstrips in this section<br>should be sorted from top to bottom</html>");
		sortOrder.getModel().addListDataListener(new ListDataListener() {
			@Override
			public void intervalAdded(ListDataEvent e) {}
			@Override
			public void intervalRemoved(ListDataEvent e) {}
			@Override
			public void contentsChanged(ListDataEvent e) {
				writeSortOrder((AbstractOrder<?>)sortOrder.getSelectedItem());
			}
		});
		sortOrder.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {}
			@Override
			public void focusLost(FocusEvent e) {
				writeSortOrder((AbstractOrder<?>)sortOrder.getSelectedItem());
			}
		});
		panel.add(sortOrder, gbc);
		gbc.gridy++;
		// ascending
		ascending.setToolTipText("<html>ascending or descending sort order</html>");
		ascending.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		    	section.getOrder().setAscending(e.getStateChange() == ItemEvent.SELECTED);
		    }
		});
		ascending.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {}
			@Override
			public void focusLost(FocusEvent e) {
				section.getOrder().setAscending(ascending.isSelected());
			}
		});
		panel.add(ascending, gbc);
		gbc.gridy++;
		// columns
		columns.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		columns.setCellSelectionEnabled(false);
		columns.setShowVerticalLines(false);
		columns.setToolTipText("<html>enter a column title and hit RETURN</html>");
		gbc.insets = new Insets(5, 5, 5, 5);
		panel.add(columns, gbc);
		gbc.gridy++;
		// row: add and remove column
		JPanel row_panel = new JPanel();
		panel.add(row_panel, gbc);
		row_panel.setLayout(new GridLayout(1, 4, 5, 0));
		gbc.gridy++;
        // constraints
		// add column
		more.setToolTipText("<html>show more options</html>");
		more.setSelected(true);
		more.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				layoutPanel.setVisible(more.isSelected());
				listPanel.setVisible(more.isSelected());
				adjustSizeAndPosition();
			}
		});
		row_panel.add(more);
		// spacer
		row_panel.add(new JLabel());
		// add column
		addColumn.setToolTipText("<html>increase the number of columns</html>");
		addColumn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		    	section.addColumn("");
				adjustSizeAndPosition();
			}
		});
		row_panel.add(addColumn);
		// remove column
		delColumn.setToolTipText("<html>decrease the number of columns</html>");
		delColumn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		    	section.removeLastColumn();
				adjustSizeAndPosition();
			}
		});
		row_panel.add(delColumn);
		return outer_panel;
	}
	
	public JPanel createLayoutColumn() {
        JPanel outer_panel = new JPanel();
        outer_panel.setBorder(BorderFactory.createTitledBorder("Layout"));
        outer_panel.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        outer_panel.add(panel, BorderLayout.PAGE_START);
        // constraints
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		// row: load and save layout: select filename
		defaultFilename.setToolTipText("<html>select slot to load from or save to<br>default for every airport and every role</html>");
		roleFilename.setToolTipText("<html>select slot to load from or save to<br>default for every airport and this role</html>");
		airportFilename.setToolTipText("<html>select slot to load from or save to<br>default for this airport and every role</html>");
		callsignFilename.setToolTipText("<html>select slot to load from or save to<br>default for this airport and this role</html>");
		filename.add(defaultFilename);
		panel.add(defaultFilename, gbc);
		gbc.gridy++;
		filename.add(roleFilename);
		panel.add(roleFilename, gbc);
		gbc.gridy++;
		filename.add(airportFilename);
		panel.add(airportFilename, gbc);
		gbc.gridy++;
		filename.add(callsignFilename);
		panel.add(callsignFilename, gbc);
		gbc.gridy++;
		callsignFilename.setSelected(true);
		// load layout
		loadLayout.setToolTipText("<html>load layout</html>");
		loadLayout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				SectionData s = section;
				section = null;
				s.getLogicManager().LoadLayout(callsignFilename.isSelected() ? FilenameId.CALLSIGN : 
											   (airportFilename.isSelected() ? FilenameId.AIRPORT : 
												(roleFilename.isSelected() ? FilenameId.ROLE : FilenameId.DEFAULT)));
			}
		});
		panel.add(loadLayout, gbc);
		gbc.gridy++;
		// save layout
		saveLayout.setToolTipText("<html>save layout</html>");
		saveLayout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				section.getLogicManager().SaveLayout(callsignFilename.isSelected() ? FilenameId.CALLSIGN : 
					                                 (airportFilename.isSelected() ? FilenameId.AIRPORT : 
						                              (roleFilename.isSelected() ? FilenameId.ROLE : FilenameId.DEFAULT)));
				section = null;
				setVisible(false);
			}
		});
		panel.add(saveLayout, gbc);
		gbc.gridy++;
		// rules Button
		// save layout
		rules.setToolTipText("<html>save layout</html>");
		rules.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				section.getLogicManager().showDialog();
			}
		});
		outer_panel.add(rules, BorderLayout.PAGE_END);
		return outer_panel;
	}
	
	public void setSection(SectionData section, boolean showMore) {
		this.section = section;
		autoVisible.setSelected(section.isAutoVisible());
		showHeader.setSelected(section.getShowHeader());
		sectionTitle.setText(section.getTitle());
		showColumnTitles.setSelected(section.getShowColumnTitles());
		AbstractOrder<?> order = section.getOrder();
		//System.out.println("section: " + section.getTitle() + " order: " + (order == null ? "<null>" : order.getClass().getSimpleName()));
		sortOrder.setSelectedOrder(order);
		ascending.setEnabled(order != null);
		if (order != null) ascending.setSelected(order.isAscending());
		// layout and size
		columns.setModel(this);
		columns.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		columns.getColumnModel().getColumn(0).setMaxWidth(10);
		updateSections();
		if (more.isSelected() != showMore) more.doClick();
		adjustSizeAndPosition();
	}

	protected void writeSortOrder(AbstractOrder<?> order) {
		section.setOrder(order);
		ascending.setEnabled(order != null);
		if (order != null) ascending.setSelected(order.isAscending());
	}
	
	// --- WindowFocusListener ---
	
	@Override
	public void windowGainedFocus(WindowEvent e) {
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		section = null;
		setVisible(false);
	}

	// ========================================================
	
	protected class OrderComboBox extends JComboBox<AbstractOrder<? extends Comparable<?>>> {

		private static final long serialVersionUID = 1L;

	    public OrderComboBox() {
			super();
			setModel(new OrderListModel());
			setRenderer(new OrderRenderer());
		}
		
	    public void setSelectedOrder(AbstractOrder<?> order) {
	    	OrderListModel model = (OrderListModel)getModel();
            setSelectedItem(model.getElementAt(model.indexOfClass(order)));
	    }

	    // ========================================================

		protected class OrderListModel extends ArrayList<AbstractOrder<? extends Comparable<?>>> implements ComboBoxModel<AbstractOrder<? extends Comparable<?>>> {
			
			private static final long serialVersionUID = 1L;
			
			private int selectedItem = -1;
			private final ArrayList<ListDataListener> listDataListeners = new ArrayList<ListDataListener>(); 

			public OrderListModel() {
				// create a list of AbstractOrder instances (not classes)
				addAll(OrderManager.getAvailableOrders()); 
			}
			
			protected int indexOfClass(Object order) {
				if (order == null) return 0;
				for (int i = 0; i < size(); i++) {
					if (get(i).getClass().equals(order.getClass())) return i + 1;
				}
				return -1;
			}

			@Override
			public int getSize() {
				return size() + 1;
			}

			@Override
			public AbstractOrder<? extends Comparable<?>> getElementAt(int index) {
				return (index <= 0) ? null : get(index - 1);
			}

			@Override
			public void addListDataListener(ListDataListener l) {
				listDataListeners.add(l);
			}

			@Override
			public void removeListDataListener(ListDataListener l) {
				listDataListeners.remove(l);
			}

			@Override
			public void setSelectedItem(Object anItem) {
				int i = indexOfClass(anItem);
				if (i >= 0) {
					selectedItem = i;
					for (ListDataListener l : listDataListeners) l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1)); 
				}
			}

			@Override
			public Object getSelectedItem() {
				return getElementAt(selectedItem);
			}
			
		}
		
		// ========================================================

		protected class OrderRenderer extends JLabel implements ListCellRenderer<AbstractOrder<? extends Comparable<?>>> {

			private static final long serialVersionUID = 1L;

			public OrderRenderer() {
				setOpaque(true);
			}
			
			@Override
			public Component getListCellRendererComponent(
					JList<? extends AbstractOrder<? extends Comparable<?>>> list,
					AbstractOrder<? extends Comparable<?>> value, int index,
					boolean isSelected, boolean cellHasFocus) {
				setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
				setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
				setText(value == null ? "manual" : "sort by " + value.getDisplayName());
				setEnabled(list.isEnabled());
				setFont(list.getFont());
				setOpaque(true);
				return this;
			}

		}
		
	}
	
	// =====================================================
	
	protected class ColumnEdit extends JTextField {

		private static final long serialVersionUID = -2687496532378115367L;

		private final String regexp;
		
		public ColumnEdit() {
			this.regexp = "[1-9]";
			setPreferredSize(new Dimension(12, getMinimumSize().height));
			((AbstractDocument) getDocument()).setDocumentFilter(new RegExpEditDocumentFilter());
		}

		// =====================================================

		protected class RegExpEditDocumentFilter extends DocumentFilter {
			
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
				string = string.toUpperCase();
				String text = getText();
				text = text.substring(0, offset) + string + text.substring(offset);
				if (text.matches(regexp)) {
					fb.insertString(offset, string, attr);
				}
			}

			public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
				string = string.toUpperCase();
				String text = getText();
				text = text.substring(0, offset) + string + text.substring(offset + length);
				if (text.matches(regexp)) {
					fb.replace(offset, length, string, attrs);
				}
			}

			public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
				String text = getText();
				text = text.substring(0, offset) + text.substring(offset + length);
				if (text.matches(regexp)) {
					fb.remove(offset, length);
				}
			}
			
		}
		
	}

	// --- TableModel ---
	
	@Override
	public int getRowCount() {
		return (section == null) ? 0 : section.getColumnCount();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return "";
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex > 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return columnIndex == 0 ? rowIndex : ((section == null) ? "" : section.getColumn(rowIndex).getTitle());
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (section != null) {
			section.getColumn(rowIndex).setTitle(aValue.toString());
			section.getPanel().recreateContents();
		}
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}
	
}
