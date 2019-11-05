package be.tba.websockets;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import com.google.gson.Gson;

import be.tba.servlets.helper.IntertelCallManager;
import be.tba.servlets.helper.PhoneMapManager;
import be.tba.servlets.session.SessionManager;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.AccessDeniedException;
import be.tba.util.exceptions.LostSessionException;

public class TbaWebSocketAdapter extends WebSocketAdapter
{
   private Session session;
   private WebSession webSession;
   private static Gson sGson = new Gson();

   @Override
   public void onWebSocketConnect(Session session)
   {
      super.onWebSocketConnect(session);
      this.session = session;

      System.out.println("MessagingAdapter.onWebSocketConnect");

   }

   @Override
   public void onWebSocketClose(int statusCode, String reason)
   {
      this.session = null;
      System.err.println("Close connection " + statusCode + ", " + reason);
      webSession.setWsSession(null);
      webSession.setWsActive(false);
      super.onWebSocketClose(statusCode, reason);
   }

   @Override
   public void onWebSocketText(String message)
   {
      super.onWebSocketText(message);
      System.out.println("MessagingAdapter.onWebSocketText: " + message);

      if (message.startsWith(Constants.WS_LOGIN))
      {
         String sessionId = message.substring(Constants.WS_LOGIN.length());
         try
         {
            WebSession newWebSession = SessionManager.getInstance().getSession(sessionId, "WebSockets");
            if (newWebSession != null)
            {
               webSession = newWebSession;
               newWebSession.setWsSession(this.session);
            }
         } catch (AccessDeniedException | LostSessionException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      } else
      {
         System.out.println("Message from WEbSocket: " + message);
      }

   }

   public static void broadcast(WebSocketData data)
   {
      if (data.operation == WebSocketData.CALL_ANSWERED)
      {
         // this session ID shall be used by the canvas JSP to open the call update window automatically.
         data.answeredBySession = PhoneMapManager.getInstance().getSessionIdForPhoneId(data.answeredByPhoneId);
         //System.out.println("data.answeredBySession=" + data.answeredBySession);
         
      }
      
      Collection<WebSession> activeSessions = SessionManager.getInstance().getActiveWebSockets();
      for (Iterator<WebSession> i = activeSessions.iterator(); i.hasNext();)
      {
         WebSession session = i.next();
         if (session.isWsActive())
         {
            try
            {
               System.out.println("----------------------------------\r\nsend websocket event: " + data.toString());
               session.getWsSession().getRemote().sendString(sGson.toJson(data, WebSocketData.class));
            } catch (IOException e)
            {
               // TODO Auto-generated catch block
               System.out.println("WebSocket send to client failed");
               e.printStackTrace();
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
}
