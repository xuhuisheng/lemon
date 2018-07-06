<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "plm");%>
<%pageContext.setAttribute("currentMenu", "plm");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#plm-projectForm").validate({
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
    <%@include file="/header/plm.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/plm.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="car-infoForm" method="post" action="plm-project-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="car-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="plmProject_code">标识</label>
	<div class="col-sm-5">
	  <input id="plmProject_code" type="text" name="code" value="${model.code}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="plmProject_name"><spring:message code="car-info.car-info.input.name" text="名称"/></label>
	<div class="col-sm-5">
	  <input id="plmProject_name" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="plmProject_summary">简介</label>
	<div class="col-sm-5">
	  <textarea id="plmProject_summary" name="summary" class="form-control required" maxlength="65535">${model.summary}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="plmProject_status">状态</label>
	<div class="col-sm-5">
	  <input id="plmProject_status0" type="radio" name="status" value="draft" ${model.status == 'draft' ? 'checked' : ''}> 筹备中
	  <input id="plmProject_status0" type="radio" name="status" value="active" ${model.status == 'active' ? 'checked' : ''}> 进行中
	  <input id="plmProject_status1" type="radio" name="status" value="close" ${model.status == 'close' ? 'checked' : ''}> 已关闭
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

