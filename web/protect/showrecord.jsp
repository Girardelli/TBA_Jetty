<html>
<%@ include file="protheader.jsp" %>

<head>
</head>

	<%@ page
		import="javax.ejb.*,
java.util.*,


javax.naming.Context,
javax.naming.InitialContext,

javax.ejb.*,
be.tba.ejb.pbx.interfaces.*,
be.tba.ejb.pbx.session.CallRecordSqlAdapter,
be.tba.util.constants.*,
be.tba.util.session.*"%>
<%
CallRecordEntityData mRecordData;
String mCustomerName;

if (vSession == null)
		  throw new AccessDeniedException("U bent niet aangemeld bij deze administratie pagina's.");
	
//	vSession.setCallingJsp(Constants.CLIENT_SHOW_REC_JSP);  

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
    vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/agenda.gif\" alt=\"afspraak toegevoegd in uw agenda\" height=\"13\" border=\"0\">&nbsp;&nbsp;");
  }
  if (mRecordData.getIsSmsCall())
  {
    vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/sms.gif\" alt=\"wij hebben een SMS bericht verstuurd ivm deze oproep\" height=\"13\" border=\"0\">&nbsp;&nbsp;");
  }
  if (mRecordData.getIsForwardCall())
  {
    vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/telefoon.gif\" alt=\"oproep doorgeschakeld naar u\" height=\"13\" border=\"0\">&nbsp;&nbsp;");
  }
  if (mRecordData.getIsFaxCall())
  {
    vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/fax.gif\" alt=\"binnenkomende fax voor u verwerkt\" height=\"13\" border=\"0\">&nbsp;&nbsp;");
  }
    
%>
<!-- action name must be a URI name as it is set in the <application>.xml servlet-mapping tag.-->
<form name="calllistform" method="POST"	action="/tba/CustomerDispatch">

<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
   <tr>
      <td valign="middle" width="30" bgcolor="FFFFFF"></td>
      <td valign="middle" bgcolor="FFFFFF">
      <br><br> <span class="admintitle"> Oproep <%=vDirStr%> <%=mRecordData.getName()%></span> <br><br>
		<table border="0" cellspacing="4" cellpadding="4">
			<%                  
if (vInfoGifs.length() > 0)
{
   %>
   <tr>
       <td width="200" valign="middle" class="adminsubsubtitle">
        <img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;Bijkomende Informatie</td>
       <td width="500" valign="middle" class="bodytekst"><%=vInfoGifs%></td>
   </tr>
   <%
}                  
%>
            <tr>
                <td width="200" valign="middle" class="adminsubsubtitle"><img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;Is gearchiveerd</td>
                <td width="500" valign="middle" class="bodytekst" title="klik om de oproep te archiveren" >
                  <input type="checkbox" id="cbx1" style="display:none" name=<%=Constants.RECORD_ARCHIVED%><%=(mRecordData.getIsArchived() ? " checked=\"checked\"" : "")%> />
                  <label for="cbx1" class="toggle"><span></span></label>    
                </td>
            </tr>
			<tr>
				<td width="200" valign="middle" class="adminsubsubtitle"><img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;Datum</td>
				<td width="500" valign="middle" class="bodytekst"><%=mRecordData.getDate()%></td>
			</tr>
			<tr>
				<td width="200" valign="middle" class="adminsubsubtitle"><img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;Uur</td>
				<td width="500" valign="middle" class="bodytekst"><%=mRecordData.getTime()%></td>
			</tr>
			<tr>
				<td width="200" valign="middle" class="adminsubsubtitle"><img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;<%=vDirStr%>nummer</td>
				<td width="500" valign="middle" class="bodytekst"><%=mRecordData.getNumber()%></td>
			</tr>
			<tr>
				<td width="200" valign="middle" class="adminsubsubtitle"><img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;Naam</td>
				<td width="500" valign="middle" class="bodytekst"><%=mRecordData.getName()%></td>
			</tr>
			<tr>
				<td width="200" valign="top" class="adminsubsubtitle"><img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;Omschrijving</td>
				<td width="500" valign="middle" class="bodytekst"><%=(String) mRecordData.getShortDescription()%></td>
			</tr>
			<tr>
				<td width="200" valign="top" class="adminsubsubtitle"><img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;Opvolging</td>
				<td width="500" valign="middle"><textarea
					name=<%=Constants.RECORD_SHORT_TEXT%> rows=10 cols=70></textarea></td>
			</tr>
			<%
			if (mRecordData.getLongDescription() != null && !mRecordData.getLongDescription().isEmpty())
			{
			%>
			<tr>
				<td width="200" valign="top" class="adminsubsubtitle"><img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;Bijkomende Informatie</td>
				<td width="500" valign="middle" class="bodytekst"><%=(String) mRecordData.getLongDescription()%></td>
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
         <td valign="middle" width="30" bgcolor="FFFFFF"></td>
         <td valign="middle" bgcolor="FFFFFF">
        <input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.SAVE_RECORD%>"> 
        <input type=hidden name=<%=Constants.RECORD_ID%> value="<%=mRecordData.getId()%>"> 
        <input class="tbabutton" type=submit name=action value=" Bewaar "> 
        <input class="tbabutton" type=submit value=" Terug " onclick="cancelUpdate();">
        </td>
      </tr>
</table>

</form>

<script type="text/javascript">
function cancelUpdate()
{
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.ACTION_SHOW_CALLS%>";
}
</script>

</body>

</html>

