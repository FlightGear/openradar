package de.knewcleus.openradar.sector;

import java.net.URI;

public interface FileLayer extends MapLayer{
	public URI getSource();
	public void setSource(URI uri);
	
	public String getLayer();
	public void setLayer(String layer);
	
	public FileLayerKind getType();
	public void setType(FileLayerKind type);
}
