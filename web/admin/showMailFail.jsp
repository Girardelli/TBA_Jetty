<html>
<head>
<title>TheBusinessAssistant administrator pages</title>
<meta name="Keywords"
	content="virtueel secretariaat telefoondiensten antwoorddiensten kantoor automatisering tekstverwerking administratie afsprakendienst dactylo">
<meta name="Description" content="Uw virtueel secretariaat.">
<meta name="Owner" content="yves.willems@theBusinessAssistant.be">
<meta HTTP-EQUIV="Content-Language" content="NL">
<meta name="Copyright"
	content="Copyright � 2003 TheBusinessAssistant, All rights reserved.">
<meta name="Distribution" content="Global">
<link rel="stylesheet" type="text/css"
	href="/TheBusinessAssistant/TheBusinessAssistant.css" title="main">
</head>

<body>

<table border="0" cellspacing="0" cellpadding="0" bgcolor="FFFFFF">

	<!--header 1-->

	<tr>
		<td>
		<table width="100%" height="50" border="0" cellspacing="0"
			cellpadding="0" bgcolor="000066">
			<td height="50"><img
				src="images\tba-lightblue-trans-500-50.gif"
				height="50" border="0" alt=""></td>
		</table>
		</td>
	</tr>
	<%--
File: showMailFail.jsp
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
String vErrorMessage = MailError.getInstance().getError();
if (vErrorMessage == null)
{
	vErrorMessage = "er zijn geen mailer fouten";
}
else
{
	MailError.getInstance().setError(null);
}
%>

	<tr valign="top">
		<td>
		<table border="0" cellspacing="0" cellpadding="0">

			<!-- white space -->
			<td valign="top" width="20" bgcolor="FFFFFF"></td>

			<!-- account list -->
			<td valign="top" width="750" bgcolor="FFFFFF"><br><br><br>
			<p><span class="bodysubtitle"> <%= vErrorMessage%></span></p>
			<br>
			</td>
		</table>
		</td>
	</tr>

</table>

</body>

</html>
