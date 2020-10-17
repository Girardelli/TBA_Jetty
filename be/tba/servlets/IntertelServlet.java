package be.tba.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.session.IntertelCallManager;
import be.tba.session.WebSession;
import be.tba.sqladapters.CallRecordSqlAdapter;
import be.tba.util.constants.Constants;
import be.tba.util.data.IntertelCallData;
import be.tba.websockets.TbaWebSocketAdapter;
import be.tba.websockets.WebSocketData;

public class IntertelServlet extends HttpServlet
{
   // final static Logger sLogger = LoggerFactory.getLogger(IntertelServlet.class);
   private static Logger log = LoggerFactory.getLogger(IntertelServlet.class);

   private WebSession mSession;
   private IntertelCallManager mIntertelCallManager;
   private CallRecordSqlAdapter mCallRecordSqlAdapter;
   private static String kFileScope = "day"; // month, week, day

   public IntertelServlet()
   {
      log.info("IntertelServlet started");

      mCallRecordSqlAdapter = new CallRecordSqlAdapter();
      mSession = new WebSession(Constants.MYSQL_URL);
      mIntertelCallManager = IntertelCallManager.getInstance();
   }

   /**
    * 
    */
   private static final long serialVersionUID = -671358136484886007L;

   /**
    * 
    * C:\java\curl\curl-7.64.0-win64-mingw\bin\curl -d
    * "param1=value1&param2=value2" -X POST http://localhost:8080/tba/intertel
    *
    * C:\java\curl\curl-7.64.0-win64-mingw\bin\curl -d
    * "param1=value1&param2=value2" -X POST
    * https://thebusinessassistant.be/tba/intertel
    * 
    */
   public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      // log.info("Intertel servlet doPost");
      // writeParmsToFile(req);
      String phase = req.getParameter("origin");
      // sLogger.info("Intertel servlet doPost");
      log.trace("Intertel servlet doPost: " + phase);
      IntertelCallData data;

      if (mSession == null || mSession.getConnection() == null)
      {
         try
         {
            mSession = new WebSession(Constants.MYSQL_URL);
         }
         catch (Exception e)
         {
            log.error("no connection available and could not be recovered");
            log.error(e.getMessage(), e);
            return;
         }
      }

      String calledNr = req.getParameter("phone_number_to");
      String callingNr = req.getParameter("phone_number_from");
      String intertelCallId = req.getParameter("call_id");
      String inOut = req.getParameter("inout");

      if (calledNr == null || callingNr == null || intertelCallId == null || phase == null || inOut == null)
      {
         log.info("Intertel servlet: calledNr:" + calledNr + " callingNr:" + callingNr + " intertelCallId:" + intertelCallId + " phase:" + phase + " inOut:" + inOut);
         return;
      }
      boolean isIncoming = (inOut.equals("IN"));
      long timestamp = Long.parseLong(req.getParameter("timestamp"));

