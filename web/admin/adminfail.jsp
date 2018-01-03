<html>
<head>
<title>TheBusinessAssistant administrator pages</title>
<meta name="Keywords"
	content="virtueel secretariaat telefoondiensten antwoorddiensten kantoor automatisering tekstverwerking administratie afsprakendienst dactylo">
<meta name="Description" content="Uw virtueel secretariaat.">
<meta name="Owner" content="yves.willems@theBusinessAssistant.be">
<meta HTTP-EQUIV="Content-Language" content="NL">
<meta name="Copyright"
	content="Copyright © 2003 TheBusinessAssistant, All rights reserved.">
<meta name="Distribution" content="Global">
<link rel="shortcut icon" href="./images/favicon.png" type="image/x-icon" />
<link rel="stylesheet" type="text/css"
	href="/TheBusinessAssistant/TheBusinessAssistant.css" title="main">
</head>

<body>

<table border="0" cellspacing="0" cellpadding="0" bgcolor="FFFFFF">

	<!--header 1-->

	<tr>
		<td>
		<table width="100%" height="50" border="0" cellspacing="0"
			cellpadding="0" bgcolor="#FFFFFF">
			<td><img src="/TheBusinessAssistant/images/TBA-Logo.png" style="padding:10px;" height="70" alt=""></td>
		</table>
		</td>
	</tr>
	<%--
File: adminfail.jsp
Description:  

Copyright ( c ) 2003 TheBusinessAssistant.  All rights reserved.
Version: $Revision: 1.0 $
Last Checked In: $Date: 2003/06/18 04:11:35 $
Last Checked In By: $Author: Yves Willems $
--%>

	<%@ page isErrorPage="true" session="false"
		import="be.tba.util.constants.*,
be.tba.util.session.*"%>

	<%
if (exception == null)
  System.out.println("adminfail.jsp: no exception included.");
else
  System.out.println("adminfail.jsp: " + exception.toString());
String vErrorMessage = (String) request.getAttribute(Constants.ERROR_TXT);
if (vErrorMessage == null && exception != null)
  vErrorMessage = exception.getMessage();
if (vErrorMessage == null)
  vErrorMessage = "Een error werd gedetecteerd.";

%>

	<tr valign="top">
		<td>
		<table border="0" cellspacing="0" cellpadding="0">

			<!-- white space -->
			<td valign="top" width="20" bgcolor="FFFFFF"></td>

			<!-- account list -->
			<td valign="top" width="750" bgcolor="FFFFFF"><br>
			<p><span class="bodysubtitle"> <%= vErrorMessage%></span></p>
			<br>
			<p><span class="bodytext">klik <a
				href=<%=Constants.ADMIN_LOGIN_HTML%>>hier</a> om terug te keren naar
			het adminstratie aanmeldpanel.</p>
			</td>
		</table>
		</td>
	</tr>

</table>

</body>

</html>

