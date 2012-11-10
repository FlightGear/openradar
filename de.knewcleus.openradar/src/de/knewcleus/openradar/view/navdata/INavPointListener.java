package de.knewcleus.openradar.view.navdata;

import de.knewcleus.fgfs.navdata.model.INavPoint;


public interface INavPointListener {
    /**
     * This method will be called in NavPointProvider whenever a NavPoint is being 
     * added to the view.
     * This methods can be used to fork out data about Navaids, Runways etc.
     * 
     * @param point
     */
    public void navPointAdded(INavPoint point);

}
