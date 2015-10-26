<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "dashboard");%>
<%pageContext.setAttribute("currentMenu", "dashboard");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="demo.demo.input.title" text="编辑"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#demoForm").validate({
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
    <%@include file="/header/dashboard.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/dashboard.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="demo.demo.input.title" text="编辑"/></h4>
		</header>

		<div class="content content-inner">

<form id="userRepoForm" method="post" action="${tenantPrefix}/mail/sendmail-send.do" class="form-horizontal">
  <div class="control-group">
	<label class="control-label" for="from">发信人</label>
    <div class="controls">
      <input type="text" id="from" name="from">
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="to">收信人</label>
    <div class="controls">
      <textarea id="to" name="to"></textarea>
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="subject">标题</label>
    <div class="controls">
      <input type="text" id="subject" name="subject">
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="content">内容</label>
    <div class="controls">
      <textarea id="content" name="content"></textarea>
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
