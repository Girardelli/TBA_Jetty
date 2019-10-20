<html>
<%@ include file="adminheader.jsp"%>

<head>
<meta HTTP-EQUIV="Refresh" content="<%=Constants.REFRESH%>;URL=\tba\admin\canvas.jsp">
<title>TheBusinessAssistant administrator pages</title>
<style>
</style>

</head>
<%@ page contentType="text/html;charset=UTF-8" language="java"
   import="java.util.*,
	java.lang.*,
javax.naming.InitialContext,
be.tba.ejb.account.interfaces.*,
be.tba.ejb.pbx.interfaces.*,
be.tba.util.constants.EjbJndiNames,
be.tba.ejb.pbx.session.CallRecordSqlAdapter,
be.tba.util.constants.Constants,
be.tba.websockets.WebSocketData,
be.tba.util.exceptions.AccessDeniedException,
be.tba.util.exceptions.InvalidValueException,
be.tba.servlets.session.SessionManager,
be.tba.servlets.helper.IntertelCallManager,
be.tba.util.session.AccountCache,
be.tba.util.session.MailError,
be.tba.util.timer.UrlCheckTimerTask"%>

<%!private StringBuilder allEntryIds;%>

<%
	StringBuffer modalScriptStrBuffer = new StringBuffer("\r\n//#######  My Modal scripts ######\r\n\r\n");
	try {
		vSession.setCallingJsp(Constants.CANVAS_JSP);
		// this is the websocket page. Make sure this user is known to the WS broadcast
		vSession.setWsActive(true);

		String vCustomerFilter = vSession.getCallFilter().getCustFilter();
		if (vCustomerFilter == null) {
			vSession.getCallFilter().setCustFilter(Constants.ACCOUNT_FILTER_ALL);
			vCustomerFilter = Constants.ACCOUNT_FILTER_ALL;
		}

		CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();
		//Collection<CallRecordEntityData> vRecords = vRecords = vQuerySession.getUnDocumented(vSession, null);
		Collection<CallRecordEntityData> vRecords = null;
		if (vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL)) {
			vRecords = vQuerySession.getxDaysBack(vSession, vSession.getDaysBack(), vCustomerFilter);
		} else {
			vRecords = vQuerySession.getDocumentedForMonth(vSession, vCustomerFilter, vSession.getMonthsBack(),
					vSession.getYear());
		}
