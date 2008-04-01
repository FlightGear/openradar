package de.knewcleus.radar.ui.vehicles;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ListModel;

public class FlightLevelListModel extends AbstractListModel implements ListModel {
	private static final long serialVersionUID = -7225191589275978336L;
	protected final int lowestLevel;
	protected final int highestLevel;
	protected final int levelStep;
	protected final int size;
	
	public FlightLevelListModel(int lowestLevel, int highestLevel, int levelStep) {
		assert(lowestLevel<=highestLevel);
		this.lowestLevel=lowestLevel;
		this.highestLevel=highestLevel;
		this.levelStep=levelStep;
		size=(highestLevel-lowestLevel)/levelStep+1;
	}

	@Override
	public Object getElementAt(int index) {
		final String levelText=String.format("FL%03d", lowestLevel+index*levelStep);
		return new AbstractAction(levelText) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String toString() {
				return (String)getValue(Action.NAME);
			}
		};
	}

	@Override
	public int getSize() {
		return size;
	}
	
	public int getLowestLevel() {
		return lowestLevel;
	}
	
	public int getHighestLevel() {
		return highestLevel;
	}
	
	public int getLevelStep() {
		return levelStep;
	}
	
	public int getIndexForLevel(int level) {
		if (level<lowestLevel)
			return 0;
		if (level>highestLevel)
			return size-1;
		return (level-lowestLevel+(levelStep+1)/2)/levelStep;
	}
}
