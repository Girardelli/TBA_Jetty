package be.tba.servlets.helper;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.account.session.AccountSqlAdapter;
import be.tba.ejb.mail.session.MailerSessionBean;
import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.ejb.task.session.TaskSqlAdapter;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.exceptions.SystemErrorException;
import be.tba.util.exceptions.remote.AccountNotFoundException;
import be.tba.util.invoice.InvoiceHelper;
import be.tba.util.invoice.WoltersKluwenImport;
import be.tba.util.session.AccountCache;
import be.tba.util.session.SessionParmsInf;
import be.tba.servlets.session.WebSession;

public class AccountFacade
{
    public static void archiveAccount(WebSession session, int accountID)
    {
    	  RecursiveArchive(session, accountID);
        AccountCache.getInstance().update(session);
    }

    public static void saveAccount(WebSession session, AccountEntityData newData)
    {
        AccountSqlAdapter vAccountSession = new AccountSqlAdapter();
        vAccountSession.updateRow(session, newData);
        
        AccountEntityData account = AccountCache.getInstance().get(newData.getId());
        account.set(newData);
        //AccountCache.getInstance().update(session);
    }

    public static void deregisterAccount(WebSession session, SessionParmsInf parms) throws AccountNotFoundException
    {
        String accountIdStr = (String) parms.getParameter(Constants.ACCOUNT_ID);
        int accountId = Integer.valueOf(accountIdStr);
        AccountSqlAdapter vAccountSession = new AccountSqlAdapter();
        vAccountSession.deregister(session, accountId);
        AccountCache.getInstance().update(session);
    }

    public static Vector<String> addAccount(WebSession session, HttpServletRequest req, SessionParmsInf parms) throws SystemErrorException
    {
        System.out.println("addAccount");
        String roleStr = parms.getParameter(Constants.ACCOUNT_ROLE);
        String superCustomer = parms.getParameter(Constants.ACCOUNT_SUPER_CUSTOMER);
        AccountRole role = AccountRole.fromShort(roleStr);
        
        AccountEntityData newAccount = new AccountEntityData();
        newAccount.setFullName(parms.getParameter(Constants.ACCOUNT_FULLNAME));
        newAccount.setRole(roleStr);
        
        if (role == AccountRole.ADMIN || role == AccountRole.EMPLOYEE)
        {
            Vector<String> vErrorList = ValidateEmployeeFields(parms);
            if (vErrorList.size() > 0)
            {
                req.setAttribute(Constants.ERROR_VECTOR, vErrorList);
                return vErrorList;
            }
            else
            {
                newAccount.setUserId(parms.getParameter(Constants.ACCOUNT_USERID));
                newAccount.setPassword(parms.getParameter(Constants.ACCOUNT_PASSWORD));
                System.out.println("no error on employee add");
            }
            newAccount.setSuperCustomer("");
            newAccount.setSuperCustomerId(0);
            newAccount.setFwdNumber(newAccount.getUserId());
        }
        else if (role == AccountRole.SUBCUSTOMER)
        {
            if (superCustomer == null || superCustomer.isEmpty() || superCustomer.equals("NO_VALUE")) 
            {
            	System.out.println("SystemErrorException()");
            	throw new SystemErrorException("je bent vergeten een superklant te selecteren");
            }
        	newAccount.setUserId("");
            newAccount.setPassword("");
            newAccount.setSuperCustomer(superCustomer);
            newAccount.setSuperCustomerId(AccountCache.getInstance().get(superCustomer).getId());
            newAccount.setFwdNumber(parms.getParameter(Constants.ACCOUNT_FORWARD_NUMBER));
        }
        else
        {
            newAccount.setUserId("");
            newAccount.setPassword("");
            newAccount.setSuperCustomer("");
            newAccount.setSuperCustomerId(0);
            newAccount.setFwdNumber(parms.getParameter(Constants.ACCOUNT_FORWARD_NUMBER));
        }
        newAccount.setCompanyName("");
        newAccount.setAttToName("");
        newAccount.setStreet("");
        newAccount.setCity("");
        newAccount.setBtwNumber("");
        newAccount.setHasSubCustomers(false);
        AccountSqlAdapter vAccountSession = new AccountSqlAdapter();
        vAccountSession.addRow(session, newAccount);
        AccountCache.getInstance().update(session);
        return null;
    }

