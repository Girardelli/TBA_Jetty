package be.tba.servlets;

import java.io.IOException;
import java.util.Calendar;

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
import be.tba.session.PhoneMapManager;
import be.tba.session.WebSession;
import be.tba.sqldata.LoginEntityData;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.AccessDeniedException;

public class AdminLoginServlet extends HttpServlet
{
   private static Logger log = LoggerFactory.getLogger(AdminLoginServlet.class);
   /**
   *
   */
   private static final long serialVersionUID = 10001L;

   public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      req.setCharacterEncoding("UTF-8");
      res.setContentType("text/html");
      String vUserId = "";
      String vPassword = "";
      WebSession vSession = null;
      HttpSession httpSession = null;
      try
      {

         vUserId = req.getParameter(Constants.LOGIN_USERID);
         vPassword = req.getParameter(Constants.LOGIN_PASSWORD);
         vSession = new WebSession();
         vSession.resetSqlTimer();
//         String URI = req.getRequestURI() + "?" + req.getQueryString();

         LoginEntityData login = null;
         if (vUserId.equals(Constants.MASTER_LOGIN_NAME))
         {
            login = new LoginEntityData();
            login.setRole(AccountRole.ADMIN.getShort());
            vSession.setLogin(login);
         }
         else
         {
            login = LoginBizzLogic.logIn(vSession, vUserId, vPassword, false);
         }

         if (login.getRole().equals(AccountRole.ADMIN.getShort()) || login.getRole().equals(AccountRole.EMPLOYEE.getShort()))
         {
            vSession.setRole(AccountRole.fromShort(login.getRole()));
            // vSession.setSessionFwdNr(login.getFwdNumber());
            vSession.setAccountId(login.getId());
            // vSession.setLoginData(login);
            PhoneMapManager.getInstance().mapNewLogin(vUserId, vSession.getSessionId());

            // do your thing
            // res.setHeader("fullname", acctValue.getFullName());
            httpSession = req.getSession();
            httpSession.setMaxInactiveInterval(10*60*60); // set it to 10 hours to make sure it does not expire during working hours
            httpSession.setAttribute(Constants.SESSION_OBJ, vSession);
            
                        // req.setAttribute(Constants.SESSION_ID, vKey);
            // req.setAttribute(Constants.SESSION_OBJ, vSession);
            ServletContext sc = getServletContext();
            RequestDispatcher rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
            rd.forward(req, res);
            // log.info("LoginServlet: " + login.getUserId() + " got session id " +
            // vSession.getSessionId());
         }
         else
         {
            if (vSession != null)
            {
               vSession.close();
            }
            throw new AccessDeniedException("U hebt geen administrator rechten!");
         }
      }
      catch (Exception e)
      {
         if (httpSession != null)
            httpSession.invalidate();
         if (vSession != null)
         {
            vSession.close();
         }
         // log.error(e.getMessage(), e);
         log.error("AdminLoginServlet: Mallicious admin access attempt. userid:" + vUserId + ", password:" + vPassword);
         log.error(e.getMessage(), e);
         // print error page!!
         String vMsg = e.getMessage();
         req.setAttribute(Constants.ERROR_TXT, vMsg == null ? "Onbekende error." : vMsg);
         ServletContext sc = getServletContext();
         RequestDispatcher rd = sc.getRequestDispatcher(Constants.ADMIN_FAIL_JSP);
         rd.forward(req, res);
      }
      if (vSession != null)
      {
         log.info("########### httprequest admin login done: java=" + (Calendar.getInstance().getTimeInMillis() - vSession.mWebTimer - vSession.getSqlTimer()) + ", SQL=" + vSession.getSqlTimer());
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
