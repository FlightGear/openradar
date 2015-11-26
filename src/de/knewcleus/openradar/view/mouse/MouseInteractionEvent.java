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

import java.awt.geom.Point2D;

/**
 * A mouse interaction event is sent to a {@link IMouseTargetView} when
 * the user interacts with the view in one of the defined ways.
 * 
 * @author Ralf Gerlich
 * @see IMouseTargetView
 *
 */
public class MouseInteractionEvent {
	/**
	 * The type of event.
	 * @author Ralf Gerlich
	 *
	 */
	public static enum Type {
		/**
		 * The user has initiated a <strong>press and hold</strong>
		 * action on the target.
		 */
		START_HOLD,
		
		/**
		 * The user has ended a <strong>press and hold</strong>
		 * action on the target.
		 */
		END_HOLD,
		
		/**
		 * The user has initiated a <strong>press and drag</strong>
		 * action on the target.
		 */
		START_DRAG,
		
		/**
		 * The user has moved the mouse cursor while holding down the
		 * mouse button in a <strong>press and drag</strong> action.
		 */
		DRAG,
		
		/**
		 * The user has initiated a <strong>press and drag</strong>
		 * action on the target.
		 */
		END_DRAG,
		
		/**
		 * The user has <strong>clicked</strong> on the target.
		 */
		CLICK;
	}
	
	protected final IMouseTargetView target;
	protected final ButtonType button;
	protected final Type type;
	protected final int clickCount;
	protected final Point2D point;
	protected final long when;

	/**
	 * Construct a <code>MouseInteractionEvent</code>.
	 * 
	 * @param target		The target view at which the event is directed.
	 * @param button		The button with which the event is associated.
	 * @param type			The type of event.
	 * @param clickCount	The number of successive clicks.
	 * @param point			The location of the mouse cursor on the display.
	 * @param when			The timestamp of the event in milliseconds.
	 */
	public MouseInteractionEvent(IMouseTargetView target, ButtonType button,
			Type type, int clickCount, Point2D point, long when) {
		this.target = target;
		this.button = button;
		this.type = type;
		this.clickCount = clickCount;
		this.point = point;
		this.when = when;
	}
	
	@Override
	public String toString() {
		return String.format("MouseInteractionEvent[target=%s, button=%s, type=%s, clickCount=%d, point=%s, when=%d",
				target, button, type, clickCount, point, when);
	}

	/**
	 * @return the original target of this event.
	 */
	public IMouseTargetView getTarget() {
		return target;
	}

	/**
	 * @return the button with which this event is associated.
	 */
	public ButtonType getButton() {
		return button;
	}

	/**
	 * @return the type of event.
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * @return the count of successive clicks
	 */
	public int getClickCount() {
		return clickCount;
	}

	/**
	 * @return the display position of the mouse at the time of the event.
	 */
	public Point2D getPoint() {
		return point;
	}

	/**
	 * @return the timestamp of the event in milliseconds.
	 */
	public long getWhen() {
		return when;
	}
}
