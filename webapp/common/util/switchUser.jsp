<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%pageContext.setAttribute("ctx", request.getContextPath());%>
<html>
  <head>
    <meta charset="utf-8">
	<title>switchUser</title>
    <style>
tbody tr:nth-child(odd) td,
tbody tr:nth-child(odd) th {
  background-color: #f9f9f9;
}
    </style>
	<script type="text/javascript">
function switchUser() {
	var name = prompt("请输入要切换的用户名", "");
	if (name != null && name != "") {
		window.location.href = "${ctx}/j_spring_security_switch_user?j_username=" + name;
	}
}
	</script>
  </head>
  <body>
    <div align="center">
	  <tags:hasPerm value="util,ROLE_PREVIOUS_ADMINISTRATOR">
		<a href="#" onclick="switchUser()">切换用户</a>
	  </tags:hasPerm>
	  <sec:authorize ifAnyGranted="ROLE_PREVIOUS_ADMINISTRATOR">
	    <a href="${ctx}/j_spring_security_exit_user">退出切换用户</a>
	  </sec:authorize>
	  <ul>
		<li>当前用户：<sec:authentication property="name" /></li>
		<li>是否模拟用户：
		  <sec:authorize ifAnyGranted="ROLE_PREVIOUS_ADMINISTRATOR">
		    是
		  </sec:authorize>
	      <sec:authorize ifNotGranted="ROLE_PREVIOUS_ADMINISTRATOR">
			否
		  </sec:authorize>
		</li>
	  </ul>
	</div>
  </body>
</html>
