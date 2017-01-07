/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.timer;

import java.util.Calendar;

public class CallCalendar
{
   Calendar mCalendar;

   public CallCalendar()
   {
      mCalendar = Calendar.getInstance();
      // System.out.println("CallCalendar: current time=" +
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
      // System.out.println("getStartOfMonth: month " + month);

      Calendar vStartCalendar = Calendar.getInstance();
      int year = vStartCalendar.get(Calendar.YEAR);
      int month = vStartCalendar.get(Calendar.MONTH);
      int day = vStartCalendar.get(Calendar.DAY_OF_MONTH);
      vStartCalendar.clear();
      vStartCalendar.set(year, month, day);
      // System.out.println("getStartOfMonth end : year " + year);
      return vStartCalendar.getTimeInMillis();
   }

   public long getStartOfMonth(int month, int year)
   {
      // System.out.println("getStartOfMonth: month " + month);

      Calendar vStartCalendar = Calendar.getInstance();
      vStartCalendar.clear();
      vStartCalendar.set(year, month, 1);
      // System.out.println("getStartOfMonth end : year " + year);
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

   public long getDaysBack(int daysBack)
   {
      int vCurrentYear = mCalendar.get(Calendar.YEAR);
      int vDay = mCalendar.get(Calendar.DATE);
      int vMonth = mCalendar.get(Calendar.MONTH);
      //int vDayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);

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
      // System.out.println("getDaysBack(" + daysBack + "): vCurrentYear = " +
      // vCurrentYear + "; vDay = " + vDay + "; vMonth = " + vMonth +
      // ", miliseconds=" + vStartCalendar.getTimeInMillis());
      return vStartCalendar.getTimeInMillis();
   }

}
