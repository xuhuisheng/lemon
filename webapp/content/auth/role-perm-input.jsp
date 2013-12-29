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

<form id="roleForm" method="post" action="role-perm!save.do?operationMode=STORE" class="form-horizontal">
  <input type="hidden" name="id" value="${id}">
  <s:iterator value="permTypes" var="permType">
  <div class="control-group">
	<label class="control-label"><strong>${permType.name}:</strong></label>
    <div class="controls">
      <s:iterator value="perms">
        <input id="selectedItem-${id}" type="checkbox" name="selectedItem" value="${id}" <s:if test='#action.selectedItem.contains(id)'>checked</s:if>>
        <label for="selectedItem-${id}" style="display:inline;">${name}</label>
		&nbsp;
      </s:iterator>
    </div>
  </div>
  </s:iterator>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn"><spring:message code='core.input.save' text='保存'/></button>
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