    public static void changeFwdNumber(WebSession session, String oldNr, String newNr)
    {
        System.out.println("changeFwdNumber: old nr=" + oldNr + ", new nr=" + newNr);
        // AccountEntityData vOldData = AccountCache.getInstance().get(oldNr);
        CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
        vQuerySession.changeFwdNumber(session, oldNr, newNr);
    }

    public static void mailCustomer(SessionParmsInf parms, WebSession session)
    {
        String accountIdStr = (String) parms.getParameter(Constants.ACCOUNT_ID);
        int accountId = Integer.valueOf(accountIdStr);
        AccountEntityData vAccountData = AccountCache.getInstance().get(accountId);
        String vEmail = vAccountData.getEmail();
        if (vEmail != null && vEmail.length() > 0)
        {
            MailerSessionBean.sendCallInfoMail(session, vAccountData.getId());
        }
    }

    public static File generateKlantenXml(SessionParmsInf parms, WebSession session)
    {
        String vLtd = (String) parms.getParameter(Constants.ACCOUNT_TO_DELETE);
        if (vLtd != null && vLtd.length() > 0)
        {
            //System.out.println("setInvoicesPayed: # entries " + vLtd);
            StringTokenizer vStrTok = new StringTokenizer(vLtd, ",");

            Vector<Integer> vList = new Vector<Integer>();
            while (vStrTok.hasMoreTokens())
            {
                vList.add(Integer.valueOf(vStrTok.nextToken()));
            }
            AccountSqlAdapter vAccountSession = new AccountSqlAdapter();
            Collection<AccountEntityData> accountList = vAccountSession.getAccountListByIdList(session, (Collection<Integer>) vList);
            return WoltersKluwenImport.generateKlantenXml(accountList);
        }
        System.out.println("generateKlantenXml: no invoices selected");
        return null;
    }

