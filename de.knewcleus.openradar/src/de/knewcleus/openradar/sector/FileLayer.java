package de.knewcleus.openradar.sector;

import java.net.URL;

public interface FileLayer extends MapLayer{
	public URL getSource();
	public void setSource(URL uri);
	
	public String getLayer();
	public void setLayer(String layer);
	
	public FileLayerKind getType();
	public void setType(FileLayerKind type);
}
