package be.tba.test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class AnyTest
{
   final static Logger log = LoggerFactory.getLogger(AnyTest.class);

   public static void main(String[] args)
   {
//      testIntegerPrint();
//      testExceptionLogging();
      testLoginCodeParsing();
      long now = Calendar.getInstance().getTimeInMillis();
      log.info("now=" + now);
   }
   
   private static void testIntegerPrint()
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

      log.info("76372544342283L=" + (int)(a%6));
      log.info("76372544342284L=" + (int)(b%6));
      log.info("76372544342285L=" + (int) c%6);
      log.info("76372544342286L=" + (int) d%6);
      log.info("76372544342287L=" + (int) e%6);
      log.info("76372544342288L=" + (int) f%6);
      log.info("76372544342289L=" + (int) g%6);
      
      log.info("76372544342283L=" + kExtCall[(int)(a%6)]);
      log.info("76372544342284L=" + kExtCall[(int)(b%6)]);
      log.info("76372544342285L=" + kExtCall[(int) c%6]);
      log.info("76372544342286L=" + kExtCall[(int) d%6]);
      log.info("76372544342287L=" + kExtCall[(int) e%6]);
      log.info("76372544342288L=" + kExtCall[(int) f%6]);
      log.info("76372544342289L=" + kExtCall[(int) g%6]);
   }
   
   
   private static void testExceptionLogging()
   {
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
        //ex.printStackTrace();
    	  log.error(buffer.toString());
    	  
     }
      
      
   }
   
   
   private static void testLoginCodeParsing()
   {
      int [] accountIdArr = {1, 234, 9045, 2340 }; 
      
      Calendar calendar = Calendar.getInstance();
      
      for(int account : accountIdArr)
      {
         //log.info(String.format("%04d%02d%03d", account, calendar.get(Calendar.YEAR) - 2000, calendar.get(Calendar.DAY_OF_YEAR)));
         String loginNr = String.format("%04d%06d", account, calendar.getTimeInMillis()/1000/60/60);
         
         
         long code = Long.parseUnsignedLong(loginNr);
         long checkDigit = code % 7;
         loginNr = loginNr + checkDigit;
         log.info("entered:" + String.format("%04d/%06d/%d", account, calendar.getTimeInMillis()/1000/60/60, checkDigit));
         code = Long.parseUnsignedLong(loginNr);
        
         
         
         long checkTest = Long.parseUnsignedLong(loginNr.substring(10));
         long codeTest = Long.parseUnsignedLong(loginNr.substring(0, 10));
         
         if (codeTest % 7 != checkTest)
         {
            log.error("checksum failed: " + codeTest + ", " + checkTest);
         }
         
         
         String timeRetr = loginNr.substring(4, 10);
         String accountRetr = loginNr.substring(0, 4);
         log.info("retrieved:" + accountRetr + "/" + timeRetr);
         
         log.info("---------");
      }
      
      
      
   }
   
   private static String getInvoiceNumber(int year, int month, int seqNr)
   {
      Calendar calendar = Calendar.getInstance();

      
      log.info(String.format("%02d%03d", calendar.get(Calendar.YEAR) - 2000, calendar.get(Calendar.DAY_OF_YEAR)));
      return String.format("%02d%02d%04d", year - 2000, month + 1, seqNr);
   }
   
   
  
   public static long getCRC32Checksum(byte[] bytes) 
   {
       Checksum crc32 = new CRC32();
       crc32.update(bytes, 0, bytes.length);
       return crc32.getValue();
   }
   

}
