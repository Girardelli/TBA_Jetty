<head>

<meta name="Keywords"
	content="virtueel secretariaat telefoondiensten antwoorddiensten kantoor automatisering tekstverwerking administratie afsprakendienst dactylo">
<meta name="Description" content="Uw virtueel secretariaat.">
<meta name="Owner" content="yves.willems@theBusinessAssistant.be">
<meta HTTP-EQUIV="Content-Language" content="text/html; charset=utf-8">
<!-- <meta HTTP-EQUIV="Refresh" content="30">-->
<meta name="Copyright"
	content="Copyright � 2003 TheBusinessAssistant, All rights reserved.">
<meta name="Distribution" content="Global">
<link rel="stylesheet" type="text/css"
	href="/TheBusinessAssistant/TheBusinessAssistant.css" title="main">
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
be.tba.util.exceptions.InvalidValueException,
be.tba.servlets.session.SessionManager,
be.tba.util.session.AccountCache"%>




<%@ page session="false" isThreadSafe="true" isErrorPage="false"
	errorPage="adminfail.jsp" import="be.tba.servlets.session.*"%>
<%!
private WebSession vSession;
private String vSessionId;
%>
<%
  HttpSession httpSession = request.getSession();
  vSession = (WebSession) httpSession.getAttribute(Constants.SESSION_OBJ);
  if (vSession == null)
  {
	  throw new AccessDeniedException("U bent niet aangemeld bij deze administratie pagina's.");
  }
  SessionManager.getInstance().getSession(vSession.getSessionId(), "xxx.jsp");
%>



<table width='100%' cellspacing='0' cellpadding='0' border='0' bgcolor="000066">
    <tr>
		<td valign='top' align='left' width='500' height='50' bgcolor="000066">
		<img src="/TheBusinessAssistant/images/tba-lightblue-trans-500-50.gif" height="50" border="0" alt="">
		</td>
	</tr>
	<tr>
		<td colspan="2" bgcolor="FFFFFF" height="1"></td>
	</tr>
    <tr>
        <td valign='bottom' align='left' valign='top' bgcolor="000066">
            <table class="topmenu" cellspacing='0' cellpadding='0' border='0'>
                <tr bgcolor="#000066">
                    <td bgcolor="#FF9900">
                        <a class='afmeldMenu' href="/TheBusinessAssistant/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ADMIN_LOG_OFF%>">&nbsp;&nbsp;&nbsp;Afmelden&nbsp;&nbsp;&nbsp;</a>
                    </td>
                <td onmouseover="showmenu('rubrieken')" onmouseout="hidemenu('rubrieken')">
                    <div class="topMenu">&nbsp;&nbsp;&nbsp;Rubrieken&nbsp;&nbsp;&nbsp;</div>
                        <table class="menu" id="rubrieken" width="100">
                        <tr>
                            <td class="menu">
                                <a class='norm' href="/TheBusinessAssistant/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_ACCOUNT_ADMIN%>"><nobr>Klanten</nobr></a>
                                <br>
 			   <a class='norm' href="/TheBusinessAssistant/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_RECORD_ADMIN%>"><nobr>Oproepen</nobr></a>
  							   <br>
							   <a class='norm' href="/TheBusinessAssistant/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_NOTLOGGED_CALLS%>"><nobr>Gemiste Oproepen</nobr></a>
							   <br>
							   <a class='norm' href="/TheBusinessAssistant/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_RECORD_SEARCH%>"><nobr>Zoeken</nobr></a>
							   <br>
							   <a class='norm' href="/TheBusinessAssistant/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_TASK_ADMIN%>"><nobr>Taken</nobr></a>
</td></tr>
   </table>
  </td>
  <td onmouseover="showmenu('selecteren')" onmouseout="hidemenu('selecteren')">
   <div class="topMenu">&nbsp;&nbsp;&nbsp;Selecteren&nbsp;&nbsp;&nbsp;</div>
   <table class="menu" id="selecteren" width="100">
   <tr><td class="menu">
      <a class='norm' href="#" onclick="selectAll()"><nobr>Selecteer alles</nobr></a><br>
      <a class='norm' href="#" onclick="deselectAll()"><nobr>Selecteer niets</nobr></a><br>
      <a class='norm' href="#" onclick="reverseSelection()"><nobr>Selectie omkeren</nobr></a>
   </td></tr>
   </table>
  </td>
    <%
        if (vSession.getRole() == AccountRole.ADMIN)
        {
    %>
  
  <td onmouseover="showmenu('tba_admin')" onmouseout="hidemenu('tba_admin')">
   <div class="topMenu">&nbsp;&nbsp;&nbsp;TBA Admin&nbsp;&nbsp;&nbsp;</div>
   <table class="menu" id="tba_admin" width="100">
   <tr><td class="menu">
      <a class='norm' href="/TheBusinessAssistant/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_INVOICE%>">Maak Factuur</a>
      <br>
      <a class='norm' href="/TheBusinessAssistant/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_INVOICE_ADMIN%>">Factuur lijst</a>
      <br>
      <a class='norm' href="/TheBusinessAssistant/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_OPEN_INVOICE%>">Open facturen</a>
      <br>
      <a class='norm' href="/TheBusinessAssistant/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_EMPLOYEE_ADMIN%>">Werknemers</a>
      <br>
      <a class='norm' href="/TheBusinessAssistant/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.GOTO_EMPLOYEE_COST%>">Prestaties</a>
   </td></tr>
   </table>
  </td>
    <%
        }
    %>
 </tr>
 
</table>
 </td>
 </tr>
 
 
</table>
</body>