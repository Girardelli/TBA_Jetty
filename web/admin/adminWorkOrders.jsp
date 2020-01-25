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
be.tba.ejb.task.interfaces.*,
be.tba.ejb.task.session.TaskSqlAdapter,
be.tba.ejb.task.session.WorkOrderSqlAdapter,
be.tba.util.constants.Constants,
be.tba.util.exceptions.AccessDeniedException,
be.tba.servlets.session.SessionManager,
be.tba.util.session.AccountCache,
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
        <td valign="top" width="865" bgcolor="FFFFFF"><br>
        <p><span class="admintitle">Opdrachtenlijst</span>
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
                                out.println("<p><span class=\"adminsubtitle\"> Geen openstaande opdrachten</span></p>");
                            }
                            else
                            {
            %> 
            <br>
            <br>
            <br>
            <table border="0" cellspacing="2" cellpadding="2">
                <tr>
                    <td width="300" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Klant</td>
                    <td width="60" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Status</td>
                    <td width="70" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Opleverdatum</td>
                    <td width="500" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Beschrijving</td>
                </tr>
    
                <%
    
                                  int vRowInd = 0;
                                  for (Iterator<WorkOrderData> i = workOrders.iterator(); i.hasNext();)
                                  {
                                     WorkOrderData vEntry = i.next();
                                     AccountEntityData vAccount = AccountCache.getInstance().get(vEntry.accountId);
                %>
                <tr bgcolor=#CCDD00 class="bodytekst" ondblclick="changeUrl('/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_WORKORDER%>&<%=Constants.WORKORDER_ID%>=<%=vEntry.id%>');">
                    <td width="300" valign="top"><%=vAccount.getFullName()%></td>
                    <td width="60" valign="top"><%=WorkOrderData.getStateStr(vEntry.state)%></td>
                    <td width="70" valign="top"><%=vEntry.dueDate%></td>
                    <td width="500" valign="top"><%=vEntry.title%></td>
                </tr>
                <%
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
