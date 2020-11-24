package be.tba.business;

import java.util.Calendar;
import java.util.Collection;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.session.SessionManager;
import be.tba.session.SessionParmsInf;
import be.tba.session.WebSession;
import be.tba.sqladapters.LoginSqlAdapter;
import be.tba.sqldata.AccountCache;
import be.tba.sqldata.LoginEntityData;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.AccountNotFoundException;
import be.tba.util.exceptions.SystemErrorException;

public class LoginBizzLogic
{
   private static Logger log = LoggerFactory.getLogger(LoginBizzLogic.class);

   public static LoginEntityData logIn(WebSession webSession, String userid, String password, boolean isCustomer) throws AccountNotFoundException
   {
      LoginSqlAdapter loginSession = new LoginSqlAdapter();
      LoginEntityData login = loginSession.logIn(webSession, userid, password);
      if (login != null)
      {
         if (isCustomer)
         {
            if (!login.getRole().equals(AccountRole._vCustomer) || login.getAccountId() == 0)
               throw new AccountNotFoundException("Toegang geweigerd.");
         }
         else
         {
            if (login.getRole().equals(AccountRole._vCustomer))
               throw new AccountNotFoundException("Toegang geweigerd.");
         }
      } 
      else
      {
         log.error("login not found: userid=" + userid);
         throw new AccountNotFoundException("Toegang geweigerd.");
      }
      
      Calendar vCalendar = Calendar.getInstance();
      int vMinutes = vCalendar.get(Calendar.MINUTE);
      String vLoginTime = new String(vCalendar.get(Calendar.DAY_OF_MONTH) + "/" + (vCalendar.get(Calendar.MONTH) + 1) + "/" + vCalendar.get(Calendar.YEAR) + " " + vCalendar.get(Calendar.HOUR_OF_DAY) + ":" + (vMinutes < 10 ? "0" : "") + vMinutes);
      login.setPreviousLoginTS(login.getLastLoginTS());
      login.setLastLoginTS(vCalendar.getTimeInMillis());
      login.setLastLogin(vLoginTime);
      webSession.setLogin(login);
      webSession.setAccountId(login.getAccountId());
      SessionManager.getInstance().add(webSession, userid);
      loginSession.updateLastLogin(webSession, login);
      //log.info("Login: userid=" + userid);
      return login;
   }

   public static Vector<String> addLogin(WebSession session, HttpServletRequest req, SessionParmsInf parms) throws SystemErrorException
   {
      // log.info("addLogin");
      LoginEntityData newLogin = new LoginEntityData();

      Vector<String> vErrorList = ValidateEmployeeFields(parms);
      if (vErrorList.size() > 0)
      {
         req.setAttribute(Constants.ERROR_VECTOR, vErrorList);
         return vErrorList;
      }
      newLogin.setRole(parms.getParameter(Constants.LOGIN_ROLE));
      newLogin.setUserId(parms.getParameter(Constants.LOGIN_USERID));
      newLogin.setPassword(parms.getParameter(Constants.LOGIN_PASSWORD));
      newLogin.setName(parms.getParameter(Constants.ACCOUNT_FULLNAME));
      newLogin.setLastLoginTS(Calendar.getInstance().getTimeInMillis());
      log.info("no error on employee add");
      LoginSqlAdapter vLoginSqlSession = new LoginSqlAdapter();
      vLoginSqlSession.addRow(session, newLogin);
      AccountCache.getInstance().update(session);
      return null;
   }

   public static void deleteLogin(WebSession session)
   {
      LoginSqlAdapter loginSession = new LoginSqlAdapter();
      loginSession.deleteRow(session, session.mLoginToDelete);
   }

   public static String register(WebSession session, LoginEntityData loginData)
   {
      LoginSqlAdapter vLoginSqlSession = new LoginSqlAdapter();
      return vLoginSqlSession.register(session, loginData);
   }

   public static void cleanup(WebSession session)
   {
      LoginSqlAdapter loginSqlAdapter = new LoginSqlAdapter();
      Collection<LoginEntityData> logins = loginSqlAdapter.getAllRows(session);
      long now = Calendar.getInstance().getTimeInMillis();
      for (LoginEntityData login : logins)
      {
         if ((login.getRole().equals(AccountRole._vCustomer) || login.getRole().equals(AccountRole._vSubCustomer)) &&   
              login.getLastLoginTS() < now - Constants.LOGIN_DELETE_EXPIRE)
         {
            loginSqlAdapter.deleteRow(session, login.getId());
         }
      }

   }
   private static Vector<String> ValidateEmployeeFields(SessionParmsInf parms)
   {
      Vector<String> vFormFaults = new Vector<String>();

      String vUserId = parms.getParameter(Constants.LOGIN_USERID);
      String vPassword = parms.getParameter(Constants.LOGIN_PASSWORD);
      String vPassword2 = parms.getParameter(Constants.LOGIN_PASSWORD2);

      if (vUserId == null)
         vFormFaults.add("Login naam niet ingevuld.");
      if (vPassword == null)
         vFormFaults.add("Paswoord veld 1 niet ingevuld.");
      if (vPassword2 == null)
         vFormFaults.add("Paswoord veld 2 niet ingevuld.");
      if (vUserId.length() < 5 || vUserId.length() > 10)
         vFormFaults.add("login naam moet minstens 5 en maximaal 10 karakters bevatten.");
      if (vPassword.length() < 6)
         vFormFaults.add("Paswoord moet minimaal 6 karakters bevatten.");
      if (!vPassword.equals(vPassword2))
         vFormFaults.add("Paswoorden zijn niet identiek.");
      return vFormFaults;
   }

}
