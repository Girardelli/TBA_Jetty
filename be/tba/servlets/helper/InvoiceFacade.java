package be.tba.servlets.helper;

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
import be.tba.util.invoice.InvoiceHelper;
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

        InvoiceEntityData vInvoice = vInvoiceSession.getInvoice(session, vInvoiceId);
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

        InvoiceEntityData vInvoice = vInvoiceSession.getInvoice(session, vInvoiceId);
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
                vInvoiceSession.deleteRow(session.getConnection(), Integer.parseInt(vStrTok.nextToken()));
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
                vHelper.generateInvoice();
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

}
