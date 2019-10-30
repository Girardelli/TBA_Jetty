package be.tba.ejb.pbx.session;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.pbx.interfaces.CallRecordEntityData;
import be.tba.pbx.Forum700CallRecord;
import be.tba.servlets.helper.IntertelCallManager;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;
import be.tba.util.data.AbstractSqlAdapter;
import be.tba.util.data.IntertelCallData;
import be.tba.util.data.ReleaseCallData;
import be.tba.util.session.AccountCache;
import be.tba.util.timer.CallCalendar;

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
    final static Logger sLogger = LoggerFactory.getLogger(CallRecordSqlAdapter.class);

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
            // System.out.println( "getAllForDummy: " + vCollection.size() + entries." );
            return vVector;
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
     *                       //System.out.println( "getAllForDummy for " + fwdNr +
     *                       ": " + vCollection.size() + " entries." );
     *
     *                       return translateToValueObjects(vCollection); // return
     *                       sortRecordList(vCollection, fwdNr); } catch (Exception
     *                       e) { e.printStackTrace(); } return new Vector(); }
     */
    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getxDaysBack(WebSession webSession, int daysBack, String fwdNr)
    {
        try
        {
            CallCalendar vCallCalendar = new CallCalendar();
            long vFromTimeStamp = vCallCalendar.getDaysBack(daysBack);
            long vToTimeStamp = 0;
            if (daysBack > 0)
            {
                vToTimeStamp = vCallCalendar.getDaysBack(daysBack - 1);
            }
            else
            {
                vToTimeStamp = vCallCalendar.getCurrentTimestamp();
            }
            Collection<CallRecordEntityData> vCollection = executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=FALSE ORDER BY TimeStamp DESC");
            if (fwdNr == null || fwdNr.equals(Constants.ACCOUNT_FILTER_ALL))
            {
                vCollection.addAll(executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=TRUE AND TimeStamp>" + vFromTimeStamp + " AND TimeStamp<=" + vToTimeStamp + " ORDER BY TimeStamp DESC"));
            }
            else
            {
                vCollection.addAll(executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=TRUE AND FwdNr='" + fwdNr + "' AND TimeStamp>" + vFromTimeStamp + " AND TimeStamp<=" + vToTimeStamp + " ORDER BY TimeStamp DESC"));
            }
            return vCollection;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<CallRecordEntityData>();
    }

    /*
     * Only for customer calls: Clientcalls.jsp
     */
    public Collection<CallRecordEntityData> getxWeeksBackIncludingSubcustomer(WebSession webSession, int daysBack, String fwdNr)
    {
        try
        {
        	AccountEntityData customer = AccountCache.getInstance().get(fwdNr);
        	
        	String CustomerIdsIN = "'" + fwdNr + "'";
        	if (customer.getHasSubCustomers())
        	{
        		Collection<AccountEntityData> subCustomers = AccountCache.getInstance().getSubCustomersList(customer.getId());
            	if (subCustomers != null)
            	{
                    for (Iterator<AccountEntityData> i = subCustomers.iterator(); i.hasNext();)
                    {
                        AccountEntityData vEntry = i.next();
                        CustomerIdsIN = CustomerIdsIN.concat(",'" + vEntry.getFwdNumber() + "'");
                    }
            	}
        	}
            
        	CallCalendar vCallCalendar = new CallCalendar();
            long vFromTimeStamp = vCallCalendar.getDaysBack(daysBack);
            long vToTimeStamp = 0;
            if (daysBack >= 7)
            {
                vToTimeStamp = vCallCalendar.getDaysBack(daysBack - 7);
            }
            else
            {
                vToTimeStamp = vCallCalendar.getCurrentTimestamp() + 60000; // add 1 minute in teh future
            }
            return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=TRUE AND TimeStamp>" + vFromTimeStamp + " AND TimeStamp<=" + vToTimeStamp + " AND FwdNr IN (" + CustomerIdsIN + ") ORDER BY TimeStamp DESC");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<CallRecordEntityData>();
    }
    
    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getIn(WebSession webSession, String fwdNr)
    {
        try
        {
            if (fwdNr == null)
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsIncomingCall=TRUE AND IsReleased=FALSE ORDER BY TimeStamp DESC");
            }
            else
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsIncomingCall=TRUE AND IsReleased=FALSE AND FwdNr='" + fwdNr + "' ORDER BY TimeStamp DESC");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<CallRecordEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getOut(WebSession webSession, String fwdNr) throws RemoteException
    {
        try
        {
            if (fwdNr == null)
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsIncomingCall=FALSE AND IsReleased=FALSE ORDER BY TimeStamp DESC");
            }
            else
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsIncomingCall=FALSE AND IsReleased=FALSE AND FwdNr='" + fwdNr + "' ORDER BY TimeStamp DESC");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<CallRecordEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getVirgins(WebSession webSession) throws RemoteException
    {
        try
        {
            return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsVirgin=TRUE ORDER BY TimeStamp DESC");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<CallRecordEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getInUnDocumented(WebSession webSession, String fwdNr) throws RemoteException
    {
        try
        {
            if (fwdNr == null)
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsIncomingCall=TRUE AND IsDocumented=FALSE ORDER BY TimeStamp DESC");
            }
            else
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsIncomingCall=TRUE AND IsDocumented=FALSE AND FwdNr='" + fwdNr + "' ORDER BY TimeStamp DESC");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<CallRecordEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getOutUnDocumented(WebSession webSession, String fwdNr)
    {
        try
        {
            if (fwdNr == null)
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsIncomingCall=FALSE AND IsDocumented=FALSE ORDER BY TimeStamp DESC");
            }
            else
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsIncomingCall=FALSE AND IsDocumented=FALSE AND FwdNr='" + fwdNr + "' ORDER BY TimeStamp DESC");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<CallRecordEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getUnDocumented(WebSession webSession, String fwdNr)
    {
        try
        {
            if (fwdNr == null)
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=FALSE ORDER BY TimeStamp DESC");
            }
            else
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=FALSE AND FwdNr='" + fwdNr + "' ORDER BY TimeStamp DESC");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<CallRecordEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getInDocumented(WebSession webSession, String fwdNr)
    {
        try
        {
            if (fwdNr == null)
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsIncomingCall=TRUE AND IsDocumented=TRUE ORDER BY TimeStamp DESC");
            }
            else
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsIncomingCall=TRUE AND IsDocumented=TRUE AND FwdNr='" + fwdNr + "' ORDER BY TimeStamp DESC");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<CallRecordEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getOutDocumented(WebSession webSession, String fwdNr)
    {
        try
        {
            if (fwdNr == null)
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsIncomingCall=FALSE AND IsDocumented=TRUE ORDER BY TimeStamp DESC");
            }
            else
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsIncomingCall=FALSE AND IsDocumented=TRUE AND FwdNr='" + fwdNr + "' ORDER BY TimeStamp DESC");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<CallRecordEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getDocumentedUnReleased(WebSession webSession, String fwdNr)
    {
        try
        {
            if (fwdNr == null)
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=TRUE AND IsReleased=FALSE ORDER BY TimeStamp DESC");
            }
            else
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE FwdNr='" + fwdNr + "' AND IsDocumented=TRUE AND IsReleased=FALSE ORDER BY TimeStamp DESC");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<CallRecordEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getDocumentedForMonth(WebSession webSession, String fwdNr, int month, int year)
    {
        try
        {
            CallCalendar vCalendar = new CallCalendar();
            long vStart = vCalendar.getStartOfMonth(month, year);
            long vEnd = vCalendar.getEndOfMonth(month, year);
            if (fwdNr == null)
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE TimeStamp>" + vStart + " AND TimeStamp<=" + vEnd + " AND IsDocumented=TRUE ORDER BY TimeStamp DESC");
            }
            else
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE FwdNr='" + fwdNr + "' AND TimeStamp>" + vStart + " AND TimeStamp<=" + vEnd + " AND IsDocumented=TRUE ORDER BY TimeStamp DESC");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<CallRecordEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getDocumentedToday(WebSession webSession, String fwdNr)
    {
        try
        {
            CallCalendar vCalendar = new CallCalendar();
            long vStart = vCalendar.getStartOfToday();

            if (fwdNr == null)
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE TimeStamp>" + vStart + " AND TimeStamp<=" + vCalendar.getCurrentTimestamp() + " AND IsDocumented=TRUE ORDER BY TimeStamp DESC");
            }
            else
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE FwdNr='" + fwdNr + "' AND TimeStamp>" + vStart + " AND TimeStamp<=" + vCalendar.getCurrentTimestamp() + " AND IsDocumented=TRUE ORDER BY TimeStamp DESC");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<CallRecordEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getDocumentedLastWeek(WebSession webSession, String fwdNr)
    {
        try
        {
            CallCalendar vCalendar = new CallCalendar();
            long vStart = vCalendar.getStartOfToday() - 7*24*60*1000;

            if (fwdNr == null)
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE TimeStamp>" + vStart + " AND IsDocumented=TRUE ORDER BY TimeStamp DESC");
            }
            else
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE FwdNr='" + fwdNr + "' AND TimeStamp>" + vStart + " AND IsDocumented=TRUE ORDER BY TimeStamp DESC");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<CallRecordEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getInvoiceCalls(WebSession webSession, int accountId, long start, long stop)
    {
        Collection<CallRecordEntityData> vCallList = new Vector<CallRecordEntityData>();
        collectInvoiceCalls(webSession, AccountCache.getInstance().get(accountId), vCallList, start, stop);
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
    public Collection<CallRecordEntityData> getSearchCalls(WebSession webSession, String fwdNr, String str2find, int month, int year)
    {
        try
        {
            CallCalendar vCalendar = new CallCalendar();
            long vStartTime = vCalendar.getStartOfMonth(month, year);
            long vEndTime = vCalendar.getEndOfMonth(month, year);

            Vector<CallRecordEntityData> vOutList = new Vector<CallRecordEntityData>();
            Collection<CallRecordEntityData> vCollection = executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE FwdNr='" + fwdNr + "' AND TimeStamp>" + vStartTime + " AND TimeStamp<=" + vEndTime + " AND IsDocumented=TRUE ORDER BY TimeStamp DESC");

            SortAndFilterOnString(vOutList, vCollection, str2find);

            System.out.println("getSearchCalls for " + fwdNr + " during month " + month + " (string=" + str2find + "): " + vOutList.size() + " entries. Org list size " + vCollection.size());
            return vOutList;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<CallRecordEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getIncomingCallsForMonth(WebSession webSession, int month, int year)
    {
        try
        {
            CallCalendar vCalendar = new CallCalendar();
            long vStartTime = vCalendar.getStartOfMonth(month, year);
            long vEndTime = vCalendar.getEndOfMonth(month, year);

            Collection<CallRecordEntityData> vCollection = executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE TimeStamp>" + vStartTime + " AND TimeStamp<=" + vEndTime + " AND IsMailed=TRUE AND isIncomingCall=TRUE");
            return vCollection;
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
            CallCalendar vCalendar = new CallCalendar();
            long vStartTime = vCalendar.getStartOfMonth(month, year);
            long vEndTime = vCalendar.getEndOfMonth(month, year);

            // CallRecordEntityHome callRecordHome = getEntityBean();
            return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE DoneBy='" + empl + "' AND TimeStamp>" + vStartTime + " AND TimeStamp<=" + vEndTime + " AND IsDocumented=TRUE AND IsMailed=TRUE ORDER BY TimeStamp DESC");

            // Collection vCollection = callRecordHome.findDoneBy(empl, vStartTime,
            // vEndTime);
            // return translateToValueObjects(vCollection);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<CallRecordEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getDocumentedNotMailed(WebSession webSession, String fwdNr)
    {
        try
        {
            if (fwdNr == null)
            {
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=TRUE AND IsMailed=FALSE ORDER BY TimeStamp DESC");
            }
            else
            {
            	AccountEntityData customer = AccountCache.getInstance().get(fwdNr);
            	
            	String CustomerIdsIN = "'" + fwdNr + "'";
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
                            	CustomerIdsIN = CustomerIdsIN.concat(",'" + vEntry.getFwdNumber() + "'");
                            }
                        }
                	}
            	}
                return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=TRUE AND IsMailed=FALSE AND FwdNr IN (" + CustomerIdsIN + ") ORDER BY TimeStamp DESC");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
            return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsDocumented=TRUE AND IsChanged=TRUE ORDER BY TimeStamp DESC");
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
            System.out.println("cleanDb removed " + vDummyVec.size() + " call records");
            return vDummyVec.size();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @ejb:interface-method view-type="remote"
     */

    public void removeCallRecord(WebSession webSession, int key)
    {
        CallRecordEntityData vCallRecord = getRow(webSession, key);

        if (vCallRecord.getIsNotLogged() || vCallRecord.getFwdNr().equals(Constants.NUMBER_BLOCK[0][0]) || vCallRecord.getFwdNr().equals(Constants.NUMBER_BLOCK[1][0]) || !vCallRecord.getIsDocumented())
            deleteRow(webSession, key);
        else
        {
            vCallRecord.setIsReleased(true);
            updateRow(webSession, vCallRecord);
        }
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<ReleaseCallData> getAllReleaseData(WebSession webSession)
    {
        Collection<CallRecordEntityData> vCollection = getAllRows(webSession);
        Vector<ReleaseCallData> vResData = new Vector<ReleaseCallData>();
        for (Iterator<CallRecordEntityData> i = vCollection.iterator(); i.hasNext();)
        {
            CallRecordEntityData vEntry = i.next();
            vResData.add(new ReleaseCallData(vEntry.getId(), vEntry.getFwdNr(), vEntry.getTimeStamp(), vEntry.getIsReleased(), vEntry.getIsNotLogged()));
        }

        // System.out.println( "getAllReleaseData: " + vCollection.size() +
        // " entries." );

        return vResData;
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<CallRecordEntityData> getNotLogged(WebSession webSession)
    {
        return executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE IsNotLogged=TRUE ORDER BY TimeStamp DESC");
    }
    
    static final long PERIOD = Constants.DAYS * 20;

    static final long LOOPS = 365 / 20;

    /**
     * @ejb:interface-method view-type="remote"
     */
    public void changeFwdNumber(WebSession webSession, String key, String newNr)
    {
        executeSqlQuery(webSession, "UPDATE CallRecordEntity SET FwdNr='" + newNr + "' WHERE ID='" + key + "'");
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public void removeAccountCalls(WebSession webSession, int accountID)
    {
        System.out.println("removeAccountCalls for " + accountID);
        executeSqlQuery(webSession, "DELETE FROM CallRecordEntity WHERE FwdNr='" + accountID + "'");
    }
    
    private void collectInvoiceCalls(WebSession webSession, AccountEntityData customer, Collection<CallRecordEntityData> callList, long start, long stop)
    {
        // CallRecordEntityHome callRecordHome = getEntityBean();

        Collection<CallRecordEntityData> vCollection = executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE FwdNr='" + customer.getFwdNumber() + "' AND TimeStamp>" + start + " AND TimeStamp<=" + stop + " AND IsDocumented=TRUE AND IsMailed=TRUE ORDER BY TimeStamp DESC");

        if (vCollection != null)
        {
            callList.addAll(vCollection);
        }
        if (customer.getHasSubCustomers())
        {
            Collection<AccountEntityData> vSubCustomerList = AccountCache.getInstance().getSubCustomersList(customer.getId());
            System.out.println(customer.getFullName() + " has " + vSubCustomerList.size() + " sub customers");
            for (Iterator<AccountEntityData> i = vSubCustomerList.iterator(); i.hasNext();)
            {
                AccountEntityData vEntry = i.next();

                if (vEntry.getNoInvoice())
                {
                    collectInvoiceCalls(webSession, vEntry, callList, start, stop);
                }
                else
                {
                    System.out.println("collectInvoiceCalls: " + customer.getFullName() + " has its invoice flag set.");
                }
            }
        }
    }

    private void collectInvoiceCallsHashTable(WebSession webSession, AccountEntityData customer, Hashtable<Integer, Collection<CallRecordEntityData>> callList, long start, long stop)
    {
        // CallRecordEntityHome callRecordHome = getEntityBean();
        Collection<CallRecordEntityData> vCollection = executeSqlQuery(webSession, "SELECT * FROM CallRecordEntity WHERE FwdNr='" + customer.getFwdNumber() + "' AND TimeStamp>" + start + " AND TimeStamp<=" + stop + " AND IsDocumented=TRUE AND IsMailed=TRUE ORDER BY TimeStamp DESC");

        // Collection vCollection =
        // callRecordHome.findDocumentedMailedFromTo(customer.getFwdNumber(),
        // start, stop);
        if (vCollection != null)
        {
            // vCollection = translateToValueObjects(vCollection);
            callList.put(customer.getId(), vCollection);
            System.out.println("collectInvoiceCallsHashTable for customer " + customer.getFullName() + ": " + vCollection.size() + "(" + start + ", " + stop + ")");
        }
        if (customer.getHasSubCustomers())
        {
            Collection<AccountEntityData> vSubCustomerList = AccountCache.getInstance().getSubCustomersList(customer.getId());
            System.out.println(customer.getFullName() + " has " + vSubCustomerList.size() + " sub customers");
            for (Iterator<AccountEntityData> i = vSubCustomerList.iterator(); i.hasNext();)
            {
                AccountEntityData vEntry = i.next();

                if (vEntry.getNoInvoice())
                {
                    collectInvoiceCallsHashTable(webSession, vEntry, callList, start, stop);
                }
                else
                {
                    System.out.println("collectInvoiceCalls: " + customer.getFullName() + " has its invoice flag set.");
                }
            }
        }
    }

    // private SortedMap<Long, CallRecordEntityData> filterMonth(SortedMap<Long,
    // CallRecordEntityData> sortedMap, int monthsBack)
    // {
    // Calendar vCalendar = Calendar.getInstance();
    // long vStartTime = 0;
    // long vStopTime = 0;
    // int vYear = vCalendar.get(Calendar.YEAR);
    // int vMonth = vCalendar.get(Calendar.MONTH);
    // if (monthsBack == 0)
    // {
    // vStopTime = vCalendar.getTimeInMillis() + 10000;
    // vCalendar.set(vYear, vMonth, 0, 0, 0, 0);
    // vStartTime = vCalendar.getTimeInMillis();
    // }
    // else
    // {
    // vMonth -= (monthsBack + 1);
    // if (vMonth < 0)
    // {
    // vMonth = 11;
    // vYear--;
    // }
    // vCalendar.set(vYear, vMonth, 0, 0, 0, 0);
    // vStopTime = vCalendar.getTimeInMillis();
    // if (--vMonth < 0)
    // {
    // vMonth = 11;
    // vYear--;
    // }
    // vCalendar.set(vYear, vMonth, 0, 0, 0, 0);
    // vStartTime = vCalendar.getTimeInMillis();
    // }
    //
    // Set<Long> vKeySet = sortedMap.keySet();
    // for (Iterator<Long> i = vKeySet.iterator(); i.hasNext();)
    // {
    // Long vKey = i.next();
    // CallRecordEntityData vEntry = sortedMap.get(vKey);
    // if (vEntry.getTimeStamp() < vStartTime || vEntry.getTimeStamp() >
    // vStopTime)
    // sortedMap.remove(vKey);
    // }
    // return sortedMap;
    // }

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
                if ((ReplaceNoCaseSensitive(vEntry.getW3_CustomerId(), vLowerFinder, strBuilder)))
                {
                    vEntry.setW3_CustomerId(strBuilder.toString());
                    vRemoveEntry = false;
                }
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

    public void addCallRecord(WebSession webSession, Forum700CallRecord record)
    {
        try
        {
            record.logRecord();
            CallRecordEntityData newRecord = new CallRecordEntityData();
            newRecord.setIsNotLogged(false);
            newRecord.setIsDocumented(false);
            newRecord.setIsReleased(false);
            newRecord.setIsChanged(false);

            if (record.isIncomingCall())
            {
                // search the internal number to forward number mapping.
                int i = 0;
                for (; i < Constants.NUMBER_BLOCK.length; i++)
                {
                    if (record.mInitialUser.equals(Constants.NUMBER_BLOCK[i][2]))
                    // if
                    // (record.mInitialUser.startsWith(Constants.NUMBER_BLOCK[i][1])
                    // ||
                    // record.mInitialUser.startsWith(Constants.NUMBER_BLOCK[i][2])
                    // || record.mInitialUser.endsWith(Constants.NUMBER_BLOCK[i][4]))
                    {
                        newRecord.setFwdNr(Constants.NUMBER_BLOCK[i][0]);
                        if (record.mDuration.equals("00:00:00"))
                        {
                            System.out.println("missed call from " + record.mExtCorrNumber + "!!");
                            newRecord.setIsNotLogged(true);
                            // newRecord.setIsReleased(true);
                            break;
                        }
                        if (i == 0 || i == 2) // TheBusinessAssistant or thuis
                        {
                            System.out.println("Office call (not logged): " + record.mChargedUser + "<--" + record.mExtCorrNumber);
                            // return;
                        }
                        if (i == 1) // Fax
                        {
                            // System.out.println("Fax from " +
                            newRecord.setIsFaxCall(true);
                        }
                        break;
                    }
                }
                if (i == Constants.NUMBER_BLOCK.length)
                {

                    if (record.mInitialUser.equals(record.mChargedUser))
                    {
                        // probably lost call due to forwarding
                        System.out.println("Probably lost forwarded call : " + record.mInitialUser);
                        newRecord.setFwdNr(Constants.NUMBER_BLOCK[0][0]);
                    }
                    else
                    {
                        System.out.println("Not identified call arrived on " + record.mExtCorrNumber);
                        return;
                    }
                }
                newRecord.setIsIncomingCall(true);
            }
            else if (record.mBusinCode.length() != 0)
            {
                newRecord.setIsIncomingCall(false);
                // search the busin code to forward number mapping.
                int i = 0;
                // System.out.println("array length = " +
                // Constants.NUMBER_BLOCK.length);
                for (; i < Constants.NUMBER_BLOCK.length; i++)
                {
                    if (Constants.NUMBER_BLOCK[i][3].startsWith(record.mBusinCode))
                    {
                        newRecord.setFwdNr(Constants.NUMBER_BLOCK[i][0]);
                        break;
                    }
                }
                if (i == Constants.NUMBER_BLOCK.length)
                {
                    System.out.println("Outgoing call with unknown Billing code (" + record.mBusinCode + "): " + record.mInitialUser + "-->" + record.mExtCorrNumber);
                    newRecord.setFwdNr(Constants.NUMBER_BLOCK[0][0]);
                    // return;
                }
            }
            else
            {
                // outgoing call without billingCode.
                // System.out.println("Outgoing call without Billing: " +
                // record.mChargedUser + "-->" + record.mExtCorrNumber);
                newRecord.setFwdNr(Constants.NUMBER_BLOCK[0][0]);
                // return;
            }
            newRecord.setDate(record.mDate);
            newRecord.setTime(record.mTime);
            newRecord.setNumber(record.mExtCorrNumber);
            newRecord.setName("");
            newRecord.setShortDescription("");
            newRecord.setLongDescription("");
            newRecord.setCost(record.mDuration);
            newRecord.setW3_CustomerId("");
            Calendar vCalendar = Calendar.getInstance();
            newRecord.setTimeStamp(vCalendar.getTimeInMillis());

            AccountEntityData vData = AccountCache.getInstance().get(newRecord);

            if (vData != null)
            {
            	newRecord.setAccountId(vData.getId());
            	if (AccountCache.getInstance().isMailEnabled(vData))
                    newRecord.setIsMailed(false);
                else
                    newRecord.setIsMailed(true);
                addRow(webSession, newRecord);
                //System.out.println("addCallRecord: id = " + newRecord.getId() + ", fwdnr=" + newRecord.getFwdNr() + ", isMailed=" + newRecord.getIsMailed());
                sLogger.info("addCallRecord: id={}, fwdnr={}, isMailed={}", newRecord.getId(), newRecord.getFwdNr(), newRecord.getIsMailed());

            }
            else
            {
                sLogger.info("addCallRecord failed: no account found for {}", newRecord.getFwdNr());
            }

            System.out.println("Call logged: " + newRecord.getFwdNr() + (newRecord.getIsIncomingCall() ? "<--" : "-->") + newRecord.getNumber());
            sLogger.info("Call logged: " + newRecord.getFwdNr() + (newRecord.getIsIncomingCall() ? "<--" : "-->") + newRecord.getNumber());
            return;
        }
        catch (Exception e)
        {
            System.out.println("Failed to store the following call record:");
            System.out.println(record.getFileRecord());
            e.printStackTrace();
            sLogger.error("addCallRecord failed");
            sLogger.error("", e);
        }
    }
    
    /**
     * @ejb:interface-method view-type="remote"
     */

    public void deleteCallRecords(WebSession webSession, Collection<CallRecordEntityData> vList)
    {
        int i = 0;
        for (Iterator<CallRecordEntityData> iter = vList.iterator(); iter.hasNext();)
        {
            deleteRow(webSession, iter.next().getId());
            if (++i > 300)
                break; // delete maximum 1000 calls to avoid out of memory
                       // problems
        }
    }

    /**
     * to be removed
     */
    public void releaseCallRecords(WebSession webSession, Collection<CallRecordEntityData> vList)
    {
        for (Iterator<CallRecordEntityData> iter = vList.iterator(); iter.hasNext();)
        {
            CallRecordEntityData vCallRecord = iter.next();
            vCallRecord.setIsReleased(true);
            updateRow(webSession, vCallRecord);
        }
    }

 
    /**
     * @ejb:interface-method view-type="remote"
     */
    public void setCallData(WebSession webSession, CallRecordEntityData data)
    {
       setIsDocumentedFlag(data);
       IntertelCallManager.getInstance().updateOperatorMapping(data, webSession.getSessionId());
       updateRow(webSession, data);
    }
    
    
    /**
     * @ejb:interface-method view-type="remote"
     */
    public void setOperatorLogging(WebSession webSession, CallRecordEntityData data)
    {
        setIsDocumentedFlag(data);
        IntertelCallManager.getInstance().updateOperatorMapping(data, webSession.getSessionId());
        StringBuffer sqlCmd = new StringBuffer("UPDATE CallRecordEntity SET IsAgendaCall=");
        sqlCmd.append(data.getIsAgendaCall()); 
        sqlCmd.append(",IsSmsCall=" + data.getIsSmsCall()); 
        sqlCmd.append(",IsForwardCall=" + data.getIsForwardCall()); 
        sqlCmd.append(",IsImportantCall=" + data.getIsImportantCall()); 
        sqlCmd.append(",IsFaxCall=" + data.getIsFaxCall()); 
        sqlCmd.append(",AccountID=" + data.getAccountId()); 
        sqlCmd.append(",Name='" + ((data.getName() != null) ? escapeQuotes(data.getName()) : "")); 
        sqlCmd.append("',ShortDescription='" + ((data.getShortDescription() != null) ? escapeQuotes(data.getShortDescription()) : "")); 
        sqlCmd.append("',LongDescription='" + ((data.getLongDescription() != null) ? escapeQuotes(data.getLongDescription()) : "")); 
        sqlCmd.append("',DoneBy='" + ((data.getDoneBy() != null) ? data.getDoneBy() : "")); 
        sqlCmd.append("' WHERE Id=" + data.getId());
        executeSqlQuery(webSession, sqlCmd.toString());
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public void setRelease(WebSession webSession, String key)
    {
        setRelease(webSession, Integer.parseInt(key));
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public void setRelease(WebSession webSession, int key)
    {
    	executeSqlQuery(webSession, "UPDATE CallRecordEntity SET IsReleased=TRUE WHERE Id=" + key);
//    	CallRecordEntityData data = getRow(webSession, key);
//        if (data != null)
//        {
//            data.setIsReleased(true);
//            updateRow(webSession, data);
//        }
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

//    	CallRecordEntityData data = getRow(webSession, key);
//        if (data != null)
//        {
//            data.setIsMailed(true);
//            updateRow(webSession, data);
//        }
    }

    /**
     * @ejb:interface-method view-type="remote"
     */

    public void setUnRelease(WebSession webSession, int key)
    {
    	executeSqlQuery(webSession, "UPDATE CallRecordEntity SET IsReleased=FALSE WHERE Id=" + key);
//        CallRecordEntityData data = getRow(webSession, key);
//        if (data != null)
//        {
//            data.setIsReleased(false);
//            updateRow(webSession, data);
//        }
    }

    public void setUnRelease(WebSession webSession, String key)
    {
        setUnRelease(webSession, Integer.parseInt(key));
    }

    public void setShortText(WebSession webSession, int key, String text, boolean isKlant)
    {
    	CallRecordEntityData data = getRow(webSession, key);
        if (data != null)
        {
            String strippedText = text;
            while (strippedText.lastIndexOf("\r\n") >= strippedText.length())
            {
            	strippedText = strippedText.substring(0, strippedText.length());
            }
        	if (isKlant) 
        	{
        		data.setShortDescription(data.getShortDescription() + "<br>&nbsp;&nbsp;<span class=\"bodygreenbold\">" + text + "</span>");
                data.setIsChanged(true);
        	}
        	else
        	{
        		data.setShortDescription(data.getShortDescription() + "<br>" + webSession.getUserId() + ": " + text);
        		data.setIsChanged(false);
            }
        	updateRow(webSession, data);
        }
    }

    public int addIntertelCall(WebSession webSession, IntertelCallData data)
    {
    	CallRecordEntityData newRecord = new CallRecordEntityData();
    	// to be removed:
    	//newRecord.setName(data.intertelCallId.substring(0, 6));

    	if (data.isIncoming)
    	{
    		newRecord.setFwdNr(data.calledNr); 
    		newRecord.setNumber(data.callingNr);
    	}
    	else
    	{
    		newRecord.setFwdNr(data.callingNr);
    		newRecord.setNumber(data.calledNr);
    	}
    	AccountEntityData vData = AccountCache.getInstance().get(newRecord);
    	newRecord.setTimeStamp(data.tsStart*1000);
    	newRecord.setTsStart(data.tsStart);
    	newRecord.setIsIncomingCall(data.isIncoming);
    	Calendar vToday = Calendar.getInstance();
    	newRecord.setDate(String.format("%02d/%02d/%02d", vToday.get(Calendar.DAY_OF_MONTH), vToday.get(Calendar.MONTH) + 1, vToday.get(Calendar.YEAR) - 2000));
    	newRecord.setTime(String.format("%02d:%02d", vToday.get(Calendar.HOUR_OF_DAY), vToday.get(Calendar.MINUTE)));
  	   	int dbId = 0;

        if (vData != null)
        {
        	newRecord.setAccountId(vData.getId());
        	if (AccountCache.getInstance().isMailEnabled(vData))
                newRecord.setIsMailed(false);
            else
                newRecord.setIsMailed(true);
            dbId = addRow(webSession, newRecord);
            System.out.println("addCallRecord: id = " + dbId + ", fwdnr=" + newRecord.getFwdNr() + ", isMailed=" + newRecord.getIsMailed());
            sLogger.info("addCallRecord: id={}, fwdnr={}, isMailed={}", dbId, newRecord.getFwdNr(), newRecord.getIsMailed());
        }
        else
        {
        	System.out.println("unknown customer number: " + data.calledNr);
        }
        return dbId;
    }

    public void setTsAnswer(WebSession webSession, IntertelCallData data)
    {
    	executeSqlQuery(webSession, "UPDATE CallRecordEntity SET TsAnswer='" + data.tsAnswer + "' WHERE ID='" + data.dbRecordId + "'");
    }
    
    public void setTsEnd(WebSession webSession, IntertelCallData data)
    {
    	String text = "";
    	if (data.tsAnswer == 0)
    	{
    		if (data.isIncoming)
    		{
    			text = "ShortDescription='Niet opgenomen', ";
    		}
    		else
    		{
    			text = "ShortDescription='Niet opgenomen', ";
    		}
    	}
    	executeSqlQuery(webSession, "UPDATE CallRecordEntity SET " + text +  "TsEnd='" + data.tsEnd + "', Cost='" + data.getCostStr() + "' WHERE ID='" + data.dbRecordId + "'");
    }

    public void setTransfer(WebSession webSession, IntertelCallData transferedInData, IntertelCallData transferOutData)
    {
		executeSqlQuery(webSession, "UPDATE CallRecordEntity SET IsForwardCall=true WHERE ID='" + transferedInData.dbRecordId + "'");
		executeSqlQuery(webSession, "UPDATE CallRecordEntity SET ShortDescription='Doorgeschakelde oproep van " + transferedInData.callingNr + " naar " + transferOutData.calledNr + "' WHERE ID='" + transferOutData.dbRecordId + "'");
    }
    
    public void setCallingNr(WebSession webSession, IntertelCallData data)
    {
    	executeSqlQuery(webSession, "UPDATE CallRecordEntity SET FwdNr='" + data.callingNr + "' WHERE ID='" + data.dbRecordId + "'");
    }
    
    static public void setIsDocumentedFlag(CallRecordEntityData record)
    {
        if (record.getNumber() != null && record.getNumber().length() != 0 && record.getName() != null && record.getName().length() != 0 && (record.getShortDescription() != null && record.getShortDescription().length() != 0) && (!record.getIs3W_call() || (record.getW3_CustomerId() != null && record.getW3_CustomerId().length() != 0)))
        {
            record.setIsDocumented(true);
        }
        else
        {
            record.setIsDocumented(false);
        }
        //System.out.println("setIsDocumentedFlag = " + record.getIsDocumented());
        //System.out.println("record: " + record.toNameValueString());
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
            entry.setIsReleased(rs.getBoolean(12));
            entry.setIsNotLogged(rs.getBoolean(13));
            entry.setIsAgendaCall(rs.getBoolean(14));
            entry.setIsSmsCall(rs.getBoolean(15));
            entry.setIsForwardCall(rs.getBoolean(16));
            entry.setIsImportantCall(rs.getBoolean(17));
            entry.setIs3W_call(rs.getBoolean(18));
            entry.setIsMailed(rs.getBoolean(19));
            entry.setInvoiceLevel(rs.getShort(20));
            entry.setW3_CustomerId(null2EmpthyString(rs.getString(21)));
            entry.setShortDescription(null2EmpthyString(rs.getString(22)));
            entry.setLongDescription(null2EmpthyString(rs.getString(23)));
            entry.setIsVirgin(rs.getBoolean(24));
            entry.setIsFaxCall(rs.getBoolean(25));
            entry.setIsChanged(rs.getBoolean(26));
            entry.setDoneBy(null2EmpthyString(rs.getString(27)));
            entry.setTsStart(rs.getLong(28));
            entry.setTsAnswer(rs.getLong(29));
            entry.setTsEnd(rs.getLong(30));
            vVector.add(entry);
        }
        return vVector;
    }
}
