package be.tba.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import be.tba.util.constants.Constants;
import be.tba.websockets.TbaWebSocketAdapter;


public class TbaWebSocketServlet extends WebSocketServlet 
{
	private static Logger log = LoggerFactory.getLogger(TbaWebSocketServlet.class);
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public void configure(WebSocketServletFactory factory) 
	{
        factory.register(TbaWebSocketAdapter.class);
        factory.getPolicy().setIdleTimeout((Constants.ADMIN_SESSION_TIMEOUT + 5) * 1000);
        log.info("TbaWebSocketServlet configured");
    }


   public void destroy()
   {
       log.info("TbaWebSocketServlet destroyed.");
   }
    
}
