package be.tba.websockets;

public class WebSocketData 
{
	public static  final int AUTHENTICATION_LOGIN = 1;
    public static final int NEW_CALL = 101;
    public static final int CALL_ANSWERED = 102;
    
    public int operation;
    public long timeStamp;
    public String callId;
    public String customer;
    public int dbId;
    
    public WebSocketData(int operation, long timeSt, String callId, String customer, int dbId)
    {
		this.operation = operation;
		this.timeStamp = timeSt;
		this.callId = callId;
		this.customer = customer;
		this.dbId = dbId;
    }
    
	public String toString()
	{
		return "WebSocketData: callId=" +  callId + ", customer=" + customer;
	}

}
