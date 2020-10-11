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

try {
vSession.setCallingJsp(Constants.ADMIN_WORK_ORDER_JSP);

%>
<body>
<form name="workorderform" method="POST" action="/tba/AdminDispatch">
<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.ADMIN_WORK_ORDER_JSP%>"> 
</form>
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
                <tr>
                    <td width="30"></td> 
                    <td width="140" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Ingegeven op</td>
                    <td width="300" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Klant</td>
                    <td width="60" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Status</td>
                    <td width="70" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Opleverdatum</td>
                    <td width="500" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Beschrijving</td>
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
                                     AccountEntityData vAccount = AccountCache.getInstance().get(workOrder.accountId);
                                     
                %>
                <tr class="bodytekst" ondblclick="changeUrl('/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_WORKORDER%>&<%=Constants.WORKORDER_ID%>=<%=workOrder.id%>');">
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
                    <td bgcolor=<%=bgColor%> width="140" valign="top"><%=workOrder.startDate%></td>
                    <td bgcolor=<%=bgColor%> width="300" valign="top"><%=vAccount.getFullName()%></td>
                    <td bgcolor=<%=bgColor%> width="60" valign="top"><%=WorkOrderData.getStateStr(workOrder.state)%></td>
                    <td bgcolor=<%=bgColor%> width="70" valign="top"><%=workOrder.dueDate%></td>
                    <td bgcolor=<%=bgColor%> width="500" valign="top"><%=workOrder.title%></td>
                </tr>
                <%
                                    }
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
        %>
        </td>
    </tr>
</table>
        </td>
    </tr>
</table>
<script>
function changeUrl(newURL) 
{
  location=newURL;
}

</script>
</body>
</html>
