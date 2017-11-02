package be.tba.pbx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;

public final class CallLogDbWriter extends JsscSerial64ReaderThread
{
   final static Logger sLogger = LoggerFactory.getLogger(CallLogDbWriter.class);

   public CallLogDbWriter()
   {
      super();
      this.setFileDir(Constants.CALLLOG_PATH);
      this.setFileScope("day");
      sLogger.info("Path set to {}", Constants.CALLLOG_PATH);
   }

   protected void writeToDb(Forum700CallRecord record)
   {
      WebSession session = null;
      try
      {
		  session = new WebSession(Constants.MYSQL_URL);
          CallRecordSqlAdapter callRecordSqlAdapter = new CallRecordSqlAdapter();

         // Check the record and add it if it is a valid one.
         callRecordSqlAdapter.addCallRecord(session, record);
		 sLogger.info("new call added");
      }
      catch (Exception e)
      {
         sLogger.error("Failed to store the following call record:");
         sLogger.error(record.getFileRecord());
         sLogger.error("", e);
      }
      finally
      {
         if (session != null)
         {
            session.Close();
         }
      }
   }
}
