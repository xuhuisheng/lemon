<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "meeting");%>
<%pageContext.setAttribute("currentMenu", "meeting");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="car-info.car-info.input.title" text="编辑"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#car-infoForm").validate({
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
    <%@include file="/header/meeting.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/meeting.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="car-info.car-info.input.title" text="编辑"/></h4>
		</header>

		<div class="content content-inner">

<form id="car-infoForm" method="post" action="meeting-room-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="car-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="meetingRoom_name"><spring:message code="car-info.car-info.input.name" text="名称"/></label>
	<div class="controls">
	  <input id="meetingRoom_name" type="text" name="name" value="${model.name}" size="40" class="text required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="meetingRoom_num">人数</label>
	<div class="controls">
	  <input id="meetingRoom_num" type="text" name="num" value="${model.num}" size="40" class="text number">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="meetingRoom_projector">投影仪</label>
	<div class="controls">
	  <select id="meetingRoom_projector" name="projector">
	    <option value="true" ${model.projector == 'true' ? 'selected' : ''}>有</option>
	    <option value="false" ${model.projector == 'false' ? 'selected' : ''}>无</option>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="meetingRoom_type">专用会议室</label>
	<div class="controls">
	  <select id="meetingRoom_type" name="type">
	    <option value="false" ${model.type == 'false' ? 'selected' : ''}>否</option>
	    <option value="true" ${model.type == 'true' ? 'selected' : ''}>是</option>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="meetingRoom_building">办公楼</label>
	<div class="controls">
	  <input id="meetingRoom_building" type="text" name="building" value="${model.building}" size="40" class="text required" minlength="1" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="meetingRoom_floor">楼层</label>
	<div class="controls">
	  <input id="meetingRoom_floor" type="text" name="floor" value="${model.floor}" size="40" class="text required" minlength="1" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
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
