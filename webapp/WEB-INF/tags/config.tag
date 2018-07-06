<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="org.springframework.context.ApplicationContext"%>
<%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@tag import="java.util.Properties"%>
<%@attribute name="name" type="java.lang.String" required="true"%>
<%@attribute name="var" type="java.lang.String" required="true"%>
<%
  String name = (String) jspContext.getAttribute("name");
  String var = (String) jspContext.getAttribute("var");
  ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);
  Properties properties = (Properties) ctx.getBean("applicationProperties");
  String value = properties.getProperty(name);
  jspContext.setAttribute(var, value, PageContext.REQUEST_SCOPE);
%>
