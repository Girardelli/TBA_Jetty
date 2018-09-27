package be.tba.util.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Collection;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.invoice.interfaces.InvoiceEntityData;
import be.tba.ejb.invoice.session.InvoiceSqlAdapter;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;
import be.tba.util.data.FintroPayment;
import be.tba.util.session.AccountCache;

final class InvoicePaymentStr
{
    public InvoiceEntityData invoice = null;
    public FintroPayment payment = null;
    
    InvoicePaymentStr(InvoiceEntityData invoice, FintroPayment payment)
    {
        this.invoice = invoice;
        this.payment = payment;
    }
}


final public class FintroXlsxReader
{
    final static Logger sLogger = LoggerFactory.getLogger(FintroXlsxReader.class);

    // Fintro excel sheet collumns
    private static int ID = 0;
    private static int EXEC_DATE = 1;
    private static int VALUTA_DATE = 2;
    private static int AMOUNT = 3;
    private static int CUST_ACCOUNT = 5;
    private static int DETAILS = 6;
    
    private WebSession mWebSession;
    private InvoiceSqlAdapter mInvoiceSession;
    private Map<String, Collection<FintroPayment>> mPaymentsMap = null;
    private Collection<InvoiceEntityData> mNewPayedInvoices= null;
    private Collection<InvoiceEntityData> mConfirmedPayedInvoices= null;
    private Collection<FintroPayment> mNotMatchingPayments = null;
    private Collection<InvoicePaymentStr> mWrongValuePayments = null;
    private Collection<FintroPayment> mUnknownAccountNrs = null;
    private String mHtmlProcessLog;
    private String mTxtProcessLog;
    private File mInputFile;
    private String mOutputFileName;
    
