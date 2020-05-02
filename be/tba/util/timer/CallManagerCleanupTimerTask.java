package be.tba.util.timer;

import java.util.Date;
import java.util.TimerTask;

import be.tba.servlets.helper.IntertelCallManager;
import be.tba.servlets.helper.PhoneMapManager;
import be.tba.util.constants.Constants;

public class CallManagerCleanupTimerTask extends TimerTask implements TimerTaskIntf 
{
   public CallManagerCleanupTimerTask()
   {
      super();
      System.out.println("CallManagerCleanupTimerTask created");
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
//    return Constants.SECONDS * 20;
      return Constants.MINUTES * 5;
   }

   @Override
   public TimerTask getTimerTask() {
      // TODO Auto-generated method stub
      return this;
   }

   @Override
   public void run() 
   {
      IntertelCallManager.getInstance().cleanUpMap();
      //PhoneMapManager.getInstance().cleanUpMap();
      
   }

   @Override
   public void cleanUp()
   {
      // TODO Auto-generated method stub
      System.out.println("Cancel CallManagerCleanupTimerTask");
      this.cancel();
   }

}
