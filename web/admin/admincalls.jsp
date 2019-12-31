<html>
<%@ include file="adminheader.jsp" %>

<head>
<meta HTTP-EQUIV="Refresh" content="<%=Constants.REFRESH%>;URL=\tba\admin\admincalls.jsp">
<title>TheBusinessAssistant administrator pages</title>
<style>
iframe 
{
     height: 800px;
}
</style>
</head>
<%@ page 
contentType="text/html;charset=UTF-8" language="java"
	import="java.util.*,
	java.lang.*,
javax.naming.InitialContext,
be.tba.ejb.account.interfaces.*,
be.tba.ejb.pbx.interfaces.*,
be.tba.util.constants.EjbJndiNames,
be.tba.ejb.pbx.session.CallRecordSqlAdapter,
be.tba.util.constants.Constants,
be.tba.util.exceptions.AccessDeniedException,
be.tba.servlets.session.SessionManager,
be.tba.util.session.AccountCache,
be.tba.util.session.MailError"%>

<%!
private StringBuilder allEntryIds;
%>

<%
try
{
vSession.setCallingJsp(Constants.ADMIN_CALLS_JSP);
allEntryIds = new StringBuilder("[");

CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();

// filters
boolean vCallDirectionFilterOn = false;

String vCustomerFilter = (String) vSession.getCallFilter().getCustFilter();//request.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER);
if (vCustomerFilter != null)
{
  if (vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL))
    vCustomerFilter = null;
}
String vCallStateFilter = (String) vSession.getCallFilter().getStateFilter();//request.getParameter(Constants.ACCOUNT_FILTER_CALL_STATE);
if (vCallStateFilter == null)
{
  vCallStateFilter = Constants.ACCOUNT_FILTER_ALL;
}
String vCallDirectionFilter = (String) vSession.getCallFilter().getDirFilter();//request.getParameter(Constants.ACCOUNT_FILTER_CALL_DIR);
if (vCallDirectionFilter != null)
  vCallDirectionFilterOn = !vCallDirectionFilter.equals(Constants.ACCOUNT_FILTER_ALL);
else
  vCallDirectionFilter = Constants.ACCOUNT_FILTER_ALL;

Collection<CallRecordEntityData> vRecords = null;
if (vCallDirectionFilterOn)
{
  if (vCallDirectionFilter.equals(Constants.ACCOUNT_FILTER_IN))
  {
    if (vCallStateFilter.equals(Constants.ACCOUNT_FILTER_UNFINISHED))
      vRecords = vQuerySession.getInUnDocumented(vSession, vCustomerFilter);
    else if (vCallStateFilter.equals(Constants.ACCOUNT_FILTER_FINISHED)) 
      vRecords = vQuerySession.getInDocumented(vSession, vCustomerFilter);
    else  
      vRecords = vQuerySession.getIn(vSession, vCustomerFilter);
  }
  else if (vCallDirectionFilter.equals(Constants.ACCOUNT_FILTER_OUT))
  {
    if (vCallStateFilter.equals(Constants.ACCOUNT_FILTER_UNFINISHED))
      vRecords = vQuerySession.getOutUnDocumented(vSession, vCustomerFilter);
    else if (vCallStateFilter.equals(Constants.ACCOUNT_FILTER_FINISHED)) 
      vRecords = vQuerySession.getOutDocumented(vSession, vCustomerFilter);
    else  
      vRecords = vQuerySession.getOut(vSession, vCustomerFilter);
  }
}
else
{
  if (vCallStateFilter.equals(Constants.ACCOUNT_FILTER_UNFINISHED))
    vRecords = vQuerySession.getUnDocumented(vSession, vCustomerFilter);
  else if (vCallStateFilter.equals(Constants.ACCOUNT_FILTER_FINISHED)) 
    vRecords = vQuerySession.getDocumentedUnReleased(vSession, vCustomerFilter);
  else
  {
	if (vCustomerFilter == null)
      vRecords = vQuerySession.getxDaysBack(vSession, vSession.getDaysBack(), vCustomerFilter);
	else
      vRecords = vQuerySession.getDocumentedForMonth(vSession, vCustomerFilter, vSession.getMonthsBack(), vSession.getYear());
  }
}


// convert fwd number into full name

if (vCustomerFilter == null) vCustomerFilter = Constants.ACCOUNT_FILTER_ALL;

%>
<body>

