<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="java.util.List"%>
<%@tag import="org.springframework.context.ApplicationContext"%>
<%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@tag import="com.mossle.api.menu.MenuConnector"%>
<%@tag import="com.mossle.api.menu.MenuDTO"%>
<%@tag import="com.mossle.core.auth.CurrentUserHolder"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="systemCode" type="java.lang.Object" required="true"%>
<%
  String systemCode = (String) jspContext.getAttribute("systemCode");
  // System.out.println("systemCode : " + systemCode);

  if (systemCode == null) {
    out.print("");
  } else {

    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);
    MenuConnector menuConnector = ctx.getBean(MenuConnector.class);
    CurrentUserHolder currentUserHolder = ctx.getBean(CurrentUserHolder.class);
    try {
      String userId = currentUserHolder.getUserId();
      // System.out.println("userId : " + userId);
      List<MenuDTO> menuDtos = menuConnector.findMenus(systemCode, userId);
      for (MenuDTO menuDto : menuDtos) {
        // System.out.println(menuDto.getTitle());
	jspContext.setAttribute("menu", menuDto);
%>
  <c:if test="${empty menu.children}">
        <li class="${currentHeader == menu.code ? 'active' : ''}"><a href="${tenantPrefix}/${menu.url}"><i class="icon-list"></i>${menu.title}</a></li>
  </c:if>
  <c:if test="${not empty menu.children}">
        <li class="dropdown ${currentHeader == menu.code ? 'active' : ''}">
          <a data-toggle="dropdown" class="dropdown-toggle" href="${tenantPrefix}/${menu.url}">
            <i class="icon-list"></i>${menu.title} <b class="caret"></b>
          </a>
          <ul class="dropdown-menu">
    <c:forEach items="${menu.children}" var="child">
            <li><a href="${tenantPrefix}/${child.url}"><i class="icon-list"></i>${child.title}</a></li>
    </c:forEach>
          </ul>
        </li>
  </c:if>
<%
      }
    } catch(Exception ex) {
      System.out.println(ex);
    }
  }
%>

