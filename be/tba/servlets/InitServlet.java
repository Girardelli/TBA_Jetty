package be.tba.servlets;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import be.tba.util.session.AccountCache;
import be.tba.util.timer.TimerManager;

public class InitServlet extends GenericServlet
{
   /**
    * 
    */
   private static final long serialVersionUID = 10003L;

   @SuppressWarnings("unused")
   public void init(ServletConfig config) throws ServletException
   {
      System.out.println("init servlet called.");

      try
      {
         TimerManager vTimerManager = TimerManager.getInstance();
         AccountCache vAccountCache = AccountCache.getInstance();
         // The newInstance() call is a work around for some
         // broken Java implementations

         Class.forName("com.mysql.jdbc.Driver").newInstance();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new ServletException("AccountCache init failed: " + e.getMessage());
      }
   }

   public void destroy()
   {
      System.out.println("init servlet destroyed.");
   }

   public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException
   {
   }

}