package be.tba.util.timer;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.TimerTask;

import be.tba.util.constants.Constants;

public class UrlCheckTimerTask extends TimerTask implements TimerTaskIntf 
{
	private static boolean isUrlUp = true;
	
	public static boolean getIsWebsiteUp()
	{
		return isUrlUp;
	}
	
	public UrlCheckTimerTask()
	{
		super();
		System.out.println("UrlCheckTimerTask created");
	}
	
	@Override
	public Date getStartTime() {
		// TODO Auto-generated method stub
		// start now
		return null;
	}

	@Override
	public long getPeriod() {
		// TODO Auto-generated method stub
//		return Constants.MINUTES;
		return Constants.SECONDS * 5;
	}

	@Override
	public TimerTask getTimerTask() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void run() 
	{
		boolean urlIsUp = true;
		URL url;
		HttpURLConnection con = null;
		try 
		{
			url = new URL(Constants.TBA_URL_BASE + "index.html");
			//System.out.print(url.toString());
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(3000);
			con.setReadTimeout(3000);
			con.connect();
			int resp = con.getResponseCode();
			con.disconnect();
			if (resp != 200)
			{
				urlIsUp = false;
			}
			
			//System.out.println(" returns: " + resp);
		} 
		catch (Exception e) 
		{
			urlIsUp = false;
			System.out.println("UrlCheckTimerTask returned: " + e.getClass().getName());
			//e.printStackTrace();
		}
		if (urlIsUp != UrlCheckTimerTask.isUrlUp)
		{
			UrlCheckTimerTask.isUrlUp = urlIsUp;
		}
	}

}
