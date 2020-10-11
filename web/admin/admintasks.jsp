<html>

<%@ include file="adminheader.jsp" %>

<%@ page
import="
java.util.*,
be.tba.sqldata.*,
be.tba.sqladapters.*,
be.tba.util.constants.*,
be.tba.util.exceptions.AccessDeniedException,
be.tba.session.SessionManager,
be.tba.sqldata.AccountCache,
be.tba.util.invoice.InvoiceHelper,
be.tba.util.data.*,
java.text.*" %>

<%
StringBuilder allEntryIds = new StringBuilder("[");
try {
vSession.setCallingJsp(Constants.ADMIN_TASK_JSP);

int vCustomerFilter = vSession.getCallFilter().getCustFilter();
%>
<body>
<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
	<tr>
		<!-- white space -->
		<td valign="top" width="20" bgcolor="FFFFFF"></td>

		<!-- account list -->
		<td valign="top" width="865" bgcolor="FFFFFF"><br>
		<p><span class="bodytitle"> Taken beheren<br>
		<br>
		</span></p>
		<form name="taskform" method="POST"
			action="/tba/AdminDispatch"><input type=hidden
			name=<%=Constants.TASK_TO_DELETE%> value=""> <input type=hidden
			name=<%=Constants.SRV_ACTION%> value="<%=Constants.GOTO_TASK_ADMIN%>">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td width="150" valign="top" class="bodysubtitle">&nbsp;Klant</td>
				<td width="10" valign="top">:</td>
				<td width="170" valign="top"><select
					name="<%=Constants.ACCOUNT_FILTER_CUSTOMER%>" onchange="submit()">
					<%
out.println("<option value=\"" + Constants.ACCOUNT_NOFILTER + (vCustomerFilter == Constants.ACCOUNT_NOFILTER ? "\" selected>" : "\">") + "selecteer klant");
					Collection<AccountEntityData> list = AccountCache.getInstance().getCustomerList();
					synchronized(list) 
					{
					    for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();)
					    {
					        AccountEntityData vData = vIter.next();
					        out.println("<option value=\"" + vData.getId() + (vCustomerFilter == vData.getId() ? "\" selected>" : "\">") + vData.getFullName());
					    }
					}
%>
				</select></td>
			</tr>
		</table>
		<br>
		<table>
			<tr>
				<td width="80"><input class="tbabutton" type=submit name=action value=" Toevoegen "
					onclick="addTask()"></td>
    <%
        if (vSession.getRole() == AccountRole.ADMIN)
        {
    %>
				<td width="80"><input class="tbabutton" type=submit name=action value=" Verwijder "
					onclick="deleteTask()"></td>
    <%
        }
    %>
				<td width="30"></td>
				<td width="80">
				    <input class="tbabutton" type=submit name=action value=" Vorige Taken " onclick="showPrevious()">
				</td>

				<%
if (!vSession.isCurrentMonth())
{
  out.println("<td width=\"80\"><input class=\"tbabutton\" type=submit name=action value=\" Volgende Taken \"  onclick=\"showNext()\"></td>");
}
%>
			</tr>
		</table>
		<br>
		<br>
		<%
Collection<TaskEntityData> vTasks = null;
TaskSqlAdapter vTaskSession = new TaskSqlAdapter();

if (vCustomerFilter > 0)
{
   vTasks = vTaskSession.getTasksForMonthforCustomer(vSession, vCustomerFilter, vSession.getMonthsBack(), vSession.getYear());
}
else
{
   vTasks = vTaskSession.getTasksForMonth(vSession, vSession.getMonthsBack(), vSession.getYear());
}

