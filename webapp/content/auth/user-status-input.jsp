<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.input.title" text="编辑用户"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#userForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error',
        rules: {
            username: {
                remote: {
                    url: 'user-status-checkUsername.do',
                    data: {
                        <c:if test="${model != null}">
                        id: function() {
                            return $('#user_id').val();
                        }
                        </c:if>
                    }
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
    <%@include file="/header/auth.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/auth.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="user.user.input.title" text="编辑用户"/></h4>
		</header>

		<div class="content content-inner">

<form id="userForm" method="post" action="user-status-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="user_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="user_username"><spring:message code="user.user.input.username" text="账号"/></label>
	<div class="controls">
	  <input id="user_username" type="text" name="username" value="${model.username}" size="40" class="text required" minlength="2" maxlength="50">
    </div>
  </div>
  <c:if test="${model == null}">
  <div class="control-group">
    <label class="control-label" for="user_password"><spring:message code="user.user.input.password" text="密码"/></label>
	<div class="controls">
	  <input id="user_password" type="password" name="password" size="40" class="text required" maxlength="10">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="user_confirmpassword"><spring:message code="user.user.input.confirmpassword" text="验证密码"/></label>
	<div class="controls">
	  <input id="user_confirmpassword" type="password" name="confirmPassword" size="40" class="text required" maxlength="10" equalTo="#user_password">
    </div>
  </div>
  </c:if>
  <div class="control-group">
    <label class="control-label" for="user_status"><spring:message code="user.user.input.enabled" text="启用"/></label>
	<div class="controls">
	  <input id="user_status" type="checkbox" name="status" value="1" ${model.status == 1 ? 'checked' : ''}>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="user_ref"><spring:message code="user.user.input.ref" text="引用"/></label>
	<div class="controls">
	  <input id="user_ref" type="text" name="ref" value="${model.ref}">
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
