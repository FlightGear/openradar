package de.knewcleus.radar.ui;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import de.knewcleus.radar.ui.vehicles.IVehicle;
import de.knewcleus.radar.ui.vehicles.ISelectionListener;
import de.knewcleus.radar.ui.vehicles.IVehicleUpdateListener;
import de.knewcleus.radar.ui.vehicles.VehicleManager;

public class SelectionManager implements IVehicleUpdateListener {
	private final static Logger logger=Logger.getLogger(SelectionManager.class.getName());
	
	protected final VehicleManager vehicleManager;
	protected final Set<ISelectionListener> selectionListeners=new HashSet<ISelectionListener>();
	protected IWorkableObject selectedObject=null;
	
	public SelectionManager(VehicleManager vehicleManager) {
		this.vehicleManager=vehicleManager;
		vehicleManager.registerVehicleUpdateListener(this);
	}
	
	public void registerSelectionListener(ISelectionListener listener) {
		synchronized(selectionListeners) {
			selectionListeners.add(listener);
		}
	}
	
	public void unregisterSelectionListener(ISelectionListener listener) {
		synchronized(selectionListeners) {
			selectionListeners.remove(listener);
		}
	}

	@Override
	public void vehicleLost(IVehicle lostVehicle) {
		if (lostVehicle==selectedObject) {
			logger.info("Selected vehicle was lost => deselecting");
			deselect();
		}
	}

	@Override
	public void vehicleUpdated(Set<IVehicle> updatedVehicles) {
		// NO-OP
	}
	
	public synchronized void select(IWorkableObject newSelectedObject) {
		if (newSelectedObject==selectedObject)
			return;
		IWorkableObject oldSelection=selectedObject;
		if (selectedObject!=null) {
			logger.fine("Deselecting aircraft "+selectedObject);
			selectedObject.setSelected(false);
		}
		selectedObject=newSelectedObject;
		if (selectedObject!=null) {
			logger.fine("Selecting aircraft "+selectedObject);
			selectedObject.setSelected(true);
		}
		fireSelectionChanged(oldSelection, selectedObject);
	}
	
	public void deselect() {
		select(null);
	}
	
	public synchronized IWorkableObject getSelectedObject() {
		return selectedObject;
	}
	
	private void fireSelectionChanged(IWorkableObject oldSelection, IWorkableObject newSelection) {
		synchronized(selectionListeners) {
			for (ISelectionListener listener: selectionListeners) {
				listener.selectionChanged(oldSelection, newSelection);
			}
		}
	}

}
