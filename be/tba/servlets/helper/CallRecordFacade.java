package be.tba.servlets.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
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
    
    public static void updateShortText(HttpServletRequest req, WebSession session)
    {
        String vKey = (String) req.getParameter(Constants.RECORD_ID);
        String shortText = (String) req.getParameter(Constants.RECORD_SHORT_TEXT);

        CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
        vQuerySession.setShortText(session, Integer.parseInt(vKey), shortText, false);
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
            vCallData.setIsChanged(false);
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
                if (!record.getIsDocumented() || session.getRole() == AccountRole.ADMIN)
                {
                    vQuerySession.deleteRow(session, key);
                    printCallDelete(session, key);
                }
            }
        }
    }

    // new call is created
    public static void createNewUnmappedCall(HttpServletRequest req, WebSession webSession)
    {
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

    // this function is to refresh the new call page with an updated list of incoming calls
    // so that the user can select his call form this list.
    // because this is a server loop call, possible changes to that new call data must be stored
    // temporally in the NewUnmappedCalls Map in the WebSession so that the jsp can take that data
    // and show it on the refreshed jsp page.
    public static void updateNewUnmappedCall(HttpServletRequest req, WebSession webSession)
    {
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
        newRecord.setDoneBy(webSession.getUserId());
    }

    public static boolean saveNewCall(HttpServletRequest req, WebSession webSession)
    {
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
            if (!vNewRecord.getIsImportantCall() && vNewCall.getIsImportantCall())
            {
                MailNowTask.send(vNewRecord.getFwdNr());
            }
            vNewRecord.setIsForwardCall(vNewCall.getIsForwardCall());
            vNewRecord.setIsFaxCall(vNewCall.getIsFaxCall());
            vNewRecord.setIsVirgin(false);
            vNewRecord.setIsNotLogged(false);
            vNewRecord.setIsChanged(false);
            vNewRecord.setDoneBy(webSession.getUserId());
            vQuerySession.setCallData(webSession, vNewRecord);
            vNewCall.setFwdNr(vNewRecord.getFwdNr());
            vNewCall.setId(vNewRecord.getId());
            
            Collection <AccountEntityData> subcustomers = AccountCache.getInstance().getSubCustomersList(vNewRecord.getFwdNr());
            //System.out.println("saveNewCall: id = " + vNewRecord.getId());
            if (subcustomers != null && !subcustomers.isEmpty())
            {
                //System.out.println("there are subcustomers. Set the super customer=" + vNewRecord.getFwdNr() + ", record key=" + vKey);
                // set the fwdNr of the super customer so that selectSubCustomer.jsp can prepare the sub customers list
                //req.setAttribute(Constants.ACCOUNT_ID, vNewCall.getFwdNr());
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
        CallRecordSqlAdapter vCallLogWriterSession = new CallRecordSqlAdapter();
        vCallLogWriterSession.changeFwdNumber(webSession, webSession.getRecordId(), vNewFwdNr);
        webSession.setNewUnmappedCall(null);
        webSession.setRecordId(null);
    }

    // 'terug' button was called on the newCall page. User want to cancel this entry.
    // look for a call with this key in the WebSession NewUnmappedCalls, and remove it.
    public static void removeNewCall(HttpServletRequest req, WebSession webSession)
    {
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
