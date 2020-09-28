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
		<table height="50" border="0" cellspacing="0"
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
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<td valign="top" width="60" bgcolor="FFFFFF"></td>
			<td valign="top" bgcolor="FFFFFF"><br>
			<%            
      if (vErrorList != null && vErrorList.size() > 0)
      {
         out.println("<span class='bodysubsubtitle'>Los volgende problemen op:</span>");
         out.println("<br><span class=\"bodyredbold\">");
        for (String str : vErrorList)
        {
          out.println("<img src=\"/tba/images/blueVink.gif\" border=\"0\">" + str + "<br>");
        }
        out.println("</span>");
      }
      else
      {
        out.println("<br><span class=\"bodysubtitle\"> U bent niet geregistreerd. <br>Om te registreren hebt u een registratiecode nodig. Contacteer ons voor meer informatie.</span>");
      }
%> <br>
			<p><span class="bodysubsubtitle"> <!-- action name must be a URI name as it is set in the <application>.xml servlet-mapping tag.-->
			Vul hier uw registratiecode in die je van The Business Assistant bekomen hebt.<br>
            <br>
			<table border="0" cellspacing="2" cellpadding="2">
				<tr>
					<td width="200" valign="top" class="bodyredbold">Registratiecode</td>
					<td width="350" valign="top"><input type=text
						name=<%=Constants.LOGIN_REGCODE%> size=10></td>
				</tr>
			</table>
			<br>
            <table border="0" cellspacing="2" cellpadding="2">
                <tr>
                    <td width="200" valign="top" class="bodysubsubtitle">volledige naam</td>
                    <td width="350" valign="top"><input type=text
                        name=<%=Constants.LOGIN_NAME%> size=60></td>
                </tr>
            </table>
			<br>
            Uw login naam moet tussen de 5 en 10 karakters bevatten.
			<table border="0" cellspacing="2" cellpadding="2">
            <tr>
					<td width="200" valign="top" class="bodysubsubtitle">login naam</td>
					<td width="350" valign="top"><input type=text
						name=<%=Constants.LOGIN_USERID%> size=20></td>
				</tr>
			</table>
			<br>
            Uw paswoord moet minstens 6 karakters bevatten.
			<table border="0" cellspacing="2" cellpadding="2">
				<tr>
					<td width="200" valign="top" class="bodysubsubtitle">paswoord</td>
					<td width="350" valign="top"><input type=password
						name=<%=Constants.LOGIN_PASSWORD%> size=20></td>
				</tr>
				<tr>
					<td width="200" valign="top" class="bodysubsubtitle">herhaal uw	paswoord</td>
					<td width="350" valign="top"><input type=password
						name=<%=Constants.LOGIN_PASSWORD2%> size=20></td>
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

