<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.access.input.title" text="编辑资源"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#accessForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
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
		  <h4 class="title"><spring:message code="auth.access.input.title" text="编辑资源"/></h4>
		</header>

		<div class="content content-inner">

<form id="accessForm" method="post" action="access-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="access_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
	<label class="control-label" for="access_type"><spring:message code='auth.access.input.type' text='类型'/></label>
    <div class="controls">
	  <select id="access_type" name="type">
	    <option value="URL" ${model.type == 'URL' ? selected : ''}>URL</option>
	    <option value="METHOD" ${model.type == 'METHOD' ? selected : ''}>METHOD</option>
	  </select>
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="access_value"><spring:message code='auth.access.input.value' text='资源'/></label>
    <div class="controls">
      <input id="access_value" type="text" name="value" value="${model.value}" size="40" class="text required" minlength="1" maxlength="200">
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="access_perm"><spring:message code='auth.access.input.perm' text='权限'/></label>
    <div class="controls">
	  <select id="access_perm" name="permId">
	    <c:forEach items="${perms}" var="item">
	    <option value="${item.id}" ${model.perm.id==item.id ? 'selected' : ''}>${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="access_priority"><spring:message code='auth.access.input.priority' text='排序'/></label>
    <div class="controls">
      <input id="access_priority" type="text" name="priority" value="${model.priority}" size="40" class="text required number" minlength="1" maxlength="10">
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
