package be.tba.servlets.helper;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.invoice.interfaces.InvoiceEntityData;
import be.tba.ejb.invoice.session.InvoiceSqlAdapter;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;
import be.tba.util.data.AbstractSqlAdapter;
import be.tba.util.invoice.CustomerData;
import be.tba.util.invoice.IBANCheckDigit;
import be.tba.util.invoice.InvoiceData;
import be.tba.util.invoice.InvoiceHelper;
import be.tba.util.invoice.TbaPdfInvoice;
import be.tba.util.invoice.WoltersKluwenImport;
import be.tba.util.session.AccountCache;
import be.tba.util.session.SessionParmsInf;

public class InvoiceFacade
{
   private static Logger log = LoggerFactory.getLogger(InvoiceFacade.class);
   
    public static void saveInvoice(SessionParmsInf parms, WebSession session)
    {
        String vInvoiceId = parms.getParameter(Constants.INVOICE_TO_SAVE);
        InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();

        InvoiceEntityData vInvoice = vInvoiceSession.getInvoiceById(session, Integer.valueOf(vInvoiceId).intValue());
        if (vInvoice != null)
        {
            vInvoice.setCustomerRef(parms.getParameter(Constants.INVOICE_CUST_REF));
            vInvoiceSession.updateRow(session, vInvoice);
        }
    }

    public static void savePayDate(SessionParmsInf parms, WebSession session)
    {
        String vInvoiceId = parms.getParameter(Constants.INVOICE_TO_SAVE);
        InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();

        InvoiceEntityData vInvoice = vInvoiceSession.getInvoiceById(session, Integer.valueOf(vInvoiceId).intValue());
        if (vInvoice != null)
        {
            vInvoice.setPayDate(parms.getParameter(Constants.INVOICE_PAYDATE));
            
            vInvoice.setFintroId(parms.getParameter(Constants.TASK_FINTROID));
            vInvoice.setFromBankNr(parms.getParameter(Constants.TASK_FROM_BANK_NR));
            vInvoice.setValutaDate(parms.getParameter(Constants.TASK_VAL_DATE));
            vInvoice.setPaymentDetails(parms.getParameter(Constants.TASK_PAY_DETAILS));
            vInvoice.setComment(parms.getParameter(Constants.INVOICE_INFO));

            if (vInvoice.getPayDate() != null && vInvoice.getPayDate().length() > 0)
            {
                vInvoice.setIsPayed(true);
            }
            else
            {
                vInvoice.setIsPayed(false);
            }
            vInvoiceSession.updateRow(session, vInvoice);
        }
    }

    public static void freezeInvoices(SessionParmsInf parms, WebSession session)
    {
        String vLtd = parms.getParameter(Constants.INVOICE_TO_FREEZE);
        // log.info("record to delete list: " + vLtd);
        if (vLtd != null && vLtd.length() > 0)
        {
            StringTokenizer vStrTok = new StringTokenizer(vLtd, ",");

            Vector<Integer> vList = new Vector<Integer>();
            while (vStrTok.hasMoreTokens())
            {
                vList.add(Integer.valueOf(vStrTok.nextToken()));
            }
            InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
            vInvoiceSession.freezeList(session, (Collection<Integer>) vList, session.getYear());
        }
    }

    public static void mailInvoices(SessionParmsInf parms, WebSession session)
    {
        String vLtd = parms.getParameter(Constants.INVOICE_TO_FREEZE);
        // log.info("record to delete list: " + vLtd);
        if (vLtd != null && vLtd.length() > 0)
        {
            StringTokenizer vStrTok = new StringTokenizer(vLtd, ",");

            Vector<Integer> vList = new Vector<Integer>();
            while (vStrTok.hasMoreTokens())
            {
                vList.add(Integer.valueOf(vStrTok.nextToken()));
            }
            InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
            vInvoiceSession.mailList(session, (Collection<Integer>) vList, session.getYear());
        }
    }

