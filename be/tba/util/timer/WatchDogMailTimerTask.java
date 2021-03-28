package be.tba.util.timer;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.util.constants.Constants;

final public class WatchDogMailTimerTask extends TimerTask implements TimerTaskIntf
{
   private static Logger log = LoggerFactory.getLogger(WatchDogMailTimerTask.class);
   private TimerTaskIntf mailerTimerTask;

   public WatchDogMailTimerTask(TimerTaskIntf mailerTimerTask)
   {
   	this.mailerTimerTask = mailerTimerTask;
   }
   
   @Override
   public Date getStartTime()
   {
      // TODO Auto-generated method stub
      GregorianCalendar vCalendar = new GregorianCalendar();
      return vCalendar.getTime();
   }

   @Override
   public long getPeriod()
   {
      // TODO Auto-generated method stub
      return Constants.MAILER_DRUMBEAT*5;
   }

   @Override
   public TimerTask getTimerTask()
   {
      // TODO Auto-generated method stub
      return this;
   }

	@Override
	public void cleanUp()
	{
      log.info("Cancel WatchDogMailTimerTask");
      this.cancel();
	}

	@Override
	public void run()
	{
      log.info("WatchDogMailTimerTask run");
		GregorianCalendar vCalendar = new GregorianCalendar();
   	long currentTime = vCalendar.getTimeInMillis();
   	long lastMailerRunTime = MailTimerTask.getLastRunStart();
   	if (lastMailerRunTime > 0 && (currentTime - lastMailerRunTime) > 10*Constants.MAILER_DRUMBEAT)
   	{
         log.info("Recover MailerTask!!!!!!!");
   		TimerManager.getInstance().destroyTimer(mailerTimerTask);
   		this.mailerTimerTask = new MailTimerTask();
   		TimerManager.getInstance().add(this.mailerTimerTask);
   	}
	}
}
