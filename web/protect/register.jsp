<html>
<head>
<title>TheBusinessAssistant administrator pages</title>
<meta name="Keywords"
	content="virtueel secretariaat telefoondiensten antwoorddiensten kantoor automatisering tekstverwerking administratie afsprakendienst dactylo">
<meta name="Description" content="Uw virtueel secretariaat.">
<meta name="Owner" content="yves.willems@theBusinessAssistant.be">
<meta HTTP-EQUIV="Content-Language" content="NL">
<!-- <meta HTTP-EQUIV="Refresh" content="30">-->
<meta name="Copyright"
	content="Copyright © 2003 TheBusinessAssistant, All rights reserved.">
<meta name="Distribution" content="Global">
<link rel="shortcut icon" href="/tba/images/favicon.png" type="image/x-icon" />
<link rel="stylesheet" type="text/css"
	href="TheBusinessAssistant.css" title="main">
</head>
<%@ page import="java.util.*,
be.tba.util.constants.*"%>

<body>

<table border="0" cellspacing="0" cellpadding="0" bgcolor="FFFFFF">

	<!--header 1-->

	<tr>
		<td>
		<table width="100%" height="50" border="0" cellspacing="0"
			cellpadding="0" bgcolor="#FFFFFF">
			<td><img src="/tba/images/TBA-Logo.png" style="padding:10px;" height="70" alt=""></td>
		</table>
		</td>
	</tr>
    <tr>
        <td style="padding: 0px;" colspan="0" bgcolor="#F89920" height="2"></td>
    </tr>

<%
try
{
  Collection<String> vErrorList = (Collection<String>) request.getAttribute(Constants.ERROR_VECTOR);
%>
	<!--Customer Register window-->
   <tr>
	<td>
    <form name="loginform" method="POST" action="/tba/Login"><br>
	<table border="0" cellpadding="0" cellspacing="0" width="580">
		<tr>
			<td valign="top" width="60" bgcolor="FFFFFF"></td>
			<td valign="top" bgcolor="FFFFFF"><br>
			<%            
      if (vErrorList != null && vErrorList.size() > 0)
      {
        out.println("<br><span class=\"bodyredbold\">");
        for (Iterator<String> i = vErrorList.iterator(); i.hasNext();)
        {
          out.println("<img src=\"/tba/images/blueVink.gif\" border=\"0\">" + i.next() + "<br>");
        }
        out.println("</span>");
      }
      else
      {
        out.println("<br><span class=\"adminsubtitle\"> Om te registreren hebt u een registratiecode nodig. Contacteer ons voor meer informatie.</span>");
      }
%> <br>
			<p><span class="bodytekst"> <!-- action name must be a URI name as it is set in the <application>.xml servlet-mapping tag.-->
			Vul hier uw registratiecode in die je van The Business Assistant bekomen hebt.<br>
			<table border="0" cellspacing="2" cellpadding="2">
				<tr>
					<td width="120" valign="top" class="bodyredbold">registratiecode</td>
					<td width="350" valign="top"><input type=text
						name=<%=Constants.ACCOUNT_REGCODE%> size=10></td>
				</tr>
			</table>
			<br>
			Uw login naam moet tussen de 5 en 10 karakters bevatten.<br>
			<table border="0" cellspacing="2" cellpadding="2">
				<tr>
					<td width="120" valign="top" class="bodysubsubtitle">login naam</td>
					<td width="350" valign="top"><input type=text
						name=<%=Constants.ACCOUNT_USERID%> size=10></td>
				</tr>
			</table>
			<br>
			Uw paswoord moet tussen de 6 en 10 karakters bevatten.<br>
			<table border="0" cellspacing="2" cellpadding="2">
				<tr>
					<td width="120" valign="top" class="bodysubsubtitle">paswoord</td>
					<td width="350" valign="top"><input type=password
						name=<%=Constants.ACCOUNT_PASSWORD%> size=8></td>
				</tr>
				<tr>
					<td width="120" valign="top" class="bodysubsubtitle">herhaal uw
					paswoord</td>
					<td width="350" valign="top"><input type=password
						name=<%=Constants.ACCOUNT_PASSWORD2%> size=8></td>
				</tr>
			</table>
			<br>
			<br>
			<input type=hidden name=<%=Constants.SRV_ACTION%>
				value="<%=Constants.ACTION_REGISTER%>"> <input class="tbabutton" type=submit
				name=action value="Registreer">
			</span> <br>
			</p>
			</td>
		</tr>
	</table>
    </form>
	</td>
   </tr>

	<%
}
catch (Exception ex)
{
  ex.printStackTrace();
}
%>

</table>

</body>

</html>

