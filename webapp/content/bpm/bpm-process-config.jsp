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

<form id="userRepoForm" method="post" action="bpm-process-saveConfig.do" class="form-horizontal">

<c:forEach items="${taskMap}" var="taskDefinition">
      <article class="m-widget">
        <header class="header">
		  <h4 class="title">${taskDefinition.key.nameExpression}</h4>
		</header>
		<div class="content content-inner">
	<table class="table">
	  <thead>
	    <tr>
		  <th width="20%">类型</th>
		  <th width="20%">提醒人</th>
		  <th width="20%">提醒时间</th>
		  <th>邮件模板</th>
		  <th width="10%"><a class="btn btn-small" href="bpm-task-def-notice-input.do?bpmProcessId=${param.id}&taskDefinitionKey=${taskDefinition.key.key}">新增</a></th>
		</tr>
	  </thead>
	  <tbody>
	    <c:forEach items="${taskDefinition.value}" var="item">
	    <tr>
		  <td>${item.type == 1 ? '到达' : '超时'}</td>
		  <td>${item.receiver}</td>
		  <td>${item.dueDate}</td>
		  <td>${item.bpmMailTemplate.name}</td>
		  <td><a class="btn btn-small" href="bpm-task-def-notice-removeNotice.do?id=${item.id}">删除</a></td>
		</tr>
		</c:forEach>
	  </tbody>
	</table>
		</div>
      </article>
</c:forEach>

</form>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
