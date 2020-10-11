<html>

<%@ include file="adminheader.jsp" %>

	<%@ page
		import="java.util.*,
be.tba.sqldata.*,
be.tba.sqladapters.*,
be.tba.sqldata.AccountCache,
be.tba.util.data.*,
be.tba.util.constants.*,
be.tba.util.invoice.*"%>
<%
TaskEntityData mTaskData;
AccountEntityData mCustomerData;
String vRecordId = null;
try
{
vSession.setCallingJsp(Constants.UPDATE_TASK_JSP);
mTaskData = vSession.getCurrentTask();
if (mTaskData == null)
{
  out.println("no call Task set in session context when entering updaterecord.jsp");
  return;
}
mCustomerData = AccountCache.getInstance().get(mTaskData);
%>

<body>
<form name="taskform" method="POST" action="/tba/AdminDispatch">
<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">

	<!--Update task jsp-->

	<tr>
		<td valign="top" width="30" bgcolor="FFFFFF"></td>
		<td valign="top" bgcolor="FFFFFF"><br>
		<br>
		<table border="0" cellspacing="1" cellpadding="1">
		
<% if ((!mTaskData.getIsRecuring() && mTaskData.getInvoiceId() > 0) ||
		//or a recuring task that has been stopped
		(mTaskData.getIsRecuring() && mTaskData.getStopTime() < Long.MAX_VALUE))
{
%>		
            <tr>
                <td width="50"></td>
                <td width="250" valign="top" class="bodysubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;klant</td>
                <td width="580" valign="top"><%=mCustomerData.getFullName()%></td>
            </tr>
            <tr>
                <td width="50"></td>
                <td width="250" valign="top" class="bodysubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Uitgevoerd door</td>
                <td width="580" valign="top"><%=mTaskData.getDoneBy() %></td>
            </tr>
            <tr>
                <td width="50"></td>
                <td width="250" valign="top" class="bodysubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Datum</td>
                <td width="580" valign="top"><%=mTaskData.getDate() %></td>
            </tr>
            <tr>
                <td width="50"></td>
                <td width="250" valign="top" class="bodysubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Vaste prijs (in euro)</td>
                <td width="580" valign="top"><%=mTaskData.getIsFixedPrice()?mTaskData.getFixedPrice():"-"%></td>
            </tr>
            <tr>
                <td width="50"></td>
                <td width="250" valign="top" class="bodysubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Minuten gewerkt</td>
                <td width="580" valign="top"><%=mTaskData.getIsFixedPrice()?"-":mTaskData.getTimeSpend()%></td>
            </tr>
<%
if (mTaskData.getIsRecuring())
{
%>            
            <tr>
                <td width="50"></td>
                <td width="250" valign="top" class="bodysubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Maandelijks terugkerend</td>
                <td width="580" valign="top">from: <%=mTaskData.getStartTime()%> till <%=mTaskData.getStopTime()%></td>
            </tr>
<%
}
%> 
            <tr>
                <td width="50"></td>
                <td width="250" valign="top" class="bodysubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Omschrijving</td>
                <td width="580" valign="top"><%=mTaskData.getDescription()%></td>
            </tr>
		
<%
}
else
{
%>		
			<tr>
				<td width="50"></td>
				<td width="250" valign="top" class="bodysubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Andere klant</td>
				<td width="580" valign="top">
<%    
	out.println("<select name=\"" + Constants.TASK_ACCOUNT_ID + "\">");
	Collection<AccountEntityData> list = AccountCache.getInstance().getCustomerList();
	synchronized(list) 
	{
	   for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();)
	   {
	       AccountEntityData vValue = (AccountEntityData) vIter.next();
	       out.println("<option value=\"" + vValue.getId() + (vValue.getId() == mTaskData.getAccountId() ? "\" selected>" : "\">") + vValue.getFullName());
	   }
	}
	out.println("</select>");

	Calendar vCalendar = Calendar.getInstance();
	int vDay = vCalendar.get(Calendar.DAY_OF_MONTH);
	int vMonth = vCalendar.get(Calendar.MONTH) + 1;
	int vYear = vCalendar.get(Calendar.YEAR) - 2000;
	String vDate = new String(vDay + "/" + vMonth  + ((vYear < 10) ? "/0" : "/") + vYear);
	LoginSqlAdapter loginSqlAdapter = new LoginSqlAdapter();
	Collection<LoginEntityData> logins = loginSqlAdapter.getEmployeeList(vSession);
%>
				</td>
			</tr>

			<tr>
				<td width="50"></td>
				<td width="250" valign="top" class="bodysubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Uitgevoerd door</td>
				<td width="580" valign="top">
<%    

   out.println("<select name=\"" + Constants.TASK_DONE_BY_EMPL + "\">");
   
   out.println("<option value=\"\"> Selecteer een werknemer");
    for (LoginEntityData account : logins)
    {
        out.println("<option value=\"" + account.getUserId() + ((account.getUserId().equals(mTaskData.getDoneBy()) ? "\" selected>" : "\">") + account.getName()));
    }
   out.println("</select>");
%>
				</td>
			</tr>
			<tr valign="top">
				<td width="50"></td>
				<td width="250" valign="top" class="bodysubsubtitle">
					<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Datum
				</td>
				<td width="580" valign="top">
					<input type=text size=20 name=<%=Constants.TASK_DATE%> value="<%=mTaskData.getDate()%>">
				</td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="250" valign="top" class="bodysubsubtitle">
					<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Vaste prijs (in euro)
				</td>
				<td width="580" valign="top" class="bodysubsubtitle">
					<input type=checkbox name=<%=Constants.TASK_IS_FIXED_PRICE%> value="<%=mTaskData.getIsFixedPrice()?Constants.YES:Constants.NO%>" <%=mTaskData.getIsFixedPrice()?" checked":""%>>&nbsp;&nbsp;&nbsp;&nbsp;
					<input type=text size=20 name=<%=Constants.TASK_FIXED_PRICE%> value="<%=mTaskData.getFixedPrice()%>">
				</td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="250" valign="top" class="bodysubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Minuten gewerkt</td>
				<td width="580" valign="top" class="bodysubsubtitle">
				    <input type=text size=20 name=<%=Constants.TASK_TIME_SPEND%> value="<%=mTaskData.getTimeSpend()%>">
				</td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="250" valign="top" class="bodysubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Maandelijks terugkerend</td>
				<td width="580" valign="top" class="bodysubsubtitle">
					<input type=checkbox name=<%=Constants.TASK_IS_RECURING%> value="<%=mTaskData.getIsRecuring()?Constants.YES:Constants.NO%>" <%=mTaskData.getIsRecuring()?" checked":""%>>
					<%=mTaskData.getIsRecuring()?"&nbsp; (verwijder selectie om deze wederkerende taak te stoppen vanaf nu.)":""%>
				</td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="250" valign="top" class="bodysubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Omschrijving</td>
				<td width="580" valign="top">
				    <textarea name=<%=Constants.TASK_DESCRIPTION%> rows=10 cols=70><%=mTaskData.getDescription()%></textarea>
				</td>
			</tr>
<%
}
%>
		</table>
		<br>
		<br>
		<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.SAVE_TASK%>"> 
<% if ((!mTaskData.getIsRecuring() && mTaskData.getInvoiceId() == 0) ||
        //or a recuring task that has not been stopped
        (mTaskData.getIsRecuring() && mTaskData.getStopTime() == Long.MAX_VALUE))
{
%>      
		<input class="tbabutton" type=submit name=action value=" Bewaar "> 
		<input class="tbabutton" type=reset> 
<%
}
else
{
%>
        <span class="bodysubsubtitle">Deze taak is opgenomen in een bevrozen factuur en kan daardoor niet meer worden aangepast.</span><br>
<%
}
%>
		<input class="tbabutton" type=submit value=" Terug " onclick="cancelUpdate();">
		</td>
	</tr>

<%
}
catch (Exception e)
{
    e.printStackTrace();
}
%>
</table>
</form>

</body>

</html>

<script type="text/javascript">

function cancelUpdate()
{
  document.taskform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_TASK_ADMIN%>";
}
</script>
