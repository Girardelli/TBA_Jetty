<html>
<%@ include file="protheader.jsp" %>

<head>
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
be.tba.ejb.pbx.interfaces.*,
be.tba.ejb.pbx.session.CallRecordSqlAdapter,
be.tba.util.constants.*,
be.tba.util.session.*"%>
	<%!
private CallRecordEntityData mRecordData;
private String mCustomerName;
%>
	<%
  CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();

  mRecordData = vQuerySession.getRecord(vSession, (String) request.getParameter(Constants.RECORD_ID));

  String vDirStr = mRecordData.getIsIncomingCall() ? "Van " : "Naar ";

  String vNumberHtml;
  if (mRecordData.getNumber().length() == 0)
    vNumberHtml = new String("><input type=text size=20 name=" + Constants.RECORD_NUMBER + " value=\"\">");
  else
    vNumberHtml = new String("><input type=text size=20 name=" + Constants.RECORD_NUMBER + " value=" + mRecordData.getNumber() + ">");
  String vInfoGifs = "";
  if (mRecordData.getIsAgendaCall())
  {
    vInfoGifs = vInfoGifs.concat("<img src=\"/TheBusinessAssistant/images/agenda.gif\" alt=\"afspraak toegevoegd in uw agenda\" height=\"13\" border=\"0\">&nbsp;&nbsp;");
  }
  if (mRecordData.getIsSmsCall())
  {
    vInfoGifs = vInfoGifs.concat("<img src=\"/TheBusinessAssistant/images/sms.gif\" alt=\"wij hebben een SMS bericht verstuurd ivm deze oproep\" height=\"13\" border=\"0\">&nbsp;&nbsp;");
  }
  if (mRecordData.getIsForwardCall())
  {
    vInfoGifs = vInfoGifs.concat("<img src=\"/TheBusinessAssistant/images/telefoon.gif\" alt=\"oproep doorgeschakeld naar u\" height=\"13\" border=\"0\">&nbsp;&nbsp;");
  }
  if (mRecordData.getIsFaxCall())
  {
    vInfoGifs = vInfoGifs.concat("<img src=\"/TheBusinessAssistant/images/fax.gif\" alt=\"binnenkomende fax voor u verwerkt\" height=\"13\" border=\"0\">&nbsp;&nbsp;");
  }
    
%>
<br>
<span class="admintitle"> Oproep <%=vDirStr%> <%=mRecordData.getName()%>.</span>
<br>
<!-- action name must be a URI name as it is set in the <application>.xml servlet-mapping tag.-->
<form name="calllistform" method="GET"
	action="/TheBusinessAssistant/CustomerDispatch">

<table width='100%' cellspacing='0' cellpadding='0' border='0'
	bgcolor="FFFFFF">
	<tr>
		<td valign="top" width="30" bgcolor="FFFFFF"></td>
		<td valign="top" bgcolor="FFFFFF"><br>
		<table width="100%" border="0" cellspacing="1" cellpadding="1">
			<%                  
if (vInfoGifs.length() > 0)
{
out.println("<tr>");
out.println("  <td width=\"50\"></td>");
out.println("  <td width=\"120\" valign=\"top\" class=\"adminsubsubtitle\"><img src=\"/TheBusinessAssistant/images/blueSphere.gif\" width=\"10\" height=\"10\">&nbsp;Extra's</td>");
out.println("  <td width=\"580\" valign=\"top\" class=\"bodytekst\">" + vInfoGifs + "</td>");
out.println("</tr>");
}                  
%>
			<tr>
				<td width="30"></td>
				<td width="170" valign="top" class="adminsubsubtitle"><img
					src="/TheBusinessAssistant/images/blueSphere.gif" width="10"
					height="10">&nbsp;Datum</td>
				<td width="550" valign="top" class="bodytekst"><%=mRecordData.getDate()%></td>
			</tr>
			<tr>
				<td width="30"></td>
				<td width="170" valign="top" class="adminsubsubtitle"><img
					src="/TheBusinessAssistant/images/blueSphere.gif" width="10"
					height="10">&nbsp;Uur</td>
				<td width="550" valign="top" class="bodytekst"><%=mRecordData.getTime()%></td>
			</tr>
			<tr>
				<td width="30"></td>
				<td width="170" valign="top" class="adminsubsubtitle"><img
					src="/TheBusinessAssistant/images/blueSphere.gif" width="10"
					height="10">&nbsp;<%=vDirStr%>nummer</td>
				<td width="550" valign="top" class="bodytekst"><%=mRecordData.getNumber()%></td>
			</tr>
			<tr>
				<td width="30"></td>
				<td width="170" valign="top" class="adminsubsubtitle"><img
					src="/TheBusinessAssistant/images/blueSphere.gif" width="10"
					height="10">&nbsp;Naam</td>
				<td width="550" valign="top" class="bodytekst"><%=mRecordData.getName()%></td>
			</tr>
			<tr>
				<td width="30"></td>
				<td width="170" valign="top" class="adminsubsubtitle"><img
					src="/TheBusinessAssistant/images/blueSphere.gif" width="10"
					height="10">&nbsp;Omschrijving</td>
				<td width="550" valign="top" class="bodytekst"><%=(String) mRecordData.getShortDescription()%></td>
			</tr>
			<tr>
				<td width="30"></td>
				<td width="170" valign="top" class="adminsubsubtitle"><img
					src="/TheBusinessAssistant/images/blueSphere.gif" width="10"
					height="10">&nbsp;Opvolging</td>
				<td width="550" valign="top"><textarea
					name=<%=Constants.RECORD_SHORT_TEXT%> rows=10 cols=70></textarea></td>
			</tr>
			<tr>
				<td width="30"></td>
				<td width="170" valign="top" class="adminsubsubtitle"><img
					src="/TheBusinessAssistant/images/blueSphere.gif" width="10"
					height="10">&nbsp;Bijkomende Informatie</td>
				<td width="550" valign="top" class="bodytekst"><%=(String) mRecordData.getLongDescription()%></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
<br>
<br>
<br>
		<input type=hidden
			name=<%=Constants.SRV_ACTION%> value="<%=Constants.SAVE_RECORD%>"> <input
			type=submit name=action value=" Bewaar "> <input type=submit
			value=" Terug " onclick="cancelUpdate();">
		</form>

<script type="text/javascript">
function cancelUpdate()
{
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.ACTION_SHOW_CALLS%>";
}
</script>

</body>

</html>

