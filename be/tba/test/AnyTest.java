package be.tba.test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class AnyTest
{
   final static Logger log = LoggerFactory.getLogger(AnyTest.class);

   public static void main(String[] args)
   {
      // TODO Auto-generated method stub
      
      StringBuffer formattedStr = new StringBuffer("");
      String rawStr = "520505213";
      
      char[] rawArr  = rawStr.toCharArray();
      char[] noCharsArr = new char[rawArr.length];
      int i = 0;
      int y = 0;

      for(char c : rawArr)
      {
          int temp = (int) c;
          if (temp >= 48 && temp <= 57)
          {
              noCharsArr[i-y] =  rawArr[i];
          }
          else
          {
              ++y;
          }
          ++i;
      }
      String noCharsStr = new String(noCharsArr);
      noCharsStr = noCharsStr.substring(0, i-y);
      if (noCharsStr.length() > 10)
      {
          log.error("string length (" + noCharsStr.length()  + ") must have 9 or 10 chars: " + noCharsStr);
      }
      else if (noCharsStr.length() == 9)
      {
         noCharsStr = "0" + noCharsStr;
      }
      else
      {
         log.error("string length (" + noCharsStr.length()  + ") must have 9 or 10 chars: " + noCharsStr);
      }
      i = 0;
      if (noCharsStr.indexOf('0') == 0)
      {
          ++i;
      }
      formattedStr.append('0');
      formattedStr.append(noCharsStr.substring(i, i + 3));
      formattedStr.append('.');
      formattedStr.append(noCharsStr.substring(i + 3, i + 6));
      formattedStr.append('.');
      formattedStr.append(noCharsStr.substring(i + 6, i + 9));

      log.info("input: " + rawStr + " --> output: " + formattedStr.toString());

      final int kExtCall[] = {15, 16, 17, 18, 19, 20 };
      long a = 6372544342283L;
      long b = 6372544342284L;
      long c = 6372544342285L;
      long d = 6372544342286L;
      long e = 6372544342287L;
      long f = 6372544342288L;
      long g = 6372544342289L;

//      log.info("76372544342283L=" + (int)(a%6));
//      log.info("76372544342284L=" + (int)(b%6));
//      log.info("76372544342285L=" + (int) c%6);
//      log.info("76372544342286L=" + (int) d%6);
//      log.info("76372544342287L=" + (int) e%6);
//      log.info("76372544342288L=" + (int) f%6);
//      log.info("76372544342289L=" + (int) g%6);
//      
//      log.info("76372544342283L=" + kExtCall[(int)(a%6)]);
//      log.info("76372544342284L=" + kExtCall[(int)(b%6)]);
//      log.info("76372544342285L=" + kExtCall[(int) c%6]);
//      log.info("76372544342286L=" + kExtCall[(int) d%6]);
//      log.info("76372544342287L=" + kExtCall[(int) e%6]);
//      log.info("76372544342288L=" + kExtCall[(int) f%6]);
//      log.info("76372544342289L=" + kExtCall[(int) g%6]);
      
      try
      {
    	  throw new Exception("dit is mijn exception");
      }
      catch (Exception ex)
      {
    	  log.info("\\r\\n\\r\\n----------------------------------------------");
    	  log.error(ex.getMessage(), ex);
    	  log.info("\\r\\n\\r\\n----------------------------------------------");
    	  log.error(ex.getMessage(), ex);
    	  log.info("\\r\\n\\r\\n----------------------------------------------");
    	  Writer buffer = new StringWriter();
    	  PrintWriter pw = new PrintWriter(buffer);
    	  ex.printStackTrace(pw);
    	  log.error(buffer.toString());
     }
      
      
   }
   
   
   
   
   

}
