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
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * The view paint visitor is responsible for painting the map.
 * 
 * @author Ralf Gerlich
 *
 */
public class ViewPaintVisitor implements IViewVisitor {
	protected final Graphics2D g2d;
	protected final Rectangle2D clip;
	
	public ViewPaintVisitor(Graphics2D g2d) {
		this.g2d = g2d;
		clip = g2d.getClipBounds();
	}

	@Override
	public void visitContainer(IContainer container) {
		if (container.isVisible() && intersectsClip(container)) {
			container.paint(g2d);
			container.traverse(this);
		}
	}

	@Override
	public void visitView(IView view) {
		if (view.isVisible() && intersectsClip(view)) {
			view.paint(g2d);
		}
	}

	protected boolean intersectsClip(IView view) {
		if (view instanceof IBoundedView) {
			final Rectangle2D extents = ((IBoundedView)view).getDisplayExtents();
			return extents.intersects(clip);
		}
		return true;
	}
}