<form name="calllistform" method="POST"	action="/tba/AdminDispatch">
	<input type=hidden name=<%=Constants.RECORD_ID%> value=""> 
    <input type=hidden name=<%=Constants.RECORD_SHORT_TEXT%> value=""> 
    <input type=hidden name=<%=Constants.RECORDS_TO_HANDLE%> value=""> 
	<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.GOTO_CANVAS%>"> 
<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
    <tr><td><br></td></tr>
	<tr>
		<!-- white space -->
		<td valign="top" width="20" bgcolor="FFFFFF"></td>

		<!-- account list -->
		<td valign="top" bgcolor="FFFFFF">
		<p><span class="admintitle"> Oproepenlijst: <%=vRecords.size()%> oproepen </span></p>	
	
	<table  cellspacing='20' cellpadding='0' border='0' bgcolor="FFFFFF">
	<tr>
	<td class="tdborder">
		  <table border="0" cellspacing="5" cellpadding="0">
			<tr>
				<td width="150" valign="top" class="adminsubtitle">&nbsp;Klant</td>
				<td width="10" valign="top">:</td>
				<td width="170" valign="top">
				<select name="<%=Constants.ACCOUNT_FILTER_CUSTOMER%>" onchange="submit()">
				<%

				Collection<AccountEntityData> list = AccountCache.getInstance().getCallCustomerList();
				synchronized(list) 
				{
				    for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();)
				    {
				        AccountEntityData vData = vIter.next();
				        out.println("<option value=\"" + vData.getFwdNumber() + (vCustomerFilter.equals(vData.getFwdNumber()) ? "\" selected>" : "\">") + vData.getFullName() + "</option>");
				    }
				}
				%>
				<option value="<%=Constants.NUMBER_BLOCK[0][0]%>" <%=(vCustomerFilter.equals(Constants.NUMBER_BLOCK[0][0]) ? "\"selected\"" : "")%>> <%=(vCustomerFilter.equals(Constants.NUMBER_BLOCK[0][0]) ? "\"selected\"" : "")%> <%=Constants.NUMBER_BLOCK[0][3]%></option>
				<option value="<%=Constants.NUMBER_BLOCK[1][0]%>" <%=(vCustomerFilter.equals(Constants.NUMBER_BLOCK[1][0]) ? "\"selected\"" : "")%>> <%=Constants.NUMBER_BLOCK[1][3]%></option>
				<option value="<%=Constants.ACCOUNT_FILTER_ALL%>" <%=(vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL) ? "\"selected\"" : "")%>> Alle klanten</option>
			 </select>
			 </td>
			</tr>
			<!--  
			<tr>
				<td width="150" valign="top" class="adminsubtitle">&nbsp;Oproep	status</td>
				<td width="10" valign="top">:</td>
				<%
//out.println("<td width=\"170\" valign=\"top\">");
//out.println("  <select name=\"" + Constants.ACCOUNT_FILTER_CALL_STATE + "\" onchange=\"submit()\">");
//out.println("  <option value=\"" + Constants.ACCOUNT_FILTER_UNFINISHED + (vCallStateFilter.equals(Constants.ACCOUNT_FILTER_UNFINISHED) ? "\" selected>" : "\">") + "Onvolledige");
//out.println("  <option value=\"" + Constants.ACCOUNT_FILTER_FINISHED + (vCallStateFilter.equals(Constants.ACCOUNT_FILTER_FINISHED) ? "\" selected>" : "\">") + "Volledige");
//out.println("  <option value=\"" + Constants.ACCOUNT_FILTER_ALL + (vCallStateFilter.equals(Constants.ACCOUNT_FILTER_ALL) ? "\" selected>" : "\">") + "Alle");
//out.println("  </select>");
//out.println("</td>");
%>
			</tr>
			<tr>
				<td width="150" valign="top" class="adminsubtitle">&nbsp;Inkomend/Uitgaand</td>
				<td width="10" valign="top">:</td>
				<%
