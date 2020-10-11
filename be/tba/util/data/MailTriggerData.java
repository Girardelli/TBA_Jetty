/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.data;

import java.io.Serializable;

/**
 * Base Data Container for all other Value Objects
 * 
 * @author Yves Willems
 * @version $Revision: 1.0 $
 */
final public class MailTriggerData implements Serializable
{
   /**
   * 
   */
   private static final long serialVersionUID = 10006L;

   private String mAccountFwdNr;

   private short mHour = 0;

   private short mMinutes = 0;

   public MailTriggerData(String accountFwdNr, short hour, short minutes)
   {
      mAccountFwdNr = accountFwdNr;
      mHour = hour;
      mMinutes = minutes;
   }

   public void setAccountFwdNr(String fwdNr)
   {
      mAccountFwdNr = fwdNr;
   }

   public String getAccountFwdNr()
   {
      return mAccountFwdNr;
   }

   public void setHour(short hour)
   {
      mHour = hour;
   }

   public short getHour()
   {
      return mHour;
   }

   public void setMinutes(short minutes)
   {
      mMinutes = minutes;
   }

   public short getMinutes()
   {
      return mMinutes;
   }

   public boolean isEmpty()
   {
      return (mHour == 0 && mMinutes == 0);
   }

   public boolean equals(MailTriggerData cmp)
   {
      return (mHour == cmp.getHour() && mMinutes == cmp.getMinutes() && mAccountFwdNr == cmp.getAccountFwdNr());
   }

   public String toString()
   {
      return new String("MailTriggerData: id=" + mAccountFwdNr + ", hour=" + mHour + ", minutes=" + mMinutes);
   }

}
