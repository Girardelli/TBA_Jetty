/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.timer;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.servlets.FileDownloadServlet;

public class CallCalendar
{
   private static Logger log = LoggerFactory.getLogger(CallCalendar.class);

   private Calendar mCalendar;

   public CallCalendar()
   {
      mCalendar = Calendar.getInstance();
      // log.info("CallCalendar: current time=" +
      // mCalendar.getTimeInMillis());
   }

   public Calendar getWrappedCalendar()
   {
      return mCalendar;
   }

   public long getCurrentTimestamp()
   {
      return mCalendar.getTimeInMillis();
   }

   public long getStartOfToday()
   {
      // log.info("getStartOfMonth: month " + month);

      Calendar vStartCalendar = Calendar.getInstance();
      int year = vStartCalendar.get(Calendar.YEAR);
      int month = vStartCalendar.get(Calendar.MONTH);
      int day = vStartCalendar.get(Calendar.DAY_OF_MONTH);
      vStartCalendar.clear();
      vStartCalendar.set(year, month, day);
      // log.info("getStartOfMonth end : year " + year);
      return vStartCalendar.getTimeInMillis();
   }

   public long getStartOfMonth(int month, int year)
   {
      // log.info("getStartOfMonth: month " + month);

      Calendar vStartCalendar = Calendar.getInstance();
      vStartCalendar.clear();
      vStartCalendar.set(year, month, 1);
      // log.info("getStartOfMonth end : year " + year);
      return vStartCalendar.getTimeInMillis();
   }

   public long getEndOfMonth(int month, int year)
   {
      if (month == Calendar.DECEMBER)
      {
         ++year;
         month = Calendar.JANUARY;
      }
      else
      {
         ++month;
      }
      Calendar vEndCalendar = Calendar.getInstance();
      vEndCalendar.clear();
      vEndCalendar.set(year, month, 1);
      return vEndCalendar.getTimeInMillis();
   }

   public Calendar getDaysBack(int daysBack)
   {
      int vCurrentYear = mCalendar.get(Calendar.YEAR);
      int vDay = mCalendar.get(Calendar.DATE);
      int vMonth = mCalendar.get(Calendar.MONTH);
      // int vDayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);

      // System.out.print( "getDaysBack(" + daysBack + ") vCurrentDay = " +
      // vDay);

      if (vDay <= daysBack)
      {
         if (vMonth == Calendar.JANUARY)
         {
            vMonth = Calendar.DECEMBER;
            --vCurrentYear;
         }
         else
            --vMonth;

         int vDaysInMonth = 31;
         switch (vMonth)
         {
         case Calendar.APRIL:
         case Calendar.JUNE:
         case Calendar.SEPTEMBER:
         case Calendar.NOVEMBER:
            vDaysInMonth = 30;
            break;

         case Calendar.FEBRUARY:
            if (vCurrentYear / 4 == 0)
               vDaysInMonth = 29;
            else
               vDaysInMonth = 28;
            break;
         }
         vDay = vDaysInMonth - (daysBack - vDay);
      }
      else
      {
         vDay -= daysBack;
      }

      Calendar vStartCalendar = Calendar.getInstance();
      vStartCalendar.clear();
      vStartCalendar.set(vCurrentYear, vMonth, vDay);
      // log.info("getDaysBack(" + daysBack + "): vCurrentYear = " +
      // vCurrentYear + "; vDay = " + vDay + "; vMonth = " + vMonth +
      // ", miliseconds=" + vStartCalendar.getTimeInMillis());
      //return vStartCalendar.getTimeInMillis();
      return vStartCalendar;
   }

   /*
    * str must have format dd/mm/yyyy This format is used overall in this
    * application
    * 
    */
   static public Calendar str2Calendar(String str)
   {
      log.info(str);
      int firstSlash = str.indexOf('/');
      if (firstSlash >= 0)
      {
         int secondSlash = str.lastIndexOf('/');
         int day = Integer.parseInt(str.substring(0, firstSlash));
         int month = Integer.parseInt(str.substring(firstSlash + 1, secondSlash));
         int year = Integer.parseInt(str.substring(secondSlash + 1));

         log.info(String.format("%s: %d %d %d", str, day, month, year));
         Calendar cal = Calendar.getInstance();
         cal.clear();
         cal.set(year, month - 1, day);

         return cal;
      }
      log.error("Unexpected str format: " + str);
      return Calendar.getInstance();
   }

   /*
    * str must have format yyyy-mm-dd and convert it to dd/mm/yyyy.
    * This format is used overall in this application
    * 
    */
   static public String calendarStrTbaStr(String str)
   {
      int firstSlash = str.indexOf('-');
      if (firstSlash >= 0)
      {
         int secondSlash = str.lastIndexOf('-');
         int year = Integer.parseInt(str.substring(0, firstSlash));
         int month = Integer.parseInt(str.substring(firstSlash + 1, secondSlash));
         int day = Integer.parseInt(str.substring(secondSlash + 1));

         log.info(String.format("%s: %d %d %d", str, day, month, year));
         return String.format("%02d/%02d/%04d", day, month, year);
      }
      log.error("Unexpected str format: " + str);
      return str;
   }
}
