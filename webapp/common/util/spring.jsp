<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<html>
  <head>
    <meta charset="utf-8">
	<title>spring</title>
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
		  <th>name</th>
		  <th>type</th>
		</tr>
	  </thead>
	  <tbody>
<%
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);

	List<String> beanDefinitionNames = new ArrayList<String>(Arrays.asList(ctx.getBeanDefinitionNames()));
	Collections.sort(beanDefinitionNames);

	for (String name : beanDefinitionNames) {
		pageContext.setAttribute("name", name);
		pageContext.setAttribute("clz", ctx.getBean(name).getClass().getName());
%>
		<tr>
		  <td>${name}</td>
		  <td>${clz}</td>
		</tr>
<%
	}
%>
	  </tbody>
	</table>
  </body>
</html>
