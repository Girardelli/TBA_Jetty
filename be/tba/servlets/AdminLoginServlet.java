package be.tba.servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.account.session.AccountSqlAdapter;
import be.tba.servlets.helper.PhoneMapManager;
import be.tba.servlets.session.SessionManager;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.AccessDeniedException;

public class AdminLoginServlet extends HttpServlet
{
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
            vUserId = req.getParameter(Constants.ACCOUNT_USERID);
            vPassword = req.getParameter(Constants.ACCOUNT_PASSWORD);

            AccountEntityData vAccount = null;
            if (vUserId.equals(Constants.MASTER_LOGIN_NAME))
            {
                vAccount = new AccountEntityData();
                vAccount.setRole(AccountRole.ADMIN.getShort());
            }
            else
            {
                vSession = new WebSession();
                AccountSqlAdapter vAccountSession = new AccountSqlAdapter();
                vAccount = vAccountSession.logIn(vSession, vUserId, vPassword);
                //System.out.println("LoginServlet: " + vAccount.getFullName() + " logged in.");
            }

            if (vAccount.getRole().equals(AccountRole.ADMIN.getShort()) || vAccount.getRole().equals(AccountRole.EMPLOYEE.getShort()))
            {
                if (vSession == null)
                    vSession = new WebSession();
                String vKey = SessionManager.getInstance().add(vSession);
                vSession.init(vUserId, vKey);
                vSession.setRole(AccountRole.fromShort(vAccount.getRole()));
                vSession.setFwdNumber(vAccount.getFwdNumber());

                PhoneMapManager.getInstance().mapNewLogin(vUserId, vSession.getSessionId());
                
                // do your thing
                // res.setHeader("fullname", acctValue.getFullName());
                httpSession.setAttribute(Constants.SESSION_OBJ, vSession);

                // req.setAttribute(Constants.SESSION_ID, vKey);
                // req.setAttribute(Constants.SESSION_OBJ, vSession);
                ServletContext sc = getServletContext();
                RequestDispatcher rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
                rd.forward(req, res);
                //System.out.println("LoginServlet: " + vAccount.getUserId() + " got session id " + vSession.getSessionId());
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
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("AdminLoginServlet: Mallicious admin access attempt. userid:" + vUserId + ", password:" + vPassword);
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

}
