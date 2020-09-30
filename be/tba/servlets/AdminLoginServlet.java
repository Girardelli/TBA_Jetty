package be.tba.servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.account.interfaces.LoginEntityData;
import be.tba.ejb.account.session.AccountSqlAdapter;
import be.tba.ejb.account.session.LoginSqlAdapter;
import be.tba.ejb.task.session.TaskSqlAdapter;
import be.tba.servlets.helper.PhoneMapManager;
import be.tba.servlets.session.SessionManager;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.AccessDeniedException;
import be.tba.util.exceptions.TbaException;
import be.tba.util.exceptions.remote.AccountNotFoundException;

public class AdminLoginServlet extends HttpServlet
{
	private static Logger log = LoggerFactory.getLogger(AdminLoginServlet.class);
    /**
    *
    */
    private static final long serialVersionUID = 10001L;

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        res.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");
        res.setContentType("text/html");
        String vUserId = "";
        String vPassword = "";
        WebSession vSession = null;

        try
        {
            HttpSession httpSession = req.getSession();
            vUserId = req.getParameter(Constants.LOGIN_USERID);
            vPassword = req.getParameter(Constants.LOGIN_PASSWORD);
            vSession = new WebSession();
            
            LoginEntityData login = null;
            if (vUserId.equals(Constants.MASTER_LOGIN_NAME))
            {
                login = new LoginEntityData();
                login.setRole(AccountRole.ADMIN.getShort());
            }
            else
            {
                LoginSqlAdapter vAccountSession = new LoginSqlAdapter();
                login = vAccountSession.logIn(vSession, vUserId, vPassword);
            }
            vSession.mLoginData = login;
            if (login.getRole().equals(AccountRole.ADMIN.getShort()) || login.getRole().equals(AccountRole.EMPLOYEE.getShort()))
            {
                SessionManager.getInstance().add(vSession, vUserId);
                vSession.setRole(AccountRole.fromShort(login.getRole()));
 //               vSession.setSessionFwdNr(login.getFwdNumber());
                vSession.setAccountId(login.getId());
   //             vSession.setLoginData(login);
                PhoneMapManager.getInstance().mapNewLogin(vUserId, vSession.getSessionId());
                
                // do your thing
                // res.setHeader("fullname", acctValue.getFullName());
                httpSession.setAttribute(Constants.SESSION_OBJ, vSession);

                // req.setAttribute(Constants.SESSION_ID, vKey);
                // req.setAttribute(Constants.SESSION_OBJ, vSession);
                ServletContext sc = getServletContext();
                RequestDispatcher rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
                rd.forward(req, res);
                //log.info("LoginServlet: " + login.getUserId() + " got session id " + vSession.getSessionId());
            }
            else
            {
                if (vSession != null)
                {
                    vSession.Close();
                }
                throw new AccessDeniedException("U hebt geen administrator rechten!");
            }
        }
        catch (AccountNotFoundException e)
        {
           log.error("AdminLoginServlet: Mallicious admin access attempt. userid:" + vUserId + ", password:" + vPassword);
           String vMsg = e.getMessage();
           req.setAttribute(Constants.ERROR_TXT, vMsg == null ? "Onbekende error." : vMsg);
           ServletContext sc = getServletContext();
           RequestDispatcher rd = sc.getRequestDispatcher(Constants.ADMIN_FAIL_JSP);
           rd.forward(req, res);
        }
        catch (TbaException e)
        {
           log.error("AdminLoginServlet: Mallicious admin access attempt. userid:" + vUserId + ", password:" + vPassword);
           //log.error(e.getMessage(), e);
           // print error page!!
           String vMsg = e.getMessage();
           req.setAttribute(Constants.ERROR_TXT, vMsg == null ? "Onbekende error." : vMsg);
           ServletContext sc = getServletContext();
           RequestDispatcher rd = sc.getRequestDispatcher(Constants.ADMIN_FAIL_JSP);
           rd.forward(req, res);
        }
        catch (Exception e)
        {
            //log.error(e.getMessage(), e);
            log.error("AdminLoginServlet: Mallicious admin access attempt. userid:" + vUserId + ", password:" + vPassword);
            log.error(e.getMessage(), e);
            // print error page!!
            String vMsg = e.getMessage();
            req.setAttribute(Constants.ERROR_TXT, vMsg == null ? "Onbekende error." : vMsg);
            ServletContext sc = getServletContext();
            RequestDispatcher rd = sc.getRequestDispatcher(Constants.ADMIN_FAIL_JSP);
            rd.forward(req, res);
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doGet(req, res);
    }

    public void destroy()
    {
        log.info("AdminLoginServlet destroyed.");
    }

}
