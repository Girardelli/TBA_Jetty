package be.tba.util.timer;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.ejb.phoneMap.session.UrlCheckerSqlAdapter;
import be.tba.util.constants.Constants;

public class UrlCheckTimerTask extends TimerTask implements TimerTaskIntf 
{
   private static Logger log = LoggerFactory.getLogger(UrlCheckTimerTask.class);
   private static boolean isUrlUp = true;
	private static long deadStart = 0;
	
	public static boolean getIsWebsiteUp()
	{
		return isUrlUp;
	}
	
	public UrlCheckTimerTask()
	{
		super();
		log.info("UrlCheckTimerTask created");
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
		return Constants.SECONDS * 15;
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
			
			//log.info(" returns: " + resp);
		} 
		catch (Exception e) 
		{
		   urlIsUp = false;
		   if (UrlCheckTimerTask.isUrlUp)
		   {
		      log.info("UrlCheckTimerTask returned: " + e.getClass().getName());
		   }
			//log.error(e.getMessage(), e);
			if (con != null)
			{
				con.disconnect();
			}
		}
		if (urlIsUp != UrlCheckTimerTask.isUrlUp)
		{
			UrlCheckTimerTask.isUrlUp = urlIsUp;
			if (urlIsUp)
			{
			   // again OK
			   Calendar calendar = Calendar.getInstance();
			   long deadTime = (calendar.getTimeInMillis() - deadStart) / 1000; 
            log.info("UrlCheckTimerTask stopped counting link down: " + deadTime + " seconds down");
            UrlCheckerSqlAdapter checker = new UrlCheckerSqlAdapter();
            checker.update(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), deadTime);
			}
			else
			{
			   // it went down
			   deadStart = Calendar.getInstance().getTimeInMillis();
			   log.info("UrlCheckTimerTask starts counting link down");
			}
		}
	}

   @Override
   public void cleanUp()
   {
      // TODO Auto-generated method stub
      log.info("Cancel UrlCheckTimerTask");
      this.cancel();
   }
}
