package be.tba.websockets;

import be.tba.util.data.IntertelCallData;

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
    public String answeredByPhoneId;
    public String answeredBySession;
    
    public WebSocketData(int operation, long timeSt, IntertelCallData data)
    {
		this.operation = operation;
		this.timeStamp = timeSt;
		this.callId = data.intertelCallId;
		this.customer = data.customer;
		this.dbId = data.dbRecordId;
		this.answeredByPhoneId = data.answeredBy;
		this.answeredBySession = "";
    }
    
	public String toString()
	{
		return "WebSocketData: callId=" +  callId + ", customer=" + customer + ", dbId=" + dbId + ", PhoneId=" + answeredByPhoneId + ", session=" + answeredBySession;
	}

}
