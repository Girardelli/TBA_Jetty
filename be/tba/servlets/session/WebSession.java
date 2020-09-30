/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.servlets.session;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Collection;
import java.util.Vector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.account.interfaces.LoginEntityData;
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
	 private static Logger log = LoggerFactory.getLogger(WebSession.class);
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public AccountRole mRole = null;

    public CallFilter mCallFilter;

    public LoginEntityData mLoginData= null;

    public String mId = "";

    public long mLastAccess = 0;
    
    public int mWorkOrderId = 0;

    public String mFwdNumber = null;

    public String mCurrentRecordId = null;

    public CallRecordEntityData mCurrentRecord = null;

    public TaskEntityData mCurrentTask = null;

    private int mCurrentAccountId = 0;
    public String mCurrentAccountFwdNr = null;

    public AccountEntityData mNewAccount = null;

    public String mSearchString = "";

    public int mDaysBack = 0;

    public int mMonthsBack = 0;

    public int mYear = 0;

    public int mInvoiceId = -1;
    public int accountIdToDelete = 0;

    public HttpServletRequest mOldRequest;

    public InvoiceHelper mInvoiceHelper;

    public String mCurrentJsp;

    //public Map<Integer, CallRecordEntityData> mNewUnmappedCalls = new HashMap<Integer, CallRecordEntityData>();
    public CallRecordEntityData mNewUnmappedCall;
    
    public Connection mConnection;
    
    public int mRecordId;
    public int mLoginId;
    public int mLoginToDelete;

    public String mUploadedFileName = null;
    
    public String mFintroProcessLog = null;
    public long mSqlTimer = 0;
    public long mWebTimer = 0;
    public boolean mIsWebSocketActive = false;
    public Session mWsSession = null;
    public boolean mIsAutoUpdateRecord = false;
    public Collection<String> mErrorList = new Vector<String>();

    public WebSession()
    {
      init();
      initConnection();
    }

    
    public WebSession(String mysqlURL)
    {
        init();
        try
        {
            //log.info("Create WebSession without DataSource");
            init();
            mConnection = DriverManager.getConnection(mysqlURL);
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage(), ex);
            log.info("Error in WebSession. Can not create DB Connection.");
        }
    }

    
    public void userInit(String userId, String key)
    {
       mLoginData.setUserId(userId);
        mId = key;
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
                log.info("Error in WebSession.close(): SQL connection could not be closed.");
            }
        }
    }
   
    public Collection<String> getErrorList()
    {
       return mErrorList;
    }
    
    public void setErrorList(Collection<String> list)
    {
       mErrorList = list;
    }
    
    public int getRecordId()
    {
        return mRecordId;
    }
    
    public void setRecordId(int id)
    {
        mRecordId = id;
    }

 
    public Connection getConnection()
    {
       if (mConnection == null)
       {
          log.error("connection == null in session. Reinitialize the DB connection");
          initConnection();
       }
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
       mLoginData.setUserId(userid);
    }

    public String getUserId()
    {
        return mLoginData.getUserId();
    }

    public String getSessionId()
    {
        return mId;
    }

    public void setSessionId(String sessionId)
    {
        mId = sessionId;
    }

    public void setCallFilter(CallFilter filter)
    {
        mCallFilter = filter;
    }

    public CallFilter getCallFilter()
    {
        return mCallFilter;
    }

    public void setSessionFwdNr(String fwdNr)
    {
        mFwdNumber = fwdNr;
    }

    public String getSessionFwdNr()
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
    
    public void setAccountIdToDelete(int id)
    {
       accountIdToDelete = id;
    }

    public int getAccountIdToDelete()
    {
        return accountIdToDelete;
    }

    public void setAccountId(int id)
    {
       mCurrentAccountId = id;
    }

    public int getAccountId()
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

    public void setSearchString(String searchString)
    {
        mSearchString = searchString;
    }

    public String getSearchString()
    {
        return mSearchString;
    }

    public void setUploadedFileName(String fintroFile)
    {
        mUploadedFileName = fintroFile;
    }

    public String getUploadedFileName()
    {
        return mUploadedFileName;
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

    public void setWorkOrderId(int id)
    {
       mWorkOrderId = id;
    }

    public int getWorkOrderId()
    {
        return mWorkOrderId;
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
        log.info("incrementMonthsBack() returns " + mMonthsBack);
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
        log.info("decrementMonthsBack() returns " + mMonthsBack);
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
    
    public Session getWsSession()
    {
    	return mWsSession;
    }
    
    public void setWsSession(Session session)
    {
    	mWsSession = session;
    	setWsActive(true);
    }
   
    public boolean isWsActive()
    {
    	return mWsSession != null && mIsWebSocketActive;
    }
    
    public void setWsActive(boolean state)
    {
       mIsWebSocketActive = state;
    }
    
    public boolean isAutoUpdateRecord()
    {
      return mIsAutoUpdateRecord;
    }
    
    public void setIsAutoUpdateRecord(boolean state)
    {
       mIsAutoUpdateRecord = state;
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
        log.info(caller + "(" + mLoginData.getUserId() + ")WebSession.isExpired since " + (vCurrTime - vTimeout) / 1000 + " seconds.");
        return true;
    }
    
    public long getSqlTimer()
    {
    	return mSqlTimer;
    }
    
    public void resetSqlTimer()
    {
    	mSqlTimer = 0;
    	mWebTimer = Calendar.getInstance().getTimeInMillis();
    }
    
    public void addSqlTimer(long cnt)
    {
    	mSqlTimer += cnt;
    }

    private void init()
    {
        mCallFilter = new CallFilter();
        mCallFilter.init();
        Calendar calendar = Calendar.getInstance();
        mLastAccess = calendar.getTimeInMillis();
        mMonthsBack = calendar.get(Calendar.MONTH);
        mDaysBack = 0;
        mYear = calendar.get(Calendar.YEAR);
        mRole = AccountRole.CUSTOMER;
        mInvoiceId = -1;
    }
    
    private void initConnection()
    {
       try
       {
           //log.info("Create WebSession without DataSource");
           init();
           mConnection = DriverManager.getConnection(Constants.MYSQL_URL);
       }
       catch (Exception ex)
       {
           log.error(ex.getMessage(), ex);
           log.info("Error in WebSession. Can not create DB Connection.");
       }
    }

}
