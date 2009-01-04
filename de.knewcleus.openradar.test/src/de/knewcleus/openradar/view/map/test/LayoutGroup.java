package de.knewcleus.openradar.view.map.test;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.openradar.view.layout.ILayoutManager;
import de.knewcleus.openradar.view.layout.ILayoutPart;
import de.knewcleus.openradar.view.layout.ILayoutPartContainer;
import de.knewcleus.openradar.view.layout.ILayoutPartVisitor;
import de.knewcleus.openradar.view.layout.Insets2D;

public class LayoutGroup implements ILayoutPart, ILayoutPartContainer {
	protected final ILayoutPartContainer container;
	protected ILayoutManager layoutManager;
	protected final List<ILayoutPart> parts = new ArrayList<ILayoutPart>();
	protected boolean visible = true;
	protected Insets2D insets = new Insets2D();
	
	public LayoutGroup(ILayoutPartContainer container) {
		this.container = container;
	}
	
	public void add(ILayoutPart part) {
		parts.add(part);
		invalidateLayout();
	}
	
	public void remove(ILayoutPart part) {
		parts.remove(part);
		invalidateLayout();
	}
	
	public void clear() {
		parts.clear();
		invalidateLayout();
	}

	@Override
	public double getBaselineOffset(Dimension2D size) {
		return 0;
	}

	@Override
	public ILayoutPartContainer getLayoutPartContainer() {
		return container;
	}
	
	public void setLayoutManager(ILayoutManager layoutManager) {
		this.layoutManager = layoutManager;
		invalidateLayout();
	}

	@Override
	public Dimension2D getMinimumSize() {
		return layoutManager.getMinimumSize();
	}

	@Override
	public Dimension2D getPreferredSize() {
		return layoutManager.getPreferredSize();
	}

	@Override
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
		invalidateLayout();
	}

	@Override
	public void setBounds(Rectangle2D bounds) {
		layoutManager.layout(bounds);
	}

	@Override
	public Insets2D getInsets() {
		return insets;
	}
	
	public void setInsets(Insets2D insets) {
		this.insets = insets;
		invalidateLayout();
	}

	@Override
	public void invalidateLayout() {
		container.invalidateLayout();
	}

	@Override
	public void traverse(ILayoutPartVisitor visitor) {
		for (ILayoutPart part: parts) {
			visitor.visit(part);
		}
	}

}
