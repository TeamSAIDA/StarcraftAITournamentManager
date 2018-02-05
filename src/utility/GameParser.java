package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import objects.BattleApply;
import objects.Bot;
import objects.Game;
import objects.GameStorage;
import objects.Map;
import server.CallbackTaskAbstract;
import server.ServerSettings;

public class GameParser 
{
	final static Logger LOG = LoggerFactory.getLogger(GameParser.class);
	
	private static GameStorage games;
	private static Vector<Bot> bots;
	private static Vector<Map> maps;
	private static int gameId = 0;
	private static int turn = 0;

	private GameParser(){};
	
	public static GameStorage getGames()
	{
		return games;
	}

	public static GameStorage getGames(Vector<Bot> p_bots, Vector<Map> p_maps)
	{
		try
		{
			maps = p_maps;
			bots = p_bots;
			parse();
		}
		catch (Exception e)
		{
			LOG.error("Couldn't load games file list\n", e);
			System.exit(-1);
		}
		
		return games;
	}

	private static void parse() throws NumberFormatException, Exception 
	{
		games = new GameStorage();
		try 
		{
		
			if (!new File(ServerSettings.Instance().GamesListFile).exists())
			{
				return;
			}
		
			BufferedReader br = new BufferedReader(new FileReader(ServerSettings.Instance().GamesListFile));
			parseGames(br);
			br.close();
		} 
		catch (FileNotFoundException e) 
		{
			LOG.error("Could not read settings file", e);
		} 
		catch (IOException e) 
		{
			LOG.error("IOException while reading settings.ini", e);
		}
	}
	
	private static void parseGames(BufferedReader br) throws NumberFormatException, Exception 
	{
		String line;
		
		while ((line = br.readLine()) != null) 
		{
			line = line.trim();
			if(!line.startsWith("#") && line.length() > 0)
			{
				String[] args = line.split("\\s+");
				int gId = Integer.parseInt(args[1]);
				if (gId < gameId) {
					continue;
				}
				turn = Integer.parseInt(args[0]);
				gameId = gId;
				Game newGame = new Game(turn++, gameId++, Integer.parseInt(args[2]), findBot(args[3]), findBot(args[4]), findMap(args[5])); 
				games.addGame(newGame);
				BattleApply ba = new BattleApply(newGame);
				CallbackTaskAbstract.callbackData.add(newGame.getTurn(), newGame.getGameID(), ba);
			}
		}
	}
	
	private static Bot findBot(String name) throws Exception
	{
		for(Bot b : bots)
		{
			if(b.getName().equals(name))
			{
				return b;
			}
		}
		
		throw new Exception("Bot not found!!\n Was looking for \"" + name + "\"");
	}
	
	private static Map findMap(String name) throws Exception
	{
		for (Map m : maps)
		{
			if (m.getMapName().equals(name))
			{
				return m;
			}
		}
		
		throw new Exception("Map not found!!\n Was looking for\"" + name + "\"");
	}
	
	public static void addBot(HashMap<String, Object> reqBot) {
		try
		{
			String botName = (String)reqBot.get("bot_name");
			String botRace = reqBot.get("botRace") == null ? "Terran" : (String)reqBot.get("botRace");
			String type = reqBot.get("type") == null ? "proxy" : (String)reqBot.get("type");
			String BWAPIVer = reqBot.get("BWAPIVer") == null ? "BWAPI_412" : (String)reqBot.get("BWAPIVer");
			int round = reqBot.get("round") == null ? 1 : (int)reqBot.get("round");
			ServerSettings.Instance().addBot(botName, botRace, type, BWAPIVer);
			GameListGenerator.GenerateGames(turn, gameId, round, maps, bots, "1VsAll", ServerSettings.Instance().getBotFromBotName(botName));

			BufferedReader br = new BufferedReader(new FileReader(ServerSettings.Instance().GamesListFile));
			parseGames(br);
			br.close();
		}
		catch (Exception e)
		{
			LOG.error("Couldn't add game list", e);
		}
	}
}
