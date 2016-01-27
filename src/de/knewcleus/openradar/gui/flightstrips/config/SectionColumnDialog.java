package de.knewcleus.openradar.gui.flightstrips.config;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.knewcleus.openradar.gui.flightstrips.SectionData;
import de.knewcleus.openradar.gui.flightstrips.order.AbstractOrder;
import de.knewcleus.openradar.gui.flightstrips.order.OrderManager;

public class SectionColumnDialog extends JDialog implements WindowFocusListener {

	private static final long serialVersionUID = 1L;

	private SectionData section = null;
	
	private final JCheckBox autoVisible = new JCheckBox("auto visible");
	private final JCheckBox showHeader = new JCheckBox("show header");
	private final JCheckBox showColumnTitles = new JCheckBox("show column titles");
	private final OrderComboBox sortOrder = new OrderComboBox();
	private final JCheckBox ascending = new JCheckBox("ascending");
	
	public SectionColumnDialog() {
		setTitle("section / columns configuration");
		addWindowFocusListener(this);
		// --- components ---
        JPanel jPnlContentPane = new JPanel();
        setContentPane(jPnlContentPane);
        jPnlContentPane.setLayout(new GridBagLayout());
        // constraints
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		// autoVisible
		autoVisible.setToolTipText("<html>section is ...<br>unchecked: ...always visible<br>checked: ...visible if not empty</html>");
		//autoVisible.setHorizontalAlignment(SwingConstants.CENTER);
		autoVisible.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		    	section.setAutoVisible(e.getStateChange() == ItemEvent.SELECTED);
		    }
		});
		autoVisible.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
			}
			@Override
			public void focusLost(FocusEvent e) {
		    	section.setAutoVisible(autoVisible.isSelected());
			}
		});
		jPnlContentPane.add(autoVisible, gridBagConstraints);
		gridBagConstraints.gridy++;
		// showHeader
		showHeader.setToolTipText("<html>show or hide the header of the section</html>");
		//showHeader.setHorizontalAlignment(SwingConstants.CENTER);
		showHeader.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		    	section.setShowHeader(e.getStateChange() == ItemEvent.SELECTED);
		    }
		});
		showHeader.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
			}
			@Override
			public void focusLost(FocusEvent e) {
		    	section.setShowHeader(showHeader.isSelected());
			}
		});
		jPnlContentPane.add(showHeader, gridBagConstraints);
		gridBagConstraints.gridy++;
		// showColumnTitles
		showColumnTitles.setToolTipText("<html>show or hide column titles</html>");
		//showColumnTitles.setHorizontalAlignment(SwingConstants.CENTER);
		showColumnTitles.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		    	section.setShowColumnTitles(e.getStateChange() == ItemEvent.SELECTED);
		    }
		});
		showColumnTitles.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
			}
			@Override
			public void focusLost(FocusEvent e) {
		    	section.setShowColumnTitles(showColumnTitles.isSelected());
			}
		});
		jPnlContentPane.add(showColumnTitles, gridBagConstraints);
		gridBagConstraints.gridy++;
		// sortOrder
		sortOrder.setToolTipText("<html>choose how the flightstrips in this section<br>should be sorted from top to bottom</html>");
		sortOrder.getModel().addListDataListener(new ListDataListener() {

			@Override
			public void intervalAdded(ListDataEvent e) {
			}

			@Override
			public void intervalRemoved(ListDataEvent e) {
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				writeSortOrder((AbstractOrder<?>)sortOrder.getSelectedItem());
			}
		});
		sortOrder.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
			}
			@Override
			public void focusLost(FocusEvent e) {
				writeSortOrder((AbstractOrder<?>)sortOrder.getSelectedItem());
			}
		});
		jPnlContentPane.add(sortOrder, gridBagConstraints);
		gridBagConstraints.gridy++;
		// ascending
		ascending.setToolTipText("<html>show or hide column titles</html>");
		//ascending.setHorizontalAlignment(SwingConstants.CENTER);
		ascending.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		    	section.getOrder().setAscending(e.getStateChange() == ItemEvent.SELECTED);
		    }
		});
		ascending.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
			}
			@Override
			public void focusLost(FocusEvent e) {
				section.getOrder().setAscending(ascending.isSelected());
			}
		});
		jPnlContentPane.add(ascending, gridBagConstraints);
		gridBagConstraints.gridy++;
		
		// layout and size
        doLayout();
        pack();
	}
	
	public void setSection(SectionData section) {
		this.section = section;
		autoVisible.setSelected(section.isAutoVisible());
		showHeader.setSelected(section.getShowHeader());
		showColumnTitles.setSelected(section.getShowColumnTitles());
		AbstractOrder<?> order = section.getOrder();
		sortOrder.setSelectedOrder(order);
		ascending.setEnabled(order != null);
		if (order != null) ascending.setSelected(order.isAscending());
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
	
}
