package be.tba.servlets.helper;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.invoice.interfaces.InvoiceEntityData;
import be.tba.ejb.invoice.session.InvoiceSqlAdapter;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;
import be.tba.util.invoice.CustomerData;
import be.tba.util.invoice.InvoiceHelper;
import be.tba.util.invoice.TbaPdfInvoice;
import be.tba.util.session.AccountCache;

public class InvoiceFacade
{
    // public static void showInvoice(HttpServletRequest req, WebSession session)
    // throws CreateException, RemoteException, NamingException, RemoveException
    // {
    // String vKey = (String) req.getParameter(Constants.INVOICE_ID);
    // InitialContext vContext = new InitialContext();
    //
    // InvoiceSessionHome vHome = (InvoiceSessionHome)
    // vContext.lookup(EjbJndiNames.EJB_JNDI_INVOICE_SESSION);
    // InvoiceSession vQuerySession = vHome.create();
    // // session.setCurrentInvoice(vQuerySession.getInvoice(vKey));
    // vQuerySession.remove();
    // }

    public static void saveInvoice(HttpServletRequest req, WebSession session)
    {
        String vInvoiceId = req.getParameter(Constants.INVOICE_TO_SAVE);
        InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();

        InvoiceEntityData vInvoice = vInvoiceSession.getInvoiceById(session, vInvoiceId);
        if (vInvoice != null)
        {
            vInvoice.setCustomerRef((String) req.getParameter(Constants.INVOICE_CUST_REF));
            vInvoiceSession.updateRow(session.getConnection(), vInvoice);
        }
    }

    public static void savePayDate(HttpServletRequest req, WebSession session)
    {
        String vInvoiceId = req.getParameter(Constants.INVOICE_TO_SAVE);
        InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();

        InvoiceEntityData vInvoice = vInvoiceSession.getInvoiceById(session, vInvoiceId);
        if (vInvoice != null)
        {
            vInvoice.setPayDate((String) req.getParameter(Constants.INVOICE_PAYDATE));
            if (vInvoice.getPayDate() != null || vInvoice.getPayDate().length() > 0)
            {
                vInvoice.setIsPayed(true);
            }
            else
            {
                vInvoice.setIsPayed(false);
            }
            vInvoiceSession.updateRow(session.getConnection(), vInvoice);
        }
    }

