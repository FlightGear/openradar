package de.knewcleus.openradar.util;

import java.util.TreeMap;

public class InterpTable {

    private TreeMap<Double, Double> table = new TreeMap<Double, Double>(); 
    
    public void clear() {
        table.clear();
    }

    public void addEntry(double x, double y) {
        table.put(x,y);
    }

    public double interpolate(double x) {
        if(x < table.firstEntry().getValue()) {
            return table.firstEntry().getValue();
        } else if(x > table.lastEntry().getValue()) {
            return table.lastEntry().getValue();
        }
        // interpolate
        for(double curX: table.keySet()) {
            double curY = table.get(curX);
            double nextX = table.higherKey(curX);
            double nextY = table.get(nextX);
            if(curX <= x && nextX >= x) {
                return curY + curX * (nextY-curY)/(nextX/curX);
            }
        }
        return 0; // this should not happen
    }
}
