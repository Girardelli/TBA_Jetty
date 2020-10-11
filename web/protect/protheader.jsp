<head>

<meta name="Keywords" content="virtueel secretariaat telefoondiensten antwoorddiensten kantoor automatisering tekstverwerking administratie afsprakendienst dactylo">
<meta name="Description" content="Uw virtueel secretariaat.">
<meta name="Owner" content="yves.willems@theBusinessAssistant.be">
<meta HTTP-EQUIV="Content-Language" content="NL">
<!-- <meta HTTP-EQUIV="Refresh" content="30">-->
<meta name="Copyright" content="Copyright © 2003 TheBusinessAssistant, All rights reserved.">
<meta name="Distribution" content="Global">
<link rel="shortcut icon" href="/tba/images/favicon.png" type="image/x-icon" />
<link rel="stylesheet" type="text/css" href="/tba/TheBusinessAssistant.css" title="main">
</head>

<style>
</style>
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

<%@ page session="false" isThreadSafe="true" isErrorPage="false"
	errorPage="protectfail.jsp"
	import="be.tba.util.constants.*,
be.tba.session.*,
be.tba.util.exceptions.*"%>
<%
HttpSession httpSession = request.getSession();
WebSession vSession = (WebSession) httpSession.getAttribute(Constants.SESSION_OBJ);
if (vSession == null)
{
      throw new AccessDeniedException("U bent niet aangemeld.");
}
SessionManager.getInstance().getSession(vSession.getSessionId(), "clientcalls.jsp");
%>
<body>
<table width='100%' cellspacing='0' cellpadding='0' border='0' bgcolor="#FFFFFF">
	<tr>
		<td valign='top' align='left' width='500' height='50'
			bgcolor="#FFFFFF"><img
			src="/tba/images/TBA-Logo.png"
			style="padding: 10px;" height="70" alt=""></td>
	</tr>
    <tr>
        <td style="padding: 2px;" colspan="0" bgcolor="#F89920" height="0"></td>
    </tr>
	<tr>
		<td valign='bottom' align='left' valign='top' bgcolor="#22205F">
			<table class="topmenu" cellspacing='0' cellpadding='5'>
                <tr bgcolor="#488FCD" height="40">
					<td onmouseover="showmenu('rubrieken')"
						onmouseout="hidemenu('rubrieken')">
						<div class="topMenu">&nbsp;&nbsp;&nbsp;Rubrieken&nbsp;&nbsp;&nbsp;</div>
						<table class="menu" id="rubrieken">
							<tr>
								<td class="menu">
								    <a class='norm' href="/tba/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACTION_SHOW_CALLS%>">Oproepen bekijken</a><br> 
                                    <a class='norm' href="/tba/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACTION_ARCHIVED_CALLS%>">Gearchiveerde oproepen</a><br> 
								    <a class='norm' href="/tba/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACTION_GOTO_SHOW_TASKS%>">Taken bekijken</a><br>
                                    <a class='norm' href="/tba/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACTION_GOTO_WORKORDERS%>">Opdrachten doorgeven</a><br>
                                    <a class='norm' href="/tba/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACTION_SEARCH_CALLS%>">Zoeken in oproepen</a><br>
                                </td>
							</tr>
						</table>
					</td>
					<td onmouseover="showmenu('persoonlijk')"
						onmouseout="hidemenu('persoonlijk')">
						<div class="topMenu">&nbsp;&nbsp;&nbsp;Persoonlijk&nbsp;&nbsp;&nbsp;</div>
						<table class="menu" id="persoonlijk">
							<tr>
								<td class="menu"><a class='norm' href="/tba/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_UPDATE_PREFS%>">Instellingen</a><br>
								</td>
							</tr>
						</table>
					</td>
					<td onmouseover="showmenu('help')" onmouseout="hidemenu('help')">
						<div class="topMenu">&nbsp;&nbsp;&nbsp;Help&nbsp;&nbsp;&nbsp;</div>
						<table class="menu" id="help">
							<tr>
								<td class="menu"><a class='norm'
									href="/tba/protect/helpprot.jsp">Toelichtingen</a><br>
								</td>
							</tr>
						</table>
					</td>
                    <td width=100%>
                    </td>
                    <td><div align="right"><a class='tbabuttonorange' href="/tba/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACTION_LOGOFF%>">Afmelden</a></div>
                    </td>
                    <td>&nbsp;&nbsp;</td>
				</tr>

			</table>
		</td>
	</tr>


</table>




