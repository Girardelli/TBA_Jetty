package be.tba.business;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.mail.MailerSessionBean;
import be.tba.session.SessionParmsInf;
import be.tba.session.WebSession;
import be.tba.sqladapters.FileLocationSqlAdapter;
import be.tba.sqladapters.TaskSqlAdapter;
import be.tba.sqladapters.WorkOrderSqlAdapter;
import be.tba.sqldata.AccountCache;
import be.tba.sqldata.AccountEntityData;
import be.tba.sqldata.FileLocationData;
import be.tba.sqldata.TaskEntityData;
import be.tba.sqldata.WorkOrderData;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.timer.CallCalendar;

public class TaskBIzzLogic
{
   private static Logger log = LoggerFactory.getLogger(TaskBIzzLogic.class);
   private static String kMailBody = "Beste,<br><br>Wij hebben uw opdracht opgeleverd. U kan het opgeleverde werk downloaden van ons portaal na dat u u hebt aangemeld.<br><br>Vriendelijke groeten<br><br>Het TBA team";

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

   public static int saveWorkOrder(SessionParmsInf parms, WebSession session)
   {
      int id = 0;
      WorkOrderSqlAdapter vWorkOrderSession = new WorkOrderSqlAdapter();
      String idStr = parms.getParameter(Constants.WORKORDER_ID);
      String dueDate = null;
      if (parms.getParameter(Constants.WORKORDER_DUEDATE) != null)
         dueDate = parms.getParameter(Constants.WORKORDER_DUEDATE);
      if (dueDate != null && dueDate.contains("-"))
      {
         dueDate = CallCalendar.calendarStrTbaStr(dueDate);
      }

      if (idStr != null && !idStr.isEmpty() && !idStr.equals("0"))
      {
         // update entry
         WorkOrderData workorder = vWorkOrderSession.getRow(session, Integer.parseInt(idStr));
         if (parms.getParameter(Constants.WORKORDER_TITLE) != null)
            workorder.title = parms.getParameter(Constants.WORKORDER_TITLE);
         if (parms.getParameter(Constants.WORKORDER_INSTRUCTION) != null)
            workorder.instructions = parms.getParameter(Constants.WORKORDER_INSTRUCTION);
         workorder.isUrgent = (parms.getParameter(Constants.WORKORDER_URGENT) != null);
         if (dueDate != null)
            workorder.dueDate = dueDate;
         if ((session.getRole() == AccountRole.ADMIN || session.getRole() == AccountRole.EMPLOYEE) && parms.getParameter(Constants.WORKORDER_STATE) != null)
         {
            WorkOrderData.State oldState = workorder.state;
            workorder.state = WorkOrderData.StateStr2Enum(parms.getParameter(Constants.WORKORDER_STATE));
            if (workorder.state == WorkOrderData.State.kDone && oldState != workorder.state)
            {
               MailerSessionBean.sendMail(session, workorder.accountId, "Uw opdracht is opgeleverd", kMailBody);
            }
         }
         vWorkOrderSession.updateRow(session, workorder);
         TaskBIzzLogic.log.info("old idStr=" + idStr + ", " + workorder.toString());
         id = workorder.id;
      }
      else
      {
         // new entry
         WorkOrderData workorder = new WorkOrderData();
         AccountEntityData vAccountData = AccountCache.getInstance().get(session.getAccountId());
         workorder.accountId = vAccountData.getId();
         workorder.title = parms.getParameter(Constants.WORKORDER_TITLE);
         workorder.instructions = parms.getParameter(Constants.WORKORDER_INSTRUCTION);
         workorder.dueDate = dueDate;
         workorder.isUrgent = (parms.getParameter(Constants.WORKORDER_URGENT) != null);
         id = vWorkOrderSession.addRow(session, workorder);
         TaskBIzzLogic.log.info("new idStr=" + idStr + ", " + workorder.toString());

         MailerSessionBean.sendMail(session, 0, "Nieuwe opdracht van " + vAccountData.getFullName(), "");
      }
      session.setWorkOrderId(id);
      return id;
   }

