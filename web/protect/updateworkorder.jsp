<html>

<%@ include file="protheader.jsp" %>
<link href="/tba/css/picker.css" rel="stylesheet">

<%@ page
import="
java.util.*,
javax.naming.Context,
javax.naming.InitialContext,

be.tba.ejb.account.interfaces.*,
be.tba.ejb.task.interfaces.*,
be.tba.ejb.task.session.*,
be.tba.ejb.account.session.*,
be.tba.util.session.AccountCache,
be.tba.util.constants.Constants,
be.tba.util.timer.CallCalendar,
be.tba.util.data.*"%>

<%
try
{
   vSession.setCallingJsp(Constants.WORKORDER_JSP);
   WorkOrderSqlAdapter vWorkOrderSession = new WorkOrderSqlAdapter();
   FileLocationSqlAdapter fileLocationSession = new FileLocationSqlAdapter();
   AccountEntityData vAccountData = AccountCache.getInstance().get(vSession.getAccountId());
   WorkOrderData workOrder = null;
   Collection<FileLocationData> inputFiles = null;
   Collection<FileLocationData> outputFiles = null;
   Calendar calendar = Calendar.getInstance();
   String today = String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
   int workOrderId = vSession.getWorkOrderId();
   if (workOrderId > 0)
   {
      workOrder = vWorkOrderSession.getRow(vSession, workOrderId);
      inputFiles = fileLocationSession.getInputFiles(vSession, workOrder.id);
      outputFiles = fileLocationSession.getOutputFiles(vSession, workOrder.id);
   }
   else
   {
      workOrder = new WorkOrderData();
      inputFiles = new Vector<FileLocationData>();
      outputFiles = new Vector<FileLocationData>();
      workOrder.dueDate = String.format("%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.YEAR));
   }
   calendar.add(Calendar.MONTH, +2);
   String endDate = String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
   
   Calendar dueData = CallCalendar.str2Calendar(workOrder.dueDate);
   String curDueDate = String.format("%04d-%02d-%02d", dueData.get(Calendar.YEAR), dueData.get(Calendar.MONTH)+1, dueData.get(Calendar.DAY_OF_MONTH));
%>

<body>
<table  cellspacing='0' cellpadding='0' border='0' bgcolor="FFFFFF">

	<tr valign="top">
		<td width="30" bgcolor="FFFFFF"></td>
		<td bgcolor="FFFFFF"><br>
		<br>
		<span class="bodytitle">Specifieer uw opdracht naar het The Business Assistant Team:</span>
		<br><br>
        <form name="workoderform1" method="POST" action="/tba/CustomerDispatch">
        <input type=hidden name=<%=Constants.SRV_ACTION%> value="<%=Constants.ACTION_SAVE_WORKORDER%>"> 
		<input type=hidden name=<%=Constants.WORKORDER_FILE_ID%> value=null> 
        <input type=hidden name=<%=Constants.WORKORDER_ID%> value="<%=workOrder.id%>"> 
        <table border="0" cellspacing="4" cellpadding="1">
            <tr>
               <td width="10"></td>
               <td width="210" valign="middle" class="bodysubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Titel</td>
               <td width="530" valign="middle"><input type=text size=65 name=<%=Constants.WORKORDER_TITLE%> value="<%=workOrder.title%>">
               </td>
            </tr>
            <tr>
               <td width="10"></td>
               <td width="210" valign="middle" class="bodysubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Dringend actie nemen</td>
               <td width="530" valign="middle">
               <input type="checkbox" id="cbx1" style="display:none" name=<%=Constants.WORKORDER_URGENT%><%=(workOrder.isUrgent ? " checked=\"checked\"" : "")%> />
               <label for="cbx1" class="toggle"><span></span></label>
               </td>
            </tr>
            <tr>
               <td width="10"></td>
               <td width="210" valign="middle" class="bodysubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Status</td>
               <td width="530" valign="middle"><%=WorkOrderData.getStateStr(workOrder.state)%>
<%
if (workOrder.state == WorkOrderData.State.kDone)
{
   %>
   <input class="tbabutton" type=submit value=" Archiveer " onclick="archive('<%=workOrder.state.name()%>')">
   <%
}

%>               
               
               </td>
            </tr>
            <tr>
               <td width="10"></td>
               <td width="210" valign="top" class="bodysubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Instructies</td>
               <td width="530" valign="top"><textarea name=<%=Constants.WORKORDER_INSTRUCTION%> rows=10 cols=70><%=workOrder.instructions%></textarea></td>
            </tr>
            <tr>
               <td width="10"></td>
               <td width="210" valign="middle" class="bodysubsubtitle"><img src=".\images\blueSphere.gif" width="10" height="10">&nbsp;Gewenste opleverdatum</td>
               <td width="530" valign="middle">
               <div id="position">
                    <input class="date-input-native" id="dateTba" type="date" name="<%=Constants.WORKORDER_DUEDATE%>" min="<%=today%>" max="<%=endDate%>" value="<%=curDueDate%>" title="klik om de datum te wijzigen">
                    <input class="date-input-fallback" id="alt" type="text" placeholder="<%=curDueDate%>">
                    <div id="picker" hidden></div>
               </div>     
               </td>
            </tr>
		</table>
        <br>
        <span class="bodysubtitle">Te verwerken bestanden:</span>
        <br>
        <table border="0" cellspacing="4" cellpadding="1">
<%
if (inputFiles.isEmpty())
{
%>
            <tr>
               <td width="10"></td>
               <td width="750"><span class="bodytekst">Geen.</span></td>
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
                 <input type=image src="/tba/images/waste.gif" onclick="removeFile(<%=fileEntry.id%>)" title="verwijder">
               </td>
               <td bgcolor="FFCC66" width="750" valign="top"><%=fileEntry.name%> (<%=fileEntry.size%>KB)</td>
            </tr>
<%
   }
}
%>
  </table>
     <input class="tbabutton" type=file name=<%=Constants.WORKORDER_FILE%> value=null accept=".*">
     <input class="tbabutton" type=submit value=" Laad de file op " onclick="uploadFile()">
  <br>
  <br>
  <span class="bodysubtitle">Opgeleverde bestanden door The Business Assistant:</span>
  <br>
     <table border="0" cellspacing="4" cellpadding="1">
<%
if (outputFiles.isEmpty())
{
%>
            <tr>
               <td width="10"></td>
               <td width="750"><span class="bodytekst">Geen.</span></td>
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
                 <input type=image src="/tba/images/upload.gif" onclick="downloadOutFile(<%=fileEntry.id%>)" title="bewaar deze aangeleverde file">
               <td bgcolor="FFCC66" width="750" valign="top"><%=fileEntry.name%> (<%=fileEntry.size%>KB)</td>
            </tr>
<%
   }
}
%>
    </table>
   <br><br>
		<input class="tbabutton" type=submit value="Bewaar" onclick="save();"> 
		<input class="tbabutton" type=submit value="Cancel" onclick="cancelUpdate();">
   </form>
