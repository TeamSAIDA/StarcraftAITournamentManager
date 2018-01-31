package objects;

public class KeepAliveMessage implements Message
{
	private static final long serialVersionUID = -347052767995407052L;
	private final String address;

	public KeepAliveMessage(String address)
	{
		this.address = address;
	}
	
	public String toString()
	{
		return address + " Keep Alive!";
	}
}