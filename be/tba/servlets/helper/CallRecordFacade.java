package be.tba.servlets.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.pbx.interfaces.CallRecordEntityData;
import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.AccountRole;
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
    
    public static void updateShortText(HttpServletRequest req, WebSession session, boolean isCustomer)
    {
    	//System.out.println("updateShortText()");
    	String vKey = (String) req.getParameter(Constants.RECORD_ID);
        String shortText = (String) req.getParameter(Constants.RECORD_SHORT_TEXT);
        if (shortText != null)
        {
            CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
            vQuerySession.setShortText(session, Integer.parseInt(vKey), shortText, isCustomer);
        }
    }

    /*
     * this method saves (updates) a record that was opened from the call list.
     * 
     */
    public static void saveRecord(HttpServletRequest req, WebSession session)
    {
    	System.out.println("saveRecord()");
    	AccountEntityData vNewCustomer = null;
    	CallRecordSqlAdapter vCallLogWriterSession = new CallRecordSqlAdapter();

        // Check the record and add it if it is a valid one.

        CallRecordEntityData vCallData = session.getCurrentRecord();
        if (vCallData == null)
        {
            System.out.println("AdminDispatchServlet: no call record in session context (SAVE RECORD)");
        }
        else
        {
            AccountEntityData vOldCustomer = AccountCache.getInstance().get(vCallData);
            if (vOldCustomer.getHasSubCustomers() && req.getParameter(Constants.ACCOUNT_SUB_CUSTOMER) != null)
            {
                vCallData.setFwdNr(req.getParameter(Constants.ACCOUNT_SUB_CUSTOMER));
                AccountEntityData newCustomer = AccountCache.getInstance().get(vCallData.getFwdNr()); // take FwdNr because vCallDatat still has the previous accountId and shall make that new FwdNr shall be skipped.
                vCallData.setAccountId(newCustomer.getId());
                System.out.println("Super customer call: set fwd number to " + vCallData.getFwdNr() + ", new account ID=" + newCustomer.getId());
            }
            else
            {
                String newFwdNr = req.getParameter(Constants.ACCOUNT_FORWARD_NUMBER);
            	if (newFwdNr != null)
                {
                    if (!vCallData.getFwdNr().equals(newFwdNr))
                    {
                    	// customer changed!! Check the isMailed flag.
                        vCallData.setFwdNr(newFwdNr);
                        vNewCustomer = AccountCache.getInstance().get(newFwdNr);
                        vCallData.setAccountId(vNewCustomer.getId());

                        if (AccountCache.getInstance().isMailEnabled(vNewCustomer))
                            vCallData.setIsMailed(false);
                        else
                            vCallData.setIsMailed(true);
                    }
                }
            }
            if (vCallData.getAccountId() == 0)
            {
            	AccountEntityData customer = AccountCache.getInstance().get(vCallData);
                if (customer != null)
                	vCallData.setAccountId(customer.getId());
            }
            vCallData.setNumber((String) req.getParameter(Constants.RECORD_NUMBER));
            vCallData.setName((String) req.getParameter(Constants.RECORD_CALLER_NAME));
            vCallData.setShortDescription((String) req.getParameter(Constants.RECORD_SHORT_TEXT));
            vCallData.setLongDescription((String) req.getParameter(Constants.RECORD_LONG_TEXT));
            vCallData.setIsSmsCall(req.getParameter(Constants.RECORD_SMS) != null);
            vCallData.setIsAgendaCall(req.getParameter(Constants.RECORD_AGENDA) != null);
            vCallData.setIsForwardCall(req.getParameter(Constants.RECORD_FORWARD) != null);
            boolean prevIsImportant = vCallData.getIsImportantCall();
            vCallData.setIsImportantCall(req.getParameter(Constants.RECORD_IMPORTANT) != null);
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
            vCallData.setIsChanged(false);
            vCallData.setDoneBy(session.getUserId());
            vCallLogWriterSession.setCallData(session, vCallData);
            printCallInsert(session, vCallData);
            if (!prevIsImportant && vCallData.getIsImportantCall() && vCallData.getIsDocumented())
            {
                MailNowTask.send(vCallData.getAccountId());
            }
        }
    }

    /*
     * this method is called when the operator creates a call from scratch, and knowing already for what
     * customer she is creating it. This call shall be saved in the database as a fully valid and documented call.
     * So this call shall not be stored as a 'unmapped' call that must be linked to an incoming call afterwards. 
     */
    public static void saveManualRecord(HttpServletRequest req, WebSession session)
    {
    	System.out.println("saveManualRecord()");
    	CallRecordEntityData newRecord = new CallRecordEntityData();
        Calendar vCalendar = Calendar.getInstance();
        newRecord.setIsNotLogged(false);
        newRecord.setIsReleased(false);
        newRecord.setIsVirgin(false);
        newRecord.setIsChanged(false);

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

        AccountEntityData vData = AccountCache.getInstance().get(newRecord);
        newRecord.setAccountId(vData.getId());
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
            MailNowTask.send(vData.getId());
        }

        // Check the record and add it if it is a valid one.
        System.out.println("saveManualRecord: id=" + newRecord.getId() + ", cust=" + newRecord.getFwdNr() + ", number=" + newRecord.getNumber());
        CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
        vQuerySession.addRow(session, newRecord);
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

    /* This method is called when a new call is created from scratch. An incoming call shall (can be) be selected later
     * when the call is ended and recorded by the call log. 
     * For the time being this new 'unmapped' call is saved in the session context of the operator. 
     * 
     */
    public static void createNewUnmappedCall(HttpServletRequest req, WebSession webSession)
    {
    	System.out.println("createNewUnmappedCall()");
    	CallRecordEntityData newRecord = new CallRecordEntityData();
        newRecord.setIsNotLogged(false);
        newRecord.setIsReleased(false);
        newRecord.setIsChanged(false);
        newRecord.setIsAgendaCall(req.getParameter(Constants.RECORD_AGENDA) != null);
        newRecord.setIsSmsCall(req.getParameter(Constants.RECORD_SMS) != null);
        newRecord.setIsForwardCall(req.getParameter(Constants.RECORD_FORWARD) != null);
        newRecord.setIsImportantCall(req.getParameter(Constants.RECORD_IMPORTANT) != null);
        newRecord.setIsFaxCall(req.getParameter(Constants.RECORD_FAX) != null);
        newRecord.setName((String) req.getParameter(Constants.RECORD_CALLER_NAME));
        newRecord.setShortDescription((String) req.getParameter(Constants.RECORD_SHORT_TEXT));
        newRecord.setLongDescription((String) req.getParameter(Constants.RECORD_LONG_TEXT));
        newRecord.setDoneBy(webSession.getUserId());

        webSession.setNewUnmappedCall(newRecord);
    }

    // this function is called:
    //   - when a new not yet call is created (none existing that shal be mapped later on a logged call
    //   - to refresh the new call page with an updated list of incoming calls
    // so that the user can select his call form this list.
    // because this is a server loop call, possible changes to that new call data must be stored
    // temporally in the NewUnmappedCalls Map in the WebSession so that the jsp can take that data
    // and show it on the refreshed jsp page.
    public static void updateNewUnmappedCall(HttpServletRequest req, WebSession webSession)
    {
    	System.out.println("updateNewUnmappedCall()");
    	CallRecordEntityData newRecord = webSession.getNewUnmappedCall();
        if (newRecord == null)
        {
            // strange situation
            //System.out.println("updateNewUnmappedCall called with a valid NewUnmappedCall");
            // make a new one.
            newRecord = new CallRecordEntityData();
            webSession.setNewUnmappedCall(newRecord);
        }
        //System.out.println("updateNewUnmappedCall");

        newRecord.setIsNotLogged(false);
        newRecord.setIsReleased(false);
        newRecord.setIsChanged(false);
        newRecord.setIsAgendaCall(req.getParameter(Constants.RECORD_AGENDA) != null);
        newRecord.setIsSmsCall(req.getParameter(Constants.RECORD_SMS) != null);
        newRecord.setIsForwardCall(req.getParameter(Constants.RECORD_FORWARD) != null);
        newRecord.setIsImportantCall(req.getParameter(Constants.RECORD_IMPORTANT) != null);
        newRecord.setIsFaxCall(req.getParameter(Constants.RECORD_FAX) != null);
        newRecord.setName((String) req.getParameter(Constants.RECORD_CALLER_NAME));
        newRecord.setShortDescription((String) req.getParameter(Constants.RECORD_SHORT_TEXT));
        newRecord.setLongDescription((String) req.getParameter(Constants.RECORD_LONG_TEXT));
        newRecord.setDoneBy(webSession.getUserId());
    }

    /*
     * called from the new call page when a recorded (from PBX) call is selected. Both recorded and already created 
     * new call shall be merged into the recorded call in the database.
     */
    public static boolean saveNewCall(HttpServletRequest req, WebSession webSession)
    {
    	System.out.println("saveNewCall()");
    	updateNewUnmappedCall(req, webSession);
        
        CallRecordEntityData vNewCall = webSession.getNewUnmappedCall();
        String vKey = (String) req.getParameter(Constants.RECORD_ID);

        //System.out.println("saveNewCall: id = " + vKey);

        CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
        CallRecordEntityData vNewRecord = vQuerySession.getRecord(webSession, vKey);

        if (vNewRecord != null)
        {
            vNewRecord.setName(vNewCall.getName());
            vNewRecord.setShortDescription(vNewCall.getShortDescription());
            vNewRecord.setLongDescription(vNewCall.getLongDescription());
            vNewRecord.setIsSmsCall(vNewCall.getIsSmsCall());
            vNewRecord.setIsAgendaCall(vNewCall.getIsAgendaCall());
            vNewRecord.setIsForwardCall(vNewCall.getIsForwardCall());
            vNewRecord.setIsFaxCall(vNewCall.getIsFaxCall());
            vNewRecord.setIsVirgin(false);
            vNewRecord.setIsNotLogged(false);
            vNewRecord.setIsChanged(false);
            vNewRecord.setDoneBy(webSession.getUserId());
            if (vNewRecord.getAccountId() == 0) 
            {
                AccountEntityData vData = AccountCache.getInstance().get(vNewRecord);
                vNewRecord.setAccountId(vData.getId());
            }
            if (!vNewRecord.getIsImportantCall() && vNewCall.getIsImportantCall())
            {
                MailNowTask.send(vNewRecord.getAccountId());
            }
            vQuerySession.setCallData(webSession, vNewRecord);
            vNewCall.setFwdNr(vNewRecord.getFwdNr());
            vNewCall.setAccountId(vNewRecord.getAccountId());
            vNewCall.setId(vNewRecord.getId());
            
            Collection <AccountEntityData> subcustomers = AccountCache.getInstance().getSubCustomersList(vNewRecord.getAccountId());
            //System.out.println("saveNewCall: id = " + vNewRecord.getId());
            if (subcustomers != null && !subcustomers.isEmpty())
            {
            	System.out.println("subcustomer must be selected(account id=" + vNewRecord.getAccountId());
            	for (Iterator<AccountEntityData> iter = subcustomers.iterator(); iter.hasNext();)
            	{
            		AccountEntityData acc = iter.next();
            		//System.out.println("subcustomer: " + acc.getFullName());
            	}
                //System.out.println("there are subcustomers. Set the super customer=" + vNewRecord.getFwdNr() + ", record key=" + vKey);
                // set the fwdNr of the super customer so that selectSubCustomer.jsp can prepare the sub customers list
                //req.setAttribute(Constants.ACCOUNT_ID, vNewCall.getId());
                webSession.setRecordId(vKey);
                return true;
            }
            else
            {
                // no sub customers: this call can be saved and finished processing
                
                webSession.setNewUnmappedCall(null);
                webSession.setRecordId(null);
            }
        }
        return false;
    }

    public static void saveNewSubCustomer(HttpServletRequest req, WebSession webSession, String vNewFwdNr)
    {
    	System.out.println("saveNewSubCustomer()");
    	CallRecordSqlAdapter vCallLogWriterSession = new CallRecordSqlAdapter();
        vCallLogWriterSession.changeFwdNumber(webSession, webSession.getRecordId(), vNewFwdNr);
        webSession.setNewUnmappedCall(null);
        webSession.setRecordId(null);
    }

    // 'terug' button was called on the newCall page. User want to cancel this entry.
    // look for a call with this key in the WebSession NewUnmappedCalls, and remove it.
    public static void removeNewCall(HttpServletRequest req, WebSession webSession)
    {
    	System.out.println("removeNewCall()");
    	webSession.setNewUnmappedCall(null);
        webSession.setRecordId(null);
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
    	Calendar calendar = Calendar.getInstance();

        lock.lock();
        try
        {
            FileOutputStream logStream = getCallLogFileStream(calendar);
            logStream.write((calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + " " +  session.getUserId() + ": " + CallRecordEntityData.toSqlInsertString(record)).getBytes());
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

    private static FileOutputStream getCallLogFileStream(Calendar calendar) throws IOException
    {
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
