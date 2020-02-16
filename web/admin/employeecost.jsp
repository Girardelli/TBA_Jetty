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
be.tba.ejb.invoice.interfaces.*,
be.tba.ejb.task.interfaces.*,
be.tba.util.constants.EjbJndiNames,
be.tba.util.constants.Constants,
be.tba.util.exceptions.AccessDeniedException,
be.tba.servlets.session.SessionManager,
be.tba.util.session.AccountCache,
be.tba.util.invoice.InvoiceHelper,
be.tba.util.data.*,
be.tba.ejb.pbx.session.CallRecordSqlAdapter,
be.tba.ejb.invoice.session.InvoiceSqlAdapter,
be.tba.ejb.task.session.TaskSqlAdapter,
java.text.*"%>

	<%

try 
{
	class GeneratedCost
	{
	    protected int calls;
	    protected double taskCost;
       protected long duration;
	}
	Map<String, GeneratedCost> performanceMap = new HashMap<String, GeneratedCost>();
    vSession.setCallingJsp(Constants.ADMIN_EMPLOYEE_COST_JSP);
%>
<body>
<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
	<tr>
		<!-- white space -->
		<td valign="top" width="20" bgcolor="FFFFFF"></td>

		<!-- account list -->
		<td valign="top" width="865" bgcolor="FFFFFF"><br>
		<p><span class="admintitle"> Werknemer prestaties tijdens de maand <%=vSession.getMonthsBackString()%><br>
		<br>
		</span></p>
		<form name="taskform" method="POST"
			action="/tba/AdminDispatch"><input type=hidden
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
	DecimalFormat mCostFormatter = new DecimalFormat("#0.00");		    
	TaskSqlAdapter vTaskSession = new TaskSqlAdapter();
	InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
	CallRecordSqlAdapter  vQuerySession = new CallRecordSqlAdapter();

	Collection<InvoiceEntityData> vInvoices = vInvoiceSession.getInvoiceList(vSession, vSession.getMonthsBack(), vSession.getYear());
	double totalCallCost = 0;
	double totalTasks = 0;
	for (Iterator<InvoiceEntityData> vIter = vInvoices.iterator(); vIter.hasNext();)
	{
	    InvoiceEntityData entry = vIter.next();
	    totalCallCost += entry.getTotalCost();
	}
	Collection<TaskEntityData> tasks = vTaskSession.getTasksForMonth(vSession, vSession.getMonthsBack(), vSession.getYear());
	for (Iterator<TaskEntityData> i = tasks.iterator(); i.hasNext();)
	{
	  TaskEntityData vEntry = i.next();
	  double taskCost = 0;
	  if (vEntry.getIsFixedPrice())
	  {
	      taskCost = vEntry.getFixedPrice();
	  }
	  else
	  {
	      taskCost = (vEntry.getTimeSpend() / 60) * (AccountCache.getInstance().get(vEntry.getAccountId()).getTaskHourRate() / 100);
	  }
	  totalTasks += taskCost;
	  if (performanceMap.containsKey(vEntry.getDoneBy()))
	  {
	      performanceMap.get(vEntry.getDoneBy()).taskCost += taskCost;
	  }
	  else
	  {
	      GeneratedCost genCost = new GeneratedCost();
	      genCost.calls = 0;
	      genCost.duration = 0;
         genCost.taskCost = taskCost;
	      performanceMap.put(vEntry.getDoneBy(), genCost); 
	      System.out.println("add entry from tasks for "+ vEntry.getDoneBy());
	  }
	}
	totalCallCost -= totalTasks;
	Collection<CallRecordEntityData> records = vQuerySession.getIncomingCallsForMonth(vSession, vSession.getMonthsBack(), vSession.getYear());
	int totalNrCalls = records.size() == 0 ? 1 : records.size();
    for (Iterator<CallRecordEntityData> i = records.iterator(); i.hasNext();)
    {
        CallRecordEntityData vEntry = i.next();
        if (performanceMap.containsKey(vEntry.getDoneBy()))
        {
           GeneratedCost genCost = performanceMap.get(vEntry.getDoneBy());
           genCost.calls++;
           genCost.duration += (vEntry.getTsEnd() - vEntry.getTsAnswer());
        }
        else
        {
            GeneratedCost genCost = new GeneratedCost();
            genCost.calls = 1;
            genCost.duration = (vEntry.getTsEnd() - vEntry.getTsAnswer());
            genCost.taskCost = 0;
            performanceMap.put(vEntry.getDoneBy(), genCost); 
            System.out.println("add entry from records for "+ vEntry.getDoneBy());
        }
         
    }
    
	
	System.out.println("totalCallCost=" + totalCallCost + " , totalNrCalls=" + totalNrCalls + " , totalTasks=" + totalTasks);
    
%>
			</tr>
		</table>
		<br>
        <p><span class="admintitle"> Gemiddelde opbrengst per oproep deze maand: <%=mCostFormatter.format(totalCallCost/totalNrCalls)%><br>
        <p><span class="admintitle"> Totaal aantal oproepen: <%=records.size()%><br>
        </span></p>		
		<br>
		<table width="100%" border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="300" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Uitvoerder</td>
                <td width="100" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;oproepen (min)</td>
				<td width="100" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;oproepen (Euro)</td>
				<td width="100" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Taken (Euro)</td>
				<td width="100" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Totaal (Euro)</td>
			</tr>
<%

    for (Iterator<String> vIter = performanceMap.keySet().iterator(); vIter.hasNext();)
    {
        String vEmployee = vIter.next();
        double vTaskCost = 0;
        double vCallCost = 0;
        
        GeneratedCost genCost = performanceMap.get(vEmployee);
        
        double callCostContribution = genCost.calls * totalCallCost/totalNrCalls;
        %>
		<tr bgcolor="FFCC66" class="bodytekst">
			<td width="300" valign="top"><%=vEmployee%></td>
            <td width="100" valign="top"><%=genCost.duration/1000/60%></td>
			<td width="100" valign="top"><%=mCostFormatter.format(callCostContribution)%></td>
			<td width="100" valign="top"><%=mCostFormatter.format(genCost.taskCost)%></td>
			<td width="100" valign="top"><%=mCostFormatter.format(genCost.taskCost + callCostContribution)%></td>
		</tr>
		<%
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

