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
