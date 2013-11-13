<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="java.io.*"%>
<%@page import="java.util.*"%>
<%@page import="javax.sql.*"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="com.mossle.core.jdbc.DbcpDataSourceInfo"%>
<%@page import="com.mossle.core.jdbc.DataSourceInfo"%>
<%@page import="com.mossle.core.jdbc.DataSourceService"%>
<%@page import="com.mossle.core.jdbc.DataSourceServiceFactoryBean"%>
<%@page import="com.mossle.core.jdbc.DataSourceWrapper"%>
<%
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);
	DataSourceService dataSourceService = (DataSourceService) ctx.getBean("dataSourceService");
	DataSourceServiceFactoryBean dataSourceServiceFactoryBean = (DataSourceServiceFactoryBean) ctx.getBean("&dataSourceService");
	String action = request.getParameter("action");
	if (action == null) {
		action = "list";
	}
	if ("close".equals(action)) {
		String name = request.getParameter("name");
		DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSource(name);
		dataSourceWrapper.close();
		response.sendRedirect("jdbc.jsp");
		return;
	}
	if ("restart".equals(action)) {
		String name = request.getParameter("name");
		DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSource(name);
		dataSourceWrapper.restart();
		response.sendRedirect("jdbc.jsp");
		return;
	}
	if ("edit".equals(action)) {
		String name = request.getParameter("name");
		DataSourceInfo dataSourceInfo = dataSourceService.getDataSourceInfo(name);
		pageContext.setAttribute("dataSourceInfo", dataSourceInfo);
	}
	if ("save".equals(action)) {
		String name = request.getParameter("name");

		String driverClassName = request.getParameter("driverClassName");
		String url = request.getParameter("url");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String removeAbandoned = request.getParameter("removeAbandoned");
		String testWhileIdle = request.getParameter("testWhileIdle");
		String testOnBorrow = request.getParameter("testOnBorrow");
		String validationQuery = request.getParameter("validationQuery");

		DbcpDataSourceInfo dataSourceInfo = new DbcpDataSourceInfo();
		dataSourceInfo.setName(name);
		dataSourceInfo.setDriverClassName(driverClassName);
		dataSourceInfo.setUrl(url);
		dataSourceInfo.setUsername(username);
		dataSourceInfo.setRemoveAbandoned(Boolean.getBoolean(removeAbandoned));
		dataSourceInfo.setTestWhileIdle(Boolean.getBoolean(testWhileIdle));
		dataSourceInfo.setTestOnBorrow(Boolean.getBoolean(testOnBorrow));
		dataSourceInfo.setValidationQuery(validationQuery);

		dataSourceService.register(dataSourceInfo);

		response.sendRedirect("jdbc.jsp");
		return;
	}
	if ("remove".equals(action)) {
		String name = request.getParameter("name");
		DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSource(name);
		dataSourceWrapper.close();
		dataSourceService.removeDataSource(name);
		dataSourceService.removeDataSourceInfo(name);
		response.sendRedirect("jdbc.jsp");
		return;
	}
	if ("toggle".equals(action)) {
		String name = request.getParameter("name");
		DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSource(name);
		if (dataSourceWrapper.isLog4jdbcEnabled()) {
			dataSourceWrapper.disableLog4jdbc();
		} else {
			dataSourceWrapper.enableLog4jdbc();
		}
		response.sendRedirect("jdbc.jsp");
		return;
	}
%>
<html>
  <head>
    <meta charset="utf-8">
	<title>jdbc</title>
    <style>
tbody tr:nth-child(odd) td,
tbody tr:nth-child(odd) th {
  background-color: #f9f9f9;
}
    </style>
	<script>
function toggle(key) {
	if (key == "") {
		return;
	}
	var el = document.getElementById(key);
	if (el.style.display == 'none') {
		el.style.display = '';
	} else {
		el.style.display = 'none';
	}
}
	</script>
  </head>
  <body>
    <table border="1">
	  <thead>
        <tr>
          <th>name</th>
          <th>driverClassName</th>
          <th>url</th>
          <th>numActive</th>
          <th>numIdle</th>
          <th>closed</th>
          <th>log4jdbcEnabled</th>
          <th>error</th>
          <th>&nbsp;</th>
        </tr>
	  </thead>
      <tbody>
