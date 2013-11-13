<%@page import="java.lang.management.*"%>
<%@page import="java.util.*"%>
<%@page import="javax.management.*"%>
<%@page import="ch.qos.logback.classic.jmx.JMXConfiguratorMBean"%>
<%
    MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    ObjectName objectName = new ObjectName("ch.qos.logback.classic:Name=default,Type=ch.qos.logback.classic.jmx.JMXConfigurator");

    JMXConfiguratorMBean jMXConfiguratorMBean = JMX.newMBeanProxy(mBeanServer, objectName, JMXConfiguratorMBean.class);

    String action = request.getParameter("action");
    if ("update".equals(action)) {
        String logger = request.getParameter("logger");
        String level = request.getParameter("level");

        jMXConfiguratorMBean.setLoggerLevel(logger, level);
        response.sendRedirect("logback.jsp");
        return;
    }
%>
<html>
  <head>
    <meta charset="utf-8">
	<title>logback</title>
    <style>
tbody tr:nth-child(odd) td,
tbody tr:nth-child(odd) th {
  background-color: #f9f9f9;
}
    </style>
    <script>
function $(id) {
    return document.getElementById(id);
}

function toggle(body) {
    if ($(body).style.display == '') {
        $(body).style.display = 'none';
    } else {
        $(body).style.display = '';
    }
}

function updateLoggerLevel(logger, level) {
    location.href = '?action=update&logger=' + logger + '&level=' + level;
}
    </script>
  </head>
  <body>
    <table border="1" onclick="toggle('statusBody')" style="cursor:pointer;">
      <thead>
        <tr>
          <th>status</th>
        </tr>
      </thead>
      <tbody id="statusBody" style="display:none;">
        <tr>
          <td>
<%
    List<String> statuses = jMXConfiguratorMBean.getStatuses();
    for (String status : statuses) {
%>
            <%=status%><br>
<%
    }
%>
          </td>
        </tr>
      </tbody>
    </table>

    <form action="logback.jsp">
      <input type="hidden" name="action" value="update">
      <input type="text" name="logger" value="">
      <select name="level">
        <option value="null"></option>
        <option value="OFF">OFF</option>
        <option value="ERROR">ERROR</option>
        <option value="WARN">WARN</option>
        <option value="INFO">INFO</option>
        <option value="DEBUG" selected>DEBUG</option>
        <option value="TRACE">TRACE</option>
        <option value="ALL">ALL</option>
      </select>
	  <input type="submit">
    </form>

    <table border="1">
      <caption onclick="toggle('loggerBody')" style="cursor:pointer;">logger</caption>
      <thead>
        <tr>
          <th>logger</th>
          <th>level</th>
          <th>&nbsp;</th>
        </tr>
      </thead>
      <tbody id="loggerBody">
<%
    List<String> loggers = jMXConfiguratorMBean.getLoggerList();
    for (String logger : loggers) {
        String level = jMXConfiguratorMBean.getLoggerLevel(logger);
        if (level != null && (!level.equals(""))) {
            pageContext.setAttribute("logger", logger);
            pageContext.setAttribute("level", level);
%>
        <tr>
          <td>${logger}</td>
          <td>
            <select onchange="updateLoggerLevel('${logger}', this.value)">
              <option value="OFF" ${level == 'OFF' ? 'selected' : ''}>OFF</option>
              <option value="ERROR" ${level == 'ERROR' ? 'selected' : ''}>ERROR</option>
              <option value="WARN" ${level == 'WARN' ? 'selected' : ''}>WARN</option>
              <option value="INFO" ${level == 'INFO' ? 'selected' : ''}>INFO</option>
              <option value="DEBUG" ${level == 'DEBUG' ? 'selected' : ''}>DEBUG</option>
              <option value="TRACE" ${level == 'TRACE' ? 'selected' : ''}>TRACE</option>
              <option value="ALL" ${level == 'ALL' ? 'selected' : ''}>ALL</option>
            </select>
          </td>
		  <td>
<%
			if ("ROOT".equals(logger)) {
%>
			&nbsp;
<%
			} else {
%>
		    <button onclick="updateLoggerLevel('${logger}', 'null')">delete</button>
<%
			}
%>
		  </td>
        </tr>
<%
        }
    }
%>
      </tbody>
    </table>
  </body>
</html>
