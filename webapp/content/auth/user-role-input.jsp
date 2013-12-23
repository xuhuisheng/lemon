<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth-user");%>
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

<form id="userForm2" method="post" action="user-role!save.do?operationMode=STORE" class="form-horizontal">
  <input type="hidden" name="id" value="${id}">
  <div class="control-group">
    <div class="controls">
	  <h5>local</h5>
	  <s:iterator value="roles" var="item">
        <input id="selectedItem-${item.id}" type="checkbox" name="selectedItem" value="${item.id}" <s:if test='%{#action.containsRole(#item.id)}'>checked</s:if>>&nbsp;
        <label for="selectedItem-${item.id}" style="display:inline;">${item.name}</label><br>
      </s:iterator>
    </div>
  </div>
  <hr>
<s:iterator value="sharedRoleMap" var="entry">
  <div class="control-group">
    <div class="controls">
	  <h5>${entry.key}</h5>
	  <s:iterator value="value" var="item">
        <input id="selectedItem-${item.id}" type="checkbox" name="selectedItem" value="${item.id}" <s:if test='%{#action.containsRole(#item.id)}'>checked</s:if>>&nbsp;
        <label for="selectedItem-${item.id}" style="display:inline;">${item.name}</label><br>
      </s:iterator>
    </div>
  </div>
  <hr>
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