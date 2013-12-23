<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.perm.input.title" text="编辑权限"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#permForm").validate({
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
		  <h4 class="title"><spring:message code="auth.perm.input.title" text="编辑权限"/></h4>
		</header>

		<div class="content content-inner">

<form id="permForm" method="post" action="perm!save.do?operationMode=STORE" class="form-horizontal">
  <s:if test="model != null">
  <input id="perm_id" type="hidden" name="id" value="${model.id}">
  </s:if>
  <div class="control-group">
	<label class="control-label" for="perm_resc"><spring:message code="auth.perm.input.resource" text="资源"/></label>
    <div class="controls">
      <select id="perm_resc" name="rescId">
	    <s:iterator value="rescs" var="item">
	    <option value="${item.id}" ${model.resc.id==item.id ? 'selected' : ''}>${item.name}</option>
		</s:iterator>
	  </select>
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="perm_oper"><spring:message code="auth.perm.input.operation" text="操作"/>:</label>
    <div class="controls">
      <select id="perm_oper" name="operId">
	    <option value="0"></option>
	    <s:iterator value="opers" var="item">
	    <option value="${item.id}" ${model.oper.id==item.id ? 'selected' : ''}>${item.name}</option>
		</s:iterator>
	  </select>
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
