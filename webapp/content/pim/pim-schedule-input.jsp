<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "pim");%>
<%pageContext.setAttribute("currentMenu", "pim-schedule");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#pimScheduleForm").validate({
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
    <%@include file="/header/pim3.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/pim3.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="margin-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  编辑
		</div>

		<div class="panel-body">

<form id="pimScheduleForm" method="post" action="pim-schedule-save.do" class="form-horizontal">
  <c:if test="${not empty model}">
  <input id="pimSchedule_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimSchedule_name">名称</label>
	<div class="col-sm-5">
	  <input id="pimSchedule_name" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimSchedule_location">地址</label>
	<div class="col-sm-5">
	  <input id="pimSchedule_location" type="text" name="location" value="${model.location}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimSchedule_type">类型</label>
	<div class="col-sm-5">
	  <select name="type" class="form-control">
        <option value="0">日程</option>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimSchedule_startTime">开始时间</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date col-sm-9">
	    <input id="pimSchedule_startTime" type="text" name="startTime" value="<fmt:formatDate value='${model.startTime}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control required">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimSchedule_endTime">结束时间</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date col-sm-9">
	    <input id="pimSchedule_endTime" type="text" name="endTime" value="<fmt:formatDate value='${model.endTime}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control required">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimSchedule_alertTime">提醒时间</label>
	<div class="col-sm-5">
	    <input id="pimSchedule_alertTime" type="text" name="alertTime" value="${model.alertTime}" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimSchedule_content">备注</label>
	<div class="col-sm-5">
	  <textarea id="pimSchedule_content" name="content" class="form-control">${model.content}</textarea>
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-offset-1 col-md-11">
      <button type="submit" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn btn-link a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
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
