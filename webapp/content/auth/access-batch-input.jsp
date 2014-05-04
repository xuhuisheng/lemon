<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.access.batchinput.title" text="批量录入"/></title>
    <%@include file="/common/s.jsp"%>
    <style type="text/css">
.dragClass {
    background-color: #EEEEEE;
}
    </style>
    <script type="text/javascript">
$(function() {
    $("#accessForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });
});

function doPrev() {
    $('#accessForm').attr('method', 'get');
    $('#accessForm').attr('action', 'access-batch-list.do');
    $('#accessForm').submit();
}
    </script>
  </head>

  <body>
    <%@include file="/header/auth.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/auth.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
          <h4 class="title"><spring:message code="auth.access.batchinput.title" text="批量录入"/></h4>
		</header>

		<div class="content content-inner">

<form id="accessForm" method="post" action="access-batch-save.do" class="form-horizontal">
  <div class="control-group">
    <label class="control-label"><spring:message code='auth.access.input.type' text='类型'/></label>
    <div class="controls">
      <input type="hidden" name="type" value="${type}">
      <label style="padding-top:5px;">${type}<label>
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="access_text"><spring:message code='auth.access.input.import' text='批量导入'/></label>
    <div class="controls">
	  <textarea id="access_text" name="text" style="width:600px;" rows="10">${text}</textarea>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button type="button" class="btn" onclick="doPrev()"><spring:message code='core.step.prev' text='上一步'/></button>
	  &nbsp;
      <button id="submitButton" class="btn btn-primary a-submit"><spring:message code='core.step.next' text='下一步'/></button>
    </div>
  </div>
</form>
        </div>
      </article>

      <div class="m-spacer"></div>

    </section>
    <!-- end of main -->
	</div>

  </body>

</html>
