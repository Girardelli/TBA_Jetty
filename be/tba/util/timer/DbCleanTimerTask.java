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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.servlets.session.WebSession;
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
	public TimerTask getTimerTask() {
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
            log.info("DB clean deleted " + deleted + " call records.");
        }
        catch (Exception e)
        {
            log.info("DbCleanTimerTask ; lookup failed: " + obj);
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
