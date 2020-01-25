package be.tba.ejb.mail.session;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.sun.mail.smtp.SMTPTransport;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.pbx.interfaces.CallRecordEntityData;
import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;
import be.tba.util.session.AccountCache;
import be.tba.util.session.MailError;

/**
 * Session Bean Template
 *
 * ATTENTION: Some of the XDoclet tags are hidden from XDoclet by adding a "--"
 * between @ and the namespace. Please remove this "--" to make it active or add
 * a space to make an active tag inactive.
 *
 * @ejb:bean name="MailerSession" display-name="e-mail sender" type="Stateless"
 *           transaction-type="Container"
 *           jndi-name="be/tba/ejb/mail/MailerSession"
 *
 * @jboss:resource-manager res-man-name="MailerSession"
 *                         res-man-jndi-name="be/tba/ejb/mail/MailerSession"
 *
 */
public class MailerSessionBean
{

   // -------------------------------------------------------------------------
   // Static
   // -------------------------------------------------------------------------

   // -------------------------------------------------------------------------
   // Members
   // -------------------------------------------------------------------------

   // -------------------------------------------------------------------------
   // Methods
   // -------------------------------------------------------------------------

   // private constructor; static class
   private MailerSessionBean()
   {
   }

   static public boolean sendMail(WebSession webSession, int accountId, String subject, String body)
   {
      AccountEntityData vCustomer = null;
      String vEmailAddresses = "";
      
      if (accountId == 0)
      {
         // send mail to all staff members
         Collection<AccountEntityData> employees= AccountCache.getInstance().getEmployeeList();
         for (AccountEntityData employee : employees)
         {
            vEmailAddresses.concat(employee.getEmail() + ";");
        }
      }
      else
      {
         vCustomer = AccountCache.getInstance().get(accountId);

         vEmailAddresses = vCustomer.getEmail();
         if (vEmailAddresses == null)
         {
            return false;
         }
         vEmailAddresses = vEmailAddresses.trim();
         if (vEmailAddresses.length() == 0)
         {
            return false;
         }

         if (vEmailAddresses.endsWith(";") || vEmailAddresses.endsWith(","))
         {
            vEmailAddresses = vEmailAddresses.substring(0, vEmailAddresses.length() - 1);
         }
      }
      vEmailAddresses = vEmailAddresses.replace(',', ';');
      StringTokenizer vMailTokens = new StringTokenizer(vEmailAddresses, ";");
      try
      {
         Date date = new Date();
         Address[] vTo;

         if (System.getenv("TBA_MAIL_ON") != null)
         {
            vTo = new InternetAddress[vMailTokens.countTokens()];
            int i = 0;
            while (vMailTokens.hasMoreTokens())
            {
               String addr = vMailTokens.nextToken();
               if (addr != null && addr.contains("@"))
               {
                  vTo[i++] = new InternetAddress(addr);
               }
            }
         }
         else
         {
            vTo = new InternetAddress[1];
            vTo[0] = new InternetAddress("yves.willems@theBusinessAssistant.be");
         }
         Properties prop = System.getProperties();
         Session session = Session.getInstance(prop, null);
         MimeMessage msg = new MimeMessage(session);
         msg.setFrom(new InternetAddress("nancy.olyslaegers@thebusinessassistant.be"));
         msg.setRecipients(Message.RecipientType.TO, vTo);
         msg.setSubject(subject);
         msg.setSentDate(date);
         msg.setContent(body, "text/html");
         // Get SMTPTransport
         SMTPTransport t = (SMTPTransport) session.getTransport("smtp");

         // connect
         // t.setStartTLS(true);
         t.connect("smtp.telenet.be", "a120569", "gzb625");
         // send
         t.sendMessage(msg, msg.getAllRecipients());
         t.close();
      }
      catch (javax.mail.MessagingException e)
      {
         if (vCustomer != null)
            MailError.getInstance().setError("Mail send failed to " + vCustomer.getFullName() + "\n" + e.getMessage());
         e.printStackTrace();
         return false;
      }
      return true;
   }

