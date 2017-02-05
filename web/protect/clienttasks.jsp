<html>
<%@ include file="protheader.jsp" %>

<head>
</head>
	<%--
File: clienttasks.jsp
Description:  admin page that displays all logged calls.

Copyright ( c ) 2003 TheBusinessAssistant.  All rights reserved.
Version: $Revision: 1.0 $
Last Checked In: $Date: 2003/06/18 04:11:35 $
Last Checked In By: $Author: Yves Willems $
--%>

	<%@ page
		import="javax.ejb.*,
		java.util.*,javax.rmi.PortableRemoteObject,
		java.rmi.RemoteException,
		javax.naming.Context,
		javax.naming.InitialContext,
		javax.rmi.PortableRemoteObject,
		javax.ejb.*,
		be.tba.ejb.account.interfaces.*,
		be.tba.ejb.task.interfaces.*,
		be.tba.ejb.task.session.TaskSqlAdapter,
		be.tba.util.constants.EjbJndiNames,
		be.tba.util.constants.Constants,
		be.tba.util.exceptions.AccessDeniedException,
		be.tba.servlets.session.SessionManager,
		be.tba.util.session.AccountCache,
		be.tba.util.data.*,
		java.text.*;"%>

<%!private String vAccountKey;%>
<%
	try
	{
%>

<form name="tasklistform" method="GET"
	action="/TheBusinessAssistant/CustomerDispatch">
	<input type=hidden name=<%=Constants.SRV_ACTION%>
		value="<%=Constants.ACTION_SHOW_TASKS%>">
	<table width='100%' cellspacing='0' cellpadding='0' border='0'
		bgcolor="FFFFFF">
		<tr>
			<!-- white space -->
			<td valign="top" width="20" bgcolor="FFFFFF"></td>

			<!-- account list -->
			<td valign="top" width="710" bgcolor="FFFFFF"><br>
			<p>
			    <span class="admintitle"> Uitgevoerde taken:</span>
			</p>			
			 <%
 	InitialContext vContext = new InitialContext();

 		if (vSession == null)
 			throw new AccessDeniedException("U bent niet aangemeld.");

 		if (vSession.getFwdNumber() == null)
 			throw new AccessDeniedException(
 			        "Account nummer not set in session.");

 		Collection vTasks = null;
 		TaskSqlAdapter vTaskSession = new TaskSqlAdapter();

 		vTasks = vTaskSession.getTasksForMonth(vSession, vSession.getFwdNumber(),
 		        vSession.getMonthsBack(), vSession.getYear());

 		AccountEntityData vAccountData = (AccountEntityData) AccountCache
 		        .getInstance().get(vSession.getFwdNumber());
 %> <input type=submit name=action value="Taken van vorige maand"
				onclick="showPrevious()"> <%
 	if (!vSession.isCurrentMonth())
 		{
 			out.println("<input type=submit name=action value=\"Taken van volgende maand\"  onclick=\"showNext()\">");
 		}

 		out.println("<br><br><table width=\"725\" border=\"0\" cellspacing=\"2\" cellpadding=\"2\">");
 		if (vTasks == null || vTasks.size() == 0)
 		{
 			out.println("<span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er zijn geen taken uitgevoerd tijdens de maand "
 			        + Constants.MONTHS[vSession.getMonthsBack()]
 			        + ".</span>");
 			out.println("</table>");
 		}
 		else
 		{

 			if (vTasks != null && vTasks.size() > 0)
 			{
 				if (!vSession.isCurrentMonth())
 				{
 					out.println("<span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
 					        + vTasks.size()
 					        + " taken uitgevoerd tijdens de maand "
 					        + Constants.MONTHS[vSession.getMonthsBack()]
 					        + ".</span><br>");
 				}
 				else
 				{
 					out.println("<span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
 					        + vTasks.size()
 					        + " taken uitgevoerd deze maand.</span><br>");
 				}
 %> <br>
				<table width="100%" border="0" cellspacing="2" cellpadding="2">
					<tr>
						<td width="55" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Datum</td>
						<td width="250" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Omschrijving</td>
						<td width="100" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Minuten</td>
						<td width="100" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Kost</td>
						<td width="60" valign="top" class="topMenu" bgcolor="FF9900">&nbsp;Info</td>
					</tr>

					<%
						DecimalFormat vCostFormatter = new DecimalFormat(
									        "#0.00");
									for (Iterator i = vTasks.iterator(); i.hasNext();)
									{
										TaskEntityData vEntry = ((TaskEntityData) i.next());

										String vId = "id" + vEntry.getId();
										String vKost;
										if (vEntry.getIsFixedPrice())
										{
											System.out.println("fixed price double:"
											        + vEntry.getFixedPrice());
											vKost = new String(vCostFormatter.format(vEntry
											        .getFixedPrice())
											        + "Euro (fixed)");
										}
										else
										{
											double vTaskCost = ((double) vEntry
											        .getTimeSpend() / 60.00)
											        * ((double) vAccountData
											                .getTaskHourRate() / 100.00);
											vKost = new String(vCostFormatter
											        .format(vTaskCost)
											        + "Euro");
										}
										String vTimeSpend;
										if (vEntry.getTimeSpend() == 0)
										{
											vTimeSpend = "-";
										}
										else
										{
											vTimeSpend = new String(vEntry.getTimeSpend()
											        + "min");
										}
										String vInfoGifs = "";
										if (vEntry.getIsRecuring())
										{
											vInfoGifs = vInfoGifs
											        .concat("<img src=\"/TheBusinessAssistant/images/recurintask.gif\" height=\"13\" border=\"0\">&nbsp;");
										}

										System.out.println("task: "
										        + vEntry.getDescription());
					%>
					<tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst">
						<td width="55" valign="top"><%=vEntry.getDate()%></td>
						<td width="250" valign="top"><%=vEntry.getDescription()%></td>
						<td width="100" valign="top"><%=vTimeSpend%></td>
						<td width="150" valign="top"><%=vKost%></td>
						<td width="60" valign="top"><%=vInfoGifs%></td>
					</tr>
					<%
						}
								}
								else
								{
									out.println("<br><span class=\"adminsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er zijn geen taken uitgevoerd voor deze klant tijdens de maand "
									        + Constants.MONTHS[vSession.getMonthsBack()]
									        + ".</span>");
								}

							}
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
						}
					%>

				</table></td>
		</tr>
	</table>
</form>



</body>

</html>

<script>

function showPrevious()
{
  document.tasklistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.TASK_SHOW_PREV%>";
}

function showNext()
{
  document.tasklistform.<%=Constants.SRV_ACTION%>.value="<%=Constants.TASK_SHOW_NEXT%>";
}
</script>
