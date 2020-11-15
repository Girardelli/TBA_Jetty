package be.tba.business;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.mail.Mailer;
import be.tba.session.SessionParmsInf;
import be.tba.session.WebSession;
import be.tba.sqladapters.CallRecordSqlAdapter;
import be.tba.sqladapters.FileLocationSqlAdapter;
import be.tba.sqladapters.TaskSqlAdapter;
import be.tba.sqladapters.WorkOrderSqlAdapter;
import be.tba.sqldata.AccountCache;
import be.tba.sqldata.AccountEntityData;
import be.tba.sqldata.CallRecordEntityData;
import be.tba.sqldata.FileLocationData;
import be.tba.sqldata.TaskEntityData;
import be.tba.sqldata.WorkOrderData;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.timer.CallCalendar;

public class WorkorderBizzLogic
{
   private static Logger log = LoggerFactory.getLogger(WorkorderBizzLogic.class);
   private static String kMailBody = "Beste,<br><br>Wij hebben uw opdracht opgeleverd. U kan het opgeleverde werk downloaden van ons portaal na dat u u hebt aangemeld.<br><br>Vriendelijke groeten<br><br>Het TBA team";

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
               Mailer.sendMail(session, workorder.accountId, "Uw opdracht is opgeleverd", kMailBody);
            }
         }
         vWorkOrderSession.updateRow(session, workorder);
         log.info("old idStr=" + idStr + ", " + workorder.toString());
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
         log.info("new idStr=" + idStr + ", " + workorder.toString());

         Mailer.sendMail(session, 0, "Nieuwe opdracht van " + vAccountData.getFullName(), "");
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
   
   public static void archiveWorkorders(SessionParmsInf parms, WebSession session)
   {
      String vLtd = parms.getParameter(Constants.RECORDS_TO_HANDLE);
      log.info("workorders to archive list: " + vLtd);
      if (vLtd != null && vLtd.length() > 0)
      {
         StringTokenizer vStrTok = new StringTokenizer(vLtd, ",");
         WorkOrderSqlAdapter vWorkOrderSession = new WorkOrderSqlAdapter();
         StringBuilder inStrBuf = new StringBuilder();

         while (vStrTok.hasMoreTokens())
         {
            int key = Integer.parseInt(vStrTok.nextToken());
            inStrBuf.append(key);
            inStrBuf.append(",");
         }
         if (inStrBuf.length() > 0) 
         {
            inStrBuf.deleteCharAt(inStrBuf.length() - 1);
         }
         vWorkOrderSession.archive(session, inStrBuf.toString());
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

}
