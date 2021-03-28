/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.timer;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.mail.MailError;
import be.tba.mail.Mailer;
import be.tba.session.WebSession;
import be.tba.sqldata.AccountCache;
import be.tba.sqldata.AccountEntityData;
import be.tba.util.constants.Constants;

final public class MailTimerTask extends TimerTask implements TimerTaskIntf
{
   private static Logger log = LoggerFactory.getLogger(MailTimerTask.class);
   private static long lastRunStart = 0; 

   public MailTimerTask()
   {
   	setLastRunStart(); 
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
      return Constants.MAILER_DRUMBEAT;
   }

   @Override
   public TimerTask getTimerTask()
   {
      // TODO Auto-generated method stub
      return this;
   }

   public static synchronized long getLastRunStart()
   {
   	return lastRunStart;
   }
   
   public void run()
   {
      WebSession session = null;
      log.info("MailTimerTask run");
      setLastRunStart();
      
      GregorianCalendar vCalendar = new GregorianCalendar();
      if (vCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && vCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
      {
         try
         {
         	session = new WebSession();
            Integer vKey = Integer.valueOf(vCalendar.get(Calendar.HOUR_OF_DAY) * 60 + vCalendar.get(Calendar.MINUTE));

            Collection<AccountEntityData> mailGroup = AccountCache.getInstance().getMailingGroup(vKey);
            if (mailGroup != null)
            {
            	//log.info("MailTimerTask processing account list of size: " + mailGroup.size());
               for (AccountEntityData vAccount : mailGroup)
               {
                  //log.info("Check call mail for " + vAccount.getFullName());
                  Mailer.sendCallInfoMail(session, vAccount.getId(), false);
               }
            }
         }
         catch (Exception e)
         {
            log.info("MailTimerTask exception");
            MailError.getInstance().setError("Mail send failed\n" + e.getMessage());
            log.error(e.getMessage(), e);
         }
         finally
         {
            if (session != null)
            {
               session.close();
            }
         }
         log.info("MailTimerTask run ended");
      }
      else
      {
         log.info("MailTimerTask run ended: weekend");
      }
   }

   static public String long2String(long time)
   {
      GregorianCalendar vCalendar = new GregorianCalendar();
      vCalendar.setTimeInMillis(time);
      return vCalendar.getTime().toString();

   }

   @Override
   public void cleanUp()
   {
      // TODO Auto-generated method stub
      log.info("Cancel MailTimerTask returns " + this.cancel());
   }
   
   private static synchronized void setLastRunStart()
   {
      GregorianCalendar vCalendar = new GregorianCalendar();
   	lastRunStart = vCalendar.getTimeInMillis();
   }

}
