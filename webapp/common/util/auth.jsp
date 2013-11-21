<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.List"%>
<%@include file="/common/taglibs.jsp"%>
<sec:authentication property="authorities" var="authorities" scope="page"/>
<html>
  <head>
    <meta charset="utf-8">
	<title>auth</title>
    <style>
tbody tr:nth-child(odd) td,
tbody tr:nth-child(odd) th {
  background-color: #f9f9f9;
}
    </style>
  </head>
  <body>
    <p>
	  <sec:authentication property="principal"/>
	</p>

	<hr>

    <p>
<%
	List authorities = (List) pageContext.getAttribute("authorities");
	for (Object authority : authorities) {
		pageContext.setAttribute("authority", authority);
%>
	${authority.authority}<br>
<%
	}
%>
    </p>
  </body>
</html>
