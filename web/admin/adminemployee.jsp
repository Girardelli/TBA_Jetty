<html>

<%@ include file="adminheader.jsp" %>


<head>
</head>

	<%@ page
		import="javax.ejb.*,
java.rmi.RemoteException,
javax.naming.Context,
java.util.Iterator,
javax.naming.InitialContext,
javax.rmi.PortableRemoteObject,
be.tba.ejb.account.interfaces.*,
be.tba.util.constants.*,
be.tba.util.session.*"%>

	<%!
private StringBuilder allEntryIds;

%>
<body>
<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
	<tr>
		<!-- white space -->
		<td valign="top" width="20" bgcolor="FFFFFF"></td>

		<!-- account list -->
		<td valign="top" width="750" bgcolor="FFFFFF"><br>
		<%
try
{
	allEntryIds = new StringBuilder("[");
%>
		<p><span class="admintitle"> Werknemers en Kaders</span></p>
		<form name="adminaccform" method="GET"
			action="/TheBusinessAssistant/AdminDispatch"><input type=hidden
			name=<%=Constants.ACCOUNT_TO_DELETE%> value=""> <input type=hidden
			name=<%=Constants.SRV_ACTION%> value=""> 
			
		<table>
			<tr>
				<td width="80"><input class="tbabutton" type=submit name=action value=" Toevoegen "
					onclick="addAccount()"></td>
				<td width="80"><input class="tbabutton" type=submit name=action value=" Verwijder "
					onclick="deleteAccount()"></td>
			</tr>
		</table>
		<br>
		<table width="100%" border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="60" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;UserId</td>
				<td width="80" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;nummer</td>
				<td width="190" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Naam</td>
				<td width="200" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Laatste	login</td>
			</tr>
			<%
  vSession.setCallingJsp(Constants.ADMIN_EMPLOYEE_JSP);
  int vRowInd = 0;

  Collection list = AccountCache.getInstance().getEmployeeList();
  synchronized(list) 
  {
      for (Iterator vIter = list.iterator(); vIter.hasNext();)
      {
          AccountEntityData vEntry = (AccountEntityData) vIter.next();
          String vUserId = vEntry.getUserId();
          vUserId = (vUserId == null) ? "" : vUserId;
          String vNumber = vEntry.getFwdNumber();
          vNumber = (vNumber == null) ? "" : vNumber;
          String vFullName = vEntry.getFullName();
          vFullName = (vFullName == null) ? "" : vFullName;
          String vLastLogin = vEntry.getLastLogin();
          vLastLogin = (vLastLogin == null) ? "" : vLastLogin;
          String vId = "id" + vEntry.getId();
    
%>
			<tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst"
				onmouseover="hooverOnRow('<%=vId%>','<%=vRowInd%>','#FFFF99')"
				onmouseout="hooverOffRow('<%=vId%>','<%=vRowInd%>','#FFCC66')"
				onclick="updateDeleteFlag('<%=vId%>','<%=vEntry.getId()%>','<%=vRowInd%>')">
				<td width="60" valign="top" class="bodytekst">&nbsp;<%=vUserId%></td>
				<td width="80" valign="top" class="bodytekst">&nbsp;<%=vNumber%></td>
				<td width="190" valign="top" class="bodytekst">&nbsp;<%=vFullName%></td>
				<td width="200" valign="top" class="bodytekst">&nbsp;<%=vLastLogin%></td>
			</tr>
			<%
	    vRowInd++;
		allEntryIds.append("\"");
		allEntryIds.append(vId);
		allEntryIds.append("\"");
		allEntryIds.append(",");
      }
  }
  if (vRowInd > 0)
  {
      allEntryIds.deleteCharAt(allEntryIds.length() - 1);
  }
  allEntryIds.append("]");
}
catch (Exception e)
{
    e.printStackTrace();
}
%>
		</table>
		</form>
		</td>
	</tr>
</table>

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

function deleteAccount()
{
  var shorterArr = new Array();
  var j = 0;
  for (var i = 0; i < linesToDelete.length; i++)
    if (linesToDelete[i] != null)
      shorterArr[j++] = linesToDelete[i];
  document.adminaccform.<%=Constants.ACCOUNT_TO_DELETE%>.value=shorterArr.join();
  document.adminaccform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_ACCOUNT_DELETE%>";
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

function addAccount()
{
  document.adminaccform.<%=Constants.ACCOUNT_TO_DELETE%>.value="";
  document.adminaccform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_EMPLOYEE_ADD%>";
}

function openAccount(id, rowInd)
{
  var entry = document.getElementById(id);
  linesToDelete[rowInd] = null;
  entry.style.backgroundColor = "FFFF99";
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

function changeUrl(newURL) 
{
  location=newURL;
}



</script>

</body>

</html>

