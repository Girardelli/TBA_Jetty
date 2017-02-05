<html>

<%@ include file="adminheader.jsp" %>

<%@ page
import="javax.ejb.*,
java.util.*,
javax.rmi.PortableRemoteObject,
java.rmi.RemoteException,
javax.naming.Context,
javax.naming.InitialContext,
javax.rmi.PortableRemoteObject,
javax.ejb.*,
be.tba.ejb.account.interfaces.*,
be.tba.util.constants.EjbJndiNames,
be.tba.util.constants.Constants,
be.tba.util.constants.AccountRole,
be.tba.util.session.AccountCache"%>

<body>
<table width='100%' cellspacing='0' cellpadding='0' border='0'
	bgcolor="FFFFFF">
	<!--Update account jsp-->
	<tr>
		<td valign="top" width="60" bgcolor="FFFFFF"></td>
		<td valign="top" bgcolor="FFFFFF"><br>
<%            
try
{
    vSession.setCallingJsp(Constants.ADD_EMPLOYEE_JSP);
    
Vector vErrorList = (Vector) request.getAttribute(Constants.ERROR_VECTOR);
String vSelect = "selected";
if (vErrorList != null) System.out.println("error list size " + vErrorList.size());
if (vErrorList != null && vErrorList.size() > 0)
{
  out.println("<br><span class=\"bodyredbold\">");
  for (Iterator i = vErrorList.iterator(); i.hasNext();)
  {
    out.println("<img src=\"/TheBusinessAssistant/images/blueVink.gif\" border=\"0\">" + (String) i.next() + "<br>");
  }
  out.println("</span>");
}
%> 
		<br>
		<span class="admintitle"> Voeg een nieuwe werknemer toe.</span> <br>
		<span class="bodytekst"> <!-- action name must be a URI name as it is set in the <application>.xml servlet-mapping tag.-->
		<form method="GET" action="/TheBusinessAssistant/AdminDispatch">
		<table width="700" border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;rol</td>
				<td width="580" valign="top"><select
					name=<%=Constants.ACCOUNT_ROLE%>>
					<%
for (Iterator n = AccountRole.iterator(); n.hasNext();)
{
  AccountRole vRole = (AccountRole) n.next();
  if (vRole == AccountRole.ADMIN ||
      vRole == AccountRole.EMPLOYEE)
  {
      out.println("<option value=\"" + vRole.getShort() + "\" " + vSelect + ">" + vRole.getText());
      if (vSelect != "")
        vSelect = "";
  }
}
%>
				</select></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle">
					<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;volledige naam
				</td>
				<td width="580" valign="top">
					<input type=text name=<%=Constants.ACCOUNT_FULLNAME%> size=50>
				</td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle">
					<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;werknemersnummer
				</td>
				<td width="580" valign="top">
					<input type=text name=<%=Constants.ACCOUNT_FORWARD_NUMBER%> size=50>
				</td>
			</tr>
		</table>
		<br>
		Uw login naam moet tussen de 5 en 10 karakters bevatten.
		<br>
		<table width="700" border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle">
					<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;login naam
				</td>
				<td width="580" valign="top">
					<input type=text name=<%=Constants.ACCOUNT_USERID%> size=10>
				</td>
			</tr>
		</table>
		<br>
		Uw paswoord moet tussen de 6 en 10 karakters bevatten.
		<br>
		<table width="700" border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle">
					<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;paswoord
				</td>
				<td width="580" valign="top">
					<input type=password name=<%=Constants.ACCOUNT_PASSWORD%> size=8>
				</td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle">
					<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;herhaal uw paswoord
				</td>
				<td width="580" valign="top">
					<input type=password name=<%=Constants.ACCOUNT_PASSWORD2%> size=8>
				</td>
			</tr>
		</table>
		<br>
		<br>
		<input type=submit name=action value=" Bewaar "> 
		<input type=reset> 
		<input type=submit value=" Terug " onclick="cancelUpdate();"> 
		<input type=hidden name=<%=Constants.SRV_ACTION%> value=<%=Constants.ACCOUNT_ADD%>>
		</form>
		</span>
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

function cancelUpdate()
{
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_EMPLOYEE_ADMIN%>";
}
</script>

</body>

</html>
