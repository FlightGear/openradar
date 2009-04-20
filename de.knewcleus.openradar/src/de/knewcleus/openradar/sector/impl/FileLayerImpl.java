package de.knewcleus.openradar.sector.impl;

import java.net.URI;

import de.knewcleus.openradar.sector.FileLayer;
import de.knewcleus.openradar.sector.FileLayerKind;

public class FileLayerImpl implements FileLayer {
	protected URI source;
	protected String layer;
	protected FileLayerKind type;
	
	@Override
	public URI getSource() {
		return source;
	}
	
	@Override
	public void setSource(URI source) {
		this.source = source;
	}
	
	@Override
	public String getLayer() {
		return layer;
	}
	
	@Override
	public void setLayer(String layer) {
		this.layer = layer;
	}
	
	@Override
	public FileLayerKind getType() {
		return type;
	}
	
	@Override
	public void setType(FileLayerKind type) {
		this.type = type;
	}
}
