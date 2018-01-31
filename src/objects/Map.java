package objects;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Map{
	static final private Logger LOG = LoggerFactory.getLogger(Map.class);

	private String mapName;

	private String mapLocation;

	public Map(String mapLocation) 
	{
		this.mapLocation = mapLocation;
		
		File f = new File(mapLocation);
		mapName = f.getName();
	}

	public String getMapName() 
	{
		return mapName;
	}

	public String getMapLocation() 
	{
		return mapLocation;
	}

	public void print() 
	{
		LOG.debug(this.mapName + " -> " + this.mapLocation);
	}
}
