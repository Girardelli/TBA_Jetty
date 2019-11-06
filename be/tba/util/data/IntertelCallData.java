package be.tba.util.data;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.util.constants.Constants;
import be.tba.util.session.AccountCache;

public class IntertelCallData 
{
	public static String kTbaNr = Constants.NUMBER_BLOCK[0][0];
   public static String kTbaName = Constants.NUMBER_BLOCK[0][3];
	
	
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
	public String customer;
	//public IntertelCallData transferData;
	public int dbRecordId;
	public boolean isTransferOutCall;
	public boolean isEndDone;
	public boolean isSummaryDone;
	public boolean isWsRemoved; //tell whether this call was already removed from websocket list
	public IntertelCallData callParkBug_transferLink;
	
	public IntertelCallData(boolean isIncoming, String calledNr, String callingNr, String callId, long tsStart, String phase)
	{
		// incoming calls: only keep the last 6 numbers to match it with the customer FwdNr (Constants)
		this.calledNr = calledNr;
		// outgoing calls: save the standard TBA number. The summary event shall update this to the actual number (e.g. when outgoing code is used)
		this.callingNr = callingNr;
		this.phase = phase;
		this.isIncoming = isIncoming;
		this.tsStart = tsStart;
		this.intertelCallId = callId;
		this.isEndDone = false;
		this.isSummaryDone = false;
		this.isWsRemoved = false;
		this.callParkBug_transferLink = null; 
		this.answeredBy = "";
		if (isIncoming)
		{
			AccountEntityData account = AccountCache.getInstance().get(last6Numbers(this.calledNr));
			//System.out.println("AccountCache.getInstance().get(" + last6Numbers(this.calledNr) + ") returned " + account);
			if (account != null)
			{
				this.customer = new String(account.getFullName());
			}
		}
		else
		{
         AccountEntityData account = AccountCache.getInstance().get(last6Numbers(this.callingNr));
         //System.out.println("AccountCache.getInstance().get(" + last6Numbers(this.calledNr) + ") returned " + account);
         if (account != null)
         {
            this.customer = new String(account.getFullName());
         }
         else
         {
            this.callingNr = kTbaNr;
            this.customer = kTbaName;
         }
		}
	}
	
	public void setDbRecordId(int id)
	{
		this.dbRecordId = id;
	}
	
	public void setCurrentPhase(String phase)
	{
		this.phase = (phase == null?"":phase);
	}
	
	public void setTsAnswer(long tsAnswer)
	{
		this.tsAnswer = tsAnswer;
		this.isWsRemoved = true;
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
		if (this.isIncoming)
		   this.calledNr = last6Numbers(calledNr);
		else
		   this.callingNr = last6Numbers(callingNr);
	}
	
	public void setCallingNr(String nr)
	{
		this.callingNr = ((nr == null) ? "" : nr);
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
			System.out.println("No tsAnswer timestamp logged: return full length=" + (int)(tsEnd - tsStart));
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
	
	public String toString()
	{
	   return ("calledNr=" + calledNr + ", callingNr=" + callingNr + ", phase=" + phase + ", call-id=" + intertelCallId + ", answerdBy=" + answeredBy + ", tsStart=" + tsStart + ", tsAnswer=" + tsAnswer + ", tsEnd=" + tsEnd); 
	}
	
	private String secondsToString(int seconds)
	{
		return String.format("%d:%02d:%02d", seconds / 3600, seconds % 3600 / 60, seconds % 60);
	}
	
	public static String last6Numbers(String nr)
	{
		if (nr.length() > 6)
		{
			nr = nr.substring(nr.length() - 6, nr.length());
		}
		return nr;
	}
}
