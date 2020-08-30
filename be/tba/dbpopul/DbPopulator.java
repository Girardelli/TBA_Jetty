package be.tba.dbpopul;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Calendar;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.ejb.pbx.interfaces.CallRecordEntityData;
import be.tba.test.IntertelCallInjector;

/* run it as follows:
 *
 * on our server:
 * java -classpath C:\jboss-4.0.2\server\default\deploy\tba.jar;C:\jboss-4.0.2\client\jboss-common-client.jar;C:\jboss-4.0.2\client\jnp-client.jar;C:\jboss-4.0.2\client\jbossall-client.jar be.tba.pbx.PbxLogPoller
 *
 * on my laptop:
 * java -classpath C:\jboss-4.0.2\server\default\deploy\tba.jar;C:\jboss-4.0.2\client\jboss-common-client.jar;C:\jboss-4.0.2\client\jnp-client.jar;C:\jboss-4.0.2\client\jbossall-client.jar be.tba.dbpopul.DbPopulator
 */
public class DbPopulator
{
	private static Logger log = LoggerFactory.getLogger(DbPopulator.class);
	
    static public void main(String[] argv)
    {
        try
        {
            if (argv.length != 1)
            {
                log.info("Only one argument can be used.");
                return;
            }
            log.info("Opening file " + argv[0] + " for parsing");
            BufferedReader vBuffer = new BufferedReader(new FileReader(argv[0]));

            // log.info("Opening file " + argv[1] + " for writing");
            // PrintWriter vWriter = new PrintWriter(new FileWriter(argv[1]));

            // InitialContext vContext = new InitialContext();
            // CallLogWriterSessionHome vWriterHome = (CallLogWriterSessionHome)
            // vContext.lookup(EjbJndiNames.EJB_JNDI_CALL_LOG_WRITER_SESSION);
            // CallLogWriterSession vWriterSession = vWriterHome.create();

            Calendar vCalendar = Calendar.getInstance();
            // int vYear = vCalendar.get(Calendar.YEAR);

            vCalendar.set(Calendar.MILLISECOND, 0);
            vCalendar.set(Calendar.SECOND, 0);

            int vCnt = 0;
            String vLine;
            while ((vLine = vBuffer.readLine()) != null)
            {
                if (vLine.startsWith("#"))
                    continue;
                ++vCnt;
                StringTokenizer vTokenizer = new StringTokenizer(vLine, "`");

                String[] vTokenArr = new String[9];
                for (int i = 0; i < 9; ++i)
                    vTokenArr[i] = "";
                int vTokCnt = 0;

                CallRecordEntityData vRecord = new CallRecordEntityData();

                while (vTokenizer.hasMoreTokens())
                {
                    vTokenArr[vTokCnt] = vTokenizer.nextToken();
                    vTokenArr[vTokCnt] = vTokenArr[vTokCnt].trim();
                    // log.info(vTokenArr[vTokCnt] + "**");
                    vTokCnt++;
                }

                // vCalendar.clear();

                StringTokenizer vDateTokenizer = new StringTokenizer(vTokenArr[4], "/");
                String vToken = vDateTokenizer.nextToken();
                vToken = vToken.trim();
                int vDay = Integer.parseInt(vToken);
                // vCalendar.set(Calendar.DAY_OF_MONTH,
                // Integer.parseInt(vToken));
                vToken = vDateTokenizer.nextToken();
                vToken = vToken.trim();
                int vMonth = Integer.parseInt(vToken) - 1;
                // vCalendar.set(Calendar.MONTH, Integer.parseInt(vToken) - 1);
                vToken = vDateTokenizer.nextToken();
                vToken = vToken.trim();
                int vYear = 2000 + Integer.parseInt(vToken);
                // vCalendar.set(Calendar.YEAR, Integer.parseInt(vToken));

                vDateTokenizer = new StringTokenizer(vTokenArr[5], ":");
                vToken = vDateTokenizer.nextToken();
                vToken = vToken.trim();
                int vHour = Integer.parseInt(vToken);
                // vCalendar.set(Calendar.HOUR_OF_DAY,
                // Integer.parseInt(vToken));
                vToken = vDateTokenizer.nextToken();
                vToken = vToken.trim();
                int vMinute = Integer.parseInt(vToken);
                // vCalendar.set(Calendar.MINUTE, Integer.parseInt(vToken));
                vCalendar.set(vYear, vMonth, vDay, vHour, vMinute);

                log.info(vCalendar.toString());

                log.info("---------------------------");

                if (vTokenArr[0].equals("in"))
                    vRecord.setIsIncomingCall(true);
                else
                    vRecord.setIsIncomingCall(false);

                if (vTokenArr[1].indexOf('d') != -1)
                    vRecord.setIsForwardCall(true);
                else
                    vRecord.setIsForwardCall(false);
                if (vTokenArr[1].indexOf('a') != -1)
                    vRecord.setIsAgendaCall(true);
                else
                    vRecord.setIsAgendaCall(false);
                if (vTokenArr[1].indexOf('i') != -1)
                    vRecord.setIsImportantCall(true);
                else
                    vRecord.setIsImportantCall(false);

                vRecord.setFwdNr(vTokenArr[2]);
                vRecord.setDate(vTokenArr[4]);
                vRecord.setTime(vTokenArr[5]);
                vRecord.setTimeStamp(vCalendar.getTimeInMillis());
                vRecord.setNumber(vTokenArr[6]);
                vRecord.setName(vTokenArr[7]);
                vRecord.setShortDescription(vTokenArr[8]);
                vRecord.setLongDescription("");
                vRecord.setCost("");
                vRecord.setIsDocumented(true);
                vRecord.setIsSmsCall(false);
                vRecord.setIsMailed(true);
                vRecord.setInvoiceLevel((short) 0);

                log.info(vRecord.toString());
                // vWriterSession.createCallRecord(vRecord);
                // log.info();
                /*
                 * vWriter.print(vTokenArr[0] + "`"); vWriter.print(vTokenArr[8] + "`");
                 * vWriter.print(vTokenArr[1] + "`"); vWriter.print(vTokenArr[2] + "`");
                 * vWriter.print(vTokenArr[3] + "`"); vWriter.print(vTokenArr[4] + "`");
                 * vWriter.print(vTokenArr[6] + "`"); vWriter.print(vTokenArr[7] + "`");
                 * vWriter.println(vTokenArr[5]);
                 */
            }
            log.info("Finished parsing. " + vCnt + " entries added");
            // vWriter.close();
            vBuffer.close();
            // vWriterSession.remove();

        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);

        }
    }

}
