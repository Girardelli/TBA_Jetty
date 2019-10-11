<html>
<%@ include file="adminheader.jsp" %>

<head>
<meta HTTP-EQUIV="Refresh" content="<%=Constants.REFRESH%>;URL=\tba\admin\admincalls.jsp">
<title>TheBusinessAssistant administrator pages</title>
</head>

	<%@ page
		import="javax.ejb.*,
java.util.*,


javax.naming.Context,
javax.naming.InitialContext,

javax.ejb.*,
be.tba.ejb.account.interfaces.*,
be.tba.ejb.pbx.interfaces.*,
be.tba.ejb.task.interfaces.*,
be.tba.util.constants.EjbJndiNames,
be.tba.util.constants.Constants,
be.tba.util.exceptions.AccessDeniedException,
be.tba.util.exceptions.InvalidValueException,
be.tba.servlets.session.SessionManager,
be.tba.util.session.AccountCache,
be.tba.ejb.pbx.session.CallRecordSqlAdapter,
be.tba.servlets.session.*,
be.tba.util.data.*"%>

<body>

<form name="newcallform" method="POST" action="/tba/AdminDispatch">
<table border="0" cellspacing="0" cellpadding="0" bgcolor="FFFFFF">
		<tr>
			<td valign="top" width="30" bgcolor="FFFFFF"></td>
			<td valign="top" bgcolor="FFFFFF"><br>
			<br>
			<%
