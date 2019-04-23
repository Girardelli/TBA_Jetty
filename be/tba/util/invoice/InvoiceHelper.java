package be.tba.util.invoice;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.invoice.interfaces.InvoiceEntityData;
import be.tba.ejb.invoice.session.InvoiceSqlAdapter;
import be.tba.ejb.pbx.interfaces.CallRecordEntityData;
import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.ejb.task.interfaces.TaskEntityData;
import be.tba.ejb.task.session.TaskSqlAdapter;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;
import be.tba.util.session.AccountCache;
import be.tba.util.timer.CallCalendar;

import java.util.Hashtable;

class Tarifs
{
    int group;

    String sign;

    int minCalls;

    double startCost;

    double extraCost;

    Tarifs(int group, String sign, int minCalls, double startCost, double extraCost)
    {
        this.group = group;
        this.sign = sign;
        this.minCalls = minCalls;
        this.startCost = startCost;
        this.extraCost = extraCost;
    }
}

/*
 * import jxl.demo.*; import jxl.format.*; import jxl.write.*; import jxl.*;
 */
public class InvoiceHelper
{
    // -------------------------------------------------------------------------
    // Static
    // -------------------------------------------------------------------------

    // agenda invoice types
    public static final short kNoAgenda = 0;

    public static final short kPercentageOnAgendaCalls = 1;

    public static final short kPercentageOnTotalCallCost = 2;

    public static final short kEuroCentOnAgendaCalls = 3;

    public static final short kEuroCentOnAllCalls = 4;

    public static final short kStandardAgenda = 5;

    // call invoice types
    public static final short kStandardInvoice = 0;

    public static final short kCustomInvoice = 1;

    public static final short kTelemarketingInvoice = 2;

    public static final short kNoCallsAccount = 3;

    public static final short kWeekInvoice = 4;

    // call invoice types
    public static final short kLevel1 = 0;

    public static final short kLevel2 = 1;

    public static final short kLevel3 = 2;

    /*
     * static Tarifs[] mTarifs;
     * 
     * static { mTarifs = new Tarifs[] { new Tarifs(1, "I", 75, 110.00, 1.0), new
     * Tarifs(2, "II", 40, 80.00, 1.00), new Tarifs(3, "III forfait", 0, 80.00,
     * 0.00), new Tarifs(4, "IV", 12, 60.00, 1.45), // new Tarifs(1, "I", 75, 80.00,
     * 0.75), // new Tarifs(2, "II", 40, 50.00, 1.00), // new Tarifs(3,
     * "III forfait", 0, 50.00, 0.00), // new Tarifs(4, "IV", 12, 30.00, 1.20), //
     * week tarief }; }
     */
    // -------------------------------------------------------------------------
    // Members
    // -------------------------------------------------------------------------
    // private int mMonth;
    // private int mYear;
    // private PrintWriter mInvoiceFile;

    // private WritableSheet mSheet;
    // private jxl.format.CellFormat mBasicCellFormat;

    private Collection<CallRecordEntityData> mRecords;
    private Hashtable<String, Collection<CallRecordEntityData>> mRecordsHashTable = null;
    private Hashtable<String, Collection<TaskEntityData>> mTasksHashTable = null;

    private Tarifs[] mTarifs = new Tarifs[4];

    private Collection<TaskEntityData> mTasks;

