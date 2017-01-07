package be.tba.servlets.helper;

import java.util.Calendar;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import be.tba.ejb.task.interfaces.TaskEntityData;
import be.tba.ejb.task.session.TaskSqlAdapter;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;

public class TaskFacade
{
   public static void deleteTask(HttpServletRequest req, WebSession session)
   {
      String vLtd = (String) req.getParameter(Constants.TASK_TO_DELETE);
      StringTokenizer vStrTok = new StringTokenizer(vLtd, ",");

      TaskSqlAdapter vTaskSession = new TaskSqlAdapter();

      while (vStrTok.hasMoreTokens())
      {
         vTaskSession.removeTask(session, Integer.parseInt(vStrTok.nextToken()));
      }
   }

   public static void modifyTask(HttpServletRequest req, WebSession session)
   {
      int vKey = Integer.parseInt((String) req.getParameter(Constants.TASK_ID));

      TaskSqlAdapter vTaskSession = new TaskSqlAdapter();
      session.setCurrentTask(vTaskSession.getTask(session, vKey));
      if (req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER) != null)
      {
         session.getCallFilter().setCustFilter((String) req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
      }
   }

   public static void saveTask(HttpServletRequest req, WebSession session) 
   {
      int vId = session.getCurrentTask().getId();
      TaskSqlAdapter vTaskSession = new TaskSqlAdapter();

      TaskEntityData vTask = vTaskSession.getTask(session, vId);
      if (vTask != null)
      {
         String vNewDate = (String) req.getParameter(Constants.TASK_DATE);

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

         vTask.setFwdNr((String) req.getParameter(Constants.TASK_FORWARD_NUMBER));
         vTask.setDoneBy((String) req.getParameter(Constants.DONE_BY_EMPL));
         vTask.setDate((String) req.getParameter(Constants.TASK_DATE));
         vTask.setDescription((String) req.getParameter(Constants.TASK_DESCRIPTION));
         String tmp = (String) req.getParameter(Constants.TASK_TIME_SPEND);
         if (tmp == null || tmp == "")
            tmp = "0";
         vTask.setTimeSpend(Integer.parseInt(tmp));
         if (req.getParameter(Constants.TASK_IS_FIXED_PRICE) != null)
         {
            vTask.setIsFixedPrice(true);
            String vFixedPrice = (String) req.getParameter(Constants.TASK_FIXED_PRICE);
            vFixedPrice = vFixedPrice.replace(',', '.');
            vTask.setFixedPrice(Double.parseDouble(vFixedPrice));
         }
         else
         {
            vTask.setIsFixedPrice(false);
            vTask.setFixedPrice(0.0);
         }

         if (vTask.getIsRecuring() && req.getParameter(Constants.TASK_IS_RECURING) == null)
         {
            // stop recuring task
            Calendar mCalendar = Calendar.getInstance();
            vTask.setStopTime(mCalendar.getTimeInMillis());
         }
         else if (!vTask.getIsRecuring() && req.getParameter(Constants.TASK_IS_RECURING) != null)
         {
            // start recuring task
            Calendar mCalendar = Calendar.getInstance();

            vTask.setStartTime(mCalendar.getTimeInMillis());
            vTask.setStopTime(Long.MAX_VALUE);
         }
      }
      vTaskSession.updateRow(session.getConnection(), vTask);
      if (req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER) != null)
         session.getCallFilter().setCustFilter((String) req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
   }

   public static void addTask(HttpServletRequest req, WebSession session) 
   {
      TaskEntityData newTask = new TaskEntityData();
      newTask.setFwdNr((String) req.getParameter(Constants.TASK_FORWARD_NUMBER));
      newTask.setDoneBy((String) req.getParameter(Constants.DONE_BY_EMPL));
      newTask.setDate((String) req.getParameter(Constants.TASK_DATE));
      newTask.setTimeStamp(dateStr2Timestamp(newTask.getDate()));

      newTask.setDescription((String) req.getParameter(Constants.TASK_DESCRIPTION));
      String vTimeSpend = (String) req.getParameter(Constants.TASK_TIME_SPEND);
      if (vTimeSpend != null && vTimeSpend.length() > 0)
         newTask.setTimeSpend(Integer.parseInt((String) req.getParameter(Constants.TASK_TIME_SPEND)));
      else
         newTask.setTimeSpend(0);
      if (req.getParameter(Constants.TASK_IS_FIXED_PRICE) != null)
      {
         newTask.setIsFixedPrice(true);
         if (req.getParameter(Constants.TASK_FIXED_PRICE) != null)
         {
            String vFixedPrice = (String) req.getParameter(Constants.TASK_FIXED_PRICE);

            System.out.println("task price=" + vFixedPrice);

            vFixedPrice = vFixedPrice.replace(',', '.');
            newTask.setFixedPrice(Double.parseDouble(vFixedPrice));
            System.out.println("task price after set=" + newTask.getFixedPrice());
         }
         else
            newTask.setFixedPrice(0.0);
      }
      newTask.setIsRecuring(req.getParameter(Constants.TASK_IS_RECURING) != null);
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
      vTaskSession.addRow(session.getConnection(), newTask);
   }

   private static long dateStr2Timestamp(String date)
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
      Calendar vCalendar = Calendar.getInstance();
      vCalendar.set(vYear, vMonth, vDay);
      return vCalendar.getTimeInMillis();
   }
}
