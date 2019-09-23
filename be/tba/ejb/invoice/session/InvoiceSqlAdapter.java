package be.tba.ejb.invoice.session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.InitialContext;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.invoice.interfaces.InvoiceEntityData;
import be.tba.ejb.task.interfaces.TaskEntityData;
import be.tba.ejb.task.session.TaskSqlAdapter;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;
import be.tba.util.data.AbstractSqlAdapter;
import be.tba.util.data.FintroPayment;
import be.tba.util.invoice.IBANCheckDigit;
import be.tba.util.invoice.InvoiceHelper;
import be.tba.util.session.AccountCache;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.sql.ResultSet;

/**
 * Session Bean Template
 *
 * ATTENTION: Some of the XDoclet tags are hidden from XDoclet by adding a "--"
 * between @ and the namespace. Please remove this "--" to make it active or add
 * a space to make an active tag inactive.
 *
 * @ejb:bean name="InvoiceSession" display-name="Invoice query" type="Stateless"
 *           transaction-type="Container"
 *           jndi-name="be/tba/ejb/task/InvoiceSession"
 *
 * @ejb:ejb-ref ejb-name="InvoiceEntity"
 *
 */
public class InvoiceSqlAdapter extends AbstractSqlAdapter<InvoiceEntityData>
{

    // -------------------------------------------------------------------------
    // Static
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Members
    // -------------------------------------------------------------------------