   static public boolean sendCallInfoMail(WebSession webSession, int accountId)
   {
      AccountEntityData vCustomer = null;
      boolean ret = true;
//        try
//        {
      vCustomer = AccountCache.getInstance().get(accountId);
//
//            String vEmailAddr = vCustomer.getEmail();
//            if (vEmailAddr == null)
//            {
//                return false;
//            }
//            vEmailAddr = vEmailAddr.trim();
//            if (vEmailAddr.length() == 0)
//            {
//                return false;
//            }
//
//            if (vEmailAddr.endsWith(";") || vEmailAddr.endsWith(","))
//            {
//                vEmailAddr = vEmailAddr.substring(0, vEmailAddr.length() - 1);
//            }

      CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
      CallRecordSqlAdapter vWriterSession = new CallRecordSqlAdapter();

      Collection<CallRecordEntityData> vRecords = vQuerySession.getDocumentedNotMailed(webSession, vCustomer.getFwdNumber());

      AtomicBoolean isImportant = new AtomicBoolean(false);
      if (!vRecords.isEmpty() || !vCustomer.getNoEmptyMails())
      {
         System.out.println("sendMail start for:" + vCustomer.getFullName());
         StringBuffer vBody;
         if (vCustomer.getIsXmlMail())
         {
            vBody = buildXmlBody(vCustomer, vRecords, isImportant);
         }
         else if (vCustomer.getTextMail())
         {
            vBody = buildTextMailBody(vCustomer, vRecords, isImportant);
            // vEmailAddr = vEmailAddr.concat(";" +
            // Constants.NANCY_EMAIL);
         }
         else
         {
            vBody = buildMailBody(vCustomer, vRecords, isImportant);
            // vEmailAddr = vEmailAddr.concat(";" +
            // Constants.NANCY_EMAIL);
         }
         ret = sendMail(webSession, accountId, "Uw oproepenlijst tot " + DateFormat.getDateInstance(DateFormat.LONG, new Locale("nl", "BE")).format(new Date()) + " " + vCustomer.getFullName(), vBody.toString());
         flagRecordsAsMailed(webSession, vRecords, vWriterSession);

//                StringTokenizer vMailTokens = new StringTokenizer(vEmailAddr, ";");
//                try
//                {
//                    Date date = new Date();
//                    Address[] vTo;
//
//                    if (System.getenv("TBA_MAIL_ON") != null)
//                    {
//                        vTo = new InternetAddress[vMailTokens.countTokens()];
//                        int i = 0;
//                        while (vMailTokens.hasMoreTokens())
//                        {
//                            String addr = vMailTokens.nextToken();
//                            if (addr != null && addr.contains("@"))
//                            {
//                                vTo[i++] = new InternetAddress(addr);
//                            }
//                        }
//                    }
//                    else
//                    {
//                        vTo = new InternetAddress[1];
//                        vTo[0] = new InternetAddress("yves.willems@theBusinessAssistant.be");
//                    }
//                    Properties prop = System.getProperties();
//                    Session session = Session.getInstance(prop, null);
//                    MimeMessage msg = new MimeMessage(session);
//                    msg.setFrom(new InternetAddress("nancy.olyslaegers@thebusinessassistant.be"));
//                    msg.setRecipients(Message.RecipientType.TO, vTo);
//                    msg.setSubject("Uw oproepenlijst tot " + DateFormat.getDateInstance(DateFormat.LONG, new Locale("nl", "BE")).format(date) + " " + vCustomer.getFullName());
//                    msg.setSentDate(date);
//                    msg.setContent(vBody.toString(), "text/html");
//                    if (isImportant.get())
//                    {
//                    	msg.addHeader("X-Priority", "2");
//                    }
//        			// Get SMTPTransport
//                    SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
//        			
//        			// connect
//                    //t.setStartTLS(true);
//                    t.connect("smtp.telenet.be", "a120569", "gzb625");
//        			// send
//                    t.sendMessage(msg, msg.getAllRecipients());
//                    System.out.println("Response: " + t.getLastServerResponse());
//                    t.close();	        
//                    flagRecordsAsMailed(webSession, vRecords, vWriterSession);
//                    // vAccountSession.setAccount(vCustomer);
//                }
//                catch (javax.mail.MessagingException e)
//                {
//                    if (vCustomer != null)
//                        MailError.getInstance().setError("Mail send failed to " + vCustomer.getFullName() + "\n" + e.getMessage());
//                    e.printStackTrace();
//                    return false;
//                }
      }
//        }
//        catch (Exception e)
//        {
//            if (vCustomer != null)
//                MailError.getInstance().setError("Mail send failed to " + vCustomer.getFullName() + "\n" + e.getMessage());
//            e.printStackTrace();
//            return false;
//        }
      return ret;
   }

