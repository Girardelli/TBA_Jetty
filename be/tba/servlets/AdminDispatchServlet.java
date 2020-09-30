package be.tba.servlets;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.account.interfaces.LoginEntityData;
import be.tba.ejb.account.session.AccountSqlAdapter;
import be.tba.ejb.account.session.LoginSqlAdapter;
import be.tba.ejb.mail.session.MailerSessionBean;
import be.tba.ejb.pbx.interfaces.CallRecordEntityData;
import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.servlets.helper.AccountFacade;
import be.tba.servlets.helper.CallRecordFacade;
import be.tba.servlets.helper.IntertelCallManager;
import be.tba.servlets.helper.InvoiceFacade;
import be.tba.servlets.helper.LoginFacade;
import be.tba.servlets.helper.TaskFacade;
import be.tba.servlets.session.SessionManager;
import be.tba.servlets.session.WebSession;
import be.tba.util.common.Tools;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.AccessDeniedException;
import be.tba.util.exceptions.LostSessionException;
import be.tba.util.exceptions.SystemErrorException;
import be.tba.util.file.FileUploader;
import be.tba.util.session.AccountCache;
import be.tba.util.session.SessionParms;
import be.tba.util.session.SessionParmsInf;

@WebServlet("/upload")
@MultipartConfig
public class AdminDispatchServlet extends HttpServlet
{
   /**
    *
    */
   private static final long serialVersionUID = 1L;
   private static Logger log = LoggerFactory.getLogger(AdminDispatchServlet.class);

   public AdminDispatchServlet()
   {
      log.info("AdminDispatchServlet created");
   }

