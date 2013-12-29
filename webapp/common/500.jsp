<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@include file="/common/taglibs.jsp"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="java.io.StringWriter"%>
<%@ page import="java.net.InetAddress"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="org.slf4j.Logger"%>
<%@ page import="org.slf4j.LoggerFactory"%>
<%response.setStatus(200);%>

<%
pageContext.setAttribute("ctx", request.getContextPath());
Throwable ex = null;
if (exception != null) {
	ex = exception;
}
if (request.getAttribute("javax.servlet.error.exception") != null) {
	ex = (Throwable) request.getAttribute("javax.servlet.error.exception");
}

//记录日志
Logger logger = LoggerFactory.getLogger("500.jsp");
String requestInfo = "";
try {
	StringBuilder buff = new StringBuilder();
	buff.append(InetAddress.getLocalHost())
		.append("\n");
    buff.append("Header....\n");
    Enumeration<String> e = request.getHeaderNames();
    String key;
    while(e.hasMoreElements()){
        key = e.nextElement();
        buff.append(key)
			.append("=")
			.append(request.getHeader(key))
			.append("\n");
    }
    buff.append("Attribute....\n");
    e = request.getAttributeNames();
    while(e.hasMoreElements()){
        key = e.nextElement();
        buff.append(key)
			.append("=")
			.append(request.getAttribute(key))
			.append("\n");
    }

    buff.append("Parameter....\n");
    e = request.getParameterNames();
    while(e.hasMoreElements()){
        key = e.nextElement();
		buff.append(key)
			.append("=")
			.append(java.util.Arrays.asList(request.getParameterValues(key)))
			.append("\n");
    }
	requestInfo = buff.toString().replaceAll("<", "&lt;");
} catch(Throwable t) {
    logger.error("fetch request info error", t);
}
logger.error(requestInfo, ex);
%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>500 - <spring:message code="core.500.error" text="系统内部发生错误"/></title>
	<link href="${ctx}/s/bootstrap/css/bootstrap.min.css" rel="stylesheet">
	<link href="${ctx}/s/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">
	<script type="text/javascript" src="${ctx}/s/jquery/jquery.min.js"></script>
	<script type="text/javascript">
$(function() {
	$('#targetContentDiv').height($(window).innerHeight() - 150);
})
    </script>
	<style type="text/css">
#targetContentDiv {
	background-color: #b8b8b8;
	padding: 70px 0 80px;
	text-align: center;
}

#targetContentDiv h1 {
	font-size: 120px;
	letter-spacing: -2px;
    line-height: 1;
}

#targetContentDiv p {
	font-size: 40px;
    font-weight: 200;
    line-height: 1.25;
	font-weight: bold;
	padding: 10px;
}

#targetContentDiv li {
	display: inline;
	list-style: none outside none;
}
	</style>
  </head>

  <body>

	<div id="targetContentDiv">
	  <div class="container">
		<h1>500</h1>
		<p><spring:message code="core.500.error" text="系统内部发生错误"/></p>
		<ul>
		  <li><a class="btn btn-primary" href="${ctx}/"><spring:message code="core.500.back" text="返回首页"/></a></li>
		  <li><a class="btn" href="javascript:void(0);" onclick="">联系管理员</a></li>
		  <li><a class="btn" href="javascript:void(0);" onclick="$('#output').show();$('#targetContentDiv').hide();">显示详情</a></li>
		</ul>
      </div>
	</div>
	<div id="output" style="display:none">
	  <div style="text-align:center;" class="container">
	    <a class="btn btn-info container" href="javascript:void(0);" onclick="$('#output').hide();$('#targetContentDiv').show();">返回</a>
	  </div>
	  <pre>
<%=requestInfo%>
<hr>
<%
StringWriter writer = new StringWriter();
ex.printStackTrace(new PrintWriter(writer));
out.println(writer.toString());
%>
	  </pre>
	</div>

  </body>

</html>
