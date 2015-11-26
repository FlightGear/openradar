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
package de.knewcleus.openradar.view.mouse;

import de.knewcleus.openradar.view.IPickable;
import de.knewcleus.openradar.view.IView;

/**
 * <p>A mouse target view is a view with which the user can interact by
 * means of the mouse.</p>
 * 
 * <p>Two distinct mouse buttons are available, the action button and the
 * information button. With each of these the user can click, press and hold or
 * press and drag on a mouse target view.</p>
 * 
 * <p>A <strong>click</strong> is performed by pressing and releasing the mouse button within
 * a time limit called the <strong>hold delay</strong>. Multiple successive clicks
 * are counted (e.g. double-clicks) if the time between the releases does not exceed
 * the <strong>multi-click delay</strong>.</p>
 * 
 * <p><strong>Press and hold</strong> is performed by pressing and holding the button without moving the
 * mouse for at least the time given by the <strong>hold delay</strong>.</p>
 * 
 * <p><strong>Press and drag</strong> is performed by pressing and holding the button and moving the
 * mouse cursor.
 * Press and holding followed by dragging without releasing the button inbetween is
 * interpreted the same as if the button had been released in the mean time.</p>
 * 
 * @author Ralf Gerlich
 * @see MouseButtonManager
 * @see ButtonType
 */
public interface IMouseTargetView extends IPickable, IView {
	public void processMouseInteractionEvent(MouseInteractionEvent e);
}
