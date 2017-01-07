/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.constants;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final public class AccountRole implements Serializable
{
   /**
     * 
     */
   private static final long serialVersionUID = 87273771L;

   private String vText;

   private String vShort;

   private static final Map<String, AccountRole> vMap = new HashMap<String, AccountRole>(5);

   public static final String _vCustomer = "K";

   public static final AccountRole CUSTOMER = new AccountRole(_vCustomer, "Klant");

   public static final String _vSubCustomer = "S";

   public static final AccountRole SUBCUSTOMER = new AccountRole(_vSubCustomer, "Sub-klant");

   public static final String _vAdminstrator = "A";

   public static final AccountRole ADMIN = new AccountRole(_vAdminstrator, "Administrator");

   public static final String _vEmployee = "W";

   public static final AccountRole EMPLOYEE = new AccountRole(_vEmployee, "Werknemer");

   public String getShort()
   {
      return vShort;
   }

   public String getText()
   {
      return vText;
   }

   public static AccountRole fromShort(String aShort)
   {
      return (AccountRole) vMap.get(aShort);
   }

   public static AccountRole fromText(String aText)
   {
      for (Iterator<AccountRole> n = AccountRole.iterator(); n.hasNext();)
      {
         AccountRole vRole = n.next();
         if (vRole.getText().equals(aText))
            return vRole;
      }
      return null;
   }

   public static int getSize()
   {
      return vMap.size();
   }

   public static Iterator<AccountRole> iterator()
   {
      return vMap.values().iterator();
   }

   // constructors
   private AccountRole(String aShort, String aText)
   {
      vText = aText;
      vShort = aShort;

      vMap.put(aShort, this);
   }

   private AccountRole()
   {
   }

}
