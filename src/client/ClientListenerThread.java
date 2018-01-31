package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import objects.*;

public class ClientListenerThread extends Thread
{
	static final private Logger LOG = LoggerFactory.getLogger(ClientListenerThread.class);
	private Client client;
	private Socket socket = null;
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	
	public boolean connected = false;

	public ClientListenerThread(Client client) 
	{
		LOG.debug("ClientListenerThread constructor");
		this.client = client;
	}

	private void setupSocket()
	{
		String temp = client.getServer();
		int port = Integer.parseInt(temp.substring(temp.indexOf(':') + 1));
		String address = temp.substring(0, temp.indexOf(':'));
		
		LOG.debug("Connecting to Server @ " + address + ":" + port + "\n");
		Client.log("Connecting to Server @ " + address + ":" + port + "\n");
		
		while(true)
		{
			try 
			{
				Client.updateFunc("Connecting");
				socket = new Socket(address, port);
				socket.setKeepAlive(true);
				connected = true;
				break;
			} 
			catch (Exception e) 
			{
				LOG.debug("Couldn't connect to server, trying again in 5 seconds.\n");
				Client.log("Couldn't connect to server, trying again in 5 seconds.\n");
				connected = false;
			}
			
			try { Thread.sleep(5000); } catch (Exception e) { };
		}
	}
	
	private void setupStreams()
	{
		try 
		{
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			LOG.error("in or out failed", e);
			System.exit(-1);
		}
	}
	
	public String getAddress()
	{
		return "" + socket.getInetAddress();
	}
	
	public void run()
	{
		LOG.debug("ClientListenerThread run()");
		
		setupSocket();
		setupStreams();

		if (ClientSettings.Instance().ClientProperties != null)
		{
			client.sendProperties(ClientSettings.Instance().ClientProperties);
		}
		
		client.setStatus(ClientStatus.READY);
		
		while (true) 
		{
			try 
			{
				//Client.log("CListner: Waiting for Message...\n");
				LOG.debug("CListner: Waiting for Message...");
				Message m = (Message) ois.readObject();
				Client.log("CListner: Message recieved: " + m.toString() + "\n");
				LOG.debug("CListner: Message recieved: " + m.toString());
				client.receiveMessage(m);
			} 
			catch (Exception e1) 
			{
				connected = false;
				Client.log("Server disconnected, reconnecting...\n");
				LOG.error("Cannot contact server, reconnecting", e1);
				client.gameInit();
				
				try {
					ois.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
				try {
					oos.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
				try {
					socket.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
				
				setupSocket();
				setupStreams();

				client.setStatus(ClientStatus.READY);
			}
		}	
	}

	public void sendMessageToServer(Message m)
	{
		try 
		{
			oos.writeObject(m);
			oos.flush();
			oos.reset();
		}
		catch (IOException e) 
		{
			LOG.error("sendMessageToServer() exception", e);
			System.exit(-1);
		}
	}
}

