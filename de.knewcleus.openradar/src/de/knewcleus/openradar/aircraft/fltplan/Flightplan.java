package de.knewcleus.openradar.aircraft.fltplan;

public class Flightplan {
	protected final Object reference;
	protected String callsign;
	protected char flightrules;
	protected char flighttype;
	protected int number;
	protected String aircraftType;
	protected char wakeTurbulenceCategory;
	protected String equipment;
	protected String departureAerodrome;
	protected String eobt;
	protected String route;
	protected String destinationAerodrome;
	protected String ete;
	protected String alternateAerodrome;
	protected String alternateAerodrome2;
	protected String remarks;
	
	public Flightplan(Object identifier) {
		this.reference=identifier;
	}
	
	public Object getReference() {
		return reference;
	}
	
	@Override
	public String toString() {
		String fpl;
		
		fpl=String.format("(FPL-%s-%c%c-",
				callsign,flightrules,flighttype);
		if (number!=1) {
			fpl+=String.format("%02d",number);
		}
		fpl+=String.format("%4s/%c-%s-%4s%4s-%s-%4s%4s",
				aircraftType,wakeTurbulenceCategory,equipment,
				departureAerodrome,eobt,
				route,
				destinationAerodrome,ete);
		if (alternateAerodrome!=null) {
			fpl+=String.format("%4s",alternateAerodrome);
		}
		if (alternateAerodrome2!=null) {
			fpl+=String.format("%4s",alternateAerodrome2);
		}
		if (remarks!=null) {
			fpl+="-"+remarks;
		}
		
		fpl+=")";
		
		return fpl;
	}
	
	public String getCallsign() {
		return callsign;
	}
	public char getFlightrules() {
		return flightrules;
	}
	public char getFlighttype() {
		return flighttype;
	}
	public int getNumber() {
		return number;
	}
	public String getAircraftType() {
		return aircraftType;
	}
	public char getWakeTurbulenceCategory() {
		return wakeTurbulenceCategory;
	}
	public String getEquipment() {
		return equipment;
	}
	public String getDepartureAerodrome() {
		return departureAerodrome;
	}
	public String getEobt() {
		return eobt;
	}
	public String getRoute() {
		return route;
	}
	public String getDestinationAerodrome() {
		return destinationAerodrome;
	}
	public String getEte() {
		return ete;
	}
	public String getAlternateAerodrome() {
		return alternateAerodrome;
	}
	public String getAlternateAerodrome2() {
		return alternateAerodrome2;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setCallsign(String callsign) {
		this.callsign = callsign;
	}
	public void setFlightrules(char flightrules) {
		this.flightrules = flightrules;
	}
	public void setFlighttype(char flighttype) {
		this.flighttype = flighttype;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public void setAircraftType(String aircraftType) {
		this.aircraftType = aircraftType;
	}
	public void setWakeTurbulenceCategory(char wakeTurbulenceCategory) {
		this.wakeTurbulenceCategory = wakeTurbulenceCategory;
	}
	public void setEquipment(String equipment) {
		this.equipment = equipment;
	}
	public void setDepartureAerodrome(String departureAerodrome) {
		this.departureAerodrome = departureAerodrome;
	}
	public void setEobt(String eobt) {
		this.eobt = eobt;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public void setDestinationAerodrome(String destinationAerodrome) {
		this.destinationAerodrome = destinationAerodrome;
	}
	public void setEte(String ete) {
		this.ete = ete;
	}
	public void setAlternateAerodrome(String alternateAerodrome) {
		this.alternateAerodrome = alternateAerodrome;
	}
	public void setAlternateAerodrome2(String alternateAerodrome2) {
		this.alternateAerodrome2 = alternateAerodrome2;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
}
