package de.knewcleus.openradar.map;

/**
 * An element container groups zero or more elements.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IContainer extends IView {
	/**
	 * Traverse the children of this group with the given visitor from bottom to top.
	 */
	public void traverse(IViewVisitor visitor);
}
