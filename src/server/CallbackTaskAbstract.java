package server;

import java.util.TimerTask;

public abstract class CallbackTaskAbstract extends TimerTask {
	static public CallbackData callbackData = new CallbackData();
	
	@Override
	public void run() {
		int completeTurn = callbackData.getComplete();
		
		Object sr = summaryResult(completeTurn);
		
		if (sr != null) {
			sendResult(sr, ServerSettings.Instance().SendUrl);
		}
	}
	
	abstract protected Object summaryResult(int completeTurn);

	abstract protected void sendResult(Object summary, String url);
}
