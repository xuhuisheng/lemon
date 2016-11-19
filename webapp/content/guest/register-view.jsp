<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user");%>
<%pageContext.setAttribute("currentMenu", "user");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>注册</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#userBaseForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error',
        rules: {
            username: {
                remote: {
                    url: 'register-checkUsername.do'
                }
            }
        },
        messages: {
            username: {
                remote: "<spring:message code='user.user.input.duplicate' text='存在重复账号'/>"
            }
        }
    });
})
    </script>
  </head>

  <body>
    <%@include file="/header/guest.jsp"%>

    <div class="row-fluid">

	<!-- start of main -->
    <section id="m-main" class="span12">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">注册</h4>
		</header>
		<div class="content content-inner">

<form id="userBaseForm" method="post" action="register-save.do" class="form-horizontal">
  <div class="control-group">
    <label class="control-label" for="userBase_username"><spring:message code="user.user.input.username" text="账号"/></label>
	<div class="controls">
	  <input id="userBase_username" type="text" name="username" value="${model.username}" size="40" class="text required" minlength="2" maxlength="50">
    </div>
  </div>

  <div class="control-group">
    <label class="control-label" for="userBase_password"><spring:message code="user.user.input.password" text="密码"/></label>
	<div class="controls">
	  <input id="userBase_password" type="password" name="password" size="40" class="text required" maxlength="10">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_confirmPassword"><spring:message code="user.user.input.confirmpassword" text="验证密码"/></label>
	<div class="controls">
	  <input id="userBase_confirmPassword" type="password" name="confirmPassword" size="40" class="text required" maxlength="10" equalTo="#userBase_password">
    </div>
  </div>

  <div class="control-group">
    <label class="control-label" for="userBase_displayName">显示名</label>
	<div class="controls">
	  <input id="userBase_displayName" type="text" name="displayName" value="${model.displayName}" size="40" class="text" minlength="2" maxlength="50">
    </div>
  </div>

  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
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
