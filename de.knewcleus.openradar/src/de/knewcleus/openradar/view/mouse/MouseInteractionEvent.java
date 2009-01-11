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
	
	/**
	 * The <code>ButtonType</code> defines the types of buttons
	 * available.
	 * 
	 * @author Ralf Gerlich
	 *
	 */
	public static enum ButtonType {
		/**
		 * The action button is the button assigned for invoking
		 * actions on the target.
		 */
		ACTION_BUTTON,
		
		/**
		 * The information button is the button assigned for invoking
		 * extended information on the target.
		 */
		INFORMATION_BUTTON;
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
