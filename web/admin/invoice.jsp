<html>

<%@ include file="adminheader.jsp" %>

<%@ page
	import="javax.ejb.*,java.util.*,
	
	
	javax.naming.Context,
	javax.naming.InitialContext,
	
	javax.ejb.*,
	be.tba.ejb.account.interfaces.*,
	be.tba.ejb.pbx.interfaces.*,
	be.tba.ejb.task.interfaces.*,
	be.tba.util.constants.EjbJndiNames,
	be.tba.util.constants.Constants,
	be.tba.util.exceptions.AccessDeniedException,
	be.tba.servlets.session.SessionManager,
	be.tba.util.session.AccountCache,
	be.tba.util.invoice.InvoiceHelper,
	be.tba.util.invoice.InvoiceData,
	be.tba.ejb.invoice.interfaces.InvoiceEntityData,
	be.tba.ejb.invoice.session.InvoiceSqlAdapter,
    be.tba.ejb.task.session.TaskSqlAdapter,
    be.tba.ejb.pbx.session.CallRecordSqlAdapter,
	be.tba.util.data.*"%>
<%

	try
	{
	    vSession.setCallingJsp(Constants.INVOICE_JSP);
	
	    boolean vCustomerFilterOn = false;
	
	    String vCustomerFilter = (String) vSession.getCallFilter().getCustFilter();//request.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER);
        if (vCustomerFilter == null)
        {
            vCustomerFilter = Constants.ACCOUNT_FILTER_ALL;
        }
	    
	    int vMonth = vSession.getMonthsBack();
	    int vYear = vSession.getYear();
	    int vInvoiceId = vSession.getInvoiceId();
	    Calendar vToday = Calendar.getInstance();
	    int vCurYear = vToday.get(Calendar.YEAR);
		InvoiceEntityData vInvoiceData = null;
		AccountEntityData vAccountData = null;
		String vInvoiceRef = "";
		
		if (vInvoiceId > 0)
		{
		    InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
	        vInvoiceData = vInvoiceSession.getInvoiceById(vSession, vInvoiceId);
	        if (vInvoiceData != null)
	        {
	            vInvoiceRef = vInvoiceData.getCustomerRef();
	            vAccountData =  AccountCache.getInstance().get(vInvoiceData);
	        }
		}
		if (vAccountData == null && !vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL))
		{
			vAccountData =  AccountCache.getInstance().get(vCustomerFilter);
		}
		if (vInvoiceRef == null) vInvoiceRef = "";

