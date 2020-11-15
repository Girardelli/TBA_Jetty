<html>

<%@ include file="adminheader.jsp" %>

<%@ page
import="
java.util.*,
be.tba.sqldata.*,
be.tba.sqladapters.*,
be.tba.util.constants.Constants,
be.tba.util.exceptions.AccessDeniedException,
be.tba.session.SessionManager,
be.tba.sqldata.AccountCache,
be.tba.util.invoice.InvoiceHelper,
be.tba.util.data.*,
java.text.*" %>

<%
StringBuilder allEntryIds = new StringBuilder("[");
try 
{
   vSession.setCallingJsp(Constants.ADMIN_WORK_ORDER_JSP);

%>
<body>
<form name="workorderform" method="POST" action="/tba/AdminDispatch">
<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.ARCHIVE_WORKORDERS%>"> 
<input type=hidden name=<%=Constants.RECORDS_TO_HANDLE%> value=""> 
<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
    <tr>
        <!-- white space -->
        <td valign="top" width="20" bgcolor="FFFFFF"></td>

        <!-- account list -->
        <td valign="top" bgcolor="FFFFFF"><br>
        <p><span class="bodytitle">Opdrachtenlijst</span>
       <table cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
           <tr>
            <!-- white space -->
            <td valign="top" width="20" bgcolor="FFFFFF"></td>
    
            <!-- account list -->
            <td valign="top" bgcolor="FFFFFF">
                <input class="tbabutton" type=submit name=action value="Archiveren" onclick="archive()">
                <br>
<%
    Collection<WorkOrderData> workOrders = null;
    WorkOrderSqlAdapter workOrderSession = new WorkOrderSqlAdapter();

    workOrders = workOrderSession.getOpenList(vSession);

    if (workOrders == null || workOrders.isEmpty())
    {
        out.println("<p><span class=\"bodysubtitle\"> Geen openstaande opdrachten</span></p>");
    }
    else
    {
            %> 
            <br>
            <table border="0" cellspacing="2" cellpadding="2">
                <tr bgcolor="#F89920">
                    <td width="30"></td> 
                    <td width="140" valign="top" class="topMenu">&nbsp;Ingegeven op</td>
                    <td width="400" valign="top" class="topMenu">&nbsp;Klant</td>
                    <td width="80" valign="top" class="topMenu">&nbsp;Status</td>
                    <td width="70" valign="top" class="topMenu">&nbsp;Opleverdatum</td>
                    <td width="500" valign="top" class="topMenu">&nbsp;Beschrijving</td>
                </tr>
    
                <%
       int vRowInd = 0;
       WorkOrderData.State[] stateArr = { WorkOrderData.State.kSubmitted, WorkOrderData.State.kBusy, WorkOrderData.State.kDone, WorkOrderData.State.kArchived };
       String[] colorArr = { Constants.kRed, Constants.kOrange, Constants.kGreen, Constants.kGrey };
       int i = -1;
       for (WorkOrderData.State state : stateArr)
       {
          ++i;
          String bgColor = colorArr[i];
          for (WorkOrderData workOrder : workOrders)
          {
             if (workOrder.state != state)
             {
                continue;
             }
             String vId = "id" + workOrder.getId();
             AccountEntityData vAccount = AccountCache.getInstance().get(workOrder.accountId);
             if (workOrder.state == WorkOrderData.State.kDone)
             {
             %>
                <tr bgcolor=<%=bgColor%> id=<%=vId%> class="bodytekst"  onmouseover="hooverOnRow('<%=vId%>','<%=vRowInd%>')" onmouseout="hooverOffRow('<%=vId%>','<%=vRowInd%>')" onclick="updateDeleteFlag('<%=vId%>','<%=workOrder.getId()%>','<%=vRowInd%>')"
                ondblclick="changeUrl('/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_WORKORDER%>&<%=Constants.WORKORDER_ID%>=<%=workOrder.id%>');">
             <%    
             }
             else
             {
             %>
                <tr class="bodytekst" ondblclick="changeUrl('/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_WORKORDER%>&<%=Constants.WORKORDER_ID%>=<%=workOrder.id%>');">
             <%   
             }
                %>
                    <td width="30"> 
              <%
              if (workOrder.isUrgent)
              {
              %>
                      <img src="/tba/images/important.gif" >
              <%
              }
              %>
                    </td>
                    <td width="140" valign="top"><%=workOrder.startDate%></td>
                    <td width="400" valign="top"><%=vAccount.getFullName()%></td>
                    <td width="80" valign="top"><%=WorkOrderData.getStateStr(workOrder.state)%></td>
                    <td width="70" valign="top"><%=workOrder.dueDate%></td>
                    <td width="500" valign="top"><%=workOrder.title%></td>
                </tr>
                <%
                if (workOrder.state == WorkOrderData.State.kDone)
                {
                   vRowInd++;
                   allEntryIds.append("\"");
                   allEntryIds.append(vId);
                   allEntryIds.append("\",");
                }
            }
        }
       if (vRowInd > 0) 
       {
          allEntryIds.deleteCharAt(allEntryIds.length()-1);
       }
        %>
            </table>
        <%
   }                     
}
catch (Exception e)
{
    e.printStackTrace();
}
allEntryIds.append("]");

        %>
        </td>
    </tr>
