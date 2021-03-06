/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.timer;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
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

   public MailTimerTask()
   {
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
      return Constants.MINUTES * 5;
   }

   @Override
   public TimerTask getTimerTask()
   {
      // TODO Auto-generated method stub
      return this;
   }

   public void run()
   {
      // log.info("Mail send for " + mAccountNr + ". Schedule time "
      // + long2String(scheduledExecutionTime()));
      // return;
      if (System.getenv("TBA_MAIL_ON") == null)
      {
         return;
      }
      WebSession session = null;
      // log.info("MailTimerTask run");

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
               synchronized(mailGroup)
               {
                  for (Iterator<AccountEntityData> j = mailGroup.iterator(); j.hasNext();)
                  {
                     AccountEntityData vAccount = j.next();
                     if (System.getenv("TBA_MAIL_ON") != null)
                     {
                        try
                        {
                           if (!Mailer.sendCallInfoMail(session, vAccount.getId()))
                           {
                              throw new Exception("Mail send failed!");
                           }

                        }
                        catch (Exception e)
                        {
                           log.info("Mail send failed to " + vAccount.getFullName());
                           MailError.getInstance().setError("Mail send failed to " + vAccount.getFullName() + "\n" + e.getMessage());
                           log.error(e.getMessage(), e);
                        }
                     }
                     else
                     {
                        log.info("Mail supposed to be send but disabled to " + vAccount.getFullName());
                     }
                  }
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
      log.info("Cancel MailTimerTask");
      this.cancel();
   }
}