//out.println("<td width=\"170\" valign=\"top\">");
//out.println("  <select name=\"" + Constants.ACCOUNT_FILTER_CALL_DIR + "\" onchange=\"submit()\">");
//out.println("  <option value=\"" + Constants.ACCOUNT_FILTER_IN + (vCallDirectionFilter.equals(Constants.ACCOUNT_FILTER_IN) ? "\" selected>" : "\">") + "InKomende");
//out.println("  <option value=\"" + Constants.ACCOUNT_FILTER_OUT + (vCallDirectionFilter.equals(Constants.ACCOUNT_FILTER_OUT) ? "\" selected>" : "\">") + "Uitgaande");
//out.println("  <option value=\"" + Constants.ACCOUNT_FILTER_ALL + (vCallDirectionFilter.equals(Constants.ACCOUNT_FILTER_ALL) ? "\" selected>" : "\">") + "In- en Uitgaande");
//out.println("  </select>");
//out.println("</td>");
%>
			</tr>-->
			</table>
			<table border="0" cellspacing="5" cellpadding="0">
            <tr>
			<td>
		        <input class="tbabutton" type=submit name=action value="Refresh" onclick="filterCalls()">
<%
if (vSession.getRole() == AccountRole.ADMIN)  
{
%>				
				<input class="tbabutton" type=submit name=action value="Verwijderen" onclick="deleteCalls()"> 
<% 
}
%>				
				<input class="tbabutton" type=submit name=action value="Toevoegen" onclick="addRecord()"> 
				<input class="tbabutton" type=submit name=action value="verzend mail" onclick="testMail()">
                <input class="tbabutton" type=submit name=action value="naar nieuwe oproepen"   onclick="toNewCalls()">
                
 <%
if (vSession.getUserId().equals("esosrv")) 
{
%>              
                <input class="tbabutton" type=submit name=action value="fix invoice accountId's" onclick="fixAccountIds()"> 
<% 
}
%>              
                
<%
if (MailError.getInstance().getError() != null) 
{
%>      
        <input class="tbabutton" type=submit name=action value="Mail Error" onclick="mailError()"><br>
<% 
}
%>      
		<br>
<%if (vCustomerFilter == null || vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL)) 
{
%>		
		<input class="tbabutton" type=submit name=action value="10 dagen vroeger" onclick="showPrevious10()"> 
		<input class="tbabutton" type=submit name=action value="1 dag vroeger" onclick="showPrevious()"> 
<%
	if (vSession.getDaysBack() > 0)
	{
	  out.println("<input class=\"tbabutton\" type=submit name=action value=\"1 dag later\"  onclick=\"showNext()\">");
	}
	if (vSession.getDaysBack() >= 10)
	{
	  out.println("<input class=\"tbabutton\" type=submit name=action value=\"10 dagen later\"  onclick=\"showNext10()\">");
	}
}
else
{
      out.println("<input class=\"tbabutton\" type=submit name=action value=\"1 maand vroeger\" onclick=\"showPrevious()\">"); 
	  out.println("<input class=\"tbabutton\" type=submit name=action value=\"1 maand later\"  onclick=\"showNext()\">");
}
%>
	
            </td>
            </tr>
        </table>
	</td>
  </tr>
</table>
	
			
<%

if (vRecords == null || vRecords.size() == 0)
{
	if (vCustomerFilter == null || vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL))
	  out.println("<br><span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er zijn geen oproepgegevens beschikbaar (" + vSession.getDaysBack() + " dagen terug).</span>");
	else
      out.println("<br><span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er zijn geen oproepgegevens beschikbaar (" + vSession.getMonthsBack() + " maanden terug).</span>");
}
else
{
  if (vSession.getDaysBack() > 0)
  {
	    if (vCustomerFilter == null || vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL))
	        out.println("<br><span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Oproepen van " + vSession.getDaysBack() + " dagen terug.</span><br>");
      else
    	    out.println("<br><span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Oproepen van " + vSession.getMonthsBack() + " maanden terug.</span><br>");
  }
  %>
  <table border="0" cellspacing="2" cellpadding="4">
                <tr>
                  <td width="20" bgcolor="FFFFFF"></td>
                  <td width="10" valign="top" class="topMenu" bgcolor="F89920"></td>
                  <td width="200" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Klant</td>
                  <td width="55"  valign="top" class="topMenu" bgcolor="F89920">&nbsp;Datum</td>
                  <td width="35"  valign="top" class="topMenu" bgcolor="F89920">&nbsp;Uur</td>
                  <td width="85"  valign="top" class="topMenu" bgcolor="F89920">&nbsp;Nummer</td>
                  <td width="230" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Naam</td>
                  <td width="500" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Omschrijving</td>
                  <td width="70" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Duur</td>
                  <td width="100"  valign="top" class="topMenu" bgcolor="F89920">&nbsp;Infos</td>
                </tr>
