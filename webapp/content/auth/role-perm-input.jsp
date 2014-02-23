<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.role.perm.title" text="设置权限"/></title>
    <%@include file="/common/s.jsp"%>
  </head>

  <body>
    <%@include file="/header/auth.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/auth.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="auth.role.perm.title" text="设置权限"/></h4>
		</header>

		<div class="content content-inner">

<form id="roleForm" method="post" action="role-perm-save.do" class="form-horizontal">
  <input type="hidden" name="id" value="${id}">
  <c:forEach items="${permTypes}" var="permType">
  <div class="control-group">
	<label class="control-label"><strong>${permType.name}:</strong></label>
    <div class="controls">
      <c:forEach items="${permType.perms}" var="item">
        <input id="selectedItem-${item.id}" type="checkbox" name="selectedItem" value="${item.id}" <tags:contains items="${selectedItem}" item="${item.id}">checked</tags:contains>>
        <label for="selectedItem-${item.id}" style="display:inline;">${item.name}</label>
		&nbsp;
      </c:forEach>
    </div>
  </div>
  </c:forEach>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn"><spring:message code='core.input.back' text='返回'/></button>
    </div>
  </div>
</form>
        </div>
      </article>

      <div class="m-spacer"></div>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
