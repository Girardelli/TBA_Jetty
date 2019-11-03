package be.tba.servlets.helper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.google.gson.Gson;

import be.tba.ejb.pbx.interfaces.CallRecordEntityData;
import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.servlets.session.WebSession;
import be.tba.util.data.IntertelCallData;
import be.tba.websockets.WebSocketData;

public class IntertelCallManager
{
   private final class PhoneLog
   {
      public String phoneId;
      public String userId;
      public String sessionId;
      public long lastUsed;
      
      public String toString()
      {
         return "PhoneId=" + phoneId + ", userId=" + userId + ", sessionId=" + sessionId;
      }
   }

   private static IntertelCallManager mInstance;

   private Map<String, IntertelCallData> mCallMap;
   private Map<String, PhoneLog> mOperatorPhoneMap;

   private IntertelCallManager()
   {
      mCallMap = new HashMap<String, IntertelCallData>();
      mOperatorPhoneMap = new HashMap<String, PhoneLog>();
   }

   public static IntertelCallManager getInstance()
   {
      if (mInstance == null)
      {
         mInstance = new IntertelCallManager();
      }
      return mInstance;
   }

   public synchronized void newCall(IntertelCallData data)
   {
      System.out.println("IntertelCallManager.newCall: " + data.toString());
      mCallMap.put(data.intertelCallId, data);
//      if (++mCallCnt > kCallCleaner)
//      {
//         mCallCnt = 0;
//         cleanUpMaps();
//      }
      // System.out.println(data.intertelCallId + "-created in manager");
   }

   public synchronized void removeCall(WebSession session, int callDbId)
   {
      //System.out.println("IntertelCallManager.removeCall with id:" + callDbId);
      removeCall(session, getByDbId(callDbId));
   }

   public synchronized void removeCall(WebSession session, IntertelCallData data)
   {
      if (data != null)
      {
         IntertelCallData removedCall = mCallMap.remove(data.intertelCallId);
         //System.out.println("IntertelCallManager.removeCall done: " + removedCall.toString());
         if (data.tsAnswer == 0 || data.tsEnd == 0)
         {
            CallRecordSqlAdapter vCallLogWriterSession = new CallRecordSqlAdapter();
            vCallLogWriterSession.setNotAnswered(session, data);
         }
      }
   }

   public synchronized IntertelCallData get(String callId)
   {
      return mCallMap.get(callId);
   }

   public synchronized IntertelCallData getByDbId(int id)
   {
      Collection<IntertelCallData> calls = mCallMap.values();
      System.out.println("getByDbId(" + id + "): list size=" + calls.size());
      for (Iterator<IntertelCallData> itr = calls.iterator(); itr.hasNext();)
      {
         IntertelCallData call = itr.next();
         //System.out.println("    check call.dbRecordId=" + call.dbRecordId + ", id=" + id);
         if (call.dbRecordId == id)
         {
            return call;
         }
      }
      System.out.println("getByDbId(" + id + ") not found");
      return null;
   }

   public synchronized IntertelCallData getTransferCall(String transferedCallId, String transferCalledNr, String transferCallingNr)
   {
      // callId is of the incomming call that is transfered. We are looking for a call
      // that has NOT this ID
      // CallingNr: tba number
      // calledNr: number to who the call is transfered

      for (Iterator<String> i = mCallMap.keySet().iterator(); i.hasNext();)
      {
         String key = i.next();
         IntertelCallData data = mCallMap.get(key);

         // System.out.println("getransferCall: " + transferCalledNr + "==" +
         // data.calledNr + " && " + transferCallingNr + "==" + data.callingNr + " && " +
         // transferedCallId + " != " + data.intertelCallId);

         if (transferCalledNr.equals(data.calledNr) && transferCallingNr.equals(data.callingNr) && !transferedCallId.equals(data.intertelCallId))
         {
            return data;
         }
      }
      return null;
   }
   
   public synchronized IntertelCallData getTransferCall_CallParkBugs(IntertelCallData transferedCall)
   {
      // callId is of the incomming call that is transfered. We are looking for a call
      // that has NOT this ID
      // CallingNr: tba number
      // calledNr: number to who the call is transfered
int y = 1;
      for (Iterator<IntertelCallData> i = mCallMap.values().iterator(); i.hasNext();)
      {
         IntertelCallData call = i.next();
         System.out.println("getTransferCall_CallParkBugs " + y++);
         System.out.println(transferedCall);
         System.out.println(call);
         if (call != transferedCall && call.isIncoming && call.tsEnd == 0 && transferedCall.callingNr.equals(call.answeredBy))
         {
            return call;
         }
      }
      System.out.println("getTransferCall_CallParkBugs returns null");
      return null;
   }
   
