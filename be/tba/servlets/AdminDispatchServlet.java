package be.tba.servlets;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Collection;

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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.mail.session.MailerSessionBean;
import be.tba.servlets.helper.AccountFacade;
import be.tba.servlets.helper.CallRecordFacade;
import be.tba.servlets.helper.IntertelCallManager;
import be.tba.servlets.helper.InvoiceFacade;
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

@WebServlet("/upload")
@MultipartConfig
public class AdminDispatchServlet extends HttpServlet
{
   /**
    *
    */
   private static final long serialVersionUID = 1L;
   private static Log log = LogFactory.getLog(AdminDispatchServlet.class);

   public AdminDispatchServlet()
   {
      System.out.println("AdminDispatchServlet created");
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
         if (ServletFileUpload.isMultipartContent(req))
         {
            fileUploader = new FileUploader(req);
            String accountId = fileUploader.getFormParameter(Constants.ACCOUNT_ID);
            AccountEntityData custAccount = AccountCache.getInstance().get(Integer.parseInt(accountId));
            if (custAccount == null) 
            {
               throw new Exception("Klant niet bekend voor deze file upload.");
            }
            fileUploader.setStoragePath(Constants.WORKORDER_FILEUPLOAD_DIR + File.separator + Tools.spaces2underscores(custAccount.getFullName()) + File.separator + "done");
            fileUploader.upload(req);
            vAction = fileUploader.getFormParameter(Constants.SRV_ACTION);
         }
         else
         {
            vAction = (String) req.getParameter(Constants.SRV_ACTION);
         }

         HttpSession httpSession = req.getSession();
         vSession = (WebSession) httpSession.getAttribute(Constants.SESSION_OBJ);

         if (vSession == null)
            throw new AccessDeniedException("U bent niet aangemeld.");
         vSession.resetSqlTimer();
         SessionManager.getInstance().getSession(vSession.getSessionId(), "AdminDispatchServlet(" + vAction + ")");

         System.out.println("\nAdminDispatchServlet: userid:" + vSession.getUserId() + ", websessionid:" + vSession.getSessionId() + " action=" + vAction);

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
               /*
                * //Set<Integer> vTaskList = new HashSet<Integer>();
                * 
                * //********************************** // accounts (super-subklanten relation)
                * Collection<Integer> idList = AccountCache.getInstance().getAllIds();
                * AccountSqlAdapter accountSession = new AccountSqlAdapter();
                * 
                * for (Iterator<Integer> iter = idList.iterator(); iter.hasNext();) { Integer
                * id = iter.next(); AccountEntityData account =
                * AccountCache.getInstance().get(Integer.valueOf(id)); if
                * (account.getSuperCustomer() != null && !account.getSuperCustomer().isEmpty())
                * { AccountEntityData superCust =
                * AccountCache.getInstance().get(account.getSuperCustomer());
                * accountSession.setSuperCustomerId(vSession, account.getId(),
                * superCust.getId()); } }
                * 
                * //********************************** // invoices!!! InvoiceSqlAdapter
                * vInvoiceSession = new InvoiceSqlAdapter(); Collection<InvoiceEntityData>
                * vInvoiceList = vInvoiceSession.getAllRows(vSession); for
                * (Iterator<InvoiceEntityData> vIter = vInvoiceList.iterator();
                * vIter.hasNext();) { InvoiceEntityData invoice = vIter.next();
                * 
                * // set account DB ID ipv fwdNumber //System.out.println("\t*** " +
                * invoice.getFileName()); if (invoice.getFileName() == null ||
                * invoice.getFileName().isEmpty()) { System.out.println(invoice.getInvoiceNr()
                * + " NOK: null or empty file name"); continue; } AccountEntityData account =
                * AccountCache.getInstance().get(invoice); if (account != null) { String
                * invoiceName =
                * invoice.getFileName().substring(invoice.getFileName().indexOf("FacN-") + 6);
                * int stopIndex = invoiceName.indexOf(".doc"); if (stopIndex == -1) { stopIndex
                * = invoiceName.indexOf(".pdf"); } if (stopIndex == -1) {
                * System.out.println(invoice.getInvoiceNr() +
                * " NOK: 'doc' or 'pdf' not found in file name"); continue; } invoiceName =
                * invoiceName.substring(invoiceName.indexOf('-') + 1, stopIndex); String
                * accountName = account.getFullName().replace(' ','_'); accountName =
                * accountName.replace(':','_'); accountName = accountName.replace(',','_');
                * accountName = accountName.replace(';','_');
                * 
                * if (accountName.equals(invoiceName)) {
                * System.out.println(invoice.getInvoiceNr() + " OK: " + accountName);
                * vInvoiceSession.setAccountId(vSession, invoice.getId(), account.getId(),
                * account.getFullName()); } else { System.out.println(invoice.getInvoiceNr() +
                * " NOK: invoice klant '" + invoiceName + "' != reffed account '" + accountName
                * + "'"); } } else { System.out.println(invoice.getInvoiceNr() + " NOK: '" +
                * invoice.getAccountFwdNr() + "' niet gevonden in account lijst");
                * //vInvoiceSession.setAccountId(vSession, invoice.getId(), 0); }
                * 
                * //********************************** // assign the tasks this invoice id
                * TaskSqlAdapter vTaskSession = new TaskSqlAdapter(); AccountEntityData
                * account2 = AccountCache.getInstance().get(invoice); if
                * (invoice.getAccountFwdNr() == null || account2 == null) {
                * System.out.println("invoice with FwdNumber=null or does not exist anymore");
                * continue; }
                * 
                * Collection<TaskEntityData> taskList =
                * vTaskSession.getTasksFromTillTimestamp(vSession, invoice.getAccountID(),
                * invoice.getStartTime(), invoice.getStopTime());
                * //System.out.println(invoice.getInvoiceNr() + ": process " + taskList.size()
                * + " tasks"); for (Iterator<TaskEntityData> i = taskList.iterator();
                * i.hasNext();) { TaskEntityData task = i.next(); if (task.getInvoiceId() > 0)
                * { System.out.println("###Task already assigned to invoice.");// list size=" +
                * vTaskList.size()); } else { vTaskSession.fixDbIds(vSession, task.getId(),
                * invoice.getId(), account2.getId()); }
                * 
                * } }
                */
               break;
            }

