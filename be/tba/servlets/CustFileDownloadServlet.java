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

import be.tba.ejb.task.interfaces.FileLocationData;
import be.tba.ejb.task.session.FileLocationSqlAdapter;
import be.tba.servlets.session.SessionManager;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.AccessDeniedException;
import be.tba.util.exceptions.LostSessionException;
import be.tba.util.session.SessionParms;
import be.tba.util.session.SessionParmsInf;

public class CustFileDownloadServlet extends HttpServlet
{
   private static Log log = LogFactory.getLog(CustFileDownloadServlet.class);
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
         e.printStackTrace();
         rd = sc.getRequestDispatcher(Constants.PROTECT_FAIL_JSP);
         request.setAttribute(Constants.ERROR_TXT, e.getMessage());
         rd.forward(request, response);
      }
      catch (LostSessionException e)
      {
         e.printStackTrace();
         rd = sc.getRequestDispatcher(Constants.LOGIN_HTML);
         rd.forward(request, response);
      }
      catch (Exception e)
      {
         System.out.println("URI:" + request.getRequestURI() + "?" + request.getQueryString());
         e.printStackTrace();
         rd = sc.getRequestDispatcher(Constants.PROTECT_FAIL_JSP);
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
}
