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

import be.tba.session.SessionManager;
import be.tba.session.SessionParms;
import be.tba.session.SessionParmsInf;
import be.tba.session.WebSession;
import be.tba.sqladapters.FileLocationSqlAdapter;
import be.tba.sqldata.FileLocationData;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.AccessDeniedException;
import be.tba.util.exceptions.LostSessionException;

public class CustFileDownloadServlet extends HttpServlet
{
   private static Logger log = LoggerFactory.getLogger(CustFileDownloadServlet.class);
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
         HttpSession httpSession = request.getSession();
         vSession = (WebSession) httpSession.getAttribute(Constants.SESSION_OBJ);
         if (vSession == null || SessionManager.getInstance().isExpired(vSession)|| vSession.getLogin() == null ) 
         {
            httpSession.invalidate();
            throw new AccessDeniedException("No loginData object in session");
         }
         SessionParmsInf params = new SessionParms(request);
         String URI = request.getRequestURI() + "?" + request.getQueryString();

         log.info("\nname:" + vSession.getLogin().getName() + ", websessionid:" + vSession.getSessionId() + ", URI:" + URI);
         synchronized (vSession)
         {
            // You must tell the browser the file type you are going to send
            // for example application/pdf, text/plain, text/html, image/jpg
            response.setContentType("text/plain");

            // parsing the multipart content must be done before any getXXX call on the
            // request
            // because such getXXX calls will implicitly call the parser which can only be
            // called once.
            String vAction = params.getParameter(Constants.SRV_ACTION);
            log.info("Cust File Dowload, action=" + vAction);

            // ==============================================================================================
            // Download Fintro process log
            // ==============================================================================================
            switch (vAction)
            {
            case Constants.DOWNLOAD_WORKORDER_FILE:
            {
               FileLocationSqlAdapter fileLocationSession = new FileLocationSqlAdapter();
               FileLocationData fileData = fileLocationSession.getRow(vSession, Integer.parseInt((String) params.getParameter(Constants.WORKORDER_FILE_ID)));
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
         rd = sc.getRequestDispatcher(Constants.PROTECT_FAIL_JSP);
         request.setAttribute(Constants.ERROR_TXT, e.getMessage());
         rd.forward(request, response);
      }
      catch (Exception e)
      {
         log.info("URI:" + request.getRequestURI() + "?" + request.getQueryString());
         log.error(e.getMessage(), e);
         rd = sc.getRequestDispatcher(Constants.PROTECT_FAIL_JSP);
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
      log.info("CustFileDownloadServlet destroyed.");
   }
}
