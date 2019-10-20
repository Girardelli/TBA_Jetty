package be.tba.servlets;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import be.tba.util.constants.Constants;
import be.tba.websockets.TbaWebSocketAdapter;


public class TbaWebSocketServlet extends WebSocketServlet 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public void configure(WebSocketServletFactory factory) 
	{
        factory.register(TbaWebSocketAdapter.class);
        factory.getPolicy().setIdleTimeout((Constants.ADMIN_SESSION_TIMEOUT + 5) * 1000);
        System.out.println("TbaWebSocketServlet configured");
    }


    
}
