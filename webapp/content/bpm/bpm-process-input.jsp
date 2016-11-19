<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "bpm-category");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#bpm-processForm").validate({
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
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="userRepoForm" method="post" action="bpm-process-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="userRepo_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
	<label class="control-label col-md-1" for="bpm-process_name">名称</label>
    <div class="col-sm-5">
      <input id="bpm-process_name" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="1" maxlength="10">
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-1" for="bpm-process_bpmCategoryId">流程分类</label>
    <div class="col-sm-5">
      <select id="bpm-process_bpmCategoryId" name="bpmCategoryId" class="form-control">
      <c:forEach items="${bpmCategories}" var="item">
	    <option value="${item.id}" ${item.id==model.bpmCategory.id ? 'selected' : ''}>${item.name}</option>
	  </c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-1" for="bpm-process_bpmConfBaseId">绑定流程</label>
    <div class="col-sm-5">
      <select id="bpm-process_bpmConfBaseId" name="bpmConfBaseId" class="form-control">
      <c:forEach items="${bpmConfBases}" var="item">
	    <option value="${item.id}" ${item.id==model.bpmConfBase.id ? 'selected' : ''}>${item.processDefinitionId}</option>
	  </c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-1" for="bpm-process_priority">排序</label>
    <div class="col-sm-5">
      <input id="bpm-process_priority" type="text" name="priority" value="${model.priority}" size="40" class="form-control required" minlength="1" maxlength="50">
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-1" for="bpm-process_useTaskConf">配置任务负责人</label>
    <div class="col-sm-5">
      <label><input id="bpm-process_useTaskConf_0" type="radio" name="useTaskConf" value="1" ${model.useTaskConf == 1 ? 'checked' : ''}>开启</label>
      <label><input id="bpm-process_useTaskConf_1" type="radio" name="useTaskConf" value="0" ${model.useTaskConf != 1 ? 'checked' : ''}>关闭</label>
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-1" for="bpm-process_descn">描述</label>
    <div class="col-sm-5">
      <input id="bpm-process_descn" type="text" name="descn" value="${model.descn}" size="40" class="form-control" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5">
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