   public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      RequestDispatcher rd = null;
      ServletContext sc = null;
      WebSession vSession = null;
      try
      {
         // log.info("doGet()");
         sc = getServletContext();
         res.setContentType("text/html");
         req.setCharacterEncoding("UTF-8");

         // parsing the multipart content must be done before any getXXX call on the
         // request
         // because such getXXX calls will implicitly call the parser which can only be
         // called once.
         String vAction = null;
         String uploadedFile = null;
         FileUploader fileUploader = null;
         SessionParmsInf params = null;
         String URI = null;
         if (ServletFileUpload.isMultipartContent(req))
         {
            fileUploader = new FileUploader(req);
            fileUploader.upload(req);
            params = fileUploader;
         }
         else
         {
            URI = req.getRequestURI() + "?" + req.getQueryString();
            params = new SessionParms(req);
         }
         vAction = params.getParameter(Constants.SRV_ACTION);

         HttpSession httpSession = req.getSession();
         vSession = (WebSession) httpSession.getAttribute(Constants.SESSION_OBJ);

         if (vSession == null)
            throw new AccessDeniedException("U bent niet aangemeld.");
         vSession.resetSqlTimer();
         SessionManager.getInstance().getSession(vSession.getSessionId(), "AdminDispatchServlet(" + vAction + ")");

         log.info("\nuserid:" + vSession.getUserId() + ", websessionid:" + vSession.getSessionId() + "Action: " + vAction + ", URI:" + URI);
         
         synchronized (vSession)
         {
            vSession.setWsActive(false);
            if (vSession.getRole() != AccountRole.ADMIN && vSession.getRole() != AccountRole.EMPLOYEE)
               throw new AccessDeniedException("access denied for " + vSession.getUserId());

            rd = sc.getRequestDispatcher(vSession.getCallingJsp());
            if (vAction == null)
            {
               throw new SystemErrorException("Interne fout. geen actie.");
            }

            // if (!vAction.equals(Constants.GOTO_INVOICE)
            // && !vAction.equals(Constants.GENERATE_INVOICE))
            // vSession.setMonthsBack()(CallFilter.kNoMonth);
            // if (!vAction.equals(Constants.GOTO_RECORD_SEARCH))
            // vSession.setSearchString("");
            if (!vAction.equals(Constants.RECORD_SHOW_NEXT) && !vAction.equals(Constants.RECORD_SHOW_PREV) && !vAction.equals(Constants.RECORD_SHOW_NEXT_10) && !vAction.equals(Constants.RECORD_SHOW_PREV_10))
            {
               vSession.setDaysBack(0);
            }
            // ==============================================================================================
            // FIX_ACCOUNT_IDS
            // ==============================================================================================
            switch (vAction)
            {
            case Constants.FIX_ACCOUNT_IDS:
            {
               break;
            }

            // ==============================================================================================
            // MAIL_IT test
            // ==============================================================================================
            case Constants.MAIL_IT:
            {
               if (vSession.getCallFilter().getCustFilter() > 0)
               {
                  try
                  {
                     AccountEntityData vAccountData = AccountCache.getInstance().get(vSession.getCallFilter().getCustFilter());
                     if (vAccountData != null)
                     {
                        String vEmail = vAccountData.getEmail();
                        if (vEmail != null && !vEmail.isEmpty() && AccountCache.getInstance().isMailEnabled(vAccountData))
                        {
                           MailerSessionBean.sendCallInfoMail(vSession, vAccountData.getId());
                        }
                     } // Check the record and add it if it is a valid one. //
                       // vMailSession.sendMail(5);
                  }
                  catch (Exception e)
                  {
                     log.info("MailTimerTask exception");
                     log.error(e.getMessage(), e);
                  }
                  finally
                  {
                  }
               }
               break;
            }
            // ==============================================================================================
            // SHOW_MAIL_ERROR button pushed
            // ==============================================================================================
            case Constants.SHOW_MAIL_ERROR:
            {
               rd = sc.getRequestDispatcher(Constants.SHOW_ERROR_JSP);
               break;
            }

            // ==============================================================================================
            // REMOVE_PENDING_CALL button pushed
            // ==============================================================================================
            case Constants.REMOVE_PENDING_CALL:
            {
               log.info("execute REMOVE_PENDING_CALL");
               IntertelCallManager.getInstance().removeCall(vSession, Integer.parseInt(params.getParameter(Constants.PENDING_CALL_ID)));
               vSession.setIsAutoUpdateRecord(false);
               break;
            }

            // ==============================================================================================
            // RECORD_SHOW_NEXT_10
            // ==============================================================================================
            case Constants.RECORD_SHOW_NEXT_10:
            {
               vSession.setDaysBack(vSession.getDaysBack() - 10);
               // rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // RECORD_SHOW_NEXT
            // ==============================================================================================
            case Constants.RECORD_SHOW_NEXT:
            {
               if (vSession.getCallFilter().getCustFilter() > 0)
               {
                  if (!vSession.isCurrentMonth())
                     vSession.incrementMonthsBack();
               }
               else
               {
                  vSession.setDaysBack(vSession.getDaysBack() - 1);
               }
               // rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // RECORD_SHOW_PREV
            // ==============================================================================================
            case Constants.RECORD_SHOW_PREV:
            {
               if (vSession.getCallFilter().getCustFilter() > 0)
               {
                  vSession.decrementMonthsBack();
               }
               else
               {
                  vSession.setDaysBack(vSession.getDaysBack() + 1);
               }
               // rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // RECORD_SHOW_PREV_10
            // ==============================================================================================
            case Constants.RECORD_SHOW_PREV_10:
            {
               vSession.setDaysBack(vSession.getDaysBack() + 10);
               // rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // UPDATE_SHORT_TEXT
            // ==============================================================================================
            case Constants.UPDATE_SHORT_TEXT:
            {
               CallRecordFacade.updateCustomerChanges(params, vSession, false);
               // rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // AUTO_RECORD_UPDATE
            // ==============================================================================================
            case Constants.AUTO_RECORD_UPDATE:
            {
               CallRecordFacade.retrieveRecordForUpdate(params, vSession);
               vSession.setIsAutoUpdateRecord(true);
               rd = sc.getRequestDispatcher(Constants.UPDATE_RECORD_JSP);
               break;
            }

            // ==============================================================================================
            // ACTION_GOTO_RECORD_UPDATE
            // ==============================================================================================
            case Constants.ACTION_GOTO_RECORD_UPDATE:
            {
               CallRecordFacade.retrieveRecordForUpdate(params, vSession);
               rd = sc.getRequestDispatcher(Constants.UPDATE_RECORD_JSP);
               break;
            }

            // ==============================================================================================
            // SAVE RECORD
            // ==============================================================================================
            case Constants.SAVE_RECORD:
            {
               CallRecordFacade.saveRecord(params, vSession);
               vSession.setIsAutoUpdateRecord(false);
               rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // ADD RECORD
            // ==============================================================================================
            case Constants.GOTO_ADD_RECORD:
            {
               rd = sc.getRequestDispatcher(Constants.ADD_RECORD_JSP);
               log.info("AdminDispatchServlet ready to ADD_RECORD_JSP: " + Constants.ADD_RECORD_JSP);
               break;
            }

            // ==============================================================================================
            // SAVE MANUALY CREATED RECORD
            // ==============================================================================================
            case Constants.SAVE_MAN_RECORD:
            {
               CallRecordFacade.saveManualRecord(params, vSession);
               rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // DELETE RECORD
            // ==============================================================================================
            case Constants.RECORD_DELETE:
            {
               CallRecordFacade.deleteRecords(params, vSession);
               rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO_CANVAS
            // ==============================================================================================
            case Constants.GOTO_CANVAS:
            {
               vSession.getCallFilter().setCustFilter(params.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
               rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // REMOVE_OPEN_CALL
            // ==============================================================================================
            case Constants.REMOVE_OPEN_CALL:
            {
               CallRecordFacade.removeNewCall(params, vSession);
               rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO DELETE ACCOUNT
            // ==============================================================================================
            case Constants.GOTO_ACCOUNT_DELETE:
            {
               String vLtd = params.getParameter(Constants.ACCOUNT_TO_DELETE);
               StringTokenizer vStrTok = new StringTokenizer(vLtd, ",");

               log.info("goto account delete vLtd=" + vLtd);
               if (vStrTok.countTokens() > 1)
               {
                  req.setAttribute(Constants.ERROR_TXT, "Je kan maar 1 klant of werknemer per keer verwijderen!");
                  req.setAttribute(Constants.NEXT_PAGE, Constants.GOTO_ACCOUNT_ADMIN);
                  rd = sc.getRequestDispatcher(Constants.ANNOUNCEMENT_JSP);
               }
               else
               {
                  // String vFwdNr = AccountCache.getInstance().idToFwdNr(Integer.parseInt(vLtd));

                  AccountEntityData accountData = AccountCache.getInstance().get(Integer.parseInt(vLtd));
                  if (accountData == null)
                  {
                     throw new SystemErrorException("Account not found for ID " + vLtd);
                  }
                  AccountRole role = AccountRole.fromShort(vSession.mLoginData.getRole());
                  if (role == AccountRole.ADMIN || role == AccountRole.EMPLOYEE)
                  {
                     log.info("goto account delete: setAccountId=" + vLtd + ", account fwdnr=" + accountData.getFwdNumber());
                     AccountFacade.archiveAccount(vSession, Integer.parseInt(vLtd));
                     rd = sc.getRequestDispatcher(Constants.ADMIN_EMPLOYEE_JSP);
                  }
                  else
                  {
                     req.setAttribute(Constants.ERROR_TXT, "Als je doorgaat wordt dit account gearchiveerd. Je kan dit account terug actief maken via de pagina 'gearchiveerde klanten'. Wilt u hiermee verder gaan?");
                     req.setAttribute(Constants.NEXT_PAGE, Constants.ACCOUNT_DELETE);
                     req.setAttribute(Constants.PREVIOUS_PAGE, Constants.GOTO_ACCOUNT_ADMIN);
                     vSession.setAccountIdToDelete(Integer.parseInt(vLtd));
                     rd = sc.getRequestDispatcher(Constants.ARE_YOU_SURE_JSP);
                  }
               }
               break;
            }

            // ==============================================================================================
            // GOTO EMPLOYEE ACCOUNT
            // ==============================================================================================
            case Constants.GOTO_EMPLOYEE_ADMIN:
            {
               rd = sc.getRequestDispatcher(Constants.ADMIN_EMPLOYEE_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO EMPLOYEE COST
            // ==============================================================================================
            case Constants.GOTO_EMPLOYEE_COST:
            {
               rd = sc.getRequestDispatcher(Constants.ADMIN_EMPLOYEE_COST_JSP);
               break;
            }

            // ==============================================================================================
            // DELETE ACCOUNT
            // ==============================================================================================
            case Constants.ACCOUNT_DELETE:
            {
               AccountEntityData accountData = AccountCache.getInstance().get(vSession.getAccountIdToDelete());
               log.info("account delete: key=" + vSession.getAccountId() + ", fwd nr=" + accountData.getFwdNumber());
               AccountFacade.archiveAccount(vSession, vSession.getAccountIdToDelete());
               rd = sc.getRequestDispatcher(Constants.ADMIN_ACCOUNT_JSP);
               break;
            }

            // ==============================================================================================
            // ADMIN ACCOUNT
            // ==============================================================================================
            case Constants.GOTO_ACCOUNT_ADMIN:
            {
               rd = sc.getRequestDispatcher(Constants.ADMIN_ACCOUNT_JSP);
               break;
            }
            
            // ==============================================================================================
            // GOTO_ARCHIVED_ACCOUNTS
            // ==============================================================================================
            case Constants.GOTO_ARCHIVED_ACCOUNTS:
            {
               rd = sc.getRequestDispatcher(Constants.ARCHIVED_ACCOUNT_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO_SAVE_ACCOUNT
            // ==============================================================================================
            case Constants.GOTO_SAVE_ACCOUNT:
            {
               AccountEntityData newData = AccountFacade.updateAccountData(vSession, params);
               rd = sc.getRequestDispatcher(Constants.ADMIN_ACCOUNT_JSP);
               break;
            }

            // ==============================================================================================
            // SAVE ACCOUNT
            // ==============================================================================================
//            case Constants.SAVE_ACCOUNT:
//            {
//               AccountEntityData newData = vSession.getNewAccount();
//               AccountFacade.changeFwdNumber(vSession, vSession.mLoginData.getAccountId(), newData.getFwdNumber());
//               AccountFacade.saveAccount(vSession, newData);
//               rd = sc.getRequestDispatcher(Constants.ADMIN_ACCOUNT_JSP);
//               break;
//            }

            // ==============================================================================================
            // DELETE LOGIN
            // ==============================================================================================
            case Constants.DELETE_LOGIN:
            {
               vSession.mLoginToDelete = Integer.parseInt(params.getParameter(Constants.LOGIN_ID));
               req.setAttribute(Constants.ERROR_TXT, "Bent u zeker dat u het login van deze klantwerknemer wil verwijderen?");
               req.setAttribute(Constants.NEXT_PAGE, Constants.DELETE_LOGIN_CONFIRMED);
               req.setAttribute(Constants.PREVIOUS_PAGE, Constants.ACCOUNT_UPDATE);
               req.setAttribute(Constants.ACCOUNT_ID, Integer.parseInt(params.getParameter(Constants.ACCOUNT_ID)));
               rd = sc.getRequestDispatcher(Constants.ARE_YOU_SURE_JSP);
               break;
            }
               
            // ==============================================================================================
            // DELETE EMPLOYEE
            // ==============================================================================================
            case Constants.GOTO_EMPLOYEE_DELETE:
            {
               String vLtd = params.getParameter(Constants.ACCOUNT_TO_DELETE);
               StringTokenizer vStrTok = new StringTokenizer(vLtd, ",");
               log.info("GOTO_EMPLOYEE_DELETE: " + vLtd);
               if (vStrTok.countTokens() > 1)
               {
                  req.setAttribute(Constants.ERROR_TXT, "Je kan maar 1 werknemer per keer verwijderen!");
                  req.setAttribute(Constants.NEXT_PAGE, Constants.GOTO_EMPLOYEE_ADMIN);
                  rd = sc.getRequestDispatcher(Constants.ANNOUNCEMENT_JSP);
               }
               else
               {
                  // String vFwdNr = AccountCache.getInstance().idToFwdNr(Integer.parseInt(vLtd));
                  vSession.mLoginToDelete = Integer.parseInt(vLtd);
                  req.setAttribute(Constants.ERROR_TXT, "Bent u zeker dat u het login van deze werknemer wil verwijderen?");
                  req.setAttribute(Constants.NEXT_PAGE, Constants.DELETE_EMPLOYEE_CONFIRMED);
                  req.setAttribute(Constants.PREVIOUS_PAGE, Constants.GOTO_EMPLOYEE_ADMIN);
                  rd = sc.getRequestDispatcher(Constants.ARE_YOU_SURE_JSP);
               }
               break;
            }
           
            // ==============================================================================================
            // DELETE_LOGIN_CONFIRMED
            // ==============================================================================================
            case Constants.DELETE_LOGIN_CONFIRMED:
            {
               LoginFacade.deleteLogin(vSession);
               vSession.mLoginToDelete = 0;
               rd = sc.getRequestDispatcher(Constants.UPDATE_ACCOUNT_JSP);
               break;
            }
            
            // ==============================================================================================
            // DELETE_LOGIN_CONFIRMED
            // ==============================================================================================
            case Constants.DELETE_EMPLOYEE_CONFIRMED:
            {
               LoginFacade.deleteLogin(vSession);
               vSession.mLoginToDelete = 0;
               rd = sc.getRequestDispatcher(Constants.ADMIN_EMPLOYEE_JSP);
               break;
            }
            
            // ==============================================================================================
            // ADD ACCOUNT
            // ==============================================================================================
            case Constants.GOTO_ACCOUNT_ADD:
            {
               rd = sc.getRequestDispatcher(Constants.ADD_ACCOUNT_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO_EMPLOYEE_ADD
            // ==============================================================================================
            case Constants.GOTO_EMPLOYEE_ADD:
            {
               rd = sc.getRequestDispatcher(Constants.ADD_EMPLOYEE_JSP);
               break;
            }

            // ==============================================================================================
            // ADD ACCOUNT
            // ==============================================================================================
            case Constants.EMPLOYEE_ADD:
            {
               Vector<String> errorList = LoginFacade.addLogin(vSession, req, params);
               if (errorList != null)
               {
                  vSession.setErrorList(errorList);
                  rd = sc.getRequestDispatcher(Constants.ADD_EMPLOYEE_JSP);
               }
               else
               {
                  rd = sc.getRequestDispatcher(Constants.ADMIN_EMPLOYEE_JSP);
               }
               break;
            }

            // ==============================================================================================
            // UPDATE ACCOUNT
            // ==============================================================================================
            case Constants.ACCOUNT_UPDATE:
            {
               vSession.setAccountId(Integer.parseInt(params.getParameter(Constants.ACCOUNT_ID)));
               rd = sc.getRequestDispatcher(Constants.UPDATE_ACCOUNT_JSP);
               break;
            }

            // ==============================================================================================
            // ADD ACCOUNT
            // ==============================================================================================
            case Constants.ACCOUNT_ADD:
            {
               Vector<String> errorList = AccountFacade.addAccount(vSession, req, params);
               AccountRole role = AccountRole.fromShort(params.getParameter(Constants.LOGIN_ROLE));
               if (role == AccountRole.ADMIN || role == AccountRole.EMPLOYEE)
               {
                  if (errorList != null)
                  {
                     rd = sc.getRequestDispatcher(Constants.ADD_EMPLOYEE_JSP);
                  }
                  else
                  {
                     rd = sc.getRequestDispatcher(Constants.ADMIN_EMPLOYEE_JSP);
                  }
               }
               else
               {
                  rd = sc.getRequestDispatcher(Constants.ADMIN_ACCOUNT_JSP);
               }
               break;
            }

            // ==============================================================================================
            // MAIL_CUSTOMER
            // ==============================================================================================
            case Constants.MAIL_CUSTOMER:
            {
               AccountFacade.mailCustomer(params, vSession);
               rd = sc.getRequestDispatcher(Constants.UPDATE_ACCOUNT_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO_ADD_INVOICE
            // ==============================================================================================
            case Constants.GOTO_ADD_INVOICE:
            {
               vSession.getCallFilter().setCustFilter(params.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
               rd = sc.getRequestDispatcher(Constants.ADD_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // INVOICES ADD
            // ==============================================================================================
            case Constants.INVOICE_ADD:
            {
               InvoiceFacade.addManualInvoice(params, vSession);
               rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO_INVOICE
            // ==============================================================================================
            case Constants.GOTO_INVOICE:
            {
               InvoiceFacade.prepareForSingleInvoice(params, vSession);
               rd = sc.getRequestDispatcher(Constants.INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // ADMIN INVOICES
            // ==============================================================================================
            case Constants.GOTO_INVOICE_ADMIN:
            {
               if (params.getParameter(Constants.INVOICE_MONTH) != null)
                  vSession.setMonthsBack(Integer.parseInt(params.getParameter(Constants.INVOICE_MONTH)));
               if (params.getParameter(Constants.INVOICE_YEAR) != null)
                  vSession.setYear(Integer.parseInt(params.getParameter(Constants.INVOICE_YEAR)));
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // GENERATE INVOICE
            // ==============================================================================================
            case Constants.GENERATE_INVOICE:
            {
               vSession.getCallFilter().setCustFilter(params.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
               if (params.getParameter(Constants.INVOICE_MONTH) != null)
                  vSession.setMonthsBack(Integer.parseInt(params.getParameter(Constants.INVOICE_MONTH)));

               if (vSession.getInvoiceHelper().generatePdfInvoice())
               {
                  rd = sc.getRequestDispatcher(Constants.INVOICE_JSP);
               }
               else
               {
                  req.setAttribute(Constants.ERROR_TXT, "Het factuur kan niet aangemaakt worden. Is het factuur misschien al geopend door een ander programma?");
                  req.setAttribute(Constants.NEXT_PAGE, Constants.GOTO_INVOICE);
                  rd = sc.getRequestDispatcher(Constants.ANNOUNCEMENT_JSP);
               }
               break;
            }

            // ==============================================================================================
            // GOTO OPEN INVOICE
            // ==============================================================================================
            case Constants.GOTO_OPEN_INVOICE:
            {
               rd = sc.getRequestDispatcher(Constants.OPEN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // PROCESS_FINTRO_XLSX
            // ==============================================================================================
            case Constants.PROCESS_FINTRO_XLSX:
            {
               // default storage path is used
               uploadedFile = fileUploader.waitTillFinished();
               vSession.setUploadedFileName(uploadedFile);
               log.info("PROCESS_FINTRO_XLSX: file ready for parsing: " + vSession.getUploadedFileName());
               rd = sc.getRequestDispatcher(Constants.OPEN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // GENERATE_INVOICE_XML
            // ==============================================================================================
            case Constants.GENERATE_INVOICE_XML:
            {
               InvoiceFacade.generateInvoiceXml(params, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // GENERATE All INVOICES
            // ==============================================================================================
            case Constants.GENERATE_ALL_INVOICES:
            {
               InvoiceFacade.generateInvoices(params, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // FREEZE INVOICES
            // ==============================================================================================
            case Constants.INVOICE_FREEZE:
            {
               InvoiceFacade.freezeInvoices(params, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // MAIL INVOICES
            // ==============================================================================================
            case Constants.INVOICE_MAIL:
            {
               InvoiceFacade.mailInvoices(params, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // SET INVOICES PAYED
            // ==============================================================================================
            case Constants.INVOICE_SETPAYED:
            {
               InvoiceFacade.setInvoicesPayed(params, vSession);
               rd = sc.getRequestDispatcher(Constants.OPEN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // DELETE INVOICES
            // ==============================================================================================
            case Constants.INVOICE_DELETE:
            {
               InvoiceFacade.deleteInvoices(params, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // SAVE INVOICES
            // ==============================================================================================
            case Constants.SAVE_INVOICE:
            {
               InvoiceFacade.saveInvoice(params, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // SAV INVOICE PAYDATE
            // ==============================================================================================
            case Constants.SAVE_PAYDATE:
            {
               InvoiceFacade.savePayDate(params, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // GENERATE_CREDITNOTE
            // ==============================================================================================
            case Constants.GENERATE_CREDITNOTE:
            {
               InvoiceFacade.generateCreditInvoice(params, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO_RECORD_SEARCH
            // ==============================================================================================
            case Constants.GOTO_RECORD_SEARCH:
            {
               vSession.getCallFilter().setCustFilter(params.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
               if (params.getParameter(Constants.RECORD_SEARCH_STR) != null)
                  vSession.setSearchString(params.getParameter(Constants.RECORD_SEARCH_STR));
               Calendar calendar = Calendar.getInstance();
               vSession.setYear(calendar.get(Calendar.YEAR));
               vSession.setMonthsBack(calendar.get(Calendar.MONTH));
               rd = sc.getRequestDispatcher(Constants.ADMIN_SEARCH_JSP);
               break;
            }

            // ==============================================================================================
            // SEARCH_SHOW_NEXT
            // ==============================================================================================
            case Constants.SEARCH_SHOW_NEXT:
            {
               log.info("AdminDispatchServlet: SEARCH_SHOW_NEXT");

               if (!vSession.isCurrentMonth())
                  vSession.incrementMonthsBack();
               rd = sc.getRequestDispatcher(Constants.ADMIN_SEARCH_JSP);
               break;
            }

            // ==============================================================================================
            // SEARCH_SHOW_PREV
            // ==============================================================================================
            case Constants.SEARCH_SHOW_PREV:
            {
               vSession.decrementMonthsBack();
               rd = sc.getRequestDispatcher(Constants.ADMIN_SEARCH_JSP);
               break;
            }

            // ==============================================================================================
            // DELETE TASK
            // ==============================================================================================
            case Constants.TASK_DELETE:
            {
               TaskFacade.deleteTask(params, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_TASK_JSP);
               break;
            }

            // ==============================================================================================
            // ADMIN TASK
            // ==============================================================================================
            case Constants.GOTO_TASK_ADMIN:
            {
               vSession.getCallFilter().setCustFilter(params.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
               rd = sc.getRequestDispatcher(Constants.ADMIN_TASK_JSP);
               break;
            }

            // ==============================================================================================
            // MODIFY TASK
            // ==============================================================================================
            case Constants.TASK_UPDATE:
            {
               TaskFacade.modifyTask(params, vSession);
               rd = sc.getRequestDispatcher(Constants.UPDATE_TASK_JSP);
               break;
            }

            // ==============================================================================================
            // SAVE TASK
            // ==============================================================================================
            case Constants.SAVE_TASK:
            {
               TaskFacade.saveTask(params, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_TASK_JSP);
               break;
            }

            // ==============================================================================================
            // ADD TASK
            // ==============================================================================================
            case Constants.GOTO_TASK_ADD:
            {
               rd = sc.getRequestDispatcher(Constants.ADD_TASK_JSP);
               break;
            }

            // ==============================================================================================
            // ADD TASK
            // ==============================================================================================
            case Constants.TASK_ADD:
            {
               TaskFacade.addTask(params, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_TASK_JSP);
               break;
            }

            // ==============================================================================================
            // TASK_SHOW_NEXT
            // ==============================================================================================
            case Constants.TASK_SHOW_NEXT:
            {
               if (!vSession.isCurrentMonth())
                  vSession.incrementMonthsBack();
               rd = sc.getRequestDispatcher(Constants.ADMIN_TASK_JSP);
               break;
            }

            // ==============================================================================================
            // TASK_SHOW_PREV
            // ==============================================================================================
            case Constants.TASK_SHOW_PREV:
            {
               vSession.decrementMonthsBack();
               rd = sc.getRequestDispatcher(Constants.ADMIN_TASK_JSP);
               break;
            }

            // ==============================================================================================
            // ADMIN WORKORDERS
            // ==============================================================================================
            case Constants.GOTO_ADMIN_WORKORDERS:
            {
               rd = sc.getRequestDispatcher(Constants.ADMIN_WORK_ORDER_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO WORKORDER
            // ==============================================================================================
            case Constants.GOTO_WORKORDER:
            {
               String id = params.getParameter(Constants.WORKORDER_ID);
               vSession.setWorkOrderId(Integer.parseInt(id));
               rd = sc.getRequestDispatcher(Constants.WORKORDER_JSP);
               break;
            }

            // ==============================================================================================
            // UPLOAD WORKORDER FILE
            // ==============================================================================================
            case Constants.ACTION_SAVE_WORKORDER:
            {
               TaskFacade.saveWorkOrder(params, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_WORK_ORDER_JSP);
               break;
            }
            
            // ==============================================================================================
            // UPLOAD WORKORDER INPUT FILE
            // ==============================================================================================
            case Constants.UPLOAD_WORKORDER_FILE:
            {
               String accountId = params.getParameter(Constants.ACCOUNT_ID);
               AccountEntityData custAccount = AccountCache.getInstance().get(Integer.parseInt(accountId));
               if (custAccount == null)
               {
                  throw new Exception("Klant niet bekend voor deze file upload.");
               }
               fileUploader.setStoragePath(Constants.WORKORDER_FILEUPLOAD_DIR + File.separator + Tools.spaces2underscores(custAccount.getFullName()) + File.separator + "done");
               uploadedFile = fileUploader.waitTillFinished();
               if (TaskFacade.addWorkOrderFile(params, vSession, uploadedFile))
               {
                  vSession.setUploadedFileName(uploadedFile);
                  rd = sc.getRequestDispatcher(Constants.WORKORDER_JSP);
               }
               else
               {
                  // failed
                  throw new SystemErrorException("Het bestand kon niet worden opgeladen.");
               }
               break;
            }
            
            // ==============================================================================================
            // DELETE WORKORDER FILE
            // ==============================================================================================
            case Constants.DELETE_WORKORDER_FILE:
            {
               TaskFacade.deleteWorkOrderFile(params, vSession);
               rd = sc.getRequestDispatcher(Constants.WORKORDER_JSP);
               break;
            }

            
            
            // ==============================================================================================
            // EMPLCOST_SHOW_NEXT
            // ==============================================================================================
            case Constants.EMPLCOST_SHOW_NEXT:
            {
               if (!vSession.isCurrentMonth())
                  vSession.incrementMonthsBack();
               rd = sc.getRequestDispatcher(Constants.ADMIN_EMPLOYEE_COST_JSP);
               break;
            }

            // ==============================================================================================
            // EMPLCOST_SHOW_PREV
            // ==============================================================================================
            case Constants.EMPLCOST_SHOW_PREV:
            {
               vSession.decrementMonthsBack();
               rd = sc.getRequestDispatcher(Constants.ADMIN_EMPLOYEE_COST_JSP);
               break;
            }

            // ==============================================================================================
            // ADMIN HOME
            // ==============================================================================================
            case Constants.ADMIN_HOME:
            {
               rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // LOG OUT
            // ==============================================================================================
            case Constants.ADMIN_LOG_OFF:
            {
               SessionManager.getInstance().remove(vSession.getSessionId());
               rd = sc.getRequestDispatcher(Constants.ADMIN_LOGIN);
               rd.forward(req, res);
               return;
            }

            // ==============================================================================================
            // error
            // ==============================================================================================
            default:
            {
               throw new SystemErrorException("Onbekende actie (" + vAction + ")");
            }
            }
            if (rd != null)
            {
               rd.forward(req, res);
            }
         }
      }
      catch (AccessDeniedException e)
      {
         rd = sc.getRequestDispatcher(Constants.ADMIN_FAIL_JSP);
         req.setAttribute(Constants.ERROR_TXT, e.getMessage());
         rd.forward(req, res);
      }
      catch (LostSessionException e)
      {
         rd = sc.getRequestDispatcher(Constants.ADMIN_LOGIN);
         rd.forward(req, res);
      }
      catch (SystemErrorException e)
      {
         log.error("SystemErrorException caught");
         log.error(e.getMessage(), e);
         rd = sc.getRequestDispatcher(Constants.ADMIN_FAIL_JSP);
         req.setAttribute(Constants.ERROR_TXT, e.getMessage());
         rd.forward(req, res);
      }
      catch (Exception e)
      {
         log.error("admin dispatch failed. URI:" + req.getRequestURI() + "?" + req.getQueryString());
         log.error(e.getMessage(), e);
         rd = sc.getRequestDispatcher(Constants.ADMIN_FAIL_JSP);
         req.setAttribute(Constants.ERROR_TXT, "de pagina kan niet worden getoond.");
         rd.forward(req, res);
      }
        if (vSession != null)
        {
        	log.info("########### httprequest done: java=" + (Calendar.getInstance().getTimeInMillis() - vSession.mWebTimer - vSession.getSqlTimer()) + ", SQL=" + vSession.getSqlTimer());
        }

   }

   public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      doGet(req, res);
   }

   public void destroy()
   {
       log.info("AdminDispatchServlet destroyed.");
   }

}
