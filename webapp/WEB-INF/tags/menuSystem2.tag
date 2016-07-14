<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="java.util.List"%>
<%@tag import="org.springframework.context.ApplicationContext"%>
<%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@tag import="com.mossle.api.menu.MenuConnector"%>
<%@tag import="com.mossle.api.menu.MenuDTO"%>
<%@tag import="com.mossle.core.auth.CurrentUserHolder"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%

  ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);
  MenuConnector menuConnector = ctx.getBean(MenuConnector.class);
  CurrentUserHolder currentUserHolder = ctx.getBean(CurrentUserHolder.class);
  try {
    String userId = currentUserHolder.getUserId();
    // System.out.println("userId : " + userId);
    List<MenuDTO> menuDtos = menuConnector.findSystemMenus(userId);
    for (MenuDTO menuDto : menuDtos) {
      // System.out.println(menuDto.getTitle());
      jspContext.setAttribute("menu", menuDto);
%>

              <li class="dropdown ${currentHeader == menu.code ? 'active' : ''}">
                <a data-toggle="dropdown" class="dropdown-toggle" href="#"><i class="icon-list"></i>${menu.title} <b class="caret"></b></a>
                <ul class="dropdown-menu">
    <c:forEach items="${menu.children}" var="child">
      <li><a href="${tenantPrefix}/${child.url}"><i class="icon-list"></i>${child.title}</a></li>
      <li class="divider"></li>
    </c:forEach>
                </ul>
              </li>
<%
    }
  } catch(Exception ex) {
    System.out.println(ex);
  }
%>

