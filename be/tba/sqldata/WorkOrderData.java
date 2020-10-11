package be.tba.sqldata;

import java.util.Calendar;

import be.tba.util.constants.Constants;

public class WorkOrderData extends be.tba.sqldata.AbstractData
{
   /**
    * 
    */
   private static final long serialVersionUID = -7869155937316442608L;

   static public enum State
   {
      kSubmitted, kBusy, kDone, kArchived
   };

   static public State StateStr2Enum(String str)
   {
      for (State state : State.values())
      {
         if (state.name().equals(str))
            return state;
      }
      return State.kSubmitted;
   }

   static public String getStateStr(State state)
   {
      switch (state)
      {
      case kSubmitted:
         return "Nieuw";
      case kBusy:
         return "Bezig";
      case kDone:
         return "Opgeleverd";
      case kArchived:
         return "Gearchiveerd";
      default:
         break;
      }
      return "??";
   }

   public int id;
   public int accountId;
   public int taskId;
   public String title;
   public String instructions;
   public String startDate;
   public String dueDate;
   public State state;
   public boolean isUrgent;

   public WorkOrderData()
   {
      Calendar vToday = Calendar.getInstance();
      id = 0;
      accountId = 0;
      taskId = 0;
      title = "";
      instructions = "";
      startDate = new String(vToday.get(Calendar.DAY_OF_MONTH) + " " + Constants.MONTHS[vToday.get(Calendar.MONTH)] + " " + vToday.get(Calendar.YEAR));
      dueDate = "";
      state = State.kSubmitted;
      isUrgent = false;
   }

   public WorkOrderData(WorkOrderData otherData)
   {
      id = otherData.id;
      accountId = otherData.accountId;
      taskId = otherData.taskId;
      title = otherData.title;
      instructions = otherData.instructions;
      startDate = otherData.startDate;
      dueDate = otherData.dueDate;
      state = otherData.state;
      isUrgent = otherData.isUrgent;
   }

   public int getId()
   {
      return this.id;
   }

   public String toNameValueString()
   {
      StringBuffer str = new StringBuffer();

      str.append("AccountId=");
      str.append(accountId);
      str.append(",TaskId=");
      str.append(taskId);
      str.append(",Title='");
      str.append((this.title != null) ? escapeQuotes(this.title) : "");
      str.append("',Instructions='");
      str.append((this.instructions != null) ? escapeQuotes(this.instructions) : "");
      str.append("',StartDate='");
      str.append((this.startDate != null) ? this.startDate : "");
      str.append("',DueDate='");
      str.append((this.dueDate != null) ? this.dueDate : "");
      str.append("',State='");
      str.append(state.name());
      str.append("',IsUrgent=");
      str.append(isUrgent);
      return (str.toString());
   }

   public String toValueString()
   {
      StringBuffer str = new StringBuffer();

      // "(1, '409031', '04/10/05', 1128528272192, 1, 220, 0, 'Nabelactie voor
      // client'. ',0 ,0 ,0 ,0 ,'', 0)
      str.append("0,");
      str.append(accountId);
      str.append(",");
      str.append(taskId);
      str.append(",'");
      str.append((this.title != null) ? escapeQuotes(this.title) : "");
      str.append("','");
      str.append((this.instructions != null) ? escapeQuotes(this.instructions) : "");
      str.append("','");
      str.append((this.startDate != null) ? this.startDate : "");
      str.append("','");
      str.append((this.dueDate != null) ? this.dueDate : "");
      str.append("','");
      str.append(state.name());
      str.append("',");
      str.append(isUrgent);
      return (str.toString());
   }

   public boolean equals(Object pOther)
   {
      if (pOther instanceof WorkOrderData)
      {
         WorkOrderData lTest = (WorkOrderData) pOther;
         boolean lEquals = true;

         lEquals = lEquals && this.id == lTest.id;
         lEquals = lEquals && this.accountId == lTest.accountId;
         return lEquals;
      }
      else
      {
         return false;
      }
   }

   public int hashCode()
   {
      int result = 17;
      result = 37 * result + (int) id;
      result = 37 * result + (int) accountId;
      return result;
   }

}
