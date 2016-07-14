<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s.jsp"%>

    <link type="text/css" rel="stylesheet" href="${tenantPrefix}/widgets/userpicker/userpicker.css">
    <script type="text/javascript" src="${tenantPrefix}/widgets/userpicker/userpicker.js"></script>
	<script type="text/javascript">
$(function() {
	createUserPicker({
		modalId: 'userPicker',
		url: '${tenantPrefix}/rs/user/search'
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

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">编辑</h4>
		</header>

		<div class="content content-inner">

<form id="demoForm" method="post" action="workspace-delegateTask.do" class="form-horizontal">
  <input id="demo_id" type="hidden" name="taskId" value="${param.taskId}">
  <div class="control-group">
    <label class="control-label">代理人</label>
	<div class="controls">
      <div class="input-append userPicker">
		<input type="hidden" name="userId" class="input-medium" value="">
		<input type="text" style="width: 175px;" value="">
		<span class="add-on"><i class="icon-user"></i></span>
      </div>
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
        </div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
