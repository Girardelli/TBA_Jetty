<html>

<%@ include file="adminheader.jsp" %>

<%@ page 
contentType="text/html;charset=UTF-8" language="java"
		import="javax.ejb.*,
java.util.*,
javax.rmi.PortableRemoteObject,
java.rmi.RemoteException,
javax.naming.Context,
javax.naming.InitialContext,
javax.rmi.PortableRemoteObject,
javax.ejb.*,
be.tba.ejb.account.interfaces.*,
be.tba.ejb.pbx.interfaces.*,
be.tba.util.constants.*,
be.tba.util.session.*,
be.tba.util.data.*,
be.tba.util.invoice.*"%>

<body>
<table width='100%' cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">

	<!--Add Record jsp-->
	<tr>
		<td valign="top" width="30" bgcolor="FFFFFF"></td>
		<td valign="top" bgcolor="FFFFFF"><br>
		<br>
		<span class="bodytekst"> <!-- action name must be a URI name as it is set in the <application>.xml servlet-mapping tag.-->
		<form name="calllistform" method="POST"
			action="/TheBusinessAssistant/AdminDispatch">
		<table width="100%" border="0" cellspacing="1" cellpadding="1">
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Inkomende/uitgaand</td>
				<td width="580" valign="top" class="adminsubsubtitle"><input
					type=radio name=<%=Constants.RECORD_DIR%>
					value="<%=Constants.RECORD_DIR_IN%>" checked>Inkomende oproep
				&nbsp;&nbsp;&nbsp; <input type=radio name=<%=Constants.RECORD_DIR%>
					value="<%=Constants.RECORD_DIR_OUT%>">Uitgaande oproep<br>
				</td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;belangrijk</td>
				<td width="580" valign="top" class="adminsubsubtitle"><input
					type=checkbox name=<%=Constants.RECORD_IMPORTANT%>
					value="<%=Constants.YES%>">&nbsp;&nbsp;<img
					src="/TheBusinessAssistant/images/important.gif"
					alt="belangrijke oproep!" height="13" border="0"></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;SMS verstuurd</td>
				<td width="580" valign="top" class="adminsubsubtitle"><input
					type=checkbox name=<%=Constants.RECORD_SMS%>
					value="<%=Constants.YES%>">&nbsp;&nbsp;<img
					src="/TheBusinessAssistant/images/sms.gif"
					alt="wij hebben een SMS bericht verstuurd ivm deze oproep"
					height="13" border="0"></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;afspraak</td>
				<td width="580" valign="top" class="adminsubsubtitle"><input
					type=checkbox name=<%=Constants.RECORD_AGENDA%>
					value="<%=Constants.YES%>">&nbsp;&nbsp;<img
					src="/TheBusinessAssistant/images/agenda.gif"
					alt="afspraak toegevoegd in uw agenda" height="13" border="0"></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;doorgeschakeld</td>
				<td width="580" valign="top" class="adminsubsubtitle"><input
					type=checkbox name=<%=Constants.RECORD_FORWARD%>
					value="<%=Constants.YES%>">&nbsp;&nbsp;<img
					src="/TheBusinessAssistant/images/telefoon.gif"
					alt="oproep doorgeschakeld naar u" height="13" border="0"></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;fax</td>
				<td width="580" valign="top" class="adminsubsubtitle"><input
					type=checkbox name=<%=Constants.RECORD_FAX%>
					value="<%=Constants.YES%>">&nbsp;&nbsp;<img
					src="/TheBusinessAssistant/images/fax.gif"
					alt="fax" height="13" border="0"></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Klant</td>
				<td width="580" valign="top"><%                  
try
{
vSession.setCallingJsp(Constants.ADD_RECORD_JSP);
out.println("<select name=\"" + Constants.ACCOUNT_FORWARD_NUMBER + "\">");
Collection list = AccountCache.getInstance().getCustomerList();
synchronized(list) 
{
    for (Iterator vIter = list.iterator(); vIter.hasNext();)
    {
        AccountEntityData vValue = (AccountEntityData) vIter.next();
        out.println("<option value=\"" + vValue.getFwdNumber() + "\">" + vValue.getFullName());
    }
}
out.println("</select>");
%>
</td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="170" valign="top" class="adminsubsubtitle"><img
					src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Oproep doorgeschakeld</td>
				<td width="530" valign="top" class="adminsubsubtitle"><input
					type=radio name="<%=Constants.RECORD_INVOICE_LEVEL%>"
					value="<%=Constants.RECORD_LEVEL1%>">prijs 1<br>
				<input type=radio name="<%=Constants.RECORD_INVOICE_LEVEL%>"
					value="<%=Constants.RECORD_LEVEL2%>">prijs 2<br>
				<input type=radio name="<%=Constants.RECORD_INVOICE_LEVEL%>"
					value="<%=Constants.RECORD_LEVEL3%>">prijs 3<br>
				</td>
			</tr>
<%
Calendar vCalendar = Calendar.getInstance();
int vDay = vCalendar.get(Calendar.DAY_OF_MONTH);
int vMonth = vCalendar.get(Calendar.MONTH) + 1;
int vYear = vCalendar.get(Calendar.YEAR) - 2000;
int vHour = vCalendar.get(Calendar.HOUR_OF_DAY);
int vMinutes = vCalendar.get(Calendar.MINUTE);
String vDate = new String(vDay + "/" + vMonth  + ((vYear < 10) ? "/0" : "/") + vYear);
String vTime = new String(vHour + ":" + ((vMinutes < 10) ? "0" : "") + vMinutes);
%>
			<tr valign="top">
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle">Datum</td>
				<td width="580" valign="top"><input type=text size=20
					name=<%=Constants.RECORD_DATE%> value="<%=vDate%>"></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle">Uur</td>
				<td width="580" valign="top"><input type=text size=20
					name=<%=Constants.RECORD_TIME%> value="<%=vTime%>"></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle">Nummer</td>
				<td width="580" valign="top"><input type=text size=20
					name=<%=Constants.RECORD_NUMBER%> value=""></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle">Naam</td>
				<td width="580" valign="top"><input type=text size=30
					name=<%=Constants.RECORD_CALLER_NAME%> value="">
				&nbsp;&nbsp;&nbsp;&nbsp; <input type=text size=20
					name=<%=Constants.RECORD_3W_CUSTOMER_ID%> value=""></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle">Omschrijving</td>
				<td width="580" valign="top"><textarea
					name=<%=Constants.RECORD_SHORT_TEXT%> rows=10 cols=70></textarea></td>
			</tr>
			<tr>
				<td width="50"></td>
				<td width="120" valign="top" class="adminsubsubtitle">Extra
				Informatie</td>
				<td width="580" valign="top"><textarea
					name=<%=Constants.RECORD_LONG_TEXT%> rows=10 cols=70></textarea></td>
			</tr>
		</table>
		<br>
		<br>
		<input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.SAVE_MAN_RECORD%>"> 
		<input class="tbabutton" type=submit name=action value=" Bewaar "> <input class="tbabutton" type=reset> 
		<input class="tbabutton" type=submit value=" Terug " onclick="cancelUpdate();">
		</form>
		</span> <br>
		</td>
	</tr>
</table>
	<%
}
catch (Exception e)
{
  e.printStackTrace();
}
%>

	<script type="text/javascript">

function cancelUpdate()
{
  document.calllistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_RECORD_ADMIN%>";
}
</script>

</body>

</html>

