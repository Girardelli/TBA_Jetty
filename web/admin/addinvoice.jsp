<html>

<%@ include file="adminheader.jsp" %>

<%@ page
    import="java.util.*,
   be.tba.sqldata.*,
    be.tba.util.constants.*,
    be.tba.util.exceptions.AccessDeniedException,
    be.tba.session.SessionManager,
    be.tba.sqldata.AccountCache,
    be.tba.util.invoice.InvoiceHelper,
    be.tba.util.invoice.InvoiceData,
    be.tba.sqladapters.*,
    be.tba.util.data.*"%>
<%

    try
    {
        vSession.setCallingJsp(Constants.ADD_INVOICE_JSP);
    
        boolean vCustomerFilterOn = false;
    
        int vCustomerFilter = vSession.getCallFilter().getCustFilter();//request.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER);
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
        if (vAccountData == null && vCustomerFilter > 0)
        {
            vAccountData =  AccountCache.getInstance().get(vCustomerFilter);
        }
        if (vInvoiceRef == null) vInvoiceRef = "";
        
%>
<body>
<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
    <tr>
        <!-- white space -->
        <td valign="top" width="20" bgcolor="FFFFFF"></td>

        <!-- account list -->
        <td valign="top" width="865" bgcolor="FFFFFF"><br>
        <p><span class="bodytitle">Manueel facturen toevoegen <%=(vAccountData == null)? "Selecteer een klant en maand." : "" %><br>
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
                <td width="250" valign="top" class="bodysubtitle">&nbsp;Klant</td>
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
                <td width="250" valign="top" class="bodysubtitle">&nbsp;Maand</td>
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
                <td width="250" valign="top" class="bodysubtitle">&nbsp;Jaar</td>
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
                <td width="250" valign="top" class="bodysubtitle">&nbsp;Bedrag (zonder BTW)</td>
                <td width="10" valign="top">:</td>
                <td valign="top" class="bodysubsubtitle">
                    <input type=text size=20 name=<%=Constants.INVOICE_AMONTH%> value="">
                </td>
            </tr>
            <tr>
                <td width="250" valign="top" class="bodysubtitle">&nbsp;Beschrijving</td>
                <td width="10" valign="top">:</td>
                <td valign="top" class="bodysubsubtitle">
                   <textarea name=<%=Constants.INVOICE_DESCRIPTION%> rows=12 cols=70></textarea>
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
        </td>
    </tr>
</table>

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
  document.invoiceform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_CANVAS%>";
}
</script>

</body>

</html>

