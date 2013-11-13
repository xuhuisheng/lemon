<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.springframework.security.web.FilterChainProxy"%>
<%@page import="org.springframework.security.web.SecurityFilterChain"%>
<%@page import="javax.servlet.Filter"%>
<%
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);
	FilterChainProxy filterChainProxy = (FilterChainProxy) ctx.getBean("org.springframework.security.filterChainProxy");
%>
<html>
  <head>
    <meta charset="utf-8">
	<title>filterChain</title>
    <style>
tbody tr:nth-child(odd) td,
tbody tr:nth-child(odd) th {
  background-color: #f9f9f9;
}
    </style>
  </head>
  <body>
    <table border="1">
      <thead>
	    <tr>
		  <th>requestMatcher</th>
		  <th>filters</th>
		</tr>
	  </thead>
      <tbody>
<%
	for (SecurityFilterChain securityFilterChain : filterChainProxy.getFilterChains()) {
		pageContext.setAttribute("securityFilterChain", securityFilterChain);

%>
        <tr>
          <td>${securityFilterChain.requestMatcher}</td>
          <td>
<%
		for (Filter filter : securityFilterChain.getFilters()) {
			pageContext.setAttribute("filter", filter);
%>
			${filter}<br>
<%
		}
%>
			&nbsp;
		  </td>
        </tr>
<%
	}
%>
      </tbody>
    </table>
  </body>
</html>
