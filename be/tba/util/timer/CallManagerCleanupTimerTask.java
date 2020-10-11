package be.tba.util.timer;

import java.util.Date;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.session.IntertelCallManager;
import be.tba.session.PhoneMapManager;
import be.tba.sqldata.AccountCache;
import be.tba.util.constants.Constants;

public class CallManagerCleanupTimerTask extends TimerTask implements TimerTaskIntf
{
   private static Logger log = LoggerFactory.getLogger(CallManagerCleanupTimerTask.class);

   public CallManagerCleanupTimerTask()
   {
      super();
      log.info("CallManagerCleanupTimerTask created");
   }

   @Override
   public Date getStartTime()
   {
      // TODO Auto-generated method stub
      // start now
      return null;
   }

   @Override
   public long getPeriod()
   {
      // TODO Auto-generated method stub
//    return Constants.SECONDS * 20;
      return Constants.MINUTES * 5;
   }

   @Override
   public TimerTask getTimerTask()
   {
      // TODO Auto-generated method stub
      return this;
   }

   @Override
   public void run()
   {
      IntertelCallManager.getInstance().cleanUpMap();
      // PhoneMapManager.getInstance().cleanUpMap();

   }

   @Override
   public void cleanUp()
   {
      // TODO Auto-generated method stub
      log.info("Cancel CallManagerCleanupTimerTask");
      this.cancel();
   }

}
