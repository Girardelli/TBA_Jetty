package be.tba.servlets.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.pbx.interfaces.CallRecordEntityData;
import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;
import be.tba.util.invoice.InvoiceHelper;
import be.tba.util.session.AccountCache;
import be.tba.util.timer.MailNowTask;

public class CallRecordFacade
{
   private static Lock lock = new ReentrantLock();

   public static void retrieveRecordForUpdate(HttpServletRequest req, WebSession session)
   {
      String vKey = (String) req.getParameter(Constants.RECORD_ID);

      CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
      session.setCurrentRecord(vQuerySession.getRecord(session, vKey));
   }

   public static void saveRecord(HttpServletRequest req, WebSession session)
   {
      CallRecordSqlAdapter vCallLogWriterSession = new CallRecordSqlAdapter();

      // Check the record and add it if it is a valid one.

      CallRecordEntityData vCallData = session.getCurrentRecord();
      if (vCallData == null)
      {
         System.out.println("AdminDispatchServlet: no call record in session context (SAVE RECORD)");
      }
      else
      {
         AccountEntityData vOldCustomer = AccountCache.getInstance().get(vCallData.getFwdNr());
         if (vOldCustomer.getHasSubCustomers() && req.getParameter(Constants.ACCOUNT_SUB_CUSTOMER) != null)
         {
            vCallData.setFwdNr(req.getParameter(Constants.ACCOUNT_SUB_CUSTOMER));
            System.out.println("Super customer call: set fwd number to " + vCallData.getFwdNr());
         }
         else
         {
            if (req.getParameter(Constants.ACCOUNT_FORWARD_NUMBER) != null)
            {
               if (!vCallData.getFwdNr().equals((String) req.getParameter(Constants.ACCOUNT_FORWARD_NUMBER)))
               {
                  // customer changed!! Check the isMailed flag.
                  vCallData.setFwdNr((String) req.getParameter(Constants.ACCOUNT_FORWARD_NUMBER));
                  AccountEntityData vNewCustomer = AccountCache.getInstance().get(vCallData.getFwdNr());

                  if (AccountCache.getInstance().isMailEnabled(vNewCustomer))
                     vCallData.setIsMailed(false);
                  else
                     vCallData.setIsMailed(true);
               }
            }
         }
         vCallData.setNumber((String) req.getParameter(Constants.RECORD_NUMBER));
         vCallData.setName((String) req.getParameter(Constants.RECORD_CALLER_NAME));
         vCallData.setShortDescription((String) req.getParameter(Constants.RECORD_SHORT_TEXT));
         vCallData.setLongDescription((String) req.getParameter(Constants.RECORD_LONG_TEXT));
         vCallData.setW3_CustomerId((String) req.getParameter(Constants.RECORD_3W_CUSTOMER_ID));
         if (req.getParameter(Constants.RECORD_DIR) != null)
            vCallData.setIsIncomingCall(((String) req.getParameter(Constants.RECORD_DIR)).equals(Constants.RECORD_DIR_IN));
         vCallData.setIsSmsCall(req.getParameter(Constants.RECORD_SMS) != null);
         vCallData.setIsAgendaCall(req.getParameter(Constants.RECORD_AGENDA) != null);
         vCallData.setIsForwardCall(req.getParameter(Constants.RECORD_FORWARD) != null);
         boolean prevIsImportant = vCallData.getIsImportantCall();
         vCallData.setIsImportantCall(req.getParameter(Constants.RECORD_IMPORTANT) != null);
         if (!prevIsImportant && vCallData.getIsImportantCall())
         {
            MailNowTask.send(vCallData.getFwdNr());
         }
         vCallData.setIsFaxCall(req.getParameter(Constants.RECORD_FAX) != null);
         if (req.getParameter(Constants.RECORD_INVOICE_LEVEL) != null)
         {
            if (vOldCustomer.getInvoiceType() == InvoiceHelper.kTelemarketingInvoice)
            {
               String vLevel = (String) req.getParameter(Constants.RECORD_INVOICE_LEVEL);
               if (vLevel.equals(Constants.RECORD_LEVEL3))
                  vCallData.setInvoiceLevel(InvoiceHelper.kLevel3);
               else if (vLevel.equals(Constants.RECORD_LEVEL2))
                  vCallData.setInvoiceLevel(InvoiceHelper.kLevel2);
               else
                  vCallData.setInvoiceLevel(InvoiceHelper.kLevel1);
            }
            else
               vCallData.setInvoiceLevel(InvoiceHelper.kLevel1);
         }

         if (vOldCustomer.getIs3W())
            vCallData.setIs3W_call(true);
         else
            vCallData.setIs3W_call(false);
         vCallData.setIsVirgin(false);
         vCallData.setIsNotLogged(false);
         vCallData.setDoneBy(session.getUserId());
         vCallLogWriterSession.setCallData(session, vCallData);
         printCallInsert(session, vCallData);
      }
   }

