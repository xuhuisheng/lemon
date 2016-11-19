<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#user-statusForm").validate({
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
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="userForm" method="post" action="user-status-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="user_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="user_username"><spring:message code="user.user.input.username" text="账号"/></label>
	<div class="col-sm-5">
	  <input id="user_username" type="text" name="username" value="${model.username}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <c:if test="${model == null}">
  <div class="form-group">
    <label class="control-label col-md-1" for="user_password"><spring:message code="user.user.input.password" text="密码"/></label>
	<div class="col-sm-5">
	  <input id="user_password" type="password" name="password" size="40" class="form-control required" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="user_confirmpassword"><spring:message code="user.user.input.confirmpassword" text="验证密码"/></label>
	<div class="col-sm-5">
	  <input id="user_confirmpassword" type="password" name="confirmPassword" size="40" class="form-control required" maxlength="10" equalTo="#user_password">
    </div>
  </div>
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="user_status"><spring:message code="user.user.input.enabled" text="启用"/></label>
	<div class="col-sm-5">
	  <input id="user_status" type="checkbox" name="status" value="1" ${model.status == 1 ? 'checked' : ''}>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="user_ref"><spring:message code="user.user.input.ref" text="引用"/></label>
	<div class="col-sm-5">
	  <input id="user_ref" type="text" name="ref" value="${model.ref}" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-offset-1 col-md-11">
      <button id="submitButton" class="btn btn-default"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn btn-link"><spring:message code='core.input.back' text='返回'/></button>
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

