package be.tba.ejb.task.session;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import be.tba.ejb.task.interfaces.WorkOrderData;
import be.tba.servlets.session.WebSession;
import be.tba.util.data.AbstractSqlAdapter;

public class WorkOrderSqlAdapter  extends AbstractSqlAdapter<WorkOrderData>
{

   public WorkOrderSqlAdapter()
   {
      super("WorkOrderEntity");
      // TODO Auto-generated constructor stub
   }

   public Collection<WorkOrderData> getOpenList(WebSession webSession)
   {
      return executeSqlQuery(webSession, "SELECT * FROM WorkOrderEntity WHERE State!='" + WorkOrderData.State.kArchived.name() + "' ORDER BY Id DESC");
   }

   public Collection<WorkOrderData> getListForAccount(WebSession webSession, int accountId, boolean includeArchived)
   {
      if (includeArchived)
      {
         return executeSqlQuery(webSession, "SELECT * FROM WorkOrderEntity WHERE AccountId=" + accountId + " ORDER BY Id DESC");
      }
      else
      {
         return executeSqlQuery(webSession, "SELECT * FROM WorkOrderEntity WHERE AccountId=" + accountId + " AND State!='" + WorkOrderData.State.kArchived.name() + "' ORDER BY Id DESC");
      }
      
   }

   
   
   @Override
   protected Vector<WorkOrderData> translateRsToValueObjects(ResultSet rs) throws SQLException
   {
      Vector<WorkOrderData> vVector = new Vector<WorkOrderData>();
      while (rs.next())
      {
         WorkOrderData entry = new WorkOrderData();
         
         entry.id = rs.getInt(1);
         entry.accountId = rs.getInt(2);
         entry.taskId = rs.getInt(3);
         entry.title = null2EmpthyString(rs.getString(4));
         entry.instructions = null2EmpthyString(rs.getString(5));
         entry.dueDate = null2EmpthyString(rs.getString(6));
         entry.state = WorkOrderData.StateStr2Enum(null2EmpthyString(rs.getString(7)));
          vVector.add(entry);
      }
      return vVector;
   }

}
