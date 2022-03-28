package be.tba.util.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Collection;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.session.WebSession;
import be.tba.sqladapters.InvoiceSqlAdapter;
import be.tba.sqldata.AccountCache;
import be.tba.sqldata.AccountEntityData;
import be.tba.sqldata.InvoiceEntityData;
import be.tba.util.constants.Constants;

final class InvoicePaymentStr
{
   public InvoiceEntityData invoice = null;
   public BankPayment payment = null;

   InvoicePaymentStr(InvoiceEntityData invoice, BankPayment payment)
   {
      this.invoice = invoice;
      this.payment = payment;
   }
}

final public class PaymentXlsxReader
{
   private static Logger log = LoggerFactory.getLogger(PaymentXlsxReader.class);

   private WebSession mWebSession;
   private InvoiceSqlAdapter mInvoiceSession;
   private Map<String, Collection<BankPayment>> mPaymentsMap = null;
   private Collection<InvoiceEntityData> mNewPayedInvoices = null;
   private Collection<InvoiceEntityData> mConfirmedPayedInvoices = null;
   private Collection<BankPayment> mNotMatchingPayments = null;
   private Collection<InvoicePaymentStr> mWrongValuePayments = null;
   private Collection<BankPayment> mUnknownAccountNrs = null;
   private Collection<BankPayment> mErrorPayments = null;
   private String mHtmlProcessLog;
   private String mTxtProcessLog;
   private File mInputFile;
   private String mOutputFileName;
   private StringBuilder mLog;
   private DecimalFormat mCostFormatter;
   private PaymentFileHandlerInterf mPaymentFileHandler;

