package objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleApply {
	static final private Logger LOG = LoggerFactory.getLogger(BattleApply.class);
	
	/**
	 * data
	 */
	private int turn;
	
	/**
	 * state
	 */
	private int saveCnt = 0;
	private long time = System.currentTimeMillis();
	
	public BattleApply(Game game) {
		this.turn = game.getTurn();
	}

	public int getTurn() {
		return turn;
	}
	
	synchronized public void addResult(Game game) {
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
			LOG.info("saveCnt : " + saveCnt);
			return true;
		}
		return false;
	}
}
