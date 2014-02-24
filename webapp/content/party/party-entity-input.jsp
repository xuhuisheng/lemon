<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "party");%>
<%pageContext.setAttribute("currentMenu", "party");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="org.org.input.title" text="编辑组织机构"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#orgEntityForm").validate({
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
    <%@include file="/header/party.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/party.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="org.org.input.title" text="编辑组织机构"/></h4>
		</header>

		<div class="content content-inner">

<form id="orgEntityForm" method="post" action="party-entity-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="orgentity_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="orgentity_orgType"><spring:message code="org.org.input.type" text="类型"/>:</label>
	<div class="controls">
	  <select id="orgentity_orgType" name="partyTypeId">
	    <c:forEach items="${partyTypes}" var="item">
	    <option value="${item.id}" ${model.partyType.id==item.id ? 'selected' : ''}>${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="orgentity_name"><spring:message code="org.org.input.name" text="名称"/>:</label>
    <div class="controls">
	  <input id="orgentity_name" type="text" name="name" value="${model.name}" class="text required" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="orgentity_ref">引用:</label>
    <div class="controls">
	  <input id="orgentity_ref" type="text" name="ref" value="${model.ref}" class="text" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn"><spring:message code='core.input.save' text='保存'/></button>
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
