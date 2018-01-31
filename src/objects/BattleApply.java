package objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.ServerSettings;

public class BattleApply {
	static final private Logger LOG = LoggerFactory.getLogger(BattleApply.class);
	private static final int MIN_SCORE = 900;
	
	/**
	 * data
	 */
	private String hostBotName;		// unique
	private String awayBotName;
	private String hostBotRace;		// Terran | Protoss | Zerg | Random
	private String awayBotRace;
	private String mapName;
	private String hostType;		// dll
	private String awayType;
	private String hostBWAPIVer;	// BWAPI_412
	private String awayBWAPIVer;
	private int round;
	private int turn;
	
	/**
	 * result host 기준
	 */
	private String hostReplayPath;
	private int hostReplaySize;
	private String awayReplayPath;
	private int awayReplaySize;
	private String rsltKndCd;
	private String rsltKndCd2;
	
	/**
	 * state
	 */
	private int saveCnt = 0;
	private long time = System.currentTimeMillis();
	
	public BattleApply(Game game) {
		this.turn = game.getTurn();
		this.round = game.getRound();
		Bot homeBot = game.getHomebot();
		Bot awayBot = game.getAwaybot();
		
		this.hostBotName = homeBot.getName();
		this.awayBotName = awayBot.getName();
		this.hostBotRace = homeBot.getRace();
		this.awayBotRace = awayBot.getRace();
		this.mapName = game.getMap().getMapName();
		this.hostType = homeBot.getType();
		this.awayType = awayBot.getType();
		this.hostBWAPIVer = homeBot.getBWAPIVersion();
		this.awayBWAPIVer = awayBot.getBWAPIVersion();
	}

	public String getHostBotName() {
		return hostBotName;
	}

	public void setHostBotName(String hostBotName) {
		this.hostBotName = hostBotName;
	}

	public String getAwayBotName() {
		return awayBotName;
	}

	public void setAwayBotName(String awayBotName) {
		this.awayBotName = awayBotName;
	}

	public String getHostBotRace() {
		return this.hostBotRace;
	}

	public void setHostBotRace(String hostBotRace) {
		this.hostBotRace = convertCode2Race(hostBotRace);
	}

	public String getAwayBotRace() {
		return this.awayBotRace;
	}

	public void setAwayBotRace(String awayBotRace) {
		this.awayBotRace = convertCode2Race(awayBotRace);
	}

	static private String convertCode2Race(String code) {
		switch (code) {
		case "01" :
			return "Random";
		case "02" :
			return "Zerg";
		case "03" :
			return "Terran";
		case "04" :
			return "Protoss";
		}
		return code;
	}
	
	static public String convertRace2Code(String race) {
		switch (race) {
		case "Random" :
			return "01";
		case "Zerg" :
			return "02";
		case "Terran" :
			return "03";
		case "Protoss" :
			return "04";
		}
		return race;
	}

	public String getMapName() {
		return this.mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = convertSimMapNm2MapNm(mapName);
	}
	

	static private String convertSimMapNm2MapNm(String simMapNm) {
		switch (simMapNm) {
		case "Hunter" :
			return "(8)Hunters_KeSPA1.3.scx";
		case "Lost Temple" :
			return "Lost_Temple_2.4_iCCup.scx";
		case "투혼" :
			return "Fighting_Spirit_1.3.scx";
		}
		return simMapNm;
	}
	
	static public String convertMapNm2SimMapNm(String mapName) {
		switch (mapName) {
		case "(8)Hunters_KeSPA1.3.scx" :
			return "Hunter";
		case "Lost_Temple_2.4_iCCup.scx" :
			return "Lost Temple";
		case "Fighting_Spirit_1.3.scx" :
			return "투혼";
		}
		return mapName;
	}

	public String getHostType() {
		return hostType;
	}

	public void setHostType(String hostType) {
		this.hostType = hostType;
	}
	
	public String getAwayType() {
		return awayType;
	}
	
	public void setAwayType(String awayType) {
		this.awayType = awayType;
	}

	public String getHostBWAPIVer() {
		return hostBWAPIVer;
	}

	public void setHostBWAPIVer(String hostBWAPIVer) {
		this.hostBWAPIVer = hostBWAPIVer;
	}

	public String getAwayBWAPIVer() {
		return awayBWAPIVer;
	}

	public void setAwayBWAPIVer(String awayBWAPIVer) {
		this.awayBWAPIVer = awayBWAPIVer;
	}
	
	public int getTurn() {
		return turn;
	}
	
	public void setTurn(int turn) {
		this.turn = turn;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public String getHostReplayPath() {
		return hostReplayPath;
	}

	public void setHostReplayPath(String hostReplayPath) {
		this.hostReplayPath = hostReplayPath;
	}

	public int getHostReplaySize() {
		return hostReplaySize;
	}

	public void setHostReplaySize(int hostReplaySize) {
		this.hostReplaySize = hostReplaySize;
	}

	public String getAwayReplayPath() {
		return awayReplayPath;
	}

	public void setAwayReplayPath(String awayReplayPath) {
		this.awayReplayPath = awayReplayPath;
	}

	public int getAwayReplaySize() {
		return awayReplaySize;
	}

	public void setAwayReplaySize(int awayReplaySize) {
		this.awayReplaySize = awayReplaySize;
	}

	public String getRsltKndCd() {
		return rsltKndCd;
	}

	public void setRsltKndCd(String rsltKndCd) {
		this.rsltKndCd = rsltKndCd;
	}
	
	public String getRsltKndCd2() {
		return rsltKndCd2;
	}
	
	public void setRsltKndCd2(String rsltKndCd2) {
		this.rsltKndCd2 = rsltKndCd2;
	}

	synchronized public void addResult(Game game) {
		// 오류
		if (game.isHostcrash() || game.isAwaycrash()) {
			this.rsltKndCd = "05";
			
			if (game.isHostcrash() && game.isAwaycrash()) {
				this.rsltKndCd2 = "03";
			} else if (game.isHostcrash()) {
				this.rsltKndCd2 = "04";
			} else if (game.isAwaycrash()) {
				this.rsltKndCd2 = "02";
			}			
		}
		else if (game.getHostScore() < MIN_SCORE || game.getAwayScore() < MIN_SCORE) {
			this.rsltKndCd = "05";
			
			if (game.getHostScore() < MIN_SCORE && game.getAwayScore() < MIN_SCORE) {
				this.rsltKndCd2 = "03";
			} else if (game.getHostScore() < MIN_SCORE) {
				this.rsltKndCd2 = "04";
			} else if (game.getAwayScore() < MIN_SCORE) {
				this.rsltKndCd2 = "02";
			}
		}
		// 무승부
		else if (game.isWasDraw() || game.getFinalFrame() >= ServerSettings.Instance().tmSettings.GameFrameLimit) {
			this.rsltKndCd = "03";
		}
		// host 승
		else if (game.isHostwon()) {
			this.rsltKndCd = "02";
		}
		// host 패
		else {
			this.rsltKndCd = "04";
		}
		saveCnt++;
		time = System.currentTimeMillis();
	}
	
	public boolean canSend() {
		// 2번의 결과를 받으면 결과 전송 
		if (saveCnt == 2) {
			return true;
		}
		// 결과를 받은 후 20초가 지나면 결과 전송.
		else if (saveCnt > 0 && time + 20000 < System.currentTimeMillis()) {
			LOG.info("saveCnt : " + saveCnt + ", hostRepNm : " + hostBotName + ", awayRepNm : " + awayBotName);
			return true;
		}
		return false;
	}
}
