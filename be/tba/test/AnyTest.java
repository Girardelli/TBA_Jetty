package be.tba.test;

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

   }
   
   
   
   
   

}
