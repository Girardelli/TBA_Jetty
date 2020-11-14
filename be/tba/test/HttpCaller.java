package be.tba.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.util.constants.Constants;

public class HttpCaller
{
   final static Logger log = LoggerFactory.getLogger(HttpCaller.class);
   private static String kUrl = "http://localhost:8080/tba/AdminLogin";//   ?_uid=esosrv";

   public static void main(String[] args)
   {
      try
      {
         login("esosrv", "");
//         URL url = new URL(kUrl);
//         HttpURLConnection con = (HttpURLConnection) url.openConnection();
//         con.setRequestProperty("Content-Type", "text/html");
//         con.setConnectTimeout(5000);
//         con.setReadTimeout(5000);
//         con.setRequestMethod("POST");

//         Map<String, String> parameters = new HashMap<>();
//         parameters.put("_uid", "esosrv");
//         con.setDoOutput(true);
//         DataOutputStream out = new DataOutputStream(con.getOutputStream());
//         out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//         out.flush();
//         out.close();
         
//         log.info("ready to go:" + con.getURL());
         
//         int status = con.getResponseCode();
//         StringBuffer contentBuf = getContent(con);
//         log.info("response: " + contentBuf.toString());
//         con.disconnect();

//         log.info("done without errors: " + status);

      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   
   
   
   
   
   static private String login(String userId, String password) throws IOException
   {
      URL url = new URL("http://localhost:8080/tba/AdminLogin?_uid=" + userId + "&_pwd=" + password);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestProperty("Content-Type", "text/html");
      con.setConnectTimeout(5000);
      con.setReadTimeout(5000);
      con.setRequestMethod("POST");
      if (con.getResponseCode() != 200)
         log.error("login failed");
      StringBuffer contentBuf = getContent(con);
      log.info("response: " + contentBuf.toString());

      
      
      //log.info("Object: " + con. .getHeaderField("_sobj"));
      
      Map<String, List<String>> responseMap = con.getRequestProperties();//.getHeaderFields();
      for (String key : responseMap.keySet())
      {
         log.info(key);
         List<String> values = responseMap.get(key);
         if (values != null)
         {
            for (String value : responseMap.get(key))
            {
               log.info("\t value:" + value); 
            }
            
         }
      }
      
      
      con.disconnect();
      return "";
      
   }
   
   
   
   
   
   static class ParameterStringBuilder
   {
      public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException
      {
         StringBuilder result = new StringBuilder();
         result.append("?");
         for (Map.Entry<String, String> entry : params.entrySet())
         {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
         }

         String resultString = result.toString();
         return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
      }
   }
   
   static private StringBuffer getContent(HttpURLConnection con)
   {
      BufferedReader in;
      StringBuffer content = new StringBuffer();
      try
      {
         in = new BufferedReader(new InputStreamReader(con.getInputStream()));
         String inputLine;
         
         while ((inputLine = in.readLine()) != null)
         {
            content.append(inputLine);
         }
         in.close();
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return content;
   }
}
