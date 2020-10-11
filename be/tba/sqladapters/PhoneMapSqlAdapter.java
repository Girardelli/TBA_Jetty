package be.tba.sqladapters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import be.tba.sqldata.PhoneMapEntityData;

public class PhoneMapSqlAdapter extends AbstractSqlAdapter<PhoneMapEntityData>
{
   public PhoneMapSqlAdapter()
   {
      super("PhoneMapEntity");
   }

   @Override
   protected Vector<PhoneMapEntityData> translateRsToValueObjects(ResultSet rs) throws SQLException
   {
      Vector<PhoneMapEntityData> vVector = new Vector<PhoneMapEntityData>();
      while (rs.next())
      {
         PhoneMapEntityData entry = new PhoneMapEntityData();
         entry.id = rs.getInt(1);
         entry.phoneId = null2EmpthyString(rs.getString(2));
         entry.userId = null2EmpthyString(rs.getString(3));
         vVector.add(entry);
         // log.info("InvoiceEntityData: " + entry.toNameValueString());
      }
      return vVector;
   }
}
