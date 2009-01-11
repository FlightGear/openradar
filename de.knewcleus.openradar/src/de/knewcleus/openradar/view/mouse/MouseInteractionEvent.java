package de.knewcleus.openradar.view.mouse;

import java.awt.geom.Point2D;

public class MouseInteractionEvent {
	protected final IMouseTargetView target;
	protected final ButtonType button;
	protected final MouseInteractionType type;
	protected final int clickCount;
	protected final Point2D point;
	protected final long when;

	public MouseInteractionEvent(IMouseTargetView target, ButtonType button,
			MouseInteractionType type, int clickCount, Point2D point, long when) {
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

	public IMouseTargetView getTarget() {
		return target;
	}

	public ButtonType getButton() {
		return button;
	}

	public MouseInteractionType getType() {
		return type;
	}
	
	public int getClickCount() {
		return clickCount;
	}

	public Point2D getPoint() {
		return point;
	}

	public long getWhen() {
		return when;
	}
}