    public static void freezeInvoices(HttpServletRequest req, WebSession session)
    {
        String vLtd = (String) req.getParameter(Constants.INVOICE_TO_FREEZE);
        // System.out.println("record to delete list: " + vLtd);
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

    public static void mailInvoices(HttpServletRequest req, WebSession session)
    {
        String vLtd = (String) req.getParameter(Constants.INVOICE_TO_FREEZE);
        // System.out.println("record to delete list: " + vLtd);
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

    public static void deleteInvoices(HttpServletRequest req, WebSession session)
    {
        String vLtd = (String) req.getParameter(Constants.INVOICE_TO_DELETE);
        // System.out.println("record to delete list: " + vLtd);
        if (vLtd != null && vLtd.length() > 0)
        {
            StringTokenizer vStrTok = new StringTokenizer(vLtd, ",");

            InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
            while (vStrTok.hasMoreTokens())
            {
                int key = Integer.parseInt(vStrTok.nextToken());
                InvoiceEntityData data = vInvoiceSession.getRow(session.getConnection(), key);
                if (data != null && data.getCreditId() > 0)
                {
                    //delete also the credit note
                    vInvoiceSession.deleteRow(session.getConnection(), data.getCreditId());
                }
                vInvoiceSession.deleteRow(session.getConnection(), key);
            }
        }
    }

    public static void setInvoicesPayed(HttpServletRequest req, WebSession session)
    {
        String vLtd = (String) req.getParameter(Constants.INVOICE_TO_SETPAYED);
        if (vLtd != null && vLtd.length() > 0)
        {
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

    public static void generateInvoices(HttpServletRequest req, WebSession session) throws IOException
    {
        if (req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER) != null)
            session.getCallFilter().setCustFilter(req.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER));
        if (req.getParameter(Constants.INVOICE_MONTH) != null)
            session.setMonthsBack(Integer.parseInt((String) req.getParameter(Constants.INVOICE_MONTH)));

        Collection<AccountEntityData> list = AccountCache.getInstance().getAccountListWithoutTbaNrs();
        synchronized (list)
        {
            for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();)
            {
                AccountEntityData vAccountData = (AccountEntityData) vIter.next();
                if (vAccountData.getNoInvoice())
                    continue;
                InvoiceHelper vHelper = new InvoiceHelper(session, vAccountData.getFwdNumber(), session.getMonthsBack(), session.getYear());
                vHelper.storeOrUpdate(session);
                vHelper.generatePdfInvoice();
            }
        }
    }

    public static void addInvoice(HttpServletRequest req, WebSession session)
    {
        InvoiceEntityData newInvoice = new InvoiceEntityData();
        Calendar vCalendar = Calendar.getInstance();
        int vMonth = vCalendar.get(Calendar.MONTH);
        int vYear = vCalendar.get(Calendar.YEAR);

        newInvoice.setAccountFwdNr("");
        newInvoice.setTotalCost(Double.parseDouble((String) req.getParameter(Constants.INVOICE_AMONTH)));
        newInvoice.setMonth(vMonth);
        newInvoice.setYear(vYear);
        newInvoice.setFrozenFlag(true);
        newInvoice.setIsPayed(false);
        newInvoice.setStartTime(0);
        newInvoice.setStopTime(0);
        newInvoice.setYearSeqNr(0);

        newInvoice.setCustomerName((String) req.getParameter(Constants.INVOICE_CUSTOMER));
        InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
        int invoiceNr = vInvoiceSession.getNewInvoiceNumber(session, vYear);
        newInvoice.setYearSeqNr(invoiceNr);
        newInvoice.setInvoiceNr(InvoiceHelper.getInvoiceNumber(newInvoice.getYear(), newInvoice.getMonth(), invoiceNr));
        newInvoice.setFileName(InvoiceHelper.makeFileName(newInvoice));

        vInvoiceSession.addRow(session.getConnection(), newInvoice);
    }


    public static void generateCreditInvoice(HttpServletRequest req, WebSession session) 
    {
        int invoiceId = session.getInvoiceId();
        if (invoiceId != -1)
        {
            InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
            InvoiceEntityData vInvoiceData = vInvoiceSession.getInvoiceById(session, Integer.toString(invoiceId));
            
            if (vInvoiceData != null)
            {
                InvoiceEntityData vCreditInvoiceData = new InvoiceEntityData(vInvoiceData);
                vCreditInvoiceData.setId(0);
                vCreditInvoiceData.setTotalCost(-vInvoiceData.getTotalCost());
                vCreditInvoiceData.setFileName(InvoiceHelper.makeCreditInvoiceFileName(vInvoiceData));
                vCreditInvoiceData.setIsInvoiceMailed(false);
                vCreditInvoiceData.setIsPayed(true);
                vCreditInvoiceData.setFrozenFlag(false);
                vCreditInvoiceData.setCreditId(-1);
                vCreditInvoiceData.setInvoiceNr("C" + vInvoiceData.getInvoiceNr());
                vCreditInvoiceData.setCustomerRef(vInvoiceData.getCustomerRef());
                vCreditInvoiceData.setMonth(vInvoiceData.getMonth());
                vCreditInvoiceData.setStartTime(vInvoiceData.getStartTime());
                vCreditInvoiceData.setStopTime(vInvoiceData.getStopTime());
                vCreditInvoiceData.setYear(vInvoiceData.getYear());
                vCreditInvoiceData.setYearSeqNr(vInvoiceData.getYearSeqNr());
                vCreditInvoiceData.setPayDate("Credit nota");
                // write them to the DB
                int id = vInvoiceSession.addInvoice(session, vCreditInvoiceData);
                vInvoiceData.setCreditId(id);
                vInvoiceSession.updateRow(session.getConnection(), vInvoiceData);
                
                AccountEntityData account = AccountCache.getInstance().get(vInvoiceData.getAccountFwdNr());
                CustomerData customerData = new CustomerData();
                customerData.setAddress1(account.getStreet());
                customerData.setAddress2(account.getCity());
                customerData.setBtwNr(account.getBtwNumber());
                customerData.setName(account.getCompanyName());
                customerData.setTaskHourRate(account.getTaskHourRate());
                customerData.setTAV(account.getAttToName());
                
                TbaPdfInvoice pdfCreditNote = new TbaPdfInvoice(new File(vCreditInvoiceData.getFileName()), new File(Constants.INVOICE_HEAD_TMPL));
                pdfCreditNote.setCreditNoteData(vCreditInvoiceData.getTotalCost(), 
                        account.getNoBtw() ? 0.0 : vCreditInvoiceData.getTotalCost() * 0.21,
                        vCreditInvoiceData.getCustomerRef(),
                        vCreditInvoiceData.getInvoiceNr());
                pdfCreditNote.setCustomerData(customerData);
                pdfCreditNote.createCreditNote();
                pdfCreditNote.closeAndSave();
            }
        }
    }

}
