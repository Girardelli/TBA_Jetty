package be.tba.websockets;

public class WebSocketMessage 
{
	public String from;
	public String to;
	public String body;
	public long sent;
	
	public String toString()
	{
		return "WebSocketMessage:" +  body;
	}

}
