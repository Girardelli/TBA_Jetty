package be.tba.websockets;

import java.util.Collection;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import be.tba.session.PhoneMapManager;
import be.tba.session.SessionManager;
import be.tba.session.WebSession;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.AccessDeniedException;
import be.tba.util.exceptions.LostSessionException;

public class TbaWebSocketAdapter extends WebSocketAdapter
{
   private static Logger log = LoggerFactory.getLogger(TbaWebSocketAdapter.class);
   private Session session;
   private WebSession webSession;

   public TbaWebSocketAdapter()
   {
      super();
      session = null;
      webSession = null;
   }
   
   @Override
   public void onWebSocketConnect(Session session)
   {
      this.session = session;
      super.onWebSocketConnect(session);

      // log.info("MessagingAdapter.onWebSocketConnect");

   }

   @Override
   public void onWebSocketClose(int statusCode, String reason)
   {
      cleanup();
      super.onWebSocketClose(statusCode, reason);
   }

   @Override
   public void onWebSocketText(String message)
   {
      // log.info("MessagingAdapter.onWebSocketText: " + message);

      if (message.startsWith(Constants.WS_LOGIN))
      {
         String sessionId = message.substring(Constants.WS_LOGIN.length());
         try
         {
            WebSession newWebSession = SessionManager.getInstance().getSession(sessionId);
            if (newWebSession != null)
            {
               webSession = newWebSession;
               newWebSession.setWsSession(this.session);
            }
         }
         catch (AccessDeniedException | LostSessionException e)
         {
            // TODO Auto-generated catch block
            log.warn(e.getMessage());
         }
      }
      else
      {
         log.info("Message from WEbSocket: " + message);
      }
      super.onWebSocketText(message);
   }

   public void onWebSocketError(Throwable cause)
   {
      cleanup();
      log.error(cause.getMessage());
      super.onWebSocketError(cause);
   }

   
   public static synchronized void broadcast(WebSocketData data)
   {
      if (data.operation == WebSocketData.CALL_ANSWERED)
      {
         // this session ID shall be used by the canvas JSP to open the call update
         // window automatically.
         data.answeredBySession = PhoneMapManager.getInstance().getSessionIdForPhoneId(data.answeredByPhoneId);
         // log.info("data.answeredBySession=" + data.answeredBySession);

      }
      Gson sGson = new Gson();

      Collection<WebSession> activeSessions = SessionManager.getInstance().getActiveWebSockets();
      String jsonString = sGson.toJson(data, WebSocketData.class);
      for (WebSession session : activeSessions)
      {
         if (session.getRole() == AccountRole.ADMIN || session.getRole() == AccountRole.EMPLOYEE)
         {
            try
            {
               // log.info("----------------------------------\r\nsend websocket event: " +
               // data.toString());
               session.getWsSession().getRemote().sendString(jsonString);
            }
            catch (Exception e)
            {
               // TODO Auto-generated catch block
               log.error("WebSocket broadcast to client with sessionId=" + session.getSessionId() + ", user=" + session.getUserId() + " failed");
               log.error(e.getMessage(), e);
            }
         }
      }
   }

   public static void sendToCustomer(WebSocketData data, int accountId)
   {
      Collection<WebSession> activeSessions = SessionManager.getInstance().getActiveWebSockets();
      Gson sGson = new Gson();
      String jsonString = sGson.toJson(data, WebSocketData.class);
      for (WebSession session : activeSessions)
      {
         if (session.getAccountId() == accountId)
         {
            try
            {
               // log.info("----------------------------------\r\nsend websocket event: " +
               // data.toString());
               session.getWsSession().getRemote().sendString(jsonString);
            }
            catch (Exception e)
            {
               // TODO Auto-generated catch block
               log.info("WebSocket send to client with sessionId=" + session.getSessionId() + ", user=" + session.getUserId() + " failed");
               log.error(e.getMessage(), e);
            }
         }
      }
   }

   private void sendText(String text) throws Exception
   {
      if (session != null && session.isOpen())
      {
         session.getRemote().sendString(text);
      }
   }

   public void disconnect(int status, String reason)
   {

      session.close(status, reason);
      disconnect(status, reason);
   }
   
   private void cleanup()
   {
      if (this.session != null)
      {
         this.session = null;
      }
      // System.err.println("Close connection " + statusCode + ", " + reason);
      if (webSession != null)
      {
         webSession.setWsSession(null);
         webSession.setWsActive(false);
      }

   }
}
