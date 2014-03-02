<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="org.springframework.context.ApplicationContext"%>
<%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@tag import="com.mossle.security.perm.PermissionChecker"%>
<%@attribute name="value" type="java.lang.String" required="true"%>
<%
  String value = (String) jspContext.getAttribute("value");
  ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);
  PermissionChecker permissionChecker = ctx.getBean(PermissionChecker.class);
  boolean authorized = permissionChecker.isAuthorized(value);
  if (authorized) {
%>
<jsp:doBody/>
<%
  }
%>