    public static void deleteInvoices(SessionParmsInf parms, WebSession session)
    {
        String vLtd = parms.getParameter(Constants.INVOICE_TO_DELETE);
        // log.info("record to delete list: " + vLtd);
        if (vLtd != null && vLtd.length() > 0)
        {
            StringTokenizer vStrTok = new StringTokenizer(vLtd, ",");

            InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
            while (vStrTok.hasMoreTokens())
            {
                int key = Integer.parseInt(vStrTok.nextToken());
                InvoiceEntityData data = vInvoiceSession.getRow(session, key);
                if (data == null)
                {
                   log.error("Invoice delete failed for Id=" + key);
                   continue;
                }
                if (data.getCreditId() == 0)
                {
                   // this is a credit note that is deleted.
                   // also modify the related invoice
                   Collection<InvoiceEntityData> creditedInvoices = vInvoiceSession.getCreditedInvoice(session, key);
                   for (InvoiceEntityData invoice : creditedInvoices)
                   {
                      vInvoiceSession.clearCreditId(session, invoice.getId());
                   }
                }
                else if (data.getCreditId() > 0)
                {
                    //delete also the credit note
                    vInvoiceSession.deleteRow(session, data.getCreditId());
                }
                vInvoiceSession.deleteRow(session, key);
            }
        }
    }

    public static void setInvoicesPayed(SessionParmsInf parms, WebSession session)
    {
        String vLtd = parms.getParameter(Constants.INVOICE_TO_SETPAYED);
        if (vLtd != null && vLtd.length() > 0)
        {
            log.info("setInvoicesPayed: # entries " + vLtd);
            StringTokenizer vStrTok = new StringTokenizer(vLtd, ",");

            Vector<Integer> vList = new Vector<Integer>();
            while (vStrTok.hasMoreTokens())
            {
                vList.add(Integer.valueOf(vStrTok.nextToken()));
            }
            InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
            vInvoiceSession.setListPayed(session, (Collection<Integer>) vList);
        }
    }
    
    public static File generateInvoiceXml(SessionParmsInf parms, WebSession session)
    {
        String vLtd = parms.getParameter(Constants.INVOICE_TO_SETPAYED);
        if (vLtd != null && vLtd.length() > 0)
        {
            //log.info("setInvoicesPayed: # entries " + vLtd);
            StringTokenizer vStrTok = new StringTokenizer(vLtd, ",");

            Vector<Integer> vList = new Vector<Integer>();
            while (vStrTok.hasMoreTokens())
            {
                vList.add(Integer.valueOf(vStrTok.nextToken()));
            }
            InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
            Collection<InvoiceEntityData> invoiceList = vInvoiceSession.getInvoiceListByIdList(session, (Collection<Integer>) vList);
            return WoltersKluwenImport.generateVerkopenXml(invoiceList);
        }
        log.info("generateInvoiceXml: no invoices selected");
        return null;
    }
    
    public static void generateInvoices(SessionParmsInf parms, WebSession session) throws IOException
    {
       session.getCallFilter().setCustFilter(parms.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
        if (parms.getParameter(Constants.INVOICE_MONTH) != null)
            session.setMonthsBack(Integer.parseInt(parms.getParameter(Constants.INVOICE_MONTH)));

        Collection<AccountEntityData> list = AccountCache.getInstance().getAccountListWithoutTbaNrs();
        synchronized (list)
        {
            for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();)
            {
                AccountEntityData vAccountData = (AccountEntityData) vIter.next();
                if (vAccountData.getNoInvoice())
                    continue;
                InvoiceHelper vHelper = new InvoiceHelper(session, vAccountData.getId(), session.getMonthsBack(), session.getYear());
                vHelper.storeOrUpdate(session);
                vHelper.generatePdfInvoice();
            }
        }
    }

