<%@page contentType="text/html;charset=UTF-8"%>
<%@page session="false"%>
<%@include file="/taglibs.jsp"%>
<%String url = "/portal/index.do";%>
<%response.sendRedirect(request.getContextPath() + url);%>
