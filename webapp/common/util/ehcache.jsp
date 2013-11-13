<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="net.sf.ehcache.*"%>
<%@page import="com.mossle.core.util.*"%>
<%!
	Object getValue(Object value) throws Exception {
		if (value.getClass().getName().startsWith("org.hibernate.cache.ehcache.internal.strategy.AbstractReadWriteEhcacheAccessStrategy$Item")) {
			value = ReflectUtils.getFieldValue(value, "value");
			//if (value instanceof org.hibernate.cache.spi.entry.CacheEntry) {
			//}
		}
		return value;
	}
%>
<%
	CacheManager cacheManager = CacheManager.getInstance();
%>
<html>
  <head>
    <meta charset="utf-8">
	<title>ehcache</title>
    <style>
tbody tr:nth-child(odd) td,
tbody tr:nth-child(odd) th {
  background-color: #f9f9f9;
}
    </style>
  </head>
  <body>
    <table border="1" width="100%">
      <thead>
        <tr>
          <th>cache</th>
          <th>&nbsp;</th>
        </tr>
      </thead>
      <tbody>
<%
	for (String cacheName : cacheManager.getCacheNames()) {
		Cache cache = cacheManager.getCache(cacheName);
		pageContext.setAttribute("cache", cache);
%>
        <tr>
	      <td>${cache}</td>
	      <td><a href="ehcache.jsp?name=${cache.name}">view</a></td>
	    </tr>
<%
	}
%>
      </tbody>
    </table>
	<br>
<%
	String cacheName = request.getParameter("name");
	if (cacheName != null) {
		Cache cache = cacheManager.getCache(cacheName);
		int start = 0;
		if (request.getParameter("start") != null) {
			start = Integer.parseInt(request.getParameter("start"));
		}
		int index = -1;
%>
    <a href="ehcache.jsp?name=${param.name}&start=<%=start-10%>">&lt;</a>
	&nbsp;<%=start%>/<%=cache.getKeys().size()%>&nbsp;
	<a href="ehcache.jsp?name=${param.name}&start=<%=start+10%>">&gt;</a>
    <table border="1" width="100%">
      <thead>
        <tr>
          <th>index</th>
          <th>key</th>
          <th>value</th>
        </tr>
      </thead>
      <tbody>
<%
		for (Object key : cache.getKeys()) {
			index++;
			if (index < start) {
				continue;
			}
			if (index > start + 10) {
				break;
			}
			pageContext.setAttribute("index", index);
			pageContext.setAttribute("key", key);
			pageContext.setAttribute("cache", cache);
%>
        <tr>
	      <td>${index}</td>
	      <td>${key}</td>
	      <td><%=getValue(cache.get(key).getValue())%></td>
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
