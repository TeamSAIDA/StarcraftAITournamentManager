package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import objects.BWAPISettings;
import objects.Bot;
import objects.BotVO;
import objects.ExcludeFromResultsVO;
import objects.Map;
import objects.MapVO;
import objects.TournamentModuleSettingsMessage;
import objects.TournamentModuleSettingsVO;
import utility.FileUtils;
import utility.JSONUtil;

public class ServerSettings
{
	static final private Logger LOG = LoggerFactory.getLogger(ServerSettings.class);
	public Vector<Bot> 		BotVector 			= new Vector<Bot>();
	public Vector<Map> 		MapVector 			= new Vector<Map>();
	
	//The following 4 paths are hard coded here and not options in the settings file
	public String			ServerDir			= "./";
	public String			ServerReplayDir		= "replays/";
	public String			ServerRequiredDir	= "required/";
	public String			ServerBotDir		= "bots/";
	public String	ServerSettingFileNm	= null;
	
	public String			SendUrl				= "/";
	public String			SendIP				= "/";
	public int				SendPort			= -1;
	public int				ServerPort			= -1;
	public int				RecvPort			= -1;
	public String			GamesListFile		= null;
	public String			ResultsFile			= null;
	public String 			ClearResults	 	= "ask";
	public String			ResumeTournament	= "ask";
	public boolean			DetailedResults		= false;
	public boolean			StartGamesSimul		= false;
	public String			TournamentType		= "AllVsAll";
	public boolean			EnableBotFileIO		= true;
	public Vector<String>	ExcludeFromResults	= new Vector<String>();
	
	public BWAPISettings	bwapi = new BWAPISettings();
	
	public TournamentModuleSettingsMessage tmSettings = new TournamentModuleSettingsMessage();

	private static final ServerSettings INSTANCE = new ServerSettings();
	
	private ServerSettings()
	{
		
	}
	
	public static ServerSettings Instance() 
	{
        return INSTANCE;
    }
	
	public Bot getBotFromBotName(String botname)
	{
		for (Bot b : BotVector)
		{
			if (b.getName().equalsIgnoreCase(botname))
			{
				return b;
			}
		}
		
		return null;
	}
		
	public void parseSettingsFile(String filename)
	{	
		try
		{
			this.ServerSettingFileNm = filename;
			BufferedReader br = new BufferedReader(new FileReader(filename));
			JsonObject jo = Json.parse(br).asObject();
			br.close();
			
			JsonArray bots = jo.get("bots").asArray();
			for (JsonValue botValue : bots)
			{
				JsonObject bot = botValue.asObject();
				JsonValue reqArray = bot.get("ClientRequirements");
				Vector<String> requirements = new Vector<String>();
				if (reqArray != null)
				{
					JsonArray reqs = reqArray.asArray();
					if (reqs.size() > 0)
					{
						for (JsonValue reqObject : reqs)
						{
							JsonObject req = reqObject.asObject();
							requirements.add(req.get("Property").asString());
						}
						
					}
				}
				BotVector.add(new Bot(bot.get("BotName").asString(),bot.get("Race").asString(), bot.get("BotType").asString(), bot.get("BWAPIVersion").asString(), requirements));
			}
			
			JsonArray maps = jo.get("maps").asArray();
			for (JsonValue mapValue : maps)
			{
				JsonObject map = mapValue.asObject();
				MapVector.add(new Map(map.get("mapFile").asString()));
			}
			
			GamesListFile = jo.get("gamesListFile").asString();
			ResultsFile = jo.get("resultsFile").asString();
			DetailedResults = jo.get("detailedResults").asBoolean(); 
			SendUrl = jo.get("sendUrl").asString();
			SendIP = jo.get("sendIP").asString();
			SendPort = jo.get("sendPort").asInt();
			ServerPort = jo.get("serverPort").asInt();
			RecvPort = jo.get("recvPort").asInt();
			ClearResults = jo.get("clearResults").asString();
			ResumeTournament = jo.get("resumeTournament").asString();
			StartGamesSimul = jo.get("startGamesSimultaneously").asBoolean();
			TournamentType = jo.get("tournamentType").asString();
			EnableBotFileIO = jo.get("enableBotFileIO").asBoolean();
			
			JsonArray excludedBots = jo.get("excludeFromResults").asArray();
			for (JsonValue excludedBot : excludedBots)
			{
				JsonObject exclude = excludedBot.asObject();
				ExcludeFromResults.add(exclude.get("BotName").asString());
			}
					
			JsonObject tmSettingsJO = jo.get("tournamentModuleSettings").asObject();
			tmSettings.LocalSpeed = tmSettingsJO.get("localSpeed").asInt();
			tmSettings.FrameSkip = tmSettingsJO.get("frameSkip").asInt();
			tmSettings.GameFrameLimit = tmSettingsJO.get("gameFrameLimit").asInt();
			tmSettings.DrawBotNames = tmSettingsJO.get("drawBotNames").asBoolean() ? "true" : "false";
			tmSettings.DrawTournamentInfo = tmSettingsJO.get("drawTournamentInfo").asBoolean() ? "true" : "false";
			tmSettings.DrawUnitInfo = tmSettingsJO.get("drawUnitInfo").asBoolean() ? "true" : "false";
			
			JsonArray limits = tmSettingsJO.get("timeoutLimits").asArray();
			for (JsonValue limitValue : limits)
			{
				JsonObject limit = limitValue.asObject();
				tmSettings.TimeoutLimits.add(limit.get("timeInMS").asInt());
				tmSettings.TimeoutBounds.add(limit.get("frameCount").asInt());
			}
			
		}
		catch (Exception e)
		{
			LOG.error("Error parsing settings file, exiting\n", e);
			System.exit(-1);
		}
		
		if (!checkValidSettings())
		{
			LOG.error("\n\nError in server set-up, please check documentation: http://www.cs.mun.ca/~dchurchill/starcraftaicomp/tm.shtml#ss");
			System.exit(0);
		}
	}
		
