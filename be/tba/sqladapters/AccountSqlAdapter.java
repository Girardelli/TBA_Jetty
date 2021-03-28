package be.tba.sqladapters;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.ResultSet;

import be.tba.session.WebSession;
import be.tba.sqldata.AccountCache;
import be.tba.sqldata.AccountEntityData;
import be.tba.util.constants.Constants;
import be.tba.util.data.CallFilter;

/**
 * Session Bean Template
 *
 * ATTENTION: Some of the XDoclet tags are hidden from XDoclet by adding a "--"
 * between @ and the namespace. Please remove this "--" to make it active or add
 * a space to make an active tag inactive.
 *
 * @ejb:bean name="AccountSession" display-name="Call Record query"
 *           type="Stateless" transaction-type="Container"
 *           jndi-name="be/tba/ejb/customer/info/AccountSession"
 *
 * @ejb:ejb-ref ejb-name="AccountEntity"
 *
 */
public class AccountSqlAdapter extends AbstractSqlAdapter<AccountEntityData>
{
   private static Logger log = LoggerFactory.getLogger(AccountSqlAdapter.class);

   // -------------------------------------------------------------------------
   // Static
   // -------------------------------------------------------------------------

   // -------------------------------------------------------------------------
   // Members
   // -------------------------------------------------------------------------

   /**
    *
    */
   @SuppressWarnings("unused")
   private static final long serialVersionUID = 1L;

   // -------------------------------------------------------------------------
   // Methods
   // -------------------------------------------------------------------------

   public AccountSqlAdapter()
   {
      super("AccountEntity");
   }

//    public AccountEntityData logIn(WebSession webSession, String userid, String password) throws AccountNotFoundException
//    {
//        Collection<AccountEntityData> collection = executeSqlQuery(webSession, "SELECT * FROM AccountEntity WHERE Userid='" + userid + "' AND Password='" + password + "'");
//        if (collection.size() == 1)
//        {
//            AccountEntityData account = collection.iterator().next();
//            Calendar vCalendar = Calendar.getInstance();
//            int vMinutes = vCalendar.get(Calendar.MINUTE);
//            String vLoginTime = new String(vCalendar.get(Calendar.DAY_OF_MONTH) + "/" + (vCalendar.get(Calendar.MONTH) + 1) + "/" + vCalendar.get(Calendar.YEAR) + " " + vCalendar.get(Calendar.HOUR_OF_DAY) + ":" + (vMinutes < 10 ? "0" : "") + vMinutes);
//            account.setPreviousLoginTS(account.getLastLoginTS());
//            account.setLastLoginTS(vCalendar.getTimeInMillis());
//            account.setLastLogin(vLoginTime);
//            executeSqlQuery(webSession, "UPDATE AccountEntity SET LastLogin='" + vLoginTime + "' WHERE Id='" + account.getId() + "'");
//            log.info("Login: userid=" + userid + " (" + account.getFullName() + ")");
//            return account;
//        }
//        log.info("Login FAILED: userid=" + userid + ", Password=" + password);
//        throw new AccountNotFoundException("De user id/paswoord combinatie is foutief.");
//    }

   public Collection<AccountEntityData> getAllNotArchived(WebSession session)
   {
      return executeSqlQuery(session, "SELECT * FROM AccountEntity where IsArchived=false");
   }

   /*
    * public AccountEntityData getAccountByFwdNr(WebSession webSession, String
    * fwdNr) throws AccountNotFoundException { Collection<AccountEntityData>
    * collection = executeSqlQuery(webSession,
    * "SELECT * FROM AccountEntity WHERE FwdNumber='" + fwdNr + "'"); if
    * (collection.size() == 1) { return collection.iterator().next(); }
    * log.info("Unexpected number of account (" + collection.size() +
    * ") found for FwdNumber=" + fwdNr); throw new
    * AccountNotFoundException("De user id/paswoord combinatie is foutief."); }
    */
//    public void deregister(WebSession webSession, int pkey) throws AccountNotFoundException
//    {
//        AccountEntityData account = getRow(webSession, pkey);
//        if (account != null)
//        {
//            account.setUserId("");
//            account.setPassword("");
//            account.setIsRegistered(false);
//            updateRow(webSession, account);
//            return;
//        }
//        throw new AccountNotFoundException("Geen gebruiker gevonden voor key=" + pkey);
//    }
//
//    public AccountEntityData getUnregistered(WebSession webSession, String regCode) throws AccountNotFoundException
//    {
//        Collection<AccountEntityData> collection = executeSqlQuery(webSession, "SELECT * FROM AccountEntity WHERE FwdNumber='" + regCode + "'");
//        if (collection.size() == 1)
//        {
//            return collection.iterator().next();
//        }
//        log.info("FAILED getUnregistered(" + regCode + ")");
//        throw new AccountNotFoundException("De registratie code is niet correct.");
//    }
//
//    public String register(WebSession webSession, RegisterData data)
//    {
//        try
//        {
//            AccountEntityData account = getUnregistered(webSession, data.getCode());
//            if (account != null)
//            {
//                Collection<AccountEntityData> collection = executeSqlQuery(webSession, "SELECT * FROM AccountEntity WHERE Userid='" + data.getUserId() + "'");
//                if (collection != null && collection.size() > 0)
//                {
//                    return "Uw login naam wordt al door iemand anders gebruikt. Kies een andere en registreer opnieuw.";
//                }
//                account.setUserId(data.getUserId());
//                account.setPassword(data.getPassword());
//                // if (data.getFullName() != null && data.getFullName().length() > 0)
//                // account.setFullName(data.getFullName());
//                if (data.getEmail() != null && data.getEmail().length() > 0)
//                    account.setEmail(data.getEmail());
//                account.setIsRegistered(true);
//                updateRow(webSession, account);
//                return null;
//            }
//        }
//        catch (Exception ex)
//        {
//            log.error(ex.getMessage(), ex);
//        }
//        return "Interne fout tijdens registratie";
//    }