            // ==============================================================================================
            // MAIL_IT test
            // ==============================================================================================
            case Constants.MAIL_IT:
            {
//                    Collection<Integer> list = AccountCache.getInstance().getSuperCustomersList();
//                    synchronized (list)
//                    {
//                        for (Iterator<Integer> vIter = list.iterator(); vIter.hasNext();)
//                        {
//                        	Integer accountId = vIter.next();
//                            AccountEntityData accountData = AccountCache.getInstance().get(accountId);
//                            System.out.println("addAccount: accountdata for vValue=" + accountId + " is " + (accountData == null ? "null" : accountData.getFullName()));
//                        }
//                    }

               String vCustomerFilter = (String) vSession.getCallFilter().getCustFilter();
               if (vCustomerFilter != null && !vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL))
               {
                  try
                  {
                     AccountEntityData vAccountData = AccountCache.getInstance().get(vCustomerFilter);
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
                     System.out.println("MailTimerTask exception");
                     e.printStackTrace();
                  }
                  finally
                  {
                  }
               }

               // DbCleanTimerTask task = new DbCleanTimerTask();
               // task.run();
               // System.out.println("DB Clean done");

               // CallLogWriterSessionHome vWrHome = (CallLogWriterSessionHome)
               // vContext.lookup(EjbJndiNames.EJB_JNDI_CALL_LOG_WRITER_SESSION);
               // CallLogWriterSession vCallLogWriterSession = vWrHome.create();
               // CallRecordQuerySessionHome vQHome =
               // (CallRecordQuerySessionHome)
               // vContext.lookup(EjbJndiNames.EJB_JNDI_CALL_RECORD_QUERY_SESSION);
               // CallRecordQuerySession vQuerySession = vQHome.create();
               //
               // Collection vRecords =
               // vQuerySession.getDocumentedForMonth("473054", 9, 2011);
               // for (Iterator i = vRecords.iterator(); i.hasNext();)
               // {
               // CallRecordEntityData vEntry = (CallRecordEntityData) i.next();
               // vEntry.setIsMailed(true);
               // vCallLogWriterSession.setCallData(vEntry);
               // }
               // vRecords = vQuerySession.getDocumentedForMonth("473054", 10,
               // 2011);
               // for (Iterator i = vRecords.iterator(); i.hasNext();)
               // {
               // CallRecordEntityData vEntry = (CallRecordEntityData) i.next();
               // vEntry.setIsMailed(true);
               // vCallLogWriterSession.setCallData(vEntry);
               // }
               // vRecords = vQuerySession.getDocumentedForMonth("473054", 11,
               // 2011);
               // for (Iterator i = vRecords.iterator(); i.hasNext();)
               // {
               // CallRecordEntityData vEntry = (CallRecordEntityData) i.next();
               // vEntry.setIsMailed(true);
               // vCallLogWriterSession.setCallData(vEntry);
               // }
               // vQuerySession.remove();
               // vCallLogWriterSession.remove();

