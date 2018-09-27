package be.tba.pbx;

import java.text.DecimalFormat;

import javax.naming.NamingException;

import be.tba.util.constants.Constants;
import be.tba.util.excel.FintroXlsxReader;
import be.tba.util.invoice.IBANCheckDigit;
import be.tba.util.session.AccountCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* run it as follows:
 *
 * on our server:
 * java -classpath %jetty_base%\lib\tba.jar;%jetty_base%\lib\ext\comm.jar; be.tba.pbx.PbxLogPoller
 * java -classpath %jetty_base%\lib\tba.jar;%jetty_base%\lib\ext\jssc.jar;%jetty_base%\lib\ext\mysql-connector-java-5.1.34-bin.jar be.tba.pbx.PbxLogPoller
 *
 * logback.xml must be on classpath (explicit listed)
 */
public class PbxLogPoller
{
    static private CallLogDbWriter mLogWriter = null;
    final static Logger sLogger = LoggerFactory.getLogger(PbxLogPoller.class);

    static private CallLogDbWriter getWriter() throws NamingException
    {
        if (mLogWriter == null)
        {
            sLogger.info("PbxLogPoller.getWriter(): set properties");
            // yves System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
            // "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
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
            sLogger.info("");
            sLogger.info("########## new start ############");

            //FintroXlsxReader fintroXlsxReader = new FintroXlsxReader("C:\\Users\\ywillems\\Downloads\\BE71143070729269-20180629.xlsx");
            
            String[] elements = { 
                    "N-1809nr487",
                    "N-1809nr488",
                    "N-1809nr489",
                    "N-1809nr490",
                    "N-1809nr491",
                    "N-1809nr492",
                    "N-1809nr493",
                    "N-1809nr494"};   
            for (String s: elements) 
            {           
                    //Do your stuff here
                System.out.println(s + " gives " + IBANCheckDigit.IBAN_CHECK_DIGIT.calculateOGM(s)); 
            }
            
            
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            CallLogDbWriter vLogWriter = getWriter();
            vLogWriter.start();
            sLogger.info("from PbxLogPoller: CallLogDbWriter started!");
            
            
//            WebSession session  = new WebSession(Constants.MYSQL_URL);
//            InvoiceHelper vHelper = new InvoiceHelper(null, "409003", 10, 2017);
//            vHelper.storeOrUpdate(session);
//            vHelper.setFileName();
//            vHelper.generatePdfInvoice();           
            
            for (;;)
                Thread.sleep(10000);

        }
        catch (InterruptedException e)
        {
            // e.printStackTrace();
            sLogger.error("InterruptedException when creating calllog thread", e);
        }
        catch (NoClassDefFoundError e)
        {
            // e.printStackTrace();
            sLogger.error("NoClassDefFoundError when creating calllog thread", e);
        }
        catch (NamingException e)
        {
            // e.printStackTrace();
            sLogger.error("NamingException when getting InitialContext", e);
        }
        catch (InstantiationException e)
        {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            sLogger.error("InstantiationException when getting InitialContext", e);
        }
        catch (IllegalAccessException e)
        {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            sLogger.error("IllegalAccessException when getting InitialContext", e);
        }
        catch (ClassNotFoundException e)
        {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            sLogger.error("ClassNotFoundException when getting InitialContext", e);
        }
//        catch (SQLException e)
//        {
            // TODO Auto-generated catch block
            // e.printStackTrace();
//            sLogger.error("SQLExeption when getting InitialContext", e);
//        }
    }

    public void stop()
    {
        try
        {
            sLogger.info("Destroy CallLogDbWriter");
            CallLogDbWriter vLogWriter = getWriter();
            vLogWriter.stopLogging();
            Thread.sleep(vLogWriter.getSleepTime()); // give callLogThread
            // time to stop itsself
            vLogWriter = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            sLogger.error("Error during CallLogServlet destroy.");
        }
    }
}