   public static void saveManualRecord(HttpServletRequest req, WebSession session)
   {
      CallRecordEntityData newRecord = new CallRecordEntityData();
      Calendar vCalendar = Calendar.getInstance();
      newRecord.setIsNotLogged(false);
      newRecord.setIsReleased(false);
      newRecord.setIsVirgin(false);

      newRecord.setIsAgendaCall(req.getParameter(Constants.RECORD_AGENDA) != null);
      newRecord.setIsSmsCall(req.getParameter(Constants.RECORD_SMS) != null);
      newRecord.setIsForwardCall(req.getParameter(Constants.RECORD_FORWARD) != null);
      newRecord.setIsImportantCall(req.getParameter(Constants.RECORD_IMPORTANT) != null);
      newRecord.setIsFaxCall(req.getParameter(Constants.RECORD_FAX) != null);
      newRecord.setIsIncomingCall(((String) req.getParameter(Constants.RECORD_DIR)).equals(Constants.RECORD_DIR_IN));

      newRecord.setFwdNr((String) req.getParameter(Constants.ACCOUNT_FORWARD_NUMBER));

      newRecord.setW3_CustomerId((String) req.getParameter(Constants.RECORD_3W_CUSTOMER_ID));
      newRecord.setDate((String) req.getParameter(Constants.RECORD_DATE));
      newRecord.setTime((String) req.getParameter(Constants.RECORD_TIME));
      newRecord.setNumber((String) req.getParameter(Constants.RECORD_NUMBER));
      newRecord.setName((String) req.getParameter(Constants.RECORD_CALLER_NAME));
      newRecord.setShortDescription((String) req.getParameter(Constants.RECORD_SHORT_TEXT));
      newRecord.setLongDescription((String) req.getParameter(Constants.RECORD_LONG_TEXT));
      newRecord.setCost("00:01:10");
      newRecord.setTimeStamp(vCalendar.getTimeInMillis());
      newRecord.setDoneBy(session.getUserId());

      AccountEntityData vData = AccountCache.getInstance().get(newRecord.getFwdNr());
      if (vData != null && vData.getIs3W())
         newRecord.setIs3W_call(true);
      else
         newRecord.setIs3W_call(false);

      if (AccountCache.getInstance().isMailEnabled(vData))
      {
         newRecord.setIsMailed(false);
      }
      else
         newRecord.setIsMailed(true);

      if (req.getParameter(Constants.RECORD_INVOICE_LEVEL) != null)
      {
         if (vData.getInvoiceType() == InvoiceHelper.kTelemarketingInvoice)
         {
            String vLevel = (String) req.getParameter(Constants.RECORD_INVOICE_LEVEL);
            if (vLevel.equals(Constants.RECORD_LEVEL3))
               newRecord.setInvoiceLevel(InvoiceHelper.kLevel3);
            else if (vLevel.equals(Constants.RECORD_LEVEL2))
               newRecord.setInvoiceLevel(InvoiceHelper.kLevel2);
            else
               newRecord.setInvoiceLevel(InvoiceHelper.kLevel1);
         }
         else
            newRecord.setInvoiceLevel(InvoiceHelper.kLevel1);
      }
      CallRecordSqlAdapter.setIsDocumentedFlag(newRecord);
      if (newRecord.getIsImportantCall())
      {
		  MailNowTask.send(newRecord.getFwdNr());
	  }

      // Check the record and add it if it is a valid one.
      System.out.println("saveManualRecord: id=" + newRecord.getId() + ", cust=" + newRecord.getFwdNr() + ", number=" + newRecord.getNumber());
      CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
      vQuerySession.addRow(session.getConnection(), newRecord);
      printCallInsert(session, newRecord);
   }

