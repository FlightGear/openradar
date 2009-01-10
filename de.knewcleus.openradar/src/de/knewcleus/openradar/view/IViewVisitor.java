package de.knewcleus.openradar.view;

public interface IViewVisitor {
	public void visitView(IView view);
	public void visitContainer(IContainer container);
}
