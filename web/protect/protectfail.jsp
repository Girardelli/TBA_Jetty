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
<link rel="shortcut icon" href="./images/favicon.png" type="image/x-icon" />
<link rel="stylesheet" type="text/css"
	href="TheBusinessAssistant.css" title="main">
</head>

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
	<%--
File: protectfail.jsp
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
if (exception != null)
  System.out.println("protectfail.jsp: " + exception.toString());
else
  System.out.println("protectfail.jsp: no exception included.");
String vErrorMessage = (String) request.getAttribute(Constants.ERROR_TXT);
if (vErrorMessage == null && exception != null)
  vErrorMessage = exception.getMessage();
else if (vErrorMessage == null)
  vErrorMessage = "Een error werd gedetecteerd.";

%>

	<!-- protectfail.jsp -->
	<tr valign="top">
		<td>
		<table border="0" cellspacing="0" cellpadding="0">

			<!-- white space -->
			<td valign="top" width="20" bgcolor="FFFFFF"></td>

			<!-- account list -->
			<td valign="top" width="750" bgcolor="FFFFFF"><br>
			<p><span class="adminsubtitle"> <%= vErrorMessage%></span></p>
			<br>
			<p><span class="bodytext">klik <a href=<%=Constants.LOGIN_HTML%>>hier</a>
			om terug te keren naar het aanmeldpanel.</p>
			</td>
		</table>
		</td>
	</tr>

</table>

</body>

</html>