    public static void addManualInvoice(SessionParmsInf parms, WebSession session)
    {
       log.info("addManualInvoice enter");
        InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
        InvoiceEntityData newInvoice = new InvoiceEntityData();
        Calendar vCalendar = Calendar.getInstance();
        int vDay = vCalendar.get(Calendar.DAY_OF_MONTH);
        int vMonth = vCalendar.get(Calendar.MONTH);
        int vYear = vCalendar.get(Calendar.YEAR);
        
        newInvoice.setAccountID(Integer.parseInt(parms.getParameter(Constants.ACCOUNT_ID)));
        AccountEntityData account = AccountCache.getInstance().get(newInvoice.getAccountId());
        newInvoice.setAccountFwdNr(account.getFwdNumber());
        newInvoice.setTotalCost(Double.parseDouble(parms.getParameter(Constants.INVOICE_AMONTH)));
        newInvoice.setMonth(Integer.parseInt(parms.getParameter(Constants.INVOICE_MONTH)));
        newInvoice.setYear(Integer.parseInt(parms.getParameter(Constants.INVOICE_YEAR)));
        newInvoice.setFrozenFlag(true);
        newInvoice.setIsPayed(false);
        newInvoice.setStartTime(vCalendar.getTimeInMillis());
        newInvoice.setStopTime(vCalendar.getTimeInMillis());
        newInvoice.setYearSeqNr(0);
        newInvoice.setInvoiceDate(String.format("%02d/%02d/%4d", vDay, vMonth, vYear));
        newInvoice.setDescription(parms.getParameter(Constants.INVOICE_DESCRIPTION));
        // -1 means regular invoice
        // 0 means this is a credit invoice
        // db id means it is a regular invoice with a credit invoice counterpart indicated by this id.
        if (parms.getParameter(Constants.INVOICE_IS_CREDITNOTA) != null)
        {
           newInvoice.setCreditId(0);
           newInvoice.setIsPayed(true);
           if (newInvoice.getTotalCost() > 0)
           {
              newInvoice.setTotalCost(-newInvoice.getTotalCost());
           }
        }
        else
        {
           newInvoice.setCreditId(-1);
        }
       
        newInvoice.setCustomerName(parms.getParameter(Constants.INVOICE_CUSTOMER));
        //newInvoice.setAccountID(Integer.valueOf(parms.getParameter(Constants.ACCOUNT_ID)));
        int invoiceNr = vInvoiceSession.getNewInvoiceNumber(session, vYear);
        newInvoice.setYearSeqNr(invoiceNr);
        newInvoice.setInvoiceNr(InvoiceHelper.getInvoiceNumber(newInvoice.getYear(), newInvoice.getMonth(), invoiceNr));
        newInvoice.setStructuredId(IBANCheckDigit.IBAN_CHECK_DIGIT.calculateOGM(newInvoice.getInvoiceNr()));
        newInvoice.setFileName(InvoiceHelper.makeFileName(newInvoice));
        newInvoice.setFileName(AbstractSqlAdapter.escapeQuotes(newInvoice.getFileName().replace('\\', '/')));
        
        int id = vInvoiceSession.addRow(session, newInvoice);
        if (newInvoice.getCreditId() == 0)
        {
           String tobeCreditedInvoiceNr = parms.getParameter(Constants.INVOICE_NR);
           if (tobeCreditedInvoiceNr != null &&  !tobeCreditedInvoiceNr.isEmpty())
           {
              Collection<InvoiceEntityData> invoices = vInvoiceSession.getInvoiceByNr(session, tobeCreditedInvoiceNr);
              if (invoices.size() == 1)
              {
                 vInvoiceSession.setCreditReference(session, (InvoiceEntityData) invoices.toArray()[0], id);
              }
           }
        }
        log.info("created manual invoice " + newInvoice.getInvoiceNr() + " with id=" + id);
        File vTemplate = new File(Constants.INVOICE_HEAD_TMPL);
        File vTarget = new File(newInvoice.getFileName());

        File vPath = vTarget.getParentFile();
        if (!vPath.exists())
        {
            // dir doesn't exist
            vPath.mkdirs();
        }
        
        CustomerData custData = new CustomerData();
        custData.setId(account.getId());
        custData.setAddress1(account.getStreet());
        custData.setAddress2(account.getCity());
        custData.setBtwNr(account.getBtwNumber());
        custData.setName(account.getCompanyName());
        custData.setTAV(account.getAttToName());
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.Btw = newInvoice.getTotalCost()*0.21;
        invoiceData.TotalCost = newInvoice.getTotalCost();
        invoiceData.InvoiceNr = newInvoice.getInvoiceNr();
        invoiceData.StructuredId = newInvoice.getStructuredId();
        invoiceData.Description = newInvoice.getDescription();
        invoiceData.Month = newInvoice.getMonth();
        invoiceData.Year = newInvoice.getYear();
        
        
        TbaPdfInvoice pdfInvoice = new TbaPdfInvoice(vTarget, vTemplate);
//        pdfInvoice.setCallCounts(mCallCounts);
        pdfInvoice.setCustomerData(custData);
        pdfInvoice.setInvoiceData(invoiceData);
//        pdfInvoice.setTaskData(mTasks);
//        pdfInvoice.setSubCustomers(mSubcustomerCostList);
        pdfInvoice.createManualInvoice();
        pdfInvoice.closeAndSave();

    }


