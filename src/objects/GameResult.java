package objects;

import java.util.Vector;

import server.ServerSettings;

public class GameResult implements Comparable<Object>
{
	public int turn					= -1;
	public int gameID				= -1;
	public int roundID				= -1;
	
	public String hostName			= "Error";
	public String awayName			= "Error";
	public String mapName			= "Error";
	
	public String firstReport		= "Error";
	
	public String winName			= "Error";
	public String crashName			= "";
	public String timeOutName		= "";
	
	public boolean hostWon			= false;
	public boolean hostCrash		= false;
	public boolean awayCrash		= false;
	
	public Vector<Integer> hostTimers   = new Vector<Integer>();
	public Vector<Integer> awayTimers   = new Vector<Integer>();

	public boolean hourTimeout		= false;
	
	public int hostScore			= 0;
	public int awayScore			= 0;
	public int finalFrame			= -2;
	public int hostTime				= 0;
	public int awayTime				= 0;
	
	public String hostAddress		= "unknown";
	public String awayAddress		= "unknown";
	public String startDate			= "unknown";
	public String finishDate		= "unknown"; 

	public GameResult() {}

	public GameResult (String data) 
	{
		setResult(data);

		/*int numTimers = ServerSettings.Instance().tmSettings.TimeoutLimits.size();
	
		for (int i=0; i<numTimers; ++i)
		{
			hostTimers.add(0);
			awayTimers.add(0);
		}*/
	}
	
