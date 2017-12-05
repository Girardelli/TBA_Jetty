package be.tba.servlets.helper;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.account.session.AccountSqlAdapter;
import be.tba.ejb.mail.session.MailerSessionBean;
import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.remote.AccountNotFoundException;
import be.tba.util.invoice.InvoiceHelper;
import be.tba.util.session.AccountCache;
import be.tba.servlets.session.WebSession;

public class AccountFacade
{
    public static void deleteAccount(WebSession session, String accountNr)
    {
        String accountFwdNr = AccountCache.getInstance().idToFwdNr(Integer.parseInt(accountNr));
        AccountEntityData vRemovedAccount = AccountCache.getInstance().get(accountFwdNr);

        AccountSqlAdapter vAccountSession = new AccountSqlAdapter();
        vAccountSession.deleteRow(session.getConnection(), Integer.parseInt(accountNr));
        if (vRemovedAccount != null)
        {
            if (vRemovedAccount.getHasSubCustomers())
            {
                Collection<AccountEntityData> list = AccountCache.getInstance().getSubCustomersList(accountFwdNr);
                for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();)
                {
                    AccountEntityData vValue = vIter.next();
                    vAccountSession.deleteRow(session.getConnection(), vValue.getId());
                    System.out.println("deleteAccount: also deleted subcustomer " + vValue.getFullName());
                }
            }
        }

        CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
        vQuerySession.removeAccountCalls(session, accountNr);
        AccountCache.getInstance().update(session.getConnection());
    }

    public static void saveAccount(WebSession session, AccountEntityData newData)
    {
        AccountSqlAdapter vAccountSession = new AccountSqlAdapter();
        vAccountSession.updateRow(session.getConnection(), newData);
        AccountCache.getInstance().update(session.getConnection());
    }

    public static void deregisterAccount(WebSession session, HttpServletRequest req) throws AccountNotFoundException
    {
        String vFwdNr = (String) req.getParameter(Constants.ACCOUNT_ID);
        AccountSqlAdapter vAccountSession = new AccountSqlAdapter();
        vAccountSession.deregister(session, AccountCache.getInstance().get(vFwdNr).getId());
        AccountCache.getInstance().update(session.getConnection());
    }

    public static Vector<String> addAccount(WebSession session, HttpServletRequest req)
    {
        System.out.println("addAccount");
        AccountEntityData newAccount = new AccountEntityData();
        SetDefaultCallPrices(newAccount);
        newAccount.setFullName(req.getParameter(Constants.ACCOUNT_FULLNAME));
        newAccount.setRole(req.getParameter(Constants.ACCOUNT_ROLE));
        AccountRole role = AccountRole.fromShort(newAccount.getRole());
        if (role == AccountRole.ADMIN || role == AccountRole.EMPLOYEE)
        {
            Vector<String> vErrorList = ValidateEmployeeFields(req);
            if (vErrorList.size() > 0)
            {
                req.setAttribute(Constants.ERROR_VECTOR, vErrorList);
                return vErrorList;
            }
            else
            {
                newAccount.setUserId(req.getParameter(Constants.ACCOUNT_USERID));
                newAccount.setPassword(req.getParameter(Constants.ACCOUNT_PASSWORD));
                System.out.println("no error on employee add");
            }
            newAccount.setSuperCustomer("");
            newAccount.setFwdNumber(newAccount.getUserId());
        }
        else if (role == AccountRole.SUBCUSTOMER)
        {
            newAccount.setUserId("");
            newAccount.setPassword("");
            newAccount.setSuperCustomer(req.getParameter(Constants.ACCOUNT_SUPER_CUSTOMER));
            newAccount.setFwdNumber(req.getParameter(Constants.ACCOUNT_FORWARD_NUMBER));
        }
        else
        {
            newAccount.setUserId("");
            newAccount.setPassword("");
            newAccount.setSuperCustomer("");
            newAccount.setFwdNumber(req.getParameter(Constants.ACCOUNT_FORWARD_NUMBER));
        }
        newAccount.setCompanyName("");
        newAccount.setAttToName("");
        newAccount.setStreet("");
        newAccount.setCity("");
        newAccount.setBtwNumber("");
        newAccount.setHasSubCustomers(false);
        if (req.getParameter(Constants.ACCOUNT_3W_CUSTOMER) != null)
        {
            newAccount.setIs3W(true);
            newAccount.setIsXmlMail(true);
        }
        AccountSqlAdapter vAccountSession = new AccountSqlAdapter();
        vAccountSession.addRow(session.getConnection(), newAccount);
        AccountCache.getInstance().update(session.getConnection());
        return null;
    }

    public static void changeFwdNumber(WebSession session, String oldNr, String newNr)
    {
        System.out.println("changeFwdNumber: old nr=" + oldNr + ", new nr=" + newNr);
        // AccountEntityData vOldData = AccountCache.getInstance().get(oldNr);
        CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
        vQuerySession.changeFwdNumber(session, oldNr, newNr);
    }

    public static void mailCustomer(HttpServletRequest req, WebSession session)
    {
        String vFwdNr = (String) req.getParameter(Constants.ACCOUNT_ID);
        AccountEntityData vAccountData = AccountCache.getInstance().get(vFwdNr);
        String vEmail = vAccountData.getEmail();
        if (vEmail != null && vEmail.length() > 0)
        {
            MailerSessionBean.sendMail(session, vAccountData.getFwdNumber());
        }
    }

    public static AccountEntityData updateAccountData(HttpServletRequest req)
    {
        String vFwdNr = (String) req.getParameter(Constants.ACCOUNT_ID);
        AccountEntityData vAccount = new AccountEntityData(AccountCache.getInstance().get(vFwdNr));
        vAccount.setFullName(req.getParameter(Constants.ACCOUNT_FULLNAME));
        vAccount.setFwdNumber(req.getParameter(Constants.ACCOUNT_FORWARD_NUMBER));
        vAccount.setRole(req.getParameter(Constants.ACCOUNT_ROLE));
        if (vAccount.getRole().equals(AccountRole._vSubCustomer) && req.getParameter(Constants.ACCOUNT_SUPER_CUSTOMER) != null)
        {
            vAccount.setSuperCustomer(req.getParameter(Constants.ACCOUNT_SUPER_CUSTOMER));
        }
        else
        {
            vAccount.setSuperCustomer("");
        }
        vAccount.setEmail(req.getParameter(Constants.ACCOUNT_EMAIL));
        vAccount.setInvoiceEmail(req.getParameter(Constants.ACCOUNT_INVOICE_EMAIL));
        vAccount.setGsm(req.getParameter(Constants.ACCOUNT_GSM));
        vAccount.setHasSubCustomers(req.getParameter(Constants.ACCOUNT_HAS_SUB_CUSTOMERS) != null);
        vAccount.setIsAutoRelease(req.getParameter(Constants.ACCOUNT_AUTO_RELEASE) != null);
        vAccount.setTaskHourRate(Integer.parseInt((String) req.getParameter(Constants.ACCOUNT_TASK_HOUR_RATE)));

        String vInvoiceType = (String) req.getParameter(Constants.ACCOUNT_INVOICE_TYPE);

        if (vInvoiceType.equals(Constants.INVOICE_TYPE_WEEK))
        {
            vAccount.setInvoiceType(InvoiceHelper.kWeekInvoice);
        }
        else if (vInvoiceType.equals(Constants.INVOICE_TYPE_TELEMARK))
        {
            vAccount.setInvoiceType(InvoiceHelper.kTelemarketingInvoice);
        }
        else if (vInvoiceType.equals(Constants.INVOICE_NO_CALLS))
        {
            vAccount.setInvoiceType(InvoiceHelper.kNoCallsAccount);
        }
        else
        {
            vAccount.setInvoiceType(InvoiceHelper.kStandardInvoice);
        }

        // if (vInvoiceType == null)
        // vAccount.setInvoiceType(InvoiceHelper.kStandardInvoice);
        // else if (vInvoiceType.equals(Constants.INVOICE_TYPE_CUSTOM) ||
        // vInvoiceType.equals(Constants.INVOICE_TYPE_WEEK))
        // {

        // vAccount.setFacStdInCall(Integer.parseInt((String)
        // req.getParameter(Constants.ACCOUNT_FAC_STD_IN_CALL)));
        vAccount.setFacStdOutCall(Integer.parseInt((String) req.getParameter(Constants.ACCOUNT_FAC_STD_OUT_CALL)));
        vAccount.setFacFaxCall(Integer.parseInt((String) req.getParameter(Constants.ACCOUNT_FAC_FAX_CALL)));
        vAccount.setFacSms(Integer.parseInt((String) req.getParameter(Constants.ACCOUNT_FAC_SMS)));
        vAccount.setFacCallForward(Integer.parseInt((String) req.getParameter(Constants.ACCOUNT_FAC_CALL_FORWARD)));

        vAccount.setFacTblMinCalls_I(Integer.parseInt((String) req.getParameter(Constants.ACCOUNT_FAC_TBL_MIN_CALL_I)));
        vAccount.setFacTblStartCost_I(Double.parseDouble((String) req.getParameter(Constants.ACCOUNT_FAC_TBL_START_COST_I)));
        vAccount.setFacTblExtraCost_I(Double.parseDouble((String) req.getParameter(Constants.ACCOUNT_FAC_TBL_EXTRA_COST_I)));
        vAccount.setFacTblMinCalls_II(Integer.parseInt((String) req.getParameter(Constants.ACCOUNT_FAC_TBL_MIN_CALL_II)));
        vAccount.setFacTblStartCost_II(Double.parseDouble((String) req.getParameter(Constants.ACCOUNT_FAC_TBL_START_COST_II)));
        vAccount.setFacTblExtraCost_II(Double.parseDouble((String) req.getParameter(Constants.ACCOUNT_FAC_TBL_EXTRA_COST_II)));
        vAccount.setFacTblMinCalls_III(Integer.parseInt((String) req.getParameter(Constants.ACCOUNT_FAC_TBL_MIN_CALL_III)));
        vAccount.setFacTblStartCost_III(Double.parseDouble((String) req.getParameter(Constants.ACCOUNT_FAC_TBL_START_COST_III)));
        vAccount.setFacTblExtraCost_III(Double.parseDouble((String) req.getParameter(Constants.ACCOUNT_FAC_TBL_EXTRA_COST_III)));
        vAccount.setFacTblMinCalls_IV(Integer.parseInt((String) req.getParameter(Constants.ACCOUNT_FAC_TBL_MIN_CALL_IV)));
        vAccount.setFacTblStartCost_IV(Double.parseDouble((String) req.getParameter(Constants.ACCOUNT_FAC_TBL_START_COST_IV)));
        vAccount.setFacTblExtraCost_IV(Double.parseDouble((String) req.getParameter(Constants.ACCOUNT_FAC_TBL_EXTRA_COST_IV)));
        vAccount.setFacOutLevel1(Integer.parseInt((String) req.getParameter(Constants.ACCOUNT_FAC_OUT_LEVEL1)));
        vAccount.setFacOutLevel2(Integer.parseInt((String) req.getParameter(Constants.ACCOUNT_FAC_OUT_LEVEL2)));
        vAccount.setFacOutLevel3(Integer.parseInt((String) req.getParameter(Constants.ACCOUNT_FAC_OUT_LEVEL3)));
        vAccount.setCountAllLongCalls(req.getParameter(Constants.ACCOUNT_COUNT_ALL_LONG_CALLS) != null ? true : false);
        vAccount.setCountLongFwdCalls(req.getParameter(Constants.ACCOUNT_COUNT_LONG_FWD_CALLS) != null ? true : false);
        vAccount.setFacLong(Double.parseDouble((String) req.getParameter(Constants.ACCOUNT_FAC_LONG)));
        vAccount.setFacLongFwd(Double.parseDouble((String) req.getParameter(Constants.ACCOUNT_FAC_LONG_FWD)));

        // vAccount.setFacTblMinCalls_I(Constants.kFacTblMinCalls_I);
        // vAccount.setFacTblStartCost_I(Constants.kFacTblStartCost_I);
        // vAccount.setFacTblExtraCost_I(Constants.kFacTblExtraCost_I);
        // vAccount.setFacTblMinCalls_II(Constants.kFacTblMinCalls_II);
        // vAccount.setFacTblStartCost_II(Constants.kFacTblStartCost_II);
        // vAccount.setFacTblExtraCost_II(Constants.kFacTblExtraCost_II);
        // vAccount.setFacTblMinCalls_III(Constants.kFacTblMinCalls_III);
        // vAccount.setFacTblStartCost_III(Constants.kFacTblStartCost_III);
        // vAccount.setFacTblExtraCost_III(Constants.kFacTblExtraCost_III);
        // vAccount.setFacTblMinCalls_IV(Constants.kFacTblMinCalls_IV);
        // vAccount.setFacTblStartCost_IV(Constants.kFacTblStartCost_IV);
        // vAccount.setFacTblExtraCost_IV(Constants.kFacTblExtraCost_IV);

        vAccount.setFacAgendaCall(Constants.kStandardAgendaCost);
        if (req.getParameter(Constants.ACCOUNT_FAC_AGENDA_CALL) != null)
            vAccount.setFacAgendaCall(Integer.parseInt((String) req.getParameter(Constants.ACCOUNT_FAC_AGENDA_CALL)));
        String vAgendaPriceUnit = (String) req.getParameter(Constants.ACCOUNT_FAC_AGENDA_UNIT);
        if (vAgendaPriceUnit == null || vAgendaPriceUnit.equals(Constants.AGENDA_NO))
            vAccount.setAgendaPriceUnit(InvoiceHelper.kNoAgenda);
        else if (vAgendaPriceUnit.equals(Constants.AGENDA_STANDARD))
            vAccount.setAgendaPriceUnit(InvoiceHelper.kStandardAgenda);
        else if (vAgendaPriceUnit.equals(Constants.AGENDA_PERC_PER_CALL))
            vAccount.setAgendaPriceUnit(InvoiceHelper.kPercentageOnAgendaCalls);
        else if (vAgendaPriceUnit.equals(Constants.AGENDA_PERC_ALL_CALL))
            vAccount.setAgendaPriceUnit(InvoiceHelper.kPercentageOnTotalCallCost);
        else if (vAgendaPriceUnit.equals(Constants.AGENDA_EURO_PER_CALL))
            vAccount.setAgendaPriceUnit(InvoiceHelper.kEuroCentOnAgendaCalls);
        else if (vAgendaPriceUnit.equals(Constants.AGENDA_EURO_ALL_CALL))
            vAccount.setAgendaPriceUnit(InvoiceHelper.kEuroCentOnAllCalls);
        else
            vAccount.setAgendaPriceUnit(InvoiceHelper.kNoAgenda);

        System.out.println("update account: setAgendaPriceUnit()=" + vAccount.getAgendaPriceUnit());

        if (req.getParameter(Constants.ACCOUNT_3W_CUSTOMER) != null)
        {
            vAccount.setIs3W(true);
            vAccount.setIsXmlMail(true);
            vAccount.setW3_PersonId(req.getParameter(Constants.ACCOUNT_3W_PERSON_ID));
            vAccount.setW3_CompanyId(req.getParameter(Constants.ACCOUNT_3W_COMPANY_ID));
        }
        if (req.getParameter(Constants.ACCOUNT_MAIL_ON1) != null)
        {
            vAccount.setMailMinutes1(Short.parseShort((String) req.getParameter(Constants.ACCOUNT_MAIL_MINUTEN1)));
            vAccount.setMailHour1(Short.parseShort((String) req.getParameter(Constants.ACCOUNT_MAIL_UUR1)));
        }
        else
        {
            vAccount.setMailMinutes1((short) 0);
            vAccount.setMailHour1((short) 0);
        }
        if (req.getParameter(Constants.ACCOUNT_MAIL_ON2) != null)
        {
            vAccount.setMailMinutes2(Short.parseShort((String) req.getParameter(Constants.ACCOUNT_MAIL_MINUTEN2)));
            vAccount.setMailHour2(Short.parseShort((String) req.getParameter(Constants.ACCOUNT_MAIL_UUR2)));
        }
        else
        {
            vAccount.setMailMinutes2((short) 0);
            vAccount.setMailHour2((short) 0);
        }
        if (req.getParameter(Constants.ACCOUNT_MAIL_ON3) != null)
        {
            vAccount.setMailMinutes3(Short.parseShort((String) req.getParameter(Constants.ACCOUNT_MAIL_MINUTEN3)));
            vAccount.setMailHour3(Short.parseShort((String) req.getParameter(Constants.ACCOUNT_MAIL_UUR3)));
        }
        else
        {
            vAccount.setMailMinutes3((short) 0);
            vAccount.setMailHour3((short) 0);
        }

        vAccount.setCompanyName(req.getParameter(Constants.ACCOUNT_COMPANY_NAME));
        vAccount.setAttToName(req.getParameter(Constants.ACCOUNT_ATT_TO_NAME));
        vAccount.setStreet(req.getParameter(Constants.ACCOUNT_STREET));
        vAccount.setCity(req.getParameter(Constants.ACCOUNT_CITY));
        vAccount.setBtwNumber(req.getParameter(Constants.ACCOUNT_BTW_NUMBER));

        vAccount.setNoInvoice(req.getParameter(Constants.ACCOUNT_NO_INVOICE) != null);
        vAccount.setNoBtw(req.getParameter(Constants.ACCOUNT_NO_BTW) != null);

        vAccount.setNoEmptyMails(req.getParameter(Constants.ACCOUNT_NO_EMPTY_MAILS) != null);
        vAccount.setIsMailInvoice(req.getParameter(Constants.ACCOUNT_IS_MAIL_INVOICE) != null);
        vAccount.setTextMail(req.getParameter(Constants.ACCOUNT_TEXT_MAIL) != null);
        return vAccount;
    }

    private static Vector<String> ValidateEmployeeFields(HttpServletRequest req)
    {
        Vector<String> vFormFaults = new Vector<String>();

        String vUserId = req.getParameter(Constants.ACCOUNT_USERID);
        String vPassword = req.getParameter(Constants.ACCOUNT_PASSWORD);
        String vPassword2 = req.getParameter(Constants.ACCOUNT_PASSWORD2);

        if (vUserId == null)
            vFormFaults.add("Login naam niet ingevuld.");
        if (vPassword == null)
            vFormFaults.add("Paswoord veld 1 niet ingevuld.");
        if (vPassword2 == null)
            vFormFaults.add("Paswoord veld 2 niet ingevuld.");
        if (vUserId.length() < 4 || vUserId.length() > 10)
            vFormFaults.add("login naam moet minstens 4 en maximaal 10 karakters bevatten.");
        if (vPassword.length() > 10)
            vFormFaults.add("Paswoord mag maximaal 10 karakters bevatten.");
        if (vPassword.length() < 6)
            vFormFaults.add("Paswoord moet minimaal 6 karakters bevatten.");
        if (!vPassword.equals(vPassword2))
            vFormFaults.add("Paswoorden zijn niet identiek.");
        return vFormFaults;
    }

    private static void SetDefaultCallPrices(AccountEntityData account)
    {
        account.setFacTblMinCalls_I(Constants.kFacTblMinCalls_I);
        account.setFacTblStartCost_I(Constants.kFacTblStartCost_I);
        account.setFacTblExtraCost_I(Constants.kFacTblExtraCost_I);
        account.setFacTblMinCalls_II(Constants.kFacTblMinCalls_II);
        account.setFacTblStartCost_II(Constants.kFacTblStartCost_II);
        account.setFacTblExtraCost_II(Constants.kFacTblExtraCost_II);
        account.setFacTblMinCalls_III(Constants.kFacTblMinCalls_III);
        account.setFacTblStartCost_III(Constants.kFacTblStartCost_III);
        account.setFacTblExtraCost_III(Constants.kFacTblExtraCost_III);
        account.setFacTblMinCalls_IV(Constants.kFacTblMinCalls_IV);
        account.setFacTblStartCost_IV(Constants.kFacTblStartCost_IV);
        account.setFacTblExtraCost_IV(Constants.kFacTblExtraCost_IV);

        account.setCountAllLongCalls(false);
        account.setCountLongFwdCalls(false);
        account.setFacLong(Constants.kFacLong);
        account.setFacLongFwd(Constants.kFacLongFwd);
        account.setFacStdOutCall(Constants.kOutCost);
        account.setFacFaxCall(Constants.kFaxCost);
        account.setFacSms(Constants.kSmsCost);
        account.setFacCallForward(Constants.kForwardCost);
        account.setIsMailInvoice(true);

    }
}
