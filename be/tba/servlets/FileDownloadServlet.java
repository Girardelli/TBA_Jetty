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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.business.AccountBizzLogic;
import be.tba.business.InvoiceBizzLogic;
import be.tba.session.SessionManager;
import be.tba.session.SessionParms;
import be.tba.session.SessionParmsInf;
import be.tba.session.WebSession;
import be.tba.sqladapters.FileLocationSqlAdapter;
import be.tba.sqladapters.InvoiceSqlAdapter;
import be.tba.sqldata.FileLocationData;
import be.tba.sqldata.InvoiceEntityData;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.AccessDeniedException;
import be.tba.util.exceptions.LostSessionException;

public class FileDownloadServlet extends HttpServlet
{
   private static Logger log = LoggerFactory.getLogger(FileDownloadServlet.class);
   /**
    * this servlet only takes care of file downloads : hat is from server to client
    * (browser) The file uploads are taken care of by the jsp page servlets
    */
   private static final long serialVersionUID = 1L;

   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      RequestDispatcher rd = null;
      ServletContext sc = getServletContext();
      WebSession vSession = null;
      try
      {
         HttpSession httpSession = request.getSession(false);
         if (httpSession == null)
            throw new AccessDeniedException("U bent niet aangemeld.");

         vSession = (WebSession) httpSession.getAttribute(Constants.SESSION_OBJ);
         if (vSession == null || SessionManager.getInstance().isExpired(vSession) || vSession.getLogin() == null ) 
         {
            httpSession.invalidate();
            throw new AccessDeniedException("U bent niet aangemeld.");
         }
         SessionParmsInf params = new SessionParms(request);

         log.info("\nFileDownloadServlet (http session: " + vSession + "): userid:" + vSession.getUserId() + ", websessionid:" + vSession.getSessionId());

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
               File file = InvoiceBizzLogic.generateInvoiceXml(params, vSession);
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
               File file = AccountBizzLogic.generateKlantenXml(params, vSession);
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
         log.error(e.getMessage(), e);
         rd = sc.getRequestDispatcher(Constants.ADMIN_FAIL_JSP);
         request.setAttribute(Constants.ERROR_TXT, e.getMessage());
         rd.forward(request, response);
      }
      catch (Exception e)
      {
         log.info("URI:" + request.getRequestURI() + "?" + request.getQueryString());
         log.error(e.getMessage(), e);
         rd = sc.getRequestDispatcher(Constants.ADMIN_FAIL_JSP);
         request.setAttribute(Constants.ERROR_TXT, "Het bestand dat je wil downloaden is niet beschikbaar. ");
         rd.forward(request, response);
         log.info("forwarded to fail page");

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
