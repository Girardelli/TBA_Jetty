package be.tba.servlets;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.business.AccountBizzLogic;
import be.tba.business.CallBizzLogic;
import be.tba.business.TaskBizzLogic;
import be.tba.business.WorkorderBizzLogic;
import be.tba.session.SessionManager;
import be.tba.session.SessionParms;
import be.tba.session.SessionParmsInf;
import be.tba.session.WebSession;
import be.tba.sqladapters.CallRecordSqlAdapter;
import be.tba.sqldata.AccountCache;
import be.tba.sqldata.AccountEntityData;
import be.tba.util.common.FileUploader;
import be.tba.util.common.Tools;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.AccessDeniedException;
import be.tba.util.exceptions.LostSessionException;
import be.tba.util.exceptions.SystemErrorException;

public class CustomerDispatchServlet extends HttpServlet
{
   /**
   *
   */
   private static final long serialVersionUID = 10002L;
   private static Logger log = LoggerFactory.getLogger(CustomerDispatchServlet.class);

   public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      ServletContext sc = getServletContext();
      RequestDispatcher rd = null;
      try
      {
         req.setCharacterEncoding("UTF-8");
         res.setContentType("text/html");

         HttpSession httpSession = req.getSession(false);
         if (httpSession == null)
            throw new AccessDeniedException("U bent niet aangemeld.");

         WebSession vSession = (WebSession) httpSession.getAttribute(Constants.SESSION_OBJ);
         if (vSession == null || SessionManager.getInstance().isExpired(vSession) || vSession.getLogin() == null ) 
         {
            httpSession.invalidate();
            throw new AccessDeniedException("U bent niet aangemeld.");
         }

         synchronized (vSession)
         {
            AccountEntityData customer = AccountCache.getInstance().get(vSession.getLogin().getAccountId());
            if (customer == null)
            {
               SessionManager.getInstance().remove(vSession.getSessionId());
               throw new LostSessionException();
            }
   
            String vAction = null;
            String uploadedFile = null;
            FileUploader fileUploader = null;
            SessionParmsInf params = null;
   
            log.info("\nuserid:" + vSession.getUserId() + ", websessionid:" + vSession.getSessionId() + ", URI:" + req.getRequestURI() + "?" + req.getQueryString());
   
            if (ServletFileUpload.isMultipartContent(req))
            {
               fileUploader = new FileUploader(req);
               fileUploader.setStoragePath(Constants.WORKORDER_FILEUPLOAD_DIR + File.separator + Tools.spaces2underscores(customer.getFullName()) + File.separator + "todo");
               fileUploader.upload(req);
               log.info("multipart content detected");
               // getParameter cannot be used anymore. Also not further on.
   
               params = fileUploader;
            }
            else
            {
               params = new SessionParms(req);
   
            }
            vAction = params.getParameter(Constants.SRV_ACTION);
   
            // String vSessionId = params.getParameter(Constants.SESSION_ID);
            if (vAction == null)
            {
               throw new SystemErrorException("Interne fout: geen actie in de request.");
            }
            if (vAction.equals(Constants.ACTION_LOGOFF))
            {
               SessionManager.getInstance().remove(vSession.getSessionId());
               throw new LostSessionException();
            }
   
            vSession.setWsActive(false);
            if (!vSession.getRole().getShort().equals(AccountRole.ADMIN.getShort()) && !vSession.getRole().getShort().equals(AccountRole.CUSTOMER.getShort()) && !vSession.getRole().getShort().equals(AccountRole.SUBCUSTOMER.getShort()))
               throw new AccessDeniedException("access denied for " + vSession.getUserId() + " with role " + vSession.getRole().getShort());
            log.info("\nCustomerDispatchServlet: userid:" + vSession.getUserId() + ", websessionid:" + vSession.getSessionId() + " action=" + vAction);

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
               AccountBizzLogic.updateCustomerPrefs(vSession, params);
               break;
            }

            // ==============================================================================================
            // MODIFY RECORD
            // ==============================================================================================
            case Constants.ACTION_GOTO_RECORD_UPDATE:
            {
               String vKey = params.getParameter(Constants.RECORD_ID);

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
               CallBizzLogic.updateCustomerChanges(params, vSession, true);
               // rd = sc.getRequestDispatcher(Constants.CLIENT_CALLS_JSP);

               break;
            }

            // ==============================================================================================
            // ARCHIVE RECORDS
            // ==============================================================================================
            case Constants.ACTION_ARCHIVE_RECORDS:
            {
               CallBizzLogic.archiveRecords(params, vSession);
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
               if (params.getParameter(Constants.RECORD_SEARCH_STR) != null)
                  vSession.setSearchString(params.getParameter(Constants.RECORD_SEARCH_STR));
               rd = sc.getRequestDispatcher(Constants.CLIENT_SEARCH_JSP);
               break;
            }

            // ==============================================================================================
            // SEARCH_SHOW_NEXT
            // ==============================================================================================
            case Constants.SEARCH_SHOW_NEXT:
            {
               // log.info("AdminDispatchServlet: SEARCH_SHOW_NEXT");

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
            // GOTO WORKORDERS
            // ==============================================================================================
            case Constants.ACTION_GOTO_WORKORDERS:
            {
               log.info("account id voor goto workorder: " + vSession.getAccountId());
               rd = sc.getRequestDispatcher(Constants.CLIENT_WORKORDERS_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO UPDATE WORKORDER
            // ==============================================================================================
            case Constants.ACTION_UPDATE_WORKORDER:
            {
               String id = params.getParameter(Constants.WORKORDER_ID);
               if (id == null || id.isEmpty())
               {
                  id = "0";
               }
               vSession.setWorkOrderId(Integer.parseInt(id));
               rd = sc.getRequestDispatcher(Constants.CLIENT_UPDATE_WORKORDER_JSP);
               break;
            }

            // ==============================================================================================
            // SAVE WORKORDER
            // ==============================================================================================
            case Constants.ACTION_SAVE_WORKORDER:
            {
               WorkorderBizzLogic.saveWorkOrder(params, vSession);
               rd = sc.getRequestDispatcher(Constants.CLIENT_WORKORDERS_JSP);
               break;
            }

            // ==============================================================================================
            // UPLOAD WORKORDER INPUT FILE
            // ==============================================================================================
            case Constants.UPLOAD_WORKORDER_FILE:
            {
               uploadedFile = fileUploader.waitTillFinished();
               if (WorkorderBizzLogic.addWorkOrderFile(params, vSession, uploadedFile))
               {
                  vSession.setUploadedFileName(uploadedFile);
                  rd = sc.getRequestDispatcher(Constants.CLIENT_UPDATE_WORKORDER_JSP);
               }
               else
               {
                  // failed
                  throw new SystemErrorException("Het bestand kon niet worden opgeladen.");
               }
               break;
            }

            // ==============================================================================================
            // DELETE WORKORDER
            // ==============================================================================================
            case Constants.ACTION_DELETE_WORKORDER:
            {
               WorkorderBizzLogic.deleteWorkOrder(params, vSession);
               rd = sc.getRequestDispatcher(Constants.CLIENT_WORKORDERS_JSP);
               break;
            }

            // ==============================================================================================
            // DELETE WORKORDER FILE
            // ==============================================================================================
            case Constants.DELETE_WORKORDER_FILE:
            {
               WorkorderBizzLogic.deleteWorkOrderFile(params, vSession);
               rd = sc.getRequestDispatcher(Constants.CLIENT_UPDATE_WORKORDER_JSP);
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
               // log.info("rd is null: assign " + vSession.getCallingJsp());
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
         log.error(e.getMessage(), e);
         rd = sc.getRequestDispatcher(Constants.PROTECT_FAIL_JSP);
         req.setAttribute(Constants.ERROR_TXT, e.getMessage());
         rd.forward(req, res);
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
   }

   public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
//      Enumeration<String> dataNames = req.getParameterNames();
//      StringBuilder strBuf = new StringBuilder();
//      
//      while (dataNames.hasMoreElements()) 
//      {
//        String parm = (String) dataNames.nextElement();
//        strBuf.append(parm + ":" + req.getParameter(parm) + "; ");
//      }
//      strBuf.append("---------------------------------------\r\n");
//      log.info(strBuf.toString());
      doGet(req, res);
   }

   public void destroy()
   {
      log.info("CustomerDispatchServlet destroyed.");
   }

}