               /*
                * Connection con = null; try { con =
                * DriverManager.getConnection("jdbc:mysql://localhost/tbadb");
                * MailerSessionHome vHome = (MailerSessionHome)
                * vContext.lookup(EjbJndiNames.EJB_JNDI_MAILER_SESSION); MailerSession
                * vMailSession = vHome.create(); // vMailSession.sendMail(5);
                *
                * for (Iterator n = AccountCache.getInstance().getCustomerList().iterator();
                * n.hasNext();) { AccountEntityData vAccountData = (AccountEntityData)
                * n.next(); String vEmail = vAccountData.getEmail(); if (vEmail != null &&
                * vEmail.length() > 0 && (vAccountData.getMailHour1() > 8 ||
                * vAccountData.getMailHour2() > 8 || vAccountData.getMailHour3() > 8))
                * vMailSession.sendMail(con, vAccountData.getFwdNumber()); } // Check the
                * record and add it if it is a valid one. // //vMailSession.sendMail(5);
                * vMailSession.remove(); } catch (Exception e) {
                * System.out.println("MailTimerTask exception"); e.printStackTrace(); } finally
                * { if (con == null) { try { con.close(); con = null; } catch (SQLException ex)
                * { System.out .println("Error in Mailer: SQL connection could not be closed."
                * ); } } }
                */
               // do nothing
               // rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
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
               System.out.println("execute REMOVE_PENDING_CALL");
               IntertelCallManager.getInstance().removeCall(vSession, Integer.parseInt(req.getParameter(Constants.PENDING_CALL_ID)));
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
               String custFilter = (String) vSession.getCallFilter().getCustFilter();
               if (custFilter != null && !custFilter.equals(Constants.ACCOUNT_FILTER_ALL))
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
               String custFilter = (String) vSession.getCallFilter().getCustFilter();
               if (custFilter != null && !custFilter.equals(Constants.ACCOUNT_FILTER_ALL))
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
               CallRecordFacade.updateCustomerChanges(req, vSession, false);
               // rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // AUTO_RECORD_UPDATE
            // ==============================================================================================
            case Constants.AUTO_RECORD_UPDATE:
            {
               CallRecordFacade.retrieveRecordForUpdate(req, vSession);
               vSession.setIsAutoUpdateRecord(true);
               rd = sc.getRequestDispatcher(Constants.UPDATE_RECORD_JSP);
               break;
            }

            // ==============================================================================================
            // ACTION_GOTO_RECORD_UPDATE
            // ==============================================================================================
            case Constants.ACTION_GOTO_RECORD_UPDATE:
            {
               CallRecordFacade.retrieveRecordForUpdate(req, vSession);
               rd = sc.getRequestDispatcher(Constants.UPDATE_RECORD_JSP);
               break;
            }

            // ==============================================================================================
            // SAVE RECORD
            // ==============================================================================================
            case Constants.SAVE_RECORD:
            {
               CallRecordFacade.saveRecord(req, vSession);
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
               System.out.println("AdminDispatchServlet ready to ADD_RECORD_JSP: " + Constants.ADD_RECORD_JSP);
               break;
            }

