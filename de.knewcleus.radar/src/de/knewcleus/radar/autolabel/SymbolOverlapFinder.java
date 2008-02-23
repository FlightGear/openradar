package de.knewcleus.radar.autolabel;

import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class SymbolOverlapFinder<T extends BoundedSymbol> {
	protected final SweepLineEventPriorityComparator<T> eventPriorityComparator=new SweepLineEventPriorityComparator<T>();
	protected final PriorityQueue<SweepLineEvent<T>> eventQueue=new PriorityQueue<SweepLineEvent<T>>(1,eventPriorityComparator);
	protected final Set<T> intersectedSymbols=new HashSet<T>();

	protected final OverlapModel<? super T> overlapModel;
	
	public SymbolOverlapFinder(OverlapModel<? super T> overlapModel) {
		this.overlapModel=overlapModel;
	}
	
	public void addSymbol(T symbol) {
		SweepLineEvent<T> startEvent=new SweepLineEvent<T>(SweepLineEvent.Type.START_SYMBOL, symbol.getTop(), symbol);
		SweepLineEvent<T> endEvent=new SweepLineEvent<T>(SweepLineEvent.Type.END_SYMBOL, symbol.getBottom(), symbol);
		eventQueue.add(startEvent);
		eventQueue.add(endEvent);
	}
	
	public void addSymbols(Collection<? extends T> symbols) {
		for (T symbol: symbols)
			addSymbol(symbol);
	}
	
	public void run() {
		while (!eventQueue.isEmpty()) {
			/* Process current event, adding or removing a symbol from the list of intersected symbols */
			SweepLineEvent<T> event=eventQueue.remove();
			T symbol=event.getAssociatedSymbol();
			switch (event.getEventType()) {
			case START_SYMBOL:
				if (intersectedSymbols.contains(symbol))
					break;
				/* Check for overlapMap of intersected symbols */
				// FIXME: employ a better data structure here
				for (T s2: intersectedSymbols) {
					if (symbol.getLeft()>s2.getRight() || s2.getLeft()>symbol.getRight())
						continue; // no overlap
					
					overlapModel.registerOverlap(symbol, s2);
				}
				intersectedSymbols.add(symbol);
				break;
			case END_SYMBOL:
				intersectedSymbols.remove(symbol);
				break;
			}
		}
	}
}
