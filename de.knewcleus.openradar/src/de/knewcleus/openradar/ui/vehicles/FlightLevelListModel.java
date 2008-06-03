package de.knewcleus.openradar.ui.vehicles;

import javax.swing.AbstractListModel;
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
		final String levelText=String.format("FL%03d", getLevelForIndex(index));
		
		return levelText;
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
	
	public int getLevelForIndex(int index) {
		return lowestLevel+index*levelStep;
	}
	
	public int getIndexForLevel(int level) {
		if (level<lowestLevel)
			return 0;
		if (level>highestLevel)
			return size-1;
		return (level-lowestLevel+(levelStep+1)/2)/levelStep;
	}
}
