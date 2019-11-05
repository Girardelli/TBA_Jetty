package be.tba.ejb.phoneMap.session;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import be.tba.ejb.phoneMap.interfaces.PhoneMapEntityData;
import be.tba.util.data.AbstractSqlAdapter;

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
           // System.out.println("InvoiceEntityData: " + entry.toNameValueString());
       }
       return vVector;
   }
}
