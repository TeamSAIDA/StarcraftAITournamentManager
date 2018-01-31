package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMain 
{	
	static final private Logger LOG = LoggerFactory.getLogger(ClientMain.class);
	public static void main(String[] args) throws Exception
	{
		if (!System.getProperty("os.name").contains("Windows"))
		{
			LOG.error("Sorry, Client can only be run on Windows.");
		}
		
		if (args.length == 1)
		{
			ClientSettings.Instance().parseSettingsFile(args[0]);
		}
		else
		{
			try {
				ClientSettings.Instance().parseSettingsFile("client_settings.json");
			} catch (Exception e) {
				LOG.error("\n\nPlease provide client settings file as command line argument.\n", e);
				System.exit(-1);
			}
		}
		
		Client client = new Client();
		ClientListenerThread listener = new ClientListenerThread(client);
		client.setListener(listener);
		
		client.start();

		while (true)
		{
			Thread.sleep(1000);
		}
	}
}