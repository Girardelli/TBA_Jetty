<html>
<%@ include file="adminheader.jsp" %>

<head>
<meta HTTP-EQUIV="Refresh" content="<%=Constants.REFRESH%>;URL=\TheBusinessAssistant\admin\admincalls.jsp">
<title>TheBusinessAssistant administrator pages</title>
</head>

	<%@ page
		import="javax.ejb.*,
java.util.*,
javax.rmi.PortableRemoteObject,
java.rmi.RemoteException,
javax.naming.Context,
javax.naming.InitialContext,
javax.rmi.PortableRemoteObject,
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

<table border="0" cellspacing="0" cellpadding="0" bgcolor="FFFFFF">
		<tr>
			<td valign="top" width="30" bgcolor="FFFFFF"></td>
			<td valign="top" bgcolor="FFFFFF"><br>
			<br>
			<span class="bodytekst"> <!-- action name must be a URI name as it is set in the <application>.xml servlet-mapping tag.-->
			<form name="newcallform" method="POST"
				action="/TheBusinessAssistant/AdminDispatch" target=""setTimeout('window.close()',2000)>

			<!--        <form name="newcallform" method="POST" action="/TheBusinessAssistant/AdminDispatch" target="callswindow" onSubmit="setTimeout('window.close()',1000)"> -->

			<%
