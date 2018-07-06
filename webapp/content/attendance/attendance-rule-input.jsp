<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "attendance");%>
<%pageContext.setAttribute("currentMenu", "attendance");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#attendance-infoForm").validate({
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
    <%@include file="/header/attendance-info.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/attendance-info.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="attendance-infoForm" method="post" action="attendance-rule-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="attendance-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="attendance-info_name">上班时间</label>
	<div class="col-sm-1">
	  <input id="attendance-info_name" type="text" name="startHour" value="${model.startHour}" size="40" class="form-control required" minlength="1" maxlength="2">
    </div>
	<div class="col-sm-1">
	  <input id="attendance-info_name" type="text" name="startMinute" value="${model.startMinute}" size="40" class="form-control required" minlength="1" maxlength="2">
    </div>
	<div class="col-sm-1">
	  <p class="form-control-static">误差</p>
    </div>
	<div class="col-sm-1">
	  <input id="attendance-info_name" type="text" name="startOffset" value="${model.startOffset}" size="40" class="form-control required" minlength="1" maxlength="2">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="attendance-info_name">下班时间</label>
	<div class="col-sm-1">
	  <input id="attendance-info_name" type="text" name="endHour" value="${model.endHour}" size="40" class="form-control required" minlength="1" maxlength="2">
    </div>
	<div class="col-sm-1">
	  <input id="attendance-info_name" type="text" name="endMinute" value="${model.endMinute}" size="40" class="form-control required" minlength="1" maxlength="2">
    </div>
	<div class="col-sm-1">
	  <p class="form-control-static">误差</p>
    </div>
	<div class="col-sm-1">
	  <input id="attendance-info_name" type="text" name="endOffset" value="${model.endOffset}" size="40" class="form-control required" minlength="1" maxlength="2">
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

