<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="org.springframework.context.ApplicationContext"%>
<%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@tag import="com.mossle.api.user.UserConnector"%>
<%@attribute name="userId" type="java.lang.Object" required="true"%>
<%
  Object userId = jspContext.getAttribute("userId");
  if (userId == null) {
    out.print("");
  } else {

    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);
    UserConnector userConnector = ctx.getBean(UserConnector.class);
    try {
      out.print(userConnector.findById(userId.toString()).getDisplayName());
    } catch(Exception ex) {
      out.print(userId);
      System.out.println("cannot find user : " + userId);
    }
  }
%>
