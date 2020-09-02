<html>

<%@ include file="adminheader.jsp" %>


	<%@ page
		import="
java.util.*,


javax.naming.Context,
javax.naming.InitialContext,


be.tba.ejb.account.interfaces.*,
be.tba.ejb.pbx.interfaces.*,
be.tba.ejb.pbx.session.CallRecordSqlAdapter,
be.tba.util.constants.EjbJndiNames,
be.tba.util.constants.Constants,
be.tba.util.exceptions.AccessDeniedException,
be.tba.servlets.session.SessionManager,
be.tba.util.session.AccountCache,
be.tba.util.invoice.InvoiceHelper,
be.tba.util.data.*"%>

	<%
	   try
		{
		vSession.setCallingJsp(Constants.ADMIN_SEARCH_JSP);

		boolean vCustomerFilterOn = false;
		AccountEntityData vAccount = AccountCache.getInstance().get(vSession.getCallFilter().getCustFilter());
		String vCustomerFilter = vSession.getCallFilter().getCustFilter();
		if (vCustomerFilter != null)
		{
		  if (vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL))
		    vCustomerFilter = null;
		}
		if (vCustomerFilter == null) vCustomerFilter = Constants.ACCOUNT_FILTER_ALL;
	%>
<body>
<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
	<tr>
		<!-- white space -->
		<td valign="top" width="20" bgcolor="FFFFFF"></td>

		<!-- account list -->
		<td valign="top" width="825" bgcolor="FFFFFF"><br>
		<p><span class="bodytitle"> Oproepen zoeken</span></p>
		<form name="searchform" method="POST" action="/tba/AdminDispatch">
        <input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.GOTO_RECORD_SEARCH%>"> 
		<table width="825" border="0" cellspacing="2" cellpadding="2">
			<tr>
				<td width="225" valign="top" class="bodysubtitle">Klant</td>
				<td width="10" valign="top">:</td>
				<td width="590" valign="top"><select
					name="<%=Constants.ACCOUNT_FILTER_CUSTOMER%>">
					<%
out.println("<option value=\"" + Constants.ACCOUNT_FILTER_ALL + (vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL) ? "\" selected>" : "\">") + "selecteer klant");
					Collection<AccountEntityData> list = AccountCache.getInstance().getCustomerList();
					synchronized(list) 
					{
					    for (Iterator<AccountEntityData> vIter = list.iterator(); vIter.hasNext();)
					    {
					        AccountEntityData vData = (AccountEntityData) vIter.next();
					        out.println("<option value=\"" + vData.getFwdNumber() + (vCustomerFilter.equals(vData.getFwdNumber()) ? "\" selected>" : "\">") + vData.getFullName());
					    }
					}
%>
				</select></td>
			</tr>
			<tr>
				<td width="225" valign="top" class="bodysubtitle">Zoek tekst</td>
				<td width="10" valign="top">:</td>
				<td width="590" valign="top"><input type=text size=50
					name=<%=Constants.RECORD_SEARCH_STR%>
					value="<%=vSession.getSearchString()%>"></td>
			</tr>
		</table>
		<br>
		<input class="tbabutton" type=submit name=action value="  Go  " onclick="startSearch()">
		<br>
		<br>
		<input class="tbabutton" type=submit name=action value="Vorige Oproepen"
			onclick="showPrevious()">&nbsp;&nbsp;&nbsp; <%
if (!vSession.isCurrentMonth())
{
  out.println("<input class=\"tbabutton\" type=submit name=action value=\"Volgende Oproepen\"  onclick=\"showNext()\">");
}
%> <br>
		<br>
		<table width="825" border="0" cellspacing="2" cellpadding="4">
			<%

