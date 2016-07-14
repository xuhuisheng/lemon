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
    <script type="text/javascript" src="${ctx}/s/bootbox/bootbox.min.js"></script>
	<link href="${tenantPrefix}/widgets/xform3/styles/xform.css" rel="stylesheet">
    <script type="text/javascript" src="${tenantPrefix}/widgets/xform3/xform-packed.js"></script>

    <link type="text/css" rel="stylesheet" href="../widgets/userpicker3-v2/userpicker.css">
    <script type="text/javascript" src="../widgets/userpicker3-v2/userpicker.js"></script>

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

	$("#xform").validate({
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

	<script type="text/javascript">
$(function() {
	createUserPicker({
		multiple: true,
		searchUrl: '${tenantPrefix}/rs/user/search',
		treeUrl: '${tenantPrefix}/rs/party/tree?partyStructTypeId=1',
		childUrl: '${tenantPrefix}/rs/party/searchUser'
	});
})
    </script>

	<script type="text/javascript" src="${tenantPrefix}/widgets/operation/TaskOperation.js"></script>
	<script type="text/javascript">
ROOT_URL = '${tenantPrefix}';
var taskOperation = new TaskOperation();
	</script>

  </head>

  <body>
    <%@include file="/header/bpm-workspace3.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-workspace3.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

        <c:if test="${not empty children}">
		<div class="alert alert-info" role="alert">
	    <c:forEach var="item" items="${children}">
		  <p>
		    ${item.catalog == 'communicate' ? '沟通反馈' : ''}
		    <tags:user userId="${item.assignee}"/>
			<fmt:formatDate value="${item.completeTime}" type="both"/>
			${item.comment}</p>
		</c:forEach>
		</div>
		</c:if>

      <div id="xformToolbar">
	    <c:if test="${humanTask.catalog == 'normal'}">
	    <c:forEach var="item" items="${buttons}">
		<button id="${item.name}" type="button" class="btn btn-default" onclick="taskOperation.${item.name}()">${item.label}</button>
		</c:forEach>
		</c:if>

		<c:if test="${humanTask.catalog == 'vote'}">
		<button id="approve" type="button" class="btn btn-default" onclick="taskOperation.approve()">同意</button>
		<button id="reject" type="button" class="btn btn-default" onclick="taskOperation.reject()">反对</button>
		<button id="abandon" type="button" class="btn btn-default" onclick="taskOperation.abandon()">弃权</button>
		</c:if>

		<c:if test="${humanTask.catalog == 'copy'}">
		</c:if>

		<c:if test="${humanTask.catalog == 'communicate'}">
	    <div class="alert alert-info" role="alert">
		  来自<tags:user userId="${parentHumanTask.assignee}"/>的沟通：
		  ${humanTask.message}
		</div>
		<button id="callback" type="button" class="btn btn-default" onclick="taskOperation.callback()">反馈</button>
		</c:if>

		<c:if test="${humanTask.catalog == 'start'}">
		<button id="saveDraft" type="button" class="btn btn-default" onclick="taskOperation.saveDraft()">暂存</button>
		<button id="completeTask" type="button" class="btn btn-default" onclick="taskOperation.completeTask()">提交</button>
		</c:if>
      </div>


      <c:if test="${humanTask.catalog != 'communicate'}">
      <div id="previousStep">
	  </div>
	
	  <script>
		  $.getJSON('${tenantPrefix}/rs/bpm/previous', {
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
		  $.getJSON('${tenantPrefix}/rs/bpm/next', {
			  processDefinitionId: '${formDto.processDefinitionId}',
			  activityId: '${formDto.activityId}'
		  }, function(data) {
			  $('#nextStep').append('下个环节：');
			  for (var i = 0; i < data.length; i++) {
				  $('#nextStep').append(data[i].name);
			  }
		  });
	  </script>
	  </c:if>

	  <form id="xform" method="post" action="${tenantPrefix}/operation/task-operation-completeTask.do" class="xf-form" enctype="multipart/form-data">
		<input id="humanTaskId" type="hidden" name="humanTaskId" value="${humanTaskId}">
		<div id="xf-form-table"></div>

		<c:if test="${humanTask.catalog == 'normal' || humanTask.catalog == 'vote'}">
		<div class="padding-top:20px;">
		  <fieldset>
		    <legend>意见</legend>
			<input type="hidden" id="_humantask_action_" name="_humantask_action_" value="">
		    <textarea name="_humantask_comment_" class="form-control"></textarea>
		  </fieldset>
		</div>
		</c:if>

	  </form>

	<div>
	  <c:forEach var="item" items="${logHumanTaskDtos}">
	  <c:if test="${not empty item.completeTime}">
	  <p>
		    <tags:user userId="${item.assignee}"/>
			<fmt:formatDate value="${item.completeTime}" type="both"/>
			${item.comment}</p>
	  </p>
	  </c:if>
	  </c:forEach>
	</div>

    </section>
	<!-- end of main -->

    <form id="f" action="form-template-save.do" method="post" style="display:none;">
	  <textarea id="__gef_content__" name="content">${xform.content}</textarea>
	</form>

	<div id="modal" class="modal fade">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-body">
	        <form>
	          <input type="hidden" name="humanTaskId" value="${humanTaskId}"/>
	          <input type="hidden" name="comment" value=""/>
			  <div class="input-group userPicker" style="width:200px;">
				<input id="_task_name_key" type="hidden" name="userId" class="input-medium" value="">
				<input type="text" class="form-control" name="username" placeholder="" value="">
				<div class="input-group-addon"><i class="glyphicon glyphicon-user"></i></div>
			  </div>
		      <br>
		      <button class="btn btn-default">提交</button>
		    </form>
	      </div>
		</div>
	  </div>
	</div>

	<div id="modalCommunicate" class="modal fade">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-body">
	        <form>
	          <input type="hidden" name="humanTaskId" value="${humanTaskId}"/>
			  <label>沟通人</label>
			  <div class="input-group userPicker" style="width:200px;">
				<input id="_task_name_key" type="hidden" name="userId" class="input-medium" value="">
				<input type="text" class="form-control" name="username" placeholder="" value="">
				<div class="input-group-addon"><i class="glyphicon glyphicon-user"></i></div>
			  </div>
			  <label>备注</label>
	          <textarea name="comment" class="form-control"></textarea>
		      <br>
		      <button class="btn btn-default">提交</button>
		    </form>
	      </div>
		</div>
	  </div>
	</div>

	<div id="modalCallback" class="modal fade">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-body">
	        <form>
	          <input type="hidden" name="humanTaskId" value="${humanTaskId}"/>
	          <textarea name="comment" class="form-control"></textarea>
		      <br>
		      <button class="btn btn-default">提交</button>
		    </form>
	      </div>
		</div>
	  </div>
	</div>

	<div id="modalCreateVote" class="modal fade">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-body">
	        <form>
	          <input type="hidden" name="humanTaskId" value="${humanTaskId}"/>
	          <input type="hidden" name="comment" value=""/>
			  <div class="input-group userPicker" style="width:200px;" data-multiple="true">
				<input id="_task_name_key" type="hidden" name="userIds" class="input-medium" value="">
				<input type="text" class="form-control" name="username" placeholder="" value="">
				<div class="input-group-addon"><i class="glyphicon glyphicon-user"></i></div>
			  </div>
		      <br>
		      <button class="btn btn-default">提交</button>
		    </form>
	      </div>
		</div>
	  </div>
	</div>

  </body>

</html>