   public void addAccount(WebSession webSession, AccountEntityData data)
   {
      for (int i = 0; i < 2; ++i)
      {
         if (data.getFwdNumber().equals(Constants.NUMBER_BLOCK[i]))
         {
            return;
         }
      }
      addRow(webSession, data);
   }

   public void archiveAccount(WebSession webSession, int key)
   {
      if (AccountCache.getInstance().get(key).getNoInvoice())
      {
         deleteRow(webSession, key);
      }
      else
      {
         executeSqlQuery(webSession, "UPDATE AccountEntity SET IsArchived=true, FwdNumber='' WHERE Id=" + key);
      }
   }

   public void activateAccount(WebSession webSession, int key)
   {
      executeSqlQuery(webSession, "UPDATE AccountEntity SET IsArchived=false WHERE Id=" + key);
   }

   public void setAccount(WebSession webSession, AccountEntityData data)
   {
      updateRow(webSession, data);
   }

   public void setFilter(WebSession webSession, CallFilter filter, int pkey)
   {
      executeSqlQuery(webSession, "UPDATE AccountEntity SET CustFilter=" + filter.getCustFilter() + ",StateFilter='" + ((filter.getStateFilter() != null) ? filter.getStateFilter() : "") + "',DirFilter='" + ((filter.getDirFilter() != null) ? filter.getDirFilter() : "' WHERE Id=" + pkey));
   }

   public Collection<String> getFreeNumbers(WebSession webSession)
   {
      Vector<String> vFreeNumbers = new Vector<String>();
      Collection<AccountEntityData> accounts = getAllRows(webSession);

      for (int i = 1; i < Constants.NUMBER_BLOCK.length; i++)
      {
         vFreeNumbers.add(Constants.NUMBER_BLOCK[i]);
      }
      for (Iterator<AccountEntityData> i = accounts.iterator(); i.hasNext();)
      {
         vFreeNumbers.remove(i.next().getFwdNumber());
      }
      return vFreeNumbers;
   }

   public Collection<AccountEntityData> getAccountListByIdList(WebSession webSession, Collection<Integer> list)
   {
      StringBuilder strBuf = new StringBuilder();
      for (Iterator<Integer> i = list.iterator(); i.hasNext();)
      {
         int vKey = i.next().intValue();
         strBuf.append(",");
         strBuf.append(vKey);
      }
      return executeSqlQuery(webSession, "SELECT * FROM AccountEntity WHERE NoInvoice=FALSE AND IsArchived=false AND Id IN (" + strBuf.toString().substring(1) + ")");
   }

   public static Map<String, AccountEntityData> converToHashMap(Collection<AccountEntityData> rawList)
   {
      Map<String, AccountEntityData> vMap = new HashMap<String, AccountEntityData>();

      for (Iterator<AccountEntityData> i = rawList.iterator(); i.hasNext();)
      {
         AccountEntityData vEntry = (AccountEntityData) i.next();
         vMap.put(vEntry.getFwdNumber(), vEntry);
      }
      return (vMap);
   }

   public void setSuperCustomerId(WebSession webSession, int key, int superCustId)
   {
      executeSqlQuery(webSession, "UPDATE AccountEntity SET SuperCustomerID=" + superCustId + " WHERE Id=" + key);
   }

   public String toString()
   {
      return "AccountSqlAdapter [ " + " ]";
   }

