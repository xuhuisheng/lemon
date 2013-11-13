<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="com.mossle.security.client.AutoLoginFilter"%>
<%
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);
    AutoLoginFilter autoLoginFilter = (AutoLoginFilter) ctx.getBean("autoLoginFilter");

    if (request.getParameter("toggle") != null) {
        autoLoginFilter.setEnabled(!autoLoginFilter.isEnabled());
        response.sendRedirect("autologin.jsp");
    }

    pageContext.setAttribute("autoLoginFilter", autoLoginFilter);
%>
<html>
  <head>
    <meta charset="utf-8">
    <title>autologin</title>
    <style>
tbody tr:nth-child(odd) td,
tbody tr:nth-child(odd) th {
  background-color: #f9f9f9;
}
    </style>
  </head>
  <body>
    <table border="1">
     <tbody>
        <tr>
          <td>状态</td>
          <td>${autoLoginFilter.enabled}</td>
          <td><button onclick="location.href='?toggle=true'">${autoLoginFilter.enabled ? '禁用' : '启用'}</button></td>
        </tr>
        <tr>
          <td>默认用户</td>
          <td>${autoLoginFilter.defaultUserName}</td>
          <td>&nbsp;</td>
        </tr>
      </tbody>
    </table>
  </body>
</html>
