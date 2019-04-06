package be.tba.util.data;

public class IntertelCallData 
{
	public String calledNr;
	public String callingNr;
	public String phase;
	public boolean isIncoming;
	public long tsStart;
	public long tsAnswer;
	public long tsEnd;
	public int intertelCallId;
	public IntertelCallData transferData;
	public int dbRecordId;
	public boolean isTransferCall;
	
	public IntertelCallData(boolean isIncoming, String calledNr, String CallingNr, int kNumber, long tsStart, boolean isTransferCall, String phase)
	{
		this.calledNr = calledNr;
		this.callingNr = isIncoming ? CallingNr : "409000";
		this.phase = phase;
		this.isIncoming = isIncoming;
		this.tsStart = tsStart;
		this.intertelCallId = kNumber;
		this.isTransferCall = isTransferCall;
	}
	
	public void setDbRecordId(int id)
	{
		this.dbRecordId = id;
	}
	
	public void setCurrentPhase(String phase)
	{
		this.phase = phase;
	}
	
	public void setTsAnswer(long tsAnswer)
	{
		this.tsAnswer = tsAnswer;
	}
	
	public void setTsEnd(long tsEnd)
	{
		this.tsEnd = tsEnd;
	}
	
	public void setTransferData(IntertelCallData data)
	{
		this.transferData = data;
	}
	
	public IntertelCallData getTransferData()
	{
		return transferData;
	}
	
	public int getCallDuration()
	{
		if (tsAnswer > 0 && tsEnd > 0)
		{
			return (int)(tsEnd - tsAnswer);
		}
		return 0;
	}
	
	public String getCostStr()
	{
		return secondsToString(getCallDuration());
	}
	
	private String secondsToString(int seconds)
	{
		if (seconds >= 3600)
		{
			return String.format("%02d:%02d:%02d", seconds / 3600, seconds % 3600 / 60, seconds % 60);
		}
		return String.format("%02d:%02d", seconds / 60, seconds % 60);
	}
	
}
