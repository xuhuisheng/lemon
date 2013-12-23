<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "forum");%>
<%pageContext.setAttribute("currentMenu", "forum");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="cal-info.cal-info.input.title" text="编辑"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#cal-infoForm").validate({
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
    <%@include file="/header/cal-info.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/cal-info.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="cal-info.cal-info.input.title" text="编辑"/></h4>
		</header>

		<div class="content content-inner">

<form id="cal-infoForm" method="post" action="forum-topic!createTopic.do?operationMode=STORE" class="form-horizontal">
  <s:if test="model != null">
  <input id="cal-info_id" type="hidden" name="id" value="${model.id}">
  </s:if>
  <div class="control-group">
    <label class="control-label" for="cal-info_name">标题</label>
	<div class="controls">
	  <input id="cal-info_name" type="text" name="title" value="${model.title}" size="40" class="text required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="cal-info_descn">内容</label>
	<div class="controls">
	  <textarea id="cal-info_descn" name="content">${model.content}</textarea>
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
