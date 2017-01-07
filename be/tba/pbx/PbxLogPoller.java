package be.tba.pbx;

import java.sql.DriverManager;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import be.tba.util.constants.Constants;
import be.tba.util.session.AccountCache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/* run it as follows:
 *
 * on our server:
 * java -classpath %jetty_base%\lib\tba.jar;%jetty_base%\lib\ext\comm.jar; be.tba.pbx.PbxLogPoller
 * java -classpath %jetty_base%\lib\tba.jar;%jetty_base%\lib\ext\jssc.jar;%jetty_base%\lib\ext\mysql-connector-java-5.1.34-bin.jar be.tba.pbx.PbxLogPoller
 *
 */
public class PbxLogPoller
{
   static private CallLogDbWriter mLogWriter = null;

   static private CallLogDbWriter getWriter() throws NamingException
   {
      if (mLogWriter == null)
      {
         System.out.println("PbxLogPoller.getWriter(): set properties");
         //yves System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
         // create the cash without the container support
         AccountCache.getInstance(Constants.MYSQL_URL);
         mLogWriter = new CallLogDbWriter();
      }
      return mLogWriter;
   }

   static public void main(String[] argv)
   {
      try
      {
          Class.forName("com.mysql.jdbc.Driver").newInstance();

    	  CallLogDbWriter vLogWriter = getWriter();
         vLogWriter.start();
         System.out.println("from PbxLogPoller: CallLogDbWriter started!");
         for (;;)
            Thread.sleep(10000);

      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
      catch (NoClassDefFoundError e)
      {
         e.printStackTrace();
         System.out.println("NoClassDefFoundError when creating calllog thread");
      }
      catch (NamingException e)
      {
         e.printStackTrace();
         System.out.println("NamingException when getting InitialContext");
      } catch (InstantiationException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalAccessException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }

   public void stop()
   {
      try
      {
         System.out.println("Destroy CallLogDbWriter");
         CallLogDbWriter vLogWriter = getWriter();
         vLogWriter.stopLogging();
         Thread.sleep(vLogWriter.getSleepTime()); // give callLogThread
         // time to stop itsself
         vLogWriter = null;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.out.println("Error during CallLogServlet destroy.");
      }
   }
}
