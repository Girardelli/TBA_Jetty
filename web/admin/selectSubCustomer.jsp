<html>

<%@ include file="adminheader.jsp"%>

<%@ page
	import="java.util.*,


javax.naming.Context,
javax.naming.InitialContext,

be.tba.ejb.account.interfaces.*,
be.tba.util.data.*,
be.tba.util.invoice.*,
be.tba.util.constants.*,
be.tba.util.exceptions.*,
be.tba.util.session.*"%>

<%

try 
{
    vSession.setCallingJsp(Constants.SELECT_SUBCUSTOMER_JSP);
	
	String vFwdNr = vSession.getNewUnmappedCall().getFwdNr();
	
	if (vFwdNr == null)
	  throw new SystemErrorException("Interne fout: Account key null.");
	
	AccountEntityData vCustomer = AccountCache.getInstance().get(vFwdNr);
	String vFullName = vCustomer.getFullName();
%>
<body>
	<table  cellspacing='0' cellpadding='0' border='0'
		bgcolor="FFFFFF">

		<!--Update account jsp-->
		<tr>
			<td valign="top" width="60" bgcolor="FFFFFF"></td>
			<td valign="top" bgcolor="FFFFFF">
			    <br><br> 
				<span class="bodytitle"> Selecteer sub-klant voor de oproep voor <%=vFullName%>.</span>
				<br> 
				<span class="bodytekst"> <!-- action name must be a URI name as it is set in the <application>.xml servlet-mapping tag.-->
					<form name="updateForm" method="POST" action="/tba/AdminDispatch">
						<br>
						<table width="700" border="0" cellspacing="2" cellpadding="2">
							<tr>
								<td width="200" valign="top" class="bodysubsubtitle">sub-klanten</td>
								<td width="500" valign="top">
								<select name=<%=Constants.ACCOUNT_NEW_FWDNR%>>
<%
		out.println("<option value=\"" + vFwdNr + "\">" + vFullName);
		if (vCustomer.getHasSubCustomers())
		{
		    Collection<AccountEntityData> subList = AccountCache.getInstance().getSubCustomersList(vCustomer.getId());
		    
		    synchronized(subList) 
		    {
		        for (Iterator<AccountEntityData> vIter = subList.iterator(); vIter.hasNext();)
		        {
		            AccountEntityData vSubCustomer = vIter.next();
		            if (!(vSubCustomer.getFwdNumber().equals(vFwdNr)))
		            {
		              out.println("<option value=\"" + vSubCustomer.getFwdNumber() + "\">" + vSubCustomer.getFullName());
		            }
		        }
		    }
%>
							    </select>
							    </td>
				            </tr>
				        </table>
						<br>
				        <input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.SAVE_NEW_SUBCUSTOMER%>"> 
				        <input type=hidden name=<%=Constants.ACCOUNT_FWDNR%> value="<%=vFwdNr%>"> 
				        <input class="tbabutton" type=submit name=action value="Bewaar"> <!-- onclick="Bewaar()"> -->  
        	        </form>
		         </span>
		     </td>
	     </tr>
</table>

	<%
    }
}
catch (Exception e)
{
log.error(e.getMessage(), e);
}

%>

</script>

</body>

</html>

