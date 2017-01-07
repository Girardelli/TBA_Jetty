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
be.tba.ejb.pbx.interfaces.*,
be.tba.ejb.account.interfaces.*,
be.tba.ejb.account.session.*,
be.tba.ejb.task.interfaces.*,
be.tba.util.session.AccountCache,
be.tba.util.data.*,
be.tba.util.constants.*,
be.tba.util.invoice.*"%>
	<%!
private TaskEntityData mTaskData;
private AccountEntityData mCustomerData;
private String vRecordId = null;
%>
	<%
try
{
vSession.setCallingJsp(Constants.UPDATE_TASK_JSP);
mTaskData = vSession.getCurrentTask();
if (mTaskData == null)
{
  out.println("no call Task set in session context when entering updaterecord.jsp");
  return;
}

mCustomerData = AccountCache.getInstance().get(mTaskData.getFwdNr());
%>

<body>
<table width='100%' cellspacing='0' cellpadding='0' border='0'
	bgcolor="FFFFFF">

	<!--Update task jsp-->

	<tr>
		<td valign="top" width="30" bgcolor="FFFFFF"></td>
		<td valign="top" bgcolor="FFFFFF"><br>
		<br>
		<span class="bodytekst"> <!-- action name must be a URI name as it is set in the <application>.xml servlet-mapping tag.-->
		<form name="taskform" method="POST"
			action="/TheBusinessAssistant/AdminDispatch">
		<table width="100%" border="0" cellspacing="1" cellpadding="1">
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Andere klant</td>
				<td width="580" valign="top">
<%                  
	out.println("<select name=\"" + Constants.TASK_FORWARD_NUMBER + "\">");
	Collection list = AccountCache.getInstance().getCustomerList();
	synchronized(list) 
	{
	   for (Iterator vIter = list.iterator(); vIter.hasNext();)
	   {
	       AccountEntityData vValue = (AccountEntityData) vIter.next();
	       out.println("<option value=\"" + vValue.getFwdNumber() + (vValue.getFwdNumber().equals(mTaskData.getFwdNr()) ? "\" selected>" : "\">") + vValue.getFullName());
	   }
	}
	out.println("</select>");

	Calendar vCalendar = Calendar.getInstance();
	int vDay = vCalendar.get(Calendar.DAY_OF_MONTH);
	int vMonth = vCalendar.get(Calendar.MONTH) + 1;
	int vYear = vCalendar.get(Calendar.YEAR) - 2000;
	String vDate = new String(vDay + "/" + vMonth  + ((vYear < 10) ? "/0" : "/") + vYear);
%>
				</td>
			</tr>

			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Uitgevoerd door</td>
				<td width="580" valign="top">
<%                  
	out.println("<select name=\"" + Constants.DONE_BY_EMPL + "\">");
	Collection emplList = AccountCache.getInstance().getEmployeeList();
	synchronized(list) 
	{
	   for (Iterator vIter = emplList.iterator(); vIter.hasNext();)
	   {
	       AccountEntityData vValue = (AccountEntityData) vIter.next();
	       out.println("<option value=\"" + vValue.getFwdNumber() + (vValue.getFwdNumber().equals(mTaskData.getDoneBy()) ? "\" selected>" : "\">") + vValue.getFullName());
	   }
	}
	out.println("</select>");
%>
				</td>
			</tr>
			<tr valign="top">
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle">
					<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Datum
				</td>
				<td width="580" valign="top">
					<input type=text size=20 name=<%=Constants.TASK_DATE%> value="<%=mTaskData.getDate()%>">
				</td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle">
					<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Vaste prijs (in euro)
				</td>
				<td width="580" valign="top" class="adminsubsubtitle">
					<input type=checkbox name=<%=Constants.TASK_IS_FIXED_PRICE%> value="<%=mTaskData.getIsFixedPrice()?Constants.YES:Constants.NO%>" <%=mTaskData.getIsFixedPrice()?" checked":""%>>&nbsp;&nbsp;&nbsp;&nbsp;
					<input type=text size=20 name=<%=Constants.TASK_FIXED_PRICE%> value="<%=mTaskData.getFixedPrice()%>">
				</td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Minuten gewerkt</td>
				<td width="580" valign="top" class="adminsubsubtitle"><input
					type=text size=20 name=<%=Constants.TASK_TIME_SPEND%>
					value="<%=mTaskData.getTimeSpend()%>"></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle">
					<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Maandelijks terugkerend
				</td>
				<td width="580" valign="top" class="adminsubsubtitle">
					<input type=checkbox name=<%=Constants.TASK_IS_RECURING%> value="<%=mTaskData.getIsRecuring()?Constants.YES:Constants.NO%>" <%=mTaskData.getIsRecuring()?" checked":""%>>
				</td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle">Omschrijving</td>
				<td width="580" valign="top"><textarea
					name=<%=Constants.TASK_DESCRIPTION%> rows=10 cols=70><%=mTaskData.getDescription()%></textarea></td>
			</tr>
		</table>
		<br>
		<br>
		<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.SAVE_TASK%>"> 
		<input type=submit name=action value=" Bewaar "> 
		<input type=reset> 
		<input type=submit value=" Terug " onclick="cancelUpdate();">
		</form>
		</span> <br>
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

</body>

</html>

<script type="text/javascript">

function cancelUpdate()
{
  document.taskform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_TASK_ADMIN%>";
}
</script>
