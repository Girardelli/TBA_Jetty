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

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.account.session.AccountSqlAdapter;
import be.tba.servlets.session.SessionManager;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.data.RegisterData;
import be.tba.util.exceptions.SystemErrorException;
import be.tba.util.exceptions.remote.AccountNotFoundException;
import be.tba.util.session.AccountCache;

public class LoginServlet extends HttpServlet
{
    /**
     *
     */
    private static final long serialVersionUID = 8497444764812881017L;

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        res.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");
        res.setContentType("text/html");
        AccountSqlAdapter vAccountSession = null;
        RequestDispatcher rd = null;
        ServletContext sc = null;
        WebSession vSession = null;

        String vUserId = req.getParameter(Constants.ACCOUNT_USERID);
        String vPassword = req.getParameter(Constants.ACCOUNT_PASSWORD);

        try
        {

            String vAction = (String) req.getParameter(Constants.SRV_ACTION);
            if (vAction == null)
                vAction = (String) req.getAttribute(Constants.SRV_ACTION);

            System.out.println("LoginServlet: action=" + vAction);

            sc = getServletContext();
            rd = sc.getRequestDispatcher(Constants.REGISTER_JSP);

            if (vAction == null)
            {
                throw new SystemErrorException("No action passed to LoginServlet.");
            }

            // ==============================================================================================
            // LOGIN
            // ==============================================================================================
            else if (vAction.equals(Constants.ACTION_LOGIN))
            {
                if (vUserId == null || vPassword == null)
                    throw new SystemErrorException("User id or password null.");

                AccountEntityData vAccount = null;
                if (vUserId.equals(Constants.MASTER_LOGIN_NAME))
                {
                    vAccount = new AccountEntityData();
                    vAccount.setRole(AccountRole.ADMIN.getShort());
                    System.out.println("LoginServlet: Master logged in");
                }
                else
                {
                    vSession = new WebSession();
                    vAccountSession = new AccountSqlAdapter();
                    vAccount = vAccountSession.logIn(vSession, vUserId, vPassword);
                    System.out.println("LoginServlet: " + vAccount.getFullName() + " logged in.");
                }

                if (!vAccount.getIsRegistered())
                {
                    Vector<String> vErrorList = new Vector<String>();
                    vErrorList.add("U bent nog niet geregistreerd.");
                    req.setAttribute(Constants.ERROR_VECTOR, vErrorList);
                    rd = sc.getRequestDispatcher(Constants.REGISTER_JSP);
                }
                else if (vAccount.getRole().equals(AccountRole.ADMIN.getShort()) || vAccount.getRole().equals(AccountRole.CUSTOMER.getShort()) || vAccount.getRole().equals(AccountRole.SUBCUSTOMER.getShort()))
                {
                    // Customer with access has logged in.
                    HttpSession httpSession = req.getSession();
                    if (vSession == null)
                    {
                        vSession = new WebSession();
                    }
                    httpSession.setAttribute(Constants.SESSION_OBJ, vSession);
                    String vKey = SessionManager.getInstance().add(vSession);

                    vSession.init(vUserId, vKey);
                    vSession.setRole(AccountRole.fromShort(vAccount.getRole()));
                    vSession.setFwdNumber(vAccount.getFwdNumber());
                    vSession.setIs3W(vAccount.getIs3W());
                    Calendar calendar = Calendar.getInstance();
                    vSession.setYear(calendar.get(Calendar.YEAR));
                    vSession.setMonthsBack(calendar.get(Calendar.MONTH));
                    rd = sc.getRequestDispatcher(Constants.CLIENT_CALLS_JSP);
                    System.out.println("LoginServlet: " + vAccount.getUserId() + " got session id " + vSession.getSessionId());
                }
                else
                {
                    Vector<String> vErrorList = new Vector<String>();
                    vErrorList.add("Enkel een klant of administrator kan deze pagina's gebruiken!");
                    req.setAttribute(Constants.ERROR_VECTOR, vErrorList);
                    rd = sc.getRequestDispatcher(Constants.REGISTER_JSP);
                }
            }

            // ==============================================================================================
            // REGISTER
            // ==============================================================================================
            else if (vAction.equals(Constants.ACTION_REGISTER))
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
                        String vKey = SessionManager.getInstance().add(vSession);
                        vSession.init(vUserId, vKey);
                        vSession.setRole(AccountRole.CUSTOMER);
                        vSession.setFwdNumber(req.getParameter(Constants.ACCOUNT_REGCODE));

                        // req.setAttribute(Constants.SESSION_ID, vKey);
                        // req.setAttribute(Constants.SESSION_OBJ, vSession);
                        rd = sc.getRequestDispatcher(Constants.CLIENT_CALLS_JSP);
                    }
                }
            }
            // ==============================================================================================
            // DICTAAT FORM
            // ==============================================================================================
            else if (vAction.equals(Constants.ACTION_DICTAAT_FORM))
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
            }
            else if (vAction.equals(Constants.TBA_HOME))
            {
                rd = sc.getRequestDispatcher(Constants.HOME_HTML);
            }
            else if (vAction.equals(Constants.ACTION_FIRST_REGISTER))
            {
                rd = sc.getRequestDispatcher(Constants.REGISTER_JSP);
            }
            else
            {
                throw new Exception("unknown action: " + vAction);
            }
        }
        catch (AccountNotFoundException e)
        {
            System.out.println("Mallicious admin access attempt. userid:" + vUserId + ", password:" + vPassword);
            req.setAttribute(Constants.ERROR_TXT, e.getMessage());
            rd = sc.getRequestDispatcher(Constants.PROTECT_FAIL_JSP);
        }
        catch (SystemErrorException e)
        {
            e.printStackTrace();
            rd = sc.getRequestDispatcher(Constants.SERVLET_LOGIN_HTML);
            rd.forward(req, res);
        }
        catch (Exception e)
        {
            e.printStackTrace();
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

        String vCode = req.getParameter(Constants.ACCOUNT_REGCODE);
        String vUserId = req.getParameter(Constants.ACCOUNT_USERID);
        String vPassword = req.getParameter(Constants.ACCOUNT_PASSWORD);
        String vPassword2 = req.getParameter(Constants.ACCOUNT_PASSWORD2);

        if (vCode == null)
            vFormFaults.add("Registratiecode niet ingevuld.");
        if (vUserId == null)
            vFormFaults.add("Login naam niet ingevuld.");
        if (vPassword == null)
            vFormFaults.add("Paswoord veld 1 niet ingevuld.");
        if (vPassword2 == null)
            vFormFaults.add("Paswoord veld 2 niet ingevuld.");
        if (vUserId.length() < 4 || vUserId.length() > 10)
            vFormFaults.add("login naam moet minstens 4 en maximaal 10 karakters bevatten.");
        if (vPassword.length() > 10)
            vFormFaults.add("Paswoord mag maximaal 10 karakters bevatten.");
        if (vPassword.length() < 6)
            vFormFaults.add("Paswoord moet minimaal 6 karakters bevatten.");
        if (!vPassword.equals(vPassword2))
            vFormFaults.add("Paswoorden zijn niet identiek.");
        return vFormFaults;
    }

    private Vector<String> tryRegister(HttpServletRequest req, WebSession session)
    {
        Vector<String> vFormFaults = new Vector<String>();

        RegisterData vRegData = new RegisterData();
        vRegData.setCode(req.getParameter(Constants.ACCOUNT_REGCODE));
        vRegData.setUserId(req.getParameter(Constants.ACCOUNT_USERID));
        vRegData.setPassword(req.getParameter(Constants.ACCOUNT_PASSWORD));
        try
        {
            AccountSqlAdapter vAccountSession = new AccountSqlAdapter();
            String vErrorStr = vAccountSession.register(session, vRegData);
            AccountEntityData vAccount = null;
            if (vErrorStr != null)
            {
                vFormFaults.add(vErrorStr);
            }
            else
            {
                vAccount = vAccountSession.logIn(session, vRegData.getUserId(), vRegData.getPassword());
                session.setFwdNumber(vAccount.getFwdNumber());
                AccountCache.getInstance().update(session.getConnection());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            vFormFaults.add("Fout: " + e.getMessage());
        }
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
                //Session vMailSession = (Session) PortableRemoteObject.narrow(vContext.lookup("java:comp/env/mail/Session"), Session.class);
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
                e.printStackTrace();
                vFormFaults.add("Uw aanvraag kon niet verstuurd worden. Probeer ons te contacteren via onze contact gegevens.");
            }
        }
        return vFormFaults;
    }

}
