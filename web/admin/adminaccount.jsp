<html>

<%@ include file="adminheader.jsp"%>


<head>
<meta HTTP-EQUIV="Refresh" content="<%=Constants.REFRESH%>;URL=\tba\admin\adminaccount.jsp>">
</head>

<%@ page import="
java.util.*,


javax.naming.Context,
javax.naming.InitialContext,


be.tba.ejb.account.interfaces.*,
be.tba.util.constants.*,
be.tba.util.session.*"%>

<body>
   <table cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
      <tr>
         <!-- white space -->
         <td valign="top" width="20" bgcolor="FFFFFF"></td>

         <!-- account list -->
         <td valign="top" bgcolor="FFFFFF"><br> 
<%
StringBuilder allEntryIds = new StringBuilder("[");
    try {
			vSession.setCallingJsp(Constants.ADMIN_ACCOUNT_JSP);
 %>
            <p>
               <span class="bodytitle"> Geregistreerde klanten:</span>
            </p> <%
    if (vSession.getRole() == AccountRole.ADMIN) {
 %>
            <table>
               <tr>
               <td>
                  <form name="downloadfileform" method="POST" action="/tba/download">
                     <input type=hidden name=<%=Constants.ACCOUNT_TO_DELETE%> value=""> 
                     <input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.DOWNLOAD_WK_KLANTEN_XML%>"> 
                     <input class="tbabutton" type=submit name=action value=" Download export file " onclick="downloadExportFile()">
                     </form>
                  </td>
                  </tr>
                  <tr>
                     <td >
                     <form name="adminaccform" method="POST" action="/tba/AdminDispatch">
                     <input type=hidden name=<%=Constants.ACCOUNT_TO_DELETE%> value=""> 
                     <input type=hidden name=<%=Constants.SRV_ACTION%> value="">
                     <input class="tbabutton" type=submit name=action value=" Toevoegen " onclick="addAccount()">
                     <input class="tbabutton" type=submit name=action value=" Archiveren " onclick="deleteAccount()">
                    </form>
                  </td>
               </tr>
            </table> 
<%
    }
 %>
            <table border="0" cellspacing="2" cellpadding="4">
               <col width="25">
               <col width="90">
               <col width="100">
               <col width="300">
               <col width="350">
               <col width="200">
               <tr>
                  <td bgcolor="FFFFFF"></td>
                  <td valign="top" class="topMenu" bgcolor="#F89920">Nummer</td>
                  <td valign="top" class="topMenu" bgcolor="#F89920">GSM</td>
                  <td valign="top" class="topMenu" bgcolor="#F89920">Naam</td>
                  <td valign="top" class="topMenu" bgcolor="#F89920">e-mail</td>
               </tr>
               <%
                  vSession.setCallingJsp(Constants.ADMIN_ACCOUNT_JSP);
               				int vRowInd = 0;
               				Collection<AccountEntityData> list = AccountCache.getInstance().getCustomerList();
               				synchronized (list) {
               					for (AccountEntityData vEntry : list) {
               						if (AccountRole.fromShort(vEntry.getRole()) == AccountRole.SUBCUSTOMER || vEntry.getIsArchived())
               						{
               							continue;
               						}
               						String vGsm = vEntry.getGsm();
               						vGsm = (vGsm == null) ? "" : vGsm;
               						String vNumber = vEntry.getFwdNumber();
               						vNumber = (vNumber == null) ? "" : vNumber;
               						String vFullName = vEntry.getFullName();
               						vFullName = (vFullName == null) ? "" : vFullName;
               						String vEmail = vEntry.getEmail();
               						vEmail = (vEmail == null) ? "" : vEmail;
               						String vRegImg;
               						//          if (vEntry.getIsRegistered())
               						if (vEntry.getNoInvoice())
               							vRegImg = "\"/tba/images/deleteCross.gif\"";
               						else
               							vRegImg = "\"/tba/images/greenVink.gif\"";
               						String vId = "id" + vEntry.getId();;
               %>
               <tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst" onmouseover="hooverOnRow('<%=vId%>','<%=vRowInd%>','#FFFF99')" onmouseout="hooverOffRow('<%=vId%>','<%=vRowInd%>','#FFCC66')" onclick="updateDeleteFlag('<%=vId%>','<%=vEntry.getId()%>','<%=vRowInd%>')"
                  ondblclick="changeUrl('/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACCOUNT_UPDATE%>&<%=Constants.ACCOUNT_ID%>=<%=vEntry.getId()%>');">
                  <td bgcolor="FFFFFF"><img src=<%=vRegImg%> width="16" height="16" border="0"></td>
                  <td valign="top" class="bodytekst"><%=vNumber%></td>
                  <td valign="top" class="bodytekst"><%=vGsm%></td>
                  <td valign="top" class="bodytekst"><%=vFullName%></td>
                  <td valign="top" class="bodytekst"><%=vEmail%></td>
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
            <p>
               <span class="bodytitle">Sub-Klant lijst</span>
            </p> <%
    synchronized (list) {
 					for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();) {
 						AccountEntityData vEntry = vIter.next();
 						if (vEntry.getHasSubCustomers()) {
 							Collection<AccountEntityData> subList = AccountCache.getInstance().getSubCustomersList(vEntry.getId());
 							//System.out.print("sublist for " + vEntry.getFwdNumber() + " has " + subList.size() + " members");
                           if (subList == null)
                           {
                              System.out.println("ERROR: no subcustomers found allthough the hasSubcustomer flag is set");
                              continue;
                           }
 %>
            <p>
               <span class="bodytitle"> <%=vEntry.getFullName()%></span>
            </p>
            <table border="0" cellspacing="2" cellpadding="4">
               <col width="25">
               <col width="90">
               <col width="100">
               <col width="300">
               <col width="350">
               <col width="200">
               <tr>
                  <td bgcolor="FFFFFF"></td>
                  <td valign="top" class="topMenu" bgcolor="#F89920">Nummer</td>
                  <td valign="top" class="topMenu" bgcolor="#F89920">GSM</td>
                  <td valign="top" class="topMenu" bgcolor="#F89920">Naam</td>
                  <td valign="top" class="topMenu" bgcolor="#F89920">e-mail</td>
               </tr>
               <%
                  for (Iterator<AccountEntityData> vSubIter = subList.iterator(); vSubIter.hasNext();) 
                  {
               								AccountEntityData vSubEntry = vSubIter.next();
               								if (vSubEntry.getIsArchived()) 
                                            {
               									continue;
               								}
               								String vGsm = vEntry.getGsm();
               								vGsm = (vGsm == null) ? "" : vGsm;
               								String vNumber = vSubEntry.getFwdNumber();
               								vNumber = (vNumber == null) ? "" : vNumber;
               								String vFullName = vSubEntry.getFullName();
               								vFullName = (vFullName == null) ? "" : vFullName;
               								String vEmail = vSubEntry.getEmail();
               								vEmail = (vEmail == null) ? "" : vEmail;
               								String vRegImg;
               								//			          if (vSubEntry.getIsRegistered())
               								//			            vRegImg = "\"/tba/images/greenVink.gif\"";
               								//			          else
               								//			            vRegImg = "\"/tba/images/deleteCross.gif\"";
               								if (!vSubEntry.getNoInvoice())
               									vRegImg = "\"/tba/images/greenVink.gif\"";
               								else
               									vRegImg = "\"/tba/images/deleteCross.gif\"";
               								String vId = "id" + vSubEntry.getId();
               %>
               <tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst" onmouseover="hooverOnRow('<%=vId%>','<%=vRowInd%>','#FFFF99')" onmouseout="hooverOffRow('<%=vId%>','<%=vRowInd%>','#FFCC66')" onclick="updateDeleteFlag('<%=vId%>','<%=vSubEntry.getId()%>','<%=vRowInd%>')"
                  ondblclick="changeUrl('/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACCOUNT_UPDATE%>&<%=Constants.ACCOUNT_ID%>=<%=vSubEntry.getId()%>');">
                  <td bgcolor="FFFFFF"><img src=<%=vRegImg%> width="16" height="16" border="0"></td>
                  <td valign="top" class="bodytekst"><%=vNumber%></td>
                  <td valign="top" class="bodytekst"><%=vGsm%></td>
                  <td valign="top" class="bodytekst"><%=vFullName%></td>
                  <td valign="top" class="bodytekst"><%=vEmail%></td>
               </tr>
               <%
                  vRowInd++;
               								allEntryIds.append("\"");
               								allEntryIds.append(vId);
               								allEntryIds.append("\"");
               								allEntryIds.append(",");
               							}
               %>
            </table> <%
                        }
 						else
 						{
                     
 						}           
                  
 					}
 				}
 				if (vRowInd > 0) {
 					allEntryIds.deleteCharAt(allEntryIds.length() - 1);
 				}
 				allEntryIds.append("]");
 			} catch (Exception e) {
 				e.printStackTrace();
 			}
 %></td>
      </tr>
   </table>
   <br><br>


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

function downloadExportFile()
{
      var shorterArr = new Array();
      var j = 0;
      for (var i = 0; i < linesToDelete.length; i++)
        if (linesToDelete[i] != null)
          shorterArr[j++] = linesToDelete[i];
      document.downloadfileform.<%=Constants.ACCOUNT_TO_DELETE%>.value=shorterArr.join();
      document.downloadfileform.<%=Constants.SRV_ACTION%>.value="<%=Constants.DOWNLOAD_WK_KLANTEN_XML%>";
}
                        </script>
</body>

</html>

