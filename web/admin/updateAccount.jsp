<html>

<%@ include file="adminheader.jsp"%>

<%@ page
	import="
java.util.*,


javax.naming.Context,
javax.naming.InitialContext,


be.tba.ejb.account.interfaces.*,
be.tba.util.data.*,
be.tba.util.invoice.*,
be.tba.util.constants.*,
be.tba.util.exceptions.*,
be.tba.util.session.*"%>
<%!
private static final String kSelected = " selected";
private static final String kChecked = " checked";
private static final int kMaxMailMinutes = 56;
%>
<%
AccountEntityData vCustomer;

try {
vSession.setCallingJsp(Constants.UPDATE_ACCOUNT_JSP);

String accountIdStr = (String) request.getParameter(Constants.ACCOUNT_ID);
if (accountIdStr == null)
  throw new SystemErrorException("Interne fout: Account key null.");
int accountId = Integer.valueOf(accountIdStr);
vCustomer = AccountCache.getInstance().get(accountId);

String vFullName = vCustomer.getFullName();
vFullName = (vFullName == null) ? "" : vFullName;
String vEmail = vCustomer.getEmail();
vEmail = (vEmail == null) ? "" : vEmail;
String vInvoiceEmail = vCustomer.getInvoiceEmail();
vInvoiceEmail = (vInvoiceEmail == null) ? "" : vInvoiceEmail;
String vGsm = vCustomer.getGsm();
vGsm = (vGsm == null) ? "" : vGsm;
String vCallProcessInfo = vCustomer.getCallProcessInfo();



int vMailHour1 = vCustomer.getMailHour1();
boolean vIsMailOn1 = true;
if (vMailHour1 < 1 || vMailHour1 > Constants.MAX_MAIL_HOUR)
{
  vMailHour1 = 1;
  vIsMailOn1 = false;
}
int vMailMinutes1 = vCustomer.getMailMinutes1();
vMailMinutes1 = (vMailMinutes1 < 0 || vMailMinutes1 > kMaxMailMinutes) ? 0 : vMailMinutes1;

int vMailHour2 = vCustomer.getMailHour2();
boolean vIsMailOn2 = true;
if (vMailHour2 < 1 || vMailHour2 > Constants.MAX_MAIL_HOUR)
{
  vMailHour2 = 1;
  vIsMailOn2 = false;
}
int vMailMinutes2 = vCustomer.getMailMinutes2();
vMailMinutes2 = (vMailMinutes2 < 0 || vMailMinutes2 > kMaxMailMinutes) ? 0 : vMailMinutes2;

int vMailHour3 = vCustomer.getMailHour3();
boolean vIsMailOn3 = true;
if (vMailHour3 < 1 || vMailHour3 > Constants.MAX_MAIL_HOUR)
{
  vMailHour3 = 1;
  vIsMailOn3 = false;
}
int vMailMinutes3 = vCustomer.getMailMinutes3();
vMailMinutes3 = (vMailMinutes3 < 0 || vMailMinutes3 > kMaxMailMinutes) ? 0 : vMailMinutes3;

int vFacStdInCall = vCustomer.getFacStdInCall();
int vFacStdOutCall = vCustomer.getFacStdOutCall();
int vFacFaxCall = vCustomer.getFacFaxCall();
int vFacOutLevel1 = vCustomer.getFacOutLevel1();
int vFacOutLevel2 = vCustomer.getFacOutLevel2();
int vFacOutLevel3 = vCustomer.getFacOutLevel3();
int vFacAgendaCall = vCustomer.getFacAgendaCall();
short vInvoiceType = vCustomer.getInvoiceType();

if (vInvoiceType == InvoiceHelper.kCustomInvoice) vInvoiceType = InvoiceHelper.kStandardInvoice;
short vAgendaPriceUnit = vCustomer.getAgendaPriceUnit();
int vFacSms = vCustomer.getFacSms();
int vFacCallForward = vCustomer.getFacCallForward();
int vTaskHourRate = vCustomer.getTaskHourRate();
String vCompanyName = vCustomer.getCompanyName();
String vAttToName = vCustomer.getAttToName();
String vStreet = vCustomer.getStreet();
String vCity = vCustomer.getCity();
String vBtwNumber = vCustomer.getBtwNumber();
String vAccountNr = vCustomer.getAccountNr();
String vCountryCode = vCustomer.getCountryCode();
boolean vNoEmptyMails = vCustomer.getNoEmptyMails();
boolean vTextMail = vCustomer.getTextMail();
double vFacLong = vCustomer.getFacLong();
double vFacLongFwd = vCustomer.getFacLongFwd();
int redirectId = vCustomer.getRedirectAccountId();


%>
<body>
	<table  cellspacing='0' cellpadding='0' border='0'
		bgcolor="FFFFFF">

		<!--Update account jsp-->
		<tr>
			<td valign="top" width="60" bgcolor="FFFFFF"></td>
			<td valign="top" bgcolor="FFFFFF"><br> <br> <span
				class="bodytitle"> Instellingen voor <%=vFullName%>.
			</span> <br> <br><span class="bodytekst"> <!-- action name must be a URI name as it is set in the <application>.xml servlet-mapping tag.-->
					<form name="updateForm" method="POST" action="/tba/AdminDispatch">
						<input class="tbabutton" type=submit name=action value="Bewaar"	onclick="Bewaar()"> 
						<input class="tbabutton" type=submit name=action value="De-registreren" onclick="Deregister()">&nbsp;&nbsp;
						<input class="tbabutton" type=submit name=action value=" Terug " onclick="cancelUpdate()">&nbsp;&nbsp; 
						<input class="tbabutton" type=submit name=action value=" Verstuur Mail " onclick="mailCustomer()"> 
                        <input class="tbabutton" type=submit name=action value=<%=(vCustomer.getIsArchived()?" Dearchiveer ":" Archiveer ")%> onclick="archive()"> 
						<br>
						<p class="bodysubtitle">
							<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Algemene informatie:
						</p>
						<table border="0" cellspacing="2" cellpadding="2">
							<tr>
								<td width="300" valign="top" class="bodysubsubtitle">login	naam</td>
								<td width="700" valign="top" class="bodybold"><%=(vCustomer.getUserId() == null ? "" : vCustomer.getUserId())%></td>
							</tr>
							<tr>
								<td width="300" valign="top" class="bodysubsubtitle">rol</td>
								<td width="700" valign="top" class="bodybold"><%=AccountRole.fromShort(vCustomer.getRole()).getText()%></td>
							</tr>
                            <tr>
                                <td width="300" valign="top" class="bodysubsubtitle">mijn super klant</td>
                                <td width="700" valign="top" class="bodybold"> <%=(vCustomer.getSuperCustomer() != null ? vCustomer.getSuperCustomer() : "-") %> </td>
<!--                             </tr>


								    <select	name=<%=Constants.ACCOUNT_ROLE%>>
<%
/*
					out.println("<option value=\"" + vCustomer.getRole() + "\">" + AccountRole.fromShort(vCustomer.getRole()).getText());
for (Iterator n = AccountRole.iterator(); n.hasNext();)
{
  AccountRole vRole = (AccountRole) n.next();
  if (!(vRole.getShort().equals(vCustomer.getRole())))
    out.println("<option value=\"" + vRole.getShort() + "\">" + vRole.getText());
}
out.println("</select>");
if (vCustomer.getRole().equals(AccountRole._vSubCustomer))
{
    AccountEntityData vSuperCustomer = AccountCache.getInstance().get(vCustomer.getSuperCustomer());
    out.println(" onder <select name=" + Constants.ACCOUNT_SUPER_CUSTOMER + ">");
	if (vSuperCustomer != null)
	{
	    out.println("<option value=\"" + vCustomer.getSuperCustomer() + "\">" + vSuperCustomer.getFullName());
	}
	Collection list = AccountCache.getInstance().getSuperCustomersList();
	synchronized(list) 
	{
	    for (Iterator vIter = list.iterator(); vIter.hasNext();)
	    {
	        vSuperCustomer = AccountCache.getInstance().get((String) vIter.next());
	      	if (!(vSuperCustomer.getFwdNumber().equals(vCustomer.getSuperCustomer())))
		      out.println("<option value=\"" + vSuperCustomer.getFwdNumber() + "\">" + vSuperCustomer.getFullName());
	    }
	}
    out.println("</select>");
}
*/
				%>
								</td>
							</tr> -->
							<tr>
								<td width="300" valign="top" class="bodysubsubtitle">volledige	naam</td>
								<td width="700" valign="top"><input type=text name=<%=Constants.ACCOUNT_FULLNAME%> size=50 value="<%=vFullName%>"></td>
							</tr>
							<tr>
								<td width="300" valign="top" class="bodysubsubtitle">afleidnummer</td>
                        <%
        if (vSession.getRole() == AccountRole.ADMIN)
        {
    %>
								<td width="700" valign="top" class="bodytekst">014/ <select
									name=<%=Constants.ACCOUNT_FORWARD_NUMBER%> >
										<%
out.println("<option value=\"" + vCustomer.getFwdNumber() + "\">" + vCustomer.getFwdNumber());
Collection<String> vFreeNumbers = AccountCache.getInstance().getFreeNumbers();
for (Iterator<String> n = vFreeNumbers.iterator(); n.hasNext();)
{
  String vNumber = n.next();
  if (!(vNumber.equals(vCustomer.getFwdNumber())))
    out.println("<option value=\"" + vNumber + "\">" + vNumber);
}

%>
								</select></td>
                                                    <%
        }
        else
        {
            out.println("<td width=\"500\" valign=\"top\">(014)" + vCustomer.getFwdNumber() + "</td>"); 
        }
    %>
							</tr>
							<tr>
								<td width="300" valign="top" class="bodysubsubtitle">e-mail</td>
								<td width="700" valign="top"><input type=text name=<%=Constants.ACCOUNT_EMAIL%> size=50 value="<%=vEmail%>"></td>
							</tr>
							<tr>
								<td width="300" valign="top" class="bodysubsubtitle">Facturatie
									e-mail</td>
								<td width="700" valign="top"><input type=text
									name=<%=Constants.ACCOUNT_INVOICE_EMAIL%> size=50
									value="<%=vInvoiceEmail%>"></td>
							</tr>
							<tr>
								<td width="300" valign="top" class="bodysubsubtitle">GSM nummer (SMS)</td>
								<td width="700" valign="top"><input type=text
									name=<%=Constants.ACCOUNT_GSM%> size=13 value="<%=vGsm%>"></td>
							</tr>
                            <tr>
                                <td width="300" valign="top" class="bodysubsubtitle">Land code</td>
                                <td width="700" valign="top">
                                <select name=<%=Constants.ACCOUNT_COUNTRY_CODE%>>
<%
for (int i = 0; i < Constants.COUNTRY_CODES[0].length; i++)
{
    out.println("<option value=\"" + Constants.COUNTRY_CODES[0][i] + "\"" + (vCustomer.getCountryCode().equals(Constants.COUNTRY_CODES[0][i])?kSelected:"")  + ">" + Constants.COUNTRY_CODES[1][i]);
}
%>
                                </select></td>                                    
                            </tr>
                        <%
        if (vSession.getRole() == AccountRole.ADMIN)
        {
    %>
							<tr>
								<td width="300" valign="top" class="bodysubsubtitle">Dit is een super klant</td>
								<td width="700" valign="top" class="bodytekst"><input
									type=checkbox name=<%=Constants.ACCOUNT_HAS_SUB_CUSTOMERS%>
									value="<%=Constants.YES%>"
									<%=(vCustomer.getHasSubCustomers()?kChecked:"")%>></td>
							</tr>
                            <tr>
                                <td width="300" valign="top" class="bodysubsubtitle">Verplaats alle oproepen naar</td>
                                <td width="700" valign="top" class="bodytekst">
                                
                           <%
                              out.println("<select name=\"" + Constants.ACCOUNT_REDIRECT_ACCOUNT_ID + "\">");
                              out.println("<option value=\"0\"" + (redirectId == 0?kSelected:"")  + "> functie staat af");
                              Collection<AccountEntityData> list = AccountCache.getInstance().getCustomerList();
                              synchronized (list) {
                                  for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();) {
                                      AccountEntityData vValue = vIter.next();
                                      out.println("<option value=\"" + vValue.getId() + "\"" + (redirectId == vValue.getId()?kSelected:"")  + ">" + vValue.getFullName());
                                  }
                              }
                              out.println("</select>");
                           %>
</td>
                            </tr>
    <%
        }
    %>
                            <tr>
                                <td width="300" valign="top" class="bodysubsubtitle">Info</td>
                                <td width="700" valign="top" class="bodytekst">Gebruik volgende html code om deze tekst te stylen:<br>
                                    &ltb&gt<b>bold tekst</b>&lt/b&gt<br>
                                    &lti&gt<i>italic tekst</i>&lt/i&gt<br>
                                    &ltbr&gt om een nieuwe lijn aan te duiden.<br><br>                                      
                                    <textarea name=<%=Constants.ACCOUNT_INFO%> rows=12 cols=70><%=(String) vCustomer.getCallProcessInfo()%></textarea>
                                </td>
                            </tr>
						</table>
						<%
        if (vSession.getRole() == AccountRole.ADMIN)
        {
    %>

						<p class="bodysubtitle">
							<img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Dagelijkse
							mails met de laatste oproepgegevens om:
						</p>
						<table border="0" cellspacing="2" cellpadding="2">
							<tr>
								<td width="300" valign="top" class="bodysubsubtitle">mail
									1</td>
								<td width="40" valign="top" class="bodytekst"><input
									type=checkbox name=<%=Constants.ACCOUNT_MAIL_ON1%>
									value="<%=Constants.YES%>" <%=(vIsMailOn1?kChecked:"")%>></td>
								<td width="360" valign="top" class="bodytekst"><select
									name=<%=Constants.ACCOUNT_MAIL_UUR1%>>
										<%
for (int i = 0; i <= Constants.MAX_MAIL_HOUR; ++i)
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
								<td width="300" valign="top" class="bodysubsubtitle">mail
									2</td>
								<td width="40" valign="top" class="bodytekst"><input
									type=checkbox name=<%=Constants.ACCOUNT_MAIL_ON2%>
									value="<%=Constants.YES%>" <%=(vIsMailOn2?kChecked:"")%>></td>
								<td width="360" valign="top" class="bodytekst"><select
									name=<%=Constants.ACCOUNT_MAIL_UUR2%>>
										<%
for (int i = 0; i <= Constants.MAX_MAIL_HOUR; ++i)
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
								<td width="300" valign="top" class="bodysubsubtitle">mail
									3</td>
								<td width="40" valign="top" class="bodytekst"><input
									type=checkbox name=<%=Constants.ACCOUNT_MAIL_ON3%>
									value="<%=Constants.YES%>" <%=(vIsMailOn3?kChecked:"")%>></td>
								<td width="360" valign="top" class="bodytekst"><select
									name=<%=Constants.ACCOUNT_MAIL_UUR3%>>
										<%
for (int i = 0; i <= Constants.MAX_MAIL_HOUR; ++i)
  out.println("<option value=\"" + i + "\" " + ((vMailHour3 == i) ? kSelected : "") + ">" + i);
%>
								</select> uur <select name=<%=Constants.ACCOUNT_MAIL_MINUTEN3%>>
										<%
for (int i = 0; i < kMaxMailMinutes; i += 5)
  out.println("<option value=\"" + i + "\" " + ((vMailMinutes3 == i) ? kSelected : "") + ">" + ((i<10) ? "0" : "") + i);
%>
								</select> minuten</td>
							</tr>
							<tr>
								<td width="300" valign="top" class="bodysubsubtitle">Zend
									geen lege mails</td>
								<td width="40" valign="top" class="bodytekst"><input
									type=checkbox name=<%=Constants.ACCOUNT_NO_EMPTY_MAILS%>
									value="<%=Constants.YES%>" <%=(vNoEmptyMails?kChecked:"")%>>
								</td>
							</tr>
							<tr>
								<td width="300" valign="top" class="bodysubsubtitle">Zend
									in text format</td>
								<td width="40" valign="top" class="bodytekst"><input
									type=checkbox name=<%=Constants.ACCOUNT_TEXT_MAIL%>
									value="<%=Constants.YES%>" <%=(vTextMail?kChecked:"")%>>
								</td>
							</tr>
		</table>
		<p class="bodysubtitle"><img src=".\images\blueSphere.gif" width="10"
			height="10">&nbsp;Facturatiegegevens:</p>
		<table name="mainTbl" border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="250" valign="top" class="bodysubsubtitle">Maak GEEN	factuur</td>
				<td width="400" valign="top" class="bodytekst"><input type=checkbox
					name=<%=Constants.ACCOUNT_NO_INVOICE%> value="<%=Constants.YES%>"
					<%=(vCustomer.getNoInvoice()?kChecked:"")%>></td>
			</tr>
			<tr>
				<td width="250" valign="top" class="bodysubsubtitle">Reken geen BTW</td>
				<td width="400" valign="top" class="bodytekst"><input type=checkbox
					name=<%=Constants.ACCOUNT_NO_BTW%> value="<%=Constants.YES%>"
					<%=(vCustomer.getNoBtw()?kChecked:"")%>></td>
			</tr>
			<tr>
				<td width="250" valign="top" class="bodysubsubtitle">Opdrachten uur tarrief</td>
				<td width="400" valign="top" class="bodytekst"><input type=text
					name=<%=Constants.ACCOUNT_TASK_HOUR_RATE%> size=3
					value="<%=vTaskHourRate%>">Euro cent</td>
			</tr>
            <tr>
                <td width="250" valign="top" class="bodysubsubtitle">Factuur per mail</td>
                <td width="400" valign="top" class="bodytekst"><input type=checkbox
                    name=<%=Constants.ACCOUNT_IS_MAIL_INVOICE%> value="<%=Constants.YES%>"
                    <%=(vCustomer.getIsMailInvoice()?kChecked:"")%>></td>
            </tr>
			<tr>
				<td width="250" valign="top" class="bodysubsubtitle">Oproepen facturatie mode</td>
				<td width="400" valign="top" class="bodytekst">
					<input type=radio name=<%=Constants.ACCOUNT_INVOICE_TYPE%>
						value=<%=Constants.INVOICE_TYPE_STD%>
						<%=((vInvoiceType == InvoiceHelper.kStandardInvoice)?kChecked:"")%> onclick="stdFacChanged(<%=Constants.ACCOUNT_INVOICE_TYPE%>.checked)" > standaard facturatie (web pagina prijzen)<br>
					<input type=radio name=<%=Constants.ACCOUNT_INVOICE_TYPE%>
						value=<%=Constants.INVOICE_TYPE_TELEMARK%>
						<%=((vInvoiceType == InvoiceHelper.kTelemarketingInvoice)?kChecked:"")%>> telemarketing<br>
					<input type=radio name=<%=Constants.ACCOUNT_INVOICE_TYPE%>
						value=<%=Constants.INVOICE_NO_CALLS%>
						<%=((vInvoiceType == InvoiceHelper.kNoCallsAccount)?kChecked:"")%>> geen oproepen aanrekenen<br>
					<input type=radio name=<%=Constants.ACCOUNT_INVOICE_TYPE%>
						value=<%=Constants.INVOICE_TYPE_WEEK%>
						<%=((vInvoiceType == InvoiceHelper.kWeekInvoice)?kChecked:"")%>> week tarieven<br>
				</td>
			</tr>
		</table>
		<p class="bodysubtitle"><img src=".\images\blueSphere.gif" width="10"
			height="10">&nbsp;&nbsp;&nbsp;Agenda beheer:</p>
		<table border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="250" valign="top" class="bodysubsubtitle">prijs en type	agenda beheer</td>
				<td width="400" valign="top" class="bodytekst"><input type=text
					name=<%=Constants.ACCOUNT_FAC_AGENDA_CALL%> size=3
					value="<%=vFacAgendaCall%>"> 
					<select	name=<%=Constants.ACCOUNT_FAC_AGENDA_UNIT%>>
					<option value=<%=Constants.NONE%><%=((vAgendaPriceUnit == InvoiceHelper.kNoAgenda)?kSelected:"")%>> geen agenda beheer
					<option value=<%=Constants.AGENDA_STANDARD%><%=((vAgendaPriceUnit == InvoiceHelper.kStandardAgenda)?kSelected:"")%>> standard (<%=Constants.kStandardAgendaCost%> euro)
					<option value=<%=Constants.AGENDA_PERC_PER_CALL%><%=((vAgendaPriceUnit == InvoiceHelper.kPercentageOnAgendaCalls)?kSelected:"")%>> procent per afspraak
					<option value=<%=Constants.AGENDA_PERC_ALL_CALL%><%=((vAgendaPriceUnit == InvoiceHelper.kPercentageOnTotalCallCost)?kSelected:"")%>> procent op oproepenkost
					<option value=<%=Constants.AGENDA_EURO_PER_CALL%><%=((vAgendaPriceUnit == InvoiceHelper.kEuroCentOnAgendaCalls)?kSelected:"")%>> Euro cent per afspraak
					<option value=<%=Constants.AGENDA_EURO_ALL_CALL%><%=((vAgendaPriceUnit == InvoiceHelper.kEuroCentOnAllCalls)?kSelected:"")%>> Euro cent op alle calls
					</select>
				</td>
			</tr>
		</table>
		<p class="bodysubtitle"><img src=".\images\blueSphere.gif" width="10"
			height="10">&nbsp;&nbsp;&nbsp;Settings speciale prijzen:</p>
		<table border="0" cellspacing="2" cellpadding="2" >
			<tr>
				<td width="320" valign="top" class="bodysubsubtitle"></td>
				<td width="200" valign="top" class="bodysubsubtitle">Minimum Calls</td>
                <td width="200" valign="top" class="bodysubsubtitle">Start Kost</td>
                <td width="200" valign="top" class="bodysubsubtitle">Extra Kost per call boven Min Calls</td>
			</tr>
            <tr>
                <td width="320" valign="top" class="bodysubsubtitle">Groep tarief I</td>
                <td width="200" valign="top" class="bodytekst">
                    <input type=text name=<%=Constants.ACCOUNT_FAC_TBL_MIN_CALL_I%> size=3 value="<%=vCustomer.getFacTblMinCalls_I()%>">
                </td>
                <td width="200" valign="top" class="bodytekst">
                    <input type=text name=<%=Constants.ACCOUNT_FAC_TBL_START_COST_I%> size=3 value="<%=vCustomer.getFacTblStartCost_I()%>">&nbsp;&nbsp;Euro
                </td>
                <td width="200" valign="top" class="bodytekst">
                    <input type=text name=<%=Constants.ACCOUNT_FAC_TBL_EXTRA_COST_I%> size=3 value="<%=vCustomer.getFacTblExtraCost_I()%>">&nbsp;&nbsp;Euro
                </td>
            </tr>
            <tr>
                <td width="320" valign="top" class="bodysubsubtitle">Groep tarief II</td>
                <td width="200" valign="top" class="bodytekst">
                    <input type=text name=<%=Constants.ACCOUNT_FAC_TBL_MIN_CALL_II%> size=3 value="<%=vCustomer.getFacTblMinCalls_II()%>">
                </td>
                <td width="200" valign="top" class="bodytekst">
                    <input type=text name=<%=Constants.ACCOUNT_FAC_TBL_START_COST_II%> size=3 value="<%=vCustomer.getFacTblStartCost_II()%>">&nbsp;&nbsp;Euro
                </td>
                <td width="200" valign="top" class="bodytekst">
                    <input type=text name=<%=Constants.ACCOUNT_FAC_TBL_EXTRA_COST_II%> size=3 value="<%=vCustomer.getFacTblExtraCost_II()%>">&nbsp;&nbsp;Euro
                </td>
            </tr>
            <tr>
                <td width="320" valign="top" class="bodysubsubtitle">Groep tarief III</td>
                <td width="200" valign="top" class="bodytekst">
                    <input type=text name=<%=Constants.ACCOUNT_FAC_TBL_MIN_CALL_III%> size=3 value="<%=vCustomer.getFacTblMinCalls_III()%>">
                </td>
                <td width="200" valign="top" class="bodytekst">
                    <input type=text name=<%=Constants.ACCOUNT_FAC_TBL_START_COST_III%> size=3 value="<%=vCustomer.getFacTblStartCost_III()%>">&nbsp;&nbsp;Euro
                </td>
                <td width="200" valign="top" class="bodytekst">
                    <input type=text name=<%=Constants.ACCOUNT_FAC_TBL_EXTRA_COST_III%> size=3 value="<%=vCustomer.getFacTblExtraCost_III()%>">&nbsp;&nbsp;Euro
                </td>
            </tr>
            <tr>
                <td width="320" valign="top" class="bodysubsubtitle">Groep tarief IV (week tarrief)</td>
                <td width="200" valign="top" class="bodytekst">
                    <input type=text name=<%=Constants.ACCOUNT_FAC_TBL_MIN_CALL_IV%> size=3 value="<%=vCustomer.getFacTblMinCalls_IV()%>">
                </td>
                <td width="200" valign="top" class="bodytekst">
                    <input type=text name=<%=Constants.ACCOUNT_FAC_TBL_START_COST_IV%> size=3 value="<%=vCustomer.getFacTblStartCost_IV()%>">&nbsp;&nbsp;Euro
                </td>
                <td width="200" valign="top" class="bodytekst">
                    <input type=text name=<%=Constants.ACCOUNT_FAC_TBL_EXTRA_COST_IV%> size=3 value="<%=vCustomer.getFacTblExtraCost_IV()%>">&nbsp;&nbsp;Euro
                </td>
            </tr>
         </table>
         <table border="0" cellspacing="2" cellpadding="2" >
			<tr>
				<td width="320" valign="top" class="bodysubsubtitle">uitgaande	oproep</td>
				<td width="600" valign="top" class="bodytekst">
				    <input type=text name=<%=Constants.ACCOUNT_FAC_STD_OUT_CALL%> size=3 value="<%=vFacStdOutCall%>">&nbsp;&nbsp;Euro cent</td>
			</tr>
			<tr>
				<td width="320" valign="top" class="bodysubsubtitle">Fax</td>
				<td width="600" valign="top" class="bodytekst"><input type=text
					name=<%=Constants.ACCOUNT_FAC_FAX_CALL%> size=3
					value="<%=vFacFaxCall%>">&nbsp;&nbsp;Euro cent</td>
			</tr>
			<tr>
				<td width="320" valign="top" class="bodysubsubtitle">SMS verzenden</td>
				<td width="600" valign="top" class="bodytekst">
				    <input type=text name=<%=Constants.ACCOUNT_FAC_SMS%> size=3 value="<%=vFacSms%>">&nbsp;&nbsp;Euro cent
				</td>
			</tr>
			<tr>
				<td width="320" valign="top" class="bodysubsubtitle">Oproepdoorschakelen</td>
				<td width="600" valign="top" class="bodytekst"><input type=text
					name=<%=Constants.ACCOUNT_FAC_CALL_FORWARD%> size=3
					value="<%=vFacCallForward%>">&nbsp;&nbsp;Euro cent (dit wordt niet meer aangerekend omdat automatisch buitengaande oproepen worden gemaakt.)</td>
			</tr>
         </table>
         <table border="0" cellspacing="2" cellpadding="2" >
            <tr>
                <td width="320" valign="top" class="bodysubsubtitle">Tel alle lange oproepen</td>
                <td width="100" valign="top" class="bodytekst"><input type=checkbox
                    name=<%=Constants.ACCOUNT_COUNT_ALL_LONG_CALLS%> value="<%=Constants.YES%>"
                    <%=(vCustomer.getCountAllLongCalls()?kChecked:"")%>></td>
                <td width="300" valign="top" class="bodytekst"><input type=text
                    name=<%=Constants.ACCOUNT_FAC_LONG%> size=3
                    value="<%=vFacLong%>">&nbsp;&nbsp;Euro</td>
            </tr>
            <tr>
                <td width="320" valign="top" class="bodysubsubtitle">Tel lange doorgeschakelde oproepen</td>
                <td width="100" valign="top" class="bodytekst"><input type=checkbox
                    name=<%=Constants.ACCOUNT_COUNT_LONG_FWD_CALLS%> value="<%=Constants.YES%>"
                    <%=(vCustomer.getCountLongFwdCalls()?kChecked:"")%>></td>
                <td width="300" valign="top" class="bodytekst"><input type=text
                    name=<%=Constants.ACCOUNT_FAC_LONG_FWD%> size=3
                    value="<%=vFacLongFwd%>">&nbsp;&nbsp;Euro</td>
            </tr>
		</table>
		<p class="bodysubtitle"><img src=".\images\blueSphere.gif" width="10"
			height="10">&nbsp;&nbsp;&nbsp;Settings Telemarketing:</p>
		<table border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="320" valign="top" class="bodysubsubtitle">Level 1 (geen
				succes)</td>
				<td width="400" valign="top" class="bodytekst"><input type=text
					name=<%=Constants.ACCOUNT_FAC_OUT_LEVEL1%> size=3
					value="<%=vFacOutLevel1%>">&nbsp;&nbsp;Euro cent</td>
			</tr>
			<tr>
				<td width="320" valign="top" class="bodysubsubtitle">Level 2
				(contact)</td>
				<td width="400" valign="top" class="bodytekst"><input type=text
					name=<%=Constants.ACCOUNT_FAC_OUT_LEVEL2%> size=3
					value="<%=vFacOutLevel2%>">&nbsp;&nbsp;Euro cent</td>
			</tr>
			<tr>
				<td width="320" valign="top" class="bodysubsubtitle">Level 3
				(afspraak)</td>
				<td width="400" valign="top" class="bodytekst"><input type=text
					name=<%=Constants.ACCOUNT_FAC_OUT_LEVEL3%> size=3
					value="<%=vFacOutLevel3%>">&nbsp;&nbsp;Euro cent</td>
			</tr>
		</table>
    <%
        }
    %>
		<p class="bodysubtitle"><img src=".\images\blueSphere.gif" width="10"
			height="10">&nbsp;Facturatie adres:</p>
		<table border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="300" valign="top" class="bodysubsubtitle">Bedrijfsnaam</td>
				<td width="500" valign="top"><input type=text
					name=<%=Constants.ACCOUNT_COMPANY_NAME%> size=50
					value="<%=vCompanyName%>"></td>
			</tr>
			<tr>
				<td width="300" valign="top" class="bodysubsubtitle">Ter attentie
				van</td>
				<td width="500" valign="top"><input type=text
					name=<%=Constants.ACCOUNT_ATT_TO_NAME%> size=50
					value="<%=vAttToName%>"></td>
			</tr>
			<tr>
				<td width="300" valign="top" class="bodysubsubtitle">Straat en
				nummer</td>
				<td width="500" valign="top"><input type=text
					name=<%=Constants.ACCOUNT_STREET%> size=50 value="<%=vStreet%>"></td>
			</tr>
			<tr>
				<td width="300" valign="top" class="bodysubsubtitle">Postcode en
				Plaats</td>
				<td width="500" valign="top"><input type=text
					name=<%=Constants.ACCOUNT_CITY%> size=50 value="<%=vCity%>"></td>
			</tr>
			<tr>
				<td width="300" valign="top" class="bodysubsubtitle">BTW Number</td>
				<td width="500" valign="top">
                    <input type=text name=<%=Constants.ACCOUNT_BTW_NUMBER%> size=50	value="<%=vBtwNumber%>">
                </td>
			</tr>
            <tr>
                <td width="300" valign="top" class="bodysubsubtitle">Rekeningnummers (',' separated)</td>
                <td width="500" valign="top">
                    <input type=text name=<%=Constants.ACCOUNT_NR%> size=50 value="<%=vAccountNr%>">
                </td>
            </tr>
		</table>
		<br>
		<br>
		<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.GOTO_SAVE_ACCOUNT%>"> 
        <input type=hidden name=<%=Constants.ACCOUNT_ID%> value="<%=accountId%>"> 
        <input type=hidden name=<%=Constants.ACCOUNT_TO_DELETE%> value="<%=accountId%>"> 
        <input class="tbabutton" type=submit name=action value="Bewaar" onclick="Bewaar()"> 
        <input class="tbabutton" type=submit name=action value="De-registreren" onclick="Deregister()">&nbsp;&nbsp;
		<input class="tbabutton" type=submit name=action value="Terug" onclick="cancelUpdate()">
        </form>
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
  document.updateForm.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_SAVE_ACCOUNT%>";
}

function Deregister()
{
  document.updateForm.<%=Constants.SRV_ACTION%>.value="<%=Constants.ACCOUNT_DEREG%>";
}

function cancelUpdate()
{
  document.updateForm.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_ACCOUNT_ADMIN%>";
}

function mailCustomer()
{
  document.updateForm.<%=Constants.SRV_ACTION%>.value="<%=Constants.MAIL_CUSTOMER%>";
}

function archive()
{
	document.updateForm.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_ACCOUNT_DELETE%>";
}


</script>
</table>

</body>

</html>

