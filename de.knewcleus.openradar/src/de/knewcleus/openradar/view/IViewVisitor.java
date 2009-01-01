package de.knewcleus.openradar.view;

public interface IViewVisitor {
	public void visitView(IView view);
	public void visitElement(IElement element);
	public void visitContainer(IContainer container);
}
