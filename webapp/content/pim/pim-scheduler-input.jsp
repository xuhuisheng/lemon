<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "scheduler");%>
<%pageContext.setAttribute("currentMenu", "scheduler");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="pimScheduler.pimScheduler.input.title" text="编辑"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#pimSchedulerForm").validate({
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
    <%@include file="/header/pim.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/pim.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="pimScheduler.pimScheduler.input.title" text="编辑"/></h4>
		</header>

		<div class="content content-inner">

<form id="pimSchedulerForm" method="post" action="pim-scheduler-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="pimScheduler_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="pimScheduler_name"><spring:message code="pimScheduler.pimScheduler.input.name" text="名称"/></label>
	<div class="controls">
	  <input id="pimScheduler_name" type="text" name="name" value="${model.name}" size="40" class="text required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="pimScheduler_location">地址</label>
	<div class="controls">
	  <input id="pimScheduler_location" type="text" name="location" value="${model.location}" size="40" class="text required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="pimScheduler_type">类型</label>
	<div class="controls">
	  <select name="type">
        <option value="0">日程</option>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="pimScheduler_startTIme">开始时间</label>
	<div class="controls">
      <div class="input-append datetimepicker date" style="padding-left: 0px;">
	    <input id="pimScheduler_startTIme" type="text" name="startTime" value="<fmt:formatDate value='${model.startTime}' pattern='yyyy-MM-dd'/>" size="40" class="text required" minlength="2" maxlength="20" readonly style="background-color:white;cursor:default; width: 175px;">
	    <span class="add-on" style="padding-top: 2px; padding-bottom: 2px;"><i class="icon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="pimScheduler_endTime">结束时间</label>
	<div class="controls">
      <div class="input-append datetimepicker date" style="padding-left: 0px;">
	    <input id="pimScheduler_endTime" type="text" name="endTime" value="<fmt:formatDate value='${model.endTime}' pattern='yyyy-MM-dd'/>" size="40" class="text required" minlength="2" maxlength="20" readonly style="background-color:white;cursor:default; width: 175px;">
	    <span class="add-on" style="padding-top: 2px; padding-bottom: 2px;"><i class="icon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="pimScheduler_alertTime">提醒时间</label>
	<div class="controls">
	    <input id="pimScheduler_alertTime" type="text" name="alertTime" value="${model.alertTime}'">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="pimScheduler_content">备注</label>
	<div class="controls">
	  <textarea id="pimScheduler_content" name="content">${model.content}</textarea>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button type="submit" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
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