      data = mIntertelCallManager.get(intertelCallId);
      if (data == null && !phase.equals("start"))
      {
         log.warn("Intertel event with call ID not found: " + intertelCallId);
      }
      switch (phase)
      {
      case "start":
         checkDbConnection();
         // Check the record and add it if it is a valid one.
         data = new IntertelCallData(isIncoming, calledNr, callingNr, intertelCallId, timestamp, phase);
         mIntertelCallManager.newCall(data);
         data.setDbRecordId(mCallRecordSqlAdapter.addIntertelCall(mSession, data));
         if (data.isIncoming)
         {
            TbaWebSocketAdapter.broadcast(new WebSocketData(WebSocketData.NEW_CALL, timestamp, data));
         }
         else
         {
            // ********************************************
            // logic to cope with the fact that the call park feature does not behave as
            // expected
            // ---------------------------------------------
            // check whether there is an incoming call answered with an answerBy id equal to
            // the phone_number_from of this outgoing call.
            // In that case I conclude that the incoming call being transferred to an
            // outside number.
            // Result:
            // - make sure that the calling party is the initially called customer
            // - cope with the buggy end and summary events of the transfered call because
            // they carry the ID of the incomming call
            // (!!! call log of a transfered call appended at the bottom of this source
            // file)
            String tempCallingNr = data.callingNr;
            data.callingNr = callingNr; // reset this to the original calling Party id to check for the telephone id
            IntertelCallData transferedCall = mIntertelCallManager.getTransferCall_CallParkBugs(data);
            if (transferedCall != null)
            {
               // conclude that this is a transfered call. Treat it as such because the
               // Intertel bugs will not help you
               // When the transfered call is not answered, there will be no 'transfer' event.
               // But in stead both calls shall be finished properly
               data.callTransferLink = transferedCall;
               transferedCall.callTransferLink = data;
               data.callingNr = transferedCall.calledNr;
               mCallRecordSqlAdapter.setCallingNr(mSession, data);
//    		      log.info("transfered call: start");
//               log.info(transferedCall);
//               log.info(data);
            }
            data.callingNr = tempCallingNr;

            writeToFile(data);
         }
         break;

      case "answer":
         if (data != null)
         {
            data.setTsAnswer(timestamp);
            data.setCurrentPhase(phase);
            data.setAnsweredBy(req.getParameter("answerby"));
            mCallRecordSqlAdapter.setTsAnswer(mSession, data);
            if (data.isIncoming)
            {
               TbaWebSocketAdapter.broadcast(new WebSocketData(WebSocketData.CALL_ANSWERED, timestamp, data));
            }
            // log.info(data.intertelCallId + "-answered");
         }
         break;

      case "transfer":
         if (data != null)
         {
            // transfer called party answers
            IntertelCallData transferOutCall = mIntertelCallManager.getTransferCall(data, calledNr, IntertelCallData.kTbaNr);
            data.setTsTransfer(timestamp);
            data.setCurrentPhase(phase);
            if (transferOutCall != null)
            {
               transferOutCall.setIsTransfer();
               data.callTransferLink = transferOutCall;
               transferOutCall.callTransferLink = data;
               transferOutCall.callingNr = data.calledNr;
               transferOutCall.customer = data.customer;
               data.setTsEnd(timestamp);
               mCallRecordSqlAdapter.setTransfer(mSession, data, transferOutCall);
            }
            // log.info(data.intertelCallId + "-transfered");

         }
         break;

      case "end":
         if (data != null)
         {
            // check for the callPark buggy behaviour
            /*
             * if (data.callParkBug_transferLink != null && data.tsEnd != 0) { // process
             * this event on the outgoing call data.callParkBug_transferLink.callingNr =
             * data.calledNr; data = data.callParkBug_transferLink;
             * mCallRecordSqlAdapter.setForwardCallFlag(mSession,
             * data.callParkBug_transferLink); }
             */

            /*
             * if (data.callTransferLink != null && data.callTransferLink.isTransferOutCall)
             * { // also end this one as this event is actually the end of the transfered
             * call data.callTransferLink.setTsEnd(timestamp);
             * mCallRecordSqlAdapter.setTsEnd(mSession, data.callTransferLink); } else {
             * data.setTsEnd(timestamp); mCallRecordSqlAdapter.setTsEnd(mSession, data); }
             */
            data.setCurrentPhase(phase);
            data.isEndDone = true;
            // log.info(data.intertelCallId + "-end");
            if (data.isIncoming && !data.isWsRemoved)
            {
               TbaWebSocketAdapter.broadcast(new WebSocketData(WebSocketData.CALL_ANSWERED, timestamp, data));
               data.isWsRemoved = true;
            }
         }
         break;

      case "summary":
         if (data != null)
         {
            /*
             * if (data.callParkBug_transferLink != null && data.isSummaryDone) { // process
             * this event on the outgoing call data = data.callParkBug_transferLink; }
             */
            data.isSummaryDone = true;
            if (data.callTransferLink != null && data.callTransferLink.isTransferOutCall)
            {
               // also end this one as this event is actually the end of the transfered call
               data = data.callTransferLink;
            }
            else if (!data.isIncoming && data.tsAnswer == 0)
            {
               // regular outgoing call not answered
               data.setCallingNr(req.getParameter("viaDID"));
            }
            data.setTsEnd(timestamp);
            mCallRecordSqlAdapter.setTsEnd(mSession, data);
         }
         break;

      default:
         log.warn("Intertel servlet doPost: unknown 'origin'=" + phase);

      }
      /*
       * if (data == null) { //log.info("INtertel call cannot be found: data=null");
       * return; } else if (data.isEndDone && data.isSummaryDone) { // do not remove
       * after receiving the end events. We need this call from teh manager to further
       * process it. //log.info("Remove call: " + data.toString());
       * //mIntertelCallManager.removeCall(mSession, data); writeToFile(data);
       * //log.info(data.intertelCallId + "-finalize with write to log"); }
       */
   }

   private void writeParmsToFile(HttpServletRequest req) throws IOException
   {
      File file = new File(Constants.INTERTELL_CALLLOG_PATH, "parms.txt");
      if (!file.exists())
      {
         file.createNewFile();
      }
      FileOutputStream fileStream = new FileOutputStream(file, true);

      Enumeration<String> dataNames = req.getParameterNames();
      StringBuffer strBuf = new StringBuffer();

      while (dataNames.hasMoreElements())
      {
         String parm = (String) dataNames.nextElement();
         strBuf.append(parm + ": " + req.getParameter(parm) + "\r\n");
      }
      strBuf.append("---------------------------------------\r\n");
      fileStream.write(strBuf.toString().getBytes());
      fileStream.close();
   }

   private void writeToFile(IntertelCallData data) throws IOException
   {
      Calendar calendar = Calendar.getInstance();

      int year = calendar.get(Calendar.YEAR);
      int month = calendar.get(Calendar.MONTH) + 1;
      String fileName;

      if (kFileScope.equals("day"))
      {
         int day = calendar.get(Calendar.DAY_OF_MONTH);
         fileName = new String(year + "-" + month + "-" + day + ".log");
      }
      else if (kFileScope.equals("month"))
      {
         fileName = new String(year + "-" + month + ".log");
      }
      else // if (kFileScope.equals("week"))
      {
         int week = calendar.get(Calendar.WEEK_OF_YEAR);
         fileName = new String(year + "-" + week + ".log");
      }
      StringBuffer strBuf = new StringBuffer();
      File file = new File(Constants.INTERTELL_CALLLOG_PATH, fileName);
      if (!file.exists())
      {
         file.createNewFile();
         strBuf.append("datum    uur            van -->         naar\r\n");
      }
      FileOutputStream fileStream = new FileOutputStream(file, true);

      Calendar vToday = Calendar.getInstance();
      strBuf.append(String.format("%02d/%02d/%02d %02d:%02d %12s --> %12s\r\n", vToday.get(Calendar.DAY_OF_MONTH), vToday.get(Calendar.MONTH) + 1, vToday.get(Calendar.YEAR) - 2000, vToday.get(Calendar.HOUR_OF_DAY), vToday.get(Calendar.MINUTE), data.callingNr, data.calledNr));
      fileStream.write(strBuf.toString().getBytes());
      fileStream.close();
   }

   private boolean checkDbConnection()
   {
      try
      {
         Connection connection = mSession.getConnection();
         if (connection != null)
         {
            if (connection.isValid(2))
            {
               return true;
            }
            connection.close();
         }
         mSession = new WebSession(Constants.MYSQL_URL);
         return true;
      }
      catch (SQLException e)
      {
         // TODO Auto-generated catch block
         log.error(e.getMessage(), e);
      }
      return false;
   }

   public void destroy()
   {
      log.info("IntertelServlet destroyed.");
   }

}

