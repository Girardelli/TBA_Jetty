/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.data;

import java.io.Serializable;

import be.tba.util.constants.Constants;

/**
 * Base Data Container for all other Value Objects
 * 
 * @author Yves Willems
 * @version $Revision: 1.0 $
 */
final public class CallFilter implements Serializable
{
   static public int kNoMonth = 99;
   static public int kNoYear = 99;
   /**
     * 
     */
   private static final long serialVersionUID = 6853924082949848037L;

   private String mCustFilter = "";

   private String mStateFilter = "";

   private String mDirFilter = "";

   public void setCustFilter(String filter)
   {
      mCustFilter = filter;
   }

   public String getCustFilter()
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
      mCustFilter = Constants.ACCOUNT_FILTER_ALL;
      mStateFilter = Constants.ACCOUNT_FILTER_ALL;
      mDirFilter = Constants.ACCOUNT_FILTER_ALL;
   }
}
