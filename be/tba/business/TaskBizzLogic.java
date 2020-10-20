package be.tba.business;

import java.util.Calendar;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.session.SessionParmsInf;
import be.tba.session.WebSession;
import be.tba.sqladapters.TaskSqlAdapter;
import be.tba.sqldata.AccountCache;
import be.tba.sqldata.AccountEntityData;
import be.tba.sqldata.TaskEntityData;
import be.tba.util.constants.Constants;

public class TaskBizzLogic
{
   private static Logger log = LoggerFactory.getLogger(TaskBizzLogic.class);

   public static void deleteTask(SessionParmsInf parms, WebSession session)
   {
      String vLtd = parms.getParameter(Constants.TASK_TO_DELETE);
      StringTokenizer vStrTok = new StringTokenizer(vLtd, ",");

      TaskSqlAdapter vTaskSession = new TaskSqlAdapter();

      while (vStrTok.hasMoreTokens())
      {
         vTaskSession.removeTask(session, Integer.parseInt(vStrTok.nextToken()));
      }
   }

   public static void modifyTask(SessionParmsInf parms, WebSession session)
   {
      int vKey = Integer.parseInt(parms.getParameter(Constants.TASK_ID));

      TaskSqlAdapter vTaskSession = new TaskSqlAdapter();
      session.setCurrentTask(vTaskSession.getTask(session, vKey));
      session.getCallFilter().setCustFilter(parms.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
   }

   public static void saveTask(SessionParmsInf parms, WebSession session)
   {
      int vId = session.getCurrentTask().getId();
      TaskSqlAdapter vTaskSession = new TaskSqlAdapter();

      TaskEntityData vTask = vTaskSession.getTask(session, vId);
      if (vTask != null)
      {
         String vNewDate = parms.getParameter(Constants.TASK_DATE);

         if (!vTask.getDate().equals(vNewDate))
         {
            if (vTask.getIsRecuring())
            {
               vTask.setStartTime(dateStr2Timestamp(vNewDate));
            }
            else
            {
               vTask.setTimeStamp(dateStr2Timestamp(vNewDate));
            }
         }
         AccountEntityData account = AccountCache.getInstance().get(Integer.parseInt(parms.getParameter(Constants.TASK_ACCOUNT_ID)));
         String newFwdNr = account.getFwdNumber();
         if (newFwdNr != null && !newFwdNr.equals(vTask.getFwdNr()))
         {
            vTask.setFwdNr(newFwdNr);
            vTask.setAccountId(account.getId());
         }
         account = AccountCache.getInstance().get(vTask.getFwdNr());
         vTask.setDoneBy(parms.getParameter(Constants.TASK_DONE_BY_EMPL));
         vTask.setDate(parms.getParameter(Constants.TASK_DATE));
         vTask.setDescription(parms.getParameter(Constants.TASK_DESCRIPTION));
         String tmp = parms.getParameter(Constants.TASK_TIME_SPEND);
         if (tmp == null || tmp.isBlank())
         {
            tmp = "0";
         }
         vTask.setTimeSpend(Integer.parseInt(tmp));
         if (parms.getParameter(Constants.TASK_IS_FIXED_PRICE) != null)
         {
            vTask.setIsFixedPrice(true);
            if (parms.getParameter(Constants.TASK_FIXED_PRICE) != null)
            {
               String vFixedPrice = parms.getParameter(Constants.TASK_FIXED_PRICE);
               vFixedPrice = vFixedPrice.replace(',', '.');
               vTask.setFixedPrice(Double.parseDouble(vFixedPrice));
//                    log.info("task price after set=" + newTask.getFixedPrice());
            }
            else
            {
               vTask.setFixedPrice(0.0);
            }
         }
         else
         {
            vTask.setIsFixedPrice(false);
            vTask.setFixedPrice(((double) vTask.getTimeSpend() / 60.00) * ((double) account.getTaskHourRate() / 100.00));
         }
         if (vTask.getIsRecuring() && parms.getParameter(Constants.TASK_IS_RECURING) == null)
         {
            // stop recuring task
            log.info("stop recuring task");

            Calendar mCalendar = Calendar.getInstance();
            vTask.setStopTime(mCalendar.getTimeInMillis());
         }
         else if (!vTask.getIsRecuring() && parms.getParameter(Constants.TASK_IS_RECURING) != null)
         {
            // start recuring task
            log.info("start recuring task");
            Calendar mCalendar = Calendar.getInstance();

            vTask.setStartTime(mCalendar.getTimeInMillis());
            vTask.setStopTime(Long.MAX_VALUE);
         }
      }
      vTaskSession.updateRow(session, vTask);
      session.getCallFilter().setCustFilter(parms.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
   }

   public static void addTask(SessionParmsInf parms, WebSession session)
   {
      TaskEntityData newTask = new TaskEntityData();
      // newTask.setFwdNr(parms.getParameter(Constants.TASK_ACCOUNT_ID));
      AccountEntityData account = AccountCache.getInstance().get(Integer.parseInt(parms.getParameter(Constants.TASK_ACCOUNT_ID)));
      newTask.setAccountId(account.getId());
      newTask.setFwdNr(account.getFwdNumber());
      newTask.setDoneBy(parms.getParameter(Constants.TASK_DONE_BY_EMPL));
      newTask.setDate(parms.getParameter(Constants.TASK_DATE));
      newTask.setTimeStamp(dateStr2Timestamp(newTask.getDate()));

      newTask.setDescription(parms.getParameter(Constants.TASK_DESCRIPTION));
      String vTimeSpend = parms.getParameter(Constants.TASK_TIME_SPEND);
      if (vTimeSpend != null && vTimeSpend.length() > 0)
         newTask.setTimeSpend(Integer.parseInt(parms.getParameter(Constants.TASK_TIME_SPEND)));
      else
         newTask.setTimeSpend(0);
      if (parms.getParameter(Constants.TASK_IS_FIXED_PRICE) != null)
      {
         newTask.setIsFixedPrice(true);
         if (parms.getParameter(Constants.TASK_FIXED_PRICE) != null)
         {
            String vFixedPrice = parms.getParameter(Constants.TASK_FIXED_PRICE);
            vFixedPrice = vFixedPrice.replace(',', '.');
            newTask.setFixedPrice(Double.parseDouble(vFixedPrice));
//                log.info("task price after set=" + newTask.getFixedPrice());
         }
         else
         {
            newTask.setFixedPrice(0.0);
         }
      }
      else
      {
         newTask.setIsFixedPrice(false);
         newTask.setFixedPrice(((double) newTask.getTimeSpend() / 60.00) * ((double) account.getTaskHourRate() / 100.00));
      }
      newTask.setIsRecuring(parms.getParameter(Constants.TASK_IS_RECURING) != null);
      if (newTask.getIsRecuring())
      {
         Calendar mCalendar = Calendar.getInstance();

         newTask.setStartTime(mCalendar.getTimeInMillis());
         newTask.setStopTime(Long.MAX_VALUE);
         newTask.setTimeStamp(0);
      }
      else
      {
         newTask.setStartTime(0);
         newTask.setStopTime(0);
      }
      TaskSqlAdapter vTaskSession = new TaskSqlAdapter();
      vTaskSession.addRow(session, newTask);
   }
   

   /* expectes a date like this: dd/mm/yyyy */
   private static long dateStr2Timestamp(String date)
   {
      Calendar vCalendar = Calendar.getInstance();
      try
      {
         int vIndex = date.indexOf('/');
         String vDayString = date.substring(0, vIndex);
         int vDay = Integer.parseInt(vDayString);
         int vIndex_2 = date.indexOf('/', ++vIndex);
         String vMonthString = date.substring(vIndex, vIndex_2);
         int vMonth = Integer.parseInt(vMonthString) - 1;
         int vYear = Integer.parseInt(date.substring(++vIndex_2));
         if (vYear < 2000)
         {
            vYear += 2000;
         }
         vCalendar.set(vYear, vMonth, vDay);
      }
      catch (Exception ex)
      {
         log.error(ex.getMessage(), ex);
         // the now date shall be returned
      }
      return vCalendar.getTimeInMillis();
   }
}
