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
be.tba.ejb.account.interfaces.*,
be.tba.ejb.pbx.interfaces.CallRecordEntityData,
be.tba.ejb.pbx.session.CallRecordSqlAdapter,
be.tba.util.constants.EjbJndiNames,
be.tba.util.constants.Constants,
be.tba.util.exceptions.AccessDeniedException,
be.tba.servlets.session.SessionManager,
be.tba.util.session.AccountCache,
be.tba.util.data.*"%>
<%!
private String vAccountKey;
private StringBuilder allEntryIds;
%>
	<%
try
{
allEntryIds = new StringBuilder("[");

%>

<form name="calllistform" method="GET"
	action="/TheBusinessAssistant/CustomerDispatch"><input type=hidden
	name=<%=Constants.RECORD_TO_DELETE%> value=""> <input type=hidden
	name=<%=Constants.SRV_ACTION%> value="<%=Constants.ACTION_SHOW_CALLS%>"> 
<table cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
	<tr>
		<!-- white space -->
		<td valign="top" width="20" bgcolor="FFFFFF"></td>

		<!-- account list -->
		<td valign="top" bgcolor="FFFFFF"><br>
        <p><span class="admintitle"> Huidig geregistreerde oproepen:
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input class="tbabutton" type=submit name=action value="Herlaad" onclick="refresh()"> 
        </span></p>
<%
  InitialContext vContext = new InitialContext();

  if (vSession == null)
    throw new AccessDeniedException("U bent niet aangemeld.");
    
  if (vSession.getFwdNumber() == null)
    throw new AccessDeniedException("Account nummer not set in session.");

  CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();

  Collection<CallRecordEntityData> vRecords = vQuerySession.getDocumentedForMonth(vSession, vSession.getFwdNumber(), vSession.getMonthsBack(), vSession.getYear());
  
  AccountEntityData vAccount = AccountCache.getInstance().get(vSession.getFwdNumber());
%>
<input class="tbabutton" type=submit name=action value="Vorige Oproepen" onclick="showPrevious()"> 
<%
if (!vSession.isCurrentMonth())
{
out.println("<input class=\"tbabutton\" type=submit name=action value=\"Volgende Oproepen\"  onclick=\"showNext()\">");
}

  
  out.println("<br><br><table border=\"0\" cellspacing=\"2\" cellpadding=\"2\">");
  if (vRecords == null || vRecords.size() == 0)
  {
    out.println("<span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er zijn geen nieuwe oproepgegevens beschikbaar voor de maand " + vSession.getMonthsBackString() + ".</span>");
    out.println("</table>");
  }
  else
  {
    if (vRecords.size() == 1)
      out.println("<span class=\"bodytekst\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er is </span><span class=\"bodyredbold\">1</span><span class=\"bodytekst\">  oproep beschikbaar voor de maand " + vSession.getMonthsBackString() + ".</span>");
    else
      out.println("<span class=\"bodytekst\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er zijn </span><span class=\"bodyredbold\">" + vRecords.size() + "</span><span class=\"bodytekst\"> oproepen beschikbaar voor de maand " + vSession.getMonthsBackString() + ".</span>");
    int vNewCnt = 0;
    long vLastLogin = vAccount.getPreviousLoginTS();
    for (Iterator<CallRecordEntityData> i = vRecords.iterator(); i.hasNext();)
    {
      CallRecordEntityData vEntry = i.next();
      if (vEntry.getTimeStamp() > vLastLogin)
        ++vNewCnt;
    }  
    if (vNewCnt > 0)
    {
      if (vNewCnt == 1)
        out.println("<br><span class=\"bodytekst\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Sinds uw laatste login is er </span><span class=\"bodyredbold\">1</span><span class=\"bodytekst\">  nieuwe oproep beschikbaar.</span>");
      else
        out.println("<br><span class=\"bodytekst\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Sinds uw laatste login zijn er </span><span class=\"bodyredbold\">" + vNewCnt + "</span><span class=\"bodytekst\">  nieuwe oproepen beschikbaar.</span>");
    }
    else
      out.println("<br><span class=\"bodytekst\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Sinds uw laatste login zijn er geen nieuwe oproepen geregistreerd.</span>");
    
    out.println("              <br><br>");
    out.println("              <tr>");
    out.println("                <td width=\"30\" bgcolor=\"FFFFFF\"></td>");
    out.println("                <td width=\"20\"  valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\"></td>");
    out.println("                <td width=\"65\"  valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Datum</td>");
    out.println("                <td width=\"45\"  valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Uur</td>");
    out.println("                <td width=\"80\"  valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Nummer</td>");
    out.println("                <td width=\"200\" valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Naam</td>");
    out.println("                <td width=\"500\" valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Omschrijving</td>");
    out.println("                <td width=\"80\"  valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Info</td>");
    out.println("              </tr>");

    int vRowInd = 0;
    for (Iterator<CallRecordEntityData> i = vRecords.iterator(); i.hasNext();)
    {
      CallRecordEntityData vEntry = i.next();
      String vId = "id" + vEntry.getId();
      String vDate = vEntry.getDate();
      String vTime = vEntry.getTime();
      String vNumber = vEntry.getNumber();
      String vName = vEntry.getName();
      vName = vName == null ? "" : vName;
      String vShortDesc = (String) vEntry.getShortDescription();
      vShortDesc = vShortDesc == null ? "" : vShortDesc;
      String vLongDesc = (String) vEntry.getLongDescription();
      vLongDesc = vLongDesc == null ? "" : vLongDesc;
      String vInfoGifs = "";
      if (vLongDesc.length() > 0)
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/TheBusinessAssistant/images/info.gif\" alt=\"dubbel klik om de info te bekijken\" height=\"16\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsAgendaCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/TheBusinessAssistant/images/agenda.gif\" alt=\"afspraak toegevoegd in uw agenda\" height=\"13\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsSmsCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/TheBusinessAssistant/images/sms.gif\" alt=\"wij hebben een SMS bericht verstuurd ivm deze oproep\" height=\"13\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsForwardCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/TheBusinessAssistant/images/telefoon.gif\" alt=\"oproep doorgeschakeld naar u\" height=\"13\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsFaxCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/TheBusinessAssistant/images/fax.gif\" alt=\"binnenkomende fax voor u verwerkt\" height=\"13\" border=\"0\">&nbsp;");
      }
      String vImportant = "";
      if (vEntry.getIsImportantCall())
      {
        vImportant = vImportant.concat("<img src=\"/TheBusinessAssistant/images/important.gif\" alt=\"belangrijke oproep!\" height=\"13\" border=\"0\">&nbsp;");
      }
      String vInOut;
      if (vEntry.getIsIncomingCall())
        vInOut = "\"/TheBusinessAssistant/images/incall.gif\"";
      else
        vInOut = "\"/TheBusinessAssistant/images/outcall.gif\"";
%>
	<tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst"
		ondblclick="changeUrl('/TheBusinessAssistant/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.RECORD_UPDATE%>&<%=Constants.RECORD_ID%>=<%=vEntry.getId()%>');">
		<td width="30" bgcolor="FFFFFF"><img src=<%=vInOut%> height="13" border="0"></td>
		<td width="20" valign="top"><%=vImportant%></td>
		<td width="65" valign="top"><%=vDate%></td>
		<td width="45" valign="top"><%=vTime%></td>
		<td width="80" valign="top"><%=vNumber%></td>
		<td width="200" valign="top"><%=vName%></td>
		<td width="500" valign="top"><%=vShortDesc%></td>
		<td width="80" valign="top"><%=vInfoGifs%></td>
	</tr>
	<%
      vRowInd++;
      allEntryIds.append("\"");
      allEntryIds.append(vId);
      allEntryIds.append("\"");
      allEntryIds.append(",");    
    }
    if (vRowInd > 0)
    {
        allEntryIds.deleteCharAt(allEntryIds.length() - 1);
    }
    out.println("</table>");
  }
  allEntryIds.append("]");
}
catch (Exception ex)
{
  ex.printStackTrace();
}
%>
	</td>