	private boolean checkValidSettings()
	{
		boolean valid = true;
		
		// check if all setting variables are valid
		if (MapVector.size() <= 0)		{ LOG.error("ServerSettings: Must have at least 1 map in settings file"); valid = false; }
		if (ServerDir == null)			{ LOG.error("ServerSettings: ServerDir not specified in settings file"); valid = false; }
		if (GamesListFile == null)		{ LOG.error("ServerSettings: GamesListFile not specified in settings file"); valid = false; }
		if (ResultsFile == null)		{ LOG.error("ServerSettings: ResultsFile must be specified in settings file"); valid = false; }
		if (ServerPort == -1)			{ LOG.error("ServerSettings: ServerPort must be specified as an integer in settings file"); valid = false; }
		if (RecvPort == -1)				{ LOG.error("ServerSettings: RecvPort must be specified as an integer in settings file"); valid = false; }
		
		if (!ClearResults.equalsIgnoreCase("yes") && !ClearResults.equalsIgnoreCase("no") && !ClearResults.equalsIgnoreCase("ask"))
		{
			LOG.error("ServerSettings: ClearResultsFile invalid option: " + ClearResults);
			valid = false;
		}
		
		if (!ResumeTournament.equalsIgnoreCase("yes") && !ResumeTournament.equalsIgnoreCase("no") && !ResumeTournament.equalsIgnoreCase("ask"))
		{
			LOG.error("ServerSettings: ResumeTournament invalid option: " + ResumeTournament);
			valid = false;
		}
		
		// check if all required files are present
		if (!FileUtils.CreateDirectory(ServerReplayDir))
		{
			LOG.error("ServerSettings: Replay Dir (" + ServerReplayDir + ") does not exist and could not be created");
			valid = false;
		}
		if (!FileUtils.CreateDirectory(ServerBotDir)) 		{ LOG.error("ServerSettings: Bot Dir (" + ServerBotDir + ") does not exist"); valid = false; }
		if (!FileUtils.CreateDirectory(ServerRequiredDir)) 	{ LOG.error("ServerSettings: Required Files Dir (" + ServerRequiredDir + ") does not exist"); valid = false; }
		
		// Check if all the maps exist
		/*for (Map m : MapVector)
		{
			String mapLocation = ServerRequiredDir + "Starcraft/" + m.getMapLocation();
			if (!new File(mapLocation).exists())
			{
				System.err.println("Map Error: " + m.getMapName() + " file does not exist at specified location: " + mapLocation); valid = false;
			}
		}*/
		
		return valid && checkBotValidation();
	}
	
