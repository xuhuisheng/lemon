<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.oper.input.title" text="编辑操作"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#operForm").validate({
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
    <%@include file="/header/auth.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/auth.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="auth.oper.input.title" text="编辑操作"/></h4>
		</header>

		<div class="content content-inner">

<form id="operForm" method="post" action="oper!save.do?operationMode=STORE" class="form-horizontal">
  <s:if test="model != null">
  <input id="oper_id" type="hidden" name="id" value="${model.id}">
  </s:if>
  <div class="control-group">
	<label class="control-label" for="oper_name"><spring:message code='auth.oper.input.name' text='名称'/></label>
    <div class="controls">
      <input id="oper_name" type="text" name="name" value="${model.name}" size="40" class="text required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="oper_mask"><spring:message code='auth.oper.input.mask' text='掩码'/></label>
    <div class="controls">
      <input id="oper_mask" type="text" name="mask" value="${model.mask}" size="40" class="text required number" minlength="1" maxlength="10">
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="oper_code"><spring:message code='auth.oper.input.code' text='代码'/></label>
    <div class="controls">
      <input id="oper_code" type="text" name="code" value="${model.code}" size="40" class="text required" minlength="1" maxlength="1">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="oper_descn"><spring:message code='auth.oper.input.description' text='描述'/></label>
    <div class="controls">
      <textarea id="oper_descn" name="descn" maxlength="60" rows="4">${model.descn}</textarea>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn"><spring:message code='core.input.back' text='返回'/></button>
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
