/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.session;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.util.exceptions.AccessDeniedException;
import be.tba.util.exceptions.LostSessionException;

final public class SessionManager
{
   private static Logger log = LoggerFactory.getLogger(SessionManager.class);
   private Map<String, WebSession> mMap;

   private Random mRand;

   private static SessionManager mInstance = null;

   private static final int kKeyLim = 0xFFFFFFF;

   public static SessionManager getInstance()
   {
      if (mInstance == null)
      {
         mInstance = new SessionManager();
      }
      return mInstance;
   }

   synchronized public void add(WebSession session, String userId)
   {
      WebSession ses = getSessionForUser(userId);
      if (ses != null)
      {
         removeAndClose(ses.getSessionId());
      }
      session.userInit(userId, generateId());
      mMap.put(session.getSessionId(), session);
      log.info("New session added for " + userId + ", id " + session.getSessionId() + ". active sessions:" + mMap.size());
   }

   synchronized public void remove(String sessionId)
   {
      if (sessionId != null)
         removeAndClose(sessionId);
   }

   synchronized public WebSession getSession(String sessionId) throws AccessDeniedException, LostSessionException
   {
      if (sessionId == null)
         throw new AccessDeniedException("Error: geen session id in de request.");
      WebSession vState = mMap.get(sessionId);
      if (vState == null)
         throw new AccessDeniedException("Aanmeld sessie is verlopen.");
      return vState;
   }

   synchronized public Collection<WebSession> getActiveWebSockets()
   {
      Collection<WebSession> vValuesList = mMap.values();
      Collection<WebSession> result = new Vector<WebSession>();
      for (WebSession session : vValuesList)
      {
         if (session.isWsActive())
         {
            result.add(session);
         }
      }
      return result;
   }
   
   synchronized public boolean isExpired(WebSession session)
   {
      if (session.isExpiredAndRefresh())
      {
         remove(session.getSessionId());
         return true;
      }
      return false;
   }


   synchronized public void clean()
   {
      int cnt = 0;
      Set<String> vKeySet = mMap.keySet();
      for (Iterator<String> i = vKeySet.iterator(); i.hasNext();)
      {
         String sessionId = i.next();
         WebSession vState = mMap.get(sessionId);
         if (vState.isExpiredAndRefresh())
         {
            vState.close();
            i.remove();
            cnt++;
         }
      }
      if (cnt > 0)
         log.info("SessionManager.clean: " + cnt + " sessionId's removed.");
   }

   private SessionManager()
   {
      mMap = new HashMap<String, WebSession>();
      mRand = new Random();
      log.info("SessionManager created");

   }

   private String generateId()
   {
      String vKeyStr;
      do
      {
         int vKey = mRand.nextInt(kKeyLim);
         vKeyStr = Integer.toHexString(vKey);
      } while (mMap.containsKey(vKeyStr));
      return vKeyStr;
   }

   private WebSession getSessionForUser(String user)
   {
      for (Iterator<WebSession> i = mMap.values().iterator(); i.hasNext();)
      {
         WebSession session = i.next();
         // log.info("getIdForUser: " + user + "=? sessionAccountId=" +
         // session.getAccountId());
         if (user.equals(session.getUserId()))
         {
            return session;
         }
      }
      return null;
   }

   private void removeAndClose(String sessionId)
   {
      WebSession vSession = (WebSession) mMap.get(sessionId);
      if (vSession != null)
      {
         mMap.remove(sessionId);
         vSession.close();
      }
   }

}
