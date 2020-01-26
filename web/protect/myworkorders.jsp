<html>
<%@ include file="protheader.jsp"%>

<head>
</head>

<%@ page import="javax.ejb.*,
java.util.*,


javax.naming.Context,
javax.naming.InitialContext,

javax.ejb.*,
be.tba.ejb.pbx.interfaces.*,
be.tba.ejb.pbx.session.CallRecordSqlAdapter,
be.tba.ejb.account.interfaces.*,
be.tba.ejb.task.interfaces.*,
be.tba.ejb.task.session.*,
be.tba.util.constants.*,
be.tba.util.session.*"%>
<%
try 
{
    if (vSession == null)
        throw new AccessDeniedException("U bent niet aangemeld.");
    vSession.setCallingJsp(Constants.CLIENT_WORKORDERS_JSP);
    AccountEntityData vAccountData = AccountCache.getInstance().get(vSession.getCurrentAccountId());
    //System.out.println("myworkorders: account:" + vAccountData);
    WorkOrderSqlAdapter workorderSession = new WorkOrderSqlAdapter();
    Collection<WorkOrderData> workOrders = workorderSession.getListForAccount(vSession, vAccountData.getId(), false);
    
%>

<form name="workorderlistform" method="POST" action="/tba/CustomerDispatch">
<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.ACTION_GOTO_WORKORDERS%>">
<input type=hidden name=<%=Constants.WORKORDER_ID%> value="">
<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
    <tr>
        <!-- white space -->
        <td valign="top" width="20" bgcolor="FFFFFF"></td>

        <!-- account list -->
        <td valign="top" width="865" bgcolor="FFFFFF"><br>
        <p><span class="admintitle">Mijn opdrachten</span><br>
        <br>
        <input class="tbabutton" type=submit value=" Maak nieuwe opdracht " onclick="addWorkOrder();"> 
        <br>
        <table cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
           <tr>
            <!-- white space -->
            <td valign="top" width="20" bgcolor="FFFFFF"></td>
    
            <!-- account list -->
            <td valign="top" bgcolor="FFFFFF">
                <br>
<%
    if (workOrders.isEmpty())
    {
        out.println("<p><span class=\"adminsubtitle\">Er zijn geen openstaande opdrachten.</span></p>");
    }
    else
    {
            %> 
            <br>
            <table border="0" cellspacing="2" cellpadding="2">
                <tr>
                    <td width="30"></td> 
                    <td width="70" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Status</td>
                    <td width="70" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Opleverdatum</td>
                    <td width="400" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Beschrijving</td>
                </tr>
    
                <%
    
       for (WorkOrderData workorder :  workOrders)
       {
                %>
                <tr class="bodytekst" title="dubbel muisklik om de opdracht te openen" ondblclick="changeUrl('/tba/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACTION_UPDATE_WORKORDER%>&<%=Constants.WORKORDER_ID%>=<%=workorder.id%>');">
                    <td width="30"> 
                      <img src="/tba/images/waste.gif" onclick="openModal(<%=workorder.id%>)" title="verwijder" onMouseOver="this.style.cursor='pointer'">
                    </td>
                    <td bgcolor=#FFCC66 width="70" valign="top"><%=WorkOrderData.getStateStr(workorder.state)%></td>
                    <td bgcolor=#FFCC66 width="70" valign="top"><%=workorder.dueDate%></td>
                    <td bgcolor=#FFCC66 width="400" valign="top"><%=workorder.title%></td>
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



      <!-- #######  Confirm Modal ######-->

<div id="modal_1" class="tbaModal">
<div class="modal-content">
<span id="spanModal_1" class="close">&times;</span>
<p>De opdracht die je wil verwijderen bevat misschien opgeladen bestanden.<br>
Ben je zeker dat je de opdracht wil verwijderen en de bestanden die hieraan eventueel gekoppeld zijn.</p>
<p align="right">
<input class="tbabutton" type=submit value="Terug">
<input class="tbabutton" type=submit value="Ga door" onclick="removeWorkOrder('868961', 'modalText_1')">
</p></div></div>

</form>
</body>
</html>

<script>
var modal;
var workorderId;

function addWorkOrder()
{
    document.workorderlistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.ACTION_UPDATE_WORKORDER%>";
}

function removeWorkOrder(id)
{
    document.workorderlistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.ACTION_DELETE_WORKORDER%>";
    document.workorderlistform.<%=Constants.WORKORDER_ID%>.value=workorderId;
}

function changeUrl(newURL) 
{
  location=newURL;
}

//Get the modal
function openModal(id)
{
    workorderId = id;
    modal = document.getElementById("modal_1");
    modal.style.display = "block";
}

//When the user clicks anywhere outside of the modal, close it
window.onclick = function(event)
{
  if (event.target == modal)
  {
      modal.style.display = "none";
  }
}
</script>

