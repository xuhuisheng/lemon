<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="java.util.Iterator"%>
<%@page import="javax.cache.Cache"%>
<%@page import="javax.cache.CacheManager"%>
<%
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);

	CacheManager cacheManager = ctx.getBean(CacheManager.class);

	Iterable<String> cacheNames = cacheManager.getCacheNames();

	String selectedCacheName = request.getParameter("name");
	String action = request.getParameter("action");
	String key = request.getParameter("key");
%>
<html>
  <head>
    <meta charset="utf-8">
	<title>cache</title>
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
		</tr>
	  </thead>
	  <tbody>
<%

	for (String cacheName : cacheNames) {
		Cache cache = cacheManager.getCache(cacheName);
		pageContext.setAttribute("cacheName", cacheName);
%>
	  <tr>
	    <td><a href="?name=${cacheName}">${cacheName}</a></td>
	  </tr>
<%
	}
%>
	  </tbody>
	</table>

<%
	if (selectedCacheName != null) {
%>
	<h1><%=selectedCacheName%></h1>
	<table border="1">
	  <thead>
		<tr>
		  <th>name</th>
		  <th>value</th>
		  <th>o</th>
		</tr>
	  </thead>
	  <tbody>
<%
		Cache cache = cacheManager.getCache(selectedCacheName);
		if ("remove".equals(action)) {
			cache.remove(key);
		}

		Iterator<Cache.Entry> iterator = cache.iterator();

		while (iterator.hasNext()) {
			Cache.Entry entry = iterator.next();
			pageContext.setAttribute("entry", entry);
%>
	  <tr>
	    <td>${entry.key}</td>
	    <td></td>
		<td>
		  <a href="?action=remove&name=${param.name}&key=${entry.key}">x</a>
		</td>
	  </tr>
<%
		}
%>
	  </tbody>
	</table>
<%
	}
%>

  </body>
</html>
