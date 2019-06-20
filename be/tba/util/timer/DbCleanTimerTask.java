/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.timer;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimerTask;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;

final public class DbCleanTimerTask extends TimerTask
{
    // private static final int kCleanPerSession = 50;

    public DbCleanTimerTask()
    {
    }

    static public Date getScheduleTime()
    {
        GregorianCalendar vCalendar = new GregorianCalendar();
        vCalendar.set(Calendar.HOUR_OF_DAY, 3);
        vCalendar.set(Calendar.MINUTE, 0);
        if (vCalendar.getTimeInMillis() < System.currentTimeMillis())
        {
            vCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        System.out.println("getScheduleTime() returned " + vCalendar.getTime().toString());
        return vCalendar.getTime();
    }

    static public long getPeriod()
    {
        return Constants.DAYS;
        // return Constants.MINUTES * 2;
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
            int deleted = vQuerySession.cleanDb(session);

            //
            // int cnt = 0;
            // for (int i = 0; i < 10; ++i)
            // {
            // CallRecordQuerySession vQuerySession = vQueryHome.create();
            // int deleted = vQuerySession.cleanDb(con, kCleanPerSession);
            // if (deleted != kCleanPerSession)
            // {
            // vQuerySession.remove();
            // cnt += deleted;
            // break;
            // }
            // vQuerySession.remove();
            // cnt += kCleanPerSession;
            // Thread.sleep(10000);
            // }
            System.out.println("DB clean deleted " + deleted + " call records.");
        }
        catch (Exception e)
        {
            System.out.println("DbCleanTimerTask ; lookup failed: " + obj);
            e.printStackTrace();
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
                System.out.println("Error in Mailer: SQL connection could not be closed.");
            }
        }
    }

}
