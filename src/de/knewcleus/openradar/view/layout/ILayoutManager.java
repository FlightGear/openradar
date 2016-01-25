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
package de.knewcleus.openradar.view.layout;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

/**
 * A layout manager represents a layout algorithm for a layout part container.
 * 
 * @author Ralf Gerlich
 * @see ILayoutPartContainer
 * @see ILayoutPart
 *
 */
public interface ILayoutManager {
	/**
	 * @return the minimum size required to layout the elements in the associated container.
	 */
	public Dimension2D getMinimumSize();
	
	/**
	 * @return the preferred size required to layout the elements in the associated container.
	 */
	public Dimension2D getPreferredSize();
	
	/**
	 * Mark the layout as invalid and ensure it is recalculated.
	 */
	public void invalidate();
	
	/**
	 * Layout the associated container in the given bounds.
	 */
	public void layout(Rectangle2D bounds);
}
