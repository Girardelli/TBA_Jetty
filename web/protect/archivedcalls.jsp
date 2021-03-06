<html>
<%@ include file="protheader.jsp" %>

<head>
</head>

	<%@ page
		import="
java.util.*,
be.tba.sqldata.*,
be.tba.sqladapters.*,
be.tba.util.constants.*,
be.tba.util.exceptions.AccessDeniedException,
be.tba.session.SessionManager,
be.tba.sqldata.AccountCache,
be.tba.util.data.*"%>
<%
StringBuilder allEntryIds = new StringBuilder("[");
try
{
%>
<form name="calllistform" method="POST" action="/tba/CustomerDispatch">
<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.ACTION_ARCHIVED_CALLS%>"> 
<table cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
	<tr>
		<!-- white space -->
		<td valign="top" width="20" bgcolor="FFFFFF"></td>

		<!-- account list -->
		<td valign="top" bgcolor="FFFFFF"><br>
        <p><span class="bodytitle"> Gearchiveerde oproepen:
        </span></p>
<%
   if (vSession == null)
    throw new AccessDeniedException("U bent niet aangemeld.");
  vSession.setCallingJsp(Constants.CLIENT_ARCHIVED_CALLS_JSP);  

  CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();

  Collection<CallRecordEntityData> vRecords = vQuerySession.getxWeeksBackIncludingSubcustomer(vSession, vSession.getDaysBack(), vSession.getLogin().getAccountId(), true);
  
  AccountEntityData vAccount = AccountCache.getInstance().get(vSession.getLogin().getAccountId());
%>
<input class="tbabutton" type=submit name=action value="Vorige Oproepen" onclick="showPrevious()"> 
<%
if (vSession.getDaysBack() > 0)
{
out.println("<input class=\"tbabutton\" type=submit name=action value=\"Volgende Oproepen\"  onclick=\"showNext()\">");
}

  
  out.println("<br><br><table border=\"0\" cellspacing=\"2\" cellpadding=\"2\">");
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
    out.println("<span class=\"bodysubsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er zijn geen gearchiveerde oproepgegevens beschikbaar voor deze periode.</span>");
    out.println("</table>");
  }
  else
  {
    if (vRecords.size() == 1)
      out.println("<span class=\"bodysubsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er is </span><span class=\"bodysubsubredtitle\">1</span><span class=\"bodysubsubtitle\">  gearchiveerde oproep beschikbaar voor deze periode.</span>");
    else
      out.println("<span class=\"bodysubsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er zijn </span><span class=\"bodysubsubredtitle\">" + vRecords.size() + "</span><span class=\"bodysubsubtitle\"> gearchiveerde oproepen beschikbaar voor deze periode.</span>");
    int vNewCnt = 0;
    long vLastLogin = vSession.getLogin().getPreviousLoginTS();
    %>
    <br><br>
    <tr>
    <td width="30" bgcolor="FFFFFF"></td>
    <td width="65"  valign="top" class="topMenu" bgcolor="488FCD">&nbsp;Datum</td>
    <td width="45"  valign="top" class="topMenu" bgcolor="488FCD">&nbsp;Uur</td>
<%
if (vAccount != null && vAccount.getHasSubCustomers())
{
	%>
	<td width="200"  valign="top" class="topMenu" bgcolor="488FCD">&nbsp;Medewerker</td>
	<%
}
%>
    <td width="80"  valign="top" class="topMenu" bgcolor="488FCD">&nbsp;Nummer</td>
    <td width="200" valign="top" class="topMenu" bgcolor="488FCD">&nbsp;Naam</td>
    <td width="500" valign="top" class="topMenu" bgcolor="488FCD">&nbsp;Omschrijving</td>
    <td width="80"  valign="top" class="topMenu" bgcolor="488FCD">&nbsp;Info</td>
    </tr>
<%
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
	<tr bgcolor="9fc5e6" id=<%=vId%> class="bodytekst"
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

