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
			<td><img src="/tba/images/TBA-Logo.png" style="padding:10px;" height="70" alt=""></td>
		</table>
		</td>
	</tr>
	<%--
File: register.jsp
Description:  admin page that displays all logged calls.

Copyright ( c ) 2003 TheBusinessAssistant.  All rights reserved.
Version: $Revision: 1.0 $
Last Checked In: $Date: 2003/06/18 04:11:35 $
Last Checked In By: $Author: Yves Willems $
--%>
	<%@ page import="java.util.*,
be.tba.util.constants.*"%>

	<%
try
{
  Vector vErrorList = (Vector) request.getAttribute(Constants.ERROR_VECTOR);
%>
	<!--Customer Register window-->
	<td>
	<table border="0" cellpadding="0" cellspacing="0" width="580">
		<tr>
			<td valign="top" width="60" bgcolor="FFFFFF"></td>
			<td valign="top" bgcolor="FFFFFF"><br>
			<%            
      if (vErrorList != null && vErrorList.size() > 0)
      {
        out.println("<br><span class=\"bodyredbold\">");
        for (Iterator i = vErrorList.iterator(); i.hasNext();)
        {
          out.println("<img src=\"/tba/images/blueVink.gif\" border=\"0\">" + (String) i.next() + "<br>");
        }
        out.println("</span>");
      }
      else
      {
        out.println("<br><span class=\"adminsubtitle\"> Om te registreren hebt u een registratiecode nodig. Contacteer ons voor meer informatie.</span>");
      }
%> <br>
			<p><span class="bodytekst"> <!-- action name must be a URI name as it is set in the <application>.xml servlet-mapping tag.-->
			<form name="loginform" method="GET"
				action="/tba/Login"><br>
			Vul hier uw registratiecode in die je van ons bekomen hebt.<br>
			<table width="520" border="0" cellspacing="2" cellpadding="2">
				<tr>
					<td width="120" valign="top" class="bodyredbold">registratiecode</td>
					<td width="350" valign="top"><input type=text
						name=<%=Constants.ACCOUNT_REGCODE%> size=10></td>
				</tr>
			</table>
			<br>
			Uw login naam moet tussen de 5 en 10 karakters bevatten.<br>
			<table width="520" border="0" cellspacing="2" cellpadding="2">
				<tr>
					<td width="120" valign="top" class="bodysubsubtitle">login naam</td>
					<td width="350" valign="top"><input type=text
						name=<%=Constants.ACCOUNT_USERID%> size=10></td>
				</tr>
			</table>
			<br>
			Uw paswoord moet tussen de 6 en 10 karakters bevatten.<br>
			<table width="520" border="0" cellspacing="2" cellpadding="2">
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
				name=action value="Registreer"> <input class="tbabutton" type=reset></form>
			</span> <br>
			</p>
			</td>
		</tr>
	</table>
	</td>

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

