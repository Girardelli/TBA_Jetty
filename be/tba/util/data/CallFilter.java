/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.data;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.servlets.AdminDispatchServlet;
import be.tba.util.constants.Constants;

/**
 * Base Data Container for all other Value Objects
 * 
 * @author Yves Willems
 * @version $Revision: 1.0 $
 */
final public class CallFilter implements Serializable
{
   private static Logger log = LoggerFactory.getLogger(CallFilter.class);
    static public int kNoMonth = 99;
    static public int kNoYear = 99;
    /**
     * 
     */
    private static final long serialVersionUID = 6853924082949848037L;

    private int mCustFilter = 0;

    private String mStateFilter = "";

    private String mDirFilter = "";

    public void setCustFilter(int filter)
    {
       mCustFilter = filter;
    }

    public void setCustFilter(String accountIdStr)
    {
       if (accountIdStr == null || accountIdStr.isEmpty())
       {
         mCustFilter = 0;
         log.info("customer filter reset");
       }
      else
      {
         mCustFilter = Integer.parseInt(accountIdStr);
         log.info("customer filter set to " + mCustFilter);
      }
    }
    
    public int getCustFilter()
    {
        return mCustFilter;
    }

    public void setStateFilter(String filter)
    {
        mStateFilter = filter;
    }

    public String getStateFilter()
    {
        return mStateFilter;
    }

    public void setDirFilter(String filter)
    {
        mDirFilter = filter;
    }

    public String getDirFilter()
    {
        return mDirFilter;
    }

    public String toString()
    {
        return new String("Cust:" + mCustFilter + " State:" + mStateFilter + " Dir:" + mDirFilter);
    }

    public void init()
    {
        mCustFilter = 0;
        mStateFilter = Constants.ACCOUNT_FILTER_ALL;
        mDirFilter = Constants.ACCOUNT_FILTER_ALL;
    }
}
