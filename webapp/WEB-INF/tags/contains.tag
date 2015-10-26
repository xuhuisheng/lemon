<%@tag pageEncoding="UTF-8"%>
<%@tag import="java.util.*"%>
<%@attribute name="items" type="java.util.Collection" required="true"%>
<%@attribute name="item" type="java.lang.Object" required="true"%>
<%
  Collection items = (Collection) jspContext.getAttribute("items");
  if (items == null) {
    return;
  }
  Object item = jspContext.getAttribute("item");

  if (items.contains(item)) {
%>
<jsp:doBody/>
<%
  }
%>
