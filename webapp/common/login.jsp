<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/common/taglibs.jsp"%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="core.login.title" text="登录"/></title>
	<%@include file="/common/s3.jsp"%>
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
<div class="navbar navbar-inverse navbar-fixed-top">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="${tenantPrefix}">
	    Lemon <sub><small>1.6.0</small></sub>
      </a>
    </div>
  </div>
</div>
    <!-- end of header bar -->

	<div class="row" style="margin-top:70px;">
	  <div class="container-fluid">

	  <div class="col-md-4"></div>

	<!-- start of main -->
    <section class="col-md-4">
	  <div class="alert alert-danger" role="alert" ${param.error==true ? '' : 'style="display:none"'}>
        <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
        <strong><spring:message code="core.login.failure" text="登陆失败"/></strong>
		&nbsp;
        ${sessionScope['SPRING_SECURITY_LAST_EXCEPTION'].message}
      </div>

      <article class="panel panel-default">
        <header class="panel-heading">
		  <h4 class="title"><spring:message code="core.login.title" text="登录"/></h4>
		</header>

		<div class="panel-body">

<form id="userForm" name="f" method="post" action="${tenantPrefix}/j_spring_security_check" class="form-horizontal">
  <div class="form-group" style="display:none">
    <label class="col-md-2 control-label" for="tenant">租户</label>
	<div class="col-md-10">
      <input type='text' id="tenant" name='tenant' class="form-control" value="default">
    </div>
  </div>
  <div class="form-group">
    <label class="col-md-2 control-label" for="username"><spring:message code="core.login.username" text="账号"/></label>
	<div class="col-md-10">
      <input type='text' id="username" name='j_username' class="form-control" value="${empty sessionScope['SECURITY_LAST_USERNAME'] ? cookie['SECURITY_LAST_USERNAME'].value : sessionScope['SECURITY_LAST_USERNAME']}" aria-describedby="inputSuccess3Status">
      <span id="usernameText" class="glyphicon glyphicon-remove form-control-feedback" aria-hidden="true" style="right:15px;cursor:pointer;pointer-events:auto;" onclick="$('#username').val('');$('#usernameText').hide();"></span>
    </div>
  </div>
  <div class="form-group">
    <label class="col-md-2 control-label" for="password"><spring:message code="core.login.password" text="密码"/></label>
	<div class="col-md-10">
      <input type='password' id="password" name='j_password' class="form-control" value=''>
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-2"></div>
    <div class="col-md-10">
      <input class="btn btn-default" name="submit" type="submit" value="<spring:message code='core.login.submit' text='提交'/>"/>
    </div>
  </div>
</form>
        </div>
      </article>

      <div class="m-spacer"></div>
	</section>
	<!-- end of main -->

	  <div class="col-md-4"></div>
	  </div>
    </div>

  </body>
</html>
