<html>
<%@ include file="adminheader.jsp" %>

<head>
<meta HTTP-EQUIV="Refresh" content="<%=Constants.REFRESH%>;URL=\tba\admin\admincalls.jsp">
<title>TheBusinessAssistant administrator pages</title>
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
be.tba.util.exceptions.InvalidValueException,
be.tba.servlets.session.SessionManager,
be.tba.util.session.AccountCache,
be.tba.util.session.MailError"%>

<%!
private StringBuilder allEntryIds;
%>

<%
StringBuffer modalScriptStrBuffer = new StringBuffer("\r\n//#######  My Modal scripts ######\r\n\r\n");
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
<p><span class="admintitle"> Oproepenlijst: <%=vRecords.size()%> oproepen </span></p>
<form name="calllistform" method="POST"	action="/tba/AdminDispatch">
	<input type=hidden name=<%=Constants.RECORD_ID%> value=""> 
    <input type=hidden name=<%=Constants.RECORD_SHORT_TEXT%> value=""> 
    <input type=hidden name=<%=Constants.RECORD_TO_DELETE%> value=""> 
	<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.GOTO_RECORD_ADMIN%>"> 
<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
	<tr>
		<!-- white space -->
		<td valign="top" width="20" bgcolor="FFFFFF"></td>

		<!-- account list -->
		<td valign="top" bgcolor="FFFFFF">
			
	
	<table  cellspacing='20' cellpadding='0' border='0' bgcolor="FFFFFF">
	<tr>
	<td class="tdborder">
		  <table border="0" cellspacing="5" cellpadding="0">
			<tr>
				<td width="150" valign="top" class="adminsubtitle">&nbsp;Klant</td>
				<td width="10" valign="top">:</td>
				<%
out.println("<td width=\"170\" valign=\"top\"><select name=\"" + Constants.ACCOUNT_FILTER_CUSTOMER + "\" onchange=\"submit()\">");

				Collection<AccountEntityData> list = AccountCache.getInstance().getCustomerList();
				synchronized(list) 
				{
				    for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();)
				    {
				        AccountEntityData vData = vIter.next();
				        out.println("<option value=\"" + vData.getFwdNumber() + (vCustomerFilter.equals(vData.getFwdNumber()) ? "\" selected>" : "\">") + vData.getFullName());
				    }
				}
out.println("<option value=\"" + Constants.NUMBER_BLOCK[0][0] + (vCustomerFilter.equals(Constants.NUMBER_BLOCK[0][0]) ? "\" selected>" : "\">") + Constants.NUMBER_BLOCK[0][3]);
out.println("<option value=\"" + Constants.NUMBER_BLOCK[1][0] + (vCustomerFilter.equals(Constants.NUMBER_BLOCK[1][0]) ? "\" selected>" : "\">") + Constants.NUMBER_BLOCK[1][3]);
out.println("<option value=\"" + Constants.ACCOUNT_FILTER_ALL + (vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL) ? "\" selected>" : "\">") + "Alle klanten");
out.println("</select></td>");
%>
			</tr>
			<tr>
				<td width="150" valign="top" class="adminsubtitle">&nbsp;Oproep	status</td>
				<td width="10" valign="top">:</td>
				<%
out.println("<td width=\"170\" valign=\"top\">");
out.println("  <select name=\"" + Constants.ACCOUNT_FILTER_CALL_STATE + "\" onchange=\"submit()\">");
out.println("  <option value=\"" + Constants.ACCOUNT_FILTER_UNFINISHED + (vCallStateFilter.equals(Constants.ACCOUNT_FILTER_UNFINISHED) ? "\" selected>" : "\">") + "Onvolledige");
out.println("  <option value=\"" + Constants.ACCOUNT_FILTER_FINISHED + (vCallStateFilter.equals(Constants.ACCOUNT_FILTER_FINISHED) ? "\" selected>" : "\">") + "Volledige");
out.println("  <option value=\"" + Constants.ACCOUNT_FILTER_ALL + (vCallStateFilter.equals(Constants.ACCOUNT_FILTER_ALL) ? "\" selected>" : "\">") + "Alle");
out.println("  </select>");
out.println("</td>");
%>
			</tr>
			<tr>
				<td width="150" valign="top" class="adminsubtitle">&nbsp;Inkomend/Uitgaand</td>
				<td width="10" valign="top">:</td>
				<%