</table>
        </td>
    </tr>
</table>

</form>

<script>
var linesToDelete = new Array();
var allArr = <%=allEntryIds.toString()%>;

function changeUrl(newURL) 
{
  location=newURL;
}

function hooverOnRow(id, rowInd)
{
  var entry = document.getElementById(id) ;
  if (linesToDelete[rowInd] == null)
    entry.style.backgroundColor= "FFFF99";
  else
    entry.style.backgroundColor= "FF9966";
}

function hooverOffRow(id, rowInd)
{
  var entry = document.getElementById(id) ;
  if (linesToDelete[rowInd] == null)
    entry.style.backgroundColor= "<%=Constants.kGreen%>"; // licht orange
  else
    entry.style.backgroundColor= "FF9966";
}

function updateDeleteFlag(rowid, id, rowInd)
{
  var entry = document.getElementById(rowid) ;
  if (linesToDelete[rowInd] == null)
  {
    linesToDelete[rowInd] = id;
    entry.style.backgroundColor= "ff9966"; // rood ff9966
  }
  else
  {
    linesToDelete[rowInd] = null;
    entry.style.backgroundColor= "<%=Constants.kGreen%>"; // licht geel
  }
}

function selectAll()
{
    //console.log("Select all loggging");
  for (var i = 0; i < allArr.length; i++) // length -1 because we have entered an extra ',' at the end
  {
    linesToDelete[i] = allArr[i].substring(2);
    var entry = document.getElementById(allArr[i]) ;
    entry.style.backgroundColor= "FF9966";
  }
}

function deselectAll()
{
  for (var i = 0; i < allArr.length; i++) // length -1 because we have entered an extra ',' at the end
  {
    linesToDelete[i] = null;
    var entry = document.getElementById(allArr[i]) ;
    entry.style.backgroundColor= "<%=Constants.kGreen%>";
  }
}

function reverseSelection()
{
  for (var i = 0; i < allArr.length; i++) // length -1 because we have entered an extra ',' at the end
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
      entry.style.backgroundColor= "<%=Constants.kGreen%>";
    }
  }
}

function archive()
{
    var shorterArr = new Array();
    var j = 0;
    for (var i = 0; i < linesToDelete.length; i++)
      if (linesToDelete[i] != null)
        shorterArr[j++] = linesToDelete[i];
    document.workorderform.<%=Constants.RECORDS_TO_HANDLE%>.value=shorterArr.join();
    document.workorderform.<%=Constants.SRV_ACTION%>.value="<%=Constants.ARCHIVE_WORKORDERS%>";
}

</script>
</body>
</html>
