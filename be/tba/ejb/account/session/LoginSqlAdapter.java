package be.tba.ejb.account.session;

import java.util.Collection;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.ResultSet;

import be.tba.ejb.account.interfaces.LoginEntityData;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.AccountRole;
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
 * @ejb:ejb-ref ejb-name="LoginEntity"
 *
 */
public class LoginSqlAdapter extends AbstractSqlAdapter<LoginEntityData>
{
	private static Logger log = LoggerFactory.getLogger(LoginSqlAdapter.class);

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

    public LoginSqlAdapter()
    {
        super("LoginEntity");
    }

    public LoginEntityData logIn(WebSession webSession, String userid, String password) throws AccountNotFoundException
    {
        Collection<LoginEntityData> collection = executeSqlQuery(webSession, "SELECT * FROM LoginEntity WHERE Userid= BINARY '" + userid + "' AND Password= BINARY '" + password + "'");
        if (collection.size() == 1)
        {
            LoginEntityData account = collection.iterator().next();
            return account;
        }
        log.error("Login FAILED: userid=" + userid + ", Password=" + password);
        throw new AccountNotFoundException("De user id/paswoord combinatie is foutief.");
    }

//    public void deregister(WebSession webSession, int pkey) throws AccountNotFoundException
//    {
//        LoginEntityData account = getRow(webSession, pkey);
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

    public String register(WebSession webSession, LoginEntityData data)
    {
        try
        {
           //LoginEntityData newLogin = new LoginEntityData();
           
           Collection<LoginEntityData> collection = executeSqlQuery(webSession, "SELECT * FROM LoginEntity WHERE Userid='" + data.getUserId() + "'");
             if (collection != null && collection.size() > 0)
             {
                 return "Uw login user naam is al in gebruik. Kies een andere login naam en probeer opnieuw.";
             }
             data.setIsRegistered(true);
             addRow(webSession, data);
             return null;
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage(), ex);
        }
        return "Interne fout tijdens registratie";
    }
    
    public Collection<LoginEntityData> getEmployeeList(WebSession webSession)
    {
       return executeSqlQuery(webSession, "SELECT * FROM LoginEntity WHERE Role='" + AccountRole.EMPLOYEE.getShort() + "' OR Role='" + AccountRole.ADMIN.getShort() + "'");
    }
    
    public Collection<LoginEntityData> getLoginList(WebSession webSession, int accountId)
    {
        return executeSqlQuery(webSession, "SELECT * FROM LoginEntity WHERE AccountId=" + accountId);
    }
    
    
    public void updateLastLogin(WebSession webSession, LoginEntityData login)
    {
       executeSqlQuery(webSession, "UPDATE LoginEntity SET LastLogin='" + login.getLastLogin() + "' WHERE Id='" + login.getId() + "'");
    }

    public void setFilter(WebSession webSession, CallFilter filter, int pkey)
    {
        executeSqlQuery(webSession, "UPDATE LoginEntity SET CustFilter=" + filter.getCustFilter() + ",StateFilter='" + ((filter.getStateFilter() != null) ? filter.getStateFilter() : "") + "',DirFilter='" + ((filter.getDirFilter() != null) ? filter.getDirFilter() : "' WHERE Id=" + pkey));
    }

   @Override
   protected Vector<LoginEntityData> translateRsToValueObjects(ResultSet rs) throws SQLException
   {
      Vector<LoginEntityData> vVector = new Vector<LoginEntityData>();
      while (rs.next())
      {
         LoginEntityData entry = new LoginEntityData();
          entry.setId(rs.getInt(1));
          entry.setName(null2EmpthyString(rs.getString(2)));
          entry.setUserId(null2EmpthyString(rs.getString(3)));
          entry.setPassword(null2EmpthyString(rs.getString(4)));
          entry.setAccountId(rs.getInt(5));
          entry.setRole(null2EmpthyString(rs.getString(6)));
          entry.setCustFilter(rs.getInt(7));
          entry.setStateFilter(null2EmpthyString(rs.getString(8)));
          entry.setDirFilter(null2EmpthyString(rs.getString(9)));
          entry.setLastLogin(null2EmpthyString(rs.getString(10)));
          entry.setLastLoginTS(rs.getLong(11));
          entry.setPreviousLoginTS(rs.getLong(12));
          entry.setIsRegistered(rs.getBoolean(13));

          vVector.add(entry);
          // log.info("read from DB:" + entry.toNameValueString());
      }
      return vVector;
   }
}
