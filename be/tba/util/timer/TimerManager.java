/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.timer;

import java.util.Collection;
import java.util.Date;
import java.util.Timer;
import java.util.Vector;

final public class TimerManager
{
   private final Collection<TimerTaskIntf> mTimerList;

   private static TimerManager mInstance;

   public static TimerManager getInstance()
   {
      if (mInstance == null)
         mInstance = new TimerManager();
      return mInstance;
   }

   private TimerManager()
   {
      mTimerList = new Vector<TimerTaskIntf>();
   }

   public void add(TimerTaskIntf task)
   {
      Timer taskTimer = new Timer();

      Date startTime = task.getStartTime();
      if (startTime == null)
      {
         startTime = new Date();
      }

      taskTimer.schedule(task.getTimerTask(), startTime, task.getPeriod());
      System.out.println("TimerManager() added: " + task.getClass().getName());
      mTimerList.add(task);
   }
   
   public void destroy()
   {
      for (TimerTaskIntf task : mTimerList)
      {
         task.cleanUp();
      }
   }
}
