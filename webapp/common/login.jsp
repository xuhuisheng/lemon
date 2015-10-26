<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/common/taglibs.jsp"%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="core.login.title" text="登录"/></title>
	<%@include file="/common/s.jsp"%>
	<script type="text/javascript">
$(function() {
	focusTenant();
});

function focusTenant() {
	if (document.f.tenant.value == '') {
		document.f.tenant.focus();
	} else {
		focusUsername();
	}
}

function focusUsername() {
	if (document.f.j_username.value == '') {
		document.f.j_username.focus();
	} else {
		document.f.j_password.focus();
	}
}
	</script>
  </head>

  <body>

    <!-- start of header bar -->
    <div class="navbar navbar-inverse">
      <div class="navbar-inner">
        <div class="container">
          <a href="${tenantPrefix}/" class="brand">Mossle</a>
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

<form id="userForm" name="f" method="post" action="${tenantPrefix}/j_spring_security_check" class="form-horizontal">
  <div class="control-group" style="display:none;">
    <label class="control-label" for="tenant">租户</label>
	<div class="controls">
      <input type='text' id="tenant" name='tenant' class="text" value="default">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="username"><spring:message code="core.login.username" text="账号"/></label>
	<div class="controls">
      <input type='text' id="username" name='j_username' class="text" value="${empty sessionScope['SECURITY_LAST_USERNAME'] ? cookie['SECURITY_LAST_USERNAME'].value : sessionScope['SECURITY_LAST_USERNAME']}">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="password"><spring:message code="core.login.password" text="密码"/></label>
	<div class="controls">
      <input type='password' id="password" name='j_password' class="text" value=''>
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
