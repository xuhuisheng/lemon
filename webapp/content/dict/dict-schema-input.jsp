<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "dict");%>
<%pageContext.setAttribute("currentMenu", "dict");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dictSchema.dictSchema.input.title" text="编辑"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#dictSchemaForm").validate({
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
    <%@include file="/header/dict.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/dict.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="dictSchema.dictSchema.input.title" text="编辑"/></h4>
		</header>

		<div class="content content-inner">

<form id="dictSchemaForm" method="post" action="dict-schema-save.do" class="form-horizontal">
  <input type="hidden" name="typeId" value="${param.typeId}">
  <c:if test="${model != null}">
  <input id="dictSchema_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="dictSchema_name">名称</label>
	<div class="controls">
	  <input id="dictSchema_name" type="text" name="name" value="${model.name}" size="40" class="text">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="dictSchema_type">类型</label>
	<div class="controls">
	  <input id="dictSchema_type" type="text" name="type" value="${model.type}" size="40" class="text">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="dictSchema_priority">排序</label>
	<div class="controls">
	  <input id="dictSchema_priority" type="text" name="priority" value="${model.priority}" size="40" class="text number">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="docInfo_descn">备注</label>
	<div class="controls">
	  <input id="docInfo_descn" type="text" name="descn" value="${model.descn}" size="40" class="text">
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button type="submit" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
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
