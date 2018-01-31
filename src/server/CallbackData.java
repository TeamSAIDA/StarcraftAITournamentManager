package server;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import objects.BattleApply;
import objects.Game;

public class CallbackData {
	final static private Logger LOG = LoggerFactory.getLogger(CallbackData.class);
	final private ConcurrentMap<Integer, BattleApply> callbackData = new ConcurrentHashMap<Integer, BattleApply>();
	final private ConcurrentMap<Integer, Integer> turnCnt = new ConcurrentHashMap<Integer, Integer>();
//	final private ConcurrentMap<Integer, List<BattleApply>> endData = new ConcurrentHashMap<Integer, List<BattleApply>>();
	
	public void add(Integer turn, Integer key, BattleApply value) {
		if (!this.callbackData.containsKey(key)) {
			this.callbackData.put(key, value);
			
			this.addTurnCnt(turn);
			
			LOG.debug(key + " game added! (size : " + this.callbackData.size() + ") \t" + turn + " turn (turnSize : " + turnCnt + ")");
		}
	}
	
	private void addTurnCnt(Integer turn) {
		synchronized (this.turnCnt) {
			Integer turnCnt = this.turnCnt.get(turn);
			if (turnCnt == null) {
				turnCnt = 0;
			}
			this.turnCnt.put(turn, ++turnCnt);
		}
	}
	
	private boolean isFinishAfterSubstractTurnCnt(Integer turn) {
		synchronized (this.turnCnt) {
			// 회차의 모든 게임이 종료가 되면 리턴
			Integer iTurn = this.turnCnt.get(turn);
			
			if (iTurn == 1) {
				this.turnCnt.remove(turn);
				return true;
			} else {
				this.turnCnt.put(turn, --iTurn);
			}
			return false;
		}
	}

	public void addResult(Game game) {
		BattleApply battleApply = this.callbackData.get(game.getGameID());
		if (battleApply != null)
			battleApply.addResult(game);
	}
	
	public int remove(Integer key) {
		BattleApply remove = this.callbackData.remove(key);
		int turn = remove.getTurn();

		boolean isFinish = this.isFinishAfterSubstractTurnCnt(turn);
		LOG.debug(key + " game removed! (size : " + this.callbackData.size() + ")\t" + turn + " turn (turnSize : " + turnCnt + ")");
		if (isFinish) {
			return turn;
		}

		return -1;
	}

	// 해당 회차(turn)의 모든 게임이 종료된 경우 게임 리스트를 반환한다.
	public int getComplete() {
		synchronized (this.callbackData) {
			Set<Integer> keySet = this.callbackData.keySet();
			
			for (Integer key : keySet) {
				if (this.callbackData.get(key).canSend()) {
					int rst = this.remove(key);
					
					if (rst >= 0) {
						return rst;
					}
				}
			}
		}
		
		return -1;
	}
}
