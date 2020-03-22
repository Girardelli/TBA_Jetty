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
        protected double hoursActiveOnPhone;
        protected long activeStart; 
        protected long activeCurrent; 
        protected int daysWorked;
        protected long dayStart; 
        
        protected GeneratedCost()
        {
           calls = 0;
           taskCost = 0.0;
           duration = 0;
           hoursActiveOnPhone = 0.0;
           activeStart = 0;
           activeCurrent = 0;
           daysWorked = 1;
           dayStart = 0;
        }
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
		<p><span class="bodytitle"> Werknemer prestaties tijdens de maand <%=vSession.getMonthsBackString()%><br>
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
	double totalInvoiced = 0;
    double totalCallCost = 0;
	double totalTaskCost = 0;
	for (Iterator<InvoiceEntityData> vIter = vInvoices.iterator(); vIter.hasNext();)
	{
	    InvoiceEntityData entry = vIter.next();
	    totalInvoiced += entry.getTotalCost();
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
	  totalTaskCost += taskCost;
	  if (performanceMap.containsKey(vEntry.getDoneBy()))
	  {
	      performanceMap.get(vEntry.getDoneBy()).taskCost += taskCost;
	  }
	  else
	  {
	      GeneratedCost genCost = new GeneratedCost();
          genCost.taskCost = taskCost;
	      performanceMap.put(vEntry.getDoneBy(), genCost); 
	      System.out.println("add entry from tasks for "+ vEntry.getDoneBy());
	  }
	}
	totalCallCost = totalInvoiced - totalTaskCost;
	Collection<CallRecordEntityData> records = vQuerySession.getMailedCallsForMonth(vSession, vSession.getMonthsBack(), vSession.getYear());
	int totalNrCalls = records.size() == 0 ? 1 : records.size();
    for (CallRecordEntityData record : records)
    {
        if (performanceMap.containsKey(record.getDoneBy()))
        {
           GeneratedCost genCost = performanceMap.get(record.getDoneBy());
           genCost.calls++;
           genCost.duration += (record.getTsEnd() - record.getTsAnswer());
           
           if (genCost.activeCurrent < record.getTimeStamp())
           {
              if ((record.getTimeStamp() - genCost.activeCurrent) > 15*60*1000) 
              {
                //meer dan 15 minutes geen activiteit. Tel deze periode niet mee als actief
                 genCost.hoursActiveOnPhone += (genCost.activeCurrent - genCost.activeStart)/(60*60*1000.0); //miliseconds to hours
                 genCost.activeStart = record.getTimeStamp() - 120000;
              }
              if ((record.getTimeStamp() - genCost.activeCurrent) > 11*60*60*1000) 
              {
                //meer dan 10 uur geen activiteit. Dus er is een dag voorbij
                 ++genCost.daysWorked;
                 genCost.dayStart = record.getTimeStamp();
              }
              genCost.activeCurrent = record.getTimeStamp();
           }
        }
        else
        {
            GeneratedCost genCost = new GeneratedCost();
            genCost.duration = (record.getTsEnd() - record.getTsAnswer());
            genCost.calls = 1;
            genCost.activeStart = record.getTimeStamp() - 120000; //assume 2 minutes acive before first call
            genCost.activeCurrent = record.getTimeStamp();
            genCost.dayStart = record.getTimeStamp();
            performanceMap.put(record.getDoneBy(), genCost); 
            System.out.println("add entry from records for "+ record.getDoneBy());
        }
         
    }
	
	System.out.println("totalCallCost=" + totalCallCost + " , totalNrCalls=" + totalNrCalls + " , totalTaskCost=" + totalTaskCost);
    
%>
			</tr>
		</table>
		<br>
        <p><span class="bodysubsubtitle"> Totaal gefactureerd: <%=mCostFormatter.format(totalInvoiced)%>Euro<br>
        <p><span class="bodysubsubtitle"> Totaal taken: <%=mCostFormatter.format(totalTaskCost)%>Euro<br>
        <p><span class="bodysubsubtitle"> Totaal aantal oproepen: <%=totalNrCalls%><br>
        <p><span class="bodysubsubtitle"> Gemiddelde opbrengst per oproep deze maand: <%=mCostFormatter.format(totalCallCost/totalNrCalls)%>Euro<br>
        </span></p>		
		<br>
		<table width="100%" border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="70" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;Uitvoerder</td>
                <td width="70" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;#oproepen</td>
                <td width="70" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;oproepen (hours)</td>
				<td width="70" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;oproepen (Euro)</td>
                <td width="70" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;actief telefoon (hours)</td>
                <td width="70" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;oproepen per uur</td>
                <td width="70" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;Taken (Euro)</td>
                <td width="70" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;Totaal (Euro)</td>
                <td width="70" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;Dagen gewerkt</td>
                <td width="70" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;Omzet per dag (Euro)</td>
			</tr>
<%

    for (Iterator<String> vIter = performanceMap.keySet().iterator(); vIter.hasNext();)
    {
        String vEmployee = vIter.next();
        GeneratedCost genCost = performanceMap.get(vEmployee);
        double callCostContribution = genCost.calls * totalCallCost/totalNrCalls;
        if (genCost.hoursActiveOnPhone == 0) genCost.hoursActiveOnPhone = 1;
        %>
		<tr bgcolor="FFCC66" class="bodytekst">
			<td width="70" align="right" valign="top"><%=vEmployee%></td>
            <td width="70" align="right" valign="top"><%=genCost.calls%></td>
            <td width="70" align="right" valign="top"><%=genCost.duration/(1000*60*60)%></td>
			<td width="70" align="right" valign="top"><%=mCostFormatter.format(callCostContribution)%></td>
            <td width="70" align="right" valign="top"><%=mCostFormatter.format(genCost.hoursActiveOnPhone)%></td>
            <td width="70" align="right" valign="top"><%=mCostFormatter.format(genCost.calls/genCost.hoursActiveOnPhone)%></td>
            <td width="70" align="right" valign="top"><%=mCostFormatter.format(genCost.taskCost)%></td>
            <td width="70" align="right" valign="top"><%=mCostFormatter.format(genCost.taskCost + callCostContribution)%></td>
            <td width="70" align="right" valign="top"><%=genCost.daysWorked%></td>
            <td width="70" align="right" valign="top"><%=mCostFormatter.format((genCost.taskCost + callCostContribution)/genCost.daysWorked)%></td>
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

