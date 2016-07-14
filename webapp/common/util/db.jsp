<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="java.sql.*"%>
<%@page import="java.util.*"%>
<%!
	Connection conn = null;
	Statement state = null;
	ResultSet rs = null;
	int count = 0;

	void close() {
		if (rs != null) {
			try {
				rs.close();
				rs = null;
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		if (state != null) {
			try {
				state.close();
				state = null;
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		count = 0;
	}
%>
<%
	request.setCharacterEncoding("utf-8");

	String action = request.getParameter("action");
	if ("connect".equals(action)) {
		String driver = request.getParameter("driver");
		String url = request.getParameter("url");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		Class.forName(driver);

		session.setAttribute("jdbc.url", url);
		session.setAttribute("jdbc.username", username);
		session.setAttribute("jdbc.password", password);

		try {
			conn = DriverManager.getConnection(url, username, password);
			DatabaseMetaData dmd = conn.getMetaData();
			rs = dmd.getTables(null, null, null, null);
			List<String> tables = new ArrayList<String>();
			while (rs.next()) {
				if ("INFORMATION_SCHEMA".equals(rs.getString("TABLE_SCHEM"))) {
					continue;
				}
				if ("SYSTEM_LOBS".equals(rs.getString("TABLE_SCHEM"))) {
					continue;
				}
				tables.add(rs.getString("TABLE_NAME"));
			}
			session.setAttribute("jdbc.tables", tables);
		} finally {
			close();
		}
	} else {
		String sql = request.getParameter("sql");
		if (sql != null && (!"".equals(sql))) {
			String url = (String) session.getAttribute("jdbc.url");
			String username = (String) session.getAttribute("jdbc.username");
			String password = (String) session.getAttribute("jdbc.password");
			conn = DriverManager.getConnection(url, username, password);
			state = conn.createStatement();
			if (sql.toLowerCase().startsWith("select")) {
				rs = state.executeQuery(sql);
			} else {
				count = state.executeUpdate(sql);
			}
		}
	}

	try {
%>
<html>
  <head>
    <meta charset="utf-8">
    <title>db</title>
    <style type="text/css">
tbody tr:nth-child(odd) td,
tbody tr:nth-child(odd) th {
  background-color: #f9f9f9;
}

td {
	font-size: 12px;
}
	</style>
  </head>
  <body>
	<table border="1" width="100%">
	  <thead>
	    <tr>
		  <td colspan="2">
		    <form action="db.jsp">
			  <input type="hidden" name="action" value="connect">
			  <label>driver:<input type="text" name="driver" value="org.hsqldb.jdbcDriver"></label>
			  <label>url:<input type="text" name="url" value="jdbc:hsqldb:."></label>
			  <label>username:<input type="text" name="username" value="sa"></label>
			  <label>password:<input type="text" name="password" value=""></label>
			  <button>connect</button>
			</form>
		  </td>
		</tr>
	  </thead>
	  <tbody>
		<tr>
		  <td>
		    &nbsp;
<%
				List<String> tables = (List<String>) session.getAttribute("jdbc.tables");
				if (tables != null) {
					for (String table : tables) {
%>
				  <div><a href="javascript:document.getElementById('jdbc_sql').value='SELECT * FROM <%=table%>';void(0);"><%=table%></a></div>
<%
					}
				}
%>
		  </td>
		  <td style="vertical-align:top;">
		    <form method="post" action="db.jsp?action=select">
		      <textarea id="jdbc_sql" name="sql" style="width:100%">${param.sql}</textarea>
			  <button>execute</button>
			</form>
<%
			if (rs == null) {
			  out.println(count);
			} else {
				ResultSetMetaData rsmd = rs.getMetaData();
				int numberOfColumns = rsmd.getColumnCount();
				List<String> columnList = new ArrayList<String>();
				for (int i = 0; i < numberOfColumns; i++) {
					columnList.add(rsmd.getColumnLabel(i + 1));
				}
%>
			<table border="1" width="100%">
			  <thead>
			    <tr>
<%
				for (String column : columnList) {
%>
				  <td><%=column%></td>
<%
				}
%>
				</tr>
			  </thead>
			  <tbody>
<%
				while (rs.next()) {
%>
				<tr>
<%
				int index = 1;
				for (String column : columnList) {
%>
				  <td><%=rs.getString(index)%></td>
<%
					index++;
				}
%>
				</tr>
<%
				}
%>
			  </tbody>
			</table>
<%
			}
%>
		  </td>
		</tr>
	  </tbody>
	</table>

  </body>
</html>
<%
	} finally {
	    close();
    }
%>
