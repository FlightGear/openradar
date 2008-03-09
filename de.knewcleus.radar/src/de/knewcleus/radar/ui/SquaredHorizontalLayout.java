package de.knewcleus.radar.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class SquaredHorizontalLayout implements LayoutManager {
	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public void layoutContainer(Container parent) {
		Component[] components=parent.getComponents();
		if (components.length==0)
			return;
		Dimension parentDimension=parent.getSize();
		
		int maxWidth=parentDimension.width/components.length;
		int maxHeight=parentDimension.height;
		
		int componentSize=Math.min(maxWidth, maxHeight);
		int yOffset=(parentDimension.height-componentSize)/2;
		int xOffset=(parentDimension.width-componentSize*components.length)/2;
		
		for (Component comp: components) {
			comp.setBounds(xOffset, yOffset, componentSize, componentSize);
			xOffset+=componentSize;
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		Component[] components=parent.getComponents();
		int maxMinSize=0;
		
		for (Component comp: components) {
			Dimension minCompSize=comp.getMinimumSize();
			maxMinSize=Math.max(maxMinSize,Math.max(minCompSize.width, minCompSize.height));
		}
		
		return new Dimension(maxMinSize*components.length,maxMinSize);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Component[] components=parent.getComponents();
		int maxPreferredSize=0;
		
		for (Component comp: components) {
			Dimension minCompSize=comp.getPreferredSize();
			maxPreferredSize=Math.max(maxPreferredSize,Math.max(minCompSize.width, minCompSize.height));
		}
		
		return new Dimension(maxPreferredSize*components.length,maxPreferredSize);
	}
}
