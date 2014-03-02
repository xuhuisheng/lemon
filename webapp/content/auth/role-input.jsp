<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.role.input.title" text="编辑角色"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#roleForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error',
        rules: {
            name: {
                remote: {
                    url: 'role-checkName.do',
                    data: {
                        <c:if test="${model != null}">
                        id: function() {
                            return $('#role_id').val();
                        }
                        </c:if>
                    }
                }
            }
        },
        messages: {
            name: {
                remote: "<spring:message code='auth.role.input.duplicate' text='存在重复名称'/>"
            }
        }
    });
})
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
		  <h4 class="title"><spring:message code="auth.role.input.title" text="编辑角色"/></h4>
		</header>

		<div class="content content-inner">

<form id="roleForm" method="post" action="role-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="role_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <!--
  <div class="control-group">
	<label class="control-label" for="role_name"><spring:message code='auth.role.input.name' text='名称'/></label>
    <div class="controls">
      <input id="role_name" type="text" name="name" value="${model.name}" size="40" class="text required" minlength="2" maxlength="50">
    </div>
  </div>
  -->
  <div class="control-group">
    <label class="control-label" for="role_roleDef">模板</label>
    <div class="controls">
	  <select id="role_roleDef" name="roleDefId">
	  <c:forEach items="${roleDefs}" var="item">
	   <option value="${item.id}">${item.name}</option>
	  </c:forEach>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="role_descn"><spring:message code='auth.role.input.description' text='描述'/></label>
    <div class="controls">
      <textarea id="role_descn" name="descn" maxlength="60" rows="4">${model.descn}</textarea>
    </div>
  </div>
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
