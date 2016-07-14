<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="com.mossle.core.mail.MailFacade"%>
<%@page import="com.mossle.core.mail.MailHelper"%>
<%
	String action = request.getParameter("action");

	ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);
	MailFacade mailFacade = (MailFacade) ctx.getBean("mailFacade");
	MailHelper mailHelper = (MailHelper) ctx.getBean("mailHelper");

	if ("update".equals(action)) {
		String mode = request.getParameter("mode");
		String testMail = request.getParameter("testMail");
		mailHelper.getDefaultMailServerInfo().setMode(mode);
		mailHelper.getDefaultMailServerInfo().setTestMail(testMail);
		response.sendRedirect("mail.jsp");
	} else if("send".equals(action)) {
		mailFacade.sendMail("lingo.mossle@gmail.com", "test", "test");
		response.sendRedirect("mail.jsp");
	}
	pageContext.setAttribute("mailFacade", mailFacade);
	pageContext.setAttribute("mailHelper", mailHelper);
%>
<html>
  <head>
    <meta charset="utf-8">
	<title>mail</title>
    <style>
tbody tr:nth-child(odd) td,
tbody tr:nth-child(odd) th {
  background-color: #f9f9f9;
}
    </style>
  </head>
  <body>
	<form method="post" action="mail.jsp?action=update">
	  <table border="1">
		<tbody>
		  <tr>
			<td>是否测试模式</td>
			<td><input type="checkbox" name="mode" value="TEST" ${mailHelper.defaultMailServerInfo.mode ? 'TEST' : 'checked'}></td>
		  </tr>
		  <tr>
			<td>测试邮箱</td>
			<td><input type="text" name="testMail" value="${mailHelper.defaultMailServerInfo.testMail}"></td>
		  </tr>
		  <tr>
			<td colspan="2">
			  <button>修改</button>
			  &nbsp;
			  <button type="button" onclick="location.href='mail.jsp?action=send'">发送邮件</button>
			</td>
		  </tr>
		</tbody>
	  </table>
	</form>
  </body>
</html>