<%
	for (DataSourceInfo dataSourceInfo : dataSourceService.getDataSourceInfos()) {
		String name = dataSourceInfo.getName();
		DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSource(name);
		pageContext.setAttribute("item", dataSourceInfo);
		pageContext.setAttribute("dataSource", dataSourceWrapper);
%>
        <tr onclick="toggle('${item.name}')" style="${dataSourceService.throwable != null ? 'cursor:pointer' : ''}">
          <td>${item.name}</td>
          <td>${dataSource.basicDataSource.driverClassName}</td>
          <td>${dataSource.basicDataSource.url}</td>
          <td>${dataSource.basicDataSource.numActive}</td>
          <td>${dataSource.basicDataSource.numIdle}</td>
          <td>${dataSource.basicDataSource.closed}</td>
          <td>
		    ${dataSource.log4jdbcEnabled}
			<button onclick="location.href='jdbc.jsp?action=toggle&name=${item.name}'">
			  ${dataSource.log4jdbcEnabled ? 'disable' : 'enable'}
			</button>
		  </td>
          <td>${dataSource.throwable != null}</td>
          <td>
		    <a href="jdbc.jsp?action=close&name=${item.name}">close</a>
			<a href="jdbc.jsp?action=restart&name=${item.name}">restart</a>
			<a href="jdbc.jsp?action=edit&name=${item.name}">edit</a>
			<a href="jdbc.jsp?action=remove&name=${item.name}">remove</a>
		  </td>
        </tr>
<%
		if (dataSourceWrapper.getThrowable() != null) {
%>
		<tr id="${item.name}" style="display:none;">
		  <td colspan="9">
		    <pre><%dataSourceWrapper.getThrowable().printStackTrace(new PrintWriter(out));%>
			</pre>
		  </td>
		</tr>
<%
		}
	}
%>
      </tbody>
    </table>

	<br>

	<form action="jdbc.jsp" method="post">
	  <input type="hidden" name="action" value="save">
	  <fieldset>
	    <table>
		  <tr>
		    <td>name</td>
			<td><input type="text" name="name" value="${dataSourceInfo.name}" size="100"></td>
		  </tr>
		  <tr>
		    <td>driverClassName</td>
			<td><input type="text" name="driverClassName" value="${dataSourceInfo.driverClassName}" size="100"></td>
		  </tr>
		  <tr>
		    <td>url</td>
			<td><input type="text" name="url" value="${dataSourceInfo.url}" size="100"></td>
		  </tr>
		  <tr>
		    <td>username</td>
			<td><input type="text" name="username" value="${dataSourceInfo.username}" size="100"></td>
		  </tr>
		  <tr>
		    <td>password</td>
			<td><input type="password" name="password" value="${dataSourceInfo.password}" size="100"></td>
		  </tr>
		  <tr>
		    <td>removeAbandoned</td>
			<td><input type="text" name="removeAbandoned" value="${dataSourceInfo.removeAbandoned}" size="100"></td>
		  </tr>
		  <tr>
		    <td>testWhileIdle</td>
			<td><input type="text" name="testWhileIdle" value="${dataSourceInfo.testWhileIdle}" size="100"></td>
		  </tr>
		  <tr>
		    <td>testOnBorrow</td>
			<td><input type="text" name="testOnBorrow" value="${dataSourceInfo.testOnBorrow}" size="100"></td>
		  </tr>
		  <tr>
		    <td>validationQuery</td>
			<td><input type="text" name="validationQuery" value="${dataSourceInfo.validationQuery}" size="100"></td>
		  </tr>
		</table>
		<button>save</button>
		<a href="jdbc.jsp">clear</a>
	  </fieldset>
	</form>
	<pre><%=dataSourceServiceFactoryBean.export()%></pre>
  </body>
</html>
