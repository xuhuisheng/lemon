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
	<link href="${cdnPrefix}/public/mossle-xform/0.0.11/styles/xform.css" rel="stylesheet">
    <script type="text/javascript" src="${cdnPrefix}/public/mossle-xform/0.0.11/xform-all.js"></script>

    <link type="text/css" rel="stylesheet" href="${cdnPrefix}/public/mossle-userpicker/3.0/userpicker.css">
    <script type="text/javascript" src="${cdnPrefix}/public/mossle-userpicker/3.0/userpicker.js"></script>

    <link type="text/css" rel="stylesheet" href="${cdnPrefix}/public/webuploader/0.1.5/webuploader.css">
	<script type="text/javascript" src="${cdnPrefix}/public/webuploader/0.1.5/webuploader.js"></script>

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
		treeUrl: '${tenantPrefix}/party/rs/tree-data?type=struct',
		childUrl: '${tenantPrefix}/party/rs/search-user'
	});
})
    </script>

	<script type="text/javascript" src="${cdnPrefix}/public/mossle-operation/0.0.4/TaskOperation.js?v=20190327-01"></script>
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

	  <form id="xform" method="post" action="${tenantPrefix}/operation/task-operation-completeTask.do" class="xf-form" enctype="multipart/form-data">
		<input id="humanTaskId" type="hidden" name="humanTaskId" value="${humanTaskId}">
		<div id="xf-form-table"></div>
		<input type="hidden" id="_humantask_action_" name="_humantask_action_" value="">
		<input type="hidden" id="_humantask_comment_" name="_humantask_comment_" value="">
	  </form>

	<div>
	<br>

<div class="panel panel-default">
<table width="100%" cellspacing="0" cellpadding="0" border="0" align="center" class="table table-border">
  <thead>
    <tr>
	  <th>环节</th>
	  <th>审批人</th>
	  <th>时间</th>
	  <th>结果</th>
	  <th>意见</th>
	</tr>
  </thead>
  <tbody>
	  <c:forEach var="item" items="${logHumanTaskDtos}">
	  <c:if test="${not empty item.completeTime}">
    <tr>
	  <td>${item.name}</td>
	  <td><tags:user userId="${item.assignee}"/></td>
	  <td><fmt:formatDate value="${item.completeTime}" type="both"/></td>
	  <td>${item.action}</td>
	  <td>${item.comment}</td>
	</tr>
	  </c:if>
	  </c:forEach>
  </tbody>
</table>
</div>

	</div>

	<br>
	<br>
	<br>

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


<div class="navbar navbar-default navbar-fixed-bottom">
  <div class="container-fluid">
    <div class="text-center" style="padding-top:8px;">
<c:if test="${humanTask.catalog == 'normal' || humanTask.catalog == 'vote'}">
		意见：
		<textarea name="_humantask_comment_content_" class="form-control" id="task-comment"></textarea>
<style>
#task-comment {
	height: 32px;
	vertical-align: middle;
	display: inline;
	width: auto;
}

#task-comment:hover {
	height: 100px;
	vertical-align: text-bottom;
}
</style>
</c:if>

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
  </div>
</div>

  </body>

</html>