out.println("<td width=\"170\" valign=\"top\">");
out.println("  <select name=\"" + Constants.ACCOUNT_FILTER_CALL_DIR + "\" onchange=\"submit()\">");
out.println("  <option value=\"" + Constants.ACCOUNT_FILTER_IN + (vCallDirectionFilter.equals(Constants.ACCOUNT_FILTER_IN) ? "\" selected>" : "\">") + "InKomende");
out.println("  <option value=\"" + Constants.ACCOUNT_FILTER_OUT + (vCallDirectionFilter.equals(Constants.ACCOUNT_FILTER_OUT) ? "\" selected>" : "\">") + "Uitgaande");
out.println("  <option value=\"" + Constants.ACCOUNT_FILTER_ALL + (vCallDirectionFilter.equals(Constants.ACCOUNT_FILTER_ALL) ? "\" selected>" : "\">") + "In- en Uitgaande");
out.println("  </select>");
out.println("</td>");
%>
			</tr>
			</table>
			<table border="0" cellspacing="5" cellpadding="0">
            <tr>
			<td>
		        <input class="tbabutton" type=submit name=action value="Oproep" onclick="newCall()">
		        &nbsp;&nbsp;&nbsp;&nbsp;
		        <input class="tbabutton" type=submit name=action value="Refresh" onclick="filterCalls()">
				<input class="tbabutton" type=submit name=action value="Verwijderen" onclick="deleteCalls()"> 
				<input class="tbabutton" type=submit name=action value="Toevoegen" onclick="addRecord()"> 
				<input class="tbabutton" type=submit name=action value="verzend mail"	onclick="testMail()">
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
	<td width="10">
    </td>
<%
Collection<CallRecordEntityData> chatRecords = vQuerySession.getChatRecords(vSession);

%>    
    <td class="tdborder">
      <table>
        <tr>
          <td class="tdborder" width="300px"><span class="admintitle">Mijn chats</span>
            <br><br>
<%
int cnt = 0; 
StringBuffer modalStrBuffer = new StringBuffer("<!-- \r\n#######  My Modals ######-->\r\n\r\n");

if (!chatRecords.isEmpty())
{
    for (Iterator<CallRecordEntityData> i = chatRecords.iterator(); i.hasNext();)
	 {
	     CallRecordEntityData vEntry = i.next();
	     
	     if (vEntry.getDoneBy().equalsIgnoreCase(vSession.getUserId()))
	     {
	    	 //System.out.println(vEntry.getDoneBy() + " == " + vSession.getUserId());
	    	 ++cnt;
	    	 String modalBtnId = "modalBtn" + cnt;
             String modalId = "modal" + cnt;
             String spanId = "spanModal" + cnt;
             String spanVar = "spanVar" + cnt;
             String modalText = "modalText" + cnt;
             out.println("<span class=\"tbaChat\" id=\"" + modalBtnId + "\" onclick=\"openModal('" + modalId + "');\"  >" + vEntry.getDate() + ", " + vEntry.getTime() + "&nbsp;&nbsp;" + AccountCache.getInstance().get(vEntry.getFwdNr()).getFullName() + "</span><br>");
             // fill the modal
	    	 modalStrBuffer.append("<div id=\"" + modalId + "\" class=\"tbaModal\">\r\n");
             modalStrBuffer.append("<div class=\"modal-content\">\r\n");
	         modalStrBuffer.append("<span id=\"" + spanId + "\" class=\"close\">&times;</span>\r\n");
	         modalStrBuffer.append("<p>" + vEntry.getDate() + ", " + vEntry.getTime() + "&nbsp;&nbsp;<b>\r\n");
             modalStrBuffer.append(AccountCache.getInstance().get(vEntry.getFwdNr()).getFullName() + "</b></p>\r\n");
             modalStrBuffer.append("<div class=\"old-modal-content\">" + vEntry.getShortDescription() + "<br><br>\r\n");
             modalStrBuffer.append("<textarea class=\"tbatextarea\" id=\"" + modalText + "\"></textarea></div>\r\n");
	    	 modalStrBuffer.append("<p align=\"right\">\r\n");
	    	 //modalStrBuffer.append("<button class=\"tbabutton\" id=\"myBewaar\">Cancel</button>\r\n");
	    	 //modalStrBuffer.append("<button class=\"tbabutton\" id=\"myCancel\">Bewaar</button>\r\n");
	    	 modalStrBuffer.append("<input class=\"tbabutton\" type=submit name=action value=\"Bewaar\" onclick=\"updateModalText('" + vEntry.getId() + "', '" + modalText + "')\">");
	    	 modalStrBuffer.append("</p></div></div>\r\n\r\n");
	    	 
	    	// fill the script
	    	 modalScriptStrBuffer.append("var " + spanVar + "= document.getElementById(\"" + spanId + "\");\r\n");
             modalScriptStrBuffer.append(spanVar + ".onclick = function() {modal.style.display = \"none\"; }\r\n\r\n");
	     }
	 }
	if (cnt == 0)
	{
		out.println("<p>Geen Chats</p>");
	}
	
}

