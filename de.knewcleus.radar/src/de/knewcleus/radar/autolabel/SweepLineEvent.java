package de.knewcleus.radar.autolabel;

class SweepLineEvent<T>{
	public enum Type {
		START_SYMBOL,END_SYMBOL;		
	}
	
	protected final Type eventType;
	protected final double position;
	protected final T associatedSymbol;
	
	public SweepLineEvent(Type eventType, double position, T associatedSymbol) {
		this.eventType=eventType;
		this.position=position;
		this.associatedSymbol=associatedSymbol;
	}
	
	public Type getEventType() {
		return eventType;
	}
	
	public double getPosition() {
		return position;
	}
	
	public T getAssociatedSymbol() {
		return associatedSymbol;
	}
	
	@Override
	public String toString() {
		return "["+eventType+"@"+position+":"+associatedSymbol+"]";
	}
}
