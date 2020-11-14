package be.tba.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.session.SessionParmsInf;
import be.tba.session.WebSession;
import be.tba.sqladapters.CallRecordSqlAdapter;
import be.tba.sqldata.AccountCache;
import be.tba.sqldata.AccountEntityData;
import be.tba.sqldata.CallRecordEntityData;
import be.tba.test.IntertelCallInjector;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.invoice.InvoiceHelper;
import be.tba.util.timer.NotifyCustomerTask;
import be.tba.websockets.WebSocketData;

public class CallBizzLogic
{
   private static Logger log = LoggerFactory.getLogger(CallBizzLogic.class);
   private static Lock lock = new ReentrantLock();

   public static void retrieveRecordForUpdate(SessionParmsInf parms, WebSession session)
   {
      String vKey = parms.getParameter(Constants.RECORD_ID);
      CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
      session.setCurrentRecord(vQuerySession.getRecord(session, vKey));
   }

   public static void updateCustomerChanges(SessionParmsInf parms, WebSession session, boolean isCustomer)
   {
      // log.info("updateCustomerChanges()");
      String vKey = parms.getParameter(Constants.RECORD_ID);
      String shortText = parms.getParameter(Constants.RECORD_SHORT_TEXT);
      boolean isArchived = (parms.getParameter(Constants.RECORD_ARCHIVED) != null);
      String urgent = parms.getParameter(Constants.RECORD_URGENT);
      boolean isCustomerAttentionNeeded = (urgent != null && !urgent.isBlank());
      CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
      vQuerySession.setShortText(session, Integer.parseInt(vKey), shortText, isCustomer, isArchived, isCustomerAttentionNeeded);
   }

