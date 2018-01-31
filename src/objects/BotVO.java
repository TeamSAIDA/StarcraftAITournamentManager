package objects;

public class BotVO {
	public String BotName;
	public String Race;
	public String BotType;
	public String BWAPIVersion;

	public BotVO(Bot bot)
	{
		this.BotName = bot.getName();
		this.Race = bot.getRace();
		this.BotType = bot.getType();
		this.BWAPIVersion = bot.getBWAPIVersion();
	}
}
