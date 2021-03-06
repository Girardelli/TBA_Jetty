<html>

<%@ include file="adminheader.jsp"%>

<%@ page
	import="java.util.*,be.tba.sqldata.*,be.tba.util.constants.*,be.tba.sqldata.AccountCache"%>

<%

	try
	{
	    if (vSession == null)
	        throw new AccessDeniedException("U bent niet aangemeld bij deze administratie pagina's.");
		vSession.setCallingJsp(Constants.ADD_ACCOUNT_JSP);
%>

<body>
<table cellspacing='0' cellpadding='0' border='0'
	bgcolor="FFFFFF">
	<!--Update account jsp-->
	<tr>
		<td valign="top" width="50" bgcolor="FFFFFF"></td>
		<td valign="top" bgcolor="FFFFFF"><br>
		<br>
		<span class="bodytitle"> Voeg een nieuwe klant toe.</span> <br>
		<span class="bodytekst"> <!-- action name must be a URI name as it is set in the <application>.xml servlet-mapping tag.-->
		<form name="addaccountform" method="POST" action="/tba/AdminDispatch">
		<table border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="50"></td>
				<td width="200" valign="top" class="bodysubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;rol</td>
				<td width="580" valign="top"><select
					name=<%=Constants.LOGIN_ROLE%>>
					<%
			            for (Iterator<AccountRole> n = AccountRole.iterator(); n.hasNext();)
			            {
			                AccountRole vRole = n.next();
			                if (vRole != AccountRole.ADMIN && vRole != AccountRole.EMPLOYEE)
			                {
			                    if (vRole == AccountRole.CUSTOMER)
			                    {
			                    	out.println("<option value=\"" + vRole.getShort() + "\" selected>" + vRole.getText());
			                    }
			                    else
			                    {
			                    	out.println("<option value=\"" + vRole.getShort() + "\">" + vRole.getText());
			                    }
			                }
			            }
					%>
				</select></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="200" valign="top" class="bodysubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;volledige naam</td>
				<td width="580" valign="top"><input type=text
					name=<%=Constants.ACCOUNT_FULLNAME%> size=50></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="200" valign="top" class="bodysubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;afleidnummer</td>
				<td width="580" valign="top" class="bodytekst">014/<select
					name=<%=Constants.ACCOUNT_FORWARD_NUMBER%>>
					<%
					            for (Iterator<String> n = AccountCache.getInstance().getFreeNumbers().iterator(); n.hasNext();)
					            {
					                String vNumber = n.next();
					                out.println("<option value=\"" + vNumber + "\">" + vNumber);
					            }
					%>
				</select></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="200" valign="top" class="bodysubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Super klant</td>
				<td width="580" valign="top">
<%                  
out.println("<select name=\"" + Constants.ACCOUNT_SUPER_CUSTOMER + "\">");
out.println("<option value=0 selected> heeft geen super klant");
Collection<Integer> list = AccountCache.getInstance().getSuperCustomersList();
synchronized(list) 
{
    for (Iterator<Integer> vIter = list.iterator(); vIter.hasNext();)
    {
    	Integer vValue = vIter.next();
        AccountEntityData accountData = AccountCache.getInstance().get(vValue);
        out.println("<option value=\"" + accountData.getId() + "\">" + accountData.getFullName());
    }
}
out.println("</select>");
%>
				</td>
			</tr>
		</table>
		<br>
		<br>
		<input class="tbabutton" type=submit name=action value="Bewaar"> 
		<input class="tbabutton" type=reset> 
		<input class="tbabutton" type=submit value="Terug" onclick="cancelUpdate();"> 
		<input type=hidden name=<%=Constants.SRV_ACTION%> value=<%=Constants.ACCOUNT_ADD%>>
		</form>
		</span></td>
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

function cancelUpdate()
{
  document.addaccountform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_ACCOUNT_ADMIN%>";
}
</script>

</body>

</html>

