package be.tba.ejb.account.session;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.ResultSet;

import be.tba.dbpopul.DbPopulator;
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
            log.info("Login: userid=" + userid + " (" + account.getFullName() + ")");
            return account;
        }
        log.info("Login FAILED: userid=" + userid + ", Password=" + password);
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
        log.info("Unexpected number of account (" + collection.size() + ") found for FwdNumber=" + fwdNr);
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
        log.info("FAILED getUnregistered(" + regCode + ")");
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
            entry.setEmail(null2EmpthyString(rs.getString(18)));
            entry.setGsm(null2EmpthyString(rs.getString(19)));
            entry.setInvoiceType(rs.getShort(20));
            entry.setLastInvoiceTime(rs.getLong(21));
            entry.setLastMailTime(rs.getLong(22));
            entry.setMailHour1(rs.getShort(23));
            entry.setMailMinutes1(rs.getShort(24));
            entry.setMailHour2(rs.getShort(25));
            entry.setMailMinutes2(rs.getShort(26));
            entry.setMailHour3(rs.getShort(27));
            entry.setMailMinutes3(rs.getShort(28));
            entry.setFacStdInCall(rs.getInt(29));
            entry.setFacOutLevel1(rs.getInt(30));
            entry.setFacOutLevel2(rs.getInt(31));
            entry.setFacOutLevel3(rs.getInt(32));
            entry.setIsPriceAgendaFixed(rs.getBoolean(33));
            entry.setFacAgendaCall(rs.getInt(34));
            entry.setAgendaPriceUnit(rs.getShort(35));
            entry.setFacSms(rs.getInt(36));
            entry.setFacCallForward(rs.getInt(37));
            entry.setFacStdOutCall(rs.getInt(38));
            entry.setTaskHourRate(rs.getInt(39));
            entry.setCompanyName(null2EmpthyString(rs.getString(40)));
            entry.setAttToName(null2EmpthyString(rs.getString(41)));
            entry.setStreet(null2EmpthyString(rs.getString(42)));
            entry.setCity(null2EmpthyString(rs.getString(43)));
            entry.setBtwNumber(null2EmpthyString(rs.getString(44)));
            entry.setNoInvoice(rs.getBoolean(45));
            entry.setFacFaxCall(rs.getInt(46));
            entry.setHasSubCustomers(rs.getBoolean(47));
            entry.setSuperCustomer(null2EmpthyString(rs.getString(48)));
            entry.setSuperCustomerId(rs.getInt(49));
            entry.setCountAllLongCalls(rs.getBoolean(50));
            entry.setCountLongFwdCalls(rs.getBoolean(51));
            entry.setNoBtw(rs.getBoolean(52));
            entry.setNoEmptyMails(rs.getBoolean(53));
            entry.setTextMail(rs.getBoolean(54));
            entry.setFacLong(rs.getDouble(55));
            entry.setFacLongFwd(rs.getDouble(56));
            entry.setFacTblMinCalls_I(rs.getInt(57));
            entry.setFacTblMinCalls_II(rs.getInt(58));
            entry.setFacTblMinCalls_III(rs.getInt(59));
            entry.setFacTblMinCalls_IV(rs.getInt(60));
            entry.setFacTblStartCost_I(rs.getDouble(61));
            entry.setFacTblStartCost_II(rs.getDouble(62));
            entry.setFacTblStartCost_III(rs.getDouble(63));
            entry.setFacTblStartCost_IV(rs.getDouble(64));
            entry.setFacTblExtraCost_I(rs.getDouble(65));
            entry.setFacTblExtraCost_II(rs.getDouble(66));
            entry.setFacTblExtraCost_III(rs.getDouble(67));
            entry.setFacTblExtraCost_IV(rs.getDouble(68));
            entry.setIsMailInvoice(rs.getBoolean(69));
            entry.setInvoiceEmail(null2EmpthyString(rs.getString(70)));
            entry.setAccountNr(null2EmpthyString(rs.getString(71)));
            entry.setCountryCode(null2EmpthyString(rs.getString(72)));
            entry.setIsArchived(rs.getBoolean(73));
            entry.setCallProcessInfo(null2EmpthyString(rs.getString(74)));
            entry.setRedirectAccountId(rs.getInt(75));

            vVector.add(entry);
            // log.info("read from DB:" + entry.toNameValueString());
        }
        return vVector;
    }
}