	public void setResult (String dataLine)
	{
		String[] data = dataLine.trim().split(" +");
		
		turn 				= Integer.parseInt(data[0]);
		gameID 				= Integer.parseInt(data[1]);
		roundID 			= Integer.parseInt(data[2]);
		
		hostName			= data[3];
		awayName			= data[4];
		mapName				= data[5];
		
		if (data[6].equals("true"))
		{
			hostWon = true;
		}
		
		if (data[7].equals("true"))
		{
			hostCrash = true;
		}
		
		if (data[8].equals("true"))
		{
			awayCrash = true;
		}
		
		hourTimeout			= Boolean.getBoolean(data[9]);
		
		hostScore			= Integer.parseInt(data[10]) != 0 ? Integer.parseInt(data[10]) : hostScore;
		awayScore			= Integer.parseInt(data[11]) != 0 ? Integer.parseInt(data[11]) : awayScore;
		
		finalFrame			= Integer.parseInt(data[12]) > finalFrame ? Integer.parseInt(data[12]) : finalFrame;
		
		hostTime			= Integer.parseInt(data[13]) != 0 ? Integer.parseInt(data[13]) : hostTime;
		awayTime			= Integer.parseInt(data[14]) != 0 ? Integer.parseInt(data[14]) : awayTime;
		
		int numTimers = ServerSettings.Instance().tmSettings.TimeoutLimits.size();
		for (int i=0; i<numTimers; ++i)
		{
			if(hostTimers.size()==numTimers)//this is the second time here
			{
				if(hostTimers.get(i)==0)
				{
					try
					{
						hostTimers.set(i, Integer.parseInt(data[15 + i]));
					}
					catch(java.lang.ArrayIndexOutOfBoundsException ex)
					{
						hostTimers.set(i, -1);
					}
				}
				if(awayTimers.get(i)==0)
				{
					try
					{
						awayTimers.set(i, Integer.parseInt(data[15 + numTimers + i]));
					}
					catch(java.lang.ArrayIndexOutOfBoundsException ex)
					{
						awayTimers.set(i, -1);
					}
				}
			}
			else
			{
				try
				{
					hostTimers.add(Integer.parseInt(data[15 + i]));
				}
				catch(java.lang.ArrayIndexOutOfBoundsException ex)
				{
					hostTimers.add(-1);
				}
				try
				{
					awayTimers.add(Integer.parseInt(data[15 + numTimers + i]));
				}
				catch(java.lang.ArrayIndexOutOfBoundsException ex)
				{
					awayTimers.add(-1);
				}
			}
		}
		
		// if there's an address field
		if (data.length > 15 + numTimers*2)
		{
			hostAddress = data[15 + numTimers*2];
			awayAddress = data[15 + numTimers*2 + 1];
			
			// if there's a date field
			if (data.length > 15 + numTimers*2 + 2)
			{			
				startDate = data[15 + numTimers*2 + 2];
				
				// record the finish date only from the second person to report
				if (!firstReport.equalsIgnoreCase("Error"))
				{
					finishDate = data[15 + numTimers*2 + 3];
				}
			}
		}
		
		if (finalFrame > 0 && hostCrash && !awayCrash)
		{
			crashName = hostName;
			hostWon = false;
		}
		else if (finalFrame > 0 && awayCrash && !hostCrash)
		{
			crashName = awayName;
			hostWon = true;
		}
			
		int tempFinalFrame 	= Integer.parseInt(data[12]);
		
		// if this is the first bot to report
		if (firstReport.equals("Error"))
		{
			// if host time is zero then this is the away bot reporting
			if (hostTime == 0)
			{
				firstReport = awayName;
				// if we have no final frame then the bot must have crashed
				if (tempFinalFrame == -1)
				{
					awayCrash = true;
					hostCrash = false;
					crashName = awayName;
					hostWon = true;
				}
			}
			// this is the host bot reporting
			else
			{
				firstReport = hostName;
				// if we have no final frame then the bot must have crashed
				if (tempFinalFrame == -1)
				{
					hostCrash = true;
					awayCrash = false;
					crashName = hostName;
					hostWon = false;
				}
			}
		}
		// otherwise this is the 2nd report
		// this checks to see if the 2nd report bot crashed
		else if (tempFinalFrame == -1)
		{
			// if the first bot to report was host
			if (firstReport.equals(hostName))
			{
				hostCrash = false;
				awayCrash = true;
				crashName = awayName;
				hostWon = true;
			}
			// otherwise the first bot to report was the away bot
			else
			{
				awayCrash = false;
				hostCrash = true;
				crashName = hostName;
				hostWon = false;
			}
		}
		
		// check bot time-outs
		for (int i=0; i<numTimers; ++i)
		{
			// check if the host timed out
			if (hostTimers.get(i) >= ServerSettings.Instance().tmSettings.TimeoutBounds.get(i))
			{
				timeOutName = hostName;
				hostWon = false;
				break;
			}
			
			// check if the away bot timed out
			if (awayTimers.get(i) >= ServerSettings.Instance().tmSettings.TimeoutBounds.get(i))
			{
				timeOutName = awayName;
				hostWon = true;
				break;
			}
		}
		
		// if the bots reached the hour time limit
		if (finalFrame >= ServerSettings.Instance().tmSettings.GameFrameLimit)
		{
			hourTimeout = true;
			
			// the winner is the bot with the highest score
			if (hostScore >= awayScore)
			{
				hostWon = true;
			}
			else
			{
				hostWon = false;
			}
		}
		
		// 봇이 오류로 실행되지 않은 경우
		if (awayScore > 0 && awayScore < 1000) {
			awayCrash = true;
			hostWon = true;
			crashName = awayName;
		}
		
		if (hostScore > 0 && hostScore < 1000) {
			hostCrash = true;
			hostWon = false;
			crashName = hostName;
		}
		

		//if no one timed out or crashed, we have someone kicked out
	/*	if(finalFrame > 0 && !hostCrash && !awayCrash && prevHostWon!=hostWon)
		{
			//let's pronounce as winner the one who played the shortest game (and kicked the other out)
			if(prevFinalFrame > finalFrame)
			{
			}
			else
			{
			}
		}*/
		winName = hostWon ? hostName : awayName;
	}
	
	public String toString()
	{
		String s = String.format("%7d %7d %5d %15s %15s %15s %8d %15s %15s %25s %6b %6b%6b %8d %8d %10d %10d \n",
				this.turn, this.gameID, this.roundID, this.hostName, 
				this.awayName, this.winName, this.finalFrame, this.crashName, this.timeOutName, this.mapName, 
				this.hostCrash, this.awayCrash, 
				this.hourTimeout, this.hostScore, this.awayScore, 
				this.hostTime, this.awayTime);
				
		for (int i=0; i<hostTimers.size(); ++i)
		{
			String t = String.format(" %5d", hostTimers.get(i));
			s += t;
		}
		
		for (int i=0; i<awayTimers.size(); ++i)
		{
			String t = String.format(" %5d", awayTimers.get(i));
			s += t;
		}
		
		return s;
	}
	