</table>
</td>
</tr>
</table>
</form>



</body>

</html>

<script>
var linesToDelete = new Array();


function selectAll()
{
  var allArr = <%=allEntryIds.toString()%>;
  for (var i = 0; i < allArr.length; i++)
  {
    linesToDelete[i] = allArr[i].substring(2);
    var entry = document.getElementById(allArr[i]) ;
    entry.style.backgroundColor= "FF9966";
  }
}

function deselectAll()
{
  var allArr = <%=allEntryIds.toString()%>;
  for (var i = 0; i < allArr.length; i++)
  {
    linesToDelete[i] = null;
    var entry = document.getElementById(allArr[i]) ;
    entry.style.backgroundColor= "FFCC66";
  }
}

function reverseSelection()
{
  var allArr = <%=allEntryIds.toString()%>;
  for (var i = 0; i < allArr.length; i++)
  {
    if (linesToDelete[i] == null)
    {
      linesToDelete[i] = allArr[i].substring(2);
      var entry = document.getElementById(allArr[i]) ;
      entry.style.backgroundColor= "FF9966";
    }
    else
    {
      linesToDelete[i] = null;
      var entry = document.getElementById(allArr[i]) ;
      entry.style.backgroundColor= "FFCC66";
    }
  }
}

function logoff()
{
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.ACTION_LOGOFF%>";
  window.close();
  return false;
}

function changeUrl(newURL) 
{
  location=newURL;
}


function openRecord(id, rowInd)
{
  var entry = document.getElementById(id);
  linesToDelete[rowInd] = null;
  entry.style.backgroundColor = "FFFF99";
}

function showPrevious()
{
  document.calllistform.<%=Constants.RECORD_TO_DELETE%>.value="";
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.RECORD_SHOW_PREV%>";
}

function showNext()
{
  document.calllistform.<%=Constants.RECORD_TO_DELETE%>.value="";
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.RECORD_SHOW_NEXT%>";
}

function refresh()
{
  document.calllistform.<%=Constants.RECORD_TO_DELETE%>.value="";
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.ACTION_REFRESH_CALLS%>";
}
</script>

