/**
 * Copyright (C) 2008-2009 Ralf Gerlich
 * Copyright (C) 2012,2013 Wolfram Wagner
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
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LayeredView implements IContainer {
	protected final IViewerAdapter viewAdapter;
	protected final List<IView> views = Collections.synchronizedList(new ArrayList<IView>());
	protected boolean visible = true;

	public LayeredView(IViewerAdapter mapViewAdapter) {
		this.viewAdapter = mapViewAdapter;
	}

	public synchronized void clear() {
	    for(IView view : new ArrayList<IView>(views)) {
	        removeView(view);
	    }
	}

	public synchronized void pushView(IView view) {
		views.add(view);
		if (view instanceof IBoundedView) {
			viewAdapter.getUpdateManager().markRegionDirty(((IBoundedView)view).getDisplayExtents());
		} else {
			viewAdapter.getUpdateManager().markViewportDirty();
		}
	}

	public synchronized void removeView(IView view) {
		if (view instanceof IBoundedView) {
			viewAdapter.getUpdateManager().markRegionDirty(((IBoundedView)view).getDisplayExtents());
		} else {
			viewAdapter.getUpdateManager().markViewportDirty();
		}
		views.remove(view);
	}

	@Override
	public synchronized boolean isVisible() {
		return visible;
	}

	public synchronized void setVisible(boolean visible) {
		if (visible==this.visible) {
			return;
		}
		this.visible = visible;
		viewAdapter.getUpdateManager().markViewportDirty();
	}

	@Override
	public synchronized void accept(IViewVisitor visitor) {
		visitor.visitContainer(this);
	}

	@Override
	public synchronized void traverse(IViewVisitor visitor) {
		for (IView view: views) {
			view.accept(visitor);
		}
	}

	@Override
	public synchronized void validate() {}

	@Override
	public synchronized void paint(Graphics2D g2d) {}

    public String getTooltipText(Point p) {
        for (IView view: views) {
            String text = view.getTooltipText(p);
            if(text!=null) return text;
        }
        return null;
    }
}