   public static void setWorkOrderState(SessionParmsInf parms, WebSession session)
   {
      WorkOrderSqlAdapter vWorkOrderSession = new WorkOrderSqlAdapter();
      int workorderId = Integer.parseInt(parms.getParameter(Constants.WORKORDER_ID));
      WorkOrderData workorder = null;
      if (workorderId > 0)
      {
         workorder = vWorkOrderSession.getRow(session, workorderId);
         workorder.state = WorkOrderData.StateStr2Enum(parms.getParameter(Constants.WORKORDER_STATE));
         if (workorder.state == WorkOrderData.State.kDone && workorder.taskId == 0)
         {
            Calendar vCalendar = Calendar.getInstance();
            String date = String.format("%02d/%02d/%04d", vCalendar.get(Calendar.DAY_OF_MONTH), vCalendar.get(Calendar.MONTH), vCalendar.get(Calendar.YEAR));
            TaskSqlAdapter taskSession = new TaskSqlAdapter();
            TaskEntityData task = new TaskEntityData();
            task.setDoneBy(session.getUserId());
            task.setAccountId(workorder.accountId);
            task.setDescription(workorder.title);
            task.setDate(date);
            task.setTimeStamp(vCalendar.getTimeInMillis());
            workorder.taskId = taskSession.addRow(session, task);
         }
         vWorkOrderSession.updateRow(session, workorder);
      }
      else
      {
         log.error("setWorkOrderState failed because workorder.id = 0");
      }
   }

   public static void deleteWorkOrder(SessionParmsInf parms, WebSession session)
   {
      WorkOrderSqlAdapter vWorkOrderSession = new WorkOrderSqlAdapter();
      int workorderId = Integer.parseInt(parms.getParameter(Constants.WORKORDER_ID));
      WorkOrderData workorder = null;
      if (workorderId > 0)
      {
         workorder = vWorkOrderSession.getRow(session, workorderId);
         if (workorder != null)
         {
            FileLocationSqlAdapter fileLocationSession = new FileLocationSqlAdapter();
            Collection<FileLocationData> inputFiles = fileLocationSession.getInputFiles(session, workorderId);
            Collection<FileLocationData> outputFiles = fileLocationSession.getOutputFiles(session, workorderId);

            if (inputFiles.size() > 0 || outputFiles.size() > 0)
            {

            }
            vWorkOrderSession.deleteRow(session, workorderId);
         }
      }
      else
      {
         log.error("setWorkOrderState failed because workorder.id = 0");
      }
   }

   public static boolean addWorkOrderFile(SessionParmsInf parms, WebSession session, String fullFilePath)
   {
      FileLocationSqlAdapter fileLocationSession = new FileLocationSqlAdapter();
      int workorderId = saveWorkOrder(parms, session);

      if (workorderId > 0)
      {
         File file = new File(fullFilePath);

         FileLocationData fileData = new FileLocationData();
         fileData.size = (int) file.length() / 1000;
         fullFilePath = fullFilePath.replace('\\', '/');
         fileData.storagePath = fullFilePath;
         fileData.name = fullFilePath.substring(fullFilePath.lastIndexOf('/') + 1);
         fileData.workorderId = workorderId;
         if (session.getRole() == AccountRole.ADMIN || session.getRole() == AccountRole.EMPLOYEE)
         {
            fileData.inOrOut = FileLocationData.kOutput;
         }
         else
         {
            fileData.inOrOut = FileLocationData.kInput;
         }
         return (fileLocationSession.addRow(session, fileData) > 0);
      }
      else
      {
         log.error("addWorkOrderFile failed because workorder.id = 0");
      }
      return false;
   }

   public static boolean deleteWorkOrderFile(SessionParmsInf parms, WebSession session)
   {
      FileLocationSqlAdapter fileLocationSession = new FileLocationSqlAdapter();
      FileLocationData fileData = fileLocationSession.getRow(session, Integer.parseInt(parms.getParameter(Constants.WORKORDER_FILE_ID)));
      File file = new File(fileData.storagePath);
      boolean res = true;
      if (file.delete())
      {
         log.info(fileData.storagePath + " deleted successfully");
      }
      else
      {
         log.error(fileData.storagePath + " could not be deleted");
         res = false;
      }

      fileLocationSession.deleteRow(session, fileData.id);
      return res;
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