	public String getResultString() 
	{
		String winnerName = hostWon ? hostName : awayName;
		String loserName = hostWon ? awayName : hostName;
		
		String s = String
				.format("%7d %7d %5d %15s %15s %15s %15s %25s %8d %8d %8d",
						turn,
						gameID, 
						roundID,
						winnerName,
						loserName,
						crashName.length() == 0 ? "-" : crashName,
						timeOutName.length() == 0 ? "-" : timeOutName,
						mapName,
						finalFrame, 
						hostWon ? hostScore : awayScore, 
						hostWon ? awayScore : hostScore);

		for (int i=0; i<hostTimers.size(); ++i)
		{
			String t = String.format(" %7d", hostTimers.get(i));
			s += t;
		}
		
		for (int i=0; i<awayTimers.size(); ++i)
		{
			String t = String.format(" %7d", awayTimers.get(i));
			s += t;
		}
		
		s += "  " + (hostWon ? hostAddress : awayAddress) + "  " + (hostWon ? awayAddress : hostAddress);
		s += "  " + startDate + "  " + finishDate;
		
		return s;
	}
	
	public String toJSONString() {
		return String
				.format("{\"turn\": %d, \"game_id\": %d, \"my_bot_nm\" : \"%s\", \"enemy_bot_nm\" :  \"%s\", "
						+ "\"rslt_cd\" : \"%s\", \"map_cd\" : \"%s\"}",
						turn, gameID, hostName, awayName,
						this.getRsltCd(), this.getMapCd());
	}

	public String toBotJSONString() {
		Bot hostBot = ServerSettings.Instance().getBotFromBotName(hostName);

		return String
				.format("{\"bot_name\": \"%s\", \"api_version\": \"%s\", \"type_cd\" : \"%s\", \"race_cd\" :  \"%s\"}",
						hostName, hostBot.getBWAPIVersion(), getTypeCd(hostBot.getType()), getRaceCd(hostBot.getRace()));
	}
	
	private String getMapCd() {
		switch (mapName) {
		case "(2)Benzene.scx":
			return "01";
		case "(2)Destination.scx":
			return "02";
		case "(2)HeartbreakRidge.scx":
			return "03";
		case "(3)Aztec.scx":
			return "04";
		case "(3)TauCross.scx":
			return "05";
		case "(4)Andromeda.scx":
			return "06";
		case "(4)CircuitBreaker.scx":
			return "07";
		case "(4)EmpireoftheSun.scm":
			return "08";
		case "(4)Fortress.scx":
			return "09";
		case "(4)Python.scx":
			return "10";
		default:
			return "ZZ";
		}
	}

	private String getRsltCd() {
		String rsltCd;
		// 승
		if (hostWon) {
			// 03 : 타임아웃 무승부인데 점수로 승리
			if (hourTimeout) {
				rsltCd = "03";
			}
			// 02 : 상대방의 오류로 승리
			else if (awayCrash) {
				rsltCd = "02";
			}
			// 01 : 정상 승리
			else {
				rsltCd = "01";
			}
		}
		// 패
		else {
			// 06 : 타임아웃 무승부인데 점수로 패배
			if (hourTimeout) {
				rsltCd = "06";
			}
			// 05 : 자신의 오류로 패배
			else if (hostCrash) {
				rsltCd = "05";
			}
			// 04 : 정상 패배
			else {
				rsltCd = "04";
			}
		}
		return rsltCd;
	}
	
	private String getTypeCd(String type) {
		return "proxy".equals(type) ? "01" : "02";
	}
	
	private String getRaceCd(String race) {
		switch (race) {
		case "Random" :
			return "00";
		case "Terran" :
			return "01";
		case "Protoss" :
			return "02";
		case "Zerg" :
			return "03";
		}
		return "04";
	}

	public int compareTo(Object other)
	{
		return this.gameID - ((GameResult)other).gameID;
	}

	public String getHostReplayName()
	{
		String replayName = "";
		
		replayName += hostName.toUpperCase() + "\\";
		
		String idString = "" + gameID;
		while(idString.length() < 5) { idString = "0" + idString; }
		
		replayName += idString + "-";
		replayName += hostName.substring(0, Math.min(hostName.length(), 4)).toUpperCase();
		replayName += "_";
		replayName += awayName.substring(0, Math.min(awayName.length(), 4)).toUpperCase();
		replayName += ".REP";
		
		return replayName;
	}
	
	public String getAwayReplayName()
	{
		String replayName = "";
		
		replayName += awayName.toUpperCase() + "\\";
		String idString = "" + gameID;
		while(idString.length() < 5) { idString = "0" + idString; }
		
		replayName += idString + "-";
		replayName += awayName.substring(0, Math.min(awayName.length(), 4)).toUpperCase();
		replayName += "_";
		replayName += hostName.substring(0, Math.min(hostName.length(), 4)).toUpperCase();
		replayName += ".REP";
		
		return replayName;
	}
}
