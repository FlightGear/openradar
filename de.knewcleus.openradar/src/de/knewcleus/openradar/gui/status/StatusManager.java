package de.knewcleus.openradar.gui.status;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.JTextField;

import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.view.navdata.INavPointListener;

/**
 * The controller for the status info panel
 * Main task of this class is to update the information displayed as "selection to pointer", direction to and from mousepointer, distance and time needed
 * 
 * @author Wolfram Wagner
 *
 */
public class StatusManager implements INavPointListener {

    private GuiMasterController master;
    private StatusPanel statusPanel = null;
    private CallSignActionListener callSignActionListener = new CallSignActionListener();
    private CallSignKeyListener callSignKeyListener = new CallSignKeyListener();
    
    
    public StatusManager(GuiMasterController guiInteractionManager) {
        this.master = guiInteractionManager;
    }
    
    public void setStatusPanel(StatusPanel statusPanel) {
        this.statusPanel=statusPanel;
    }
    
    public void setSelectedCallSign(String callsign) { 
        this.statusPanel.setAirport(master.getDataRegistry().getAirportCode()+" / "+ (master.getDataRegistry().getAirportName()));
        statusPanel.setSelectedCallSign(callsign);
    }
    


    @Override
    public void navPointAdded(INavPoint point) {
//        if(point instanceof )
        
    }

    public void updateMouseRadarMoved(double milesPerDot, double milesPerHour, Point2D currSelectionPoint, MouseEvent e) {
        double dx = e.getX()-currSelectionPoint.getX();
        double dy = currSelectionPoint.getY()-e.getY();
        double distance = (double)currSelectionPoint.distance(new Point2D.Double(e.getX(),e.getY()));
        Long angle = null;
        if(distance!=0) {
            if(dx>0 && dy>0) angle = Math.round(Math.asin(dx/distance)/2d/Math.PI*360d); 
            if(dx>0 && dy<0) angle = 180-Math.round(Math.asin(dx/distance)/2d/Math.PI*360d);
            if(dx<0 && dy<0) angle = 180+-1*Math.round(Math.asin(dx/distance)/2d/Math.PI*360d);
            if(dx<0 && dy>0) angle = 360+Math.round(Math.asin(dx/distance)/2d/Math.PI*360d);
        }
        ((JComponent)e.getSource()).getVisibleRect();
        Long degreesToPointer = angle!=null ? ( angle<0 ? angle+360 : angle) : null;
        Long degreesToSelection = angle!=null ? (degreesToPointer<180 ? degreesToPointer+180 : degreesToPointer-180) : null;
        Double distanceMiles = distance*milesPerDot;
        Long timeMinutes = milesPerHour>10 ? Math.round(60*distanceMiles/(double)milesPerHour) : null;
        boolean hasChanged = true;
        
        //System.out.println("dx="+ dx + " dy="+dy+" distance="+distance+" angle="+angle);
        
        if(hasChanged)statusPanel.setSelectionToPointer(degreesToPointer,degreesToSelection,distanceMiles, timeMinutes);
    }
 
    public CallSignActionListener getCallSignActionListener() {
        return callSignActionListener;
    }
    
    private class CallSignActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            master.setCurrentATCCallSign(statusPanel.getCurrentCallSign());
            
        }
    }

    public KeyListener getCallSignKeyListener() {
        return callSignKeyListener;
   }

    private class CallSignKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            JTextField tfSource = (JTextField)e.getSource();
            if(tfSource.getText().length()>6) {
                tfSource.setText(tfSource.getText().substring(0,7));
                e.consume();
            }
        }
    }
    
}