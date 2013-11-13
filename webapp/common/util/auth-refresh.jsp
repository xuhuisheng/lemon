<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="com.mossle.security.client.ResourceDetailsMonitor"%>
<%
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);
	ResourceDetailsMonitor resourceDetailsMonitor = (ResourceDetailsMonitor) ctx.getBean("resourceDetailsMonitor");

	resourceDetailsMonitor.refresh();
%>
<html>
  <head>
    <meta charset="utf-8">
	<title>auth-refresh</title>
    <style>
tbody tr:nth-child(odd) td,
tbody tr:nth-child(odd) th {
  background-color: #f9f9f9;
}
    </style>
  </head>
  <body>
  </body>
</html>