    private CallCounts mCallCounts = new CallCounts();
    private CustomerData mCustomerData = new CustomerData();
    private InvoiceData mInvoiceData = new InvoiceData();
    private Collection<SubcustomerCost> mSubcustomerCostList = new Vector<SubcustomerCost>();
    private boolean mNoValidCustomer;
    private AccountEntityData mAccountEntityData;
    private InvoiceEntityData mInvoiceEntityData;
    private boolean mIsInitialized = false;

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    public InvoiceHelper(WebSession webSession, String accountFwdNr, int month, int year)
    {
        try
        {
            System.out.println("InvoiceHelper(Account, month, year)");
            mAccountEntityData = AccountCache.getInstance().get(accountFwdNr);
            if (mAccountEntityData == null)
            {
                mNoValidCustomer = true;
            }
            else
            {
                mNoValidCustomer = false;
                mInvoiceData.Month = month;
                mInvoiceData.Year = year;
                mInvoiceData.CostFormatter = new DecimalFormat("#0.00");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public InvoiceHelper(InvoiceEntityData invoiceData, WebSession webSession)
    {
        try
        // bij invoice generate wordt deze constructor gebruikt ???
        {
            System.out.println("InvoiceHelper(InvoiceData=");
            if (invoiceData == null)
            {
                throw (new Exception("InvoiceHelper called with invoiceData=null"));
            }
            mInvoiceEntityData = invoiceData;
            mAccountEntityData = AccountCache.getInstance().get(mInvoiceEntityData.getAccountFwdNr());
            if (mAccountEntityData == null)
            {
                mNoValidCustomer = true;
            }
            else
            {
                mNoValidCustomer = false;
                mInvoiceData.Id = mInvoiceEntityData.getId();
                mInvoiceData.Month = mInvoiceEntityData.getMonth();
                mInvoiceData.Year = mInvoiceEntityData.getYear();
                mInvoiceData.CostFormatter = new DecimalFormat("#0.00");
                mInvoiceData.InvoiceNr = mInvoiceEntityData.getInvoiceNr();
                mInvoiceData.StructuredId = mInvoiceEntityData.getStructuredId();
                mInvoiceData.CustomerRef = mInvoiceEntityData.getCustomerRef();
                mInvoiceData.PayDate = (mInvoiceEntityData.getPayDate() == null ? "" : mInvoiceEntityData.getPayDate());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void storeOrUpdate(WebSession webSession)
    {
        if (mNoValidCustomer) 
        {
        	return;
        }
    	mIsInitialized = true;
        CallCalendar vCalendar = new CallCalendar();
        long vStart = vCalendar.getStartOfMonth(mInvoiceData.Month, mInvoiceData.Year);
        long vEnd = vCalendar.getEndOfMonth(mInvoiceData.Month, mInvoiceData.Year);

        mCustomerData.setId(mAccountEntityData.getId());
        mCustomerData.setAddress1(mAccountEntityData.getStreet());
        mCustomerData.setAddress2(mAccountEntityData.getCity());
        mCustomerData.setBtwNr(mAccountEntityData.getBtwNumber());
        mCustomerData.setName(mAccountEntityData.getCompanyName());
        mCustomerData.setTaskHourRate(mAccountEntityData.getTaskHourRate());
        mCustomerData.setTAV(mAccountEntityData.getAttToName());

        if (mAccountEntityData.getInvoiceType() == InvoiceHelper.kCustomInvoice)
            mAccountEntityData.setInvoiceType(InvoiceHelper.kStandardInvoice);

        mTarifs[0] = new Tarifs(1, "I", mAccountEntityData.getFacTblMinCalls_I(), mAccountEntityData.getFacTblStartCost_I(), mAccountEntityData.getFacTblExtraCost_I());
        mTarifs[1] = new Tarifs(2, "II", mAccountEntityData.getFacTblMinCalls_II(), mAccountEntityData.getFacTblStartCost_II(), mAccountEntityData.getFacTblExtraCost_II());
        mTarifs[2] = new Tarifs(3, "III forfait", mAccountEntityData.getFacTblMinCalls_III(), mAccountEntityData.getFacTblStartCost_III(), mAccountEntityData.getFacTblExtraCost_III());
        mTarifs[3] = new Tarifs(4, "IV", mAccountEntityData.getFacTblMinCalls_IV(), mAccountEntityData.getFacTblStartCost_IV(), mAccountEntityData.getFacTblExtraCost_IV());

        // System.out.println("Set new pricing for " +
        // mAccountEntityData.getFullName());
        // mTarifs[0] = new Tarifs(1, "I", 75, 110.00, 1.0);
        // mTarifs[1] = new Tarifs(2, "II", 40, 80.00, 1.00);
        // mTarifs[2] = new Tarifs(3, "III forfait", 0, 80.00, 0.00);
        // mTarifs[3] = new Tarifs(4, "IV", 12, 60.00, 1.45);

        // mAccountEntityData.setCountAllLongCalls(false);
        // mAccountEntityData.setCountLongFwdCalls(false);
        // mAccountEntityData.setFacLongFwd(0.0);//Constants.CENT_PER_LONG_CALL_SECOND);
        // mAccountEntityData.setFacLong(0.0);//Constants.CENT_PER_LONG_CALL_SECOND);

        InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
        CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();

        // if (mAccountEntityData.getFacLongFwd() == 0.0)
        // {
        // mAccountEntityData.setFacLongFwd(Constants.CENT_PER_LONG_CALL_SECOND);
        // }
        // if (mAccountEntityData.getFacLong() == 0.0)
        // {
        // mAccountEntityData.setFacLong(Constants.CENT_PER_LONG_CALL_SECOND);
        // }

        mCallCounts.LastInvoiceItem = vStart + 1;

        System.out.println("generate invoice for " + mAccountEntityData.getFullName());
        if (mInvoiceEntityData == null)
        {
            mInvoiceEntityData = vInvoiceSession.getLastInvoice(webSession, mAccountEntityData.getFwdNumber(), mInvoiceData.Month, mInvoiceData.Year);

            if (mInvoiceEntityData != null)
            {
                // There is already an invoice for this period
                if (mInvoiceEntityData.getCreditId() >= 0)
                {
                    // start counting again at the old start point.
                    vStart = mInvoiceEntityData.getStartTime();
                    System.out.println(mAccountEntityData.getFwdNumber() + ": Last invoice was a credit note: make new on for this months");
                    mInvoiceEntityData = null;
                }
                else if (mInvoiceEntityData.getFrozenFlag())
                {
                    // the last invoice for this month was frozen and had nothing to do with credit
                    // note's: make a new one
                    // starting from the stopTime of this last one.
                    vStart = mInvoiceEntityData.getStopTime() + 1;
                    mInvoiceEntityData = null;
                    System.out.println(mAccountEntityData.getFwdNumber() + " : Last invoice was frozen: make a new one");
                }
                else
                {
                    // the last invoice is not frozen: update this invoice with the
                    // figures till now.
                    vStart = mInvoiceEntityData.getStartTime();
                    System.out.println(mAccountEntityData.getFwdNumber() + ": Last invoice was not frozen: update this one");
                }
                // mRecords = vQuerySession.getInvoiceCalls(vSession,
                // mAccountEntityData.getFwdNumber(), mMonth, mYear);
            }
            else
            {
                System.out.println(mAccountEntityData.getFwdNumber() + " : no invoices yet for this number for this month. Make one.");

                // first invoice for this month: get all calls from the beginning of the month
            }
        }
        else
        {
            // There is already an invoice for this period
            if (mInvoiceEntityData.getCreditId() >= 0)
            {
                System.out.println(mAccountEntityData.getFwdNumber() + ": credit note or original invoice was provided: show the frozen data.\r\nnew Start: " + vStart + " ==> " + mInvoiceEntityData.getStartTime() + "\r\nnew Stop:  " + vEnd + " ==> " + mInvoiceEntityData.getStopTime());
                vStart = mInvoiceEntityData.getStartTime();
                vEnd = mInvoiceEntityData.getStopTime();
            }
            else if (mInvoiceEntityData.getFrozenFlag())
            {
                // the last invoice for this month was frozen: make a new one
                // starting from the stopTime of this last one.
                System.out.println(mAccountEntityData.getFwdNumber() + " : frozen invoice data, not related to credit note, provided by caller: show the frozen data.\r\nnew Start: " + vStart + " ==> " + mInvoiceEntityData.getStartTime() + "\r\nnew Stop:  " + vEnd + " ==> " + mInvoiceEntityData.getStopTime());
                vStart = mInvoiceEntityData.getStartTime();
                vEnd = mInvoiceEntityData.getStopTime();
            }
            else
            {
                // the last invoice is not frozen: update this invoice with the
                // figures till now.
                System.out.println(mAccountEntityData.getFwdNumber() + " : NOT frozen invoice data provided by caller: update this one.\r\nnew Start: " + vStart + " ==> " + mInvoiceEntityData.getStartTime());
                vStart = mInvoiceEntityData.getStartTime();
            }
        }
        // System.out.println("Calls from " + vStart + ", " + vEnd);

        /*
         * create one big list of call record. The getInvoiceCallsHashTable returns a
         * hash table with a record list for each subcustomer including the records of
         * the super customer. Only sub customers that have 'no invoice' will be taken
         * into account. The other ones will get a separate invoice. On this super list,
         * we will make the cost calculations.
         */
        if (mAccountEntityData.getHasSubCustomers())
        {
            mRecordsHashTable = vQuerySession.getInvoiceCallsHashTable(webSession, mAccountEntityData.getFwdNumber(), vStart, vEnd);
            // System.out.println("subcust for " +
            // mAccountEntityData.getFwdNumber() + ": " +
            // mRecordsHashTable.size());
            if (mRecords == null)
            {
                mRecords = new Vector<CallRecordEntityData>();
            }
            for (Iterator<Collection<CallRecordEntityData>> i = mRecordsHashTable.values().iterator(); i.hasNext();)
            {
                Collection<CallRecordEntityData> vSubCalls = i.next();
                // CallCounts subCallsCount = new CallCounts();
                // setCounters(subCallsCount, vSubCalls, null);
                // System.out.println("Calls for " + nr + ": " +
                // subCallsCount.mTotalCalls);
                // if (mRecords == null)
                // {
                // mRecords = vSubCalls;
                // }
                // else
                // {
                // mRecords.addAll(vSubCalls);
                // }
                mRecords.addAll(vSubCalls);
            }
        }
        else
        {
            mRecords = vQuerySession.getInvoiceCalls(webSession, mAccountEntityData.getFwdNumber(), vStart, vEnd);
        }

        // System.out.println(mAccountEntityData.getFwdNumber() + " : has " +
        // mRecords.size() + " calls");

        setCounters(mCallCounts, mRecords, mAccountEntityData);

        mInvoiceData.Type = mAccountEntityData.getInvoiceType();
        mInvoiceData.AgendaCost = 0;
        mInvoiceData.CallsCost = 0.0;
        boolean vNewInvoiceRequired = false;
        System.out.println("invoice type = " + mInvoiceData.Type);
        if (mInvoiceData.Type == kStandardInvoice || (mInvoiceData.Type == kWeekInvoice && (mCallCounts.TotalCalls > 0 || mCallCounts.SmsCalls > 0 || mCallCounts.FwdCalls > 0 || mCallCounts.FaxCalls > 0)))
        {
            vNewInvoiceRequired = true;
            int vTarifIndex = 3; // week tarif
            if (mInvoiceData.Type != kWeekInvoice)
            {
                vTarifIndex = getInvoiceGroup() - 1;
            }
            mInvoiceData.TarifGroup = mTarifs[vTarifIndex].sign;
            mInvoiceData.InCost = mTarifs[vTarifIndex].startCost;
            if (mCallCounts.InCalls > mTarifs[vTarifIndex].minCalls)
            {
                mInvoiceData.InCost += ((double) (mCallCounts.InCalls - mTarifs[vTarifIndex].minCalls) * mTarifs[vTarifIndex].extraCost);
            }
            mInvoiceData.InUnitCost = (mCallCounts.InCalls != 0) ? (mInvoiceData.InCost / mCallCounts.InCalls) : mTarifs[vTarifIndex].startCost;
            mInvoiceData.OutUnitCost = mAccountEntityData.getFacStdOutCall() / 100.0;
            mInvoiceData.OutCost = mCallCounts.OutCalls * mInvoiceData.OutUnitCost;
            mInvoiceData.CallsCost = mInvoiceData.InCost + mInvoiceData.OutCost;
            mInvoiceData.CallsUnitCost = (mCallCounts.InCalls * mInvoiceData.InUnitCost + mCallCounts.OutCalls * mInvoiceData.OutUnitCost) / mCallCounts.TotalCalls;
        }
        else if (mInvoiceData.Type == kTelemarketingInvoice)
        {
            mInvoiceData.Level1UnitCost = mAccountEntityData.getFacOutLevel1() / 100;
            mInvoiceData.Level2UnitCost = mAccountEntityData.getFacOutLevel2() / 100;
            mInvoiceData.Level3UnitCost = mAccountEntityData.getFacOutLevel3() / 100;
            mInvoiceData.Level1Cost = (double) mCallCounts.Level1Calls * mInvoiceData.Level1UnitCost;
            mInvoiceData.Level2Cost = (double) mCallCounts.Level2Calls * mInvoiceData.Level2UnitCost;
            mInvoiceData.Level3Cost = (double) mCallCounts.Level3Calls * mInvoiceData.Level3UnitCost;
        }
        else if (mInvoiceData.Type == kNoCallsAccount)
        {
            // do nothing
        }

        switch (mAccountEntityData.getAgendaPriceUnit())
        {
        case kStandardAgenda:
            mCallCounts.AgendaCalls = 0;
            mInvoiceData.AgendaCost = mAccountEntityData.getFacAgendaCall();
            mInvoiceData.AgendaCostString = new String("vaste prijs van " + mInvoiceData.AgendaCost + " euro");
            break;

        case kPercentageOnAgendaCalls:
            mInvoiceData.AgendaCost = (double) mCallCounts.AgendaCalls * mInvoiceData.InUnitCost * mAccountEntityData.getFacAgendaCall() / 100;
            mInvoiceData.AgendaCostString = new String(mAccountEntityData.getFacAgendaCall() + "% op agenda oproepen");
            break;

        case kEuroCentOnAgendaCalls:
            mInvoiceData.AgendaCost = (double) mCallCounts.AgendaCalls * mAccountEntityData.getFacAgendaCall() / 100;
            mInvoiceData.AgendaCostString = new String((mAccountEntityData.getFacAgendaCall() / 100.0) + "Euro op agenda oproepen");
            break;

        case kPercentageOnTotalCallCost:
            mCallCounts.AgendaCalls = 0;
            mInvoiceData.AgendaCost = (double) mInvoiceData.CallsCost * mAccountEntityData.getFacAgendaCall() / 100;
            mInvoiceData.AgendaCostString = new String(mAccountEntityData.getFacAgendaCall() + "% op oproepen kost");
            break;

        case kEuroCentOnAllCalls:
            mCallCounts.AgendaCalls = mCallCounts.InCalls + mCallCounts.OutCalls;
            mInvoiceData.AgendaCost = (double) mCallCounts.AgendaCalls * mAccountEntityData.getFacAgendaCall() / 100;
            mInvoiceData.AgendaCostString = new String((mAccountEntityData.getFacAgendaCall() / 100.0) + "Euro op alle oproepen");
            break;

        default:
            mCallCounts.AgendaCalls = 0;
            mInvoiceData.AgendaCost = 0;
            mInvoiceData.AgendaCostString = new String("geen agendabeheer");
            break;
        }

        // Task part

        mInvoiceData.TaskCost = 0;
        TaskSqlAdapter vTaskSession = new TaskSqlAdapter();

        if (mAccountEntityData.getHasSubCustomers())
        {
            mTasksHashTable = vTaskSession.getTasksFromTillTimestampHashtable(webSession, mAccountEntityData.getFwdNumber(), vStart, vEnd);
            if (mTasks == null)
            {
                mTasks = new Vector<TaskEntityData>();
            }
            for (Iterator<Collection<TaskEntityData>> i = mTasksHashTable.values().iterator(); i.hasNext();)
            {
                Collection<TaskEntityData> vTasks = i.next();
                mTasks.addAll(vTasks);
            }
        }
        else
        {
            mTasks = vTaskSession.getTasksFromTillTimestamp(webSession, mAccountEntityData.getFwdNumber(), vStart, vEnd);
        }
        mInvoiceData.NrOfTasks = mTasks.size();
        System.out.println(mInvoiceData.NrOfTasks + " Tasks found!!!");
        
        for (Iterator<TaskEntityData> i = mTasks.iterator(); i.hasNext();)
        {
            TaskEntityData vEntry = i.next();
            if (vEntry.getIsFixedPrice())
            {
                mInvoiceData.TaskCost += vEntry.getFixedPrice();
                // System.out.println("add fixed amonth: " +
                // vEntry.getFixedPrice() + ". total task is now " + mTaskCost);
            }
            else
            {
                mInvoiceData.TaskCost += ((double) vEntry.getTimeSpend() / 60.00) * ((double) mAccountEntityData.getTaskHourRate() / 100.00);
                // System.out.println("getTimeSpend=" + vEntry.getTimeSpend() +
                // ", getTaskHourRate=" + mAccountEntityData.getTaskHourRate());
                // System.out.println("add calc amonth: " + ((double)
                // vEntry.getTimeSpend() / 60.00) * ((double)
                // mAccountEntityData.getTaskHourRate() / 100.00) + ". total task is
                // now " + mTaskCost);
            }
            if (vEntry.getTimeStamp() > mCallCounts.LastInvoiceItem)
                mCallCounts.LastInvoiceItem = vEntry.getTimeStamp();
        }
        if (mInvoiceData.NrOfTasks > 0)
        {
            vNewInvoiceRequired = true;
        }

        mInvoiceData.TotalCost = mInvoiceData.TaskCost;
        if (mInvoiceData.Type == kTelemarketingInvoice)
        {
            mInvoiceData.TotalCost += mInvoiceData.Level1Cost + mInvoiceData.Level2Cost + mInvoiceData.Level3Cost;
            if (mInvoiceData.TotalCost > 0)
            {
                vNewInvoiceRequired = true;
            }
        }
        else if (mInvoiceData.Type == kStandardInvoice || mInvoiceData.Type == kWeekInvoice)
        {
            mInvoiceData.FaxUnitCost = (double) mAccountEntityData.getFacFaxCall() / 100;
            mInvoiceData.FaxCost = mInvoiceData.FaxUnitCost * mCallCounts.FaxCalls;
            mInvoiceData.SmsUnitCost = mAccountEntityData.getFacSms() / 100;
            mInvoiceData.SmsCost = mCallCounts.SmsCalls * mInvoiceData.SmsUnitCost;
            mInvoiceData.FwdUnitCost = mAccountEntityData.getFacCallForward() / 100;
            mInvoiceData.FwdCost = mCallCounts.FwdCalls * mInvoiceData.FwdUnitCost;
            mInvoiceData.FacLongUnit = mInvoiceData.CostFormatter.format(mAccountEntityData.getFacLong());
            mInvoiceData.LongCost = mCallCounts.LongCallSec * mAccountEntityData.getFacLong();
            mInvoiceData.FacLongFwdUnit = mInvoiceData.CostFormatter.format(mAccountEntityData.getFacLongFwd());
            mInvoiceData.LongFwdCost = mCallCounts.LongFwdCallSec * mAccountEntityData.getFacLongFwd();

            // mTotalCost += (mCallsCost + mLongCallSec *
            // Constants.CENT_PER_LONG_CALL_SECOND + mAgendaCost);
            mInvoiceData.TotalCost += (mInvoiceData.InCost + mInvoiceData.OutCost + mInvoiceData.FaxCost + mInvoiceData.SmsCost + mInvoiceData.FwdCost + mCallCounts.LongCallSec * mAccountEntityData.getFacLong() + mCallCounts.LongFwdCallSec * mAccountEntityData.getFacLongFwd() + mInvoiceData.AgendaCost);
        }
        if (!mAccountEntityData.getNoBtw())
        {
            mInvoiceData.Btw = mInvoiceData.TotalCost * 0.21;
        }
        if (mCallCounts.LastInvoiceItem > vEnd)
            mCallCounts.LastInvoiceItem = vEnd;
        if (mCallCounts.LastInvoiceItem < vStart)
            mCallCounts.LastInvoiceItem = vStart + 1;

        if (mInvoiceEntityData == null)
        {
            if (mInvoiceData.TotalCost > 0.0 && (vNewInvoiceRequired))
            {
                mInvoiceEntityData = new InvoiceEntityData();
                mInvoiceEntityData.setAccountFwdNr(mAccountEntityData.getFwdNumber());
                mInvoiceEntityData.setTotalCost(mInvoiceData.TotalCost);
                mInvoiceEntityData.setMonth(mInvoiceData.Month);
                mInvoiceEntityData.setYear(mInvoiceData.Year);
                mInvoiceEntityData.setYearSeqNr(0);
                mInvoiceEntityData.setInvoiceNr("");
                mInvoiceEntityData.setFrozenFlag(false);
                mInvoiceEntityData.setStartTime(vStart);
                mInvoiceEntityData.setStopTime(mCallCounts.LastInvoiceItem);
                int id = vInvoiceSession.addInvoice(webSession, mInvoiceEntityData);
                mInvoiceEntityData.setId(id);
                mInvoiceData.Id = mInvoiceEntityData.getId();
            }
        }
        else
        {
            if (!mInvoiceEntityData.getFrozenFlag() && mInvoiceEntityData.getCreditId() < 0 && mInvoiceEntityData.getTotalCost() != mInvoiceData.TotalCost)
            {
                System.out.println("new total cost " + mInvoiceData.TotalCost + " is not equal to old cost " + mInvoiceEntityData.getTotalCost());
                mInvoiceEntityData.setTotalCost(mInvoiceData.TotalCost);
                System.out.println("invoice not frozen: update start-stop: start: " + mInvoiceEntityData.getStartTime() + "-->" + vStart + ", stop: " + mInvoiceEntityData.getStopTime() + "-->" + mCallCounts.LastInvoiceItem);
                mInvoiceEntityData.setStartTime(vStart);
                mInvoiceEntityData.setStopTime(mCallCounts.LastInvoiceItem);
                vInvoiceSession.updateRow(webSession.getConnection(), mInvoiceEntityData);
            }
        }
    }

    // Copies src file to dst file.
    // If the dst file does not exist, it is created
    public boolean generatePdfInvoice()
    {
        if (!mIsInitialized)
        {
            System.out.println("InvoiceHelper not initialized");
            throw new RuntimeException();
        }
        // if (mAccountEntityData.getNoInvoice() || mNoValidCustomer ||
        // !mInvoiceEntityData.getFrozenFlag())
        if (mNoValidCustomer ||  mAccountEntityData.getNoInvoice())
        {
            System.out.println("No invoice doc generated for " + mAccountEntityData.getFullName() + ": getNoInvoice()=" + mAccountEntityData.getNoInvoice() + ", getFrozenFlag()=" + mInvoiceEntityData.getFrozenFlag());
            return true;
        }
        if (mInvoiceEntityData == null)
        {
            System.out.println("No invoice data found in DB for " + mAccountEntityData.getFullName());
            return true;
        }
        if (mInvoiceEntityData.getFileName() == null || mInvoiceEntityData.getFileName().length() == 0)
        {
            System.out.println("No invoice doc generated for " + mAccountEntityData.getFullName() + " because file name was not set");
            return true;
        }
        // Calendar vToday = Calendar.getInstance();

        File vTemplate = new File(Constants.INVOICE_HEAD_TMPL);
        File vTarget = new File(mInvoiceEntityData.getFileName());

        File vPath = vTarget.getParentFile();
        if (!vPath.exists())
        {
            // dir doesn't exist
            vPath.mkdirs();
        }
        // method is necessary for calculating the individual costs of sub customers
        generateSubCustomerCostList();
        TbaPdfInvoice pdfInvoice = new TbaPdfInvoice(vTarget, vTemplate);
        pdfInvoice.setCallCounts(mCallCounts);
        pdfInvoice.setCustomerData(mCustomerData);
        pdfInvoice.setInvoiceData(mInvoiceData);
        pdfInvoice.setTaskData(mTasks);
        pdfInvoice.setSubCustomers(mSubcustomerCostList);
        pdfInvoice.createInvoice();
        pdfInvoice.closeAndSave();
        return true;
    }

    public int getInvoiceGroup()
    {
        if (!mIsInitialized)
        {
            System.out.println("InvoiceHelper not initialized");
            throw new RuntimeException();
        }
        if (mInvoiceData.Type == kWeekInvoice)
            return 4;
        if (mCallCounts.TotalCalls < mAccountEntityData.getFacTblMinCalls_II()) // 40)
            return 3;
        if (mCallCounts.TotalCalls < mAccountEntityData.getFacTblMinCalls_I()) // 75)
            return 2;
        else
            return 1;
    }

    public String getInvoiceGroupStr()
    {
        return mTarifs[getInvoiceGroup() - 1].sign;
    }

    public int getInCalls()
    {
        return mCallCounts.InCalls;
    }

    public String getInCost()
    {
        return (mInvoiceData.InCost > 0.0) ? mInvoiceData.CostFormatter.format(mInvoiceData.InCost) : "0.0";
    }

    public int getOutCalls()
    {
        return mCallCounts.OutCalls;
    }

    public String getOutCost()
    {
        return (mInvoiceData.OutCost > 0.0) ? mInvoiceData.CostFormatter.format(mInvoiceData.OutCost) : "0.0";
    }

    public int getLongCalls()
    {
        return mCallCounts.LongCalls;
    }

    public int getLongFwdCalls()
    {
        return mCallCounts.LongFwdCalls;
    }

    public int getLongCallsSeconds()
    {
        return mCallCounts.LongCallSec;
    }

    public int getLongFwdCallsSeconds()
    {
        return mCallCounts.LongFwdCallSec;
    }

    public String getLongCost()
    {
        return (mCallCounts.LongCallSec > 0.0) ? mInvoiceData.CostFormatter.format(mCallCounts.LongCallSec * mAccountEntityData.getFacLong()) : "0.0";
    }

    public String getLongFwdCost()
    {
        return (mCallCounts.LongFwdCallSec > 0.0) ? mInvoiceData.CostFormatter.format(mCallCounts.LongFwdCallSec * mAccountEntityData.getFacLongFwd()) : "0.0";
    }

    public int getSmsCalls()
    {
        return mCallCounts.SmsCalls;
    }

    public String getSmsCost()
    {
        return (mInvoiceData.SmsCost > 0.0) ? mInvoiceData.CostFormatter.format(mInvoiceData.SmsCost) : "0.0";
    }

    public int getForwardCalls()
    {
        return mCallCounts.FwdCalls;
    }

    public String getForwardCost()
    {
        return (mInvoiceData.FwdCost > 0.0) ? mInvoiceData.CostFormatter.format(mInvoiceData.FwdCost) : "0.0";
    }

    public int getFaxCalls()
    {
        return mCallCounts.FaxCalls;
    }

    public String getFaxCost()
    {
        return (mInvoiceData.FaxCost > 0.0) ? mInvoiceData.CostFormatter.format(mInvoiceData.FaxCost) : "0.0";
    }

    public String getCallsCost()
    {
        return (mInvoiceData.CallsCost > 0.0) ? mInvoiceData.CostFormatter.format(mInvoiceData.CallsCost) : "";
    }

    public int getTotalCalls()
    {
        return mCallCounts.TotalCalls;
    }

    public int getAgendaCalls()
    {
        return mCallCounts.AgendaCalls;
    }

    public String getAgendaCost()
    {
        return (mInvoiceData.AgendaCost > 0.0) ? mInvoiceData.CostFormatter.format(mInvoiceData.AgendaCost) : "0.0";
    }

    public String getAgendaCostString()
    {
        return mInvoiceData.AgendaCostString;
    }

    public int getLevel1Calls()
    {
        return mCallCounts.Level1Calls;
    }

    public int getLevel2Calls()
    {
        return mCallCounts.Level2Calls;
    }

    public int getLevel3Calls()
    {
        return mCallCounts.Level3Calls;
    }

    public String getLevel1Cost()
    {
        return mInvoiceData.CostFormatter.format(mInvoiceData.Level1Cost);
    }

    public String getLevel2Cost()
    {
        return mInvoiceData.CostFormatter.format(mInvoiceData.Level2Cost);
    }

    public String getLevel3Cost()
    {
        return mInvoiceData.CostFormatter.format(mInvoiceData.Level3Cost);
    }

    public String getInCallUnitCost()
    {
        return (mInvoiceData.InUnitCost > 0.0) ? mInvoiceData.CostFormatter.format(mInvoiceData.InUnitCost) : "";
    }

    public String getOutCallUnitCost()
    {
        return (mInvoiceData.OutUnitCost > 0.0) ? mInvoiceData.CostFormatter.format(mInvoiceData.OutUnitCost) : "";
    }

    public String getSmsCallUnitCost()
    {
        return (mInvoiceData.SmsUnitCost > 0.0) ? mInvoiceData.CostFormatter.format(mInvoiceData.SmsUnitCost) : "";
    }

    public String getFaxUnitCost()
    {
        return (mInvoiceData.FaxUnitCost > 0.0) ? mInvoiceData.CostFormatter.format(mInvoiceData.FaxUnitCost) : "";
    }

    public String getFwdCallUnitCost()
    {
        return (mInvoiceData.FwdUnitCost > 0.0) ? mInvoiceData.CostFormatter.format(mInvoiceData.FwdUnitCost) : "";
    }

    public String getCallsUnitCost()
    {
        return (mInvoiceData.CallsUnitCost > 0.0) ? mInvoiceData.CostFormatter.format(mInvoiceData.CallsUnitCost) : "";
    }

    public String getAgendaCallUnitCost()
    {
        return (mInvoiceData.AgendaUnitCost > 0.0) ? mInvoiceData.CostFormatter.format(mInvoiceData.AgendaUnitCost) : "";
    }

    public String getLevel1UnitCost()
    {
        return mInvoiceData.CostFormatter.format(mInvoiceData.Level1UnitCost);
    }

    public String getLevel2UnitCost()
    {
        return mInvoiceData.CostFormatter.format(mInvoiceData.Level2UnitCost);
    }

    public String getLevel3UnitCost()
    {
        return mInvoiceData.CostFormatter.format(mInvoiceData.Level3UnitCost);
    }

    public String getTaskCost()
    {
        return mInvoiceData.CostFormatter.format(mInvoiceData.TaskCost);
    }

    public int getNrOfTasks()
    {
        return mInvoiceData.NrOfTasks;
    }

    public String getTotalCost()
    {
        return mInvoiceData.CostFormatter.format(mInvoiceData.TotalCost);
    }

    public String getTotalCostInclBTW()
    {
        return mInvoiceData.CostFormatter.format(mInvoiceData.TotalCost * 1.21);
    }

    public boolean isInvoiceRequired()
    {
        return (mInvoiceData.TotalCost > 0);
    }

    public int getInvoiceId()
    {
        return mInvoiceData.Id;
    }

    public Collection<CallRecordEntityData> getCallRecords()
    {
        return mRecords;
    }

    public Collection<TaskEntityData> getTasks()
    {
        return mTasks;
    }

    public String format(double val)
    {
        return mInvoiceData.CostFormatter.format(val);
    }

    public boolean isFrozen()
    {
        return (mInvoiceEntityData == null) ? false : mInvoiceEntityData.getFrozenFlag();
    }

    public InvoiceData getInvoiceData()
    {
        return mInvoiceData;
    }

    public Collection<SubcustomerCost> getSubcustomerCostList()
    {
        return mSubcustomerCostList;
    }

    public void setFileName()
    {
        if (mInvoiceEntityData != null)
        {
            mInvoiceEntityData.setFileName(makeFileName(mInvoiceEntityData));
        }
    }

    static public String getInvoiceNumber(int year, int month, int seqNr)
    {
        if ((year - 2000) < 10)
            return new String("N-0" + (year - 2000) + (((month + 1) < 10) ? "0" : "") + (month + 1) + "nr" + seqNr);
        else
            return new String("N-" + (year - 2000) + (((month + 1) < 10) ? "0" : "") + (month + 1) + "nr" + seqNr);
    }

    static public String makeFileName(InvoiceEntityData invoiceData)
    {
        AccountEntityData account = AccountCache.getInstance().get(invoiceData.getAccountFwdNr());
        if (account != null)
            return new String(Constants.INVOICE_DIR + invoiceData.getYear() + "\\" + Constants.MONTHS[invoiceData.getMonth()] + "\\Fac" + getInvoiceNumber(invoiceData.getYear(), invoiceData.getMonth(), invoiceData.getYearSeqNr()) + "-" + spaces2underscores(account.getFullName()) + ".pdf");
        else if (invoiceData.getCustomerName().length() > 0)
            return new String(Constants.INVOICE_DIR + invoiceData.getYear() + "\\" + Constants.MONTHS[invoiceData.getMonth()] + "\\Fac" + getInvoiceNumber(invoiceData.getYear(), invoiceData.getMonth(), invoiceData.getYearSeqNr()) + "-" + spaces2underscores(invoiceData.getCustomerName()) + ".pdf");
        return "";
    }

    static public String makeCreditInvoiceFileName(InvoiceEntityData invoiceData)
    {
        if (invoiceData != null)
        {
            CharSequence target = invoiceData.getInvoiceNr();
            CharSequence replacer = "C" + invoiceData.getInvoiceNr();
            return invoiceData.getFileName().replace(target, replacer);
        }
        return "";
    }

    public int setCounters(CallCounts callCounts, Collection<CallRecordEntityData> callList, AccountEntityData customerData)
    {
        if (callList != null)
        {
            // Set vKeySet = mRecords.keySet();
            for (Iterator<CallRecordEntityData> i = callList.iterator(); i.hasNext();)
            {
                CallRecordEntityData vEntry = i.next();
                if (vEntry.getIsIncomingCall())
                    ++callCounts.InCalls;
                else
                    ++callCounts.OutCalls;
                if (vEntry.getIsSmsCall())
                    ++callCounts.SmsCalls;
                if (vEntry.getIsForwardCall())
                    ++callCounts.FwdCalls;
                if (vEntry.getIsAgendaCall())
                    ++callCounts.AgendaCalls;
                if (vEntry.getIsFaxCall())
                    ++callCounts.FaxCalls;
                if (vEntry.getTimeStamp() > callCounts.LastInvoiceItem)
                    callCounts.LastInvoiceItem = vEntry.getTimeStamp();
                if (customerData != null)
                {
                    if (customerData.getCountAllLongCalls() && !vEntry.getIsForwardCall())
                    {
                        long seconds = InvoiceHelper.duration2Seconds(vEntry.getCost());
                        if (seconds > Constants.NORMAL_CALL_LENGTH)
                        {
                            ++callCounts.LongCalls;
                            callCounts.LongCallSec += (seconds - Constants.NORMAL_CALL_LENGTH);
                            // System.out.println("total long seconds=" +
                            // mLongCallSec);
                        }
                    }
                    if ((customerData.getCountLongFwdCalls() || customerData.getCountAllLongCalls()) && vEntry.getIsForwardCall())
                    {
                        long seconds = InvoiceHelper.duration2Seconds(vEntry.getCost());
                        if (seconds > Constants.NORMAL_CALL_LENGTH)
                        {
                            ++callCounts.LongFwdCalls;
                            callCounts.LongFwdCallSec += (seconds - Constants.NORMAL_CALL_LENGTH);
                            // System.out.println("total long seconds=" +
                            // mLongCallSec);
                        }
                    }
                }
                if (vEntry.getInvoiceLevel() == InvoiceHelper.kLevel3)
                    ++callCounts.Level3Calls;
                else if (vEntry.getInvoiceLevel() == InvoiceHelper.kLevel2)
                    ++callCounts.Level2Calls;
                else
                    ++callCounts.Level1Calls;
            }
            callCounts.TotalCalls = callCounts.InCalls + callCounts.OutCalls;// +
                                                                             // mSmsCalls
                                                                             // +
                                                                             // mFwdCalls
                                                                             // +
                                                                             // mFaxCalls;
            if (customerData != null)
                System.out.println("setCounters for " + customerData.getFullName() + ": " + callList.size() + "(in), " + callCounts.TotalCalls + "(total); " + callCounts.LongFwdCallSec + "sec (" + callCounts.LongFwdCalls + " calls)");
            else
                System.out.println("setCounters for unknown: " + callList.size() + "(in), " + callCounts.TotalCalls + "(total); " + callCounts.LongFwdCallSec + "sec (" + callCounts.LongFwdCalls + " calls)");
        }
        return callCounts.TotalCalls;
    }

    private void generateSubCustomerCostList()
    {
        Set<String> vCallsSubCustomers = null;
        Set<String> vTasksSubCustomers = null;

        if (mRecordsHashTable != null)
        {
            vCallsSubCustomers = mRecordsHashTable.keySet();
            for (Iterator<String> i = vCallsSubCustomers.iterator(); i.hasNext();)
            {
                mSubcustomerCostList.add(new SubcustomerCost(i.next()));
            }
        }
        if (mTasksHashTable != null)
        {
            vTasksSubCustomers = mTasksHashTable.keySet();
            for (Iterator<String> i = vTasksSubCustomers.iterator(); i.hasNext();)
            {
                String vCustFwdNr = i.next();
                SubcustomerCost subCust = new SubcustomerCost(vCustFwdNr);
                if (!mSubcustomerCostList.contains(subCust))
                {
                    mSubcustomerCostList.add(subCust);
                }
            }
        }

        // fill in the attributes for each sub customer
        for (Iterator<SubcustomerCost> i = mSubcustomerCostList.iterator(); i.hasNext();)
        {
            SubcustomerCost vSubcustomerCost = i.next();
            String vCustFwdNr = vSubcustomerCost.getFwdNr();
            AccountEntityData accountData = AccountCache.getInstance().get(vCustFwdNr);
            vSubcustomerCost.setName(accountData.getFullName());

            double vCallCost = 0.0;
            double vTaskCost = 0.0;

            Collection<CallRecordEntityData> vCallList = mRecordsHashTable.get(vCustFwdNr);
            if (vCallList != null)
            {
                CallCounts callsCount = new CallCounts();
                setCounters(callsCount, vCallList, accountData);
                vSubcustomerCost.setCalls(callsCount.TotalCalls);

                if ((mCallCounts.InCalls) > 0)
                    vCallCost = mInvoiceData.InCost * callsCount.InCalls / mCallCounts.InCalls;
                if ((mCallCounts.OutCalls) > 0)
                    vCallCost += mInvoiceData.OutCost * callsCount.OutCalls / mCallCounts.OutCalls;
                if (mCallCounts.LongCallSec > 0)
                    vCallCost += (mCallCounts.LongCallSec * mAccountEntityData.getFacLong()) * callsCount.LongCallSec / mCallCounts.LongCallSec;
                if (mCallCounts.LongFwdCallSec > 0)
                    vCallCost += (mCallCounts.LongFwdCallSec * mAccountEntityData.getFacLongFwd()) * callsCount.LongFwdCallSec / mCallCounts.LongFwdCallSec;
                if (mCallCounts.SmsCalls > 0)
                    vCallCost += mInvoiceData.SmsCost * callsCount.SmsCalls / mCallCounts.SmsCalls;
                if (mCallCounts.FaxCalls > 0)
                    vCallCost += mInvoiceData.FaxCost * callsCount.FaxCalls / mCallCounts.FaxCalls;
                if (mCallCounts.FwdCalls > 0)
                    vCallCost += mInvoiceData.FwdCost * callsCount.FwdCalls / mCallCounts.FwdCalls;

                vSubcustomerCost.setCallCost(vCallCost);
            }
            else
            {
                System.out.println("No calls for " + accountData.getFullName());
            }

            Collection<TaskEntityData> vTaskList = (Collection<TaskEntityData>) mTasksHashTable.get(vCustFwdNr);
            if (vTaskList != null)
            {
                vSubcustomerCost.setTasks(vTaskList.size());
                for (Iterator<TaskEntityData> k = vTaskList.iterator(); k.hasNext();)
                {
                    TaskEntityData vTask = k.next();
                    if (vTask.getIsFixedPrice())
                    {
                        vTaskCost += vTask.getFixedPrice();
                        // System.out.println("add fixed amonth: " +
                        // vEntry.getFixedPrice() + ". total task is now " +
                        // mTaskCost);
                    }
                    else
                    {
                        vTaskCost += ((double) vTask.getTimeSpend() / 60.00) * ((double) mCustomerData.getTaskHourRate() / 100.00);
                    }
                }
                vSubcustomerCost.setTaskCost(vTaskCost);
            }
        }
    }

    static private String spaces2underscores(String aName)
    {
        String name = aName.replace(' ', '_');
        name = name.replace(';', '_');
        name = name.replace(':', '_');
        name = name.replace(',', '_');
        return name;
    }

    static public long duration2Seconds(String duration)
    {
        StringTokenizer vTokenizer = new StringTokenizer(duration, ":");
        long seconds = 0;
        if (vTokenizer.hasMoreTokens())
        {
            String hours = vTokenizer.nextToken();
            int hrs = Integer.parseInt(hours);
            if (hrs < 5)
            {
                seconds = hrs * 3600;
                // System.out.println("hours=" + hours + " ;seconds=" + seconds);
            }
        }
        if (vTokenizer.hasMoreTokens())
        {
            String minutes = vTokenizer.nextToken();
            seconds += Integer.parseInt(minutes) * 60;
            // System.out.println("minutes=" + minutes + " ;seconds=" + seconds);
        }
        if (vTokenizer.hasMoreTokens())
        {
            String secs = vTokenizer.nextToken();
            seconds += Integer.parseInt(secs);
            // System.out.println("seconds=" + secs + " ;seconds=" + seconds);
        }
        return seconds;
    }

}
