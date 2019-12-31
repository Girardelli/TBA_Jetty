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


javax.naming.Context,
javax.naming.InitialContext,

javax.ejb.*,
be.tba.ejb.account.interfaces.*,
be.tba.ejb.pbx.interfaces.*,
be.tba.ejb.pbx.session.CallRecordSqlAdapter,
be.tba.util.constants.EjbJndiNames,
be.tba.util.constants.Constants,
be.tba.util.exceptions.AccessDeniedException,
be.tba.servlets.session.SessionManager,
be.tba.util.invoice.InvoiceHelper,
be.tba.util.session.AccountCache,
be.tba.util.data.*"%>
<%
try
{


if (vSession == null)
  throw new AccessDeniedException("U bent niet aangemeld bij deze administratie pagina's.");
vSession.setCallingJsp(Constants.CLIENT_SEARCH_JSP);  
AccountEntityData vAccount = AccountCache.getInstance().get(vSession.getFwdNumber());

boolean vCustomerFilterOn = false;

String vCustomerFilter = (String) vSession.getFwdNumber();//request.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER);
if (vCustomerFilter != null)
{
  if (vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL))
    vCustomerFilter = null;
}

if (vCustomerFilter == null) vCustomerFilter = Constants.ACCOUNT_FILTER_ALL;

%>
<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
    <tr>
		<!-- white space -->
		<td valign="top" width="20" bgcolor="FFFFFF"></td>

		<!-- account list -->
		<td valign="top" width="710" bgcolor="FFFFFF"><br>
		<p><span class="admintitle"> Oproepen zoeken</span></p>
		<form name="searchform" method="POST" action="/tba/CustomerDispatch">
			<input type="hidden" name="<%=Constants.SRV_ACTION%>" value="<%=Constants.ACTION_SEARCH_CALLS%>"> 
		<table border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="100" valign="middle" class="adminsubsubtitle">Zoek tekst</td>
				<td width="10" valign="middle" class="adminsubsubtitle">:</td>
				<td width="500" valign="middle">
                    <input type="text" name="<%=Constants.RECORD_SEARCH_STR%>" size="50" value="<%=vSession.getSearchString()%>">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <input class="tbabutton" type="submit" name="action" value=" Start " onclick="startSearch()"> 
                </td>
			</tr>
		</table>
		<br>
		<br>
<%
if (vSession.getSearchString() != null && !vSession.getSearchString().isEmpty())
{
%> 
		<input class="tbabutton" type="submit" name="action" value="Vorige Oproepen" onclick="showPrevious()">&nbsp;&nbsp;&nbsp; 
<%
}
if (!vSession.isCurrentMonth() && (vSession.getSearchString() != null && !vSession.getSearchString().isEmpty()))
{
  out.println("<input class=\"tbabutton\" type=submit name=action value=\"Volgende Oproepen\"  onclick=\"showNext()\">");
}
%> 
        <br>
		<br>
		<table border="0" cellspacing="2" cellpadding="4">
<%


Collection<CallRecordEntityData> vRecords = null;
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
      %>
    <br><tr>
    <td width="20" bgcolor="FFFFFF"></td>
    <td width="10" valign="top" class="topMenu" bgcolor="F89920"></td>
    <td width="55" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Datum</td>
    <td width="35" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Uur</td>
    <%
    if (vAccount != null && vAccount.getHasSubCustomers())
    {
        %>
        <td width="200"  valign="top" class="topMenu" bgcolor="F89920">&nbsp;Medewerker</td>
        <%
    }
    %>
    <td width="85" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Nummer</td>
    <td width="140" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Naam</td>
    <td width="280" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Omschrijving</td>
    <td width="100" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Infos</td>
    </tr>
<%

    int vRowInd = 0;

    for (Iterator<CallRecordEntityData> i = vRecords.iterator(); i.hasNext();)
    {
      
      CallRecordEntityData vEntry = i.next();

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
        vInOut = "/tba/images/incall.gif";
      else
        vInOut = "/tba/images/outcall.gif";
      String vInfoGifs = "";
      String bgrncolor = "FFCC66";
      if (vEntry.getIsArchived())
         bgrncolor = "9fc5e6";
      if (vLongDesc.length() > 0)
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/info.gif\" alt=\"dubbel klik om de info te bekijken\" height=\"16\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsAgendaCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/agenda.gif\" height=\"13\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsSmsCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/sms.gif\"  height=\"13\" border=\"0\">&nbsp");
      }
      if (vEntry.getIsForwardCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/telefoon.gif\"  height=\"13\" border=\"0\">&nbsp;");
      }
      if (vEntry.getIsFaxCall())
      {
        vInfoGifs = vInfoGifs.concat("<img src=\"/tba/images/fax.gif\"  height=\"13\" border=\"0\">&nbsp;");
      }
      String vImportant = "";
      if (vEntry.getIsImportantCall())
      {
        vImportant = vImportant.concat("<img src=\"/tba/images/important.gif\"  height=\"13\" border=\"0\">&nbsp;");
      }
  %>
			<tr bgcolor="<%=bgrncolor%>" id="<%=vId%>" class="bodytekst"
				ondblclick="changeUrl('/tba/CustomerDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACTION_GOTO_RECORD_UPDATE%>&amp;<%=Constants.RECORD_ID%>=<%=vEntry.getId()%>');">
				<td width="20" bgcolor="FFFFFF"><img src="<%=vInOut%>"
					height="13" border="0" alt=""></td>
				<td width="10" valign="top"><%=vImportant%></td>
				<td width="55" valign="top"><%=vDate%></td>
				<td width="35" valign="top"><%=vTime%></td>
<%
if (vAccount != null && vAccount.getHasSubCustomers())
{
    if (vEntry.getAccountId() > 0)
    {
    %>
        <td width="200" valign="top">&nbsp;<%=AccountCache.getInstance().get(vEntry.getAccountId()).getFullName()%></td>
    <%
    }
    else
    {
    %>
        <td width="200" valign="top">&nbsp;</td>
    <%
    }
}
%>
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
