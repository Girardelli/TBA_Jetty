<html>

<%@ include file="adminheader.jsp" %>

<%@ page
import="java.util.*,
be.tba.sqldata.*,
be.tba.util.constants.*,
be.tba.sqldata.AccountCache"%>

<body>
<table  cellspacing='0' cellpadding='0' border='0'
	bgcolor="FFFFFF">
	<!--Update account jsp-->
	<tr>
		<td valign="top" width="60" bgcolor="FFFFFF"></td>
		<td valign="top" bgcolor="FFFFFF"><br>
<%            
try
{
    vSession.setCallingJsp(Constants.ADD_EMPLOYEE_JSP);
    
Collection<String> vErrorList = vSession.getErrorList();
String vSelect = "selected";
if (vErrorList != null && vErrorList.size() > 0)
{
  out.println("<br><span class=\"bodyredbold\">");
  for (Iterator i = vErrorList.iterator(); i.hasNext();)
  {
    out.println("<img src=\"/tba/images/blueVink.gif\" border=\"0\">" + (String) i.next() + "<br>");
  }
  out.println("</span>");
}
%> 
		<br>
		<span class="bodytitle"> Voeg een nieuwe werknemer toe.</span> <br>
		<span class="bodytekst"> <!-- action name must be a URI name as it is set in the <application>.xml servlet-mapping tag.-->
		<form method="POST" action="/tba/AdminDispatch">
		<table border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="50"></td>
				<td width="200" valign="top" class="bodysubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;rol</td>
				<td width="580" valign="top">
                <select name=<%=Constants.LOGIN_ROLE%>>
					<%
for (Iterator n = AccountRole.iterator(); n.hasNext();)
{
  AccountRole vRole = (AccountRole) n.next();
  if (vRole == AccountRole.ADMIN ||
      vRole == AccountRole.EMPLOYEE)
  {
     out.println("<option value=\"" + vRole.getShort() + "\">" + vRole.getText());
/*      out.println("<option value=\"" + vRole.getShort() + "\" " + vSelect + ">" + vRole.getText());
      if (vSelect != "")
        vSelect = "";*/
  }
}
%>
				</select></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="200" valign="top" class="bodysubsubtitle">
					<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;volledige naam
				</td>
				<td width="580" valign="top">
					<input type=text name=<%=Constants.ACCOUNT_FULLNAME%> size=50>
				</td>
			</tr>
		</table>
		<br>
		Uw login naam moet tussen de 5 en 10 karakters bevatten.
		<br>
		<table border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="50"></td>
				<td width="200" valign="top" class="bodysubsubtitle">
					<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;login naam
				</td>
				<td width="580" valign="top">
					<input type=text name=<%=Constants.LOGIN_USERID%> size=30>
				</td>
			</tr>
		</table>
		<br>
		Uw paswoord moet tussen de 6 en 10 karakters bevatten.
		<br>
		<table border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="50"></td>
				<td width="200" valign="top" class="bodysubsubtitle">
					<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;paswoord
				</td>
				<td width="580" valign="top">
					<input type=password name=<%=Constants.LOGIN_PASSWORD%> size=15>
				</td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="200" valign="top" class="bodysubsubtitle">
					<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;herhaal uw paswoord
				</td>
				<td width="580" valign="top">
					<input type=password name=<%=Constants.LOGIN_PASSWORD2%> size=15>
				</td>
			</tr>
		</table>
		<br>
		<br>
		<input class="tbabutton" type=submit name=action value=" Bewaar "> 
		<input class="tbabutton" type=reset> 
		<input class="tbabutton" type=submit value=" Terug " onclick="cancelUpdate();"> 
		<input type=hidden name=<%=Constants.SRV_ACTION%> value=<%=Constants.EMPLOYEE_ADD%>>
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

