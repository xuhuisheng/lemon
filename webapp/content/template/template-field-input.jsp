<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "template");%>
<%pageContext.setAttribute("currentMenu", "template");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#template-fieldForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });

<c:if test="${model.type != 'manual'}">
	var editor = CKEDITOR.replace('templateField_content');
</c:if>

})
    </script>
  </head>

  <body>
    <%@include file="/header/template-info.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/template-info.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="template-infoForm" method="post" action="template-field-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="template-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="template-info_name"><spring:message code="template-info.template-info.input.name" text="名称"/></label>
	<div class="col-sm-5">
	  <input id="template-info_name" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="templateField_infoId">模板</label>
	<div class="col-sm-5">
	  <select id="templateField_infoId" name="infoId" class="form-control">
	    <c:forEach items="${templateInfos}" var="item">
		<option value="${item.id}">${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="template-info_code">类型</label>
	<div class="col-sm-5">
	  <label><input id="mailTemplate_manual0" type="radio" name="type" value="manual" ${model.type == 'manual' ? 'checked' : ''}>手工</label>
	  <label><input id="mailTemplate_manual0" type="radio" name="type" value="ckeditor" ${empty model.type || model.type == 'ckeditor' ? 'checked' : ''}>ckeditor</label>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="templateField_content">内容</label>
	<div class="col-sm-5">
	  <textarea id="templateField_content" name="content">${model.content}</textarea>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5">
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

