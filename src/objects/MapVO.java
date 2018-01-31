package objects;

public class MapVO {
	public String mapFile;

	public MapVO(Map map) 
	{
		this.mapFile = map.getMapLocation();
	}
}
