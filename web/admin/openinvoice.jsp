<html>

<%@ include file="adminheader.jsp" %>

<head>
<meta HTTP-EQUIV="Refresh"
	content="<%=Constants.REFRESH%>;URL=\tba\admin\openinvoice.jsp">
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
	be.tba.util.file.FileUploader,
	be.tba.util.excel.FintroXlsxReader,
	java.io.File,
	org.apache.commons.fileupload.FileItem,
	be.tba.util.data.*"	%>

<%
         try
         {
			 vSession.setCallingJsp(Constants.OPEN_INVOICE_JSP);
%>
<body>
<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
    <tr>
        <!-- white space -->
        <td valign="top" width="20" bgcolor="FFFFFF"></td>

        <!-- account list -->
        <td valign="top" width="865" bgcolor="FFFFFF"><br>
        <p><span class="admintitle">Factuurlijst bewerken<br>
        <br>



	<form name="loadfileform" method="POST" action="/tba/AdminDispatch" enctype="multipart/form-data">
	<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.GOTO_OPEN_INVOICE%>"> 
	<input class="tbabutton" type=file name=<%=Constants.FINTRO_FILE%> value=" Fintro excel opladen " accept=".xlsx">
	<input class="tbabutton" type=submit name=action value=" Laad de file op " onclick="uploadFile()">
    </form>
	<table cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
		<tr>
			<!-- white space -->
			<td valign="top" width="20" bgcolor="FFFFFF"></td>
	
			<!-- account list -->
			<td valign="top" bgcolor="FFFFFF">
				<br>
			<%
			   // check whether a Fintro file was uploaded
									   	            String fintroFileName = vSession.getUploadedFileName();
									   	            if (fintroFileName != null)
									   	            {
                                                System.out.println("Upload file " + fintroFileName);
									   	                DecimalFormat vCostFormatter = new DecimalFormat("#0.00");
									   	                FintroXlsxReader fintroXlsxReader = new FintroXlsxReader(fintroFileName);
									   	                vSession.setUploadedFileName(null);
										   	            vSession.setFintroProcessLog(fintroXlsxReader.getOutputFileName());
			%> 
				             <br>				   	            
				   	         <form name="downloadfileform" method="POST" action="/tba/download" >
						     <input class="tbabutton" type=submit name=action value=" download de procesresultaten hieronder afgedrukt " onclick="downloadProcFile()">
						     <input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.DOWNLOAD_FINTRO_PROCESS_TXT%>"> 
							 <input type=hidden name=<%=Constants.FINTRO_PROCESS_FILE%> value="<%=fintroXlsxReader.getOutputFileName()%>"> 
                             </form>
				   	         <p><span class="adminsubtitle"> Resultaten van de Fintro upload:  
                             </span></p>
				   	         <p><span class="bodytekst" >
				   	         <%=fintroXlsxReader.getHtmlProcessLog()%>
				   	         </span></p>
				   	         <%    
			   	            }
			   	            else if (vSession.getFintroProcessLog() != null && !vSession.getFintroProcessLog().isEmpty())
			   	            {
			   	               System.out.println("fintroFile = null 1");
                              File outputFile = new File(vSession.getFintroProcessLog());
			   	                outputFile.delete();
			   	                vSession.setFintroProcessLog(null); 
			   	            }
			   	            else
			   	            {
                              System.out.println("fintroFile = null 2");
			   	                vSession.setFintroProcessLog(null);
			   	            }
%>
			   	      <form name="openinvoicelistform" method="POST" action="/tba/AdminDispatch">
			   	      <input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.GOTO_OPEN_INVOICE%>"> 
			   	      <input type=hidden name=<%=Constants.INVOICE_TO_SETPAYED%> value="">
                      
                     
<%
			                Collection<InvoiceEntityData> vInvoices = null;
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
			                    for (Iterator<InvoiceEntityData> i = vInvoices.iterator(); i.hasNext();)
			                    {
			                        InvoiceEntityData vEntry = i.next();
			                        vTotalInvoice += vEntry.getTotalCost();
			                    }
			                    out.println("<p><span class=\"adminsubtitle\"> Totaal openstaande facturen : " + vCostFormatter.format(vTotalInvoice) + " (Excl BTW)</span></p>");
			%> 
			<br>
			<br>
			<input class="tbabutton" type=submit name=action value=" Betaaldvlag zetten " onclick="setPayed();">
            </form>
			<br>
			<br>
			<br>
			<table border="0" cellspacing="2" cellpadding="2">
				<tr>
					<td width="90" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Nummer</td>
					<td width="300" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Klant</td>
					<td width="50" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Excl BTW</td>
					<td width="50" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Incl BTW</td>
				</tr>
	
				<%
	
				                    int vRowInd = 0;
				                    for (Iterator<InvoiceEntityData> i = vInvoices.iterator(); i.hasNext();)
				                    {
				                        InvoiceEntityData vEntry = i.next();
				                        if (vEntry.getTotalCost() > 0)
				                        {
				                            String vCollor = "CCDD00";
				                            String vId = "id" + vEntry.getId();
				                            double vKost = vEntry.getTotalCost();
				                            AccountEntityData vAccount = AccountCache.getInstance().get(vEntry);
				                            
				                            if (vAccount != null)
				                            {
				%>
				<tr bgcolor=<%=vCollor%> id=<%=vId%> class="bodytekst"
					onclick="updateDeleteFlag('<%=vId%>','<%=vEntry.getId()%>','<%=vRowInd%>')"
					ondblclick="changeUrl('/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_INVOICE%>&<%=Constants.ACCOUNT_FILTER_CUSTOMER%>=<%=vEntry.getAccountFwdNr()%>&<%=Constants.INVOICE_ID%>=<%=vEntry.getId()%>');">
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
        </td>
    </tr>
</table>
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

function uploadFile()
{
  document.loadfileform.<%=Constants.SRV_ACTION%>.value="<%=Constants.PROCESS_FINTRO_XLSX%>";
}

function downloadProcFile()
{
  document.downloadfileform.<%=Constants.SRV_ACTION%>.value="<%=Constants.DOWNLOAD_FINTRO_PROCESS_TXT%>";
}

function changeUrl(newURL) 
{
  location=newURL;
}


</script>



