/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.constants;

/**
 * Base Data Container for all other Value Objects
 * 
 * @author Yves Willems
 * @version $Revision: 1.0 $
 */
public class EjbJndiNames
{
   public static final String EJB_JNDI_ACCOUNT = "be/tba/ejb/customer/info/AccountEntity";

   public static final String EJB_JNDI_INFO_SHEET = "be/tba/ejb/customer/info/InfoSheetEntity";

   public static final String EJB_JNDI_CALL_RECORD = "be/tba/ejb/pbx/CallRecordEntity";

   public static final String EJB_JNDI_CALL_LOG_WRITER_SESSION = "be/tba/ejb/pbx/CallLogWriterSession";

   public static final String EJB_JNDI_CALL_RECORD_QUERY_SESSION = "be/tba/ejb/pbx/CallRecordQuerySession";

   public static final String EJB_JNDI_ACCOUNT_SESSION = "be/tba/ejb/customer/info/AccountSession";

   public static final String EJB_JNDI_ADMIN_SESSION = "be/tba/ejb/admin/WebSession";

   public static final String EJB_JNDI_MAILER_SESSION = "be/tba/ejb/mail/MailerSession";

   public static final String EJB_JNDI_TASK = "be/tba/ejb/task/TaskEntity";

   public static final String EJB_JNDI_TASK_SESSION = "be/tba/ejb/task/TaskSession";

   public static final String EJB_JNDI_INVOICE = "be/tba/ejb/task/InvoiceEntity";

   public static final String EJB_JNDI_INVOICE_SESSION = "be/tba/ejb/task/InvoiceSession";

   private EjbJndiNames()
   {
   }
}
