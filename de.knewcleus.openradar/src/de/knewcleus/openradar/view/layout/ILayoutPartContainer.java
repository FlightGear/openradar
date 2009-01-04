package de.knewcleus.openradar.view.layout;

/**
 * A layout part container groups one or more layout parts and associates them
 * with a layout manager.
 * 
 * @author Ralf Gerlich
 * 
 * @see ILayoutPart
 */
public interface ILayoutPartContainer {
	/**
	 * Let the given visitor traverse the children of this container.
	 */
	public void traverse(ILayoutPartVisitor visitor);
	
	/**
	 * Mark the layout in this container as invalid and ensure that it is
	 * recalculated.
	 */
	public void invalidateLayout();

	/**
	 * Return the insets of this container.
	 * 
	 * The insets are space left between the borders of the container and its children.
	 */
	public Insets2D getInsets();
}
