package be.tba.servlets.helper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import be.tba.util.data.IntertelCallData;

public class IntertelCallManager 
{
	private static IntertelCallManager mInstance;
	
	private Map<String, IntertelCallData> mCallMap;
	
	private IntertelCallManager()
	{
		mCallMap = new HashMap<String, IntertelCallData>();
	}

	public static IntertelCallManager getInstance()
	{
		if (mInstance == null)
		{
			mInstance = new IntertelCallManager();
		}
		return mInstance;
	}
	
	public synchronized void newCall(IntertelCallData data)
	{
		mCallMap.put(data.intertelCallId, data);
	}
	
	public synchronized IntertelCallData get(String callId)
	{
		return mCallMap.get(callId);
	}
	
	public synchronized IntertelCallData getByDbId(int id)
	{
		Collection<IntertelCallData> calls = mCallMap.values();
		for (Iterator<IntertelCallData> itr = calls.iterator(); itr.hasNext();)
		{
			IntertelCallData call = itr.next();
			if (call.dbRecordId == id)
			{
				return call;
			}
		}
		return null;
	}
	
	public synchronized IntertelCallData getransferCall(String transferedCallId, String transferCalledNr, String transferCallingNr)
	{
		// callId is of the incomming call that is transfered. We are looking for a call that has NOT  this ID
		// CallingNr: tba number
		// calledNr: number to who the call is transfered
		
		for (Iterator<String> i = mCallMap.keySet().iterator(); i.hasNext();)
        {
        	String key = i.next();
        	IntertelCallData data = mCallMap.get(key);
        	
        	System.out.println("getransferCall: " + transferCalledNr + "==" + data.calledNr + " && " + transferCallingNr + "==" + data.callingNr + " && " + transferedCallId + " != " +  data.intertelCallId);
        	
        	if (transferCalledNr.equals(data.calledNr) && transferCallingNr.equals(data.callingNr) && !transferedCallId.equals(data.intertelCallId))
        	{
        		return data;
        	}
        }
        return null;
	}
	
	public synchronized void removeCall(String callId)
	{
		IntertelCallData data = mCallMap.get(callId);
		if (data.tsEnd > 0)
		{
			mCallMap.remove(callId);
		}
	}
	
	public synchronized Collection<IntertelCallData> getCallList()
	{
		Collection<IntertelCallData> calls = new Vector<IntertelCallData>();
		calls.addAll(mCallMap.values());
		return calls;
	}
	
	public synchronized void cleanUpOldCalls()
	{
		long tsNow = System.currentTimeMillis() / 1000l;
		
        for (Iterator<String> i = mCallMap.keySet().iterator(); i.hasNext();)
        {
        	String key = i.next();
        	IntertelCallData data = mCallMap.get(key);
        	if (data.tsStart < (tsNow - 7200)) // 2 hours
        	{
        		mCallMap.remove(key);
        	}
        }
	}
	
}
