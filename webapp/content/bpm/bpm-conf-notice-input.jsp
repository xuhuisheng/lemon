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

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">流程分类</h4>
		</header>
		<div class="content content-inner">

<form id="noticeForm" method="post" action="bpm-conf-notice-save.do" class="form-horizontal">

  <input id="bpm-process_id" type="hidden" name="bpmConfNodeId" value="${param.bpmConfNodeId}">
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
    <label class="control-label">提醒方式</label>
	<div class="controls">
	  <c:forEach items="${types}" var="item">
	  <label><input type="checkbox" name="notificationTypes" value="${item}">${item}</label>
	  </c:forEach>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">模板</label>
	<div class="controls">
	  <select name="templateCode">
	    <c:forEach items="${templateDtos}" var="item">
	    <option value="${item.code}">${item.name}</option>
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

		</div>
      </article>
    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
