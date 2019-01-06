<html>

<%@ include file="adminheader.jsp" %>


<head>
<meta HTTP-EQUIV="Refresh"
	content="<%=Constants.REFRESH%>;URL=\TheBusinessAssistant\admin\notloggedcalls.jsp">
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
be.tba.ejb.pbx.session.CallRecordSqlAdapter,
be.tba.ejb.pbx.interfaces.CallRecordEntityData,
be.tba.util.constants.EjbJndiNames,
be.tba.util.constants.Constants,
be.tba.util.session.AccountCache,
be.tba.util.data.*"%>

	<%!
private StringBuilder allEntryIds;
%>
<body>
	<form name="calllistform" method="GET"
		action="/TheBusinessAssistant/AdminDispatch"><input type=hidden
		name=<%=Constants.RECORD_TO_DELETE%> value=""> <input type=hidden
		name=<%=Constants.SRV_ACTION%> value=""> <br>
	<table  cellspacing='0' cellpadding='0' border='0'
		bgcolor="FFFFFF">
	<tr>
		<!-- white space -->
		<td valign="top" width="20" bgcolor="FFFFFF"></td>

		<!-- account list -->
		<td valign="top" width="750" bgcolor="FFFFFF"><br>
		<%
try
{
  vSession.setCallingJsp(Constants.ADMIN_NOTLOGGEDCALLS_JSP);
  allEntryIds = new StringBuilder("[");

  CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();

  Collection vRecords = null;
  vRecords = vQuerySession.getNotLogged(vSession);

  out.println("<table border=\"0\" cellspacing=\"2\" cellpadding=\"2\">");
  if (vRecords == null || vRecords.size() == 0)
  {
    out.println("            <p><span class=\"bodysubtitle\"> Er zijn vandaag nog geen gemiste oproepen geregistreerd.</span></p>");
  }
  else
  {
    out.println("            <p><span class=\"admintitle\"> Gemiste oproepen:</span></p>");
    out.println("            <table width=\"340\">");
    out.println("              <tr>");
    out.println("                <td width=\"20\" bgcolor=\"FFFFFF\"></td>");
    out.println("                <td width=\"145\" valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Klant</td>");
    out.println("                <td width=\"55\"  valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Datum</td>");
    out.println("                <td width=\"35\"  valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Uur</td>");
    out.println("                <td width=\"85\"  valign=\"top\" class=\"topMenu\" bgcolor=\"F89920\">&nbsp;Nummer</td>");
    out.println("              </tr>");

    int vRowInd = 0;
    for (Iterator i = vRecords.iterator(); i.hasNext();)
    {
      CallRecordEntityData vEntry = (CallRecordEntityData) i.next();
      AccountEntityData vAccountEntityData = AccountCache.getInstance().get(vEntry.getFwdNr());
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
		onmouseover="hooverOnRow('<%=vId%>','<%=vRowInd%>','#FFFF99')"
		onmouseout="hooverOffRow('<%=vId%>','<%=vRowInd%>','#FFCC66')"
		onclick="updateDeleteFlag('<%=vId%>','<%=vEntry.getId()%>','<%=vRowInd%>','#FFFF99')">
		<td width="20" bgcolor="FFFFFF"><img src=<%=vInOut%> width="16"
			height="16" border="0"></td>
		<td width="145" valign="top"><%=vAccountEntityData.getFullName()%></td>
		<td width="55" valign="top"><%=vDate%></td>
		<td width="35" valign="top"><%=vTime%></td>
		<td width="85" valign="top"><%=vNumber%></td>
		<%
      out.println("</tr>");
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
	</tr>
</table>
</form>

	<script type="text/javascript">

var linesToDelete = new Array();

function hooverOnRow(id, rowInd, colour)
{
  var entry = document.getElementById(id) ;
  if (linesToDelete[rowInd] == null)
    entry.style.backgroundColor= colour;
  else
    entry.style.backgroundColor= "FF9966";
}

function hooverOffRow(id, rowInd, colour)
{
  var entry = document.getElementById(id) ;
  if (linesToDelete[rowInd] == null)
    entry.style.backgroundColor= colour;
  else
    entry.style.backgroundColor= "FF9966";
}

function updateDeleteFlag(rowid, id, rowInd, colour)
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
    entry.style.backgroundColor= colour;
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
}

function filterCalls()
{
  document.calllistform.<%=Constants.RECORD_TO_DELETE%>.value="";
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_RECORD_ADMIN%>";
}

function openRecord(id, rowInd, customer)
{
  var entry = document.getElementById(id);
  linesToDelete[rowInd] = null;
  entry.style.backgroundColor = "FFFF99";

  ;
}

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
</script>

</body>

</html>