<%  
  int vRowInd = 0;
  AccountEntityData vAccountEntityData;

  for (Iterator<CallRecordEntityData> i = vRecords.iterator(); i.hasNext();)
  {
      CallRecordEntityData vEntry = i.next();

      String vId = "id" + vEntry.getId();
      String customerName;
      vAccountEntityData = AccountCache.getInstance().get(vEntry);
      if (vAccountEntityData == null)
      {
          customerName = "Oude klant(" + vEntry.getFwdNr() + ")";
      }
      else
      {
          customerName = vAccountEntityData.getFullName();
      }
      String vDate = vEntry.getDate();
      String vTime = vEntry.getTime();
      String vNumber = vEntry.getNumber();
      String vName = vEntry.getName();
      vName = vName == null ? "" : vName;
      String vShortDesc = (String) vEntry.getShortDescription();
      vShortDesc = vShortDesc == null ? "" : vShortDesc;
      String vLongDesc = (String) vEntry.getLongDescription();
      vLongDesc = vLongDesc == null ? "" : vLongDesc;
      String vStyleStart = "";
      String vStyleEnd = "";
      String vInOut;
      if (vEntry.getIsIncomingCall())
        vInOut = "\"/tba/images/incall.gif\"";
      else
        vInOut = "\"/tba/images/outcall.gif\"";
      String vInfoGifs = "";
      if (vLongDesc.length() > 0)
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/info.gif\" alt=\"dubbel klik om de info te bekijken\" height=\"16\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsAgendaCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/agenda.gif\" height=\"13\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsSmsCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/sms.gif\"  height=\"13\" border=\"0\">&nbsp");
      }
      if (vEntry.getIsForwardCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/telefoon.gif\"  height=\"13\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsFaxCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/fax.gif\"  height=\"13\" border=\"0\">&nbsp;");
      }
      String vImportant = "";
      if (vEntry.getIsImportantCall())
      {
        vImportant = vImportant.concat("<img src=\"/tba/images/important.gif\"  height=\"13\" border=\"0\">&nbsp;");
      }
      if (!vEntry.getIsDocumented())
      {
        vStyleStart = vStyleStart.concat("<b>");
        vStyleEnd = vStyleEnd.concat("</b>");
      }
      if (vEntry.getIsMailed())
      {
        vStyleStart = vStyleStart.concat("<i>");
        vStyleEnd = vStyleEnd.concat("</i>");
      }

  %>
	<tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst"
		onmouseover="hooverOnRow('<%=vId%>','<%=vRowInd%>')"
		onmouseout="hooverOffRow('<%=vId%>','<%=vRowInd%>')"
		onclick="updateDeleteFlag('<%=vId%>','<%=vEntry.getId()%>','<%=vRowInd%>')"
		ondblclick="changeUrl('/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACTION_GOTO_RECORD_UPDATE%>&<%=Constants.RECORD_ID%>=<%=vEntry.getId()%>');">
		<td width="20" bgcolor="FFFFFF"><img src=<%=vInOut%> height="13" border="0"></td>
		<td width="10" valign="top"><%=vImportant%></td>
		<td width="200" valign="top"><%=vStyleStart%><%=customerName%><%=vStyleEnd%></td>
		<td width="55" valign="top"><%=vStyleStart%><%=vDate%><%=vStyleEnd%></td>
		<td width="35" valign="top"><%=vStyleStart%><%=vTime%><%=vStyleEnd%></td>
		<td width="85" valign="top"><%=vStyleStart%><%=vNumber%><%=vStyleEnd%></td>
		<td width="230" valign="top"><%=vStyleStart%><%=vName%><%=vStyleEnd%></td>
		<td width="500" valign="top"><%=vStyleStart%><%=vShortDesc%><%=vStyleEnd%></td>
		<td width="70" valign="top"><%=vStyleStart%><%=vEntry.getCost()%><%=vStyleEnd%></td>
		<td width="100" valign="top"><%=vInfoGifs%></td>
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
%>
  </table>
<%  
}
allEntryIds.append("]");
%>
	</td>
    <!-- Intertel Callpark screen 
    <td valign="top" bgcolor="FFFFFF">
        <!-- <iframe src="https://pbxonline.be/tools/webconsole" width="500"></iframe>
        <iframe name="intertelFrame" src="https://pbxonline.be/index.php" width="500" onload=" frames['intertelFrame'].location.href='https://pbxonline.be/index.php?uname='+getUserName();"></iframe>
    </td> -->
  </tr>
</table>
</form>
<% 

