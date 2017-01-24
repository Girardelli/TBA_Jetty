package be.tba.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Collection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.mail.session.MailerSessionBean;
import be.tba.servlets.helper.AccountFacade;
import be.tba.servlets.helper.CallRecordFacade;
import be.tba.servlets.helper.InvoiceFacade;
import be.tba.servlets.helper.TaskFacade;
import be.tba.servlets.session.SessionManager;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.AccessDeniedException;
import be.tba.util.exceptions.LostSessionException;
import be.tba.util.exceptions.SystemErrorException;
import be.tba.util.session.AccountCache;

import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.ejb.pbx.interfaces.CallRecordEntityData;

public class AdminDispatchServlet extends HttpServlet
{
   /**
     *
     */
   private static final long serialVersionUID = 1L;
   private static Log log = LogFactory.getLog(AdminDispatchServlet.class);

   public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      RequestDispatcher rd = null;
      ServletContext sc = null;
      try
      {
         log.info("doGet()");
    	  sc = getServletContext();
         res.setContentType("text/html");
         res.setCharacterEncoding("UTF-8");
         req.setCharacterEncoding("UTF-8");
         String vAction = (String) req.getParameter(Constants.SRV_ACTION);

         HttpSession httpSession = req.getSession();
         WebSession vSession = (WebSession) httpSession.getAttribute(Constants.SESSION_OBJ);

         if (vSession == null)
            throw new AccessDeniedException("U bent niet aangemeld.");
         SessionManager.getInstance().getSession(vSession.getSessionId(), "AdminDispatchServlet(" + vAction + ")");

         System.out.println("\nAdminDispatchServlet (http session: " + vSession + "): userid:" + vSession.getUserId() + ", websessionid:" + vSession.getSessionId() + " action=" + vAction);

         synchronized (vSession)
         {
            if (vSession.getRole() != AccountRole.ADMIN && vSession.getRole() != AccountRole.EMPLOYEE)
               throw new AccessDeniedException("access denied for " + vSession.getUserId());

            if (vAction == null)
            {
               throw new SystemErrorException("Interne fout.");
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
            // MAIL_IT test
            // ==============================================================================================
            if (vAction.equals(Constants.MAIL_IT))
            {
               Collection<String> list = AccountCache.getInstance().getSuperCustomersList();
               synchronized (list)
               {
                  for (Iterator<String> vIter = list.iterator(); vIter.hasNext();)
                  {
                     String vValue = vIter.next();
                     AccountEntityData accountData = AccountCache.getInstance().get(vValue);
                     System.out.println("addAccount: accountdata for vValue=" + vValue + " is " + (accountData == null ? "null" : accountData.getFullName()));
                  }
               }

               String vCustomerFilter = (String) vSession.getCallFilter().getCustFilter();
               if (vCustomerFilter != null && !vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL))
               {
                  Connection con = null;
                  try
                  {
                     AccountEntityData vAccountData = AccountCache.getInstance().get(vCustomerFilter);
                     if (vAccountData != null)
                     {
                        String vEmail = vAccountData.getEmail();
                        if (vEmail != null && vEmail.length() > 0 && (vAccountData.getMailHour1() > 8 || vAccountData.getMailHour2() > 8 || vAccountData.getMailHour3() > 8))
                        {
                           MailerSessionBean.sendMail(vSession, vAccountData.getFwdNumber());
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
                * vContext.lookup(EjbJndiNames.EJB_JNDI_MAILER_SESSION);
                * MailerSession vMailSession = vHome.create(); //
                * vMailSession.sendMail(5);
                *
                * for (Iterator n =
                * AccountCache.getInstance().getCustomerList().iterator();
                * n.hasNext();) { AccountEntityData vAccountData =
                * (AccountEntityData) n.next(); String vEmail =
                * vAccountData.getEmail(); if (vEmail != null && vEmail.length()
                * > 0 && (vAccountData.getMailHour1() > 8 ||
                * vAccountData.getMailHour2() > 8 || vAccountData.getMailHour3()
                * > 8)) vMailSession.sendMail(con, vAccountData.getFwdNumber());
                * } // Check the record and add it if it is a valid one. //
                * //vMailSession.sendMail(5); vMailSession.remove(); } catch
                * (Exception e) { System.out.println("MailTimerTask exception");
                * e.printStackTrace(); } finally { if (con == null) { try {
                * con.close(); con = null; } catch (SQLException ex) {
                * System.out
                * .println("Error in Mailer: SQL connection could not be closed."
                * ); } } }
                */
               // do nothing
               rd = sc.getRequestDispatcher(Constants.ADMIN_CALLS_JSP);
            }
            // ==============================================================================================
            // SHOW_MAIL_ERROR button pushed
            // ==============================================================================================
            else if (vAction.equals(Constants.SHOW_MAIL_ERROR))
            {
               rd = sc.getRequestDispatcher(Constants.SHOW_ERROR_JSP);
            }
            // ==============================================================================================
            // GOTO_NOTLOGGED_CALLS button pushed
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_NOTLOGGED_CALLS))
            {
               rd = sc.getRequestDispatcher(Constants.ADMIN_NOTLOGGEDCALLS_JSP);
            }

            // ==============================================================================================
            // RECORD_SHOW_NEXT_10
            // ==============================================================================================
            else if (vAction.equals(Constants.RECORD_SHOW_NEXT_10))
            {
               vSession.setDaysBack(vSession.getDaysBack() - 10);
               rd = sc.getRequestDispatcher(Constants.ADMIN_CALLS_JSP);
            }

            // ==============================================================================================
            // RECORD_SHOW_NEXT
            // ==============================================================================================
            else if (vAction.equals(Constants.RECORD_SHOW_NEXT))
            {
               String custFilter = (String) vSession.getCallFilter().getCustFilter();
               if (custFilter != null && !custFilter.equals(Constants.ACCOUNT_FILTER_ALL))
               {
                  vSession.setMonthsBack(vSession.getMonthsBack() + 1);
               }
               else
               {
                  vSession.setDaysBack(vSession.getDaysBack() - 1);
               }
               rd = sc.getRequestDispatcher(Constants.ADMIN_CALLS_JSP);
            }

            // ==============================================================================================
            // RECORD_SHOW_PREV
            // ==============================================================================================
            else if (vAction.equals(Constants.RECORD_SHOW_PREV))
            {
               String custFilter = (String) vSession.getCallFilter().getCustFilter();
               if (custFilter != null && !custFilter.equals(Constants.ACCOUNT_FILTER_ALL))
               {
                  vSession.setMonthsBack(vSession.getMonthsBack() - 1);
               }
               else
               {
                  vSession.setDaysBack(vSession.getDaysBack() + 1);
               }
               rd = sc.getRequestDispatcher(Constants.ADMIN_CALLS_JSP);
            }

            // ==============================================================================================
            // RECORD_SHOW_PREV_10
            // ==============================================================================================
            else if (vAction.equals(Constants.RECORD_SHOW_PREV_10))
            {
               vSession.setDaysBack(vSession.getDaysBack() + 10);
               rd = sc.getRequestDispatcher(Constants.ADMIN_CALLS_JSP);
            }

            // ==============================================================================================
            // MODIFY RECORD
            // ==============================================================================================
            else if (vAction.equals(Constants.RECORD_UPDATE))
            {
               CallRecordFacade.retrieveRecordForUpdate(req, vSession);
               rd = sc.getRequestDispatcher(Constants.UPDATE_RECORD_JSP);
            }

            // ==============================================================================================
            // SAVE RECORD
            // ==============================================================================================
            else if (vAction.equals(Constants.SAVE_RECORD))
            {
               CallRecordFacade.saveRecord(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_CALLS_JSP);
            }

            // ==============================================================================================
            // ADD RECORD
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_ADD_RECORD))
            {
               rd = sc.getRequestDispatcher(Constants.ADD_RECORD_JSP);
               System.out.println("AdminDispatchServlet ready to ADD_RECORD_JSP: " + Constants.ADD_RECORD_JSP);
            }

            // ==============================================================================================
            // SAVE MANUALY CREATED RECORD
            // ==============================================================================================
            else if (vAction.equals(Constants.SAVE_MAN_RECORD))
            {
               CallRecordFacade.saveManualRecord(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_CALLS_JSP);
            }

            // ==============================================================================================
            // DELETE RECORD
            // ==============================================================================================
            else if (vAction.equals(Constants.RECORD_DELETE))
            {
               CallRecordFacade.deleteRecords(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_CALLS_JSP);
            }

            // ==============================================================================================
            // FILTER RECORD LIST
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_RECORD_ADMIN))
            {
               vSession.getCallFilter().setCustFilter((String) req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
               vSession.getCallFilter().setStateFilter((String) req.getParameter(Constants.ACCOUNT_FILTER_CALL_STATE));
               vSession.getCallFilter().setDirFilter((String) req.getParameter(Constants.ACCOUNT_FILTER_CALL_DIR));
               rd = sc.getRequestDispatcher(Constants.ADMIN_CALLS_JSP);
            }

            // ==============================================================================================
            // GET_OPEN_CALLS
            // ==============================================================================================
            else if (vAction.equals(Constants.GET_OPEN_CALLS))
            {
               CallRecordFacade.newCall(req, vSession);
               rd = sc.getRequestDispatcher(Constants.NEW_CALL_JSP);
            }

            // ==============================================================================================
            // REFRESH_OPEN_CALLS
            // ==============================================================================================
            else if (vAction.equals(Constants.REFRESH_OPEN_CALLS))
            {
               CallRecordFacade.updateNewCall(req, vSession);
               rd = sc.getRequestDispatcher(Constants.NEW_CALL_JSP);
            }

            // ==============================================================================================
            // SAVE_NEW_CALL
            // ==============================================================================================
            else if (vAction.equals(Constants.SAVE_NEW_CALL))
            {
               CallRecordFacade.saveNewCall(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_CALLS_JSP);
            }

            // ==============================================================================================
            // REMOVE_OPEN_CALL
            // ==============================================================================================
            else if (vAction.equals(Constants.REMOVE_OPEN_CALL))
            {
               CallRecordFacade.removeNewCall(req, vSession);
            }

            // ==============================================================================================
            // GOTO DELETE
            // ACCOUNT
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_ACCOUNT_DELETE))
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
                  String vFwdNr = AccountCache.getInstance().idToFwdNr(Integer.parseInt(vLtd));

                  AccountEntityData accountData = AccountCache.getInstance().get(vFwdNr);
                  if (accountData == null)
                  {
                     throw new SystemErrorException("Account not found for fwdNr " + vFwdNr);
                  }
                  AccountRole role = AccountRole.fromShort(accountData.getRole());
                  if (role == AccountRole.ADMIN || role == AccountRole.EMPLOYEE)
                  {
                     System.out.println("goto account delete: setCurrentAccountId=" + vLtd + ", account fwdnr=" + vFwdNr);
                     AccountFacade.deleteAccount(vSession, vLtd);
                     rd = sc.getRequestDispatcher(Constants.ADMIN_EMPLOYEE_JSP);
                  }
                  else
                  {
                     req.setAttribute(Constants.ERROR_TXT, "Het verwijderen van een klant heeft als gevolg dat al zijn gegevens, " + "oproepen en taken volledig verwijderd worden uit de database.\n" + "Wilt u hiermee verder gaan?");
                     req.setAttribute(Constants.NEXT_PAGE, Constants.ACCOUNT_DELETE);
                     req.setAttribute(Constants.PREVIOUS_PAGE, Constants.GOTO_ACCOUNT_ADMIN);
                     vSession.setCurrentAccountId(vLtd);
                     rd = sc.getRequestDispatcher(Constants.ARE_YOU_SURE_JSP);
                  }
               }
            }

            // ==============================================================================================
            // GOTO EMPLOYEE
            // ACCOUNT
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_EMPLOYEE_ADMIN))
            {
               rd = sc.getRequestDispatcher(Constants.ADMIN_EMPLOYEE_JSP);
            }

            // ==============================================================================================
            // GOTO
            // EMPLOYEE
            // COST
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_EMPLOYEE_COST))
            {
               rd = sc.getRequestDispatcher(Constants.ADMIN_EMPLOYEE_COST_JSP);
            }

            // ==============================================================================================
            // DELETE
            // ACCOUNT
            // ==============================================================================================
            else if (vAction.equals(Constants.ACCOUNT_DELETE))
            {
               System.out.println("account delete: current id=" + vSession.getCurrentAccountId());
               String accountFwdNr = AccountCache.getInstance().idToFwdNr(Integer.parseInt(vSession.getCurrentAccountId()));
               AccountEntityData accountData = AccountCache.getInstance().get(accountFwdNr);
               System.out.println("account delete: key=" + vSession.getCurrentAccountId() + ", fwd nr=" + accountFwdNr);

               AccountRole role = AccountRole.fromShort(accountData.getRole());
               AccountFacade.deleteAccount(vSession, vSession.getCurrentAccountId());
               if (role == AccountRole.ADMIN || role == AccountRole.EMPLOYEE)
               {
                  rd = sc.getRequestDispatcher(Constants.ADMIN_EMPLOYEE_JSP);
               }
               else
               {
                  rd = sc.getRequestDispatcher(Constants.ADMIN_ACCOUNT_JSP);
               }
            }

            // ==============================================================================================
            // ADMIN
            // ACCOUNT
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_ACCOUNT_ADMIN))
            {
               rd = sc.getRequestDispatcher(Constants.ADMIN_ACCOUNT_JSP);
            }

            // ==============================================================================================
            // GOTO
            // SAVE_ACCOUNT
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_SAVE_ACCOUNT))
            {
               String vOldNr = (String) req.getParameter(Constants.ACCOUNT_ID);
               String vNewNr = (String) req.getParameter(Constants.ACCOUNT_FORWARD_NUMBER);
               AccountEntityData newData = AccountFacade.updateAccountData(req);
               System.out.println("old nr=" + vOldNr + ", new nr=" + vNewNr);
               if (vOldNr != null && vNewNr != null && !vOldNr.equals(vNewNr))
               {
                  req.setAttribute(Constants.ERROR_TXT, "U hebt de doorschakelnummer van deze klant gewijzigd. Hierdoor " + "moeten alle oproepgegevens van deze klant in de database veranderd worden.\n" + "Wilt u hiermee verder gaan?");
                  req.setAttribute(Constants.NEXT_PAGE, Constants.SAVE_ACCOUNT);
                  req.setAttribute(Constants.PREVIOUS_PAGE, Constants.ACCOUNT_UPDATE);
                  vSession.setNewAccount(newData);
                  vSession.setCurrentAccountId(vOldNr);
                  rd = sc.getRequestDispatcher(Constants.ARE_YOU_SURE_JSP);
               }
               else
               {
                  AccountFacade.saveAccount(vSession, newData);
                  rd = sc.getRequestDispatcher(Constants.ADMIN_ACCOUNT_JSP);
               }
            }

            // ==============================================================================================
            // SAVE
            // ACCOUNT
            // ==============================================================================================
            else if (vAction.equals(Constants.SAVE_ACCOUNT))
            {
               AccountEntityData newData = vSession.getNewAccount();
               AccountFacade.changeFwdNumber(vSession, vSession.getCurrentAccountId(), newData.getFwdNumber());
               AccountFacade.saveAccount(vSession, newData);
               rd = sc.getRequestDispatcher(Constants.ADMIN_ACCOUNT_JSP);
            }

            // ==============================================================================================
            // DEREGISTRATE
            // ACCOUNT
            // ==============================================================================================
            else if (vAction.equals(Constants.ACCOUNT_DEREG))
            {
               AccountFacade.deregisterAccount(vSession, req);
               rd = sc.getRequestDispatcher(Constants.UPDATE_ACCOUNT_JSP);
            }

            // ==============================================================================================
            // ADD
            // ACCOUNT
            // button
            // pushed
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_ACCOUNT_ADD))
            {
               rd = sc.getRequestDispatcher(Constants.ADD_ACCOUNT_JSP);
            }

            // ==============================================================================================
            // GOTO_EMPLOYEE_ADD
            // button
            // pushed
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_EMPLOYEE_ADD))
            {
               rd = sc.getRequestDispatcher(Constants.ADD_EMPLOYEE_JSP);
            }

            // ==============================================================================================
            // UPDATE
            // ACCOUNT
            // (account
            // kollom
            // selected)
            // ==============================================================================================
            else if (vAction.equals(Constants.ACCOUNT_UPDATE))
            {
               rd = sc.getRequestDispatcher(Constants.UPDATE_ACCOUNT_JSP);
            }

            // ==============================================================================================
            // ADD
            // ACCOUNT
            // ==============================================================================================
            else if (vAction.equals(Constants.ACCOUNT_ADD))
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
            }

            // ==============================================================================================
            // MAIL_CUSTOMER
            // ==============================================================================================
            else if (vAction.equals(Constants.MAIL_CUSTOMER))
            {
               AccountFacade.mailCustomer(req, vSession);
               rd = sc.getRequestDispatcher(Constants.UPDATE_ACCOUNT_JSP);
            }

            // ==============================================================================================
            // GOTO_INVOICE
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_INVOICE))
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
                  vSession.setInvoiceId(-1);
                  System.out.println("admindispatch GOTO_INVOICE: INVOICE_ID = null");
               }
               rd = sc.getRequestDispatcher(Constants.INVOICE_JSP);
            }

            // ==============================================================================================
            // ADMIN
            // INVOICES
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_INVOICE_ADMIN))
            {
               if (req.getParameter(Constants.INVOICE_MONTH) != null)
                  vSession.setMonthsBack(Integer.parseInt((String) req.getParameter(Constants.INVOICE_MONTH)));
               if (req.getParameter(Constants.INVOICE_YEAR) != null)
                  vSession.setYear(Integer.parseInt((String) req.getParameter(Constants.INVOICE_YEAR)));
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
            }

            // ==============================================================================================
            // GENERATE
            // INVOICE
            // ==============================================================================================
            else if (vAction.equals(Constants.GENERATE_INVOICE))
            {
               if (req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER) != null)
                  vSession.getCallFilter().setCustFilter(req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
               if (req.getParameter(Constants.INVOICE_MONTH) != null)
                  vSession.setMonthsBack(Integer.parseInt((String) req.getParameter(Constants.INVOICE_MONTH)));

               if (vSession.getInvoiceHelper().generateInvoice())
               {
                  rd = sc.getRequestDispatcher(Constants.INVOICE_JSP);
               }
               else
               {
                  req.setAttribute(Constants.ERROR_TXT, "Het factuur kan niet aangemaakt worden. Is het factuur misschien al geopend door een ander programma?");
                  req.setAttribute(Constants.NEXT_PAGE, Constants.GOTO_INVOICE);
                  rd = sc.getRequestDispatcher(Constants.ANNOUNCEMENT_JSP);
               }
            }

            // ==============================================================================================
            // GOTO
            // OPEN
            // INVOICE
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_OPEN_INVOICE))
            {
               rd = sc.getRequestDispatcher(Constants.OPEN_INVOICE_JSP);
            }

            // ==============================================================================================
            // GENERATE
            // All
            // INVOICES
            // ==============================================================================================
            else if (vAction.equals(Constants.GENERATE_ALL_INVOICES))
            {
               InvoiceFacade.generateInvoices(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
            }

            // ==============================================================================================
            // FREEZE
            // INVOICES
            // ==============================================================================================
            else if (vAction.equals(Constants.INVOICE_FREEZE))
            {
               InvoiceFacade.freezeInvoices(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
            }

            // ==============================================================================================
            // MAIL
            // INVOICES
            // ==============================================================================================
            else if (vAction.equals(Constants.INVOICE_MAIL))
            {
               InvoiceFacade.mailInvoices(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
            }

            // ==============================================================================================
            // SET
            // INVOICES
            // PAYED
            // ==============================================================================================
            else if (vAction.equals(Constants.INVOICE_SETPAYED))
            {
               InvoiceFacade.setInvoicesPayed(req, vSession);
               rd = sc.getRequestDispatcher(vSession.getCallingJsp());
            }

            // ==============================================================================================
            // DELETE
            // INVOICES
            // ==============================================================================================
            else if (vAction.equals(Constants.INVOICE_DELETE))
            {
               InvoiceFacade.deleteInvoices(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
            }

            // ==============================================================================================
            // SAVE
            // INVOICES
            // ==============================================================================================
            else if (vAction.equals(Constants.SAVE_INVOICE))
            {
               InvoiceFacade.saveInvoice(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
            }

            // ==============================================================================================
            // SAVE
            // INVOICE
            // PAYDATE
            // ==============================================================================================
            else if (vAction.equals(Constants.SAVE_PAYDATE))
            {
               InvoiceFacade.savePayDate(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
            }

            // ==============================================================================================
            // GOTO
            // INVOICE
            // ADD
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_INVOICE_ADD))
            {
               rd = sc.getRequestDispatcher(Constants.ADD_INVOICE_JSP);
               System.out.println("add invoice jsp must come now:" + Constants.ADD_INVOICE_JSP);

            }

            // ==============================================================================================
            // INVOICES
            // ADD
            // ==============================================================================================
            else if (vAction.equals(Constants.INVOICE_ADD))
            {
               InvoiceFacade.addInvoice(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_INVOICE_JSP);
            }

            // ==============================================================================================
            // GOTO_RECORD_SEARCH
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_RECORD_SEARCH))
            {
               if (req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER) != null)
                  vSession.getCallFilter().setCustFilter(req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
               if (req.getParameter(Constants.RECORD_SEARCH_STR) != null)
                  vSession.setSearchString((String) req.getParameter(Constants.RECORD_SEARCH_STR));
               Calendar calendar = Calendar.getInstance();
               vSession.setYear(calendar.get(Calendar.YEAR));
               vSession.setMonthsBack(calendar.get(Calendar.MONTH));
               rd = sc.getRequestDispatcher(Constants.ADMIN_SEARCH_JSP);
            }

            // ==============================================================================================
            // SEARCH_SHOW_NEXT
            // ==============================================================================================
            else if (vAction.equals(Constants.SEARCH_SHOW_NEXT))
            {
               System.out.println("AdminDispatchServlet: SEARCH_SHOW_NEXT");

               if (!vSession.isCurrentMonth())
                  vSession.incrementMonthsBack();
               rd = sc.getRequestDispatcher(Constants.ADMIN_SEARCH_JSP);
            }

            // ==============================================================================================
            // SEARCH_SHOW_PREV
            // ==============================================================================================
            else if (vAction.equals(Constants.SEARCH_SHOW_PREV))
            {
               vSession.decrementMonthsBack();
               rd = sc.getRequestDispatcher(Constants.ADMIN_SEARCH_JSP);
            }

            // ==============================================================================================
            // DELETE
            // TASK
            // ==============================================================================================
            else if (vAction.equals(Constants.TASK_DELETE))
            {
               TaskFacade.deleteTask(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_TASK_JSP);
            }

            // ==============================================================================================
            // ADMIN
            // TASK
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_TASK_ADMIN))
            {
               if (req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER) != null)
                  vSession.getCallFilter().setCustFilter((String) req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
               rd = sc.getRequestDispatcher(Constants.ADMIN_TASK_JSP);
            }

            // ==============================================================================================
            // MODIFY
            // TASK
            // ==============================================================================================
            else if (vAction.equals(Constants.TASK_UPDATE))
            {
               TaskFacade.modifyTask(req, vSession);
               rd = sc.getRequestDispatcher(Constants.UPDATE_TASK_JSP);
            }

            // ==============================================================================================
            // SAVE
            // TASK
            // ==============================================================================================
            else if (vAction.equals(Constants.SAVE_TASK))
            {
               TaskFacade.saveTask(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_TASK_JSP);
            }

            // ==============================================================================================
            // ADD
            // TASK
            // button
            // pushed
            // ==============================================================================================
            else if (vAction.equals(Constants.GOTO_TASK_ADD))
            {
               rd = sc.getRequestDispatcher(Constants.ADD_TASK_JSP);
            }

            // ==============================================================================================
            // ADD
            // TASK
            // ==============================================================================================
            else if (vAction.equals(Constants.TASK_ADD))
            {
               TaskFacade.addTask(req, vSession);
               rd = sc.getRequestDispatcher(Constants.ADMIN_TASK_JSP);
            }

            // ==============================================================================================
            // TASK_SHOW_NEXT
            // ==============================================================================================
            else if (vAction.equals(Constants.TASK_SHOW_NEXT))
            {
               if (!vSession.isCurrentMonth())
                  vSession.incrementMonthsBack();
               rd = sc.getRequestDispatcher(Constants.ADMIN_TASK_JSP);
            }

            // ==============================================================================================
            // TASK_SHOW_PREV
            // ==============================================================================================
            else if (vAction.equals(Constants.TASK_SHOW_PREV))
            {
               vSession.decrementMonthsBack();
               rd = sc.getRequestDispatcher(Constants.ADMIN_TASK_JSP);
            }

            // ==============================================================================================
            // EMPLCOST_SHOW_NEXT
            // ==============================================================================================
            else if (vAction.equals(Constants.EMPLCOST_SHOW_NEXT))
            {
               if (!vSession.isCurrentMonth())
                  vSession.incrementMonthsBack();
               rd = sc.getRequestDispatcher(Constants.ADMIN_EMPLOYEE_COST_JSP);
            }

            // ==============================================================================================
            // EMPLCOST_SHOW_PREV
            // ==============================================================================================
            else if (vAction.equals(Constants.EMPLCOST_SHOW_PREV))
            {
               vSession.decrementMonthsBack();
               rd = sc.getRequestDispatcher(Constants.ADMIN_EMPLOYEE_COST_JSP);
            }

            // ==============================================================================================
            // ADMIN
            // HOME
            // ==============================================================================================
            else if (vAction.equals(Constants.ADMIN_HOME))
            {
               rd = sc.getRequestDispatcher(Constants.ADMIN_CALLS_JSP);
            }

            // ==============================================================================================
            // LOG
            // OUT
            // button
            // pushed
            // ==============================================================================================
            else if (vAction.equals(Constants.ADMIN_LOG_OFF))
            {
               SessionManager.getInstance().remove(vSession.getSessionId());
               rd = sc.getRequestDispatcher(Constants.ADMIN_LOGIN);
               rd.forward(req, res);
               return;
            }

            // ==============================================================================================
            // error
            // ==============================================================================================
            else
            {
               throw new SystemErrorException("Onbekende actie (" + vAction + ")");
            }
            if (rd != null)
               rd.forward(req, res);

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
         e.printStackTrace();
         rd = sc.getRequestDispatcher(Constants.ADMIN_FAIL_JSP);
         req.setAttribute(Constants.ERROR_TXT, e.getMessage());
         rd.forward(req, res);
      }
      catch (Exception e)
      {
         System.out.println("URI:" + req.getRequestURI() + "?" + req.getQueryString());
         e.printStackTrace();
      }
   }

   public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      doGet(req, res);
   }

}
