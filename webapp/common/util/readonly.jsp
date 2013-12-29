<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/common/taglibs.jsp"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="com.mossle.core.hibernate.ReadOnlyTransactionManager"%>
<%@page import="com.mossle.security.perm.PermissionChecker"%>
<%
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);
	PermissionChecker permissionChecker = (PermissionChecker) ctx.getBean(PermissionChecker.class);
	ReadOnlyTransactionManager readOnlyTransactionManager = (ReadOnlyTransactionManager) ctx.getBean(ReadOnlyTransactionManager.class);
	if ("true".equals(request.getParameter("readonly"))) {
		permissionChecker.setReadOnly(true);
		readOnlyTransactionManager.setReadOnly(true);
		response.sendRedirect("readonly.jsp");
	} else if ("false".equals(request.getParameter("readonly"))) {
		permissionChecker.setReadOnly(false);
		readOnlyTransactionManager.setReadOnly(false);
		response.sendRedirect("readonly.jsp");
	}
	pageContext.setAttribute("readOnly", permissionChecker.isReadOnly());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta charset="utf-8">
    <title>readonly</title>
    <style>
tbody tr:nth-child(odd) td,
tbody tr:nth-child(odd) th {
  background-color: #f9f9f9;
}
    </style>
  </head>
  <body>
    <form action="readonly.jsp" method="post">
	  <input type="hidden" name="readonly" value="${not readOnly}">
	  <input type="submit" value="${readOnly ? '取消只读' : '设置只读'}" style="color:white;background-color:${readOnly ? 'red' : 'green'}">
	</form>
  </body>
</html>
