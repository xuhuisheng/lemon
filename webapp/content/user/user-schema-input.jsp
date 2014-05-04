<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user-sys");%>
<%pageContext.setAttribute("currentMenu", "user-sys");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.userSchema.input.title" text="编辑用户库"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#userSchemaForm").validate({
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
    <%@include file="/header/user-sys.jsp"%>

	<div class="row-fluid">
	<%@include file="/menu/user-sys.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="auth.userSchema.input.title" text="编辑用户库"/></h4>
		</header>
		<div class="content content-inner">

<form id="userSchemaForm" method="post" action="user-schema-save.do?userSchemaationMode=STORE" class="form-horizontal">
  <input type="hidden" name="userRepoId" value="${param.userRepoId}">
  <c:if test="${model != null}">
  <input id="userSchema_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
	<label class="control-label" for="user-schema_name"><spring:message code='auth.userSchema.input.name' text='名称'/></label>
    <div class="controls">
      <input id="user-schema_name" type="text" name="name" value="${model.name}" size="40" class="text required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="user-schema_type">类型</label>
    <div class="controls">
      <select id="user-schema_type" name="type">
	    <option value="string">string</option>
	    <option value="boolean">boolean</option>
	    <option value="date">date</option>
	    <option value="long">long</option>
	    <option value="double">double</option>
	  </select>
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="user-schema_readOnly">只读</label>
    <div class="controls">
      <input id="user-schema_readOnly" type="checkbox" name="readOnly" value="1" ${model.readOnly == '1' ? 'checked' : ''}>
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="user-schema_notNull">非空</label>
    <div class="controls">
      <input id="user-schema_notNull" type="checkbox" name="notNull" value="1" ${model.notNull == '1' ? 'checked' : ''}>
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="user-schema_uniqueConstraint">唯一</label>
    <div class="controls">
      <input id="user-schema_uniqueConstraint" type="checkbox" name="uniqueConstraint" value="1" ${model.uniqueConstraint == '1' ? 'checked' : ''}>
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="user-schema_validator">检验方式</label>
    <div class="controls">
      <input id="user-schema_validator" type="text" name="validator" value="${model.validator}" size="40" class="text" maxlength="50">
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="user-schema_conversionPattern">转换方式</label>
    <div class="controls">
      <input id="user-schema_conversionPattern" type="text" name="conversionPattern" value="${model.conversionPattern}" size="40" class="text" maxlength="50">
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="user-schema_multiple">多值</label>
    <div class="controls">
      <input id="user-schema_multiple" type="checkbox" name="multiple" value="1" ${model.multiple == '1' ? 'checked' : ''}>
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
