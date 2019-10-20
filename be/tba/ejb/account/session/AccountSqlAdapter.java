package be.tba.ejb.account.session;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.sql.SQLException;
import java.sql.ResultSet;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;
import be.tba.util.data.AbstractSqlAdapter;
import be.tba.util.data.CallFilter;
import be.tba.util.data.RegisterData;
import be.tba.util.exceptions.remote.AccountNotFoundException;

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

    public AccountEntityData logIn(WebSession webSession, String userid, String password) throws AccountNotFoundException
    {
        Collection<AccountEntityData> collection = executeSqlQuery(webSession, "SELECT * FROM AccountEntity WHERE Userid='" + userid + "' AND Password='" + password + "'");
        if (collection.size() == 1)
        {
            AccountEntityData account = collection.iterator().next();
            Calendar vCalendar = Calendar.getInstance();
            int vMinutes = vCalendar.get(Calendar.MINUTE);
            String vLoginTime = new String(vCalendar.get(Calendar.DAY_OF_MONTH) + "/" + (vCalendar.get(Calendar.MONTH) + 1) + "/" + vCalendar.get(Calendar.YEAR) + " " + vCalendar.get(Calendar.HOUR_OF_DAY) + ":" + (vMinutes < 10 ? "0" : "") + vMinutes);
            account.setPreviousLoginTS(account.getLastLoginTS());
            account.setLastLoginTS(vCalendar.getTimeInMillis());
            account.setLastLogin(vLoginTime);
            executeSqlQuery(webSession, "UPDATE AccountEntity SET LastLogin='" + vLoginTime + "' WHERE Id='" + account.getId() + "'");
            System.out.println("Login: userid=" + userid + " (" + account.getFullName() + ")");
            return account;
        }
        System.out.println("Login FAILED: userid=" + userid + ", Password=" + password);
        throw new AccountNotFoundException("De user id/paswoord combinatie is foutief.");
    }

    public Collection<AccountEntityData> getAllNotArchived(WebSession session)
    {
        return executeSqlQuery(session, "SELECT * FROM AccountEntity where IsArchived=false");
    }
 
