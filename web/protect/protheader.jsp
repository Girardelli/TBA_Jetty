<head>

<meta name="Keywords" content="virtueel secretariaat telefoondiensten antwoorddiensten kantoor automatisering tekstverwerking administratie afsprakendienst dactylo">
<meta name="Description" content="Uw virtueel secretariaat.">
<meta name="Owner" content="yves.willems@theBusinessAssistant.be">
<meta HTTP-EQUIV="Content-Language" content="NL">
<!-- <meta HTTP-EQUIV="Refresh" content="30">-->
<meta name="Copyright" content="Copyright © 2003 TheBusinessAssistant, All rights reserved.">
<meta name="Distribution" content="Global">
<link rel="shortcut icon" href="./images/favicon.png" type="image/x-icon" />
<link rel="stylesheet" type="text/css" href="/tba/TheBusinessAssistant.css" title="main">
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

<%@ page session="false" isThreadSafe="true" isErrorPage="false"
	errorPage="protectfail.jsp"
	import="be.tba.util.constants.*,
be.tba.servlets.session.*,
be.tba.servlets.helper.*,
be.tba.util.exceptions.*"%>
<%!
private WebSession vSession;
private String vSessionId;
%>
<%
HttpSession httpSession = request.getSession();
vSession = (WebSession) httpSession.getAttribute(Constants.SESSION_OBJ);
if (vSession == null)
{
      throw new AccessDeniedException("U bent niet aangemeld.");
}
SessionManager.getInstance().getSession(vSession.getSessionId(), "clientcalls.jsp");
%>


<table width='100%' cellspacing='0' cellpadding='0' border='0' bgcolor="#FFFFFF">
	<tr>
		<td valign='top' align='left' width='500' height='50'
			bgcolor="#FFFFFF"><img
			src="/tba/images/TBA-Logo.png"
			style="padding: 10px;" height="70" alt=""></td>
	</tr>
	<tr>
		<td style="padding: 5px;" colspan="2" bgcolor="#F89920" height="3"></td>
	</tr>
	<tr>
		<td valign='bottom' align='left' valign='top' bgcolor="#22205F">
			<table class="topmenu" cellspacing='0' cellpadding='0' border='0'>
                <tr bgcolor="#22205F" height="40">
                    <td bgcolor="#F89920"><a class='afmeldMenu'
                        href="/tba/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACTION_LOGOFF%>">&nbsp;&nbsp;&nbsp;Afmelden&nbsp;&nbsp;&nbsp;</a>
                    </td>
					<td onmouseover="showmenu('rubrieken')"
						onmouseout="hidemenu('rubrieken')">
						<div class="topMenu">&nbsp;&nbsp;&nbsp;Rubrieken&nbsp;&nbsp;&nbsp;</div>
						<table class="menu" id="rubrieken" width="150">
							<tr>
								<td class="menu"><a class='norm'
									href="/tba/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACTION_SHOW_CALLS%>">Oproepen
										bekijken</a><br> <a class='norm'
									href="/tba/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACTION_GOTO_SEARCH_PAGE%>">Oproepen
										zoeken</a><br> <a class='norm'
									href="/tba/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACTION_SHOW_TASKS%>">Taken
										bekijken</a><br></td>
							</tr>
						</table>
					</td>
					<td onmouseover="showmenu('persoonlijk')"
						onmouseout="hidemenu('persoonlijk')">
						<div class="topMenu">&nbsp;&nbsp;&nbsp;Persoonlijk&nbsp;&nbsp;&nbsp;</div>
						<table class="menu" id="persoonlijk" width="150">
							<tr>
								<td class="menu"><a class='norm'
									href="/tba/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.UPDATE_PREFS%>">Instellingen</a><br>
								</td>
							</tr>
						</table>
					</td>
					<td onmouseover="showmenu('help')" onmouseout="hidemenu('help')">
						<div class="topMenu">&nbsp;&nbsp;&nbsp;Help&nbsp;&nbsp;&nbsp;</div>
						<table class="menu" id="help" width="150">
							<tr>
								<td class="menu"><a class='norm'
									href="/tba/protect/helpprot.jsp">Toelichtingen</a><br>
								</td>
							</tr>
						</table>
					</td>
				</tr>

			</table>
		</td>
	</tr>


</table>
</body>



