package be.tba.util.data;

import be.tba.util.constants.Constants;

public class IntertelCallData 
{
	public static String kTbaNr = Constants.NUMBER_BLOCK[0][0];
	
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
	public boolean isEndDone;
	public boolean isSummaryDone;
	
	public IntertelCallData(boolean isIncoming, String calledNr, String CallingNr, String callId, long tsStart, String phase)
	{
		// incoming calls: only keep the last 6 numbers to match it with the customer FwdNr (Constants)
		this.calledNr = isIncoming ? calledNr.substring(calledNr.length() - 8, calledNr.length()) : calledNr;
		// outgoing calls: save the standard TBA number. The summary event shall update this to the actual number (e.g. when outgoing code is used)
		this.callingNr = isIncoming ? CallingNr : kTbaNr;
		this.phase = phase;
		this.isIncoming = isIncoming;
		this.tsStart = tsStart;
		this.intertelCallId = callId;
		this.isEndDone = false;
		this.isSummaryDone = false;
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
