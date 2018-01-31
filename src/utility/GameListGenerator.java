package utility;

import java.io.*;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import objects.Bot;
import objects.Map;
import server.ServerSettings;

public class GameListGenerator 
{
	static final private Logger LOG = LoggerFactory.getLogger(GameListGenerator.class);
	
	public static void GenerateGames(int turn, int strId, int rounds, Vector<Map> maps, Vector<Bot> bots, String TournamentType) {
		GenerateGames(turn, strId, rounds, maps, bots, TournamentType, bots.get(0));
	}
	
	synchronized public static void GenerateGames(int turn, int strId, int rounds, Vector<Map> maps, Vector<Bot> bots, String TournamentType, Bot bot) 
	{
		try 
		{
			FileWriter fstream = new FileWriter(ServerSettings.Instance().GamesListFile, true);
			
			BufferedWriter out = new BufferedWriter(fstream);
			
			if(TournamentType.equalsIgnoreCase("1VsAll"))
			{
				generate1VsAll(turn, strId, rounds, maps, bot, bots, out);
			}
			else
			{
				generateRoundRobin(turn, strId, rounds, maps, bots, out);
			}
			
			out.write("");
			out.flush();
			out.close();
			
			LOG.debug("Generation Complete");
			
		} 
		catch (Exception e) 
		{
			LOG.error(e.getMessage(), e);
		}
	}

	public static void generateRoundRobin(int turn, int strId, int rounds, Vector<Map> maps, Vector<Bot> bots, BufferedWriter out) throws IOException 
	{
		int gameID = strId;
		int roundNum = 0;
		
		for (int i = 0; i < rounds; i++) 
		{
			for(Map m : maps)
			{
				for (int j = 0; j < bots.size(); j++) 
				{
					for (int k = j+1; k < bots.size(); k++) 
					{						
						if (roundNum % 2 == 0) 
						{
							out.write(String.format("%7d %7d %5d %20s %20s %35s", turn, gameID, roundNum, bots.get(j).getName(), bots.get(k).getName(), m.getMapName()) + System.getProperty("line.separator"));
							gameID++;
						} 
						else 
						{
							out.write(String.format("%7d %7d %5d %20s %20s %35s", turn, gameID, roundNum, bots.get(k).getName(), bots.get(j).getName(), m.getMapName()) + System.getProperty("line.separator"));
							gameID++;
						}
					}
				}
				roundNum++;
			}
		}
	}
	public static void generate1VsAll(int turn, int strId, int rounds, Vector<Map> maps, Bot bot, Vector<Bot> bots, BufferedWriter out) throws IOException 
	{
		int gameID = strId;
		int roundNum = 0;
		
		for (int i = 0; i < rounds; i++) 
		{
			for(Map m : maps)
			{
				for (int k = 0; k < bots.size(); k++) 
				{
					if (bot.getName() != bots.get(k).getName()) {
						out.write(String.format("%7d %7d %5d %20s %20s %35s", turn, gameID, roundNum, bot.getName(), bots.get(k).getName(), m.getMapName()) + System.getProperty("line.separator"));
						gameID++;
					}
				}
			}
			roundNum++;
		}
	}
}
