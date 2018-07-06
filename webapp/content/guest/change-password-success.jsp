<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/common/taglibs.jsp"%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>修改密码成功</title>
	<%@include file="/common/s.jsp"%>
  </head>

  <body>

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
	  <div class="alert m-alert-success">
        <strong>密码修改成功，请<a href="${scopePrefix}/common/util/login.jsp">重新登录</a>。</strong>
      </div>

	  <div class="span3"></div>
    </div>

  </body>
</html>
