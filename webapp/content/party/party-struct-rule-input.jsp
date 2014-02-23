<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "party");%>
<%pageContext.setAttribute("currentMenu", "party");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="org.structrule.input.title" text="组织机构结构规则"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#orgStructRuleForm").validate({
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
		  <h4 class="title"><spring:message code="org.structrule.input.title" text="组织机构结构规则"/></h4>
		</header>

		<div class="content content-inner">

<form id="orgStructRuleForm" method="post" action="party-struct-rule-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="orgStructRule_orgStructRuleId" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
	<label class="control-label" for="orgStructRule_orgStructType"><spring:message code="org.structrule.input.type" text="类型"/></label>
	<div class="controls">
	  <select id="orgStructRule_orgStructType" name="partyStructTypeId">
	    <c:forEach items="${partyStructTypes}" var="item">
	    <option value="${item.id}" ${model.partyStructType.id==item.id ? 'selected' : ''}>${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="orgStructRule_parentOrgType"><spring:message code="org.structrule.input.parenttype" text="上级类型"/></label>
    <div class="controls">
      <select id="orgStructRule_parentOrgType" name="parentTypeId">
	    <c:forEach items="${partyTypes}" var="item">
	    <option value="${item.id}" ${model.parentType.id==item.id ? 'selected' : ''}>${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="orgStructRule_childOrgType"><spring:message code="org.structrule.input.childtype" text="下级类型"/></label>
    <div class="controls">
      <select id="orgStructRule_childOrgType" name="childTypeId">
	    <c:forEach items="${partyTypes}" var="item">
	    <option value="${item.id}" ${model.childType.id==item.id ? 'selected' : ''}>${item.name}</option>
		</c:forEach>
	  </select>
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
