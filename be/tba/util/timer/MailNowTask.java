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
    static public void send(String account)
    {
        if (System.getenv("TBA_MAIL_ON") == null)
        {
            return;
        }

        Thread t = new Thread(new MailNowThread(account));
        t.start();

    }

    private static class MailNowThread implements Runnable
    {
        private String mAccount;

        MailNowThread(String account)
        {
            mAccount = account;
        }

        public void run()
        {
            // delay call query for 10 seconds to allow the db to be updated by the
            // caller.
            WebSession session = null;
            try
            {
                Thread.sleep(10000);

                System.out.println("MailNowThread run for " + mAccount);
                session = new WebSession();
                AccountEntityData vAccountData = AccountCache.getInstance().get(mAccount);

                String vEmail = vAccountData.getEmail();
                if (vEmail != null && vEmail.length() > 0)
                {
                    MailerSessionBean.sendMail(session, vAccountData.getFwdNumber());
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
