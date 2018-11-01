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

import be.tba.servlets.helper.AccountFacade;
import be.tba.servlets.helper.InvoiceFacade;
import be.tba.servlets.session.SessionManager;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.AccessDeniedException;
import be.tba.util.exceptions.LostSessionException;

public class FileDownloadServlet extends HttpServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        RequestDispatcher rd = null;
        ServletContext sc = getServletContext();;
        try
        {
            HttpSession httpSession = request.getSession();
            WebSession vSession = (WebSession) httpSession.getAttribute(Constants.SESSION_OBJ);

            if (vSession == null)
            {
                throw new AccessDeniedException("U bent niet aangemeld.");
            }
            
            SessionManager.getInstance().getSession(vSession.getSessionId(), "FileDownloadServlet()");

            System.out.println("\nFileDownloadServlet (http session: " + vSession + "): userid:" + vSession.getUserId() + ", websessionid:" + vSession.getSessionId());

            synchronized (vSession)
            {
                if (vSession.getRole() != AccountRole.ADMIN)
                {
                    throw new AccessDeniedException("access denied for " + vSession.getUserId());
                }
                // You must tell the browser the file type you are going to send
                // for example application/pdf, text/plain, text/html, image/jpg
                response.setContentType("text/plain");
                
                String vAction = (String) request.getParameter(Constants.SRV_ACTION);
                // ==============================================================================================
                // Download Fintro process log
                // ==============================================================================================
                if (vAction.equals(Constants.DOWNLOAD_FINTRO_PROCESS_TXT))
                {
                    if (vSession.getFintroProcessLog() == null)
                    {
                        throw new AccessDeniedException("Er is geen Fintro proces log beschikbaar");
                    }
                        // Make sure to show the download dialog
                    response.setHeader("Content-disposition", "attachment; filename=" + vSession.getFintroProcessLog().substring(vSession.getFintroProcessLog().lastIndexOf('\\') + 1, vSession.getFintroProcessLog().length()));
                    downloadFile(vSession.getFintroProcessLog(), response.getOutputStream());
                }
                else if (vAction.equals(Constants.DOWNLOAD_WK_VERKOPEN_XML))
                {
                    // Make sure to show the download dialog
                    File file = InvoiceFacade.generateInvoiceXml(request, vSession);
                    response.setHeader("Content-disposition", "attachment; filename=" + Constants.WC_VERKOPEN_XML);
                    downloadFile(file.getAbsolutePath(), response.getOutputStream());
                }
                else if (vAction.equals(Constants.DOWNLOAD_WK_KLANTEN_XML))
                {
                    // Make sure to show the download dialog
                    File file = AccountFacade.generateKlantenXml(request, vSession);
                    response.setHeader("Content-disposition", "attachment; filename=" + Constants.WC_KLANTEN_XML);
                    downloadFile(file.getAbsolutePath(), response.getOutputStream());
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
        }

    }
    
    private void downloadFile(String filePath, OutputStream out) throws IOException
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
        
    }
}
