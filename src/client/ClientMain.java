package client;


public class ClientMain 
{	
	public static void main(String[] args) throws Exception
	{
		if (!System.getProperty("os.name").contains("Windows"))
		{
			System.err.println("Sorry, Client can only be run on Windows.");
		}
		
		if (args.length == 1)
		{
			ClientSettings.Instance().parseSettingsFile(args[0]);
		}
		else
		{
			ClientSettings.Instance().parseSettingsFile("client_settings.json");
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