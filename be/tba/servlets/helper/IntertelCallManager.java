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
	
	private Map<Integer, IntertelCallData> mCallMap;
	
	private IntertelCallManager()
	{
		mCallMap = new HashMap<Integer, IntertelCallData>();
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
	
	public synchronized IntertelCallData get(int id)
	{
		return mCallMap.get(new Integer(id));
	}
	
	public synchronized void removeCall(int id)
	{
		mCallMap.remove(new Integer(id));
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
		
        for (Iterator<Integer> i = mCallMap.keySet().iterator(); i.hasNext();)
        {
        	Integer key = i.next();
        	IntertelCallData data = mCallMap.get(key);
        	if (data.tsStart < (tsNow - 7200)) // 2 hours
        	{
        		mCallMap.remove(key);
        	}
        }
	}
	
}
