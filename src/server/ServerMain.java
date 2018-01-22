package server;


public class ServerMain 
{

	public static String serverSettingsFile;
	
	public static void main(String[] args) throws Exception
	{
		if (args.length == 1)
		{
			ServerSettings.Instance().parseSettingsFile(args[0]);
		}
		else
		{
			ServerSettings.Instance().parseSettingsFile("server_settings.json");
		}
		
		Server.Instance().start();
		
		while (true)
		{
			Thread.sleep(1000);
		}
	}

}