   public static void deleteRecords(HttpServletRequest req, WebSession session)
   {
      String vLtd = (String) req.getParameter(Constants.RECORD_TO_DELETE);
      System.out.println("record to delete list: " + vLtd);
      if (vLtd != null && vLtd.length() > 0)
      {
         StringTokenizer vStrTok = new StringTokenizer(vLtd, ",");
         CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();

         while (vStrTok.hasMoreTokens())
         {
            int key = Integer.parseInt(vStrTok.nextToken());
            vQuerySession.deleteRow(session.getConnection(), key);
            printCallDelete(key);
         }
      }
   }

   public static void newCall(HttpServletRequest req, WebSession webSession)
   {
      HttpSession vHttpSession = req.getSession();

      Map<Integer, CallRecordEntityData> vNewCalls = webSession.getNewCalls();
      int i = 0;
      Integer key;
      do
      {
         key = new Integer(++i);
      } while (vNewCalls.containsKey(key));

      CallRecordEntityData newRecord = new CallRecordEntityData();
      newRecord.setIsNotLogged(false);
      newRecord.setIsReleased(false);
      newRecord.setIsAgendaCall(req.getParameter(Constants.RECORD_AGENDA) != null);
      newRecord.setIsSmsCall(req.getParameter(Constants.RECORD_SMS) != null);
      newRecord.setIsForwardCall(req.getParameter(Constants.RECORD_FORWARD) != null);
      newRecord.setIsImportantCall(req.getParameter(Constants.RECORD_IMPORTANT) != null);
      newRecord.setIsFaxCall(req.getParameter(Constants.RECORD_FAX) != null);
      newRecord.setName((String) req.getParameter(Constants.RECORD_CALLER_NAME));
      newRecord.setShortDescription((String) req.getParameter(Constants.RECORD_SHORT_TEXT));
      newRecord.setLongDescription((String) req.getParameter(Constants.RECORD_LONG_TEXT));

      vNewCalls.put(key, newRecord);

      vHttpSession.setAttribute("key", key);
   }

   public static void updateNewCall(HttpServletRequest req, WebSession webSession)
   {
      HttpSession vHttpSession = req.getSession();

      Map<Integer, CallRecordEntityData> vNewCalls = webSession.getNewCalls();
      Integer vKey = null;
      int i = 0;
      for (; i < vNewCalls.size(); ++i)
      {
         vKey = new Integer(i);
         if (vNewCalls.containsKey(vKey))
            break;
      }

      System.out.println("saveNewCall: local key = " + vKey);

      if (i < vNewCalls.size())
      {
         CallRecordEntityData newRecord = (CallRecordEntityData) vNewCalls.get(vKey);
         newRecord.setIsNotLogged(false);
         newRecord.setIsReleased(false);
         newRecord.setIsAgendaCall(req.getParameter(Constants.RECORD_AGENDA) != null);
         newRecord.setIsSmsCall(req.getParameter(Constants.RECORD_SMS) != null);
         newRecord.setIsForwardCall(req.getParameter(Constants.RECORD_FORWARD) != null);
         boolean prevIsImportant = newRecord.getIsImportantCall();
         newRecord.setIsImportantCall(req.getParameter(Constants.RECORD_IMPORTANT) != null);
         if (!prevIsImportant && newRecord.getIsImportantCall())
         {
            MailNowTask.send(newRecord.getFwdNr());
         }
         newRecord.setIsFaxCall(req.getParameter(Constants.RECORD_FAX) != null);
         newRecord.setName((String) req.getParameter(Constants.RECORD_CALLER_NAME));
         newRecord.setShortDescription((String) req.getParameter(Constants.RECORD_SHORT_TEXT));
         newRecord.setLongDescription((String) req.getParameter(Constants.RECORD_LONG_TEXT));
         vHttpSession.setAttribute("key", vKey);
      }
   }

