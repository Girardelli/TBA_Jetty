package be.tba.sqladapters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.business.WorkorderBizzLogic;
import be.tba.session.WebSession;
import be.tba.sqldata.WorkOrderData;

public class WorkOrderSqlAdapter extends AbstractSqlAdapter<WorkOrderData>
{
   private static Logger log = LoggerFactory.getLogger(WorkOrderSqlAdapter.class);

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
   
   public void archive(WebSession webSession, String idString)
   {
      executeSqlQuery(webSession, "UPDATE WorkOrderEntity set State='" + WorkOrderData.State.kArchived.name() + "' WHERE Id IN (" + idString + ")");
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
         entry.startDate = null2EmpthyString(rs.getString(6));
         entry.dueDate = null2EmpthyString(rs.getString(7));
         entry.state = WorkOrderData.StateStr2Enum(null2EmpthyString(rs.getString(8)));
         entry.isUrgent = rs.getBoolean(9);
         vVector.add(entry);
      }
      return vVector;
   }

}
