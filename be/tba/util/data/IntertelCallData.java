package be.tba.util.data;

public class IntertelCallData 
{
	public static String kTbaNr = "409000";
	
	public String calledNr;
	public String callingNr;
	public String phase;
	public boolean isIncoming;
	public long tsStart;
	public long tsAnswer;
	public long tsTransfer;
	public long tsEnd;
	public String intertelCallId;
	//public IntertelCallData transferData;
	public int dbRecordId;
	public boolean isTransferOutCall;
	
	public IntertelCallData(boolean isIncoming, String calledNr, String CallingNr, String callId, long tsStart, String phase)
	{
		this.calledNr = calledNr;
		this.callingNr = isIncoming ? CallingNr : kTbaNr;
		this.phase = phase;
		this.isIncoming = isIncoming;
		this.tsStart = tsStart;
		this.intertelCallId = callId;
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
	
	public void setTsTransfer(long tsTransfer)
	{
		this.tsTransfer = tsTransfer;
	}
	
	public void setIsTransfer()
	{
		this.isTransferOutCall = true;
	}
	
	public void setTsEnd(long tsEnd)
	{
		this.tsEnd = tsEnd;
	}
	
//	public void setTransferData(IntertelCallData data)
//	{
//		this.transferData = data;
//	}
//	
//	public IntertelCallData getTransferData()
//	{
//		return transferData;
//	}
	
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
	
	public boolean equalFromToButCallIdDifferent(IntertelCallData data)
	{
		// to find transfered call
		return (calledNr.equals(data.calledNr) && callingNr.equals(data.callingNr) && !intertelCallId.equals(data.intertelCallId));
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
