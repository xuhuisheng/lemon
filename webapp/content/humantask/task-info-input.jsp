<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "humantask");%>
<%pageContext.setAttribute("currentMenu", "humantask");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#taskInfoForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });
})
    </script>
    <link type="text/css" rel="stylesheet" href="${tenantPrefix}/widgets/userpicker3-v2/userpicker.css">
    <script type="text/javascript" src="${tenantPrefix}/widgets/userpicker3-v2/userpicker.js"></script>
	<script type="text/javascript">
$(function() {
	createUserPicker({
		modalId: 'userPicker',
		showExpression: true,
		searchUrl: '${tenantPrefix}/rs/user/search',
		treeUrl: '${tenantPrefix}/rs/party/tree?partyStructTypeId=1',
		childUrl: '${tenantPrefix}/rs/party/searchUser'
	});
})
    </script>
  </head>

  <body>
    <%@include file="/header/humantask.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/humantask.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="taskInfoForm" method="post" action="task-info-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="taskInfo_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="taskInfo_presentationSubject">名称</label>
	<div class="col-sm-5">
	  <input id="taskInfo_presentationSubject" type="text" name="presentationSubject" value="${model.presentationSubject}" size="40" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="taskInfo_priority">优先级</label>
	<div class="col-sm-5">
	  <input id="taskInfo_priority" type="text" name="priority" value="${model.priority}" size="40" class="form-control number">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="taskInfo_descn">备注</label>
	<div class="col-sm-5">
	  <textarea id="taskInfo_descn" type="text" name="description" class="form-control">${model.description}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="taskInfo_assignee">负责人</label>
	<div class="col-sm-5">
      <div class="input-group userPicker" style="display:block-inline;">
        <input id="_task_name_key" type="hidden" name="assignee" class="input-medium" value="${model.assignee}">
        <input type="text" name="taskAssigneeNames" value="<tags:user userId='${model.assignee}'/>" class="form-control" readOnly>
        <div class="input-group-addon"><i class="glyphicon glyphicon-user"></i></div>
      </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="taskInfo_status">状态</label>
	<div class="col-sm-5">
	  <select id="taskInfo_status" name="status" class="form-control">
	    <option value="active" ${model.status == 'active' ? 'selected' : ''}>进行中</option>
		<option value="completed" ${model.status == 'completed' ? 'selected' : ''}>完成</option>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="taskInfo_expirationTime">过期时间</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date">
	    <input id="workReportInfo_reportDate" type="text" name="expirationTime" value="<fmt:formatDate value='${model.expirationTime}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-offset-1 col-md-11">
      <button type="submit" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn btn-link a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
    </div>
  </div>
</form>

		</div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>

