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
        vSession.setCallingJsp(Constants.ADD_INVOICE_JSP);
    
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

<p><span class="admintitle">&nbsp;&nbsp;&nbsp;Manueel facturen toevoegen <%=(vAccountData == null)? "Selecteer een klant en maand." : "" %><br>
<br>
<br>
</span></p>
<form name="invoiceform" method="POST" action="/tba/AdminDispatch">
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
                <td valign="top">
                <select
                    name="<%=Constants.ACCOUNT_ID%>">
                    <%
        Collection<AccountEntityData> list = AccountCache.getInstance().getInvoiceCustomerList();
        synchronized(list) 
        {
            for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();)
            {
                AccountEntityData vData = vIter.next();
                out.println("<option value=\"" + vData.getId() + "\">" + vData.getFullName());
            }
        }
                    %>
                </select>
                </td>
            </tr>
            <tr>
                <td width="250" valign="top" class="adminsubtitle">&nbsp;Maand</td>
                <td width="10" valign="top">:</td>
                <td valign="top">
                <select
                    name="<%=Constants.INVOICE_MONTH%>">
                    <%
        out.println("<option value=\"" + CallFilter.kNoMonth + (CallFilter.kNoMonth == vMonth ? "\" selected>" : "\">") + "selecteer maand");
        for (int i = 0; i < Constants.MONTHS.length; ++i)
        {
            out.println("<option value=\"" + i + (i == vMonth ? "\" selected>" : "\">") + Constants.MONTHS[i]);
        }
                    %>
                </select>
                </td>
            </tr>
            <tr>
                <td width="250" valign="top" class="adminsubtitle">&nbsp;Jaar</td>
                <td width="10" valign="top">:</td>
                <td valign="top">
                <select
                    name="<%=Constants.INVOICE_YEAR%>">
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
            <tr>
                <td width="250" valign="top" class="adminsubtitle">&nbsp;Bedrag (zonder BTW)</td>
                <td width="10" valign="top">:</td>
                <td valign="top" class="adminsubsubtitle">
                    <input type=text size=20 name=<%=Constants.INVOICE_AMONTH%> value="">
                </td>
            </tr>
            <tr>
                <td width="250" valign="top" class="adminsubtitle">&nbsp;Beschrijving</td>
                <td width="10" valign="top">:</td>
                <td valign="top" class="adminsubsubtitle">
                   <textarea name=<%=Constants.INVOICE_DESCRIPTION%> rows=12 cols=70></textarea>
                </td>
            </tr>
            <tr>
                <td width="250" valign="top" class="adminsubtitle">&nbsp;Is Credit Nota</td>
                <td width="10" valign="top">:</td>
                <td valign="top" class="adminsubsubtitle">
                    <input type=checkbox name=<%=Constants.INVOICE_IS_CREDITNOTA%> value="">&nbsp;&nbsp;&nbsp;Voor Factuur nummer:&nbsp;&nbsp;
                    <input type=text size=20 name=<%=Constants.INVOICE_NR%> value="">
                </td>
            </tr>
        </table>
        <br>
        <br>
        <input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.INVOICE_ADD%>"> 
        <input class="tbabutton" type=submit name=action value=" Maak "> 
        <input class="tbabutton" type=submit value=" Terug " onclick="cancelAdd();">
    </td>
    </tr>
</table>
</form>

<%       
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
%>
<script type="text/javascript">

function cancelAdd()
{
  document.taskform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_CANVAS%>";
}
</script>

</body>

</html>

