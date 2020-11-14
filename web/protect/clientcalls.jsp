<html>
<%@ include file="protheader.jsp" %>

<head>
</head>

	<%@ page
		import="
java.util.*,
com.google.gson.Gson,
be.tba.sqldata.*,
be.tba.sqladapters.*,
be.tba.util.constants.*,
be.tba.util.exceptions.AccessDeniedException,
be.tba.websockets.WebSocketData,
be.tba.session.SessionManager,
be.tba.sqldata.AccountCache,
be.tba.util.data.*"%>


<%
StringBuilder allEntryIds = new StringBuilder("[");
Collection<CallRecordEntityData> vUrgentRecords = new Vector<CallRecordEntityData>();
try
{
if (vSession == null)
 throw new AccessDeniedException("U bent niet aangemeld.");
vSession.setCallingJsp(Constants.CLIENT_CALLS_JSP);  
// this is the websocket page. Make sure this user is known to the WS broadcast
vSession.setWsActive(true);

CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();

Collection<CallRecordEntityData> vRecords = vQuerySession.getxWeeksBackIncludingSubcustomer(vSession, vSession.getDaysBack(), vSession.getLogin().getAccountId(), false);

boolean IsCustAttentionNeeded = false;
for (CallRecordEntityData record : vRecords)
{
   if (record.getIsCustAttentionNeeded())
   {
      vUrgentRecords.add(record);
   }
}

AccountEntityData vAccount = AccountCache.getInstance().get(vSession.getLogin().getAccountId());
%>

<form name="calllistform" method="POST" action="/tba/CustomerDispatch">
<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.ACTION_SHOW_CALLS%>"> 
<input type=hidden name=<%=Constants.RECORDS_TO_HANDLE%> value=""> 
<table cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
<%
if (true) //vUrgentRecords.size() > 0)
{
%>
    <tr>
        <!-- white space -->
        <td valign="top" width="20" bgcolor="FFFFFF"></td>
        <!-- account list -->
        <td valign="top" bgcolor="FFFFFF"><br>
         <!-- ################ urgent calls ################ -->
         <div id="urgentCalls"></div> 

         </td>
    </tr>
<%
}
%>         
    
    <tr>
        <!-- white space -->
        <td valign="top" width="20" bgcolor="FFFFFF"></td>
        <!-- account list -->
        <td valign="top" bgcolor="FFFFFF"><br>
         <!-- ################ buttons ################ -->
        <p><span class="bodysubtitle"> Huidig geregistreerde oproepen:
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input class="tbabutton" type=submit value="Herlaad (Vandaag)" onclick="refresh()"> 
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="archiveButton" class="tbabutton" type="submit" value="Archiveer" onclick="archive()"> 
        </span></p>

<input class="tbabutton" type=submit value="Vorige Oproepen" onclick="showPrevious()"> 
<%
if (vSession.getDaysBack() > 0)
{
out.println("<input class=\"tbabutton\" type=submit value=\"Volgende Oproepen\"  onclick=\"showNext()\">");
}
%>
  
  <!-- ################ calls ################ -->
  <br><br><table border="0" cellspacing="2" cellpadding="2">
  <%
  if (vSession.getDaysBack() > 0)
  {
	    out.println("<span class=\"bodysubsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + vSession.getDaysBack() + "&nbsp;dagen terug:</span>");
  }
  else 
  {
      out.println("<span class=\"bodysubsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Vandaag:</span>");
  }
  if (vRecords == null || vRecords.size() == 0)
  {
    out.println("<span class=\"bodysubsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er zijn geen nieuwe oproepgegevens beschikbaar.</span>");
    out.println("</table>");
  }
  else
  {
    if (vRecords.size() == 1)
      out.println("<span class=\"bodysubsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er is </span><span class=\"bodysubsubredtitle\">1</span><span class=\"bodysubsubtitle\">  oproep beschikbaar.</span>");
    else
      out.println("<span class=\"bodysubsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er zijn </span><span class=\"bodysubsubredtitle\">" + vRecords.size() + "</span><span class=\"bodysubsubtitle\"> oproepen beschikbaar.</span>");
    int vNewCnt = 0;
    long vLastLogin = vSession.getLogin().getPreviousLoginTS();
    %>
    <tr>
    <td width="30" bgcolor="FFFFFF"></td>
    <td width="65"  valign="top" class="topMenu" bgcolor="F89920">&nbsp;Datum</td>
    <td width="45"  valign="top" class="topMenu" bgcolor="F89920">&nbsp;Uur</td>
<%
if (vAccount != null && vAccount.getHasSubCustomers())
{
	%>
	<td width="200"  valign="top" class="topMenu" bgcolor="F89920">&nbsp;Medewerker</td>
	<%
}
%>
    <td width="80"  valign="top" class="topMenu" bgcolor="F89920">&nbsp;Nummer</td>
    <td width="200" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Naam</td>
    <td width="500" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Omschrijving</td>
    <td width="80"  valign="top" class="topMenu" bgcolor="F89920">&nbsp;Info</td>
    </tr>
<%
    int vRowInd = 0;
    for (CallRecordEntityData vEntry : vRecords)
    {
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
        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/info.gif\" alt=\"dubbel klik om de info te bekijken\" height=\"16\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsAgendaCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/agenda.gif\" alt=\"afspraak toegevoegd in uw agenda\" height=\"13\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsSmsCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/sms.gif\" alt=\"wij hebben een SMS bericht verstuurd ivm deze oproep\" height=\"13\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsForwardCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/telefoon.gif\" alt=\"oproep doorgeschakeld naar u\" height=\"13\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsFaxCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/fax.gif\" alt=\"binnenkomende fax voor u verwerkt\" height=\"13\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsImportantCall())
      {
    	  vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/important.gif\" alt=\"belangrijke oproep!\" height=\"13\" border=\"0\">&nbsp;");
      }
      String vInOut;
      if (vEntry.getIsIncomingCall())
        vInOut = "\"/tba/images/incall.gif\"";
      else
        vInOut = "\"/tba/images/outcall.gif\"";
%>
	<tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst" title="dubbele muisklik om de oproep te openen" onclick="updateArchiveFlag('<%=vId%>','<%=vEntry.getId()%>','<%=vRowInd%>')"
		ondblclick="changeUrl('/tba/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACTION_GOTO_RECORD_UPDATE%>&<%=Constants.RECORD_ID%>=<%=vEntry.getId()%>');">
		<td width="30" bgcolor="FFFFFF"><img src=<%=vInOut%> height="13" border="0"></td>
		<td width="65" valign="top"><%=vDate%></td>
		<td width="45" valign="top"><%=vTime%></td>
<%
if (vAccount != null && vAccount.getHasSubCustomers())
{
	if (vEntry.getAccountId() > 0)
	{
    %>
        <td width="200" valign="top">&nbsp;<%=AccountCache.getInstance().get(vEntry.getAccountId()).getFullName()%></td>
    <%
	}
	else
	{
    %>
        <td width="200" valign="top">&nbsp;</td>
    <%
	}
}
%>		<td width="80" valign="top"><%=vNumber%></td>
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
</table>
</td>
</tr>
</table>
</form>
</body>

</html>




<script>
var allArr = <%=allEntryIds.toString()%>;
var linesToDelete = new Array();
var callsSelected = 0;
document.getElementById("archiveButton").style.visibility = "hidden";

<%
if (System.getenv("TBA_MAIL_ON") != null)
{
%>
var socket = new WebSocket("wss://thebusinessassistant.be/tba/ws");
<%
}
else
{
%>
var socket = new WebSocket("ws://localhost:8080/tba/ws");
<%
}
%>

var urgentCalls = [];
<%
for (CallRecordEntityData call : vUrgentRecords)
{
   WebSocketData wsData = new WebSocketData(0, call.getId(), call.getName(), CallRecordSqlAdapter.abbrevText(call.getShortDescription()), call.getTime());
   String jsonStr = (new Gson()).toJson(wsData, WebSocketData.class);
    %>
    var json = JSON.parse('<%=jsonStr%>');
    urgentCalls.push(json);
    <%
}
%>

window.onload = function() 
{
    updateUrgentCalls();
}

socket.onopen = function() 
{ 
    socket.send('<%=Constants.WS_LOGIN + vSession.getSessionId()%>');
}

socket.onerror = function() 
{ 
    alert("Socket error received");
}

socket.onmessage = function(msg) 
{
    console.log(msg);
    var json = JSON.parse(msg.data);
    if (json.operation == <%=WebSocketData.URGENT_CALL%>)
    {
        console.log("add urgent call");
        urgentCalls.push(json);
        updateUrgentCalls();
        //window.location.reload( true );
    }
    else 
   {
    console.log("unknown operation:" + json.operation);
   }
}

function timeStamp2Txt(thenTime, nowTime)
{
    var sec_num = nowTime-thenTime;
    var minutes = Math.floor(sec_num  / 60);
    var seconds = sec_num - (minutes * 60);

    if (minutes < 10) {minutes = "0" + minutes;}
    if (seconds < 10) {seconds = "0" + seconds;}
    return minutes + ':' + seconds;
}

function updateUrgentCalls()
{
    var now = Math.floor(Date.now() / 1000);
    
    var content = "";
    if (urgentCalls.length == 0)
    {
        content += "<table><tr><td></td></tr></table>";
    }
    else
    {
        content += "<table><tr><td class=\"tdborder\" width=\"630\"><span class=\"bodysubtitle\">Oproepen die uw aandacht vragen:</span><table>";
        for (i = 0; i < urgentCalls.length; i++) 
        {
            content += "<tr class=\"tbaNotify\" onclick=\"changeUrl('/tba/CustomerDispatch?_act=_a16&_rid=" + urgentCalls[i].dbCallId + "');\">";
            content += "<td width=\"45\">" + urgentCalls[i].timeStr + "</td>";
            content += "<td width=\"155\">" + urgentCalls[i].customer + "</td>";
            content += "<td width=\"400\">" + urgentCalls[i].callText + "</td></tr>";
        }
        content += "</table></td></tr></table>";
    }
    
    
    console.log("updatePendingCalls(): " + content);

    document.getElementById('urgentCalls').innerHTML = content;
}


function selectAll()
{
  for (var i = 0; i < allArr.length; i++)
  {
    linesToDelete[i] = allArr[i].substring(2);
    var entry = document.getElementById(allArr[i]) ;
    entry.style.backgroundColor= "FFFF99";
    ++callsSelected;
  }
}

function deselectAll()
{
  for (var i = 0; i < allArr.length; i++)
  {
    linesToDelete[i] = null;
    var entry = document.getElementById(allArr[i]) ;
    entry.style.backgroundColor= "FFCC66";
    callsSelected = 0;
  }
}

function reverseSelection()
{
  callsSelected = 0;
  for (var i = 0; i < allArr.length; i++)
  {
    if (linesToDelete[i] == null)
    {
      linesToDelete[i] = allArr[i].substring(2);
      var entry = document.getElementById(allArr[i]) ;
      entry.style.backgroundColor= "FFFF99";
      ++callsSelected;
    }
    else
    {
      linesToDelete[i] = null;
      var entry = document.getElementById(allArr[i]) ;
      entry.style.backgroundColor= "FFCC66";
    }
  }
}

function updateArchiveFlag(rowid, id, rowInd)
{
  var entry = document.getElementById(rowid) ;
  if (linesToDelete[rowInd] == null)
  {
      //select entry
    linesToDelete[rowInd] = id;
    entry.style.backgroundColor= "FFFF99";
    ++callsSelected;
  }
  else
  {
      // deselect entry
    linesToDelete[rowInd] = null;
    entry.style.backgroundColor= "FFCC66";
    --callsSelected;
  }
  var row = document.getElementById('archiveButton');
  if (callsSelected > 0)
      row.style.visibility = "visible";
  else
      row.style.visibility = "hidden";
          
  //console.log("callsSelected=" + callsSelected);
}

function archive()
{
    
    var shorterArr = new Array();
    var j = 0;
    for (var i = 0; i < linesToDelete.length; i++)
      if (linesToDelete[i] != null)
        shorterArr[j++] = linesToDelete[i];
    //console.log("archive: j=" + j);
    document.calllistform.<%=Constants.RECORDS_TO_HANDLE%>.value=shorterArr.join();
    document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.ACTION_ARCHIVE_RECORDS%>";
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
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.RECORD_SHOW_PREV%>";
}

function showNext()
{
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.RECORD_SHOW_NEXT%>";
}

function refresh()
{
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.ACTION_REFRESH_CALLS%>";
}

</script>

