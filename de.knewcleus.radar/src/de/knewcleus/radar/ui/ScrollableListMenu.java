package de.knewcleus.radar.ui;

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
	protected ListModel listModel;
	protected static final String uiClassID="ScrollableListMenuUI";
	
	public ScrollableListMenu(ListModel listMenuModel) {
		this.listModel=listMenuModel;
		updateUI();
	}
	
	public ScrollableListMenuUI getUI() {
		return (ScrollableListMenuUI)ui;
	}
	
	public void ensureIndexIsVisible(int index) {
		getUI().ensureIndexIsVisible(index);
	}

	public ListModel getListModel() {
		return listModel;
	}

	public void setListModel(ListModel listMenuModel) {
		this.listModel = listMenuModel;
	}
	
	protected void setUI(ScrollableListMenuUI newUI) {
		super.setUI(newUI);
	}
	
	@Override
	public void updateUI() {
		setUI((ScrollableListMenuUI)UIManager.getUI(this));
	}
	
	@Override
	public String getUIClassID() {
		return uiClassID;
	}
}
