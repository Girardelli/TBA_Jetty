<html>

<%@ include file="adminheader.jsp" %>


<head>
<meta HTTP-EQUIV="Refresh"	content="<%=Constants.REFRESH%>;URL=\TheBusinessAssistant\admin\adminaccount.jsp>">
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
be.tba.util.constants.*,
be.tba.util.session.*"%>

	<%!
private StringBuilder allEntryIds;

%>
<body>
<table width='100%' cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
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
		<p><span class="admintitle"> Geregistreerde klanten:</span></p>
		<form name="adminaccform" method="GET" action="/TheBusinessAssistant/AdminDispatch">
		<input type=hidden name=<%=Constants.ACCOUNT_TO_DELETE%> value=""> 
		<input type=hidden name=<%=Constants.SRV_ACTION%> value="yves"> 
    <%
        if (vSession.getRole() == AccountRole.ADMIN)
        {
    %>
		<table>
			<tr>
				<td width="80"><input type=submit name=action value=" Toevoegen "
					onclick="addAccount()"></td>
				<td width="80"><input type=submit name=action value=" Verwijder "
					onclick="deleteAccount()"></td>
			</tr>
		</table>
    <%
        }
    %>
		<table width="100%" border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="25" bgcolor="FFFFFF"></td>
				<td width="60" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Nummer</td>
				<td width="70" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;GSM</td>
				<td width="190" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Naam</td>
				<td width="300" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;e-mail</td>
				<td width="200" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Laatste	login</td>
			</tr>
			<%
  vSession.setCallingJsp(Constants.ADMIN_ACCOUNT_JSP);
  int vRowInd = 0;
  Collection list = AccountCache.getInstance().getCustomerList();
  synchronized(list) 
  {
      for (Iterator vIter = list.iterator(); vIter.hasNext();)
      {
          AccountEntityData vEntry = (AccountEntityData) vIter.next();
          
          if (AccountRole.fromShort(vEntry.getRole()) == AccountRole.SUBCUSTOMER)
        	  continue;
          String vGsm = vEntry.getGsm();
          vGsm = (vGsm == null) ? "" : vGsm;
          String vNumber = vEntry.getFwdNumber();
          vNumber = (vNumber == null) ? "" : vNumber;
          String vFullName = vEntry.getFullName();
          vFullName = (vFullName == null) ? "" : vFullName;
          String vEmail = vEntry.getEmail();
          vEmail = (vEmail == null) ? "" : vEmail;
          String vLastLogin = vEntry.getLastLogin();
          vLastLogin = (vLastLogin == null) ? "" : vLastLogin;
          String vRegImg;
          if (vEntry.getIsRegistered())
            vRegImg = "\"/TheBusinessAssistant/images/greenVink.gif\"";
          else
            vRegImg = "\"/TheBusinessAssistant/images/deleteCross.gif\"";
          String vId = "id" + vEntry.getId();;
          
      %>
      			<tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst"
      				onmouseover="hooverOnRow('<%=vId%>','<%=vRowInd%>','#FFFF99')"
      				onmouseout="hooverOffRow('<%=vId%>','<%=vRowInd%>','#FFCC66')"
      				onclick="updateDeleteFlag('<%=vId%>','<%=vEntry.getId()%>','<%=vRowInd%>')"
      				ondblclick="changeUrl('/TheBusinessAssistant/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACCOUNT_UPDATE%>&<%=Constants.ACCOUNT_ID%>=<%=vEntry.getFwdNumber()%>');">
      				<td width="25" bgcolor="FFFFFF"><img src=<%=vRegImg%> width="16"
      					height="16" border="0"></td>
      				<td width="60" valign="top" class="bodytekst">&nbsp;<%=vNumber%></td>
      				<td width="70" valign="top" class="bodytekst">&nbsp;<%=vGsm%></td>
      				<td width="190" valign="top" class="bodytekst">&nbsp;<%=vFullName%></td>
      				<td width="300" valign="top" class="bodytekst">&nbsp;<%=vEmail%></td>
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
  %>
  </table>
  <p><span class="admintitle">Sub-Klant lijst</span></p>
  <%
  synchronized(list) 
  {
      for (Iterator vIter = list.iterator(); vIter.hasNext();)
      {
          AccountEntityData vEntry = (AccountEntityData) vIter.next();
          if (vEntry.getHasSubCustomers())
          {
       		  Collection subList = AccountCache.getInstance().getSubCustomersList(vEntry.getFwdNumber());
        	  System.out.print("sublist for " + vEntry.getFwdNumber() + " has " + subList.size() + " members");  
              %>
              <p><span class="admintitle"> <%=vEntry.getFullName()%></span></p>
              <table width="100%" border="0" cellspacing="2" cellpadding="2">
              <tr>
                  <td width="25" bgcolor="FFFFFF"></td>
                  <td width="60" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Nummer</td>
                  <td width="70" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;GSM</td>
                  <td width="190" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Naam</td>
                  <td width="300" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;e-mail</td>
                  <td width="200" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Laatste login</td>
              </tr>
              <%
		      for (Iterator vSubIter = subList.iterator(); vSubIter.hasNext();)
		      {
		          AccountEntityData vSubEntry = (AccountEntityData) vSubIter.next();
		          String vGsm = vEntry.getGsm();
		          vGsm = (vGsm == null) ? "" : vGsm;
		          String vNumber = vSubEntry.getFwdNumber();
		          vNumber = (vNumber == null) ? "" : vNumber;
		          String vFullName = vSubEntry.getFullName();
		          vFullName = (vFullName == null) ? "" : vFullName;
		          String vEmail = vSubEntry.getEmail();
		          vEmail = (vEmail == null) ? "" : vEmail;
		          String vLastLogin = vSubEntry.getLastLogin();
		          vLastLogin = (vLastLogin == null) ? "" : vLastLogin;
		          String vRegImg;
		          if (vSubEntry.getIsRegistered())
		            vRegImg = "\"/TheBusinessAssistant/images/greenVink.gif\"";
		          else
		            vRegImg = "\"/TheBusinessAssistant/images/deleteCross.gif\"";
		          String vId = "id" + vSubEntry.getId();
		          
		      %>
		                <tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst"
		                    onmouseover="hooverOnRow('<%=vId%>','<%=vRowInd%>','#FFFF99')"
		                    onmouseout="hooverOffRow('<%=vId%>','<%=vRowInd%>','#FFCC66')"
		                    onclick="updateDeleteFlag('<%=vId%>','<%=vSubEntry.getId()%>','<%=vRowInd%>')"
		                    ondblclick="changeUrl('/TheBusinessAssistant/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACCOUNT_UPDATE%>&<%=Constants.ACCOUNT_ID%>=<%=vNumber%>');">
		                    <td width="25" bgcolor="FFFFFF"><img src=<%=vRegImg%> width="16"
		                        height="16" border="0"></td>
		                    <td width="60" valign="top" class="bodytekst">&nbsp;<%=vNumber%></td>
		                    <td width="70" valign="top" class="bodytekst">&nbsp;<%=vGsm%></td>
		                    <td width="190" valign="top" class="bodytekst">&nbsp;<%=vFullName%></td>
		                    <td width="300" valign="top" class="bodytekst">&nbsp;<%=vEmail%></td>
		                    <td width="200" valign="top" class="bodytekst">&nbsp;<%=vLastLogin%></td>
		                </tr>
		                <%
		          vRowInd++;
		          allEntryIds.append("\"");
		          allEntryIds.append(vId);
		          allEntryIds.append("\"");
		          allEntryIds.append(",");
              }
              %>
              </table>
              <%              
              
          }
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
		</form>
		</td>
	</tr>

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

function updateDeleteFlag(rowid,id, rowInd)
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
  document.adminaccform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_ACCOUNT_ADD%>";
}

function openAccount(id, rowInd)
{
  entry = document.getElementById(id);
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
</table>

</body>

</html>

