<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "meeting");%>
<%pageContext.setAttribute("currentMenu", "meeting");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <link rel="stylesheet/less" type="text/css" href="${cdnPrefix}/public/bootstrap-timerpicker/0.5.2/css/timepicker.less" />
    <script type="text/javascript" src="${cdnPrefix}/public/bootstrap-timerpicker/0.5.2/js/bootstrap-timepicker.js"></script>
    <script src="${cdnPrefix}/public/less/2.5.1/less.min.js"></script>
    <script type="text/javascript">
$(function() {
    $("#meeting-infoForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });

	$('.timepicker input').timepicker({
		showMeridian: false,
		minuteStep: 30
	});
})
    </script>

    <link type="text/css" rel="stylesheet" href="${cdnPrefix}/public/mossle-userpicker/3.0/userpicker.css">
    <script type="text/javascript" src="${cdnPrefix}/public/mossle-userpicker/3.0/userpicker.js"></script>
	<script type="text/javascript">
$(function() {
	createUserPicker({
		modalId: 'userPicker',
		showExpression: true,
		searchUrl: '${tenantPrefix}/rs/user/search',
		treeUrl: '${tenantPrefix}/rs/party/tree?partyStructTypeId=1',
		childUrl: '${tenantPrefix}/rs/party/searchUser'
	});
})
    </script>
  </head>

  <body>
    <%@include file="/header/meeting-user.jsp"%>

    <div class="row-fluid" style="padding-top:65px;">

	<!-- start of main -->
      <section id="m-main" class="col-md-12">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  预定会议室
		</div>

		<div class="panel-body">


<form id="car-infoForm" method="post" action="save.do" class="form-horizontal">
  <input id="car-info_id" type="hidden" name="roomId" value="${meetingRoom.id}">
  <input id="car-info_id" type="hidden" name="calendarDate" value="${param.calendarDate}">
  <div class="form-group">
	<label class="control-label col-md-1 col-sm-1" for="perm_resc">会议室</label>
    <div class="col-sm-5">
       <p class="form-control-static">${meetingRoom.name}</p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1 col-sm-1" for="meetingInfo_subject">主题</label>
	<div class="col-sm-5">
	  <input id="meetingInfo_subject" type="text" name="subject" value="${model.subject}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1 col-sm-1" for="cal-info_startTime">开始时间</label>
	<div class="col-sm-5">
      <div class="input-group bootstrap-timepicker timepicker">
        <input class="form-control input-small" type="text" name="startTime" value="${startTime}">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-time"></i></span>
      </div>
	</div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1 col-sm-1" for="cal-info_endTime">结束时间</label>
	<div class="col-sm-5">
      <div class="input-group bootstrap-timepicker timepicker">
        <input class="form-control input-small" type="text" name="endTime" value="${endTime}">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-time"></i></span>
      </div>
	</div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1 col-sm-1" for="meetingInfo_organizer">发起人</label>
	<div class="col-sm-5">
	  <input type="text" name="organizer" value="" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1 col-sm-1" for="meetingInfo_attendees">与会人</label>
	<div class="col-sm-5">
	  <input type="text" name="attendees" value="" class="form-control">
	</div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1 col-sm-1" for="meetingInfo_content">备注</label>
	<div class="col-sm-5">
	  <textarea id="meetingInfo_content" name="content" class="form-control required" minlength="2" maxlength="10">${model.content}</textarea>
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

