<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "group");%>
<%pageContext.setAttribute("currentMenu", "group");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="scope-info.scope-info.input.title" text="编辑"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#scope-infoForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });

	$("#scope_local_global").change(function() {
		$("#scope-infoForm").validate().resetForm();
	});
})
    </script>
  </head>

  <body>
    <%@include file="/header/group-info.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/group-info.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  <spring:message code="scope-info.scope-info.input.title" text="编辑"/>
		</div>

		<div class="panel-body">

<form id="scope-infoForm" method="post" action="group-info-save.do" class="form-horizontal">
  <s:if test="model != null">
  <input id="scope-info_id" type="hidden" name="id" value="${model.id}">
  </s:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="scope-info_name"><spring:message code="scope-info.scope-info.input.name" text="名称"/></label>
	<div class="col-sm-5">
	  <input id="scope-info_name" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-offset-1 col-md-11">
      <button id="submitButton" type="submit" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn btn-link a-cancel"><spring:message code='core.input.back' text='返回'/></button>
    </div>
  </div>
</form>
        </div>
      </div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>