   public PaymentXlsxReader(String input)
   {
      OPCPackage pkg = null;
      XSSFWorkbook wb;
      mInputFile = new File(input);
      mPaymentsMap = new HashMap<String, Collection<BankPayment>>();
      mNewPayedInvoices = new Vector<InvoiceEntityData>();
      mConfirmedPayedInvoices = new Vector<InvoiceEntityData>();
      mNotMatchingPayments = new Vector<BankPayment>();
      mUnknownAccountNrs = new Vector<BankPayment>();
      mWrongValuePayments = new Vector<InvoicePaymentStr>();
      mErrorPayments = new Vector<BankPayment>();
      mLog = new StringBuilder();
      mCostFormatter = new DecimalFormat("#0.00");

      try
      {
         log.info("PaymentXlsxReader constructor for file: " + input);
         mWebSession = new WebSession();
         mInvoiceSession = new InvoiceSqlAdapter();

         // XSSFWorkbook, File
        	pkg = OPCPackage.open(mInputFile, PackageAccess.READ);

         wb = new XSSFWorkbook(pkg);

         Sheet sheet = wb.getSheetAt(0);

         // Decide which rows to process
         int rowStart = 0;
         int rowEnd = Math.min(1500, sheet.getLastRowNum()) + 1;
         if (sheet.getLastRowNum() >= 1500)
         {
            mLog.append("ERROR: file is too long to process. Only 1500 rows shall be processed.<br>");
         }
         
         log.info("last collumn: " + rowEnd);
         int i;
         int cnt = 0;
         
         // determin the xlsx origin:
         // - Fintro
         // - Bank van breda
         Row checkRow = sheet.getRow(rowEnd > 1 ? 1 : (rowEnd - 1));
         
         String checkRowContent = checkRow.getCell(5).toString();
         log.info("check file origin with field: " + checkRowContent);
         if (checkRowContent.equals("EUR"))
         {
            // Bank van Breda
            mPaymentFileHandler = new BankVanBredaHandler();
         }
         else
         {
            //Fintro
            mPaymentFileHandler = new FintroHandler();
         }
         for (i = rowStart; i < rowEnd; i++)
         {
            Row row = sheet.getRow(i);
            if (row == null)
            {
               // This whole row is empty
               // Handle it as needed
               log.info("process row " + i + " =null");

               continue;
            }
            if (row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) == null)
            {
               log.info("process row " + i + " =blank");
               continue;
            }
            if (!mPaymentFileHandler.isValidRow(row))
            {
               log.info("process row " + i + " =not valid");
               continue;
            }
            log.info("process row " + i);
//            log.info("cell 3=" + row.getCell(3).toString());
            
            BankPayment entry = mPaymentFileHandler.parseRow(row);
            log.info("Payment: " + entry.toString());
            
            
            if (entry.amount > 0)
            {
               Collection<BankPayment> customerPaymentList = mPaymentsMap.get(entry.accountNrCustomer);
               if (customerPaymentList == null)
               {
                  customerPaymentList = new Vector<BankPayment>();
                  mPaymentsMap.put(entry.accountNrCustomer, customerPaymentList);
               }
               customerPaymentList.add(entry);
               ++cnt;
               log.trace("entry added: size=" + cnt + " ##" + entry.toString());
            }
         }
         log.info("--------------------------------------------------------");
         mLog.append("INFO: " + cnt + " rows found to be processed.<br>");
         processPaymentsMap();
         if (cnt != (mNewPayedInvoices.size() +
                                       mConfirmedPayedInvoices.size() +
                                       mNotMatchingPayments.size() +
                                       mUnknownAccountNrs.size() + 
                                       mWrongValuePayments.size() + 
                                       mErrorPayments.size()))
         {
            mLog.append("ERROR: not all payments seem to have processed:<br>");
            mLog.append("payments expected to be processed: " + cnt + "<br>");
            mLog.append("processed payments sum up to: " + (mNewPayedInvoices.size() +
                  mConfirmedPayedInvoices.size() +
                  mNotMatchingPayments.size() +
                  mUnknownAccountNrs.size() + 
                  mWrongValuePayments.size() + 
                  mErrorPayments.size()) + "<br>");
            mLog.append("mNewPayedInvoices      : " + mNewPayedInvoices.size() + "<br>");
            mLog.append("mConfirmedPayedInvoices: " + mConfirmedPayedInvoices.size() + "<br>");
            mLog.append("mNotMatchingPayments   : " + mNotMatchingPayments.size() + "<br>");
            mLog.append("mUnknownAccountNrs     : " + mUnknownAccountNrs.size() + "<br>");
            mLog.append("mWrongValuePayments    : " + mWrongValuePayments.size() + "<br>");
            mLog.append("mErrorPayments         : " + mErrorPayments.size() + "<br>");
            
         }
         createProcessLogs();
      }
      catch (Exception e)
      {
      	log.error(e.getMessage(), e);
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
            log.error(e.getMessage(), e);
         }
      }

   }

   private void processPaymentsMap()
   {
      for (Iterator<String> vIter = mPaymentsMap.keySet().iterator(); vIter.hasNext();)
      {
         // for all bank account numbers in the payment list
         String accountNrCustomer = vIter.next();
         Collection<BankPayment> paymentsDoneByAccount = mPaymentsMap.get(accountNrCustomer);
         Collection<Integer> accountIds = AccountCache.getInstance().getAccountIdsForBankAccountNr(accountNrCustomer);
         if (accountIds.isEmpty())
         {
            log.info("-----------------------------");
            // no TBA customer found with this bank account number
            // just store the first payment of this payment list made via the unknown
            // accountNr.
            log.info("no customer found for bank account " + accountNrCustomer + ". " + paymentsDoneByAccount.size() + " payments not processed");
            for (BankPayment payment : paymentsDoneByAccount)
            {
               mUnknownAccountNrs.add(payment);
            }
            continue;
         }

         for (Iterator<BankPayment> vPayIter = paymentsDoneByAccount.iterator(); vPayIter.hasNext();)
         {
            log.info("-----------------------------");
            // for all payments done via the bank account number accountNrCustomer
            BankPayment payment = vPayIter.next();
            // log.info(payment.details);
            // check for a structured message like: 'MEDEDELING : 181200070764'
            String structuredId = getStructuredId(payment);
            // ------------------------------------------
            // here we start finding a match with invoices
            boolean isMatchFound = false;
            if (!structuredId.isEmpty())
            {
               log.info("Structured ID found: " + structuredId);
               Collection<InvoiceEntityData> vInvoices = mInvoiceSession.getInvoiceByStructuredId(mWebSession, structuredId);
               if (vInvoices.size() == 1)
               {
                  // log.info("structid found in db");
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
                  mLog.append("ERROR: structid " + structuredId + " in payment " + payment.id + " not found in db<br>");
                  log.info("ERROR: structid not found in db. Id=" + structuredId);
               }
            }
            else
            {
               // log.info("NO structid");

            }

            // structured ID didn't work
            Collection<InvoiceEntityData> vInvoices = mInvoiceSession.getInvoicesByValueAndAccounts(mWebSession, accountIds, payment.amount);
            if (!vInvoices.isEmpty())
            {
               if (vInvoices.size() == 1)
               {
                  InvoiceEntityData invoice = (InvoiceEntityData) vInvoices.toArray()[0];
                  // log.info("1 matching invoice found." + " Payment=" + payment.id + " [" +
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
                           // log.info("matching invoice found on factuur nummer in details." + "
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
                              if (payment.id.equals(oldestMatchingInvoice.getFintroId()))
                              {
                                 mLog.append("INFO : payment " + payment.id + ": should have been found in DB based on FinrtoId on invoice " + oldestMatchingInvoice.getInvoiceNr() + " (id=" + oldestMatchingInvoice.getId() + ")<br>");
                                 mConfirmedPayedInvoices.add(oldestMatchingInvoice);
                              }
                              else
                              {
                                 mLog.append("ERROR: payment " + payment.id + ": matches invoice " + oldestMatchingInvoice.getInvoiceNr() + " (id=" + oldestMatchingInvoice.getId() + "), but this invoice was set as payed with another BankId=" + oldestMatchingInvoice.getFintroId() + "<br>");
                                 mErrorPayments.add(payment);
                              }
                              isMatchFound = true;
                           }
                        }
                     }
                     // do it again but this time loop over the already paid invoices
                     // log.info("do it again and try to find it in the paid invoices");
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
                  // log.info("Wrong payment." + " Payment=" + payment.id + " [" +
                  // payment.amount/1.21 + "], " + " account=" + payment.accountNrCustomer + ", "+
                  // payment.details);
                  mWrongValuePayments.add(new InvoicePaymentStr(openInvoice, payment));
               }
               else
               {
                  // log.info("no matching invoice found." + " Payment=" + payment.id +
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

   public Collection<BankPayment> getNotMatchingPayments()
   {
      return mNotMatchingPayments;
   }

   public Collection<BankPayment> getUnknownAccountNrs()
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

   private void FillInvoiceWithPaymentInfo(InvoiceEntityData invoice, BankPayment payment)
   {
      Collection<InvoiceEntityData> alreadyUsedFintroIdInvoices = mInvoiceSession.getInvoiceByFintroId(mWebSession, payment.id);
      if (alreadyUsedFintroIdInvoices.size() > 1)
      {
         // multiple invoices linked to payment.id
         mErrorPayments.add(payment);
         mLog.append("ERROR: payment " + payment.id + " matches more than 1 invoice: ");
         for (InvoiceEntityData entry : alreadyUsedFintroIdInvoices)
         {
            mLog.append(entry.getInvoiceNr());
            mLog.append(", ");
         }
         mLog.append("Fix also the double links!!<br>");
      }
      else if (alreadyUsedFintroIdInvoices.size() == 1)
      {
         // 1 match on FintroID
         InvoiceEntityData dbInvoice = (InvoiceEntityData) alreadyUsedFintroIdInvoices.toArray()[0];
         if (dbInvoice.getId() == invoice.getId())
         {
            // the invoice matching the payment is already set with this FintroId
            mConfirmedPayedInvoices.add(invoice);
         }
         else
         {
            mErrorPayments.add(payment);
            mLog.append("ERROR: payment " + payment.id + ": match found on open invoice " + invoice.getInvoiceNr() + " (id=" + invoice.getId() + "), but this payment was already linked in DB to " + dbInvoice.getInvoiceNr() + " (id=" + dbInvoice.getId() + ")<br>");
         }
      }
      else
      {
         // FintroId not found in the DB
         if (invoice.getIsPayed())
         {
            // this should not happen because an already payed invoice should not be offered here for setting new payment info
            if (payment.id.equals(invoice.getFintroId()))
            {
               mLog.append("INFO : payment " + payment.id + ": should have been found in DB based on FinrtoId on invoice " + invoice.getInvoiceNr() + " (id=" + invoice.getId() + ")<br>");
               mConfirmedPayedInvoices.add(invoice);
            }
            else
            {
               mLog.append("ERROR: payment " + payment.id + ": matches invoice " + invoice.getInvoiceNr() + " (id=" + invoice.getId() + "), but this invoice was set as payed with another FintroId=" + invoice.getFintroId() + "<br>");
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
      // log.info("\r\nConfirmed payed invoices: \r\n" + strBuf.toString());
      String confirmedPayments = strBuf.toString();
      strBuf = new StringBuilder();
      for (Iterator<InvoiceEntityData> vPayIter = mNewPayedInvoices.iterator(); vPayIter.hasNext();)
      {
         InvoiceEntityData invoice = vPayIter.next();
         strBuf.append(invoice.getInvoiceNr());
         strBuf.append(',');
      }
      String newPayedInvoices = strBuf.toString();
      // log.info("\r\nNew payed invoices: \r\n" + strBuf.toString());
      strBuf = new StringBuilder();
      for (Iterator<BankPayment> vPayIter = mNotMatchingPayments.iterator(); vPayIter.hasNext();)
      {
         BankPayment payment = vPayIter.next();
         fillPaymentStrBuffer(payment, strBuf);
      }
      String notMatchingPayments = strBuf.toString();
      // log.info("\r\nNot matching payments: \r\n" + strBuf.toString());
      strBuf = new StringBuilder();
      for (Iterator<BankPayment> vPayIter = mUnknownAccountNrs.iterator(); vPayIter.hasNext();)
      {
         BankPayment payment = vPayIter.next();
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
      for (Iterator<BankPayment> vPayIter = mErrorPayments.iterator(); vPayIter.hasNext();)
      {
         BankPayment payment = vPayIter.next();
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

   private String getStructuredId(BankPayment payment)
   {
      String structuredId = "";
      int y = payment.details.indexOf("MEDEDELING : ");
      if (y >= 0 && payment.details.length() > y + 13 + 12)
      {
         y += 13;
         String flatId = payment.details.substring(y, y + 12);
         // log.info("Flat Structured ID found: " + flatId);
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
               // log.info("x = " + x);
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
      // log.info("isInvoiceNrFoundInDetail(invoiceNr=" + invoiceNr + ",
      // detail=" + detail + ")");
      if (invoiceNr.length() < 9)
      {
         return false;
      }
      // subtract the 2 number parts from e.g. 'N-1801nr57'
      String month = invoiceNr.substring(2, 6);
      String seqnr = invoiceNr.substring(8, invoiceNr.length());
      // log.info("month=" + month + ", seqnr=" + seqnr );

      return (detail.indexOf(month) != -1 && detail.indexOf(seqnr) != -1);
   }

   private void fillPaymentStrBuffer(BankPayment payment, StringBuilder strBuf)
   {
      strBuf.append("Bedrag: " + payment.amount + " (excl BTW: " + mCostFormatter.format(payment.amount / 1.21) + ")<br>");
      strBuf.append("Van Banknummer: " + payment.accountNrCustomer + "<br>");
      strBuf.append(payment.details);
      strBuf.append("<br>FintroId: " + payment.id);
      strBuf.append("<br>ValutaDate: " + payment.valutaDate + "<br><br>");
   }
}