    public FintroXlsxReader(String input)
    {
        OPCPackage pkg = null;
        XSSFWorkbook wb;
        mInputFile = new File(input);
        mPaymentsMap = new HashMap<String, Collection<FintroPayment>>();
        mNewPayedInvoices = new Vector<InvoiceEntityData>();
        mConfirmedPayedInvoices = new Vector<InvoiceEntityData>();
        mNotMatchingPayments = new Vector<FintroPayment>();
        mUnknownAccountNrs = new Vector<FintroPayment>();
        mWrongValuePayments = new Vector<InvoicePaymentStr>();
        
        try
        {
            sLogger.info("FintroXlsxReader constructor for file: " + input);
            mWebSession = new WebSession();
            mInvoiceSession = new InvoiceSqlAdapter();
            
         // XSSFWorkbook, File
            pkg = OPCPackage.open(mInputFile);
            wb = new XSSFWorkbook(pkg);
            Sheet sheet = wb.getSheetAt(0);
            //for (int i = 1; i < 10; ++i)
            
            // Decide which rows to process
            int rowStart = 1;
            int rowEnd = Math.min(1500, sheet.getLastRowNum());
            sLogger.info("last collumn: "+ sheet.getLastRowNum());
            int i;
            for (i = rowStart; i < rowEnd; i++) 
            {
               Row row = sheet.getRow(i);
               if (row == null) 
               {
                  // This whole row is empty
                  // Handle it as needed
                  continue;
               }
               if (row.getCell(ID, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) == null)
               {
                   continue;
               }

               //Row row = sheet.getRow(i);
               FintroPayment entry = new FintroPayment();
               entry.id = row.getCell(ID).toString();
               entry.payDate= row.getCell(EXEC_DATE).toString();
               entry.valutaDate= row.getCell(VALUTA_DATE).toString();
               entry.amount= row.getCell(AMOUNT).getNumericCellValue();
               entry.accountNrCustomer= row.getCell(CUST_ACCOUNT).toString();
               entry.details= row.getCell(DETAILS).toString();
               // remove ' and " chars
               entry.details = entry.details.replace('\'', ' ');
               entry.details = entry.details.replace('\"', ' ');
               if (entry.amount > 0)
               {
                   Collection<FintroPayment> customerPaymentList = mPaymentsMap.get(entry.accountNrCustomer);
                   if (customerPaymentList == null)
                   {
                       customerPaymentList = new Vector<FintroPayment>();
                       mPaymentsMap.put(entry.accountNrCustomer, customerPaymentList);
                   }
                   customerPaymentList.add(entry);
                   sLogger.info("entry added: size=" + mPaymentsMap.size() + " ##" + entry.toString());
               }
               else
               {
                   sLogger.info("outgoing payment!! " + entry.toString());
               }
                       
            }
            sLogger.info("--------------------------------------------------------");
            processPaymentsMap();
            createProcessLogs();
        }
        catch (IOException | InvalidFormatException | SQLException e)
        {
            e.printStackTrace(); 
        }
        finally
        {
            try
            {
                if (pkg != null)
                    pkg.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        
    }
    
    public Map<String, Collection<FintroPayment>> getPaymentsMap()
    {
        return mPaymentsMap; 
    }
    
    public void processPaymentsMap()
    {
        for (Iterator<String> vIter = mPaymentsMap.keySet().iterator(); vIter.hasNext();)
        {
            String accountNrCustomer = vIter.next();
            Collection<FintroPayment> payments = mPaymentsMap.get(accountNrCustomer);
            Collection<String> fwdNrs = AccountCache.getInstance().getFwdNumbersForAccountNr(accountNrCustomer);
            if (fwdNrs.isEmpty())
            {
                // just store the first payment of this payment list made via the unknown accountNr. 
                mUnknownAccountNrs.add((FintroPayment) payments.toArray()[0]);
                continue;
            }
            
            for (Iterator<FintroPayment> vPayIter = payments.iterator(); vPayIter.hasNext();)
            {
                FintroPayment payment = vPayIter.next();
                int i = payment.details.indexOf("+++");
                if (i >= 0)
                {
                    // details hold a structured ID
                    
                    String structuredId = payment.details.substring(i, i + 20);
                    Collection<InvoiceEntityData> vInvoices = mInvoiceSession.getUnpayedInvoiceByStructuredId(mWebSession, structuredId);
                    if (vInvoices.size() == 1)
                    {
                        InvoiceEntityData invoice = (InvoiceEntityData) vInvoices.toArray()[0];
                        AccountEntityData account = AccountCache.getInstance().get(invoice.getAccountFwdNr());
                        double inclBtw = (account.getNoBtw() ? invoice.getTotalCost() : invoice.getTotalCost() * 1.21);
                        if (accountNrCustomer.equals(payment.accountNrCustomer) &&
                                inclBtw > payment.amount - 0.015 && inclBtw < payment.amount + 0.015  )
                        {
                            FillInvoiceWithPaymentInfo(invoice, payment);
                            break;
                        }
                    }
                }
                
                Collection<InvoiceEntityData> vInvoices = mInvoiceSession.getUnpayedInvoicesByValueAndFwdNrs(mWebSession, fwdNrs, payment.amount);
                if (!vInvoices.isEmpty())
                {
                    if (vInvoices.size() == 1)
                    {
                        InvoiceEntityData invoice = (InvoiceEntityData) vInvoices.toArray()[0];
                        //sLogger.info("1 matching invoice found." + " Payment=" + payment.id + " [" + payment.amount + "], " + invoice.getAccountFwdNr() + ", " + payment.details);
                        FillInvoiceWithPaymentInfo(invoice, payment);
                    }
                    else
                    {
                        // multiple invoices to this customer FWD number match the payment. Try searching for the factuur nummer
                        boolean match = false;
                        int notPayedCnt = 0;
                        InvoiceEntityData notPayedInvoice = null;
                        for (Iterator<InvoiceEntityData> invoiceIter = vInvoices.iterator(); invoiceIter.hasNext();)
                        {
                            InvoiceEntityData invoice = invoiceIter.next();
                            if (!invoice.getIsPayed())
                            {
                               ++notPayedCnt;
                               notPayedInvoice = invoice;
                            }
                            if (invoice.getInvoiceNr().length() < 9)
                            {
                                sLogger.info("Invoice number to short: " + invoice.getInvoiceNr());
                                mNotMatchingPayments.add(payment);
                            }
                            else
                            {
                                if (isInvoiceNrFoundInDetail(invoice.getInvoiceNr(), payment.details)) 
                                {
                                    //sLogger.info("matching invoice found on factuur nummer in details." + " Payment=" + payment.id + " [" + payment.amount + "], " + ", " + invoice.getAccountFwdNr() + ", " + payment.details);
                                    match = true;
                                    FillInvoiceWithPaymentInfo(invoice, payment);
                                    break;
                                }
                            }
                        }
                        if (!match)
                        {
                            // no match
                            if (notPayedCnt == 1) 
                            {
                                // But from the multi's only 1 was not yet payed --> conclude that this payment is for the invoice
                                FillInvoiceWithPaymentInfo(notPayedInvoice, payment);
                            }
                            else
                            {
                                sLogger.info("could not select from multiple invoice matches." + " Payment=" + payment.id + " [" + payment.amount/1.21 + "], " + " account=" + payment.accountNrCustomer + ", " + payment.details);
                                mNotMatchingPayments.add(payment);
                            }
                        }
                    }
                }
                else
                {
                    // no invoices were returned for cost and customer
                    // try searching for an invoice number to provide actionable information
                    //
                    // get open invoices for the customer:
                    boolean isInvoiceNrFound = false;
                    InvoiceEntityData openInvoice = null;
                    Collection<InvoiceEntityData> openInvoices = mInvoiceSession.getUnpayedInvoicesByFwdNrs(mWebSession, fwdNrs);
                    for (Iterator<InvoiceEntityData> invoiceIter = openInvoices.iterator(); invoiceIter.hasNext();)
                    {
                        openInvoice = invoiceIter.next();
                        if (isInvoiceNrFoundInDetail(openInvoice.getInvoiceNr(), payment.details))
                        {
                            isInvoiceNrFound = true;
                            break;
                        }
                    }
                    if (isInvoiceNrFound)
                    {
                        sLogger.info("Wrong payment."  + " Payment=" + payment.id + " [" + payment.amount/1.21 + "], " + " account=" + payment.accountNrCustomer + ", "+ payment.details);
                        mWrongValuePayments.add(new InvoicePaymentStr(openInvoice, payment));
                    }
                    else
                    {
                        sLogger.info("no matching invoice found."  + " Payment=" + payment.id + " [" + payment.amount/1.21 + "], " + " account=" + payment.accountNrCustomer + ", "+ payment.details);
                        mNotMatchingPayments.add(payment);
                    }
                }
            }
        }
    }
    
    public Collection<InvoiceEntityData> getNewPayedInvoices()
    {
        return mNewPayedInvoices;
    }
    
    public Collection<InvoiceEntityData> getConfirmedPayedInvoices()
    {
        return mConfirmedPayedInvoices;
    }
    
    public Collection<FintroPayment> getNotMatchingPayments()
    {
        return mNotMatchingPayments;
    }
    
    public Collection<FintroPayment> getUnknownAccountNrs()
    {
        return mUnknownAccountNrs;
    }
    
    public String getHtmlProcessLog()
    {
        return mHtmlProcessLog;
    }
       
    public String getTxtProcessLog()
    {
        return mTxtProcessLog;
    }
    
    public String getOutputFileName()
    {
        return mOutputFileName;
    }
       
    private void FillInvoiceWithPaymentInfo(InvoiceEntityData invoice, FintroPayment payment)
    {
        mInvoiceSession.setPaymentInfo(mWebSession, invoice.getId(), payment);
        if (invoice.getIsPayed())
        {
            mConfirmedPayedInvoices.add(invoice);
        }
        else
        {
            mNewPayedInvoices.add(invoice);
        }
    }
    
    private void createProcessLogs() throws IOException
    {
        DecimalFormat vCostFormatter = new DecimalFormat("#0.00");
        StringBuilder strBuf = new StringBuilder();
        for (Iterator<InvoiceEntityData> vPayIter = mConfirmedPayedInvoices.iterator(); vPayIter.hasNext();)
        {
           InvoiceEntityData invoice = vPayIter.next();
           strBuf.append(invoice.getInvoiceNr());
           strBuf.append(',');      
        }
        //System.out.println("\r\nConfirmed payed invoices: \r\n" + strBuf.toString());
        String confirmedPayments = strBuf.toString();
        strBuf = new StringBuilder();
        for (Iterator<InvoiceEntityData> vPayIter = mNewPayedInvoices.iterator(); vPayIter.hasNext();)
        {
           InvoiceEntityData invoice = vPayIter.next();
           strBuf.append(invoice.getInvoiceNr());
           strBuf.append(',');      
        }
        String newPayedInvoices = strBuf.toString();
        //System.out.println("\r\nNew payed invoices: \r\n" + strBuf.toString());
        strBuf = new StringBuilder();
        for (Iterator<FintroPayment> vPayIter = mNotMatchingPayments.iterator(); vPayIter.hasNext();)
        {
           FintroPayment payment = vPayIter.next();
           strBuf.append("Bedrag: " + payment.amount + " (excl BTW: " + vCostFormatter.format(payment.amount/1.21) + ")<br>" + payment.details + "<br><br>");
        }
        String notMatchingPayments = strBuf.toString();
        //System.out.println("\r\nNot matching payments: \r\n" + strBuf.toString());
        strBuf = new StringBuilder();
        for (Iterator<FintroPayment> vPayIter = mUnknownAccountNrs.iterator(); vPayIter.hasNext();)
        {
           FintroPayment payment = vPayIter.next();
           strBuf.append("Bedrag: " + payment.amount + " (excl BTW: " + vCostFormatter.format(payment.amount/1.21) + ")<br>Van Banknummer:  " + payment.accountNrCustomer + "<br>" + payment.details + "<br><br>");
        }
        String unknownAccountNrs = strBuf.toString();
        
        strBuf = new StringBuilder();
        for (Iterator<InvoicePaymentStr> vInvoicePaymentIter = mWrongValuePayments.iterator(); vInvoicePaymentIter.hasNext();)
        {
            InvoicePaymentStr invoicePayment = vInvoicePaymentIter.next();
           strBuf.append("Factuur " + invoicePayment.invoice.getInvoiceNr() + ", bedrag: " + vCostFormatter.format(invoicePayment.invoice.getTotalCost()*1.21) + " (Excl BTW)=" + invoicePayment.invoice.getTotalCost() + "<br>Bedrag betaald:  " + invoicePayment.payment.amount + " (excl BTW: " + vCostFormatter.format(invoicePayment.payment.amount/1.21) + ")<br>Van Banknummer:  " + invoicePayment.payment.accountNrCustomer + "<br>" + invoicePayment.payment.details + "<br><br>");
        }
        String wrongValuePayments = strBuf.toString();

        StringBuilder htmlProcessLogStrBuf = new StringBuilder();
        htmlProcessLogStrBuf.append("<b>Nog niet gekende rekeningnummers (of geen klant/factuur betaling):</b><br>");
        htmlProcessLogStrBuf.append(unknownAccountNrs);
        htmlProcessLogStrBuf.append("<b>Foutieve betalingen:</b><br>");
        htmlProcessLogStrBuf.append(wrongValuePayments);
        htmlProcessLogStrBuf.append("<br><b>Niet erkende betalingen:</b><br>");
        htmlProcessLogStrBuf.append(notMatchingPayments);
        htmlProcessLogStrBuf.append("<br><b>Bevestigde betalingen (waren al als 'betaald' gezet):</b><br>");
        htmlProcessLogStrBuf.append(confirmedPayments);
        htmlProcessLogStrBuf.append("<br><br><b>Nieuwe betalingen:</b><br>");
        htmlProcessLogStrBuf.append(newPayedInvoices);
        htmlProcessLogStrBuf.append("<br><br>");
        mHtmlProcessLog = htmlProcessLogStrBuf.toString();
        mTxtProcessLog = mHtmlProcessLog.replaceAll("<b>", "");
        mTxtProcessLog = mTxtProcessLog.replaceAll("</b>", "");
        mTxtProcessLog = mTxtProcessLog.replaceAll("<br>", "\r\n");
        
        mOutputFileName = Constants.TEMP_DIR + "\\" + mInputFile.getName().substring(0, mInputFile.getName().indexOf('.')) + ".txt";
        File output = new File(mOutputFileName);
        output.setWritable(true);
        if (output.exists())
        {
            output.delete();
        }
        if (!output.createNewFile())
        {
            throw new IOException("Cannot write to " + output.getAbsolutePath());
        }
        FileOutputStream oStream = new FileOutputStream(output);
        oStream.write(mTxtProcessLog.getBytes());
        oStream.close();
    }
    
    private boolean isInvoiceNrFoundInDetail(String invoiceNr, String detail)
    {
     // subtract the 2 number parts from e.g. 'N-1801nr57'
        String month = invoiceNr.substring(2, 6);
        String seqnr = invoiceNr.substring(8, invoiceNr.length());
        
        return (detail.indexOf(month) != -1 && detail.indexOf(seqnr) != -1);
    }
}
