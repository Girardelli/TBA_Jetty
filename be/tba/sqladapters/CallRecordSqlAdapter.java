package be.tba.sqladapters;

//import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import be.tba.session.PhoneMapManager;
import be.tba.session.WebSession;
import be.tba.sqldata.AccountCache;
import be.tba.sqldata.AccountEntityData;
import be.tba.sqldata.CallRecordEntityData;
import be.tba.util.constants.Constants;
import be.tba.util.data.IntertelCallData;
import be.tba.util.timer.CallCalendar;
import be.tba.util.timer.NotifyCustomerTask;
import be.tba.websockets.WebSocketData;

import java.sql.SQLException;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean Template
 *
 * ATTENTION: Some of the XDoclet tags are hidden from XDoclet by adding a "--"
 * between @ and the namespace. Please remove this "--" to make it active or add
 * a space to make an active tag inactive.
 *
 * @ejb:bean name="CallRecordQuerySession" display-name="Call Record query"
 *           type="Stateless" transaction-type="Container"
 *           jndi-name="be/tba/ejb/pbx/CallRecordQuerySession"
 *
 * @ejb:ejb-ref ejb-name="CallRecordEntity"
 *
 */
public class CallRecordSqlAdapter extends AbstractSqlAdapter<CallRecordEntityData>
{
   private static Logger log = LoggerFactory.getLogger(CallRecordSqlAdapter.class);

   public CallRecordSqlAdapter()
   {
      super("CallRecordEntity");
      // TODO Auto-generated constructor stub
   }

   // -------------------------------------------------------------------------
   // Static
   // -------------------------------------------------------------------------

   // -------------------------------------------------------------------------
   // Members
   // -------------------------------------------------------------------------

   // -------------------------------------------------------------------------
   // Methods
   // -------------------------------------------------------------------------