            // ==============================================================================================
            // SAVE MANUALY CREATED RECORD
            // ==============================================================================================
            case Constants.SAVE_MAN_RECORD:
            {
               CallRecordFacade.saveManualRecord(req, vSession);
               rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // DELETE RECORD
            // ==============================================================================================
            case Constants.RECORD_DELETE:
            {
               CallRecordFacade.deleteRecords(req, vSession);
               rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO_CANVAS
            // ==============================================================================================
            case Constants.GOTO_CANVAS:
            {
               vSession.getCallFilter().setCustFilter(req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
               rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // FILTER RECORD LIST
            // ==============================================================================================
//                case Constants.GOTO_RECORD_ADMIN:
//                {
//                    vSession.getCallFilter().setCustFilter((String) req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
//                    vSession.getCallFilter().setStateFilter((String) req.getParameter(Constants.ACCOUNT_FILTER_CALL_STATE));
//                    vSession.getCallFilter().setDirFilter((String) req.getParameter(Constants.ACCOUNT_FILTER_CALL_DIR));
//                    rd = sc.getRequestDispatcher(Constants.ADMIN_CALLS_JSP);
//                    break;
//                }

            // ==============================================================================================
            // NEW_CALL
            // ==============================================================================================
            case Constants.NEW_CALL:
            {
               CallRecordFacade.createNewUnmappedCall(req, vSession);
               rd = sc.getRequestDispatcher(Constants.NEW_CALL_JSP);
               break;
            }

            // ==============================================================================================
            // REFRESH_OPEN_CALLS
            // ==============================================================================================
            case Constants.REFRESH_OPEN_CALLS:
            {
               CallRecordFacade.updateNewUnmappedCall(req, vSession);
               rd = sc.getRequestDispatcher(Constants.NEW_CALL_JSP);
               break;
            }

            // ==============================================================================================
            // SAVE_NEW_CALL
            // ==============================================================================================
            case Constants.SAVE_NEW_CALL:
            {
               String vKey = (String) req.getParameter(Constants.RECORD_ID);
               if (vKey == null || vKey.isEmpty())
               {
                  rd = sc.getRequestDispatcher(Constants.NEW_CALL_JSP);
               }
               else
               {
                  if (CallRecordFacade.saveNewCall(req, vSession))
                  {
                     // System.out.println("fire SELECT_SUBCUSTOMER_JSP");
                     rd = sc.getRequestDispatcher(Constants.SELECT_SUBCUSTOMER_JSP);
                     // System.out.println("getRequestDispatcher done");
                  }
                  else
                  {
                     // System.out.println("fire CANVAS_JSP");
                     rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
                  }
               }
               break;
            }

            // ==============================================================================================
            // SAVE_NEW_SUBCUSTOMER
            // ==============================================================================================
            case Constants.SAVE_NEW_SUBCUSTOMER:
            {
               // String vKey = (String) req.getParameter(Constants.RECORD_ID);
               String vNewFwdNr = (String) req.getParameter(Constants.ACCOUNT_NEW_FWDNR);
               String vOldFwdNr = (String) req.getParameter(Constants.ACCOUNT_FWDNR);

               // System.out.println("SAVE_NEW_SUBCUSTOMER " + vNewFwdNr + ", " + vOldFwdNr);
               if (vNewFwdNr == null)
               {
                  rd = sc.getRequestDispatcher(Constants.NEW_CALL_JSP);
               }
               else
               {
                  if (vOldFwdNr != null)
                  {
                     if (!vOldFwdNr.equals(vNewFwdNr))
                     {
                        CallRecordFacade.saveNewSubCustomer(req, vSession, vNewFwdNr);
                     }
                     rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
                  }
                  else
                  {
                     rd = sc.getRequestDispatcher(Constants.NEW_CALL_JSP);
                  }
               }
               break;
            }
            // ==============================================================================================
            // REMOVE_OPEN_CALL
            // ==============================================================================================
            case Constants.REMOVE_OPEN_CALL:
            {
               CallRecordFacade.removeNewCall(req, vSession);
               rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO DELETE ACCOUNT
            // ==============================================================================================
            case Constants.GOTO_ACCOUNT_DELETE:
            {
               String vLtd = (String) req.getParameter(Constants.ACCOUNT_TO_DELETE);
               StringTokenizer vStrTok = new StringTokenizer(vLtd, ",");

               System.out.println("goto account delete vLtd=" + vLtd);
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
                  AccountRole role = AccountRole.fromShort(accountData.getRole());
                  if (role == AccountRole.ADMIN || role == AccountRole.EMPLOYEE)
                  {
                     System.out.println("goto account delete: setCurrentAccountId=" + vLtd + ", account fwdnr=" + accountData.getFwdNumber());
                     AccountFacade.archiveAccount(vSession, Integer.parseInt(vLtd));
                     rd = sc.getRequestDispatcher(Constants.ADMIN_EMPLOYEE_JSP);
                  }
                  else
                  {
                     req.setAttribute(Constants.ERROR_TXT, "Als je doorgaat wordt dit account gearchiveerd. Je kan dit account terug actief maken via de pagina 'gearchiveerde klanten'. Wilt u hiermee verder gaan?");
                     req.setAttribute(Constants.NEXT_PAGE, Constants.ACCOUNT_DELETE);
                     req.setAttribute(Constants.PREVIOUS_PAGE, Constants.GOTO_ACCOUNT_ADMIN);
                     vSession.setSessionFwdNr(vLtd);
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
               System.out.println("account delete: current id=" + vSession.getCurrentAccountId());
               // String accountFwdNr =
               // AccountCache.getInstance().idToFwdNr(Integer.parseInt(vSession.getCurrentAccountId()));
               AccountEntityData accountData = AccountCache.getInstance().get(vSession.getCurrentAccountId());
               System.out.println("account delete: key=" + vSession.getCurrentAccountId() + ", fwd nr=" + accountData.getFwdNumber());

               AccountRole role = AccountRole.fromShort(accountData.getRole());
               AccountFacade.archiveAccount(vSession, vSession.getCurrentAccountId());
               if (role == AccountRole.ADMIN || role == AccountRole.EMPLOYEE)
               {
                  rd = sc.getRequestDispatcher(Constants.ADMIN_EMPLOYEE_JSP);
               }
               else
               {
                  rd = sc.getRequestDispatcher(Constants.ADMIN_ACCOUNT_JSP);
               }
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
            // GOTO_SAVE_ACCOUNT
            // ==============================================================================================
            case Constants.GOTO_SAVE_ACCOUNT:
            {
               String accountIdStr = (String) req.getParameter(Constants.ACCOUNT_ID);
               int accountId = Integer.valueOf(accountIdStr);
               AccountEntityData account = AccountCache.getInstance().get(accountId);
               String vOldNr = account.getFwdNumber();
               String vNewNr = (String) req.getParameter(Constants.ACCOUNT_FORWARD_NUMBER);
               AccountEntityData newData = AccountFacade.updateAccountData(vSession, req);
               System.out.println("old nr=" + vOldNr + ", new nr=" + vNewNr);
               if (vOldNr != null && vNewNr != null && !vOldNr.equals(vNewNr))
               {
                  req.setAttribute(Constants.ERROR_TXT, "U hebt de doorschakelnummer van deze klant gewijzigd.\nWilt u hiermee verder gaan?");
                  req.setAttribute(Constants.NEXT_PAGE, Constants.SAVE_ACCOUNT);
                  req.setAttribute(Constants.PREVIOUS_PAGE, Constants.ACCOUNT_UPDATE);
                  vSession.setNewAccount(newData);
                  vSession.setCurrentAccountId(Integer.parseInt(vOldNr));
                  rd = sc.getRequestDispatcher(Constants.ARE_YOU_SURE_JSP);
               }
               else
               {
                  AccountFacade.saveAccount(vSession, newData);
                  rd = sc.getRequestDispatcher(Constants.ADMIN_ACCOUNT_JSP);
               }
               break;
            }

            // ==============================================================================================
            // SAVE ACCOUNT
            // ==============================================================================================
            case Constants.SAVE_ACCOUNT:
            {
               AccountEntityData newData = vSession.getNewAccount();
               AccountFacade.changeFwdNumber(vSession, vSession.getSessionFwdNr(), newData.getFwdNumber());
               AccountFacade.saveAccount(vSession, newData);
               rd = sc.getRequestDispatcher(Constants.ADMIN_ACCOUNT_JSP);
               break;
            }

            // ==============================================================================================
            // DEREGISTRATE ACCOUNT
            // ==============================================================================================
            case Constants.ACCOUNT_DEREG:
            {
               AccountFacade.deregisterAccount(vSession, req);
               rd = sc.getRequestDispatcher(Constants.UPDATE_ACCOUNT_JSP);
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
            // UPDATE ACCOUNT
            // ==============================================================================================
            case Constants.ACCOUNT_UPDATE:
            {
               rd = sc.getRequestDispatcher(Constants.UPDATE_ACCOUNT_JSP);
               break;
            }

            // ==============================================================================================
            // ADD ACCOUNT
            // ==============================================================================================
            case Constants.ACCOUNT_ADD:
            {
               Vector<String> errorList = AccountFacade.addAccount(vSession, req);
               AccountRole role = AccountRole.fromShort(req.getParameter(Constants.ACCOUNT_ROLE));
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
               AccountFacade.mailCustomer(req, vSession);
               rd = sc.getRequestDispatcher(Constants.UPDATE_ACCOUNT_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO_ADD_INVOICE
            // ==============================================================================================
            case Constants.GOTO_ADD_INVOICE:
            {
               vSession.getCallFilter().setCustFilter(req.getParameter(Constants.ACCOUNT_FILTER_ALL));
               rd = sc.getRequestDispatcher(Constants.ADD_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // INVOICES ADD
            // ==============================================================================================
            case Constants.INVOICE_ADD:
            {
               InvoiceFacade.addManualInvoice(req, vSession);
               rd = sc.getRequestDispatcher(Constants.CANVAS_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO_INVOICE
            // ==============================================================================================
            case Constants.GOTO_INVOICE:
            {
               if (req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER) != null)
                  vSession.getCallFilter().setCustFilter(req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
               if (req.getParameter(Constants.INVOICE_MONTH) != null)
                  vSession.setMonthsBack(Integer.parseInt((String) req.getParameter(Constants.INVOICE_MONTH)));
               if (req.getParameter(Constants.INVOICE_YEAR) != null)
                  vSession.setYear(Integer.parseInt((String) req.getParameter(Constants.INVOICE_YEAR)));
               if (req.getParameter(Constants.INVOICE_ID) != null)
               {
                  vSession.setInvoiceId(Integer.parseInt((String) req.getParameter(Constants.INVOICE_ID)));
                  System.out.println("admindispatch GOTO_INVOICE: INVOICE_ID = " + req.getParameter(Constants.INVOICE_ID));
               }
               else
               {
                  vSession.setInvoiceId(0);
                  System.out.println("admindispatch GOTO_INVOICE: INVOICE_ID = null");
               }
               rd = sc.getRequestDispatcher(Constants.INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // ADMIN INVOICES
            // ==============================================================================================
            case Constants.GOTO_INVOICE_ADMIN:
            {
               if (req.getParameter(Constants.INVOICE_MONTH) != null)
                  vSession.setMonthsBack(Integer.parseInt((String) req.getParameter(Constants.INVOICE_MONTH)));
               if (req.getParameter(Constants.INVOICE_YEAR) != null)
                  vSession.setYear(Integer.parseInt((String) req.getParameter(Constants.INVOICE_YEAR)));
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // GENERATE INVOICE
            // ==============================================================================================
            case Constants.GENERATE_INVOICE:
            {
               if (req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER) != null)
                  vSession.getCallFilter().setCustFilter(req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
               if (req.getParameter(Constants.INVOICE_MONTH) != null)
                  vSession.setMonthsBack(Integer.parseInt((String) req.getParameter(Constants.INVOICE_MONTH)));

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
               fileUploader.upload(req);
               uploadedFile = fileUploader.waitTillFinished();
               vSession.setUploadedFileName(uploadedFile);
               System.out.println("PROCESS_FINTRO_XLSX: file ready for parsing: " + vSession.getUploadedFileName());
               rd = sc.getRequestDispatcher(Constants.OPEN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // GENERATE_INVOICE_XML
            // ==============================================================================================
            case Constants.GENERATE_INVOICE_XML:
            {
               InvoiceFacade.generateInvoiceXml(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // GENERATE All INVOICES
            // ==============================================================================================
            case Constants.GENERATE_ALL_INVOICES:
            {
               InvoiceFacade.generateInvoices(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // FREEZE INVOICES
            // ==============================================================================================
            case Constants.INVOICE_FREEZE:
            {
               InvoiceFacade.freezeInvoices(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // MAIL INVOICES
            // ==============================================================================================
            case Constants.INVOICE_MAIL:
            {
               InvoiceFacade.mailInvoices(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // SET INVOICES PAYED
            // ==============================================================================================
            case Constants.INVOICE_SETPAYED:
            {
               InvoiceFacade.setInvoicesPayed(req, vSession);
               rd = sc.getRequestDispatcher(Constants.OPEN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // DELETE INVOICES
            // ==============================================================================================
            case Constants.INVOICE_DELETE:
            {
               InvoiceFacade.deleteInvoices(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // SAVE INVOICES
            // ==============================================================================================
            case Constants.SAVE_INVOICE:
            {
               InvoiceFacade.saveInvoice(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // SAV INVOICE PAYDATE
            // ==============================================================================================
            case Constants.SAVE_PAYDATE:
            {
               InvoiceFacade.savePayDate(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // GENERATE_CREDITNOTE
            // ==============================================================================================
            case Constants.GENERATE_CREDITNOTE:
            {
               InvoiceFacade.generateCreditInvoice(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
               break;
            }

            // ==============================================================================================
            // GOTO_RECORD_SEARCH
            // ==============================================================================================
            case Constants.GOTO_RECORD_SEARCH:
            {
               if (req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER) != null)
                  vSession.getCallFilter().setCustFilter(req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
               if (req.getParameter(Constants.RECORD_SEARCH_STR) != null)
                  vSession.setSearchString((String) req.getParameter(Constants.RECORD_SEARCH_STR));
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
               System.out.println("AdminDispatchServlet: SEARCH_SHOW_NEXT");

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
               TaskFacade.deleteTask(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_TASK_JSP);
               break;
            }

            // ==============================================================================================
            // ADMIN TASK
            // ==============================================================================================
            case Constants.GOTO_TASK_ADMIN:
            {
               if (req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER) != null)
                  vSession.getCallFilter().setCustFilter((String) req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
               rd = sc.getRequestDispatcher(Constants.ADMIN_TASK_JSP);
               break;
            }

            // ==============================================================================================
            // MODIFY TASK
            // ==============================================================================================
            case Constants.TASK_UPDATE:
            {
               TaskFacade.modifyTask(req, vSession);
               rd = sc.getRequestDispatcher(Constants.UPDATE_TASK_JSP);
               break;
            }

            // ==============================================================================================
            // SAVE TASK
            // ==============================================================================================
            case Constants.SAVE_TASK:
            {
               TaskFacade.saveTask(req, vSession);
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
               TaskFacade.addTask(req, vSession);
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
               String id = (String) req.getParameter(Constants.WORKORDER_ID);
               vSession.setWorkOrderId(Integer.parseInt(id));
               rd = sc.getRequestDispatcher(Constants.WORKORDER_JSP);
               break;
            }

            // ==============================================================================================
            // UPLOAD WORKORDER FILE
            // ==============================================================================================
            case Constants.ACTION_SAVE_WORKORDER:
            {
               TaskFacade.saveWorkOrder(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_WORK_ORDER_JSP);
               break;
            }
            
            // ==============================================================================================
            // UPLOAD WORKORDER INPUT FILE
            // ==============================================================================================
            case Constants.UPLOAD_WORKORDER_FILE:
            {
               fileUploader.upload(req);
               uploadedFile = fileUploader.waitTillFinished();
               if (TaskFacade.addWorkOrderFile(req, vSession, uploadedFile))
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
               TaskFacade.deleteWorkOrderFile(req, vSession);
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
         System.out.println("SystemErrorException caught");
         e.printStackTrace();
         rd = sc.getRequestDispatcher(Constants.ADMIN_FAIL_JSP);
         req.setAttribute(Constants.ERROR_TXT, e.getMessage());
         rd.forward(req, res);
      }
      catch (Exception e)
      {
         System.out.println("admin dispatch failed. URI:" + req.getRequestURI() + "?" + req.getQueryString());
         e.printStackTrace();
         rd = sc.getRequestDispatcher(Constants.ADMIN_FAIL_JSP);
         req.setAttribute(Constants.ERROR_TXT, "de pagina kan niet worden getoont.");
         rd.forward(req, res);
      }
//        if (vSession != null)
//        	System.out.println("httprequest done: SQL timer=" + vSession.getSqlTimer());

   }

   public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      doGet(req, res);
   }

}
