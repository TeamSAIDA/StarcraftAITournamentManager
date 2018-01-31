package server;

import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMain 
{
	static final private Logger LOG = LoggerFactory.getLogger(ServerMain.class);
	public static void main(String[] args) throws Exception
	{
		if (args.length == 1)
		{
			ServerSettings.Instance().parseSettingsFile(args[0]);
		}
		else
		{
			try {
				ServerSettings.Instance().parseSettingsFile("server_settings.json");
			} catch (Exception e) {
				LOG.error("\n\nPlease provide server settings file as command line argument.\n", e);
				System.exit(-1);
			}
		}
		
		// 토너먼트 서버 구동
		Server.Instance().start();
		
		// 게임결과 확인 및 리턴하는 쓰레드
		startResultReqThread();

		// 대전 수행 명령을 받는 쓰레드
		startRestServer();

		// Server 와 Client 간의 연결 유지를 위한 KeepAlive 쓰레드
		startKeepAliveThread();

		while (true)
		{
			Thread.sleep(1000);
		}
	}
	
	private static void startKeepAliveThread() {
		KeepAliveTask keepAliveTask = new KeepAliveTask();
		
		Timer keepAliveTimer = new Timer(true);
		
		keepAliveTimer.scheduleAtFixedRate(keepAliveTask, 0, 600000);
	}

	private static void startRestServer() {
		new BattleListener().start();
	}
	
	private static void startResultReqThread() {
		Timer battleResultTimer = new Timer(true);
		
		battleResultTimer.scheduleAtFixedRate(new CallbackTaskHttpRes(), 0, 1000);
	}

}
