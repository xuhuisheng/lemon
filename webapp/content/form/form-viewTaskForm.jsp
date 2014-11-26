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
	<script type="text/javascript">
document.onmousedown = function(e) {};
document.onmousemove = function(e) {};
document.onmouseup = function(e) {};
document.ondblclick = function(e) {};

var buttons = [];
<c:forEach items="${formDto.buttons}" var="item">
buttons.push('${item}');
</c:forEach>

if (buttons.length == 0) {
	buttons = ['保存草稿', '完成任务'];
}

var html = '';

for (var i = 0; i < buttons.length; i++) {
	html += '<button type="button">' + buttons[i] + '</button>';
}

var xform;

$(function() {
	$('#xf-form-button').html(html);

	xform = new xf.Xform('xf-form-table');
	xform.render();

	if ($('#__gef_content__').val() != '') {
		xform.doImport($('#__gef_content__').val());
	}

	xform.setValue(${json});

	$("#demoForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });

	$(document).delegate('#xf-form-button button', 'click', function(e) {
		switch($(this).html()) {
			case '保存草稿':
				$('#xf-form').attr('action', 'form-saveDraft.do');
				$('#xf-form').submit();
				break;
			case '完成任务':
				$('#xf-form').attr('action', 'form-completeTask.do');
				$('#xf-form').submit();
				break;
			case '发起流程':
				$('#xf-form').attr('action', 'form-startProcessInstance.do');
				$('#xf-form').submit();
				break;
			case '驳回':
				$('#xf-form').attr('action', '${scopePrefix}/bpm/workspace-rollback.do');
				$('#xf-form').submit();
				break;
			case '转办':
				$('#modal form').attr('action', '${scopePrefix}/bpm/workspace-doDelegate.do');
				$('#modal').modal();
				break;
			case '协办':
				$('#modal form').attr('action', '${scopePrefix}/bpm/workspace-doDelegateHelp.do');
				$('#modal').modal();
				break;
		}
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
  </head>

  <body>
    <%@include file="/header/bpm-workspace.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-workspace.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10" style="float:right">

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

	  <form id="xf-form" method="post" action="${scopePrefix}/form/form-completeTask.do" class="xf-form">
		<input id="taskId" type="hidden" name="taskId" value="${formDto.taskId}">
		<input id="businessKey" type="hidden" name="businessKey" value="${dynamicModel.id}">
		<div id="xf-form-table"></div>
		<br>
		<div id="xf-form-button" style="text-align:center;">
		</div>
	  </form>
    </section>
	<!-- end of main -->

    <form id="f" action="form-template-save.do" method="post" style="display:none;">
	  <textarea id="__gef_content__" name="content">${formTemplate.content}</textarea>
	</form>

	<div id="modal" class="modal hide fade">
	  <div class="modal-body">
	  <form>
	    <input type="hidden" name="taskId" value="${formDto.taskId}"/>
        <div class="input-append userPicker">
		  <input type="hidden" name="attorney" class="input-medium" value="">
		  <input type="text" style="width: 175px;" value="">
		  <span class="add-on"><i class="icon-user"></i></span>
        </div>
		<br>
		<button class="btn">提交</button>
	  </div>
	</div>

  </body>

</html>