   /*
    * this method saves (updates) a record that was opened from the call list.
    * 
    */
   public static void saveRecord(SessionParmsInf parms, WebSession session)
   {
      // Check the record and add it if it is a valid one.

      CallRecordEntityData vCallData = session.getCurrentRecord();
      if (vCallData == null)
      {
         log.info("AdminDispatchServlet: no call record in session context (SAVE RECORD), vCallData=" + vCallData);
         return;
      }
      AccountEntityData vNewCustomer = null;
      CallRecordSqlAdapter vCallLogWriterSession = new CallRecordSqlAdapter();
      AccountEntityData vOldCustomer = AccountCache.getInstance().get(vCallData);

      if (vOldCustomer != null && vOldCustomer.getHasSubCustomers() && parms.getParameter(Constants.ACCOUNT_SUB_CUSTOMER) != null)
      {
         //log.info("change to sub customer");
         // subcustomer has been selected to assign this call to
         vCallData.setAccountId(Integer.parseInt(parms.getParameter(Constants.ACCOUNT_SUB_CUSTOMER)));
         AccountEntityData newCustomer = AccountCache.getInstance().get(vCallData.getAccountId()); // take FwdNr because vCallDatat still has the previous accountId and shall make
         // that new FwdNr shall be skipped.
         vCallData.setFwdNr(newCustomer.getFwdNumber());
         // log.info("Super customer call: set fwd number to " + vCallData.getFwdNr() +
         // ", new account ID=" + newCustomer.getId());

         // redirect function can only be applied for subcustomers
         if (newCustomer.getRedirectAccountId() != 0)
         {
            // move this call to the redirect customer
            AccountEntityData redirectAccount = AccountCache.getInstance().get(newCustomer.getRedirectAccountId());
            vCallData.setFwdNr(redirectAccount.getFwdNumber());
            vCallData.setAccountId(redirectAccount.getRedirectAccountId());
            vCallData.setShortDescription("<p style=\"color:red\"><b><u>" + vOldCustomer.getFullName() + "</u></b></p>" + parms.getParameter(Constants.RECORD_SHORT_TEXT));
         }
         else
         {
            vCallData.setShortDescription(parms.getParameter(Constants.RECORD_SHORT_TEXT));
         }
      }
      else
      {
         //log.info("NO change to sub customer");
         vCallData.setShortDescription(parms.getParameter(Constants.RECORD_SHORT_TEXT));
      }
      String newCustIdStr = parms.getParameter(Constants.ACCOUNT_FORWARD_NUMBER);
      if (newCustIdStr != null && !newCustIdStr.isEmpty())
      {
         int newCustomerId = Integer.parseInt(newCustIdStr);
         if (newCustomerId > 0 && newCustomerId != vOldCustomer.getId())
         {
            log.info("ACCOUNT_FORWARD_NUMBER set=" + newCustomerId + ", old vCallData.getFwdNr=" + vOldCustomer.getId());
            // customer changed!! Check the isMailed flag.
            vCallData.setAccountId(newCustomerId);
            vNewCustomer = AccountCache.getInstance().get(newCustomerId);
            vCallData.setFwdNr(vNewCustomer.getFwdNumber());

            if (AccountCache.getInstance().isMailEnabled(vNewCustomer))
               vCallData.setIsMailed(false);
            else
               vCallData.setIsMailed(true);
         }
      }
      AccountEntityData customer = AccountCache.getInstance().get(vCallData);
      if (vCallData.getAccountId() == 0)
      {
         if (customer != null)
            vCallData.setAccountId(customer.getId());
      }
      vCallData.setNumber(parms.getParameter(Constants.RECORD_NUMBER));
      vCallData.setName(parms.getParameter(Constants.RECORD_CALLER_NAME));
      vCallData.setLongDescription(parms.getParameter(Constants.RECORD_LONG_TEXT));
      vCallData.setIsSmsCall(parms.getParameter(Constants.RECORD_SMS) != null);
      vCallData.setIsAgendaCall(parms.getParameter(Constants.RECORD_AGENDA) != null);
      vCallData.setIsForwardCall(parms.getParameter(Constants.RECORD_FORWARD) != null);
      boolean prevIsImportant = vCallData.getIsImportantCall();
      vCallData.setIsImportantCall(parms.getParameter(Constants.RECORD_IMPORTANT) != null);
      vCallData.setIsFaxCall(parms.getParameter(Constants.RECORD_FAX) != null);
      if (vOldCustomer != null && parms.getParameter(Constants.RECORD_INVOICE_LEVEL) != null)
      {
         if (vOldCustomer.getInvoiceType() == InvoiceHelper.kTelemarketingInvoice)
         {
            String vLevel = parms.getParameter(Constants.RECORD_INVOICE_LEVEL);
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
      String str = parms.getParameter(Constants.RECORD_NOTIFY);
      boolean isNotifySet = (str != null && !str.isEmpty());
      vCallData.setIsVirgin(false);
      vCallData.setIsChangedByCust(false);
      vCallData.setDoneBy(session.getUserId());
      CallRecordSqlAdapter.setIsDocumentedFlag(vCallData);
      printCallInsert(session, vCallData);
      if ((!prevIsImportant && vCallData.getIsImportantCall() && vCallData.getIsDocumented()) || isNotifySet)
      {
         NotifyCustomerTask.notify(vCallData.getAccountId(), new WebSocketData(WebSocketData.URGENT_CALL, vCallData.getId(), vCallData.getName(), CallRecordSqlAdapter.abbrevText(vCallData.getShortDescription()), vCallData.getTime()), !vCallData.getIsMailed());
         vCallData.setIsCustAttentionNeeded(true);
      }
      else if (vCallData.getIsImportantCall())
      {
         log.warn("expected a mail for important call. prevIsImportant=" + prevIsImportant + ", isDocumented=" + vCallData.getIsDocumented());
      }
      vCallLogWriterSession.setCallData(session, vCallData);

   }

   /*
    * this method is called when the operator creates a call from scratch, and
    * knowing already for what customer she is creating it. This call shall be
    * saved in the database as a fully valid and documented call. So this call
    * shall not be stored as a 'unmapped' call that must be linked to an incoming
    * call afterwards.
    */
   public static void saveManualRecord(SessionParmsInf parms, WebSession session)
   {
      // log.info("saveManualRecord()");
      CallRecordEntityData newRecord = new CallRecordEntityData();
      Calendar vCalendar = Calendar.getInstance();
      newRecord.setIsVirgin(false);
      newRecord.setIsChangedByCust(false);

      newRecord.setIsAgendaCall(parms.getParameter(Constants.RECORD_AGENDA) != null);
      newRecord.setIsSmsCall(parms.getParameter(Constants.RECORD_SMS) != null);
      newRecord.setIsForwardCall(parms.getParameter(Constants.RECORD_FORWARD) != null);
      newRecord.setIsImportantCall(parms.getParameter(Constants.RECORD_IMPORTANT) != null);
      newRecord.setIsFaxCall(parms.getParameter(Constants.RECORD_FAX) != null);
      newRecord.setIsIncomingCall((parms.getParameter(Constants.RECORD_DIR)).equals(Constants.RECORD_DIR_IN));
      newRecord.setFwdNr(parms.getParameter(Constants.ACCOUNT_FORWARD_NUMBER));
      newRecord.setDate(parms.getParameter(Constants.RECORD_DATE));
      newRecord.setTime(parms.getParameter(Constants.RECORD_TIME));
      newRecord.setNumber(parms.getParameter(Constants.RECORD_NUMBER));
      newRecord.setName(parms.getParameter(Constants.RECORD_CALLER_NAME));
      newRecord.setShortDescription(parms.getParameter(Constants.RECORD_SHORT_TEXT));
      newRecord.setLongDescription(parms.getParameter(Constants.RECORD_LONG_TEXT));
      newRecord.setCost("00:01:10");
      newRecord.setTimeStamp(vCalendar.getTimeInMillis());
      newRecord.setDoneBy(session.getUserId());
      newRecord.setMonthInt(vCalendar.get(Calendar.YEAR) * 100 + vCalendar.get(Calendar.MONTH));
      newRecord.setDayInt(vCalendar.get(Calendar.YEAR) * 10000 + vCalendar.get(Calendar.MONTH) * 100 + vCalendar.get(Calendar.DAY_OF_MONTH));

      AccountEntityData account = AccountCache.getInstance().get(newRecord);
      newRecord.setAccountId(account.getId());

      if (AccountCache.getInstance().isMailEnabled(account))
      {
         newRecord.setIsMailed(false);
      }
      else
         newRecord.setIsMailed(true);

      if (parms.getParameter(Constants.RECORD_INVOICE_LEVEL) != null)
      {
         if (account.getInvoiceType() == InvoiceHelper.kTelemarketingInvoice)
         {
            String vLevel = parms.getParameter(Constants.RECORD_INVOICE_LEVEL);
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
         newRecord.setIsCustAttentionNeeded(true);
      }

      // Check the record and add it if it is a valid one.
      // log.info("saveManualRecord: id=" + newRecord.getId() + ", cust=" +
      // newRecord.getFwdNr() + ", number=" + newRecord.getNumber());
      CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
      int id = vQuerySession.addRow(session, newRecord);
      if (newRecord.getIsImportantCall() && newRecord.getIsDocumented())
      {
         NotifyCustomerTask.notify(account.getId(), new WebSocketData(WebSocketData.URGENT_CALL, id, newRecord.getName(), CallRecordSqlAdapter.abbrevText(newRecord.getShortDescription()), newRecord.getTime()), !newRecord.getIsMailed());
      }
      printCallInsert(session, newRecord);
   }

   public static void deleteRecords(SessionParmsInf parms, WebSession session)
   {
      String vLtd = parms.getParameter(Constants.RECORDS_TO_HANDLE);
      log.info("record to delete list: " + vLtd);
      if (vLtd != null && vLtd.length() > 0)
      {
         StringTokenizer vStrTok = new StringTokenizer(vLtd, ",");
         CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();

         while (vStrTok.hasMoreTokens())
         {
            int key = Integer.parseInt(vStrTok.nextToken());
            CallRecordEntityData record = vQuerySession.getRow(session, key);
            // only delete calls that have not yet been documented
            if (record != null && (!record.getIsDocumented() || session.getRole() == AccountRole.ADMIN))
            {
               vQuerySession.deleteRow(session, key);
               printCallDelete(session, key);
            }
         }
      }
   }

   public static void saveNewSubCustomer(SessionParmsInf parms, WebSession webSession, int accountId)
   {
      // log.info("saveNewSubCustomer()");
      CallRecordSqlAdapter vCallLogWriterSession = new CallRecordSqlAdapter();
      vCallLogWriterSession.changeFwdNumber(webSession, webSession.getRecordId(), accountId);
      webSession.setNewUnmappedCall(null);
      webSession.setRecordId(0);
   }

   public static void archiveRecords(SessionParmsInf parms, WebSession webSession)
   {
      String vLtd = parms.getParameter(Constants.RECORDS_TO_HANDLE);
      // log.info("archiveRecords() " + vLtd);
      if (vLtd != null && vLtd.length() > 0)
      {
         StringTokenizer vStrTok = new StringTokenizer(vLtd, ",");
         CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
         boolean isFirstLoop = true;

         StringBuilder strBldr = new StringBuilder();
         while (vStrTok.hasMoreTokens())
         {
            if (!isFirstLoop)
            {
               strBldr.append(",");
            }
            isFirstLoop = false;
            int key = Integer.parseInt(vStrTok.nextToken());
            strBldr.append(key);
         }
         vQuerySession.archiveRecords(webSession, strBldr.toString());
      }
   }

   public static void changeFwdNumber(WebSession session, int callId, int newAccountId)
   {
      CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
      vQuerySession.changeFwdNumber(session, callId, newAccountId);
   }

   // 'terug' button was called on the newCall page. User want to cancel this
   // entry.
   // look for a call with this key in the WebSession NewUnmappedCalls, and remove
   // it.
   public static void removeNewCall(SessionParmsInf parms, WebSession webSession)
   {
      log.info("removeNewCall()");
      webSession.setNewUnmappedCall(null);
      webSession.setRecordId(0);
   }

   private static void printCallDelete(WebSession session, int key)
   {
      Calendar calendar = Calendar.getInstance();

      lock.lock();
      try
      {
         FileOutputStream logStream = getCallLogFileStream(calendar);
         logStream.write((calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + " " + session.getUserId() + ": " + CallRecordEntityData.toSqlDeleteString(key)).getBytes());
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
         return;
      }
      finally
      {
         lock.unlock();
      }
   }

   private static void printCallInsert(WebSession session, CallRecordEntityData record)
   {
      Calendar calendar = Calendar.getInstance();

      lock.lock();
      try
      {
         FileOutputStream logStream = getCallLogFileStream(calendar);
         logStream.write((calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + " " + session.getUserId() + ": " + CallRecordEntityData.toSqlInsertString(record)).getBytes());
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
         return;
      }
      finally
      {
         lock.unlock();
      }
   }

   private static FileOutputStream getCallLogFileStream(Calendar calendar)
   {
      int year = calendar.get(Calendar.YEAR);
      int month = calendar.get(Calendar.MONTH) + 1;
      int day = calendar.get(Calendar.DAY_OF_MONTH);
      String fileName = new String(Constants.RECORDS_OF_TODAY_PATH + year + "-" + month + "-" + day + ".sql");
      FileOutputStream fileStream = null;

      try
      {
         File file = new File(fileName);
         if (!file.exists())
         {
            file.createNewFile();
         }
         fileStream = new FileOutputStream(file, true);
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         log.error(fileName + " could not be created.");
      }
      return fileStream;
   }

}
