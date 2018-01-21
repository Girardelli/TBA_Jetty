<html>

<%@ include file="adminheader.jsp" %>

<head>
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
	be.tba.util.data.*"%>
<%!
private StringBuilder allEntryIds;%>

<%

            try
            {
                vSession.setCallingJsp(Constants.ADMIN_INVOICE_JSP);

                int vMonth = vSession.getMonthsBack();
                int vYear = vSession.getYear();
                Calendar vToday = Calendar.getInstance();
                int vCurYear = vToday.get(Calendar.YEAR);

                InitialContext vContext = new InitialContext();
%>
<body>
<p><span class="admintitle"> Factuurlijst bewerken<br>
</span></p>
<form name="invoicelistform" method="POST" action="/TheBusinessAssistant/AdminDispatch">
	<input type=hidden name=<%=Constants.INVOICE_TO_FREEZE%> value=""> 
	<input type=hidden name=<%=Constants.INVOICE_TO_SETPAYED%> value=""> 
	<input type=hidden name=<%=Constants.INVOICE_TO_DELETE%> value="">
	<input type=hidden name=<%=Constants.INVOICE_ID%> value="">
	<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.GOTO_INVOICE_ADMIN%>"> 
	<input type=hidden name=<%=Constants.ACCOUNT_FILTER_CUSTOMER%> value="">
	<table width='100%' cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
		<tr>
			<!-- white space -->
			<td valign="top" width="20" bgcolor="FFFFFF"></td>
	
			<!-- account list -->
			<td valign="top" width="865" bgcolor="FFFFFF"><br>
			<table>
				<tr>
					<td width="100" valign="top" class="adminsubtitle">&nbsp;Maand</td>
					<td width="10" valign="top">:</td>
					<td width="170" valign="top">
						<select	name="<%=Constants.INVOICE_MONTH%>" onchange="submit()">
<%
	out.println("<option value=\"" + CallFilter.kNoMonth + ((CallFilter.kNoMonth == vMonth) ? "\" selected>" : "\">") + "selecteer maand");
	for (int i = 0; i < Constants.MONTHS.length; ++i)
	{
	    out.println("<option value=\"" + i + (i == vMonth ? "\" selected>" : "\">") + Constants.MONTHS[i]);
	}
%>
						</select>
					</td>
					<td width="100" valign="top" class="adminsubtitle">&nbsp;Jaar</td>
					<td width="10" valign="top">:</td>
					<td width="170" valign="top">
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
						<br>
						<br>
						<br>
					</td>
				</tr>
<!--  				
				<tr>
					<td width="80"><input class="tbabutton" type=submit name=action value=" Verwijderen " onclick="verwijderen()"></td>
					<td width="80"><input class="tbabutton" type=submit name=action value=" Bevriezen " onclick="vriezen()"></td>
					<td width="80"><input class="tbabutton" type=submit name=action value=" Betaaldvlag zetten " onclick="setPayed()"></td>
					<td width="80"><input class="tbabutton" type=submit name=action value=" Betaaldvlag zetten " onclick="setPayed()"></td>
					<td width="180"><input class="tbabutton" type=submit name=action value=" Genereer factuur docs "	onclick="generateAllInvoices()"></td>
				</tr> -->
			</table>
			<br>
			<input class="tbabutton" type=submit name=action value=" Factuur toevoegen " onclick="toevoegen()">
			<input class="tbabutton" type=submit name=action value=" Verwijderen " onclick="verwijderen()">
			<input class="tbabutton" type=submit name=action value=" Bevriezen en docs genereren " onclick="vriezen()">
            <input class="tbabutton" type=submit name=action value=" Mailen " onclick="mailen()">
			<input class="tbabutton" type=submit name=action value=" Betaaldvlag zetten " onclick="setPayed()">
			<br>
			<input class="tbabutton" type=submit name=action value=" Genereer factuurlijst " onclick="generateAllInvoices()">
			<br>
			<%
	
			                allEntryIds = new StringBuilder("[");
	
			                Collection<InvoiceEntityData> vInvoices = null;
			                InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
	
			                vInvoices = vInvoiceSession.getInvoiceList(vSession, vMonth, vYear);
	
			                if (vInvoices == null || vInvoices.size() == 0)
			                {
			                    out.println("<p><span class=\"adminsubtitle\"> Geen facturen gevonden voor deze " + Constants.MONTHS[vMonth] + "/" + vYear + ".</span></p>");
			                }
			                else
			                {
			                    DecimalFormat vCostFormatter = new DecimalFormat("#0.00");
			                    double vTotalInvoice = 0.0;
			                    
			                    for (Iterator<InvoiceEntityData> i = vInvoices.iterator(); i.hasNext();)
			                    {
			                        InvoiceEntityData vEntry = ((InvoiceEntityData) i.next());
			                        vTotalInvoice += vEntry.getTotalCost();
			                    }
			                    out.println("<p><span class=\"adminsubtitle\"> Totaal gefactureerd voor " + Constants.MONTHS[vMonth] + "/" + vYear + ": " + vCostFormatter.format(vTotalInvoice) + " (Excl BTW)</span></p>");
			%> <br>
			<table border="0" cellspacing="2" cellpadding="2">
				<tr>
					<td width="150" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Betaald</td>
					<td width="110" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Nummer</td>
					<td width="350" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Klant</td>
					<td width="50" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Excl BTW</td>
					<td width="50" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Incl BTW</td>
				</tr>
	
				<%

         int vRowInd = 0;
         for (Iterator<InvoiceEntityData> i = vInvoices.iterator(); i.hasNext();)
         {
             InvoiceEntityData vEntry = ((InvoiceEntityData) i.next());
             if (vEntry.getTotalCost() != 0)
             {
                 String vCollor = "FFCC66";
                 if (vEntry.getFrozenFlag())
                 {
                     vCollor = "CCDD00";
                 }
                 String vMailed = "";
                 String vStyleStart = "";
                 String vStyleEnd = "";
                 if (vEntry.getIsInvoiceMailed())
                 {
                     vStyleStart = vStyleStart.concat("<i>");
                     vStyleEnd = vStyleEnd.concat("</i>");
                 }
                 String vId = "id" + vEntry.getId();
                 String vEuroGif = "";
                 String vCompanyName;
                 double vKost = vEntry.getTotalCost();
                 AccountEntityData vAccount = AccountCache.getInstance().get(vEntry.getAccountFwdNr());
                 
                 if (vAccount != null || (vEntry.getCustomerName() != null && vEntry.getCustomerName().length() > 0))
                 {
                     if (vAccount != null)
                     {
                         vCompanyName = vAccount.getFullName();
                     }
                     else
                     {
                         vCompanyName = vEntry.getCustomerName();
                     }
                 }
                 else
                 {
                 	vCompanyName = vEntry.getFileName();
                 }
				if (vEntry.getIsPayed())
				{
				    vEuroGif = "<img src=\"/TheBusinessAssistant/images/euro-16x16.png\" height=\"16\" border=\"0\">";
				}
				%>
				<tr bgcolor=<%=vCollor%> id=<%=vId%> class="bodytekst"
					onmouseover="hooverOnRow('<%=vId%>','<%=vRowInd%>')"
					onmouseout="hooverOffRow('<%=vId%>','<%=vRowInd%>')"
					onclick="updateDeleteFlag('<%=vId%>','<%=vEntry.getId()%>','<%=vRowInd%>')"
					ondblclick="changeUrl('/TheBusinessAssistant/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_INVOICE%>&<%=Constants.ACCOUNT_FILTER_CUSTOMER%>=<%=vEntry.getAccountFwdNr()%>&<%=Constants.INVOICE_ID%>=<%=vEntry.getId()%>');">
					<td width="150" valign="top"><%=vEuroGif%>&nbsp&nbsp<%=vEntry.getPayDate()%></td>
					<td width="110" valign="top"><%=vStyleStart%><%=vEntry.getInvoiceNr()%><%=vStyleEnd%></td>
					<td width="350" valign="top"><%=vStyleStart%><%=vCompanyName%><%=vStyleEnd%></td>
					<td width="50" valign="top"><%=vStyleStart%><%=vCostFormatter.format(vKost)%><%=vStyleEnd%></td>
					<td width="50" valign="top"><%=vStyleStart%><%=((vAccount!= null && vAccount.getNoBtw()) ? "0.0" : vCostFormatter.format(vKost * 1.21))%><%=vStyleEnd%></td>
				</tr>
				<%
			    vRowInd++;
				allEntryIds.append("\"");
				allEntryIds.append(vId);
				allEntryIds.append("\"");
				allEntryIds.append(",");
            }
        }
        if (vRowInd > 0)
        {
            allEntryIds.deleteCharAt(allEntryIds.length() - 1);
        }
				%>
			</table>
		<%

    }
    allEntryIds.append("]");		            
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

function hooverOnRow(id, rowInd, colour)
{
  var entry = document.getElementById(id) ;
  if (invoicesToDelete[rowInd] == null)
    entry.style.backgroundColor= colour;
  else
    entry.style.backgroundColor= "FF9966";
}

function hooverOffRow(id, rowInd, colour)
{
  var entry = document.getElementById(id) ;
  if (invoicesToDelete[rowInd] == null)
    entry.style.backgroundColor= colour;
  else
    entry.style.backgroundColor= "FF9966";
}

function toevoegen()
{
  document.invoicelistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_INVOICE_ADD%>";
}

function verwijderen()
{
  var shorterArr = new Array();
  var j = 0;
  for (var i = 0; i < invoicesToDelete.length; i++)
    if (invoicesToDelete[i] != null)
      shorterArr[j++] = invoicesToDelete[i];
  document.invoicelistform.<%=Constants.INVOICE_TO_DELETE%>.value=shorterArr.join();
  document.invoicelistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.INVOICE_DELETE%>";
}

function vriezen()
{
  var shorterArr = new Array();
  var j = 0;
  for (var i = 0; i < invoicesToDelete.length; i++)
    if (invoicesToDelete[i] != null)
      shorterArr[j++] = invoicesToDelete[i];
  document.invoicelistform.<%=Constants.INVOICE_TO_FREEZE%>.value=shorterArr.join();
  document.invoicelistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.INVOICE_FREEZE%>";
}

function mailen()
{
  var shorterArr = new Array();
  var j = 0;
  for (var i = 0; i < invoicesToDelete.length; i++)
    if (invoicesToDelete[i] != null)
      shorterArr[j++] = invoicesToDelete[i];
  document.invoicelistform.<%=Constants.INVOICE_TO_FREEZE%>.value=shorterArr.join();
  document.invoicelistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.INVOICE_MAIL%>";
}

function setPayed()
{
  var shorterArr = new Array();
  var j = 0;
  for (var i = 0; i < invoicesToDelete.length; i++)
    if (invoicesToDelete[i] != null)
      shorterArr[j++] = invoicesToDelete[i];
  document.invoicelistform.<%=Constants.INVOICE_TO_SETPAYED%>.value=shorterArr.join();
  document.invoicelistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.INVOICE_SETPAYED%>";
}

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

function generateAllInvoices()
{
  document.invoicelistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GENERATE_ALL_INVOICES%>";
}

function openInvoice(id, rowInd)
{
  var entry = document.getElementById(id);
  invoicesToDelete[rowInd] = null;
  entry.style.backgroundColor = "FFFF99";
  document.invoicelistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_INVOICE%>";
}

function selectAll()
{
  var allArr = <%=allEntryIds.toString()%>;
  for (var i = 0; i < allArr.length; i++)
  {
    invoicesToDelete[i] = allArr[i].substring(2);
    var entry = document.getElementById(allArr[i]) ;
    entry.style.backgroundColor= "FF9966";
  }
}

function deselectAll()
{
  var allArr = <%=allEntryIds.toString()%>;
  for (var i = 0; i < allArr.length; i++)
  {
    invoicesToDelete[i] = null;
    var entry = document.getElementById(allArr[i]) ;
    entry.style.backgroundColor= "FFCC66";
  }
}

function reverseSelection()
{
  var allArr = <%=allEntryIds.toString()%>;
  for (var i = 0; i < allArr.length; i++)
  {
    if (invoicesToDelete[i] == null)
    {
      invoicesToDelete[i] = allArr[i].substring(2);
      var entry = document.getElementById(allArr[i]) ;
      entry.style.backgroundColor= "FF9966";
    }
    else
    {
      invoicesToDelete[i] = null;
      var entry = document.getElementById(allArr[i]) ;
      entry.style.backgroundColor= "FFCC66";
    }
  }
}

function changeUrl(newURL) 
{
  location=newURL;
}




</script>

