package be.tba.ejb.invoice.session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
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
import javax.rmi.PortableRemoteObject;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.invoice.interfaces.InvoiceEntityData;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;
import be.tba.util.data.AbstractSqlAdapter;
import be.tba.util.invoice.InvoiceHelper;
import be.tba.util.session.AccountCache;
import be.tba.util.timer.CallCalendar;

import java.sql.SQLException;
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
        addRow(webSession.getConnection(), data);
        Collection<InvoiceEntityData> invoices = executeSqlQuery(webSession.getConnection(), "SELECT * FROM InvoiceEntity WHERE AccountFwdNr='" + data.getAccountFwdNr() + "' AND StartTime=" + data.getStartTime() + " AND StopTime=" + data.getStopTime());
        return ((InvoiceEntityData) invoices.toArray()[0]).getId();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public InvoiceEntityData getInvoice(WebSession webSession, String key)
    {
        return getRow(webSession.getConnection(), Integer.parseInt(key));
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<InvoiceEntityData> getInvoiceList(WebSession webSession, String fwdNr, int month, int year)
    {
        return executeSqlQuery(webSession.getConnection(), "SELECT * FROM InvoiceEntity WHERE AccountFwdNr='" + fwdNr + "' AND Month=" + month + " AND Year=" + year + " ORDER BY AccountFwdNr DESC");
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<InvoiceEntityData> getInvoiceList(WebSession webSession, int month, int year)
    {
        return executeSqlQuery(webSession.getConnection(), "SELECT * FROM InvoiceEntity WHERE Month=" + month + " AND Year=" + year + " ORDER BY YearSeqNr DESC");
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<InvoiceEntityData> getOpenInvoiceList(WebSession webSession)
    {
        return executeSqlQuery(webSession.getConnection(), "SELECT * FROM InvoiceEntity WHERE IsPayed=FALSE AND FrozenFlag=TRUE ORDER BY YearSeqNr DESC");
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public int getNewInvoiceNumber(WebSession webSession, int year)
    {
        try
        {
            Collection<InvoiceEntityData> vInvoiceList = executeSqlQuery(webSession.getConnection(), "SELECT * FROM InvoiceEntity WHERE Year=" + year + " AND FrozenFlag=TRUE ORDER BY YearSeqNr DESC");
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
    public InvoiceEntityData getLastInvoice(WebSession webSession, String fwdNr, int month, int year)
    {
        try
        {
            Collection<InvoiceEntityData> vInvoiceList = executeSqlQuery(webSession.getConnection(), "SELECT * FROM InvoiceEntity WHERE AccountFwdNr='" + fwdNr + "' AND Month=" + month + " AND Year=" + year + " ORDER BY AccountFwdNr DESC");
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
                    break;
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
            InvoiceEntityData vInvoiceData = getRow(webSession.getConnection(), vKey);
            if (vInvoiceData != null)
            {
                if (mailIt(vInvoiceData))
                {
                    vInvoiceData.setIsInvoiceMailed(true);
                    updateRow(webSession.getConnection(), vInvoiceData);
                }
            }
        }
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public void setListPayed(WebSession webSession, Collection<Integer> freezeList)
    {
        for (Iterator<Integer> i = freezeList.iterator(); i.hasNext();)
        {
            int vKey = i.next().intValue();
            InvoiceEntityData vInvoiceData = getRow(webSession.getConnection(), vKey);
            if (vInvoiceData != null)
            {
                vInvoiceData.setIsPayed(true);
                updateRow(webSession.getConnection(), vInvoiceData);
            }
        }
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public boolean freezeInvoice(WebSession webSession, int key, int invoiceNr)
    {
        InvoiceEntityData vInvoiceData = getRow(webSession.getConnection(), key);
        if (vInvoiceData != null)
        {
            System.out.println("freezeInvoice: current year seq nr:" + vInvoiceData.getYearSeqNr());
            if (vInvoiceData.getYearSeqNr() < 1)
            {
                CallCalendar vCalendar = new CallCalendar();
                long now = vCalendar.getCurrentTimestamp();
                if (now < vInvoiceData.getStopTime())
                    vInvoiceData.setStopTime(now);
                vInvoiceData.setFrozenFlag(true);
                vInvoiceData.setYearSeqNr(invoiceNr);
                vInvoiceData.setInvoiceNr(InvoiceHelper.getInvoiceNumber(vInvoiceData.getYear(), vInvoiceData.getMonth(), invoiceNr));
                vInvoiceData.setFileName(InvoiceHelper.makeFileName(vInvoiceData));
                vInvoiceData.setPayDate("");
                System.out.println("freezeInvoice: new frozen number:" + invoiceNr);
            }
            InvoiceHelper vHelper = new InvoiceHelper(vInvoiceData, webSession);
            vHelper.storeOrUpdate(webSession);
            vHelper.generatePdfInvoice();
            // replace windows style '\\' with unix style '/'. DB does not seem
            // to handle good the windows style
            String unixStyle = vInvoiceData.getFileName().replace('\\', '/');
            executeSqlQuery(webSession.getConnection(), "UPDATE InvoiceEntity SET FileName='" + escapeQuotes(unixStyle) + "', YearSeqNr=" + vInvoiceData.getYearSeqNr() + ",InvoiceNr='" + vInvoiceData.getInvoiceNr() + "',FrozenFlag=true WHERE id=" + key);

            // vInvoice.setValueObject(vInvoiceData);
            return true;
        }
        return false;
    }

    private boolean mailIt(InvoiceEntityData invoiceData)
    {
        if (invoiceData == null || invoiceData.getFileName() == null || invoiceData.getFileName().length() == 0)
        {
            System.out.println("Invoice not froozen for " + invoiceData.getAccountFwdNr());
            return false;
        }

        AccountEntityData vCustomer = null;
        try
        {
            vCustomer = AccountCache.getInstance().get(invoiceData.getAccountFwdNr());

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
            Address[] vTo;

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

            vSession = (Session) PortableRemoteObject.narrow(vContext.lookup("java:comp/env/mail/Session"), Session.class);
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

            System.out.println("Invoice mailed to " + vCustomer.getFullName() + " (" + vCustomer.getEmail() + ")");
            return true;
        }
        catch (Exception e)
        {
            if (vCustomer != null)
            {
                System.out.println("Invoice mail can not be send to " + vCustomer.getFullName() + " (" + vCustomer.getEmail() + ")");
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
            entry.setAccountFwdNr(null2EmpthyString(rs.getString(3)));
            entry.setTotalCost(rs.getDouble(4));
            entry.setMonth(rs.getInt(5));
            entry.setYear(rs.getInt(6));
            entry.setYearSeqNr(rs.getInt(7));
            entry.setInvoiceNr(null2EmpthyString(rs.getString(8)));
            entry.setFrozenFlag(rs.getBoolean(9));
            entry.setIsPayed(rs.getBoolean(10));
            entry.setStartTime(rs.getLong(11));
            entry.setStopTime(rs.getLong(12));
            entry.setCustomerName(null2EmpthyString(rs.getString(13)));
            entry.setIsInvoiceMailed(rs.getBoolean(14));
            entry.setCustomerRef(null2EmpthyString(rs.getString(15)));
            entry.setPayDate(null2EmpthyString(rs.getString(16)));
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
        return "InvoiceSessionBean [ " + " ]";
    }

}
