package be.tba.websockets;

import be.tba.util.data.IntertelCallData;

public class WebSocketData 
{
	public static  final int AUTHENTICATION_LOGIN = 1;
    public static final int NEW_CALL = 101;
    public static final int CALL_ANSWERED = 102;
    public static final int URGENT_CALL = 103;
    public static final int NEW_CHAT = 104;
    
    public int operation;
    public long timeStamp;
    public String intertelCallId;
    public String customer;
    public int dbCallId;
    public String answeredByPhoneId;
    public String answeredBySession;
    public String callText;
    public String timeStr;
    
    public WebSocketData(int operation, long timeSt, IntertelCallData data)
    {
		this.operation = operation;
		this.timeStamp = timeSt;
		this.intertelCallId = data.intertelCallId;
		this.customer = data.customer;
		this.dbCallId = data.dbRecordId;
		this.answeredByPhoneId = data.answeredBy;
		this.answeredBySession = "";
      this.callText = "";
      this.timeStr = "";
    }

    public WebSocketData(int operation, int callId, String customer, String text, String timeStr)
    {
      this.operation = operation;
      this.timeStamp = 0;
      this.dbCallId = callId;
      this.customer = customer;
      this.answeredBySession = "";
      this.callText = text;
      this.timeStr = timeStr;
    }

	public String toString()
	{
		return "WebSocketData: callId=" +  intertelCallId + ", customer=" + customer + ", dbId=" + dbCallId + ", PhoneId=" + answeredByPhoneId + ", session=" + answeredBySession + ", text=" + callText;
	}

}
