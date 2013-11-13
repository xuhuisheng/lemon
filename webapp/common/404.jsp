<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@include file="/common/taglibs.jsp"%>
<%response.setStatus(200);%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>404 - <spring:message code="core.404.notfound" text="页面不存在"/></title>
	<link href="${ctx}/s/bootstrap/css/bootstrap.min.css" rel="stylesheet">
	<link href="${ctx}/s/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">
	<script type="text/javascript" src="${ctx}/s/jquery/jquery.min.js"></script>
	<script type="text/javascript">
$(function() {
	$('#targetContentDiv').height($(window).innerHeight() - 150);
})
    </script>
	<style type="text/css">
#targetContentDiv {
	background-color: #b8b8b8;
	padding: 70px 0 80px;
	text-align: center;
}

#targetContentDiv h1 {
	font-size: 120px;
	letter-spacing: -2px;
    line-height: 1;
}

#targetContentDiv p {
	font-size: 40px;
    font-weight: 200;
    line-height: 1.25;
	font-weight: bold;
	padding: 10px;
}

#targetContentDiv li {
	display: inline;
	list-style: none outside none;
}
	</style>
  </head>

  <body>

	<div id="targetContentDiv">
	  <div class="container">
		<h1>404</h1>
		<p><spring:message code="core.404.notfound" text="页面不存在"/></p>
		<ul>
		  <li><a class="btn btn-primary" href="javascript:void(0);" onclick="history.back()"><spring:message code="core.404.back" text="返回"/></a></li>
		  <li><a class="btn" href="${ctx}/j_spring_security_logout"><spring:message code="core.404.logout" text="注销"/></a></li>
		</ul>
      </div>
	</div>

  </body>

</html>
