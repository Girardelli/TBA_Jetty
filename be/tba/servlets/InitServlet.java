package be.tba.servlets;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.util.session.AccountCache;
import be.tba.util.timer.CallManagerCleanupTimerTask;
import be.tba.util.timer.DbCleanTimerTask;
import be.tba.util.timer.MailTimerTask;
import be.tba.util.timer.TimerManager;
import be.tba.util.timer.UrlCheckTimerTask;


public class InitServlet extends GenericServlet
{
	private static Logger log = LoggerFactory.getLogger(InitServlet.class);
    /**
    * 
    */
    // final static Logger sLogger = LoggerFactory.getLogger(InitServlet.class);
    private static final long serialVersionUID = 10003L;

    @SuppressWarnings("unused")
    public void init(ServletConfig config) throws ServletException
    {
        log.info("init servlet called.");
        // sLogger.info("CallLogThread.run()");
        try
        {
            TimerManager vTimerManager = TimerManager.getInstance();
            AccountCache vAccountCache = AccountCache.getInstance();
            log.info("AccountCache initialized");
            
            vTimerManager.add(new DbCleanTimerTask());
            vTimerManager.add(new MailTimerTask());
            vTimerManager.add(new UrlCheckTimerTask());
            vTimerManager.add(new CallManagerCleanupTimerTask());
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new ServletException("AccountCache init failed: " + e.getMessage());
        }
        log.info("Servlets initialized");
    }

    public void destroy()
    {
       TimerManager vTimerManager = TimerManager.getInstance();
       vTimerManager.destroy();
       log.info("InitServlet destroyed.");
    }

    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException
    {
    }

}
