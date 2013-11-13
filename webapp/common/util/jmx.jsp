<%@page import="java.lang.management.*"%>
<%@page import="java.util.*"%>
<%@page import="javax.management.*"%>
<%@page import="javax.management.openmbean.CompositeData"%>
<%@page import="javax.management.openmbean.TabularData"%>
<%!
	public String convert(Object value) {
		if (value == null) {
			return "null";
		}
		if (value instanceof CompositeData) {
			return convertCompositeData(value);
		}
		if (value instanceof TabularData) {
			return convertTabularData(value);
		}
		if (value.getClass().isArray()) {
			if (value instanceof boolean[]) {
				return Arrays.toString((boolean[]) value);
			} else if (value instanceof char[]) {
				return Arrays.toString((char[]) value);
			} else if (value instanceof byte[]) {
				return Arrays.toString((byte[]) value);
			} else if (value instanceof short[]) {
				return Arrays.toString((short[]) value);
			} else if (value instanceof int[]) {
				return Arrays.toString((int[]) value);
			} else if (value instanceof long[]) {
				return Arrays.toString((long[]) value);
			} else if (value instanceof float[]) {
				return Arrays.toString((float[]) value);
			} else if (value instanceof double[]) {
				return Arrays.toString((double[]) value);
			} else {
				StringBuffer buff = new StringBuffer();
				for (Object object : (Object[]) value) {
					buff.append(convert(object))
						.append("<br>");
				}
				return buff.toString();
			}
		}
		if (value instanceof Collection) {
			StringBuffer buff = new StringBuffer();

			for (Object object : (Collection) value) {
				buff.append(convert(object))
					.append("<br>");
			}

			return buff.toString();
		}

		return value.toString();
	}

	public String convertCompositeData(Object value) {
		CompositeData compositeData = (CompositeData) value;
		StringBuffer buff = new StringBuffer();
		for (String key : compositeData.getCompositeType().keySet()) {
			buff.append(key)
				.append("=")
				.append(convert(compositeData.get(key)))
				.append("<br>");
		}
		return buff.toString();
	}

	public String convertTabularData(Object value) {
		TabularData tabularData = (TabularData) value;
		StringBuffer buff = new StringBuffer();
		for (Object object : tabularData.values()) {
			buff.append(convert(object));
		}

		return buff.toString();
	}

    public String getAttributeValue(ObjectName objectName, String attributeName)
            throws Exception {
        try {
            Object value = ManagementFactory.getPlatformMBeanServer()
                    .getAttribute(objectName, attributeName);
			return convert(value);
        } catch (Exception ex) {
            return ex.toString();
        }
    }
%>
<html>
  <head>
    <meta charset="utf-8">
	<title>jmx</title>
    <style>
tbody tr:nth-child(odd) td,
tbody tr:nth-child(odd) th {
  background-color: #f9f9f9;
}
    </style>
  </head>
  <body>
<%
	MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

	String type = request.getParameter("type");

	if ("view".equals(type)) {
		String objectNameString = request.getParameter("objectNameString");
        ObjectName objectName = ObjectName.getInstance(objectNameString);
        MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(objectName);

		out.println(objectName + "<br>");
		out.println(mBeanInfo.getClassName() + "<br>");
		out.println(mBeanInfo.getDescription() + "<br>");
		out.println("<hr>");
		out.println("<table border='1'>");
		out.println("<thead>");
		out.println("<tr>");
		out.println("<th>name</th>");
		out.println("<th>value</th>");
		out.println("</tr>");
		out.println("</thead>");
		out.println("<tbody>");
		for (MBeanAttributeInfo attr : mBeanInfo.getAttributes()) {
			out.println("<tr>");
			out.println("<td>" + attr.getName() + "</td>");
			out.println("<td>" + getAttributeValue(objectName, attr.getName()) + "</td>");
			out.println("</tr>");
		}
		out.println("</tbody>");
		out.println("</table>");
/*
    <hr>
    <table border="1">
      <tr>
        <th>name</th>
        <th>value</th>
      </tr>
    <#list action.MBeanInfo.attributes as attr>
      <tr>
        <td>${attr.name}</td><td>${action.getAttributeValue(action.objectName, attr.name)}</td>
      </tr>
    </#list>
    </table>
*/
	} else {
		Set<ObjectName> names = mBeanServer.queryNames(null, null);
		Map<String, List<ObjectName>> objectNameMap = new HashMap<String, List<ObjectName>>();

		for (ObjectName objectName : names) {
			String domain = objectName.getDomain();
			List<ObjectName> list = objectNameMap.get(domain);

			if (list == null) {
				list = new ArrayList<ObjectName>();
				objectNameMap.put(domain, list);
			}

			list.add(objectName);
		}

		for (Map.Entry<String, List<ObjectName>> entry : objectNameMap.entrySet()) {
			out.println("<p>");
			out.println(entry.getKey() + "<br>");
			out.println("<ul>");
			for (ObjectName objectName : entry.getValue()) {
				out.println("<li><a href='jmx.jsp?type=view&objectNameString=" + objectName + "'>");
				out.println(objectName);
				out.println("</a></li>");
			}
			out.println("</ul>");
			out.println("</p>");
		}
	}
%>
  </body>
</html>