try
{
vSession.setCallingJsp(Constants.NEW_CALL_JSP);
CallRecordEntityData vNewRecord = null;
boolean vIsVirgin = false;
boolean vNewCallsAvailable = false;

HttpSession vHttpSession = request.getSession();
  Integer vKey = (Integer) vHttpSession.getAttribute(new String("key"));
  vNewRecord = null;
  if (vKey != null)
  {
    Map vNewCalls = vSession.getNewCalls();
	if (!vNewCalls.isEmpty())
	{
    vNewRecord = (CallRecordEntityData) vNewCalls.get(vKey);
    if (vNewRecord != null)
    {
      // provide the customer call selection stuff
      InitialContext vContext = new InitialContext();

      CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
      Collection vRecords = vQuerySession.getVirgins(vSession);

      AccountEntityData vAccountEntityData;

      if (vRecords.size() > 0)
      {
        vNewCallsAvailable = true;
%>
			<table width="100%" border="0" cellspacing="1" cellpadding="1">
				<tr>
					<span class="adminsubtitle">Selecteer 1 van onderstaande oproepen.</span>
				</tr>
				<tr>
					<td>
					<table width="100%" border="0" cellspacing="2" cellpadding="4">
						<tr>
							<td width="20" bgcolor="FFFFFF"></td>
							<td width="140" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Klant</td>
							<td width="55" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Datum</td>
							<td width="35" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Uur</td>
							<td width="85" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Nummer</td>
						</tr>


						<%

        for (Iterator i = vRecords.iterator(); i.hasNext();)
        {
          CallRecordEntityData vEntry = (CallRecordEntityData) i.next();

          vAccountEntityData = AccountCache.getInstance().get(vEntry.getFwdNr());
          if (vAccountEntityData == null)
          {
            throw new InvalidValueException("Oproepen database refereert naar een klantnummer 014/" + vEntry.getFwdNr() +
                                            " die niet gekend is. Maak een klant aan met deze klantnummer om deze oproepen zichtbaar te maken.", null);
          }
          String vId = "id" + vEntry.getId();
          String vDate = vEntry.getDate();
          String vTime = vEntry.getTime();
          String vNumber = vEntry.getNumber();
          String vInOut;
          if (vEntry.getIsIncomingCall())
            vInOut = "\"/TheBusinessAssistant/images/incall.gif\"";
          else
            vInOut = "\"/TheBusinessAssistant/images/outcall.gif\"";
      %>
						<tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst"
							onmouseover="hooverOnRow('<%=vId%>', '<%=vEntry.getId()%>')"
							onmouseout="hooverOffRow('<%=vId%>', '<%=vEntry.getId()%>')"
							onclick="updateSaveId('<%=vId%>', '<%=vEntry.getId()%>');">
							<td width="20" bgcolor="FFFFFF"><img src=<%=vInOut%> height="13"
								border="0"></td>
							<td width="140" valign="top"><%=vAccountEntityData.getFullName()%></td>
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
				<tr>
					<span class="adminsubtitle"></span>
				</tr>
			</table>
			<%
      }
      else
      {
%>
			<table width="100%" border="0" cellspacing="1" cellpadding="1">
				<tr>
					<span class="adminsubtitle">Er zijn geen nieuwe oproepen beschikbaar.</span>
					<br>
					<br>
				</tr>
			</table>
			<%
      }
    }
	}
  }
  if (vNewRecord == null)
  {
    vNewRecord = new CallRecordEntityData();
    vNewRecord.setName("");
    vNewRecord.setLongDescription("");
    vNewRecord.setIsAgendaCall(false);
    vNewRecord.setIsSmsCall(false);
    vNewRecord.setIsForwardCall(false);
    vNewRecord.setIsForwardCall(false);
    vNewRecord.setIsFaxCall(false);
    vNewRecord.setIs3W_call(false);
    vNewRecord.setShortDescription("");
    vIsVirgin = true;
  }
%>

			<table width="100%" border="0" cellspacing="1" cellpadding="1">
				<tr>
					<td width="50"></td>
					<td width="170" valign="top" class="adminsubsubtitle"><img
						src="/TheBusinessAssistant/images/blueSphere.gif" width="10"
						height="10">&nbsp;Belangrijke oproep</td>
					<td width="530" valign="top" class="adminsubsubtitle"><input
						type=checkbox name=<%=Constants.RECORD_IMPORTANT%>
						value="<%=Constants.YES%>"
						<%=(vNewRecord.getIsImportantCall() ? " checked" : "")%>>&nbsp;&nbsp;<img
						src="/TheBusinessAssistant/images/important.gif"
						alt="belangrijke oproep!" height="13" border="0"></td>
				</tr>
				<tr>
					<td width="50"></td>
					<td width="170" valign="top" class="adminsubsubtitle"><img
						src="/TheBusinessAssistant/images/blueSphere.gif" width="10"
						height="10">&nbsp;SMS verstuurd</td>
					<td width="530" valign="top" class="adminsubsubtitle"><input
						type=checkbox name=<%=Constants.RECORD_SMS%>
						value="<%=Constants.YES%>"
						<%=(vNewRecord.getIsSmsCall() ? "checked=\"checked\"" : "")%>>&nbsp;&nbsp;<img
						src="/TheBusinessAssistant/images/sms.gif"
						alt="wij hebben een SMS bericht gestuurd betreffende deze oproep"
						height="13" border="0"></td>
				</tr>
				<tr>
					<td width="50"></td>
					<td width="170" valign="top" class="adminsubsubtitle"><img
						src="/TheBusinessAssistant/images/blueSphere.gif" width="10"
						height="10">&nbsp;Afspraak toegevoegd</td>
					<td width="530" valign="top" class="adminsubsubtitle"><input
						type=checkbox name=<%=Constants.RECORD_AGENDA%>
						value="<%=Constants.YES%>"
						<%=(vNewRecord.getIsAgendaCall() ? "checked=\"checked\"" : "")%>>&nbsp;&nbsp;<img
						src="/TheBusinessAssistant/images/agenda.gif"
						alt="afspraak toegevoegd in uw agenda" height="13" border="0"></td>
				</tr>
				<tr>
					<td width="50"></td>
					<td width="170" valign="top" class="adminsubsubtitle"><img
						src="/TheBusinessAssistant/images/blueSphere.gif" width="10"
						height="10">&nbsp;Oproep doorgeschakeld</td>
					<td width="530" valign="top" class="adminsubsubtitle"><input
						type=checkbox name=<%=Constants.RECORD_FORWARD%>
						value="<%=Constants.YES%>"
						<%=(vNewRecord.getIsForwardCall() ? "checked=\"checked\"" : "")%>>&nbsp;&nbsp;<img
						src="/TheBusinessAssistant/images/telefoon.gif"
						alt="oproep doorgeschakeld naar u" height="13" border="0"></td>
				</tr>
				<tr>
					<td width="50"></td>
					<td width="170" valign="top" class="adminsubsubtitle"><img
						src="/TheBusinessAssistant/images/blueSphere.gif" width="10"
						height="10">&nbsp;Fax</td>
					<td width="530" valign="top" class="adminsubsubtitle"><input
						type=checkbox name=<%=Constants.RECORD_FAX%>
						value="<%=Constants.YES%>"
						<%=(vNewRecord.getIsFaxCall() ? "checked=\"checked\"" : "")%>>&nbsp;&nbsp;<img
						src="/TheBusinessAssistant/images/fax.gif"
						alt="binnenkomende fax voor u verwerkt" height="13" border="0"></td>
				</tr>
                <tr>
                    <td width="50"></td>
                    <td width="170" valign="top" class="adminsubsubtitle"><img
                        src="/TheBusinessAssistant/images/blueSphere.gif" width="10"
                        height="10">&nbsp;oproeper</td>
                    <td width="530" valign="top"><input type=text size=30
                        name=<%=Constants.RECORD_TEMP_CALLER%>
                        value="<%=vNewRecord.getNumber()%>"></td>
                </tr>
				<tr>
					<td width="50"></td>
					<td width="170" valign="top" class="adminsubsubtitle"><img
						src="/TheBusinessAssistant/images/blueSphere.gif" width="10"
						height="10">&nbsp;Naam</td>
					<td width="530" valign="top"><input type=text size=30
						name=<%=Constants.RECORD_CALLER_NAME%>
						value="<%=vNewRecord.getName()%>"></td>
				</tr>
				<tr>
					<td width="50"></td>
					<td width="170" valign="top" class="adminsubsubtitle"><img
						src="/TheBusinessAssistant/images/blueSphere.gif" width="10"
						height="10">&nbsp;Omschrijving</td>
					<td width="530" valign="top"><textarea
						name=<%=Constants.RECORD_SHORT_TEXT%> rows=10 cols=70><%=(String) vNewRecord.getShortDescription()%></textarea></td>
				</tr>
				<tr>
					<td width="50"></td>
					<td width="170" valign="top" class="adminsubsubtitle"><img
						src="/TheBusinessAssistant/images/blueSphere.gif" width="10"
						height="10">&nbsp;Extra Informatie</td>
					<td width="530" valign="top"><textarea
						name=<%=Constants.RECORD_LONG_TEXT%> rows=10 cols=70><%=(String) vNewRecord.getLongDescription()%></textarea></td>
				</tr>
			</table>
			<br>
			<br>
<%
if (vIsVirgin)
{
  out.println("<input type=submit name=action value=\" Bewaar \">");
  out.println("<input type=hidden name=" + Constants.SRV_ACTION + " value=\"" + Constants.GET_OPEN_CALLS + "\" >");

}
else
{
  out.println("<input type=submit name=action value=\" Bewaar \"  onclick=\"saveCall();\">");
  out.println("<input type=submit name=action value=\" Kijk voor nieuwe oproepen \" onclick=\"refreshOpenCalls()\">");
  out.println("<input type=hidden name=" + Constants.SRV_ACTION + " value=\"" + Constants.SAVE_NEW_CALL + "\" >");
}
%> <input type=hidden name=<%=Constants.NEW_RECORD_KEY%> value=<%=vKey%>>
			<input type=hidden name=<%=Constants.RECORD_ID%> value=""> <input
				type=reset> <input type=submit value=" Terug "
				onclick="removeOpenCalls()"></form>
			</span> <br>
			</td>
		</tr>
</table>
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
  window.close();
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
  //setTimeout("window.close()",2000);
  if (recordToSave != null)
  {
    document.newcallform.<%=Constants.RECORD_ID%>.value=recordToSave;
    document.newcallform.<%=Constants.SRV_ACTION%>.value="<%=Constants.SAVE_NEW_CALL%>";
    document.newcallform.submit();
    document.newcallform.target="callswindow";
    window.close();
  }
  return false;
}


</script>

</body>

</html>

