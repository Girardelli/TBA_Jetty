/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.timer;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimerTask;
import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.mail.session.MailerSessionBean;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;
import be.tba.util.session.AccountCache;
import be.tba.util.session.MailError;

final public class MailTimerTask extends TimerTask implements TimerTaskIntf
{
    public MailTimerTask()
    {
    }

 	@Override
	public Date getStartTime() {
		// TODO Auto-generated method stub
 		GregorianCalendar vCalendar = new GregorianCalendar();
 		return vCalendar.getTime();
	}

	@Override
	public long getPeriod() {
		// TODO Auto-generated method stub
		return Constants.MINUTES * 5;
	}

	@Override
	public TimerTask getTimerTask() {
		// TODO Auto-generated method stub
		return this;
	}


    public void run()
    {
        // System.out.println("Mail send for " + mAccountNr + ". Schedule time "
        // + long2String(scheduledExecutionTime()));
        // return;
        if (System.getenv("TBA_MAIL_ON") == null)
        {
            return;
        }
        WebSession session = null;
        //System.out.println("MailTimerTask run");

        GregorianCalendar vCalendar = new GregorianCalendar();
        if (vCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && vCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
        {
            try
            {
                session = new WebSession();
                Integer vKey = Integer.valueOf(vCalendar.get(Calendar.HOUR_OF_DAY) * 60 + vCalendar.get(Calendar.MINUTE));

                Collection<AccountEntityData> mailGroup = AccountCache.getInstance().getMailingGroup(vKey);
                if (mailGroup != null)
                {
                    for (Iterator<AccountEntityData> j = mailGroup.iterator(); j.hasNext();)
                    {
                        AccountEntityData vAccount = j.next();
                        if (System.getenv("TBA_MAIL_ON") != null)
                        {
                            try
                            {
                                MailerSessionBean.sendMail(session, vAccount.getId());
                                System.out.println("Mail sent to " + vAccount.getFullName());
                            }
                            catch (Exception e)
                            {
                                System.out.println("Mail send failed to " + vAccount.getFullName());
                                MailError.getInstance().setError("Mail send failed to " + vAccount.getFullName() + "\n" + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            System.out.println("Mail supposed to be send but disabled to " + vAccount.getFullName());
                        }
                    }
                }
            }
            catch (Exception e)
            {
                System.out.println("MailTimerTask exception");
                MailError.getInstance().setError("Mail send failed\n" + e.getMessage());
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

    static public String long2String(long time)
    {
        GregorianCalendar vCalendar = new GregorianCalendar();
        vCalendar.setTimeInMillis(time);
        return vCalendar.getTime().toString();

    }
}
