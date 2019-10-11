package be.tba.util.data;

import be.tba.util.constants.Constants;

public class IntertelCallData 
{
	public static String kTbaNr = Constants.NUMBER_BLOCK[0][0];
	
	public String calledNr;
	public String callingNr;
	public String answeredBy;
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
		this.calledNr = isIncoming ? last6Numbers(calledNr) : calledNr;
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
	
	public void setCallingNr(String nr)
	{
		if (nr == null)
			nr = "";
		this.callingNr = last6Numbers(nr);
	}
	
	public void setAnsweredBy(String phoneId)
	{
		if (phoneId == null)
			phoneId = "";
		this.answeredBy = phoneId;
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
		// to be removed!!!!! --> bug in new Intertel webhook logging, missing answer event
		else if (tsStart > 0 && tsEnd > 0)
		{
			System.out.println("No tsAnswer timestamp logged: return full length=" + (int)(tsStart - tsEnd));
			return (int)(tsEnd - tsStart);
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
		return String.format("%d:%02d:%02d", seconds / 3600, seconds % 3600 / 60, seconds % 60);
	}
	
	private String last6Numbers(String nr)
	{
		if (nr.length() > 6)
		{
			nr = nr.substring(nr.length() - 6, nr.length());
		}
		return nr;
	}
}