%>            
          </td>
          <td width="10">
          </td>
          <td class="tdborder" width="300px"><span class="admintitle">Andere chats</span>
            <br><br>
<%
cnt = 0;
if (!chatRecords.isEmpty())
{
    for (Iterator<CallRecordEntityData> i = chatRecords.iterator(); i.hasNext();)
     {
         CallRecordEntityData vEntry = i.next();
         if (!vEntry.getDoneBy().equalsIgnoreCase(vSession.getUserId()))
         {
        	 //System.out.println(vEntry.getDoneBy() + " == " + vSession.getUserId());
             ++cnt;
             String modalBtnId = "modalBtn_" + cnt;
             String modalId = "modal_" + cnt;
             String spanId = "spanModal_" + cnt;
             String spanVar = "spanVar_" + cnt;
             String modalText = "modalText_" + cnt;
             out.println("<span class=\"tbaChat\" id=\"" + modalBtnId + "\" onclick=\"openModal('" + modalId + "');\"  >" + vEntry.getDate() + ", " + vEntry.getTime() + "&nbsp;&nbsp;" + AccountCache.getInstance().get(vEntry.getFwdNr()).getFullName() + "</span><br>");
             // fill the modal
             modalStrBuffer.append("<div id=\"" + modalId + "\" class=\"tbaModal\">\r\n");
             modalStrBuffer.append("<div class=\"modal-content\">\r\n");
             modalStrBuffer.append("<span id=\"" + spanId + "\" class=\"close\">&times;</span>\r\n");
             modalStrBuffer.append("<p>" + vEntry.getDate() + ", " + vEntry.getTime() + "&nbsp;&nbsp;<b>\r\n");
             modalStrBuffer.append(AccountCache.getInstance().get(vEntry.getFwdNr()).getFullName() + "</b></p>\r\n");
             modalStrBuffer.append("<div class=\"old-modal-content\">" + vEntry.getShortDescription() + "<br><br>\r\n");
             modalStrBuffer.append("<textarea  class=\"tbatextarea\" id=\"" + modalText + "\"></textarea></div>\r\n");
             modalStrBuffer.append("<p align=\"right\">\r\n");
             modalStrBuffer.append("<input class=\"tbabutton\" type=submit name=action value=\"Bewaar\" onclick=\"updateModalText('" + vEntry.getId() + "', '" + modalText + "')\">");
             modalStrBuffer.append("</p></div></div>\r\n");
             // fill the script
             modalScriptStrBuffer.append("var " + spanVar + "= document.getElementById(\"" + spanId + "\");\r\n");
             modalScriptStrBuffer.append(spanVar + ".onclick = function() {modal.style.display = \"none\"; }\r\n\r\n");
         }
     }
    if (cnt == 0)
    {
        out.println("<p>Geen Chats</p>");
    }
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
	  out.println("<br><br><span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er zijn geen oproepgegevens beschikbaar (" + vSession.getDaysBack() + " dagen terug).</span>");
	else
      out.println("<br><br><span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er zijn geen oproepgegevens beschikbaar (" + vSession.getMonthsBack() + " maanden terug).</span>");
}
else
{
  if (vSession.getDaysBack() > 0)
  {
	    if (vCustomerFilter == null || vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL))
	        out.println("<br><br><span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Oproepen van " + vSession.getDaysBack() + " dagen terug.</span><br>");
      else
    	    out.println("<br><br><span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Oproepen van " + vSession.getMonthsBack() + " maanden terug.</span><br>");
  }
  out.println("<table border=\"0\" cellspacing=\"2\" cellpadding=\"4\">");
  out.println("              <br><tr>");
  out.println("                <td width=\"20\" bgcolor=\"FFFFFF\"></td>");
  out.println("                <td width=\"10\" valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\"></td>");
  out.println("                <td width=\"200\" valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Klant</td>");
  out.println("                <td width=\"55\"  valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Datum</td>");
  out.println("                <td width=\"35\"  valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Uur</td>");
  out.println("                <td width=\"85\"  valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Nummer</td>");
  out.println("                <td width=\"230\" valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Naam</td>");
  out.println("                <td width=\"500\" valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Omschrijving</td>");
  out.println("                <td width=\"70\" valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Duur</td>");
  out.println("                <td width=\"100\"  valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Infos</td>");
  out.println("              </tr>");

  int vRowInd = 0;
  AccountEntityData vAccountEntityData;

  for (Iterator<CallRecordEntityData> i = vRecords.iterator(); i.hasNext();)
  {
      CallRecordEntityData vEntry = i.next();

      String vId = "id" + vEntry.getId();
      String customerName;
      vAccountEntityData = AccountCache.getInstance().get(vEntry.getFwdNr());
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
      if (vEntry.getIs3W_call())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/3w.gif\"  height=\"13\" border=\"0\">&nbsp;");
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
		ondblclick="changeUrl('/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.RECORD_UPDATE%>&<%=Constants.RECORD_ID%>=<%=vEntry.getId()%>');">
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
}
allEntryIds.append("]");

