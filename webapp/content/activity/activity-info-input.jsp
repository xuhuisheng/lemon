<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "activity");%>
<%pageContext.setAttribute("currentMenu", "activity");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#activity-infoForm").validate({
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
    <%@include file="/header/activity-info.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/activity-info.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="activity-infoForm" method="post" action="activity-info-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="activity-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="activity-info_name"><spring:message code="activity-info.activity-info.input.name" text="名称"/></label>
	<div class="col-sm-5">
	  <input id="activity-info_name" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="activityInfo_startTime">开始时间</label>
	<div class="col-sm-5">
      <div class="input-append datetimepicker date" style="padding-left: 0px;">
	    <input id="activityInfo_startTime" type="text" name="startTime" value="<fmt:formatDate value='${model.startTime}' pattern='yyyy-MM-dd HH:mm'/>" size="40" class="form-control required" readonly style="background-color:white;cursor:default; width: 175px;">
	    <span class="add-on" style="padding-top: 2px; padding-bottom: 2px;"><i class="icon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="activityInfo_endTime">结束时间</label>
	<div class="col-sm-5">
      <div class="input-append datetimepicker date" style="padding-left: 0px;">
	    <input id="activityInfo_endTime" type="text" name="endTime" value="<fmt:formatDate value='${model.endTime}' pattern='yyyy-MM-dd HH:mm'/>" size="40" class="form-control required" readonly style="background-color:white;cursor:default; width: 175px;">
	    <span class="add-on" style="padding-top: 2px; padding-bottom: 2px;"><i class="icon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="activityInfo_openTime">报名开始时间</label>
	<div class="col-sm-5">
      <div class="input-append datetimepicker date" style="padding-left: 0px;">
	    <input id="activityInfo_openTime" type="text" name="openTime" value="<fmt:formatDate value='${model.openTime}' pattern='yyyy-MM-dd HH:mm'/>" size="40" class="form-control required" readonly style="background-color:white;cursor:default; width: 175px;">
	    <span class="add-on" style="padding-top: 2px; padding-bottom: 2px;"><i class="icon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="activityInfo_closeTime">报名截止时间</label>
	<div class="col-sm-5">
      <div class="input-append datetimepicker date" style="padding-left: 0px;">
	    <input id="activityInfo_closeTime" type="text" name="closeTime" value="<fmt:formatDate value='${model.closeTime}' pattern='yyyy-MM-dd HH:mm'/>" size="40" class="form-control required" readonly style="background-color:white;cursor:default; width: 175px;">
	    <span class="add-on" style="padding-top: 2px; padding-bottom: 2px;"><i class="icon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="activityInfo_headCount">报名人数</label>
	<div class="col-sm-5">
	  <input id="activityInfo_headCount" type="text" name="headCount" value="${model.headCount}" size="40" class="form-control number">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="activityInfo_location">地点</label>
	<div class="col-sm-5">
	  <input id="activityInfo_location" type="text" name="location" value="${model.location}" size="40" class="text">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="activityInfo_content">活动内容</label>
	<div class="col-sm-5">
	  <textarea id="activityInfo_content" name="content">${model.people}</textarea>
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

