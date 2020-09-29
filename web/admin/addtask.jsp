<html>

<%@ include file="adminheader.jsp" %>

	<%@ page
		import="
java.util.*,


javax.naming.Context,
javax.naming.InitialContext,


be.tba.ejb.account.interfaces.*,
be.tba.ejb.account.session.*,
be.tba.ejb.pbx.interfaces.*,
be.tba.ejb.task.interfaces.*,
be.tba.util.constants.*,
be.tba.util.session.*,
be.tba.util.data.*,
be.tba.util.invoice.*"%>

<body>
<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">

	<!--Add Task jsp-->
	<tr>
		<td valign="top" width="30" bgcolor="FFFFFF"></td>
		<td valign="top" bgcolor="FFFFFF"><br>
		<br>
		<span class="bodytekst"> <!-- action name must be a URI name as it is set in the <application>.xml servlet-mapping tag.-->
		<form name="taskform" method="POST" action="/tba/AdminDispatch" onsubmit="return validate_form(this)">
		<table border="0" cellspacing="1" cellpadding="1">
			<tr>
				<td width="50"></td>
				<td width="250" valign="top" class="bodysubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Klant</td>
				<td width="580" valign="top"><%                  
try
{
vSession.setCallingJsp(Constants.ADD_TASK_JSP);
int vCustomerFilter = vSession.getCallFilter().getCustFilter();
out.println("<select name=\"" + Constants.TASK_ACCOUNT_ID + "\">");
Collection<AccountEntityData> list = AccountCache.getInstance().getCustomerList();
LoginSqlAdapter loginSqlAdapter = new LoginSqlAdapter();
Collection<LoginEntityData> logins = loginSqlAdapter.getEmployeeList(vSession);
synchronized(list) 
{
    for (AccountEntityData account : list)
    {
        out.println("<option value=\"" + account.getId() + (vCustomerFilter == account.getId() ? "\" selected>" : "\">") + account.getFullName());
    }
}
out.println("</select>");
%></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="250" valign="top" class="bodysubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Uitgevoerd door</td>
				<td width="580" valign="top"><%                  
out.println("<select name=\"" + Constants.TASK_DONE_BY_EMPL + "\">");
out.println("<option value=\"\" selected> Selecteer een werknemer");
 for (LoginEntityData account : logins)
 {
     out.println("<option value=\"" + account.getUserId() + "\">" + account.getName());
 }
out.println("</select>");

Calendar vCalendar = Calendar.getInstance();
int vDay = vCalendar.get(Calendar.DAY_OF_MONTH);
int vMonth = vCalendar.get(Calendar.MONTH) + 1;
int vYear = vCalendar.get(Calendar.YEAR) - 2000;
String vDate = new String(vDay + "/" + vMonth  + ((vYear < 10) ? "/0" : "/") + vYear);
%></td>
			</tr>
			<tr valign="top">
				<td width="50"></td>
				<td width="250" valign="top" class="bodysubsubtitle">
					<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Datum
				</td>
				<td width="580" valign="top"><input type=text size=20
					name=<%=Constants.TASK_DATE%> value="<%=vDate%>"></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="250" valign="top" class="bodysubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Vaste prijs (in euro)</td>
				<td width="580" valign="top" class="bodysubsubtitle">
					<input type=checkbox name=<%=Constants.TASK_IS_FIXED_PRICE%> value="<%=Constants.YES%>">&nbsp;&nbsp;&nbsp;&nbsp; 
					<input type=text size=20 name=<%=Constants.TASK_FIXED_PRICE%> value="">
				</td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="250" valign="top" class="bodysubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Minuten gewerkt</td>
				<td width="580" valign="top" class="bodysubsubtitle"><input type=text size=20 name=<%=Constants.TASK_TIME_SPEND%> value=""></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="250" valign="top" class="bodysubsubtitle">
					<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Maandelijks terugkerend
				</td>
				<td width="580" valign="top" class="bodysubsubtitle">
					<input type=checkbox name=<%=Constants.TASK_IS_RECURING%> value="<%=Constants.NO%>">
				</td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="250" valign="top" class="bodysubsubtitle">Omschrijving</td>
				<td width="580" valign="top"><textarea
					name=<%=Constants.TASK_DESCRIPTION%> rows=10 cols=70></textarea></td>
			</tr>
		</table>
		<br>
		<br>
		<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.TASK_ADD%>"> 
		<input class="tbabutton" type=submit name=action value=" Bewaar "> <input class="tbabutton" type=reset> 
		<input class="tbabutton" type=submit value=" Terug " onclick="cancelAdd();">
		</form>
		</span> <br>
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
  document.taskform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_TASK_ADMIN%>";
}

function validate_required(field, alerttxt)
{
with (field)
  {
  if (value==null||value=="")
    {
    alert(alerttxt);return false;
    }
  else
    {
    return true;
    }
  }
}

function validate_form(thisform)
{
with (thisform)
  {
  if (validate_required(<%=Constants.TASK_DONE_BY_EMPL%>,"Werknemer moet geselecteerd zijn!")==false)
  {<%=Constants.TASK_DONE_BY_EMPL%>.focus();return false;}
  }
}

</script>

</body>

</html>

