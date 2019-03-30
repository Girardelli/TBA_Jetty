<html>
<%@ include file="protheader.jsp" %>

<head>
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
be.tba.util.exceptions.*,
be.tba.util.session.*"%>
	<%!
private AccountEntityData vCustomer;
private Collection vFreeNumbers;

private static final String kSelected = "selected";
private static final String kChecked = "checked";
private static final int kMaxMailHour = 23;
private static final int kMaxMailMinutes = 56;
%>
<%
try 
{
vCustomer = AccountCache.getInstance().get(vSession.getFwdNumber());
System.out.println("vCustomer for " + vSession.getFwdNumber() + " is " + vCustomer);

String vEmail = vCustomer.getEmail();
vEmail = (vEmail == null) ? "" : vEmail;
String vInvoiceEmail = vCustomer.getInvoiceEmail();
vInvoiceEmail = (vInvoiceEmail == null) ? "" : vInvoiceEmail;
String vGsm = vCustomer.getGsm();
vGsm = (vGsm == null) ? "" : vGsm;
String v3W_PersonId = vCustomer.getW3_PersonId();
v3W_PersonId = (v3W_PersonId == null) ? "" : v3W_PersonId;
String vW3_CompanyId = vCustomer.getW3_CompanyId();
vW3_CompanyId = (vW3_CompanyId == null) ? "" : vW3_CompanyId;

int vMailHour1 = vCustomer.getMailHour1();
boolean vIsMailOn1 = true;
if (vMailHour1 < 1 || vMailHour1 > kMaxMailHour)
{
  vMailHour1 = 1;
  vIsMailOn1 = false;
}
int vMailMinutes1 = vCustomer.getMailMinutes1();
vMailMinutes1 = (vMailMinutes1 < 0 || vMailMinutes1 > kMaxMailMinutes) ? 0 : vMailMinutes1;

int vMailHour2 = vCustomer.getMailHour2();
boolean vIsMailOn2 = true;
if (vMailHour2 < 1 || vMailHour2 > kMaxMailHour)
{
  vMailHour2 = 1;
  vIsMailOn2 = false;
}
int vMailMinutes2 = vCustomer.getMailMinutes2();
vMailMinutes2 = (vMailMinutes2 < 0 || vMailMinutes2 > kMaxMailMinutes) ? 0 : vMailMinutes2;

int vMailHour3 = vCustomer.getMailHour3();
boolean vIsMailOn3 = true;
if (vMailHour3 < 1 || vMailHour3 > kMaxMailHour)
{
  vMailHour3 = 1;
  vIsMailOn3 = false;
}
int vMailMinutes3 = vCustomer.getMailMinutes3();
vMailMinutes3 = (vMailMinutes3 < 0 || vMailMinutes3 > kMaxMailMinutes) ? 0 : vMailMinutes3;
boolean vNoEmptyMails = vCustomer.getNoEmptyMails();
boolean vTextMail = vCustomer.getTextMail();
boolean vInvoicePerMail = vCustomer.getIsMailInvoice();
%>
<table  cellspacing='0' cellpadding='0' border='0'
    bgcolor="FFFFFF">

	<!--Update account jsp-->
	<tr>
		<td valign="top" width="30" bgcolor="FFFFFF"></td>
		<td valign="top" bgcolor="FFFFFF"><br>
		<br>
		<span class="admintitle">Stel uw voorkeuren in.</span> <br>
		<span class="bodytekst"> <!-- action name must be a URI name as it is set in the <application>.xml servlet-mapping tag.-->
		<form name="updateForm" method="POST"
			action="/tba/CustomerDispatch">
		<table width="700" border="0" cellspacing="4" cellpadding="4">
			<tr>
				<td width="200" valign="top" class="adminsubsubtitle"><img
					src="/tba/images/blueSphere.gif" width="10"
					height="10">&nbsp;e-mail</td>
				<td width="500" valign="top"><input type=text
					name=<%=Constants.ACCOUNT_EMAIL%> size=50 value="<%=vEmail%>"></td>
			</tr>
            <tr>
                <td width="200" valign="top" class="adminsubsubtitle"><img
                    src="/tba/images/blueSphere.gif" width="10"
                    height="10">&nbsp;Invoice e-mail</td>
                <td width="500" valign="top"><input type=text
                    name=<%=Constants.ACCOUNT_INVOICE_EMAIL%> size=50 value="<%=vInvoiceEmail%>"></td>
            </tr>
			<tr>
				<td width="200" valign="top" class="adminsubsubtitle"><img
					src="/tba/images/blueSphere.gif" width="10"
					height="10">&nbsp;GSM nummer (SMS)</td>
				<td width="500" valign="top"><input type=text
					name=<%=Constants.ACCOUNT_GSM%> size=13 value="<%=vGsm%>"></td>
			</tr>
		</table>
		<p>&nbsp;&nbsp;&nbsp;&nbsp;Stuur mij een mail met daarin de laatste	oproepgegevens om:</p>
		<table width="700" border="0" cellspacing="4" cellpadding="4">
			<tr>
				<td width="200" valign="top" class="adminsubsubtitle"><img
					src="/tba/images/blueSphere.gif" width="10"
					height="10">&nbsp;mail 1</td>
				<td width="40" valign="top" class="bodytekst"><input type=checkbox
					name=<%=Constants.ACCOUNT_MAIL_ON1%> value="<%=Constants.YES%>"
					<%=(vIsMailOn1?kChecked:"")%>></td>
				<td width="460" valign="top" class="bodytekst"><select
					name=<%=Constants.ACCOUNT_MAIL_UUR1%>>
					<%
for (int i = 5; i <= kMaxMailHour; ++i)
  out.println("<option value=\"" + i + "\" " + ((vMailHour1 == i) ? kSelected : "") + ">" + i);
%>
				</select> uur <select name=<%=Constants.ACCOUNT_MAIL_MINUTEN1%>>
					<%
for (int i = 0; i < kMaxMailMinutes; i += 5)
  out.println("<option value=\"" + i + "\" " + ((vMailMinutes1 == i) ? kSelected : "") + ">" + ((i<10) ? "0" : "") + i);
%>
				</select> minuten</td>
			</tr>
			<tr>
				<td width="200" valign="top" class="adminsubsubtitle"><img
					src="/tba/images/blueSphere.gif" width="10"
					height="10">&nbsp;mail 2</td>
				<td width="40" valign="top" class="bodytekst"><input type=checkbox
					name=<%=Constants.ACCOUNT_MAIL_ON2%> value="<%=Constants.YES%>"
					<%=(vIsMailOn2?kChecked:"")%>></td>
				<td width="460" valign="top" class="bodytekst"><select
					name=<%=Constants.ACCOUNT_MAIL_UUR2%>>
					<%
for (int i = 5; i <= kMaxMailHour; ++i)
  out.println("<option value=\"" + i + "\" " + ((vMailHour2 == i) ? kSelected : "") + ">" + i);
%>
				</select> uur <select name=<%=Constants.ACCOUNT_MAIL_MINUTEN2%>>
					<%
for (int i = 0; i < kMaxMailMinutes; i += 5)
  out.println("<option value=\"" + i + "\" " + ((vMailMinutes2 == i) ? kSelected : "") + ">" + ((i<10) ? "0" : "") + i);
%>
				</select> minuten</td>
			</tr>
			<tr>
				<td width="200" valign="top" class="adminsubsubtitle"><img
					src="/tba/images/blueSphere.gif" width="10"
					height="10">&nbsp;mail 3</td>
				<td width="40" valign="top" class="bodytekst"><input type=checkbox
					name=<%=Constants.ACCOUNT_MAIL_ON3%> value="<%=Constants.YES%>"
					<%=(vIsMailOn3?kChecked:"")%>></td>
				<td width="460" valign="top" class="bodytekst"><select
					name=<%=Constants.ACCOUNT_MAIL_UUR3%>>
					<%
for (int i = 5; i <= kMaxMailHour; ++i)
  out.println("<option value=\"" + i + "\" " + ((vMailHour3 == i) ? kSelected : "") + ">" + i);
%>
				</select> uur <select name=<%=Constants.ACCOUNT_MAIL_MINUTEN3%>>
					<%
for (int i = 0; i < kMaxMailMinutes; i += 5)
  out.println("<option value=\"" + i + "\" " + ((vMailMinutes3 == i) ? kSelected : "") + ">" + ((i<10) ? "0" : "") + i);
%>
				</select> minuten</td>
			</tr>
			<br>
			<tr>
				<td width="200" valign="top" class="adminsubsubtitle">Zend geen lege mails</td>
				<td width="40" valign="top" class="bodytekst">
					<input type=checkbox name=<%=Constants.ACCOUNT_NO_EMPTY_MAILS%> value="<%=Constants.YES%>" <%=(vNoEmptyMails?kChecked:"")%>>
				</td>
			</tr>
			<br>
			<tr>
				<td width="200" valign="top" class="adminsubsubtitle">Zend in text format</td>
				<td width="40" valign="top" class="bodytekst">
					<input type=checkbox name=<%=Constants.ACCOUNT_TEXT_MAIL%> value="<%=Constants.YES%>" <%=(vTextMail?kChecked:"")%>>
				</td>
			</tr>
            <br>
            <tr>
                <td width="200" valign="top" class="adminsubsubtitle">Verstuur mijn factuur per mail</td>
                <td width="40" valign="top" class="bodytekst">
                    <input type=checkbox name=<%=Constants.ACCOUNT_IS_MAIL_INVOICE%> value="<%=Constants.YES%>" <%=(vInvoicePerMail?kChecked:"")%>>
                </td>
            </tr>
			<%
if (vCustomer.getIs3W())
{
%>

			<tr>
				<td width="200" valign="top" class="adminsubsubtitle"><img
					src="/tba/images/blueSphere.gif" width="10"
					height="10">&nbsp;3W settings</td>
				<td width="150" valign="top" class="bodytekst"><select
					name=<%=Constants.ACCOUNT_3W_COMPANY_ID%>>
					<option value="<%=Constants.NONE%>">geen selectie
					<option value="3W" <%=(vW3_CompanyId.equals("3W")? kSelected:"")%>>3W
					Associates
					<option value="3WFINANCE "
						<%=(vW3_CompanyId.equals("3WFINANCE")?kSelected:"")%>>3W Finance
					
					<option value="3WICT "
						<%=(vW3_CompanyId.equals("3WICT")?kSelected:"")%>>3W ICT
				</select></td>
				<td width="150" valign="top" class="bodytekst">3W Persoon ID</td>
				<td width="200" valign="top"><input type=text
					name=<%=Constants.ACCOUNT_3W_PERSON_ID%> size=20
					value="<%=v3W_PersonId%>"></td>
			</tr>
			<%
}
%>
		</table>
		<br>
		<input class="tbabutton" type=reset value=" Blad Wissen "> 
        <input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.SAVE_PREFS%>"> 
        <input class="tbabutton" type=submit name=action value="Bewaar" onclick="Bewaar()"></form>
		</span></td>
	</tr>
	<%
}
catch (Exception e)
{
e.printStackTrace();
}

%>
<script type="text/javascript">
function Bewaar()
{
  document.updateForm.<%=Constants.SRV_ACTION%>.value="<%=Constants.SAVE_PREFS%>";
}
</script>
</table>

</body>

</html>

