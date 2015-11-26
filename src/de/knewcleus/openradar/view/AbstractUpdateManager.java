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
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractUpdateManager implements IUpdateManager {
	protected IView rootView = null;
	protected final Set<IView> invalidViews = new HashSet<IView>();
	protected boolean validating = false;
	
	@Override
	public IView getRootView() {
		return rootView;
	}
	
	@Override
	public void setRootView(IView rootView) {
		this.rootView = rootView;
	}

	@Override
	public synchronized void markViewInvalid(IView view) {
		invalidViews.add(view);
		scheduleRevalidation();
	}
	
	/**
	 * Schedule a call to {@link #validate()}.
	 */
	protected abstract void scheduleRevalidation();
	
	/**
	 * Ensure that all views are valid.
	 */
	public synchronized void validate() {
		if (validating) {
			return;
		}
		try {
			validating = true;
			for (IView view: invalidViews) {
				view.validate();
			}
		} finally {
			validating = false;
		}
	}
	
	/**
	 * Repaint the view on the given graphics context.
	 */
	public void paint(Graphics2D g2d) {
		if (rootView==null) {
			return;
		}
		final Rectangle clipBounds = g2d.getClipBounds();
		g2d.clearRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
		final ViewPaintVisitor paintVisitor = new ViewPaintVisitor(g2d);
		rootView.accept(paintVisitor);
	}
}
