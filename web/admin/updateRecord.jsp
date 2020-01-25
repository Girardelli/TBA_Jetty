<html>

<%@ include file="adminheader.jsp"%>

<%@ page
   import="javax.ejb.*,
java.util.*,


javax.naming.Context,
javax.naming.InitialContext,

javax.ejb.*,
be.tba.servlets.helper.IntertelCallManager,
be.tba.ejb.pbx.interfaces.*,
be.tba.ejb.account.interfaces.*,
be.tba.ejb.account.session.*,
be.tba.util.session.AccountCache,
be.tba.util.constants.Constants,
be.tba.util.data.*,
be.tba.util.invoice.*"%>
<%!private CallRecordEntityData mRecordData;
	private AccountEntityData mCustomerData;
	private String vRecordId = null;%>
<%
   try {
				vSession.setCallingJsp(Constants.UPDATE_RECORD_JSP);
				boolean isAutoUpdateRecord = vSession.isAutoUpdateRecord();

				mRecordData = vSession.getCurrentRecord();
				if (mRecordData == null) {
%>
<body>
   <table cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">

      <!--Update Record jsp-->
      <tr>
         <td valign="top" width="30" bgcolor="FFFFFF"></td>
         <td valign="top" bgcolor="FFFFFF"><br> <br> <span class="admintitle"> Deze oproep is niet meer gekend in de database.</span> <br>
            <form name="calllistform1" method="POST" action="/tba/AdminDispatch">
               <input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.GOTO_CANVAS%>"> <input class="tbabutton" type=submit value="Cancel" onclick="cancelUpdate();">
            </form></td>
      </tr>
   </table>
</body>
<%
   return;
				}

				IntertelCallData intertelCall = null;
				if (isAutoUpdateRecord) {
					intertelCall = IntertelCallManager.getInstance().getByDbId(mRecordData.getId());
				}

				mCustomerData = AccountCache.getInstance().get(mRecordData); // yves: to be changed in .getId() in de herfst van 2020

				String vDirStr = mRecordData.getIsIncomingCall() ? "Van Nummer" : "Naar Nummer";
				String vNumberHtml;
				if (isAutoUpdateRecord && intertelCall != null) {
					vNumberHtml = intertelCall.callingNr;
				} else {
					if (mRecordData.getNumber().length() == 0)
						vNumberHtml = new String(
								"<input type=text size=20 name=" + Constants.RECORD_NUMBER + " value=\"\">");
					else
						vNumberHtml = new String("<input type=text size=20 name=" + Constants.RECORD_NUMBER
								+ " value=\"" + mRecordData.getNumber() + "\">");
				}
%>

<body>
   <form name="calllistform" method="POST" action="/tba/AdminDispatch">
      <table cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">

         <!--Update Record jsp-->
         <tr valign="top">
            <td width="30" bgcolor="FFFFFF"></td>
            <td bgcolor="FFFFFF"><br> <br> <span class="admintitle"> Voeg extra informatie toe aan oproep voor <%=((mCustomerData == null) ? "" : mCustomerData.getFullName())%>.
            </span> <br> <span class="bodytekst">
                  <table border="0" cellspacing="1" cellpadding="1">
                     <%
                        if (mCustomerData != null && mCustomerData.getHasSubCustomers()) {
                     %>
                     <tr>
                        <td width="50"></td>
                        <td width="170" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Oproep voor</td>
                        <td width="530" valign="top"><select name="<%=Constants.ACCOUNT_SUB_CUSTOMER%>">
                              <option value="<%=mCustomerData.getFwdNumber()%>" selected>
                                 <%=mCustomerData.getFullName()%></option>
                              <%
                                 Collection<AccountEntityData> list = AccountCache.getInstance()
                              							.getSubCustomersList(mCustomerData.getId());
                              					synchronized (list) {
                              						for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();) {
                              							AccountEntityData vValue = vIter.next();
                              %>
                              <option value="<%=vValue.getFwdNumber()%>"><%=vValue.getFullName()%></option>
                              <%
                                 }
                              					}
                              %>
                        </select></td>
                     </tr>
                     <%
                        }
                     %>
                     <tr>
                        <td width="50"></td>
                        <td width="170" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Belangrijke oproep</td>
                        <td width="530" valign="top" class="adminsubsubtitle"><input type=checkbox name=<%=Constants.RECORD_IMPORTANT%> value="<%=Constants.YES%>" <%=(mRecordData.getIsImportantCall() ? " checked" : "")%>>&nbsp;&nbsp;<img src="/tba/images/important.gif"
                           alt="belangrijke oproep!" height="13" border="0"></td>
                     </tr>
                     <tr>
                        <td width="50"></td>
                        <td width="170" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;SMS verstuurd</td>
                        <td width="530" valign="top" class="adminsubsubtitle"><input type=checkbox name=<%=Constants.RECORD_SMS%> value="<%=Constants.YES%>" <%=(mRecordData.getIsSmsCall() ? "checked=\"checked\"" : "")%>>&nbsp;&nbsp;<img src="/tba/images/sms.gif"
                           alt="wij hebben een SMS bericht gestuurd betreffende deze oproep" height="13" border="0"></td>
                     </tr>
                     <tr>
                        <td width="50"></td>
                        <td width="170" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Afspraak toegevoegd</td>
                        <td width="530" valign="top" class="adminsubsubtitle"><input type=checkbox name=<%=Constants.RECORD_AGENDA%> value="<%=Constants.YES%>" <%=(mRecordData.getIsAgendaCall() ? "checked=\"checked\"" : "")%>>&nbsp;&nbsp;<img src="/tba/images/agenda.gif"
                           alt="afspraak toegevoegd in uw agenda" height="13" border="0"></td>
                     </tr>
                     <tr>
                        <td width="50"></td>
                        <td width="170" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Oproep doorgeschakeld</td>
                        <td width="530" valign="top" class="adminsubsubtitle"><input type=checkbox name=<%=Constants.RECORD_FORWARD%> value="<%=Constants.YES%>" <%=(mRecordData.getIsForwardCall() ? "checked=\"checked\"" : "")%>>&nbsp;&nbsp;<img src="/tba/images/telefoon.gif"
                           alt="oproep doorgeschakeld naar u" height="13" border="0"></td>
                     </tr>
                     <tr>
                        <td width="50"></td>
                        <td width="170" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Fax</td>
                        <td width="530" valign="top" class="adminsubsubtitle"><input type=checkbox name=<%=Constants.RECORD_FAX%> value="<%=Constants.YES%>" <%=(mRecordData.getIsFaxCall() ? "checked=\"checked\"" : "")%>>&nbsp;&nbsp;<img src="/tba/images/fax.gif"
                           alt="binnenkomende fax voor u verwerkt" height="13" border="0"></td>
                     </tr>
                     <%
                        if (mCustomerData != null && mCustomerData.getInvoiceType() == InvoiceHelper.kTelemarketingInvoice) {
                     					out.println("<tr>");
                     					out.println("<td width=\"50\"></td>");
                     					out.println("<td width=\"170\" valign=\"top\" class=\"adminsubsubtitle\"></td>");
                     					out.println("<td width=\"530\" valign=\"top\" class=\"adminsubsubtitle\">");
                     					out.println("<input type=radio name=\"" + Constants.RECORD_INVOICE_LEVEL + "\" value=\""
                     							+ Constants.RECORD_LEVEL1
                     							+ ((mRecordData.getInvoiceLevel() == InvoiceHelper.kLevel1) ? "\" checked" : "\"")
                     							+ ">geen succes&nbsp;&nbsp;&nbsp;");
                     					out.println("<input type=radio name=\"" + Constants.RECORD_INVOICE_LEVEL + "\" value=\""
                     							+ Constants.RECORD_LEVEL2
                     							+ ((mRecordData.getInvoiceLevel() == InvoiceHelper.kLevel2) ? "\" checked" : "\"")
                     							+ ">contact&nbsp;&nbsp;&nbsp;");
                     					out.println("<input type=radio name=\"" + Constants.RECORD_INVOICE_LEVEL + "\" value=\""
                     							+ Constants.RECORD_LEVEL3
                     							+ ((mRecordData.getInvoiceLevel() == InvoiceHelper.kLevel3) ? "\" checked" : "\"")
                     							+ ">afspraak!");
                     					out.println("</td>");
                     					out.println("</tr>");
                     				}
                     %>
                     <tr>
                        <td width="50"></td>
                        <td width="170" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Datum</td>
                        <td width="530" valign="top" class="bodytekst"><%=mRecordData.getDate()%></td>
                     </tr>
                     <tr>
                        <td width="50"></td>
                        <td width="170" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Uur</td>
                        <td width="530" valign="top" class="bodytekst"><%=mRecordData.getTime()%></td>
                     </tr>
                     <tr>
                        <td width="50"></td>
                        <td width="170" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;<%=vDirStr%></td>
                        <td width="530" valign="top"><%=vNumberHtml%></td>
                     </tr>
                     <tr>
                        <td width="50"></td>
                        <td width="170" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Naam</td>
                        <td width="530" valign="top"><input type=text size=30 name=<%=Constants.RECORD_CALLER_NAME%> value="<%=mRecordData.getName()%>"></td>
                     </tr>
                     <tr>
                        <td width="50"></td>
                        <td width="170" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Omschrijving</td>
                        <td width="530" valign="top"><textarea name=<%=Constants.RECORD_SHORT_TEXT%> rows=10 cols=70><%=(String) mRecordData.getShortDescription()%></textarea></td>
                     </tr>
                     <tr>
                        <td width="50"></td>
                        <td width="170" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Extra Informatie</td>
                        <td width="530" valign="top"><textarea name=<%=Constants.RECORD_LONG_TEXT%> rows=10 cols=70><%=(String) mRecordData.getLongDescription()%></textarea></td>
                     </tr>
                     <%
                        if (!isAutoUpdateRecord)// && !mRecordData.getIsMailed())
                     				{
                     %>
                     <tr>
                        <td width="50"></td>
                        <td width="170" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Andere klant</td>
                        <td width="530" valign="top">
                           <%
                              out.println("<select name=\"" + Constants.ACCOUNT_FORWARD_NUMBER + "\">");
                           					Collection<AccountEntityData> list = AccountCache.getInstance().getCustomerList();
                           					synchronized (list) {
                           						for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();) {
                           							AccountEntityData vValue = vIter.next();
                           							out.println("<option value=\"" + vValue.getFwdNumber()
                           									+ (mRecordData.getFwdNr().equals(vValue.getFwdNumber()) ? "\" selected>" : "\">")
                           									+ vValue.getFullName());
                           						}
                           					}
                           					out.println("</select>");
                           %>
                        </td>
                     </tr>
                     <%
                        }
                     %>
                  </table> <br> <br> <input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.SAVE_RECORD%>"> <%
    if (isAutoUpdateRecord && intertelCall != null) 
    {
 %> <input type=hidden name=<%=Constants.RECORD_NUMBER%> value="<%=intertelCall.calledNr%>"> <%
    }
 %> <input class="tbabutton" type=submit name=action value="Bewaar"> <input class="tbabutton" type=reset> <input class="tbabutton" type=submit value="Cancel" onclick="cancelUpdate();">
            </span> <br></td>
            <%
               if (mCustomerData != null && mCustomerData.getCallProcessInfo() != null
            						&& !mCustomerData.getCallProcessInfo().isEmpty()) {
            %>

            <td>
               <table>
                  <tr>
                     <td bgcolor="FFFFFF"><br> <br> <span class="admintitle">Informatie die u moet weten voor deze klant.</span> <br></td>
                  </tr>
                  <tr>
                     <td class="tdborder" valign="top" width="400"><%=mCustomerData.getCallProcessInfo()%></td>
                  </tr>
               </table>
            </td>
            <%
               }
            %>
         </tr>
         <%
            } catch (Exception ex) {
         				ex.printStackTrace();
         			}
         %>
      </table>
   </form>
</body>

<script type="text/javascript">

function cancelUpdate()
{
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_CANVAS%>";
}
</script>


</html>

