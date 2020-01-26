package be.tba.util.session;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


public class SessionParms implements SessionParmsInf
{
   private Map<String, String> mKeyValuesMap;
   
   public SessionParms(HttpServletRequest req)
   {
      mKeyValuesMap = new HashMap<String, String>();
      for (Enumeration<String> parms = req.getParameterNames(); parms.hasMoreElements();)
      {
         String key = parms.nextElement();
         mKeyValuesMap.put(key, req.getParameter(key));
      }
   }
   
   public String getParameter(String key)
   {
      return mKeyValuesMap.get(key);
   }

}