if (vTasks != null && vTasks.size() > 0)
{
  if (!vSession.isCurrentMonth())
  {
    out.println("<span class=\"bodysubtitle\">" + vTasks.size() + " taken uitgevoerd tijdens de maand " + vSession.getMonthsBackString() + ".</span><br>");
  }
  else
  {
    out.println("<span class=\"bodysubtitle\">" + vTasks.size() + " taken uitgevoerd deze maand.</span><br>");
  }
  

%> <br>
		<table border="0" cellspacing="2" cellpadding="2">
			<tr>
<%
if (vCustomerFilter <= 0)
{
%>
                <td width="200" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Klant</td>
<%
}
%>
				<td width="55" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Datum</td>
				<td width="250" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Omschrijving</td>
				<td width="100" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Minuten</td>
				<td width="100" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Kost</td>
				<td width="60" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Info</td>
			</tr>

			<%
  int vRowInd = 0;
  DecimalFormat vCostFormatter = new DecimalFormat("#0.00");
  for (TaskEntityData vEntry : vTasks)
  {
     String vId = "id" + vEntry.getId();
     String vKost;
     AccountEntityData vAccountData = null;
     String vCustomerName = "onbekend";
     if (vEntry.getAccountId() == 0)
     {
        vAccountData = (AccountEntityData) AccountCache.getInstance().get(vEntry.getFwdNr());
     }
     else
     {
        vAccountData = (AccountEntityData) AccountCache.getInstance().get(vEntry.getAccountId());
     }
     if (vAccountData != null)
     {
        vCustomerName = vAccountData.getFullName();
        if (vEntry.getIsFixedPrice())
        {
          vKost = new String(vCostFormatter.format(vEntry.getFixedPrice()) + "Euro (fixed)");   
        }
        else
        {
          double vTaskCost = ((double) vEntry.getTimeSpend() / 60.00) * ((double) vAccountData.getTaskHourRate() / 100.00);
          vKost = new String(vCostFormatter.format(vTaskCost) + "Euro");   
        }
     }
     else
     {
        vKost = new String(vCostFormatter.format(vEntry.getFixedPrice()) + "Euro (fixed)"); 
     }
     
    String vTimeSpend;
    if (vEntry.getTimeSpend() == 0)
    {
      vTimeSpend = "-";
    }
    else
    {
      vTimeSpend = new String(vEntry.getTimeSpend() + "min");
    }
    String vInfoGifs = "";
    if (vEntry.getIsRecuring())
    {
      vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/recurintask.gif\" height=\"13\" border=\"0\">&nbsp;");
    }
    
	if (vSession.getRole() == AccountRole.ADMIN)
	{
%>
			<tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst"
				onmouseover="hooverOnRow('<%=vId%>','<%=vRowInd%>')"
				onmouseout="hooverOffRow('<%=vId%>','<%=vRowInd%>')"
				onclick="updateDeleteFlag('<%=vId%>','<%=vEntry.getId()%>','<%=vRowInd%>')"
				ondblclick="changeUrl('/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.TASK_UPDATE%>&<%=Constants.TASK_ID%>=<%=vEntry.getId()%>');">
<%
if (vCustomerFilter <= 0)
{
%>
            <td width="300" valign="top"><%=vCustomerName%></td>
<%
}
%>
				<td width="55" valign="top"><%=vEntry.getDate()%></td>
				<td width="250" valign="top"><%=vEntry.getDescription()%></td>
				<td width="100" valign="top"><%=vTimeSpend%></td>
				<td width="150" valign="top"><%=vKost%></td>
				<td width="60" valign="top"><%=vInfoGifs%></td>
			</tr>
<%
	}
	else
	{
%>
        <tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst"
            onmouseover="hooverOnRow('<%=vId%>','<%=vRowInd%>')"
            onmouseout="hooverOffRow('<%=vId%>','<%=vRowInd%>')">
<%
if (vCustomerFilter <= 0)
{
%>
            <td width="300" valign="top"><%=vCustomerName%></td>
<%
}
%>
            <td width="55" valign="top"><%=vEntry.getDate()%></td>
            <td width="250" valign="top"><%=vEntry.getDescription()%></td>
            <td width="100" valign="top"><%=vTimeSpend%></td>
            <td width="150" valign="top"><%=vKost%></td>
            <td width="60" valign="top"><%=vInfoGifs%></td>
        </tr>
<%
		}
	    vRowInd++;
		allEntryIds.append("\"");
		allEntryIds.append(vId);
		allEntryIds.append("\"");
		allEntryIds.append(",");
  }
  if (vRowInd > 0)
  {
      allEntryIds.deleteCharAt(allEntryIds.length() - 1);
  }
}
else
{
  out.println("<br><span class=\"bodysubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er zijn geen taken uitgevoerd voor deze klant tijdens de maand " + vSession.getMonthsBackString() + ".</span>");
}

