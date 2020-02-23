<html>
<%@ include file="protheader.jsp"%>


<%@ page import="javax.ejb.*,
java.util.*,
javax.naming.Context,
javax.naming.InitialContext,
javax.ejb.*,
be.tba.ejb.account.interfaces.*,
be.tba.util.constants.*,
be.tba.util.exceptions.*,
be.tba.util.session.*"%>
<%!
private static final String kSelected = "selected";
private static final String kChecked = "checked";
private static final int kMaxMailHour = 23;
private static final int kMaxMailMinutes = 56;%>
<%
AccountEntityData vCustomer;
   try {
				if (vSession == null)
					throw new AccessDeniedException("U bent niet aangemeld bij deze administratie pagina's.");

				vSession.setCallingJsp(Constants.CLIENT_PREF_JSP);
				vCustomer = AccountCache.getInstance().get(vSession.getSessionFwdNr());
				//System.out.println("vCustomer for " + vSession.getFwdNumber() + " is " + vCustomer);

				String vEmail = vCustomer.getEmail();
				vEmail = (vEmail == null) ? "" : vEmail;
				String vInvoiceEmail = vCustomer.getInvoiceEmail();
				vInvoiceEmail = (vInvoiceEmail == null) ? "" : vInvoiceEmail;
				String vGsm = vCustomer.getGsm();
				vGsm = (vGsm == null) ? "" : vGsm;

				int vMailHour1 = vCustomer.getMailHour1();
				boolean vIsMailOn1 = true;
				if (vMailHour1 < 1 || vMailHour1 > kMaxMailHour) {
					vMailHour1 = 1;
					vIsMailOn1 = false;
				}
				int vMailMinutes1 = vCustomer.getMailMinutes1();
				vMailMinutes1 = (vMailMinutes1 < 0 || vMailMinutes1 > kMaxMailMinutes) ? 0 : vMailMinutes1;

				int vMailHour2 = vCustomer.getMailHour2();
				boolean vIsMailOn2 = true;
				if (vMailHour2 < 1 || vMailHour2 > kMaxMailHour) {
					vMailHour2 = 1;
					vIsMailOn2 = false;
				}
				int vMailMinutes2 = vCustomer.getMailMinutes2();
				vMailMinutes2 = (vMailMinutes2 < 0 || vMailMinutes2 > kMaxMailMinutes) ? 0 : vMailMinutes2;

				int vMailHour3 = vCustomer.getMailHour3();
				boolean vIsMailOn3 = true;
				if (vMailHour3 < 1 || vMailHour3 > kMaxMailHour) {
					vMailHour3 = 1;
					vIsMailOn3 = false;
				}
				int vMailMinutes3 = vCustomer.getMailMinutes3();
				vMailMinutes3 = (vMailMinutes3 < 0 || vMailMinutes3 > kMaxMailMinutes) ? 0 : vMailMinutes3;
				boolean vNoEmptyMails = vCustomer.getNoEmptyMails();
				boolean vTextMail = vCustomer.getTextMail();
				boolean vInvoicePerMail = vCustomer.getIsMailInvoice();
%>
<form name="updateForm" method="POST" action="/tba/CustomerDispatch">
<table cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">

   <!--Update account jsp-->
   <tr>
      <td valign="middle" width="30" bgcolor="FFFFFF"></td>
      <td valign="middle" bgcolor="FFFFFF"><br> <br> 
      <span class="bodytitle">Stel uw voorkeuren in.</span> <br> 
      <table border="0" cellspacing="4" cellpadding="4">
         <tr>
            <td width="200" valign="middle" class="bodysubsubtitle"><img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;Facturatie e-mail adres</td>
            <td width="500" valign="middle"><input type=text name=<%=Constants.ACCOUNT_INVOICE_EMAIL%> size=50 value="<%=vInvoiceEmail%>"></td>
         </tr>
         <tr>
            <td width="200" valign="middle" class="bodysubsubtitle"><img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;GSM nummer (SMS)</td>
            <td width="500" valign="middle"><input type=text name=<%=Constants.ACCOUNT_GSM%> size=13 value="<%=vGsm%>"></td>
         </tr>
      </table>
      <br><p class="bodysubsubtitle">&nbsp;&nbsp;&nbsp;&nbsp;Stuur mij een mail met daarin de laatste oproepgegevens:</p>
      <table border="0" cellspacing="4" cellpadding="4">
         <tr>
            <td width="200" valign="middle" class="bodysubsubtitle"><img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;naar e-mail adres</td>
            <td width="500" valign="middle"><input type=text name=<%=Constants.ACCOUNT_EMAIL%> size=50 value="<%=vEmail%>"></td>
         </tr>
         <tr>
            <td width="200" valign="middle" class="bodysubsubtitle"><img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;op tijdstip 1</td>
            <td width="500" valign="middle">
            <table><tr><td width="30">
               <input type="checkbox" id="cbx1" style="display:none" name=<%=Constants.ACCOUNT_MAIL_ON1%><%=(vIsMailOn1 ? " checked=\"checked\"" : "")%> />
               <label for="cbx1" class="toggle"><span></span></label>    
            </td><td>
               <select name=<%=Constants.ACCOUNT_MAIL_UUR1%>>
                        <%
                           for (int i = 5; i <= kMaxMailHour; ++i)
                        					out.println("<option value=\"" + i + "\" " + ((vMailHour1 == i) ? kSelected : "") + ">" + i);
                        %>
                     </select> 
               uur 
            <select name=<%=Constants.ACCOUNT_MAIL_MINUTEN1%>>
                  <%
                     for (int i = 0; i < kMaxMailMinutes; i += 5)
                  					out.println("<option value=\"" + i + "\" " + ((vMailMinutes1 == i) ? kSelected : "") + ">"
                  							+ ((i < 10) ? "0" : "") + i);
                  %>
            </select> minuten
            </td></tr></table>
            </td>
         </tr>
         <tr>
            <td width="200" valign="middle" class="bodysubsubtitle"><img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;op tijdstip 2</td>
            <td width="500" valign="middle" class="bodytekst">
            <table><tr><td width="30">
               <input type="checkbox" id="cbx2" style="display:none" name=<%=Constants.ACCOUNT_MAIL_ON2%><%=(vIsMailOn2 ? " checked=\"checked\"" : "")%> />
               <label for="cbx2" class="toggle"><span></span></label>    
            </td><td>
               <select name=<%=Constants.ACCOUNT_MAIL_UUR2%>>
                  <%
                     for (int i = 5; i <= kMaxMailHour; ++i)
                  					out.println("<option value=\"" + i + "\" " + ((vMailHour2 == i) ? kSelected : "") + ">" + i);
                  %>
            </select> uur <select name=<%=Constants.ACCOUNT_MAIL_MINUTEN2%>>
                  <%
                     for (int i = 0; i < kMaxMailMinutes; i += 5)
                  					out.println("<option value=\"" + i + "\" " + ((vMailMinutes2 == i) ? kSelected : "") + ">"
                  							+ ((i < 10) ? "0" : "") + i);
                  %>
            </select> minuten
            </td></tr></table>
            </td>
         </tr>
         <tr>
            <td width="200" valign="middle" class="bodysubsubtitle"><img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;op tijdstip 3</td>
            <td width="500" valign="middle">
            <table><tr><td width="30">
               <input type="checkbox" id="cbx3" style="display:none" name=<%=Constants.ACCOUNT_MAIL_ON3%><%=(vIsMailOn3 ? " checked=\"checked\"" : "")%> />
               <label for="cbx3" class="toggle"><span></span></label>    
            </td><td>
               <select name=<%=Constants.ACCOUNT_MAIL_UUR3%>>
                  <%
                     for (int i = 5; i <= kMaxMailHour; ++i)
                  					out.println("<option value=\"" + i + "\" " + ((vMailHour3 == i) ? kSelected : "") + ">" + i);
                  %>
            </select> uur <select name=<%=Constants.ACCOUNT_MAIL_MINUTEN3%>>
                  <%
                     for (int i = 0; i < kMaxMailMinutes; i += 5)
                  					out.println("<option value=\"" + i + "\" " + ((vMailMinutes3 == i) ? kSelected : "") + ">"
                  							+ ((i < 10) ? "0" : "") + i);
                  %>
            </select> minuten
            </td></tr></table>
            </td>
         </tr>
         <tr>
            <td width="200" valign="middle" class="bodysubsubtitle"><img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;Zend geen lege mails</td>
            <td width="500" valign="middle">
               <input type="checkbox" id="cbx4" style="display:none" name=<%=Constants.ACCOUNT_NO_EMPTY_MAILS%><%=(vNoEmptyMails ? " checked=\"checked\"" : "")%> />
               <label for="cbx4" class="toggle"><span></span></label>    
            </td>
         </tr>
         <tr>
            <td width="200" valign="middle" class="bodysubsubtitle"><img src="/tba/images/blueSphere.gif" width="10" height="10">&nbsp;Zend in text format</td>
            <td width="500" valign="middle">
               <input type="checkbox" id="cbx5" style="display:none" name=<%=Constants.ACCOUNT_TEXT_MAIL%><%=(vTextMail ? " checked=\"checked\"" : "")%> />
               <label for="cbx5" class="toggle"><span></span></label>    
            </td>
         </tr>
      </table>
      <br><input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.SAVE_PREFS%>"> 
      <input class="tbabutton" type=submit name=action value="Bewaar" onclick="Bewaar()">
     </td>
   </tr>
   <%
      } catch (Exception e) {
   				e.printStackTrace();
   			}
   %>
</table>
</form>

</body>
<script>
function Bewaar()
{
  document.updateForm.<%=Constants.SRV_ACTION%>.value="<%=Constants.SAVE_PREFS%>";
}
</script>

</html>

