package de.knewcleus.radar.ui;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.ListModel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import de.knewcleus.radar.ui.plaf.ListMenuUI;

/**
 * A ListMenu presents a menu of items, one of which can be selected by the user, thereby closing the menu.
 * 
 * The list of items is generated from a ListModel, which may return Action objects.
 * 
 * @author ralfg
 *
 */
public class ListMenu extends JComponent implements Scrollable {
	private static final long serialVersionUID = -1480809186587554356L;
	protected static final String uiClassID="ListMenuUI";
	
	protected ListModel model;
	
	protected int visibleListElements=9;
	
	public ListMenu(ListModel listMenuModel) {
		this.model=listMenuModel;
		updateUI();
	}
	
	public void setVisibleListElements(int visibleListElements) {
		this.visibleListElements = visibleListElements;
	}
	
	public int getVisibleListElements() {
		return visibleListElements;
	}
	
	public Rectangle getCellBounds(int index0, int index1) {
		return getUI().getCellBounds(index0, index1);
	}
	
	public void ensureIndexIsVisible(int index) {
		getUI().ensureIndexIsVisible(index);
	}

	public ListModel getModel() {
		return model;
	}

	public void setModel(ListModel newValue) {
		ListModel oldValue=this.model;
		this.model = newValue;
		firePropertyChange("model", oldValue, newValue);
		invalidate();
	}
	
	public void setUI(ListMenuUI newUI) {
		super.setUI(newUI);
	}
	
	@Override
	public void updateUI() {
		setUI((ListMenuUI)UIManager.getUI(this));
	}
	
	public ListMenuUI getUI() {
		return (ListMenuUI)ui;
	}
	
	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getUI().getPreferredScrollableViewportSize();
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (orientation==SwingConstants.VERTICAL) {
			return visibleRect.height;
		} else {
			return visibleRect.width;
		}
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		final Dimension elementSize=getCellBounds(0, 0).getSize();
		if (orientation==SwingConstants.VERTICAL) {
			return elementSize.height;
		} else {
			return elementSize.width;
		}
	}
}
