/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.servlets.session;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import be.tba.util.exceptions.AccessDeniedException;
import be.tba.util.exceptions.LostSessionException;

final public class SessionManager
{
    private Map mMap;

    private Random mRand;

    private static SessionManager mInstance = null;

    private static final int kKeyLim = 0xFFFFF;

    public static SessionManager getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new SessionManager();

        }
        return mInstance;
    }

    public String add(WebSession session)
    {
        String vKey = generateId();
        mMap.put(vKey, session);
        System.out.println("New session added with id " + vKey);
        return vKey;
    }

    public WebSession remove(String sessionId)
    {
        if (sessionId == null)
            return null;
        WebSession vSession = (WebSession) mMap.get(sessionId);
        vSession.Close();
        return (WebSession) mMap.remove(sessionId);
    }

    public WebSession getSession(String sessionId, String caller) throws AccessDeniedException, LostSessionException
    {
        if (sessionId == null)
            throw new AccessDeniedException("Error: geen session id in de request.");
        WebSession vState = (WebSession) mMap.get(sessionId);
        if (vState == null)
            throw new AccessDeniedException("Aanmeld sessie is verlopen.");
        if (vState.isExpired(caller))
        {
            vState.Close();
            mMap.remove(vState);
            throw new LostSessionException();
        }
        return vState;
    }

    public void clean()
    {
        int cnt = 0;
        Set vKeySet = mMap.keySet();
        for (Iterator i = vKeySet.iterator(); i.hasNext();)
        {
            Object vKey = i.next();
            WebSession vState = (WebSession) mMap.get(vKey);
            if (vState.isExpired("cleaner"))
            {
                vState.Close();
                mMap.remove(vKey);
                cnt++;
            }
        }
        if (cnt > 0)
            System.out.println("SessionManager.clean: " + cnt + " sessionId's removed.");
    }

    private SessionManager()
    {
        mMap = Collections.synchronizedMap(new HashMap());
        mRand = new Random();
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

}
