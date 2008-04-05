package de.knewcleus.radar.ui;

import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.ListModel;
import javax.swing.UIManager;

import de.knewcleus.radar.ui.plaf.ScrollableListMenuUI;

/**
 * A ScrollableListMenu presents a menu of items, one of which can be selected by the user, thereby closing the menu.
 * 
 * The list of items is generated from a ListModel, which may return Action objects.
 * 
 * @author ralfg
 *
 */
public class ScrollableListMenu extends JComponent {
	private static final long serialVersionUID = -1480809186587554356L;
	protected static final String uiClassID="ScrollableListMenuUI";
	
	protected ListModel listModel;
	
	public ScrollableListMenu(ListModel listMenuModel) {
		this.listModel=listMenuModel;
		updateUI();
	}
	
	public Rectangle getCellBounds(int index0, int index1) {
		return getUI().getCellBounds(index0, index1);
	}
	
	public void ensureIndexIsVisible(int index) {
		getUI().ensureIndexIsVisible(index);
	}

	public ListModel getListModel() {
		return listModel;
	}

	public void setListModel(ListModel listMenuModel) {
		this.listModel = listMenuModel;
		invalidate();
	}
	
	public void setUI(ScrollableListMenuUI newUI) {
		super.setUI(newUI);
	}
	
	@Override
	public void updateUI() {
		setUI((ScrollableListMenuUI)UIManager.getUI(this));
	}
	
	public ScrollableListMenuUI getUI() {
		return (ScrollableListMenuUI)ui;
	}
	
	@Override
	public String getUIClassID() {
		return uiClassID;
	}
}
