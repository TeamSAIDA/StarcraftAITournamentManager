package server;

import java.util.TimerTask;

public class KeepAliveTask extends TimerTask {
	@Override
	public void run() {
		Server.Instance().keepAlive();
	}
}
