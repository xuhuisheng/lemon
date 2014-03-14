<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "party");%>
<%pageContext.setAttribute("currentMenu", "party");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="org.struct.input.title" text="组织机构结构"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#orgStructForm").validate({
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
		  <h4 class="title"><spring:message code="org.struct.input.title" text="组织机构结构"/></h4>
		</header>

		<div class="content content-inner">

<form id="orgStructForm" method="post" action="party-struct-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="orgStruct_orgStructId" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
	<label class="control-label" for="orgStruct_partyStructType"><spring:message code="org.struct.input.type" text="类型"/></label>
	<div class="controls">
	  <select id="orgStruct_partyStructType" name="partyStructTypeId">
	    <c:forEach items="${partyStructTypes}" var="item">
	    <option value="${item.id}" ${model.partyStructType.id==item.id ? 'selected' : ''}>${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="orgStruct_parentEntity"><spring:message code="org.struct.input.parententity" text="上级组织"/></label>
    <div class="controls">
      <select id="orgStruct_parentEntity" name="parentEntityId">
	    <c:forEach items="${partyEntities}" var="item">
	    <option value="${item.id}" ${model.parentEntity.id==item.id ? 'selected' : ''}>${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="orgStruct_childEntity"><spring:message code="org.struct.input.childentity" text="下级组织"/></label>
    <div class="controls">
      <select id="orgStruct_childEntity" name="childEntityId">
	    <c:forEach items="${partyEntities}" var="item">
	    <option value="${item.id}" ${model.childEntity.id==item.id ? 'selected' : ''}>${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="orgStruct_partTime0">兼职</label>
    <div class="controls">
      <label><input id="orgStruct_partTime0" type="radio" name="partTime" value="0" ${model.partTime == 0 ? 'checked' : ''}>全职</label>
      <label><input id="orgStruct_partTime1" type="radio" name="partTime" value="1" ${model.partTime != 0 ? 'checked' : ''}>兼职</label>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="orgStruct_link">关联</label>
    <div class="controls">
      <input id="orgStruct_link" type="text" name="link" value="${model.link}" size="40" class="text number" maxlength="10">
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="orgStruct_priority">排序</label>
	<div class="controls">
      <input id="orgStruct_priority" type="text" name="priority" value="${model.priority}" size="40" class="text number" maxlength="10">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="orgStruct_admin0">管理</label>
    <div class="controls">
      <label><input id="orgStruct_admin0" type="radio" name="admin" value="0" ${model.admin == 0 ? 'checked' : ''}>员工</label>
      <label><input id="orgStruct_admin0" type="radio" name="admin" value="1" ${model.admin != 0 ? 'checked' : ''}>负责人</label>
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