</td></tr>
 </table>
</body>
<%
}
catch (Exception ex)
{
  log.error(ex.getMessage(), ex);
}
%>
<script src="/tba/css/picker.js"></script>
<script type="text/javascript">

function cancelUpdate()
{
  document.workoderform1.<%=Constants.SRV_ACTION%>.value="<%=Constants.ACTION_GOTO_WORKORDERS%>";
}

function save()
{
    document.workoderform1.<%=Constants.SRV_ACTION%>.value="<%=Constants.ACTION_SAVE_WORKORDER%>";
}

function removeFile(fileId)
{
    document.workoderform1.<%=Constants.SRV_ACTION%>.value="<%=Constants.DELETE_WORKORDER_FILE%>";
    document.workoderform1.<%=Constants.WORKORDER_FILE_ID%>.value=fileId;
}

function downloadOutFile(fileId)
{
    document.workoderform1.<%=Constants.SRV_ACTION%>.value="<%=Constants.DOWNLOAD_WORKORDER_FILE%>";
    document.workoderform1.<%=Constants.WORKORDER_FILE_ID%>.value=fileId;
    document.workoderform1.action="/tba/custdownload";
}

function uploadFile()
{
    document.workoderform1.enctype="multipart/form-data";
    document.workoderform1.<%=Constants.SRV_ACTION%>.value="<%=Constants.UPLOAD_WORKORDER_FILE%>";
}

function archive(state)
{
    document.workoderform1.<%=Constants.WORKORDER_STATE%>.value=state;
    save();
}

(function(){

    'use strict';

    var dayNamesShort = ['Ma', 'Di', 'Wo', 'Do', 'Vr', 'Za', 'Zo'];
    var monthNames = ['Januari', 'Februari', 'Maart', 'April', 'Mei', 'Juni', 'Juli', 'Augustus', 'September', 'Oktober', 'November', 'December'];
    var icon = '<svg viewBox="0 0 512 512"><polygon points="268.395,256 134.559,121.521 206.422,50 411.441,256 206.422,462 134.559,390.477 "/></svg>';

    var root = document.getElementById('picker');
    var dateInput = document.getElementById('dateTba');
    var altInput = document.getElementById('alt');
    var doc = document.documentElement;

    function format ( dt ) {
        return Picker.prototype.pad(dt.getDate()) + ' ' + monthNames[dt.getMonth()].slice(0,3) + ' ' + dt.getFullYear();
    }

    function show ( ) {
        root.removeAttribute('hidden');
    }

    function hide ( ) {
        root.setAttribute('hidden', '');
        doc.removeEventListener('click', hide);
    }

    function onSelectHandler ( ) {

        var value = this.get();

        if ( value.start ) {
            dateInput.value = value.start.Ymd();
            altInput.value = format(value.start);
            hide();
        }
    }

    var picker = new Picker(root, {
        min: new Date(dateInput.min),
        max: new Date(dateInput.max),
        icon: icon,
        twoCalendars: false,
        dayNamesShort: dayNamesShort,
        monthNames: monthNames,
        onSelect: onSelectHandler
    });

    root.parentElement.addEventListener('click', function ( e ) { e.stopPropagation(); });

    dateInput.addEventListener('change', function ( ) {

        if ( dateInput.value ) {
            picker.select(new Date(dateInput.value));
        } else {
            picker.clear();
        }
    });

    altInput.addEventListener('focus', function ( ) {
        altInput.blur();
        show();
        doc.addEventListener('click', hide, false);
    });

}());
</script>



</html>

