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
		searchUrl: '${tenantPrefix}/user/rs/s',
		treeUrl: '${tenantPrefix}/party/rs/tree-data?type=struct',
		childUrl: '${tenantPrefix}/party/rs/search-user'
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

	<script type="text/javascript" src="${cdnPrefix}/public/mossle-operation/0.0.4/TaskOperation.js"></script>
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
	  </form>

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
	  <%--
	  <c:if test="${not empty item.completeTime}">
	  --%>
    <tr>
	  <td>${item.name}</td>
	  <td><tags:user userId="${item.assignee}"/></td>
	  <td><fmt:formatDate value="${item.completeTime}" type="both"/></td>
	  <td>${item.action}</td>
	  <td>${item.comment}</td>
	</tr>
	  <%--
	  </c:if>
	  --%>
	  </c:forEach>
  </tbody>
</table>
</div>

    </section>
	<!-- end of main -->

    <form id="f" action="form-template-save.do" method="post" style="display:none;">
	  <textarea id="__gef_content__" name="content">${fn:replace(fn:replace(xform.content, '"readOnly": false', '"readOnly": true'), '"readOnly":false', '"readOnly":true')}</textarea>
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
