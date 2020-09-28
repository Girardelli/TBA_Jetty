<html>

<%@ include file="adminheader.jsp"%>


<head>
<meta HTTP-EQUIV="Refresh" content="<%=Constants.REFRESH%>;URL=\tba\admin\archivedaccount.jsp>">
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
try 
{
            vSession.setCallingJsp(Constants.ARCHIVED_ACCOUNT_JSP);
 %>
            <p>
               <span class="bodytitle"> Gearchiveerde klanten:</span>
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
                  vSession.setCallingJsp(Constants.ADMIN_ACCOUNT_JSP);
               				int vRowInd = 0;
               				Collection<AccountEntityData> list = AccountCache.getInstance().getArchivedList();
               				synchronized (list) 
                           {
               					for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();) 
                              {
               						AccountEntityData vEntry = vIter.next();
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
               <tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst" ondblclick="changeUrl('/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACCOUNT_UPDATE%>&<%=Constants.ACCOUNT_ID%>=<%=vEntry.getId()%>');">
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
<%
	allEntryIds.append("]");
} catch (Exception e) {
	e.printStackTrace();
}
 %>
        </td>
      </tr>
   </table>

      <script type="text/javascript">

function changeUrl(newURL) 
{
  location=newURL;
}

</script>

</body>

</html>

