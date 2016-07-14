<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="org.springframework.context.ApplicationContext"%>
<%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@tag import="java.util.Properties"%>
<%
  ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);
  Properties properties = (Properties) ctx.getBean("applicationProperties");
  String applicationBaseUrl = properties.getProperty("application.baseUrl");

  out.print(applicationBaseUrl);
%>
