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
be.tba.ejb.pbx.interfaces.*,
be.tba.ejb.task.interfaces.*,
be.tba.util.constants.EjbJndiNames,
be.tba.util.constants.Constants,
be.tba.util.exceptions.AccessDeniedException,
be.tba.servlets.session.SessionManager,
be.tba.util.session.AccountCache,
be.tba.util.invoice.InvoiceHelper,
be.tba.util.data.*,
be.tba.ejb.pbx.session.CallRecordSqlAdapter,
be.tba.ejb.task.session.TaskSqlAdapter,
java.text.*"%>

	<%

try {
vSession.setCallingJsp(Constants.ADMIN_EMPLOYEE_COST_JSP);

InitialContext vContext = new InitialContext();

%>
<body>
<table width='100%' cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
	<tr>
		<!-- white space -->
		<td valign="top" width="20" bgcolor="FFFFFF"></td>

		<!-- account list -->
		<td valign="top" width="865" bgcolor="FFFFFF"><br>
		<p><span class="admintitle"> Werknemer prestaties tijdens de maand <%=vSession.getMonthsBackString()%><br>
		<br>
		<br>
		</span></p>
		<form name="taskform" method="POST"
			action="/TheBusinessAssistant/AdminDispatch"><input type=hidden
			name=<%=Constants.TASK_TO_DELETE%> value=""> <input type=hidden
			name=<%=Constants.SRV_ACTION%> value="<%=Constants.GOTO_TASK_ADMIN%>">
		<br>
		<table>
			<tr>
				<td width="30"></td>
				<td width="80"><input class="tbabutton" type=submit name=action value=" Vorige maand "
					onclick="showPrevious()"></td>
				<%
if (!vSession.isCurrentMonth())
{
  out.println("<td width=\"80\"><input class=\"tbabutton\" type=submit name=action value=\" Volgende maand \"  onclick=\"showNext()\"></td>");
}
%>
			</tr>
		</table>
		<br>
		<br>
		<table width="100%" border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="300" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Uitvoerder</td>
				<td width="100" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;oproepen (Euro)</td>
				<td width="100" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Taken (Euro)</td>
				<td width="100" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Totaal (Euro)</td>
			</tr>
<%
	
TaskSqlAdapter vTaskSession = new TaskSqlAdapter();

CallRecordSqlAdapter  vQuerySession = new CallRecordSqlAdapter();

Collection emplList = AccountCache.getInstance().getEmployeeList();
synchronized(emplList) 
{
    for (Iterator vIter = emplList.iterator(); vIter.hasNext();)
    {
        AccountEntityData vEmployee = (AccountEntityData) vIter.next();
        double vTaskCost = 0;
        double vCallCost = 0;
        
        Collection vTasks = vTaskSession.getDoneByTasks(vSession, vEmployee.getUserId(), vSession.getMonthsBack(), vSession.getYear());
        for (Iterator i = vTasks.iterator(); i.hasNext();)
        {
          TaskEntityData vEntry = ((TaskEntityData) i.next());
          if (vEntry.getIsFixedPrice())
          {
              vTaskCost += vEntry.getFixedPrice();
          }
          else
          {
              vTaskCost += ((double) vEntry.getTimeSpend() / 60.00) * ((double) Constants.CENT_PER_HOUR_WORK / 100.00);
          }
        }
        Collection vRecords = vQuerySession.getDoneByCalls(vSession, vEmployee.getUserId(), vSession.getMonthsBack(), vSession.getYear());

        for (Iterator i = vRecords.iterator(); i.hasNext();)
        {
            CallRecordEntityData vEntry = (CallRecordEntityData) i.next();
            
        }
        %>
		<tr bgcolor="FFCC66" class="bodytekst">
			<td width="300" valign="top"><%=vEmployee.getFullName()%></td>
			<td width="100" valign="top"><%=vRecords.size()%></td>
			<td width="100" valign="top"><%=vTaskCost%></td>
			<td width="100" valign="top"><%=vTaskCost + vRecords.size()%></td>
		</tr>
		<%
    }
}

}
catch (Exception e)
{
e.printStackTrace();
}
%>
		</table>
		</form>
		</td>
	</tr>


	<script type="text/javascript">

function selectAll()
{
}

function deselectAll()
{
}

function reverseSelection()
{
}

function changeUrl(newURL) 
{
  location=newURL;
}

function showPrevious()
{
  document.taskform.<%=Constants.SRV_ACTION%>.value="<%=Constants.EMPLCOST_SHOW_PREV%>";
}

function showNext()
{
  document.taskform.<%=Constants.SRV_ACTION%>.value="<%=Constants.EMPLCOST_SHOW_NEXT%>";
}

</script>
</table>

</body>

</html>