if (vRecords != null && vRecords.size() > 0)
{
  out.println("<br><br><input class=\"tbabutton\" type=submit name=action value=\"Vorige Oproepen\"  onclick=\"showPrevious()\">");
  if (vSession.getDaysBack() > 0)
    out.println("&nbsp;&nbsp;&nbsp;<input class=\"tbabutton\" type=submit name=action value=\"Volgende Oproepen\"  onclick=\"showNext()\">");
}

}
catch (Exception e)
{
    e.printStackTrace();
}

%>

<script type="text/javascript">

var linesToDelete = new Array();

window.name="callswindow"; 


function hooverOnRow(id, rowInd)
{
  var entry = document.getElementById(id) ;
  if (linesToDelete[rowInd] == null)
    entry.style.backgroundColor= "FFFF99";
  else
    entry.style.backgroundColor= "FF9966";
}

function hooverOffRow(id, rowInd)
{
  var entry = document.getElementById(id) ;
  if (linesToDelete[rowInd] == null)
    entry.style.backgroundColor= "FFCC66";
  else
    entry.style.backgroundColor= "FF9966";
}

function updateDeleteFlag(rowid, id, rowInd)
{
  var entry = document.getElementById(rowid) ;
  if (linesToDelete[rowInd] == null)
  {
    linesToDelete[rowInd] = id;
    entry.style.backgroundColor= "FF9966";
  }
  else
  {
    linesToDelete[rowInd] = null;
    entry.style.backgroundColor= "FFFF99";
  }
}

function deleteCalls()
{
  var shorterArr = new Array();
  var j = 0;
  for (var i = 0; i < linesToDelete.length; i++)
    if (linesToDelete[i] != null)
      shorterArr[j++] = linesToDelete[i];
  document.calllistform.<%=Constants.RECORDS_TO_HANDLE%>.value=shorterArr.join();
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.RECORD_DELETE%>";

  document.<%=Constants.RECORDS_TO_HANDLE%>.value=shorterArr.join();
}

function selectAll()
{
  var allArr = <%=allEntryIds.toString()%>;
  for (var i = 0; i < allArr.length; i++) // length -1 because we have entered an extra ',' at the end
  {
    linesToDelete[i] = allArr[i].substring(2);
    var entry = document.getElementById(allArr[i]) ;
    entry.style.backgroundColor= "FF9966";
  }
}

function deselectAll()
{
  var allArr = <%=allEntryIds.toString()%>;
  for (var i = 0; i < allArr.length; i++) // length -1 because we have entered an extra ',' at the end
  {
    linesToDelete[i] = null;
    var entry = document.getElementById(allArr[i]) ;
    entry.style.backgroundColor= "FFCC66";
  }
}

function reverseSelection()
{
  var allArr = <%=allEntryIds.toString()%>;
  for (var i = 0; i < allArr.length; i++) // length -1 because we have entered an extra ',' at the end
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


function filterCalls()
{
  document.calllistform.<%=Constants.RECORDS_TO_HANDLE%>.value="";
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_CANVAS%>";
}

function addRecord()
{
  document.calllistform.<%=Constants.RECORDS_TO_HANDLE%>.value="";
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_ADD_RECORD%>";
}

function showPrevious10()
{
  document.calllistform.<%=Constants.RECORDS_TO_HANDLE%>.value="";
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.RECORD_SHOW_PREV_10%>";
}

function showPrevious()
{
  document.calllistform.<%=Constants.RECORDS_TO_HANDLE%>.value="";
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.RECORD_SHOW_PREV%>";
}

function showNext()
{
  document.calllistform.<%=Constants.RECORDS_TO_HANDLE%>.value="";
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.RECORD_SHOW_NEXT%>";
}

function showNext10()
{
  document.calllistform.<%=Constants.RECORDS_TO_HANDLE%>.value="";
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.RECORD_SHOW_NEXT_10%>";
}

function changeUrl(newURL) 
{
  location=newURL;
}

function testMail()
{
  document.calllistform.<%=Constants.RECORDS_TO_HANDLE%>.value="";
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.MAIL_IT%>";
}

function fixAccountIds()
{
	document.calllistform.<%=Constants.RECORDS_TO_HANDLE%>.value="";
	document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.FIX_ACCOUNT_IDS%>";
}

function mailError()
{
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.SHOW_MAIL_ERROR%>";
}

var newwindow = '';

function newCall()
{
	document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.NEW_CALL%>";
}

function toNewCalls()
{
    document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_CANVAS%>";
	
}
</script>

</body>

</html>

