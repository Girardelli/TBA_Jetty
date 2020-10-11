package be.tba.sqladapters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import be.tba.session.WebSession;
import be.tba.sqldata.FileLocationData;

public class FileLocationSqlAdapter extends AbstractSqlAdapter<FileLocationData>
{

   public FileLocationSqlAdapter()
   {
      super("FileLocationEntity");
      // TODO Auto-generated constructor stub
   }

   public Collection<FileLocationData> getInputFiles(WebSession webSession, int workOrderId)
   {
      return executeSqlQuery(webSession, "SELECT * FROM FileLocationEntity WHERE workOrderId=" + workOrderId + " AND InOrOut=" + FileLocationData.kInput);
   }

   public Collection<FileLocationData> getOutputFiles(WebSession webSession, int workOrderId)
   {
      return executeSqlQuery(webSession, "SELECT * FROM FileLocationEntity WHERE workOrderId=" + workOrderId + " AND InOrOut=" + FileLocationData.kOutput);
   }

   @Override
   protected Vector<FileLocationData> translateRsToValueObjects(ResultSet rs) throws SQLException
   {
      Vector<FileLocationData> vVector = new Vector<FileLocationData>();
      while (rs.next())
      {
         FileLocationData entry = new FileLocationData();

         entry.id = rs.getInt(1);
         entry.workorderId = rs.getInt(2);
         entry.inOrOut = rs.getInt(3);
         entry.size = rs.getInt(4);
         entry.name = null2EmpthyString(rs.getString(5));
         entry.storagePath = null2EmpthyString(rs.getString(6));
         vVector.add(entry);
      }
      return vVector;
   }

}
