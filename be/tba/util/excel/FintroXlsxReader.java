package be.tba.util.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Collection;
import java.util.Date;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
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
   private Collection<InvoiceEntityData> mNewPayedInvoices = null;
   private Collection<InvoiceEntityData> mConfirmedPayedInvoices = null;
   private Collection<FintroPayment> mNotMatchingPayments = null;
   private Collection<InvoicePaymentStr> mWrongValuePayments = null;
   private Collection<FintroPayment> mUnknownAccountNrs = null;
   private Collection<FintroPayment> mErrorPayments = null;
   private String mHtmlProcessLog;
   private String mTxtProcessLog;
   private File mInputFile;
   private String mOutputFileName;
   private StringBuffer mLog;
   private DecimalFormat mCostFormatter;

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
      mErrorPayments = new Vector<FintroPayment>();
      mLog = new StringBuffer();
      mCostFormatter = new DecimalFormat("#0.00");

      try
      {
         sLogger.info("FintroXlsxReader constructor for file: " + input);
         mWebSession = new WebSession();
         mInvoiceSession = new InvoiceSqlAdapter();

         // XSSFWorkbook, File
         pkg = OPCPackage.open(mInputFile, PackageAccess.READ);
         wb = new XSSFWorkbook(pkg);
         Sheet sheet = wb.getSheetAt(0);
         // for (int i = 1; i < 10; ++i)

         // Decide which rows to process
         int rowStart = 1;
         int rowEnd = Math.min(1500, sheet.getLastRowNum()) + 1;
         sLogger.info("last collumn: " + rowEnd);
         int i;
         int cnt = 0;
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

            // Row row = sheet.getRow(i);
            FintroPayment entry = new FintroPayment();
            entry.id = row.getCell(ID).toString();
            
            try 
            {
               // try reading it as a date field. (!!! it is not clear here what the date format was on the computer importing the content)
               Date date = row.getCell(EXEC_DATE).getDateCellValue();
               // serious hack I'm doing here. If the date column does not read in as a string, excel has formated this 
               // cell as date but with a wrong (US) local setting. Therefore treat month as day and day as month!!!!
               SimpleDateFormat dt1 = new SimpleDateFormat("MM/dd/yyyy");
               entry.payDate = dt1.format(date); 
            }
            catch (Exception ex1)
            {
               sLogger.error("date cell cannot be read as text. unknown cell format");
               entry.payDate = row.getCell(EXEC_DATE).toString();
            }
//            sLogger.info(entry.payDate);
            entry.valutaDate = row.getCell(VALUTA_DATE).toString();
            entry.amount = row.getCell(AMOUNT).getNumericCellValue();
            entry.accountNrCustomer = row.getCell(CUST_ACCOUNT).toString();
            entry.details = (row.getCell(DETAILS) == null ? "" : row.getCell(DETAILS).toString());
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
               ++cnt;
               System.out.println("entry added: size=" + cnt + " ##" + entry.toString());
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

//   public Map<String, Collection<FintroPayment>> getPaymentsMap()
//   {
//      return mPaymentsMap;
//   }

   private void processPaymentsMap()
   {
      for (Iterator<String> vIter = mPaymentsMap.keySet().iterator(); vIter.hasNext();)
      {
         // for all bank account numbers in the payment list
         String accountNrCustomer = vIter.next();
         Collection<FintroPayment> paymentsDoneByAccount = mPaymentsMap.get(accountNrCustomer);
         Collection<Integer> accountIds = AccountCache.getInstance().getAccountIdsForBankAccountNr(accountNrCustomer);
         if (accountIds.isEmpty())
         {
            System.out.println("-----------------------------");
            // no TBA customer found with this bank account number
            // just store the first payment of this payment list made via the unknown
            // accountNr.
            System.out.println("no customer found for bank account " + accountNrCustomer + ". " + paymentsDoneByAccount.size() + " payments not processed");
            for (FintroPayment payment : paymentsDoneByAccount)
            {
               mUnknownAccountNrs.add(payment);
            }
            continue;
         }

         for (Iterator<FintroPayment> vPayIter = paymentsDoneByAccount.iterator(); vPayIter.hasNext();)
         {
            System.out.println("-----------------------------");
            // for all payments done via the bank account number accountNrCustomer
            FintroPayment payment = vPayIter.next();
            // System.out.println(payment.details);
            // check for a structured message like: 'MEDEDELING : 181200070764'
            String structuredId = getStructuredId(payment);
            // ------------------------------------------
            // here we start finding a match with invoices
            boolean isMatchFound = false;
            if (!structuredId.isEmpty())
            {
               System.out.println("Structured ID found: " + structuredId);
               Collection<InvoiceEntityData> vInvoices = mInvoiceSession.getInvoiceByStructuredId(mWebSession, structuredId);
               if (vInvoices.size() == 1)
               {
                  // System.out.println("structid found in db");
                  InvoiceEntityData invoice = (InvoiceEntityData) vInvoices.toArray()[0];
                  AccountEntityData account = AccountCache.getInstance().get(invoice);
                  double inclBtw = (account.getNoBtw() ? invoice.getTotalCost() : invoice.getTotalCost() * 1.21);
                  if (accountNrCustomer.equals(payment.accountNrCustomer) && inclBtw > payment.amount - 0.015 && inclBtw < payment.amount + 0.015)
                  {
                     FillInvoiceWithPaymentInfo(invoice, payment);
                  }
                  else
                  {
                     mWrongValuePayments.add(new InvoicePaymentStr(invoice, payment));
                  }
                  // isMatchFound = true;
                  // continue with the next payment
                  continue;
               }
               else
               {
                  mLog.append("ERROR: structid " + structuredId + " in payment " + payment.id  + " not found in db<br>");
                  System.out.println("ERROR: structid not found in db. Id=" + structuredId);
               }
            }
            else
            {
               // System.out.println("NO structid");

            }

            // structured ID didn't work
            Collection<InvoiceEntityData> vInvoices = mInvoiceSession.getInvoicesByValueAndAccounts(mWebSession, accountIds, payment.amount);
            if (!vInvoices.isEmpty())
            {
               if (vInvoices.size() == 1)
               {
                  InvoiceEntityData invoice = (InvoiceEntityData) vInvoices.toArray()[0];
                  // sLogger.info("1 matching invoice found." + " Payment=" + payment.id + " [" +
                  // payment.amount + "], " + invoice.getAccountFwdNr() + ", " + payment.details);
                  FillInvoiceWithPaymentInfo(invoice, payment);
                  isMatchFound = true;
               }
               else
               {
                  // multiple invoices to this customer FWD number match the payment. Try
                  // searching for the factuur nummer
                  boolean isPaid = false;
                  // loop twice over the invoice list with the matching value:
                  // 1ste time only check the not payed invoices
                  // 2nd time the payed ones
                  for (int r = 0; r < 2 && !isMatchFound; ++r)
                  {
                     InvoiceEntityData oldestMatchingInvoice = null;
                     for (Iterator<InvoiceEntityData> invoiceIter = vInvoices.iterator(); invoiceIter.hasNext();)
                     {
                        InvoiceEntityData invoice = invoiceIter.next();
                        if (invoice.getIsPayed() != isPaid)
                        {
                           continue;
                        }
                        if (isInvoiceNrFoundInDetail(invoice.getInvoiceNr(), payment.details))
                        {
                           // sLogger.info("matching invoice found on factuur nummer in details." + "
                           // Payment=" + payment.id + " [" + payment.amount + "], " + ", " +
                           // invoice.getAccountFwdNr() + ", " + payment.details);
                           FillInvoiceWithPaymentInfo(invoice, payment);
                           isMatchFound = true;
                           break;
                        }
                        if (oldestMatchingInvoice == null || invoice.getStopTime() < oldestMatchingInvoice.getStopTime())
                        {
                           oldestMatchingInvoice = invoice;
                        }
                     }
                     if (!isMatchFound)
                     {
                        if (!isPaid)
                        {
                           if (oldestMatchingInvoice != null)
                           {
                              // we found at least 1 matching not payed invoice: take the oldest
                              FillInvoiceWithPaymentInfo(oldestMatchingInvoice, payment);
                              isMatchFound = true;
                           }
                        }
                        else
                        {
                           // multiple
                           if (oldestMatchingInvoice != null)
                           {
                              System.out.println("Confirmed " + oldestMatchingInvoice.getInvoiceNr() + " with \"" + payment.details + "\"");
                              mConfirmedPayedInvoices.add(oldestMatchingInvoice);
                              isMatchFound = true;
                           }
                        }
                     }
                     // do it again but this time loop over the already paid invoices
                     // System.out.println("do it again and try to find it in the paid invoices");
                     isPaid = true;
                  }
               }
               if (!isMatchFound)
               {
                  mNotMatchingPayments.add(payment);
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
               Collection<InvoiceEntityData> openInvoices = mInvoiceSession.getUnpayedInvoicesByAccounts(mWebSession, accountIds);
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
                  // System.out.println("Wrong payment." + " Payment=" + payment.id + " [" +
                  // payment.amount/1.21 + "], " + " account=" + payment.accountNrCustomer + ", "+
                  // payment.details);
                  mWrongValuePayments.add(new InvoicePaymentStr(openInvoice, payment));
               }
               else
               {
                  // System.out.println("no matching invoice found." + " Payment=" + payment.id +
                  // " [" + payment.amount/1.21 + "], " + " account=" + payment.accountNrCustomer
                  // + ", "+ payment.details);
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
      Collection<InvoiceEntityData> alreadyUsedFintroIdInvoices = mInvoiceSession.getInvoiceByFintroId(mWebSession, payment.id);
      if (alreadyUsedFintroIdInvoices.size() > 0)
      {
         InvoiceEntityData dbInvoice = (InvoiceEntityData) alreadyUsedFintroIdInvoices.toArray()[0];
         if (alreadyUsedFintroIdInvoices.size() == 1)
         {
            mConfirmedPayedInvoices.add((InvoiceEntityData) alreadyUsedFintroIdInvoices.toArray()[0]);
            if (dbInvoice.getId() != invoice.getId())
            {
               mLog.append("ERROR: payment " + payment.id  + ": match found on open invoice " + invoice.getInvoiceNr() + " (id=" + invoice.getId() + "), but this payment was already linked in DB to "+ dbInvoice.getInvoiceNr() + " (id=" + dbInvoice.getId() + ")<br>");
            }
         }
         else
         {
            mErrorPayments.add(payment);
            mLog.append("ERROR: payment " + payment.id  + ": already multiple matches found in DB for this FintoId: ");
            for (InvoiceEntityData entry : alreadyUsedFintroIdInvoices)
            {
               mLog.append(", ");
               mLog.append(entry.getInvoiceNr());
            }
            mLog.append("<br>");
         }
      }
      else
      {
         // FintroId not found in the DB
         if (invoice.getIsPayed())
         {
            if (payment.id.equals(invoice.getFintroId()))
            {
               mLog.append("INFO : payment " + payment.id  + ": should have been found in DB based on FinrtoId on invoice " + invoice.getInvoiceNr() + " (id=" + invoice.getId() + ")<br>");
               mConfirmedPayedInvoices.add(invoice);
            }
            else
            {
               mLog.append("ERROR: payment " + payment.id  + ": matches invoice " + invoice.getInvoiceNr() + " (id=" + invoice.getId() + "), but this invoice was set as payed with another FintroId=" + invoice.getFintroId() + "<br>");
               mErrorPayments.add(payment);
            }
         }
         else
         {
            mInvoiceSession.setPaymentInfo(mWebSession, invoice.getId(), payment);
            mNewPayedInvoices.add(invoice);
         }
      }
   }

   private void createProcessLogs() throws IOException
   {
      StringBuilder strBuf = new StringBuilder();
      for (Iterator<InvoiceEntityData> vPayIter = mConfirmedPayedInvoices.iterator(); vPayIter.hasNext();)
      {
         InvoiceEntityData invoice = vPayIter.next();
         strBuf.append(invoice.getInvoiceNr());
         strBuf.append(',');
      }
      // System.out.println("\r\nConfirmed payed invoices: \r\n" + strBuf.toString());
      String confirmedPayments = strBuf.toString();
      strBuf = new StringBuilder();
      for (Iterator<InvoiceEntityData> vPayIter = mNewPayedInvoices.iterator(); vPayIter.hasNext();)
      {
         InvoiceEntityData invoice = vPayIter.next();
         strBuf.append(invoice.getInvoiceNr());
         strBuf.append(',');
      }
      String newPayedInvoices = strBuf.toString();
      // System.out.println("\r\nNew payed invoices: \r\n" + strBuf.toString());
      strBuf = new StringBuilder();
      for (Iterator<FintroPayment> vPayIter = mNotMatchingPayments.iterator(); vPayIter.hasNext();)
      {
         FintroPayment payment = vPayIter.next();
         fillPaymentStrBuffer(payment, strBuf);
      }
      String notMatchingPayments = strBuf.toString();
      // System.out.println("\r\nNot matching payments: \r\n" + strBuf.toString());
      strBuf = new StringBuilder();
      for (Iterator<FintroPayment> vPayIter = mUnknownAccountNrs.iterator(); vPayIter.hasNext();)
      {
         FintroPayment payment = vPayIter.next();
         fillPaymentStrBuffer(payment, strBuf);
      }
      String unknownAccountNrs = strBuf.toString();

      strBuf = new StringBuilder();
      for (Iterator<InvoicePaymentStr> vInvoicePaymentIter = mWrongValuePayments.iterator(); vInvoicePaymentIter.hasNext();)
      {
         InvoicePaymentStr invoicePayment = vInvoicePaymentIter.next();
         strBuf.append("Factuur " + invoicePayment.invoice.getInvoiceNr() + ", bedrag: " + mCostFormatter.format(invoicePayment.invoice.getTotalCost() * 1.21) + " (Excl BTW)=" + invoicePayment.invoice.getTotalCost() + "<br>Bedrag betaald:  " + invoicePayment.payment.amount + " (excl BTW: " + mCostFormatter.format(invoicePayment.payment.amount / 1.21) + ")<br>Van Banknummer:  "
               + invoicePayment.payment.accountNrCustomer + "<br>");
         strBuf.append(invoicePayment.payment.details);
         strBuf.append("<br>FintroId: " + invoicePayment.payment.id);
         strBuf.append("<br>ValutaDate: " + invoicePayment.payment.valutaDate + "<br><br>");
      }
      String wrongValuePayments = strBuf.toString();
      strBuf = new StringBuilder();
      for (Iterator<FintroPayment> vPayIter = mErrorPayments.iterator(); vPayIter.hasNext();)
      {
         FintroPayment payment = vPayIter.next();
         fillPaymentStrBuffer(payment, strBuf);      
      }
      String errorPayments = strBuf.toString();

      StringBuilder htmlProcessLogStrBuf = new StringBuilder();
      if (mLog.length() > 0)
      {
         htmlProcessLogStrBuf.append("<b>Process ERROR Log:</b><br>");
         htmlProcessLogStrBuf.append(mLog.toString() + "<br><br>");
      }
      if (errorPayments.length() > 0)
      {
         htmlProcessLogStrBuf.append("<b>Error payments:</b><br>");
         htmlProcessLogStrBuf.append(errorPayments);
      }
      if (unknownAccountNrs.length() > 0)
      {
         htmlProcessLogStrBuf.append("<b>Nog niet gekende rekeningnummers (of geen klant/factuur betaling):</b><br>");
         htmlProcessLogStrBuf.append(unknownAccountNrs);
      }
      if (wrongValuePayments.length() > 0)
      {
         htmlProcessLogStrBuf.append("<br><b>Foutieve betalingen:</b><br>");
         htmlProcessLogStrBuf.append(wrongValuePayments);
      }
      if (notMatchingPayments.length() > 0)
      {
         htmlProcessLogStrBuf.append("<br><b>Niet erkende betalingen:</b><br>");
         htmlProcessLogStrBuf.append(notMatchingPayments);
      }
      if (confirmedPayments.length() > 0)
      {
         htmlProcessLogStrBuf.append("<br><b>Bevestigde betalingen (waren al als 'betaald' gezet):</b><br>");
         htmlProcessLogStrBuf.append(confirmedPayments);
      }
      if (newPayedInvoices.length() > 0)
      {
         htmlProcessLogStrBuf.append("<br><b>Nieuwe betalingen:</b><br>");
         htmlProcessLogStrBuf.append(newPayedInvoices);
      }
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

   private String getStructuredId(FintroPayment payment)
   {
      String structuredId = "";
      int y = payment.details.indexOf("MEDEDELING : ");
      if (y >= 0 && payment.details.length() > y + 13 + 12)
      {
         y += 13;
         String flatId = payment.details.substring(y, y + 12);
         // System.out.println("Flat Structured ID found: " + flatId);
         if (flatId.length() == 12)
         {
            byte[] numberArr = flatId.getBytes();
            int x = 0;
            while (x < 12 && numberArr[x] >= '0' && numberArr[x] <= '9')
            {
               ++x;
            }
            if (x == 12)
            {
               structuredId = "+++" + flatId.substring(0, 3) + "/" + flatId.substring(3, 7) + "/" + flatId.substring(7) + "+++";
            }
            else
            {
               // System.out.println("x = " + x);
            }
         }
      }
      if (structuredId.isEmpty())
      {
         // check for a structured message like: +++090/9337/55493+++
         int i = payment.details.indexOf("+++");
         if (i >= 0)
         {
            // details hold a structured ID
            structuredId = payment.details.substring(i, i + 20);
         }
      }
      return structuredId;
   }
   
   private boolean isInvoiceNrFoundInDetail(String invoiceNr, String detail)
   {
      // System.out.println("isInvoiceNrFoundInDetail(invoiceNr=" + invoiceNr + ",
      // detail=" + detail + ")");
      if (invoiceNr.length() < 9)
      {
         return false;
      }
      // subtract the 2 number parts from e.g. 'N-1801nr57'
      String month = invoiceNr.substring(2, 6);
      String seqnr = invoiceNr.substring(8, invoiceNr.length());
      // System.out.println("month=" + month + ", seqnr=" + seqnr );

      return (detail.indexOf(month) != -1 && detail.indexOf(seqnr) != -1);
   }
   
   private void fillPaymentStrBuffer(FintroPayment payment, StringBuilder strBuf)
   {
      strBuf.append("Bedrag: " + payment.amount + " (excl BTW: " + mCostFormatter.format(payment.amount / 1.21) + ")<br>");
      strBuf.append("Van Banknummer: " + payment.accountNrCustomer + "<br>");
      strBuf.append(payment.details);
      strBuf.append("<br>FintroId: " + payment.id);
      strBuf.append("<br>ValutaDate: " + payment.valutaDate + "<br><br>");
   }
}
