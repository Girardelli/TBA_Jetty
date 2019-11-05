package be.tba.servlets.helper;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import be.tba.ejb.pbx.interfaces.CallRecordEntityData;
import be.tba.ejb.phoneMap.interfaces.PhoneMapEntityData;
import be.tba.ejb.phoneMap.session.PhoneMapSqlAdapter;
import be.tba.servlets.session.SessionManager;
import be.tba.servlets.session.WebSession;
import be.tba.util.data.IntertelCallData;

public class PhoneMapManager
{
   class PhoneMapData
   {
      public PhoneMapEntityData phoneUserMap;
      public String sessionId;
      public long lastUsed;
      
      public PhoneMapData()
      {
         this.phoneUserMap = new PhoneMapEntityData();
         this.sessionId = "";
         this.lastUsed = System.currentTimeMillis() / 1000l;
      }
      
      public PhoneMapData(PhoneMapEntityData phoneUserMap)
      {
         this.phoneUserMap = phoneUserMap;
         this.sessionId = "";
         this.lastUsed = System.currentTimeMillis() / 1000l;
      }
      
      public String toString()
      {
         return "phoneUserMap=" + phoneUserMap + ", sessionId=" + sessionId + "' lastUsed=" + lastUsed;
      }
   }
   
   
   
   private static PhoneMapManager mInstance;

   private Map<String, PhoneMapData> mOperatorPhoneMap;

   private PhoneMapManager()
   {
      mOperatorPhoneMap = new HashMap<String, PhoneMapData>();
      PhoneMapSqlAdapter sqlAdapter = new PhoneMapSqlAdapter();
      WebSession session = null;
      try
      {
         session = new WebSession();
      } catch (SQLException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      Collection<PhoneMapEntityData> phones = sqlAdapter.getAllRows(session);
      for (Iterator<PhoneMapEntityData> i = phones.iterator(); i.hasNext();)
      {
         PhoneMapEntityData phone = i.next();
         mOperatorPhoneMap.put(phone.phoneId, new PhoneMapData(phone));
      }
      System.out.println("PhoneMapManager() size=" + mOperatorPhoneMap.size());
   }

   public static PhoneMapManager getInstance()
   {
      if (mInstance == null)
      {
         mInstance = new PhoneMapManager();
      }
      return mInstance;
   }

   public synchronized void updateOperatorMapping(CallRecordEntityData data, WebSession session)
   {
      IntertelCallData call = IntertelCallManager.getInstance().getByDbId(data.getId());
      PhoneMapSqlAdapter sqlAdapter = null;
      System.out.println("updateOperatorMapping: data.getId()=" + data.getId() + ", sessionId=" + session.getSessionId() + ", call" + call);
      if (call == null || call.answeredBy.isEmpty())
      {
         return; // to be removed once we have switched to Intertel
      }
      boolean addIt = false;
      if (mOperatorPhoneMap.containsKey(call.answeredBy))
      {
         PhoneMapData phoneMapData = mOperatorPhoneMap.get(call.answeredBy);
         if (phoneMapData.phoneUserMap.phoneId.equals(call.answeredBy))
         {
            // already mapped: update timestamp
            System.out.println("updateOperatorMapping : match");
            phoneMapData.lastUsed = System.currentTimeMillis() / 1000l;
            phoneMapData.sessionId = session.getSessionId();
         } 
         else
         {
            System.out.println("updateOperatorMapping : no match. remove " + phoneMapData.toString());
            mOperatorPhoneMap.remove(phoneMapData.phoneUserMap.phoneId);
            sqlAdapter = new PhoneMapSqlAdapter();
            sqlAdapter.deleteRow(session, phoneMapData.phoneUserMap.id);
            addIt = true;
         }
      } else
      {
         addIt = true;
      }
      if (addIt)
      {
         PhoneMapData phoneMapData = new PhoneMapData();
         phoneMapData.phoneUserMap.phoneId = call.answeredBy;
         phoneMapData.phoneUserMap.userId = data.getDoneBy();
         phoneMapData.sessionId = session.getSessionId();
         phoneMapData.lastUsed = System.currentTimeMillis() / 1000l;
         System.out.println("mOperatorPhoneMap.put: " + phoneMapData.toString());
         mOperatorPhoneMap.put(phoneMapData.phoneUserMap.phoneId, phoneMapData);
         if (sqlAdapter == null)
            sqlAdapter = new PhoneMapSqlAdapter();
         sqlAdapter.addRow(session, phoneMapData.phoneUserMap);
      }
   }
   
   public synchronized String getSessionIdForPhoneId(String phoneId)
   {
      PhoneMapData phoneMapData = mOperatorPhoneMap.get(phoneId);
      if (phoneMapData != null)
      {
         return phoneMapData.sessionId;
      }
      System.out.println("getSessionIdForPhoneId: " + phoneId + " not found");
      return "";
   }

   public void mapNewLogin(String userId, String sessionId)
   {
      for (Iterator<PhoneMapData> i = mOperatorPhoneMap.values().iterator(); i.hasNext();)
      {
         PhoneMapData entry = i.next();
         if (userId.equals(entry.phoneUserMap.userId))
         {
            entry.sessionId = sessionId;
            return;
         }
      }
   }
   
   public synchronized void cleanUpMap()
   {
      long tsNow = System.currentTimeMillis() / 1000l;
      PhoneMapSqlAdapter sqlAdapter = null;
      WebSession session = null;
      //System.out.println(this + ". mCallMap.size()=" + mCallMap.size() + ", mOperatorPhoneMap=" + mOperatorPhoneMap.size());

      for (Iterator<String> i = mOperatorPhoneMap.keySet().iterator(); i.hasNext();)
      {
         String key = i.next();
         PhoneMapData phoneMapEntityData = mOperatorPhoneMap.get(key);
         if ((tsNow - phoneMapEntityData.lastUsed) > 3600) // 1 hour
         {
            System.out.println("CleanUP: PhoneMapManager.removeOperator: " + phoneMapEntityData.toString());
            if (sqlAdapter == null)
            {
               sqlAdapter = new PhoneMapSqlAdapter();
               try
               {
                  session = new WebSession();
               } catch (SQLException e)
               {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
            }
            sqlAdapter.deleteRow(session, phoneMapEntityData.phoneUserMap.id);
            i.remove();
            // System.out.println("PhoneMapManager: removed from mOperatorPhoneMap: " +
            // key);
         }
      }

   }

}