	private boolean checkBotValidation() {
		boolean valid = true;
		// check all bot directories
		for (Bot b : BotVector)
		{
			String botDir 		= ServerBotDir + b.getName() + "/";
			String botAIDir 	= botDir + "AI/";
			String botDLLFile	= botAIDir + b.getName() + ".dll";
			String botWriteDir 	= botDir + "write/";
			String botReadDir 	= botDir + "read/";
			String proxyScript	= botAIDir + "run_proxy.bat";
			String botBWAPIReq  = ServerRequiredDir + "Required_" + b.getBWAPIVersion() + ".zip";
			
			// Check if all the bot files exist
			if (!FileUtils.CreateDirectory(botDir)) 		{ LOG.error("Bot Error: " + b.getName() + " bot directory " + botDir + " does not exist."); valid = false; }
			if (!FileUtils.CreateDirectory(botAIDir)) 		{ LOG.error("Bot Error: " + b.getName() + " bot AI directory " + botAIDir + " does not exist."); valid = false; }
			if (!b.isProxyBot() && !new File(botDLLFile).exists()) 	{ LOG.error("Bot Error: " + b.getName() + " bot dll file " + botDLLFile + " does not exist."); valid = false; }
			if (!FileUtils.CreateDirectory(botWriteDir)) 	{ LOG.error("Bot Error: " + b.getName() + " bot write directory " + botWriteDir + " does not exist."); valid = false; }
			if (!FileUtils.CreateDirectory(botReadDir)) 	{ LOG.error("Bot Error: " + b.getName() + " bot read directory " + botReadDir + " does not exist."); valid = false; }
			if (!new File(botBWAPIReq).exists()) 	{ LOG.error("Bot Error: " + b.getName() + " bot required BWAPI files " + botBWAPIReq + " does not exist."); valid = false; }

			// Check if the bot is proxy and the proxy bot exists
			if (b.isProxyBot() && !new File(proxyScript).exists()) 
			{
				try (BufferedWriter bw = new BufferedWriter(new FileWriter(botAIDir + "run_proxy.bat"))) {
					bw.write("if \"%STARCRAFT_HOME%\" == \"\" (");
					bw.newLine();
					bw.write("	setx STARCRAFT_HOME c:\\Starcraft");
					bw.newLine();
					bw.write(")");
					bw.write("cd %STARCRAFT_HOME%");
					bw.newLine();
					bw.write("%STARCRAFT_HOME%\\bwapi-data\\AI\\" + b.getName() + ".exe");
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
				
				LOG.info("Bot : " + b.getName() + " listed as proxy but " + proxyScript + " does not exist. file created!"); 
			}
		}

		return valid;
	}

	public void addBot(String name, String race, String type, String bwapiVersion) {
		if (getBotFromBotName(name) == null) {
			Bot bot = new Bot(name, race, type, bwapiVersion);
			BotVector.add(bot);
			checkBotValidation();
			writeSettingsFile();
			
		}
	}
	
	private void writeSettingsFile() {
		try {
			Vector<BotVO> BotVOVector = new Vector<BotVO>();
			for (Bot b : BotVector) {
				BotVOVector.add(new BotVO(b));
			}

			Vector<MapVO> MapVOVector = new Vector<MapVO>();
			for (Map m : MapVector) {
				MapVOVector.add(new MapVO(m));
			}
			
			Vector<ExcludeFromResultsVO> ExcludeVOVector = new Vector<ExcludeFromResultsVO>();
			for (String s : ExcludeFromResults) {
				ExcludeVOVector.add(new ExcludeFromResultsVO(s));
			}
			
			TournamentModuleSettingsVO tms = new TournamentModuleSettingsVO(tmSettings);

			LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

			map.put("bots", BotVOVector);
			map.put("maps", MapVOVector);
			map.put("gamesListFile", GamesListFile);
			map.put("resultsFile", ResultsFile);
			map.put("detailedResults", DetailedResults);
			map.put("sendUrl", SendUrl);
			map.put("sendIP", SendIP);
			map.put("sendPort", SendPort);
			map.put("serverPort", ServerPort);
			map.put("recvPort", RecvPort);
			map.put("clearResults", ClearResults);
			map.put("resumeTournament", ResumeTournament);
			map.put("startGamesSimultaneously", StartGamesSimul);
			map.put("tournamentType", TournamentType);
			map.put("enableBotFileIO", EnableBotFileIO);
			map.put("excludeFromResults", ExcludeVOVector);
			map.put("tournamentModuleSettings", tms);

			BufferedWriter bw = new BufferedWriter(new FileWriter(this.ServerSettingFileNm));

			bw.write(JSONUtil.writeValue(map, true));

			bw.close();
		} catch (Exception e) {
			LOG.error("Error parsing settings file, exiting\n", e);
			System.exit(-1);
		}
	}
}