   // for test
   /**
    * @ejb:interface-method view-type="remote"
    */
   public Vector<CallRecordEntityData> getAllForDummy(WebSession webSession)
   {
      try
      {
         Collection<CallRecordEntityData> vRecordList = executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity");
         Vector<CallRecordEntityData> vVector = new Vector<CallRecordEntityData>();
         for (Iterator<CallRecordEntityData> i = vRecordList.iterator(); i.hasNext();)
         {
            CallRecordEntityData vEntry = i.next();
            // Long vTimeStamp = new Long(vEntry.getTimeStamp());
            vVector.add(vEntry);
         }
         // log.info( "getAllForDummy: " + vCollection.size() + entries." );
         return vVector;
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
      return new Vector<CallRecordEntityData>();
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public CallRecordEntityData getRecord(WebSession webSession, String key)
   {
      return getRow(webSession, Integer.parseInt(key));
   }

   /**
    * @ejb:interface-method view-type="remote" * / public Collection
    *                       getUnReleased(String fwdNr) throws RemoteException {
    *                       try { CallRecordEntityHome callRecordHome =
    *                       getEntityBean(); Collection vCollection =
    *                       callRecordHome.findUnReleased();
    *
    *                       //log.info( "getAllForDummy for " + fwdNr + ": " +
    *                       vCollection.size() + " entries." );
    *
    *                       return translateToValueObjects(vCollection); // return
    *                       sortRecordList(vCollection, fwdNr); } catch (Exception
    *                       e) { log.error(e.getMessage(), e); } return new
    *                       Vector(); }
    */
   /**
    * @ejb:interface-method view-type="remote"
    */
   public Collection<CallRecordEntityData> getxDaysBack(WebSession webSession, int daysBack, int id)
   {
      try
      {
         CallCalendar vCallCalendar = new CallCalendar();

         Calendar targetCal = vCallCalendar.getDaysBack(daysBack);
         int dayInt = targetCal.get(Calendar.YEAR) * 10000 + targetCal.get(Calendar.MONTH) * 100 + targetCal.get(Calendar.DAY_OF_MONTH);
         Collection<CallRecordEntityData> vCollection = executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=FALSE ORDER BY TimeStamp DESC");

         if (id <= 0)
         {
            vCollection.addAll(executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=TRUE AND DayInt=" + dayInt + " ORDER BY TimeStamp DESC"));
         }
         else
         {
            vCollection.addAll(executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=TRUE AND AccountID=" + id + " AND DayInt=" + dayInt + " ORDER BY TimeStamp DESC"));
         }
         return vCollection;
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
      return new Vector<CallRecordEntityData>();
   }

   /*
    * Only for customer calls: Clientcalls.jsp
    */
   public Collection<CallRecordEntityData> getxWeeksBackIncludingSubcustomer(WebSession webSession, int daysBack, int accountId, boolean includeArchived)
   {
      try
      {
         AccountEntityData customer = AccountCache.getInstance().get(accountId);

         String customerIdsIN = "'" + accountId + "'";
         if (customer.getHasSubCustomers())
         {
            Collection<AccountEntityData> subCustomers = AccountCache.getInstance().getSubCustomersList(customer.getId());
            if (subCustomers != null)
            {
               for (Iterator<AccountEntityData> i = subCustomers.iterator(); i.hasNext();)
               {
                  AccountEntityData vEntry = i.next();
                  customerIdsIN = customerIdsIN.concat(",'" + vEntry.getId() + "'");
               }
            }
         }

         CallCalendar vCallCalendar = new CallCalendar();

         Calendar targetCal = vCallCalendar.getDaysBack(daysBack);
//       int monthInt = targetCal.get(Calendar.YEAR)*100 + targetCal.get(Calendar.MONTH);
         int fromDayInt = targetCal.get(Calendar.YEAR) * 10000 + targetCal.get(Calendar.MONTH) * 100 + targetCal.get(Calendar.DAY_OF_MONTH);

         if (daysBack >= 7)
         {
            targetCal = vCallCalendar.getDaysBack(daysBack - 7);
         }
         else
         {
            targetCal = vCallCalendar.getDaysBack(0);
         }
         int toDayInt = targetCal.get(Calendar.YEAR) * 10000 + targetCal.get(Calendar.MONTH) * 100 + targetCal.get(Calendar.DAY_OF_MONTH);
         return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=TRUE AND IsArchived=" + (includeArchived ? "TRUE" : "FALSE") + " AND DayInt>=" + fromDayInt + " AND DayInt<=" + toDayInt + " AND AccountID IN (" + customerIdsIN + ") ORDER BY TimeStamp DESC");
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
      return new Vector<CallRecordEntityData>();
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public Collection<CallRecordEntityData> getIn(WebSession webSession, int id)
   {
      try
      {
         if (id <= 0)
         {
            return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsIncomingCall=TRUE ORDER BY TimeStamp DESC");
         }
         else
         {
            return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsIncomingCall=TRUE AND AccountID=" + id + " ORDER BY TimeStamp DESC");
         }
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
      return new Vector<CallRecordEntityData>();
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public Collection<CallRecordEntityData> getOut(WebSession webSession, int id) // throws RemoteException
   {
      try
      {
         if (id <= 0)
         {
            return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsIncomingCall=FALSE ORDER BY TimeStamp DESC");
         }
         else
         {
            return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsIncomingCall=FALSE AND AccountID=" + id + " ORDER BY TimeStamp DESC");
         }
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
      return new Vector<CallRecordEntityData>();
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public Collection<CallRecordEntityData> getVirgins(WebSession webSession) // throws RemoteException
   {
      try
      {
         return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsVirgin=TRUE ORDER BY TimeStamp DESC");
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
      return new Vector<CallRecordEntityData>();
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public Collection<CallRecordEntityData> getDocumentedForMonth(WebSession webSession, int id, int month, int year)
   {
      try
      {
         int monthInt = year * 100 + month;
         if (id <= 0)
         {
            return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE MonthInt=" + monthInt + " AND IsDocumented=TRUE ORDER BY TimeStamp DESC");
         }
         else
         {
            return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE AccountID=" + id + " AND MonthInt=" + monthInt + " AND IsDocumented=TRUE ORDER BY TimeStamp DESC");
         }
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
      return new Vector<CallRecordEntityData>();
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public Collection<CallRecordEntityData> getDocumentedToday(WebSession webSession, int id)
   {
      try
      {
         Calendar vCalendar = Calendar.getInstance();
         int dayInt = vCalendar.get(Calendar.YEAR) * 10000 + vCalendar.get(Calendar.MONTH) * 100 + vCalendar.get(Calendar.DAY_OF_MONTH);
         if (id <= 0)
         {
            return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE DayInt=" + dayInt + " AND IsDocumented=TRUE ORDER BY TimeStamp DESC");
         }
         else
         {
            return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE AccountID=" + id + " AND DayInt=" + dayInt + " AND IsDocumented=TRUE ORDER BY TimeStamp DESC");
         }
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
      return new Vector<CallRecordEntityData>();
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public Collection<CallRecordEntityData> getInvoiceCalls(WebSession webSession, int accountId, int year, int month, long start, long stop)
   {
      Collection<CallRecordEntityData> vCallList = new Vector<CallRecordEntityData>();
      collectInvoiceCalls(webSession, AccountCache.getInstance().get(accountId), vCallList, year, month, start, stop);
      return vCallList;
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public Hashtable<Integer, Collection<CallRecordEntityData>> getInvoiceCallsHashTable(WebSession webSession, int accountId, long start, long stop)
   {
      Hashtable<Integer, Collection<CallRecordEntityData>> vCallList = new Hashtable<Integer, Collection<CallRecordEntityData>>();
      collectInvoiceCallsHashTable(webSession, AccountCache.getInstance().get(accountId), vCallList, start, stop);
      return vCallList;
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public Collection<CallRecordEntityData> getSearchCalls(WebSession webSession, int id, String str2find, int month, int year)
   {
      try
      {
         AccountEntityData customer = AccountCache.getInstance().get(id);

         if (customer != null)
         {
            String customerIdsIN = Integer.toString(id);
            if (customer.getHasSubCustomers())
            {
               Collection<AccountEntityData> subCustomers = AccountCache.getInstance().getSubCustomersList(customer.getId());
               if (subCustomers != null)
               {
                  for (Iterator<AccountEntityData> i = subCustomers.iterator(); i.hasNext();)
                  {
                     AccountEntityData vEntry = i.next();
                     customerIdsIN = customerIdsIN.concat("," + vEntry.getId());
                  }
               }
            }

            int monthInt = year * 100 + month;
            Vector<CallRecordEntityData> vOutList = new Vector<CallRecordEntityData>();
            Collection<CallRecordEntityData> vCollection = executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE MonthInt=" + monthInt + " AND IsDocumented=TRUE AND AccountID IN (" + customerIdsIN + ") ORDER BY TimeStamp DESC");
            SortAndFilterOnString(vOutList, vCollection, str2find);

            log.info("getSearchCalls for " + id + " during month " + month + " (string=" + str2find + "): " + vOutList.size() + " entries. Org list size " + vCollection.size());
            return vOutList;
         }
      }
      catch (Exception e)
      {
         log.info("getSearchCalls failed for AccountID=" + id + " , str2find=" + str2find);
         log.error(e.getMessage(), e);
      }
      return new Vector<CallRecordEntityData>();
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public Collection<CallRecordEntityData> getMailedCallsForMonth(WebSession webSession, int month, int year)
   {
      try
      {
         int monthInt = year * 100 + month;
         return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE MonthInt=" + monthInt + " AND IsMailed=TRUE ORDER BY TimeStamp ASC");
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
      return new Vector<CallRecordEntityData>();
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public Collection<CallRecordEntityData> getDoneByCalls(WebSession webSession, String empl, int month, int year)
   {
      try
      {
         int monthInt = year * 100 + month;
         return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE DoneBy='" + empl + "' AND MonthInt=" + monthInt + " AND IsDocumented=TRUE AND IsMailed=TRUE ORDER BY TimeStamp DESC");
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
      return new Vector<CallRecordEntityData>();
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public Collection<CallRecordEntityData> getDocumentedNotMailed(WebSession webSession, int id)
   {
      try
      {
         if (id <= 0)
         {
            return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=TRUE AND IsMailed=FALSE ORDER BY TimeStamp DESC");
         }
         else
         {
            AccountEntityData customer = AccountCache.getInstance().get(id);

            String CustomerIdsIN = Integer.toString(id);
            if (customer.getHasSubCustomers())
            {
               Collection<AccountEntityData> subCustomers = AccountCache.getInstance().getSubCustomersList(customer.getId());
               if (subCustomers != null)
               {
                  for (Iterator<AccountEntityData> i = subCustomers.iterator(); i.hasNext();)
                  {
                     AccountEntityData vEntry = i.next();
                     if (!AccountCache.getInstance().isMailEnabled(vEntry) || vEntry.getEmail() == null || vEntry.getEmail().isEmpty())
                     {
                        // no email set for this subcustomer --> mail the calls to the supercustomer
                        CustomerIdsIN = CustomerIdsIN.concat("," + vEntry.getId());
                     }
                  }
               }
            }
            return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=TRUE AND IsMailed=FALSE AND AccountID IN (" + CustomerIdsIN + ") ORDER BY TimeStamp DESC");
         }
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
      return new Vector<CallRecordEntityData>();
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public Collection<CallRecordEntityData> getChatRecords(WebSession webSession)
   {
      try
      {
         return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=TRUE AND IsChanged=TRUE AND IsArchived=FALSE ORDER BY TimeStamp DESC");
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
      return new Vector<CallRecordEntityData>();
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public int cleanDb(WebSession webSession)
   {
      // if (count == 0)
      // return 0;

      try
      {
         Calendar vCalendar = Calendar.getInstance();
         long vCurTimeStamp = vCalendar.getTimeInMillis();
         long vEndTime = vCurTimeStamp - Constants.RECORD_DELETE_EXPIRE;

         Collection<CallRecordEntityData> vDummyVec = executeSqlQuery(webSession, "DELETE FROM CallRecordEntity WHERE Timestamp<" + vEndTime);
         log.info("cleanDb removed " + vDummyVec.size() + " call records");
         return vDummyVec.size();
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
      return 0;
   }

   static final long PERIOD = Constants.DAYS * 20;

   static final long LOOPS = 365 / 20;

   /**
    * @ejb:interface-method view-type="remote"
    */
   public void changeFwdNumber(WebSession webSession, int key, int accountId)
   {
      executeSqlQuery(webSession, "UPDATE CallRecordEntity SET AccountID='" + accountId + "' WHERE ID=" + key + "");
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public void removeAccountCalls(WebSession webSession, int accountID)
   {
      log.info("removeAccountCalls for " + accountID);
      executeSqlQuery(webSession, "DELETE FROM CallRecordEntity WHERE AccountID=" + accountID);
   }

   private void collectInvoiceCalls(WebSession webSession, AccountEntityData customer, Collection<CallRecordEntityData> callList, int year, int month, long start, long stop)
   {
      int monthInt = year * 100 + month;
      Collection<CallRecordEntityData> vCollection = executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE monthInt=" + monthInt + " AND AccountID='" + customer.getId() + "' AND TimeStamp>" + start + " AND TimeStamp<=" + stop + " AND IsDocumented=TRUE AND IsMailed=TRUE ORDER BY TimeStamp DESC");

      if (vCollection != null)
      {
         callList.addAll(vCollection);
      }
      if (customer.getHasSubCustomers())
      {
         Collection<AccountEntityData> vSubCustomerList = AccountCache.getInstance().getSubCustomersList(customer.getId());
         if (vSubCustomerList != null)
         {
            for (Iterator<AccountEntityData> i = vSubCustomerList.iterator(); i.hasNext();)
            {
               AccountEntityData vEntry = i.next();

               if (vEntry.getNoInvoice())
               {
                  collectInvoiceCalls(webSession, vEntry, callList, year, month, start, stop);
               }
               else
               {
                  log.info("collectInvoiceCalls: " + customer.getFullName() + " has its invoice flag set.");
               }
            }
         }
      }
   }

   private void collectInvoiceCallsHashTable(WebSession webSession, AccountEntityData customer, Hashtable<Integer, Collection<CallRecordEntityData>> callList, long start, long stop)
   {
      Collection<CallRecordEntityData> vCollection = executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE AccountID=" + customer.getId() + " AND TimeStamp>" + start + " AND TimeStamp<=" + stop + " AND IsDocumented=TRUE AND IsMailed=TRUE ORDER BY TimeStamp DESC");
      if (vCollection != null)
      {
         callList.put(customer.getId(), vCollection);
         log.info("collectInvoiceCallsHashTable for customer " + customer.getFullName() + ": " + vCollection.size() + "(" + start + ", " + stop + ")");
      }
      if (customer.getHasSubCustomers())
      {
         Collection<AccountEntityData> vSubCustomerList = AccountCache.getInstance().getSubCustomersList(customer.getId());
         log.info(customer.getFullName() + " has " + vSubCustomerList.size() + " sub customers");
         for (Iterator<AccountEntityData> i = vSubCustomerList.iterator(); i.hasNext();)
         {
            AccountEntityData vEntry = i.next();

            if (vEntry.getNoInvoice())
            {
               collectInvoiceCallsHashTable(webSession, vEntry, callList, start, stop);
            }
            else
            {
               log.info("collectInvoiceCalls: " + customer.getFullName() + " has its invoice flag set.");
            }
         }
      }
   }

   private Collection<CallRecordEntityData> SortAndFilterOnString(Collection<CallRecordEntityData> outputList, Collection<CallRecordEntityData> inputList, String str2find)
   {
      if (str2find != null && str2find.length() > 0)
      {
         String vLowerFinder = str2find.toLowerCase();
         StringBuilder strBuilder = new StringBuilder();
         for (Iterator<CallRecordEntityData> i = inputList.iterator(); i.hasNext();)
         {
            boolean vRemoveEntry = true;
            CallRecordEntityData vEntry = i.next();
            strBuilder = new StringBuilder();
            if ((ReplaceNoCaseSensitive(vEntry.getShortDescription(), vLowerFinder, strBuilder)))
            {
               vEntry.setShortDescription(strBuilder.toString());
               vRemoveEntry = false;
            }
            strBuilder = new StringBuilder();
            if ((ReplaceNoCaseSensitive(vEntry.getName(), vLowerFinder, strBuilder)))
            {
               vEntry.setName(strBuilder.toString());
               vRemoveEntry = false;
            }
            strBuilder = new StringBuilder();
            if ((ReplaceNoCaseSensitive(vEntry.getDate(), vLowerFinder, strBuilder)))
            {
               vEntry.setDate(strBuilder.toString());
               vRemoveEntry = false;
            }
            strBuilder = new StringBuilder();
            if ((ReplaceNoCaseSensitive(vEntry.getTime(), vLowerFinder, strBuilder)))
            {
               vEntry.setTime(strBuilder.toString());
               vRemoveEntry = false;
            }
            strBuilder = new StringBuilder();
            if ((ReplaceNoCaseSensitive(vEntry.getNumber(), vLowerFinder, strBuilder)))
            {
               vEntry.setNumber(strBuilder.toString());
               vRemoveEntry = false;
            }
            strBuilder = new StringBuilder();
            if ((ReplaceNoCaseSensitive(vEntry.getLongDescription(), vLowerFinder, strBuilder)))
            {
               vEntry.setLongDescription(strBuilder.toString());
               vRemoveEntry = false;
            }
            strBuilder = new StringBuilder();
            if (!vRemoveEntry)
            {
               outputList.add(vEntry);
            }
         }
      }
      return outputList;
   }

   private static boolean ReplaceNoCaseSensitive(String inStr, String lowerReplacer, StringBuilder result)
   {
      boolean hit = false;
      if (inStr != null && lowerReplacer != null)
      {
         String lowerStr = inStr.toLowerCase();
         int ind = 0;
         int prevInd = 0;
         int findLen = lowerReplacer.length();
         while ((ind = lowerStr.indexOf(lowerReplacer, ind)) != -1)
         {
            if (ind > 0)
            {
               result.append(inStr.substring(prevInd, ind));
            }
            result.append("<span class=findtekst>");
            result.append(inStr.substring(ind, ind + findLen));
            result.append("</span>");
            ind += findLen;
            prevInd = ind;
            hit = true;
         }
         if (hit)
         {
            result.append(inStr.substring(prevInd, inStr.length()));
         }
      }
      return hit;
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public void setCallData(WebSession webSession, CallRecordEntityData data)
   {
      if (data.getCost().equals(Constants.kNullCost))
      {
         setOperatorLogging(webSession, data);
         return;
      }
      else
      {
         setIsDocumentedFlag(data);
         PhoneMapManager.getInstance().updateOperatorMapping(data, webSession);
      }
      updateRow(webSession, data);
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public void setOperatorLogging(WebSession webSession, CallRecordEntityData data)
   {
      setIsDocumentedFlag(data);
      PhoneMapManager.getInstance().updateOperatorMapping(data, webSession);
      StringBuffer sqlCmd = new StringBuffer("UPDATE CallRecordEntity SET IsAgendaCall=");
      sqlCmd.append(data.getIsAgendaCall());
      sqlCmd.append(",AccountID=" + data.getAccountId());
      sqlCmd.append(",FwdNr='" + data.getFwdNr());
      sqlCmd.append("',IsSmsCall=" + data.getIsSmsCall());
      sqlCmd.append(",IsForwardCall=" + data.getIsForwardCall());
      sqlCmd.append(",IsImportantCall=" + data.getIsImportantCall());
      sqlCmd.append(",IsFaxCall=" + data.getIsFaxCall());
      sqlCmd.append(",IsDocumented=" + data.getIsDocumented());
      sqlCmd.append(",IsMailed=" + data.getIsMailed());
      sqlCmd.append(",IsVirgin=" + data.getIsVirgin());
      sqlCmd.append(",IsFaxCall=" + data.getIsFaxCall());
      sqlCmd.append(",IsChanged=" + data.getIsChangedByCust());
      sqlCmd.append(",IsCustAttentionNeeded=" + data.getIsCustAttentionNeeded());
      sqlCmd.append(",Name='" + ((data.getName() != null) ? escapeQuotes(data.getName()) : ""));
      sqlCmd.append("',ShortDescription='" + ((data.getShortDescription() != null) ? escapeQuotes(data.getShortDescription()) : ""));
      sqlCmd.append("',LongDescription='" + ((data.getLongDescription() != null) ? escapeQuotes(data.getLongDescription()) : ""));
      sqlCmd.append("',DoneBy='" + ((data.getDoneBy() != null) ? data.getDoneBy() : ""));
      sqlCmd.append("',InvoiceLevel=" + data.getInvoiceLevel());

      if (!webSession.isAutoUpdateRecord())
      {
         // this is not a save resulting from an auto update.
         // fill also the other possible changed fields
         sqlCmd.append(",FwdNr='" + ((data.getFwdNr() != null) ? data.getFwdNr() : ""));
         sqlCmd.append("',Number='" + ((data.getNumber() != null) ? escapeQuotes(data.getNumber()) : ""));
         sqlCmd.append("',AccountID=" + data.getAccountId());
      }
      sqlCmd.append(" WHERE Id=" + data.getId());
      executeSqlQuery(webSession, sqlCmd.toString());
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public void setIsMailed(WebSession webSession, String key)
   {
      setIsMailed(webSession, Integer.parseInt(key));
   }

   /**
    * @ejb:interface-method view-type="remote"
    */
   public void setIsMailed(WebSession webSession, int key)
   {
      executeSqlQuery(webSession, "UPDATE CallRecordEntity SET IsMailed=TRUE WHERE Id=" + key);
   }

   public void setShortText(WebSession webSession, int key, String text, boolean isCustomer, boolean isArchived, boolean isCustomerAttentionNeeded)
   {
      CallRecordEntityData data = getRow(webSession, key);
      if (data != null && text != null)
      {
         String strippedText = text;
         while (strippedText.lastIndexOf("\r\n") >= strippedText.length())
         {
            strippedText = strippedText.substring(0, strippedText.length());
         }
         if (isCustomer)
         {
            if (!text.isBlank())
            {
               data.setShortDescription(data.getShortDescription() + "<br>&nbsp;&nbsp;<span class=\"bodygreenbold\">" + text + "</span>");
               data.setIsChangedByCust(true);
            }
            data.setIsArchived(isArchived);
            data.setIsCustAttentionNeeded(false);
         }
         else
         {
            data.setIsChangedByCust(false);
            if (!text.isBlank())
            {
               if (data.getShortDescription().isBlank())
                  data.setShortDescription(data.getShortDescription() + webSession.getUserId() + ": " + text);
               else
                  data.setShortDescription(data.getShortDescription() + "<br>" + webSession.getUserId() + ": " + text);
               data.setIsCustAttentionNeeded(isCustomerAttentionNeeded);
               if (isCustomerAttentionNeeded)
               {
                  NotifyCustomerTask.notify(data.getAccountId(), new WebSocketData(WebSocketData.URGENT_CALL, data.getId(), data.getName(), abbrevText(data.getShortDescription()), data.getTime()), false);
               }
            }
         }
         // log.info("setShortText: [" + text + "]");
         updateRow(webSession, data);
      }
   }

   public int addIntertelCall(WebSession webSession, IntertelCallData data)
   {
      CallRecordEntityData newRecord = new CallRecordEntityData();
      // to be removed:
      // newRecord.setName(data.intertelCallId.substring(0, 6));

      if (data.isIncoming)
      {
         newRecord.setFwdNr(IntertelCallData.last6Numbers(data.calledNr));
         newRecord.setNumber(data.callingNr);
      }
      else
      {
         newRecord.setFwdNr(IntertelCallData.last6Numbers(data.callingNr));
         newRecord.setNumber(data.calledNr);
      }
      // log.info("getAccount: FwdNr=" + newRecord.getFwdNr() + ", number=" +
      // newRecord.getNumber());
      AccountEntityData vAccount = AccountCache.getInstance().get(newRecord.getFwdNr());
      if (vAccount == null)
      {
         vAccount = AccountCache.getInstance().get(Constants.NUMBER_BLOCK[0][0]);
      }
      newRecord.setTimeStamp(data.tsStart * 1000);
      newRecord.setTsStart(data.tsStart);
      newRecord.setIsIncomingCall(data.isIncoming);
      Calendar vToday = Calendar.getInstance();
      newRecord.setMonthInt(vToday.get(Calendar.YEAR) * 100 + vToday.get(Calendar.MONTH));
      newRecord.setDayInt(vToday.get(Calendar.YEAR) * 10000 + vToday.get(Calendar.MONTH) * 100 + vToday.get(Calendar.DAY_OF_MONTH));
      newRecord.setDate(String.format("%02d/%02d/%02d", vToday.get(Calendar.DAY_OF_MONTH), vToday.get(Calendar.MONTH) + 1, vToday.get(Calendar.YEAR) - 2000));
      newRecord.setTime(String.format("%02d:%02d", vToday.get(Calendar.HOUR_OF_DAY), vToday.get(Calendar.MINUTE)));
      newRecord.setCost(Constants.kNullCost);
      int dbId = 0;

      if (vAccount != null)
      {
         newRecord.setAccountId(vAccount.getId());
         if (AccountCache.getInstance().isMailEnabled(vAccount))
            newRecord.setIsMailed(false);
         else
            newRecord.setIsMailed(true);
         dbId = addRow(webSession, newRecord);
         // log.info("addCallRecord: id={}, fwdnr={}, isMailed={}", dbId,
         // newRecord.getFwdNr(), newRecord.getIsMailed());
      }
      else
      {
         log.info("unknown customer number: " + data.calledNr);
      }
      return dbId;
   }

   public void setTsAnswer(WebSession webSession, IntertelCallData data)
   {
      executeSqlQuery(webSession, "UPDATE CallRecordEntity SET TsAnswer='" + data.tsAnswer + "' WHERE Id='" + data.dbRecordId + "'");
   }

   public void setTsEnd(WebSession webSession, IntertelCallData data)
   {
      String shortDescr = "";
      if (data.tsAnswer == 0)
      {
         shortDescr = "ShortDescription='Niet opgenomen', ";
      }
      if (!data.isIncoming)
      {
         if (data.isTransferOutCall)
         {
            // transfered outgoing call
            executeSqlQuery(webSession, "UPDATE CallRecordEntity SET " + shortDescr + "TsEnd='" + data.tsEnd + "', Cost='" + data.getCostStr() + "' WHERE Id='" + data.dbRecordId + "'");
         }
         else
         {
            // regular outgoing call
            String callingParty = "";// "', FwdNr='" + data.callingNr;
            if (data.tsAnswer > 0)
            {
               callingParty = "', FwdNr='" + IntertelCallData.last6Numbers(data.answeredBy);
            }
            executeSqlQuery(webSession, "UPDATE CallRecordEntity SET " + shortDescr + "TsEnd='" + data.tsEnd + "', Cost='" + data.getCostStr() + callingParty + "' WHERE Id='" + data.dbRecordId + "'");
         }
         return;
      }
      executeSqlQuery(webSession, "UPDATE CallRecordEntity SET " + shortDescr + "TsEnd='" + data.tsEnd + "', Cost='" + data.getCostStr() + "' WHERE Id='" + data.dbRecordId + "'");
   }

   public void setTransfer(WebSession webSession, IntertelCallData transferedInData, IntertelCallData transferOutData)
   {
      executeSqlQuery(webSession, "UPDATE CallRecordEntity SET IsForwardCall=true, TsEnd='" + transferedInData.tsEnd + "', Cost='" + transferedInData.getCostStr() + "' WHERE Id='" + transferedInData.dbRecordId + "'");
      executeSqlQuery(webSession, "UPDATE CallRecordEntity SET FwdNr='" + IntertelCallData.last6Numbers(transferOutData.callingNr) + "', ShortDescription='Doorgeschakelde oproep van " + transferedInData.callingNr + " naar " + transferOutData.calledNr + "' WHERE Id='" + transferOutData.dbRecordId + "'");
   }

   public void setCallingNr(WebSession webSession, IntertelCallData data)
   {
      executeSqlQuery(webSession, "UPDATE CallRecordEntity SET FwdNr='" + IntertelCallData.last6Numbers(data.callingNr) + "' WHERE Id='" + data.dbRecordId + "'");
   }

   public void setForwardCallFlag(WebSession webSession, IntertelCallData data)
   {
      executeSqlQuery(webSession, "UPDATE CallRecordEntity SET IsForwardCall=true WHERE ID=" + data.dbRecordId);
   }

   public void setNotAnswered(WebSession webSession, IntertelCallData data)
   {
      executeSqlQuery(webSession, "UPDATE CallRecordEntity SET Cost='" + Constants.kNullCost + "', ShortDescription='verloren oproep' WHERE Id=" + data.dbRecordId);
   }

   public void archiveRecords(WebSession webSession, String inStrOfKeys)
   {
      executeSqlQuery(webSession, "UPDATE CallRecordEntity SET IsArchived=true WHERE Id IN (" + inStrOfKeys + ")");
   }

   public void setIndexes(WebSession webSession, int monthInt, int dayInt, int id)
   {
      executeSqlQuery(webSession, "UPDATE CallRecordEntity SET MonthInt=" + monthInt + ", DayInt=" + dayInt + " WHERE Id=" + id);
   }

   static public void setIsDocumentedFlag(CallRecordEntityData record)
   {
      if (record.getNumber() != null && record.getNumber().length() != 0 && record.getName() != null && record.getName().length() != 0 && (record.getShortDescription() != null && record.getShortDescription().length() != 0))
      {
         record.setIsDocumented(true);
      }
      else
      {
         record.setIsDocumented(false);
      }
      // log.info("setIsDocumentedFlag = " + record.getIsDocumented());
      // log.info("record: " + record.toNameValueString());
   }

   static public String abbrevText(String text)
   {
      String abbrev = text;
      int brInd = abbrev.indexOf("<br>");
      if (brInd != -1 && brInd < Constants.kAbbrevWidth)
      {
         abbrev = abbrev.substring(0, brInd) + "...";
      }
      else if (abbrev.length() > Constants.kAbbrevWidth)
      {
         abbrev = abbrev.substring(0, Constants.kAbbrevWidth - 1) + "...";
      }
      return abbrev;
   }

   /**
    * Describes the instance and its content for debugging purpose
    *
    * @return Debugging information about the instance and its content
    */
   public String toString()
   {
      return "CallRecordSqlAdapter [ " + " ]";
   }

   @Override
   protected Vector<CallRecordEntityData> translateRsToValueObjects(ResultSet rs) throws SQLException
   {
      Vector<CallRecordEntityData> vVector = new Vector<CallRecordEntityData>();
      while (rs.next())
      {
         CallRecordEntityData entry = new CallRecordEntityData();
         entry.setId(rs.getInt(1));
         entry.setAccountId(rs.getInt(2));
         entry.setFwdNr(null2EmpthyString(rs.getString(3)));
         entry.setDate(null2EmpthyString(rs.getString(4)));
         entry.setTime(null2EmpthyString(rs.getString(5)));
         entry.setNumber(null2EmpthyString(rs.getString(6)));
         entry.setName(null2EmpthyString(rs.getString(7)));
         entry.setCost(null2EmpthyString(rs.getString(8)));
         entry.setTimeStamp(rs.getLong(9));
         entry.setIsIncomingCall(rs.getBoolean(10));
         entry.setIsDocumented(rs.getBoolean(11));
         entry.setIsAgendaCall(rs.getBoolean(12));
         entry.setIsSmsCall(rs.getBoolean(13));
         entry.setIsForwardCall(rs.getBoolean(14));
         entry.setIsImportantCall(rs.getBoolean(15));
         entry.setIsMailed(rs.getBoolean(16));
         entry.setInvoiceLevel(rs.getShort(17));
         entry.setShortDescription(null2EmpthyString(rs.getString(18)));
         entry.setLongDescription(null2EmpthyString(rs.getString(19)));
         entry.setIsVirgin(rs.getBoolean(20));
         entry.setIsFaxCall(rs.getBoolean(21));
         entry.setIsChangedByCust(rs.getBoolean(22));
         entry.setIsArchived(rs.getBoolean(23));
         entry.setIsCustAttentionNeeded(rs.getBoolean(24));
         entry.setDoneBy(null2EmpthyString(rs.getString(25)));
         entry.setTsStart(rs.getLong(26));
         entry.setTsAnswer(rs.getLong(27));
         entry.setTsEnd(rs.getLong(28));
         entry.setMonthInt(rs.getInt(29));
         entry.setDayInt(rs.getInt(30));
         vVector.add(entry);
      }
      return vVector;
   }
}
