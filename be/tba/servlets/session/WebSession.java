/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.servlets.session;

import java.io.Serializable;

import java.util.Calendar;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.pbx.interfaces.CallRecordEntityData;
import be.tba.ejb.task.interfaces.TaskEntityData;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.data.CallFilter;
import be.tba.util.invoice.InvoiceHelper;

/**
 * Base Data Container for all other Value Objects
 *
 * @author Yves Willems
 * @version $Revision: 1.0 $
 */
final public class WebSession implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private AccountRole mRole = null;

    private CallFilter mCallFilter;

    private String mUserId = "";

    private String mId = "";

    private long mLastAccess = 0;

    private String mFwdNumber = null;

    private String mCurrentRecordId = null;

    private CallRecordEntityData mCurrentRecord = null;

    private TaskEntityData mCurrentTask = null;

    private String mCurrentAccountId = null;

    private AccountEntityData mNewAccount = null;

    private boolean mIs3W = false;

    private String mSearchString = "";

    private int mDaysBack = 0;

    private int mMonthsBack = 0;

    private int mYear = 0;

    private int mInvoiceId = -1;

    private HttpServletRequest mOldRequest;

    private InvoiceHelper mInvoiceHelper;

    private String mCurrentJsp;

    //private Map<Integer, CallRecordEntityData> mNewUnmappedCalls = new HashMap<Integer, CallRecordEntityData>();
    private CallRecordEntityData mNewUnmappedCall;
    
    private Connection mConnection;
    
    private String mRecordId = null;

    private String mFintroFileName = null;
    
    private String mFintroProcessLog = null;

    public WebSession() throws SQLException
    {
        try
        {
            System.out.println("Create WebSession without DataSource");
            init();
            //InitialContext ctx = new InitialContext();
            //DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/MySqlDS");
            //mConnection = ds.getConnection();
            mConnection = DriverManager.getConnection(Constants.MYSQL_URL);
            //System.out.println("WebSession created");

            // mConnection = DriverManager.getConnection("jdbc:mysql://localhost/tbadb");
        }
/*        catch (NamingException ex)
        {
            ex.printStackTrace();
            System.out.println("Error in WebSession. Can not create DB Connection.");
        }
*/        catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("Error in WebSession. Can not create DB Connection.");
        }
    }

    public WebSession(String mysqlURL) throws SQLException
    {
        // try
        // {
        //System.out.println("Create WebSession with DataSource");
        init();
        // mConnection = ds.getConnection();
        mConnection = DriverManager.getConnection(mysqlURL);
        // }
        // catch (NamingException ex)
        // {
        // ex.printStackTrace();
        // System.out.println("Error in WebSession. Can not create DB Connection.");
        // }
    }

    public void Close()
    {
        if (mConnection != null)
        {
            try
            {
                mConnection.close();
                mConnection = null;
            }
            catch (SQLException ex)
            {
                System.out.println("Error in WebSession.close(): SQL connection could not be closed.");
            }
        }
    }
    
    public String getRecordId()
    {
        return mRecordId;
    }
    
    public void setRecordId(String id)
    {
        mRecordId = id;
    }

    
    public Connection getConnection()
    {
        return mConnection;
    }

    public CallRecordEntityData getNewUnmappedCall()
    {
        return mNewUnmappedCall;
    }
    
    public void setNewUnmappedCall(CallRecordEntityData newCall)
    {
        mNewUnmappedCall = newCall;
    }

    public void setRole(AccountRole role)
    {
        mRole = role;
    }

    public AccountRole getRole()
    {
        return mRole;
    }

    public void setUserId(String userid)
    {
        mUserId = userid;
    }

    public String getUserId()
    {
        return mUserId;
    }

    public String getSessionId()
    {
        return mId;
    }

    public void setCallFilter(CallFilter filter)
    {
        mCallFilter = filter;
    }

    public CallFilter getCallFilter()
    {
        return mCallFilter;
    }

    public void setFwdNumber(String fwdNr)
    {
        mFwdNumber = fwdNr;
    }

    public String getFwdNumber()
    {
        return mFwdNumber;
    }

    public void setCurrentRecordId(String id)
    {
        mCurrentRecordId = id;
    }

    public String getCurrentRecordId()
    {
        return mCurrentRecordId;
    }

    public void setCurrentRecord(CallRecordEntityData rec)
    {
        mCurrentRecord = (rec == null ? null : (CallRecordEntityData) rec.clone());
    }

    public CallRecordEntityData getCurrentRecord()
    {
        return mCurrentRecord;
    }

    public void setCurrentTask(TaskEntityData rec)
    {
        mCurrentTask = (rec == null ? null : (TaskEntityData) rec.clone());
    }

    public TaskEntityData getCurrentTask()
    {
        return mCurrentTask;
    }

    public void setCurrentAccountId(String rec)
    {
        mCurrentAccountId = rec;
    }

    public String getCurrentAccountId()
    {
        return mCurrentAccountId;
    }

    public void setCallingJsp(String page)
    {
        mCurrentJsp = page;
    }

    public String getCallingJsp()
    {
        return mCurrentJsp;
    }

    public void setNewAccount(AccountEntityData data)
    {
        mNewAccount = (data == null ? null : (AccountEntityData) data.clone());
    }

    public AccountEntityData getNewAccount()
    {
        return mNewAccount;
    }

    public void setOldRequest(HttpServletRequest req)
    {
        mOldRequest = req;
    }

    public HttpServletRequest getOldRequest()
    {
        return mOldRequest;
    }

    public void setIs3W(boolean is3W)
    {
        mIs3W = is3W;
    }

    public boolean getIs3W()
    {
        return mIs3W;
    }

    public void setSearchString(String searchString)
    {
        mSearchString = searchString;
    }

    public String getSearchString()
    {
        return mSearchString;
    }

    public void setFintroFile(String fintroFile)
    {
        mFintroFileName = fintroFile;
    }

    public String getFintroFile()
    {
        return mFintroFileName;
    }

    public void setDaysBack(int cnt)
    {
        if (cnt < 0)
            cnt = 0;
        mDaysBack = cnt;
    }

    public int getDaysBack()
    {
        return mDaysBack;
    }

    public void setMonthsBack(int cnt)
    {
        mMonthsBack = cnt;
    }

    public int getMonthsBack()
    {
        return mMonthsBack;
    }

    public String getMonthsBackString()
    {
        if (mMonthsBack > 11)
        {
            return Constants.MONTHS[mMonthsBack % 12];
        }
        else if (mMonthsBack < 0)
        {
            int cycle = 0;
            while ((mMonthsBack + (++cycle * 12)) < 0)
                ;
            return Constants.MONTHS[mMonthsBack + (cycle * 12)];
        }
        return Constants.MONTHS[mMonthsBack];
    }

    public void setYear(int cnt)
    {
        mYear = cnt;
    }

    public int getYear()
    {
        return mYear;
    }

    public void setInvoiceId(int id)
    {
        mInvoiceId = id;
    }

    public int getInvoiceId()
    {
        return mInvoiceId;
    }

    public int incrementMonthsBack()
    {
        if (mMonthsBack == Calendar.DECEMBER)
        {
            mMonthsBack = Calendar.JANUARY;
            ++mYear;
        }
        else
            ++mMonthsBack;
        return mMonthsBack;
    }

    public int decrementMonthsBack()
    {
        if (mMonthsBack == Calendar.JANUARY)
        {
            mMonthsBack = Calendar.DECEMBER;
            --mYear;
        }
        else
            --mMonthsBack;
        return mMonthsBack;
    }

    public boolean isCurrentMonth()
    {
        Calendar calendar = Calendar.getInstance();
        return (mMonthsBack == calendar.get(Calendar.MONTH) && mYear == calendar.get(Calendar.YEAR));
    }

    public void setInvoiceHelper(InvoiceHelper helper)
    {
        mInvoiceHelper = helper;
    }

    public InvoiceHelper getInvoiceHelper()
    {
        return mInvoiceHelper;
    }

    public String getFintroProcessLog()
    {
        return mFintroProcessLog;
    }
    
    public void setFintroProcessLog(String fileName)
    {
        mFintroProcessLog = fileName;
    }
    
    public void init(String userid, String sessionId)
    {
        mId = sessionId;
        mRole = AccountRole.CUSTOMER;
        mUserId = userid;
        mCallFilter.init();
        Calendar calendar = Calendar.getInstance();
        mLastAccess = calendar.getTimeInMillis();
        mMonthsBack = calendar.get(Calendar.MONTH);
        mYear = calendar.get(Calendar.YEAR);
        mInvoiceId = -1;
    }

    public boolean isExpired(String caller)
    {
        long vTimeout = Constants.ADMIN_SESSION_TIMEOUT;
        if (mRole == AccountRole.CUSTOMER)
            vTimeout = Constants.CUSTOMER_SESSION_TIMEOUT;
        vTimeout += mLastAccess;
        long vCurrTime = Calendar.getInstance().getTimeInMillis();

        if (!caller.equals("cleaner"))
        {
            mLastAccess = vCurrTime;
        }
        if (vTimeout > vCurrTime)
        {
            return false;
        }
        System.out.println(caller + "(" + mUserId + ")WebSession.isExpired since " + (vCurrTime - vTimeout) / 1000 + " seconds.");
        return true;
    }

    private void init()
    {
        mCallFilter = new CallFilter();
        Calendar calendar = Calendar.getInstance();
        mLastAccess = calendar.getTimeInMillis();
        mMonthsBack = calendar.get(Calendar.MONTH);
        mYear = calendar.get(Calendar.YEAR);

    }
}
