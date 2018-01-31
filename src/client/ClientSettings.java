package client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import objects.BWAPISettings;

public class ClientSettings
{	
	static final private Logger LOG = LoggerFactory.getLogger(ClientSettings.class);
	public String			ClientStarcraftDir;
	
	public String			TournamentModuleFilename;
	
	public String			ServerAddress;
	public String 			DefaultBWAPISettingsFileName;
	
	Vector<String>			ClientProperties;
	
	public BWAPISettings	bwapi = new BWAPISettings();

	private static final ClientSettings INSTANCE = new ClientSettings();
	
	private ClientSettings()
	{
		
	}
	
	public static ClientSettings Instance() 
	{
        return INSTANCE;
    }
	
	public void parseSettingsFile(String filename)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(filename));
			JsonObject jo = Json.parse(br).asObject();
			br.close();
			
			ClientStarcraftDir = jo.get("ClientStarcraftDir").asString();
			LOG.debug("StarCraft Dir:   " + ClientStarcraftDir);
			DefaultBWAPISettingsFileName = jo.get("DefaultBWAPISettings").asString();
			TournamentModuleFilename = jo.get("TournamentModule").asString();
			ServerAddress = jo.get("ServerAddress").asString();
			
			
			ClientProperties = new Vector<String>();
			JsonValue propertiesArray = jo.get("ClientProperties");
			if (propertiesArray != null)
			{
				JsonArray properties = propertiesArray.asArray();
				for (JsonValue propObject : properties)
				{
					JsonObject prop = propObject.asObject();
					ClientProperties.add(prop.get("Property").asString());
				}
			}
		}
		catch (Exception e)
		{
			LOG.error("Error parsing settings file, exiting\n", e);
			System.exit(-1);
		}
		
		bwapi.loadFromFile(DefaultBWAPISettingsFileName);
	}
}