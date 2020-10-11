<html>

<%@ include file="adminheader.jsp" %>

	<%@ page
		import="
java.util.*,
be.tba.sqldata.*,
be.tba.util.constants.Constants,
be.tba.util.exceptions.AccessDeniedException,
be.tba.session.SessionManager,
be.tba.sqldata.AccountCache,
be.tba.util.invoice.InvoiceHelper,
be.tba.util.data.*,
be.tba.sqladapters.*,
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
        protected Collection<Integer> daysInMonth;
        
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
           daysInMonth = new Vector<Integer>();
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
	UrlCheckerSqlAdapter vUrlCheckerSession = new UrlCheckerSqlAdapter();
    Calendar calendar = Calendar.getInstance();

    UrlCheckerEntityData urlCheckerData = vUrlCheckerSession.getForMonth(vSession.getYear(), vSession.getMonthsBack());
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
	for (TaskEntityData task : tasks)
	{
	  double taskCost = 0;
      if (task.getIsFixedPrice() || task.getFixedPrice() > 0)
	  {
	      taskCost = task.getFixedPrice();
	  }
	  else
	  {
	     AccountEntityData account = AccountCache.getInstance().get(task);
         if (account != null)
         {
            taskCost = (task.getTimeSpend() / 60) * (account.getTaskHourRate() / 100);
         }
	  }
	  totalTaskCost += taskCost;
	  GeneratedCost genCost = null;
	  if (performanceMap.containsKey(task.getDoneBy()))
	  {
	      genCost = performanceMap.get(task.getDoneBy());
	      genCost.taskCost += taskCost;
	  }
	  else
	  {
	      genCost = new GeneratedCost();
          genCost.taskCost = taskCost;
	      performanceMap.put(task.getDoneBy(), genCost); 
	  }
     if (!task.getIsRecuring())
     {
        calendar.setTimeInMillis(task.getTimeStamp());
        if (!genCost.daysInMonth.contains(calendar.get(Calendar.DAY_OF_MONTH)))
        {
           genCost.daysInMonth.add(Integer.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        }
     }
	}
	totalCallCost = totalInvoiced - totalTaskCost;
	Collection<CallRecordEntityData> records = vQuerySession.getMailedCallsForMonth(vSession, vSession.getMonthsBack(), vSession.getYear());
	int totalNrCalls = records.size() == 0 ? 1 : records.size();
    for (CallRecordEntityData record : records)
    {
       if (InvoiceHelper.duration2Seconds(record.getCost()) == 0)
       {
          continue;
       }
       GeneratedCost genCost = null;
        if (performanceMap.containsKey(record.getDoneBy()))
        {
           genCost = performanceMap.get(record.getDoneBy());
           genCost.calls++;
           
           genCost.duration +=  InvoiceHelper.duration2Seconds(record.getCost());
           //genCost.duration += (record.getTsEnd() - record.getTsAnswer());
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
                //meer dan 11 uur geen activiteit. Dus er is een dag voorbij
                 ++genCost.daysWorked;
                 genCost.dayStart = record.getTimeStamp();
                 genCost.activeCurrent = genCost.dayStart;
              }
              genCost.activeCurrent = record.getTimeStamp();
           }
        }
        else
        {
           genCost = new GeneratedCost();
            genCost.duration = InvoiceHelper.duration2Seconds(record.getCost());
            //genCost.duration = (record.getTsEnd() - record.getTsAnswer());
            genCost.calls = 1;
            genCost.activeStart = record.getTimeStamp() - 120000; //assume 2 minutes acive before first call
            genCost.activeCurrent = record.getTimeStamp();
            genCost.dayStart = record.getTimeStamp();
            performanceMap.put(record.getDoneBy(), genCost); 
        }
        calendar.setTimeInMillis(record.getTimeStamp());
        if (!genCost.daysInMonth.contains(calendar.get(Calendar.DAY_OF_MONTH)))
        {
           genCost.daysInMonth.add(Integer.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        }
    }
%>
			</tr>
		</table>
		<br>
<%
if (urlCheckerData != null)
{
%>
        <p><span class="bodysubsubtitle"> #Telenet hick-ups: <%=urlCheckerData.hickUps%></span></p>
        <p><span class="bodysubsubtitle"> Totaal Telenet down: <%=urlCheckerData.secondsOut%> seconden</span></p>
<%   
}
%>      
        <br>
        <p><span class="bodysubsubtitle"> Totaal gefactureerd: <%=mCostFormatter.format(totalInvoiced)%>Euro</span></p>
        <p><span class="bodysubsubtitle"> Totaal taken: <%=mCostFormatter.format(totalTaskCost)%>Euro</span></p>
        <p><span class="bodysubsubtitle"> Totaal aantal oproepen: <%=totalNrCalls%></span></p>
        <p><span class="bodysubsubtitle"> Gemiddelde opbrengst per oproep deze maand: <%=mCostFormatter.format(totalCallCost/totalNrCalls)%>Euro</span></p>
		<br>
		<table border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="90" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;Uitvoerder</td>
                <td width="90" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;#oproepen</td>
                <td width="90" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;oproepen (min)</td>
				<td width="90" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;oproepen (Euro)</td>
                <td width="90" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;actief telefoon (hours)</td>
                <td width="90" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;oproepen per uur</td>
                <td width="90" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;Taken (Euro)</td>
                <td width="90" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;Omzet per maand (Euro)</td>
                <td width="90" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;Dagen gewerkt</td>
                <td width="90" valign="top" align="right" class="topMenu" bgcolor="#F89920">&nbsp;Omzet per dag (Euro)</td>
			</tr>
<%

    for (Iterator<String> vIter = performanceMap.keySet().iterator(); vIter.hasNext();)
    {
        String vEmployee = vIter.next();
        GeneratedCost genCost = performanceMap.get(vEmployee);
        double callCostContribution = genCost.calls * totalCallCost/totalNrCalls;
        int daysWorked = genCost.daysInMonth.size();
        if (daysWorked == 0) ++daysWorked;
        if (genCost.hoursActiveOnPhone == 0) genCost.hoursActiveOnPhone = 1;
        %>
		<tr bgcolor="FFCC66" class="bodytekst">
			<td width="90" align="right" valign="top"><%=vEmployee%></td>
            <td width="90" align="right" valign="top"><%=genCost.calls%></td>
            <td width="90" align="right" valign="top"><%=genCost.duration/60%></td>
			<td width="90" align="right" valign="top"><%=mCostFormatter.format(callCostContribution)%></td>
            <td width="90" align="right" valign="top"><%=mCostFormatter.format(genCost.hoursActiveOnPhone)%></td>
            <td width="90" align="right" valign="top"><%=mCostFormatter.format(genCost.calls/genCost.hoursActiveOnPhone)%></td>
            <td width="90" align="right" valign="top"><%=mCostFormatter.format(genCost.taskCost)%></td>
            <td width="90" align="right" valign="top"><%=mCostFormatter.format(genCost.taskCost + callCostContribution)%></td>
            <td width="90" align="right" valign="top"><%=daysWorked%></td>
            <td width="90" align="right" valign="top"><%=mCostFormatter.format((genCost.taskCost + callCostContribution)/daysWorked)%></td>
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

</table>
<br>
<br>

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

</body>

</html>

