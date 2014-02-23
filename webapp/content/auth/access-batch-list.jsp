<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.access.batch.title" text="批量处理"/></title>
    <%@include file="/common/s.jsp"%>
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

function doSort() {
    $('#accessForm').attr('method', 'get');
    $('#accessForm').attr('action', 'access-batch-edit.do');
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

<form id="accessForm" method="get" action="access-batch-input.do?operationMode=STORE" class="form-horizontal">
  <div class="control-group">
	<label class="control-label" for="access_type"><spring:message code='auth.access.input.type' text='类型'/></label>
    <div class="controls">
	  <select id="access_type" name="type">
	    <option value="URL" ${type == 'URL' ? 'selected' : ''}>URL</option>
	    <option value="METHOD" ${type == 'METHOD' ? 'selected' : ''}>METHOD</option>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn a-submit">提交</button>
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
