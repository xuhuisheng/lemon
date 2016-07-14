<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "my");%>
<%pageContext.setAttribute("currentMenu", "my");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.changepassword.title" text="修改密码"/></title>
    <%@include file="/common/s3.jsp"%>
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
    <%@include file="/header/my.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/my.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="margin-top:65px;">

      <article class="panel panel-default">
        <header class="panel-heading">
		  <spring:message code="user.user.input.title" text="编辑用户"/>
		</header>
		<div class="panel-body">

<form id="userForm" method="post" action="my-change-password-save.do" class="form-horizontal">
  <div class="form-group">
    <label class="control-label col-md-1" for="oldPassword"><spring:message code="user.user.changepassword.old" text="原密码"/></label>
	<div class="col-sm-5">
	  <input id="oldPassword" name="oldPassword" type="password" value="" class="form-control required" maxlength="20">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="oldPassword"><spring:message code="user.user.changepassword.new" text="新密码"/></label>
	<div class="col-sm-5">
      <input id="newPassword" name="newPassword" type="password" value="" class="form-control required" maxlength="20">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="confirmPassword"><spring:message code="user.user.changepassword.confirm" text="确认密码"/></label>
	<div class="col-sm-5">
      <input id="confirmPassword" name="confirmPassword" type="password" value="" equalTo="#newPassword" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-offset-1 col-md-11">
      <button id="submitButton" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn btn-link"><spring:message code='core.input.back' text='返回'/></button>
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