out.println("</table>");
if (vRecords != null && vRecords.size() > 0)
{
  out.println("<br><br><input class=\"tbabutton\" type=submit name=action value=\"Vorige Oproepen\"  onclick=\"showPrevious()\">");
  if (vSession.getDaysBack() > 0)
    out.println("&nbsp;&nbsp;&nbsp;<input class=\"tbabutton\" type=submit name=action value=\"Volgende Oproepen\"  onclick=\"showNext()\">");
}
out.println(modalStrBuffer.toString());


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
  document.calllistform.<%=Constants.RECORD_TO_DELETE%>.value=shorterArr.join();
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.RECORD_DELETE%>";

  document.<%=Constants.RECORD_TO_DELETE%>.value=shorterArr.join();
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
  document.calllistform.<%=Constants.RECORD_TO_DELETE%>.value="";
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_RECORD_ADMIN%>";
}

function addRecord()
{
  document.calllistform.<%=Constants.RECORD_TO_DELETE%>.value="";
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_ADD_RECORD%>";
}

function showPrevious10()
{
  document.calllistform.<%=Constants.RECORD_TO_DELETE%>.value="";
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.RECORD_SHOW_PREV_10%>";
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

function showNext10()
{
  document.calllistform.<%=Constants.RECORD_TO_DELETE%>.value="";
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.RECORD_SHOW_NEXT_10%>";
}

function changeUrl(newURL) 
{
  location=newURL;
}

function testMail()
{
  document.calllistform.<%=Constants.RECORD_TO_DELETE%>.value="";
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.MAIL_IT%>";
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

function updateModalText(id, modalText)
{
	document.calllistform.<%=Constants.RECORD_ID%>.value=id;
	document.calllistform.<%=Constants.RECORD_SHORT_TEXT%>.value=document.getElementById(modalText).value;
	document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.UPDATE_SHORT_TEXT%>";
	//alert(document.calllistform.<%=Constants.RECORD_SHORT_TEXT%>.value);
}

//Get the modal
var modal;

function openModal(modalId)
{
    modal = document.getElementById(modalId);
    modal.style.display = "block";
}

//When the user clicks the button, open the modal 
//btn.onclick = function() {
//modal.style.display = "block";
//}

//When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
if (event.target == modal) {
modal.style.display = "none";
}
}

<%
out.println(modalScriptStrBuffer.toString());
%>
</script>

</body>

</html>