%>
<body>
<p><span class="admintitle"> Facturen maken <%=(vAccountData == null)? "Selecteer een klant en maand." : "" %><br>
<br>
<br>
</span></p>
<form name="invoiceform" method="POST" action="/tba/AdminDispatch">
<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.GOTO_INVOICE%>">
<table  width="100%" cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">

	<tr>
		<!-- white space -->
		<td valign="top" width="20" bgcolor="FFFFFF"></td>

		<!-- account list -->
		<td valign="top" bgcolor="FFFFFF">
		
		
		<table  border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="250" valign="top" class="adminsubtitle">&nbsp;Klant</td>
				<td width="10" valign="top">:</td>
				<td width="270" valign="top">
				<select
					name="<%=Constants.ACCOUNT_FILTER_CUSTOMER%>" onchange="submit()">
					<%

		out.println("<option value=\"" + Constants.ACCOUNT_FILTER_ALL + (vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL) ? "\" selected>" : "\">") + "selecteer klant");
		Collection<TaskEntityData> vTasks = new Vector<TaskEntityData>();
		Collection<CallRecordEntityData> vRecords = new Vector<CallRecordEntityData>();
		Collection<AccountEntityData> list = AccountCache.getInstance().getInvoiceCustomerList();
		synchronized(list) 
		{
		    for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();)
		    {
                AccountEntityData vData = (AccountEntityData) vIter.next();
		        out.println("<option value=\"" + vData.getFwdNumber() + (vCustomerFilter.equals(vData.getFwdNumber()) ? "\" selected>" : "\">") + vData.getFullName());
		    }
		}
					%>
				</select>
				</td>
			</tr>
			<tr>
				<td width="250" valign="top" class="adminsubtitle">&nbsp;Maand</td>
				<td width="10" valign="top">:</td>
				<td width="270" valign="top">
				<select
					name="<%=Constants.INVOICE_MONTH%>" onchange="submit()">
					<%
		out.println("<option value=\"" + CallFilter.kNoMonth + (CallFilter.kNoMonth == vMonth ? "\" selected>" : "\">") + "selecteer maand");
		for (int i = 0; i < Constants.MONTHS.length; ++i)
		{
		    out.println("<option value=\"" + i + (i == vMonth ? "\" selected>" : "\">") + Constants.MONTHS[i]);
		}
					%>
				</select>
				</td>
				<td width="250" valign="top" class="adminsubtitle">&nbsp;Jaar</td>
				<td width="10" valign="top">:</td>
				<td width="270" valign="top">
				<select
					name="<%=Constants.INVOICE_YEAR%>" onchange="submit()">
					<%
		out.println("<option value=\"" + CallFilter.kNoYear + (CallFilter.kNoYear == vYear ? "\" selected>" : "\">") + "selecteer jaar");
		for (int i = vCurYear; i > 2000; --i)
		{
		    out.println("<option value=\"" + i + (i == vYear ? "\" selected>" : "\">") + i);
		}
					%>
				</select>
				</td>
			</tr>
		</table>
			
		<% 
		if (vAccountData == null)
		{
%>
</td>
</tr>
</table>
</form>
</body>
<%
            return;
		}
        if (vInvoiceData != null && vInvoiceData.getFrozenFlag())
       	{
       		if (vInvoiceData.getIsInvoiceMailed())
       		{
       			String fintroId;
       			String valutaDate;
       			String message;
       			String payDate;
       			String fromBankNr;
       			if (vInvoiceData.getIsPayed())
       			{
                       // not possible to change this data
       				fintroId = vInvoiceData.getFintroId();
                    valutaDate = vInvoiceData.getValutaDate();
                    message = vInvoiceData.getPaymentDetails();
                    payDate = vInvoiceData.getPayDate();
                    fromBankNr = vInvoiceData.getFromBankNr();
       			}
       			else
       			{
       				// allow to enter this data manually
                    fintroId = "<input type=text name=\"" + Constants.TASK_FINTROID + "\" value=\"" + vInvoiceData.getFintroId() + "\">";
                    valutaDate = "<input type=text name=\"" + Constants.TASK_VAL_DATE + "\" value=\"" + vInvoiceData.getValutaDate() + "\">";
                    message = "<input width=\"500\" type=text name=\"" + Constants.TASK_PAY_DETAILS + "\" value=\"" + vInvoiceData.getPaymentDetails() + "\">";
                    payDate = "<input type=text name=\"" + Constants.INVOICE_PAYDATE + "\" value=\"" + vInvoiceData.getPayDate() + "\">";
                    fromBankNr = "<input type=text name=\"" + Constants.TASK_FROM_BANK_NR + "\" value=\"" + vInvoiceData.getFromBankNr() + "\">";
       			}
%>
        <br><br>Het factuurnummer is bevrozen en kan niet meer aangepast worden.<br>
        <table  border="0" cellspacing="2" cellpadding="2">
            <tr>
                <td width="250" valign="top" class="adminsubtitle">&nbsp;Factuur nummer</td>
                <td width="10" valign="top">:</td>
                <td width="270" valign="top"> <%=vInvoiceData.getInvoiceNr()%>
                </td>
            </tr>
            <br>
            <tr>
                <td width="250" valign="top" class="adminsubtitle">&nbsp;FintroID</td>
                <td width="10" valign="top">:</td>
                <td width="270" valign="top"> <%=fintroId%>
                </td>
            </tr>
            <tr>
                <td width="250" valign="top" class="adminsubtitle">&nbsp;Valuta Datum</td>
                <td width="10" valign="top">:</td>
                <td width="270" valign="top"> <%=valutaDate%>
                </td>
            </tr>
            <tr>
                <td width="250" valign="top" class="adminsubtitle">&nbsp;Betaald op</td>
                <td width="10" valign="top">:</td>
                <td width="270" valign="top"> <%=payDate%>
                </td>
            </tr>
            <tr>
                <td width="250" valign="top" class="adminsubtitle">&nbsp;Mededeling</td>
                <td width="10" valign="top">:</td>
                <td width="270" valign="top"> <%=message%>
                </td>
            </tr>
            <tr>
                <td width="250" valign="top" class="adminsubtitle">&nbsp;Van Banknummer</td>
                <td width="10" valign="top">:</td>
                <td width="270" valign="top"> <%=fromBankNr%>
                </td>
            </tr>
            <tr>
                <td width="250" valign="top" class="adminsubtitle">&nbsp;Bedrag (incl BTW)</td>
                <td width="10" valign="top">:</td>
                <td width="270" valign="top"> <%=vInvoiceData.getTotalCost()%>
                </td>
            </tr>
            <tr>
                <td width="250" valign="top" class="adminsubtitle">&nbsp;Gestructureerde mededeling</td>
                <td width="10" valign="top">:</td>
                <td width="270" valign="top"> <%=vInvoiceData.getStructuredId()%>
                </td>
            </tr>
            <tr>
                <td width="250" valign="top" class="adminsubtitle">&nbsp;PDF bestand</td>
                <td width="10" valign="top">:</td>
                <td width="270" valign="top"> <%=vInvoiceData.getFileName()%>
                </td>
            </tr>
            <tr>
                <td width="250" valign="top" class="adminsubtitle">&nbsp;Klant referencie</td>
                <td width="10" valign="top">:</td>
                <td width="270" valign="top"> <%=vInvoiceData.getCustomerRef().isEmpty()?"-":vInvoiceData.getCustomerRef()%>
                </td>
            </tr>
            
        </table>
        <p><span class=\"adminsubtitle\"> 
        <input class="tbabutton" type=submit name=action value=" Bewaar " onclick="savePayDate()" > 
        <input class="tbabutton" type=submit name=action value=" Maak Credit Nota " onclick="createCreditNote()" > 
        </span></p>
<%
        	    // get the lists:
                TaskSqlAdapter vTaskSession = new TaskSqlAdapter();
        		vTasks = vTaskSession.getTasksForInvoice(vSession, vInvoiceData.getId());
        	    CallRecordSqlAdapter vCallRecordSession = new CallRecordSqlAdapter();
        	    vRecords = vCallRecordSession.getInvoiceCalls(vSession, vInvoiceData.getAccountID(), vInvoiceData.getStartTime(), vInvoiceData.getStopTime());
       		}
       	}
        %>
        <p><span class=\"adminsubtitle\"> 
        <input class="tbabutton" type=submit name=action value=" Terug naar lijst " onclick="backToList()" > 
        </span></p>
        <%
        
        // *********************************************************************************
        // Not frozen invoice: InvoiceHelper is called and all details shall be printed
        //
		if (vInvoiceData == null || vInvoiceData.getFrozenFlag() == false)
       	{
        		// No invoice yet or not yet frozen, so get the help of InvoiceHelper to collect the current invoice
                InvoiceHelper vInvoiceHelper = null;
                if (vSession.getMonthsBack() != CallFilter.kNoMonth && vCustomerFilter != null && !vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL))
                {
                    if (vInvoiceData != null)
                    {
                        if (vInvoiceData.getCreditId() != 0)
                        {
                            vInvoiceHelper = new InvoiceHelper(vInvoiceData, vSession);
                        }
                    }
                    else
                    {
                        AccountEntityData account = AccountCache.getInstance().get(vCustomerFilter);
                        if (account != null)
                        {
                            vInvoiceHelper = new InvoiceHelper(vSession, account.getId(), vMonth, vYear);
                        }
                        else
                        {
                            vInvoiceHelper = null;
                        }
                    }
                    if (vInvoiceHelper != null)
                    {
                    	System.out.println("invoiceHelper created: run it!");
                        vInvoiceHelper.storeOrUpdate(vSession);
                        vSession.setInvoiceHelper(vInvoiceHelper);
                        vRecords = vInvoiceHelper.getCallRecords();
                        vTasks = vInvoiceHelper.getTasks();
                        System.out.println("invoiceHelper loaded: tasks=" + (vTasks==null?"null":vTasks.size()) + ", records=" + (vRecords==null?"null":vRecords.size()));
                    }
                }
                out.println("<p><span class=\"adminsubtitle\"><br>");
                out.println("Facturatiegegevens voor de maand " + vSession.getMonthsBackString() + ", " + vSession.getYear() + ":<br><br>");

                if ((vRecords == null || vRecords.size() == 0) && (vTasks == null || vTasks.size() == 0))
                {
                	out.println("<br><br>Er zijn geen facturatiegegevens beschikbaar voor de geselecteerde periode.<br><br>");
                }
                else
                {
%>
                    </span></p>
                    <table width="643" border="0" cellspacing="0" cellpadding="0">
<%
                            System.out.println("invoice type = " + vAccountData.getInvoiceType());
                            if (vAccountData.getInvoiceType() == InvoiceHelper.kStandardInvoice || 
                                vAccountData.getInvoiceType() == InvoiceHelper.kWeekInvoice)
                            {
                                if (vRecords == null || vRecords.size() == 0)
                                {
                                    out.println("<p><span class=\"adminsubtitle\"> Geen oproepen voor de geselecteerde maand.</span></p>");
                                }
                            
                                if (vAccountData.getInvoiceType() == InvoiceHelper.kStandardInvoice)
                                {
                                    out.println("<p><span class=\"adminsubtitle\"> Standaard facturatie. (Group " + vInvoiceHelper.getInvoiceGroupStr() + ")</span></p>");
                                }
                                else if (vAccountData.getInvoiceType() == InvoiceHelper.kWeekInvoice)
                                {
                                    out.println("<p><span class=\"adminsubtitle\"> Week facturatie. (Group " + vInvoiceHelper.getInvoiceGroupStr() + ")</span></p>");
                                }
                %>

                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubtitle">&nbsp;&nbsp;Kosten rubriek</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="adminsubtitle">&nbsp;&nbsp;Aantal</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="adminsubtitle">&nbsp;&nbsp;Eenheidsprijs (Euro)</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="adminsubtitle">&nbsp;&nbsp;Kost (Euro)</td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" height="1" bgcolor="#000000"></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubsubtitle">&nbsp;&nbsp;binnenkomende oproepen</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getInCalls()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getInCallUnitCost()%> (Group <%=vInvoiceHelper.getInvoiceGroupStr()%>)</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getInCost()%></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubsubtitle">&nbsp;&nbsp;uitgaande oproepen</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getOutCalls()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getOutCallUnitCost()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getOutCost()%></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" height="1" bgcolor="#000000"></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubsubtitle">&nbsp;&nbsp;Totaal oproepen</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getTotalCalls()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="bodytekst">&nbsp;&nbsp;</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getCallsCost()%></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" height="1" bgcolor="#000000"></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubsubtitle">&nbsp;&nbsp;doorgeschakelde oproepen</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getForwardCalls()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getFwdCallUnitCost()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getForwardCost()%></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubsubtitle">&nbsp;&nbsp;Sms berichten</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getSmsCalls()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getSmsCallUnitCost()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getSmsCost()%></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubsubtitle">&nbsp;&nbsp;FAX</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getFaxCalls()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getFaxUnitCost()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getFaxCost()%></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubsubtitle">&nbsp;&nbsp;toeslag lange oproepen</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getLongCalls()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vAccountData.getFacLong()%> (<%=vInvoiceHelper.getLongCallsSeconds()%> seconden)</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getLongCost()%></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubsubtitle">&nbsp;&nbsp;toeslag lange doorgeschakelde oproepen</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getLongFwdCalls()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vAccountData.getFacLongFwd()%> (<%=vInvoiceHelper.getLongFwdCallsSeconds()%> seconden)</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getLongFwdCost()%></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" height="1" bgcolor="#000000"></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubsubtitle">&nbsp;&nbsp;Agenda beheer</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getAgendaCalls()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getAgendaCostString()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getAgendaCost()%></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubsubtitle">&nbsp;&nbsp;Taken</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getNrOfTasks()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="bodytekst">&nbsp;&nbsp;(taak afhankelijk)</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getTaskCost()%></td>
                    </tr>
                
                    <%
                              }
                              else if (vAccountData.getInvoiceType() == InvoiceHelper.kTelemarketingInvoice)
                              {
                    %>
	                <tr>
	                <p><span class="adminsubtitle"> Telemarketing facturatie.</span></p>
	                </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubtitle">&nbsp;&nbsp;Kosten rubriek</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="adminsubtitle">&nbsp;&nbsp;Aantal</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="adminsubtitle">&nbsp;&nbsp;Eenheidsprijs</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="adminsubtitle">&nbsp;&nbsp;Kost</td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" height="1" bgcolor="#000000"></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubsubtitle">&nbsp;&nbsp;Taken</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getNrOfTasks()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="bodytekst">&nbsp;&nbsp;(taak afhankelijjk)</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getTaskCost()%></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubsubtitle">&nbsp;&nbsp;Level 1 (geen succes)</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getLevel1Calls()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getLevel1UnitCost()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getLevel1Cost()%></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubsubtitle">&nbsp;&nbsp;Level 2 (contact)</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getLevel2Calls()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getLevel2UnitCost()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getLevel2Cost()%></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubsubtitle">&nbsp;&nbsp;Level 3 (afspraak)</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getLevel3Calls()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getLevel3UnitCost()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getLevel3Cost()%></td>
                    </tr>
                
        <%
                                }
                                else if (vAccountData.getInvoiceType() == InvoiceHelper.kNoCallsAccount)
                                {
        %>
                    <tr>
                    <p><span class="adminsubtitle"> Facturatie zonder oproepen.</span></p>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubtitle">&nbsp;&nbsp;Kosten rubriek</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="adminsubtitle">&nbsp;&nbsp;Aantal</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="adminsubtitle">&nbsp;&nbsp;Eenheidsprijs</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="adminsubtitle">&nbsp;&nbsp;Kost</td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" height="1" bgcolor="#000000"></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubsubtitle">&nbsp;&nbsp;Taken</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getNrOfTasks()%></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="bodytekst">&nbsp;&nbsp;(taak afhankelijjk)</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="bodytekst">&nbsp;&nbsp;<%=vInvoiceHelper.getTaskCost()%></td>
                    </tr>
                
                    <%
                }
                    %>
                    <tr>
                        <td width="20"></td>
                        <td width="200" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" height="1" bgcolor="#000000"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" height="1" bgcolor="#000000"></td>
                    </tr>
                    <tr>
                        <td width="20"></td>
                        <td width="200" valign="top" class="adminsubtitle">&nbsp;&nbsp;Totaal</td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="50"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="270" valign="top" class="bodytekst"></td>
                        <td width="1" bgcolor="#000000"></td>
                        <td width="100" valign="top" class="bodyredbold">&nbsp;&nbsp;<%=vInvoiceHelper.getTotalCost()%>&nbsp;&nbsp;(<%=vInvoiceHelper.getTotalCostInclBTW()%> incl BTW)</td>
                    </tr>
                </table>
 		<br>
		<%
		
		// print the lists
		if (vTasks != null && vTasks.size() > 0)
		{
	    %>
		<p class="adminsubtitle"><img src=".\images\blueSphere.gif"
			width="10" height="10">&nbsp;&nbsp;&nbsp;Takenlijst voor deze maand:</p>
		<table width="100%" border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="20" bgcolor="FFFFFF"></td>
				<td width="40" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Datum</td>
				<td width="295" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Omschrijving</td>
				<td width="100" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Kost</td>
			</tr>
			<%
							for (Iterator i = vTasks.iterator(); i.hasNext();)
							{
							    TaskEntityData vEntry = ((TaskEntityData) i.next());
							
							    String vId = "id" + vEntry.getId();
							    String vKost;
							    double vTaskCost;
							
							    if (vEntry.getIsFixedPrice())
							    {
							        vTaskCost = vEntry.getFixedPrice();
				//			        vKost = new String(vEntry.getFixedPrice() + ".00 Euro");
							    }
							    else
							    {
							        vTaskCost = ((double) vEntry.getTimeSpend() / 60.00) * ((double) vAccountData.getTaskHourRate() / 100.00);
							    }
						        vKost = new String(vInvoiceHelper.format(vTaskCost) + " Euro");
						%>
						<tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst">
				<td width="20" bgcolor="FFFFFF"></td>
				<td width="40" valign="top"><%=vEntry.getDate()%></td>
				<td width="295" valign="top"><%=vEntry.getDescription()%></td>
				<td width="100" valign="top"><%=vKost%></td>
			</tr>
			<%
							}
			%>
		</table>
		<br>
		<br>
		<%
		}
		if (vRecords != null && vRecords.size() > 0)
		{
		%>
		<p class="adminsubtitle"><img src=".\images\blueSphere.gif"
			width="10" height="10">&nbsp;&nbsp;&nbsp;Oproepenlijst:</p>
		<table width="100%" border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="20" bgcolor="FFFFFF"></td>
				<td width="10" valign="top" class="topMenu" bgcolor="#F89920"></td>
				<td width="55" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Datum</td>
				<td width="35" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Uur</td>
				<td width="85" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Nummer</td>
				<td width="140" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Naam</td>
				<td width="50" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Duur</td>
				<td width="50" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Infos</td>
			</tr>
	
			<%
								 int vRowInd = 0;
								 for (Iterator i = vRecords.iterator(); i.hasNext();)
								 {
								 	CallRecordEntityData vEntry = ((CallRecordEntityData) i.next());
								
								 	String vId = "id" + vEntry.getId();
								    String vDate = vEntry.getDate();
								    String vTime = vEntry.getTime();
								    String vNumber = vEntry.getNumber();
								    String vName = vEntry.getName();
								    String vDuration = vEntry.getCost();
								    vName = vName == null ? "" : vName;
								    String vStyleStart = "";
								    String vStyleEnd = "";
								    String vInOut;
								    if (vEntry.getIsIncomingCall())
								        vInOut = "\"/tba/images/incall.gif\"";
								    else
								        vInOut = "\"/tba/images/outcall.gif\"";
								    String vInfoGifs = "";
								    if (vEntry.getIsAgendaCall())
								    {
								        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/agenda.gif\" height=\"13\" border=\"0\">&nbsp;");
								    }
								    if (vEntry.getIsSmsCall())
								    {
								        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/sms.gif\"  height=\"13\" border=\"0\">&nbsp;");
								    }
								    if (vEntry.getIsForwardCall())
								    {
								        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/telefoon.gif\"  height=\"13\" border=\"0\">&nbsp;");
								    }
								    if (vEntry.getIsFaxCall())
								    {
								        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/fax.gif\"  height=\"13\" border=\"0\">&nbsp;");
								    }
								    if (vEntry.getIs3W_call())
								    {
								        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/3w.gif\"  height=\"13\" border=\"0\">&nbsp;");
									}
							      	String vImportant = "";
							        if (vEntry.getIsImportantCall())
						         	{
						             	vImportant = vImportant.concat("<img src=\"/tba/images/important.gif\"  height=\"13\" border=\"0\">&nbsp;");
						            }
						            long seconds = InvoiceHelper.duration2Seconds(vDuration);
						            if (seconds > Constants.NORMAL_CALL_LENGTH)
					                {
					                    vInfoGifs = vInfoGifs.concat("<span class=\"bodyredbold\">L" + (seconds-Constants.NORMAL_CALL_LENGTH) + "</span>&nbsp;");
					                }
					                if (!vEntry.getIsDocumented())
					                {
					                    vStyleStart = "<b>";
					                    vStyleEnd = "</b>";
					                }
				%>
			<tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst" onmouseover="hooverOnRow('<%=vId%>','<%=vRowInd%>')"
					onmouseout="hooverOffRow('<%=vId%>','<%=vRowInd%>')"
				ondblclick="changeUrl('/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.RECORD_UPDATE%>&<%=Constants.RECORD_ID%>=<%=vEntry.getId()%>');">
				<td width="20" bgcolor="FFFFFF"><img src=<%=vInOut%> height="13" border="0"></td>
				<td width="10" valign="top"><%=vImportant%></td>
				<td width="55" valign="top"><%=vStyleStart%><%=vDate%><%=vStyleEnd%></td>
				<td width="35" valign="top"><%=vStyleStart%><%=vTime%><%=vStyleEnd%></td>
				<td width="85" valign="top"><%=vStyleStart%><%=vNumber%><%=vStyleEnd%></td>
				<td width="140" valign="top"><%=vStyleStart%><%=vName%><%=vStyleEnd%></td>
				<td width="50" valign="top"><%=vDuration%></td>
				<td width="50" valign="top"><%=vInfoGifs%></td>
			</tr>
			<%
									vRowInd++;
								}
			%>
			</table>
			<%
	        			    }
				       	}
	}
		%>
		</td>
	</tr>
