<html>

<%@ include file="adminheader.jsp" %>

	<%@ page
		import="javax.ejb.*,
java.util.*,
javax.naming.Context,
javax.naming.InitialContext,
javax.ejb.*,
be.tba.ejb.task.interfaces.*,
be.tba.ejb.task.session.*,
be.tba.ejb.account.session.*,
be.tba.util.session.AccountCache,
be.tba.util.constants.Constants,
be.tba.util.data.*"%>

<%
try
{
   vSession.setCallingJsp(Constants.WORKORDER_JSP);
   int workOrderId = vSession.getWorkOrderId();
   WorkOrderSqlAdapter vWorkOrderSession = new WorkOrderSqlAdapter();
   WorkOrderData workOrder = vWorkOrderSession.getRow(vSession, workOrderId);
   AccountEntityData account = AccountCache.getInstance().get(workOrder.accountId);
   FileLocationSqlAdapter fileLocationSession = new FileLocationSqlAdapter();
   Collection<FileLocationData> inputFiles = fileLocationSession.getInputFiles(vSession, workOrder.id);
   Collection<FileLocationData> outputFiles = fileLocationSession.getOutputFiles(vSession, workOrder.id);
   String dueDate = workOrder.dueDate;
   if (dueDate == null || dueDate.isEmpty() || dueDate.isBlank()) dueDate = "niet opgegeven";
%>

<body>
<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">

	<tr valign="top">
		<td width="30" bgcolor="FFFFFF"></td>
		<td bgcolor="FFFFFF"><br>
		<br>
		<span class="admintitle">Opdracht ingegeven door <%=((account == null) ? "??" : account.getFullName())%>.</span>
        <br>
        <form name="workoderform1" method="POST" action="/tba/AdminDispatch">
        <input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.ACTION_SAVE_WORKORDER%>"> 
        <input type=hidden name=<%=Constants.WORKORDER_FILE_ID%> value=""> 
		<table border="0" cellspacing="1" cellpadding="1">
            <tr>
               <td width="30"></td>
               <td width="250" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Opdracht</td>
               <td width="530" valign="top"><%=workOrder.title%></td>
            </tr>
            <tr>
               <td width="30"></td>
               <td width="250" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Status</td>
               <td width="530" valign="top">
               
                <select
                    name="<%=Constants.WORKORDER_STATE%>">
                    <%
                    for (WorkOrderData.State state: WorkOrderData.State.values())
                     {
                         out.println("<option value=\"" + state.name() + (state == workOrder.state ? "\" selected>" : "\">") + WorkOrderData.getStateStr(state));
                     }
                    %>
                </select>
                </td>
            </tr>
            <tr>
               <td width="30"></td>
               <td width="250" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Instructies</td>
               <td width="530" valign="top"><textarea name=<%=Constants.WORKORDER_INSTRUCTION%> rows=10 cols=70><%=(String) workOrder.instructions%></textarea></td>
            </tr>
            <tr>
               <td width="30"></td>
               <td width="250" valign="top" class="adminsubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Verwachte opleverdatum</td>
               <td width="530" valign="top"><%=dueDate%></td>
            </tr>
		</table>
		<br>
        <span class="adminsubsubtitle">Aangeleverde bestanden:</span>
		<br>
        <table border="0" cellspacing="4" cellpadding="1">
<%
if (inputFiles.isEmpty())
{
%>
            <tr>
               <td width="10"></td>
               <td width="500"><span class="bodytekst">Geen.</span></td>
            </tr>
<%
}
else
{
   for (FileLocationData fileEntry : inputFiles)
   {
      
%>
            <tr>
               <td width="30"> 
                 <input type=image src="/tba/images/upload.gif" onclick="downloadInFile(<%=fileEntry.id%>)">
               <td bgcolor="FFCC66" width="500" valign="top"><%=fileEntry.name%> (<%=fileEntry.size%>KB)</td>
            </tr>
<%
   }
}
%>
  </table>
  <br>
  <br>
  <span class="adminsubsubtitle">Opgeleverde bestanden door ons:</span>
  <br>
   <table border="0" cellspacing="4" cellpadding="1">
<%
if (outputFiles.isEmpty())
{
%>
            <tr>
               <td width="10"></td>
               <td width="500"><span class="bodytekst">Geen.</span></td>
            </tr>
<%
}
else
{
   for (FileLocationData fileEntry : outputFiles)
   {
      
%>
            <tr>
               <td width="30"> 
                 <input type=image src="/tba/images/waste.gif" onclick="removeOutFile(<%=fileEntry.id%>)">
               <td bgcolor="FFCC66" width="500" valign="top"><%=fileEntry.name%> (<%=fileEntry.size%>KB)</td>
            </tr>
<%
   }
}
%>
</table>
  </form>
   <form name="loadfileform" method="POST" action="/tba/AdminDispatch" enctype="multipart/form-data">
   <input class="tbabutton" type=file name=<%=Constants.WORKORDER_FILE%> value=" Bestand opladen " accept=".*">
   <input type=hidden name=<%=Constants.WORKORDER_STATE%> value=<%=workOrder.state%>> 
   <input type=hidden name=<%=Constants.WORKORDER_INSTRUCTION%> value=<%=workOrder.instructions%>> 
   <input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.UPLOAD_WORKORDER_FILE%>">
   <input type=hidden name=<%=Constants.ACCOUNT_ID%> value=<%=workOrder.accountId%>>
   <input type=hidden name=<%=Constants.WORKORDER_ID%> value=<%=workOrder.id%>> 
   <input class="tbabutton" type=submit value=" Laad de file op " onclick="uploadFile()">
 </form>
<br><br>
<form name="workoderform2" method="POST" action="/tba/AdminDispatch">
        <input type=hidden name=<%=Constants.WORKORDER_STATE%> value=<%=workOrder.state%>> 
        <input type=hidden name=<%=Constants.WORKORDER_INSTRUCTION%> value=<%=workOrder.instructions%>> 
        <input type=hidden name=<%=Constants.WORKORDER_FILE_ID%> value=""> 
        <input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.ACTION_SAVE_WORKORDER%>"> 
        <input type=hidden name=<%=Constants.WORKORDER_ID%> value=<%=workOrder.id%>> 
        <input class="tbabutton" type=submit value="Bewaar" onclick="save();"> 
        <input class="tbabutton" type=submit value="Cancel" onclick="cancelUpdate();">
</form>
</body>
<%
}
catch (Exception ex)
{
  ex.printStackTrace();
}
%>
<script>


