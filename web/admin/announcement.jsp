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
<link rel="stylesheet" type="text/css"
	href="TheBusinessAssistant.css" title="main">
</head>
<!--
<script language="JavaScript">
var sURL = unescape(window.location.pathname);

function doLoad()
{
    // the timeout value should be the same as in the "refresh" meta-tag
    setTimeout( "refresh()", 30*1000 );
}

function refresh()
{
    //  This version of the refresh function will cause a new
    //  entry in the visitor's history.  It is provided for
    //  those browsers that only support JavaScript 1.0.
    //
    window.location.href = sURL;
}

</script>

<script language="JavaScript1.1">

function refresh()
{
    //  This version does NOT cause an entry in the browser's
    //  page view history.  Most browsers will always retrieve
    //  the document from the web-server whether it is already
    //  in the browsers page-cache or not.
    //
    window.location.replace( sURL );
}

</script>

<script language="JavaScript1.2">

function refresh()
{
    //  This version of the refresh function will be invoked
    //  for browsers that support JavaScript version 1.2
    //

    //  The argument to the location.reload function determines
    //  if the browser should retrieve the document from the
    //  web-server.  In our example all we need to do is cause
    //  the JavaScript block in the document body to be
    //  re-evaluated.  If we needed to pull the document from
    //  the web-server again (such as where the document contents
    //  change dynamically) we would pass the argument as 'true'.
    //
    window.location.reload( false );
}

</script>
<body onload="doLoad()">
-->
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

	<%@ page isErrorPage="false" session="false"
		import="be.tba.util.constants.*,
be.tba.servlets.session.*,
be.tba.servlets.helper.*"%>
	<%
String vNextAction = (String) request.getAttribute(Constants.NEXT_PAGE);
String vErrorMessage = (String) request.getAttribute(Constants.ERROR_TXT);
if (vErrorMessage == null)
  vErrorMessage = "Bent u zeker dat u dit commando wil uitvoeren.";
%>

	<tr valign="top">
		<td>
		<form name="announcement" method="POST"
			action="/TheBusinessAssistant/AdminDispatch"><input type=hidden
			name=<%=Constants.SRV_ACTION%> value="<%=vNextAction%>"> 
		<table border="0" cellspacing="0" cellpadding="0">
			<!-- white space -->
			<td valign="top" width="20" bgcolor="FFFFFF"></td>

			<!-- account list -->
			<td valign="top" width="750" bgcolor="FFFFFF"><br>
			<p><span class="bodysubtitle"> <%= vErrorMessage%></span></p>
			<br>
			<input class="tbabutton" type=submit name=action value="ga verder"></td>
		</table>
		</form>
		</td>
	</tr>
</table>

</body>

</html>

