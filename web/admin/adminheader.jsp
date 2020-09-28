<head>

<meta name="Keywords"
	content="virtueel secretariaat telefoondiensten antwoorddiensten kantoor automatisering tekstverwerking administratie afsprakendienst dactylo">
<meta name="Description" content="Uw virtueel secretariaat.">
<meta name="Owner" content="yves.willems@theBusinessAssistant.be">
<meta HTTP-EQUIV="Content-Language" content="text/html; charset=utf-8">
<!-- <meta HTTP-EQUIV="Refresh" content="30">-->
<meta name="Copyright" content="Copyright © 2003 TheBusinessAssistant, All rights reserved.">
<meta name="Distribution" content="Global">
<link rel="shortcut icon" href="/tba/images/favicon.png" type="image/x-icon" />
<link rel="stylesheet" type="text/css"
	href="/tba/TheBusinessAssistant.css" title="main">
</head>


<script type="text/javascript">
function showmenu(elmnt)
{
document.getElementById(elmnt).style.visibility="visible";
}
function hidemenu(elmnt)
{
document.getElementById(elmnt).style.visibility="hidden";
}
</script>

<%@ page
	import="java.util.*,
javax.naming.InitialContext,
be.tba.ejb.account.interfaces.*,
be.tba.ejb.pbx.interfaces.*,
be.tba.util.constants.EjbJndiNames,
be.tba.util.constants.Constants,
be.tba.util.constants.AccountRole,
be.tba.util.exceptions.AccessDeniedException,
be.tba.servlets.session.SessionManager,
be.tba.util.session.AccountCache"%>




<%@ page session="false" isThreadSafe="true" isErrorPage="false"
	errorPage="adminfail.jsp" import="be.tba.servlets.session.*"%>
<%
  HttpSession httpSession = request.getSession();
  WebSession vSession = (WebSession) httpSession.getAttribute(Constants.SESSION_OBJ);
  if (vSession == null)
  {
	  throw new AccessDeniedException("U bent niet aangemeld bij deze administratie pagina's.");
  }
  SessionManager.getInstance().getSession(vSession.getSessionId(), "xxx.jsp");
%>



<table width='100%' cellspacing='0' cellpadding='0' border='0' bgcolor="#FFFFFF">
    <tr>
        <td valign='top' align='left' width='500' height='50' bgcolor="#FFFFFF">
        <img src="/tba/images/TBA-Logo.png" style="padding: 10px;" height="70" alt="">
        </td>
    </tr>
    <tr>
        <td style="padding: 2px;" colspan="0" bgcolor="#F89920" height="0"></td>
    </tr>
    <tr>
        <td valign='bottom' align='left' valign='top' bgcolor="#22205F">
			<table class="topmenu" cellspacing='0' cellpadding='0' border='0'>
				<tr bgcolor="#488FCD" height="40">
					<td onmouseover="showmenu('rubrieken')" onmouseout="hidemenu('rubrieken')">
						<div class="topMenu">&nbsp;&nbsp;&nbsp;Rubrieken&nbsp;&nbsp;&nbsp;</div>
						<table class="menu" id="rubrieken">
							<tr>
								<td class="menu">
                                    <a class='norm' href="/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_CANVAS%>">Oproepen lijst</a>
									<br> <a class='norm' href="/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_ACCOUNT_ADMIN%>">Klanten</a>
                                    <br> <a class='norm' href="/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_RECORD_SEARCH%>">Zoeken</a>
                                    <br> <a class='norm' href="/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_ADMIN_WORKORDERS%>">Opdrachten</a>
									<br> <a class='norm' href="/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_TASK_ADMIN%>">Taken</a>
								</td>
							</tr>
						</table>
					</td>
					<td onmouseover="showmenu('selecteren')" onmouseout="hidemenu('selecteren')">
						<div class="topMenu">&nbsp;&nbsp;&nbsp;Selecteren&nbsp;&nbsp;&nbsp;</div>
						<table class="menu" id="selecteren" width="200">
							<tr>
								<td class="menu">
                                    <a class='norm' href="#" onclick="selectAll()">Selecteer alles</a><br>
									<a class='norm' href="#" onclick="deselectAll()">Selecteer niets</a><br> 
                                    <a class='norm' href="#" onclick="reverseSelection()">Selectie omkeren</a>
								</td>
							</tr>
						</table>
					</td>
					<%
        if (vSession.getRole() == AccountRole.ADMIN)
        {
    %>

					<td onmouseover="showmenu('tba_admin')"	onmouseout="hidemenu('tba_admin')">
						<div class="topMenu">&nbsp;&nbsp;&nbsp;Administratie&nbsp;&nbsp;&nbsp;</div>
						<table class="menu" id="tba_admin">
							<tr>
								<td class="menu">
                                    <a class='norm' href="/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_INVOICE_ADMIN%>">Factuur lijst</a> <br> 
                                    <a class='norm' href="/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_INVOICE%>">Maak Factuur</a> <br> 
                                    <a class='norm' href="/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_ADD_INVOICE%>">Maak Manueel Factuur</a> <br> 
                                    <a class='norm' href="/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_OPEN_INVOICE%>">Open facturen</a> <br> 
                                    <a class='norm' href="/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_EMPLOYEE_ADMIN%>">Werknemers</a><br> 
                                    <a class='norm' href="/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_EMPLOYEE_COST%>">Prestaties</a><br>
                                    <a class='norm' href="/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_ARCHIVED_ACCOUNTS%>">Gearchiveerde klanten</a>
								</td>
							</tr>
						</table>
					</td>
                    <%
        }
    %>
                    <td width=100%>
                    </td>
                    <td><div align="right"><a class='tbabuttonorange' href="/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ADMIN_LOG_OFF%>">Afmelden</a></div>
                    </td>
                    <td>&nbsp;&nbsp;</td>
				</tr>

			</table>
		</td>
	</tr>


</table>
</body>