try
{
vSession.setCallingJsp(Constants.NEW_CALL_JSP);
boolean vIsVirgin = false;
boolean vNewCallsAvailable = false;

HttpSession vHttpSession = request.getSession();
  
    CallRecordEntityData vNewRecord = vSession.getNewUnmappedCall();
	if (vNewRecord != null)
    {
      // provide the customer call selection stuff
      CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
      Collection<CallRecordEntityData> vRecords = vQuerySession.getVirgins(vSession);

      if (vRecords.size() > 0)
      {
        vNewCallsAvailable = true;
%>
			<table width="100%" border="0" cellspacing="1" cellpadding="1">
				<tr><td>
					<span class="adminsubtitle">Selecteer 1 van onderstaande oproepen.</span>
				</td></tr>
				<tr>
					<td>
					<table width="100%" border="0" cellspacing="2" cellpadding="4">
						<tr>
							<td width="20" bgcolor="FFFFFF"></td>
							<td width="140" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Klant</td>
							<td width="55" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Datum</td>
							<td width="35" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Uur</td>
							<td width="85" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Nummer</td>
						</tr>


						<%

        for (Iterator<CallRecordEntityData> i = vRecords.iterator(); i.hasNext();)
        {
          CallRecordEntityData vEntry = (CallRecordEntityData) i.next();

          AccountEntityData vAccountEntityData = AccountCache.getInstance().get(vEntry);
          String name;
          if (vAccountEntityData == null)
          {
              name = vEntry.getFwdNr() + "  is onbekend!";
        	  System.out.println("Oproepen database refereert naar een klantnummer " + vEntry.getFwdNr() +
                                            " die niet gekend is. Maak een klant aan met deze klantnummer om deze oproepen zichtbaar te maken.");
          }
          else
          {
        	  name = vAccountEntityData.getFullName();
          }
          String vId = "id" + vEntry.getId();
          String vDate = vEntry.getDate();
          String vTime = vEntry.getTime();
          String vNumber = vEntry.getNumber();
          String vInOut;
          if (vEntry.getIsIncomingCall())
            vInOut = "\"/tba/images/incall.gif\"";
          else
            vInOut = "\"/tba/images/outcall.gif\"";
      %>
						<tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst"
							onmouseover="hooverOnRow('<%=vId%>', '<%=vEntry.getId()%>')"
							onmouseout="hooverOffRow('<%=vId%>', '<%=vEntry.getId()%>')"
							onclick="updateSaveId('<%=vId%>', '<%=vEntry.getId()%>');">
							<td width="20" bgcolor="FFFFFF"><img src=<%=vInOut%> height="13"
								border="0"></td>
							<td width="140" valign="top"><%=name%></td>
							<td width="55" valign="top"><%=vDate%></td>
							<td width="35" valign="top"><%=vTime%></td>
							<td width="85" valign="top"><%=vNumber%></td>
						</tr>
						<%
        }
%>
					</table>
					<br>
					<br>
					</td>
				</tr>
				<tr><td>
					<span class="adminsubtitle"></span>
				</td></tr>
			</table>
			<%
      }
      else
      {
%>
			<table width="100%" border="0" cellspacing="1" cellpadding="1">
				<tr><td>
					<span class="adminsubtitle">Er zijn geen nieuwe oproepen beschikbaar.</span>
					<br>
					<br>
				</td></tr>
			</table>
			<%
      }
  }
  else
  {
    vNewRecord = new CallRecordEntityData();
  }
   if (request.getParameter(Constants.RECORD_CALLER_NAME) != null)
   {
       vNewRecord.setName((String) request.getParameter(Constants.RECORD_CALLER_NAME));
   }
   else
   {
       vIsVirgin = true;
       vNewRecord.setName("");
   }
   if (request.getParameter(Constants.RECORD_SHORT_TEXT) != null)
   {
       vNewRecord.setShortDescription((String) request.getParameter(Constants.RECORD_SHORT_TEXT));
   }
   else
   {
       vIsVirgin = true;
       vNewRecord.setShortDescription("");
   }
   vNewRecord.setLongDescription(request.getParameter(Constants.RECORD_LONG_TEXT) != null ? (String) request.getParameter(Constants.RECORD_LONG_TEXT) : "");
   vNewRecord.setIsSmsCall(request.getParameter(Constants.RECORD_SMS) != null ? request.getParameter(Constants.RECORD_SMS) != null : false);
   vNewRecord.setIsAgendaCall(request.getParameter(Constants.RECORD_AGENDA) != null ? request.getParameter(Constants.RECORD_AGENDA) != null : false);
   vNewRecord.setIsForwardCall(request.getParameter(Constants.RECORD_FORWARD) != null ? request.getParameter(Constants.RECORD_FORWARD) != null : false);
   vNewRecord.setIsImportantCall(request.getParameter(Constants.RECORD_IMPORTANT) != null ? request.getParameter(Constants.RECORD_IMPORTANT) != null : false);
   vNewRecord.setIsFaxCall(request.getParameter(Constants.RECORD_FAX) != null ? request.getParameter(Constants.RECORD_FAX) != null : false);
   vNewRecord.setIs3W_call(false);

if (vIsVirgin)
{
  out.println("<input class=\"tbabutton\" type=submit name=action value=\" Bewaar \">");
  out.println("<input type=hidden name=" + Constants.SRV_ACTION + " value=\"" + Constants.REFRESH_OPEN_CALLS + "\" >");

}
else
{
  out.println("<input class=\"tbabutton\" type=submit name=action value=\" Bewaar \"  onclick=\"saveCall();\">");
  out.println("<input class=\"tbabutton\" type=submit name=action value=\" Kijk voor nieuwe oproepen \" onclick=\"refreshOpenCalls()\">");
  out.println("<input type=hidden name=" + Constants.SRV_ACTION + " value=\"" + Constants.SAVE_NEW_CALL + "\" >");
}
%> 
            <input type=hidden name=<%=Constants.RECORD_ID%> value=""> 
            <input class="tbabutton" type=reset> 
            <input class="tbabutton" type=submit value=" Terug " onclick="removeOpenCalls()">
            <br>
            <br>

			<table width="100%" border="0" cellspacing="1" cellpadding="1">
				<tr>
					<td width="50"></td>
					<td width="170" valign="top" class="adminsubsubtitle">
					   <img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;Belangrijke oproep
					</td>
					<td width="530" valign="top" class="adminsubsubtitle">
					   <input type=checkbox name=<%=Constants.RECORD_IMPORTANT%> value="<%=Constants.YES%>" <%=(vNewRecord.getIsImportantCall() ? " checked" : "")%>>&nbsp;&nbsp;
					   <img	src="/tba/images/important.gif" alt="belangrijke oproep!" height="13" border="0">
					</td>
				</tr>
				<tr>
					<td width="50"></td>
					<td width="170" valign="top" class="adminsubsubtitle"><img
						src="/tba/images/blueSphere.gif" width="10"
						height="10">&nbsp;SMS verstuurd</td>
					<td width="530" valign="top" class="adminsubsubtitle"><input
						type=checkbox name=<%=Constants.RECORD_SMS%>
						value="<%=Constants.YES%>"
						<%=(vNewRecord.getIsSmsCall() ? "checked=\"checked\"" : "")%>>&nbsp;&nbsp;<img
						src="/tba/images/sms.gif"
						alt="wij hebben een SMS bericht gestuurd betreffende deze oproep"
						height="13" border="0"></td>
				</tr>
				<tr>
					<td width="50"></td>
					<td width="170" valign="top" class="adminsubsubtitle"><img
						src="/tba/images/blueSphere.gif" width="10"
						height="10">&nbsp;Afspraak toegevoegd</td>
					<td width="530" valign="top" class="adminsubsubtitle"><input
						type=checkbox name=<%=Constants.RECORD_AGENDA%>
						value="<%=Constants.YES%>"
						<%=(vNewRecord.getIsAgendaCall() ? "checked=\"checked\"" : "")%>>&nbsp;&nbsp;<img
						src="/tba/images/agenda.gif"
						alt="afspraak toegevoegd in uw agenda" height="13" border="0"></td>
				</tr>
				<tr>
					<td width="50"></td>
					<td width="170" valign="top" class="adminsubsubtitle"><img
						src="/tba/images/blueSphere.gif" width="10"
						height="10">&nbsp;Oproep doorgeschakeld</td>
					<td width="530" valign="top" class="adminsubsubtitle"><input
						type=checkbox name=<%=Constants.RECORD_FORWARD%>
						value="<%=Constants.YES%>"
						<%=(vNewRecord.getIsForwardCall() ? "checked=\"checked\"" : "")%>>&nbsp;&nbsp;<img
						src="/tba/images/telefoon.gif"
						alt="oproep doorgeschakeld naar u" height="13" border="0"></td>
				</tr>
				<tr>
					<td width="50"></td>
					<td width="170" valign="top" class="adminsubsubtitle"><img
						src="/tba/images/blueSphere.gif" width="10"
						height="10">&nbsp;Fax</td>
					<td width="530" valign="top" class="adminsubsubtitle"><input
						type=checkbox name=<%=Constants.RECORD_FAX%>
						value="<%=Constants.YES%>"
						<%=(vNewRecord.getIsFaxCall() ? "checked=\"checked\"" : "")%>>&nbsp;&nbsp;<img
						src="/tba/images/fax.gif"
						alt="binnenkomende fax voor u verwerkt" height="13" border="0"></td>
				</tr>
                <tr>
					<td width="50"></td>
					<td width="170" valign="top" class="adminsubsubtitle"><img
						src="/tba/images/blueSphere.gif" width="10"
						height="10">&nbsp;Naam</td>
					<td width="530" valign="top"><input type=text size=30
						name=<%=Constants.RECORD_CALLER_NAME%>
						value="<%=vNewRecord.getName()%>"></td>
				</tr>
				<tr>
					<td width="50"></td>
					<td width="170" valign="top" class="adminsubsubtitle"><img
						src="/tba/images/blueSphere.gif" width="10"
						height="10">&nbsp;Omschrijving</td>
					<td width="530" valign="top"><textarea
						name=<%=Constants.RECORD_SHORT_TEXT%> rows=10 cols=70><%=(String) vNewRecord.getShortDescription()%></textarea></td>
				</tr>
				<tr>
					<td width="50"></td>
					<td width="170" valign="top" class="adminsubsubtitle"><img
						src="/tba/images/blueSphere.gif" width="10"
						height="10">&nbsp;Extra Informatie</td>
					<td width="530" valign="top"><textarea
						name=<%=Constants.RECORD_LONG_TEXT%> rows=10 cols=70><%=(String) vNewRecord.getLongDescription()%></textarea></td>
				</tr>
			</table>
			</td>
		</tr>
</table>
</form>
	<%
}
catch (Exception e)
{
  e.printStackTrace();
}
%>



	<script type="text/javascript">

