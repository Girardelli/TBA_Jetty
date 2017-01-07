<html>

<%@ include file="adminheader.jsp" %>


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
be.tba.util.session.AccountCache,
be.tba.util.invoice.InvoiceHelper,
be.tba.util.data.*"%>

	<%

try
{
vSession.setCallingJsp(Constants.ADMIN_SEARCH_JSP);

boolean vCustomerFilterOn = false;

String vCustomerFilter = (String) vSession.getCallFilter().getCustFilter();//request.getParameter(Constants.ACCOUNT_FILTER_CUSTOMER);
if (vCustomerFilter != null)
{
  if (vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL))
    vCustomerFilter = null;
}

InitialContext vContext = new InitialContext();

if (vCustomerFilter == null) vCustomerFilter = Constants.ACCOUNT_FILTER_ALL;

%>
<body>
<table width='100%' cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">
	<tr>
		<!-- white space -->
		<td valign="top" width="20" bgcolor="FFFFFF"></td>

		<!-- account list -->
		<td valign="top" width="825" bgcolor="FFFFFF"><br>
		<p><span class="admintitle"> Oproepen zoeken</span></p>
		<form name="searchform" method="POST"
			action="/TheBusinessAssistant/AdminDispatch"><input type=hidden
			name=<%=Constants.SRV_ACTION%>
			value="<%=Constants.GOTO_RECORD_SEARCH%>"> 
		<table width="825" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td width="225" valign="top" class="adminsubtitle">&nbsp;Klant</td>
				<td width="10" valign="top">:</td>
				<td width="590" valign="top"><select
					name="<%=Constants.ACCOUNT_FILTER_CUSTOMER%>">
					<%
out.println("<option value=\"" + Constants.ACCOUNT_FILTER_ALL + (vCustomerFilter.equals(Constants.ACCOUNT_FILTER_ALL) ? "\" selected>" : "\">") + "selecteer klant");
					Collection list = AccountCache.getInstance().getCustomerList();
					synchronized(list) 
					{
					    for (Iterator vIter = list.iterator(); vIter.hasNext();)
					    {
					        AccountEntityData vData = (AccountEntityData) vIter.next();
					        out.println("<option value=\"" + vData.getFwdNumber() + (vCustomerFilter.equals(vData.getFwdNumber()) ? "\" selected>" : "\">") + vData.getFullName());
					    }
					}
%>
				</select></td>
			</tr>
			<tr>
				<td width="225" valign="top" class="adminsubtitle">&nbsp;Zoek tekst</td>
				<td width="10" valign="top">:</td>
				<td width="590" valign="top"><input type=text size=50
					name=<%=Constants.RECORD_SEARCH_STR%>
					value="<%=vSession.getSearchString()%>"></td>
			</tr>
		</table>
		<br>
		<input type=submit name=action value="  Go  " onclick="startSearch()">
		<br>
		<br>
		<input type=submit name=action value="Vorige Oproepen"
			onclick="showPrevious()">&nbsp;&nbsp;&nbsp; <%
if (!vSession.isCurrentMonth())
{
  out.println("<input type=submit name=action value=\"Volgende Oproepen\"  onclick=\"showNext()\">");
}
%> <br>
		<br>
		<table width="825" border="0" cellspacing="2" cellpadding="4">
			<%

Collection vRecords = null;
if (vSession.getSearchString() != null && vSession.getSearchString().length() > 0)
{
  CallRecordSqlAdapter vQuerySession = new CallRecordSqlAdapter();

  vRecords = vQuerySession.getSearchCalls(vSession, vCustomerFilter, vSession.getSearchString(), vSession.getMonthsBack(), vSession.getYear());

  if (vRecords == null || vRecords.size() == 0)
  {
      if (vSession.isCurrentMonth())
      {
          out.println("<br><span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Geen oproepen gevonden voor deze maand.</span><br>");
      }
      else
      {
          out.println("<br><span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Geen oproepen gevonden voor maand " + vSession.getMonthsBackString() + ", " + vSession.getYear() + ".</span><br>");
      }
  }
  else
  {
      if (!vSession.isCurrentMonth())
      {
         out.println("<br><span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + vRecords.size() + " zoekresultaten voor de maand " + vSession.getMonthsBackString() + ", " + vSession.getYear() + ".</span><br>");
      }
      else
      {
         out.println("<br><span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + vRecords.size() + " zoekresultaten voor deze maand.</span><br>");
      }
    out.println("              <br><tr>");
    out.println("                <td width=\"20\" bgcolor=\"FFFFFF\"></td>");
    out.println("                <td width=\"10\" valign=\"top\" class=\"topMenu\" bgcolor=\"FF9900\"></td>");
    out.println("                <td width=\"55\"  valign=\"top\" class=\"topMenu\" bgcolor=\"FF9900\">&nbsp;Datum</td>");
    out.println("                <td width=\"35\"  valign=\"top\" class=\"topMenu\" bgcolor=\"FF9900\">&nbsp;Uur</td>");
    out.println("                <td width=\"85\"  valign=\"top\" class=\"topMenu\" bgcolor=\"FF9900\">&nbsp;Nummer</td>");
    out.println("                <td width=\"140\" valign=\"top\" class=\"topMenu\" bgcolor=\"FF9900\">&nbsp;Naam</td>");
    out.println("                <td width=\"380\" valign=\"top\" class=\"topMenu\" bgcolor=\"FF9900\">&nbsp;Omschrijving</td>");
    out.println("                <td width=\"100\"  valign=\"top\" class=\"topMenu\" bgcolor=\"FF9900\">&nbsp;Infos</td>");
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
      String vStyleStart = "";
      String vStyleEnd = "";
      String vInOut;
      if (vEntry.getIsIncomingCall())
        vInOut = "\"/TheBusinessAssistant/images/incall.gif\"";
      else
        vInOut = "\"/TheBusinessAssistant/images/outcall.gif\"";
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
			<tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst"
				onmouseover="hooverOnRow('<%=vId%>','<%=vRowInd%>')"
				onmouseout="hooverOffRow('<%=vId%>','<%=vRowInd%>')"
				ondblclick="changeUrl('/TheBusinessAssistant/AdminDispatch?<%=Constants.SRV_ACTION%>=<%=Constants.RECORD_UPDATE%>&<%=Constants.RECORD_ID%>=<%=vEntry.getId()%>');">
				<td width="20" bgcolor="FFFFFF"><img src=<%=vInOut%> height="13"
					border="0"></td>
				<td width="10" valign="top"><%=vImportant%></td>
				<td width="55" valign="top"><%=vStyleStart%><%=vDate%><%=vStyleEnd%></td>
				<td width="35" valign="top"><%=vStyleStart%><%=vTime%><%=vStyleEnd%></td>
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
</table>

</body>

</html>

