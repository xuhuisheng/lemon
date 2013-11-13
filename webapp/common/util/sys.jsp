<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="java.lang.management.*"%>
<%@page import="java.net.*"%>
<%@page import="java.util.*"%>
<%@page import="javax.management.*"%>
<%@page import="javax.management.openmbean.CompositeData"%>
<%@page import="javax.management.openmbean.TabularData"%>
<%!
	public static class EntryBean {
		private Object key;
		private Object value;
		public EntryBean(Object key, Object value) {
			this.key = key;
			this.value = value;
		}
		public Object getKey() {
			return key;
		}
		public Object getValue() {
			return value;
		}
	}

	Object getJmxObject(String objectNameString, String attributeNameString) throws Exception {
		ObjectName objectName = ObjectName.getInstance(objectNameString);
		Object value = ManagementFactory.getPlatformMBeanServer().getAttribute(objectName, attributeNameString);
		return value;
	}

	Object getJmxDate(String objectNameString, String attributeNameString) throws Exception {
		Object value = getJmxObject(objectNameString, attributeNameString);
		Date startTime = new Date((Long) value);
		return startTime;
	}

	Object getJmxComposite(String objectNameString, String attributeNameString) throws Exception {
		Object value = getJmxObject(objectNameString, attributeNameString);
		CompositeData compositeData = (CompositeData) value;
		StringBuffer buff = new StringBuffer();
		for (String key : compositeData.getCompositeType().keySet()) {
			buff.append(key)
				.append("=")
				.append(compositeData.get(key))
				.append(",");
		}
		return buff.toString();
	}
%>
<html>
  <head>
    <meta charset="utf-8">
    <title>system info</title>
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
    <h1>server</h1>

	<table border="1" width="100%">
	  <tbody>
		<tr>
		  <td>host name</td>
		  <td><%=InetAddress.getLocalHost().getHostName()%></td>
		</tr>
		<tr>
		  <td>host address</td>
		  <td><%=InetAddress.getLocalHost().getHostAddress()%></td>
		</tr>
		<tr>
		  <td>os</td>
		  <td><%=(System.getProperty("os.name") + " " + System.getProperty("os.version"))%></td>
		</tr>
		<tr>
		  <td>java</td>
		  <td><%=System.getProperty("java.version")%></td>
		</tr>
	  </tbody>
	</table>

    <!-- ====================================================================== -->
	<br>
    <!-- ====================================================================== -->

	<h1>jvm statistics</h1>

	<table border="1" width="100%">
	  <tbody>
<%
{
%>
		<tr>
		  <td>Start Time</td>
		  <td><%=getJmxDate("java.lang:type=Runtime", "StartTime")%></td>
		</tr>
		<tr>
		  <td>Memory(total / free)</td>
		  <td>
		    <%=getJmxObject("java.lang:type=OperatingSystem", "TotalPhysicalMemorySize")%> /
		    <%=getJmxObject("java.lang:type=OperatingSystem", "FreePhysicalMemorySize")%>
		  </td>
		</tr>
		<tr>
		  <td>Heap Memory</td>
		  <td><%=getJmxComposite("java.lang:type=Memory", "HeapMemoryUsage")%></td>
		</tr>
		<tr>
		  <td>Non Heap Memory</td>
		  <td><%=getJmxComposite("java.lang:type=Memory", "NonHeapMemoryUsage")%></td>
		</tr>
		<tr>
		  <td>System Load Average</td>
		  <td><%=getJmxObject("java.lang:type=OperatingSystem", "SystemLoadAverage")%></td>
		</tr>
		<tr>
		  <td>Loaded Classes (currently / total / unloaded)</td>
		  <td>
		    <%=getJmxObject("java.lang:type=ClassLoading", "LoadedClassCount")%> /
		    <%=getJmxObject("java.lang:type=ClassLoading", "TotalLoadedClassCount")%> /
		    <%=getJmxObject("java.lang:type=ClassLoading", "UnloadedClassCount")%>
		  </td>
		</tr>
		<tr>
		  <td>Threads (total / peak / daemon)</td>
		  <td>
		    <%=getJmxObject("java.lang:type=Threading", "TotalStartedThreadCount")%> /
		    <%=getJmxObject("java.lang:type=Threading", "PeakThreadCount")%> /
		    <%=getJmxObject("java.lang:type=Threading", "DaemonThreadCount")%>
		  </td>
		</tr>
<%
}
%>
	  </tbody>
	</table>

    <!-- ====================================================================== -->
	<br>
    <!-- ====================================================================== -->

	<h1>system info</h1>

	<table border="1" width="100%">
	  <tbody>
<%
{
	Map<Object, Object> sortedMap = new TreeMap<Object, Object>(System.getProperties());
	for (Map.Entry<Object, Object> entry : sortedMap.entrySet()) {
		pageContext.setAttribute("entry", entry);
		if ("line.separator".equals(entry.getKey())) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			value = value.toString()
						.replace("\n", "\\n")
						.replace("\r", "\\r");
			pageContext.setAttribute("entry", new EntryBean(key, value));
		}
%>
		<tr>
		  <td>${entry.key}</td>
		  <td>${entry.value}</td>
		</tr>
<%
	}
}
%>
	  </tbody>
	</table>

    <!-- ====================================================================== -->
	<br>
    <!-- ====================================================================== -->

	<h1>env info</h1>

	<table border="1" width="100%">
	  <tbody>
<%
{
	Map<Object, Object> sortedMap = new TreeMap<Object, Object>(System.getenv());
	for (Map.Entry<Object, Object> entry : sortedMap.entrySet()) {
		pageContext.setAttribute("entry", entry);
		if ("line.separator".equals(entry.getKey())) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			value = value.toString()
						.replace("\n", "\\n")
						.replace("\r", "\\r");
			pageContext.setAttribute("entry", new EntryBean(key, value));
		}
%>
		<tr>
		  <td>${entry.key}</td>
		  <td>${entry.value}</td>
		</tr>
<%
	}
}
%>
	  </tbody>
	</table>
  </body>
</html>
