<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-task");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="demo.demo.input.title" text="编辑"/></title>
    <%@include file="/common/s.jsp"%>
	<link href="${scopePrefix}/widgets/xform/styles/xform.css" rel="stylesheet">
    <script type="text/javascript" src="${scopePrefix}/widgets/xform/xform-packed.js"></script>

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

	xform.setValue(${xform.jsonData});

	$("#demoForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
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
	
	<link type="text/css" rel="stylesheet" href="${scopePrefix}/widgets/userpicker/userpicker.css">
    <script type="text/javascript" src="${scopePrefix}/widgets/userpicker/userpicker.js"></script>
	<script type="text/javascript">
$(function() {
	createUserPicker({
		modalId: 'userPicker',
		url: '${scopePrefix}/rs/user/search'
	});
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

		<div id="previousStep">
		</div>
		  <script>
		  $.getJSON('${scopePrefix}/rs/bpm/previous', {
			  processDefinitionId: '${formDto.processDefinitionId}',
			  activityId: '${formDto.activityId}'
		  }, function(data) {
			  $('#previousStep').append('上个环节：');
			  for (var i = 0; i < data.length; i++) {
				  $('#previousStep').append(data[i].name);
			  }
		  });
		  </script>
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

	  <form id="xform" method="post" action="${scopePrefix}/operation/task-operation-completeTask.do" class="xf-form" enctype="multipart/form-data">
		<input id="humanTaskId" type="hidden" name="humanTaskId" value="${humanTaskId}">
		<div id="xf-form-table"></div>
	  </form>
    </section>
	<!-- end of main -->

    <form id="f" action="form-template-save.do" method="post" style="display:none;">
	  <textarea id="__gef_content__" name="content">${xform.content}</textarea>
	</form>

	<div id="modal" class="modal hide fade">
	  <div class="modal-body">
	  <form>
	    <input type="hidden" name="humanTaskId" value="${humanTaskId}"/>
        <div class="input-append userPicker">
		  <input type="hidden" name="userId" class="input-medium" value="">
		  <input type="text" style="width: 175px;" value="">
		  <span class="add-on"><i class="icon-user"></i></span>
        </div>
		<br>
		<button class="btn">提交</button>
	  </div>
	</div>

  </body>

</html>
