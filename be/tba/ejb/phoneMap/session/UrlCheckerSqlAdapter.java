package be.tba.ejb.phoneMap.session;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.ejb.phoneMap.interfaces.UrlCheckerEntityData;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;
import be.tba.util.data.AbstractSqlAdapter;

public class UrlCheckerSqlAdapter extends AbstractSqlAdapter<UrlCheckerEntityData>
{
   private static Logger log = LoggerFactory.getLogger(UrlCheckerSqlAdapter.class);
   private WebSession mWebSession = null;

   public UrlCheckerSqlAdapter()
   {
       super("UrlCheckerEntity");
   }
   
   public UrlCheckerEntityData getForMonth(int year, int month)
   {
      try
      {
         if (mWebSession == null) mWebSession = new WebSession();
         Collection<UrlCheckerEntityData> checkerList = executeSqlQuery(mWebSession, "SELECT * FROM UrlCheckerEntity where Month=" + month);
         if (checkerList.size() == 0)
         {
            int key = addRow(mWebSession, new UrlCheckerEntityData(year, month, 0, 0));
            return getRow(mWebSession, key);
         }
         else if (checkerList.size() > 1)
         {
            log.error("more than 1 UrlChecker entry for month " + Constants.MONTHS[month]);
         }
         else
         {
            return (UrlCheckerEntityData) checkerList.toArray()[0];
         }
      } catch (Exception e)
      {
         log.error("Exception for getForMonth(month=" + Constants.MONTHS[month]);
         log.error(e.getMessage(), e);
      }
      return null; 
   }
   
   public void update(int year, int month, long deadTime)
   {
      try
      {
         UrlCheckerEntityData checker = getForMonth(year, month);
         if (checker != null)
         {
            checker.secondsOut += deadTime; 
            checker.hickUps++;
            updateRow(mWebSession, checker);
         }
      } catch (Exception e)
      {
         log.error("Exception for update(month=" + Constants.MONTHS[month] + ", deadTime=" + deadTime);
         log.error(e.getMessage(), e);
      }
   }
   
   @Override
   protected Vector<UrlCheckerEntityData> translateRsToValueObjects(ResultSet rs) throws SQLException
   {
       Vector<UrlCheckerEntityData> vVector = new Vector<UrlCheckerEntityData>();
       while (rs.next())
       {
          UrlCheckerEntityData entry = new UrlCheckerEntityData();
           entry.id = rs.getInt(1);
           entry.secondsOut = rs.getLong(2);
           entry.year = rs.getInt(3);
           entry.month = rs.getInt(4);
           entry.hickUps = rs.getInt(5);
           vVector.add(entry);
       }
       return vVector;
   }
   
   
}