    public static AccountEntityData updateAccountData(WebSession session, SessionParmsInf parms)
    {
        String accountIdStr = (String) parms.getParameter(Constants.ACCOUNT_ID);
        int accountId = Integer.valueOf(accountIdStr);
        AccountEntityData vAccount = new AccountEntityData(AccountCache.getInstance().get(accountId));
        vAccount.setFullName(parms.getParameter(Constants.ACCOUNT_FULLNAME));
        vAccount.setEmail(parms.getParameter(Constants.ACCOUNT_EMAIL));
        vAccount.setInvoiceEmail(parms.getParameter(Constants.ACCOUNT_INVOICE_EMAIL));
        vAccount.setGsm(parms.getParameter(Constants.ACCOUNT_GSM));
        vAccount.setCountryCode(parms.getParameter(Constants.ACCOUNT_COUNTRY_CODE));
        
        if (session.getRole() == AccountRole.ADMIN)
        {
           vAccount.setFwdNumber(parms.getParameter(Constants.ACCOUNT_FORWARD_NUMBER));
           vAccount.setHasSubCustomers(parms.getParameter(Constants.ACCOUNT_HAS_SUB_CUSTOMERS) != null);
           vAccount.setIsAutoRelease(parms.getParameter(Constants.ACCOUNT_AUTO_RELEASE) != null);
           vAccount.setTaskHourRate(Integer.parseInt((String) parms.getParameter(Constants.ACCOUNT_TASK_HOUR_RATE)));

           String vInvoiceType = (String) parms.getParameter(Constants.ACCOUNT_INVOICE_TYPE);

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
           // parms.getParameter(Constants.ACCOUNT_FAC_STD_IN_CALL)));
           vAccount.setFacStdOutCall(Integer.parseInt((String) parms.getParameter(Constants.ACCOUNT_FAC_STD_OUT_CALL)));
           vAccount.setFacFaxCall(Integer.parseInt((String) parms.getParameter(Constants.ACCOUNT_FAC_FAX_CALL)));
           vAccount.setFacSms(Integer.parseInt((String) parms.getParameter(Constants.ACCOUNT_FAC_SMS)));
           vAccount.setFacCallForward(Integer.parseInt((String) parms.getParameter(Constants.ACCOUNT_FAC_CALL_FORWARD)));

           vAccount.setFacTblMinCalls_I(Integer.parseInt((String) parms.getParameter(Constants.ACCOUNT_FAC_TBL_MIN_CALL_I)));
           vAccount.setFacTblStartCost_I(Double.parseDouble((String) parms.getParameter(Constants.ACCOUNT_FAC_TBL_START_COST_I)));
           vAccount.setFacTblExtraCost_I(Double.parseDouble((String) parms.getParameter(Constants.ACCOUNT_FAC_TBL_EXTRA_COST_I)));
           vAccount.setFacTblMinCalls_II(Integer.parseInt((String) parms.getParameter(Constants.ACCOUNT_FAC_TBL_MIN_CALL_II)));
           vAccount.setFacTblStartCost_II(Double.parseDouble((String) parms.getParameter(Constants.ACCOUNT_FAC_TBL_START_COST_II)));
           vAccount.setFacTblExtraCost_II(Double.parseDouble((String) parms.getParameter(Constants.ACCOUNT_FAC_TBL_EXTRA_COST_II)));
           vAccount.setFacTblMinCalls_III(Integer.parseInt((String) parms.getParameter(Constants.ACCOUNT_FAC_TBL_MIN_CALL_III)));
           vAccount.setFacTblStartCost_III(Double.parseDouble((String) parms.getParameter(Constants.ACCOUNT_FAC_TBL_START_COST_III)));
           vAccount.setFacTblExtraCost_III(Double.parseDouble((String) parms.getParameter(Constants.ACCOUNT_FAC_TBL_EXTRA_COST_III)));
           vAccount.setFacTblMinCalls_IV(Integer.parseInt((String) parms.getParameter(Constants.ACCOUNT_FAC_TBL_MIN_CALL_IV)));
           vAccount.setFacTblStartCost_IV(Double.parseDouble((String) parms.getParameter(Constants.ACCOUNT_FAC_TBL_START_COST_IV)));
           vAccount.setFacTblExtraCost_IV(Double.parseDouble((String) parms.getParameter(Constants.ACCOUNT_FAC_TBL_EXTRA_COST_IV)));
           vAccount.setFacOutLevel1(Integer.parseInt((String) parms.getParameter(Constants.ACCOUNT_FAC_OUT_LEVEL1)));
           vAccount.setFacOutLevel2(Integer.parseInt((String) parms.getParameter(Constants.ACCOUNT_FAC_OUT_LEVEL2)));
           vAccount.setFacOutLevel3(Integer.parseInt((String) parms.getParameter(Constants.ACCOUNT_FAC_OUT_LEVEL3)));
           vAccount.setCountAllLongCalls(parms.getParameter(Constants.ACCOUNT_COUNT_ALL_LONG_CALLS) != null ? true : false);
           vAccount.setCountLongFwdCalls(parms.getParameter(Constants.ACCOUNT_COUNT_LONG_FWD_CALLS) != null ? true : false);
           vAccount.setFacLong(Double.parseDouble((String) parms.getParameter(Constants.ACCOUNT_FAC_LONG)));
           vAccount.setFacLongFwd(Double.parseDouble((String) parms.getParameter(Constants.ACCOUNT_FAC_LONG_FWD)));

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
           if (parms.getParameter(Constants.ACCOUNT_FAC_AGENDA_CALL) != null)
               vAccount.setFacAgendaCall(Integer.parseInt((String) parms.getParameter(Constants.ACCOUNT_FAC_AGENDA_CALL)));
           String vAgendaPriceUnit = (String) parms.getParameter(Constants.ACCOUNT_FAC_AGENDA_UNIT);
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

           if (parms.getParameter(Constants.ACCOUNT_MAIL_ON1) != null)
           {
               vAccount.setMailMinutes1(Short.parseShort((String) parms.getParameter(Constants.ACCOUNT_MAIL_MINUTEN1)));
               vAccount.setMailHour1(Short.parseShort((String) parms.getParameter(Constants.ACCOUNT_MAIL_UUR1)));
           }
           else
           {
               vAccount.setMailMinutes1((short) 0);
               vAccount.setMailHour1((short) 0);
           }
           if (parms.getParameter(Constants.ACCOUNT_MAIL_ON2) != null)
           {
               vAccount.setMailMinutes2(Short.parseShort((String) parms.getParameter(Constants.ACCOUNT_MAIL_MINUTEN2)));
               vAccount.setMailHour2(Short.parseShort((String) parms.getParameter(Constants.ACCOUNT_MAIL_UUR2)));
           }
           else
           {
               vAccount.setMailMinutes2((short) 0);
               vAccount.setMailHour2((short) 0);
           }
           if (parms.getParameter(Constants.ACCOUNT_MAIL_ON3) != null)
           {
               vAccount.setMailMinutes3(Short.parseShort((String) parms.getParameter(Constants.ACCOUNT_MAIL_MINUTEN3)));
               vAccount.setMailHour3(Short.parseShort((String) parms.getParameter(Constants.ACCOUNT_MAIL_UUR3)));
           }
           else
           {
               vAccount.setMailMinutes3((short) 0);
               vAccount.setMailHour3((short) 0);
           }
           vAccount.setNoInvoice(parms.getParameter(Constants.ACCOUNT_NO_INVOICE) != null);
           vAccount.setNoBtw(parms.getParameter(Constants.ACCOUNT_NO_BTW) != null);

           vAccount.setNoEmptyMails(parms.getParameter(Constants.ACCOUNT_NO_EMPTY_MAILS) != null);
           vAccount.setIsMailInvoice(parms.getParameter(Constants.ACCOUNT_IS_MAIL_INVOICE) != null);
           vAccount.setTextMail(parms.getParameter(Constants.ACCOUNT_TEXT_MAIL) != null);
        }

        vAccount.setCompanyName(parms.getParameter(Constants.ACCOUNT_COMPANY_NAME));
        vAccount.setAttToName(parms.getParameter(Constants.ACCOUNT_ATT_TO_NAME));
        vAccount.setStreet(parms.getParameter(Constants.ACCOUNT_STREET));
        vAccount.setCity(parms.getParameter(Constants.ACCOUNT_CITY));
        vAccount.setBtwNumber(parms.getParameter(Constants.ACCOUNT_BTW_NUMBER));
        vAccount.setAccountNr(parms.getParameter(Constants.ACCOUNT_NR));
        vAccount.setCallProcessInfo(parms.getParameter(Constants.ACCOUNT_INFO));
        if (parms.getParameter(Constants.ACCOUNT_REDIRECT_ACCOUNT_ID) != null)
           vAccount.setRedirectAccountId(Integer.parseInt((String) parms.getParameter(Constants.ACCOUNT_REDIRECT_ACCOUNT_ID)));
        return vAccount;
    }

