<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#perm-typeForm").validate({
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
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="permTypeForm" method="post" action="perm-type-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="permType_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
	<label class="control-label col-md-1" for="permType_name"><spring:message code='auth.permType.input.name' text='名称'/></label>
    <div class="col-sm-5">
      <input id="permType_name" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-1" for="permType_type"><spring:message code='auth.permType.input.type' text='类型'/></label>
    <div class="col-sm-5">
	  <label for="permType_type_0" class="radio inline">
	    <input id="permType_type_0" type="radio" name="type" value="0" class="required" ${model.type != 1 ? 'checked' : ''}>
		显示
	  </label>
	  <label for="permType_type_1" class="radio inline">
	    <input id="permType_type_1" type="radio" name="type" value="1" class="required" ${model.type == 1 ? 'checked' : ''}>
		隐藏
	  </label>
	  <label for="permType_type_0" class="validate-error" generated="true" style="display:none;"></label>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="permType_descn"><spring:message code='auth.permType.input.description' text='描述'/></label>
    <div class="col-sm-5">
      <textarea id="permType_descn" name="descn" maxlength="60" rows="4">${model.descn}</textarea>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn"><spring:message code='core.input.back' text='返回'/></button>
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

