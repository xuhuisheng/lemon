<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "scope");%>
<%pageContext.setAttribute("currentMenu", "workcal");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑工作日历时间段</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#scope-globalForm").validate({
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
    <%@include file="/header/scope.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/workcal.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="scope-global.scope-global.input.title" text="编辑"/></h4>
		</header>

		<div class="content content-inner">

<form id="scope-globalForm" method="post" action="workcal-part-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="scope-global_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="scope-global_name">时间段</label>
	<div class="controls">
	  <select name="shift">
	    <option value="0">上午</option>
	    <option value="1">下午</option>
	    <option value="2">前半夜</option>
	    <option value="3">后半夜</option>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="scope-global_name">开始时间</label>
	<div class="controls">
	  <input id="scope-global_name" type="text" name="startTime" value="${model.startTime}" size="40" class="text required" minlength="1" maxlength="5">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="scope-global_name">结束时间</label>
	<div class="controls">
	  <input id="scope-global_name" type="text" name="endTime" value="${model.endTime}" size="40" class="text required" minlength="1" maxlength="5">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="scope-global_name">工作日历类型</label>
	<div class="controls">
	  <select name="workcalRuleId">
	    <c:forEach items="${workcalRules}" var="item">
	    <option value="${item.id}">${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" type="submit" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn a-cancel"><spring:message code='core.input.back' text='返回'/></button>
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