   @Override
   protected Vector<AccountEntityData> translateRsToValueObjects(ResultSet rs) throws SQLException
   {
      Vector<AccountEntityData> vVector = new Vector<AccountEntityData>();
      while (rs.next())
      {
      	AccountEntityData entry = new AccountEntityData();
         entry.setId(rs.getInt(1));
         entry.setWcPrime(rs.getInt(2));
         entry.setWcAlfa(null2EmpthyString(rs.getString(3)));
         entry.setFwdNumber(null2EmpthyString(rs.getString(4)));
         entry.setRole(null2EmpthyString(rs.getString(5)));
         entry.setFullName(null2EmpthyString(rs.getString(6)));
         entry.setIsAutoRelease(rs.getBoolean(7));
         entry.setIsXmlMail(rs.getBoolean(8));
         entry.setEmail(null2EmpthyString(rs.getString(9)));
         entry.setGsm(null2EmpthyString(rs.getString(10)));
         entry.setInvoiceType(rs.getShort(11));
         entry.setLastInvoiceTime(rs.getLong(12));
         entry.setLastMailTime(rs.getLong(13));
         entry.setMailHour1(rs.getShort(14));
         entry.setMailMinutes1(rs.getShort(15));
         entry.setMailHour2(rs.getShort(16));
         entry.setMailMinutes2(rs.getShort(17));
         entry.setMailHour3(rs.getShort(18));
         entry.setMailMinutes3(rs.getShort(19));
         entry.setFacStdInCall(rs.getInt(20));
         entry.setFacOutLevel1(rs.getInt(21));
         entry.setFacOutLevel2(rs.getInt(22));
         entry.setFacOutLevel3(rs.getInt(23));
         entry.setIsPriceAgendaFixed(rs.getBoolean(24));
         entry.setFacAgendaCall(rs.getInt(25));
         entry.setAgendaPriceUnit(rs.getShort(26));
         entry.setFacSms(rs.getInt(27));
         entry.setFacCallForward(rs.getInt(28));
         entry.setFacStdOutCall(rs.getInt(29));
         entry.setTaskHourRate(rs.getInt(30));
         entry.setCompanyName(null2EmpthyString(rs.getString(31)));
         entry.setAttToName(null2EmpthyString(rs.getString(32)));
         entry.setStreet(null2EmpthyString(rs.getString(33)));
         entry.setCity(null2EmpthyString(rs.getString(34)));
         entry.setBtwNumber(null2EmpthyString(rs.getString(35)));
         entry.setNoInvoice(rs.getBoolean(36));
         entry.setFacFaxCall(rs.getInt(37));
         entry.setHasSubCustomers(rs.getBoolean(38));
         entry.setSuperCustomer(null2EmpthyString(rs.getString(39)));
         entry.setSuperCustomerId(rs.getInt(40));
         entry.setCountAllLongCalls(rs.getBoolean(41));
         entry.setCountLongFwdCalls(rs.getBoolean(42));
         entry.setNoBtw(rs.getBoolean(43));
         entry.setNoEmptyMails(rs.getBoolean(44));
         entry.setTextMail(rs.getBoolean(45));
         entry.setFacLong(rs.getDouble(46));
         entry.setFacLongFwd(rs.getDouble(47));
         entry.setFacTblMinCalls_I(rs.getInt(48));
         entry.setFacTblMinCalls_II(rs.getInt(49));
         entry.setFacTblMinCalls_III(rs.getInt(50));
         entry.setFacTblMinCalls_IV(rs.getInt(51));
         entry.setFacTblStartCost_I(rs.getDouble(52));
         entry.setFacTblStartCost_II(rs.getDouble(53));
         entry.setFacTblStartCost_III(rs.getDouble(54));
         entry.setFacTblStartCost_IV(rs.getDouble(55));
         entry.setFacTblExtraCost_I(rs.getDouble(56));
         entry.setFacTblExtraCost_II(rs.getDouble(57));
         entry.setFacTblExtraCost_III(rs.getDouble(58));
         entry.setFacTblExtraCost_IV(rs.getDouble(59));
         entry.setIsMailInvoice(rs.getBoolean(60));
         entry.setInvoiceEmail(null2EmpthyString(rs.getString(61)));
         entry.setAccountNr(null2EmpthyString(rs.getString(62)));
         entry.setCountryCode(null2EmpthyString(rs.getString(63)));
         entry.setIsArchived(rs.getBoolean(64));
         entry.setCallProcessInfo(null2EmpthyString(rs.getString(65)));
         entry.setRedirectAccountId(rs.getInt(66));

         vVector.add(entry);
         // log.info("read from DB:" + entry.toNameValueString());
      }
      return vVector;
   }

}
