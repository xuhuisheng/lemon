<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "dict");%>
<%pageContext.setAttribute("currentMenu", "dict");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dictSchema.dictSchema.input.title" text="编辑"/></title>
    <%@include file="/common/s3.jsp"%>
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
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">

<form id="dictSchemaForm" method="post" action="dict-schema-save.do" class="form-horizontal">
  <input type="hidden" name="typeId" value="${param.typeId}">
  <c:if test="${model != null}">
  <input id="dictSchema_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="dictSchema_name">名称</label>
	<div class="col-sm-5">
	  <input id="dictSchema_name" type="text" name="name" value="${model.name}" size="40" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="dictSchema_type">类型</label>
	<div class="col-sm-5">
	  <input id="dictSchema_type" type="text" name="type" value="${empty model ? param.typeId : model.type}" size="40" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="dictSchema_priority">排序</label>
	<div class="col-sm-5">
	  <input id="dictSchema_priority" type="text" name="priority" value="${model.priority}" size="40" class="form-control number">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="docInfo_descn">备注</label>
	<div class="col-sm-5">
	  <input id="docInfo_descn" type="text" name="descn" value="${model.descn}" size="40" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5 col-md-offset-1">
      <button type="submit" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn btn-link a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
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

