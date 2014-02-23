<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "bpm-category");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>邮件模板</title>
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
		  <h4 class="title">邮件模板</h4>
		</header>
		<div class="content content-inner">

<form id="userRepoForm" method="post" action="bpm-mail-template-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="userRepo_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
	<label class="control-label" for="bpm-mail-template_name">名称</label>
    <div class="controls">
      <input id="bpm-mail-template_name" type="text" name="name" value="${model.name}" size="40" class="text required" minlength="1" maxlength="10">
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="bpm-mail-template_subject">标题</label>
    <div class="controls">
      <input id="bpm-mail-template_subject" type="text" name="subject" value="${model.subject}" size="40" class="text required" minlength="1" maxlength="50">
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="bpm-mail-template_content">内容</label>
    <div class="controls">
      <textarea id="bpm-mail-template_content" name="content">${model.content}</textarea>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
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