/*    
    public AccountEntityData getAccountByFwdNr(WebSession webSession, String fwdNr) throws AccountNotFoundException
    {
        Collection<AccountEntityData> collection = executeSqlQuery(webSession, "SELECT * FROM AccountEntity WHERE FwdNumber='" + fwdNr + "'");
        if (collection.size() == 1)
        {
            return collection.iterator().next();
        }
        System.out.println("Unexpected number of account (" + collection.size() + ") found for FwdNumber=" + fwdNr);
        throw new AccountNotFoundException("De user id/paswoord combinatie is foutief.");
    }
*/
    public void deregister(WebSession webSession, int pkey) throws AccountNotFoundException
    {
        AccountEntityData account = getRow(webSession, pkey);
        if (account != null)
        {
            account.setUserId("");
            account.setPassword("");
            account.setIsRegistered(false);
            updateRow(webSession, account);
            return;
        }
        throw new AccountNotFoundException("Geen gebruiker gevonden voor key=" + pkey);
    }

    public AccountEntityData getUnregistered(WebSession webSession, String regCode) throws AccountNotFoundException
    {
        Collection<AccountEntityData> collection = executeSqlQuery(webSession, "SELECT * FROM AccountEntity WHERE FwdNumber='" + regCode + "'");
        if (collection.size() == 1)
        {
            return collection.iterator().next();
        }
        System.out.println("FAILED getUnregistered(" + regCode + ")");
        throw new AccountNotFoundException("De registratie code is niet correct.");
    }

    public String register(WebSession webSession, RegisterData data)
    {
        try
        {
            AccountEntityData account = getUnregistered(webSession, data.getCode());
            if (account != null)
            {
                Collection<AccountEntityData> collection = executeSqlQuery(webSession, "SELECT * FROM AccountEntity WHERE Userid='" + data.getUserId() + "'");
                if (collection != null && collection.size() > 0)
                {
                    return "Uw login naam wordt al door iemand anders gebruikt. Kies een andere en registreer opnieuw.";
                }
                account.setUserId(data.getUserId());
                account.setPassword(data.getPassword());
                // if (data.getFullName() != null && data.getFullName().length() > 0)
                // account.setFullName(data.getFullName());
                if (data.getEmail() != null && data.getEmail().length() > 0)
                    account.setEmail(data.getEmail());
                account.setIsRegistered(true);
                updateRow(webSession, account);
                return null;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "Interne fout tijdens registratie";
    }

    public void addAccount(WebSession webSession, AccountEntityData data)
    {
        for (int i = 0; i < 2; ++i)
        {
            if (data.getFwdNumber().equals(Constants.NUMBER_BLOCK[i][0]))
            {
                return;
            }
        }
        addRow(webSession, data);
    }

    public void archiveAccount(WebSession webSession, int key)
    {
        executeSqlQuery(webSession, "UPDATE AccountEntity SET IsArchived=true, FwdNumber='' WHERE Id=" + key);
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
        executeSqlQuery(webSession, "UPDATE AccountEntity SET CustFilter='" + ((filter.getCustFilter() != null) ? filter.getCustFilter() : "") + "',StateFilter='" + ((filter.getStateFilter() != null) ? filter.getStateFilter() : "") + "',DirFilter='" + ((filter.getDirFilter() != null) ? filter.getDirFilter() : "' WHERE Id=" + pkey));
    }

    public Collection<String> getFreeNumbers(WebSession webSession)
    {
        Vector<String> vFreeNumbers = new Vector<String>();
        Collection<AccountEntityData> accounts = getAllRows(webSession);

        for (int i = 1; i < Constants.NUMBER_BLOCK.length; i++)
        {
            vFreeNumbers.add(Constants.NUMBER_BLOCK[i][0]);
        }
        for (Iterator<AccountEntityData> i = accounts.iterator(); i.hasNext();)
        {
            vFreeNumbers.remove(i.next().getFwdNumber());
        }
        return vFreeNumbers;
    }

    
    public Collection<AccountEntityData>  getAccountListByIdList(WebSession webSession, Collection<Integer> list)
    {
        StringBuffer strBuf = new StringBuffer();
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
            entry.setUserId(null2EmpthyString(rs.getString(4)));
            entry.setPassword(null2EmpthyString(rs.getString(5)));
            entry.setFwdNumber(null2EmpthyString(rs.getString(6)));
            entry.setRole(null2EmpthyString(rs.getString(7)));
            entry.setFullName(null2EmpthyString(rs.getString(8)));
            entry.setCustFilter(null2EmpthyString(rs.getString(9)));
            entry.setStateFilter(null2EmpthyString(rs.getString(10)));
            entry.setDirFilter(null2EmpthyString(rs.getString(11)));
            entry.setLastLogin(null2EmpthyString(rs.getString(12)));
            entry.setLastLoginTS(rs.getLong(13));
            entry.setPreviousLoginTS(rs.getLong(14));
            entry.setIsRegistered(rs.getBoolean(15));
            entry.setIsAutoRelease(rs.getBoolean(16));
            entry.setIsXmlMail(rs.getBoolean(17));
            entry.setIs3W(rs.getBoolean(18));
            entry.setW3_PersonId(null2EmpthyString(rs.getString(19)));
            entry.setW3_CompanyId(null2EmpthyString(rs.getString(20)));
            entry.setEmail(null2EmpthyString(rs.getString(21)));
            entry.setGsm(null2EmpthyString(rs.getString(22)));
            entry.setInvoiceType(rs.getShort(23));
            entry.setLastInvoiceTime(rs.getLong(24));
            entry.setLastMailTime(rs.getLong(25));
            entry.setMailHour1(rs.getShort(26));
            entry.setMailMinutes1(rs.getShort(27));
            entry.setMailHour2(rs.getShort(28));
            entry.setMailMinutes2(rs.getShort(29));
            entry.setMailHour3(rs.getShort(30));
            entry.setMailMinutes3(rs.getShort(31));
            entry.setFacStdInCall(rs.getInt(32));
            entry.setFacOutLevel1(rs.getInt(33));
            entry.setFacOutLevel2(rs.getInt(34));
            entry.setFacOutLevel3(rs.getInt(35));
            entry.setIsPriceAgendaFixed(rs.getBoolean(36));
            entry.setFacAgendaCall(rs.getInt(37));
            entry.setAgendaPriceUnit(rs.getShort(38));
            entry.setFacSms(rs.getInt(39));
            entry.setFacCallForward(rs.getInt(40));
            entry.setFacStdOutCall(rs.getInt(41));
            entry.setTaskHourRate(rs.getInt(42));
            entry.setCompanyName(null2EmpthyString(rs.getString(43)));
            entry.setAttToName(null2EmpthyString(rs.getString(44)));
            entry.setStreet(null2EmpthyString(rs.getString(45)));
            entry.setCity(null2EmpthyString(rs.getString(46)));
            entry.setBtwNumber(null2EmpthyString(rs.getString(47)));
            entry.setNoInvoice(rs.getBoolean(48));
            entry.setFacFaxCall(rs.getInt(49));
            entry.setHasSubCustomers(rs.getBoolean(50));
            entry.setSuperCustomer(null2EmpthyString(rs.getString(51)));
            entry.setSuperCustomerId(rs.getInt(52));
            entry.setCountAllLongCalls(rs.getBoolean(53));
            entry.setCountLongFwdCalls(rs.getBoolean(54));
            entry.setNoBtw(rs.getBoolean(55));
            entry.setNoEmptyMails(rs.getBoolean(56));
            entry.setTextMail(rs.getBoolean(57));
            entry.setFacLong(rs.getDouble(58));
            entry.setFacLongFwd(rs.getDouble(59));
            entry.setFacTblMinCalls_I(rs.getInt(60));
            entry.setFacTblMinCalls_II(rs.getInt(61));
            entry.setFacTblMinCalls_III(rs.getInt(62));
            entry.setFacTblMinCalls_IV(rs.getInt(63));
            entry.setFacTblStartCost_I(rs.getDouble(64));
            entry.setFacTblStartCost_II(rs.getDouble(65));
            entry.setFacTblStartCost_III(rs.getDouble(66));
            entry.setFacTblStartCost_IV(rs.getDouble(67));
            entry.setFacTblExtraCost_I(rs.getDouble(68));
            entry.setFacTblExtraCost_II(rs.getDouble(69));
            entry.setFacTblExtraCost_III(rs.getDouble(70));
            entry.setFacTblExtraCost_IV(rs.getDouble(71));
            entry.setIsMailInvoice(rs.getBoolean(72));
            entry.setInvoiceEmail(null2EmpthyString(rs.getString(73)));
            entry.setAccountNr(null2EmpthyString(rs.getString(74)));
            entry.setCountryCode(null2EmpthyString(rs.getString(75)));
            entry.setIsArchived(rs.getBoolean(76));
            entry.setCallProcessInfo(null2EmpthyString(rs.getString(77)));
            vVector.add(entry);
            // System.out.println("read from DB:" + entry.toNameValueString());
        }
        return vVector;
    }
}