   static private StringBuffer buildMailBody(AccountEntityData account, Collection<CallRecordEntityData> records, AtomicBoolean isImportant)
   {
      StringBuffer vBody = new StringBuffer("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">");
      vBody.append("<HTML><HEAD>"); // <TITLE>TheBusinessAssistant administrator pages</TITLE>");
      vBody.append("<META http-equiv=Content-Type content=\"text/html; charset=iso-8859-1\"><BASE ");
      vBody.append("href=" + Constants.TBA_URL_BASE + "index.html>");
      // vBody.append("<META ");
      // vBody.append("content=\"virtueel secretariaat telefoondiensten
      // antwoorddiensten kantoor automatisering tekstverwerking administratie
      // afsprakendienst dactylo\" ");
      // vBody.append("name=Keywords>");
      vBody.append("<META content=\"Uw virtueel secretariaat.\" name=Description>");
      // vBody.append("<META content=yves.willems@theBusinessAssistunt.be
      // name=Owner>");
      vBody.append("<META http-equiv=Content-Language content=NL>");
      // vBody.append("<META content=\"Copyright � 2003 TheBusinessAssistant, All
      // rights reserved.\" name=Copyright>");
      vBody.append("<META content=Global name=Distribution><LINK title=main href=\"TheBusinessAssistant.css\" type=text/css rel=stylesheet>");
      // vBody.append("<META content=\"MSHTML 6.00.2800.1106\" name=GENERATOR>");
      vBody.append("</HEAD><BODY>");
      vBody.append("<TABLE cellSpacing=0 cellPadding=0 bgColor=#ffffff border=0><!--header 1-->");
      vBody.append("<TBODY>");
      vBody.append("<span class=\"bodytekst\">");
      vBody.append("Geachte,<br><br>");
      vBody.append("Gelieve hieronder uw oproepen te willen vinden die wij genoteerd hebben");
      if (account.getSuperCustomer() != null && !account.getSuperCustomer().isEmpty())
      {
         vBody.append(" voor <b><i>" + account.getFullName() + "</i></b>");
      }
      vBody.append(" sinds de vorige mail.<br>");

      vBody.append("Voor vragen kan u zich richten tot Nancy.<br>");

      long vCurrentTime = Calendar.getInstance().getTimeInMillis();
      // long vLastMailTime = account.getLastMailTime();
      account.setLastMailTime(vCurrentTime);

      System.out.println("Mail send to " + account.getFullName() + " : " + records.size() + " records.");

      if (records == null || records.size() == 0)
      {
         vBody.append("<br></span><span class=\"bodyredbold\">Er zijn geen nieuwe oproepgegevens beschikbaar.</span><span class=\"bodytekst\">");
      }
      else
      {
         // Set vKeySet = records.keySet();
         if (records.size() == 1)
            vBody.append("<br>Er is </span><span class=\"bodyredbold\">1</span><span class=\"bodytekst\"> nieuwe oproep beschikbaar.");
         else
            vBody.append("<br>Er zijn </span><span class=\"bodyredbold\">" + records.size() + "</span><span class=\"bodytekst\"> nieuwe oproepen beschikbaar.");
         // int vNewCnt = 0;

         vBody.append("</span><br><br>");
         vBody.append("<table border=\"0\" cellspacing=\"2\" cellpadding=\"2\">");
         vBody.append("<tr>");
         vBody.append("<td width=20 bgcolor=FFFFFF></td>");
         vBody.append("<td width=10  class=\"topMenu\" bgcolor=FF9900>&nbsp;</td>");
         vBody.append("<td width=55  class=\"topMenu\" bgcolor=FF9900>&nbsp;Datum</td>");
         vBody.append("<td width=35  class=\"topMenu\" bgcolor=FF9900>&nbsp;Uur</td>");
         if (account.getHasSubCustomers())
         {
            vBody.append("<td width=150 class=\"topMenu\" bgcolor=FF9900>&nbsp;Voor</td>");
         }
         vBody.append("<td width=85  class=\"topMenu\" bgcolor=FF9900>&nbsp;Nummer</td>");
         vBody.append("<td width=140 class=\"topMenu\" bgcolor=FF9900>&nbsp;Naam</td>");
         vBody.append("<td width=280 class=\"topMenu\" bgcolor=FF9900>&nbsp;Omschrijving</td>");
         vBody.append("<td width=115  class=\"topMenu\" bgcolor=FF9900>&nbsp;Info</td>");
         vBody.append("</tr>");

         // vKeySet = records.keySet();
         // int vRowInd = 0;
         for (Iterator<CallRecordEntityData> i = records.iterator(); i.hasNext();)
         {
            CallRecordEntityData vEntry = i.next();
            // int vId = vEntry.getId();
            String vDate = vEntry.getDate();
            String vTime = vEntry.getTime();
            String vNumber = vEntry.getNumber();
            String vName = vEntry.getName();
            vName = vName == null ? "" : vName;
            String vShortDesc = (String) vEntry.getShortDescription();
            vShortDesc = vShortDesc == null ? "" : vShortDesc;
            String vLongDesc = (String) vEntry.getLongDescription();
            vLongDesc = vLongDesc == null ? "" : vLongDesc;
            String vInOut = Constants.TBA_URL_BASE;
            if (vEntry.getIsIncomingCall())
               vInOut = vInOut.concat("images/incall.gif");
            else
               vInOut = vInOut.concat("images/outcall.gif");

            if (vEntry.getIsImportantCall())
            {
               vBody.append("<tr bgcolor=FFCC66 class=\"bodybold\">");
               vBody.append("<td width=20 bgcolor=FFFFFF><img src=\"" + vInOut + "\" height=\"13\" border=\"0\"></td>");
               vBody.append("<td width=10><img src=\"" + Constants.TBA_URL_BASE + "images/important.gif\"  height=\"13\" border=\"0\"></td>");
               isImportant.set(true);
            }
            else
            {
               vBody.append("<tr bgcolor=FFFF99 class=\"bodytekst\">");
               vBody.append("<td width=20 bgcolor=FFFFFF><img src=\"" + vInOut + "\" height=\"13\" border=\"0\"></td>");
               vBody.append("<td width=10></td>");
            }
            vBody.append("<td width=55  valign=top>" + vDate + "</td>");
            vBody.append("<td width=35  valign=top>" + vTime + "</td>");
            if (account.getHasSubCustomers())
            {
               AccountEntityData subCustomer = AccountCache.getInstance().get(vEntry);
               vBody.append("<td width=150 valign=top>" + subCustomer.getFullName() + "</td>");
            }
            vBody.append("<td width=85  valign=top>" + vNumber + "</td>");
            vBody.append("<td width=140 valign=top>" + vName + "</td>");
            vBody.append("<td width=280 valign=top>" + vShortDesc + "</td>");
            vBody.append("<td width=115>");
            if (vLongDesc.length() != 0)
               vBody.append("<img src=\"" + Constants.TBA_URL_BASE + "images/info.gif\" alt=\"Extra info\" height=\"16\" border=\"0\">");
            if (vEntry.getIsAgendaCall())
               vBody.append("<img src=\"" + Constants.TBA_URL_BASE + "images/agenda.gif\" height=\"13\" border=\"0\">&nbsp;");
            if (vEntry.getIsSmsCall())
               vBody.append("<img src=\"" + Constants.TBA_URL_BASE + "images/sms.gif\"  height=\"13\" border=\"0\">&nbsp");
            if (vEntry.getIsForwardCall())
               vBody.append("<img src=\"" + Constants.TBA_URL_BASE + "images/telefoon.gif\"  height=\"13\" border=\"0\">&nbsp;");
            if (vEntry.getIsFaxCall())
               vBody.append("<img src=\"" + Constants.TBA_URL_BASE + "images/fax.gif\"  height=\"13\" border=\"0\">&nbsp;");
            vBody.append("</td></tr>");
            // vRowInd++;
         }
         vBody.append("</table>");
      }
      /*
       * vBody.append("<br><img src=\"./images/info.gif\" alt=\"Extra info\"><br>" );
       * vBody.append("<img src=\
       * "./images/info.gif\" alt=\"Extra info\" width=\"15\" height=\"15\" border=\"0\">"
       * ); vBody.append("<img src=\
       * "/tba/images/agenda.gif\" height=\"13\" border=\"0\">&nbsp;" );
       * vBody.append("<img src=\
       * "/tba/images/sms.gif\" height=\"13\" border=\"0\">&nbsp" );
       * vBody.append("<img src=\
       * "/tba/images/telefoon.gif\" height=\"13\" border=\"0\">&nbsp;" );
       */

      vBody.append("<span class=\"bodytekst\"><br><br>");
      vBody.append("Dit is een automatisch gegenereerde mail.<br>");
      vBody.append("U kan het tijdstip van deze mail zelf instellen als u zich aanmeldt op onze webpagina (<a href=\"http://www.theBusinessAssistant.be\">www.theBusinessAssistant.be</a>).<br>");
      vBody.append("Eventuele extra informatie aangegeven met het <img src=\"" + Constants.TBA_URL_BASE + "images/info.gif\" alt=\"Extra info\" height=\"16\" border=\"0\"> &nbsp;icoontje, kan daar ook geraadpleegd worden.<br>");
      vBody.append("<br><br>Vriendelijke groeten<br>");
      vBody.append("<br><br>Het TBA team<br>");
      vBody.append("</TBODY></TABLE></BODY></HTML>");
      return vBody;
   }

   static private StringBuffer buildTextMailBody(AccountEntityData account, Collection<CallRecordEntityData> records, AtomicBoolean isImportant)
   {
      StringBuffer vBody = new StringBuffer("");
      vBody.append("Geachte\n\n");
      vBody.append("Gelieve hieronder uw oproepen te willen vinden die wij genoteerd hebben sinds de vorige mail.\n");
      vBody.append("Voor vragen kan u zich richten tot Nancy.\n");

      System.out.println("Mail send to " + account.getFullName() + " : " + records.size() + " records.");

      if (records == null || records.size() == 0)
      {
         vBody.append("\nEr zijn geen nieuwe oproepgegevens beschikbaar.");
      }
      else
      {
         // Set vKeySet = records.keySet();
         if (records.size() == 1)
            vBody.append("\nEr is 1 nieuwe oproep beschikbaar.");
         else
            vBody.append("\nEr zijn " + records.size() + " nieuwe oproepen beschikbaar.");

         vBody.append("\n\n");

         // vKeySet = records.keySet();
         for (Iterator<CallRecordEntityData> i = records.iterator(); i.hasNext();)
         {
            CallRecordEntityData vEntry = i.next();
            String vName = vEntry.getName();
            vName = vName == null ? "" : vName;
            String vShortDesc = (String) vEntry.getShortDescription();
            vShortDesc = vShortDesc == null ? "" : vShortDesc;
            String vLongDesc = (String) vEntry.getLongDescription();
            vLongDesc = vLongDesc == null ? "" : vLongDesc;
            if (vEntry.getIsImportantCall())
            {
               isImportant.set(true);
            }

            vBody.append("\n\n" + vEntry.getDate() + " " + vEntry.getTime());
            vBody.append("\nvan nummer : " + vEntry.getNumber());
            vBody.append("\nnaam oproeper: " + vName);
            vBody.append("\nbeschrijving:\n" + vShortDesc);
         }
      }
      return vBody;
   }

   static private StringBuffer buildXmlBody(AccountEntityData account, Collection<CallRecordEntityData> records, AtomicBoolean isImportant)
   {
      StringBuffer vBody = new StringBuffer("<?xml version=\"1.0\"?>\n");
      vBody.append("<objects>\n");

      long vCurrentTime = Calendar.getInstance().getTimeInMillis();
      // long vLastMailTime = account.getLastMailTime();
      account.setLastMailTime(vCurrentTime);

      // Set vKeySet = records.keySet();
      for (Iterator<CallRecordEntityData> i = records.iterator(); i.hasNext();)
      {
         CallRecordEntityData vEntry = i.next();
         // CallRecordEntityData vEntry = ((CallRecordEntityData)
         // records.get((Long) i.next()));//.getValueObject();

         String vDate = vEntry.getDate();
         String vTime = vEntry.getTime();
         String vNumber = vEntry.getNumber();
         String vName = vEntry.getName();
         vName = vName == null ? "" : vName;
         String vShortDesc = (String) vEntry.getShortDescription();
         vShortDesc = vShortDesc == null ? "" : vShortDesc;
         String vLongDesc = (String) vEntry.getLongDescription();
         vLongDesc = vLongDesc == null ? "" : vLongDesc;

         vBody.append(" <object>\n");

         vBody.append("  <date>" + vDate + "</date>\n");
         vBody.append("  <time>" + vTime + "</time>\n");
         vBody.append("  <description>" + vShortDesc + "</description>\n");
         vBody.append("  <tel>" + vNumber + "</tel>\n");
         vBody.append("  <name>" + vName + "</name>\n");
         if (vLongDesc.length() > 0)
            vBody.append("  <info>" + vLongDesc + "</info>\n");
         if (vEntry.getIsAgendaCall())
            vBody.append("  <meeting>true</meeting>\n");
         if (vEntry.getIsSmsCall())
            vBody.append("  <sms>true</sms>\n");
         if (vEntry.getIsForwardCall())
            vBody.append("  <forward>true</forward>\n");
         if (vEntry.getIsImportantCall())
            vBody.append("  <belangrijk>true</belangrijk>\n");
         if (vEntry.getIsFaxCall())
            vBody.append("  <fax>true</fax>\n");
         vBody.append(" </object>\n");
         if (vEntry.getIsImportantCall())
         {
            isImportant.set(true);
         }
      }
      vBody.append("</objects>\n");
      return vBody;
   }

   static private void flagRecordsAsMailed(WebSession webSession, Collection<CallRecordEntityData> records, CallRecordSqlAdapter writerSession)
   {
      // Set vKeySet = records.keySet();

      for (Iterator<CallRecordEntityData> i = records.iterator(); i.hasNext();)
      {
         CallRecordEntityData vEntry = i.next();
         writerSession.setIsMailed(webSession, vEntry.getId());
      }
   }
}
