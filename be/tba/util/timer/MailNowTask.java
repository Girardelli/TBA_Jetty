/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.timer;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.mail.session.MailerSessionBean;
import be.tba.servlets.session.WebSession;
import be.tba.util.session.AccountCache;

final public class MailNowTask 
{
    static public void send(AccountEntityData account)
    {
        if (System.getenv("TBA_MAIL_ON") == null)
        {
            return;
        }

        Thread t = new Thread(new MailNowThread(account));
        t.start();
    }
    static public void send(int accountId)
    {
    	MailNowTask.send(AccountCache.getInstance().get(accountId));
    }

    private static class MailNowThread implements Runnable
    {
        private int mAccountId;
        private AccountEntityData mAccountData;

        MailNowThread(AccountEntityData account)
        {
        	mAccountData = account;
        	mAccountId = account.getId();
        }


        public void run()
        {
            // delay call query for 10 seconds to allow the db to be updated by the
            // caller.
            WebSession session = null;
            try
            {
                Thread.sleep(10000);

                System.out.println("MailNowThread run for " + mAccountId);
                session = new WebSession();
                
                String vEmail = mAccountData.getEmail();
                if (vEmail != null && vEmail.length() > 0)
                {
                    MailerSessionBean.sendMail(session, mAccountData.getId());
                }
                session.Close();
            }
            catch (InterruptedException e1)
            {
                System.out.println("MailNowThread exception");
                e1.printStackTrace();
            }
            catch (Exception e)
            {
                System.out.println("MailNowThread exception");
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