function removeOutFile(fileId)
{
    document.workoderform1.<%=Constants.SRV_ACTION%>.value="<%=Constants.DELETE_WORKORDER_FILE%>";
    document.workoderform1.<%=Constants.WORKORDER_FILE_ID%>.value=fileId;
}

function downloadInFile(fileId)
{
    document.workoderform1.<%=Constants.SRV_ACTION%>.value="<%=Constants.DOWNLOAD_WORKORDER_FILE%>";
    document.workoderform1.<%=Constants.WORKORDER_FILE_ID%>.value=fileId;
    document.workoderform1.action="/tba/download";
}

function uploadFile()
{
  document.loadfileform.<%=Constants.SRV_ACTION%>.value="<%=Constants.UPLOAD_WORKORDER_FILE%>";
  document.loadfileform.<%=Constants.WORKORDER_INSTRUCTION%>.value=document.workoderform1.<%=Constants.WORKORDER_INSTRUCTION%>.value;
  document.loadfileform.<%=Constants.WORKORDER_STATE%>.value=document.workoderform1.<%=Constants.WORKORDER_STATE%>.value;
}


function save()
{
    console.log("workorderState=" + document.workoderform1.<%=Constants.WORKORDER_STATE%>.value);
    document.workoderform2.<%=Constants.WORKORDER_INSTRUCTION%>.value=document.workoderform1.<%=Constants.WORKORDER_INSTRUCTION%>.value;
    document.workoderform2.<%=Constants.WORKORDER_STATE%>.value=document.workoderform1.<%=Constants.WORKORDER_STATE%>.value;
}

function cancelUpdate()
{
  document.workoderform2.<%=Constants.SRV_ACTION%>.value="<%=Constants.GOTO_ADMIN_WORKORDERS%>";
}
</script>


</html>

