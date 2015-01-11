<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="demo.demo.input.title" text="编辑"/></title>
    <%@include file="/common/s.jsp"%>
	<link href="${scopePrefix}/widgets/xform/styles/xform.css" rel="stylesheet">
    <script type="text/javascript" src="${scopePrefix}/widgets/xform/xform-packed.js"></script>

    <link type="text/css" rel="stylesheet" href="../widgets/userpicker/userpicker.css">
    <script type="text/javascript" src="../widgets/userpicker/userpicker.js"></script>

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

	$("#demoForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });

	createUserPicker({
		multiple: true,
		url: '${scopePrefix}/rs/user/search'
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

	<script type="text/javascript" src="${scopePrefix}/widgets/operation/TaskOperation.js"></script>
	<script type="text/javascript">
ROOT_URL = '${scopePrefix}';
var taskOperation = new TaskOperation();
	</script>

  </head>

  <body>
    <%@include file="/header/bpm-workspace.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-workspace.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10" style="float:right">

      <div id="xformToolbar">
	    <c:forEach var="item" items="${buttons}">
		<button id="${item.name}" type="button" class="btn" onclick="taskOperation.${item.name}()">${item.label}</button>
		</c:forEach>
      </div>

      <form id="xform" method="post" action="${scopePrefix}/operation/process-operation-startProcessInstance.do" class="xf-form" enctype="multipart/form-data">
<input id="processDefinitionId" type="hidden" name="processDefinitionId" value="${formDto.processDefinitionId}">
<input id="bpmProcessId" type="hidden" name="bpmProcessId" value="${bpmProcessId}">
<input id="autoCompleteFirstTask" type="hidden" name="autoCompleteFirstTask" value="${formDto.autoCompleteFirstTask}">
<input id="businessKey" type="hidden" name="businessKey" value="${businessKey}">
<!--
<input id="taskId" type="hidden" name="taskId" value="${taskId}">
-->
		<div id="xf-form-table"></div>
		<br>
		<div id="nextStep">
		</div>
		  <script>
		  $.getJSON('${scopePrefix}/rs/bpm/next', {
			  processDefinitionId: '${formDto.processDefinitionId}',
			  activityId: '${formDto.activityId}'
		  }, function(data) {
			  $('#nextStep').append('下个环节：');
			  for (var i = 0; i < data.length; i++) {
				  $('#nextStep').append(data[i].name);
			  }
		  });
		  </script>
	  </form>

    </section>
	<!-- end of main -->

    <form id="f" action="form-template-save.do" method="post" style="display:none;">
	  <textarea id="__gef_content__" name="content">${xform.content}</textarea>
	</form>

  </body>

</html>