   public synchronized Collection<IntertelCallData> getCallList()
   {
      Collection<IntertelCallData> calls = new Vector<IntertelCallData>();
      calls.addAll(mCallMap.values());
      return calls;
   }

   public synchronized Collection<String> getPendingCallList()
   {
      Collection<String> pendingCalls = new Vector<String>();
      for (Iterator<IntertelCallData> i = mCallMap.values().iterator(); i.hasNext();)
      {
         IntertelCallData call = i.next();
         //System.out.println("getPendingCallList " + call.toString());
         if (call.phase.equals("start"))
         {
            WebSocketData data = new WebSocketData(WebSocketData.NEW_CALL, call.tsStart, call);
            pendingCalls.add((new Gson()).toJson(data, WebSocketData.class));
         }
      }
      return pendingCalls;
   }

   public synchronized void updateOperatorMapping(CallRecordEntityData data, String sessionId)
   {
      IntertelCallData call = getByDbId(data.getId());
      System.out.println("updateOperatorMapping: data.getId()=" + data.getId() + ", sessionId=" + sessionId + ", call" + call);
      if (call == null || call.answeredBy.isEmpty())
      {
         return; // to be removed once we have switched to Intertel
      }
      boolean addIt = false;
      if (mOperatorPhoneMap.containsKey(call.answeredBy))
      {
         PhoneLog phoneLog = mOperatorPhoneMap.get(call.answeredBy);
         if (phoneLog.phoneId.equals(call.answeredBy))
         {
            // already mapped: update timestamp
            System.out.println("updateOperatorMapping : match");
            phoneLog.lastUsed = System.currentTimeMillis() / 1000l;
         } 
         else
         {
            System.out.println("updateOperatorMapping : no match. remove " + phoneLog.toString());
            mOperatorPhoneMap.remove(phoneLog.phoneId);
            addIt = true;
         }
      } else
      {
         addIt = true;
      }
      if (addIt)
      {
         PhoneLog phoneLog = new PhoneLog();
         phoneLog.phoneId = call.answeredBy;
         phoneLog.userId = data.getDoneBy();
         phoneLog.sessionId = sessionId;
         phoneLog.lastUsed = System.currentTimeMillis() / 1000l;
         System.out.println("mOperatorPhoneMap.put: " + phoneLog.toString());
         mOperatorPhoneMap.put(phoneLog.phoneId, phoneLog);
      }
   }
   
   public synchronized String getSessionIdForPhoneId(String phoneId)
   {
      PhoneLog phoneLog = mOperatorPhoneMap.get(phoneId);
      if (phoneLog != null)
      {
         return phoneLog.sessionId;
      }
      System.out.println("getSessionIdForPhoneId: " + phoneId + " not found");
      return "";
   }

   public synchronized void cleanUpMaps()
   {
      long tsNow = System.currentTimeMillis() / 1000l;
      //System.out.println(this + ". mCallMap.size()=" + mCallMap.size() + ", mOperatorPhoneMap=" + mOperatorPhoneMap.size());

      for (Iterator<String> i = mCallMap.keySet().iterator(); i.hasNext();)
      {
         String key = i.next();
         IntertelCallData data = mCallMap.get(key);
         if ((data.tsEnd != 0 &&  data.tsEnd < (tsNow - 1000)) ||
               (data.tsStart < (tsNow - 60*30))) // 0.5 hours
         {
            System.out.println("Cleanup: IntertelCallManager.removeCall (tsNow=" + tsNow + "): " + data.toString());
            i.remove();
         }
      }
      for (Iterator<String> i = mOperatorPhoneMap.keySet().iterator(); i.hasNext();)
      {
         String key = i.next();
         PhoneLog phoneLog = mOperatorPhoneMap.get(key);
         if ((tsNow - phoneLog.lastUsed) > 3600) // 1 hour
         {
            System.out.println("CleanUP: IntertelCallManager.removeOperator: " + phoneLog.toString());
            i.remove();
            // System.out.println("IntertelCallManager: removed from mOperatorPhoneMap: " +
            // key);
         }
      }

   }

}
