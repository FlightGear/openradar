package de.knewcleus.radar.aircraft;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

public class AircraftState {
	protected final String callsign;
	protected AircraftTaskState taskState=AircraftTaskState.PENDING_IN;
	protected final AssumeAction assumeAction=new AssumeAction(this);
	protected final TransferAction transferAction=new TransferAction(this);
	protected final SkipAction skipAction=new SkipAction(this);
	
	public AircraftState(String callsign) {
		this.callsign=callsign;
	}
	
	public AircraftTaskState getTaskState() {
		return taskState;
	}
	
	public List<Action> getAvailableActions() {
		List<Action> actionList=new ArrayList<Action>();
		
		if (taskState==AircraftTaskState.PENDING) {
			actionList.add(skipAction);
		}
		
		if (taskState==AircraftTaskState.PENDING_IN) {
			actionList.add(assumeAction);
		}
		
		if (taskState==AircraftTaskState.ASSUMED) {
			actionList.add(transferAction);
		}
		
		return actionList;
	}
	
	public void assume() {
		assert(taskState==AircraftTaskState.PENDING_IN);
		taskState=AircraftTaskState.ASSUMED;
	}
	
	public void transfer() {
		assert(taskState==AircraftTaskState.ASSUMED);
		taskState=AircraftTaskState.ASSUMED_OUT;
	}
	
	public void skip() {
		assert(taskState==AircraftTaskState.PENDING);
		taskState=AircraftTaskState.ASSUMED_OUT;
	}
}
