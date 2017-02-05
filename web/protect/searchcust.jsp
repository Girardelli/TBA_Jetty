<html>
<%@ include file="protheader.jsp" %>

<head>
</head>

<%--


File: search.jsp
Description:  admin page that allows to select a customer and month and generate (and mail) the invoice details.

Copyright ( c ) 2003 TheBusinessAssistant.  All rights reserved.
Version: $Revision: 1.0 $
Last Checked In: $Date: 2003/06/18 04:11:35 $
Last Checked In By: $Author: Yves Willems $
--%>
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
be.tba.ejb.pbx.interfaces.*,
be.tba.ejb.pbx.session.CallRecordSqlAdapter,
be.tba.util.constants.EjbJndiNames,
be.tba.util.constants.Constants,
be.tba.util.exceptions.AccessDeniedException,
be.tba.servlets.session.SessionManager,
be.tba.util.invoice.InvoiceHelper,
be.tba.util.data.*"%>


	<%

try
{


if (vSession == null)
  throw new AccessDeniedException("U bent niet aangemeld bij deze administratie pagina's.");

boolean vCustomerFilterOn = false;

String vCustomerFilter = (String) vSession.getFwdNumber();//request.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER);
if (vCustomerFilter != null)
{
  if (vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL))
    vCustomerFilter = null;
}

if (vCustomerFilter == null) vCustomerFilter = Constants.ACCOUNT_FILTER_ALL;

%>
<table width='100%' cellspacing='0' cellpadding='0' border='0'
    bgcolor="FFFFFF">
    <tr>
		<!-- white space -->
		<td valign="top" width="20" bgcolor="FFFFFF"></td>

		<!-- account list -->
		<td valign="top" width="710" bgcolor="FFFFFF"><br>
		<p><span class="admintitle"> Oproepen zoeken</span></p>
		<form name="searchform" method="POST" action="/TheBusinessAssistant/CustomerDispatch">
			<input type="hidden" name="<%=Constants.SRV_ACTION%>" value="<%=Constants.ACTION_SEARCH_CALLS%>"> 
		<table width="710" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td width="200" valign="top" class="adminsubsubtitle">&nbsp;Zoek tekst</td>
				<td width="10" valign="top" class="adminsubsubtitle">:</td>
				<td width="500" valign="top"><input type="text"
					name="<%=Constants.RECORD_SEARCH_STR%>" size="50"
					value="<%=vSession.getSearchString()%>"></td>
			</tr>
		</table>
		<br>
		<input type="submit" name="action" value="  Go  "
			onclick="startSearch()"> <br>
		<br>
		<input type="submit" name="action" value="Vorige Oproepen"
			onclick="showPrevious()">&nbsp;&nbsp;&nbsp; <%
if (!vSession.isCurrentMonth())
{
  out.println("<input type=submit name=action value=\"Volgende Oproepen\"  onclick=\"showNext()\">");
}
%> <br>
		<br>
		<table width="710" border="0" cellspacing="2" cellpadding="4">
			<%