Collection<CallRecordEntityData> vRecords = null;
if (vSession.getSearchString() != null && vSession.getSearchString().length() > 0)
{
  CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();

  vRecords = vQuerySession.getSearchCalls(vSession, vCustomerFilter, vSession.getSearchString(), vSession.getMonthsBack(), vSession.getYear());

  if (vRecords == null || vRecords.size() == 0)
  {
      if (vSession.isCurrentMonth())
      {
          out.println("<br><span class=\"bodysubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Geen oproepen gevonden voor deze maand.</span><br>");
      }
      else
      {
          out.println("<br><span class=\"bodysubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Geen oproepen gevonden voor maand " + vSession.getMonthsBackString() + ", " + vSession.getYear() + ".</span><br>");
      }
  }
  else
  {
      if (!vSession.isCurrentMonth())
      {
         out.println("<br><span class=\"bodysubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + vRecords.size() + " zoekresultaten voor de maand " + vSession.getMonthsBackString() + ", " + vSession.getYear() + ".</span><br>");
      }
      else
      {
         out.println("<br><span class=\"bodysubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + vRecords.size() + " zoekresultaten voor deze maand.</span><br>");
      }
      %>
    <br><tr>
    <td width="20" bgcolor="FFFFFF"></td>
    <td width="10" valign="top" class="topMenu" bgcolor="F89920"></td>
    <td width="55"  valign="top" class="topMenu" bgcolor="F89920">&nbsp;Datum</td>
    <td width="35"  valign="top" class="topMenu" bgcolor="F89920">&nbsp;Uur</td>
    <%
    if (vAccount != null && vAccount.getHasSubCustomers())
    {
        %>
        <td width="200"  valign="top" class="topMenu" bgcolor="F89920">&nbsp;Medewerker</td>
        <%
    }
    %>
    <td width="85"  valign="top" class="topMenu" bgcolor="F89920">&nbsp;Nummer</td>
    <td width="140" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Naam</td>
    <td width="380" valign="top" class="topMenu" bgcolor="F89920">&nbsp;Omschrijving</td>
    <td width="100"  valign="top" class="topMenu" bgcolor="F89920">&nbsp;Infos</td>
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
      String vStyleStart = "";
      String vStyleEnd = "";
      String vInOut;
      if (vEntry.getIsIncomingCall())
        vInOut = "\"/tba/images/incall.gif\"";
      else
        vInOut = "\"/tba/images/outcall.gif\"";
      String vInfoGifs = "";
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
			<tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst"
				onmouseover="hooverOnRow('<%=vId%>','<%=vRowInd%>')"
				onmouseout="hooverOffRow('<%=vId%>','<%=vRowInd%>')"
				ondblclick="changeUrl('/tba/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.ACTION_GOTO_RECORD_UPDATE%>&<%=Constants.RECORD_ID%>=<%=vEntry.getId()%>');">
				<td width="20" bgcolor="FFFFFF"><img src=<%=vInOut%> height="13"
					border="0"></td>
				<td width="10" valign="top"><%=vImportant%></td>
				<td width="55" valign="top"><%=vStyleStart%><%=vDate%><%=vStyleEnd%></td>
				<td width="35" valign="top"><%=vStyleStart%><%=vTime%><%=vStyleEnd%></td>
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
				<td width="85" valign="top"><%=vStyleStart%><%=vNumber%><%=vStyleEnd%></td>
				<td width="140" valign="top"><%=vStyleStart%><%=vName%><%=vStyleEnd%></td>
				<td width="380" valign="top"><%=vStyleStart%><%=vShortDesc%><%=vStyleEnd%></td>
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


<script type="text/javascript">
	var linesToDelete = new Array();
	
	function hooverOnRow(id, rowInd, colour)
	{
	  var entry = document.getElementById(id) ;
	  if (linesToDelete[rowInd] == null)
	    entry.style.backgroundColor= colour;
	  else
	    entry.style.backgroundColor= "FF9966";
	}

	function hooverOffRow(id, rowInd, colour)
	{
	  var entry = document.getElementById(id) ;
	  if (linesToDelete[rowInd] == null)
	    entry.style.backgroundColor= colour;
	  else
	    entry.style.backgroundColor= "FF9966";
	}

function startSearch()
{
  document.searchform.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_RECORD_SEARCH%>";
}

function changeUrl(newURL) 
{
  location=newURL;
}

function showPrevious()
{
  document.searchform.<%=Constants.SRV_ACTION%>.value="<%=Constants.SEARCH_SHOW_PREV%>";
}

function showNext()
{
  document.searchform.<%=Constants.SRV_ACTION%>.value="<%=Constants.SEARCH_SHOW_NEXT%>";
}

</script>

</html>

