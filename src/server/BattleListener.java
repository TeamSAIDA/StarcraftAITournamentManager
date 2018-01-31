package server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpServer;

class BattleListener extends Thread {
	static final private Logger LOG = LoggerFactory.getLogger(BattleListener.class);
	private HttpServer server;

	public BattleListener() {
		int port = ServerSettings.Instance().RecvPort;

		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			LOG.debug("Server: Server address " + Inet4Address.getLocalHost().getHostAddress() + " Listening on port " + port);
			server.createContext("/", new HttpServerHandler());
			server.setExecutor(Executors.newCachedThreadPool());
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public void run() {
		server.start();
	}
}
