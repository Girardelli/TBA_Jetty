package be.tba.pbx;

import java.sql.Connection;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;

public final class CallLogDbWriter extends JsscSerial64ReaderThread
{
   public CallLogDbWriter()
   {
      super();
      this.setFileDir(Constants.CALLLOG_PATH);
      this.setFileScope("day");
      System.out.println("Path set to " + Constants.CALLLOG_PATH);
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
      }
      catch (Exception e)
      {
         System.out.println("Failed to store the following call record:");
         System.out.println(record.getFileRecord());
         e.printStackTrace();
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