</table>
<input type=hidden name=<%=Constants.INVOICE_TO_SAVE%> value="<%=vInvoiceId%>"> 
</form>
</body>

            <%
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
            %>

<script type="text/javascript">

var invoiceToSave = null
var linesToDelete = new Array();

function hooverOnRow(id, rowInd, colour)
{
  var entry = document.getElementById(id) ;
  if (linesToDelete[rowInd] == null)
    entry.style.backgroundColor= colour;
  else
    entry.style.backgroundColor= "FF9966";
}

function hooverOffRow(id, rowInd, colour)
{
  var entry = document.getElementById(id) ;
  if (linesToDelete[rowInd] == null)
    entry.style.backgroundColor= colour;
  else
    entry.style.backgroundColor= "FF9966";
}

function getInvoice()
{
  document.invoiceform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_INVOICE%>";
}

function saveInvoice()
{
    document.invoiceform.<%=Constants.SRV_ACTION%>.value="<%=Constants.SAVE_INVOICE%>";
}

function savePayDate()
{
    document.invoiceform.<%=Constants.SRV_ACTION%>.value="<%=Constants.SAVE_PAYDATE%>";
}

function createCreditNote()
{
    document.invoiceform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GENERATE_CREDITNOTE%>";
}

function backToList()
{
    document.invoiceform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_INVOICE_ADMIN%>";
}

</script>

</html>


