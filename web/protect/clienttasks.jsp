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
		import="
		java.util.*,
		
		javax.naming.Context,
		javax.naming.InitialContext,
		
		
		be.tba.ejb.account.interfaces.*,
		be.tba.ejb.task.interfaces.*,
		be.tba.ejb.task.session.TaskSqlAdapter,
		be.tba.util.constants.EjbJndiNames,
		be.tba.util.constants.Constants,
		be.tba.util.exceptions.AccessDeniedException,
		be.tba.servlets.session.SessionManager,
		be.tba.util.session.AccountCache,
		be.tba.util.data.*,
		java.text.*"%>

<%
	try
	{
%>

<form name="tasklistform" method="POST"
	action="/tba/CustomerDispatch">
	<input type=hidden name=<%=Constants.SRV_ACTION%>
		value="<%=Constants.ACTION_GOTO_SHOW_TASKS%>">
	<table  cellspacing='0' cellpadding='0' border='0'
		bgcolor="FFFFFF">
		<tr>
			<!-- white space -->
			<td valign="top" width="20" bgcolor="FFFFFF"></td>

			<!-- account list -->
			<td valign="top" bgcolor="FFFFFF"><br>
			<p>
			    <span class="bodytitle"> Uitgevoerde taken:</span>
			</p>			
			 <%
						    if (vSession == null)
						 						  			throw new AccessDeniedException("U bent niet aangemeld.");
						 						  		vSession.setCallingJsp(Constants.CLIENT_SHOW_TASKS_JSP);  
						 						  		  
						 						  		if (vSession.getSessionFwdNr() == null)
						 						  			throw new AccessDeniedException(
						 						  			        "Account nummer not set in session.");

						 						  		Collection<TaskEntityData> vTasks = null;
						 						  		TaskSqlAdapter vTaskSession = new TaskSqlAdapter();

						 						  		vTasks = vTaskSession.getTasksForMonthforFwdNr(vSession, vSession.getSessionFwdNr(), vSession.getMonthsBack(), vSession.getYear());

						 						  		AccountEntityData vAccountData = (AccountEntityData) AccountCache.getInstance().get(vSession.getSessionFwdNr());
						 %> <input class="tbabutton" type=submit name=action value="Taken van vorige maand"
				onclick="showPrevious()"> <%
 	if (!vSession.isCurrentMonth())
 		{
 			out.println("<input class=\"tbabutton\" type=submit name=action value=\"Taken van volgende maand\"  onclick=\"showNext()\">");
 		}

 		out.println("<br><br><table border=\"0\" cellspacing=\"2\" cellpadding=\"2\">");
 		if (vTasks == null || vTasks.size() == 0)
 		{
 			out.println("<span class=\"bodysubsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er zijn geen taken uitgevoerd tijdens de maand "
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
 					out.println("<span class=\"bodysubsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
 					        + vTasks.size()
 					        + " taken uitgevoerd tijdens de maand "
 					        + Constants.MONTHS[vSession.getMonthsBack()]
 					        + ".</span><br>");
 				}
 				else
 				{
 					out.println("<span class=\"bodysubsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
 					        + vTasks.size()
 					        + " taken uitgevoerd deze maand.</span><br>");
 				}
 %> <br>
				<table border="0" cellspacing="2" cellpadding="2">
					<tr>
						<td width="65" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Datum</td>
						<td width="500" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Omschrijving</td>
						<td width="100" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Minuten</td>
						<td width="100" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Kost</td>
						<td width="80" valign="top" class="topMenu" bgcolor="#F89920">&nbsp;Info</td>
					</tr>

					<%
						DecimalFormat vCostFormatter = new DecimalFormat(
									        "#0.00");
									for (Iterator<TaskEntityData> i = vTasks.iterator(); i.hasNext();)
									{
										TaskEntityData vEntry = i.next();

										String vId = "id" + vEntry.getId();
										String vKost;
										if (vEntry.getIsFixedPrice())
										{
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
											        .concat("<img src=\"/tba/images/recurintask.gif\" height=\"13\" border=\"0\">&nbsp;");
										}
					%>
					<tr bgcolor="FFCC66" id=<%=vId%> class="bodytekst">
						<td width="65" valign="top"><%=vEntry.getDate()%></td>
						<td width="500" valign="top"><%=vEntry.getDescription()%></td>
						<td width="100" valign="top"><%=vTimeSpend%></td>
						<td width="150" valign="top"><%=vKost%></td>
						<td width="80" valign="top"><%=vInfoGifs%></td>
					</tr>
					<%
						}
					}
					else
					{
						out.println("<br><span class=\"bodysubsubtitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Er zijn geen taken uitgevoerd voor deze klant tijdens de maand "
						        + Constants.MONTHS[vSession.getMonthsBack()]
						        + ".</span>");
					}

				}
			}
			catch (Exception ex)
			{
				log.error(ex.getMessage(), ex);
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

