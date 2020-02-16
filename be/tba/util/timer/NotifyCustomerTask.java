/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.timer;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.mail.session.MailerSessionBean;
import be.tba.servlets.session.WebSession;
import be.tba.util.session.AccountCache;
import be.tba.websockets.TbaWebSocketAdapter;
import be.tba.websockets.WebSocketData;

/* This task is only fired when an important call has been triggered that must be notified to the customer.
 * This means that a mail must be send with the latest calls and a websocket notification 
 * must be send to the web page of the related customer
 * 
 * 
 */
final public class NotifyCustomerTask
{
   static public void notify(int accountId, WebSocketData wsData, boolean alsoMail)
   {
      Thread t = new Thread(new NotifyCustomerThread(accountId, wsData, alsoMail));
      t.start();
   }

   private static class NotifyCustomerThread implements Runnable
   {
      private int mAccountId;
      private AccountEntityData mAccountEntityData;
      private WebSocketData mWsData = null;
      private boolean mAlsoMail= false;

      NotifyCustomerThread(int accountId, WebSocketData wsData, boolean alsoMail)
      {
         mAccountId = accountId;
         mWsData = wsData;
         mAlsoMail = alsoMail;
         mAccountEntityData = AccountCache.getInstance().get(mAccountId);
         if (mAccountEntityData == null)
         {
            System.out.println("ERROR: MailNowThread.send with null account");
            return;
         }
         if (System.getenv("TBA_MAIL_ON") == null)
         {
            System.out.println("Dev mode: in production mail would be send to account " + mAccountEntityData.getEmail());
            return;
         }
      }

      public void run()
      {
         // delay call query for 10 seconds to allow the db to be updated by the
         // caller.
         WebSession session = null;
         try
         {
            Thread.sleep(2000);

            session = new WebSession();
            
            // send websocket event to customer
            TbaWebSocketAdapter.sendToCustomer(mWsData, mAccountId);
            // send mail to customer
            String vEmail = mAccountEntityData.getEmail();
            if (mAlsoMail && vEmail != null && vEmail.length() > 0)
            {
               if (!MailerSessionBean.sendCallInfoMail(session, mAccountId))
               {
                  System.out.println("NotifyCustomerThread sendmail failed: wait 5 sec and retry");
                  // wait another 5 seconds an retry once
                  Thread.sleep(5000);
                  if (!MailerSessionBean.sendCallInfoMail(session, mAccountId))
                  {
                     System.out.println("NotifyCustomerThread sendmail failed again");
                  }
               }
            }
            session.Close();
         }
         catch (InterruptedException e1)
         {
            System.out.println("NotifyCustomerThread exception");
            e1.printStackTrace();
         }
         catch (Exception e)
         {
            System.out.println("NotifyCustomerThread exception");
            e.printStackTrace();
         }
         finally
         {
            if (session != null)
            {
               session.Close();
            }
         }
      }
   }
}
