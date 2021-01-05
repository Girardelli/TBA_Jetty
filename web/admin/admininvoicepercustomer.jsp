<html>

<%@ include file="adminheader.jsp" %>

<head>
</head>
<%@ page
	import="java.util.*,
	be.tba.sqldata.*,
	be.tba.util.constants.Constants,
	be.tba.util.exceptions.AccessDeniedException,
	be.tba.session.SessionManager,
	be.tba.sqldata.AccountCache,
	be.tba.util.invoice.InvoiceHelper,
	java.text.DecimalFormat,
   be.tba.sqldata.AccountEntityData,
   be.tba.sqladapters.InvoiceSqlAdapter,
	be.tba.util.data.*"%>
<%
StringBuilder allEntryIds = new StringBuilder("[");

try
{
    vSession.setCallingJsp(Constants.ADMIN_INVOICE_CUST_JSP);
    AccountEntityData vAccount = null;
    int accountId = vSession.getAccountId();
    if (accountId != 0)
    {
       vAccount = AccountCache.getInstance().get(accountId);
    }
%>
<body>

<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
    <tr>
        <!-- white space -->
        <td valign="top" width="20" bgcolor="FFFFFF"></td>

        <!-- account list -->
        <td valign="top" width="865" bgcolor="FFFFFF"><br>



<p><span class="bodytitle"> Factuurlijst bewerken<br>
</span></p>
<form name="invoicelistform" method="POST" action="/tba/AdminDispatch">
	<input type=hidden name=<%=Constants.INVOICE_TO_SETPAYED%> value=""> 
	<input type=hidden name=<%=Constants.INVOICE_TO_DELETE%> value="">
	<input type=hidden name=<%=Constants.INVOICE_ID%> value="">
	<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.GOTO_INVOICE_ADMIN_CUSTOMER%>"> 
	<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
		<tr>
			<!-- white space -->
			<td valign="top" width="20" bgcolor="FFFFFF"></td>
	
			<!-- account list -->
			<td valign="top" width="865" bgcolor="FFFFFF"><br>
			<table>
            <tr>
                <td width="250" valign="top" class="bodysubtitle">&nbsp;Klant</td>
                <td width="10" valign="top">:</td>
                <td width="270" valign="top">
                <select
                    name="<%=Constants.ACCOUNT_FILTER_CUSTOMER%>" onchange="submit()">
                    <%

        out.println("<option value=\"" + Constants.ACCOUNT_NOFILTER + (accountId == 0 ? "\" selected>" : "\">") + "selecteer klant");
        Collection<AccountEntityData> list = AccountCache.getInstance().getInvoiceCustomerList();
        synchronized(list) 
        {
            for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();)
            {
                AccountEntityData vData = (AccountEntityData) vIter.next();
                out.println("<option value=\"" + vData.getId() + (accountId == vData.getId() ? "\" selected>" : "\">") + vData.getFullName());
            }
        }
                    %>
                </select>
                </td>
            </tr>
			</table>
			<br>
			<!--  <input class="tbabutton" type=submit name=action value=" Factuur toevoegen " onclick="toevoegen()">-->
			<input class="tbabutton" type=submit name=action value=" Verwijderen " onclick="verwijderen()">
            <input class="tbabutton" type=submit name=action value=" Mailen " onclick="mailen()">
			<input class="tbabutton" type=submit name=action value=" Betaaldvlag zetten " onclick="setPayed()">
			<br>
			<%
          Collection<InvoiceEntityData> vInvoices = null;
          InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();

          if (accountId > 0)
             vInvoices = vInvoiceSession.getCustomerInvoiceList(vSession, accountId);

          if (vInvoices == null || vInvoices.size() == 0)
          {
              out.println("<p><span class=\"bodysubtitle\"> Geen facturen gevonden voor deze klant.</span></p>");
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
              out.println("<p><span class=\"bodysubtitle\"> Totaal gefactureerd: " + vCostFormatter.format(vTotalInvoice) + " (Excl BTW)</span></p>");
			%> <br>
			<table border="0" cellspacing="2" cellpadding="2">
				<tr>
					<td width="150" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Betaald</td>
					<td width="110" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Nummer</td>
					<td width="60" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Excl BTW</td>
					<td width="60" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Incl BTW</td>
                    <td width="110" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Uittreksel</td>
                    <td width="30" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Info</td>
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
                 if (!vEntry.getIsPayed())
                 {
                    vCollor = "FF9797";
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
                 double vKost = vEntry.getTotalCost();
                 
                 //extractCustomerNameFromInvoiceFileName
                 
				if (vEntry.getIsPayed())
				{
				    vEuroGif = "<img src=\"/tba/images/euro-16x16.png\" height=\"16\" border=\"0\">";
				}
                  String vInfoGifs = "";
                  if (!vEntry.getComment().isBlank()) 
                  {
                      vInfoGifs = vInfoGifs.concat(
                            "<img src=\"/tba/images/info.gif\" height=\"16\" border=\"0\">");
                  }
				%>
				<tr bgcolor=<%=vCollor%> id=<%=vId%> class="bodytekst"
					onmouseover="hooverOnRow('<%=vId%>','<%=vRowInd%>')"
					onmouseout="hooverOffRow('<%=vId%>','<%=vRowInd%>')"
					onclick="updateDeleteFlag('<%=vId%>','<%=vEntry.getId()%>','<%=vRowInd%>')"
					ondblclick="changeUrl('/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_INVOICE%>&<%=Constants.ACCOUNT_FILTER_CUSTOMER%>=<%=accountId%>&<%=Constants.INVOICE_ID%>=<%=vEntry.getId()%>');">
					<td width="150" valign="top"><%=vEuroGif%><%=vEntry.getPayDate()%></td>
					<td width="110" valign="top"><%=vStyleStart%><%=vEntry.getInvoiceNr()%><%=vStyleEnd%></td>
					<td width="60" valign="top"><%=vStyleStart%><%=vCostFormatter.format(vKost)%><%=vStyleEnd%></td>
					<td width="60" valign="top"><%=vStyleStart%><%=((vAccount!= null && vAccount.getNoBtw()) ? "0.0" : vCostFormatter.format(vKost * 1.21))%><%=vStyleEnd%></td>
                    <td width="110" valign="top"><%=vEntry.getFintroId()%></td>
                    <td width="30" valign="top"><%=vInfoGifs%></td>
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
        </td>
    </tr>
</table>
<br>
<br>
<br>
</body>

</html>

<script type="text/javascript">
var invoiceArray = new Array();

function hooverOnRow(id, rowInd, colour)
{
  var entry = document.getElementById(id) ;
  if (invoiceArray[rowInd] == null)
    entry.style.backgroundColor= colour;
  else
    entry.style.backgroundColor= "FF9966";
}

function hooverOffRow(id, rowInd, colour)
{
  var entry = document.getElementById(id) ;
  if (invoiceArray[rowInd] == null)
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
  for (var i = 0; i < invoiceArray.length; i++)
    if (invoiceArray[i] != null)
      shorterArr[j++] = invoiceArray[i];
  document.invoicelistform.<%=Constants.INVOICE_TO_DELETE%>.value=shorterArr.join();
  document.invoicelistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.INVOICE_DELETE%>";
}

function vriezen()
{
  var shorterArr = new Array();
  var j = 0;
  for (var i = 0; i < invoiceArray.length; i++)
    if (invoiceArray[i] != null)
      shorterArr[j++] = invoiceArray[i];
  document.invoicelistform.<%=Constants.INVOICE_TO_FREEZE%>.value=shorterArr.join();
  document.invoicelistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.INVOICE_FREEZE%>";
}

function mailen()
{
  var shorterArr = new Array();
  var j = 0;
  for (var i = 0; i < invoiceArray.length; i++)
    if (invoiceArray[i] != null)
      shorterArr[j++] = invoiceArray[i];
  document.invoicelistform.<%=Constants.INVOICE_TO_FREEZE%>.value=shorterArr.join();
  document.invoicelistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.INVOICE_MAIL%>";
}

function setPayed()
{
  var shorterArr = new Array();
  var j = 0;
  for (var i = 0; i < invoiceArray.length; i++)
    if (invoiceArray[i] != null)
      shorterArr[j++] = invoiceArray[i];
  document.invoicelistform.<%=Constants.INVOICE_TO_SETPAYED%>.value=shorterArr.join();
  document.invoicelistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.INVOICE_SETPAYED%>";
}

function updateDeleteFlag(rowid, id, rowInd)
{
  var entry = document.getElementById(rowid) ;
  if (invoiceArray[rowInd] == null)
  {
    invoiceArray[rowInd] = id;
    entry.style.backgroundColor= "FF9966";
  }
  else
  {
    invoiceArray[rowInd] = null;
    entry.style.backgroundColor= "FFFF99";
  }
}

function generateAllInvoices()
{
  document.invoicelistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GENERATE_ALL_INVOICES%>";
}

function downloadExportFile()
{
	  var shorterArr = new Array();
	  var j = 0;
	  for (var i = 0; i < invoiceArray.length; i++)
	    if (invoiceArray[i] != null)
	      shorterArr[j++] = invoiceArray[i];
	  document.downloadfileform.<%=Constants.INVOICE_TO_SETPAYED%>.value=shorterArr.join();
	  document.downloadfileform.<%=Constants.SRV_ACTION%>.value="<%=Constants.DOWNLOAD_WK_VERKOPEN_XML%>";
}

function openInvoice(id, rowInd)
{
  var entry = document.getElementById(id);
  invoiceArray[rowInd] = null;
  entry.style.backgroundColor = "FFFF99";
  document.invoicelistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_INVOICE%>";
}

function selectAll()
{
  var allArr = <%=allEntryIds.toString()%>;
  for (var i = 0; i < allArr.length; i++)
  {
    invoiceArray[i] = allArr[i].substring(2);
    var entry = document.getElementById(allArr[i]) ;
    entry.style.backgroundColor= "FF9966";
  }
}

function deselectAll()
{
  var allArr = <%=allEntryIds.toString()%>;
  for (var i = 0; i < allArr.length; i++)
  {
    invoiceArray[i] = null;
    var entry = document.getElementById(allArr[i]) ;
    entry.style.backgroundColor= "FFCC66";
  }
}

function reverseSelection()
{
  var allArr = <%=allEntryIds.toString()%>;
  for (var i = 0; i < allArr.length; i++)
  {
    if (invoiceArray[i] == null)
    {
      invoiceArray[i] = allArr[i].substring(2);
      var entry = document.getElementById(allArr[i]) ;
      entry.style.backgroundColor= "FF9966";
    }
    else
    {
      invoiceArray[i] = null;
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

