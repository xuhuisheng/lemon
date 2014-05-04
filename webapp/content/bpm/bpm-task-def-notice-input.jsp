<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "bpm-category");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>流程定义</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#userRepoForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });
})
    </script>
  </head>

  <body>
    <%@include file="/header/bpm-console.jsp"%>

	<div class="row-fluid">
	<%@include file="/menu/bpm-console.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

<form id="noticeForm" method="post" action="bpm-task-def-notice-save.do" class="form-horizontal">

  <input id="bpm-process_id" type="hidden" name="bpmProcessId" value="${param.bpmProcessId}">
  <input id="taskDefinitionKey" type="hidden" name="taskDefinitionKey" value="${param.taskDefinitionKey}">
  <div class="control-group">
    <label class="control-label">类型</label>
	<div class="controls">
	  <select name="type">
	    <option value="0">到达</option>
	    <option value="1">完成</option>
	    <option value="2">超时</option>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">提醒人</label>
	<div class="controls">
	  <input type="text" name="receiver" value="">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">提醒时间</label>
	<div class="controls">
	  <input type="text" name="dueDate" value="">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">邮件模板</label>
	<div class="controls">
	  <select name="bpmMailTemplateId">
	    <c:forEach items="${bpmMailTemplates}" var="item">
	    <option value="${item.id}">${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" type="submit" class="btn">保存</button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn">返回</button>
    </div>
  </div>

</form>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
