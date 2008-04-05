package de.knewcleus.radar.ui;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import de.knewcleus.radar.ui.plaf.VerticalScrollPaneUI;

public class VerticalScrollPane extends JComponent {
	private static final long serialVersionUID = -1270030201787983155L;
	protected static final String uiClassID="VerticalScrollPaneUI";
	
	protected JViewport viewport;
	
	protected int unitIncrement;
	protected boolean unitIncrementSet=false;
	
	public VerticalScrollPane() {
		viewport=new JViewport();
		updateUI();
	}
	
	public JViewport getViewport() {
		return viewport;
	}
	
	public void setUnitIncrement(int unitIncrement) {
		this.unitIncrement = unitIncrement;
		unitIncrementSet=true;
	}
	
	public int getUnitIncrement(int direction) {
		final Component view=viewport.getView();
		
		if (unitIncrementSet)
			return unitIncrement;
		
		if (view==null) {
			return 0;
		}
		
		if (view instanceof Scrollable) {
			Scrollable scrollable=(Scrollable)view;
			return scrollable.getScrollableUnitIncrement(viewport.getViewRect(), SwingConstants.VERTICAL, direction);
		}
		
		return 1;
	}

	public void setUI(VerticalScrollPaneUI newUI) {
		super.setUI(newUI);
	}
	
	@Override
	public void updateUI() {
		setUI((VerticalScrollPaneUI)UIManager.getUI(this));
	}
	
	public VerticalScrollPaneUI getUI() {
		return (VerticalScrollPaneUI)ui;
	}
	
	@Override
	public String getUIClassID() {
		return uiClassID;
	}
}
