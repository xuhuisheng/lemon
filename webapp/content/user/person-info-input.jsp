<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user");%>
<%pageContext.setAttribute("currentMenu", "user");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.input.title" text="编辑用户"/></title>
    <%@include file="/common/s.jsp"%>
  </head>

  <body>
    <%@include file="/header/user.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/user.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="user.user.input.title" text="编辑用户"/></h4>
		</header>
		<div class="content content-inner">

<form id="userBaseForm" method="post" action="person-info-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="userBase_id" type="hidden" name="id" value="${model.id}">
  </c:if>

  <div class="control-group">
    <label class="control-label" for="userBase_email">邮箱</label>
	<div class="controls">
	  <input id="userBase_email" type="text" name="email" value="${model.email}" size="40" class="text required" minlength="2" maxlength="50">
    </div>
  </div>

  <div class="control-group">
    <label class="control-label" for="userBase_cellphone">手机</label>
	<div class="controls">
	  <input id="userBase_cellphone" type="text" name="cellphone" value="${model.cellphone}" size="40" class="text required" minlength="2" maxlength="50">
    </div>
  </div>

  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
      <button type="button" onclick="history.back();" class="btn a-cancel"><spring:message code='core.input.back' text='返回'/></button>
    </div>
  </div>
</form>
		</div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
