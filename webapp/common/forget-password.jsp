<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/common/taglibs.jsp"%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>忘记密码</title>
	<%@include file="/common/s.jsp"%>
  </head>

  <body onload='document.f.j_username.focus();'>

    <!-- start of header bar -->
    <div class="navbar navbar-inverse">
      <div class="navbar-inner">
        <div class="container">
          <a href="${scopePrefix}/" class="brand">Mossle</a>
        </div>
      </div><!-- /navbar-inner -->
    </div>
    <!-- end of header bar -->

	<div class="row-fluid">
	  <div class="span3"></div>

	<!-- start of main -->
    <section class="span6">
	  <div class="alert m-alert-error" ${param.error==true ? '' : 'style="display:none"'}>
        <strong><spring:message code="core.login.failure" text="登陆失败"/></strong>
		&nbsp;
        ${sessionScope['SPRING_SECURITY_LAST_EXCEPTION'].message}
      </div>
      <br>

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="core.login.title" text="登录"/></h4>
		</header>

		<div class="content content-inner">

<form id="userForm" name="f" method="post" action="${scopePrefix}/user/change-password.do" class="form-horizontal">
  <div class="control-group">
    <label class="control-label" for="oldPassword">原密码</label>
	<div class="controls">
      <input type='text' id="oldPassword" name='oldPassword' class="text" value="">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="newPassword">新密码</label>
	<div class="controls">
      <input type='text' id="newPassword" name='newPassword' class="text" value="">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="confirmPassword">重复新密码</label>
	<div class="controls">
      <input type='text' id="confirmPassword" name='confirmPassword' class="text" value="">
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <input class="btn" name="submit" type="submit" value="<spring:message code='core.login.submit' text='提交'/>"/>
    </div>
  </div>
</form>
        </div>
      </article>

      <div class="m-spacer"></div>
	</section>
	<!-- end of main -->

	  <div class="span3"></div>
    </div>

  </body>
</html>
