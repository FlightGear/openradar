/**
 * Copyright (C) 2015 Wolfram Wagner 
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.fgfs.navaids;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Pavement {

    protected final String rowCode;
    public enum SurfaceType {Asphalt,Concrete,TurfGrass,Dirt,Gravel,DryLakebed,Water,SnowIce,Transparent}
    private final SurfaceType surfaceType;
    protected final Map<String,SurfaceType> SurfaceCodes = new TreeMap<>();
    protected final String description; 
    
    protected final List<PavementNode> nodes = new ArrayList<>();
    
    public Pavement(String[] def) {
        SurfaceCodes.put("1",SurfaceType.Asphalt);
        SurfaceCodes.put("2",SurfaceType.Concrete);
        SurfaceCodes.put("3",SurfaceType.TurfGrass);
        SurfaceCodes.put("4",SurfaceType.Dirt);
        SurfaceCodes.put("5",SurfaceType.Gravel);
        SurfaceCodes.put("12",SurfaceType.DryLakebed);
        SurfaceCodes.put("13",SurfaceType.Water);
        SurfaceCodes.put("14",SurfaceType.SnowIce);
        SurfaceCodes.put("15",SurfaceType.Transparent);
        
        this.rowCode = def[0];
        this.surfaceType = SurfaceCodes.get(def[1]);
        // 2 runway smoothness
        // 3 Orientation of texture grain
        this.description = def[4];
    }

    public SurfaceType getSurfaceType() {
        return surfaceType;
    }
    
    public void addNode(String[] def) {
        PavementNode node = new PavementNode(def);
        nodes.add(node);
    }
    
    public List<PavementNode> getNodes() {
        return nodes;
    }

    public void clearNodes() {
        nodes.clear();
    }

    public void convertLastNodeToEndNode() {
        if(nodes.size()>0) {
            PavementNode last = nodes.get(nodes.size()-1);
            last.isEndNode = true;
        }
        
    }
    
}
