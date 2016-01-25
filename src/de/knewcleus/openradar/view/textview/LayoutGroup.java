/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.textview;

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
		invalidate();
	}
	
	public void remove(ILayoutPart part) {
		parts.remove(part);
		invalidate();
	}
	
	public void clear() {
		parts.clear();
		invalidate();
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
		invalidate();
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
		invalidate();
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
		invalidate();
	}

	@Override
	public void invalidate() {
		layoutManager.invalidate();
		container.invalidate();
	}

	@Override
	public void traverse(ILayoutPartVisitor visitor) {
		for (ILayoutPart part: parts) {
			visitor.visit(part);
		}
	}

}
