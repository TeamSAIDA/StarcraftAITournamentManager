package server;

import java.net.Inet4Address;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ServerListenerThread extends Thread 
{
	static private final Logger LOG = LoggerFactory.getLogger(ServerListenerThread.class);
	private ServerSocket 	serverSocket = null;
	private Server 			server;

	ServerListenerThread(Server s) 
	{
		server = s;
		
		try 
		{
			serverSocket = new ServerSocket(server.getPort());
			server.log("Server: Server address " + Inet4Address.getLocalHost().getHostAddress() + " Listening on port " + server.getPort() + "\n");
		} 
		catch (Exception e) 
		{
			LOG.error("Listener: Could not listen on port " + server.getPort(), e);
			System.exit(-1);
		}
	}

	public void run() 
	{
		while (true) 
		{
			ServerClientThread c = null;
			try 
			{
				c = new ServerClientThread(serverSocket.accept(), server);
				server.log("Listener: New client connected\n");
				c.start();
				server.log("Listener: New client thread started\n");
			} 
			catch (Exception e) 
			{
				LOG.error("Listener: Error in run() main loop", e);
				System.exit(-1);
			}
		}
		
	}

	synchronized void stopThread() 
	{
		this.interrupt();
	}

	protected void finalize() 
	{
		try 
		{
			serverSocket.close();
		} 
		catch (Exception e) 
		{
			LOG.error("Listener: Could not close.", e);
		}
	}
}
