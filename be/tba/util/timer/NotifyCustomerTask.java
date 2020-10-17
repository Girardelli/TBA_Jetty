/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.mail.Mailer;
import be.tba.session.WebSession;
import be.tba.sqldata.AccountCache;
import be.tba.sqldata.AccountEntityData;
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
   private static Logger log = LoggerFactory.getLogger(NotifyCustomerTask.class);

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
      private boolean mAlsoMail = false;

      NotifyCustomerThread(int accountId, WebSocketData wsData, boolean alsoMail)
      {
         mAccountId = accountId;
         mWsData = wsData;
         mAlsoMail = alsoMail;
         mAccountEntityData = AccountCache.getInstance().get(mAccountId);
         if (mAccountEntityData == null)
         {
            log.error("ERROR: MailNowThread.send with null account");
            return;
         }
         if (System.getenv("TBA_MAIL_ON") == null)
         {
            log.info("Dev mode: in production mail would be send to account " + mAccountEntityData.getEmail());
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
               if (!Mailer.sendCallInfoMail(session, mAccountId))
               {
                  log.error("NotifyCustomerThread sendmail failed: wait 5 sec and retry");
                  // wait another 5 seconds an retry once
                  Thread.sleep(5000);
                  if (!Mailer.sendCallInfoMail(session, mAccountId))
                  {
                     log.error("NotifyCustomerThread sendmail failed again");
                  }
               }
            }
            session.Close();
         }
         catch (InterruptedException e)
         {
            log.error("NotifyCustomerThread exception");
            log.error(e.getMessage(), e);
         }
         catch (Exception e)
         {
            log.error("NotifyCustomerThread exception");
            log.error(e.getMessage(), e);
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