%>
<body>
   <audio id="ringRing">
      <source src="audio/Ringing_Phone.wav" type="audio/wav">
      <source src="audio/Ringing_Phone.mp3" type="audio/mp3">
   </audio>
   <form name="calllistform" method="POST" action="/tba/AdminDispatch">
      <input type=hidden name=<%=Constants.RECORD_ID%> value=""> <input type=hidden name=<%=Constants.RECORD_SHORT_TEXT%> value=""> <input type=hidden name=<%=Constants.RECORD_TO_DELETE%> value=""> <input type=hidden name=<%=Constants.SRV_ACTION%>
         value="<%=Constants.GOTO_CANVAS%>">
      <table>
         <tr>
            <td><br></td>
         </tr>
         <tr>
            <!-- white space -->
            <td valign="top" width="20" bgcolor="FFFFFF"></td>

            <!-- account list -->
            <td valign="top" bgcolor="FFFFFF">
               <%
               	if (!UrlCheckTimerTask.getIsWebsiteUp()) {
               %> <br>
               <p>
                  <span class="mtbtitle"> Telenet ligt plat!! Klanten kunnen niet aan de website.</span>
               </p> <%
 	}
 		Collection<String> pendingCalls = IntertelCallManager.getInstance().getPendingCallList();
 		String pendingCallsMsg = "";
 		for (Iterator<String> i = pendingCalls.iterator(); i.hasNext();) {
 			String call = i.next();
 			pendingCallsMsg.concat(call + "\r\n");
 		}
 %>
               <table>
                  <tr>
                     <td valign='bottom'>
                        <!-- ################ pending calls ################ -->
                        <div id="pendingCalls"></div> <br> <!-- ################ buttons ################ -->
                        <table class="tdborder" width=100%>
                           <tr>
                              <td>
                                 <table>
                                    <tr>


                                       <td width="50" valign="top" class="adminsubtitle">&nbsp;Klant</td>
                                       <td width="10" valign="top">:</td>
                                       <td valign="top"><select name="<%=Constants.ACCOUNT_FILTER_CUSTOMER%>" onchange="submit()">
                                             <%
                                             	Collection<AccountEntityData> list = AccountCache.getInstance().getCallCustomerList();
                                             		synchronized (list) {
                                             			for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();) {
                                             				AccountEntityData vData = vIter.next();
                                             %>
                                             <option value="<%=vData.getFwdNumber()%>" <%=(vCustomerFilter.equals(vData.getFwdNumber()) ? " selected" : "")%>><%=vData.getFullName()%></option>
                                             <%
                                             	}
                                             		}
                                             %>
                                             <option value="<%=Constants.NUMBER_BLOCK[0][0]%>" <%=(vCustomerFilter.equals(Constants.NUMBER_BLOCK[0][0]) ? " selected" : "")%>><%=Constants.NUMBER_BLOCK[0][3]%></option>
                                             <option value="<%=Constants.NUMBER_BLOCK[1][0]%>" <%=(vCustomerFilter.equals(Constants.NUMBER_BLOCK[1][0]) ? " selected" : "")%>><%=Constants.NUMBER_BLOCK[1][3]%></option>
                                             <option value="<%=Constants.ACCOUNT_FILTER_ALL%>" <%=(vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL) ? " selected" : "")%>>Alle klanten</option>
                                       </select></td>
                                    </tr>
                                 </table>
                              </td>
                           </tr>
                           <tr>
                              <td><input class="tbabuttonorange" type=submit name=action value="Oproep" onclick="newCall()"> &nbsp;&nbsp;&nbsp;&nbsp; <input class="tbabutton" type=submit name=action value="Refresh"> <%
 	if (vSession.getRole() == AccountRole.ADMIN) {
 %> <input class="tbabutton" type=submit name=action value="Verwijderen" onclick="deleteCalls()"> <%
 	}
 %> <input class="tbabutton" type=submit name=action value="Toevoegen" onclick="addRecord()"> <input class="tbabutton" type=submit name=action value="verzend mail" onclick="testMail()"> <%
 	if (vSession.getUserId().equals("esosrv")) {
 %> <input class="tbabutton" type=submit name=action value="fix invoice accountId's" onclick="fixAccountIds()"> <%
 	}
 %></td>
                           </tr>
                           <tr>
                              <td>
                                 <%
                                 	if (vCustomerFilter == null || vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL)) {
                                 %> <input class="tbabutton" type=submit name=action value="10 dagen vroeger" onclick="showPrevious10()"> <input class="tbabutton" type=submit name=action value="1 dag vroeger" onclick="showPrevious()"> <%
 	if (vSession.getDaysBack() > 0) {
 %> <input class="tbabutton" type=submit name=action value="1 dag later" onclick="showNext()"> <%
 	}
 			if (vSession.getDaysBack() >= 10) {
 %> <input class="tbabutton" type=submit name=action value="10 dagen later" onclick="showNext10()"> <%
 	}
 		} else {
 %> <input class="tbabutton" type=submit name=action value="1 maand vroeger" onclick="showPrevious()"> <input class="tbabutton" type=submit name=action value="1 maand later" onclick="showNext()"> <%
 	}
 %>
                              </td>
                           </tr>
                           <%
                           	if (MailError.getInstance().getError() != null) {
                           %>
                           <tr>
                              <td><input class="tbabutton" type=submit name=action value="Mail Error" onclick="mailError()"></td>
                           </tr>
                           <%
                           	}
                           %>
                        </table>
                     </td>
                     <td width="10"></td>
                     <td class="tdborder">
                        <!-- ################ chats ################ -->
                        <table>
                           <tr>
                              <td class="tdborder" width="300px"><span class="admintitle">Mijn chats</span> <br> <br> <%
 	Collection<CallRecordEntityData> chatRecords = vQuerySession.getChatRecords(vSession);
 		int cnt = 0;
 		StringBuffer modalStrBuffer = new StringBuffer("<!-- \r\n#######  My Modals ######-->\r\n\r\n");

 		if (!chatRecords.isEmpty()) {
 			for (Iterator<CallRecordEntityData> i = chatRecords.iterator(); i.hasNext();) {
 				CallRecordEntityData vEntry = i.next();

 				if (vEntry.getDoneBy().equalsIgnoreCase(vSession.getUserId())) {
 					//System.out.println(vEntry.getDoneBy() + " == " + vSession.getUserId());
 					++cnt;
 					String modalBtnId = "modalBtn" + cnt;
 					String modalId = "modal" + cnt;
 					String spanId = "spanModal" + cnt;
 					String spanVar = "spanVar" + cnt;
 					String modalText = "modalText" + cnt;
 					out.println("<span class=\"tbaChat\" id=\"" + modalBtnId + "\" onclick=\"openModal('"
 							+ modalId + "');\"  >" + vEntry.getDate() + ", " + vEntry.getTime() + "&nbsp;&nbsp;"
 							+ AccountCache.getInstance().get(vEntry).getFullName() + "</span><br>");
 					// fill the modal
 					modalStrBuffer.append("<div id=\"" + modalId + "\" class=\"tbaModal\">\r\n");
 					modalStrBuffer.append("<div class=\"modal-content\">\r\n");
 					modalStrBuffer.append("<span id=\"" + spanId + "\" class=\"close\">&times;</span>\r\n");
 					modalStrBuffer.append("<p><b>Naam:</b>&nbsp;" + vEntry.getName() + "\r\n");
 					modalStrBuffer.append("<p><b>Nummer:</b>&nbsp;" + vEntry.getNumber() + "\r\n");
 					modalStrBuffer
 							.append("<p>" + vEntry.getDate() + ", " + vEntry.getTime() + "&nbsp;&nbsp;<b>\r\n");
 					modalStrBuffer
 							.append(AccountCache.getInstance().get(vEntry).getFullName() + "</b></p>\r\n");
 					modalStrBuffer.append("<div class=\"old-modal-content\">" + vEntry.getShortDescription()
 							+ "<br><br>\r\n");
 					modalStrBuffer.append(
 							"<textarea class=\"tbatextarea\" id=\"" + modalText + "\"></textarea></div>\r\n");
 					modalStrBuffer.append("<p align=\"right\">\r\n");
 					//modalStrBuffer.append("<button class=\"tbabutton\" id=\"myBewaar\">Cancel</button>\r\n");
 					//modalStrBuffer.append("<button class=\"tbabutton\" id=\"myCancel\">Bewaar</button>\r\n");
 					modalStrBuffer.append(
 							"<input class=\"tbabutton\" type=submit name=action value=\"Bewaar\" onclick=\"updateModalText('"
 									+ vEntry.getId() + "', '" + modalText + "')\">");
 					modalStrBuffer.append("</p></div></div>\r\n\r\n");

 					// fill the script
 					modalScriptStrBuffer
 							.append("var " + spanVar + "= document.getElementById(\"" + spanId + "\");\r\n");
 					modalScriptStrBuffer.append(
 							spanVar + ".onclick = function() {modal.style.display = \"none\"; }\r\n\r\n");
 				}
 			}
 			if (cnt == 0) {
 				out.println("<p>Geen Chats</p>");
 			}
		}
 %></td>
                           </tr>
                           <tr>
                              <td class="tdborder" width="300px"><span class="admintitle">Andere chats</span> <br> <br> <%
 	cnt = 0;
 		if (!chatRecords.isEmpty()) {
 			for (Iterator<CallRecordEntityData> i = chatRecords.iterator(); i.hasNext();) {
 				CallRecordEntityData vEntry = i.next();
 				if (!vEntry.getDoneBy().equalsIgnoreCase(vSession.getUserId())) {
 					//System.out.println(vEntry.getDoneBy() + " == " + vSession.getUserId());
 					++cnt;
 					String modalBtnId = "modalBtn_" + cnt;
 					String modalId = "modal_" + cnt;
 					String spanId = "spanModal_" + cnt;
 					String spanVar = "spanVar_" + cnt;
 					String modalText = "modalText_" + cnt;
 					out.println("<span class=\"tbaChat\" id=\"" + modalBtnId + "\" onclick=\"openModal('"
 							+ modalId + "');\"  >" + vEntry.getDate() + ", " + vEntry.getTime() + "&nbsp;&nbsp;"
 							+ AccountCache.getInstance().get(vEntry).getFullName() + "</span><br>");
 					// fill the modal
 					modalStrBuffer.append("<div id=\"" + modalId + "\" class=\"tbaModal\">\r\n");
 					modalStrBuffer.append("<div class=\"modal-content\">\r\n");
 					modalStrBuffer.append("<span id=\"" + spanId + "\" class=\"close\">&times;</span>\r\n");
 					modalStrBuffer.append("<p><b>Naam:</b>&nbsp;" + vEntry.getName() + "\r\n");
 					modalStrBuffer.append("<p><b>Nummer:</b>&nbsp;" + vEntry.getNumber() + "\r\n");
 					modalStrBuffer
 							.append("<p>" + vEntry.getDate() + ", " + vEntry.getTime() + "&nbsp;&nbsp;<b>\r\n");
 					modalStrBuffer
 							.append(AccountCache.getInstance().get(vEntry).getFullName() + "</b></p>\r\n");
 					modalStrBuffer.append("<div class=\"old-modal-content\">" + vEntry.getShortDescription()
 							+ "<br><br>\r\n");
 					modalStrBuffer.append(
 							"<textarea  class=\"tbatextarea\" id=\"" + modalText + "\"></textarea></div>\r\n");
 					modalStrBuffer.append("<p align=\"right\">\r\n");
 					modalStrBuffer.append(
 							"<input class=\"tbabutton\" type=submit name=action value=\"Bewaar\" onclick=\"updateModalText('"
 									+ vEntry.getId() + "', '" + modalText + "')\">");
 					modalStrBuffer.append("</p></div></div>\r\n");
 					// fill the script
 					modalScriptStrBuffer
 							.append("var " + spanVar + "= document.getElementById(\"" + spanId + "\");\r\n");
 					modalScriptStrBuffer.append(
 							spanVar + ".onclick = function() {modal.style.display = \"none\"; }\r\n\r\n");
 				}
 			}
 			if (cnt == 0) {
 				out.println("<p>Geen Chats</p>");
 			}
 		}
 %></td>
                           </tr>
                        </table>
                     </td>
                  </tr>
               </table>
      <p>
         <span class="admintitle"> Oproepenlijst: <%=vRecords.size()%> oproepen</span>
         <button id="onOffButton" onclick="alterAudioOnOff()" type="button"><img src="/tba/images/soundOff.jpg" alt="zet geluid aan / af" style="width:30px;height:30px;"></button>
      </p>
      <%
      	if (vRecords == null || vRecords.size() == 0) {
      			if (vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL)) {
      %>
      <br> <span class="adminsubtitle">Er zijn geen oproepgegevens beschikbaar (<%=vSession.getDaysBack()%> dagen terug).
      </span>
      <%
      	} else {
      %>
      <br> <span class="adminsubtitle">Er zijn geen oproepgegevens beschikbaar voor maand (<%=Constants.MONTHS[vSession.getMonthsBack()]%>).
      </span>
      <%
      	}
      		} else {
      			if (vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL)) {
      %>
      <span class="adminsubtitle">Oproepen van <%=vSession.getDaysBack()%> dagen terug.
      </span>
      <%
      	} else {
      %>
      <span class="adminsubtitle">Oproepen tijdens de maand <%=Constants.MONTHS[vSession.getMonthsBack()]%>.
      </span>
      <%
      	}
      		}
      		allEntryIds = new StringBuilder("[");
      		if (vRecords != null && vRecords.size() > 0) {
      %>
      <br>
      <table border-spacing=2px border="0" cellspacing="2" cellpadding="4">
         <tr>
            <td width="20" bgcolor="FFFFFF"></td>
            <td width="10" valign="top" class="topMenu" bgcolor="F89920"></td>
            <td width="200" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Klant</td>
            <td width="55" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Datum</td>
            <td width="35" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Uur</td>
            <td width="85" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Nummer</td>
            <td width="230" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Naam</td>
            <td width="500" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Omschrijving</td>
            <td width="70" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Duur</td>
            <td width="100" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Infos</td>
         </tr>
         <%
         	int vRowInd = 0;
         			AccountEntityData vAccountEntityData;

         			for (Iterator<CallRecordEntityData> i = vRecords.iterator(); i.hasNext();) {
         				CallRecordEntityData vEntry = i.next();

         				String vId = "id" + vEntry.getId();
         				String customerName;
         				vAccountEntityData = AccountCache.getInstance().get(vEntry);
         				if (vAccountEntityData == null) {
         					customerName = "Oude klant(" + vEntry.getFwdNr() + ")";
         				} else {
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
         				if (vLongDesc.length() > 0) {
         					vInfoGifs = vInfoGifs.concat(
         							"<img src=\"/tba/images/info.gif\" alt=\"dubbel klik om de info te bekijken\" height=\"16\" border=\"0\">&nbsp;");
         				}
         				if (vEntry.getIsAgendaCall()) {
         					vInfoGifs = vInfoGifs
         							.concat("<img src=\"/tba/images/agenda.gif\" height=\"13\" border=\"0\">&nbsp;");
         				}
         				if (vEntry.getIsSmsCall()) {
         					vInfoGifs = vInfoGifs
         							.concat("<img src=\"/tba/images/sms.gif\"  height=\"13\" border=\"0\">&nbsp");
         				}
         				if (vEntry.getIsForwardCall()) {
         					vInfoGifs = vInfoGifs
         							.concat("<img src=\"/tba/images/telefoon.gif\"  height=\"13\" border=\"0\">&nbsp;");
         				}
         				if (vEntry.getIsFaxCall()) {
         					vInfoGifs = vInfoGifs
         							.concat("<img src=\"/tba/images/fax.gif\"  height=\"13\" border=\"0\">&nbsp;");
         				}
         				if (vEntry.getIs3W_call()) {
         					vInfoGifs = vInfoGifs
         							.concat("<img src=\"/tba/images/3w.gif\"  height=\"13\" border=\"0\">&nbsp;");
         				}
         				String vImportant = "";
         				if (vEntry.getIsImportantCall()) {
         					vImportant = vImportant.concat(
         							"<img src=\"/tba/images/important.gif\"  height=\"13\" border=\"0\">&nbsp;");
         				}
         				if (!vEntry.getIsDocumented()) {
         					vStyleStart = vStyleStart.concat("<b>");
         					vStyleEnd = vStyleEnd.concat("</b>");
         				}
         				if (vEntry.getIsMailed()) {
         					vStyleStart = vStyleStart.concat("<i>");
         					vStyleEnd = vStyleEnd.concat("</i>");
         				}
         %>
         <tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst" onmouseover="hooverOnRow('<%=vId%>','<%=vRowInd%>')" onmouseout="hooverOffRow('<%=vId%>','<%=vRowInd%>')" onclick="updateDeleteFlag('<%=vId%>','<%=vEntry.getId()%>','<%=vRowInd%>')"
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
         			if (vRowInd > 0) {
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
        <iframe src="https://pbxonline.be/tools/webconsole" width="500"></iframe>
    </td> -->
      </tr>
      </table>
      <%
      	out.println(modalStrBuffer.toString());
      %>
   </form>
   <%
   	} catch (Exception e) {
   		e.printStackTrace();
   	}
   %>

   <script>

var linesToDelete = new Array();

window.name="callswindow"; 


var socket = new WebSocket("ws://localhost:8080/tba/ws");
var pendingCalls = [];
<%
Collection<String> calls = IntertelCallManager.getInstance().getPendingCallList();
for (Iterator<String> i = calls.iterator(); i.hasNext();)
{
    %>
    var json = JSON.parse('<%=i.next()%>');
    pendingCalls.push(json);
    <%
}
%>
var isRingOn = getRingOn();
console.log(' at start isRingOn=' + isRingOn);
var ring = document.getElementById("ringRing"); 
setOnOffText();
updatePendingCalls();
setInterval(updatePendingCalls, 5000);

function playRing() 
{ 
  if (isRingOn == 1)
    ring.play(); 
} 

function setOnOffText()
{
   if (isRingOn == 1)
   {
	    document.getElementById('onOffButton').innerHTML = "<img src=\"/tba/images/soundOn.jpg\" alt=\"zet geluid aan / af\" style=\"width:30px;height:30px;\">";
   }
   else
   {
	    document.getElementById('onOffButton').innerHTML = "<img src=\"/tba/images/soundOff.jpg\" alt=\"zet geluid aan / af\" style=\"width:30px;height:30px;\">";
   }
   console.log('isRingOn=' + isRingOn);
}


function alterAudioOnOff() 
{ 
    isRingOn ^= 1;
    setOnOffText();
    setCookie('ringOn', isRingOn, 7); 
   console.log('alter: isRingOn=' + isRingOn);
} 


function getRingOn()
{
    var isOn = getCookie('ringOn');
    if (isOn == "1")
    {
        return 1;
    }
    else if (isOn != "0")
   	{
    	setCookie('ringOn', 0, 7); 
   	}
    return 0;
}

/*
document.forms.publish.onsubmit = function() 
{
  var outgoingMessage = this.message.value;
  
  if (socket.isopen())
  {
	  //socket.send(outgoingMessage);
  }
  else
  {
	  alert("Socket is closed. Refresh your page first.");
  }
  return false;
}
*/
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
    if (json.operation == <%=WebSocketData.NEW_CALL%>)
    {
        console.log("add pending call");
        pendingCalls.push(json);
    	playRing();
    }
    else if (json.operation == <%=WebSocketData.CALL_ANSWERED%>)
   	{
       console.log("check for remove");
   	   for (i = 0; i < pendingCalls.length; i++) 
   	   {
   	      if (pendingCalls[i].callId == json.callId)
   	      {
   	        console.log("removed pending call");

   	          pendingCalls.splice(i, 1);
   	       break;
   	      }
   	   }
   	}
    else 
        {
        console.log("unknown operation:" + json.operation);
        }
    updatePendingCalls();
}


function updatePendingCalls()
{
    
    var now = Math.floor(Date.now() / 1000);
    
	var content = "<table class='tdborder' width=100%>";
    if (pendingCalls.length == 0)
    {
        content += "<tr><td>Er zijn geen wachtende oproepen.</td></tr>";
    }
    else
    {
        for (i = 0; i < pendingCalls.length; i++) 
        {
            content += "<tr onclick=\"changeUrl('/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.RECORD_UPDATE%>&<%=Constants.RECORD_ID%>=" + pendingCalls[i].dbId + "');\">";
            content += "<td><table width=100% class='trBlock'><tr><td>" + timeStamp2Txt(pendingCalls[i].timeStamp, now) + "</td><td width=20px></td><td>" + pendingCalls[i].customer + "</td></tr></table></td></tr>";
        }
    }
	content += "</table>";
    
	console.log("updatePendingCalls(): " + content);

	document.getElementById('pendingCalls').innerHTML = content;
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

function getCookie(cname) 
{
	  var name = cname + "=";
	  var decodedCookie = decodeURIComponent(document.cookie);
	  var ca = decodedCookie.split(';');
	  for(var i = 0; i <ca.length; i++) {
	    var c = ca[i];
	    while (c.charAt(0) == ' ') {
	      c = c.substring(1);
	    }
	    if (c.indexOf(name) == 0) 
	    {
	    	console.log('cookie found:' + c);
	      return c.substring(name.length, c.length);
	    }
	  }
	  console.log('cookie ' + cname + ' not found');
	  return "";
}

function setCookie(cname, cvalue, exdays) 
{
	console.log('setCookie(' + cname + '=' + cvalue + ';' + exdays + ')');  
	var d = new Date();
	  d.setTime(d.getTime() + (exdays*24*60*60*1000));
	  var expires = "expires="+ d.toUTCString();
	  document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
	  console.log('set cookie: ' + cname + "=" + cvalue + ";" + expires + ";path=/");
}
	
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
	console.log("Selecet all loggging");
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

function toCallList()
{
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_CANVAS%>";
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
}

//Get the modal
var modal;

function openModal(modalId)
{
  modal = document.getElementById(modalId);
  modal.style.display = "block";
}

//When the user clicks anywhere outside of the modal, close it
window.onclick = function(event)
{
  if (event.target == modal)
  {
      modal.style.display = "none";
  }
}
<%out.println(modalScriptStrBuffer.toString());%>
                
</script>

</body>

</html>