   public static void saveNewCall(HttpServletRequest req, WebSession webSession)
   {
      String vKey = (String) req.getParameter(Constants.RECORD_ID);

      System.out.println("saveNewCall: id = " + vKey);

      CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
      CallRecordEntityData vNewRecord = vQuerySession.getRecord(webSession, vKey);

      if (vNewRecord != null)
      {
         HttpSession vHttpSession = req.getSession();

         Map<Integer, CallRecordEntityData> vNewCalls = webSession.getNewCalls();
         Integer vLocalKey = (Integer) vHttpSession.getAttribute(new String("key"));

         CallRecordEntityData vLocalRecord = (CallRecordEntityData) vNewCalls.remove(vLocalKey);
         if (vLocalRecord != null)
         {
            CallRecordSqlAdapter vCallLogWriterSession = new CallRecordSqlAdapter();

            vNewRecord.setIsMailed(false);
            vNewRecord.setName((String) req.getParameter(Constants.RECORD_CALLER_NAME));
            vNewRecord.setShortDescription((String) req.getParameter(Constants.RECORD_SHORT_TEXT));
            vNewRecord.setLongDescription((String) req.getParameter(Constants.RECORD_LONG_TEXT));
            vNewRecord.setIsSmsCall(req.getParameter(Constants.RECORD_SMS) != null);
            vNewRecord.setIsAgendaCall(req.getParameter(Constants.RECORD_AGENDA) != null);
            vNewRecord.setIsForwardCall(req.getParameter(Constants.RECORD_FORWARD) != null);
            boolean prevIsImportant = vNewRecord.getIsImportantCall();
            vNewRecord.setIsImportantCall(req.getParameter(Constants.RECORD_IMPORTANT) != null);
            if (!prevIsImportant && vNewRecord.getIsImportantCall())
            {
               MailNowTask.send(vNewRecord.getFwdNr());
            }
            vNewRecord.setIsFaxCall(req.getParameter(Constants.RECORD_FAX) != null);
            vNewRecord.setIsVirgin(false);
            vNewRecord.setIsNotLogged(false);

            System.out.println("saveNewCall: id = " + vNewRecord.getId());

            vCallLogWriterSession.setCallData(webSession, vNewRecord);
         }
      }
   }

   public static void removeNewCall(HttpServletRequest req, WebSession webSession)
   {
      HttpSession vHttpSession = req.getSession();

      Map<Integer, CallRecordEntityData> vNewCalls = webSession.getNewCalls();
      Integer vKey = (Integer) vHttpSession.getAttribute(new String("key"));
      vNewCalls.remove(vKey);
   }

   private static void printCallDelete(int key)
   {
      lock.lock();
      try
      {
         FileOutputStream logStream = getCallLogFileStream();
         logStream.write(CallRecordEntityData.toSqlDeleteString(key).getBytes());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return;
      }
      finally
      {
         lock.unlock();
      }
   }

   // private static void printCallUpdate(CallRecordEntityData record)
   // {
   // lock.lock();
   // try
   // {
   // FileOutputStream logStream = getCallLogFileStream();
   // logStream.write(CallRecordEntityData.toSqlUpdateString(record).getBytes());
   // }
   // catch (Exception e)
   // {
   // e.printStackTrace();
   // return;
   // }
   // finally
   // {
   // lock.unlock();
   // }
   // }

   private static void printCallInsert(WebSession session, CallRecordEntityData record)
   {
      lock.lock();
      try
      {
         FileOutputStream logStream = getCallLogFileStream();
         logStream.write(CallRecordEntityData.toSqlInsertString(record).getBytes());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return;
      }
      finally
      {
         lock.unlock();
      }
   }

   private static FileOutputStream getCallLogFileStream() throws IOException
   {
      Calendar calendar = Calendar.getInstance();

      int year = calendar.get(Calendar.YEAR);
      int month = calendar.get(Calendar.MONTH) + 1;
      int day = calendar.get(Calendar.DAY_OF_MONTH);
      String fileName = new String(Constants.RECORDS_OF_TODAY_PATH + year + "-" + month + "-" + day + ".sql");

      File file = new File(fileName);
      FileOutputStream fileStream = null;
      if (!file.exists())
      {
         file.createNewFile();
      }
      fileStream = new FileOutputStream(file, true);
      return fileStream;
   }

}