    public static void updateCustomerPrefs(WebSession session, SessionParmsInf parms)
    {
       AccountSqlAdapter vAccountSession = new AccountSqlAdapter();
       AccountEntityData vAccount = vAccountSession.getRow(session, AccountCache.getInstance().get(session.getSessionFwdNr()).getId());
       vAccount.setEmail(parms.getParameter(Constants.ACCOUNT_EMAIL));
       vAccount.setInvoiceEmail(parms.getParameter(Constants.ACCOUNT_INVOICE_EMAIL));
       vAccount.setGsm(parms.getParameter(Constants.ACCOUNT_GSM));
       vAccount.setCountryCode(parms.getParameter(Constants.ACCOUNT_COUNTRY_CODE));
       vAccount.setIsAutoRelease(parms.getParameter(Constants.ACCOUNT_AUTO_RELEASE) != null);

       if (parms.getParameter(Constants.ACCOUNT_MAIL_ON1) != null)
       {
           vAccount.setMailMinutes1(Short.parseShort((String) parms.getParameter(Constants.ACCOUNT_MAIL_MINUTEN1)));
           vAccount.setMailHour1(Short.parseShort((String) parms.getParameter(Constants.ACCOUNT_MAIL_UUR1)));
       }
       else
       {
           vAccount.setMailMinutes1((short) 0);
           vAccount.setMailHour1((short) 0);
       }
       if (parms.getParameter(Constants.ACCOUNT_MAIL_ON2) != null)
       {
           vAccount.setMailMinutes2(Short.parseShort((String) parms.getParameter(Constants.ACCOUNT_MAIL_MINUTEN2)));
           vAccount.setMailHour2(Short.parseShort((String) parms.getParameter(Constants.ACCOUNT_MAIL_UUR2)));
       }
       else
       {
           vAccount.setMailMinutes2((short) 0);
           vAccount.setMailHour2((short) 0);
       }
       if (parms.getParameter(Constants.ACCOUNT_MAIL_ON3) != null)
       {
           vAccount.setMailMinutes3(Short.parseShort((String) parms.getParameter(Constants.ACCOUNT_MAIL_MINUTEN3)));
           vAccount.setMailHour3(Short.parseShort((String) parms.getParameter(Constants.ACCOUNT_MAIL_UUR3)));
       }
       else
       {
           vAccount.setMailMinutes3((short) 0);
           vAccount.setMailHour3((short) 0);
       }
       vAccount.setNoEmptyMails(parms.getParameter(Constants.ACCOUNT_NO_EMPTY_MAILS) != null);
       vAccount.setTextMail(parms.getParameter(Constants.ACCOUNT_TEXT_MAIL) != null);
       vAccount.setIsMailInvoice(parms.getParameter(Constants.ACCOUNT_IS_MAIL_INVOICE) != null);
       vAccountSession.updateRow(session, vAccount);
       AccountCache.getInstance().update(session);
    }

    
    private static void RecursiveArchive(WebSession session, int accountID)
    {
        AccountEntityData vRemovedAccount = AccountCache.getInstance().get(accountID);

        if (vRemovedAccount != null)
        {
            if (vRemovedAccount.getHasSubCustomers())
            {
                Collection<AccountEntityData> list = AccountCache.getInstance().getSubCustomersList(accountID);
                for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();)
                {
                    AccountEntityData vValue = vIter.next();
                    RecursiveArchive(session, vValue.getId());
                    System.out.println("deleteAccount: also deleted subcustomer " + vValue.getFullName());
                }
            }
        }
        AccountSqlAdapter vAccountSession = new AccountSqlAdapter();
        vAccountSession.archiveAccount(session, accountID);
    }

    
    private static Vector<String> ValidateEmployeeFields(SessionParmsInf parms)
    {
        Vector<String> vFormFaults = new Vector<String>();

        String vUserId = parms.getParameter(Constants.ACCOUNT_USERID);
        String vPassword = parms.getParameter(Constants.ACCOUNT_PASSWORD);
        String vPassword2 = parms.getParameter(Constants.ACCOUNT_PASSWORD2);

        if (vUserId == null)
            vFormFaults.add("Login naam niet ingevuld.");
        if (vPassword == null)
            vFormFaults.add("Paswoord veld 1 niet ingevuld.");
        if (vPassword2 == null)
            vFormFaults.add("Paswoord veld 2 niet ingevuld.");
        if (vUserId.length() < 5 || vUserId.length() > 10)
            vFormFaults.add("login naam moet minstens 5 en maximaal 10 karakters bevatten.");
        if (vPassword.length() < 6)
            vFormFaults.add("Paswoord moet minimaal 6 karakters bevatten.");
        if (!vPassword.equals(vPassword2))
            vFormFaults.add("Paswoorden zijn niet identiek.");
        return vFormFaults;
    }

}