allEntryIds.append("]");
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

	<script type="text/javascript">
var linesToDelete = new Array();

function hooverOnRow(id, rowInd, colour)
{
  var entry = document.getElementById(id) ;
  if (linesToDelete[rowInd] == null)
    entry.style.backgroundColor= colour;
  else
    entry.style.backgroundColor= "FF9966";
}

function hooverOffRow(id, rowInd, colour)
{
  var entry = document.getElementById(id) ;
  if (linesToDelete[rowInd] == null)
    entry.style.backgroundColor= colour;
  else
    entry.style.backgroundColor= "FF9966";
}

function deleteTask()
{
  var shorterArr = new Array();
  var j = 0;
  for (var i = 0; i < linesToDelete.length; i++)
    if (linesToDelete[i] != null)
      shorterArr[j++] = linesToDelete[i];
  document.taskform.<%=Constants.TASK_TO_DELETE%>.value=shorterArr.join();
  document.taskform.<%=Constants.SRV_ACTION%>.value="<%=Constants.TASK_DELETE%>";
}

function updateDeleteFlag(rowid, id, rowInd)
{
  var entry = document.getElementById(rowid) ;
  if (linesToDelete[rowInd] == null)
  {
    linesToDelete[rowInd] = id;
    entry.style.backgroundColor= "FF9966";
  }
  else
  {
    linesToDelete[rowInd] = null;
    entry.style.backgroundColor= "FFFF99";
  }
}





function addTask()
{
  document.taskform.<%=Constants.TASK_TO_DELETE%>.value="";
  document.taskform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_TASK_ADD%>";
}

function openAccount(id, rowInd)
{
  var entry = document.getElementById(id);
  linesToDelete[rowInd] = null;
  entry.style.backgroundColor = "FFFF99";
}

function selectAll()
{
  var allArr = <%=allEntryIds.toString()%>;
  for (var i = 0; i < allArr.length; i++)
  {
    linesToDelete[i] = allArr[i].substring(2);
    var entry = document.getElementById(allArr[i]) ;
    entry.style.backgroundColor= "FF9966";
  }
}

function deselectAll()
{
  var allArr = <%=allEntryIds.toString()%>;
  for (var i = 0; i < allArr.length; i++)
  {
    linesToDelete[i] = null;
    var entry = document.getElementById(allArr[i]) ;
    entry.style.backgroundColor= "FFCC66";
  }
}

function reverseSelection()
{
  var allArr = <%=allEntryIds.toString()%>;
  for (var i = 0; i < allArr.length; i++)
  {
    if (linesToDelete[i] == null)
    {
      linesToDelete[i] = allArr[i].substring(2);
      var entry = document.getElementById(allArr[i]) ;
      entry.style.backgroundColor= "FF9966";
    }
    else
    {
      linesToDelete[i] = null;
      var entry = document.getElementById(allArr[i]) ;
      entry.style.backgroundColor= "FFCC66";
    }
  }
}

function changeUrl(newURL) 
{
  location=newURL;
}

function showPrevious()
{
  document.taskform.<%=Constants.TASK_TO_DELETE%>.value="";
  document.taskform.<%=Constants.SRV_ACTION%>.value="<%=Constants.TASK_SHOW_PREV%>";
}

function showNext()
{
  document.taskform.<%=Constants.TASK_TO_DELETE%>.value="";
  document.taskform.<%=Constants.SRV_ACTION%>.value="<%=Constants.TASK_SHOW_NEXT%>";
}




</script>

</body>

</html>