    /**
     *
     */
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    public InvoiceSqlAdapter()
    {
        super("InvoiceEntity");
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public int addInvoice(WebSession webSession, InvoiceEntityData data)
    {
        addRow(webSession, data);
        Collection<InvoiceEntityData> invoices = executeSqlQuery(webSession, "SELECT * FROM InvoiceEntity WHERE AccountID=" + data.getAccountID() + " AND TotalCost=" + data.getTotalCost()  + " AND StartTime=" + data.getStartTime() + " AND StopTime=" + data.getStopTime());
        return ((InvoiceEntityData) invoices.toArray()[0]).getId();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public InvoiceEntityData getInvoiceById(WebSession webSession, int key)
    {
        return getRow(webSession, key);
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<InvoiceEntityData> getInvoicesByValueAndAccounts(WebSession webSession, Collection<Integer> accountIDs, double inclBtwCost)
    {
        if (accountIDs != null && !accountIDs.isEmpty())
        {
            
            String accountIdSqlsequence = "IN (";
            
            for (Iterator<Integer> vFwdNrsIter = accountIDs.iterator(); vFwdNrsIter.hasNext();)
            {
            	accountIdSqlsequence = accountIdSqlsequence + vFwdNrsIter.next();
                if (vFwdNrsIter.hasNext())
                {
                	accountIdSqlsequence = accountIdSqlsequence + ",";
                }
            }
            accountIdSqlsequence = accountIdSqlsequence + ")";
            DecimalFormat vCostFormatter = new DecimalFormat("#0.000");
            // ugly way to force for '.' decimal separator. But Java is realy persistent on using dodgy OS settings that are hard control.
            double rangeLow = Double.parseDouble(vCostFormatter.format(inclBtwCost /1.21 - 0.015).replace(',', '.'));
            double rangeHigh = Double.parseDouble(vCostFormatter.format(inclBtwCost /1.21 + 0.015).replace(',', '.'));
            return executeSqlQuery(webSession, "SELECT * FROM InvoiceEntity WHERE IsInvoiceMailed=TRUE AND CreditId=-1 AND TotalCost BETWEEN " + rangeLow + " AND " + rangeHigh + " AND AccountID " + accountIdSqlsequence); 
        }
        else
        {
            System.out.println("getInvoicesByValueAndFwdNrs: accountID's null or empty");
        }
        return new Vector<InvoiceEntityData>();
    }

    public Collection<InvoiceEntityData> getUnpayedInvoicesByAccounts(WebSession webSession, Collection<Integer> accountIDs)
    {
        if (accountIDs != null && !accountIDs.isEmpty())
        {
            String accountIdSqlsequence = "IN (";
            
            for (Iterator<Integer> vFwdNrsIter = accountIDs.iterator(); vFwdNrsIter.hasNext();)
            {
            	accountIdSqlsequence = accountIdSqlsequence + vFwdNrsIter.next();
                if (vFwdNrsIter.hasNext())
                {
                	accountIdSqlsequence = accountIdSqlsequence + ",";
                }
            }
            accountIdSqlsequence = accountIdSqlsequence + ")";
            return executeSqlQuery(webSession, "SELECT * FROM InvoiceEntity WHERE IsInvoiceMailed=TRUE AND IsPayed=false AND CreditId=-1 AND AccountID " + accountIdSqlsequence); 
        }
        else
        {
            System.out.println("getUnpayedInvoicesByFwdNr: FwdNrs nulll or empty");
        }
        return new Vector<InvoiceEntityData>();
    }

    public Collection<InvoiceEntityData> getInvoiceByStructuredId(WebSession webSession, String structuredId)
    {
        if (structuredId != null)
        {
            return executeSqlQuery(webSession, "SELECT * FROM InvoiceEntity WHERE IsInvoiceMailed=TRUE AND CreditId=-1 AND StructuredId='" + structuredId + "'"); 
        }
        return new Vector<InvoiceEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<InvoiceEntityData> getInvoiceList(WebSession webSession, int accountId, int month, int year)
    {
        return executeSqlQuery(webSession, "SELECT * FROM InvoiceEntity WHERE AccountID=" + accountId + " AND Month=" + month + " AND Year=" + year + " ORDER BY AccountID DESC");
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<InvoiceEntityData> getInvoiceList(WebSession webSession, int month, int year)
    {
        return executeSqlQuery(webSession, "SELECT * FROM InvoiceEntity WHERE Month=" + month + " AND Year=" + year + " ORDER BY YearSeqNr DESC");
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<InvoiceEntityData> getOpenInvoiceList(WebSession webSession)
    {
        return executeSqlQuery(webSession, "SELECT * FROM InvoiceEntity WHERE IsPayed=FALSE AND FrozenFlag=TRUE ORDER BY InvoiceNr DESC");
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public int getNewInvoiceNumber(WebSession webSession, int year)
    {
        try
        {
            Collection<InvoiceEntityData> vInvoiceList = executeSqlQuery(webSession, "SELECT * FROM InvoiceEntity WHERE Year=" + year + " AND FrozenFlag=TRUE ORDER BY YearSeqNr DESC");
            // InvoiceEntityHome vInvoiceHome = getEntityBean();
            // Collection vInvoiceList = vInvoiceHome.findFrozenByYear(year);
            if (vInvoiceList != null && vInvoiceList.size() > 0)
            {
                Iterator<InvoiceEntityData> vIter = vInvoiceList.iterator();
                // InvoiceEntityData vInvoice = ((InvoiceEntity)
                // vIter.next()).getValueObject();
                InvoiceEntityData vInvoice = vIter.next();
                int size = vInvoice.getYearSeqNr() + 1;

                // System.out.println("getNewInvoiceNumber init size = " + size);
                // System.out.println("getNewInvoiceNumber invoicelist size = " +
                // vInvoiceList.size());
                int arr[] = new int[size + 10];
                Arrays.fill(arr, 0, size + 10, 0);
                for (Iterator<InvoiceEntityData> i = vInvoiceList.iterator(); i.hasNext();)
                {
                    vInvoice = i.next();
                    // vInvoice = ((InvoiceEntity) i.next()).getValueObject();
                    // System.out.println("getNewInvoiceNumber nr " +
                    // vInvoice.getYearSeqNr());
                    if (vInvoice.getYearSeqNr() >= size)
                    {
                        // grow the arrList
                        throw new IndexOutOfBoundsException("unexpected invoice seq number " + vInvoice.getYearSeqNr() + ". Max size = " + size);
                    }
                    arr[vInvoice.getYearSeqNr()] = 1;
                }
                for (int i = 1; i < size; ++i)
                {
                    if (arr[i] == 0)
                    {
                        System.out.println("getNewInvoiceNumber found gap in sequence: returns " + i);
                        return i;
                    }
                }
                System.out.println("getNewInvoiceNumber returns next sequence: " + size);
                return size;
            }
            // first invoice of the year
            return 1;
        }
        catch (Exception e)
        {
            System.out.println("Failed to getNewInvoiceNumber");
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public InvoiceEntityData getLastInvoice(WebSession webSession, int accountId, int month, int year)
    {
        try
        {
            Collection<InvoiceEntityData> vInvoiceList = executeSqlQuery(webSession, "SELECT * FROM InvoiceEntity WHERE AccountID=" + accountId + " AND Month=" + month + " AND Year=" + year + " ORDER BY AccountID DESC");
            // System.out.println("getInvoiceList for month " + month + ", year " +
            // year);
            // InvoiceEntityHome vInvoiceHome = getEntityBean();
            // Collection vInvoiceList =
            // vInvoiceHome.findByFwdNrMonthAndYear(fwdNr, month, year);
            // System.out.println("getLastInvoice returns list of " +
            // vInvoiceList.size() + " entries for month " +
            // Constants.MONTHS[month] + ", " + year);

            long vLastStopTimeData = 0;
            InvoiceEntityData vLastInvoice = null;
            for (Iterator<InvoiceEntityData> i = vInvoiceList.iterator(); i.hasNext();)
            {
                // InvoiceEntityData data = ((InvoiceEntity)
                // i.next()).getValueObject();
                InvoiceEntityData data = i.next();
                if (data.getStopTime() > vLastStopTimeData)
                {
                    vLastInvoice = data;
                    vLastStopTimeData = data.getStopTime();
                }
            }
            if (vLastInvoice != null)
                System.out.println("LastInvoice has id " + vLastInvoice.getId());
            return vLastInvoice;
        }
        catch (Exception e)
        {
            System.out.println("Failed to getInvoiceList");
            e.printStackTrace();
        }
        // System.out.println("getInvoiceList for month " + month + ", year " +
        // year);
        return null;
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public void freezeList(WebSession webSession, Collection<Integer> freezeList, int year)
    {
        try
        {
            for (Iterator<Integer> i = freezeList.iterator(); i.hasNext();)
            {
                int vKey = i.next().intValue();
                int vNr = getNewInvoiceNumber(webSession, year);
                if (vNr < 1 || !freezeInvoice(webSession, vKey, vNr))
                {
                	System.out.println("Failed to freeze invoice with key=" + vKey + " and number " + vNr);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Failed to freezeList");
            e.printStackTrace();
        }
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public void mailList(WebSession webSession, Collection<Integer> mailList, int year)
    {
        for (Iterator<Integer> i = mailList.iterator(); i.hasNext();)
        {
            int vKey = i.next().intValue();
            InvoiceEntityData vInvoiceData = getRow(webSession, vKey);
            if (vInvoiceData != null)
            {
                if (mailIt(vInvoiceData))
                {
                    vInvoiceData.setIsInvoiceMailed(true);
                    updateRow(webSession, vInvoiceData);
                }
            }
        }
    }

    public void setListPayed(WebSession webSession, Collection<Integer> freezeList)
    {
        for (Iterator<Integer> i = freezeList.iterator(); i.hasNext();)
        {
            int vKey = i.next().intValue();
            executeSqlQuery(webSession, "UPDATE InvoiceEntity SET IsPayed=true WHERE id=" + vKey);
        }
    }
    

    public Collection<InvoiceEntityData>  getInvoiceListByIdList(WebSession webSession, Collection<Integer> freezeList)
    {
        StringBuffer strBuf = new StringBuffer();
        for (Iterator<Integer> i = freezeList.iterator(); i.hasNext();)
        {
            int vKey = i.next().intValue();
            strBuf.append(",");
            strBuf.append(vKey);
        }
        return executeSqlQuery(webSession, "SELECT * FROM InvoiceEntity WHERE IsInvoiceMailed=TRUE AND Id IN (" + strBuf.toString().substring(1) + ")"); 
    }

    public void setPaymentInfo(WebSession webSession, int id, FintroPayment payment)
    {
        executeSqlQuery(webSession, "UPDATE InvoiceEntity SET IsPayed=true, FintroId='" + payment.id + "', PayDate='" + payment.payDate + "', ValutaDate='" + payment.valutaDate + "', FromBankNr='" + payment.accountNrCustomer +"', PaymentDetails='" + payment.details + "' WHERE id=" + id);
    }

    public void setAccountId(WebSession webSession, int key, int accountID, String accountName)
    {
        executeSqlQuery(webSession, "UPDATE InvoiceEntity SET AccountID=" + accountID + ", CustomerName='" + accountName  + "' WHERE id=" + key);
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public boolean freezeInvoice(WebSession webSession, int key, int invoiceNr)
    {
        InvoiceEntityData vInvoiceData = getRow(webSession, key);
        if (vInvoiceData != null && vInvoiceData.getFrozenFlag() == false)
        {
            //System.out.println("freezeInvoice: current year seq nr:" + vInvoiceData.getYearSeqNr());
            if (vInvoiceData.getYearSeqNr() < 1)
            {
                Calendar vCalendar = Calendar.getInstance();
                long now = vCalendar.getTimeInMillis();
                if (now < vInvoiceData.getStopTime())
                    vInvoiceData.setStopTime(now);
                vInvoiceData.setFrozenFlag(false);
                vInvoiceData.setYearSeqNr(invoiceNr);
                vInvoiceData.setInvoiceDate(String.format("%d/%d/%d", vCalendar.get(Calendar.DAY_OF_MONTH), vCalendar.get(Calendar.MONTH) + 1, vCalendar.get(Calendar.YEAR)));
                vInvoiceData.setInvoiceNr(InvoiceHelper.getInvoiceNumber(vInvoiceData.getYear(), vInvoiceData.getMonth(), invoiceNr));
                vInvoiceData.setStructuredId(IBANCheckDigit.IBAN_CHECK_DIGIT.calculateOGM(vInvoiceData.getInvoiceNr()));
                vInvoiceData.setFileName(InvoiceHelper.makeFileName(vInvoiceData));
                vInvoiceData.setPayDate("");
                System.out.println("freezeInvoice: new frozen number:" + invoiceNr);
            }
            InvoiceHelper vHelper = new InvoiceHelper(vInvoiceData, webSession);
            vHelper.storeOrUpdate(webSession);
            vHelper.generatePdfInvoice();
            // set invoiceID on tasks
            
            Collection<TaskEntityData> tasks = vHelper.getTasks();
            TaskSqlAdapter vTaskSession = new TaskSqlAdapter();
            
            for (Iterator<TaskEntityData> i = tasks.iterator(); i.hasNext();)
            {
                TaskEntityData task = i.next();
                vTaskSession.setInvoiceId(webSession, task.getId(), vInvoiceData.getId());
            }
            // replace windows style '\\' with unix style '/'. DB does not seem
            // to handle good the windows style
            vInvoiceData.setFileName(escapeQuotes(vInvoiceData.getFileName().replace('\\', '/')));
            vInvoiceData.setFrozenFlag(true);
            updateRow(webSession, vInvoiceData);
            return true;
        }
        return false;
    }

    private boolean mailIt(InvoiceEntityData invoiceData)
    {
        if (invoiceData == null || invoiceData.getFileName() == null || invoiceData.getFileName().length() == 0)
        {
            //System.out.println("Invoice not froozen for " + invoiceData.getAccountId());
            return false;
        }

        AccountEntityData vCustomer = null;
        Address[] vTo = new InternetAddress[1];
        try
        {
            vCustomer = AccountCache.getInstance().get(invoiceData);

            BufferedReader reader = new BufferedReader(new FileReader(Constants.INVOICE_DIR + "\\factuurMail.txt"));
            String vBody = reader.readLine() + "\r\n";

            String strLine;
            // Read File Line By Line
            while ((strLine = reader.readLine()) != null)
            {
                vBody = vBody.concat(strLine + "\r\n");
            }
            reader.close();

            vBody = vBody.replace("#maand#", Constants.MONTHS[invoiceData.getMonth()]);
            vBody = vBody.replace("#jaar#", Integer.toString(invoiceData.getYear()));

            Date date = new Date();
            

            if (System.getenv("TBA_MAIL_ON") != null)
            {
                String vEmailAddr = vCustomer.getInvoiceEmail();
                if (vEmailAddr == null || vEmailAddr.length() == 0)
                {
                    vEmailAddr = vCustomer.getEmail();
                    if (vEmailAddr == null || vEmailAddr.length() == 0)
                    {
                        System.out.println("Invoice mail can not be send to " + vCustomer.getFullName() + " (no email address specified)");

                        return false;
                    }
                }
                StringTokenizer vMailTokens = new StringTokenizer(vEmailAddr, ";");
                vTo = new InternetAddress[vMailTokens.countTokens()];
                int i = 0;
                while (vMailTokens.hasMoreTokens())
                {
                    vTo[i++] = new InternetAddress(vMailTokens.nextToken());
                }
            }
            else
            {
                vTo = new InternetAddress[1];
                vTo[0] = new InternetAddress("girardelli65@gmail.com");
            }
            InitialContext vContext = new InitialContext();
            Session vSession = null;

            //vSession = (Session) PortableRemoteObject.narrow(vContext.lookup("java:comp/env/mail/Session"), Session.class);
            vSession = (Session) vContext.lookup("java:comp/env/mail/Session");
            
            System.out.println("mail session=" + vSession);
            
            MimeMessage m = new MimeMessage(vSession);
            m.setFrom();

            m.setRecipients(Message.RecipientType.TO, vTo);
            m.setSubject("Factuur maand " + Constants.MONTHS[invoiceData.getMonth()]);
            m.setSentDate(date);
            // m.setContent(vBody.toString(), "text/html");

            MimeBodyPart messagePart = new MimeBodyPart();
            messagePart.setText(vBody.toString());

            //
            // Set the email attachment file
            //
            File attach = new File(invoiceData.getFileName());
            MimeBodyPart attachmentPart = new MimeBodyPart();
            FileDataSource fileDataSource = new FileDataSource(attach)
            {
                // @Override
                public String getContentType()
                {
                    return "application/octet-stream";
                }
            };
            attachmentPart.setDataHandler(new DataHandler(fileDataSource));
            attachmentPart.setFileName(invoiceData.getInvoiceNr() + ".pdf");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messagePart);
            multipart.addBodyPart(attachmentPart);

            m.setContent(multipart);
            Transport.send(m);

            System.out.println("Invoice mailed to " + vCustomer.getFullName() + " (" + vTo[0] + ")");
            return true;
        }
        catch (Exception e)
        {
            if (vCustomer != null)
            {
                System.out.println("Invoice mail can not be send to " + vCustomer.getFullName() + " (" + vTo[0] + ")");
            }
            e.printStackTrace();
        }
        return false;
    }

    protected Vector<InvoiceEntityData> translateRsToValueObjects(ResultSet rs) throws SQLException
    {
        Vector<InvoiceEntityData> vVector = new Vector<InvoiceEntityData>();
        while (rs.next())
        {
            InvoiceEntityData entry = new InvoiceEntityData();
            entry.setId(rs.getInt(1));
            entry.setFileName(null2EmpthyString(rs.getString(2)));
            entry.setAccountID(rs.getInt(3));
            entry.setAccountFwdNr(null2EmpthyString(rs.getString(4)));
            entry.setTotalCost(rs.getDouble(5));
            entry.setMonth(rs.getInt(6));
            entry.setYear(rs.getInt(7));
            entry.setYearSeqNr(rs.getInt(8));
            entry.setInvoiceNr(null2EmpthyString(rs.getString(9)));
            entry.setFrozenFlag(rs.getBoolean(10));
            entry.setIsPayed(rs.getBoolean(11));
            entry.setStartTime(rs.getLong(12));
            entry.setStopTime(rs.getLong(13));
            entry.setCustomerName(null2EmpthyString(rs.getString(14)));
            entry.setIsInvoiceMailed(rs.getBoolean(15));
            entry.setInvoiceDate(null2EmpthyString(rs.getString(16)));
            entry.setCustomerRef(null2EmpthyString(rs.getString(17)));
            entry.setPayDate(null2EmpthyString(rs.getString(18)));
            entry.setCreditId(rs.getInt(19));
            entry.setFintroId(null2EmpthyString(rs.getString(20)));
            entry.setValutaDate(null2EmpthyString(rs.getString(21)));
            entry.setFromBankNr(null2EmpthyString(rs.getString(22)));
            entry.setPaymentDetails(null2EmpthyString(rs.getString(23)));
            entry.setStructuredId(null2EmpthyString(rs.getString(24)));
            vVector.add(entry);
            // System.out.println("InvoiceEntityData: " + entry.toNameValueString());
        }
        return vVector;
    }

    /**
     * Describes the instance and its content for debugging purpose
     *
     * @return Debugging information about the instance and its content
     */
    public String toString()
    {
        return "InvoiceSession [ " + " ]";
    }

}
