/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.timer;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import be.tba.business.LoginBizzLogic;
import be.tba.session.SessionManager;
import be.tba.session.WebSession;
import be.tba.sqladapters.CallRecordSqlAdapter;
import be.tba.sqladapters.LoginSqlAdapter;
import be.tba.sqldata.LoginEntityData;
import be.tba.util.constants.Constants;

final public class DbCleanTimerTask extends TimerTask implements TimerTaskIntf
{
   private static Logger log = LoggerFactory.getLogger(DbCleanTimerTask.class);
   // private static final int kCleanPerSession = 50;

   public DbCleanTimerTask()
   {
   }

   public Date getStartTime()
   {
      GregorianCalendar vCalendar = new GregorianCalendar();
      vCalendar.set(Calendar.HOUR_OF_DAY, 3);
      vCalendar.set(Calendar.MINUTE, 0);
      if (vCalendar.getTimeInMillis() < System.currentTimeMillis())
      {
         vCalendar.add(Calendar.DAY_OF_MONTH, 1);
      }
      log.info("getScheduleTime() returned " + vCalendar.getTime().toString());
      return vCalendar.getTime();
   }

   @Override
   public TimerTask getTimerTask()
   {
      // TODO Auto-generated method stub
      return this;
   }

   @Override
   public long getPeriod()
   {
      // TODO Auto-generated method stub
      return Constants.DAYS;
   }

   public void run()
   {
      Object obj = new String();
      WebSession session = null;
      try
      {
         session = new WebSession();
         File file = new File(Constants.RECORDS_OF_TODAY_PATH);
         if (file.exists())
         {
            file.delete();
         }

         CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
         vQuerySession.cleanDb(session);
         
         // logins
         //LoginBizzLogic.cleanup(session);
         
         // sessions
         SessionManager.getInstance().clean();
         //log.info("DB clean deleted " + deleted + " call records.");
      }
      catch (Exception e)
      {
         log.info("DbCleanTimerTask ; lookup failed: " + obj);
         log.error(e.getMessage(), e);
      }
      finally
      {
         try
         {
            if (session != null && session.getConnection() != null)
               session.getConnection().close();
         }
         catch (SQLException ex)
         {
            log.info("Error in Mailer: SQL connection could not be closed.");
         }
      }
   }

   @Override
   public void cleanUp()
   {
      // TODO Auto-generated method stub
      log.info("Cancel DbCleanTimerTask");
      this.cancel();
   }
}
