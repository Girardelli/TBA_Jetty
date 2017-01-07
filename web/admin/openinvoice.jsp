<html>

<%@ include file="adminheader.jsp" %>

<head>
<meta HTTP-EQUIV="Refresh"
	content="<%=Constants.REFRESH%>;URL=\TheBusinessAssistant\admin\admincalls.jsp">
</head>
<%@ page
	import="java.util.*,
	javax.naming.InitialContext,
	be.tba.ejb.invoice.interfaces.*,
	be.tba.util.constants.EjbJndiNames,
	be.tba.util.constants.Constants,
	be.tba.util.exceptions.AccessDeniedException,
	be.tba.servlets.session.SessionManager,
	be.tba.util.session.AccountCache,
	java.text.DecimalFormat,
	be.tba.ejb.account.interfaces.AccountEntityData,
	be.tba.ejb.invoice.session.InvoiceSqlAdapter,
	be.tba.util.data.*;"
	%>
<%!
%>

<%
         try
         {
			 vSession.setCallingJsp(Constants.OPEN_INVOICE_JSP);
             InitialContext vContext = new InitialContext();
%>
<body>
<p><span class="admintitle"> Factuurlijst bewerken<br>
</span></p>
<form name="openinvoicelistform" method="POST" action="/TheBusinessAssistant/AdminDispatch">
	<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.GOTO_OPEN_INVOICE%>"> 
	<table width='100%' cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
		<tr>
			<!-- white space -->
			<td valign="top" width="20" bgcolor="FFFFFF"></td>
	
			<!-- account list -->
			<td valign="top" width="865" bgcolor="FFFFFF">
				<br>
			<%
	
			                Collection vInvoices = null;
			                InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
	
			                vInvoices = vInvoiceSession.getOpenInvoiceList(vSession);
	
			                if (vInvoices == null || vInvoices.size() == 0)
			                {
			                    out.println("<p><span class=\"adminsubtitle\"> Geen openstaande facturen</span></p>");
			                }
			                else
			                {
			                    DecimalFormat vCostFormatter = new DecimalFormat("#0.00");
			                    double vTotalInvoice = 0.0;
			                    for (Iterator i = vInvoices.iterator(); i.hasNext();)
			                    {
			                        InvoiceEntityData vEntry = ((InvoiceEntityData) i.next());
			                        vTotalInvoice += vEntry.getTotalCost();
			                    }
			                    out.println("<p><span class=\"adminsubtitle\"> Totaal openstaande facturen : " + vCostFormatter.format(vTotalInvoice) + " (Excl BTW)</span></p>");
			%> 
			<br>
			<br>
			<input type=submit name=action value=" Betaaldvlag zetten " onclick="setPayed()">
			<br>
			<br>
			<br>
			<table width="100%" border="0" cellspacing="2" cellpadding="2">
				<tr>
					<td width="90" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Nummer</td>
					<td width="300" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Klant</td>
					<td width="50" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Excl BTW</td>
					<td width="50" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Incl BTW</td>
				</tr>
	
				<%
	
				                    int vRowInd = 0;
				                    for (Iterator i = vInvoices.iterator(); i.hasNext();)
				                    {
				                        InvoiceEntityData vEntry = ((InvoiceEntityData) i.next());
				                        if (vEntry.getTotalCost() > 0)
				                        {
				                            String vCollor = "CCDD00";
				                            String vId = "id" + vEntry.getId();
				                            double vKost = vEntry.getTotalCost();
				                            AccountEntityData vAccount = AccountCache.getInstance().get(vEntry.getAccountFwdNr());
				                            
				                            if (vAccount != null)
				                            {
				%>
				<tr bgcolor=<%=vCollor%> id=<%=vId%> class="bodytekst"
					onclick="updateDeleteFlag('<%=vId%>','<%=vEntry.getId()%>','<%=vRowInd%>')">
					<td width="90" valign="top"><%=vEntry.getInvoiceNr()%></td>
					<td width="300" valign="top"><%=vAccount.getFullName()%></td>
					<td width="50" valign="top"><%=vCostFormatter.format(vKost)%></td>
					<td width="50" valign="top"><%=vCostFormatter.format(vKost * 1.21)%></td>
				</tr>
				<%
	            
				                            }
				                            else
				                            {
				                                System.out.println("AccountCache returned NULL for account number " + vEntry.getAccountFwdNr());
				                            }
				                        }
				                    }
				%>
			</table>
		<%

		                }
}
catch (Exception e)
{
    e.printStackTrace();
}
		%>
		</td>
	</tr>
</table>
</form>
<br>
<br>
<br>
</body>

</html>

<script type="text/javascript">
var invoicesToDelete = new Array();

function updateDeleteFlag(rowid, id, rowInd)
{
  var entry = document.getElementById(rowid) ;
  if (invoicesToDelete[rowInd] == null)
  {
    invoicesToDelete[rowInd] = id;
    entry.style.backgroundColor= "FF9966";
  }
  else
  {
    invoicesToDelete[rowInd] = null;
    entry.style.backgroundColor= "FFFF99";
  }
}

function setPayed()
{
  var shorterArr = new Array();
  var j = 0;
  for (var i = 0; i < invoicesToDelete.length; i++)
    if (invoicesToDelete[i] != null)
      shorterArr[j++] = invoicesToDelete[i];
  document.openinvoicelistform.<%=Constants.INVOICE_TO_SETPAYED%>.value=shorterArr.join();
  document.openinvoicelistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.INVOICE_SETPAYED%>";
}


</script>



