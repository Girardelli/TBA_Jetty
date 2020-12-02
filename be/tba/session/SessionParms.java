package be.tba.session;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.servlets.CustomerDispatchServlet;
import be.tba.util.constants.Constants;

public class SessionParms implements SessionParmsInf
{
   private static Logger log = LoggerFactory.getLogger(SessionParms.class);
   private Map<String, String> mKeyValuesMap;

   public SessionParms(HttpServletRequest req)
   {
      int cnt = 0;
      mKeyValuesMap = new HashMap<String, String>();
      for (Enumeration<String> parms = req.getParameterNames(); parms.hasMoreElements();)
      {
         String key = parms.nextElement();
         mKeyValuesMap.put(key, req.getParameter(key));
         ++cnt;
      }
      if (cnt == 0)
      {
         log.warn("no parameters found in request");
      }
      if (getParameter(Constants.SRV_ACTION) == null)
      {
         log.error("no ACTION parm in request");
         printParms(req);
      }
   }

   public String getParameter(String key)
   {
      return mKeyValuesMap.get(key);
   }
   
   public String getQueryString()
   {
      StringBuilder strB = new StringBuilder();
      for (String key : mKeyValuesMap.keySet())
      {
         strB.append(key);
         strB.append('=');
         strB.append(mKeyValuesMap.get(key));
         strB.append('&');
      }
      return strB.toString();
   }
   
   private void printParms(HttpServletRequest req)
   {
      for (Enumeration<String> parms = req.getParameterNames(); parms.hasMoreElements();)
      {
         String key = parms.nextElement();
         log.info("parm=" + key + ", value=" + req.getParameter(key));
      }
   }

}
