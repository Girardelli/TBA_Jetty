package be.tba.servlets;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.business.LoginBizzLogic;
import be.tba.session.SessionManager;
import be.tba.session.WebSession;
import be.tba.sqldata.AccountCache;
import be.tba.sqldata.LoginEntityData;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.data.RegisterData;
import be.tba.util.exceptions.AccountNotFoundException;
import be.tba.util.exceptions.SystemErrorException;

public class LoginServlet extends HttpServlet
{
   /**
    *
    */
   private static final long serialVersionUID = 8497444764812881017L;
   private static Logger log = LoggerFactory.getLogger(LoginServlet.class);

   public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      req.setCharacterEncoding("UTF-8");
      res.setContentType("text/html");
//        LoginSqlAdapter vLoginSqlSession = null;
      RequestDispatcher rd = null;
      ServletContext sc = null;
      WebSession vSession = null;

      String vUserId = req.getParameter(Constants.LOGIN_USERID);
      String vPassword = req.getParameter(Constants.LOGIN_PASSWORD);

      try
      {
         sc = getServletContext();
         String vAction = (String) req.getParameter(Constants.SRV_ACTION);
         if (vAction == null)
         {
            vAction = (String) req.getAttribute(Constants.SRV_ACTION);
            log.error(Constants.SRV_ACTION + "=" + vAction);
            throw new Exception("getAttribute called");
         }
         // log.info("LoginServlet: action=" + vAction);

         rd = sc.getRequestDispatcher(Constants.REGISTER_JSP);

         switch (vAction)
         {

         // ==============================================================================================
         // LOGIN
         // ==============================================================================================
         case Constants.ACTION_LOGIN:
         {
            if (vUserId == null || vPassword == null)
               throw new SystemErrorException("User id or password null.");

//                LoginEntityData vLogin = null;
            vSession = new WebSession();
            LoginBizzLogic.logIn(vSession, vUserId, vPassword);
            HttpSession httpSession = req.getSession();
            httpSession.setAttribute(Constants.SESSION_OBJ, vSession);

//                if (!vSession.mLoginData.getIsRegistered())
//                {
//                    Vector<String> vErrorList = new Vector<String>();
//                    vErrorList.add("U bent nog niet geregistreerd.");
//                    req.setAttribute(Constants.ERROR_VECTOR, vErrorList);
//                    rd = sc.getRequestDispatcher(Constants.REGISTER_JSP);
//                }
//                else if (vSession.mLoginData.getRole().equals(AccountRole.CUSTOMER.getShort()))
//                {
//                    // Customer with access has logged in.
//                    HttpSession httpSession = req.getSession();
//                    httpSession.setAttribute(Constants.SESSION_OBJ, vSession);
//                    SessionManager.getInstance().add(vSession, vUserId);
//                    vSession.setRole(AccountRole.fromShort(vSession.mLoginData.getRole()));
//                    vSession.setAccountId(vLogin.getAccountId());
//                    Calendar calendar = Calendar.getInstance();
//                    vSession.setYear(calendar.get(Calendar.YEAR));
//                    vSession.setMonthsBack(calendar.get(Calendar.MONTH));
//                    rd = sc.getRequestDispatcher(Constants.CLIENT_CALLS_JSP);
//                    log.info("LoginServlet: " + vSession.mLoginData.getUserId() + " got session id " + vSession.getSessionId());
//                }
//                else
//                {
//                    Vector<String> vErrorList = new Vector<String>();
//                    vErrorList.add("Enkel een klant of administrator kan deze pagina's gebruiken!");
//                    req.setAttribute(Constants.ERROR_VECTOR, vErrorList);
//                    rd = sc.getRequestDispatcher(Constants.REGISTER_JSP);
//                }
            log.info("LoginServlet: " + vSession.mLoginData.getUserId() + " got session id " + vSession.getSessionId());
            rd = sc.getRequestDispatcher(Constants.CLIENT_CALLS_JSP);
            break;
         }

         // ==============================================================================================
         // REGISTER
         // ==============================================================================================
         case Constants.ACTION_REGISTER:
         {
            Vector<String> vErrorList = ValidateRegistrationForm(req);
            if (vErrorList.size() > 0)
            {
               req.setAttribute(Constants.ERROR_VECTOR, vErrorList);
               rd = sc.getRequestDispatcher(Constants.REGISTER_JSP);
            }
            else
            {
               HttpSession httpSession = req.getSession();
               if (vSession == null)
               {
                  vSession = new WebSession();
               }
               httpSession.setAttribute(Constants.SESSION_OBJ, vSession);
               vErrorList = tryRegister(req, vSession);
               if (vErrorList.size() > 0)
               {
                  req.setAttribute(Constants.ERROR_VECTOR, vErrorList);
                  rd = sc.getRequestDispatcher(Constants.REGISTER_JSP);
               }
               else
               {
                  // register was successful, login
                  LoginBizzLogic.logIn(vSession, vUserId, vPassword);
                  rd = sc.getRequestDispatcher(Constants.CLIENT_CALLS_JSP);
               }
            }
            break;
         }
         // ==============================================================================================
         // DICTAAT FORM
         // ==============================================================================================
         case Constants.ACTION_DICTAAT_FORM:
         {
            Vector<String> vErrorList = ValidateDictaatForm(req);
            if (vErrorList.size() > 0)
            {
               req.setAttribute(Constants.ERROR_VECTOR, vErrorList);
               rd = sc.getRequestDispatcher(Constants.DICTAAT_FORM_JSP);
            }
            else
            {
               rd = sc.getRequestDispatcher(Constants.FORM_SUBMIT_SUCCESS);
            }
            break;
         }
         case Constants.TBA_HOME:
         {
            rd = sc.getRequestDispatcher(Constants.HOME_HTML);
            break;
         }
         case Constants.ACTION_FIRST_REGISTER:
         {
            rd = sc.getRequestDispatcher(Constants.REGISTER_JSP);
            break;
         }
         default:
         {
            throw new Exception("unknown action: " + vAction);
         }
         }
      }
      catch (AccountNotFoundException e)
      {
         log.info("Mallicious admin access attempt. userid:" + vUserId + ", password:" + vPassword);
         req.setAttribute(Constants.ERROR_TXT, e.getMessage());
         rd = sc.getRequestDispatcher(Constants.PROTECT_FAIL_JSP);
      }
      catch (SystemErrorException e)
      {
         log.error(e.getMessage(), e);
         rd = sc.getRequestDispatcher(Constants.SERVLET_LOGIN_HTML);
         rd.forward(req, res);
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
         req.setAttribute(Constants.ERROR_TXT, "Onbekende error! Meldt deze error bij <a href=\"mailto:webmaster@thebusinessassistant.be\">webmaster@thebusinessassistant.be</a>.");
         rd = sc.getRequestDispatcher(Constants.PROTECT_FAIL_JSP);
      }
      rd.forward(req, res);
   }

   public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      doGet(req, res);
   }

   private Vector<String> ValidateRegistrationForm(HttpServletRequest req)
   {
      Vector<String> vFormFaults = new Vector<String>();

      String vCode = req.getParameter(Constants.LOGIN_REGCODE);
      String vUserId = req.getParameter(Constants.LOGIN_USERID);
      String vPassword = req.getParameter(Constants.LOGIN_PASSWORD);
      String vPassword2 = req.getParameter(Constants.LOGIN_PASSWORD2);

      if (vCode == null)
         vFormFaults.add("Registratiecode niet ingevuld.");
      else if (LoginEntityData.checkLoginCode(vCode) < 0)
         vFormFaults.add("Foutieve registratiecode.");
      if (vUserId == null)
         vFormFaults.add("Login naam niet ingevuld.");
      if (vPassword == null)
         vFormFaults.add("Paswoord veld 1 niet ingevuld.");
      if (vPassword2 == null)
         vFormFaults.add("Paswoord veld 2 niet ingevuld.");
      if (vUserId.length() < 4 || vUserId.length() > 10)
         vFormFaults.add("login naam moet minstens 4 en maximaal 10 karakters bevatten.");
      if (vPassword.length() < 6)
         vFormFaults.add("Paswoord moet minimaal 6 karakters bevatten.");
      if (!vPassword.equals(vPassword2))
         vFormFaults.add("Paswoorden zijn niet identiek.");
      return vFormFaults;
   }

   private Vector<String> tryRegister(HttpServletRequest req, WebSession session)
   {
      Vector<String> vFormFaults = new Vector<String>();

      LoginEntityData vRegData = new LoginEntityData();

      int accountId = LoginEntityData.checkLoginCode(req.getParameter(Constants.LOGIN_REGCODE));
      if (accountId < 0)
      {
         log.error("account id could not be retrieved from registration code " + req.getParameter(Constants.LOGIN_REGCODE));
         vFormFaults.add("registratie code is geweigerd. Probeer opnieuw.");
      }
      vRegData.setRole(AccountRole.CUSTOMER.getShort());
      vRegData.setUserId(req.getParameter(Constants.LOGIN_USERID));
      vRegData.setPassword(req.getParameter(Constants.LOGIN_PASSWORD));
      vRegData.setAccountId(accountId);
      vRegData.setName(req.getParameter(Constants.LOGIN_NAME));
      try
      {
         String vErrorStr = LoginBizzLogic.register(session, vRegData);
         if (vErrorStr != null)
         {
            vFormFaults.add(vErrorStr);
         }
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
         vFormFaults.add("Fout: " + e.getMessage());
      }
      // no errors in this list means successful registration
      return vFormFaults;
   }

   private Vector<String> ValidateDictaatForm(HttpServletRequest req)
   {
      Vector<String> vFormFaults = new Vector<String>();
      String vName = (String) req.getParameter(Constants.FORM_NAME);
      String vTel = (String) req.getParameter(Constants.FORM_TEL);
      String vBrand = (String) req.getParameter(Constants.FORM_DIC_BRAND);
      String vDuration = (String) req.getParameter(Constants.FORM_DURATION);
      String vLanguage = (String) req.getParameter(Constants.FORM_LANGUAGE);

      if (vName == null || vName.length() == 0)
         vFormFaults.add("Uw naam is niet ingevuld.");
      if (vTel == null || vTel.length() == 0)
         vFormFaults.add("Telefoon nummer is niet ingevuld.");
      if (vBrand == null || vBrand.length() == 0)
         vFormFaults.add("Merk en type van uw dictafoon is niet ingevuld.");
      if (vDuration == null || vDuration.length() == 0)
         vFormFaults.add("De lengte het dictaat is niet ingevuld.");
      if (vLanguage == null || vLanguage.length() == 0)
         vFormFaults.add("de taal is niet ingevuld.");

      if (vFormFaults.size() == 0)
      {
         StringBuffer vBody = new StringBuffer();

         String vCompany = (String) req.getParameter(Constants.FORM_COMPANY);
         if (vCompany == null)
            vCompany = "";

         String vEmail = (String) req.getParameter(Constants.FORM_EMAIL);
         if (vEmail == null)
            vEmail = "";
         String vFax = (String) req.getParameter(Constants.FORM_FAX);
         if (vFax == null)
            vFax = "";
         String vText = (String) req.getParameter(Constants.FORM_TEXT);
         if (vText == null)
            vText = "";
         String vTechnology = (String) req.getParameter(Constants.FORM_DICTAAT_TECH);
         if (vTechnology == null)
            vTechnology = "";

         vBody.append("\r\n" + vText + "\r\n\r\n");
         vBody.append("Naam: " + vName + "\r\n");
         vBody.append("Bedrijf: " + vCompany + "\r\n");
         vBody.append("Telefoon: " + vTel + "\r\n");
         vBody.append("Fax: " + vFax + "\r\n");
         vBody.append("E-mail: " + vEmail + "\r\n\r\n");
         vBody.append("Merk en type dictafoon: " + vBrand + "\r\n");
         vBody.append("Technologie: " + (vTechnology.equals("digital") ? "digitaal" : "analoog") + "\r\n");
         vBody.append("Lengte van het dictaat: " + vDuration + " minuten\r\n");
         vBody.append("Taal: " + vLanguage);

         try
         {
            Address[] vTo = new InternetAddress[1];
            // vTo[0] = new
            // InternetAddress("yves.willems@theBusinessAssistant.be");
            vTo[0] = new InternetAddress(Constants.NANCY_EMAIL);

            InitialContext vContext = new InitialContext();
            // Session vMailSession = (Session)
            // PortableRemoteObject.narrow(vContext.lookup("java:comp/env/mail/Session"),
            // Session.class);
            Session vMailSession = (Session) vContext.lookup("java:comp/env/mail/Session");

            MimeMessage m = new MimeMessage(vMailSession);
            m.setFrom();

            m.setRecipients(Message.RecipientType.TO, vTo);
            m.setSubject("Dictaat aanvraag van " + vName);
            m.setSentDate(new Date());
            m.setContent(vBody.toString(), "text/plain");
            Transport.send(m);
         }
         catch (Exception e)
         {
            log.error(e.getMessage(), e);
            vFormFaults.add("Uw aanvraag kon niet verstuurd worden. Probeer ons te contacteren via onze contact gegevens.");
         }
      }
      return vFormFaults;
   }

   public void destroy()
   {
      log.info("LoginServlet destroyed.");
   }

}
