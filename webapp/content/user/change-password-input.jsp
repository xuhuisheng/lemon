<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.changepassword.title" text="修改密码"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $('#userForm').validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });
});
    </script>
  </head>

  <body>
    <%@include file="/header/user.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/user.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="user.user.changepassword.title" text="修改密码"/></h4>
		</header>

		<div class="content content-inner">

<form id="userForm" method="post" action="change-password-save.do?operationMode=STORE" class="form-horizontal">
  <div class="control-group">
    <label class="control-label" for="oldPassword"><spring:message code="user.user.changepassword.old" text="原密码"/></label>
	<div class="controls">
	  <input id="oldPassword" name="oldPassword" type="password" value="" class="required" maxlength="20">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="oldPassword"><spring:message code="user.user.changepassword.new" text="新密码"/></label>
	<div class="controls">
      <input id="newPassword" name="newPassword" type="password" value="" class="required" maxlength="20">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="confirmPassword"><spring:message code="user.user.changepassword.confirm" text="确认密码"/></label>
	<div class="controls">
      <input id="confirmPassword" name="confirmPassword" type="password" value="" equalTo="#newPassword">
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
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