    public static void generateCreditInvoice(SessionParmsInf parms, WebSession session) 
    {
        int invoiceId = session.getInvoiceId();
        if (invoiceId != -1)
        {
            InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
            InvoiceEntityData vInvoiceData = vInvoiceSession.getInvoiceById(session, invoiceId);
            
            if (vInvoiceData != null)
            {
               int yearSeqNr = vInvoiceSession.getNewInvoiceNumber(session, vInvoiceData.getYear());
               InvoiceEntityData vCreditInvoiceData = new InvoiceEntityData(vInvoiceData);
                vCreditInvoiceData.setId(0);
                vCreditInvoiceData.setTotalCost(-vInvoiceData.getTotalCost());
                vCreditInvoiceData.setIsInvoiceMailed(true);
                vCreditInvoiceData.setIsPayed(true);
                vCreditInvoiceData.setFrozenFlag(true);
                vCreditInvoiceData.setCreditId(0);
                vCreditInvoiceData.setInvoiceNr(InvoiceHelper.getInvoiceNumber(vInvoiceData.getYear(), vInvoiceData.getMonth(), yearSeqNr));
                vCreditInvoiceData.setStructuredId(IBANCheckDigit.IBAN_CHECK_DIGIT.calculateOGM(vCreditInvoiceData.getInvoiceNr()));
                vCreditInvoiceData.setCustomerRef(vInvoiceData.getCustomerRef());
                vCreditInvoiceData.setMonth(vInvoiceData.getMonth());
                vCreditInvoiceData.setStartTime(vInvoiceData.getStartTime());
                vCreditInvoiceData.setStopTime(vInvoiceData.getStopTime());
                vCreditInvoiceData.setYear(vInvoiceData.getYear());
                vCreditInvoiceData.setYearSeqNr(yearSeqNr);
                vCreditInvoiceData.setPayDate("Credit nota");
                vCreditInvoiceData.setFileName(makeCreditInvoiceFileName(vCreditInvoiceData, vInvoiceData));
                
                // write credit note to the DB
                int id = vInvoiceSession.addInvoice(session, vCreditInvoiceData);
                // link the original to the credit note
                vInvoiceData.setCreditId(id);
                vInvoiceData.setIsPayed(true);
                vInvoiceData.setFrozenFlag(true);
                vInvoiceData.setPaymentDetails("Gecrediteerd");
                vInvoiceData.setPayDate("Gecrediteerd");
                // clear the content of this invoice by setting stop = start time.
                // NO!, this messes up the InvoiceHelper logic. This is solved over there
                //vInvoiceData.setStopTime(vInvoiceData.getStartTime());
                vInvoiceSession.updateRow(session, vInvoiceData);
                
                AccountEntityData account = AccountCache.getInstance().get(vInvoiceData);
                CustomerData customerData = new CustomerData();
                customerData.setId(account.getId());
                customerData.setAddress1(account.getStreet());
                customerData.setAddress2(account.getCity());
                customerData.setBtwNr(account.getBtwNumber());
                customerData.setName(account.getCompanyName());
                customerData.setTaskHourRate(account.getTaskHourRate());
                customerData.setTAV(account.getAttToName());
                
                InvoiceData invoiceData = new InvoiceData();
                invoiceData.Btw = vCreditInvoiceData.getTotalCost()*0.21;
                invoiceData.TotalCost = vCreditInvoiceData.getTotalCost();
                invoiceData.InvoiceNr = vCreditInvoiceData.getInvoiceNr();
                invoiceData.StructuredId = vCreditInvoiceData.getStructuredId();
                invoiceData.Description = vCreditInvoiceData.getDescription();
                invoiceData.Month = vCreditInvoiceData.getMonth();
                invoiceData.Year = vCreditInvoiceData.getYear();

                
                TbaPdfInvoice pdfCreditNote = new TbaPdfInvoice(new File(vCreditInvoiceData.getFileName()), new File(Constants.INVOICE_HEAD_TMPL));
                pdfCreditNote.setInvoiceData(invoiceData);
//                pdfCreditNote.setCreditNoteData(vCreditInvoiceData.getId(),
//                        vCreditInvoiceData.getTotalCost(), 
//                        account.getNoBtw() ? 0.0 : vCreditInvoiceData.getTotalCost() * 0.21,
//                        vCreditInvoiceData.getCustomerRef(),
//                        vCreditInvoiceData.getInvoiceNr(),
//                        vCreditInvoiceData.getStructuredId());
                pdfCreditNote.setCustomerData(customerData);
                pdfCreditNote.createCreditNote(vInvoiceData.getInvoiceNr());
                pdfCreditNote.closeAndSave();
            }
        }
    }
    
    static private String makeCreditInvoiceFileName(InvoiceEntityData  creditInvoice, InvoiceEntityData invoiceData)
    {
        if (invoiceData != null)
        {
            CharSequence target = invoiceData.getInvoiceNr();
            CharSequence replacer = "CreditNota-" + creditInvoice.getInvoiceNr();
            return invoiceData.getFileName().replace(target, replacer);
        }
        return "";
    }
    

}
