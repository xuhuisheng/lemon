<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="demo.demo.input.title" text="编辑"/></title>
    <%@include file="/common/s3.jsp"%>

	<!-- bootbox -->
    <script type="text/javascript" src="${cdnPrefix}/bootbox/bootbox.min.js"></script>
	<link href="${cdnPrefix}/xform3/styles/xform.css" rel="stylesheet">
    <script type="text/javascript" src="${cdnPrefix}/xform3/xform-packed.js"></script>

    <link type="text/css" rel="stylesheet" href="${cdnPrefix}/userpicker3-v2/userpicker.css">
    <script type="text/javascript" src="${cdnPrefix}/userpicker3-v2/userpicker.js"></script>

	<style type="text/css">
.xf-handler {
	cursor: auto;
}
	</style>

	<script type="text/javascript">
document.onmousedown = function(e) {};
document.onmousemove = function(e) {};
document.onmouseup = function(e) {};
document.ondblclick = function(e) {};

var xform;

$(function() {
	xform = new xf.Xform('xf-form-table');
	xform.render();

	if ($('#__gef_content__').val() != '') {
		xform.doImport($('#__gef_content__').val());
	}

	if ('${xform.jsonData}' != '') {
		xform.setValue(${xform.jsonData});
	}

	$("#xform").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });

	createUserPicker({
		multiple: true,
		searchUrl: '${tenantPrefix}/rs/user/search',
		treeUrl: '${tenantPrefix}/rs/party/tree?partyStructTypeId=1',
		childUrl: '${tenantPrefix}/rs/party/searchUser'
	});

	setTimeout(function() {
		$('.datepicker').datepicker({
			autoclose: true,
			language: 'zh_CN',
			format: 'yyyy-mm-dd'
		})
	}, 500);
})
    </script>

	<script type="text/javascript" src="${cdnPrefix}/operation/TaskOperation.js"></script>
	<script type="text/javascript">
ROOT_URL = '${tenantPrefix}';
var taskOperation = new TaskOperation();
	</script>

  </head>

  <body>
    <%@include file="/header/bpm-workspace3.jsp"%>

    <div class="container">

	<!-- start of main -->
      <section id="m-main" class="col-md-12" style="padding-top:65px;">

<table width="100%" cellspacing="0" cellpadding="0" border="0" align="center" class="xf-table">
  <tbody>
    <tr>
	  <td width="25%" class="xf-cell xf-cell-right xf-cell-bottom xf-cell-top xf-cell-left">
	    <label style="display:block;text-align:right;margin-bottom:0px;padding-top:10px;padding-bottom:10px;">下个环节&nbsp;</label>
	  </td>
	  <td width="75%" class="xf-cell xf-cell-right xf-cell-bottom xf-cell-top" colspan="3" rowspan="1">
	    <div id="nextStep"></div>
	  </td>
	</td>
  </tbody>
</table>
		  <script>
		  $.getJSON('${tenantPrefix}/rs/bpm/next', {
			  processDefinitionId: '${formDto.processDefinitionId}',
			  activityId: '${formDto.activityId}'
		  }, function(data) {
			  $('#nextStep').append('&nbsp;');
			  for (var i = 0; i < data.length; i++) {
				  $('#nextStep').append(data[i].name);
			  }
		  });
		  </script>

      <form id="xform" method="post" action="${tenantPrefix}/operation/process-operation-startProcessInstance.do" class="xf-form" enctype="multipart/form-data">
<input id="processDefinitionId" type="hidden" name="processDefinitionId" value="${formDto.processDefinitionId}">
<input id="bpmProcessId" type="hidden" name="bpmProcessId" value="${bpmProcessId}">
<input id="autoCompleteFirstTask" type="hidden" name="autoCompleteFirstTask" value="${formDto.autoCompleteFirstTask}">
<input id="businessKey" type="hidden" name="businessKey" value="${businessKey}">
<!--
<input id="taskId" type="hidden" name="taskId" value="${taskId}">
-->
		<div id="xf-form-table"></div>
		<br>
		<br>
		<br>
		<br>
	  </form>

    </section>
	<!-- end of main -->

    <form id="f" action="form-template-save.do" method="post" style="display:none;">
	  <textarea id="__gef_content__" name="content">${xform.content}</textarea>
	</form>


<div class="navbar navbar-default navbar-fixed-bottom">
  <div class="container-fluid">
    <div class="text-center" style="padding-top:8px;">

	    <c:forEach var="item" items="${buttons}">
		<button id="${item.name}" type="button" class="btn btn-default" onclick="taskOperation.${item.name}()">${item.label}</button>
		</c:forEach>
	
	</div>
  </div>
</div>


  </body>

</html>
