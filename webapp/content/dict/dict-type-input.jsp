<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "dict");%>
<%pageContext.setAttribute("currentMenu", "dict");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#dict-typeForm").validate({
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


<form id="dictTypeForm" method="post" action="dict-type-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="dictType_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="dictType_name">名称</label>
	<div class="col-sm-5">
	  <input id="dictType_name" type="text" name="name" value="${model.name}" size="40" class="text">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="dictType_type">类型</label>
	<div class="col-sm-5">
	  <input id="dictType_type" type="text" name="type" value="${model.type}" size="40" class="text">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="docInfo_descn">备注</label>
	<div class="col-sm-5">
	  <input id="docInfo_descn" type="text" name="descn" value="${model.descn}" size="40" class="text">
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5">
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