var recordToSave = null

function refreshOpenCalls()
{
  document.newcallform.<%=Constants.SRV_ACTION%>.value="<%=Constants.REFRESH_OPEN_CALLS%>";
}

function removeOpenCalls()
{
  document.newcallform.<%=Constants.SRV_ACTION%>.value="<%=Constants.REMOVE_OPEN_CALL%>";
}

function hooverOnRow(id, recordId)
{
  entry = document.getElementById(id) ;
  if (recordToSave != recordId)
    entry.style.backgroundColor= "FFFF99";
}

function hooverOffRow(id, recordId)
{
  entry = document.getElementById(id) ;
  if (recordToSave != recordId)
    entry.style.backgroundColor= "FFCC66";
}

function updateSaveId(id, recordId)
{
  if (recordToSave == recordId)
  {
    entry = document.getElementById(id) ;
    entry.style.backgroundColor= "FFCC66";
    recordToSave = null;
  }
  else
  {
    if (recordToSave != null)
    {
      entry = document.getElementById(id) ;
      entry.style.backgroundColor= "FFCC66";
    }
    entry = document.getElementById(id) ;
    entry.style.backgroundColor= "FF9966";
    recordToSave = recordId;
  }
}

function saveCall()
{
  if (recordToSave != null)
  {
    document.newcallform.<%=Constants.RECORD_ID%>.value=recordToSave;
    document.newcallform.<%=Constants.SRV_ACTION%>.value="<%=Constants.SAVE_NEW_CALL%>";
  }
  return false;
}


</script>

</body>

</html>

