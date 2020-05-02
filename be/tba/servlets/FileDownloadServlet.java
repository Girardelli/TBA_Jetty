package be.tba.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.tba.ejb.invoice.interfaces.InvoiceEntityData;
import be.tba.ejb.invoice.session.InvoiceSqlAdapter;
import be.tba.ejb.task.interfaces.FileLocationData;
import be.tba.ejb.task.session.FileLocationSqlAdapter;
import be.tba.servlets.helper.AccountFacade;
import be.tba.servlets.helper.InvoiceFacade;
import be.tba.servlets.session.SessionManager;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.AccessDeniedException;
import be.tba.util.exceptions.LostSessionException;
import be.tba.util.session.SessionParms;
import be.tba.util.session.SessionParmsInf;

public class FileDownloadServlet extends HttpServlet
{
   private static Log log = LogFactory.getLog(FileDownloadServlet.class);
   /**
    * this servlet only takes care of file downloads : hat is from server to client (browser)
    * The file uploads are taken care of by the jsp page servlets
    */
   private static final long serialVersionUID = 1L;

   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      RequestDispatcher rd = null;
      ServletContext sc = getServletContext();
      WebSession vSession = null;
      try
      {
         HttpSession httpSession = request.getSession();
         vSession = (WebSession) httpSession.getAttribute(Constants.SESSION_OBJ);
         SessionParmsInf params = new SessionParms(request);
         SessionManager.getInstance().getSession(vSession.getSessionId(), "FileDownloadServlet()");

         System.out.println("\nFileDownloadServlet (http session: " + vSession + "): userid:" + vSession.getUserId() + ", websessionid:" + vSession.getSessionId());

         synchronized (vSession)
         {
            // You must tell the browser the file type you are going to send
            // for example application/pdf, text/plain, text/html, image/jpg
            response.setContentType("text/plain");

            String vAction = (String) params.getParameter(Constants.SRV_ACTION);

            // ==============================================================================================
            // Download Fintro process log
            // ==============================================================================================
            switch (vAction)
            {
            case Constants.DOWNLOAD_FINTRO_PROCESS_TXT:
            {
               if (vSession.getRole() != AccountRole.ADMIN)
               {
                  throw new AccessDeniedException("access denied for " + vSession.getUserId());
               }
               if (vSession.getFintroProcessLog() == null)
               {
                  throw new AccessDeniedException("Er is geen Fintro proces log beschikbaar");
               }
               // Make sure to show the download dialog
               response.setHeader("Content-disposition", "attachment; filename=" + vSession.getFintroProcessLog().substring(vSession.getFintroProcessLog().lastIndexOf('\\') + 1, vSession.getFintroProcessLog().length()));
               downloadFile(vSession.getFintroProcessLog(), response.getOutputStream(), true);
               break;
            }
            case Constants.DOWNLOAD_WK_VERKOPEN_XML:
            {
               if (vSession.getRole() != AccountRole.ADMIN)
               {
                  throw new AccessDeniedException("access denied for " + vSession.getUserId());
               }
               // Make sure to show the download dialog
               File file = InvoiceFacade.generateInvoiceXml(params, vSession);
               response.setHeader("Content-disposition", "attachment; filename=" + Constants.WC_VERKOPEN_XML);
               downloadFile(file.getAbsolutePath(), response.getOutputStream(), true);
               break;
            }
            case Constants.DOWNLOAD_WK_KLANTEN_XML:
            {
               if (vSession.getRole() != AccountRole.ADMIN)
               {
                  throw new AccessDeniedException("access denied for " + vSession.getUserId());
               }
               // Make sure to show the download dialog
               File file = AccountFacade.generateKlantenXml(params, vSession);
               response.setHeader("Content-disposition", "attachment; filename=" + Constants.WC_KLANTEN_XML);
               downloadFile(file.getAbsolutePath(), response.getOutputStream(), true);
               break;
            }
            case Constants.DOWNLOAD_FACTUUR:
            {
               if (vSession.getRole() != AccountRole.ADMIN)
               {
                  throw new AccessDeniedException("access denied for " + vSession.getUserId());
               }
               int vInvoiceId = vSession.getInvoiceId();
               if (vInvoiceId > 0)
               {
                  InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
                  InvoiceEntityData vInvoiceData = vInvoiceSession.getInvoiceById(vSession, vInvoiceId);
                  if (vInvoiceData != null)
                  {
                     File file = new File(vInvoiceData.getFileName());
                     response.setHeader("Content-disposition", "attachment; filename=" + vInvoiceData.getInvoiceNr() + ".pdf");
                     downloadFile(file.getAbsolutePath(), response.getOutputStream(), false);
                  }
               }
               break;
            }
            case Constants.DOWNLOAD_WORKORDER_FILE:
            {
               FileLocationSqlAdapter fileLocationSession = new FileLocationSqlAdapter();
               FileLocationData fileData = fileLocationSession.getRow(vSession, Integer.parseInt((String) request.getParameter(Constants.WORKORDER_FILE_ID)));
               log.info(fileData.toString());
               if (fileData != null)
               {
                  File file = new File(fileData.storagePath);

                  if (file.exists())
                  {
                     response.setHeader("Content-disposition", "attachment; filename=" + fileData.name);
                     downloadFile(file.getAbsolutePath(), response.getOutputStream(), false);
                  }
                  else
                  {
                     log.error("DOWNLOAD_WORKORDER_FILE: file '" + fileData.storagePath + "' does not exist.");
                  }
               }
               break;
            }
            default:
               log.error("FileDownloadServlet unknown action received: " + vAction);
            }
         }
      }
      catch (AccessDeniedException e)
      {
         e.printStackTrace();
         rd = sc.getRequestDispatcher(Constants.ADMIN_FAIL_JSP);
         request.setAttribute(Constants.ERROR_TXT, e.getMessage());
         rd.forward(request, response);
      }
      catch (LostSessionException e)
      {
         e.printStackTrace();
         rd = sc.getRequestDispatcher(Constants.ADMIN_LOGIN);
         rd.forward(request, response);
      }
      catch (Exception e)
      {
         System.out.println("URI:" + request.getRequestURI() + "?" + request.getQueryString());
         e.printStackTrace();
         rd = sc.getRequestDispatcher(Constants.ADMIN_FAIL_JSP);
         request.setAttribute(Constants.ERROR_TXT, "Het bestand dat je wil downloaden is niet beschikbaar. ");
         rd.forward(request, response);
         System.out.println("forwarded to fail page");

      }

   }

   private void downloadFile(String filePath, OutputStream out, boolean removeFile) throws IOException
   {
      File file = new File(filePath);

      // This should send the file to browser
      FileInputStream in = new FileInputStream(file);
      byte[] buffer = new byte[4096];
      int length;
      while ((length = in.read(buffer)) > 0)
      {
         out.write(buffer, 0, length);
      }
      in.close();
      out.flush();
      if (removeFile)
      {
         file.delete();
      }
   }

   public void destroy()
   {
       log.info("FileDownloadServlet destroyed.");
   }


}
