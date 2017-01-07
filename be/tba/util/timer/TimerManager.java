/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.timer;

import java.util.Timer;

final public class TimerManager
{
   private final Timer mTimer;

   private static TimerManager mInstance;

   public static TimerManager getInstance()
   {
      if (mInstance == null)
         mInstance = new TimerManager();
      return mInstance;
   }

   private TimerManager()
   {
      mTimer = new Timer();
      mTimer.scheduleAtFixedRate(new DbCleanTimerTask(), DbCleanTimerTask.getScheduleTime(), DbCleanTimerTask.getPeriod());
      System.out.println("TimerManager() DbCleaner started.");

      mTimer.schedule(new MailTimerTask(), 0, MailTimerTask.getPeriod());
      System.out.println("TimerManager() MailTimerTask started.");
   }
}
