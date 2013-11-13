<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "acl");%>
<%pageContext.setAttribute("currentMenu", "acl");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.input.title" text="编辑用户"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#aclObjectTypeForm").validate({
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
    <%@include file="/header/acl.jsp"%>

	<div class="row-fluid">
	<%@include file="/menu/acl.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
          <h4 class="title"><spring:message code="user.user.input.title" text="编辑用户"/></h4>
		</header>

		<div class="content content-inner">

<form id="userForm" method="post" action="acl-entry!save.do?operationMode=STORE" class="form-horizontal">
  <s:if test="model != null">
  <input id="user_id" type="hidden" name="id" value="${model.id}">
  </s:if>
  <div class="control-group">
    <label class="control-label" for="user_username">次序</label>
	<div class="controls">
	  <input id="user_username" type="text" name="aceOrder" value="${model.aceOrder}" size="40" class="text required" minlength="1" maxlength="10">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="user_username">授予权限</label>
	<div class="controls">
	  是<input type="radio" name="granting" value="1">
	  否<input type="radio" name="granting" value="0">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="user_username">权限</label>
	<div class="controls">
	  READ<input type="radio" name="mask" value="1">
	  WRITE<input type="radio" name="mask" value="2">
	  UPDATE<input type="radio" name="mask" value="4">
	  DELETE<input type="radio" name="mask" value="8">
	  ADMINISTRATE<input type="radio" name="mask" value="16">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="user_username">审计授权</label>
	<div class="controls">
	  是<input type="radio" name="auditSuccess" value="1">
	  否<input type="radio" name="auditSuccess" value="0">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="user_username">审计取消</label>
	<div class="controls">
	  是<input type="radio" name="auditFailure" value="1">
	  否<input type="radio" name="auditFailure" value="0">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="user_username">数据</label>
	<div class="controls">
	  <select name="identityId">
	  <s:iterator value="aclObjectIdentities" var="item">
	    <option value="${item.id}">${item.aclObjectType.name}#${item.reference}</option>
	  </s:iterator>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="user_username">主体</label>
	<div class="controls">
	  <select name="sidId">
	  <s:iterator value="aclSids" var="item">
	    <option value="${item.id}">${item.name}</option>
	  </s:iterator>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn"><spring:message code='core.input.save' text='保存'/></button>
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
