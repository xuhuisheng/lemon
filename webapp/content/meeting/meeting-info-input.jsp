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
    <%@include file="/header/meeting.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/meeting.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="car-infoForm" method="post" action="meeting-info-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="car-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="meetingInfo_subject">主题</label>
	<div class="col-sm-5">
	  <input id="meetingInfo_subject" type="text" name="subject" value="${model.subject}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cal-info_startTime">开始时间</label>
	<div class="input-group date datetimepicker col-md-5" style="padding-left:15px;padding-right:15px;">
	  <input id="cal-info_startTime" name="startTime" size="16" type="text" value="<fmt:formatDate value='${model.startTime}' pattern='yyyy-MM-dd HH:mm'/>" readonly style="background-color:white;cursor:default;" class="form-control required">
      <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
      <span class="input-group-addon"><span class="glyphicon glyphicon-th"></span></span>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cal-info_endTime">结束时间</label>
	<div class="input-group date datetimepicker col-md-5" style="padding-left:15px;padding-right:15px;">
	  <input id="cal-info_endTime" name="endTime" size="16" type="text" value="<fmt:formatDate value='${model.endTime}' pattern='yyyy-MM-dd HH:mm'/>" readonly style="background-color:white;cursor:default;" class="form-control required">
      <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
      <span class="input-group-addon"><span class="glyphicon glyphicon-th"></span></span>
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-1" for="perm_resc">会议室</label>
    <div class="col-sm-5">
      <select id="perm_resc" name="meetingRoomId" class="form-control">
	    <c:forEach items="${meetingRooms}" var="item">
	    <option value="${item.id}" ${model.meetingRoom.id==item.id ? 'selected' : ''}>${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="meetingInfo_organizer">发起人</label>
	<div class="col-sm-5">
		<div class="input-group userPicker" style="display:block-inline;">
		  <input id="_task_name_key" type="hidden" name="organizer" class="input-medium" value="">
		  <input type="text" name="taskAssigneeNames" style="background-color:white;" value="" class="form-control" readonly>
		  <div class="input-group-addon"><i class="glyphicon glyphicon-user"></i></div>
		</div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="meetingInfo_attendees">与会人</label>
	<div class="col-sm-5">
	  <input id="meetingInfo_attendees" type="text" name="attendees" value="${attendeeNames}" size="40" class="form-control required" minlength="2" maxlength="100">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="meetingInfo_items">配套资源</label>
	<div class="col-sm-5">
	  <label class="checkbox-inline">
	    <input id="meetingInfo_items0" type="checkbox" name="items" value="polycom" <tags:contains items="${items}" item="polycom">checked</tags:contains>>
		八爪鱼
	  </label>
	  <label class="checkbox-inline">
	    <input id="meetingInfo_items1" type="checkbox" name="items" value="projector" <tags:contains items="${items}" item="projector">checked</tags:contains>>
		投影仪
	  </label>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="meetingInfo_content">内容</label>
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

