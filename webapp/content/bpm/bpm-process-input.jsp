<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "bpm-category");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>流程定义</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#userRepoForm").validate({
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
    <%@include file="/header/bpm-console.jsp"%>

	<div class="row-fluid">
	<%@include file="/menu/bpm-console.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">流程定义</h4>
		</header>
		<div class="content content-inner">

<form id="userRepoForm" method="post" action="bpm-process-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="userRepo_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
	<label class="control-label" for="bpm-process_name">名称</label>
    <div class="controls">
      <input id="bpm-process_name" type="text" name="name" value="${model.name}" size="40" class="text required" minlength="1" maxlength="10">
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="bpm-process_bpmCategoryId">流程分类</label>
    <div class="controls">
      <select id="bpm-process_bpmCategoryId" name="bpmCategoryId">
      <c:forEach items="${bpmCategories}" var="item">
	    <option value="${item.id}" ${item.id==model.bpmCategory.id ? 'selected' : ''}>${item.name}</option>
	  </c:forEach>
	  </select>
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="bpm-process_bpmConfBaseId">绑定流程</label>
    <div class="controls">
      <select id="bpm-process_bpmConfBaseId" name="bpmConfBaseId">
      <c:forEach items="${bpmConfBases}" var="item">
	    <option value="${item.id}" ${item.id==model.bpmConfBase.id ? 'selected' : ''}>${item.processDefinitionId}</option>
	  </c:forEach>
	  </select>
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="bpm-process_priority">排序</label>
    <div class="controls">
      <input id="bpm-process_priority" type="text" name="priority" value="${model.priority}" size="40" class="text required" minlength="1" maxlength="50">
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="bpm-process_useTaskConf">配置任务负责人</label>
    <div class="controls">
      <label><input id="bpm-process_useTaskConf_0" type="radio" name="useTaskConf" value="1" ${model.useTaskConf == 1 ? 'checked' : ''}>开启</label>
      <label><input id="bpm-process_useTaskConf_1" type="radio" name="useTaskConf" value="0" ${model.useTaskConf != 1 ? 'checked' : ''}>关闭</label>
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="bpm-process_descn">描述</label>
    <div class="controls">
      <input id="bpm-process_descn" type="text" name="descn" value="${model.descn}" size="40" class="text" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
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
