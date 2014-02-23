<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.role.title" text="设置角色"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#userForm2").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });
});
    </script>
  </head>

  <body>
    <%@include file="/header/auth.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/auth.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="user.user.role.title" text="设置角色"/></h4>
		</header>
		<div class="content content-inner">

<form id="userForm2" method="post" action="user-role-save.do" class="form-horizontal">
  <input type="hidden" name="id" value="${id}">
  <div class="control-group">
    <div class="controls">
	  <h5>local</h5>
	  <c:forEach items="${roles}" var="item">
        <input id="selectedItem-${item.id}" type="checkbox" name="selectedItem" value="${item.id}" <tags:contains items="${userRoleIds}" item="${item.id}">checked</tags:contains>>&nbsp;
        <label for="selectedItem-${item.id}" style="display:inline;">${item.name}</label><br>
      </c:forEach>
    </div>
  </div>
  <hr>
<c:forEach items="${sharedRoleMap}" var="entry">
  <div class="control-group">
    <div class="controls">
	  <h5>${entry.key}</h5>
	  <c:forEach items="${entry.value}" var="item">
        <input id="selectedItem-${item.id}" type="checkbox" name="selectedItem" value="${item.id}" <tags:contains items="${userRoleIds}" item="${item.id}">checked</tags:contains>>&nbsp;
        <label for="selectedItem-${item.id}" style="display:inline;">${item.name}</label><br>
      </c:forEach>
    </div>
  </div>
  <hr>
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