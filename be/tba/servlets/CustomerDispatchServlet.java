package be.tba.servlets;

import java.io.IOException;
import java.util.Calendar;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.account.session.AccountSqlAdapter;
import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.servlets.helper.AccountFacade;
import be.tba.servlets.helper.CallRecordFacade;
import be.tba.servlets.session.SessionManager;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.AccessDeniedException;
import be.tba.util.exceptions.LostSessionException;
import be.tba.util.exceptions.SystemErrorException;
import be.tba.util.session.AccountCache;

public class CustomerDispatchServlet extends HttpServlet
{
   /**
   *
   */
   private static final long serialVersionUID = 10002L;

   public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      ServletContext sc = getServletContext();
      RequestDispatcher rd = null;
      try
      {
         req.setCharacterEncoding("UTF-8");
         res.setContentType("text/html");
         String vAction = (String) req.getParameter(Constants.SRV_ACTION);

         HttpSession httpSession = req.getSession();
         WebSession vSession = (WebSession) httpSession.getAttribute(Constants.SESSION_OBJ);

         if (vSession == null)
            throw new AccessDeniedException("U bent niet aangemeld.");
         SessionManager.getInstance().getSession(vSession.getSessionId(), "AdminDispatchServlet(" + vAction + ")");

         AccountEntityData customer = AccountCache.getInstance().get(vSession.getFwdNumber());
         if (customer == null)
         {
            SessionManager.getInstance().remove(vSession.getSessionId());
            throw new LostSessionException();
         }

         // String vSessionId = (String) req.getParameter(Constants.SESSION_ID);
         if (vAction == null)
         {
            throw new SystemErrorException("Interne fout.");
         }
         if (vAction.equals(Constants.ACTION_LOGOFF))
         {
            SessionManager.getInstance().remove(vSession.getSessionId());
            throw new LostSessionException();
         }

         // if (vSessionId == null)
         // throw new AccessDeniedException("U bent niet aangemeld.");
         // WebSession vSession =
         // SessionManager.getInstance().getSession(vSessionId,
         // "CustomerDispatchServlet(" + vAction + ")");

         synchronized (vSession)
         {
            if (!vSession.getRole().getShort().equals(AccountRole.ADMIN.getShort()) && !vSession.getRole().getShort().equals(AccountRole.CUSTOMER.getShort()) && !vSession.getRole().getShort().equals(AccountRole.SUBCUSTOMER.getShort()))
               throw new AccessDeniedException("access denied for " + vSession.getUserId() + " with role " + vSession.getRole().getShort());
            System.out.println("\nCustomerDispatchServlet: userid:" + vSession.getUserId() + ", websessionid:" + vSession.getSessionId() + " action=" + vAction);

            switch (vAction)
            {

            // ==============================================================================================
            // VIEW CALLS
            // ==============================================================================================
            case Constants.ACTION_SHOW_CALLS:
            {
               Calendar calendar = Calendar.getInstance();
               vSession.setYear(calendar.get(Calendar.YEAR));
               vSession.setMonthsBack(calendar.get(Calendar.MONTH));
               rd = sc.getRequestDispatcher(Constants.CLIENT_CALLS_JSP);
               break;
            }

            // ==============================================================================================
            // VIEW ARCHIVED CALLS
            // ==============================================================================================
            case Constants.ACTION_ARCHIVED_CALLS:
            {
               Calendar calendar = Calendar.getInstance();
               vSession.setYear(calendar.get(Calendar.YEAR));
               vSession.setMonthsBack(calendar.get(Calendar.MONTH));
               rd = sc.getRequestDispatcher(Constants.CLIENT_ARCHIVED_CALLS_JSP);
               break;
            }

            // ==============================================================================================
            // CALLS_REFRESH
            // ==============================================================================================
            case Constants.ACTION_REFRESH_CALLS:
            {
               vSession.setDaysBack(0);
               // rd = sc.getRequestDispatcher(Constants.CLIENT_CALLS_JSP);
               break;
            }

            // ==============================================================================================
            // RECORD_SHOW_NEXT
            // ==============================================================================================
            case Constants.RECORD_SHOW_NEXT:
            {
               vSession.setDaysBack(vSession.getDaysBack() - 7);
               if (vSession.getDaysBack() < 0)
                  vSession.setDaysBack(0);
               // rd = sc.getRequestDispatcher(Constants.CLIENT_CALLS_JSP);
               break;
            }

            // ==============================================================================================
            // RECORD_SHOW_PREV
            // ==============================================================================================
            case Constants.RECORD_SHOW_PREV:
            {
               vSession.setDaysBack(vSession.getDaysBack() + 7);
               // rd = sc.getRequestDispatcher(Constants.CLIENT_CALLS_JSP);
               break;
            }

            // ==============================================================================================
            // TASK_SHOW_NEXT
            // ==============================================================================================
            case Constants.TASK_SHOW_NEXT:
            {
               if (!vSession.isCurrentMonth())
                  vSession.incrementMonthsBack();
               // rd = sc.getRequestDispatcher(Constants.CLIENT_SHOW_TASKS_JSP);
               break;
            }

            // ==============================================================================================
            // TASK_SHOW_PREV
            // ==============================================================================================
            case Constants.TASK_SHOW_PREV:
            {
               vSession.decrementMonthsBack();
               // rd = sc.getRequestDispatcher(Constants.CLIENT_SHOW_TASKS_JSP);
               break;
            }

            // ==============================================================================================
            // UPDATE PREFERENCES
            // ==============================================================================================
            case Constants.GOTO_UPDATE_PREFS:
            {
               rd = sc.getRequestDispatcher(Constants.CLIENT_PREF_JSP);
               break;

            }

            // ==============================================================================================
            // SAVE PREFERENCES
            // ==============================================================================================
            case Constants.SAVE_PREFS:
            {
               AccountFacade.updateCustomerPrefs(vSession, req);
               break;
            }

            // ==============================================================================================
            // MODIFY RECORD
            // ==============================================================================================
            case Constants.ACTION_GOTO_RECORD_UPDATE:
            {
               String vKey = (String) req.getParameter(Constants.RECORD_ID);

               CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
               vSession.setCurrentRecord(vQuerySession.getRecord(vSession, vKey));
               rd = sc.getRequestDispatcher(Constants.CLIENT_SHOW_REC_JSP);
               break;
            }

            // ==============================================================================================
            // SAVE RECORD
            // ==============================================================================================
            case Constants.SAVE_RECORD:
            {
               CallRecordFacade.updateCustomerChanges(req, vSession, true);
               //rd = sc.getRequestDispatcher(Constants.CLIENT_CALLS_JSP);
               break;
            }

            // ==============================================================================================
            // ARCHIVE RECORDS
            // ==============================================================================================
            case Constants.ACTION_ARCHIVE_RECORDS:
            {
               CallRecordFacade.archiveRecords(req, vSession);
               rd = sc.getRequestDispatcher(Constants.CLIENT_CALLS_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO SEARCH PAGE
            // ==============================================================================================
            case Constants.ACTION_GOTO_SEARCH_PAGE:
            {
               vSession.setSearchString("");
               rd = sc.getRequestDispatcher(Constants.CLIENT_SEARCH_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO SHOW TASKS PAGE
            // ==============================================================================================
            case Constants.ACTION_GOTO_SHOW_TASKS:
            {
               vSession.setSearchString("");
               rd = sc.getRequestDispatcher(Constants.CLIENT_SHOW_TASKS_JSP);
               break;
            }

            // ==============================================================================================
            // SEARCH PAGE
            // ==============================================================================================
            case Constants.ACTION_SEARCH_CALLS:
            {
               if (req.getParameter(Constants.RECORD_SEARCH_STR) != null)
                  vSession.setSearchString((String) req.getParameter(Constants.RECORD_SEARCH_STR));
               rd = sc.getRequestDispatcher(Constants.CLIENT_SEARCH_JSP);
               break;
            }

            // ==============================================================================================
            // SEARCH_SHOW_NEXT
            // ==============================================================================================
            case Constants.SEARCH_SHOW_NEXT:
            {
               // System.out.println("AdminDispatchServlet: SEARCH_SHOW_NEXT");

               if (!vSession.isCurrentMonth())
                  vSession.incrementMonthsBack();
               rd = sc.getRequestDispatcher(Constants.CLIENT_SEARCH_JSP);
               break;
            }

            // ==============================================================================================
            // SEARCH_SHOW_PREV
            // ==============================================================================================
            case Constants.SEARCH_SHOW_PREV:
            {
               vSession.decrementMonthsBack();
               rd = sc.getRequestDispatcher(Constants.CLIENT_SEARCH_JSP);
               break;
            }

            // ==============================================================================================
            // error
            // ==============================================================================================
            default:
            {
               throw new SystemErrorException("Onbekende actie (" + vAction + ")");
            }
            }

            if (rd == null)
            {
               rd = sc.getRequestDispatcher(vSession.getCallingJsp());
            }
            rd.forward(req, res);
         }
      }
      catch (AccessDeniedException e)
      {
         rd = sc.getRequestDispatcher(Constants.PROTECT_FAIL_JSP);
         req.setAttribute(Constants.ERROR_TXT, e.getMessage());
         rd.forward(req, res);
      }
      catch (LostSessionException e)
      {
         rd = sc.getRequestDispatcher(Constants.SERVLET_LOGIN_HTML);
         rd.forward(req, res);
      }
      catch (SystemErrorException e)
      {
         e.printStackTrace();
         rd = sc.getRequestDispatcher(Constants.PROTECT_FAIL_JSP);
         req.setAttribute(Constants.ERROR_TXT, e.getMessage());
         rd.forward(req, res);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      doGet(req, res);
   }
}
