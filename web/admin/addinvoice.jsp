<html>

<%@ include file="adminheader.jsp" %>


	<%@ page
		import="javax.ejb.*,
java.util.*,


javax.naming.Context,
javax.naming.InitialContext,

javax.ejb.*,
be.tba.ejb.account.interfaces.*,
be.tba.ejb.pbx.interfaces.*,
be.tba.ejb.task.interfaces.*,
be.tba.util.constants.*,
be.tba.util.session.*,
be.tba.util.data.*,
be.tba.util.invoice.*"%>
<%
vSession.setCallingJsp(Constants.ADD_INVOICE_JSP);
%>
<body>
<p><span class="admintitle"> Niet meer in gebruik<br>
</span></p>
<!-- action name must be a URI name as it is set in the <application>.xml servlet-mapping tag.

<p><span class="admintitle"> Factuur toevoegen voor een niet klant<br>
</span></p>
<table  cellspacing='0' cellpadding='0' border='0'
	bgcolor="FFFFFF">
	
	<tr>
		<td valign="top" width="30" bgcolor="FFFFFF"></td>
		<td valign="top" bgcolor="FFFFFF"><br>
		<br>
		<span class="bodytekst"> 
		<form name="taskform" method="POST"
			action="/tba/AdminDispatch">
		<table width="100%" border="0" cellspacing="1" cellpadding="1">
			<tr>
				<td width="50"></td>
				<td width="160" valign="top" class="adminsubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp; Bedrag (in euro)</td>
				<td width="540" valign="top" class="adminsubsubtitle">
					<input type=text size=20 name=<%=Constants.INVOICE_AMONTH%> value="">
				</td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="160" valign="top" class="adminsubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp; Klant</td>
				<td width="540" valign="top" class="adminsubsubtitle">
					<input type=text size=80 name=<%=Constants.INVOICE_CUSTOMER%> value="">
				</td>
			</tr>
		</table>
		<br>
		<br>
		<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.INVOICE_ADD%>"> 
		<input class="tbabutton" type=submit name=action value=" Bewaar "> 
		<input class="tbabutton" type=reset> 
		<input class="tbabutton" type=submit value=" Terug " onclick="cancelAdd();">
		</form>
		</span> <br>
		</td>
	</tr>
</table>

<script type="text/javascript">

function cancelAdd()
{
  document.taskform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_INVOICE_ADMIN%>";
}
</script>
-->
</body>

</html>