Collection vRecords = null;
if (vSession.getSearchString() != null && vSession.getSearchString().length() > 0)
{
  CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();

  vRecords = vQuerySession.getSearchCalls(vSession, vCustomerFilter, vSession.getSearchString(), vSession.getMonthsBack(), vSession.getYear());

  out.println("<p><span class=\"adminsubsubtitle\"> Zoekresultaat voor tekst \"" + vSession.getSearchString() + "\" : </span>");

  if (vRecords == null || vRecords.size() == 0)
  {
      if (vSession.isCurrentMonth())
      {
          out.println("<span class=\"adminsubsubtitle\">Geen oproepen gevonden voor deze maand.</span><br>");
      }
      else
      {
          out.println("<span class=\"adminsubsubtitle\">Geen oproepen gevonden voor maand " + Constants.MONTHS[vSession.getMonthsBack()] + ", " + vSession.getYear() + ".</span><br>");
      }
  }
  else
  {
      if (!vSession.isCurrentMonth())
      {
         out.println("<span class=\"adminsubsubtitle\">" + vRecords.size() + " zoekresultaten voor de maand " + Constants.MONTHS[vSession.getMonthsBack()] + ", " + vSession.getYear() + ".</span><br>");
      }
      else
      {
         out.println("<span class=\"adminsubsubtitle\">" + vRecords.size() + " zoekresultaten voor deze maand.</span><br>");
      }
    out.println("              <br><tr>");
    out.println("                <td width=\"20\" bgcolor=\"FFFFFF\"></td>");
    out.println("                <td width=\"10\" valign=\"top\" class=\"topMenu\" bgcolor=\"FF9900\"></td>");
    out.println("                <td width=\"55\" valign=\"top\" class=\"topMenu\" bgcolor=\"FF9900\">&nbsp;Datum</td>");
    out.println("                <td width=\"35\" valign=\"top\" class=\"topMenu\" bgcolor=\"FF9900\">&nbsp;Uur</td>");
    out.println("                <td width=\"85\" valign=\"top\" class=\"topMenu\" bgcolor=\"FF9900\">&nbsp;Nummer</td>");
    out.println("                <td width=\"140\" valign=\"top\" class=\"topMenu\" bgcolor=\"FF9900\">&nbsp;Naam</td>");
    out.println("                <td width=\"280\" valign=\"top\" class=\"topMenu\" bgcolor=\"FF9900\">&nbsp;Omschrijving</td>");
    out.println("                <td width=\"100\" valign=\"top\" class=\"topMenu\" bgcolor=\"FF9900\">&nbsp;Infos</td>");
    out.println("              </tr>");


    int vRowInd = 0;

    for (Iterator i = vRecords.iterator(); i.hasNext();)
    {
      
      CallRecordEntityData vEntry = (CallRecordEntityData) i.next();

      String vId = "id" + vEntry.getId();
      String vDate = vEntry.getDate();
      String vTime = vEntry.getTime();
      String vNumber = vEntry.getNumber();
      String vName = vEntry.getName();
      vName = vName == null ? "" : vName;
      String vShortDesc = (String) vEntry.getShortDescription();
      vShortDesc = vShortDesc == null ? "" : vShortDesc;
      String vLongDesc = (String) vEntry.getLongDescription();
      vLongDesc = vLongDesc == null ? "" : vLongDesc;
      String vInOut;
      if (vEntry.getIsIncomingCall())
        vInOut = "/TheBusinessAssistant/images/incall.gif";
      else
        vInOut = "/TheBusinessAssistant/images/outcall.gif";
      String vInfoGifs = "";
      if (vLongDesc.length() > 0)
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/TheBusinessAssistant/images/info.gif\" alt=\"dubbel klik om de info te bekijken\" height=\"16\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsAgendaCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/TheBusinessAssistant/images/agenda.gif\" height=\"13\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsSmsCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/TheBusinessAssistant/images/sms.gif\"  height=\"13\" border=\"0\">&nbsp");
      }
      if (vEntry.getIsForwardCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/TheBusinessAssistant/images/telefoon.gif\"  height=\"13\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsFaxCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/TheBusinessAssistant/images/fax.gif\"  height=\"13\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIs3W_call())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/TheBusinessAssistant/images/3w.gif\"  height=\"13\" border=\"0\">&nbsp;");
      }
      String vImportant = "";
      if (vEntry.getIsImportantCall())
      {
        vImportant = vImportant.concat("<img src=\"/TheBusinessAssistant/images/important.gif\"  height=\"13\" border=\"0\">&nbsp;");
      }
  %>
			<tr bgcolor="FFCC66" id="<%=vId%>" class="bodytekst"
				ondblclick="changeUrl('/TheBusinessAssistant/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.RECORD_UPDATE%>&amp;<%=Constants.RECORD_ID%>=<%=vEntry.getId()%>');">
				<td width="20" bgcolor="FFFFFF"><img src="<%=vInOut%>"
					height="13" border="0" alt=""></td>
				<td width="10" valign="top"><%=vImportant%></td>
				<td width="55" valign="top"><%=vDate%></td>
				<td width="35" valign="top"><%=vTime%></td>
				<td width="85" valign="top"><%=vNumber%></td>
				<td width="140" valign="top"><%=vName%></td>
				<td width="280" valign="top"><%=vShortDesc%></td>
				<td width="100" valign="top"><%=vInfoGifs%></td>
			</tr>
			<%
      ++vRowInd;

    }
  }
}
}
catch (Exception e)
{
e.printStackTrace();
}
%>
		</table>
		</form>
		</td>
	</tr>


</table>

</body>

</html>

<script type="text/javascript">

function startSearch()
{
  document.searchform.<%=Constants.SRV_ACTION%>.value="<%=Constants.ACTION_SEARCH_CALLS%>";
}

function showPrevious()
{
  document.searchform.<%=Constants.SRV_ACTION%>.value="<%=Constants.SEARCH_SHOW_PREV%>";
}

function showNext()
{
  document.searchform.<%=Constants.SRV_ACTION%>.value="<%=Constants.SEARCH_SHOW_NEXT%>";
}


function changeUrl(newURL) 
{
  location=newURL;
}
</script>